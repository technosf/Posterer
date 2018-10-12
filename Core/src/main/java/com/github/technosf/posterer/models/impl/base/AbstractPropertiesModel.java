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
package com.github.technosf.posterer.models.impl.base;

import static java.lang.System.getProperty;
import static org.apache.commons.io.FileUtils.sizeOf;
import static org.apache.commons.lang3.StringUtils.isWhitespace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.impl.ProxyBean;
import com.github.technosf.posterer.models.impl.RequestBean;

/**
 * Abstract implementation of basic {@code PreferencesModel} methods based
 * on using local files
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
     * RequestBean map
     */
    private final Map<Integer, RequestBean> requestProperties =
            new HashMap<>();

    /**
     * KeyStore file paths
     */
    private final Set<String> keystoreProperties = new TreeSet<>();

    /**
     * ProxiesBean map
     */
    private final Map<Integer, ProxyBean> proxyProperties =
            new HashMap<>();

    /**
     * End point map - endpoint and ref count
     */
    private final Map<String, Integer> endpoints =
            new TreeMap<>();

    /*
     * Is the properties config dirty and need saving to disk?
     */
    boolean dirty = false;


    /**
     * Constructor - create the properties directory
     */
    @SuppressWarnings("null")
    protected AbstractPropertiesModel(final String prefix,
            @Nullable final File directory,
            @Nullable final String filename)
    {

        propsDir = ObjectUtils.defaultIfNull(directory,
                new File(FilenameUtils.concat(getProperty(PROP_USER_HOME),
                        PROPERTIES_SUBDIR)));
        PROPERTIES_DIR = this.propsDir.getAbsolutePath();

        if (!propsDir.exists())
        {
            propsDir.mkdir();
        }

        String file =
                StringUtils.defaultIfBlank(filename, PROPERTIES_FILENAME);

        if (!isWhitespace(prefix))
        {
            file = prefix + file;
        }

        PROPERTIES_FILE = FilenameUtils.concat(PROPERTIES_DIR, file);
        propsFile = new File(PROPERTIES_FILE);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getRequests()
     */
    @Override
    public final List<Request> getRequests()
    {
        return new ArrayList<>(requestProperties.values());
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getRequests()
     */
    @Override
    public final List<Proxy> getProxies()
    {
        return new ArrayList<>(proxyProperties.values());
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getRequests()
     */
    @Override
    public final List<String> getKeyStores()
    {
        return new ArrayList<>(keystoreProperties);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getPropertiesDir()
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
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#removeData(com.github.technosf.posterer.models.Properties.impl.PropertiesModel.Request)
     */
    @SuppressWarnings("null")
    @Override
    public final boolean removeData(final @Nullable Request request)
    {
        if (request != null)
        {
            RequestBean pdi = new RequestBean(request);

            if (pdi.isActionable()
                    && (requestProperties.remove(pdi.hashCode()) != null)) // Check and remove the properties
            {
                return erase(pdi);
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Properties#save()
     */
    @Override
    public final synchronized boolean save()
    {
        if (dirty && write())
        {
            dirty = false;
            return true;
        }
        return false;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Properties#addData(com.github.technosf.posterer.models.Proxy)
     */
    @SuppressWarnings("null")
    @Override
    public final boolean addData(final @Nullable File keyStoreFile)
    {
        boolean result = false;
        try
        {
            String filepath;
            if (keyStoreFile != null
                    && keyStoreFile.canRead()
                    && (result = !keystoreProperties
                            .contains(filepath =
                                    keyStoreFile.getCanonicalPath())))
            {
                addKeystore(filepath);
                save();
            }
        }
        catch (IOException e)
        {
            // LOG.error("File issue for keystore file: {}", e);
        }
        return result;
    }


    /* ---------------------------------------------------------------- */

    /**
     * Erase the given requests from the configuration
     * 
     * @param requestBean
     *            the request to erase
     * @return true if the request was erased
     */
    protected abstract boolean erase(RequestBean requestBean);


    /**
     * Write out the current configuration
     * 
     * @return true if the config was written
     */
    protected abstract boolean write();


    /**
     * Adds a keystore path to the configuration
     * 
     * @param filepath
     *            the keystore file path
     */
    protected abstract void addKeystore(String filepath);


    /* ---------------------------------------------------------------- */

    /**
     * Is there a properties file?
     * 
     * @return true if there is
     */
    public final boolean isPropsFileExtant()
    {
        return propsFile.exists();
    }


    /**
     * Returns the size of the properties file in bytes.
     * 
     * @return the file size
     */
    public final long sizePropsFile()
    {

        if (isPropsFileExtant())
        {
            return sizeOf(propsFile);
        }

        return 0;
    }


    /**
     * Returns the absolute file system path to the properties file
     * 
     * @return the properties file path
     * @throws IOException
     *             exception accessing the properties file
     */
    @SuppressWarnings("null")
    public final String pathPropsFile() throws IOException
    {
        return propsFile.getAbsolutePath();
    }


    /* ---------------------------------------------------------------- */

    /**
     * @param requestBean
     * @return true if the RequestBean was added
     */
    @SuppressWarnings("null")
    protected final boolean putIfAbsent(RequestBean requestBean)
    {
        return null == requestProperties.putIfAbsent(requestBean.hashCode(),
                requestBean);
    }


    /**
     * @param requestBean
     * @return true if the RequestBean was added
     */
    @SuppressWarnings("null")
    protected final boolean putIfAbsent(ProxyBean proxyBean)
    {
        return null == proxyProperties.putIfAbsent(proxyBean.hashCode(),
                proxyBean);
    }


    /**
     * @param requestBean
     * @return true if the RequestBean was added
     */
    protected final boolean putIfAbsent(String keystorefilepath)
    {
        return keystoreProperties.add(keystorefilepath);
    }


    /**
     * Sets the dirty flag
     */
    protected final void dirty()
    {
        dirty = true;
    }


    /**
     * @return
     */
    protected final boolean isDirty()
    {
        return dirty;
    }


    /**
     * Add an endpoint to the current endpoint map
     * 
     * @param endpoint
     */
    protected final synchronized void addEndpoint(final String endpoint)
    {
        int endpointCount =
                (endpoints.containsKey(endpoint) ? endpoints.get(endpoint) : 0);
        endpoints.put(endpoint, ++endpointCount);
        dirty();
    }


    /**
     * Remove an endpoint from the current endpoint map
     * 
     * @param endpoint
     */
    protected final synchronized void removeEndpoint(final String endpoint)
    {
        int endpointCount =
                (endpoints.containsKey(endpoint) ? endpoints.get(endpoint) : 0);

        if (endpointCount > 1)
        {
            endpoints.put(endpoint, --endpointCount);
        }
        else
        {
            endpoints.remove(endpoint);
        }

        dirty();
    }
}
