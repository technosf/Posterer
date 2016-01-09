/*
 * Copyright 2015 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.modules.commons.transport.ssl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.function.BooleanSupplier;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import com.github.technosf.posterer.models.impl.KeyStoreBean;
import com.github.technosf.posterer.utils.Auditor;
import com.github.technosf.posterer.utils.ssl.AuditingX509KeyManager;
import com.github.technosf.posterer.utils.ssl.AuditingX509TrustManager;

/**
 * {@code LayeredConnectionSocketFactory} that provides audited SSL connections
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class AuditingSSLSocketFactory
        implements LayeredConnectionSocketFactory
{
    private final Auditor auditor;
    private final SSLContext sslContext;
    private final BooleanSupplier neededClientAuth;


    /**
     * @param auditor
     * @param security
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws CertificateException
     */
    @SuppressWarnings({ "null" })
    public AuditingSSLSocketFactory(Auditor auditor, String security)
            throws NoSuchAlgorithmException, KeyManagementException,
            UnrecoverableKeyException, KeyStoreException, FileNotFoundException,
            IOException, CertificateException
    {
        this.auditor = auditor;
        sslContext = SSLContext.getInstance(security);

        /* ---- Trust Manager ------ */

        TrustManager[] myTMs =
                new TrustManager[] {
                        new AuditingX509TrustManager(auditor, true) };

        neededClientAuth = new BooleanSupplier()
        {
            @Override
            public boolean getAsBoolean()
            {
                return false;
            }
        };
        // Initialize the security context
        sslContext.init(null, myTMs, null);
    }


    /**
     * @param auditor
     * @param security
     * @param keyStoreBean
     * @param alias
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws KeyStoreException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CertificateException
     */
    @SuppressWarnings({ "null" })
    public AuditingSSLSocketFactory(final Auditor auditor,
            final String security, final KeyStoreBean keyStoreBean,
            final String alias)
                    throws NoSuchAlgorithmException, KeyManagementException,
                    UnrecoverableKeyException, KeyStoreException,
                    FileNotFoundException,
                    IOException, CertificateException
    {
        this.auditor = auditor;
        sslContext = SSLContext.getInstance(security);

        /* ---- Trust Manager ------ */

        TrustManager[] myTMs =
                new TrustManager[] {
                        new AuditingX509TrustManager(auditor, true) };

        /* ----- KayStore Manager ----- */

        KeyManagerFactory managerFactory =
                KeyManagerFactory.getInstance("SunX509");

        managerFactory.init(keyStoreBean.getKeyStore(),
                keyStoreBean.getPassword().toCharArray());
        X509KeyManager keyManager =
                (X509KeyManager) managerFactory.getKeyManagers()[0];
        AuditingX509KeyManager km =
                new AuditingX509KeyManager(auditor, keyManager);
        KeyManager[] myKMs =
                new KeyManager[] { km };
        neededClientAuth = km.wasCalled;
        // Initialize the security context
        sslContext.init(myKMs, myTMs, null);
    }


    /**
     * {@inheritDoc}
     *
     * @see org.apache.http.conn.socket.ConnectionSocketFactory#connectSocket(int,
     *      java.net.Socket, org.apache.http.HttpHost,
     *      java.net.InetSocketAddress, java.net.InetSocketAddress,
     *      org.apache.http.protocol.HttpContext)
     */
    @Override
    public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host,
            InetSocketAddress remoteAddress, InetSocketAddress localAddress,
            HttpContext context)
                    throws IOException
    {
        if (sock == null)
        {
            sock = createSocket(context);
        }

        if (sock.getClass().isAssignableFrom(SSLSocket.class))
        {
            throw new AssertionError("Socket is not an SSLSocket: " + sock);
        }

        SSLSocket sslSocket = (SSLSocket) sock;
        auditSocket(sslSocket);

        if (localAddress != null)
        {
            sslSocket.bind(localAddress);
        }

        sslSocket.connect(remoteAddress, connectTimeout);

        return sslSocket;
    }


    /**
     * {@inheritDoc}
     *
     * @see org.apache.http.conn.socket.ConnectionSocketFactory#createSocket(org.apache.http.protocol.HttpContext)
     */
    @Override
    public Socket createSocket(HttpContext arg0) throws IOException
    {
        return sslContext.getSocketFactory().createSocket();
    }


    /**
     * {@inheritDoc}
     *
     * @see org.apache.http.conn.socket.LayeredConnectionSocketFactory#createLayeredSocket(java.net.Socket,
     *      java.lang.String, int, org.apache.http.protocol.HttpContext)
     */
    @Override
    public Socket createLayeredSocket(final Socket socket,
            final String host,
            final int port,
            final HttpContext context) throws IOException, UnknownHostException
    {
        return auditSocket(
                (SSLSocket) sslContext.getSocketFactory().createSocket(socket,
                        host, port,
                        true));
    }


    /**
     * @param sslSocket
     */
    private SSLSocket auditSocket(SSLSocket sslSocket)
    {
        sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener()
        {

            @Override
            public void handshakeCompleted(HandshakeCompletedEvent arg0)
            {
                auditor.append(true, "SSL :: Handshake event: [%1$s]",
                        arg0.getSocket().toString());
            }
        });

        return sslSocket;
    }


    /**
     * Was Client Auth needed for this call?
     * 
     * @return
     */
    public BooleanSupplier getNeededClientAuthSupplier()
    {
        return neededClientAuth;
    }

}
