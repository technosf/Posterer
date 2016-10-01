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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.utils.Auditor;

/**
 * X509TrustManager that audits activity
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class AuditingX509TrustManager
        implements X509TrustManager
{

    /**
     * Pass the client certs back as accepted?
     */
    private final boolean acceptClientCerts;

    /**
     * client cert
     */
    private X509Certificate @Nullable [] clientCerts;

    private Auditor auditor;


    /**
     * Constructor for Trust Mnaager
     * 
     * @param AcceptClientCerts
     *            true is provided clients certs are passed back
     */
    public AuditingX509TrustManager(Auditor auditor,
            boolean acceptClientCerts)
    {
        this.acceptClientCerts = acceptClientCerts;
        this.auditor = auditor;
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
     *      java.lang.String)
     */
    @Override
    public void checkClientTrusted(X509Certificate @Nullable [] arg0,
            @Nullable String arg1)
                    throws CertificateException
    {

        if (arg0 != null)
        {

            if (acceptClientCerts)
                clientCerts = arg0;

            auditor.append(true,
                    "SSL :: TrustManager checkClientTrusted - Request Type :[%2$s]\n\tCertificate: [%1$s]",
                    arg0[0].getSubjectX500Principal().toString(), arg1);
        }
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
     *      java.lang.String)
     */
    @Override
    public void checkServerTrusted(X509Certificate @Nullable [] arg0,
            @Nullable String arg1)
                    throws CertificateException
    {
        if (arg0 != null)
            auditor.append(true,
                    "SSL :: TrustManager checkServerTrusted - Request Type :[%2$s]\n\tCertificate: [%1$s]",
                    arg0[0].getSubjectX500Principal().toString(), arg1);
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate @Nullable [] getAcceptedIssuers()
    {
        auditor.append(true, "SSL :: TrustManager getAcceptedIssuers");
        if (clientCerts != null)
        {
            auditor.append(false, "\tHanding back certificate :[%1$s]",
                    clientCerts[0].getSubjectX500Principal().toString());
        }
        return clientCerts;
    }

}
