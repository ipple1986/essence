import com.jcraft.jsch.*;

public class SftpClient {
    public static void main(String[] args) throws JSchException, SftpException {
        JSch jSch = new JSch();
        Session session = jSch.getSession("admin","127.0.0.1",8800);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword("admin");
        session.connect();
        ChannelSftp channelSftp = (ChannelSftp)session.openChannel("sftp");
        channelSftp.connect();
        String lpwd = channelSftp.lpwd();
        System.out.println(lpwd);
        //channelSftp.mkdir("a");
        System.out.println(channelSftp.getHome());
        System.out.println(channelSftp.get("settings.gradle"));
        channelSftp.disconnect();
        session.disconnect();
    }
}
