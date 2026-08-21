package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Controller.AuthController;
import com.suryoday.EtbFdOpening.Others.CustomProperties;
import com.suryoday.EtbFdOpening.Others.EncryptDecryptHelper;
import com.suryoday.EtbFdOpening.Others.Encrypter;
import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Service.AadharOtpValidateService;



@Component
public class AadharOtpValidateServImpl implements AadharOtpValidateService{
	private static final String JCE_PROVIDER = "BC";
	private static final int SYMMETRIC_KEY_SIZE = 256;
	@Override
	public String getXmlRequest(String otp, String uid, String stan) throws Exception {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
			LocalDateTime now = LocalDateTime.now();
			String todayDate = dtf.format(now);
			DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			String time5 =dtf3.format(now);
			DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HHmmss");
			String time = dtf1.format(now);
			
			DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MMdd");
			String date = dtf2.format(now);


			CustomProperties prop = loadpropertyfile();

			String ci="20280813";

			Encrypter e1= new Encrypter(prop.getAuthPreProdCert());
			byte[] genratekey = generateSessionKey();
			byte[] encryptsesionwithpublic=e1.encryptUsingPublicKey(genratekey);
			String ciContent = new String(Base64.encodeBase64(encryptsesionwithpublic), "UTF-8");
			
			StringBuffer data1 =new StringBuffer();
			String ver="2.0";
			data1.append("<Pid");
			data1.append(" ts=\"" +time5 );
			data1.append("\" ver =\"" + ver);
			data1.append("\" wadh =\"" + ""+"\">");
			data1.append("<Pv");
			data1.append(" otp=\"" +otp +""+"\" />");
			data1.append("</Pid>");
			String data2 = data1.toString();
			System.out.println(data2);
			byte[] encodedPidData =getPIDData(data2.getBytes(), genratekey, time5);
			String genrateData=new String(Base64.encodeBase64(encodedPidData), "UTF-8");
			String dataType="X";
			byte[]  encodedHash = getHmac(data2.getBytes(), genratekey, time5);
			String Hmac=new String(Base64.encodeBase64(encodedHash), "UTF-8");
			
			String type="A";
			String ch="01";
			String bio="n";
			String otpsend="y";
			String lr="Y";
			String ra="O";
			StringBuffer auth =new StringBuffer();
			
			auth.append("<KycRequest>");
			auth.append("<TransactionInfo>");
			auth.append("<Pan>6080220"+uid+"</Pan>");
			auth.append("<Proc_Code>" + prop.getProc_code() + "</Proc_Code>");
			auth.append("<Transm_Date_time>" + todayDate + "</Transm_Date_time>");
			auth.append("<Stan>" + stan + "</Stan>");
			auth.append("<Local_Trans_Time>" + time + "</Local_Trans_Time>");
			auth.append("<Local_date>" + date + "</Local_date>");
			auth.append("<Mcc>" + prop.getMcc() + "</Mcc>");
			auth.append("<Pos_entry_mode>" + prop.getPos_entry_mode() + "</Pos_entry_mode>");
			auth.append("<Pos_code>" + prop.getPos_code() + "</Pos_code>");
			auth.append("<AcqId>" + prop.getAcqId() + "</AcqId>");
			auth.append("<RRN>" + time + stan + "</RRN>");
			auth.append("<CA_Tid>" + prop.getcA_Tid() + "</CA_Tid>");
			auth.append("<CA_ID>" + prop.getcA_ID() + "</CA_ID>");
			auth.append("<CA_TA>" + prop.getcA_TA() + "</CA_TA>");
			auth.append("</TransactionInfo>");
			auth.append("<KycReqInfo");
			auth.append(" ver=\"" + prop.getVer());
			auth.append("\" ra=\"" + ra);
			auth.append("\" rc=\"" + prop.getRc());
			auth.append("\" pfr=\"" + prop.getPfr());
			auth.append("\" lr=\"" + lr);
			auth.append("\" de=\"" + prop.getDe() + "\">");
			auth.append("<Auth ");
			auth.append(" ac=\"" + prop.getAc());
			auth.append("\" tid=\"" + "");
			auth.append("\" sa=\"" + prop.getSa());
			auth.append("\" txn=\"UKC:" + stan);
			auth.append("\" lk=\"" + prop.getOpt_lk());
			auth.append("\" uid =\"" + uid);
			auth.append("\" ver=\"" + prop.getVer());
			auth.append("\" rc=\"" + prop.getRc() + "\">");
			auth.append("<Uses ");
			auth.append(" pi=\"" + prop.getPi());
			auth.append("\" pa=\"" + prop.getPa());
			auth.append("\" pfa=\"" + prop.getPfa());
			auth.append("\" pin=\"" + prop.getPin());
			auth.append("\" bio=\"" + bio);
			auth.append("\" otp=\"" + otpsend + "\"/>");
			auth.append("<Meta ");
			auth.append("rdsId=\"" +"");
			auth.append("\" rdsVer=\"" +"");
			auth.append("\" dpId=\"" +"");
			auth.append("\" dc=\"" +"");
			auth.append("\" mi=\"" +"");
			auth.append("\" mc=\"" + "" + "\" />");
			auth.append("<Skey ");
			auth.append("ci=\"" + ci + "\">");
			auth.append(ciContent);
			auth.append("</Skey>");
			auth.append("<Data type=\"" + dataType + "\">");
			auth.append(genrateData);
			auth.append("</Data>");
			auth.append("<Hmac>" + Hmac + "</Hmac>");
			auth.append("</Auth>");
			auth.append("</KycReqInfo>");
			auth.append("</KycRequest>");
			
			String authRequest = auth.toString();
			authRequest = authRequest.replaceAll("\t", "");
			return authRequest;
	}

