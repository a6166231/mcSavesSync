import java.io.File;

public class LocalGit {
    public static void main(String[] args) {

        String gitType = "";
        if (args.length > 0) {
            gitType = args[0];
        }

        String ppath = LocalGit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String dpath = new File(ppath).getParent();

        CfgReader cfgReader = new CfgReader();
        cfgReader.init(dpath);
        GitCaller gitCaller = new GitCaller();
        gitCaller.init(cfgReader.hmclVersionsPath, cfgReader.repository_path);

        System.out.println("=========================================");

        if (gitType.equals("pull")) {
            gitCaller.pull();
        } else if (gitType.equals("commit")) {
            gitCaller.commit();
        }

    }
}
