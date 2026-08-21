package com.suryoday.FdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.suryoday.FdOpening.Controller.AuthController;
import com.suryoday.FdOpening.Others.CustomProperties;
import com.suryoday.FdOpening.Others.GenerateProperty;
import com.suryoday.FdOpening.Service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	private static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public String sendAuth(String request) {

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

			obj = new URL(prop.getAuthUrl());

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/xml");
			response = getResponseData(request, response, con, "POST");

		} catch (Exception e) {

			e.getMessage();
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

	public String getJsonRequest(String pidxml, String uid) throws Exception {

		// XML to JSON Conversion
		JSONObject jsonConverted = XML.toJSONObject(pidxml);
		String jsonString = jsonConverted.toString();
		logger.debug("XML Data Converted To JSON : " + jsonConverted);
		logger.debug("JSONString : " + jsonString);

		JSONObject obj = new JSONObject(jsonString);

		String rdsId = obj.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("rdsId");
		String rdsVer = obj.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("rdsVer");
		String dpId = obj.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("dpId");
		String dc = obj.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("dc");
		String mi = obj.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("mi");
		String mc = obj.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("mc");
		int ci = obj.getJSONObject("PidData").getJSONObject("Skey").getInt("ci");
		String ciContent = obj.getJSONObject("PidData").getJSONObject("Skey").getString("content");
		String dataType = obj.getJSONObject("PidData").getJSONObject("Data").getString("type");
		String dataContent = obj.getJSONObject("PidData").getJSONObject("Data").getString("content");
		String Hmac = obj.getJSONObject("PidData").getString("Hmac");

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
		LocalDateTime now = LocalDateTime.now();
		String todayDate = dtf.format(now);
		logger.debug("got todayDate in ddMMyyyyHHmmss : " + todayDate);

		DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HHmmss");
		String time = dtf1.format(now);
		logger.debug(" time in HHmmss : " + time);

		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MMdd");
		String date = dtf2.format(now);
		logger.debug("got todayDate in MMdd :  " + date);

		int max = 899999;
		int min = 800000;
//		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

		int random_int = SECURE_RANDOM.nextInt((max - min) + 1) + min;
		String stan = Integer.toString(random_int);
		logger.debug("Stan : " + stan);

		CustomProperties prop = loadpropertyfile();

		StringBuffer auth = new StringBuffer();
		auth.append("<Request_auth>");
		auth.append("<TransactionInfo>");
		auth.append("<Pan> 6080220 " + uid + "</Pan>");
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
		auth.append("<Auth ");
		auth.append(" uid =\"" + uid);
		auth.append("\" ac=\"" + prop.getAc());
		auth.append("\" tid=\"" + prop.gettId());
		auth.append("\" ver=\"" + prop.getVer());
		auth.append("\" txn=\"" + stan);
		auth.append("\" lk=\"" + prop.getAua_lk());
		auth.append("\" sa=\"" + prop.getSa());
		auth.append("\" rc=\"" + prop.getRc() + "\">");
		auth.append("<Uses ");
		auth.append(" pi=\"" + prop.getPi());
		auth.append("\" pa=\"" + prop.getPa());
		auth.append("\" pfa=\"" + prop.getPfa());
		auth.append("\" bio=\"" + prop.getBio());
		auth.append("\" bt=\"" + prop.getBt());
		auth.append("\" pin=\"" + prop.getPin());
		auth.append("\" otp=\"" + prop.getOtp() + "\"/>");
		auth.append("<Meta ");
		auth.append("rdsId=\"" + rdsId);
		auth.append("\" rdsVer=\"" + rdsVer);
		auth.append("\" dpId=\"" + dpId);
		auth.append("\" dc=\"" + dc);
		auth.append("\" mi=\"" + mi);
		auth.append("\" mc=\"" + mc + "\" />");
		auth.append("<Skey ");
		auth.append("ci=\"" + ci + "\">");
		auth.append(ciContent);
		auth.append("</Skey>");
		auth.append("<Data type=\"" + dataType + "\">");
		auth.append(dataContent);
		auth.append("</Data>");
		auth.append("<Hmac>" + Hmac + "</Hmac>");
		auth.append("</Auth>");
		auth.append("</Request_auth>");

		String authRequest = auth.toString();
		authRequest = authRequest.replaceAll("\t", "");
		return authRequest;
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
			prop1.setAuthUrl(prop.getProperty("authUrl"));
			prop1.setKycUrl(prop.getProperty("kycUrl"));

		} catch (Exception e) {

			e.getMessage();
		}

		return prop1;

	}

}
