package src;

import java.sql.Statement;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BomImage {
    
    
    public static void main(String[] args) throws Exception {
        BomImage main = new BomImage();
        main.getData();
    }

    public void getData() throws Exception {
        Connection conSource = null, conTarget = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver"); 
            conSource = DriverManager.getConnection(Common.sourceUrl, Common.sourceId, Common.sourcePw);
            conTarget = DriverManager.getConnection(Common.targetUrl, Common.targetId, Common.targetPw);
            String query = 
            " select WS_NO, RAW_FILE \n"+
            "   from PCC_CS_BOM_IMG where  \n"+
            "factory = 'DS'  \n"+
            "   and img_type = 'OUTSOLE' \n" +
            "   and ws_no in ( \n"+
            " 'WS202005000000000337' \n"+
            ",'WS202008000000000036' \n"+
            ",'WS202008000000000699' \n"+
            ",'WS202008000000000701' \n"+
            ",'WS202009000000000137' \n"+
            ",'WS202009000000000136' \n"+
            ",'WS202005000000000285' \n"+
            ",'WS202008000000000389' \n"+
            ",'WS202008000000000820' \n"+
            ",'WS202008000000000817' \n"+
            "   ) \n";

            stmt = conSource.createStatement();
            rs = stmt.executeQuery(query);
            

            while(rs.next()){
                
                String wsNo = rs.getString(1);
                Blob img = rs.getBlob(2);
                InputStream is = img.getBinaryStream();

                String iQ = "UPDATE ifc_pcc_bom_head SET OUTSOLE_IMG = ? "+
                " WHERE FACTORY = 'DS' AND WS_NO = ? ";
                pstmt = conTarget.prepareStatement(iQ);
                
                //pstmt.setBlob(1,img );
                pstmt.setBlob(1, is);
                pstmt.setString(2,wsNo);
                
               int a = pstmt.executeUpdate();
               System.out.println(rs.getString(1) + ">>>" + a);
               is.close();
            }
            
        }catch(Exception e){
            e.printStackTrace();
            
        }finally{
            rs.close();
            stmt.close();
            pstmt.close();
            conSource.close();
            conTarget.close();
        }
    }





 


}