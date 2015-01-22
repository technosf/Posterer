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
package com.github.technosf.posterer.models.impl;

import static org.apache.commons.lang3.ObjectUtils.hashCodeMulti;
import static org.apache.commons.lang3.StringEscapeUtils.escapeXml;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.ObjectUtils;

import com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class RequestBean implements PropertiesData
{

	private URI uri;

	private int timeout;

	private String endpoint;

	private String payload;

	private String method;

	private String contentType;

	private boolean base64;

	private String httpUser;

	private String httpPassword;


	/**
	 * @param propertiesData
	 */
	public RequestBean()
	{
		this.uri = null;
		this.endpoint = "";
		this.payload = "";
		this.method = "";
		this.contentType = "";
		this.httpUser = "";
		this.httpPassword = "";
	}


	/**
	 * @param propertiesData
	 */
	public RequestBean(PropertiesData propertiesData)
	{
		this(propertiesData.getEndpoint(),
						propertiesData.getPayload(),
						propertiesData.getMethod(),
						propertiesData.getContentType(),
						propertiesData.getBase64(),
						propertiesData.getHttpUser(),
						propertiesData.getHttpPassword());
	}


	/**
	 * @param endpoint
	 * @param payload
	 * @param method
	 * @param contentType
	 * @param base64
	 */
	public RequestBean(String endpoint, String payload, String method,
					String contentType, Boolean base64, String httpUser,
					String httpPassword)
	{
		this.endpoint = endpoint;
		this.payload = payload;
		this.method = method;
		this.contentType = contentType;
		this.base64 = base64;
		this.httpUser = httpUser;
		this.httpPassword = httpPassword;
		this.uri = constructUri(endpoint);
	}


	/**
	 * @return
	 */
	public URI getURI()
	{
		return uri;
	}


	/**
	 * @return
	 */
	public int getTimeout()
	{
		return timeout;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData#getEndpoint()
	 */
	@Override
	public String getEndpoint()
	{
		return endpoint;
	}


	/**
	 * @param endpoint
	 *            the endpoint to set
	 */
	public final void setEndpoint(String endpoint)
	{
		this.endpoint = endpoint;
		this.uri = constructUri(endpoint);
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData#getPayload()
	 */
	@Override
	public String getPayload()
	{
		return unescapeXml(payload);
	}


	/**
	 * @return
	 */
	public String getPayloadRaw()
	{
		return payload;
	}


	/**
	 * @param payload
	 *            the request to set
	 */
	public final void setPayload(String payload)
	{
		this.payload = escapeXml(payload);
	}


	/**
	 * @param method
	 *            the method to set
	 */
	public final void setMethod(String method)
	{
		this.method = method;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData#getMethod()
	 */
	@Override
	public String getMethod()
	{
		return method;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData#getContentType()
	 */
	@Override
	public String getContentType()
	{
		return contentType;
	}


	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public final void setContentType(String contentType)
	{
		this.contentType = contentType;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData#getBase64()
	 */
	@Override
	public Boolean getBase64()
	{
		return base64;
	}


	/**
	 * @param base64
	 *            the base64 to set
	 */
	public final void setBase64(boolean base64)
	{
		this.base64 = base64;
	}


	/**
	 * @return the httpPassword
	 */
	@Override
	public final String getHttpPassword()
	{
		return httpPassword;
	}


	/**
	 * @param httpPassword
	 *            the httpPassword to set
	 */
	public final void setHttpPassword(String httpPassword)
	{
		this.httpPassword = httpPassword;
	}


	/**
	 * @return the httpUser
	 */
	@Override
	public final String getHttpUser()
	{
		return httpUser;
	}


	/**
	 * @param httpUser
	 *            the httpUser to set
	 */
	public final void setHttpUser(String httpUser)
	{
		this.httpUser = httpUser;
	}


	/* -------------------------------------------------------------------- */

	/**
	 * @return
	 */
	public final boolean isComplete()
	{
		return isNotBlank(endpoint) && isNotBlank(method)
						&& isNotBlank(contentType);
	}


	/**
	 * @param format
	 * @return
	 */
	public final String toString(String format)
	{
		return String.format(format, endpoint, payload, method, contentType,
						base64, httpUser, httpPassword);
	}


	/**
	 * @param propertiesData
	 * @return
	 */
	public final int hashCode(PropertiesData propertiesData)
	{
		return hashCodeMulti(
						ObjectUtils.toString(propertiesData.getEndpoint()),
						ObjectUtils.toString(propertiesData.getPayload()),
						ObjectUtils.toString(propertiesData.getMethod()),
						ObjectUtils.toString(propertiesData.getContentType()),
						ObjectUtils.toString(propertiesData.getBase64()),
						ObjectUtils.toString(propertiesData.getHttpUser()),
						ObjectUtils.toString(propertiesData.getHttpPassword()));
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode()
	{
		return hashCode(this);
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj)
	{
		return PropertiesData.class.isInstance(obj)
						&& hashCode() == hashCode((PropertiesData) obj);
	}


	/**
	 * @param endpoint
	 * @return
	 */
	private URI constructUri(final String endpoint)
	{
		URI uri = null;

		try
		{
			uri = new URI(endpoint);
		}
		catch (URISyntaxException e)
		{
		}

		return uri;
	}

}
