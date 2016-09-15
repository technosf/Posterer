package com.github.technosf.posterer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Integration Test Class - A simple HTTP server that will reflect back the
 * request sent to it for confirmation.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
@SuppressWarnings("restriction")
public class Reposter
{
    private static final String CONST_ADDRESS = "127.0.0.1";
    private static final Integer CONST_PORT = 9999;
    private static final String CONST_PATH = "/";

    //private static int count = 0;


    public static void main(String[] args)
            throws IOException
    {
        /*
         * Create a server and configure it's SSL engine
         */
        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(CONST_ADDRESS, CONST_PORT),
                        0);
        server.createContext(CONST_PATH, new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    /**
     * A request handler that presents audit info
     * if the request passes the SSL config requirements.
     */
    static class MyHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchng) throws IOException
        {
            URI uri = exchng.getRequestURI();
            String method = exchng.getRequestMethod();
            Map<String, List<String>> headers = exchng.getRequestHeaders();
            byte[] body = IOUtils.toByteArray(exchng.getRequestBody());

            StringBuilder audit =
                    new StringBuilder(
                            String.format(
                                    "URI:\t\t\t%1$s\nMethod:\t\t%2$s\nHeader count:\t\t%3$s\nBody size:\t\t%4$s\n\n",
                                    uri,
                                    method,
                                    headers.size(),
                                    body.length));

            audit.append("Header and Values\n");
            for (String header : headers.keySet())
            {
                audit.append("\t").append(StringUtils.rightPad(header, 24));
                for (String value : headers.get(header))
                {
                    audit.append("\t").append(value).append("\n");
                }
            }

            if (body.length > 0)
            {
                audit.append("\n--== Body ==--\n").append(new String(body));
            }
            else
            {
                audit.append("\n--== No Body ==--");
            }

            System.out
                    .println(
                            "\n----------------------------------------------");
            System.out.println(audit.toString());

            audit.insert(0, "This is the response:\n");
            exchng.sendResponseHeaders(200, audit.length());
            OutputStream os = exchng.getResponseBody();
            os.write(audit.toString().getBytes());
            os.close();
            exchng.close();
        }
    }
}
