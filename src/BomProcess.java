package src;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class BomProcess {
    
    
    public static void main(String[] args) throws Exception {
        BomProcess main = new BomProcess();
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
            String strCols = "FACTORY,WS_NO,PART_SEQ,FN_PCC_PROCESS_NAMES ('CS_BOM',FACTORY,WS_NO,PART_SEQ)";
            String[] cols = strCols.split(",");
            String query = 
            "select "+strCols+" \n"+
            " from PCC_CS_BOM_TAIL where  \n"+
            "  factory = 'DS'  \n"+
            "   and ws_no in ( \n"+
            " 'WS202005000000000337' \n"+
           /* ",'WS202008000000000036' \n"+
            ",'WS202008000000000699' \n"+
            ",'WS202008000000000701' \n"+*/
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
                
                String iQ = "UPDATE IFC_PCC_CS_BOM_TAIL SET PROCESS = ? \n"+
                " WHERE FACTORY = ? AND WS_NO = ? AND PART_SEQ = ? ";
                pstmt = conTarget.prepareStatement(iQ);
                
                pstmt.setString(1,rs.getString(4) );
                pstmt.setString(2,rs.getString(1));
                pstmt.setString(3,rs.getString(2) );
                pstmt.setString(4,rs.getString(3) );
                
               int a = pstmt.executeUpdate();
               System.out.println(rs.getString(2) +"," +rs.getString(3) + ">>>" + a);
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