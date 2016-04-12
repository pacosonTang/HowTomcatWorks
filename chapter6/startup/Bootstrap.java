package com.tomcat.chapter6.startup;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

import com.tomcat.chapter6.core.SimpleContext;
import com.tomcat.chapter6.core.SimpleContextLifecycleListener;
import com.tomcat.chapter6.core.SimpleContextMapper;
import com.tomcat.chapter6.core.SimpleLoader;
import com.tomcat.chapter6.core.SimpleWrapper;

public final class Bootstrap {
  public static void main(String[] args) {
	// 连接器,创建服务器套接字，维护HttpProcessor 对象池（stack）
    Connector connector = new HttpConnector(); 
    // servlet最低级容器 Wrapper，用于封装servlet，并提供类加载器，加载相应的servlet
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("servlet.PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("servlet.ModernServlet");

    // 将Wrapper容器添加到其父容器Context
    Context context = new SimpleContext(); 
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    // 映射器Mapper，其作用是通过 请求路径，如 http://localhost:8080/Modern;
    // 通过HttpProcessor.process() 方法解析reqeust，获取 访问绝对路径 /Modern
    // 通过在映射器（的map方法）查找该路径对应的容器资源名称(Modern)
    // 然后还是在其map方法中继续通过容器名称(Modern)映射到servlet名称（servlet.ModernServlet）
    // 这样才通过 /Modern 映射到对应的servlet访问路径(加载路径，以便类加载器加载)
    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    
    // 添加生命周期监听器
    LifecycleListener listener = new SimpleContextLifecycleListener();
    ((Lifecycle) context).addLifecycleListener(listener);
    context.addMapper(mapper);
    
    // 添加类加载器
    Loader loader = new SimpleLoader();
    context.setLoader(loader);
    
    // 添加servlet访问路径（资源路径） 和 资源名称的映射关系
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context);
    try {
      connector.initialize(); // 初始化，主要返回服务器套接字
      // 触发生命周期事件（可以参考下面的测试用例调用过程示例图）
      ((Lifecycle) connector).start(); // highlight line.
      ((Lifecycle) context).start(); // highlight line.

      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) context).stop(); // highlight line.
    }
    catch (Exception e) {
      e.printStackTrace();
    }
   /* try { // these are some code in tomcat(5)-servlet container.
        connector.initialize();
        connector.start();

        // make the application wait until we press a key.
        System.in.read();
      }*/
  }
}