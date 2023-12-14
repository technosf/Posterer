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
package com.github.technosf.posterer.core.models;

import com.github.technosf.posterer.core.models.impl.KeyStoreBean;

/**
 * Model for HTTP request definition and creation
 * <p>
 * Proxy and Timeouts are modeled as linked to the RequestModel, while the
 * actual request itself is per invocation.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface RequestModel
{

    /**
     * Creates and fires off the HTTP request, returning an ResponseModel that
     * encapsulates the response.
     * 
     * @param requestBean
     *            the request
     * @return the response
     */
    ResponseModel doRequest(final Request request);


    /**
     * Creates and fires off the HTTP request, returning an ResponseModel that
     * encapsulates the response. Uses the given proxy
     * 
     * @param requestBean
     *            the request
     * @param proxy
     *            use this proxy
     * @return the response
     */
    ResponseModel doRequest(final Request request, final Proxy proxy);


    /**
     * Creates and fires off the HTTP request, returning an ResponseModel that
     * encapsulates the response. Uses the given security certificate
     * 
     * @param requestBean
     *            the request
     * @param keyStoreBean
     *            the certificate store
     * @param alias
     *            the alias of the certificate to use
     * @return the response
     */
    ResponseModel doRequest(final Request request,
            final KeyStoreBean keyStoreBean, final String alias);


    /**
     * Creates and fires off the HTTP request, returning an ResponseModel that
     * encapsulates the response. Uses the given proxy, and provides a security
     * certificate
     * 
     * @param requestBean
     *            the request
     * @param proxy
     *            use this proxy
     * @param keyStoreBean
     *            the certificate store
     * @param alias
     *            the alias of the certificate to use
     * @return the response
     */
    ResponseModel doRequest(final Request request, final Proxy proxy,
            final KeyStoreBean keyStoreBean, final String alias);


    /**
     * Set the request timeout
     * 
     * @param timeout
     *            in seconds
     */
    void setTimeout(int timeout);


    /**
     * Returns the request timeout
     * 
     * @return timeout in seconds
     */
    int getTimeout();

}
