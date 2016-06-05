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
		ModelAgent agent = new ModelAgent(); // ����ģ��MBean����.
		
		MBeanServer mBeanServer = agent.getMBeanServer(); // ����MBean ������.
		
		Car car = new Car();
		System.out.println("Creating ObjectName");
		
		ObjectName objectName = agent.createObjectName(); // ���� ObjectName����ΪMBean�ı�ʶ����.
		try {
			ModelMBean modelMBean = agent.createModelMBean("myMBean"); // ����ģ��MBean.			
			modelMBean.setManagedResource(car, "ObjectReference"); // ����������Դ.
			
			mBeanServer.registerMBean(modelMBean, objectName); // ע��ģ��MBean �� MBean ������.(objectName �Ǳ�ʶ��)
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		// manage the bean
		try {
			Attribute attribute = new Attribute("Color", "green"); 
			mBeanServer.setAttribute(objectName, attribute); // �������Բ����õ� MBean�������б�ʶΪobjectName��MBean.
			String color = (String) mBeanServer.getAttribute(objectName,
					"Color");
			System.out.println("Color:" + color); // ��ȡ���Բ���ӡ.

			attribute = new Attribute("Color", "blue");
			mBeanServer.setAttribute(objectName, attribute);
			color = (String) mBeanServer.getAttribute(objectName, "Color");
			System.out.println("Color:" + color); // ��������->��ȡ����->��ӡ����
			
			mBeanServer.invoke(objectName, "drive", null, null); // ����MBean �������б�ʾΪ objectName��MBean�����drive����

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
