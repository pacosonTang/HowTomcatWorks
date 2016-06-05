package com.tomcat.chapter20.modelmbeantest2;

import java.io.InputStream;
import java.net.URL;
import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;

import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;

public class ModelAgent {
	private Registry registry;
	private MBeanServer mBeanServer;

	public ModelAgent() {
		registry = createRegistry();
		try {
			mBeanServer = Registry.getServer();
		} catch (Throwable t) {
			t.printStackTrace(System.out);
			System.exit(1);
		}
	}

	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	public Registry createRegistry() {
		Registry registry = null;
		try {
			URL url = ModelAgent.class
					.getResource("/com/tomcat/chapter20/modelmbeantest2/car-mbean-descriptor.xml");
			InputStream stream = url.openStream();
			Registry.loadRegistry(stream);
			stream.close();
			registry = Registry.getRegistry();
		} catch (Throwable t) {
			System.out.println(t.toString());
		}
		return (registry);
	}

	public ModelMBean createModelMBean(String mBeanName) throws Exception {
		ManagedBean managed = registry.findManagedBean(mBeanName);
		if (managed == null) {
			System.out.println("ManagedBean null");
			return null;
		}
		ModelMBean mbean = managed.createMBean();
		ObjectName objectName = createObjectName();
		return mbean;
	}

	private ObjectName createObjectName() {
		ObjectName objectName = null;
		String domain = mBeanServer.getDefaultDomain();
		try {
			objectName = new ObjectName(domain + ":type=MyCar");
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}
		return objectName;
	}

	public static void main(String[] args) {
		ModelAgent agent = new ModelAgent(); // 创建模型MBean代理.
		
		MBeanServer mBeanServer = agent.getMBeanServer(); // 创建MBean 服务器.
		
		Car car = new Car();
		System.out.println("Creating ObjectName");
		
		ObjectName objectName = agent.createObjectName(); // 创建 ObjectName，作为MBean的标识对象.
		try {
			ModelMBean modelMBean = agent.createModelMBean("myMBean"); // 创建模型MBean.			
			modelMBean.setManagedResource(car, "ObjectReference"); // 对象引用资源.
			
			mBeanServer.registerMBean(modelMBean, objectName); // 注册模型MBean 到 MBean 服务器.(objectName 是标识符)
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		// manage the bean
		try {
			Attribute attribute = new Attribute("Color", "green"); 
			mBeanServer.setAttribute(objectName, attribute); // 创建属性并设置到 MBean服务器中标识为objectName的MBean.
			String color = (String) mBeanServer.getAttribute(objectName,
					"Color");
			System.out.println("Color:" + color); // 获取属性并打印.

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
