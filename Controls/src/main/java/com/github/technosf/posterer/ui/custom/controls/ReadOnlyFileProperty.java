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
package com.github.technosf.posterer.ui.custom.controls;

import java.io.File;

import javafx.beans.property.ReadOnlyObjectPropertyBase;

/**
 * Read Only, observable File property
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.2
 */
public class ReadOnlyFileProperty
        extends ReadOnlyObjectPropertyBase<File>
{

    private final static String CONST_NAME = "File";

    /**
     * The underlying observable property
     */
    private File underlyingFile;


    /**
     * Set the selected file when there is a change
     * 
     * @param file
     *            the new file for the property
     * @return the current property file
     */
    public File set(final File file)
    {

        if ((file == null && underlyingFile != null)
                ||
                (file != null && !file.equals(underlyingFile)))
        {
            underlyingFile = file;
            fireValueChangedEvent();
        }

        return file;
    }


    /**
     * {@inheritDoc}
     *
     * @see javafx.beans.property.ReadOnlyProperty#getBean()
     */
    @Override
    public Object getBean()
    {
        return this;
    }


    /**
     * {@inheritDoc}
     *
     * @see javafx.beans.property.ReadOnlyProperty#getName()
     */
    @Override
    public String getName()
    {
        return CONST_NAME;
    }


    /**
     * {@inheritDoc}
     *
     * @see javafx.beans.value.ObservableObjectValue#get()
     */
    @Override
    public File get()
    {
        return underlyingFile;
    }
}
