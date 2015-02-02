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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.PropertiesModel;
import com.github.technosf.posterer.models.RequestData;

public abstract class PropertiesModelAbstractTest
{

	protected abstract PropertiesModel getClassUnderTest();


	@BeforeClass
	public final void beforeClass()
	{
		assertNotNull(getClassUnderTest());
	}


	/*
     * 
     */

	protected abstract String expected_PropertiesDir();


	@Test(enabled = true)
	public final void getPropertiesDir()
	{
		String propertiesDir = null;
		try
		{
			propertiesDir = getClassUnderTest().getPropertiesDir();
		}
		catch (IOException e)
		{
			fail(propertiesDir);
		}

		assertNotNull(propertiesDir);
		assertEquals(propertiesDir, expected_PropertiesDir());

	}


	/*
     * 
     */

	@DataProvider(name = "get_set_Default")
	public final Object[][] dataProvider_get_set_Default()
	{
		return new Object[][]
			{
								{
												"Null to Null", null, false
								},
								{
												"Null to PropertiesData",
												createMock(RequestData.class), true
								},
								{
												"PropertiesData to PropertiesData",
												createMock(RequestData.class), true
								},
								{
												"PropertiesData to PropertiesData",
												createMock(RequestData.class), true
								},
								{
												"PropertiesData to Null", null, true
								},
								{
												"Null to PropertiesData",
												createMock(RequestData.class), true
								},
								{
												"PropertiesData to Null", null, true
								},

								{
												"Null to Null", null, false
								}
		};
}


	// @Test(dataProvider = "get_set_Default", dependsOnMethods = { "getPropertiesDir" }, enabled = false)
	// public final void get_set_Default(String desc, PropertiesData propertyData,
	// boolean change)
	// {
	// if (change)
	// {
	// assertNotEquals(getClassUnderTest().getDefault(), propertyData);
	// }
	// getClassUnderTest().setDefault(propertyData);
	// assertEquals(getClassUnderTest().getDefault(), propertyData);
	// }

	/*
     * 
     */

	private RequestData mockPropertiesData(String name)
	{
		RequestData mockPropertiesData =
						createNiceMock(name, RequestData.class);
		expect(mockPropertiesData.getEndpoint()).andReturn(name).anyTimes();
		expect(mockPropertiesData.getMethod()).andReturn(name).anyTimes();
		expect(mockPropertiesData.getPayload()).andReturn(name).anyTimes();
		expect(mockPropertiesData.getContentType()).andReturn(name).anyTimes();
		expect(mockPropertiesData.getBase64()).andReturn(false).anyTimes();
		return mockPropertiesData;
	}


	@DataProvider(name = "get_add_Data")
	public final Object[][] dataProvider_set_get_Data()
	{
		RequestData one = mockPropertiesData("One");
		RequestData two = mockPropertiesData("Two");
		RequestData three = mockPropertiesData("Three");
		replay(one);

		return new Object[][]
			{
								{
												"Null add", true, null, false, false, null, false
								},
								{
												"Null remove", false, null, false, true, null, false
								},
								{
												"Add one", true, one, true, false, null, false
								},
								{
												"Add dupe, remove unknown", true, one, false, false,
												two, false
								},
								{
												"Add dupe, remove known", true, one, false, false, two, false
								},
								{
												"Null remove", false, null, false, true, null, false
								}
		};
}


	@Test(dataProvider = "get_add_Data")
	// , dependsOnMethods = { "get_set_Default" })
	public final void get_set_Data(String desc,
					boolean testAdd,
					RequestData addPropertyData,
					boolean expectedResultAdd,
					boolean testRemove,
					RequestData removePropertyData,
					boolean expectedResultRemove)
	{
		boolean actualResult;

		if (testAdd)
		// Test addition
		{
			actualResult = getClassUnderTest().addData(addPropertyData);
			assertEquals(actualResult, expectedResultAdd);
			if (expectedResultAdd)
			{
				assertTrue(listContainsPropertiesData(getClassUnderTest()
								.getData(),
								addPropertyData));
			}
		}

		if (testRemove)
		// Test removal
		{
			actualResult = getClassUnderTest().removeData(addPropertyData);
			assertEquals(actualResult, expectedResultRemove);
			if (expectedResultRemove)
			{
				assertFalse(listContainsPropertiesData(getClassUnderTest()
								.getData(), removePropertyData));
			}
		}
	}


	/**
	 * @param propertiesDataList
	 * @param propertiesData
	 * @return
	 */
	private boolean listContainsPropertiesData(
					List<RequestData> propertiesDataList,
					RequestData propertiesData)
	{
		for (Iterator<RequestData> implIterator =
						propertiesDataList.iterator(); implIterator
						.hasNext();)
		{
			RequestData impl = implIterator.next();

			System.out.println(propertiesData.getEndpoint());
			System.out.println(impl.getEndpoint());

			if (StringUtils.equals(propertiesData.getEndpoint(),
							impl.getEndpoint())
							&& StringUtils.equals(propertiesData.getPayload(),
											impl.getPayload())
							&& StringUtils.equals(propertiesData.getMethod(),
											impl.getMethod())
							&& StringUtils.equals(propertiesData.getContentType(),
											impl.getContentType())
							&& propertiesData.getBase64().equals(impl.getBase64()))
				return true;
		}

		return false;
	}
}