	@Override
	public String decryptString(String hsmData) {
		String decrypt = "";
		CustomProperties prop;
		try {
			prop = loadpropertyfile();
		
		String KEY = prop.getEkyc_key();
			byte[] bytesToDecrypt = java.util.Base64.getDecoder().decode(hsmData);

			decrypt = EncryptDecryptHelper.decrypt(KEY.getBytes(), bytesToDecrypt);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return decrypt;
	}

	@Override
	public String sendEkyc(String xmlRequest) {
		String response = null;
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			CustomProperties prop = loadpropertyfile();
			
			obj = new URL(prop.getKycUrl());
		
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/xml");
			response = getResponseData(xmlRequest, response, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}
	private static String getResponseData(String parent, String response2, HttpURLConnection con, String MethodType)
			throws IOException {

		con.setDoOutput(true);
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		os.writeBytes(parent);
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
	

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(response.toString());
			response2 = stringBuffer.toString();

		} else {
			
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<Description><Error>");
			stringBuffer.append(" Server Error  " + responseCode + "</Error></Description>");
			response2 = stringBuffer.toString();

		}

		return response2;
	}
	
	public CustomProperties loadpropertyfile() throws Exception {
		String sConfigFile = "props/uidai_admin_iris.properties";

		InputStream in = AuthController.class.getClassLoader().getResourceAsStream(sConfigFile);
		if (in == null) {
			throw new FileNotFoundException("Property file '" + sConfigFile + "' not found in classpath");
		
		}
		Properties prop = new Properties();
		prop.load(in);

		CustomProperties prop1 = new CustomProperties();
		try {

			prop1.setAc(prop.getProperty("ac"));
			prop1.setSa(prop.getProperty("sa"));
			prop1.setMcc(prop.getProperty("Mcc"));
			prop1.setAcqId(prop.getProperty("AcqId"));
			prop1.settId(prop.getProperty("tid"));
			prop1.setRc(prop.getProperty("rc"));
			prop1.setPos_entry_mode(prop.getProperty("pos_entry_mode"));
			prop1.setPos_code(prop.getProperty("pos_code"));
			prop1.setcA_Tid(prop.getProperty("cA_Tid"));
			prop1.setcA_ID(prop.getProperty("cA_ID"));
			prop1.setcA_TA(prop.getProperty("cA_TA"));
			prop1.setAua_lk(prop.getProperty("aua_lk"));
			prop1.setProc_code(prop.getProperty("proc_code"));
			prop1.setVer(prop.getProperty("ver"));
			prop1.setPi(prop.getProperty("pi"));
			prop1.setPa(prop.getProperty("pa"));
			prop1.setPfa(prop.getProperty("pfa"));
			prop1.setBio(prop.getProperty("bio"));
			prop1.setBt(prop.getProperty("bt"));
			prop1.setPin(prop.getProperty("pin"));
			prop1.setOtp(prop.getProperty("otp"));
			prop1.setAuthUrl(prop.getProperty("authUrl"));
			prop1.setKycUrl(prop.getProperty("kycUrl"));
			prop1.setRa(prop.getProperty("ra"));
			prop1.setPfr(prop.getProperty("pfr"));
			prop1.setLr(prop.getProperty("lr"));
			prop1.setDe(prop.getProperty("de"));
			prop1.setEkyc_key(prop.getProperty("ekyc_key"));

		} catch (Exception e) {

			e.printStackTrace();
		}

		return prop1;
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
	public byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES", JCE_PROVIDER);
		kgen.init(SYMMETRIC_KEY_SIZE);
		SecretKey key = kgen.generateKey();
		byte[] symmKey = key.getEncoded();
		return symmKey;
	}
}
