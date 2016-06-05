package com.tomcat.chapter20.standardmbeantest;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

public class StandardAgent {

	private MBeanServer mBeanServer = null;

	public StandardAgent() {
		mBeanServer = MBeanServerFactory.createMBeanServer();
	}

	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	public ObjectName createObjectName(String name) {
		ObjectName objectName = null;
		try {
			objectName = new ObjectName(name);
		} catch (Exception e) {
		}
		return objectName;
	}

	private void createStandardBean(ObjectName objectName,
			String managedResourceClassName) {
		try {
			mBeanServer.createMBean(managedResourceClassName, objectName);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		StandardAgent agent = new StandardAgent(); // ������׼����MBean�Ĵ���.
		MBeanServer mBeanServer = agent.getMBeanServer(); // ����MBean������
		
		String domain = mBeanServer.getDefaultDomain(); // ������
		String managedResourceClassName = "com.tomcat.chapter20.standardmbeantest.Car"; // ����Ҫ��������ȫ�޶���.
		
		ObjectName objectName = agent.createObjectName(domain + ":type="
				+ managedResourceClassName); // ���� ObjectName ����.(���ڱ�ʶMBean)
		
		agent.createStandardBean(objectName, managedResourceClassName); //������׼���͵�MBean.

		// manage MBean
		try {
			Attribute colorAttribute = new Attribute("Color", "blue");
			mBeanServer.setAttribute(objectName, colorAttribute); // Ϊ��MBean��������.
			
			System.out.println(mBeanServer.getAttribute(objectName, "Color")); // ��ȡ��MBean������.
			mBeanServer.invoke(objectName, "drive", null, null); // ���ø�MBean��drive����.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
