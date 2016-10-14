/************************************************************
 * Copyright (c) 2015, Lawrence Livermore National Security, LLC.
 * Produced at the Lawrence Livermore National Laboratory.
 * Written by Timothy Meier, meier3@llnl.gov, All rights reserved.
 * LLNL-CODE-673346
 *
 * This file is part of the OpenSM Monitoring Service (OMS) package.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (as published by
 * the Free Software Foundation) version 2.1 dated February 1999.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * OUR NOTICE AND TERMS AND CONDITIONS OF THE GNU GENERAL PUBLIC LICENSE
 *
 * Our Preamble Notice
 *
 * A. This notice is required to be provided under our contract with the U.S.
 * Department of Energy (DOE). This work was produced at the Lawrence Livermore
 * National Laboratory under Contract No.  DE-AC52-07NA27344 with the DOE.
 *
 * B. Neither the United States Government nor Lawrence Livermore National
 * Security, LLC nor any of their employees, makes any warranty, express or
 * implied, or assumes any liability or responsibility for the accuracy,
 * completeness, or usefulness of any information, apparatus, product, or
 * process disclosed, or represents that its use would not infringe privately-
 * owned rights.
 *
 * C. Also, reference herein to any specific commercial products, process, or
 * services by trade name, trademark, manufacturer or otherwise does not
 * necessarily constitute or imply its endorsement, recommendation, or favoring
 * by the United States Government or Lawrence Livermore National Security,
 * LLC. The views and opinions of authors expressed herein do not necessarily
 * state or reflect those of the United States Government or Lawrence Livermore
 * National Security, LLC, and shall not be used for advertising or product
 * endorsement purposes.
 *
 *        file: OsmApiSession.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.infiniband.opensm.plugin.net;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.AbstractObjectClientProtocol;
import gov.llnl.lc.net.NetworkConstants;
import gov.llnl.lc.net.NetworkProperties;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.net.ObjectSessionInterface;
import gov.llnl.lc.security.AbstractAuthenticationResponder;
import gov.llnl.lc.security.AuthenticationResponder;
import gov.llnl.lc.security.KeyStoreTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**********************************************************************
 * An OsmApiSession represents a transaction based connection between a
 * client and the OSM Monitoring Service on a remote host.  A session
 * can provide information about itself, as well the various API's that
 * are supported by the service.
 * <p>
 * @see  ObjectSession
 * @see  OsmObjectClient
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 11:50:16 AM
 **********************************************************************/
