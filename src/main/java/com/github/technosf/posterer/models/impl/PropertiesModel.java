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
package com.github.technosf.posterer.models.impl;

import java.io.IOException;
import java.util.List;

/**
 * Model for managing user preferences.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface PropertiesModel
{

	public interface PropertiesData
	{

		String getEndpoint();


		String getPayload();


		String getMethod();


		String getContentType();


		Boolean getBase64();


		String getHttpUser();


		String getHttpPassword();

	}

	/**
	 * The name of directory in which to store preferences
	 */
	static final String PROPERTIES_SUBDIR = ".posterer";

	/**
	 * The name of file in which to store preferences
	 */
	static final String PROPERTIES_FILENAME = "posterer.properties";


	/**
	 * @return
	 */
	String getBasicPropertiesFileContent();


	/**
	 * Returns the path of the directory being used to store preference data
	 * 
	 * @return
	 */
	String getPropertiesDir() throws IOException;


	/**
	 * @return
	 */
	// PropertiesData getDefault();

	/**
     * 
     */
	// void setDefault(PropertiesData propertyData);

	/**
	 * @return
	 */
	List<PropertiesData> getData();


	/**
	 * @param propertyData
	 */
	boolean addData(PropertiesData propertyData);


	/**
	 * @param propertyData
	 * @return
	 */
	boolean removeData(PropertiesData propertyData);

}
