package com.tomcat.chapter3;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

// StringManager的source code.
// 该类用于处理 properties文件， 这些文件记录其所在包的类的错误信息；
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
