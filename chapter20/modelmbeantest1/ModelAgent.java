package com.tomcat.chapter20.modelmbeantest1;

import javax.management.Attribute;
import javax.management.Descriptor;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

public class ModelAgent {

	private String MANAGED_CLASS_NAME = "com.tomcat.chapter20.modelmbeantest1.Car";
	private MBeanServer mBeanServer = null;

	public ModelAgent() {
		mBeanServer = MBeanServerFactory.createMBeanServer();
	}

	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	private ObjectName createObjectName(String name) {
		ObjectName objectName = null;
		try {
			objectName = new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}
		return objectName;
	}

	private ModelMBean createMBean(ObjectName objectName, String mbeanName) {
		ModelMBeanInfo mBeanInfo = createModelMBeanInfo(objectName, mbeanName);
		RequiredModelMBean modelMBean = null;
		try {
			modelMBean = new RequiredModelMBean(mBeanInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelMBean;
	}

	private ModelMBeanInfo createModelMBeanInfo(ObjectName inMbeanObjectName,
			String inMbeanName) {
		ModelMBeanInfo mBeanInfo = null;
		ModelMBeanAttributeInfo[] attributes = new ModelMBeanAttributeInfo[1];
		ModelMBeanOperationInfo[] operations = new ModelMBeanOperationInfo[3];
		try {
			attributes[0] = new ModelMBeanAttributeInfo("Color",
					"java.lang.String", "the color.", true, true, false, null);
			operations[0] = new ModelMBeanOperationInfo("drive",
					"the drive method", null, "void",
					MBeanOperationInfo.ACTION, null);
			operations[1] = new ModelMBeanOperationInfo("getColor",
					"get color attribute", null, "java.lang.String",
					MBeanOperationInfo.ACTION, null);

			Descriptor setColorDesc = new DescriptorSupport(new String[] {
					"name=setColor", "descriptorType=operation",
					"class=" + MANAGED_CLASS_NAME, "role=operation" });
			MBeanParameterInfo[] setColorParams = new MBeanParameterInfo[] { (new MBeanParameterInfo(
					"new color", "java.lang.String", "new Color value")) };
			operations[2] = new ModelMBeanOperationInfo("setColor",
					"set Color attribute", setColorParams, "void",
					MBeanOperationInfo.ACTION, setColorDesc);

			mBeanInfo = new ModelMBeanInfoSupport(MANAGED_CLASS_NAME, null,
					attributes, null, operations, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBeanInfo;
	}

	public static void main(String[] args) {
		ModelAgent agent = new ModelAgent();  // 创建模型MBean代理. 
		MBeanServer mBeanServer = agent.getMBeanServer(); // 创建MBean 服务器.
		
		Car car = new Car();
		String domain = mBeanServer.getDefaultDomain(); // 创建 域  
		String managedResourceClassName = "com.tomcat.chapter20.modelmbeantest1.Car";  // 设置要管理的类的全限定名.  
		ObjectName objectName = agent.createObjectName(domain + ":type=" + 
												managedResourceClassName); // 创建 ObjectName 对象.(用于标识MBean)  
		
		String mBeanName = "myMBean";
		ModelMBean modelMBean = agent.createMBean(objectName, mBeanName); //创建模型类型的MBean. 
		try {
			modelMBean.setManagedResource(car, "ObjectReference"); // 为该模型MBean 设置资源类型
			mBeanServer.registerMBean(modelMBean, objectName); // 将该模型MBean 注册到 MBean 服务器.
		} catch (Exception e) {
		}

		// manage the bean
		try {
			Attribute attribute = new Attribute("Color", "green");
			mBeanServer.setAttribute(objectName, attribute); 
			String color = (String) mBeanServer.getAttribute(objectName,
					"Color");
			System.out.println("Color:" + color); // 设置属性->获取属性->打印属性  

			attribute = new Attribute("Color", "blue");
			mBeanServer.setAttribute(objectName, attribute);
			color = (String) mBeanServer.getAttribute(objectName, "Color");
			System.out.println("Color:" + color); // 设置属性->获取属性->打印属性  
			
			mBeanServer.invoke(objectName, "drive", null, null); // 调用MBean 服务器中表示为 objectName的MBean对象的drive方法 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
