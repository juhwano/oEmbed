package io.purple.oembed.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class OembedService {
	private static JSONParser jsonParser = null;
	private static List<String> lst = null;
	private static JSONArray jsonArray = null;
	private URL url;

	// 프로바이더 데이터 map 생성
	public void providerData() throws IOException {
	    lst = new ArrayList<String>();
	    jsonParser = new JSONParser();
	        // 클래스패스리소스를 통해 providers.json을 불러와서 JSON으로 불러와야 한다.
	    ClassPathResource classPathResource = new ClassPathResource("json/providers.json");
	    BufferedReader rd = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()));
	    try {
	        Object obj = jsonParser.parse(rd);

	        JSONArray jsonArr = (JSONArray) obj;

	        for (int i = 0; i < jsonArr.size(); i++) {
	            JSONObject provider_url = (JSONObject) jsonArr.get(i);
	            String url = (String) provider_url.get("endpoints").toString();

	            // endpoints 데이터의 url만 가져오기
	            Object obj2 = jsonParser.parse(url);
	            jsonArray = new JSONArray();
	            jsonArray = (JSONArray) obj2;
	            JSONObject urlData = (JSONObject) jsonArray.get(0);

	            String value = (String) urlData.get("url");
	            lst.add(value);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
  
  
//호스트 체크
public String hostCheck(String str) {
   String result = "";
   try {
       url = new URL(str);

       String[] split = url.getHost().split("\\.");
       // split 데이터가 2개일 경우
       if (split.length == 2) {
           result = split[0];
       } else if (split.length == 3) {
           result = split[1];
       }
   } catch (MalformedURLException e) {
       e.printStackTrace();
   }
   return result;
}

//프로바이더 url + 포맷 + 인코드 합친 문자 만들기
public String createAddr(String host, String encode) {
  String oembedUrl = "";

  // 프로바이더 url을 검색해서 해당되는 url이 나오면 멈춘다
  for (String str : lst) {
      // 만약, url에 host(문자) 가 포함되어 있다면 멈추고 해당 데이터를 조합한다
      if (str.contains(host)) {
          // 프로바이더 url + ?format=json&url= + encode data
          if (str.contains("oembed.")) {
              // {format}이 들어있으면 json으로 변환해준다
              if (str.contains("{format}")) {
                  str = str.replace("{format}", "json");
              }

              // provider_url내에 oembed.이 포함되어 있을 경우
              // format=json을 하지 않는다.
              oembedUrl = str + "?url=" + encode;

          } else if (str.contains("_oembed")) {
              // instagram은 엑세스 토큰이 필요하다
              oembedUrl = "";
          } else {
              oembedUrl = str + "?format=json&url=" + encode;
          }
          break;
      }
  }

  return oembedUrl;
}
  

}