public class OsmApiSession extends ObjectSessionInterface implements OsmSession, CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = -2911042793122181215L;
  
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  /* the secure i/o streams and protocol shared for all api's in this session */
  private java.io.ObjectOutputStream os = null;
  private java.io.ObjectInputStream is = null;
  
  /**  the client side of the transaction protocol **/
  protected OsmObjectClient   ClientProtocol = null;
  
  private ObjectSession Session;
  private static OsmApiSession EventSession = null;
  
  /* session and connection info */
  private SSLSocketFactory SocketFactory = null;
  private SSLSocket SslSocket = null;
  
  private SSLContext my_ctx;

  private String[] my_ciphers;
  private String[] my_protocols;
  
  private String HostName = null;
  private String dBugLevel = null;
  private int PortNum = 0;
  private int SocketTimeout = 0;
  private boolean Connected = false;
  private boolean authenticated = false;
  private boolean childSession = false;

  /* the various api's that may or may not be used */
  private static OsmClientInterface ClientInterface = null;  
  private static OsmAdminInterface  AdminInterface  = null;
  private OsmEventInterface  EventInterface  = null;
  private static OsmTestInterface   TestInterface   = null;      
  
  /* the one and only OsmServiceManager */
  private volatile static OsmServiceManager OsmService = OsmServiceManager.getInstance();

  /** a session can spawn child sessions, using the same config **/
  private java.util.ArrayList <OsmApiSession> Clone_Sessions;
    
  /************************************************************
   * Method Name:
   *  getSSLSocket
  **/
  /**
   * Gets a new socket (to the host/port) without re-authenticating
   * because we have already established a secure connection.
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param hostName
   * @param portNum
   * @return
   * @throws Exception
   ***********************************************************/
  private SSLSocket getSSLSocket(String hostName, int portNum) throws Exception
  {
    SSLSocket sslSock = null;
    
    try
    {
      sslSock = (SSLSocket) SocketFactory.createSocket(hostName, portNum);
    }
    catch (IOException e)
    {
      logger.severe("1Exception: " + e.getMessage());
      logger.severe("Could not listen on port:" + PortNum);
      throw new IOException("Could not listen on port:" + PortNum);
    }

    try
    {
      sslSock.setSoTimeout(SocketTimeout);
      sslSock.startHandshake();
    }
    catch (SSLHandshakeException e)
    {        
      /* the handshake failed, so the certs must need to be installed */
      logger.severe("SSLHandshake exception: previous keystore certs worked, why not now?");
      return null;
    }
    return sslSock;
  }
  
  private SSLSocket getSSLSocket(String HostName) throws Exception
  {
    return getSSLSocket(HostName, null);
    
  }
    private SSLSocket getSSLSocket(String HostName, String PortNumber) throws Exception
    {
    NetworkProperties prop = new NetworkProperties();
    /* get the Hostname and portnumber for the service */    
    String hostName = HostName == null ? prop.getHostName(): HostName;
    PortNum = PortNumber == null? prop.getPortNumber(): Integer.parseInt(PortNumber);
    SocketTimeout = prop.getSocketTimeout();
    dBugLevel = prop.getDebugLevel();

    SSLSocket sslSock = null;
    boolean connected = false;
    int num_tries = 0;
    
    if(hostName != null)
      this.HostName = hostName;

    logger.info("HostName: " + HostName + ", and PortNum: " + PortNum);

    while (!connected && (num_tries < 3))
    {
      num_tries++;
      KeyStore ks = KeyStoreTools.getJKS_KeyStore(true, null);
      try
      {
        /*
         *    Cipher suite = TLS_DHE_DSS_WITH_AES_128_CBC_SHA
         *    Protocol = TLSv1
         *
         */
        my_ctx = SSLContext.getInstance("TLS");
        
        my_protocols = GetProtocolList(my_ctx);
        my_ciphers   = GetCipherList(my_ctx);

        my_ctx.init(KeyStoreTools.getKeyManagers(true, ks, null), KeyStoreTools.getTrustManagers(ks), null);
        
        my_protocols = GetProtocolList(my_ctx);
        my_ciphers   = GetCipherList(my_ctx);
        if(NetworkConstants.NET_DEBUG_ALL_LEVEL.equals(dBugLevel))
          printSSLContextInfo(my_ctx);

        SocketFactory = (SSLSocketFactory) my_ctx.getSocketFactory();
        if(NetworkConstants.NET_DEBUG_ALL_LEVEL.equals(dBugLevel))
          printSocketFactoryInfo(SocketFactory);
      }
      catch (Exception e)
      {
        logger.severe("Unsuppored Operation getting socket factory: " + e.getMessage());
        throw new IOException("Unsuppored Operation getting socket factory: " + e.getMessage());
      }

      try
      {
        sslSock = (SSLSocket) SocketFactory.createSocket(hostName, PortNum);
        
        // make sure the socket is set up with the preferred cipher and protocol
        sslSock.setEnabledProtocols(my_protocols);
        sslSock.setEnabledCipherSuites(my_ciphers);
        sslSock.setSoTimeout(SocketTimeout);
      }
      catch (IOException e)
      {
        logger.severe("IOException: " + e.getMessage());
        logger.severe("Could not set up SSL Socket on port:" + PortNum);
        throw new IOException("Could not set up SSL Socket on port:" + PortNum);
      }
      catch (Exception e)
      {
        logger.severe("Exception: " + e.getMessage());
        logger.severe("Unhandled exception setting up SSL Socket on port:" + PortNum);
        throw new IOException("Unhandled exception setting up SSL Socket on port:" + PortNum);
      }

      try
      {
        if(NetworkConstants.NET_DEBUG_ALL_LEVEL.equals(dBugLevel))
          printSocketInfo(sslSock);
        
        sslSock.startHandshake();   // this will throw an exception if cipher and protocol not correct
        connected = true;
      }
      catch (SSLHandshakeException e)
      {        
        /* the handshake failed, so the certs must need to be installed */
        logger.severe("The SSL Handshake failed for reason (" + e.getLocalizedMessage() + ")");
        logger.severe("Assuming I need to install the host certificates ???? ");
        KeyStoreTools.installCerts(ks, HostName, null);
      }
      catch (Exception e)
      {        
        /* the handshake failed, so the certs must need to be installed */
        logger.severe("Could not complete SSL Handshake, invalid type??: " + e.getMessage());
        throw new IOException("Could not complete SSL Handshake, invalid type??: " + e.getMessage());
      }

    }
    return sslSock;
  }
    
    /************************************************************
     * Method Name:
     *  printSocketInfo
    **/
    /**
     * A useful diagnostic when handshakes, authentication, or encryption
     * between server and client (socket) can not be established.
     *
     * @see     describe related java objects
     *
     * @param socket
     ***********************************************************/
    private void printSocketInfo(SSLSocket socket)
    {
      System.out.println("Socket class: " + socket.getClass());
      System.out.println("   Remote address = " + socket.getInetAddress().toString());
      System.out.println("   Remote port = " + socket.getPort());
      System.out.println("   Local socket address = " + socket.getLocalSocketAddress().toString());
      System.out.println("   Local address = " + socket.getLocalAddress().toString());
      System.out.println("   Local port = " + socket.getLocalPort());
      System.out.println("   Need client authentication = " + socket.getNeedClientAuth());
      SSLSession ss = socket.getSession();
      System.out.println("   Cipher suite = " + ss.getCipherSuite());
      System.out.println("   Protocol = " + ss.getProtocol());
      System.out.println();
      
      String[] protocols = socket.getSupportedProtocols();

//      System.out.println("Supported Protocols: " + protocols.length);
//      for(int i = 0; i < protocols.length; i++)
//      {
//          System.out.println(" " + protocols[i]);
//      }
//
      protocols = socket.getEnabledProtocols();

      System.out.println("Enabled Protocols: " + protocols.length);
      for(int i = 0; i < protocols.length; i++)
      {
          System.out.println(" " + protocols[i]);
      }
      
      String[] ciphers = socket.getEnabledCipherSuites();
      System.out.println("Enabled Ciphers: " + ciphers.length);
      for(int i = 0; i < ciphers.length; i++)
      {
          System.out.println(" " + ciphers[i]);
      }

      Certificate[] serverCerts = null;

      try 
      {
        serverCerts = socket.getSession().getPeerCertificates();
      } catch (SSLPeerUnverifiedException ex)
      {
          ex.printStackTrace();
      }
      if(serverCerts != null)
      {
        System.out.println("Retreived Server's Certificate Chain");

        System.out.println(serverCerts.length + ") Certifcates Found\n\n\n");
        for (int i = 0; i < serverCerts.length; i++)
        {
            Certificate myCert = serverCerts[i];
            System.out.println("====Certificate:" + (i + 1) + "====");
            System.out.println("-Public Key-\n" + myCert.getPublicKey());
            System.out.println("-Certificate Type-\n " + myCert.getType());

            System.out.println();
        }        
      }
      else
      {
        System.out.println("Could not retreive the Server's Certificate Chain");
        
      }
  }
    
    private void printSocketFactoryInfo(SSLSocketFactory socketFactory)
    {
      System.out.println("SocketFactory class: " + socketFactory.getClass());
      
      String[] defaultCiphers   = socketFactory.getDefaultCipherSuites();
      String[] supportedCiphers = socketFactory.getSupportedCipherSuites();
      
      for (int i = 0; i < defaultCiphers.length; i++)
      {
        System.out.println("====Default Cipher:" + (i + 1) + "====");
        System.out.println(defaultCiphers[i]);
        System.out.println();
      }
      
      for (int i = 0; i < supportedCiphers.length; i++)
      {
        System.out.println("====Supported Cipher:" + (i + 1) + "====");
        System.out.println(supportedCiphers[i]);
        System.out.println();
      }
    }
    
    private void printSSLContextInfo(SSLContext m_ctx)
    {
      System.out.println("SSLContext class: " + m_ctx.getClass());
      
      String[] protocolList   = GetProtocolList(m_ctx);
      
      for (int i = 0; i < protocolList.length; i++)
      {
        System.out.println("====Protocols:" + (i + 1) + "====");
        System.out.println(protocolList[i]);
        System.out.println();
      }
      
      String[] cipherList   = GetCipherList(m_ctx);
      
      for (int i = 0; i < cipherList.length; i++)
      {
        System.out.println("====Ciphers:" + (i + 1) + "====");
        System.out.println(cipherList[i]);
        System.out.println();
      }
    }
    
    protected String[] GetProtocolList(SSLContext m_ctx)
    {
        String[] preferredProtocols = { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" };
        String[] availableProtocols = null;

        SSLSocket socket = null;

        try
        {
            SSLSocketFactory factory = m_ctx.getSocketFactory();
            socket = (SSLSocket)factory.createSocket();

            availableProtocols = socket.getSupportedProtocols();
            Arrays.sort(availableProtocols);
        }
        catch(Exception e)
        {
            return new String[]{ "TLSv1" };
        }
        finally
        {
            if(socket != null)
              try
              {
                socket.close();
              }
              catch (IOException e)
              {
                e.printStackTrace();
              }
        }
        
        // return only the protocols both supported and preferred 
        List<String> aa = new ArrayList<String>();
        for(int i = 0; i < preferredProtocols.length; i++)
        {
            int idx = Arrays.binarySearch(availableProtocols, preferredProtocols[i]);
            if(idx >= 0)
                aa.add(preferredProtocols[i]);
        }
        return aa.toArray(new String[0]);
    }
    
    protected String[] GetCipherList(SSLContext m_ctx)
    {
        String[] preferredCiphers = {

            // *_CHACHA20_POLY1305 are 3x to 4x faster than existing cipher suites.
            //   http://googleonlinesecurity.blogspot.com/2014/04/speeding-up-and-strengthening-https.html
            // Use them if available. Normative names can be found at (TLS spec depends on IPSec spec):
            //   http://tools.ietf.org/html/draft-nir-ipsecme-chacha20-poly1305-01
            //   http://tools.ietf.org/html/draft-mavrogiannopoulos-chacha-tls-02
            "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305",
            "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305",
            "TLS_ECDHE_ECDSA_WITH_CHACHA20_SHA",
            "TLS_ECDHE_RSA_WITH_CHACHA20_SHA",

            "TLS_DHE_RSA_WITH_CHACHA20_POLY1305",
            "TLS_RSA_WITH_CHACHA20_POLY1305",
            "TLS_DHE_RSA_WITH_CHACHA20_SHA",
            "TLS_RSA_WITH_CHACHA20_SHA",

            // Done with bleeding edge, back to TLS v1.2 and below
            "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",

            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",

            // TLS v1.0 (with some SSLv3 interop)
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",

            "TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
            "TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
            "SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA",
            "SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA",

            // RSA key transport sucks, but they are needed as a fallback.
            // For example, microsoft.com fails under all versions of TLS
            // if they are not included. If only TLS 1.0 is available at
            // the client, then google.com will fail too. TLS v1.3 is
            // trying to deprecate them, so it will be interesting to see
            // what happens.
            "TLS_RSA_WITH_AES_256_CBC_SHA256",
            "TLS_RSA_WITH_AES_256_CBC_SHA",
            "TLS_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_RSA_WITH_AES_128_CBC_SHA"
        };

        String[] availableCiphers = null;

        try
        {
            SSLSocketFactory factory = m_ctx.getSocketFactory();
            availableCiphers = factory.getSupportedCipherSuites();
            Arrays.sort(availableCiphers);
        }
        catch(Exception e)
        {
            return new String[] {
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_RSA_WITH_AES_256_CBC_SHA",
                "TLS_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_RSA_WITH_AES_128_CBC_SHA",
                "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
            };
        }

        // return only the ciphers both supported and preferred 
        List<String> aa = new ArrayList<String>();
        for(int i = 0; i < preferredCiphers.length; i++)
        {
            int idx = Arrays.binarySearch(availableCiphers, preferredCiphers[i]);
            if(idx >= 0)
                aa.add(preferredCiphers[i]);
        }

        aa.add("TLS_EMPTY_RENEGOTIATION_INFO_SCSV");

        return aa.toArray(new String[0]);
    }
    
  
  /************************************************************
   * Method Name:
   *  OsmApiSession
  **/
  /**
   * Creates a new OsmApiSession by connecting to the remote host using the provided
   * username and password.  If the username and password are not provided, then authentication is
   * established via interactive command line.  If hostname is not provided, an attempt is made to
   * obtain it via the NetworkProperties object.
   *
   * Currently the name and password arguments are not implemented, just pass null
   *
   * @see     AuthenticationResponder
   * @see     OsmObjectClient
   *
    * @param HostName the url of the remote host providing the service
   * @param UserName  the user account name, or null
   * @param Password  the user account password, or null
   * @throws Exception
   ***********************************************************/
  protected OsmApiSession(String HostName, String PortNum, String UserName, String Password) throws Exception
  {
    Clone_Sessions    = new java.util.ArrayList<OsmApiSession>();
    PrintWriter out   = null;
    BufferedReader in = null;
    AuthenticationResponder optAuthRspnd = AbstractAuthenticationResponder.getAuthenticationResponder();
    String openResponse = null;

    try
    {
      SslSocket = getSSLSocket(HostName, PortNum);

      out = new PrintWriter(SslSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(SslSocket.getInputStream()));
      
      os = new java.io.ObjectOutputStream(SslSocket.getOutputStream());
      is = new java.io.ObjectInputStream(SslSocket.getInputStream());

      Connected = true;
    }
    catch (UnknownHostException e)
    {
      logger.severe("Don't know about host: " + HostName);
      throw new UnknownHostException("Don't know about host: " + HostName);
    }
    catch (IOException e)
    {
      logger.severe("Not catching this error?? - 111");
      logger.severe("IOException: " + HostName + " ; " + e.getMessage());
      if((e.getMessage() != null) && (e.getMessage().startsWith("Could not set up SSL Socket")))
      {
        // No service, or perhaps a handshake problem
        logger.severe("Unable to set up SSL socket, check to see if service is available, and if SSL TSL handshake is failing");
        System.err.println("Unable to set up SSL socket, check to see if service is available, and if SSL TSL handshake is failing");
        // bail here, no sense in continuing
        System.exit(1);
      }
      logger.severe("Couldn't get I/O for the connection to: " + HostName);
      throw new IOException("Couldn't get I/O for the connection to: " + HostName);
    }

    /* secure connection is established, now authenticate with user id and password */
    authenticated = optAuthRspnd.requestAuthentication(in, out);

    if(authenticated)
    {
      /* finally, hook this up to our protocol */
      ClientProtocol = new OsmObjectClient();
      openResponse = ClientProtocol.openSession(is, os);
      classLogger.severe("openSession response: " + openResponse);
      
      /* create the api's supported by this Session, and provide session info */
      ClientInterface = new OsmClientInterface(this);      
      AdminInterface  = new OsmAdminInterface(this);      
//      EventInterface  = new OsmEventInterface(this);      
      TestInterface   = new OsmTestInterface(this);      
    }
}
  
  /************************************************************
   * Method Name:
   *  cloneSession
  **/
  /**
   * Creates and returns a copy of this Session, but only if this
   * session is not a clone itself.  An active session can create
   * "child" sessions by inheriting the "parents" traits.  This is
   * useful to avoid the overhead of discovering and developing all
   * the session attributes and authentications.
   * 
   * The number of times a clone can be created is limited
   *
   * @see     this.MaxClones
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   * @throws Exception
   ***********************************************************/
  public OsmApiSession cloneSession() throws Exception
  {
    OsmApiSession clone = null;
    
    /* max number of clones allowed */
    if((Clone_Sessions != null) && (Clone_Sessions.size() < MaxClones))
    {
      clone = new OsmApiSession(this);
      if (clone != null)
      {
        this.Clone_Sessions.add(clone); 
      }
    }
    else
      logger.severe("Maximum number of clones (" + MaxClones + ") has been reached");
    return clone;
  }
  
  /************************************************************
   * Method Name:
   *  OsmApiSession
  **/
  /**
   * Copy constructor, but it really creates a child clone.  The parent maintains
   * knowledge of child sessions, and if the parent session dies, the children get
   * killed off (like vampires).
   *
   * @see     describe related java objects
   *
   * @param   describe the parameters if any
   *
   * @param session
   * @throws Exception
   ***********************************************************/
  private OsmApiSession(OsmApiSession session) throws Exception
  {
    AuthenticationResponder optAuthRspnd = AbstractAuthenticationResponder.getAuthenticationResponder();
    PrintWriter out   = null;
    BufferedReader in = null;
    String openResponse;
    this.childSession = true;

    if((session!= null) && session.authenticated && !session.childSession)
    {
      /* inherit from the session */
      this.authenticated = session.authenticated;
      this.HostName = session.HostName;
      this.PortNum = session.PortNum;
      this.SocketTimeout = session.SocketTimeout;
      this.SocketFactory = session.SocketFactory;
     
      try
      {
        SslSocket = getSSLSocket(HostName, PortNum);        
        out = new PrintWriter(SslSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(SslSocket.getInputStream()));
        
        os = new java.io.ObjectOutputStream(SslSocket.getOutputStream());
        is = new java.io.ObjectInputStream(SslSocket.getInputStream());
        Connected = true;
      }
      catch (UnknownHostException e)
      {
        logger.severe("Don't know about host: " + HostName);
        throw new UnknownHostException("Don't know about host: " + HostName);
      }
      catch (IOException e)
      {
        logger.severe("IOException: " + HostName + " ; " + e.getMessage());
        logger.severe("Couldn't get I/O for the connection to: " + HostName);
        throw new IOException("Couldn't get I/O for the connection to: " + HostName);
      }

      /* secure connection is established, now authenticate with parent session */
      optAuthRspnd.setClientSession(session.getSessionStatus());
      authenticated = optAuthRspnd.requestAuthentication(in, out);

      if(authenticated)
      {
        /* finally, hook this up to our protocol */
        ClientProtocol = new OsmObjectClient();
        openResponse = ClientProtocol.openSession(is, os);
        
        /* create the api's supported by this Session, and provide session info */
        ClientInterface = new OsmClientInterface(this);      
        AdminInterface  = new OsmAdminInterface(this);      
//        EventInterface  = new OsmEventInterface(this);      
        TestInterface   = new OsmTestInterface(this);      
      }
      else
      {
        logger.severe("  Authentication Denied (shouldn't happen - using auth parent??)");        
      }
    }
    else
    {
      logger.severe("Cannot create a clone session from this session (only from valid parents)");
      if(session == null)
        logger.severe("  parent session is null");
      else if (!session.authenticated)
        logger.severe("  parent session is not authenticated");
      else
        logger.severe("  parent session is a child (" + session.childSession + ")");
    }
}
  

  /************************************************************
   * Method Name:
   *  compareTo
  **/
  /**
   * Compares two sessions and returns 0 if some key
   * attributes match.
   *
   * @param   session to compare with
   *
   * @return  0 if the same, 1 otherwise
   ***********************************************************/

  private int compareTo(OsmApiSession session)
  {
    int rtnVal = 1;
    if(session == null)
      throw new NullPointerException();
    
    // just compare a few elements, and call it good
    if((session.PortNum == this.PortNum) &&
        (session.authenticated == this.authenticated) &&
        (session.childSession == this.childSession) &&
        (session.HostName.equalsIgnoreCase(this.HostName)) &&
        (session.is.equals(this.is)))
      rtnVal = 0;
    
    return rtnVal;
  }

  public OsmClientApi getClientApi()
  {
    /* return the class that implments this api */
    return ClientInterface;
  }

  public OsmAdminApi getAdminApi()
  {
    /* return the class that implments this api */
    return AdminInterface;
  }
//
//  @Override
//  public ObjectSession getSessionStatus()
//  {
//    Object inObj = null;
//    try
//    {
//      inObj = ClientProtocol.getSessionStatus();
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//      return null;
//    }
//    
//    if (inObj instanceof ObjectSession)
//    {
//      Session = (ObjectSession)inObj;
//      return Session;
//    }
//    return (ObjectSession)inObj;
//
//  }

  @Override
  public OsmEventApi getEventApi()
  {
    // force every EventApi to be in its own session, since its not "session safe"
    OsmEventApi rtnApi = null;
    if (EventSession == null)
    {
      try
      {
        EventSession = (OsmApiSession) OsmService.openSession(this);
        EventSession.EventInterface = new OsmEventInterface(this);
      }
      catch (Exception e)
      {
        logger.severe(e.getMessage());
        logger.severe(e.getStackTrace().toString());
        e.printStackTrace();
      }
    }
    rtnApi = EventSession.EventInterface;

    // put this in its own session, and the clients can share this
    return rtnApi;
  }
  
  @Override
  public OsmTestApi getTestApi()
  {
    return TestInterface;
  }

  private boolean closeIO() throws IOException
  {
    boolean rtnval = true;
    
    /* the connection is closing */
    logger.info("The socket address: " + SslSocket.getInetAddress() + " port: " + SslSocket.getPort() + " is closing");

    os.close();
    is.close();
    SslSocket.close();
    Connected = false;
    return rtnval;
  }

  /************************************************************
   * Method Name:
   *  close
  **/
  /**
   * Closes and releases the io stream and socket connection.  If
   * this is a parent session, then it first closes all of its
   * children, before closing itself.
   *
   * @return true
   * @throws Exception
   ***********************************************************/
  public boolean close() throws Exception
  {
    boolean rtnval = true;
    
    if(childSession)
    {
      closeIO();
    }
    else
    {
      /* this is a parent, so close all children before closing itself */
      for (OsmApiSession s : Clone_Sessions) 
      {
          s.close();        
      }
      closeIO();
    }      
    return rtnval;
  }

  /************************************************************
   * Method Name:
   *  getSessionClientProtocol
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionApi#getSessionClientProtocol()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public AbstractObjectClientProtocol getSessionClientProtocol()
  {
    return ClientProtocol;
  }

  /************************************************************
   * Method Name:
   *  isAuthenticated
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionApi#isAuthenticated()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public boolean isAuthenticated()
  {
    return authenticated;
  }

  /************************************************************
   * Method Name:
   *  isConnected
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionApi#isConnected()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public boolean isConnected()
  {
    // TODO Auto-generated method stub
    return Connected;
  }
  
  protected void setConnected(boolean connected)
  {
    Connected = connected;
  }
}
