
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CfgReader {
    public String repository_path;

    public String hmclVersionsPath;

    public void init(String ppath) {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(ppath + "/cfg.txt");
            try {
                properties.load(fileInputStream);
                this.repository_path = properties.getProperty("repository_path");
                this.hmclVersionsPath = properties.getProperty("HMCL_versions_path");
                System.out.println("repository_path: " + this.repository_path);
                System.out.println("HMCL_versions_path: " + this.hmclVersionsPath);
                fileInputStream.close();
            } catch (Throwable throwable) {
                try {
                    fileInputStream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }
}
