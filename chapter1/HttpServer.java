package com.tomcat.chapter1;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class HttpServer {

  /** WEB_ROOT is the directory where our HTML and other files reside.
   *  For this package, WEB_ROOT is the "webroot" directory under the working
   *  directory.
   *  The working directory is the location in the file system
   *  from where the java command was invoked.
   */
  public static final String WEB_ROOT =
    System.getProperty("user.dir") + File.separator  + "webroot";

  // shutdown command
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // the shutdown command received
  private boolean shutdown = false;

  public static void main(String[] args) {
	System.out.println(System.getProperty("user.dir"));
    HttpServer server = new HttpServer();
    server.await();// 会在指定端口上等待HTTP请求。
  }

  public void await() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Loop waiting for a request
    while (!shutdown) {
      Socket socket = null;
      InputStream input = null;
      OutputStream output = null;
      try {
        socket = serverSocket.accept();
        input = socket.getInputStream();
        output = socket.getOutputStream();

        // create Request object and parse，创建 HTTP请求对象
        Request request = new Request(input);
        request.parse(); // 解析HTTP请求字符串

        // create Response object，创建HTTP 响应对象
        Response response = new Response(output);
        response.setRequest(request);
        response.sendStaticResource(); // 发送静态资源到client

        // Close the socket
        socket.close();

        //check if the previous URI is a shutdown command
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        continue;
      }
    }
  }
}
