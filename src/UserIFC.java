package src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/*
Lab
"byungkwan.lee","sungchul.kim","heeyeun.nam","junyong.park"
,"jonghoon.baek","jungwook.oh","taeseong.yu","jungsik.lee"
,"hwasook.lee","chiwon.jang","heesu.jeon"

IE



"seunghwan.baek"
,"yoonjung.lee"
,"ys.jeong"
,"minsu.choi"
,"junghee.kim"
,"jaehee.jang"
,"sunhee.cho"
,"young.jung"
,"TaeHwan.Ju"
,"kyunghwan.ko"
,"lynn.kim"
,"toehyun.kim"
,"hyunwoo.park"
,"minsu.son"
,"jinwoo.jung"
,"zoey.choi"



Workshop

"cheolsu.lee"
,"dokyung.kim"
,"hyunjin.cho"


3p


*/

public class UserIFC {
    public static void main(String[] args) throws Exception {
        // System.out.println(getUser("cheol.hwang"));
        String[] users = { 
            "myeongho.yeo"
            ,"EunKyoung.Kim"
            ,"byungjin.chun"
        };

        for (String userNm : users) {
            try {
                saveAras(getUser(userNm));
            } catch (Exception err) {
                System.out.println("error>>>" + userNm);
            }
        }

    }

    public static User getUser(String userName) throws Exception {
        String urlMyCs = Common.urlMyCs + userName;
        URL url = new URL(urlMyCs);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept",
                " text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Accept-Language", "ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7");
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        con.setRequestProperty("Cookie",
                "Login=; ASP.NET_SessionId=whf20rrjtkeewgvbebzjsanr; UTF8_Option=0; LoginCookie=3115531729471636534057662425178837581636367437033758570857661636233117885708409624255834341111073921570831152104233153174096294746053622370300504528073417880050005016721788071007102542178800500050; myEWSURL=57082104210447605317110715991599311553175834576621040050178831155317294716365340576624251788375816363674159937494813078915993749392137585708242558343411576617882425531736743921; SKINNUM=1;");

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // System.out.println("HTTP 응답 코드 : " + responseCode);
        // System.out.println("HTTP body : " + response.toString());

        Document doc = Jsoup.parse(response.toString());
        String company = doc.select("span#LiteralCompany").text();
        String dept = doc.select("span#LiteralDept").text();
        String name = doc.select("span#LiteralDisplayName").text();
        String email = doc.select("span#LiteralEmail").text();
        String phone = doc.select("span#LiteralPhone").text();
        String mobile = doc.select("span#LiteralMobile").text();
        String img = doc.select("th.popup_pic_bg img").attr("src");

        User user = new User();
        if (!"".equals(email)) {
            user.login_name = email.split("@")[0];
            user.first_name = name.substring(0, 1);
            user.last_name = name.substring(1);
            user.company_name = company;
            user.email = email;
            user.telephone = phone;
            user.cell = mobile;
            user.img = Common.urlMyCsRoot + img;
        } else {
            user.login_name = userName;
            user.first_name = userName.substring(0, 1);
            user.last_name = userName.substring(1);
            user.company_name = "창신INC";
            user.dept = dept;
            user.email = userName + "@changshininc.com";
            user.telephone = "010-0000-0000";
            user.cell = "010-0000-0000";
            user.img = "";
        }

        return user;
    }

    public static String token() throws Exception {
        String authServer = Common.authServer;
        String database = Common.database;
        String id = Common.arasId;
        String pw = Common.arasPw;

        URL url = new URL(authServer);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String parameters = String.format(
                "grant_type=password&scope=Innovator&client_id=IOMApp&username=%s&password=%s&database=%s", id, pw,
                database);

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(postData.length));
        con.setUseCaches(false);
        con.setDoOutput(true);
        con.setRequestProperty("grant_type", "password");
        con.setRequestProperty("scope", "Innovator");
        con.setRequestProperty("client_id", "IOMApp");
        con.setRequestProperty("username", id);
        con.setRequestProperty("password", pw);
        con.setRequestProperty("database", database);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());
        String token = (String) json.get("access_token");

        return token;
    }

    public static void saveAras(User user) throws Exception {
        String token = token();

        String apiServer = Common.apiServer;

        URL url = new URL(apiServer + "/USER");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        JSONObject userObj = new JSONObject();
        userObj.put("login_name", user.login_name);
        userObj.put("logon_enabled", user.logon_enabled);
        userObj.put("first_name", user.first_name);
        userObj.put("last_name", user.last_name);
        userObj.put("company_name", user.company_name);
        userObj.put("email", user.email);
        userObj.put("telephone", user.telephone);
        userObj.put("cell", user.cell);
        userObj.put("password", user.password);

        String parameters = userObj.toJSONString();

        byte[] postData = parameters.getBytes("UTF-8");
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(postData.length));
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setUseCaches(false);
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());

    }

}

class User {
    String login_name;
    String first_name;
    String last_name;
    String company_name;
    String password = "c4ca4238a0b923820dcc509a6f75849b";
    String email;
    String telephone;
    String cell;
    String img;
    boolean logon_enabled = true;
    String dept;

    @Override
    public String toString() {
        return String.format(
                "login_name : %s, first_name : %s, last_name : %s, company_name: %s, dept: %s, password : %s, email : %s, telephone : %s, cell : %s, img : %s",
                login_name, first_name, last_name, company_name, dept, password, email, telephone, cell, img);
    }

}