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
package com.github.technosf.posterer.transports.commons;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.base.AbstractResponseModelTask;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class CommonsResponseModelTaskImpl
				extends AbstractResponseModelTask<HttpResponse>
				implements ResponseModel
{
	private static final HttpClientBuilder clientBuilder = HttpClientBuilder
					.create().useSystemProperties();

	private static final String CRLF = "\r\n";

	private CloseableHttpClient client;
	private HttpUriRequest request;
	private boolean isResponseProcessed = false;


	/**
	 * @param requestId
	 * @param uri
	 * @param timeout
	 * @param method
	 * @param contentType
	 * @param encode
	 * @param user
	 * @param password
	 */
	public CommonsResponseModelTaskImpl(int requestId, URI uri, int timeout,
					String method,
					String contentType,
					boolean encode,
					String user,
					String password)
	{
		super(requestId, uri, timeout,
						method,
						contentType,
						encode,
						user,
						password);

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.base.AbstractResponseModelTask#prepareClient()
	 */
	@Override
	protected void prepareClient()
	{
		client = clientBuilder.build();
		request = createRequest(uri, method);
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.base.AbstractResponseModelTask#getReponse()
	 */
	@Override
	protected HttpResponse getReponse()
					throws ClientProtocolException, IOException
	{
		return client.execute(request);
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.base.AbstractResponseModelTask#closeClient()
	 */
	@Override
	protected void closeClient()
	{
		HttpClientUtils.closeQuietly(getValue());
	}


	/**
	 * @param uri
	 * @param method
	 * @return
	 */
	private static HttpUriRequest createRequest(URI uri, String method)
	{
		switch (method)
		{
			case "GET":
				return new HttpGet(uri);
			case "HEAD":
				return new HttpHead(uri);
			case "POST":
				return new HttpPost(uri);
			case "PUT":
				return new HttpPut(uri);
			case "DELETE":
				return new HttpDelete(uri);
			case "TRACE":
				return new HttpTrace(uri);
			case "OPTIONS":
				return new HttpOptions(uri);
			case "PATCH":
				return new HttpPatch(uri);
		}

		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.technosf.posterer.models.AbstractResponseModelTask#processResponse()
	 */
	@Override
	protected synchronized void processResponse()
	{
		if (!isResponseProcessed)
		{
			response = getValue();
			// headers = getValue().getStatusLine().toString();
			// headers = Arrays.toString(response.getAllHeaders());
			headers = prettyPrintHeaders(response.getAllHeaders());
			if (response.getEntity() != null)
			{
				try
				{
					body = EntityUtils.toString(response.getEntity());
				}
				catch (ParseException | IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			closeClient();
			isResponseProcessed = true;
		}
	}


	/**
	 * @param headers
	 * @return
	 */
	private String prettyPrintHeaders(Header[] headers)
	{
		StringBuilder sb = new StringBuilder();
		for (Header header : headers)
		{
			if (sb.length() > 0)
			{
				sb.append(CRLF);
			}
			sb.append(header.getName())
							.append("=")
							.append(header.getValue());
		}

		return sb.toString();
	}
}
