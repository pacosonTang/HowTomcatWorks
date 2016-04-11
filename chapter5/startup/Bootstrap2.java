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
    
    Wrapper wrapper1 = new SimpleWrapper();  // ��ͼ���servlet���� Wrapper�����ü�������̬���غͷ�װservlet
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("servlet.PrimitiveServlet");
    
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("servlet.ModernServlet");

    Context context = new SimpleContext(); // ��Wrapper��һ�������� Context,����
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Valve valve1 = new HeaderLoggerValve(); // �ǻ�����
    Valve valve2 = new ClientIPLoggerValve(); // �ǻ�����

    ((Pipeline) context).addValve(valve1);
    ((Pipeline) context).addValve(valve2); // ��ӻ������� Context

    Mapper mapper = new SimpleContextMapper(); // ӳ����
    mapper.setProtocol("http");
    context.addMapper(mapper); // ��ӳ������ӵ� context
    
    Loader loader = new SimpleLoader(); // �������
    context.setLoader(loader); // ��������������õ�context������
    
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive"); // ���servlet����·��ӳ��(put��HashMap��)
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context); // ����������Tomcat ������
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