package io.purple.oembed.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import io.purple.oembed.service.OembedService;

@Controller
public class OembedController {

	  @Autowired
	  private OembedService oembedService;

	  @GetMapping("/")
	  public String home(Model model) throws ClientProtocolException, IOException, URISyntaxException {
	      // provider의 url 데이터들 넣어놓기
	     oembedService.providerData();

	      // providers.json 데이터 가져오기
	      // parser : try catch 해줘야 함
	      // 현재 방식 -------------------------------------------------
	      // 사용자가 입력한 url의 호스트명을 확인해서 split으로 나누고,
	      // split.length == 2는 첫번째 문자가 url에 포함되어 있는지 확인
	      // split.length == 3은 두번째 문자를 url에 포함되어 있는지 확인
	      // 입력한 url 문자에 아래와 같이 포함되는 문자는 url encoding 해준다
	      // 해당되는 url + ?format=json&url= + encoding된 문자를 합쳐서 해주면 oembed 완성!

	      return "/oembed";
	  }

	// oembed 리턴
	  @GetMapping("/oembedResponse")
	  @ResponseBody
	  public String oembedResponse(@RequestParam("userUrlData") String userUrlData)
	          throws ClientProtocolException, IOException {
	      String result = "";
	      try {
	          String host = oembedService.hostCheck(userUrlData);
	          String encode = URLEncoder.encode(userUrlData, StandardCharsets.UTF_8);
	          String oembedUrl = oembedService.createAddr(host, encode);

	          // httpclients를 생성해 데이터를 받아온다
	          CloseableHttpClient hc = HttpClients.createDefault();
	          HttpGet httpGet = new HttpGet(oembedUrl);

	          // httpGet Header에 content type을 지정해주고
	          httpGet.addHeader("Content-Type", "application/json");

	          // request 요청해서 Response 받는다
	          CloseableHttpResponse httpResponse = hc.execute(httpGet);

	          result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

	      } catch (Exception e) {
	          e.printStackTrace();
	          result = "";
	      }

	      return result;
	  }
}
