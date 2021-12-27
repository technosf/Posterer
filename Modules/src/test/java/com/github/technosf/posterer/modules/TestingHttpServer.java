/*
 * Copyright 2015 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.technosf.posterer.modules;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Utility class to stand up, for client testing, a basic, local HTTP server
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class TestingHttpServer
{
    private HttpServer httpServer;
    private int count;

    /**
     * Request interface I/F
     * Requires instantator to
     */
    private interface Processor
    {
        String process(URI requestUri, int count);
    }


    public static void main(String[] args) throws Exception
    {
        TestingHttpServer server =
                new TestingHttpServer(8000, "/", new Processor()
                {
                    @Override
                    public String process(URI requestUri, int count)
                    {
                        return String
                                .format("This is the test response for URI: [%1$s].\n\nThis is request #%2$d received.\n\nThis page was generated on %3$s",
                                        requestUri, count, new Date());
                    }
                });

        System.out.println("main :: Starting http server");
        server.start();
    }


    public TestingHttpServer(final int port, final String context,
            final Processor requestprocessor)
            throws IOException
    {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(null); // creates a default executor
        httpServer.createContext(context, new HttpHandler()
        {
            final Processor processor = requestprocessor;


            @Override
            public void handle(HttpExchange t) throws IOException
            {
                System.out.println("handle :: Got request");

                String response =
                        processor.process(t.getRequestURI(), getCount());

                t.getResponseHeaders()
                        .add("X-Count", Integer.toString(count++));
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        System.out.println("constructor :: Instantiated");
    }


    public void start()
    {
        httpServer.start();
    }


    public void stop()
    {
        httpServer.stop(0);
    }


    public int getCount()
    {
        return count;
    }
}
