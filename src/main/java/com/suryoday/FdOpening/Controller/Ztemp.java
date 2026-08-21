package com.suryoday.FdOpening.Controller;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Ztemp {
//	public static void main(String[] args) {
//
//		int max = 899999;
//		int min = 800000;
//		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
//		System.out.println(random_int);
//
//		SecureRandom secureRandom = new SecureRandom();
//		int random_int2 = secureRandom.nextInt((max - min) + 1) + min;
//
//		System.out.println(random_int2);
//	}
	
	public byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
		kgen.init(256);
		SecretKey key = kgen.generateKey();
		byte[] symmKey = key.getEncoded();
		return symmKey;
	}
}
