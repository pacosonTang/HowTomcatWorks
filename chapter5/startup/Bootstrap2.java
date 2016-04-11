package com.tomcat.chapter5.startup;

import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

import com.tomcat.chapter5.core.SimpleContext;
import com.tomcat.chapter5.core.SimpleContextMapper;
import com.tomcat.chapter5.core.SimpleLoader;
import com.tomcat.chapter5.core.SimpleWrapper;
import com.tomcat.chapter5.valves.ClientIPLoggerValve;
import com.tomcat.chapter5.valves.HeaderLoggerValve;

public final class Bootstrap2 {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector();
    
    Wrapper wrapper1 = new SimpleWrapper();  // 最低级的servlet容器 Wrapper，利用加载器动态加载和封装servlet
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("servlet.PrimitiveServlet");
    
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("servlet.ModernServlet");

    Context context = new SimpleContext(); // 比Wrapper高一级的容器 Context,可以
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Valve valve1 = new HeaderLoggerValve(); // 非基础阀
    Valve valve2 = new ClientIPLoggerValve(); // 非基础阀

    ((Pipeline) context).addValve(valve1);
    ((Pipeline) context).addValve(valve2); // 添加基础阀到 Context

    Mapper mapper = new SimpleContextMapper(); // 映射器
    mapper.setProtocol("http");
    context.addMapper(mapper); // 将映射器添加到 context
    
    Loader loader = new SimpleLoader(); // 类加载器
    context.setLoader(loader); // 将该类加载器设置到context容器中
    
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive"); // 添加servlet访问路径映射(put到HashMap中)
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context); // 设置容器到Tomcat 连接器
    try {
      connector.initialize();
      connector.start();

      // make the application wait until we press a key.
      System.in.read();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}