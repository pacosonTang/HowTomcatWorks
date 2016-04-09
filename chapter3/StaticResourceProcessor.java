package com.tomcat.chapter3;

import java.io.IOException;
import com.tomcat.chapter3.connector.http.HttpRequest;
import com.tomcat.chapter3.connector.http.HttpResponse;

public class StaticResourceProcessor {

  public void process(HttpRequest request, HttpResponse response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
