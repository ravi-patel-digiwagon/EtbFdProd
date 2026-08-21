package com.suryoday.FdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suryoday.FdOpening.Others.GenerateProperty;
import com.suryoday.FdOpening.Pojo.FdOpening;
import com.suryoday.FdOpening.Repository.FdOpeningRepository;
import com.suryoday.FdOpening.Service.CustomerDetailsService;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.FdRecieptService;

@Component
public class FdRecieptServImpl implements FdRecieptService {
	private static Logger logger = LoggerFactory.getLogger(FdRecieptServImpl.class);
	@Autowired
	FdOpeningRepository fdopeningrepo;
	@Autowired
	CustomerDetailsService customerdetailsservice;

	@Override
	public String downloadRecietPdf(StringBuilder htmlString, String mobileNo, FdOpening fdopening) {
		JSONObject header = new JSONObject();
		header.put("X-User-ID", "30639");
		JSONObject customerDetails = customerdetailsservice.getCustomerDetails(mobileNo, header);
		String data = customerDetails.getString("data");
		JSONObject Data = new JSONObject(data);
		System.out.println(Data);
		JSONArray addtessDet = Data.getJSONObject("Data").getJSONArray("CustomerDetails").getJSONObject(0)
				.getJSONObject("AddressDetails").getJSONArray("AddressDet");
		String address1 = addtessDet.getJSONObject(0).getString("ADDRESS1");
		String address2 = addtessDet.getJSONObject(0).getString("ADDRESS2");
		String city = addtessDet.getJSONObject(0).getString("CITY");
		String pinCode = addtessDet.getJSONObject(0).getString("PINCODE");
		String state = addtessDet.getJSONObject(0).getString("STATE");
		String country = addtessDet.getJSONObject(0).getString("COUNTRY");
		String accountNo = Data.getJSONObject("Data").getJSONArray("AccountDetails").getJSONObject(0)
				.getString("AccountId");
		String customerid = Data.getJSONObject("Data").getJSONArray("CustomerDetails").getJSONObject(0)
				.getString("CIFNo");
		String panNo = Data.getJSONObject("Data").getJSONArray("CustomerDetails").getJSONObject(0)
				.getString("PANNumber");
		System.out.println(addtessDet);
		String depositAccountNo = fdopening.getDepositAccountNo();
		JSONObject getAccountDetails = customerdetailsservice.getAccountDetails(depositAccountNo, header);
		System.out.println(getAccountDetails);
		String data2 = getAccountDetails.getString("data");
		JSONObject Data2 = new JSONObject(data2);
		JSONObject accountDetailsJson = Data2.getJSONObject("Data").getJSONArray("Account").getJSONObject(0);
		System.out.println(accountDetailsJson);
		String maturityDate = fdopening.getMaturityDate();
		LocalDateTime maturitydate = LocalDateTime.parse(maturityDate);
		String term = fdopening.getTenure();
		term = term.replace("Y", "Y ");
		term = term.replace("M", "M ");
		String maturityType = "";
		String maturityTypeInterest = "";
		String disbOption = accountDetailsJson.getJSONObject("Maturity").getString("DisbursementOption");
		if (disbOption.equals("0")) {
			maturityTypeInterest = "Re Invest";
		} else if (disbOption.equals("2")) {
			maturityTypeInterest = "Transfer To Another Account";
		}
		String disbOption2 = accountDetailsJson.getJSONObject("Maturity").getJSONObject("Principal")
				.getString("DisbursementOption");
		if (disbOption2.equals("3")) {
			maturityType = "Auto Renewal";
		} else if (disbOption2.equals("2")) {
			maturityType = "Transfer To Another Account";
		}
		String NomineeRegistered = "";
		String NomineeName = "";
		JSONObject party = accountDetailsJson.getJSONObject("Party");
		if (party.has("Name")) {
			String nomineeName = party.getString("Name");
			if (nomineeName.length() == 0) {
				NomineeRegistered = "No";
			} else {
				NomineeRegistered = "Yes";
				NomineeName = nomineeName;
			}
		} else {
			NomineeRegistered = "No";
		}
		String description = accountDetailsJson.getJSONObject("Product").getString("Description");
		GenerateProperty x = GenerateProperty.getInstance();
		htmlString.append(new String("<!DOCTYPE html>\r\n" + "<html>\r\n" + "\r\n" + "<head>\r\n" + "    <style>\r\n"
				+ "        table,\r\n" + "        th,\r\n" + "        td {\r\n"
				+ "            border: 1px solid black;\r\n" + "            border-collapse: collapse;\r\n"
				+ "        }\r\n" + "        p{\r\n" + "            margin: 8px;\r\n" + "        }" + "    </style>\r\n"
				+ "</head>\r\n" + "<body>\r\n" + "    <div class=\"logo\" style=\"text-align: left;\">\r\n"
				+ "        <img style=\"width: 20%;\" src=\"" + x.Logo + "logo.jpg\">\r\n" + "    </div>\r\n"
				+ "    <div class=\"container\">\r\n"
				+ "        <h3 style=\"text-align: center;\"><b>FIXED DEPOSIT ADVICE</b></h3>\r\n"
				+ "        <h3 style=\"text-align: center;\"><b>NON TRANSFERABLE</b></h3>\r\n"
				+ "         <div style=\"padding: 0 20px;\">\r\n" + "	    <div style=\"display: flex;\">\r\n"
				+ "	    <div>\r\n" + "	        <p>" + accountDetailsJson.getString("Name") + "\r\n"
				+ "	        </p>\r\n" + "	        <p>" + address1 + "</p>\r\n" + "	        <p>" + address2
				+ "</p>\r\n" + "	        <p>" + pinCode + ",</p>\r\n" + "	        <p>" + state + " , " + country
				+ "</p>\r\n" + "	        <p>Account Relationship Single</p>\r\n" + "	    </div>\r\n"
				+ "	    <div style=\"margin:60px\">\r\n" + "	\r\n" + "	    </div>\r\n" + "	    <div>\r\n"
				+ "	\r\n" + "	        <p>Customer Id  &nbsp;&nbsp;:  &nbsp; " + customerid + "</p>\r\n"
				+ "	        <p>Account Number  &nbsp;&nbsp;:  &nbsp;" + accountNo + "</p>\r\n"
				+ "	        <p>Deposit Type  &nbsp;&nbsp;:  &nbsp;" + description.substring(0, 2).toUpperCase() + " "
				+ description.substring(3).toLowerCase() + "</p>\r\n"
				+ "	        <p>Account Status  &nbsp;&nbsp;: &nbsp;Active</p>\r\n"
				+ "	        <p>Join Account Holder  &nbsp;&nbsp;:  &nbsp;Active</p>\r\n" + "	    </div>\r\n"
				+ "	    \r\n" + "	    </div>" + "     <br>"
				+ "        <div class=\"box\" style=\"width:700px; height:280px; border: 1px solid black;\">\r\n"
				+ "        <div style=\"display: flex;\">\r\n" + "	    <div>\r\n"
				+ "	        <p> Principal Amount &nbsp;&nbsp;:&nbsp; " + fdopening.getDepositAmount() + " </p>\r\n"
				+ "	        <p>Maturity Amount &nbsp;&nbsp;:&nbsp; " + fdopening.getMaturityAmout() + " </p>\r\n"
				+ "	        <p>Maturity Type &nbsp;&nbsp;:&nbsp; " + maturityType + " </p>\r\n"
				+ "	        <p>Principal/Maturity Credit &nbsp;&nbsp;:&nbsp; " + fdopening.getFromAccount()
				+ " </p>\r\n" + "	        <p style=\"margin-bottom: 0;\">Maturity Type&nbsp;&nbsp;:&nbsp; "
				+ maturityTypeInterest + " </p>\r\n" + "         <p>(Interest)</p>\r\n"
				+ "	        <p>Interest Credit Account &nbsp;&nbsp;:&nbsp; " + fdopening.getDepositAccountNo()
				+ " </p>\r\n" + "	        <p> Nominee Registered &nbsp;&nbsp;:&nbsp; " + NomineeRegistered
				+ " </p>\r\n" + "	    </div>\r\n" + "	    <div style=\"margin:40px\">\r\n" + "	\r\n"
				+ "	    </div>\r\n" + "	    <div>\r\n" + "	        <p> Term&nbsp;&nbsp;:&nbsp; " + term + "</p>\r\n"
				+ "	        <p>Date Opened&nbsp;&nbsp;: &nbsp;" + accountDetailsJson.getString("CreationDate")
				+ "</p>\r\n" + "	        <p>Maturity Date&nbsp;&nbsp;:&nbsp; " + maturitydate.toLocalDate()
				+ " </p>\r\n" + "	        <p>Rate Of Interest&nbsp;&nbsp;:&nbsp; " + fdopening.getInterestEarned()
				+ "</p>\r\n" + "	        <p>Currency&nbsp;&nbsp;: &nbsp;INR</p>\r\n"
				+ "         <p style=\"margin-bottom: 0;\"></p>\r\n" + "         <p></p>\r\n"
				+ "	        <p>PAN No&nbsp;&nbsp;: &nbsp;" + panNo + " </p>\r\n"
				+ "	        <p>Name&nbsp;&nbsp;: &nbsp;" + NomineeName + " </p>\r\n" + "	    </div>\r\n"
				+ "	    \r\n" + "	    </div>\r\n" + "	\r\n" + "	    </div>" + "        </div>\r\n" + "    <br>"
				+ "        <p>1. Premature withdrawal is not permitted in Tax Saver FD. The deposit cannot be auto renewed. On &nbsp;&nbsp;&nbsp;&nbsp;maturity, the deposit will be credited to your Savings Account.</p>\r\n"
				+ "        <p>2. No loans or liens against Tax Saver will be permitted.</p>\r\n" + "        <br>\r\n"
				+ "        <p>Thank you for banking with us.</p>\r\n" + "   <br>"
				+ "        <p>This is system generated certificate, hence does not require any signature.</p>\r\n"
				+ "    </div>\r\n" + "</body>\r\n" + "</html>"));
		return htmlString.toString();
	}

	@Override
	public FdOpening fetchByMobNoAndSessionId(String mobileNo, String x_Session_ID) {
		Optional<FdOpening> fetchBymobNo = fdopeningrepo.fetchBymobNo(mobileNo, x_Session_ID);
		if (fetchBymobNo.isPresent()) {
			return fetchBymobNo.get();
		} else {
			throw new NoSuchElementException("No record found");
		}
	}

	@Override
	public JSONObject createLead(JSONObject jsonObject, JSONObject header) {
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
			logger.debug(x.BASEURL+"LeadService/leadManagement/saveLeadCreation");

			obj = new URL(x.BASEURL+"LeadService/leadManagement/saveLeadCreation");

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("api-key", x.api_key);

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
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

}
