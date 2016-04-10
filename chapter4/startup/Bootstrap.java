/* explains Tomcat's default container */
package com.tomcat.chapter4.startup;

import org.apache.catalina.connector.http.HttpConnector;
import com.tomcat.chapter4.core.SimpleContainer;

public final class Bootstrap {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector();
    SimpleContainer container = new SimpleContainer();
    connector.setContainer(container);
    try {
      connector.initialize();
      connector.start();

      // make the application wait until we press any key.
      System.in.read();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}