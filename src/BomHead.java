package src;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class BomHead {
    
    
    public static void main(String[] args) throws Exception {
        BomHead main = new BomHead();
        main.getData(2185805);  //IFC_ID 반드시 줘야 함 max(IFC_ID) + 1
    }

    public void getData(int ifcId) throws Exception {
        Connection conSource = null, conTarget = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver"); 
            conSource = DriverManager.getConnection(Common.sourceUrl, Common.sourceId, Common.sourcePw);
            conTarget = DriverManager.getConnection(Common.targetUrl, Common.targetId, Common.targetPw);
            String strCols = "FACTORY,WS_NO,DPA,BOM_ID,BOM_REV,ST_CD,SUB_ST_CD,SR_NO,SEASON_CD,PCC_ORG,CATEGORY,DEV_NAME,STYLE_CD,SAMPLE_ETS,SAMPLE_QTY,SAMPLE_SIZE,PROD_FACTORY,GENDER,TD,COLOR_VER,MODEL_ID,PATTERN_ID,LAST_CD,LAST_DIM,MIDSOLE_METHOD,OUTSOLE_METHOD,PCC_PM,BOM_LOAD_DATE,BOM_CFM_DATE,BOM_ISSUE_DATE,MTO_ACCOUNT,WHQ_DEV,STL_FILE,SAMPLE_WEIGHT,COLLAR_HEIGHT,HEEL_HEIGHT,MEDIAL_HEIGHT,LATERAL_HEIGHT,MS_HARDNESS,IDS_LENGTH,LACE_LENGTH,IPW,UPPER_MATERIAL,REQ_DATE,NIKE_DEV,NIKE_SEND_QTY,WATER_JET_YN,REWORK_YN,REWORK_COMMENT,XML_UPLOAD,WS_QTY,WS_STATUS,REQ_BOM_YN,CS_BOM_CFM,CBD_YN,PER_CD,TEMP_KEY,STATUS,UPD_USER,UPD_YMD,PROTO_YN,INNOVATION_YN,DEV_STYLE_ID,DEV_COLORWAY_ID,SOURCING_CONFIG_ID,PCX_BOM_ID,PRODUCT_ID,MATERIAL_VERSION,DEV_STYLE_NAME,DEV_STYLE_NUMBER,DEV_SAMPLE_REQ_ID,COLORWAY_NAME,MS_MATERIAL,OUTSOLE_MATERIAL,LENGTH_TOE_SPRING,MS_CODE,OS_CODE,UPLOAD_TYPE,SUB_TYPE_REMARK,CONVERT,REP_YN,GEL_YN,LOCK_YN";
            String[] cols = strCols.split(",");
            String query = 
            "select "+strCols+" \n"+
            " from PCC_BOM_HEAD where  \n"+
            "  factory = 'DS'  \n"+
            "   and ws_no in ( \n"+
            "'WS202010000000000939' \n"+
            ",'WS201908000000000024' \n"+
            ",'WS202011000000000419' \n"+
            "   ) \n";

            stmt = conSource.createStatement();
            System.out.println(query);
            rs = stmt.executeQuery(query);
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            while(rs.next()){
                String qryVal = "";
                HashMap<String,String> hR = new HashMap<>();
                for (String col : cols) {
                    hR.put(col, rs.getString(col));
                    qryVal = qryVal + "," + "?";
                }
                String iQ = "INSERT INTO IFC_PCC_BOM_HEAD (IFC_ID,IFC_STATUS,IFC_UPD_USER,IFC_UPD_YMD,IFC_FLAG, "+strCols+" )"+
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