package org.cups4j.operations;

import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cups4j.CupsAuthentication;
import org.cups4j.CupsPrinter;

public final class IppHttp {

	private static final int MAX_CONNECTION_BUFFER = 20;

	private static final int CUPSTIMEOUT = Integer.parseInt(System.getProperty("cups4j.timeout", "10000"));

	private static final boolean TRUST_ALL_CERTS = Boolean.parseBoolean(
			System.getProperty("cups4j.ssl.trustAll", "false"));

	private static final RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(CUPSTIMEOUT).setConnectTimeout(CUPSTIMEOUT)
			.build();

	private static final CloseableHttpClient client = createClient();

	private static CloseableHttpClient createClient() {
		HttpClientBuilder builder = HttpClientBuilder.create()
				.disableCookieManagement()
				.disableRedirectHandling()
				.evictExpiredConnections()
				.setMaxConnPerRoute(MAX_CONNECTION_BUFFER)
				.setMaxConnTotal(MAX_CONNECTION_BUFFER)
				.setRetryHandler(new DefaultHttpRequestRetryHandler());

		if (TRUST_ALL_CERTS) {
			try {
				SSLContext sslContext = createTrustAllSSLContext();
				SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
						sslContext, NoopHostnameVerifier.INSTANCE);
				builder.setSSLSocketFactory(sslSocketFactory);
			} catch (Exception e) {
				throw new RuntimeException("Failed to configure SSL trust-all context", e);
			}
		}

		return builder.build();
	}

	private static SSLContext createTrustAllSSLContext() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() { return null; }
				public void checkClientTrusted(X509Certificate[] certs, String authType) { }
				public void checkServerTrusted(X509Certificate[] certs, String authType) { }
			}
		};
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		return sslContext;
	}

	private IppHttp() {
	}

	public static CloseableHttpClient createHttpClient() {
		return client;
	}

	public static void setHttpHeaders(HttpPost httpPost, CupsPrinter targetPrinter,
			CupsAuthentication creds) {
		 if (targetPrinter == null) {
			 httpPost.addHeader("target-group", "local");
		 } else {
		 	 httpPost.addHeader("target-group", targetPrinter.getName());
		 }
	   httpPost.setConfig(requestConfig);

	   if (creds != null && StringUtils.isNotBlank(creds.getUserid())
	    		&& StringUtils.isNotBlank(creds.getPassword())) {
		    String auth = creds.getUserid() + ":" + creds.getPassword();
		    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
		    String authHeader = "Basic " + new String(encodedAuth);
		    httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
	   }
	}

}
