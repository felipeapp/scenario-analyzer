package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPSUtil {

	public static InputStreamReader getInputStreamReader(String url) {
		InputStream is = getInputStream(url);
		
		if (is == null)
			return null;
		
		return new InputStreamReader(is);
	}

	public static InputStream getInputStream(String url) {
		if (url.startsWith("https://")) {
			return HTTPSUtil.getInputStreamForHTTPSIgnoreCertificateValidness(url);
		} else {
			try {
				return new URL(url).openStream();
			} catch (FileNotFoundException ex) {
				// Just continue and return null because the URL was not found
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	private static InputStream getInputStreamForHTTPSIgnoreCertificateValidness(String url) {
		try {
			return getInputStreamForHTTPSIgnoreCertificateValidness(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static InputStream getInputStreamForHTTPSIgnoreCertificateValidness(URL url) {
		TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) { }
				
				public void checkServerTrusted(X509Certificate[] certs, String authType) { }
			}
		};

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			connection.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String rserver, SSLSession sses) {
					if (!rserver.equals(sses.getPeerHost()))
						System.out.println("Certificate <" + rserver + "> does not match host <" + sses.getPeerHost() + ">, but continuing anyway!");
					
					return true;
				}
			});

			connection.setDoOutput(true);

			return connection.getInputStream();
		} catch (FileNotFoundException e) {
			// Just continue and return null because the URL was not found
		}
		catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
