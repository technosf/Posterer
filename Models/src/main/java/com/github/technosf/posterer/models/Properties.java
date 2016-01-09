/*
 * Copyright 2014 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.technosf.posterer.models;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Model for managing properties and storing {@code Request} data.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface Properties
{

    /**
     * The name of directory in which to store preferences
     */
    static final String PROPERTIES_SUBDIR = ".posterer";

    /**
     * The name of file in which to store preferences
     */
    static final String PROPERTIES_FILENAME = "posterer.properties";


    /**
     * Save the current properties configuration
     *
     * @return true if the properties were saved
     */
    boolean save();


    /**
     * Returns the properties file contents as a {@code String}.
     * 
     * @return the properties file contents
     */
    String getBasicPropertiesFileContent();


    /**
     * Returns the path of the directory being used to store properties data
     * 
     * @return the properties file directory
     */
    String getPropertiesDir() throws IOException;


    /**
     * Returns stored requests as a {@code List}.
     * 
     * @return stored {@code Request} objects
     */
    List<Request> getRequests();


    /**
     * Adds {@code Request} objects to the properties to be maintained.
     * 
     * @param request
     *            the {@code Request} object to store
     */
    boolean addData(Request request);


    /**
     * Removed {@code Request} objects from the properties to be maintained.
     * 
     * @param request
     *            the {@code Request} object to remove
     * @return true if the {@code Request} was found and removed
     */
    boolean removeData(Request request);


    /**
     * Returns stored proxies as a {@code List}.
     * 
     * @return stored {@code Proxy} objects
     */
    List<Proxy> getProxies();


    /**
     * Adds {@code Proxy} objects to the properties to be maintained.
     * 
     * @param proxy
     *            the {@code Proxy} object to store
     */
    boolean addData(Proxy request);


    /**
     * Returns previously used local key stores
     * 
     * @return stored keysstores
     */
    List<String> getKeyStores();


    /**
     * Adds a keystore file location to the properties
     * 
     * @param keyStoreFile
     *            the keystore file ref
     * @return true if stored
     */
    boolean addData(File keyStoreFile);

}
