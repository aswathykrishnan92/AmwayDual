package website.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Api {
	public static void main(String[] args) {
		String url="https://api-dv.amwayglobal.com/rest/oauth2/v1/token?client_id=jszrb8u4a8px4njubvvmax32&client_secret=9g4MdTqpfx&grant_type=client_credentials&scope=aboNum=670 salesPlanAff=010 partyId=2058947";
		String response=getResponse(url);
		System.out.println(response);
	}
	public static String getResponse(String url){

		//System.setProperty("http.proxyHost", "166.76.3.199");
		//System.setProperty("http.proxyPort", "80");

		HttpURLConnection conn = null;
		InputStream is = null;
		URL urlObj = null;                      
		BufferedReader rd = null;
		StringBuilder response = new StringBuilder();
		String line = "";

		try
		{
			urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			//conn.setRequestProperty("Referer", baseUrl);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			//conn.setRequestProperty("User-Agent", "SHC Automation/1.0 KTXN");
			conn.addRequestProperty("Accept", "application/json");
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.addRequestProperty("Cache-Control", "no-cache");
			conn.setConnectTimeout(80000);
			conn.setReadTimeout(80000);

			is = conn.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));

			while ((line = rd.readLine()) != null)
			{
				response.append(line);
			}                         
		}
		catch (MalformedURLException mue)
		{
			mue.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} finally{
			if(null!=conn)
				conn.disconnect();
		}

		return response.toString();
	}
}
