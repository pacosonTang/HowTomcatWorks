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
//	为了通知StandardContext 实例到哪里查找应用程序目录，需要设置一个名为"catalina.base"的系统属性，其值为"user.dir"属性的值；
    System.setProperty("catalina.base", System.getProperty("user.dir"));
    
    Connector connector = new HttpConnector(); //实例化默认连接器
    Wrapper wrapper1 = new SimpleWrapper(); // 为两个servlet类创建两个Wrapper实例；
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");
 
    // 创建StandardContext 的一个实例，设置应用程序路径和上下文的文档根路径
    Context context = new StandardContext();
    // StandardContext's start method adds a default mapper
    context.setPath("/myApp");
    context.setDocBase("myApp");
    // 上面的代码在功能上等同于下面的在server.xml 文件中的配置
    // <Context path="/myApp" docBase="myApp" />
    
    context.addChild(wrapper1); // 将两个Wrapper实例添加到Context容器中
    context.addChild(wrapper2);

 // 为它们设置访问路径的映射关系，这样Context 容器就能够定位到他们
    // context.addServletMapping(pattern, name); 
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    // add ContextConfig. This listener is important because it configures
    // StandardContext (sets configured to true), otherwise StandardContext
    // won't start
    
    // 下一步，实例化一个监听器，并通过 Context容器注册它.
    LifecycleListener listener = new SimpleContextConfig();
    ((Lifecycle) context).addLifecycleListener(listener);

    // 接着，它会实例化WebappLoader类，并将其关联到Context容器.
    // here is our loader
    Loader loader = new WebappLoader();
    // associate the loader with the Context
    context.setLoader(loader);
    
    // 然后，将Context容器与默认连接器相关联，调用默认连接器的initialize() and start()方法，
    // 再调用 Context容器的 start() 方法，这样servlet容器准备就绪，可以处理servlet请求了.
    // (获取到服务器套接字，以接收client发出的http请求)
    connector.setContainer(context);
    try {
      connector.initialize();
      ((Lifecycle) connector).start();
      ((Lifecycle) context).start();
      
      // 接下来的几行代码仅仅显示出资源的docBase属性值和类载入器中所有的仓库的名字.
      // now we want to know some details about WebappLoader
      WebappClassLoader classLoader = (WebappClassLoader) loader.getClassLoader();
      System.out.println("Resources' docBase: " + ((ProxyDirContext)classLoader.getResources()).getDocBase());
      String[] repositories = classLoader.findRepositories();
      for (int i=0; i<repositories.length; i++) {
        System.out.println("  repository: " + repositories[i]);
      }

      // 最后，用户输入任意键后，程序退出.
      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) context).stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}