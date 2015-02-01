package com.ute.bihapi.wydarzeniatekstowe.httpRequest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * Klasa bazowa s³u¿¹ca do wysy³ania HTTP Get Request do serwera
 * otrzymuje gotowy ju¿ link i zwraca odpowiedŸ.
 * @author Robert2
 *
 */
public class HttpRequest implements HttpRequestInterface {
	
	private static HttpHost targetHost;
	private HttpResponse response;
	private DefaultHttpClient client;
	
	private HttpRequest()
	{
		targetHost = new HttpHost("api.bihapi.pl", 443, "https");
     	client = new DefaultHttpClient();  
     	Log.i("HttpREquest:42","Got connection");
     	client.getCredentialsProvider().setCredentials(new AuthScope(	targetHost.getHostName(), 
     			 														targetHost.getPort()), 
     			 														new UsernamePasswordCredentials("48514168606", "CqXWAJHmpmqL7m9xk9"));
	}
	
	private static class HttpRequestHolder { 
        private final static HttpRequest instance = new HttpRequest();
    }
 
    public static HttpRequest get() {
        return HttpRequestHolder.instance;
    }	
	
	public String execute(String HTTPLink)
	{
		try {
	      	 sslClient(client);
	      	 HttpGet httpget = new HttpGet(HTTPLink);
	      	 Log.i("HttpRequest:64","Sending to: "+HTTPLink);
	      	 response = client.execute(httpget);
	       	 
	   	}catch (Exception e) {
	   		System.out.println("!!!!!"+e);
	   	}
		int responseCode = response.getStatusLine().getStatusCode();
		Log.i("ResponseCode",response.getStatusLine().toString());
		switch(responseCode)
		{
		    case 200:
		    {
		        HttpEntity entity = response.getEntity();
		        if(entity != null)
		        {
		        	String responseBody = null;
		        	try {
						responseBody = EntityUtils.toString(entity);
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
		        	return responseBody;
		        }
		    }
		    case 400:
		    {
		    	Log.i("ResponseCode 400","Nie mo¿na wys³aæ wiadomoœci. Kod b³êdu HTTP: 400");
		    }
		///Dodaæ wiêcej responseCode - obs³uga b³êdów przy nieprawid³owoœciach
		} 
	   	return null;
	}
	
	private void sslClient(HttpClient client) {       
        try {
            X509TrustManager tm = new X509TrustManager() { 
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            MySSLSocketFactory ssf = new MySSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
          //  return new DefaultHttpClient(ccm, client.getParams());
        } catch (Exception e) {
        	System.out.println(e);
        }
    }
}
