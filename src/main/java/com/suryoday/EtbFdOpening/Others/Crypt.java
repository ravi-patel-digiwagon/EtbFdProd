package com.suryoday.EtbFdOpening.Others;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crypt {

	private static final String ALGO = "AES"; // Default uses ECB PKCS5Padding
	private static Logger logger = LoggerFactory.getLogger(Crypt.class);

	public static String encrypt(String Data, String secret) throws Exception {
		Key key = generateKey(secret);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = Base64.getEncoder().encodeToString(encVal);
		return encryptedValue;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			Key key = generateKey(secret);
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			logger.debug("Error while decrypting: " + e.toString());
		}
		return null;
	}

	private static Key generateKey(String secret) throws Exception {
		byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
		Key key = new SecretKeySpec(decoded, ALGO);
		return key;
	}

	public static String decodeKey(String str) {
		byte[] decoded = Base64.getDecoder().decode(str.getBytes());
		return new String(decoded);
	}

	public static String encodeKey(String str) {
		byte[] encoded = Base64.getEncoder().encode(str.getBytes());
		return new String(encoded);
	}

//	public static void main(String a[]) throws Exception {
//		/*
//		 * Secret Key must be in the form of 16 byte like,
//		 *
//		 * private static final byte[] secretKey = new byte[] { ‘m’, ‘u’, ‘s’, ‘t’, ‘b’,
//		 * ‘e’, ‘1’, ‘6’, ‘b’, ‘y’, ‘t’,’e’, ‘s’, ‘k’, ‘e’, ‘y’};
//		 *
//		 * below is the direct 16byte string we can use
//		 */
//		String secretKey = "Viju@12345567767";
//		String encodedBase64Key = encodeKey(secretKey);
//		logger.debug("EncodedBase64Key = " + encodedBase64Key); // This need to be share between client and server
//
//// To check actual key from encoded base 64 secretKey
//// String toDecodeBase64Key = decodeKey(encodedBase64Key);
//// logger.debug(“toDecodeBase64Key = “+toDecodeBase64Key);
//
//		String toEncrypt = "Hi Virendra!";
//		logger.debug("Plain text = " + toEncrypt);
//
//// AES Encryption based on above secretKey
//		String encrStr = Crypt.encrypt(toEncrypt, encodedBase64Key);
//		logger.debug("Cipher Text: Encryption of str = " + encrStr);
//		encrStr = "2AlKbDFL3pKhkHbo5EeqXBcwyy+EQWyWT2mFWtDas43GHRPDZsnY/E2LfwomEi+L";
//// AES Decryption based on above secretKey
//		String encodeId = "YWNhMzA4ZDktNjA1Yy00MQ==";
//		String decrStr = Crypt.decrypt(encrStr, encodeId);
//
//		logger.debug("Decryption of str = " + decrStr);
//	}
}