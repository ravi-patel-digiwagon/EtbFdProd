package com.suryoday.FdOpening.Controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryoday.FdOpening.Others.Crypt;
import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.AadharOtpService;
import com.suryoday.FdOpening.Service.AadharOtpValidateService;
import com.suryoday.FdOpening.Service.ErrorResponseService;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.SendOtpService;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
//@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000", "https://corporate.suryodaybank.com/suryoday-ntb-fd" ,"https://corporate.suryodaybank.com/suryoday-ntb-fd/" })
@CrossOrigin(origins = "*")
public class AadharOtpControllerEncy {
	@Autowired
	AadharOtpService otpservice;
	@Autowired
	SendOtpService sendotpservice;
	@Autowired
	FdOpeningService fdservice;
	@Autowired
	AadharOtpValidateService validateOtpService;
	@Autowired
	ErrorResponseService errorResponseService;
	private static Logger logger = LoggerFactory.getLogger(AadharOtpControllerEncy.class);
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@RequestMapping(value = "/ekycSendOtpEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> ekycOtpEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID, HttpServletRequest req)
			throws Exception {
		boolean sessionId = sendotpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);

			String aadharNo = jsonObject.getJSONObject("Data").getString("AadharNo");
//			int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
			int max = 899999;
			int min = 800000;

			int random_int = SECURE_RANDOM.nextInt((max - min) + 1) + min;
			String stan = Integer.toString(random_int);

			String xmlRequest = otpservice.getXmlRequest(aadharNo, stan);
			logger.debug("Xml Req" + xmlRequest);
			String sendEkyc = otpservice.sendEkyc(xmlRequest);

			org.json.JSONObject jsonConverted1 = XML.toJSONObject(sendEkyc);
			String response = jsonConverted1.toString();
			JSONObject jsonAPIResponse = new JSONObject(response);
			logger.debug("jsonAPIResponse :: " + jsonAPIResponse);
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
				data = pdresponse.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
			} else {
				pdresponse.put("message", "please enter valid aadharnumber");
				data = pdresponse.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/ekycValidateOtpEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateOtpEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID, HttpServletRequest req)
			throws Exception {
		boolean sessionId = sendotpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject request = new JSONObject(decryptContainerString);
			String otp = request.getJSONObject("Data").getString("Otp");
			String aadharNo = request.getJSONObject("Data").getString("AadharNo");
			String applicationno = request.getJSONObject("Data").getString("ApplicationNo");
			String UKC = request.getJSONObject("Data").getString("UKC");
			String xmlRequest = validateOtpService.getXmlRequest(otp, aadharNo, UKC);
			logger.debug("Xml Req :: " + xmlRequest);
			FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationno));
			fdOpening.setEkycRequest(xmlRequest);
			String sendEkyc = validateOtpService.sendEkyc(xmlRequest);
