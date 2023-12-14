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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.core.utils.Auditor;

/**
 * Promiscuous Hostname Verify
 * <p>
 * Verifies all host names without exception.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class PromiscuousHostnameVerifier
        implements HostnameVerifier
{

    private final Auditor auditor;


    /**
     * @param auditor
     */
    public PromiscuousHostnameVerifier(final Auditor auditor)
    {
        this.auditor = auditor;
    }


    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
     *      javax.net.ssl.SSLSession)
     */
    @Override
    public boolean verify(@Nullable String hostname,
            @Nullable SSLSession session)
    {
        auditor.append(true, "SSL :: Verifying hostname: [%1$s]", hostname);
        return true;
    }

}
