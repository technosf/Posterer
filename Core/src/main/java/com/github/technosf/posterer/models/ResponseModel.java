/*
 * Copyright 2018 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.technosf.posterer.models;

import java.util.concurrent.ExecutionException;

/**
 * Models the response attributes and actions expected from the HTTP Client
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface ResponseModel
{
    /**
     * Returns a reference id tying the response to the request
     * 
     * @return the call reference id
     */
    int getReferenceId();


    /**
     * Returns the {@code Request} used to make the request
     * 
     * @return the request data
     */
    Request getRequest();


    /**
     * Returns TRUE if the request is complete and the full response is
     * available.
     * 
     * @return True if response is available.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    boolean isComplete() throws InterruptedException, ExecutionException;


    /**
     * Returns the time that the request/response was in-flight in milliseconds.
     * 
     * @return the elapsed time of the request/response
     */
    long getElaspedTimeMilli();


    /**
     * Return the Response Status line
     * 
     * @return the status line
     */
    String getStatus();


    /**
     * Returns the Response as a string.
     * 
     * @return the response.
     */
    String getResponse();


    /**
     * Returns the Response headers, excluding the body, as named-value pairs.
     * 
     * @return the payload headers.
     */
    String getHeaders();


    /**
     * Returns the Response body, excluding the headers.
     * 
     * @return the payload body.
     */
    String getBody();


    /**
     * Did the server require client authentication to return a response?
     * 
     * @return true is server asked for client auth
     */
    boolean neededClientAuth();
}
