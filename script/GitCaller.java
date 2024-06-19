import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GitCaller {
    String hmclVersionsPath;

    String repositoryPath;

    public void init(String hmclVersionsPath, String repositoryPath) {
        this.hmclVersionsPath = hmclVersionsPath;
        this.repositoryPath = repositoryPath;
    }

    public List<String> getAllVersionDirs(String paramString) {
        File file = new File(paramString);
        if (!file.exists() || !file.isDirectory())
            throw new IllegalArgumentException("hmclVersionsPath is not exist: " + paramString);

        return Arrays.<File>stream(file.listFiles(File::isDirectory))
                .map(File::getName)
                .filter(str -> {
                    return !str.equals(".git");
                })
                .collect(Collectors.toList());
    }

    public void pull() {
        boolean bool = gitCMD("git", "pull");
        if (!bool)
            return;
        List<String> list = getAllVersionDirs(this.repositoryPath);
        for (String str1 : list) {
            String str2 = this.hmclVersionsPath + "/" + str1 + "/saves";
            File file = new File(str2);
            if (!file.exists() || !file.isDirectory())
                continue;
            String str3 = this.repositoryPath + "/" + str1;
            List<String> list1 = getAllVersionDirs(str3);
            if (list1.size() == 0)
                continue;
            for (String str : list1) {
                System.out.println("git -> local: " + str1 + "/" + str);
                copyFolder(str3 + "/" + str, str2 + "/" + str);
            }
        }
    }

    public void commit() {
        List<String> list = getAllVersionDirs(this.hmclVersionsPath);
        for (String str1 : list) {
            String str2 = this.hmclVersionsPath + "/" + str1 + "/saves";
            List<String> list1 = getAllVersionDirs(str2);
            if (list1.size() == 0)
                continue;
            String str3 = this.repositoryPath + "/" + str1;
            File file = new File(str3);
            if (!file.exists() || !file.isDirectory())
                file.mkdir();
            for (String str : list1) {
                System.out.println("local -> git: " + str1 + "/" + str);
                copyFolder(str2 + "/" + str, str3 + "/" + str);
            }
        }
        boolean bool = gitCMD("git", "add", ".");
        if (!bool)
            return;
        bool = gitCMD("git", "commit", "-a", "-m", "saves update");
        if (!bool)
            return;
        gitCMD("git", "push");
    }

    public void copyFolder(String paramString1, String paramString2) {
        try {
            File file1 = new File(paramString1);
            File file2 = new File(paramString2);
            file2.deleteOnExit();
            file2.mkdirs();
            File[] arrayOfFile = file1.listFiles();
            if (arrayOfFile != null)
                for (File file3 : arrayOfFile) {
                    File file4 = new File(file2, file3.getName());
                    if (file3.isFile()) {
                        Files.copy(file3.toPath(), file4.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } else if (file3.isDirectory()) {
                        copyFolder(file3.toPath().toString(), file4.toPath().toString());
                    }
                }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public boolean gitCMD(String... paramVarArgs) {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        processBuilder.command(paramVarArgs);
        processBuilder.directory(new File(repositoryPath));
        try {
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str;
            while ((str = bufferedReader.readLine()) != null)
                System.out.println(str);
            int i = process.waitFor();
            if (i != 0) {
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String str2;
                while ((str2 = bufferedReader2.readLine()) != null)
                    System.out.println(str2);
                System.out.println("git cmd faild: " + i);
                return false;
            }
            return true;
        } catch (IOException | InterruptedException iOException) {
            iOException.printStackTrace();
            return false;
        }
    }
}
