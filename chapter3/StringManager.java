package com.tomcat.chapter3;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

// StringManager��source code.
// �������ڴ��� properties�ļ��� ��Щ�ļ���¼�����ڰ�����Ĵ�����Ϣ��
public class StringManager {
	private static Hashtable managers = new Hashtable();
	private String packageName;
	
	private StringManager(String packageName) {
		this.packageName = packageName;
	}
	
	public synchronized static StringManager getManager(String packageName) {
		StringManager manager = (StringManager) managers.get(packageName);
		if(manager == null) {
			manager = new StringManager(packageName);
			managers.put(packageName, manager);
		}
		return manager;
	}
}
