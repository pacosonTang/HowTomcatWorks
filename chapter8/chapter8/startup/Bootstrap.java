package com.tomcat.chapter8.startup;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.naming.resources.ProxyDirContext;

import com.tomcat.chapter8.core.SimpleContextConfig;
import com.tomcat.chapter8.core.SimpleWrapper;

public final class Bootstrap {
  public static void main(String[] args) {

    //invoke: http://localhost:8080/Modern or  http://localhost:8080/Primitive
//	Ϊ��֪ͨStandardContext ʵ�����������Ӧ�ó���Ŀ¼����Ҫ����һ����Ϊ"catalina.base"��ϵͳ���ԣ���ֵΪ"user.dir"���Ե�ֵ��
    System.setProperty("catalina.base", System.getProperty("user.dir"));
    
    Connector connector = new HttpConnector(); //ʵ����Ĭ��������
    Wrapper wrapper1 = new SimpleWrapper(); // Ϊ����servlet�ഴ������Wrapperʵ����
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");
 
    // ����StandardContext ��һ��ʵ��������Ӧ�ó���·���������ĵ��ĵ���·��
    Context context = new StandardContext();
    // StandardContext's start method adds a default mapper
    context.setPath("/myApp");
    context.setDocBase("myApp");
    // ����Ĵ����ڹ����ϵ�ͬ���������server.xml �ļ��е�����
    // <Context path="/myApp" docBase="myApp" />
    
    context.addChild(wrapper1); // ������Wrapperʵ����ӵ�Context������
    context.addChild(wrapper2);

 // Ϊ�������÷���·����ӳ���ϵ������Context �������ܹ���λ������
    // context.addServletMapping(pattern, name); 
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    // add ContextConfig. This listener is important because it configures
    // StandardContext (sets configured to true), otherwise StandardContext
    // won't start
    
    // ��һ����ʵ����һ������������ͨ�� Context����ע����.
    LifecycleListener listener = new SimpleContextConfig();
    ((Lifecycle) context).addLifecycleListener(listener);

    // ���ţ�����ʵ����WebappLoader�࣬�����������Context����.
    // here is our loader
    Loader loader = new WebappLoader();
    // associate the loader with the Context
    context.setLoader(loader);
    
    // Ȼ�󣬽�Context������Ĭ�������������������Ĭ����������initialize() and start()������
    // �ٵ��� Context������ start() ����������servlet����׼�����������Դ���servlet������.
    // (��ȡ���������׽��֣��Խ���client������http����)
    connector.setContainer(context);
    try {
      connector.initialize();
      ((Lifecycle) connector).start();
      ((Lifecycle) context).start();
      
      // �������ļ��д��������ʾ����Դ��docBase����ֵ���������������еĲֿ������.
      // now we want to know some details about WebappLoader
      WebappClassLoader classLoader = (WebappClassLoader) loader.getClassLoader();
      System.out.println("Resources' docBase: " + ((ProxyDirContext)classLoader.getResources()).getDocBase());
      String[] repositories = classLoader.findRepositories();
      for (int i=0; i<repositories.length; i++) {
        System.out.println("  repository: " + repositories[i]);
      }

      // ����û�����������󣬳����˳�.
      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) context).stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}