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
		StandardAgent agent = new StandardAgent(); // 创建标准类型MBean的代理.
		MBeanServer mBeanServer = agent.getMBeanServer(); // 创建MBean服务器
		
		String domain = mBeanServer.getDefaultDomain(); // 设置域
		String managedResourceClassName = "com.tomcat.chapter20.standardmbeantest.Car"; // 设置要管理的类的全限定名.
		
		ObjectName objectName = agent.createObjectName(domain + ":type="
				+ managedResourceClassName); // 创建 ObjectName 对象.(用于标识MBean)
		
		agent.createStandardBean(objectName, managedResourceClassName); //创建标准类型的MBean.

		// manage MBean
		try {
			Attribute colorAttribute = new Attribute("Color", "blue");
			mBeanServer.setAttribute(objectName, colorAttribute); // 为该MBean设置属性.
			
			System.out.println(mBeanServer.getAttribute(objectName, "Color")); // 获取该MBean的属性.
			mBeanServer.invoke(objectName, "drive", null, null); // 调用该MBean的drive方法.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
