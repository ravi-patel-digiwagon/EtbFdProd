package com.suryoday.FdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suryoday.FdOpening.Others.GenerateProperty;
import com.suryoday.FdOpening.Others.ResponseData;
import com.suryoday.FdOpening.Pojo.FdOpening;
import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Pojo.MerchantTCDetails;
import com.suryoday.FdOpening.Pojo.OtpValidation;
import com.suryoday.FdOpening.Repository.FdOpeningRepository;
import com.suryoday.FdOpening.Repository.MerchantConsentDetailsRepo;
import com.suryoday.FdOpening.Repository.NtbFdRepo;
import com.suryoday.FdOpening.Repository.OtpValidationRepo;
import com.suryoday.FdOpening.Service.SendOtpService;

@Component
public class SendOtpServiceImpl implements SendOtpService{
	private static Logger logger = LoggerFactory.getLogger(SendOtpServiceImpl.class);
	@Autowired
	FdOpeningRepository fdOpeningRepo;
	@Autowired
	SendOtpService otpservice;
	@Autowired
	NtbFdRepo fdOpeningNtbRepo;
	@Autowired
	OtpValidationRepo validateotprepo;
	@Autowired
	MerchantConsentDetailsRepo merchantConsentDetailsRepo;
	@Override
	public JSONObject sendOtp(String mobileNo, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		JSONObject request4 = getRequest4(mobileNo);

		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			logger.debug(x.BASEURL + "notification/otp/sms?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "notification/otp/sms?api_key=" + x.api_key);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
//				con.setRequestProperty("X-Correlation-ID",header.getString("X-Correlation-ID"));
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));

			sendResponse = getResponseData(request4, sendResponse, con, "POST");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	private JSONObject getRequest4(String mobileNo) {
		JSONObject Data = new JSONObject();

		JSONObject data = new JSONObject();
		data.put("MobileNumber","91"+mobileNo);
		data.put("CustomerId", "");
		data.put("EmailId", "");
		data.put("IntFlag", "1");
		data.put("TemplateId", "OTP628");
		JSONArray dynamicparam = new JSONArray();
		JSONObject obj1 = new JSONObject();
		obj1.put("Value", "");
		obj1.put("Name", "otp");
		dynamicparam.put(obj1);
		data.put("DynamicParam", dynamicparam);
		Data.put("Data", data);
		System.out.println(Data);
		return Data;

	}
	
	private static JSONObject getResponseData(JSONObject parent, JSONObject sendAuthenticateResponse,
			HttpURLConnection con, String MethodType) throws IOException {

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

			JSONObject sendauthenticateResponse1 = new JSONObject();
			sendauthenticateResponse1.put("data", response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		} else {
			logger.debug("POST request not worked");

			JSONObject sendauthenticateResponse1 = new JSONObject();

			JSONObject errr = new JSONObject();
			errr.put("Description", "Server Error " + responseCode);

			JSONObject j = new JSONObject();
			j.put("Error", errr);

			sendauthenticateResponse1.put("data", "" + j);
			sendAuthenticateResponse = sendauthenticateResponse1;
		}

		return sendAuthenticateResponse;

	}

	@Override
	public JSONObject validateOTP(String oTP, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		URL obj = null;
		try {
			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			// String
			// url="https://intramashery.suryodaybank.co.in/transaction/OTP/validate?api_key=kyqak5muymxcrjhc5q57vz9v&OTP=";
			int otp = Integer.parseInt(oTP);
			// String transactionType="&TransactionType=D";
			logger.debug(x.BASEURL+"tr/OTP/validate?api_key="+x.api_key+"&OTP="+oTP+"&TransactionType=D");
			obj = new URL(x.BASEURL+"tr/OTP/validate?api_key="+x.api_key+"&OTP="+oTP+"&TransactionType=D");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID", header.getString("X-Correlation-ID"));
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));

