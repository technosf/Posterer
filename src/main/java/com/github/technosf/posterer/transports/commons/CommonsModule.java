/*
 * Copyright 2015 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.transports.commons;

import com.github.technosf.posterer.models.PropertiesModel;
import com.github.technosf.posterer.models.RequestModel;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class CommonsModule extends AbstractModule
{
	private final String prefix;


	public CommonsModule(String prefix)
	{
		this.prefix = prefix;
	}


	@Override
	protected void configure()
	{
		bindConstant()
						.annotatedWith(Names.named("PropertiesPrefix")).to(prefix);

		bind(RequestModel.class).to(CommonsRequestModelImpl.class);
		bind(PropertiesModel.class).to(CommonsConfiguratorPropertiesImpl.class)
						.in(Singleton.class);
	}

}
