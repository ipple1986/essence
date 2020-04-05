# approach 1
keytool -genkey -alias server -keyalg RSA -keystore server.jks -storepass 123456 -keypass 123456 -dname "CN=Server"
keytool -export -alias server -file server.cer -keystore server.jks -storepass 123456

keytool -genkey -alias client -keyalg RSA -keystore client.jks -storepass 654321 -keypass 654321 -dname "CN=S2"
keytool -export -alias client -file client.cer -keystore client.jks -storepass 654321

keytool -import -alias server -file server.cer -keystore client.jks -storepass 654321
keytool -import -alias client -file client.cer -keystore server.jks -storepass 123456

# approach 2 
 
 CA -> Server
 
 CA -> Client

Server <- -> Client

keytool -genkeypair -alias ca -dname "CN=CA" -keystore ca.jks -storepass 111111 -keyalg RSA -keypass 111111
keytool -exportcert -alias ca -keystore ca.jks -storepass 111111 -file ca.cer

keytool -genkeypair -alias server -dname "CN=Server" -keystore server2.jks -storepass 123456  -keyalg RSA -keypass 123456
keytool -certreq -alias server -file server.csr -keystore server2.jks -storepass 123456
keytool -gencert -infile server.csr -outfile server.cer -alias ca -keystore ca.jks -storepass 111111
keytool -importcert -file ca.cer -alias ca -keystore server2.jks -storepass 123456
keytool -importcert -file server.cer -alias server -keystore server2.jks -storepass 123456

keytool -genkeypair -alias client -dname "CN=Client" -keystore client2.jks -storepass 654321  -keyalg RSA -keypass 654321
keytool -certreq -alias client -file client.csr -keystore client2.jks -storepass 654321
keytool -gencert -infile client.csr -outfile client.cer -alias ca -keystore ca.jks -storepass 111111
keytool -importcert -file ca.cer -alias ca -keystore client2.jks -storepass 654321
keytool -importcert -file client.cer -alias client -keystore client2.jks -storepass 654321