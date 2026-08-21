package com.suryoday.EtbFdOpening.Others;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Encrypter {
	private static final String JCE_PROVIDER = "BC";

	private static final String ASYMMETRIC_ALGO = "RSA/ECB/PKCS1Padding";
	private static final int SYMMETRIC_KEY_SIZE = 256;

	private static final String CERTIFICATE_TYPE = "X.509";

	private PublicKey publicKey;
	private Date certExpiryDate;


	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Constructor
	 * @param publicKeyFileName Location of UIDAI public key file (.cer file)
	 */
	
	Encrypter()
	{
		
	}
	public Encrypter(String publicKeyFileName) {
		FileInputStream fileInputStream = null;
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE, JCE_PROVIDER);
			fileInputStream = new FileInputStream(new File(publicKeyFileName));
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(fileInputStream);
			publicKey = cert.getPublicKey();
			certExpiryDate = cert.getNotAfter();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not intialize encryption module", e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Creates a AES key that can be used as session key (skey)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES", JCE_PROVIDER);
		kgen.init(SYMMETRIC_KEY_SIZE);
		SecretKey key = kgen.generateKey();
		byte[] symmKey = key.getEncoded();
		return symmKey;
	}

	/**
	 * Encrypts given data using UIDAI public key
	 * @param data Data to encrypt
	 * @return Encrypted data
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public byte[] encryptUsingPublicKey(byte[] data) throws IOException, GeneralSecurityException {
		// encrypt the session key with the public key
		Cipher pkCipher = Cipher.getInstance(ASYMMETRIC_ALGO, JCE_PROVIDER);
		pkCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encSessionKey = pkCipher.doFinal(data);
		return encSessionKey;
	}

	/**
	 * Encrypts given data using session key
	 * @param skey Session key
	 * @param data Data to encrypt
	 * @return Encrypted data
	 * @throws InvalidCipherTextException
	 */
	public byte[] encryptUsingSessionKey(byte[] skey, byte[] data) throws InvalidCipherTextException {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine(), new PKCS7Padding());

		cipher.init(true, new KeyParameter(skey));

		int outputSize = cipher.getOutputSize(data.length);

		byte[] tempOP = new byte[outputSize];
		int processLen = cipher.processBytes(data, 0, data.length, tempOP, 0);
		int outputLen = cipher.doFinal(tempOP, processLen);

		byte[] result = new byte[processLen + outputLen];
		System.arraycopy(tempOP, 0, result, 0, result.length);
		return result;

	}
	
	/**
	 * Returns UIDAI certificate's expiry date in YYYYMMDD format using GMT time zone. 
	 * It can be used as certificate identifier
	 * @return Certificate identifier for UIDAI public certificate
	 */
	public String getCertificateIdentifier() {
		SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd");
		ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String certificateIdentifier = ciDateFormat.format(this.certExpiryDate);
		return certificateIdentifier;
	}
	
	public byte[] generateSha256Hash(byte[] message) {
		String algorithm = "SHA-256";
		String SECURITY_PROVIDER = "BC";

		byte[] hash = null;

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(algorithm, SECURITY_PROVIDER);
			digest.reset();
			hash = digest.digest(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}
	
	
	private  static byte[] getHmac(byte[] piddata,byte[] keyBytes,String ts) {
		String Hmac = null;
		 byte[] encHmac=null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(piddata);
			byte byteData[] = md.digest();
			String Smac = byteArrayToHexString(byteData);

			//byte[] keyBytes = sessionKey.getBytes();

			SecretKeySpec key1 = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			final byte[] nonce = new byte[12];
			final byte[] aad = new byte[16];

			byte[] btsd = ts.getBytes();
			System.arraycopy(btsd, btsd.length - nonce.length, nonce, 0, nonce.length);
			System.arraycopy(btsd, btsd.length - aad.length, aad, 0, aad.length);

			GCMParameterSpec iv = new GCMParameterSpec(128, nonce); //16x8
			cipher.init(Cipher.ENCRYPT_MODE, key1, iv);
			cipher.updateAAD(aad);
		encHmac = cipher.doFinal(hexStringToByteArray(Smac));
			//Hmac = Base64.encodeToString(encHmac, Base64.DEFAULT);
			//Hmac =  new String(Base64.encode(encHmac));

		}catch (Exception e) {
			e.printStackTrace();
		}
		return encHmac;
	}
	
	
	private static byte[] getPIDData(byte[] piddata,byte[] keyBytes,String ts){
        String enPIDData = null;
        byte[] buf=null;
        try {
           // byte[] keyBytes = sessionKey.getBytes();
           // logger.error(TAG,">>>>> sessionkey:\n"+HexString.bufferToHex(keyBytes));
            SecretKeySpec key=new SecretKeySpec(keyBytes, "AES");
            Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");

            final byte[] nonce = new byte[12];
            final byte[] aad = new byte[16];

            byte[] btsd = ts.getBytes();

            System.arraycopy(btsd,btsd.length-nonce.length,nonce,0,nonce.length);
            System.arraycopy(btsd,btsd.length-aad.length,aad,0,aad.length);

            GCMParameterSpec iv = new GCMParameterSpec(128, nonce); //16x8

            byte[] biv = iv.getIV();
            //logger.e(TAG,">>>>> ts:\n"+HexString.bufferToHex(btsd));
            //logger.error(TAG,">>>>> ivdata:\n"+HexString.bufferToHex(biv));

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            cipher.updateAAD(aad);
            byte []encPIDData = cipher.doFinal(piddata);
           // logger.error(TAG,">>>>> input  Ori:\n"+HexString.bufferToHex(piddata));
            //logger.error(TAG,">>>>> output Enc:\n"+HexString.bufferToHex(encPIDData));

            byte[] bts= ts.getBytes();
            buf = new byte[bts.length + encPIDData.length];
            System.arraycopy(bts, 0, buf, 0, bts.length);
            System.arraycopy(encPIDData, 0, buf, bts.length, encPIDData.length);
           /* ByteBuffer buf = new ByteBuffer();
            buf.appendBytes(bts);
            buf.appendBytes(encPIDData);*/
           // logger.error(TAG,"");
           // enPIDData = Base64.encodeToString(buf.getBuffer(), Base64.DEFAULT);
            
            //enPIDData = Base64.encodeToString(buf, Base64.DEFAULT);
           // enPIDData =  new String(Base64.encode(buf));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf;
    }
	
	 private static String byteArrayToHexString(byte[] bytes) {
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				result.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return result.toString();
		}
	 
		private static byte[] hexStringToByteArray(String data) {
			int k = 0;
			byte[] results = new byte[data.length() / 2];
			for (int i = 0; i < data.length();) {
				results[k] = (byte) (Character.digit(data.charAt(i++), 16) << 4);
				results[k] += (byte) (Character.digit(data.charAt(i++), 16));
				k++;
			}
			return results;
		}
	
	
//	public static void main(String args[]) throws IOException, GeneralSecurityException
//	{
//		System.out.println("welcome");
//		
//		
//		Encrypter e= new Encrypter();
//		//getnrate public key
//		
//		
////			Properties prop = new Properties();
////			try {
////			    //load a properties file from class path, inside static method
////			    prop.load(GenerateProperty.class.getClassLoader().getResourceAsStream("cert/uidai_auth_preprod.cer"));
////
////			   // prop.load(new FileInputStream("conf/application.properties"));
////			    
////			    //get the property value and print it out
////			    //logger.debug(prop.getProperty("userBucket.path"));
////			    
////			    
////			    System.out.println(prop);
////			    
////			   // logger.debug(prop.getProperty("BASEURL"));
////
////			   		} 
////			catch (IOException ex) {
////			    ex.printStackTrace();
////			    System.out.println(""+ex.getStackTrace());
////			}
//		
//		
//			Encrypter e1= new Encrypter("C:\\Users\\Neosoft\\Documents\\UAT\\aocpvUat\\src\\main\\resources\\props\\uidai_auth_preprod.cer");
//			System.out.println(e1.publicKey);
//			System.out.println(e1.certExpiryDate);
//			
//		
//		
//			
//		byte[] genratekey=e1.generateSessionKey();
//		
//	
//		System.out.println("Session KEY : " +new String(Base64.encodeBase64(genratekey), "UTF-8"));
//		
////		try {
////			byte[] genratepublic =	e1.encryptUsingPublicKey("Data1".getBytes());
////			System.out.println(new String(genratepublic));
////		} catch (IOException e2) {
////			// TODO Auto-generated catch block
////			e2.printStackTrace();
////		} catch (GeneralSecurityException e2) {
////			// TODO Auto-generated catch block
////			e2.printStackTrace();
////		}
//		
//		System.out.println("-----------------------------------------gernate Session-----------");
//		byte[] encryptsesionwithpublic=e1.encryptUsingPublicKey(genratekey);
//		
//		String plainrStr12 = new String(Base64.encodeBase64(encryptsesionwithpublic), "UTF-8");
//		System.out.println("genrate   "+plainrStr12);
//		
//		String data1="<Pid ts=\"2023-02-01T13:48:00\" ver=\"2.0\" wadh=\"\"><Pv otp=\"265461\" /></Pid>";
//		System.out.println("Data " +data1);
//		
//		String time = "2023-02-01T13:48:00";
//		byte[] bTime = time.getBytes("UTF-8");
//		
//		
//		
//		
//		
//		byte[] encodedPidData = getPIDData(data1.getBytes(), genratekey, time);
//		
//		String encodedata1=new String(Base64.encodeBase64(encodedPidData), "UTF-8");
//		System.out.println("genrate data  "+encodedata1);
//		
//		System.out.println("-----------------------------------------gernate data-----------");
//		byte[]  encodedHash = getHmac(data1.getBytes(), genratekey, time);
//		String encodedata2=new String(Base64.encodeBase64(encodedHash), "UTF-8");
//		System.out.println("genrate encodedHash  "+encodedata2);
//		
//		System.out.println("-----------------------------------------gernate hmac-----------");
//		
////		try {
////			byte[] genratekeyency=e1.encryptUsingSessionKey(genratekey,data1.getBytes());
////			
////			
////			String plainrStr1 = new String(Base64.encodeBase64(genratekeyency), "UTF-8");
////			System.out.println("encryptUsingSessionKey  "+plainrStr1);
////		} catch (InvalidCipherTextException e2) {
////			// TODO Auto-generated catch block
////			e2.printStackTrace();
////		}
////		
////		
////		
////		
////		byte[] genratehash =e1.generateSha256Hash(data1.getBytes());
////		System.out.println("genrate Hash "+new String(Base64.encodeBase64(genratehash), "UTF-8"));
////		
////		try {
////			byte[] genratedhashwithencryption=e1.encryptUsingSessionKey(genratekey,genratehash);
////			
////			
////			String plainrStr2 = new String(Base64.encodeBase64(genratedhashwithencryption), "UTF-8");
////			System.out.println("encrypte using session HASH   "+plainrStr2);
////		} catch (InvalidCipherTextException e2) {
////			// TODO Auto-generated catch block
////			e2.printStackTrace();
////		}
////	}
//		
//	}
	
}
