package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Controller.AuthController;
import com.suryoday.EtbFdOpening.Others.CustomProperties;
import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Service.AadharOtpService;



@Component
public class AadharOtpServImpl implements AadharOtpService{
	private static Logger logger = LoggerFactory.getLogger(AadharOtpServImpl.class);
	@Override
	public String getXmlRequest(String uid, String stan) throws Exception{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
		LocalDateTime now = LocalDateTime.now();
		String todayDate = dtf.format(now);
		
		DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HHmmss");
		String time = dtf1.format(now);
		
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MMdd");
		String date = dtf2.format(now);

//		int max = 899999;
//		int min = 800000;
//		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
//		String stan = Integer.toString(random_int);
		int proc_code=140000;
		String type="A";
		String ch="01";
		CustomProperties prop = loadpropertyfile();
		StringBuffer auth =new StringBuffer();
		
		auth.append("<OtpRequest>");
		auth.append("<TransactionInfo>");
		auth.append("<Pan>6080220" + uid + "</Pan>");
		auth.append("<Proc_Code>" + proc_code + "</Proc_Code>");
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
		auth.append("<Otp");
		auth.append(" uid=\"" + uid);
		auth.append("\" ac=\"" + prop.getAc());
		auth.append("\" sa=\"" + prop.getSa());
		auth.append("\" ver=\"" + prop.getVer());
		auth.append("\" txn=\"" + stan);
		auth.append("\" lk=\"" + prop.getAua_lk());
		auth.append("\" type=\"" + type + "\">");
		auth.append("<Opts");
		auth.append(" ch=\"" + ch + "\"/>");
		auth.append("</Otp>");
		auth.append("</OtpRequest>");
		
		String authRequest = auth.toString();
		authRequest = authRequest.replaceAll("\t", "");
		return authRequest;
	}

	@Override
	public String sendEkyc(String xmlRequest) {

		String response = null;
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			x.getappprop();
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			CustomProperties prop = loadpropertyfile();

			obj = new URL(prop.getKycotpurl());

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
		logger.debug("POST Response Code :: " + responseCode);

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
			logger.debug("POST request not worked");
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
			prop1.setAuthUrl(prop.getProperty("authOtpUrl"));
			//prop1.setKycUrl(prop.getProperty("kycUrl"));
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

	@Override
	public JSONObject createAadharReference(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		
		URL obj = null;
		try {
			
			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			 x.bypassssl();
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid); 
			logger.debug(x.BASEURL+"aadhar/reference/v2?api_key="+x.api_key);
			 obj =new URL(x.BASEURL+"aadhar/reference/v2?api_key="+x.api_key);
//			 obj =new URL("https://intramashery.suryodaybank.co.in/aadhar/reference?api_key=kyqak5muymxcrjhc5q57vz9v");				
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("X-Correlation-ID",header.getString("X-Correlation-ID"));
				con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
				con.setRequestProperty("tenant",header.getString("tenant"));

				sendResponse = getResponseData(jsonObject, sendResponse, con,"POST");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return sendResponse;
	}
	
	private static JSONObject getResponseData(JSONObject parent, JSONObject sendAuthenticateResponse,
			HttpURLConnection con,String MethodType) throws IOException {
		
		con.setDoOutput(true);
		OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
		os.write(parent.toString());
		os.flush();
		os.close();
		
		int responseCode = con.getResponseCode();
		logger.debug("POST Response Code :: " + responseCode);
		
		
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			
			JSONObject 	sendauthenticateResponse1  = new JSONObject();
			   sendauthenticateResponse1.put("data", response.toString());
			   sendAuthenticateResponse = sendauthenticateResponse1;
		}
		else
		{
					logger.debug("POST request not worked");
			
			JSONObject 	sendauthenticateResponse1  = new JSONObject();
			 
			 JSONObject errr = new JSONObject();
			 errr.put("Description","Server Error "+responseCode);
			 
			 JSONObject j = new JSONObject();
			 j.put("Error",errr);
			 
			sendauthenticateResponse1.put("data", ""+j);
			sendAuthenticateResponse = sendauthenticateResponse1;
		}
		
				return sendAuthenticateResponse;
		
	}


}
