package com.suryoday.FdOpening.Controller;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.AadharOtpService;
import com.suryoday.FdOpening.Service.AadharOtpValidateService;
import com.suryoday.FdOpening.Service.ErrorResponseService;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
//@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000","https://corporate.suryodaybank.com/suryoday-ntb-fd/","https://corporate.suryodaybank.com/suryoday-ntb-fd"})
@CrossOrigin(origins = "*")
public class AadharOtpController {
	@Autowired
	AadharOtpService otpservice;
	@Autowired
	AadharOtpValidateService validateOtpService;
	@Autowired
	FdOpeningService fdservice;
	@Autowired
	ErrorResponseService errorResponseService;
	@Autowired
	SendOtpService sendotpservice;
	private static Logger logger = LoggerFactory.getLogger(AadharOtpController.class);
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@RequestMapping(value = "/ekycSendOtp", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> ekycOtp(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID) throws Exception {
		JSONObject jsonObject = new JSONObject(bm);
		String aadharNo = jsonObject.getJSONObject("Data").getString("AadharNo");
		int max = 899999;
		int min = 800000;
//		int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
		int random_int = SECURE_RANDOM.nextInt((max - min) + 1) + min;
		String stan = Integer.toString(random_int);

		String xmlRequest = otpservice.getXmlRequest(aadharNo, stan);
		logger.debug("Xml Req" + xmlRequest);
		String sendEkyc = otpservice.sendEkyc(xmlRequest);

		JSONObject jsonConverted1 = XML.toJSONObject(sendEkyc);
		logger.debug(jsonConverted1.toString());
		String response = jsonConverted1.toString();
		JSONObject jsonAPIResponse = new JSONObject(response);
		logger.debug(jsonAPIResponse.toString());
		String responseCode = jsonAPIResponse.getJSONObject("Response").getJSONObject("ResponseData")
				.getJSONObject("OtpResponse").getJSONObject("OtpRes").getString("ret");
		org.json.simple.JSONObject pdresponse = new org.json.simple.JSONObject();
		if (responseCode.equalsIgnoreCase("y")) {
			String info = jsonAPIResponse.getJSONObject("Response").getJSONObject("ResponseData")
					.getJSONObject("OtpResponse").getJSONObject("OtpRes").getString("info");
			int indexOf = info.indexOf("*");
			String substring = info.substring(indexOf + 1, indexOf + 11);
			logger.debug(substring);
			pdresponse.put("message", "otp send successfully");
			pdresponse.put("mobile", substring);
			pdresponse.put("UKC", stan);

			return new ResponseEntity<Object>(pdresponse, HttpStatus.OK);
		} else {
			pdresponse.put("message", "please enter valid aadharnumber");
			return new ResponseEntity<Object>(pdresponse, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/ekycValidateOtp", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateOtp(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID) throws Exception {
		JSONObject request = new JSONObject(bm);
		String otp = request.getJSONObject("Data").getString("Otp");
		String aadharNo = request.getJSONObject("Data").getString("AadharNo");
		String UKC = request.getJSONObject("Data").getString("UKC");
		String applicationno = request.getJSONObject("Data").getString("ApplicationNo");
		String xmlRequest = validateOtpService.getXmlRequest(otp, aadharNo, UKC);
		logger.debug("Xml Req" + xmlRequest);
//		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationno));
//		fdOpening.setEkycRequest(xmlRequest);
		String sendEkyc = validateOtpService.sendEkyc(xmlRequest);
//		String sendEkyc="";
		logger.debug("XMLResp" + sendEkyc);
		org.json.JSONObject jsonConverted1 = XML.toJSONObject(sendEkyc);
		String response = jsonConverted1.toString();
		JSONObject jsonAPIResponse = new JSONObject(response);
//		System.out.println("jsonAPIResponse" + jsonAPIResponse);
		if (jsonAPIResponse.has("Response")) {
			org.json.JSONObject jsonObject = jsonAPIResponse.getJSONObject("Response");
			String HsmData = jsonAPIResponse.getJSONObject("Response").getString("HsmData");
			if (!HsmData.equals("")) {
				String decryptString = validateOtpService.decryptString(HsmData);
//				System.out.println(decryptString);
				org.jsoup.nodes.Document doc = Jsoup.parse(decryptString, "", Parser.xmlParser());

				Elements select = doc.select("KycRes");
//				System.out.println(select.toString());
				String string = select.toString();
				org.json.JSONObject jsonConverted2 = XML.toJSONObject(string);
				String jsonStringResponse = jsonConverted2.toString();
				org.json.JSONObject jsonResponse = new org.json.JSONObject(jsonStringResponse);
//				System.out.println("JSONStringResponse" + jsonStringResponse);
				org.json.JSONObject mainResponse = new org.json.JSONObject();
				String responseCode = jsonAPIResponse.getJSONObject("Response").getString("ResponseCode");
				if (responseCode.equals("99")) {
					String responseMesage = jsonAPIResponse.getJSONObject("Response").getString("ResponseMessage");
					mainResponse.put("ResponseCode", responseCode);
					mainResponse.put("ResponseMessage", responseMesage);
					return new ResponseEntity<Object>(mainResponse.toString(), HttpStatus.OK);
				}
				if (!jsonResponse.equals(null)) {
					HttpStatus h = HttpStatus.BAD_GATEWAY;
					if (jsonResponse.has("KycRes")) {
						jsonObject.put("HsmData", jsonResponse);

						org.json.JSONObject responseObj = new org.json.JSONObject();
						org.json.JSONObject kycRes = new org.json.JSONObject();

						org.json.JSONObject uidData = jsonResponse.getJSONObject("KycRes").getJSONObject("UidData");
						JSONObject PoaResponse = uidData.getJSONObject("Poa");
						if (!PoaResponse.has("po")) {
							PoaResponse.put("po", "");
						}
						if (!PoaResponse.has("house")) {
							PoaResponse.put("house", "");
						}
						if (!PoaResponse.has("street")) {
							PoaResponse.put("street", "");
						}
						if (!PoaResponse.has("dist")) {
							PoaResponse.put("dist", "");
						}
						if (!PoaResponse.has("vtc")) {
							PoaResponse.put("vtc", "");
						}
						if (!PoaResponse.has("lm")) {
							PoaResponse.put("lm", "");
						}
						if (!PoaResponse.has("loc")) {
							PoaResponse.put("loc", "");
						}
						if (!PoaResponse.has("subdist")) {
							PoaResponse.put("subdist", "");
						}
						if (!PoaResponse.has("pc")) {
							PoaResponse.put("pc", "");
						}
						if (!PoaResponse.has("state")) {
							PoaResponse.put("state", "");
						}
						if (!PoaResponse.has("country")) {
							PoaResponse.put("country", "");
						}
						if (!PoaResponse.has("co")) {
							PoaResponse.put("co", "");
						}
						if (!PoaResponse.has("lang")) {
							PoaResponse.put("lang", "");
						}
						String txn = jsonResponse.getJSONObject("KycRes").getString("txn");
						String ttl = jsonResponse.getJSONObject("KycRes").getString("ttl");
						String ts = jsonResponse.getJSONObject("KycRes").getString("ts");
						org.json.JSONObject responseData = jsonObject.getJSONObject("ResponseData");
						String responseCode2 = jsonObject.getString("ResponseCode");
						String responseMessage = jsonObject.getString("ResponseMessage");
						String response2 = jsonObject.getString("Response2");
						String response1 = jsonObject.getString("Response1");

						kycRes.put("txn", txn);
						kycRes.put("ttl", ttl);
						kycRes.put("ts", ts);
						responseObj.put("KycRes", kycRes);
						responseObj.put("ResponseData", responseData);
						responseObj.put("ResponseCode", responseCode2);
						responseObj.put("ResponseMessage", responseMessage);
						responseObj.put("Response2", response2);
						responseObj.put("Response1", response1);

						String ekycPhoto = uidData.getString("Pht");
						String ekycAadhar = uidData.getJSONObject("Prn").getString("content");
						Map<String, String> map = new HashMap();
						map.put("ekycPhoto", ekycPhoto);
						map.put("ekycAadhar", ekycAadhar);

						org.json.JSONObject ekyc_photo = new org.json.JSONObject();
						ekyc_photo.put("image", "photo.jpg");
						ekyc_photo.put("Lat", "0.0");
						ekyc_photo.put("Long", "0.0");
						ekyc_photo.put("Address", "");
						LocalDateTime localDateTime = LocalDateTime.now();
						ekyc_photo.put("timestamp", localDateTime);

						org.json.JSONObject ekyc_aadhar = new org.json.JSONObject();
						ekyc_aadhar.put("image", "photo.pdf");
						ekyc_aadhar.put("Lat", "0.0");
						ekyc_aadhar.put("Long", "0.0");
						ekyc_aadhar.put("Address", "");
						ekyc_aadhar.put("timestamp", localDateTime);

						org.json.JSONObject ekyc_photo1 = new org.json.JSONObject();
						ekyc_photo1.put("ekyc_photo", ekyc_photo);
						org.json.JSONObject ekyc_aadhar1 = new org.json.JSONObject();
						ekyc_aadhar1.put("ekyc_aadhar", ekyc_aadhar);

						org.json.JSONArray jsonArray = new org.json.JSONArray();
						jsonArray.put(ekyc_aadhar1);
						jsonArray.put(ekyc_photo1);

//					uidData.remove("Pht");
//				uidData.remove("Prn");
						kycRes.put("UidData", uidData);
						JSONObject poi = uidData.getJSONObject("Poi");
						mainResponse.put("Response", responseObj);
						org.json.JSONObject DBResponse = new JSONObject(mainResponse.toMap());
						h = HttpStatus.OK;
						DBResponse.getJSONObject("Response").getJSONObject("KycRes").getJSONObject("UidData")
								.remove("Pht");
						if (applicationno.length() > 0) {
							FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationno));
							fdOpening.setEkycRequest(xmlRequest);
							fdOpening.setEkycResponse(mainResponse.toString());
							fdOpening.setIsEkyc("Y");
							sendotpservice.save(fdOpening);
						}
					} else if (jsonResponse.has("Description")) {
						h = HttpStatus.BAD_REQUEST;
					}
					logger.debug("Main Response : " + mainResponse.toString());

					return new ResponseEntity<>(mainResponse.toString(), h.OK);
				} else {

					return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
				}
			} else {
				logger.debug("If HmsData Empty Response : " + jsonAPIResponse.toString());
				String kycRes = jsonAPIResponse.getJSONObject("Response").getJSONObject("ResponseData")
						.getJSONObject("KycResponse").getJSONObject("Resp").getString("kycRes");
				String applicationNo = "123";
				String errorResponse = errorResponseService.getError(kycRes, Long.parseLong(applicationNo));
				if (applicationno.length() > 0) {
					FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationno));
					fdOpening.setEkycResponse(errorResponse);
					fdOpening.setIsEkyc("N");
					sendotpservice.save(fdOpening);
				}
				return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
			}
		} else if (jsonAPIResponse.has("Description")) {

			return new ResponseEntity<>(jsonAPIResponse.toString(), HttpStatus.BAD_REQUEST);
		} else {

			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/aadharReference", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createAadharReference(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "tenant", required = true) String tenant,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("tenant", tenant);
		Header.put("X-Request-ID", X_Request_ID);
		logger.debug("POST Request", bm);

		JSONObject jsonObject = new JSONObject(bm);
		// String AadharNumber =
		// jsonObject.getJSONObject("Data").getString("AadharNumber");

		JSONObject createAadharReference = otpservice.createAadharReference(jsonObject, Header);
		logger.debug("response from CreateAadharReference", createAadharReference);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createAadharReference != null) {
			String Data2 = createAadharReference.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);
			logger.debug("JSON Object ", Data2);

			if (Data1.has("Data")) {

				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("Main Response", Data1.toString());
			return new ResponseEntity<Object>(Data1.toString(), h);
		} else {
			logger.debug("GATEWAY_TIMEOUT");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/saveEkycDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveEkycDetails(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveEkycDetails start");
		logger.debug("saveEkycDetails request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
		JSONObject ekycDetails = jsonObject.getJSONObject("Data").getJSONObject("EkycDetails");
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
		logger.debug("setEkycDetails " + ekycDetails.toString());
		fdOpening.setEkycDetails(ekycDetails.toString());
		fdOpening.setName(ekycDetails.getString("name"));
		fdOpening.setFlowStaus("ED");
		sendotpservice.save(fdOpening);
		logger.debug( " fdOpening :: "+fdOpening.toString());
		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
		org.json.simple.JSONObject data = new org.json.simple.JSONObject();
		data.put("Success", "Data Saved Successfully");
		response.put("Data", data);
		logger.debug("saveEkycDetails  Response :: "+ response.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}
}
