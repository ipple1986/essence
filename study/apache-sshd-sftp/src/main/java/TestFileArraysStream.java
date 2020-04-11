import java.io.File;
import java.util.Arrays;

public class TestFileArraysStream {
    public static void main(String[] args) {
        //File file = new File(System.getProperty("user.dir").concat("/appache-sshd-sftp/src/main/resources/test"));
        File file = new File("demoPath");
        Arrays.stream(file.listFiles()).filter(f->f.getName().equals("a.xml")).forEach(f->f.delete());
    }
}
