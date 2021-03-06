package br.com.speedy.ipapp_me.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpConnection {
	public static String getSetDataWeb(String url, String method, String data){
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
        HttpGet httpGet = new HttpGet(url);
		String answer = "";
		
		try{

            if (method.equals("get-json")){
                HttpResponse resposta = httpClient.execute(httpGet);
                answer = EntityUtils.toString(resposta.getEntity(), "UTF-8");
            }else{
                /*ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
                valores.add(new BasicNameValuePair("method", method));
                valores.add(new BasicNameValuePair("json", data));*/

                StringEntity se = new StringEntity(data);

                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse resposta = httpClient.execute(httpPost);
                answer = EntityUtils.toString(resposta.getEntity(), "UTF-8");
            }

		}
		catch(NullPointerException e){
            e.printStackTrace();
        }
		catch(ClientProtocolException e){
            e.printStackTrace();
        }
		catch(IOException e){
            e.printStackTrace();
        }
		
		return(answer);
	}
}
