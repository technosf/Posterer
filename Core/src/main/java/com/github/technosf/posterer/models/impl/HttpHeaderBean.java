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
package com.github.technosf.posterer.models.impl;

import com.github.technosf.posterer.models.HttpHeader;

/**
 * HTTP Header bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class HttpHeaderBean 
	implements HttpHeader 
{
	
    private final String name;

    private final String value;

    /**
     * @param name
     * @param value
     */
    public HttpHeaderBean(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

	/* (non-Javadoc)
	 * @see com.github.technosf.posterer.models.HttpHeader#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.github.technosf.posterer.models.HttpHeader#getValue()
	 */
	@Override
	public String getValue() 
	{
		return value;
	}

}
