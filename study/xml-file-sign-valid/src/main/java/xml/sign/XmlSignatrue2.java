package xml.sign;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// refer to :
// https://docs.oracle.com/javase/8/docs/technotes/guides/security/xmldsig/XMLDigitalSignature.html#wp511122
public class XmlSignatrue2 {
    public static void main(String[] args) throws Exception {


        // generate keypair
        // keytool -genkeypair -keyalg RSA -sigalg Sha256withRSA -alias test -keystore test.jks -storepass 123456 -keypass 123456 -dname CN=org.test
        KeyStore keyStore = KeyStore.getInstance("jks");
        String storePath = "xml-file-sign-valid/src/main/resources/test.jks";
        char[] storePass = "123456".toCharArray();
        keyStore.load(new FileInputStream(storePath),storePass);
        Certificate certificate = keyStore.getCertificate("test");
        PublicKey publicKey = certificate.getPublicKey();

        sign(keyStore,"xml-file-sign-valid/src/main/resources/web.xml","xml-file-sign-valid/src/main/resources/websigned.xml");
        valid(publicKey,"xml-file-sign-valid/src/main/resources/websigned.xml");
    }

    public static void valid(PublicKey publicKey,String src) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document document = builder.parse(new File(src));

        XMLSignatureFactory xmlSignatureFactory2 = XMLSignatureFactory.getInstance("DOM");
        NodeList nl = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        DOMValidateContext valContext = new DOMValidateContext(publicKey , nl.item(0));

        XMLSignature signature =xmlSignatureFactory2.unmarshalXMLSignature(valContext);
        System.out.println(signature.validate(valContext));
    }
    public static void sign(KeyStore keyStore ,String src,String dst) throws Exception{
        Key key = keyStore.getKey("test","123456".toCharArray());
        PrivateKey privateKey = (PrivateKey)key;
        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate("test");
        X500Principal issuerX500Principal = x509Certificate.getIssuerX500Principal();
        X500Principal subjectX500Principal =  x509Certificate.getSubjectX500Principal();
        String issuerName  = issuerX500Principal.getName();
        BigInteger issuerNumber = x509Certificate.getSerialNumber();

        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        // generate reference used by signedInfo
        File file = new File(src);
        Reference ref = xmlSignatureFactory.newReference
                (file.toURI().toString(), xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null));
        // generate signedInfo
        SignedInfo si = xmlSignatureFactory.newSignedInfo(
                xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (XMLStructure)null),
                        xmlSignatureFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
                        Collections.singletonList(ref));
        // generate keyInfo
        KeyInfoFactory kif = xmlSignatureFactory.getKeyInfoFactory();
        X509IssuerSerial x509IssuerSerial = kif.newX509IssuerSerial(issuerName, issuerNumber);

        List x509DataList = new ArrayList<>(2);
        x509DataList.add(x509IssuerSerial);
        x509DataList.add(subjectX500Principal.getName());
        X509Data x509Data = kif.newX509Data(x509DataList);

        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509Data));
        // construct the Signature Object
        XMLSignature signature = xmlSignatureFactory.newXMLSignature(si, ki);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();
        DOMSignContext dsc = new DOMSignContext(privateKey, doc);
        // signature
        signature.sign(dsc);
        // output
        FileOutputStream fileOutputStream = new FileOutputStream(new File(dst));
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(fileOutputStream));
        
    }


    public static KeyPair generateKeyPair() throws Exception{
        // generate keypair
        // keytool -genkeypair -keyalg RSA -sigalg Sha256withRSA -alias test -keystore test.jks -storepass 123456 -keypass 123456 -dname CN=org.test
        KeyStore keyStore = KeyStore.getInstance("jks");
        String storePath = "xml-file-sign-valid/src/main/resources/test.jks";
        char[] storePass = "123456".toCharArray();
        keyStore.load(new FileInputStream(storePath),storePass);
        Key key = keyStore.getKey("test",storePass);
        PrivateKey privateKey = (PrivateKey)key;
        Certificate certificate = keyStore.getCertificate("test");
        PublicKey publicKey = certificate.getPublicKey();
        KeyPair keyPair = new KeyPair(publicKey,privateKey);
        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate("test");

        // or below
        /*
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
        */
        return keyPair;
    }
}
