import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.util.List;

public class LUMatInfo{
    public static void main(String[] args) throws Exception {
        String[] call1 = call();
        String jsalt = call1[0];
        String cookie = call1[1];
        String cookie2 = call2(jsalt,cookie);
        call3(cookie2);
    }

    public static String[] call() throws Exception{
        String jsalt = "";
        String cookie = "";
        URL url = new URL("https://www.nikeconnect.com/irj/portal");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Host", "www.nikeconnect.com");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");


        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            for (String value : header.getValue()) {
                if("Set-Cookie".equals(header.getKey())){
                    cookie = cookie + value+";";
                }
                System.out.println(header.getKey() + " :" + value);
            }
        }

        // 응답 내용(BODY) 구하기
        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            String output = new String(out.toByteArray(), "UTF-8");
            
            Document doc = Jsoup.parse(output);
            jsalt = doc.select("input[name=j_salt]").val();
           // System.out.println(jsalt);
            //System.out.println(cookie);
        
        }

        // 접속 해제
        conn.disconnect();
        return new String[]{jsalt,cookie};
    }

    public static String call2(String jsalt, String cookie) throws Exception{
        String cookie2 = "";
        String id = "";
        String pw = "";
        URL url = new URL("https://www.nikeconnect.com/irj/portal");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        StringBuffer buffer = new StringBuffer(); 
        buffer.append("login_submit").append("=").append("on").append("&"); 
        buffer.append("login_do_redirect").append("=").append("1").append("&"); 
        buffer.append("no_cert_storing").append("=").append("on").append("&"); 
        buffer.append("j_salt").append("=").append(jsalt).append("&"); 
        buffer.append("j_username").append("=").append(id).append("&"); 
        buffer.append("j_password").append("=").append(pw).append("&"); 
        buffer.append("uidPasswordLogon").append("=").append("로그온"); 

        String param = buffer.toString();
        byte[] postData = param.getBytes( StandardCharsets.UTF_8 );

        conn.setRequestMethod("POST");
        conn.setDefaultUseCaches(false); 
        conn.setDoInput(true); 
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        conn.setRequestProperty("Host","www.nikeconnect.com");
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language","ko-KR,en-US;q=0.7,en;q=0.3");
        conn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        conn.setRequestProperty("Origin","https://www.nikeconnect.com");
        conn.setRequestProperty("Connection","keep-alive");
        conn.setRequestProperty("Referer","https://www.nikeconnect.com/irj/portal");
        conn.setRequestProperty("Upgrade-Insecure-Requests","1");
        conn.setRequestProperty("Pragma","no-cache");
        conn.setRequestProperty("Cache-Control","no-cache");
        conn.setRequestProperty("Cookie", cookie);
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length ));
        
        // 서버로 전송 
        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write( postData );
            wr.flush();
         }

        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            for (String value : header.getValue()) {
                if("Set-Cookie".equals(header.getKey())){
                    cookie2 = cookie2 + value+";";
                }
            }
        }

        // 응답 내용(BODY) 구하기
        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            String output = new String(out.toByteArray(), "UTF-8");
        }

        // 접속 해제
        conn.disconnect();
        return cookie2;
    }

    public static void call3( String cookie) throws Exception{
        URL url = new URL("https://www.nikeconnect.com/Asia.CHN.Guangzhou.LTM_SSL/FWMCS_List_LTM.aspx");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestProperty("Cookie", cookie);
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Host","www.nikeconnect.com");
        conn.setInstanceFollowRedirects(false);
        //conn.setRequestProperty("Accept-Language", "ko-KR,en-US;q=0.7,en;q=0.3");
        //conn.setRequestProperty("Referer", "https://www.nikeconnect.com/Asia.CHN.Guangzhou.LTM_SSL/FWMain_LTM.aspx");
        //conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        //conn.setRequestProperty("Pragma","no-cache");
        //conn.setRequestProperty("Cache-Control","no-cache");

        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            for (String value : header.getValue()) {
                System.out.println(header.getKey() + " : " + value);
                if("Set-Cookie".equals(header.getKey())){
                    cookie = cookie + value+";";
                }
            }
        }
        System.out.println(cookie);

        // 응답 내용(BODY) 구하기
        /*int responseCode = conn.getResponseCode(); 
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8")); 
        String inputLine; 
        StringBuffer response = new StringBuffer(); 
        while ((inputLine = in.readLine()) != null) { 
            response.append(inputLine); } 
            in.close(); 
            // print result 
            System.out.println("HTTP 응답 코드 : " + responseCode); 
            System.out.println("HTTP body : " + response.toString());

*/
        /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer stringBuffer = new StringBuffer();
        String inputLine;

        while ((inputLine = bufferedReader.readLine()) != null)  {
            stringBuffer.append(inputLine);
        }
        bufferedReader.close();

        String response = stringBuffer.toString();*/
        
        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            
            String output = new String(out.toByteArray(),"CP936");
            System.out.println(output);
            Document doc = Jsoup.parse(output);
        }


        
        // 접속 해제
        conn.disconnect();
    }
}