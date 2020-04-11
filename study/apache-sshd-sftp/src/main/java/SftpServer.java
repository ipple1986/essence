import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.io.IoAcceptor;
import org.apache.sshd.common.io.IoConnector;
import org.apache.sshd.common.io.IoServiceEventListener;
import org.apache.sshd.common.io.nio2.Nio2Acceptor;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.apache.sshd.server.subsystem.sftp.*;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Collections;

public class SftpServer {
    public static void main(String[] args) throws IOException {
        SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setPort(8800);

        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

        SubsystemFactory subsystemFactory = new SftpSubsystemFactory.Builder()
                .withSftpErrorStatusDataHandler(SftpErrorStatusDataHandler.DEFAULT)
                .withFileSystemAccessor(SftpFileSystemAccessor.DEFAULT)
                .withUnsupportedAttributePolicy(UnsupportedAttributePolicy.ThrowException)
                .build();
        sshServer.setSubsystemFactories(Collections.singletonList(subsystemFactory));
        sshServer.setPasswordAuthenticator((username,pwd,serverSession)-> "admin".equals(username) && "admin".equals(pwd) );


        sshServer.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
        sshServer.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(false));

        sshServer.start();
        System.in.read();

        //sshServer.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        //sshServer.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        //System.out.println(sshServer.getFileSystemFactory().createFileSystem());


/*        sshServer.addChannelListener(new ChannelListener() {
            @Override
            public void channelInitialized(Channel channel) {
                System.out.println("--channelInitialized---");
            }

            @Override
            public void channelOpenSuccess(Channel channel) {
                System.out.println("--channelOpenSuccess---");
            }

            @Override
            public void channelStateChanged(Channel channel, String hint) {
                System.out.println(hint);
            }

        });*/

    }
}
