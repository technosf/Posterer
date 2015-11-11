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

import static org.easymock.EasyMock.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;
import static org.testng.FileAssert.assertFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.impl.KeyStoreBean.KeyStoreBeanException;

/**
 * Basic tests for {@code KeyStoreBean}
 * <p>
 * Test for:
 * <ul>
 * <li>Non-existing key store
 * <li>Key store with unknown pw
 * <li>Empty key store
 * <li>Test key store with certs
 * </ul>
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
@SuppressWarnings("null")
public class KeyStoreBeanTest
{
    /*
     * Temporary test Keystore file defs
     */
    private final static String unknownKeyStore =
            FileUtils.getTempDirectoryPath()
                    + File.separatorChar
                    + "unknownKeyStore.unknown";

    private final static String missingKeyStore =
            FileUtils.getTempDirectoryPath()
                    + File.separatorChar
                    + "missingKeyStore.jks";

    private final static String emptyKeyStore = FileUtils.getTempDirectoryPath()
            + File.separatorChar
            + "emptyKeyStore.jks";

    private final static Set<String> emptySet = new HashSet<String>();
    private final static Set<String> populatedAliasSet =
            new HashSet<String>(Arrays.asList("testcert1",
                    "selfsigned",
                    "technosf.github.com"));

    /*
     * The main keystore file and it's info
     */
    @Nullable
    File testKeyStoreFile;
    String password = "changeit";
    char[] passwordchr = password.toCharArray();

    /*
     * Use one test class so we can segment the tests better via dependencies
     */
    @NonNull
    KeyStoreBean classUnderTest = mock(KeyStoreBean.class); // place holder


    /* ----------------- Setup and Teardown -------------------- */

    /**
     * Create clean temp key store files and ensure we can access the main test
     * key store file
     */
    @BeforeClass
    private void init()
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException
    {
        // Delete preexisting testing keystores
        FileUtils.deleteQuietly(FileUtils.getFile(missingKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(unknownKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(emptyKeyStore));

        // Get the keystore algo and create the ks in memory
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, passwordchr);

        // Write out unknown pw keystore
        FileOutputStream fos = new FileOutputStream(unknownKeyStore);
        ks.store(fos, "unknownpw".toCharArray());
        fos.close();
        assertFalse(FileUtils.getFile(missingKeyStore).exists());

        // Write out empty key store
        fos = new FileOutputStream(emptyKeyStore);
        ks.store(fos, passwordchr);
        fos.close();
        assertFalse(FileUtils.getFile(missingKeyStore).exists());

        // Check the main test key store
        URL testKeystoreURL = this.getClass().getResource("/testkeystore.jks");
        testKeyStoreFile = FileUtils.toFile(testKeystoreURL);
        assertNotNull(testKeyStoreFile);
    }


    /**
     * Remove temp key stores
     */
    @AfterClass
    private void cleanUp()
    {
        // Delete testing keystores
        FileUtils.deleteQuietly(FileUtils.getFile(missingKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(unknownKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(emptyKeyStore));
    }

    /* ---------------------- Tests -------------------------------- */

    /**
     * Test creation and initialization with null keystore spec
     * 
     * @throws KeyStoreBeanException
     */
    //    @Test(groups = {
    //            "init"
    //    })
    //    public void nullKeyStore()
    //    {
    //        try
    //        {
    //            new KeyStoreBean(null, password);
    //        }
    //        catch (KeyStoreBeanException e)
    //        {
    //            return;
    //        }
    //
    //        fail("Expected KeyStoreBeanException not thrown");
    //    }


    /**
     * Test creation and initialization of missing keystore
     * 
     * @throws KeyStoreBeanException
     */
    @Test(groups = {
            "init"
    })
    public void missingKeyStore()
    {
        assertFalse(FileUtils.getFile(missingKeyStore).exists());
        try
        {
            new KeyStoreBean(FileUtils.getFile(missingKeyStore), password);
        }
        catch (KeyStoreBeanException e)
        {
            return;
        }
        fail("Expected KeyStoreBeanException not thrown");

    }


    /**
     * Test creation and initialization of unknown password keystore
     * 
     * @throws KeyStoreBeanException
     */
    @Test(groups = {
            "init"
    })
    public void unknownKeyStore()
    {
        assertFile(FileUtils.getFile(unknownKeyStore));

        try
        {
            new KeyStoreBean(FileUtils.getFile(unknownKeyStore),
                    "wrong password");
        }
        catch (KeyStoreBeanException e)
        {
            return;
        }

        fail("Expected KeyStoreBeanException not thrown");
    }


    /**
     * Test creation and initialization of empty keystore
     * 
     * @throws KeyStoreBeanException
     */
    @Test(groups = {
            "init"
    })
    public void emptyKeyStore()
    {
        assertFile(FileUtils.getFile(emptyKeyStore));
        try
        {
            classUnderTest = new KeyStoreBean(FileUtils.getFile(emptyKeyStore),
                    password);
        }
        catch (KeyStoreBeanException e)
        {
            fail("Unexpected exception", e);
        }

        assertNotNull(classUnderTest);
        assertEquals(classUnderTest.getPassword(), password);
        assertEquals(classUnderTest.getSize(), 0);
        assertEquals(classUnderTest.getAliases(), emptySet);
        assertNull(classUnderTest.getCertificate("qwerty"));
    }


    /**
     * Test creation and initialization of a populated keystore
     * 
     * @throws KeyStoreBeanException
     */
    @Test(dependsOnGroups = {
            "init"
    })
    public void populatedKeyStore()
    {
        assertFile(FileUtils.getFile(testKeyStoreFile));
        try
        {
            classUnderTest = new KeyStoreBean(
                    FileUtils.getFile(testKeyStoreFile), password);
        }
        catch (KeyStoreBeanException e)
        {
            fail("Unexpected exception", e);
        }
        assertNotNull(classUnderTest);
        assertEquals(classUnderTest.getPassword(), password);
        assertEquals(classUnderTest.getSize(), 3);
        assertEquals(classUnderTest.getAliases(), populatedAliasSet);
    }


    /**
     * @throws CertificateEncodingException
     */
    @Test(dependsOnMethods = {
            "populatedKeyStore"
    })
    public void aliasTestcert1() throws CertificateEncodingException
    {
        test509Cert("testcert1", 1084136385);
    }


    /**
     * @throws CertificateEncodingException
     */
    @Test(dependsOnMethods = {
            "populatedKeyStore"
    })
    public void aliasSelfsigned() throws CertificateEncodingException
    {
        test509Cert("selfsigned", 344375010);
    }


    /**
     * @throws CertificateEncodingException
     */
    @Test(dependsOnMethods = {
            "populatedKeyStore"
    })
    public void aliasTechnosf() throws CertificateEncodingException
    {
        test509Cert("technosf.github.com", 1557478189);
    }


    /* ---------------- Helpers --------------------------- */

    private void test509Cert(String alias, int serial)
    {
        Certificate cert = classUnderTest.getCertificate(alias);
        assertNotNull(cert);
        assertEquals(cert.getType(), "X.509");
        X509Certificate x509 = (X509Certificate) cert;
        assertEquals(x509.getSerialNumber().intValue(), serial);

    }
}