			sendResponse = getResponse(oTP, sendResponse, con, "GET");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	private static JSONObject getResponse(String parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
			String MethodType) throws IOException {

		con.setDoOutput(true);
		// OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
		// os.write(parent.toString());
		// os.flush();
		// os.close();

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

			JSONObject sendauthenticateResponse1 = new JSONObject();
			sendauthenticateResponse1.put("data", response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		} else {
			logger.debug("GET request not worked");

			JSONObject sendauthenticateResponse1 = new JSONObject();

			JSONObject errr = new JSONObject();
			errr.put("Description", "Server Error " + responseCode);

			JSONObject j = new JSONObject();
			j.put("Error", errr);

			sendauthenticateResponse1.put("data", "" + j);
			sendAuthenticateResponse = sendauthenticateResponse1;
		}

		return sendAuthenticateResponse;

	}

	@Override
	public String saveData(FdOpening fdopening) {
		long id=1;
		Optional<Long> optional = fdOpeningRepo.fetchLastId();
		if(optional.isPresent())
		{
			id=optional.get();
			id++;
		}
		fdopening.setId(id);
		fdOpeningRepo.save(fdopening);
		return "data saved ";
	}

	@Override
	public String getSessionId(String mobileNo) {
		Optional<String> optional = fdOpeningRepo.getSessionId(mobileNo);
		if(optional.isPresent())
		{
			String sessionId=optional.get();
			return sessionId;
		}
		else
		{
			throw new NoSuchElementException("No sessionId Is Present");
		}
		
	}

	@Override
	public String getRequestSession(HttpServletRequest req) {
		HttpSession session=req.getSession();
		session.invalidate();
		String sessionId = session.getId();
		return sessionId;
	}

	@Override
	public boolean validateSessionId(String x_Session_ID, String mobileNo) {
		String sessionId = otpservice.getSessionId(mobileNo);
		System.out.println(sessionId);
		if(sessionId.equals(x_Session_ID))
		{
			return true;
		}
		else {
			return false;
		}
		
	}

	@Override
	public void deleteAllSessions(String mobileNo, String sessionid) {
		fdOpeningRepo.deleteAllSessions(mobileNo,sessionid);
		
	}

	@Override
	public FdOpening fetchData(String mobileNo, String type, String status) {
		Optional<FdOpening> optional=fdOpeningRepo.fetchData(mobileNo,type,status);
		if(optional.isPresent())
		{
			return optional.get();
		}
		throw new NoSuchElementException("No record found");
	}

	@Override
	public void save(FdOpening fdOpening) {
		fdOpeningRepo.save(fdOpening);
		
	}

	@Override
	public String saveNewJourney(String mobileNo) {
		int count=fdOpeningNtbRepo.count(mobileNo,"Progress");
		if(count>=3)
		{
			throw new NoSuchElementException("You cant create new journey");
		}
		String applicationNo = createNTbApplicationNo();
		LocalDateTime now=LocalDateTime.now();
		FdOpeningNTB fdOpeningNTB=new FdOpeningNTB();
		fdOpeningNTB.setApplicationNo(Long.parseLong(applicationNo));
		fdOpeningNTB.setMobileNo(mobileNo);
		fdOpeningNTB.setCustType("NTB");
		fdOpeningNTB.setStatus("Progress");
		fdOpeningNTB.setCreatedDate(now);
		fdOpeningNtbRepo.save(fdOpeningNTB);
		return applicationNo;
	}

	@Override
	public JSONObject saveNtbFd(String mobileNo, String type, String status) {
		List<FdOpeningNTB> list = fdOpeningNtbRepo.fetchByMobileNo(mobileNo,status);
		if(list.isEmpty())
		{
			String applicationNo = createNTbApplicationNo();
			LocalDateTime now=LocalDateTime.now();
			FdOpeningNTB fdOpeningNTB=new FdOpeningNTB();
			fdOpeningNTB.setApplicationNo(Long.parseLong(applicationNo));
			fdOpeningNTB.setMobileNo(mobileNo);
			fdOpeningNTB.setCustType(type);
			fdOpeningNTB.setStatus(status);
			fdOpeningNTB.setCreatedDate(now);
			fdOpeningNtbRepo.save(fdOpeningNTB);
			JSONObject resp=new JSONObject();
			resp.put("ApplicationNo", applicationNo);
			resp.put("ApplicationStatus","New");
			return resp;
		}
		else
		{
			FdOpeningNTB fdOpeningNTB = list.get(0);
			long applicationNo2 = fdOpeningNTB.getApplicationNo();
			JSONObject resp=new JSONObject();
			resp.put("ApplicationNo", String.valueOf(applicationNo2));
			resp.put("ApplicationStatus","Existing");
			return resp;
		}
	}
	
