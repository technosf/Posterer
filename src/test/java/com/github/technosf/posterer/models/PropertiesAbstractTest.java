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

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.Request;

/**
 * Basic tests for classes implementing {@code PropertiesModel}
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class PropertiesAbstractTest
{

    /* ------------------ Abstract Methods ------------------------- */

    /**
     * Get the concrete class under test
     * 
     * @return class under test
     */
    protected abstract Properties getClassUnderTest();


    /**
     * Return the expected properties directory.
     * 
     * @return the expected properties directory
     */
    protected abstract String expected_PropertiesDir();


    /* ------------------ Test Setup and Teardown -------------------- */

    /**
     * Ensure that the class under test is present
     */
    @BeforeClass
    public final void beforeClass()
    {
        assertNotNull(getClassUnderTest());
    }


    /* ------------------ Tests -------------------- */

    /**
     * Test that the correct properties directory is obtained.
     */
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

    /**
     * @return
     */
    @DataProvider(name = "get_set_Default")
    public final Object[][] dataProvider_get_set_Default()
    {
        return new Object[][] {
                {
                        "Null to Null", null, false
                },
                {
                        "Null to PropertiesData",
                        createMock(Request.class), true
                },
                {
                        "PropertiesData to PropertiesData",
                        createMock(Request.class), true
                },
                {
                        "PropertiesData to PropertiesData",
                        createMock(Request.class), true
                },
                {
                        "PropertiesData to Null", null, true
                },
                {
                        "Null to PropertiesData",
                        createMock(Request.class), true
                },
                {
                        "PropertiesData to Null", null, true
                },

                {
                        "Null to Null", null, false
                }
        };
    }

    // @Test(dataProvider = "get_set_Default", dependsOnMethods = { "getPropertiesDir" }, enabled =
    // false)
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


    /**
     * @return
     */
    @DataProvider(name = "get_add_Data")
    public final Object[][] dataProvider_set_get_Data()
    {
        Request one = mockPropertiesData("One");
        Request two = mockPropertiesData("Two");
        Request three = mockPropertiesData("Three");
        replay(one);

        return new Object[][] {
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
                        "Add dupe, remove known", true, one, false, false, two,
                        false
                },
                {
                        "Null remove", false, null, false, true, null, false
                }
        };
    }


    /**
     * Tests getting and setting data on PropertyData object
     * <p>
     * Fired using a <em>data provider</em>
     * 
     * @param desc
     *            the test description
     * @param testAdd
     *            true if adding data
     * @param requestDataToAdd
     *            data to add
     * @param expectedResultFromAdd
     *            expected result
     * @param testRemove
     * @param removePropertyData
     * @param expectedResultRemove
     */
    @Test(dataProvider = "get_add_Data")
    // , dependsOnMethods = { "get_set_Default" })
    public final void get_set_Data(String desc,
            boolean testAdd,
            Request requestDataToAdd,
            boolean expectedResultFromAdd,
            boolean testRemove,
            Request removePropertyData,
            boolean expectedResultRemove)
    {
        boolean actualResult;

        if (testAdd)
        // Test addition to PropertyData object
        {
            actualResult = getClassUnderTest().addData(requestDataToAdd);

            /*
             * Test the equivalence
             */
            assertEquals(actualResult, expectedResultFromAdd);
            if (expectedResultFromAdd)
            {
                assertTrue(listContainsPropertiesData(getClassUnderTest()
                        .getRequests(),
                        requestDataToAdd));
            }
        }

        if (testRemove)
        // Test removal from PropertyData object
        {
            actualResult = getClassUnderTest().removeData(requestDataToAdd);
            assertEquals(actualResult, expectedResultRemove);
            if (expectedResultRemove)
            {
                assertFalse(listContainsPropertiesData(getClassUnderTest()
                        .getRequests(), removePropertyData));
            }
        }
    }

    /* -------------------- Helpers ---------------------- */


    /**
     * Creates and trains a mock {@code RequestData} with the given name
     * <p>
     * The name is used as the value for each property
     * 
     * @param name
     *            the name of the mock
     * @return the mock {@code RequestData}
     */
    private Request mockPropertiesData(String name)
    {
        Request mockPropertiesData =
                createNiceMock(name, Request.class);
        expect(mockPropertiesData.getEndpoint()).andReturn(name).anyTimes();
        expect(mockPropertiesData.getMethod()).andReturn(name).anyTimes();
        expect(mockPropertiesData.getPayload()).andReturn(name).anyTimes();
        expect(mockPropertiesData.getContentType()).andReturn(name).anyTimes();
        expect(mockPropertiesData.getBase64()).andReturn(false).anyTimes();
        return mockPropertiesData;
    }


    /**
     * Checks that a {@code RequestData} contains a list of expected values
     * 
     * @param propertiesDataList
     *            the desired values
     * @param propertiesData
     *            the actual data model
     * @return true if they model
     */
    private boolean listContainsPropertiesData(
            List<Request> propertiesDataList,
            Request propertiesData)
    {
        for (Iterator<Request> implIterator =
                propertiesDataList.iterator(); implIterator
                        .hasNext();)
        //
        {
            Request impl = implIterator.next();

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
