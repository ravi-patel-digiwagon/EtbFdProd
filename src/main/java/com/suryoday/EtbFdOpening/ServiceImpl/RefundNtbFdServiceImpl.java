package com.suryoday.EtbFdOpening.ServiceImpl;

import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Repository.NtbFdRepo;
import com.suryoday.EtbFdOpening.Service.RefundNtbFdService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class RefundNtbFdServiceImpl implements RefundNtbFdService {

	private static Logger logger = LoggerFactory.getLogger(RefundNtbFdServiceImpl.class);

	@Autowired
    NtbFdRepo ntbFdRepo;

//	@Autowired
//	FdRefundRepository fdRefundRepo;
//
//	@Override
//	public RefundNtbFd save(RefundNtbFd refundFd) {
//		return fdRefundRepo.save(refundFd);
//	}
//
//	@Override
//	public List<RefundNtbFd> getAllRefundNtbFdList(String isRefundDone) {
//		return fdRefundRepo.refundNtbFdList(isRefundDone);
//	}
//	
	
	
	@Override
	public JSONObject paymentTransactionPushSer(JSONObject payload) {
		JSONObject sendResponse = new JSONObject();
		logger.debug("paymentTransactionPushSer payload {}", payload.toString());
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			x.bypassssl();
//			HostnameVerifier allHostsValid = new HostnameVerifier() {
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}
//			};
//
//			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			String url ="";
//			String url = x.BASEURL + "payment/transaction/push";
			obj = new URL(url);
			logger.debug("paymentTransactionPushSer " + url);

			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("api_key", x.api_key);
			con.setRequestProperty("X-Request-ID", "WNT");
//			con.setRequestProperty("X-Correlation-ID", formattedDateTime);

			logger.debug("paymentTransactionPushSer Headers set. Sending request... {}", con.getRequestProperties());

			sendResponse = getResponseData(payload, sendResponse, con, "POST");

			logger.debug("API Response: " + sendResponse.toString());

		} catch (Exception e) {
			logger.error("Error occurred in Payment Transaction Push: " + e.getMessage(), e);
			e.printStackTrace();
		}

		return sendResponse;

	}

	@Override
	public List<FdOpeningNTB> findRefundNtbFdList(String isActive, String isRefundDone, String isPaymentDone, String isRefundProcessedApproved) {
		return ntbFdRepo.findRefundNtbFdList(isActive, isRefundDone, isPaymentDone, isRefundProcessedApproved);
	}

	@Override
	public JSONObject paymentRefundTransactionRequest(JSONObject payload) {
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
			String url ="";
//			String url = x.BASEURL + "refund/transaction";

			obj = new URL(url);
			logger.debug("API URL : " + url);
			logger.debug("HTTP Headers set for Payment Refund Transaction");
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("X-Request-ID", "WNT");
			con.setRequestProperty("api_key", x.api_key);
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
			logger.debug("API PayLoad : " + payload.toString());
			logger.debug("paymentRefundTransactionRequest Headers set. Sending request... {}",
					con.getRequestProperties());

			sendResponse = getResponseData(payload, sendResponse, con, "POST");

			logger.debug("API Response: " + sendResponse.toString());

		} catch (Exception e) {
			logger.error("Error occurred in Payment  Payment Refund Transaction: " + e.getMessage(), e);
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