	public String createNTbApplicationNo() {
		String applicationNo = (LocalDate.now().toString().replace("-", "") + "0001").substring(2, 12);
		Optional<String> fetchLastApplicationNo = fdOpeningNtbRepo.fetchLastApplicationNo();

		if (fetchLastApplicationNo.isPresent()) {
			logger.debug("If ApplicationNo is Present");

			String application_No = fetchLastApplicationNo.get();
			logger.debug(application_No);
			String dateInDB = application_No.substring(0, 6);
			String currentDate = LocalDate.now().toString().replace("-", "").substring(2, 8);
			if (currentDate.equals(dateInDB)) {
				logger.debug("If current Date  is equal to db date");
				Long applicationno = Long.parseLong(application_No);
				applicationno++;
				applicationNo = applicationno.toString();
				logger.debug(applicationNo + "after increment");
			}

//			Long application = Long.parseLong(application_No);
//			application++;
//			applicationNo = application.toString();
		}
		return applicationNo;
	}

	@Override
	public void save(FdOpeningNTB fdOpening) {
		fdOpeningNtbRepo.save(fdOpening);
		
	}

	@Override
	public Optional<OtpValidation> fetchOtpData(String mobileNo) {
		Optional<OtpValidation> optional=validateotprepo.fetchOtpData(mobileNo);
		return optional;
	}

	@Override
	public void saveValidateData(OtpValidation otpValidation) {
		validateotprepo.save(otpValidation);
		
	}

