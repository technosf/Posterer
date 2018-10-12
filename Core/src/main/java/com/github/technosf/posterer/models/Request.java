/*
 * Copyright 2018 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.models;

import java.net.URI;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Definition of the data in an outgoing Request
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface Request
        extends Actionable
{

    /**
     * Returns the endpoint as an URI, or null if the request is invalid.
     * 
     * @return the endpoint as an Uri
     */
    @Nullable
    URI getUri();


    /**
     * Returns the http request uri
     * 
     * @return the endpoint uri
     */
    String getEndpoint();


    /**
     * Returns the http requst payload
     * 
     * @return the request payload
     */
    String getPayload();


    /**
     * Returns the request http method
     * 
     * @return The request method
     */
    String getMethod();


    /**
     * Returns the request security requirement
     * 
     * @return The request security
     */
    String getSecurity();


    /**
     * Returns the content mime type
     * 
     * @return the type
     */
    String getContentType();


    /**
     * Encode as Base 64
     * 
     * @return true if base 64 encoded
     */
    Boolean getBase64();


    /**
     * Test for {@code Request} actionability.
     * 
     * @return True if {@code Request} can sent via HTTP
     */
    @Override
    boolean isActionable();

}
