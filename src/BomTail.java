package src;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class BomTail {
    
    
    public static void main(String[] args) throws Exception {
        BomTail main = new BomTail();
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
            String strCols = "FACTORY,WS_NO,PART_SEQ,LAM_SEQ,PART_NIKE_NO,PART_NIKE_NAME,PART_NIKE_TYPE,PART_NIKE_COMMENT,PART_CD,PART_NAME,PART_TYPE,BTTM,MXSXL_NUMBER,CS_CD,MAT_CD,MAT_NAME,MAT_COMMENT,MCS_NUMBER,COLOR_CD,COLOR_NAME,COLOR_COMMENT,M_COLOR_CD1,M_COLOR_CD2,M_COLOR_CD3,SORT_NO,REMARKS,UPD_USER,UPD_YMD,PTRN_PART_NAME,PUR_CHK_PMC,PUR_CHK_3P,PCX_SUPP_MAT_ID,PCX_COLOR_ID,STATUS,VENDOR_NAME,PCX_MAT_ID,COMBINE_YN,STICKER_YN,PTRN_PART_CD,MDSL_CHK,OTSL_CHK,CELL_COLOR,CS_PTRN_CD,CS_PTRN_NAME";
            String[] cols = strCols.split(",");
            String query = 
            "select "+strCols+" \n"+
            " from PCC_CS_BOM_TAIL where  \n"+
            "  factory = 'DS'  \n"+
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
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int ifcId = 3917033;

            while(rs.next()){
                String qryVal = "";
                HashMap<String,String> hR = new HashMap<>();
                for (String col : cols) {
                    hR.put(col, rs.getString(col));
                    qryVal = qryVal + "," + "?";
                }
                String iQ = "INSERT INTO IFC_PCC_CS_BOM_TAIL (IFC_ID,IFC_STATUS,IFC_UPD_USER,IFC_UPD_YMD,IFC_FLAG, "+strCols+" )"+
                            " VALUES (?,?,?,sysdate,? "+ qryVal +")";
                pstmt = conTarget.prepareStatement(iQ);

                
                pstmt.setInt(1,ifcId++ );
                pstmt.setString(2,"C" );
                pstmt.setString(3,"admin" );
                pstmt.setString(4,"N" );
                int idxQ = 5;
                for (int i = 0; i<cols.length; i++) {
                    String val = hR.get( cols[i] );
                    System.out.println((ifcId-1)+" : "+cols[i] + "  " + val + " => " + resultSetMetaData.getColumnType(i+1));
                    switch(resultSetMetaData.getColumnType(i+1)){
                        case 2://Numeric
                            Integer iVal = (val!=null && !val.equals("null"))?Integer.parseInt(val):null;
                            if(iVal == null) pstmt.setNull(idxQ++,0);
                            else pstmt.setInt(idxQ++,iVal );
                            break;
                        case 12: //varchar
                            pstmt.setString(idxQ++, val);
                        break;
                        case 93://timestamp
                            Timestamp st = (val != null && !val.equals("null"))?Timestamp.valueOf(val):null;
                            pstmt.setTimestamp(idxQ++, st);
                        break;
                        default:
                            pstmt.setString(idxQ++, val);
                        break;
                    }
                    
                }
                pstmt.executeUpdate();
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