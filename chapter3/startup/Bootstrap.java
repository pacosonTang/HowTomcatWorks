package com.tomcat.chapter3.startup;

import com.tomcat.chapter3.connector.http.HttpConnector;

// 启动应用程序
public final class Bootstrap {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector(); // 连接器
    connector.start(); //启动一个线程
  }
}