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
package com.github.technosf.posterer.modules;

import static com.google.inject.Guice.createInjector;

import java.io.File;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.core.models.Properties;
import com.github.technosf.posterer.core.models.RequestModel;
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

    private static final Logger LOG = LoggerFactory
            .getLogger(Factory.class);

    /**
     * A Guice Injector for the properties and request module implementation
     */
    @NonNull
    private final Injector injector;

    /**
     * Properties parameter encapsulation class
     */
    public static class PropertiesParameter
    {
        public final String prefix;
        @Nullable public final File directory;
        @Nullable public final String filename;


        public PropertiesParameter(final String prefix,
                @Nullable final File directory,
                @Nullable final String filename)
        {
            this.prefix = prefix;
            this.directory = directory;
            this.filename = filename;

        }
    }


    protected Factory(final PropertiesParameter propsparam)
            throws ModuleException
    {
        Module module = new CommonsModule(propsparam);

        Injector mi = createInjector(module);
        if (mi == null)
        {
            LOG.error("Module factory could not create injector - Excepting.");
            throw new ModuleException("Could not create Injector from Module");
        }
        injector = mi;
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


    public static Factory getFactory(final String prefix,
            @Nullable final File directory,
            @Nullable final String filename) throws ModuleException
    {
        PropertiesParameter param =
                new PropertiesParameter(prefix, directory, filename);
        return new Factory(param);
    }
}
