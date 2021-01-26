//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package xml.sign;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.jcp.xml.dsig.internal.dom.ApacheData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.crypto.OctetStreamData;

public class ApacheOctetStreamData extends OctetStreamData implements ApacheData {
    private XMLSignatureInput xi;

    public static byte[] inputStream2byte(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                byteArrayOutputStream.write(buff, 0, rc);
            }
            return byteArrayOutputStream.toByteArray();
        }finally {
            inputStream.close();
        }

    }

    public ApacheOctetStreamData(XMLSignatureInput var1) throws CanonicalizationException, IOException {
        super(var1.getOctetStream(), var1.getSourceURI(), var1.getMIMEType());
        this.xi = new XMLSignatureInput(inputStream2byte(var1.getOctetStream()));
    }

    public XMLSignatureInput getXMLSignatureInput() {
        return this.xi;
    }
}
