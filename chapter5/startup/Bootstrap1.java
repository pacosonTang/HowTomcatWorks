package com.tomcat.chapter5.startup;

import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

import com.tomcat.chapter5.core.SimpleLoader;
import com.tomcat.chapter5.core.SimpleWrapper;
import com.tomcat.chapter5.valves.ClientIPLoggerValve;
import com.tomcat.chapter5.valves.HeaderLoggerValve;

public final class Bootstrap1 {
  public static void main(String[] args) {

/* call by using http://localhost:8080/ModernServlet,
   but could be invoked by any name */

    HttpConnector connector = new HttpConnector();
    Wrapper wrapper = new SimpleWrapper();
    wrapper.setServletClass("servlet.ModernServlet"); // 设置servlet的相对路径
    
    Loader loader = new SimpleLoader(); // 类加载器
    Valve valve1 = new HeaderLoggerValve(); // 把请求头信息output到 console
    Valve valve2 = new ClientIPLoggerValve();// 用来将client的IP 地址输出到控制台上

    wrapper.setLoader(loader);
    ((Pipeline) wrapper).addValve(valve1); // 新增阀
    ((Pipeline) wrapper).addValve(valve2); // 新增阀

    connector.setContainer(wrapper);

    try {
      connector.initialize(); // 创建服务器套接字
      connector.start(); // 

      // make the application wait until we press a key.
      System.in.read();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}