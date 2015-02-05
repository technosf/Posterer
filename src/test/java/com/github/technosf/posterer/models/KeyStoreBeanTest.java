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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class KeyStoreBeanTest
{
    String unknownKeyStore = FileUtils.getTempDirectoryPath()
            + File.separatorChar
            + "unknownKeyStore.unknown";

    String missingKeyStore = FileUtils.getTempDirectoryPath()
            + File.separatorChar
            + "missingKeyStore.jks";

    String testKeyStore = FileUtils.getTempDirectoryPath() + File.separatorChar
            + "testKeyStore.jks";
    KeyStore ks;
    String password = "changeit";
    char[] passwordchr = password.toCharArray();
    KeyStoreBean classUnderTest;


    @BeforeClass
    private void init()
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException
    {
        FileUtils.deleteQuietly(FileUtils.getFile(missingKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(unknownKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(testKeyStore));

        ks = KeyStore.getInstance("JKS");
        ks.load(null, passwordchr);

        FileOutputStream fos = new FileOutputStream(unknownKeyStore);
        ks.store(fos, passwordchr);
        fos.close();

        fos = new FileOutputStream(testKeyStore);
        ks.store(fos, passwordchr);
        fos.close();
    }


    @AfterClass
    private void cleanUp()
    {
        FileUtils.deleteQuietly(FileUtils.getFile(missingKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(unknownKeyStore));
        FileUtils.deleteQuietly(FileUtils.getFile(testKeyStore));
    }


    @Test
    public void KeyStoreBean()
    {
        assertFalse(FileUtils.getFile(missingKeyStore).exists());
        assertTrue(FileUtils.getFile(testKeyStore).exists());

        classUnderTest =
                new KeyStoreBean(null, password);
        assertNotNull(classUnderTest);
        assertFalse(classUnderTest.isValid());

        classUnderTest =
                new KeyStoreBean(FileUtils.getFile(missingKeyStore), password);
        assertNotNull(classUnderTest);
        assertFalse(classUnderTest.isValid());

        classUnderTest =
                new KeyStoreBean(FileUtils.getFile(unknownKeyStore), password);
        assertNotNull(classUnderTest);
        assertFalse(classUnderTest.isValid());

        classUnderTest =
                new KeyStoreBean(FileUtils.getFile(testKeyStore), password);
        assertNotNull(classUnderTest);
    }


    @Test(dependsOnMethods = { "KeyStoreBean" })
    public void validate()
    {
        assertTrue(classUnderTest.isValid());
    }
}
