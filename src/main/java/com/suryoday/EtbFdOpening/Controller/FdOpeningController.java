package com.suryoday.EtbFdOpening.Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryoday.EtbFdOpening.Pojo.ErrorResponse;
import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.FdRecieptService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class FdOpeningController extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(FdOpeningController.class);
	@Autowired
	FdOpeningService fdopeningservice;
	@Autowired
	FdRecieptService fdservice;
	@Autowired
	SendOtpService sendotpservice;

	@Autowired
	FdRecieptService fdRecieptService;

	@RequestMapping(value = "/createDeposit", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createDeposit(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createDeposit start");
		logger.debug("createDeposit request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		JSONObject createDeposit = fdopeningservice.createDeposit(jsonObject, Header);
		System.out.println(createDeposit);
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createDeposit != null) {
			String Data2 = createDeposit.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			FdOpening fdopening = fdservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
			fdopening.setFdRequest(jsonObject.toString());
			fdopening.setFdResponse(Data1.toString());
			fdopeningservice.save(fdopening);
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/createDepositNTB", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createDepositNTB(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "ApplicationNo", required = true) String ApplicationNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createDeposit start");
		logger.debug("createDeposit request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		JSONObject createDeposit = fdopeningservice.createDepositNtb(jsonObject, Header);
		FdOpeningNTB fetchByApplicationNo = fdopeningservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
		fetchByApplicationNo.setFdOpeningReq(jsonObject.toString());
		System.out.println(createDeposit);
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createDeposit != null) {
			String Data2 = createDeposit.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;
				fetchByApplicationNo.setIsFdCreated("Y");
				fetchByApplicationNo.setIsDmsUpload("N");
				String accountId = Data1.getJSONArray("Data").getJSONObject(0).getString("AccountId");
				fetchByApplicationNo.setFdAccNo(accountId);
			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
				fetchByApplicationNo.setIsFdCreated("N");
			}
			fetchByApplicationNo.setFdOpeningResp(Data1.toString());
			sendotpservice.save(fetchByApplicationNo);
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/saveFdData", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveFdData(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveFdData start");
		logger.debug("saveFdData request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);

		String saveFdData = fdopeningservice.saveFdData(mobileNo, X_Session_ID, jsonObject);

		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
		org.json.simple.JSONObject data = new org.json.simple.JSONObject();
		data.put("Success", saveFdData);
		response.put("Data", data);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/saveNtbFdData", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveNtbFdData(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveFdData start");
		logger.debug("saveFdData request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);

		String saveFdData = fdopeningservice.saveNtbFdData(jsonObject);

		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
		org.json.simple.JSONObject data = new org.json.simple.JSONObject();
		data.put("Success", saveFdData);
		response.put("Data", data);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/loadPage", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> loadPage(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type) throws Exception {
		logger.debug("saveFdData start");
		logger.debug("saveFdData request" + bm);
		JSONObject Header = new JSONObject();

		JSONObject jsonObject = new JSONObject(bm);
		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
		org.json.simple.JSONObject data = new org.json.simple.JSONObject();
		data.put("Success", "200");
		response.put("Data", data);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/CloseFd", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> CloseFd(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("CloseFd start");
		logger.debug("CloseFd request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		JSONObject json = new JSONObject(bm);
		String accountId = json.getJSONObject("Data").getString("AccountId");
		String toaccountId = json.getJSONObject("Data").getString("ToAccountId");
		JSONObject jsonObject = new JSONObject();
		JSONObject Data = new JSONObject();
		Data.put("TypeOfClosure", "C");
		Data.put("AccountId", accountId);
		Data.put("ToAccountId", toaccountId);
		Data.put("Comments", "Yes");
		Data.put("Amount", "");
		jsonObject.put("Data", Data);
		JSONObject CloseFd = fdopeningservice.CloseFd(jsonObject, Header);
		System.out.println(CloseFd);
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (CloseFd != null) {
			String Data2 = CloseFd.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/FdMaturityChange", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> FdMaturityChange(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("CloseFd start");
		logger.debug("CloseFd request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject FdMaturityChange = fdopeningservice.FdMaturityChange(jsonObject, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (FdMaturityChange != null) {
			String Data2 = FdMaturityChange.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/fetchByApplicationId", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchByApplicationId(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchByApplicationId start");
		logger.debug("fetchByApplicationId request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
		FdOpeningNTB fdOpeningNTB = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
		JSONObject data = new JSONObject(fdOpeningNTB);
		logger.debug("fetchByApplicationId response" + fdOpeningNTB.toString());

		JSONObject response = new JSONObject();
		response.put("Data", data);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

	}


	@RequestMapping(value = "/fetchByIdEtb", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchByIdEtb(@RequestBody String bm,
													   @RequestHeader(name = "Accept", required = true) String accept,
													   @RequestHeader(name = "Content-Type", required = true) String Content_Type,
													   @RequestHeader(name = "MobileNo", required = true) String mobileNo,
													   @RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
													   @RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchByIdEtb start");
		logger.debug("fetchByIdEtb request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
//		String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
//		FdOpeningNTB fdOpeningNTB = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));

		JSONObject response = new JSONObject();
		try {
			FdOpening fdopening = fdRecieptService.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
			JSONObject data = buildFdOpeningData(fdopening);
			logger.debug("fetchByIdEtb response" + fdopening.toString());
			response.put("Data", data);
		} catch (java.util.NoSuchElementException e) {
			logger.debug("fetchByIdEtb no record found for mobileNo: " + mobileNo + ", sessionId: " + X_Session_ID);
			response.put("Data", JSONObject.NULL);
		}
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

	}

	private JSONObject buildFdOpeningData(FdOpening fdopening) {
		JSONObject data = new JSONObject();
		data.put("id", fdopening.getId());
		data.put("mobileNo", nullSafe(fdopening.getMobileNo()));
		data.put("sessionId", nullSafe(fdopening.getSessionId()));
		data.put("depositAccountNo", nullSafe(fdopening.getDepositAccountNo()));
		data.put("depositAmount", nullSafe(fdopening.getDepositAmount()));
		data.put("tenure", nullSafe(fdopening.getTenure()));
		data.put("maturityAmout", nullSafe(fdopening.getMaturityAmout()));
		data.put("interestEarned", nullSafe(fdopening.getInterestEarned()));
		data.put("cifNo", nullSafe(fdopening.getCifNo()));
		data.put("roi", nullSafe(fdopening.getRoi()));
		data.put("fromAccount", nullSafe(fdopening.getFromAccount()));
		data.put("maturityDate", nullSafe(fdopening.getMaturityDate()));
		data.put("createdDate", nullSafe(fdopening.getCreatedDate()));
		data.put("updatedDate", nullSafe(fdopening.getUpdatedDate()));
		data.put("status", nullSafe(fdopening.getStatus()));
		data.put("FdRequest", nullSafe(fdopening.getFdRequest()));
		data.put("FdResponse", nullSafe(fdopening.getFdResponse()));
		data.put("custType", nullSafe(fdopening.getCustType()));
		data.put("productCode", nullSafe(fdopening.getProductCode()));
		data.put("upiId", nullSafe(fdopening.getUpiId()));
		data.put("isUpiVerify", nullSafe(fdopening.getIsUpiVerify()));
		data.put("verifyUpiReq", nullSafe(fdopening.getVerifyUpiReq()));
		data.put("verifyUpiResp", nullSafe(fdopening.getVerifyUpiResp()));
		data.put("nomineeDetails", nullSafe(fdopening.getNomineeDetails()));
		data.put("isPaymentDone", nullSafe(fdopening.getIsPaymentDone()));
		data.put("createOrderResp", nullSafe(fdopening.getCreateOrderResp()));
		data.put("paymentDetails", nullSafe(fdopening.getPaymentDetails()));
		data.put("isAccountVerify", nullSafe(fdopening.getIsAccountVerify()));
		data.put("accountNo", nullSafe(fdopening.getAccountNo()));
		data.put("ifsc", nullSafe(fdopening.getIfsc()));
		data.put("payuOrderId", nullSafe(fdopening.getPayuOrderId()));
		data.put("paymentDate", nullSafe(fdopening.getPaymentDate()));
		data.put("mihPayid", nullSafe(fdopening.getMihPayid()));
		return data;
	}

	private Object nullSafe(Object value) {
		return value == null ? JSONObject.NULL : value;
	}

	@RequestMapping(value = "/saveAccountDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveAccountDetails(@RequestBody String bm,
			@RequestHeader(name = "Accept") String accept, @RequestHeader(name = "Content-Type") String contentType,
			@RequestHeader(name = "Lg-Code", required = false) String lgcode,
			@RequestHeader(name = "MobileNo") String mobileNo, @RequestHeader(name = "X-Session-ID") String xSessionId,
			@RequestHeader(name = "X-Request-ID") String xRequestId, HttpServletRequest req) throws Exception {

		logger.debug("saveAccountDetails start");
		logger.debug("Request received from MobileNo: {}, Body: {}", mobileNo, bm);

		JSONObject header = new JSONObject();
		header.put("X-Request-ID", xRequestId);

		JSONObject jsonObject = new JSONObject(bm);
		JSONObject dataObject = jsonObject.optJSONObject("Data");

		String applicationNo = dataObject != null ? dataObject.optString("ApplicationNo", null) : null;
		logger.debug("Processing ApplicationNo: {}", applicationNo);

		if (applicationNo == null) {
			logger.error("ApplicationNo is missing in request.");
			JSONObject errorResponse = new JSONObject();
			JSONObject errorData = new JSONObject();
			errorData.put("Message", "Failure");
			errorData.put("Description", "ApplicationNo is missing in request.");
			errorResponse.put("Data", errorData);
			return new ResponseEntity<Object>(errorResponse.toMap(), HttpStatus.BAD_REQUEST);
		}

		FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));

		if (fdOpening == null) {
			logger.error("No record found for ApplicationNo: {}", applicationNo);
			JSONObject errorResponse = new JSONObject();
			JSONObject errorData = new JSONObject();
			errorData.put("Message", "Failure");
			errorData.put("Description", "No record found for ApplicationNo: " + applicationNo);
			errorResponse.put("Data", errorData);
			return new ResponseEntity<Object>(errorResponse.toMap(), HttpStatus.NOT_FOUND);
		}

		JSONObject accDetails = dataObject.optJSONObject("AccountDetails");

		if (accDetails == null) {
			logger.error("AccountDetails is missing for ApplicationNo: {}", applicationNo);
			JSONObject errorResponse = new JSONObject();
			JSONObject errorData = new JSONObject();
			errorData.put("Message", "Failure");
			errorData.put("Description", "AccountDetails is missing in request.");
			errorResponse.put("Data", errorData);
			return new ResponseEntity<Object>(errorResponse.toMap(), HttpStatus.BAD_REQUEST);
		}

		String year = accDetails.optString("tenureYear", "0");
		if (year.isEmpty()) {
			year = "0";
		}
		String month = accDetails.optString("tenureMonth", "0");
		if (month.isEmpty()) {
			month = "0";
		}
		String day = accDetails.optString("tenureDay", "0");
		if (day.isEmpty()) {
			day = "0";
		}

		if (lgcode != null && !lgcode.isEmpty()) {
			fdOpening.setLgCode(lgcode);
		}
		// Set Account Details
		fdOpening.setAccountDetails(accDetails.toString());
		fdOpening.setDepositAmount(accDetails.optString("depositAmount", ""));
		fdOpening.setTenure(year + "Y" + month + "M" + day + "D");
		fdOpening.setMaturityAmout(accDetails.optString("maturityAmount", ""));
		fdOpening.setMaturityDate(accDetails.optString("maturityDate", ""));
		fdOpening.setInterestEarned(accDetails.optString("interestEarned", ""));
		fdOpening.setFlowStaus("AD");

		sendotpservice.save(fdOpening);
		logger.debug("Account details saved for ApplicationNo: {}", applicationNo);
		logger.debug("Account details saved for ApplicationNo: {}", fdOpening.getAccountDetails());

		// Success Response
		JSONObject successResponse = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("Message", "Success");
		data.put("Description", "Data saved successfully");
		successResponse.put("Data", data);

		return new ResponseEntity<Object>(successResponse.toMap(), HttpStatus.OK);
	}

//	@RequestMapping(value = "/saveAccountDetails", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> saveAccountDetails(@RequestBody String bm,
//			@RequestHeader(name = "Accept", required = true) String accept,
//			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
//			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
//			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
//			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
//			throws Exception {
//		logger.debug("saveAccountDetails start");
//		logger.debug("saveAccountDetails request "+ mobileNo +" :: "+ bm);
//		JSONObject Header = new JSONObject();
//		Header.put("X-Request-ID", X_Request_ID);
//
//		JSONObject jsonObject = new JSONObject(bm);
//		String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
//		JSONObject accDetails = jsonObject.getJSONObject("Data").getJSONObject("AccountDetails");
//		logger.debug("saveAccountDetails request applicationNo :: " + applicationNo);
//		FdOpeningNTB fdOpening= fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
//		fdOpening.setAccountDetails(accDetails.toString());
//		fdOpening.setDepositAmount(accDetails.getString("depositAmount"));
//		fdOpening.setTenure(accDetails.getString("tenureMonth"));
//		fdOpening.setMaturityAmout(accDetails.getString("maturityAmount"));
//		fdOpening.setMaturityDate(accDetails.getString("maturityDate"));
//		fdOpening.setInterestEarned(accDetails.getString("interestEarned"));
//		fdOpening.setFlowStaus("Ad");
//		sendotpservice.save(fdOpening);
//		logger.debug("saveAccountDetails fdOpening :: " + fdOpening.toString());
//		logger.debug("saveAccountDetails update DB");
//		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
//		org.json.simple.JSONObject data = new org.json.simple.JSONObject();
//		data.put("Success", "Data Saved Successfully");
//		response.put("Data", data);
//		return new ResponseEntity<Object>(response, HttpStatus.OK);
//
//	}

	@RequestMapping(value = "/createWorkItem", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createWorkItem(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createWorkItem start");
		logger.debug("createWorkItem request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		JSONObject createWorkItem = fdopeningservice.createWorkItem(jsonObject, Header);
		System.out.println(createWorkItem);
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createWorkItem != null) {
			String Data2 = createWorkItem.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/createOrder", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createOrder(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createOrder start");
		logger.debug("createOrder request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("Authorization", Authorization);
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject createOrder = fdopeningservice.createOrder(jsonObject, Header);
		System.out.println(createOrder);
		HttpStatus h = HttpStatus.OK;
		if (createOrder != null) {
			String Data2 = createOrder.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			fdOpening.setIsPaymentDone("N"); // createOrder
			if (Data1.has("id"))
				;
			fdOpening.setOrderId(Data1.getString("id"));
			fdOpening.setCreateOrderResp(Data1.toString());
			sendotpservice.save(fdOpening);
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/createOrderETB", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createOrderETB(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createOrder start");
		logger.debug("createOrder request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("Authorization", Authorization);
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject createOrder = fdopeningservice.createOrder(jsonObject, Header);
		System.out.println(createOrder);
		HttpStatus h = HttpStatus.OK;
		if (createOrder != null) {
			String Data2 = createOrder.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			FdOpening fdOpening = fdservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
			fdOpening.setIsPaymentDone("N"); // createOrderETB
			fdOpening.setCreateOrderResp(Data1.toString());
			sendotpservice.save(fdOpening);
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/fetchOrder", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchOrder(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchOrder start");
		logger.debug("fetchOrder request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("Authorization", Authorization);
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject fetchOrder = fdopeningservice.fetchOrder(jsonObject, Header);
		System.out.println(fetchOrder);
		HttpStatus h = HttpStatus.OK;
		if (fetchOrder != null) {
			String Data2 = fetchOrder.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug("response" + Data1);
			String status = Data1.getString("status");
			FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			if (status.equalsIgnoreCase("paid")) {
				h = HttpStatus.OK;
				fdOpening.setIsPaymentDone("Y");// fetchOrder
			} else {
				h = HttpStatus.OK;
				fdOpening.setIsPaymentDone("N");// fetchOrder
			}
			fdOpening.setPaymentDetails(Data1.toString());
			sendotpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/fetchOrderETB", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchOrderETB(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchOrder start");
		logger.debug("fetchOrder request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("Authorization", Authorization);
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject fetchOrder = fdopeningservice.fetchOrder(jsonObject, Header);
		System.out.println(fetchOrder);
		HttpStatus h = HttpStatus.OK;
		if (fetchOrder != null) {
			String Data2 = fetchOrder.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug("response" + Data1);
			String status = Data1.getString("status");
			FdOpening fdOpening = fdservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
			if (status.equalsIgnoreCase("paid")) {
				h = HttpStatus.OK;
				fdOpening.setIsPaymentDone("Y");// fetchOrderETB
			} else {
				h = HttpStatus.OK;
				fdOpening.setIsPaymentDone("N");// fetchOrderETB
			}
			fdOpening.setPaymentDetails(Data1.toString());
			sendotpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getMethod().equals("OPTIONS")) {
			// response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			// throw new NoSuchElementException("You are Not authorized");
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "unatharised Access");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setCode(401);
			errorResponse.setMessage("Unauthorized Access");

			byte[] responseToSend = restResponseBytes(errorResponse);
			((HttpServletResponse) response).setHeader("Content-Type", "application/json");
			((HttpServletResponse) response).setStatus(401);
			response.getOutputStream().write(responseToSend);
			return;
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private byte[] restResponseBytes(ErrorResponse eErrorResponse) throws IOException {
		String serialized = new ObjectMapper().writeValueAsString(eErrorResponse);
		return serialized.getBytes();
	}

}
