package com.suryoday.FdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.html2pdf.HtmlConverter;
import com.suryoday.FdOpening.Others.GenerateProperty;
import com.suryoday.FdOpening.Pojo.InterestRates;
import com.suryoday.FdOpening.Pojo.MerchantTCDetails;
import com.suryoday.FdOpening.Repository.InteresrRatesRepo;
import com.suryoday.FdOpening.Repository.MerchantConsentDetailsRepo;
import com.suryoday.FdOpening.Service.CustomerDetailsService;

@Component
public class CustomerDetailsServImpl implements CustomerDetailsService{
	private static Logger logger = LoggerFactory.getLogger(CustomerDetailsServImpl.class);
	@Autowired
	InteresrRatesRepo interestrepo;
	@Autowired
	MerchantConsentDetailsRepo consentDetailsRepo;
	
	public JSONObject getCustomerDetailsEtbOrNtb(String AadhaarNo, String PanNo, JSONObject header) {
		logger.debug("getCustomerDetailsEtbOrNtb Calling" );
		
		JSONObject sendResponse = new JSONObject();
		
		JSONObject req=new JSONObject();
		JSONObject data=new JSONObject();
		data.put("MobileNo","");
		data.put("AadhaarNo",AadhaarNo);
		data.put("AadhaarReferenceNo","");
		data.put("PanNo",PanNo);
		data.put("CustomerNo","");
		data.put("BranchCode","");
		data.put("ProductGroup","CASA");
		req.put("Data",data);
		
		logger.debug("getCustomerDetailsEtbOrNtb Req :: ", req.toString());
		
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			String url = x.BASEURL + "customers/details?api_key=" + x.api_key;
			logger.debug("Costomer ETB OR NTB Req :: {} ", url);

			obj = new URL(url);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("X-Request-ID", "IBR");
			con.setRequestProperty("X-User-ID", "30639");
			con.setRequestProperty("X-From-ID", "WNT");

			sendResponse = getResponseData(req, sendResponse, con, "POST");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	
	
	
	
	
	@Override
	public JSONObject getCustomerDetails(String mobileNo, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		JSONObject req=getCustomerDetailsReq(mobileNo);
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
			logger.debug(x.BASEURL + "customers/details?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "customers/details?api_key=" + x.api_key);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("X-Request-ID", "IBR");
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", "WNT");

			sendResponse = getResponseData(req, sendResponse, con, "POST");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	
	@Override
	public JSONObject validateCustomerMobileNumber(String mobileNo, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		
		JSONObject rqstBody= new JSONObject();
		JSONObject req= new JSONObject();
		req.put("MobileNumber", mobileNo);
		rqstBody.put("RqstBody", req);
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
			String url = x.BASEURL + "mobile/number/dedupe?api_key=" + x.api_key;
			logger.debug("URL :: "+url);
			logger.debug("Request :: " + rqstBody.toString());
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");			
			con.setRequestProperty("Postman-Token", header.getString("Postman-Token"));
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("cache-control", header.getString("cache-control"));
//			con.setRequestProperty("Postman-Token", header.getString("Postman-Token"));
			logger.debug("Request :: " + rqstBody.toString());
			sendResponse = getResponseData(rqstBody, sendResponse, con, "POST");
			logger.debug("Response :: " + sendResponse.toString());
		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	
	private JSONObject getCustomerDetailsReq(String mobileNo) {
		JSONObject parent=new JSONObject();
		JSONObject data=new JSONObject();
		data.put("MobileNo",mobileNo);
		data.put("AadhaarNo","");
		data.put("AadhaarReferenceNo","");
		data.put("PanNo","");
		data.put("CustomerNo","");
		data.put("BranchCode","");
		data.put("ProductGroup","CASA");
		parent.put("Data",data);
		System.out.println(parent);
		return parent;
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
	public JSONObject getAccountDetails(String accountNo, JSONObject header) {
JSONObject sendResponse = new JSONObject();
		
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
			logger.debug(x.BASEURL + "accounts/TDRD/"+accountNo+"?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "accounts/TDRD/"+accountNo+"?api_key=" + x.api_key);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("X-Request-ID", "SER");
			con.setRequestProperty("X-User-ID", "S7050");
			con.setRequestProperty("X-From-ID", "CB");
			con.setRequestProperty("X-To-ID","WNT");
			con.setRequestProperty("X-Transaction-ID", "EabeDcEE-db3c-BddD-CbD7-4bAA992c75d4");

			sendResponse = getResponse(accountNo, sendResponse, con, "GET");

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
	public JSONObject calculateDeposit(JSONObject jsonObject, JSONObject header) {
		String amount = jsonObject.getJSONObject("Data").getString("Amount");
		String tenure = jsonObject.getJSONObject("Data").getString("Tenure");
		String productCode = jsonObject.getJSONObject("Data").getString("ProductCode");
		JSONObject sendResponse = new JSONObject();
		
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
			logger.debug(x.BASEURL + "deposit/"+productCode+"/"+amount+"/calculator?Term="+tenure+"&api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "deposit/"+productCode+"/"+amount+"/calculator?Term="+tenure+"&api_key=" + x.api_key);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", "IBR");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("UserID", header.getString("UserID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));

			sendResponse = getResponse(amount, sendResponse, con, "GET");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}


	@Override
	public List<InterestRates> fetchInterestRates() {
		List<InterestRates> list=interestrepo.fetchInterestRates();
		if(list.isEmpty())
		{
			throw new NoSuchElementException("List is empty");
		}
		return list;
	}
	
	@Override
	public JSONObject cifCreation(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();

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
			logger.debug(x.BASEURL+"customers?api_key="+x.api_key);

			obj = new URL(x.BASEURL+"customers?api_key="+x.api_key);

			// obj= new
			// URL("https://intramashery.suryodaybank.co.in/ssfbuat/customer/v2?api_key=kyqak5muymxcrjhc5q57vz9v");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", "WNT");
			con.setRequestProperty("X-User-ID", "S5050");
			con.setRequestProperty("X-From-ID", "CB");
			con.setRequestProperty("X-To-ID", "WNT");
			con.setRequestProperty("X-Transaction-ID","EabeDcEE-db3c-BddD-CbD7-4bAA992c75d4");
//			con.setRequestProperty("X-Correlation-ID",String.valueOf(generateRandom(10)));
			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}
	
	public long generateRandom(int length) {
//      Random random = new Random();
		SecureRandom  random = new SecureRandom ();
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return Long.parseLong(new String(digits));
	}

	@Override
	public JSONObject nameMatch(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		String name1 = jsonObject.getJSONObject("Data").getString("Name1");
		String name2 = jsonObject.getJSONObject("Data").getString("Name2");
		JSONObject nameRequest=getNameReq(name1,name2);
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
			logger.debug(x.BASEURL+ "find/name/similarity?api_key=" + x.api_key);

			obj = new URL(x.BASEURL+ "find/name/similarity?api_key=" + x.api_key);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", "IEXCEED");
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));

			sendResponse = getResponseData(nameRequest, sendResponse, con, "GET");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	private JSONObject getNameReq(String name1, String name2) {
		JSONObject parent=new JSONObject();
		JSONObject data=new JSONObject();
		data.put("Name1",name1);
		data.put("Name2",name2);
		data.put("Type","individual");
		data.put("Preset","L");
		parent.put("Data",data);
		return parent;
	}


	@Override
	public JSONObject accountCreation(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();

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
			logger.debug(x.BASEURL+"account/CASA/v2?api_key="+x.api_key);

			obj = new URL(x.BASEURL+"account/CASA/v2?api_key="+x.api_key);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			// obj= new
			// URL("https://intramashery.suryodaybank.co.in/ssfbuat/customer/v2?api_key=kyqak5muymxcrjhc5q57vz9v");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}


	@Override
	public JSONObject getDetailsByCustId(String custId, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		JSONObject req=getDetailsByCustIdReq(custId);
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
			logger.debug(x.BASEURL + "customers/details?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "customers/details?api_key=" + x.api_key);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("X-Request-ID", "IBR");
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-From-ID", "WNT");

			sendResponse = getResponseData(req, sendResponse, con, "POST");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}


	private JSONObject getDetailsByCustIdReq(String custId) {
		JSONObject parent=new JSONObject();
		JSONObject data=new JSONObject();
		data.put("MobileNo","");
		data.put("AadhaarNo","");
		data.put("AadhaarReferenceNo","");
		data.put("PanNo","");
		data.put("CustomerNo",custId);
		data.put("BranchCode","");
		data.put("ProductGroup","CASA");
		parent.put("Data",data);
		System.out.println(parent);
		return parent;
	}


	@Override
	public JSONObject checkAml(JSONObject jsonObject) {
		JSONObject sendResponse = new JSONObject();
		JSONObject req=amlReq(jsonObject);
		logger.debug("checkAml req "+req.toString());
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
			//https://intramashery.suryodaybank.com/ssfb/customer/aml/query/v3
			//https://intramashery.suryodaybank.com/ssfb/
			String url = x.BASEURL + "customer/aml/query/v3";
			logger.debug("checkAml url :: "+ url);
			obj = new URL(url);
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("api_key",x.api_key);
			con.setRequestProperty("X-Request-ID", "WNT"); // RAVI -- when it's WNT we got 500 response (TAB)
			
			sendResponse = getResponseData(req, sendResponse, con, "POST");
			logger.debug("checkAml sendResponse "+sendResponse.toString());
		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	
	private JSONObject amlReq(JSONObject jsonObject) {
	    JSONObject data = jsonObject.getJSONObject("Data");

	    JSONObject geoLocation = new JSONObject()
	            .put("Latitude", "")
	            .put("Longitude", "");

	    JSONObject postalAddress = new JSONObject()
	            .put("Type", "")
	            .put("StreetName", "")
	            .put("BuildingNumber", "")
	            .put("Department", "")
	            .put("SubDepartment", "")
	            .put("TownName", "")
	            .put("City", "")
	            .put("Landmark", "")
	            .put("CountrySubDivision", "")
	            .put("Country", "")
	            .put("PostCode", "")
	            .put("AddressLine", "")
	            .put("GeoLocation", geoLocation);

	    JSONArray postalAddresses = new JSONArray().put(postalAddress);

	    JSONArray identificationTypes = new JSONArray()
	            .put(new JSONObject()
	                    .put("IdentityNumber", data.optString("AadharNo", ""))
	                    .put("IdentityType", "Aadhar"))
	            .put(new JSONObject()
	                    .put("IdentityNumber", "")
	                    .put("IdentityType", "PAN"));

	    JSONObject customer = new JSONObject()
	            .put("DateOfBirth", data.optString("Dob", ""))
	            .put("PostalAddress", postalAddresses)
	            .put("FirstName", data.optString("FirstName", ""))
	            .put("LastName", data.optString("LastName", ""))
	            .put("MiddleName", "")
	            .put("Identification_Type", identificationTypes)
	            .put("UCIC", "");

	    JSONObject amlData = new JSONObject()
	            .put("EntityType", "1")
	            .put("CreationDateTime", "2021-11-30")
	            .put("RequestorName", "Suryoday")
	            .put("Customer", customer)
	            .put("BranchId", "")
	            .put("RequestType", "");

	    JSONObject request = new JSONObject().put("Data", amlData);

	    return request;
	}

	
	
//	private JSONObject amlReq(JSONObject jsonObject) {
//		String req="{\r\n"
//				+ "    \"Data\": {\r\n"
//				+ "        \"EntityType\": \"1\",\r\n"
//				+ "        \"CreationDateTime\": \"2021-11-30\",\r\n"
//				+ "        \"RequestorName\": \"Suryoday\",    \r\n"
//				+ "        \"Customer\": {\r\n"
//				+ "            \"DateOfBirth\": \""+jsonObject.getJSONObject("Data").getString("Dob")+"\",\r\n"
//				+ "            \"PostalAddress\": [\r\n"
//				+ "                {\r\n"
//				+ "                    \"Type\": \"\",\r\n"
//				+ "                    \"StreetName\": \"\",\r\n"
//				+ "                    \"BuildingNumber\": \"\",\r\n"
//				+ "                    \"Department\": \"\",\r\n"
//				+ "                    \"SubDepartment\": \"\",\r\n"
//				+ "                    \"TownName\": \"\",\r\n"
//				+ "                    \"City\": \"\",\r\n"
//				+ "                    \"Landmark\": \"\",\r\n"
//				+ "                    \"CountrySubDivision\": \"\",\r\n"
//				+ "                    \"Country\": \"\",\r\n"
//				+ "                    \"PostCode\": \"\",\r\n"
//				+ "                    \"AddressLine\": \"\",\r\n"
//				+ "                    \"GeoLocation\": {\r\n"
//				+ "                        \"Latitude\": \"\",\r\n"
//				+ "                        \"Longitude\": \"\"\r\n"
//				+ "                    }\r\n"
//				+ "                }\r\n"
//				+ "            ],\r\n"
//				+ "            \"FirstName\": \""+jsonObject.getJSONObject("Data").getString("FirstName")+"\",\r\n"
//				+ "            \"LastName\": \""+jsonObject.getJSONObject("Data").getString("LastName")+"\",\r\n"
//				+ "            \"MiddleName\": \"\",\r\n"
//				+ "            \"Identification_Type\": [\r\n"
//				+ "                {\r\n"
//				+ "                    \"IdentityNumber\": \""+jsonObject.getJSONObject("Data").getString("AadharNo")+"\",\r\n"
//				+ "                    \"IdentityType\": \"Aadhar\"\r\n"
//				+ "                },\r\n"
//				+ "                {\r\n"
//				+ "                    \"IdentityNumber\": \"\",\r\n"
//				+ "                    \"IdentityType\": \"PAN\"\r\n"
//				+ "                }\r\n"
//				+ "            ],\r\n"
//				+ "            \"UCIC\": \"\"\r\n"
//				+ "        },\r\n"
//				+ "        \"BranchId\": \"\",\r\n"
//				+ "        \"RequestType\": \"\"\r\n"
//				+ "    }\r\n"
//				+ "}";
//		JSONObject request=new JSONObject(req);
//		return request;
//	}
	
	@Override
	public JSONObject dmsUpload(JSONObject jsonObject) throws IOException {
		JSONObject sendResponse = new JSONObject();
		String request4 = getHtmlString(jsonObject);
		String OutputFileName = "";
		
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
//		OutputFileName = "D://Web//"+jsonObject.getString("accountNo")+".pdf";
		OutputFileName="/opt/digital/apache-tomcat-9.0.37/temp"+jsonObject.getString("accountNo")+".pdf";
		HtmlConverter.convertToPdf(request4, new FileOutputStream(OutputFileName));
		document.close();
		byte[] inFileBytes = Files.readAllBytes(Paths.get(OutputFileName));
		JSONObject parent=new JSONObject();
		JSONObject data=new JSONObject();
		data.put("DocName",jsonObject.getString("customerId")+"_QR_Merchant_Consent.pdf");
		data.put("DocType","application/pdf");
		data.put("FileContent",Base64.getEncoder().encodeToString(inFileBytes));
		JSONArray metadata=new JSONArray();
		JSONObject metadata1=new JSONObject();
		metadata1.put("meta-Id", "310");
		metadata1.put("meta-Name", "MD_Customer");
		JSONObject properties=new JSONObject();
		JSONObject pro233=new JSONObject();
		pro233.put("DataName","Application_Number");
		pro233.put("Value","");
		properties.put("PRO233",pro233);
		JSONObject pro222=new JSONObject();
		pro222.put("DataName","Document_Number");
		pro222.put("Value",jsonObject.getString("customerId"));
		properties.put("PRO222",pro222);
		JSONObject pro223=new JSONObject();
		pro223.put("DataName","Account_Number");
		pro223.put("Value",jsonObject.getString("accountNo"));
		properties.put("PRO223",pro223);
		JSONObject pro221=new JSONObject();
		pro221.put("DataName","Document_Type");
		pro221.put("Value","CIF");
		properties.put("PRO221",pro221);
		JSONObject pro220=new JSONObject();
		pro220.put("DataName","CIF_Number");
		pro220.put("Value",jsonObject.getString("customerId"));
		properties.put("PRO220",pro220);
		metadata1.put("Properties",properties);
		metadata.put(metadata1);
		data.put("Metadata",metadata);
		data.put("Extension","pdf");
		data.put("Root",false);
		parent.put("Data",data);
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
			logger.debug(x.BASEURL + "banking/2/dms/upload?api_key="+x.api_key);

			obj = new URL(x.BASEURL + "banking/2/dms/upload?api_key="+x.api_key);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json");
			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        String formattedDateTime = now.format(dateTimeFormatter);
				con.setRequestProperty("X-Correlation-ID",formattedDateTime);
			con.setRequestProperty("X-Request-ID", "JPB");

			
//			System.out.println(jsonObject.getString("accountNo"));
			List<MerchantTCDetails> list=consentDetailsRepo.fetchByAccountId(jsonObject.getString("accountNo"));
//			System.out.println(list);
			MerchantTCDetails merchantTCDetails = list.get(0);
			merchantTCDetails.setDmsUploadReq(parent.toString());
			sendResponse = getResponseData(parent, sendResponse, con, "PUT");
			if (sendResponse != null) {
				String Data2 = sendResponse.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);
				merchantTCDetails.setDmsUploadResp(Data1.toString());
				if(Data1.getJSONArray("Response").getJSONObject(0).has("Data"))
				{	
					merchantTCDetails.setMerchantPdf(inFileBytes);	
//					merchantTCDetails.setDmsUpload(true);
				}
				consentDetailsRepo.save(merchantTCDetails);
				return Data1;
			}

		} catch (Exception e) {

//			e.getMessage();
			e.printStackTrace();
		}

		return sendResponse;		
	}

	private String getHtmlString(JSONObject jsonObject) throws IOException {
		
		
		StringBuilder htmlString = new StringBuilder();
		htmlString.append(new String("<!DOCTYPE html>\r\n"
				+ "<html lang=\"en\">\r\n"
				+ "  <head>\r\n"
				+ "    <meta charset=\"UTF-8\" />\r\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\r\n"
				+ "    <title>Merchant T&C PDF</title>\r\n"
				+ "    <style>\r\n"
				+ "      body {\r\n"
				+ "        font-family: Arial, sans-serif;\r\n"
				+ "        margin: 0;\r\n"
				+ "        padding: 40px;\r\n"
				+ "        background-color: #f8f9fa;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .pdf-container {\r\n"
				+ "        width: 600px;\r\n"
				+ "        background: white;\r\n"
				+ "        padding: 40px;\r\n"
				+ "        margin: auto;\r\n"
				+ "        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\r\n"
				+ "        border-radius: 10px;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .header {\r\n"
				+ "        display: flex;\r\n"
//				+ "        justify-content: space-between;\r\n"
				+ "        align-items: center;\r\n"
				+ "        border-bottom: 3px solid #f58220;\r\n"
				+ "        padding-bottom: 10px;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .logo-placeholder {\r\n"
				+ "        width: 150px;\r\n"
				+ "        height: 50px;\r\n"
				+ "        background: #ddd;\r\n"
				+ "        display: flex;\r\n"
				+ "        justify-content: center;\r\n"
				+ "        align-items: center;\r\n"
				+ "        font-size: 12px;\r\n"
				+ "        font-weight: bold;\r\n"
				+ "        border-radius: 5px;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .title {\r\n"
				+ "        font-size: 22px;\r\n"
				+ "        font-weight: bold;\r\n"
				+ "        color: #f58220;\r\n"
				+ "        text-align: left;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .content {\r\n"
				+ "        margin-top: 20px;\r\n"
				+ "        font-size: 14px;\r\n"
				+ "        color: #333;\r\n"
				+ "        line-height: 1.6;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .content ol {\r\n"
				+ "        padding-left: 20px;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      .footer {\r\n"
				+ "        margin-top: 30px;\r\n"
				+ "        text-align: center;\r\n"
				+ "        font-size: 12px;\r\n"
				+ "        color: #555;\r\n"
				+ "        background: #002f87;\r\n"
				+ "        padding: 10px;\r\n"
				+ "        border-radius: 5px;\r\n"
				+ "        color: white;\r\n"
				+ "      }\r\n"
				+ "      .custom-text {\r\n"
				+ "        color: #33388c;\r\n"
				+ "      }\r\n"
				+ "      .text-color {\r\n"
				+ "        color: #393a72;\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      ul {\r\n"
				+ "        list-style: none; /* Remove default bullet points */\r\n"
				+ "        padding: 0; /* Remove default padding */\r\n"
				+ "        margin: 0; /* Remove default margin */\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "      li {\r\n"
				+ "        margin-bottom: 8px; /* Add spacing between items */\r\n"
				+ "      }\r\n"
				+ "    </style>\r\n"
				+ "  </head>\r\n"
				+ "  <body>\r\n"
				+ "    <div class=\"pdf-container\">\r\n"
				+ "      <div class=\"header\">\r\n"
				+ "        <div class=\"title custom-text\">Merchant Terms & Conditions</div>\r\n"
				+ "        <div class=\"\">\r\n"
//				+ "            <img src=\"../assets/logo (2).png\"/>\r\n"
				+ "        </div>\r\n"
				+ "      </div>\r\n"
				+ "\r\n"
				+ "      <div class=\"content\">\r\n"
				+ "        <h2 class=\"text custom-text\">The merchant undertake to</h2>\r\n"
				+ "        <p>\r\n"
				+ "          <strong>QR Codes</strong> QR Codes” refers to Quick Response Code, “QR\r\n"
				+ "          Code” – shall mean a unique readable code of an array of black and\r\n"
				+ "          white squares which can be scanned for making payment specific to the\r\n"
				+ "          Merchant using UPI. These terms and conditions govern your use of UPI\r\n"
				+ "          QR merchant services provided by us. By signing the Merchant\r\n"
				+ "          Application Form, you agree to accept payment for your goods and\r\n"
				+ "          services through our UPI QR merchant services upon these terms and\r\n"
				+ "          conditions. You also agree to comply with Operating Policies and\r\n"
				+ "          Procedures of National Payment Corporation of India (NPCI) and the\r\n"
				+ "          Bank from time to time. If you use our merchant services, you are\r\n"
				+ "          deemed to have agreed to these terms and conditions\r\n"
				+ "        </p>\r\n"
				+ "        <ol class=\"text-color\">\r\n"
				+ "          <li>\r\n"
				+ "            Ensure that business activities, products, and services comply with\r\n"
				+ "            laws.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Not sell or accept payment for goods and services which are\r\n"
				+ "            prohibited or restricted or likely be prohibited or restricted under\r\n"
				+ "            applicable laws, rules and regulations or which violates the Bank’s,\r\n"
				+ "            internal policy including such goods and services that is notified\r\n"
				+ "            in writing to the Merchant from time to time.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Comply with its contract with the customer including but not limited\r\n"
				+ "            to the nature, quality and delivery of goods and services contracted\r\n"
				+ "            to be sold and supplied.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Not discriminate against any customer wanting to make buy goods\r\n"
				+ "            and/or services using the QR code.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Not impose any surcharge or price increase on any customer who wants\r\n"
				+ "            to use the QR code to purchase goods and/or service.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Ensure that all the Merchants’ representations and/or advertisements\r\n"
				+ "            relating to merchandise and/or services offered by the Merchant are\r\n"
				+ "            not illegal, do not damage the Bank’s, reputation and the Merchant\r\n"
				+ "            must not represent that the Bank for any of the goods and services\r\n"
				+ "            sold or supplied by the Merchant.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Be solely responsible for resolving all disputes which may arise\r\n"
				+ "            with the customer, amicably and promptly without involving the Bank\r\n"
				+ "            in any way.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Provide the Bank with a list containing the names and addresses of its updated location where the QR is used and to provide an updated list within seven (7) days from the date changes are made; and\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Comply with applicable security requirements and established security standards, and that its service providers (including those appointed by the Merchant for accessing, storing, transmitting and processing customer data) also comply with the same.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            In the event that suspicious or abnormal settlement transactions are detected and the Bank has reason to believe that there is fraud, the Merchant shall facilitate the Bank’s further investigation and NPCI and / or the Bank may withhold and/or refuse payment for such settlement transactions to the Merchant.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Ensure not to be involved in terrorism financing or business or business transactions and/or where monies are received from unlawful activities in breach of the Prevention of Money Laundering Act 2002.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Not to accept payment for a transaction if it knew or ought to have known or had reasons to suspect or that it was a fraudulent or improper transaction\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Any information or any documents provided by the Merchant to the Bank is not incorrect or misleading or inaccurate.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            If any customer disputes the validity of any transaction or if any transaction is invalid or if the Merchant breaches any of its undertakings or if the Bank is otherwise allowed by law or under any rules and regulations issued by RBI or NPCI, the Bank may by giving notice to the Merchant:- (a) refuse payment to the Merchant for the amount of such transaction; and (b) where payment has been made by the Bank to the Merchant, deduct such payment from any of the Merchant’s sales proceeds and/or raise a debit against the Merchant for the amount of such transaction and debit or cause to be debited the same from the Merchant's Account.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            To indemnify the Bank on a full indemnity basis against all claims, liabilities, penalties, expenses, costs, loss or damage of whatever nature (including legal costs on a full indemnity basis incurred by the Bank) suffered or incurred by the Bank arising directly or indirectly from any breach of these terms and conditions by, or from any act or omission of, the Merchant or its servants, agents, employees or contractors.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Allow the Bank, to periodically conduct a site visits and inspections, due diligence and/or audit review of the financial and operational condition of the Merchant (during business hours) upon the Bank’s written request to do so (electronic or otherwise).\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            The Bank shall not be liable in case of any unauthorised, fraudulent, unlawful transactions or use of the UPI Services and/or the QR Code by the Merchant and/or the User and disclaims all liability arising out of any unauthorised or unlawful source of payment in any transactions pursuant to the use of such UPI services and/or the QR Code.\r\n"
				+ "          </li>\r\n"
				+ "          <li>\r\n"
				+ "            Nothing contained herein shall limit or restrict or preclude the Bank from pursuing such further and other legal actions, including but not limited to the right to disable the QR Code, against the Merchant or User or any other third party for any breach or noncompliance of the terms as mentioned herewith.\r\n"
				+ "          </li>\r\n"
				+ "<li>\r\n"
				+ "    The Bank shall have the absolute right to grant access to the Merchant to use the QR Code. Nothing herein shall restrict the Bank’s right to delete / block / blacklist / prohibit creation of such QR Code which in the Bank’s sole and absolute discretion are misleading and/or likely to create confusion.\r\n"
				+ "</li>\r\n"
				+ "        </ol>\r\n"
				+ "      </div>\r\n"
				+ "   \r\n"
				+ "\r\n"
				+ "      <div class=\"container\">\r\n"
				+ "        <p class=\"declaration custom-text\">Declaration:</p>\r\n"
				+ "        <p class=\"form-text\">\r\n"
				+ "          I, <span class=\"underline\">"+jsonObject.getString("name")+"</span>, confirm\r\n"
				+ "          that I will be using the QR for receiving payments and have read,\r\n"
				+ "          understood, and agreed to the terms and conditions mentioned above. My\r\n"
				+ "          bank account number is\r\n"
				+ "          <span class=\"underline\">"+jsonObject.getString("accountNo")+"</span> and I\r\n"
				+ "          acknowledge the same by signing below.\r\n"
				+ "        </p>\r\n"
				+ "\r\n"
				+ "        <div class=\"row signature\">\r\n"
				+ "          <div class=\"col-md-6\">\r\n"
				+ "            <label class=\"form-text\">Customer signature:</label>\r\n"
				+ "            <span class=\"underline\">"+jsonObject.getString("name")+"</span>\r\n"
				+ "          </div>\r\n"
				+ "          <div class=\"col-md-6 text-end\">\r\n"
				+ "            <label class=\"form-text\">Date:</label>\r\n"
				+ "            <span class=\"underline\">"+LocalDateTime.now()+"</span>\r\n"
				+ "          </div>\r\n"
				+ "        </div>\r\n"
				+ "      </div>\r\n"
				+ "\r\n"
				+ "      <div class=\"footer\">\r\n"
				+ "        <p>SURYODAY SMALL FINANCE BANK</p>\r\n"
				+ "        <p>1800 266 7711 | www.suryodaybank.com | Visit nearest branch</p>\r\n"
				+ "      </div>\r\n"
				+ "    </div>\r\n"
				+ "  </body>\r\n"
				+ "</html>\r\n"
				+ ""));
		
		
		return htmlString.toString();
	}

	@Override
	public JSONObject downloadPdf(JSONObject jsonObject) {
//		List<MerchantTCDetails> list=consentDetailsRepo.fetchByAccountId(jsonObject.getString("AccountNo"));
		MerchantTCDetails merchantTCDetails = consentDetailsRepo.findById(Long.parseLong(jsonObject.getString("AccountNo"))).orElseThrow(() ->new NoSuchElementException("No record found"));
		JSONObject resp=new JSONObject();
		JSONObject data=new JSONObject();
		data.put("Base64",Base64.getEncoder().encodeToString(merchantTCDetails.getMerchantPdf()));
		resp.put("Data",data);
		return resp;
	}


	
}
