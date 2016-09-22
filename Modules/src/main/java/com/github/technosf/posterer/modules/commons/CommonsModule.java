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
package com.github.technosf.posterer.modules.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.Factory.PropertiesParameter;
import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.modules.commons.config.CommonsConfiguratorPropertiesImpl;
import com.github.technosf.posterer.modules.commons.transport.CommonsRequestModelImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Guice module to inject Apache Commons HTTP transports
 * and also the Commons Configurator for properties storage.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class CommonsModule
        extends AbstractModule
{
    private static final Logger LOG = LoggerFactory
            .getLogger(CommonsModule.class);

    private final PropertiesParameter propsparam;


    /**
     * Creates the {@code Module}, setting the prefix for properties
     * 
     * @param prefix
     *            the prefix to use on properties in the {@code PropertiesModel}
     */
    public CommonsModule(PropertiesParameter propsparam)
    {

        super();
        this.propsparam = propsparam;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @SuppressWarnings("null")
    @Override
    protected void configure()
    {
        bind(PropertiesParameter.class).annotatedWith(Names.named("Properties"))
                .toInstance(propsparam);
        bind(Properties.class).to(CommonsConfiguratorPropertiesImpl.class)
                .in(Singleton.class);
        bind(RequestModel.class).to(CommonsRequestModelImpl.class);

        LOG.debug("Configured CommonsModule");
    }

}
