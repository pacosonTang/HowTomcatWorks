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
	// ������,�����������׽��֣�ά��HttpProcessor ����أ�stack��
    Connector connector = new HttpConnector(); 
    // servlet��ͼ����� Wrapper�����ڷ�װservlet�����ṩ���������������Ӧ��servlet
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("servlet.PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("servlet.ModernServlet");

    // ��Wrapper������ӵ��丸����Context
    Context context = new SimpleContext(); 
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    // ӳ����Mapper����������ͨ�� ����·������ http://localhost:8080/Modern;
    // ͨ��HttpProcessor.process() ��������reqeust����ȡ ���ʾ���·�� /Modern
    // ͨ����ӳ��������map���������Ҹ�·����Ӧ��������Դ����(Modern)
    // Ȼ��������map�����м���ͨ����������(Modern)ӳ�䵽servlet���ƣ�servlet.ModernServlet��
    // ������ͨ�� /Modern ӳ�䵽��Ӧ��servlet����·��(����·�����Ա������������)
    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    
    // ����������ڼ�����
    LifecycleListener listener = new SimpleContextLifecycleListener();
    ((Lifecycle) context).addLifecycleListener(listener);
    context.addMapper(mapper);
    
    // ����������
    Loader loader = new SimpleLoader();
    context.setLoader(loader);
    
    // ���servlet����·������Դ·���� �� ��Դ���Ƶ�ӳ���ϵ
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context);
    try {
      connector.initialize(); // ��ʼ������Ҫ���ط������׽���
      // �������������¼������Բο�����Ĳ����������ù���ʾ��ͼ��
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