package com.suryoday.FdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.NtbFdDmsService;
import com.suryoday.FdOpening.Service.SendOtpService;

@Component
public class NtbFdDmsServImpl implements NtbFdDmsService {

	@Autowired
	SendOtpService sendotpservice;

	private static Logger logger = LoggerFactory.getLogger(NtbFdDmsServImpl.class);

	public void FdDmsUpload(FdOpeningNTB fd) {
		JSONObject sendResponse = new JSONObject();

		JSONObject request = setRequest(fd);

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
			String url = x.BASEURL + "banking/2/dms/upload?api_key=" + x.api_key;
			logger.debug("URL " + url);

			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Postman-Token", "f5685eda-b5f0-41f8-9688-7899c5b75e29");
			con.setRequestProperty("X-Request-ID", "WNT");
//			con.setRequestProperty("cache-control", "no-cache");
			fd.setDmsUploadReq(request.toString());
			sendResponse = getResponseData(request, sendResponse, con, "PUT");
			if (sendResponse != null) {
				String Data2 = sendResponse.getString("data");
				logger.debug("FD DMS UPLOAD RESPONSE: " + Data2);
				JSONObject Data1 = new JSONObject(Data2);
				if (Data1.toString().contains(":{\"Status\":\"DMSUpload")) {
					fd.setDmsUploadResp(Data1.toString());
					fd.setIsDmsUpload("Y");
				}
			}
			sendotpservice.save(fd);

		} catch (Exception e) {
			logger.debug(e.toString());
			e.getMessage();
			e.printStackTrace();
		}
	}

	private static JSONObject setRequest(FdOpeningNTB ntbFd) {
		// Common Metadata values (from Java variables)
		String aadharReferenceNumber = "";
		String cifNumber = "";
		String photo = "";
		String aadhaarPdf = "";
		boolean root = false;

		// Get CIF Number from response
		String cifResponse = ntbFd.getCifResponse();
		if (cifResponse != null && !cifResponse.isEmpty()) {
			JSONObject jsonObject = new JSONObject(cifResponse);
			cifNumber = jsonObject.getJSONObject("Data").getString("UCIC");
		}

		// Get Aadhar Reference Number from request
		String cifRequest = ntbFd.getCifRequest();
		if (cifRequest != null && !cifRequest.isEmpty()) {
			JSONObject jsonObject = new JSONObject(cifRequest);
			JSONObject dataObj = jsonObject.optJSONObject("Data");
			JSONObject individual = (dataObj != null) ? dataObj.optJSONObject("Individual") : null;
			JSONArray documents = (individual != null) ? individual.optJSONArray("Document") : null;

			if (documents != null) {
				for (int i = 0; i < documents.length(); i++) {
					JSONObject doc = documents.getJSONObject(i);
					if ("AADHAR".equals(doc.optString("Type"))) {
						aadharReferenceNumber = doc.optString("IdentityNumber");
						break;
					}
				}
			} else {
				logger.debug("Individual/Document not found in cifRequest, skipping aadhar reference extraction");
			}
		}

		// Get VKYC Response details
		String ekycResponse = ntbFd.getEkycResponse();
		if (ekycResponse != null && !ekycResponse.isEmpty()) {
			JSONObject obj = new JSONObject(ekycResponse);
			if (obj.has("Response")) {
//				if (ntbFd.getIsVkycDone().equalsIgnoreCase("Y")) {
				JSONObject kycRes = obj.getJSONObject("Response").getJSONObject("KycRes");
				JSONObject uidData = kycRes.getJSONObject("UidData");

				photo = uidData.getString("Pht");
				aadhaarPdf = uidData.getJSONObject("Prn").getString("content");
			} else {
				photo = obj.getJSONObject("aadhaarData").getJSONObject("OfflinePaperlessKyc").getJSONObject("UidData").getString("Pht");
				aadhaarPdf = ntbFd.getEkycRequest().toString();
			}
		}

		// File details for both documents
		String[] docNames = { cifNumber + "_PHOTO.jpg", cifNumber + "_AADHAR.pdf" };
		String[] docTypes = { "image/jpeg", "application/pdf" };
		String[] extensions = { "jpg", "pdf" };
		String[] fileContents = { photo, aadhaarPdf };
		String[] documentTypes = { "PHOTO", "AADHAR" };
		// Create Data JSONArray
		JSONArray dataArray = new JSONArray();

		for (int i = 0; i < docNames.length; i++) {
			// Create Metadata JSONObject (specific for each document)
			JSONObject properties = new JSONObject();
			properties.put("PRO233", new JSONObject().put("DataName", "Application_Number").put("Value",ntbFd.getApplicationNo()));
			properties.put("PRO223",new JSONObject().put("DataName", "Account_Number").put("Value", ntbFd.getFdAccNo()));
			properties.put("PRO222",new JSONObject().put("DataName", "Document_Number").put("Value", aadharReferenceNumber));
			properties.put("PRO221", new JSONObject().put("DataName", "Document_Type").put("Value", documentTypes[i]));
			properties.put("PRO220", new JSONObject().put("DataName", "CIF_Number").put("Value", cifNumber));

			JSONObject metadata = new JSONObject();
			metadata.put("meta-Id", "310");
			metadata.put("meta-Name", "MD_Customer");
			metadata.put("Properties", properties);

			JSONArray metadataArray = new JSONArray();
			metadataArray.put(metadata);

			// Create Document JSONObject
			JSONObject document = new JSONObject();
			document.put("DocName", docNames[i]);
			document.put("DocType", docTypes[i]);
			document.put("FileContent", fileContents[i]);
			document.put("Metadata", metadataArray);
			document.put("Extension", extensions[i]);
			document.put("Root", root);

			dataArray.put(document);
		}

		// Create final JSONObject
		JSONObject finalJson = new JSONObject();
		finalJson.put("Data", dataArray);
		logger.debug("DMS upload " + ntbFd.getApplicationNo() + " :: " + finalJson.toString());
		return finalJson;
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
