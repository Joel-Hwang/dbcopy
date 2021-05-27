import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PMXdownload {
/*
windchill 첨부파일 경로 구하기
SELECT WM.NAME                        AS "WTDOCUMENTMASTER.NAME"
     , WC.VERSIONIDA2VERSIONINFO ||'.' || WC.ITERATIONIDA2ITERATIONINFO ||'(' || WC.STATECHECKOUTINFO ||')' AS VERSION
     , APP.ROLE                      AS "ROLE"
     , APP.FILENAME                  AS ORIGINAL_FILENAME
     , TO_CHAR(FVI.UNIQUESEQUENCENUMBER,'FM0000000000000x')AS STORED_FILENAME
     , FVM.PATH ||'/' || TO_CHAR(FVI.UNIQUESEQUENCENUMBER,'FM0000000000000x')AS STORED_FILEPATH
     ,'cp ' || FVM.PATH ||'/' || TRIM(TO_CHAR(FVI.UNIQUESEQUENCENUMBER,'0000000000000x')) ||' ' ||'~/temp/' || APP.FILENAMEAS COMMAND
FROM   WTDOCUMENTMASTER WM
JOIN   WTDOCUMENT WCON WM.IDA2A2=WC.IDA3MASTERREFERENCEAND WC.STATECHECKOUTINFOIN ('c/i','c/o')
JOIN   HOLDERTOCONTENT HTCON WC.CLASSNAMEA2A2=HTC.CLASSNAMEKEYROLEAOBJECTREFAND WC.IDA2A2=HTC.IDA3A5
JOIN   APPLICATIONDATA APPON HTC.IDA3B5=APP.IDA2A2
JOIN   FVITEM FVION FVI.IDA2A2=APP.IDA3A5
JOIN   FVFOLDER FVFON FVI.IDA3A4=FVF.IDA2A2
JOIN   FVMOUNT FVMON FVF.IDA2A2=FVM.IDA3A5
WHERE  WM.NAME ='TheNameOfWTDocument'
AND    WC.LATESTITERATIONINFO = 1;

*/


    final static String mainUrl = "http://pmx.dskorea.com";
    public static void main(String[] args) throws Exception{
        String[] prjIds = {"2683815",
        "2687184",
        "2691703",
        "2683778",
        "2680598"};
        String cookie = "";
       
        FileWriter wr = new FileWriter("C:\\Users\\cheol.hwang\\Downloads\\test.txt");
        for(String prjId : prjIds){
            ArrayList<String> folders = getFolders(prjId, cookie);

            ArrayList<PmxFile> totFiles = new ArrayList<>();
            for(String folder : folders){
                ArrayList<PmxFile> files = files(prjId,cookie,folder);
                totFiles.addAll(files);
            }
     
            for(PmxFile f : totFiles){
                System.out.println(f);
                wr.append(f.toString()+"\n");
            }
        }
        
        wr.flush();
        
        
        
    
    }

    public static ArrayList<String> getFolders(String prjId, String cookie) throws Exception{
        ArrayList<String> res = new ArrayList<>();

        URL url = new URL(mainUrl +"/Windchill/servlet/DhtmlxXMLServlet?cmd=getProjectDocumentTreeDataXML&pjtOid=ext.pmx.project.entity.DSProject:"+prjId+"&dhxr1622081244658=1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        //conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Host", mainUrl);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");
        conn.setRequestProperty("Cookie", cookie);
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        

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
            for(Element elem : doc.select("item")){
                res.add(elem.id());
            }
        
        }
        // 접속 해제
        conn.disconnect();
        return res;
    }

    public static ArrayList<PmxFile> files(String prjId, String cookie, String folder) throws Exception{
        ArrayList<PmxFile> res = new ArrayList<>();

        URL url = new URL(mainUrl+"/Windchill/ext/project/getProjectDocumentData");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        StringBuffer buffer = new StringBuffer(); 
        buffer.append("folderPath").append("=").append(URLEncoder.encode(folder, StandardCharsets.UTF_8)).append("&"); 
        buffer.append("pjtOid").append("=").append(URLEncoder.encode("ext.pmx.project.entity.DSProject:"+prjId,StandardCharsets.UTF_8)); 
        String param = buffer.toString();
        byte[] postData = param.getBytes( StandardCharsets.UTF_8 );

        conn.setRequestMethod("POST");
        conn.setDefaultUseCaches(false); 
        conn.setDoInput(true); 
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        conn.setRequestProperty("Accept-Language","ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7");
        conn.setRequestProperty("Accept-Encoding","identity");
        conn.setRequestProperty("Connection","keep-alive");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length ));
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        conn.setRequestProperty("Cookie", cookie);
        conn.setRequestProperty("Host",mainUrl);
        conn.setRequestProperty("Origin",mainUrl);
        conn.setRequestProperty("Referer",mainUrl+"/Windchill/ext/project/docInfo?pjtOid=ext.pmx.project.entity.DSProject:10516889&lMenuCode=project");
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
        conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
    

        
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
            String output = new String(out.toByteArray(), "UTF-8");

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(output);
            JSONArray files = (JSONArray)obj;
            for(Object f : files){
                JSONObject file = (JSONObject)f;
                String fileName = file.get("fileName").toString();
                String downURL = file.get("downURL").toString();
                String fileSize = file.get("fileSize").toString();
                String creator_txt = file.get("creator_txt").toString();
                res.add(new PmxFile(prjId, fileName, mainUrl + downURL, fileSize, creator_txt));
                
            }
           // System.out.println(output);
        }

        // 접속 해제
        conn.disconnect();
        return res;
    }

    
}

class PmxFile{
    String prjId;
    String fileName;
    String downURL;
    String fileSize;
    String creator_txt;
    public PmxFile(String prjId, String fileName, String downURL, String fileSize, String creator_txt){
        this.prjId = prjId;
        this.fileName = fileName;
        this.downURL = downURL;
        this.fileSize = fileSize;
        this.creator_txt = creator_txt;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format("%s\t%s\t%s\t%s\t%s", this.prjId, this.fileName,this.fileSize,this.creator_txt,this.downURL);
    }
}