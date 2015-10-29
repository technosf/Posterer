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
package com.github.technosf.posterer.modules.commons.transport;

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.impl.base.AbstractRequestModel;

/**
 * Apache Commons implementation of {@RequestModel}
 * <p>
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @param <T>
 *            The implementing type for the Response
 */
public class CommonsRequestModelImpl extends AbstractRequestModel<CommonsResponseModelTaskImpl>implements RequestModel
{

	@Override
	protected CommonsResponseModelTaskImpl createRequest(int requestId, int timeout, Request request)
	{
		return new CommonsResponseModelTaskImpl(requestId, timeout, request);
	}

	@Override
	protected CommonsResponseModelTaskImpl createRequest(int requestId, int timeout, Request request, Proxy proxy)
	{
		return new CommonsResponseModelTaskImpl(requestId, timeout, request, proxy);
	}

}
