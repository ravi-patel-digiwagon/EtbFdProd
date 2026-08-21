package com.suryoday.FdOpening.Controller;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.FdOpening.Pojo.CraftMerchantDetails;
import com.suryoday.FdOpening.Pojo.FdOpening;
import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Repository.CraftMerchantDetailsRepo;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.FdRecieptService;
import com.suryoday.FdOpening.Service.SendOtpService;
import com.suryoday.FdOpening.Service.UpiMapperService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class UpiMapperController {
	Logger logger = LoggerFactory.getLogger(UpiMapperController.class);

	@Autowired
	UpiMapperService upimapperservice;
	@Autowired
	FdOpeningService fdservice;
	@Autowired
	FdRecieptService fdetbservice;
	@Autowired
	SendOtpService otpservice;
	@Autowired
	CraftMerchantDetailsRepo craftMerchantDetailsRepo;

	@RequestMapping(value = "/upiMapper", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> upiMapper(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);

		JSONObject jsonObject = new JSONObject(bm);
		String MobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
		JSONObject request = new JSONObject();
		JSONObject Data = new JSONObject();
		Data.put("ClientReferenceId", "2023092109062712581821");
		Data.put("MerchantId", "MER0000000009631");
		Data.put("MerchantVpa", "suryodayav@suryoday");
//		Comment on 20260223 With reference with rushali Email  
//		Data.put("MerchantId", "MER0000000000002");
//		Data.put("MerchantVpa", "bhavanimedicalstore@suryoday");
		Data.put("UPINumber", MobileNo);
		request.put("Data", Data);
		JSONObject upiMapper = upimapperservice.upiMapper(request, Header);
//		JSONObject upiMapper=null;
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (upiMapper != null) {
//				String upaName ="dfsdfs";
			String Data2 = upiMapper.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			if (Data1.has("Data")) {
				h = HttpStatus.OK;
			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
			}
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {

			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/paymentVpa", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> paymentVpa(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		JSONObject jsonObject = new JSONObject(bm);
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
		fdOpening.setVerifyUpiReq(jsonObject.toString());
		JSONObject paymentVpa = upimapperservice.paymentVpa(jsonObject, Header);
//		JSONObject upiMapper=null;
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (paymentVpa != null) {
//				String upaName ="dfsdfs";
			String Data2 = paymentVpa.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			if (Data1.has("Data")) {
				h = HttpStatus.OK;
				fdOpening.setIsUpiVerify("Y");
				fdOpening.setUpiId(jsonObject.getJSONObject("Data").getString("CustomerVPA"));
				fdOpening.setAccountNo(Data1.getJSONObject("Data").getString("PayeeAccountNumber"));
				fdOpening.setIfsc(Data1.getJSONObject("Data").getString("IFSC"));
			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
				fdOpening.setIsUpiVerify("N");
			}
			fdOpening.setVerifyUpiResp(Data1.toString());
			otpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {

			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/paymentVpaETB", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> paymentVpaETB(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		JSONObject jsonObject = new JSONObject(bm);
		FdOpening fdOpening = fdetbservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
		fdOpening.setVerifyUpiReq(jsonObject.toString());
		JSONObject paymentVpa = upimapperservice.paymentVpa(jsonObject, Header);
//		JSONObject upiMapper=null;
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (paymentVpa != null) {
//				String upaName ="dfsdfs";
			String Data2 = paymentVpa.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			if (Data1.has("Data")) {
				h = HttpStatus.OK;
				fdOpening.setIsUpiVerify("Y");
				fdOpening.setUpiId(jsonObject.getJSONObject("Data").getString("CustomerVPA"));
			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
				fdOpening.setIsUpiVerify("N");
			}
			fdOpening.setVerifyUpiResp(Data1.toString());
			otpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {

			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@PostMapping(value = "/generateQR", produces = "application/json")
	public ResponseEntity<Object> saveQR(@RequestBody String bm) throws Exception {
		JSONObject json = new JSONObject(bm);
		logger.debug("generateQR requst :: " + bm.toString());
		JSONObject response = upimapperservice.generateQR(json);
		logger.debug("generateQR response :: " + response.toString());
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/faceMatch", produces = "application/json")
	public ResponseEntity<Object> faceMatch(@RequestBody String bm) throws Exception {
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject response = upimapperservice.faceMatch(jsonObject);
		CraftMerchantDetails craftMerchantDetails = craftMerchantDetailsRepo
				.findById(jsonObject.getJSONObject("Data").getString("MobileNo"))
				.orElseThrow(() -> new NoSuchElementException("No record found"));
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (response != null) {
//			String upaName ="dfsdfs";
			String Data2 = response.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			if (Data1.has("Data")) {
				craftMerchantDetails.setIsFaceMatches("Y");
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				craftMerchantDetails.setIsFaceMatches("N");
				h = HttpStatus.BAD_REQUEST;

			}
			craftMerchantDetails.setFaceMatchResp(Data1.toString());
			craftMerchantDetailsRepo.save(craftMerchantDetails);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {

			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

}