//		String sendEkyc="";
			org.json.JSONObject jsonConverted1 = XML.toJSONObject(sendEkyc);
			String response = jsonConverted1.toString();
			JSONObject jsonAPIResponse = new JSONObject(response);
			logger.debug("jsonAPIResponse :: " + jsonAPIResponse);
			if (jsonAPIResponse.has("Response")) {
				org.json.JSONObject jsonObject = jsonAPIResponse.getJSONObject("Response");
				String HsmData = jsonAPIResponse.getJSONObject("Response").getString("HsmData");
				if (!HsmData.equals("")) {
					String decryptString = validateOtpService.decryptString(HsmData);
					logger.debug(" DEC STRING " + decryptString);
					org.jsoup.nodes.Document doc = Jsoup.parse(decryptString, "", Parser.xmlParser());

					Elements select = doc.select("KycRes");
					logger.debug(" KycRes :: " + select.toString());
					String string = select.toString();
					org.json.JSONObject jsonConverted2 = XML.toJSONObject(string);
					String jsonStringResponse = jsonConverted2.toString();
					org.json.JSONObject jsonResponse = new org.json.JSONObject(jsonStringResponse);
					logger.debug("JSONStringResponse :: " + jsonStringResponse);
					org.json.JSONObject mainResponse = new org.json.JSONObject();
					String responseCode = jsonAPIResponse.getJSONObject("Response").getString("ResponseCode");
					if (responseCode.equals("99")) {
						String responseMesage = jsonAPIResponse.getJSONObject("Response").getString("ResponseMessage");
						mainResponse.put("ResponseCode", responseCode);
						mainResponse.put("ResponseMessage", responseMesage);
						data = mainResponse.toString();
						String encryptString2 = Crypt.encrypt(data, X_encode_ID);
						org.json.JSONObject data2 = new org.json.JSONObject();
						data2.put("value", encryptString2);
						org.json.JSONObject data3 = new org.json.JSONObject();
						data3.put("Data", data2);
						logger.debug("response : " + data3.toString());
						return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
					}
					if (!jsonResponse.equals(null)) {
						HttpStatus h = HttpStatus.BAD_GATEWAY;
						if (jsonResponse.has("KycRes")) {
							jsonObject.put("HsmData", jsonResponse);

							org.json.JSONObject responseObj = new org.json.JSONObject();
							org.json.JSONObject kycRes = new org.json.JSONObject();

							org.json.JSONObject uidData = jsonResponse.getJSONObject("KycRes").getJSONObject("UidData");
							logger.debug("uidData :: " + uidData.toString());
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
//							uidData.remove("Prn");
							kycRes.put("UidData", uidData);
							JSONObject poi = uidData.getJSONObject("Poi");
							mainResponse.put("Response", responseObj);
							org.json.JSONObject DBResponse = new JSONObject(mainResponse.toMap());

							DBResponse.getJSONObject("Response").getJSONObject("KycRes").getJSONObject("UidData")
									.remove("Pht");
							fdOpening.setEkycResponse(mainResponse.toString());
							fdOpening.setIsEkyc("Y");
							sendotpservice.save(fdOpening);
							h = HttpStatus.OK;

						} else if (jsonResponse.has("Description")) {
							h = HttpStatus.BAD_REQUEST;
						}
						logger.debug("Main Response :: " + mainResponse.toString());
						data = mainResponse.toString();
						String encryptString2 = Crypt.encrypt(data, X_encode_ID);
						org.json.JSONObject data2 = new org.json.JSONObject();
						data2.put("value", encryptString2);
						org.json.JSONObject data3 = new org.json.JSONObject();
						data3.put("Data", data2);
						logger.debug("response :: " + data3.toString());
						return new ResponseEntity<Object>(data3.toString(), h.OK);
					} else {

						return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
					}
				} else {
					logger.debug("If HmsData Empty Response : " + jsonAPIResponse.toString());
					String kycRes = jsonAPIResponse.getJSONObject("Response").getJSONObject("ResponseData")
							.getJSONObject("KycResponse").getJSONObject("Resp").getString("kycRes");
					String applicationNo = "123";
					String errorResponse = errorResponseService.getError(kycRes, Long.parseLong(applicationNo));
					fdOpening.setEkycResponse(errorResponse);
					fdOpening.setIsEkyc("N");
					sendotpservice.save(fdOpening);
					data = errorResponse.toString();
					String encryptString2 = Crypt.encrypt(data, X_encode_ID);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);
				}
			} else if (jsonAPIResponse.has("Description")) {

				data = jsonAPIResponse.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);
			} else {

				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/aadharReferenceEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createAadharReference(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "tenant", required = true) String tenant,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("tenant", tenant);
		Header.put("X-Request-ID", X_Request_ID);
		logger.debug("POST Request", bm);

		boolean sessionId = sendotpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			// String AadharNumber =
			// jsonObject.getJSONObject("Data").getString("AadharNumber");

			JSONObject createAadharReference = otpservice.createAadharReference(jsonObject, Header);
			logger.debug("response from CreateAadharReference", createAadharReference);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (createAadharReference != null) {
				String Data2 = createAadharReference.getString("data");

				JSONObject Data1 = new JSONObject(Data2);
				logger.debug("JSON Object ", Data2);

				if (Data1.has("Data")) {

					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				logger.debug("Main Response", Data1.toString());
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);
			} else {
				logger.debug("GATEWAY_TIMEOUT");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/saveEkycDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveEkycDetailsEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveEkycDetailsEncy start");
		logger.debug("saveEkycDetailsEncy request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = sendotpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("saveEkycDetailsEncy request" + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
			JSONObject ekycDetails = jsonObject.getJSONObject("Data").getJSONObject("EkycDetails");
			FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			logger.debug("setEkycDetails " + ekycDetails.toString());
			fdOpening.setEkycDetails(ekycDetails.toString());
			fdOpening.setName(ekycDetails.getString("name"));
			fdOpening.setFlowStaus("ED");
			sendotpservice.save(fdOpening);
			JSONObject response = new JSONObject();
			JSONObject Data = new JSONObject();
			Data.put("Success", "Data Saved Successfully");
			response.put("Data", Data);
			data = response.toString();
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			JSONObject data2 = new JSONObject();
			data2.put("value", encryptString2);
			JSONObject data3 = new JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
		} else {
			JSONObject data2 = new JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			JSONObject data3 = new JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/zipokyc")
	public ResponseEntity<?> convertZipXml(@RequestParam("file") MultipartFile file,
			@RequestParam("password") String password, @RequestParam("ApplicationNo") String ApplicationNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "aadhaarNumber", required = true) String aadhaarNumber) throws Exception {

		logger.debug("Received ZIP file upload");

		boolean sessionId = sendotpservice.validateSessionId(X_Session_ID, mobileNo);
		if (!sessionId) {
			logger.error("Invalid session ID for MobileNo: {}", mobileNo);
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Invalid or expired session.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
		if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".zip")) {
			logger.error("Invalid ZIP file uploaded.");
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Only password-protected ZIP files are supported.");
			return ResponseEntity.badRequest().body(response);
		}

		File tempZip = null;
		File tempXml = null;
		try {
			tempZip = File.createTempFile("upload-", ".zip");
			file.transferTo(tempZip);

			ZipFile zipFile = new ZipFile(tempZip);
			if (!zipFile.isEncrypted()) {
				logger.error("ZIP file is not encrypted.");
				Map<String, Object> response = new HashMap<>();
				response.put("error", "ZIP file must be password protected.");
				return ResponseEntity.badRequest().body(response);
			}

			zipFile.setPassword(password.toCharArray());

			List<FileHeader> headers = zipFile.getFileHeaders();
			if (headers.size() != 1 || !headers.get(0).getFileName().toLowerCase().endsWith(".xml")) {
				logger.error("ZIP does not contain a single XML file.");
				Map<String, Object> response = new HashMap<>();
				response.put("error", "ZIP must contain exactly one XML file.");
				return ResponseEntity.badRequest().body(response);
			}

			FileHeader xmlHeader = headers.get(0);

			// Last modified time of Aadhaar XML file

			// Correct way to get the real file time
			long lastModifiedMillis = xmlHeader.getLastModifiedTimeEpoch();
			Date lastModifiedDate = new Date(lastModifiedMillis);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = sdf.format(lastModifiedDate);

			logger.debug("Last modified time of Aadhaar XML file: {}", formattedDate);

			// Reject if older than 3 days
			long currentTimeMillis = System.currentTimeMillis();
			long threeDaysMillis = 3L * 24 * 60 * 60 * 1000;
			if ((currentTimeMillis - lastModifiedMillis) > threeDaysMillis) {
				logger.error("Aadhaar XML is older than 3 days. Generated at: {}", formattedDate);
				Map<String, Object> response = new HashMap<>();
				response.put("error", "Aadhaar XML file must not be older than 3 days.");
				return ResponseEntity.badRequest().body(response);
			}

			tempXml = File.createTempFile("aadhaar-", ".xml");
			zipFile.extractFile(xmlHeader, tempXml.getParent(), tempXml.getName());

			byte[] bytes = Files.readAllBytes(tempXml.toPath());
			String xmlContent = new String(bytes, StandardCharsets.UTF_8);
			int xmlStart = xmlContent.indexOf("<?xml");
			if (xmlStart > 0) {
				xmlContent = xmlContent.substring(xmlStart);
			}

			fdOpening.setEkycRequest(xmlContent);
			logger.debug("xmlContent :: " + xmlContent);

			ResponseEntity<?> responseEntity = convertXmlContentToJson(xmlContent, aadhaarNumber, mobileNo, password);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonBody = objectMapper.writeValueAsString(responseEntity.getBody());
			logger.debug("jsonBody :: " + jsonBody);
			fdOpening.setEkycResponse(jsonBody);
			fdOpening.setIsEkyc("Y");
			sendotpservice.save(fdOpening);

			return responseEntity;

		} catch (ZipException e) {
			logger.error("ZIP error: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Invalid ZIP or wrong password.");
			return ResponseEntity.badRequest().body(response);
		} catch (IOException e) {
			logger.error("IO Error: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Failed to process ZIP file.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		} finally {
			if (tempZip != null && tempZip.exists()) {
				tempZip.delete();
			}
			if (tempXml != null && tempXml.exists()) {
				tempXml.delete();
			}
		}
	}

	private boolean isValidXml(String xmlContent) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean verifySignature(String xmlContent) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

		Element sigElement = (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature").item(0);
		if (sigElement == null) {
			throw new Exception("Signature element not found.");
		}

		XMLSignature signature = new XMLSignature(sigElement, null);

		Element certElem = (Element) sigElement.getElementsByTagNameNS(Constants.SignatureSpecNS, "X509Certificate")
				.item(0);
		if (certElem == null) {
			throw new Exception("X509Certificate not found in Signature.");
		}

		String certBase64 = certElem.getTextContent().replaceAll("\\s+", "");
		byte[] certBytes = Base64.getDecoder().decode(certBase64);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));

		return signature.checkSignatureValue(cert.getPublicKey());
	}

	private ResponseEntity<?> convertXmlContentToJson(String xmlContent, String aadhaarNumber, String mobileNo,
			String password) {
		if (!isValidXml(xmlContent)) {
			logger.error("Invalid XML content.");
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Invalid XML content.");
			return ResponseEntity.badRequest().body(response);
		}

		try {
			boolean signatureValid = verifySignature(xmlContent);
			String referenceId = extractReferenceId(xmlContent);

			if (referenceId == null || referenceId.length() < 4) {
				logger.error("Invalid or missing referenceId.");
				Map<String, Object> response = new HashMap<>();
				response.put("error", "Invalid or missing referenceId.");
				return ResponseEntity.badRequest().body(response);
			}

			// Validate Aadhaar number ending
			String referencePrefix = referenceId.substring(0, 4);
			String aadhaarPrefix = aadhaarNumber.length() >= 4 ? aadhaarNumber.substring(aadhaarNumber.length() - 4)
					: "";
			if (!referencePrefix.equals(aadhaarPrefix)) {
				logger.warn("Mismatch: Aadhaar reference prefix '{}' does not match user input '{}'", aadhaarPrefix,
						referencePrefix);
				Map<String, Object> response = new HashMap<>();
				response.put("error", "Aadhaar number does not match with Offline KYC reference ID.");
				return ResponseEntity.badRequest().body(response);
			}

			// Parse XML to JSON
			JSONObject jsonObject = XML.toJSONObject(xmlContent);
			Map<String, Object> aadhaarData = jsonObject.toMap();
			Map<String, Object> uidData = (Map<String, Object>) aadhaarData.get("OfflinePaperlessKyc");
			Map<String, Object> poi = (Map<String, Object>) ((Map<String, Object>) uidData.get("UidData")).get("Poi");

			String mobileHashFromXml = (String) poi.get("m");

			// Aadhaar hash repeat count logic
			char lastDigitChar = aadhaarNumber.charAt(aadhaarNumber.length() - 1);
			int repeatCount = Character.isDigit(lastDigitChar) ? Character.getNumericValue(lastDigitChar) : 1;
			if (repeatCount == 0)
				repeatCount = 1;

			// Compute and compare hash
			boolean mobileVerified = false;
			try {
				String computedHash = computeMobileHash(mobileNo, password, repeatCount);
				mobileVerified = computedHash.equalsIgnoreCase(mobileHashFromXml);
			} catch (Exception e) {
				logger.error("Mobile hash verification failed: {}", e.getMessage());
			}

			// Build final response
			Map<String, Object> response = new HashMap<>();
			response.put("mobileVerified", mobileVerified);
			response.put("signatureValid", signatureValid);
			response.put("aadhaarNumber", aadhaarNumber);
			response.put("aadhaarData", aadhaarData);

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			logger.error("Error processing XML: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Failed to process Aadhaar XML.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	private String computeMobileHash(String mobile, String password, int iterations) throws NoSuchAlgorithmException {
		String input = mobile + password;
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		for (int i = 1; i < iterations; i++) {
			hash = digest.digest(bytesToHex(hash).getBytes(StandardCharsets.UTF_8));
		}
		return bytesToHex(hash);
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private String extractReferenceId(String xmlContent) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

			NodeList list = doc.getElementsByTagName("OfflinePaperlessKyc");
			if (list.getLength() > 0) {
				Element kycElement = (Element) list.item(0);
				return kycElement.getAttribute("referenceId");
			}
		} catch (Exception e) {
			logger.error("Error extracting referenceId: {}", e.getMessage());
		}
		return null;
	}

	@PostConstruct
	public void init() {
		Init.init(); // This must be called once before any XMLSecurity API usage
		System.out.println("Apache XML Security Initialized.");
	}

}
