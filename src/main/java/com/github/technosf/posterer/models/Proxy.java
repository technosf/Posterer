/*
 * Copyright 2015 technosf [https://github.com/technosf]
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

/**
 * Model for HTTP request definition and creation
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface Proxy
{

    /**
     * Returns the HTTP proxy host
     * 
     * @return the proxy host
     */
    //@Nullable
    String getProxyHost();


    /**
     * Sets the HTTP proxy host
     * 
     * @param host
     *            the proxy host
     */
    void setProxyHost(String host);


    /**
     * Returns the HTTP proxy port
     * 
     * @return the proxy port
     */
    //@Nullable
    String getProxyPort();


    /**
     * Sets the HTTP proxy
     * 
     * @param port
     *            the proxy port
     */
    void setProxyPort(String port);


    /**
     * Returns the HTTP proxy authenticatin user
     * 
     * @return the proxy authentication user
     */
    // @Nullable
    String getProxyUser();


    /**
     * Sets the HTTP proxy authentication user
     * 
     * @param user
     *            the proxy authentication user
     */
    void setProxyUser(String user);


    /**
     * Returns the HTTP proxy authentication password
     * 
     * @return the proxy authentication password
     */
    //@Nullable
    String getProxyPassword();


    /**
     * Sets the HTTP proxy authentication password
     * 
     * @param password
     *            the proxy authentication password
     */
    void setProxyPassword(String password);

}
