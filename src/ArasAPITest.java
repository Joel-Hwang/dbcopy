import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class ArasAPITest{
    public static void main(String[] args) throws Exception {
        String url = "";
        String id = "";
        String pw = "";
        String db = "";
        String token = getToken(url, id, pw, db).replace("{\"access_token\":\"", "").replace("\",\"expires_in\":3600,\"token_type\":\"Bearer\",\"scope\":\"Innovator\"}", "");
        //System.out.println(token);
        getIdentity(url, token);
    }

    public static String getToken(String sUrl, String id, String pw, String db) throws Exception{
        String output = "";
        URL url = new URL(sUrl + "oauthserver/connect/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        StringBuffer buffer = new StringBuffer(); 
        buffer.append("grant_type").append("=").append("password").append("&"); 
        buffer.append("scope").append("=").append("Innovator").append("&"); 
        buffer.append("client_id").append("=").append("IOMApp").append("&"); 
        buffer.append("username").append("=").append(id).append("&"); 
        buffer.append("password").append("=").append(pw).append("&"); 
        buffer.append("database").append("=").append(db); 
    
        String param = buffer.toString();
        byte[] postData = param.getBytes( StandardCharsets.UTF_8 );

        conn.setRequestMethod("POST");
        conn.setDefaultUseCaches(false); 
        conn.setDoInput(true); 
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length ));
        
        // 서버로 전송 
        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write( postData );
            wr.flush();
         }

        // 응답 내용(BODY) 구하기
        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            output = new String(out.toByteArray(), "UTF-8");
        }

        // 접속 해제
        conn.disconnect();
        return output;
    }
    

    public static void getIdentity(String sUrl, String token) throws Exception{
        URL url = new URL(sUrl + "server/odata/Identity?$filter=classification%20eq%20%27Department%27%20and%20is_alias%20eq%20%270%27&$select=id,name,description" );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("Authorization", "Bearer "+token);
        conn.setRequestProperty("Host", "203.228.101.197");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Connection", "keep-alive");
    
        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            String output = new String(out.toByteArray(), "UTF-8");
            System.out.println(output);
        }

        // 접속 해제
        conn.disconnect();
    }
}