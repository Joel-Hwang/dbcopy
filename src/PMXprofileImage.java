package src;

import java.sql.Statement;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class PMXprofileImage {

    public static void main(String[] args) throws Exception {
        PMXprofileImage main = new PMXprofileImage();
        main.getData();
    }

    public void getData() throws Exception {
        Connection conSource = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conSource = DriverManager.getConnection(Common.pmxSourceUrl, Common.pmxSourceId, Common.pmxSourcePw);

            String query = " select bottomimage, profileimage \n" + "   from dsprojectimages where  \n"
                    + "ida2a2 = '2681557'  \n";

            stmt = conSource.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {

                Blob btmimg = rs.getBlob(1);
                // Blob profimg = rs.getBlob(2);
                InputStream is = btmimg.getBinaryStream();
                StringBuffer buffer = new StringBuffer();
                byte[] b = new byte[4096];
                int i;
                while ((i = is.read(b)) != -1) {
                    buffer.append(new String(b, 0, i));
                }
                String str = buffer.toString();
                is.close();

                String base64Data = str.split(",")[1];
                byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
                
                String image_name = System.currentTimeMillis() + ".jpg";
                FileOutputStream fos = new FileOutputStream("C:\\Users\\cheol.hwang\\Downloads\\" + image_name);
                fos.write(decodedBytes);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            rs.close();
            stmt.close();
            conSource.close();
        }
    }

}