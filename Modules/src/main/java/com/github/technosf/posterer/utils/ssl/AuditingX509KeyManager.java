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
package com.github.technosf.posterer.utils.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.function.BooleanSupplier;

import javax.net.ssl.X509KeyManager;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.core.utils.Auditor;

/**
 * X509KeyManager that audits activity
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class AuditingX509KeyManager
        implements X509KeyManager
{

    private final Auditor auditor;
    private final X509KeyManager keyManager;
    private boolean called = false;

    /* ----- State callback ----------- */

    public final BooleanSupplier wasCalled = new BooleanSupplier()
    {

        @Override
        public boolean getAsBoolean()
        {
            return called;
        }
    };


    /* ------  KeyManager code -------- */

    /**
     * @param auditor
     * @param keyManager
     */
    public AuditingX509KeyManager(Auditor auditor, X509KeyManager keyManager)
    {
        this.auditor = auditor;
        this.keyManager = keyManager;
    }


    @Override
    public @Nullable String chooseClientAlias(String @Nullable [] keyType,
            Principal @Nullable [] issuers,
            @Nullable Socket socket)
    {
        auditor.append(true, "SSL :: KeyManager chooseClientAlias");
        called = true;
        return keyManager.chooseClientAlias(keyType, issuers, socket);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String,
     *      java.security.Principal[], java.net.Socket)
     */
    @Override
    public @Nullable String chooseServerAlias(@Nullable String keyType,
            Principal @Nullable [] issuers, @Nullable Socket socket)
    {
        auditor.append(true, "SSL :: KeyManager chooseServerAlias");
        called = true;
        return keyManager.chooseServerAlias(keyType, issuers, socket);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
     */
    @Override
    public X509Certificate @Nullable [] getCertificateChain(
            @Nullable String alias)
    {
        auditor.append(true, "SSL :: KeyManager getCertificateChain");
        called = true;
        return keyManager.getCertificateChain(alias);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String,
     *      java.security.Principal[])
     */
    @Override
    public String @Nullable [] getClientAliases(@Nullable String keyType,
            Principal @Nullable [] issuers)
    {
        auditor.append(true, "SSL :: KeyManager getClientAliases");
        called = true;
        return keyManager.getClientAliases(keyType, issuers);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
     */
    @Override
    public @Nullable PrivateKey getPrivateKey(@Nullable String alias)
    {
        auditor.append(true, "SSL :: KeyManager getPrivateKey");
        called = true;
        return keyManager.getPrivateKey(alias);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String,
     *      java.security.Principal[])
     */
    @Override
    public String @Nullable [] getServerAliases(@Nullable String keyType,
            Principal @Nullable [] issuers)
    {
        auditor.append(true, "SSL :: KeyManager getServerAliases");
        called = true;
        return keyManager.getServerAliases(keyType, issuers);
    }

}
