/*
 * Copyright 2016 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer;

import static com.google.inject.Guice.createInjector;

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.modules.commons.CommonsModule;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The Module Factory produces concrete classes for specific interfaces from a
 * particular implementation
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class Factory
{

    /**
     * A Guice Injector for the properties and request module implementation
     */
    private final Injector injector;


    /**
     * Instantiate a factory with a particular implementation
     * 
     * @param props_prefix
     */
    public Factory(String props_prefix)
    {
        Module module = new CommonsModule(props_prefix);
        injector = createInjector(module);
    }


    /**
     * Returns the properties
     * 
     * @return the properties
     */
    public final Properties getProperties()
    {
        return injector.getInstance(Properties.class);
    }


    /**
     * Returns the request model
     * 
     * @return the request model
     */
    public final RequestModel getRequestModel()
    {
        return injector.getInstance(RequestModel.class);
    }
}