	@Override
	public JSONObject sendSms(String mobileNo, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		String data="{\r\n"
				+ "    \"Data\": {\r\n"
				+ "        \"PhoneNumber\": \"91"+mobileNo+"\",\r\n"
				+ "        \"OTP\": \"\",\r\n"
				+ "        \"IntFlag\": \"1\",\r\n"
				+ "        \"DynamicParam\": [\r\n"
				+ "        ],\r\n"
				+ "        \"TemplateId\": \"SMS1318\"\r\n"
				+ "    }\r\n"
				+ "}";
		logger.debug("Send sms req "+new JSONObject(data));
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			logger.debug(x.BASEURL + "notification/sms?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "notification/sms?api_key=" + x.api_key);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
//				con.setRequestProperty("X-Correlation-ID",header.getString("X-Correlation-ID"));
			con.setRequestProperty("X-Request-ID","WNT");
			

			sendResponse = getResponseData(new JSONObject(data), sendResponse, con, "POST");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	@Override
	public JSONObject sendsms(JSONObject jsonObject) {
		JSONObject sendResponse = new JSONObject();
		String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
		List<MerchantTCDetails> list= merchantConsentDetailsRepo.fetchByMobNo(mobileNo);
		JSONObject request4 = new JSONObject(new String("{\r\n"
				+ "    \"Data\": {\r\n"
				+ "        \"DynamicParam\": [\r\n"
				+ "            {\r\n"
				+ "                \"Name\": \"Timestamp\",\r\n"
				+ "                \"Value\": \"https://applyonline.suryodaybank.com/suryoday-merchant-qr/link?account="+Base64.getEncoder().encodeToString(String.valueOf(list.get(0).getApplicationNo()).getBytes())+"\"\r\n"
				+ "            }\r\n"
				+ "            \r\n"
				+ "        ],\r\n"
				+ "        \"PhoneNumber\": \"91"+mobileNo+"\",\r\n"
				+ "        \"OTP\": \"false\",\r\n"
				+ "        \"IntFlag\": \"0\",\r\n"
				+ "        \"TemplateId\": \"SMS1360\"\r\n"
				+ "    }\r\n"
				+ "}"));

		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			logger.debug(x.BASEURL + "notification/sms?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "notification/sms?api_key=" + x.api_key);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
//				con.setRequestProperty("X-Correlation-ID",header.getString("X-Correlation-ID"));
			con.setRequestProperty("X-Request-ID", "IEXCEED");
			

			sendResponse = getResponseData(request4, sendResponse, con, "POST");
			if (sendResponse != null) {
				String Data2 = sendResponse.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);
				return Data1;
			}

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;	
	}

	@Override
	public JSONObject saveConsentDetails(MerchantTCDetails merchantTCDetails) throws Exception {
		try {
		merchantTCDetails.setApplicationNo(Long.parseLong(generateApplicationNumber()));
		merchantTCDetails.setMerchantPdf(null);
		merchantConsentDetailsRepo.save(merchantTCDetails);
		JSONObject data=new JSONObject();
		data.put("message", "Data saved successfully");
		return data;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}
	
	public static String generateApplicationNumber() {
        LocalDate date = LocalDate.now(); // Get current date
        String formattedDate = date.toString().replace("-", ""); // Format as YYYYMMDD
        
//        Random random = new Random();
        SecureRandom  random = new SecureRandom();
        int randomNumber = 1000 + random.nextInt(9000); // Generate a 4-digit random number

        return String.valueOf(randomNumber); // Combine date and random number
    }

	@Override
	public JSONObject sendOtpNew(String mobileNo, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		JSONObject request4 = getRequest5(mobileNo);

		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			logger.debug(x.BASEURL + "notification/otp/sms?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "notification/otp/sms?api_key=" + x.api_key);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
//				con.setRequestProperty("X-Correlation-ID",header.getString("X-Correlation-ID"));
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));

			sendResponse = getResponseData(request4, sendResponse, con, "POST");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	private JSONObject getRequest5(String mobileNo) {
		JSONObject Data = new JSONObject();

		JSONObject data = new JSONObject();
		data.put("MobileNumber","91"+mobileNo);
		data.put("CustomerId", "");
		data.put("EmailId", "");
		data.put("IntFlag", "1");
		data.put("TemplateId", "OTP454");
		JSONArray dynamicparam = new JSONArray();
		JSONObject obj1 = new JSONObject();
		obj1.put("Value", "");
		obj1.put("Name", "otp");
		dynamicparam.put(obj1);
		data.put("DynamicParam", dynamicparam);
		Data.put("Data", data);
		System.out.println(Data);
		return Data;

	}
	
	@Override
	public JSONObject emailOtp(JSONObject jsonObject) throws Exception {
		try {
			JSONObject response=new JSONObject();
			String req="{\r\n"
					+ "  \"Data\": {\r\n"
					+ "    \"MobileNumber\": \"\",\r\n"
					+ "    \"CustomerId\": \"\",\r\n"
					+ "    \"EmailId\": \""+jsonObject.getString("EmailId")+"\",\r\n"
					+ "    \"IntFlag\": \"0\",\r\n"
					+ "    \"TemplateId\": \"|Suryoday_Bank_OTP607\",\r\n"
					+ "    \"DynamicParam\": [\r\n"
					+ "      {\r\n"
					+ "        \"Value\": \"\",\r\n"
					+ "        \"Name\": \"otp\"\r\n"
					+ "      }\r\n"
					+ "    ]\r\n"
					+ "  }\r\n"
					+ "}";
			JSONObject generateOtpRequest = new JSONObject(req);
			GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			x.getappprop();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			String sendOtpUrl = x.BASEURL + "notification/otp/sms?api_key=" + x.api_key;
			logger.debug("Send otp Url:- " + sendOtpUrl);
			URL obj = new URL(sendOtpUrl);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("accept", "application/json");
			con.setRequestProperty("X-Request-ID", "MB");
			response=ResponseData.postResponseData(generateOtpRequest, response, con, "POST");
			return response;
		} catch (Exception e) {
			logger.error("Send Otp API Error"+e.getMessage());
			throw new Exception("Send Otp Api Failed");
		}
	}


}
