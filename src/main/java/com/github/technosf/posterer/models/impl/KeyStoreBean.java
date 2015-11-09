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
package com.github.technosf.posterer.models.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@code KeyStore} as a java bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class KeyStoreBean
{

    @SuppressWarnings("null")
    private static final Logger LOG = LoggerFactory
            .getLogger(KeyStoreBean.class);

    /* ---------------- Private Classes ----------------------- */

    /**
     * Generic wrapper for exceptions met in this bean..
     */
    public class KeyStoreBeanException extends Exception
    {
        private static final long serialVersionUID = 5640384469629352735L;


        public KeyStoreBeanException(String message, Exception e)
        {
            super(message, e);
            LOG.debug(message, e);
        }


        public KeyStoreBeanException(String message)
        {
            super(message);
            LOG.debug(message);
        }
    }

    /* ---------------- Storage ----------------------- */

    private final File file;
    private final String fileName;
    private final String type;
    private final String password;

    private final KeyStore keyStore;
    private final int size;
    private final Map<String, Certificate> certificates =
            new HashMap<String, Certificate>();


    /* ---------------- Code ----------------------- */

    /**
     * Instantiates a {@code KeyStoreBean} wrapping the given keystore
     * <p>
     * Loads the Key Store file into a {@code KeyStore} and checks the password.
     * If the Key Store
     * can be accessed successfully, validation is successful..
     * 
     * @param file
     *            the KeyStore file
     * @param password
     *            the Key Store password
     * @throws KeyStoreBeanException
     *             Thrown when a {@code KeyStoreBean} cannot be created.
     */
    @SuppressWarnings("null")
    public KeyStoreBean(final File keyStoreFile, final String keyStorePassword)
            throws KeyStoreBeanException
    {
        file = keyStoreFile;
        password = keyStorePassword;

        InputStream inputStream = null;

        /*
         * Check file existence
         */
        if (keyStoreFile == null ||
                !keyStoreFile.exists()
                || !keyStoreFile.canRead())
        // Key Store File cannot be read
        {
            throw new KeyStoreBeanException("Cannot read Key Store file");
        }

        try
        // to get the file input stream
        {
            inputStream =
                    Files.newInputStream(keyStoreFile.toPath(),
                            StandardOpenOption.READ);
        }
        catch (IOException e)
        {
            throw new KeyStoreBeanException("Error reading Key Store file", e);
        }

        // Get the file name and extension
        fileName = FilenameUtils.getName(keyStoreFile.getName());
        String fileExtension = FilenameUtils
                .getExtension(keyStoreFile.getName().toLowerCase());

        /*
         * Identify keystore type, and create an instance
         */
        try
        {
            switch (fileExtension) {
                case "p12":
                    keyStore = KeyStore.getInstance("PKCS12");
                    break;
                case "jks":
                    keyStore = KeyStore.getInstance("JKS");
                    break;
                default:
                    throw new KeyStoreBeanException(
                            String.format("Unknown keystore extention: [%1$s]",
                                    fileExtension));
            }
        }
        catch (KeyStoreException e)
        {
            throw new KeyStoreBeanException("Cannot get keystore instance");
        }

        /*
         * Load the keystore data into the keystore instance
         */
        try
        {
            keyStore.load(inputStream, password.toCharArray());
        }
        catch (NoSuchAlgorithmException | CertificateException
                | IOException e)
        {
            throw new KeyStoreBeanException("Cannot load the KeyStore", e);
        }

        /*
         * Key store loaded, so config the bean
         */
        try
        {
            type = keyStore.getType();
            size = keyStore.size();

            Enumeration<String> aliasIterator = keyStore.aliases();
            while (aliasIterator.hasMoreElements())
            {
                String alias = aliasIterator.nextElement();
                certificates.put(alias, keyStore.getCertificate(alias));
            }
        }
        catch (KeyStoreException e)
        {
            throw new KeyStoreBeanException("Cannot process the KeyStore", e);
        }
    }


    /* -------------------- Getters and Setters ---------------- */

    /**
     * The key store file
     * 
     * @return the file
     */
    @SuppressWarnings("null")
    public String getFile()
    {
        return file.getAbsolutePath();
    }


    /**
     * The key store file name
     * 
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }


    /**
     * The key store type
     * 
     * @return the type
     */
    public String getType()
    {
        return type;
    }


    /**
     * The key store password
     * 
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }


    /**
     * The number of certificates in this key store
     * 
     * @return number of certificates
     */
    public int getSize()
    {
        return size;
    }


    /**
     * The certificate aliases in this key store
     * 
     * @return the certificate aliases
     */
    @SuppressWarnings("null")
    public Set<String> getAliases()
    {
        return certificates.keySet();
    }


    /**
     * Returns the certificate for the given aliased
     * 
     * @param alias
     *            the certificate aliases
     * @return the certificate
     */
    public Certificate getCertificate(String alias)
    {
        return certificates.get(alias);
    }

}
