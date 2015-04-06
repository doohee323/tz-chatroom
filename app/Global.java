import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;

import play.Application;
import play.GlobalSettings;
import play.Play;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

/**
 * 
 */
public class Global extends GlobalSettings {

  private static org.slf4j.Logger Logger = org.slf4j.LoggerFactory.getLogger(Global.class);

  @Override
  public Action<?> onRequest(Request request, Method actionMethod) {
    String runscopeInput = Play.application().configuration().getString("runscope.input");
    if (runscopeInput != null && runscopeInput.equals("Y")) {
      return runscope(request, actionMethod);
    }
    Logger.error("======" + request.uri());
    return super.onRequest(request, actionMethod);
  }

  private Action<?> runscope(Request request, Method actionMethod) {
    String runscopeUseYn = Play.application().configuration().getString("runscope.useYn");
    String runscopeUrl = Play.application().configuration().getString("runscope.url");
    String port = Play.application().configuration().getString("runscope.port");

    HttpClient httpclient = new DefaultHttpClient();
    String url = runscopeUrl + request.uri();
    String responseBody = null;
    try {
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      if (request.method().equals("GET") || request.method().equals("DELETE")) {
        Map map2 = request.queryString();
        String params = "";
        Set<String> keySet = map2.keySet();
        Iterator<String> keySetIterator = keySet.iterator();
        while (keySetIterator.hasNext()) {
          String key = keySetIterator.next();
          String val = request.getQueryString(key) == null ? "" : request.getQueryString(key);
          params += key + "=" + URLEncoder.encode(val, "UTF-8") + "&";
        }
        if (params.indexOf("runscope") > -1) {
          return super.onRequest(request, actionMethod);
        }
        params += "runscope=Y&";
        if (params.length() > 0)
          params = params.substring(0, params.length() - 1);
        if (url.indexOf("?") > -1)
          url = url.substring(0, url.indexOf("?"));
        if (runscopeUseYn != null && runscopeUseYn.equals("Y")) {
          HttpResponse response = null;
          if (request.method().equals("GET")) {
            HttpGet httpget = new HttpGet(url + "?" + params);
            httpget.addHeader("Runscope-Request-Port", port);
            response = httpclient.execute(httpget);
          } else if (request.method().equals("DELETE")) {
            HttpDelete httpdelete = new HttpDelete(url + "?" + params);
            httpdelete.addHeader("Runscope-Request-Port", port);
            response = httpclient.execute(httpdelete);
          }
          responseBody = response.getEntity().getContent().toString();
        }
      } else {
        List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        JsonNode json = request.body().asJson();
        if(json != null) {
          Iterator<Entry<String, JsonNode>> nodeIterator = json.getFields();
          while (nodeIterator.hasNext()) {
             Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();
             postData.add(new BasicNameValuePair(entry.getKey(), entry.getValue().asText()));
          }          
        } else {
          boolean bMulti = false;
          Map<String, String[]> map2 = request.body().asFormUrlEncoded();
          if (map2 == null) {
            map2 = request.body().asMultipartFormData().asFormUrlEncoded();
            bMulti = true;
          }
          Set<String> keySet = map2.keySet();
          Iterator<String> keySetIterator = keySet.iterator();
          while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            String val = null;
            if (bMulti) {
              val = request.body().asMultipartFormData().asFormUrlEncoded().get(key)[0];
            } else {
              val = request.body().asFormUrlEncoded().get(key)[0];
            }
            postData.add(new BasicNameValuePair(key, val));
          }
        }
        if (postData.toString().indexOf("runscope") > -1) {
          return super.onRequest(request, actionMethod);
        }
        postData.add(new BasicNameValuePair("runscope", "Y"));
        if (runscopeUseYn != null && runscopeUseYn.equals("Y")) {
          if (request.method().equals("POST")) {
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Runscope-Request-Port", port);
            httppost.setEntity(new UrlEncodedFormEntity(postData, "UTF-8"));
            responseBody = httpclient.execute(httppost, responseHandler);
          } else if (request.method().equals("PUT")) {
            HttpPut httpput = new HttpPut(url);
            httpput.addHeader("Runscope-Request-Port", port);
            httpput.setEntity(new UrlEncodedFormEntity(postData, "UTF-8"));
            responseBody = httpclient.execute(httpput, responseHandler);
          }
        }
      }
    } catch (ClientProtocolException e) {
      Logger.error(e.getMessage());
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }
    return super.onRequest(request, actionMethod);
  }

  /**
   * (non-Javadoc) Manages the application actives periodically.
   * 
   * @see play.GlobalSettings#onStart(play.Application)
   */
  @Override
  public void onStart(Application arg0) {
    super.onStart(arg0);
  }

  @Override
  public void onStop(Application arg0) {
    super.onStop(arg0);
  }

  @Override
  public Result onError(RequestHeader arg0, Throwable arg1) {
    Logger.error("onError ======" + arg0.uri() + " : " + arg1.getMessage());
    return super.onError(arg0, arg1);
  }
}
