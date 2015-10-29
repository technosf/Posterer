/*
 * Copyright 2014 technosf [https://github.com/technosf]
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

	/**
	 * Returns the Use HTTP proxy flag
	 * 
	 * @return true if use proxy flag is set
	 */
	boolean getUseProxy();

	/**
	 * Set to use proxy
	 * 
	 */
	void setUseProxy(boolean useProxy);

	/**
	 * Set the current proxy
	 * 
	 * @param proxy
	 *            the proxy
	 */
	void setProxy(Proxy proxy);

}
