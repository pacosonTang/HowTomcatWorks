package com.tomcat.chapter3.startup;

import com.tomcat.chapter3.connector.http.HttpConnector;

// ����Ӧ�ó���
public final class Bootstrap {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector(); // ������
    connector.start(); //����һ���߳�
  }
}