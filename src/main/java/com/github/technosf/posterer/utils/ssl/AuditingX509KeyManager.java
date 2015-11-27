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

import javax.net.ssl.X509KeyManager;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.utils.Auditor;

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
    //private final X509KeyManager keyManager;


    /**
     * @param auditor
     * @param keyManager
     */
    public AuditingX509KeyManager(Auditor auditor)//, X509KeyManager keyManager)
    {
        this.auditor = auditor;
        //this.keyManager = keyManager;
    }


    @Override
    public @Nullable String chooseClientAlias(String @Nullable [] keyType,
            Principal @Nullable [] issuers,
            @Nullable Socket socket)
    {
        auditor.append(true, "SSL :: KeyManager chooseClientAlias");
        return null;
        //        return keyManager.chooseClientAlias(keyType, issuers, socket);
    }


    @Override
    public @Nullable String chooseServerAlias(@Nullable String keyType,
            Principal @Nullable [] issuers, @Nullable Socket socket)
    {
        auditor.append(true, "SSL :: KeyManager chooseServerAlias");
        return null;
        //return keyManager.chooseServerAlias(keyType, issuers, socket);
    }


    @Override
    public X509Certificate @Nullable [] getCertificateChain(
            @Nullable String alias)
    {
        auditor.append(true, "SSL :: KeyManager getCertificateChain");
        return null;
        //return keyManager.getCertificateChain(alias);
    }


    @Override
    public String @Nullable [] getClientAliases(@Nullable String keyType,
            Principal @Nullable [] issuers)
    {
        auditor.append(true, "KeyManager getClientAliases");
        return null;
        //return keyManager.getClientAliases(keyType, issuers);
    }


    @Override
    public @Nullable PrivateKey getPrivateKey(@Nullable String alias)
    {
        auditor.append(true, "KeyManager getPrivateKey");
        return null;
        //return keyManager.getPrivateKey(alias);
    }


    @Override
    public String @Nullable [] getServerAliases(@Nullable String keyType,
            Principal @Nullable [] issuers)
    {
        auditor.append(true, "KeyManager getServerAliases");
        return null;
        //return keyManager.getServerAliases(keyType, issuers);
    }

}
