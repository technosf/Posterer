/*
 * Copyright 2014 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.ui.models.base;

import static java.lang.System.getProperty;
import static org.apache.commons.io.FileUtils.sizeOf;
import static org.apache.commons.lang3.StringUtils.isWhitespace;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.github.technosf.posterer.modules.Properties;

/**
 * Abstract implementation of basic {@code PreferencesModel} methods
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class AbstractPropertiesModel
        implements Properties
{

    /**
     * Property keys
     */
    private static final String PROP_USER_HOME = "user.home";

    /**
     * Generated properties directory path
     */
    private final String PROPERTIES_DIR;

    /**
     * Generated properties file path
     */
    private final String PROPERTIES_FILE;

    /**
     * The File representing the properties directory
     */
    protected final File propsDir;
    /**
     * The File representing the properties directory
     */
    protected final File propsFile;


    /**
     * Default constructor - create the properties directory
     */
    //@SuppressWarnings("null")
    protected AbstractPropertiesModel(String prefix)
    {
        PROPERTIES_DIR = FilenameUtils.concat(getProperty(PROP_USER_HOME),
                PROPERTIES_SUBDIR);

        propsDir = new File(PROPERTIES_DIR);

        if (!propsDir.exists())
        {
            propsDir.mkdir();
        }

        String fileName = PROPERTIES_FILENAME;

        if (!isWhitespace(prefix))
        {
            fileName = prefix + fileName;
        }

        PROPERTIES_FILE = FilenameUtils.concat(PROPERTIES_DIR, fileName);
        propsFile = new File(PROPERTIES_FILE);

    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.modules.Properties#getPropertiesDir()
     */
    @Override
    public final String getPropertiesDir() throws IOException
    {
        if (!propsDir.exists())
        /* 
         * Properties directory was not created
         */
        {
            throw new IOException(String.format(
                    "Directory [%1$s] has not been created.",
                    PROPERTIES_DIR));
        }
        else if (!propsDir.isDirectory())
        /*
         *  Properties directory is not a directory
         */
        {
            throw new IOException(String.format(
                    "Location [%1$s] is not a directory.",
                    PROPERTIES_DIR));
        }
        else if (!propsDir.canWrite())
        /*
         *  Properties directory is not writable
         */
        {
            throw new IOException(String.format(
                    "Cannot write to directory: [%1$s]",
                    PROPERTIES_DIR));
        }

        return PROPERTIES_DIR;
    }


    /**
     * Is there a properties file?
     * 
     * @return true if there is
     */
    //@SuppressWarnings("null")
    public final boolean isPropsFile()
    {
        return propsFile != null && propsFile.exists();
    }


    /**
     * Returns the size of the properties file in bytes.
     * 
     * @return the file size
     */
    public final long sizePropsFile()
    {
        long size = 0;

        if (isPropsFile())
        {
            size = sizeOf(propsFile);
        }
        return size;
    }


    /**
     * Returns the absolute file system path to the propoerties file
     * 
     * @return the properties file path
     * @throws IOException
     *             exception accessing the properties file
     */
    //@SuppressWarnings("null")
    public final String pathPropsFile() throws IOException
    {
        return propsFile.getAbsolutePath();
    }
}
