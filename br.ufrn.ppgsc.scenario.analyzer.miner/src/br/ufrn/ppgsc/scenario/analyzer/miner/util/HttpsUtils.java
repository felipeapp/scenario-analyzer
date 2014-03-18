package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class HttpsUtils 
{
	private final Logger logger = Logger.getLogger(HttpsUtils.class);
	/**
     * Gets an inputstreamreader for a https:// url, which ignores whether a certificate is valid
     * @param url
     * @return
     * @throws FRASRException
     */
    public static InputStreamReader getInputStreamReaderFixHttps(String url) 
    {
        return new InputStreamReader(getInputStreamFixHttps(url));
    }

    /**
     * Gets an inputstream which, in case a https:// url is provided, fixes invalid certificate exceptions etc
     * @param url
     * @return
     * @throws FRASRException
     */
    public static InputStream getInputStreamFixHttps(String url) 
    {
        if(url.startsWith("https://")) 
        {
            return HttpsUtils.getInputStreamForHTTPSIgnoreCertificateValidness(url);
        } 
        else 
        {
            try 
            {
                return new URL(url).openStream();
            } 
            catch(MalformedURLException ex) 
            {
                Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
            } 
            catch(IOException ex) 
            {
                Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
            }
        }
        
        return null;
    }
    
    /**
     * Gets an inputstream which, in case a https:// url is provided, fixes invalid certificate exceptions etc
     * @param url
     * @return
     * @throws FRASRException
     */
    public static InputStream getInputStreamFixHttpsWithExistingContents(String url, String value) 
    {
        if(url.startsWith("https://")) 
        {
            return HttpsUtils.getInputStreamForHTTPSIgnoreCertificateValidness(url);
        } 
        else 
        {
            try 
            {
                URL myurl = new URL(url);
                URLConnection connection = myurl.openConnection();
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write("issuelist="+value);
                out.close();
                return connection.getInputStream();
            } 
            catch(MalformedURLException ex) 
            {
                Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
            } 
            catch(IOException ex) 
            {
                Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
            }
        }
        
        return null;
    }

    
    /**
     * Gets an inputstream for a https:// url, which ignores whether a certificate is valid
     * @param url
     * @return
     * @throws FRASRException
     */
    public static InputStream getInputStreamForHTTPSIgnoreCertificateValidness(String url) 
    {
        try 
        {
            return getInputStreamForHTTPSIgnoreCertificateValidness(new URL(url));
        } 
        catch(MalformedURLException ex) 
        {
            Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
        }
        
        return null;
    }
    
    /**
     * Gets an inputstream for a https:// url, which ignores whether a certificate is valid
     * @param url
     * @return
     * @throws FRASRException
     */
    public static InputStream getInputStreamForHTTPSIgnoreCertificateValidness(URL url) 
    {
        TrustManager[] trustAllCerts = new TrustManager[]
        {
            new X509TrustManager() 
            {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs,String authType) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs,String authType) {
                }
            }
        };

        try 
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null,trustAllCerts,new java.security.SecureRandom());

            URL sslUrl = url;
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection connection = (HttpsURLConnection) sslUrl.openConnection();

            connection.setHostnameVerifier(
                    new HostnameVerifier() 
                    {

                        public boolean verify(String rserver,SSLSession sses)
                        {
                            if(!rserver.equals(sses.getPeerHost())) {
                                System.out.println("certificate <" + rserver + "> does not match host <" + sses.getPeerHost() + "> but continuing anyway");
                            }
                            return true;
                        }
                    });

            connection.setDoOutput(true);

            return connection.getInputStream();
        } 
        catch(IOException ex) 
        {
            Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
        } 
        catch(NoSuchAlgorithmException ex) 
        {
            Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
        } 
        catch(KeyManagementException ex) 
        {
            Logger.getLogger(HttpsUtils.class.getName()).log(Level.ERROR,null,ex);
        }
        
        return null;
    }
    
}
