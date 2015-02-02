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

import java.io.IOException;
import java.util.List;

/**
 * Model for managing properties and storing {@code Request} data.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface PropertiesModel
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
    List<RequestData> getData();


    /**
     * Adds {@code Request} objects to the properties to be maintained.
     * 
     * @param request
     *            the {@code Request} object to store
     */
    boolean addData(RequestData request);


    /**
     * Removed {@code Request} objects from the properties to be maintained.
     * 
     * @param request
     *            the {@code Request} object to remove
     * @return true if the {@code Request} was found and removed
     */
    boolean removeData(RequestData request);

}
