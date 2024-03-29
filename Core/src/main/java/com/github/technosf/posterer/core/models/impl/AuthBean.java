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
package com.github.technosf.posterer.core.models.impl;

import com.github.technosf.posterer.core.models.Auth;

/**
 * HTTP Authentication bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class AuthBean 
	implements Auth 
{
	
    private final String user;

    private final String password;

    public AuthBean(String user, String password)
    {
        this.user = user;
        this.password = password;
    }

	/* (non-Javadoc)
	 * @see com.github.technosf.posterer.models.impl.Auth#getUser()
	 */
	@Override
	public String getUser() 
	{
		return user;
	}

	/* (non-Javadoc)
	 * @see com.github.technosf.posterer.models.impl.Auth#getPassword()
	 */
	@Override
	public String getPassword() 
	{
		return password;
	}
}
