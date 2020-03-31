/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalstatsfactions;

import java.io.BufferedReader;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class TornAPIwithTimeout{ 

    
    /**
     * Used in turning API into String
     * @param rd
     * @return
     * @throws IOException 
     */
  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  /**
   * Used in turning API into JSON object
   * @param url
   * @return
   * @throws IOException
   * @throws JSONException 
   */
  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    URL urll = new URL(url); 
    HttpURLConnection httpURLConnection = (HttpURLConnection) urll.openConnection();
    httpURLConnection.setRequestProperty("User-Agent", "Chrome");
    httpURLConnection.setConnectTimeout(30*1000);
    httpURLConnection.setReadTimeout(120*1000);
    InputStream is = httpURLConnection.getInputStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  
 
  /**
   * Use 
   * @param apiKey Users API Key
   * @param category Category to retrieve API info (i.e user,property,faction,company,market,torn etc).
   * @param id Id of the item to retrieve (i.e. User ID, Faction ID, item ID)
   * @param selections  Selections of the the category (i.e personalStats, etc.)
   * @return JSON object retrieved from the torn API site
   * @throws IOException
   * @throws JSONException 
   */
  public JSONObject connect(String apiKey,String category,String id,String selections) throws IOException, JSONException {
    //FIX ME : Find appropriate exception to be made here - try catch 
    String link="https://api.torn.com/"+category+"/"+id+"?selections="+selections+"&key="+apiKey;
    JSONObject json = readJsonFromUrl(link);
    return json;
  }
  
}