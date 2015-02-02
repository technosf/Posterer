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

import java.net.URI;

import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.base.AbstractRequestModel;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @param <T>
 *            The implementing type for the Response
 */
public class CommonsRequestModelImpl
				extends AbstractRequestModel<CommonsResponseModelTaskImpl>
				implements RequestModel
{

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.models.base.AbstractRequestModel#createRequest(int, java.net.URI, int,
	 *      java.lang.String, java.lang.String, boolean, java.lang.String, java.lang.String)
	 */
	// public CommonsResponseModelTaskImpl createRequest(int requestId, URI uri,
	// int timeout,
	// String method,
	// String contentType,
	// boolean encode,
	// String user,
	// String password)
	// {
	// return new CommonsResponseModelTaskImpl(requestId, uri, timeout,
	// method,
	// contentType, encode, user, password);
	// }

	@Override
	protected CommonsResponseModelTaskImpl createRequest(int requestId,
					RequestBean requestBean)
	{

		return new CommonsResponseModelTaskImpl(requestId,
						requestBean.getURI(),
						requestBean.getTimeout(),
						requestBean.getMethod(),
						requestBean.getContentType(),
						requestBean.getBase64(),
						requestBean.getHttpUser(),
						requestBean.getHttpPassword());
	}


	@Override
	protected CommonsResponseModelTaskImpl createRequest(int requestId,
					URI uri, int timeout, String method, String contentType,
					boolean encode, String user, String password)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
