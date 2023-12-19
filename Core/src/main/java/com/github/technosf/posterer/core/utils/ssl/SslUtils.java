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
package com.github.technosf.posterer.core.utils.ssl;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SSL Utilites
 * <p>
 * Provides utulities to manage SSL
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class SslUtils
{

    /**
     * Regex to pull SSL versions from SSL Context
     */
    private static String SSLCONTEXT_REGEX =
            "(?:\\.)?SSLContext.(?<ssl>\\w*v\\d\\.\\d|\\w*v\\d)";

    /**
     * Compiles Regex for SSL versions
     */
    private static Pattern SSLCONTEXT_PATTERN =
            Pattern.compile(SSLCONTEXT_REGEX);

    /**
     * 
     */
    private static List<String> SECURITY = new ArrayList<String>();


    /**
     * Interrogates the security providers and identifies SSL implementations.
     */
    private static void identifySecurity()
    {

        StringBuilder sb = new StringBuilder();

        try
        /*
         * Pull all security info into a string
         */
        {
            List<Provider> providers =
                    Arrays.asList(Security.getProviders());

            for (Provider provider : providers)
            {
                sb.append(provider).append("\n");
            }
            sb.append("\n");
            for (Provider provider : providers)
            {
                sb.append(provider).append("\n");
                for (Object o : new TreeSet<>(provider.keySet()))
                {
                    sb.append("\t").append(o).append("\n");
                }
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        /*
         * Parse security string for what we are interested in
         */
        Matcher matcher = SSLCONTEXT_PATTERN.matcher(sb.toString());
        while (matcher.find())
        {
            SECURITY.add(matcher.group("ssl"));
        }
    }


    /**
     * Returns the available SSL implementation versions
     * 
     * @return list of SSL versions
     */
    public static List<String> getSecurityChoices()
    {
        if (SECURITY.isEmpty())
        {
            identifySecurity();
        }
        return SECURITY;
    }
}
