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
package com.github.technosf.posterer.models;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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

    private static final Logger logger = LoggerFactory
            .getLogger(KeyStoreBean.class);

    private final File keyStoreFile;
    private final String keyStoreFileExtension;
    private final String password;

    private final InputStream keyStoreFileInputStream;

    private boolean validated = false;
    private KeyStore keyStore;


    /**
     * Instantiates a {@code KeyStoreBean}, checking the parameters, but not
     * loading the KeyStore.
     * 
     * @param keyStoreFile
     *            the KeyStore file
     * @param password
     *            the Key Store password
     */
    public KeyStoreBean(final File keyStoreFile, final String password)
    {
        this.keyStoreFile = keyStoreFile;
        this.password = password;

        String fileExtension = null;
        InputStream inputStream = null;

        if (keyStoreFile != null && keyStoreFile.exists()
                && keyStoreFile.canRead())
        // Key Store File can be read
        {
            // Get the file extension
            fileExtension = FilenameUtils
                    .getExtension(keyStoreFile.getName().toLowerCase());

            try
            // to get the file input stream
            {
                inputStream =
                        Files.newInputStream(keyStoreFile.toPath(),
                                StandardOpenOption.READ);

            }
            catch (IOException e)
            {
                logger.debug("Cannot read Key Store file", e);
                inputStream = null;
            }
        }

        keyStoreFileExtension = fileExtension;
        keyStoreFileInputStream = inputStream;
    }


    /**
     * @return
     */
    public boolean isValid()
    {
        boolean isValid = false;

        if (keyStoreFileInputStream != null)
        {
            if (!validated)
            {
                validate();
            }

            isValid = (keyStore != null);
        }

        return isValid;
    }


    /**
     * Loads the Key Store file into a {@code KeyStore} and checks the password.
     * If the Key Store can be accessed successfully, validation is successful.
     * 
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     */
    public String validate()
    {
        if (!validated)
        {
            KeyStore ks = null;
            try
            {
                switch (keyStoreFileExtension)
                {
                    case "p12":

                        ks = KeyStore.getInstance("PKCS12");

                        break;
                    case "jks":
                        ks = KeyStore.getInstance("JKS");
                        break;
                    default:
                        logger.error("Unknown keystore extention: [{}]",
                                FilenameUtils
                                        .getExtension(keyStoreFile.getName()
                                                .toLowerCase()));
                        return null;
                }
            }
            catch (KeyStoreException e)
            {
                logger.error("Cannot get a KeyStore instance", e);
                return null;
            }

            try
            {
                ks.load(keyStoreFileInputStream, password.toCharArray());
            }
            catch (NoSuchAlgorithmException | CertificateException
                    | IOException e)
            {
                logger.error("Cannot load the KeyStore", e);
                return null;
            }

            validated = true;
            keyStore = ks;
        }

        return keyStore.toString();

    }
}
