package com.suryoday.EtbFdOpening.Controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.suryoday.EtbFdOpening.Service.*;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class VKYCController {
	Logger logger = LoggerFactory.getLogger(VKYCController.class);
	@Autowired
	VKYCService vkycservice;
	@Autowired
	FdOpeningService fdservice;

	@Autowired
    RefundNtbFdService refundFdService;

	@Autowired
	SendOtpService otpservice;
	@Autowired
	CustomerDetailsService custservice;

	@Autowired
	NtbFdDmsService dmsService;
	
	
	@RequestMapping(value = "/getVkycDetailsNew", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getHyperVergeVkycDetails(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("getVkycDetails start");
		logger.debug("getVkycDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("timestamp", timestamp);
		Header.put("client-id", clientId);

		JSONObject jsonObject = new JSONObject(bm);
		String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
//		JSONObject getVkycDetails = vkycservice.getVkycDetails(trackingId, Header);
		logger.debug("getHyperVergeVkycDetails start");
		JSONObject getVkycDetails = vkycservice.getHyperVergeVkycDetails(trackingId, Header);
		logger.debug("getHyperVergeVkycDetails end :: {}", getVkycDetails.toString());
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (getVkycDetails != null) {
			String Data2 = getVkycDetails.getString("data");
			System.out.println("Data2" + Data2);
			if (Data2.startsWith("{")) {
				JSONObject Data1 = new JSONObject(Data2);
				h = HttpStatus.OK;
				logger.debug(Data1.toString());
				return new ResponseEntity<Object>(Data1.toString(), h.OK);
			}
			return new ResponseEntity<Object>(Data2, h.BAD_REQUEST);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/createVkycNew", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createHyperVergeVkyc(@RequestBody String bm,
			@RequestHeader(name = "ApplicationNo", required = true) String ApplicationNo, HttpServletRequest req)
			throws Exception {
		logger.debug("createVkyc start");
		logger.debug("createVkyc request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );

		JSONObject jsonObject = new JSONObject(bm);
//		String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
		fdOpening.setVkycReq(jsonObject.toString());
//		JSONObject createVkyc = vkycservice.createVkyc(jsonObject, Header);
		JSONObject createVkyc = vkycservice.createHyperVergeVkyc(jsonObject, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createVkyc != null) {
			String Data2 = createVkyc.getString("data");
			System.out.println("Data2" + Data2);
			if (Data2.startsWith("{")) {
				JSONObject Data1 = new JSONObject(Data2);
				h = HttpStatus.OK;
				logger.debug(Data1.toString());
//				String trackingId = Data1.getString("trackingId");
				String trackingId = jsonObject.getString("userId");
				fdOpening.setVkycResp(Data1.toString());
				fdOpening.setVkycTrackingId(trackingId);
				fdOpening.setVkycStatus("Initiated");
				fdOpening.setIsVkycDone("N");
				otpservice.save(fdOpening);
				return new ResponseEntity<Object>(Data1.toString(), h.OK);
			}
			fdOpening.setVkycResp(Data2);
			fdOpening.setIsVkycDone("N");
			otpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data2, h.BAD_REQUEST);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}
	
	
	
	

	@RequestMapping(value = "/getVkycDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getVkycDetails(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("getVkycDetails start");
		logger.debug("getVkycDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("timestamp", timestamp);
		Header.put("client-id", clientId);

		JSONObject jsonObject = new JSONObject(bm);
		String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
		JSONObject getVkycDetails = vkycservice.getVkycDetails(trackingId, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (getVkycDetails != null) {
			String Data2 = getVkycDetails.getString("data");
			System.out.println("Data2" + Data2);
			if (Data2.startsWith("{")) {
				JSONObject Data1 = new JSONObject(Data2);
				h = HttpStatus.OK;
				logger.debug(Data1.toString());
				return new ResponseEntity<Object>(Data1.toString(), h.OK);
			}
			return new ResponseEntity<Object>(Data2, h.BAD_REQUEST);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/createVkyc", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createVkyc(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "ApplicationNo", required = true) String ApplicationNo,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("createVkyc start");
		logger.debug("createVkyc request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("timestamp", timestamp);
		Header.put("client-id", clientId);

		JSONObject jsonObject = new JSONObject(bm);
//		String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
		fdOpening.setVkycReq(jsonObject.toString());
		JSONObject createVkyc = vkycservice.createVkyc(jsonObject, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createVkyc != null) {
			String Data2 = createVkyc.getString("data");
			System.out.println("Data2" + Data2);
			if (Data2.startsWith("{")) {
				JSONObject Data1 = new JSONObject(Data2);
				h = HttpStatus.OK;
				logger.debug(Data1.toString());
				String trackingId = Data1.getString("trackingId");
				fdOpening.setVkycResp(Data1.toString());
				fdOpening.setVkycTrackingId(trackingId);
				fdOpening.setVkycStatus("Initiated");
				fdOpening.setIsVkycDone("N");
				otpservice.save(fdOpening);
				return new ResponseEntity<Object>(Data1.toString(), h.OK);
			}
			fdOpening.setVkycResp(Data2);
			fdOpening.setIsVkycDone("N");
			otpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data2, h.BAD_REQUEST);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/fetchByTrackingId", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchByTrackingId(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchByTrackingId start");
		logger.debug("fetchByTrackingId request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
		FdOpeningNTB fdOpeningNTB = fdservice.fetchByTrackingId(trackingId);
		JSONObject data = new JSONObject(fdOpeningNTB);
		JSONObject response = new JSONObject();
		response.put("Data", data);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

	}

	@PostMapping("/savePartialVkyc")
	public ResponseEntity<?> savePartialVkyc(@RequestBody String bm,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID) {
		try {
			logger.debug("Received request to save account info with session ID: {}", X_Session_ID);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(bm);

			String applicationNo = root.path("Data").path("applicationNo").asText();
			String mobileNo = root.path("Data").path("MobileNo").asText();

			boolean sessionIdValid = otpservice.validateSessionId(X_Session_ID, mobileNo);
			logger.debug("Session ID validation result for mobile {}: {}", mobileNo, sessionIdValid);

			if (sessionIdValid) {
				FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
				if (fdOpening == null) {
					logger.warn("No application found for ApplicationNo: {}", applicationNo);
					Map<String, String> errorMap = new HashMap<>();
					errorMap.put("error", "Application not found");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
				}

				fdOpening.setIsPartialVkyc("Y");
				fdOpening.setVkycStatus("Partial");
				otpservice.save(fdOpening);

				logger.debug("Partial VKYC details saved for ApplicationNo: {}", applicationNo);
				JSONObject response = new JSONObject();
				JSONObject subresponse = new JSONObject();
				subresponse.put("message", "Partial VKYC information saved successfully");
				response.put("Data", subresponse);
				return ResponseEntity.ok(response.toMap());
			} else {
				JSONObject data2 = new JSONObject();
				data2.put("value", "SessionId is expired or Invalid sessionId");
				JSONObject data3 = new JSONObject();
				data3.put("Error", data2);

				logger.warn("Invalid or expired Session ID for mobileNo: {}", mobileNo);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data3.toString());
			}
		} catch (Exception e) {
			logger.error("Exception occurred while saving account information", e);
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
		}
	}

	@RequestMapping(value = "/getalltrackingids", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> getAllTrackingIds() {
		List<String> trackingIds = vkycservice.getAllTrackingIds("Y");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("count", trackingIds.size());
		response.put("data", trackingIds);

		return ResponseEntity.ok(response);
	}

//	@RequestMapping(value = "/cifAccCreationNew", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> cifAccCreationPROD() {
//		CronJob cronJob = new CronJob();
//		ResponseEntity<Object> cifAccCreationPROD = cronJob.cifAccCreationPROD();
//		return cifAccCreationPROD;
//	}
	
	
	@RequestMapping(value = "/cifAccCreationNew", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> cifAccCreationPRODOLD() {

		logger.info("START :: cifAccCreationPROD API triggered");

		JSONObject response = new JSONObject();

		JSONObject header = new JSONObject();
		header.put("X-Request-ID", "WNT");
		header.put("timestamp", String.valueOf(System.currentTimeMillis()));
		header.put("client-id", "101");

		try {

			/* ================= PARTIAL VKYC PROCESSING ================= */
			List<FdOpeningNTB> partialVkyList = vkycservice.findByIsPartialVkycAndIsFdCreated("Y", "Y", "N");

			logger.info("Partial VKYC records count :: {}", partialVkyList.size());

			for (FdOpeningNTB fd : partialVkyList) {
				try {
					logger.info("Processing Partial VKYC :: applicationNo={}", fd.getApplicationNo());
					processPartialVkyc(fd, header);
				} catch (Exception e) {
					logger.error("Error while processing partial VKYC :: applicationNo={}", fd.getApplicationNo(), e);
				}
			}

			/* ================= VKYC STATUS PROCESSING ================= */
			List<String> trackingIds = vkycservice.getAllTrackingIds("Y");
			logger.info("Total trackingIds to process :: {}", trackingIds.size());

			for (String trackingId : trackingIds) {

				if (trackingId == null) {
					logger.warn("Skipping null trackingId");
					continue;
				}

				try {
					logger.info("Fetching VKYC details :: trackingId={}", trackingId);

					JSONObject vkycDetails = vkycservice.getHyperVergeVkycDetails(trackingId, header);

					JSONObject summary = null;
					if (vkycDetails == null) {
						logger.warn("VKYC response is NULL :: trackingId={}", trackingId);
						continue;
					}

//					if (vkycDetails.has("data")) {
//						summary = new JSONObject(vkycDetails.getString("data")).getJSONObject("summary");
//					}
					
					if (vkycDetails.has("data")) {
//						summary = new JSONObject(vkycDetails.getString("data")).getJSONObject("summary");
						JSONObject dataObj = new JSONObject(vkycDetails.optString("data", "{}"));
					    summary = dataObj.optJSONObject("summary");
					}

					if (summary == null) {
						logger.warn("summary response is NULL :: trackingId={}", trackingId);
						continue;
					}

					FdOpeningNTB fdData = fdservice.fetchByTrackingId(trackingId);

					if (fdData == null) {
						logger.warn("No FD record found :: trackingId={}", trackingId);
						continue;
					}
					fdData.setVkycResp(vkycDetails.toString());

					if (summary.has("vkycDetails")) {
						fdData.setAgentAction(summary.getJSONObject("vkycDetails").toString());
					}

					if (summary.has("auditDetails")) {
						fdData.setAuditorAction(summary.getJSONObject("auditDetails").toString());
					}

					if (summary.has("checkerDetails")) {

						fdData.setCheckerAction(summary.getJSONObject("checkerDetails").toString());

						String vkycStatus = summary.getJSONObject("checkerDetails").getString("checkerDecision");

						logger.info("VKYC Status :: trackingId={}, status={}", trackingId, vkycStatus);
						fdData.setVkycStatus(vkycStatus);
						if ("APPROVED".equalsIgnoreCase(vkycStatus)) {

							fdData.setIsVkycDone("Y");
							fdData.setVkycStatus("completed");

							if (!"Y".equalsIgnoreCase(fdData.getIsCifCreated())) {
								logger.info("Creating CIF :: applicationNo={}", fdData.getApplicationNo());
								createCif(fdData, header);
							}

							if ("Y".equalsIgnoreCase(fdData.getIsCifCreated())
									&& !"Y".equalsIgnoreCase(fdData.getIsFdCreated())) {

								logger.info("Creating FD :: applicationNo={}", fdData.getApplicationNo());
								createFd(fdData, header);
							}

							triggerfdDmsUploadManually();
						}

						if ("DECLINED".equalsIgnoreCase(vkycStatus)) {
							logger.info("VKYC Declined :: initiating refund :: applicationNo={}",
									fdData.getApplicationNo());

//							RefundNtbFd refund = new RefundNtbFd();
//							refund.setApplicationNo(fdData.getApplicationNo());
//							refund.setAccountNo(fdData.getAccountNo());
//							refund.setIfsc(fdData.getIfsc());
//							refund.setMobileNo(fdData.getMobileNo());
//							refund.setName(fdData.getName());
//							refund.setPaymentDate(fdData.getPaymentDate());
//							refund.setPayuOrderId(fdData.getPayuOrderId());
//							refund.setMihPayid(fdData.getMihPayid());
//							refund.setAmount(fdData.getDepositAmount());
//							refund.setIsRefundDone("N");
//
//							refundFdService.save(refund);
						}
					}

					otpservice.save(fdData);

				} catch (Exception e) {
					logger.error("FATAL ERROR in cifAccCreationPROD", e);

					response.put("status", "FAILURE");
					response.put("message", "Internal server error");
				}
			}

			response.put("status", "SUCCESS");
			response.put("message", "VKYC processing completed");

		} catch (Exception e) {
			logger.error("Fatal error in cifAccCreationPROD API", e);
			response.put("status", "FAILURE");
			response.put("message", "Internal server error");
		}

		logger.info("END :: cifAccCreationPROD API");

		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}


//	@RequestMapping(value = "/cifAccCreation", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> cifAccCreation() throws Exception {
//
//		logger.debug("cifAccCreation start");
//		logger.debug("cifAccCreation request");
//		try {
//			JSONObject Header = new JSONObject();
//			Header.put("X-Request-ID", "WNT");
//			Header.put("timestamp", "1706508024779");
//			Header.put("client-id", "101");
//
//			List<FdOpeningNTB> partialVkyList = vkycservice.findByIsPartialVkycAndIsFdCreated("Y", "Y", "N");
//			logger.debug(" partialVkyList  list :: " + partialVkyList.toString());
//			if (!partialVkyList.isEmpty()) {
//				for (FdOpeningNTB fd : partialVkyList) {
//					processPartialVkyc(fd, Header);
//				}
//			} else {
//				logger.debug("No partial VKYC records found to process");
//			}
//
//			List<String> list = vkycservice.getAllTrackingIds("Y");
//			logger.debug("cifAccCreation list :: " + list.toString());
//			for (int i = 0; i < list.size(); i++) {
//				String trackingId = list.get(i);
//				if (trackingId != null) {
//					JSONObject getVkycDetails = vkycservice.getVkycDetails(trackingId, Header);
//
//					if (getVkycDetails != null) {
//						logger.debug("inside get vkyc :: " + getVkycDetails.toString());
//						String Data2 = getVkycDetails.getString("data");
//						if (Data2.startsWith("{")) {
//							JSONObject Data1 = new JSONObject(Data2);
//							String status = Data1.getString("status");
//							logger.debug(trackingId + " ==>> " + status);
//							FdOpeningNTB fetchByTrackingId = fdservice.fetchByTrackingId(trackingId);
//							fetchByTrackingId.setVkycStatus(status);
//							String auditorActionStr = "";
//							if (Data1.has("auditorAction") && !Data1.isNull("auditorAction")) {
//								auditorActionStr = Data1.getJSONObject("auditorAction").toString();
//							}
//
//							// Extract agentAction JSON as String (if present)
//							String agentActionStr = "";
//							if (Data1.has("agentAction") && !Data1.isNull("agentAction")) {
//								agentActionStr = Data1.getJSONObject("agentAction").toString();
//							}
//
//							// Set extracted JSON strings in record
//							fetchByTrackingId.setAuditorAction(auditorActionStr);
//							fetchByTrackingId.setAgentAction(agentActionStr);
//							otpservice.save(fetchByTrackingId);
//
////							if (status.equalsIgnoreCase("Rejected")) {
////								RefundNtbFd refundNtbFd = new RefundNtbFd();
////								refundNtbFd.setApplicationNo(fetchByTrackingId.getApplicationNo());
////								refundNtbFd.setAccountNo(fetchByTrackingId.getAccountNo());
////								refundNtbFd.setIfsc(fetchByTrackingId.getIfsc());
////								refundNtbFd.setMobileNo(fetchByTrackingId.getMobileNo());
////								refundNtbFd.setName(fetchByTrackingId.getName());
////								refundNtbFd.setPaymentDate(fetchByTrackingId.getPaymentDate());
////								refundNtbFd.setPayuOrderId(fetchByTrackingId.getPayuOrderId());
////								refundNtbFd.setIsRefundDone("N");
////								refundNtbFd.setMihPayid(fetchByTrackingId.getMihPayid());
////								refundNtbFd.setAmount(fetchByTrackingId.getDepositAmount());
////								refundFdService.save(refundNtbFd);
////
////							}
//
//							if (status.equalsIgnoreCase("Approved")) {
//
//								logger.debug("Inside APPROVED");
////								FdOpeningNTB fetchByTrackingId = fdservice.fetchByTrackingId(trackingId);
//								fetchByTrackingId.setVkycResp(Data1.toString());
//								fetchByTrackingId.setIsVkycDone("Y");
//								fetchByTrackingId.setVkycStatus("completed");
//								otpservice.save(fetchByTrackingId);
//								if (fetchByTrackingId.getIsCifCreated() == null
//										|| fetchByTrackingId.getIsCifCreated().equalsIgnoreCase("N")) {
//									logger.debug("Inside CIF CREATION");
//									createCif(fetchByTrackingId, Header);
//
//								}
//								if (fetchByTrackingId.getIsCifCreated().equalsIgnoreCase("Y")) {
//									if (fetchByTrackingId.getIsFdCreated() == null
//											|| fetchByTrackingId.getIsFdCreated().equalsIgnoreCase("N")) {
//										logger.debug("Inside FD CREATION");
//										createFd(fetchByTrackingId, Header);
//
//									}
//								}
//
//							}
//
//						}
//
//					}
//				}
//			}
//			triggerfdDmsUploadManually();
//		} catch (Exception e) {
//			logger.debug("Exception " + e.getMessage());
//		}
//		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
//		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
//	}

	private void processPartialVkyc(FdOpeningNTB fd, JSONObject headers) {
		logger.debug("Start processing Partial VKYC: ApplicationNo={}", fd.getApplicationNo());
		logger.debug("processPartialVkyc obj" + fd.toString());
		if ("Y".equalsIgnoreCase(fd.getIsPartialVkyc())) {
			logger.debug("Partial VKYC found for ApplicationNo={}", fd.getApplicationNo());

			try {
				if (fd.getIsCifCreated() == null || "N".equalsIgnoreCase(fd.getIsCifCreated())) {
					createCif(fd, headers);
				}

				if ("Y".equalsIgnoreCase(fd.getIsCifCreated())
						&& (fd.getIsFdCreated() == null || "N".equalsIgnoreCase(fd.getIsFdCreated()))) {
					createFd(fd, headers);
				}
			} catch (Exception e) {
				logger.error("Error occurred while processing VKYC approval for record: {}, . Error: {}", fd,
						e.getMessage(), e);

			}
		}
	}

//	private void createCif(FdOpeningNTB fd, JSONObject headers) {
//		String cifRequest = fd.getCifRequest();
//		JSONObject cifReqInJson = new JSONObject(cifRequest);
//		JSONObject cifCreation = custservice.cifCreation(cifReqInJson, headers);
//		if (cifCreation != null) {
//			JSONObject Data3 = new JSONObject(cifCreation.getString("data"));
//			if (Data3.has("Data")) {
//				fd.setIsCifCreated("Y");
//			} else if (Data3.has("Error")) {
//				fd.setIsCifCreated("N");
//			}
//			fd.setCifResponse(Data3.toString());
//			otpservice.save(fd);
//		}
//
//	}

	private void createCif(FdOpeningNTB fd, JSONObject headers) {
		try {
			logger.debug("Starting CIF creation for FD: {}", fd.getApplicationNo());

			String cifRequest = fd.getCifRequest();
			if (cifRequest == null || cifRequest.trim().isEmpty()) {
				logger.error("CIF Request Payload is null or empty for FD: {}", fd.getApplicationNo());
				fd.setIsCifCreated("N");
				otpservice.save(fd);
				return; // exit early, no point in proceeding
			}
			logger.debug("CIF Request Payload: {}", cifRequest);

			JSONObject cifReqInJson = new JSONObject(cifRequest);
			JSONObject cifCreation = custservice.cifCreation(cifReqInJson, headers);

			JSONObject Data3 = new JSONObject(cifCreation.getString("data"));
			if (Data3.has("Data")) {
				JSONObject dataObj = Data3.getJSONObject("Data");
				String matchStatus = dataObj.optString("MatchStatus", null);

				if ("NoMatch".equalsIgnoreCase(matchStatus)) {
					String ucic = dataObj.optString("UCIC", null);
					fd.setCifCustomerId(ucic);
					fd.setIsCifCreated("Y");
					logger.debug("CIF created successfully for FD: {}", fd.getApplicationNo());
				}
			} else if (Data3.has("Error")) {
				fd.setIsCifCreated("N");
				logger.warn("CIF creation failed with Error response for FD: {}, Response: {}", fd.getApplicationNo(),
						Data3.toString());
			}

			fd.setCifResponse(Data3.toString());
			otpservice.save(fd);

		} catch (Exception e) {
			logger.error("Exception during CIF creation for FD: {}", fd.getApplicationNo(), e);
			fd.setIsCifCreated("N");
			otpservice.save(fd);
		}
//	        if (cifCreation != null) {
//	            logger.debug("CIF Creation Response: {}", cifCreation.toString());
//
//	            JSONObject Data3 = new JSONObject(cifCreation.getString("data"));
//	            String matchStatus = Data3.getJSONObject("Data").optString("MatchStatus",null);
//	            if (Data3.has("Data") && "NoMatch".equalsIgnoreCase(matchStatus)) {
//	            	String ucic = Data3.getJSONObject("Data").optString("UCIC",null);
//	            	fd.setCifCustomerId(ucic);
//	                fd.setIsCifCreated("Y");
//	                logger.debug("CIF created successfully for FD: {}", fd.getApplicationNo());
//	            } else if (Data3.has("Error")) {
//	                fd.setIsCifCreated("N");
//	                logger.warn("CIF creation failed with Error response for FD: {}, Response: {}", fd.getApplicationNo(),Data3.toString());
//	            }
//
//	            fd.setCifResponse(Data3.toString());
//	            otpservice.save(fd);
//	            logger.debug("CIF response saved for FD: {}", fd.getApplicationNo());
//	        } else {
//	            logger.error("CIF Creation returned NULL response for FD: {}", fd.getApplicationNo());
//	        }
	}

//	private void createFd(FdOpeningNTB fd, JSONObject headers) {
//		String fdRequest = fd.getFdOpeningReq();
//		String cifResponse = fd.getCifResponse();
//		JSONObject cifRespJson = new JSONObject(cifResponse);
//		JSONObject fdReqInJson = new JSONObject(fdRequest);
//		fdReqInJson.getJSONObject("Data").remove("CustomerId");
//		fdReqInJson.getJSONObject("Data").put("CustomerId", cifRespJson.getJSONObject("Data").getString("UCIC"));
//		if (fd.getPaymentDate() != null) {
//			fdReqInJson.getJSONObject("Data").put("AccountOpenDate", fd.getPaymentDate().toLocalDate().toString());
//		}
//		JSONObject createDeposit = fdservice.createDeposit(fdReqInJson, headers);
//		if (createDeposit != null) {
//			JSONObject Data3 = new JSONObject(createDeposit.getString("data"));
//			if (Data3.has("Data")) {
//				fd.setIsFdCreated("Y");
//				fd.setIsDmsUpload("N");
//				String accountId = Data3.getJSONArray("Data").getJSONObject(0).getString("AccountId");
//				fd.setFdAccNo(accountId);
//				if ("Y".equalsIgnoreCase(fd.getIsPaymentDone())) {
//					paymentTransactionPush(fd, headers);
//				}
//			} else if (Data3.has("Error")) {
//				fd.setIsFdCreated("N");
//			}
//			fd.setFdOpeningReq(fdReqInJson.toString());
//			fd.setFdOpeningResp(Data3.toString());
//			logger.debug("cifAccCreation :: fetchByTrackingId :: " + fd.toString());
//			otpservice.save(fd);
//
//		}
//
//	}

	private void createFd(FdOpeningNTB fd, JSONObject headers) {
		try {
			logger.debug("Starting FD creation for FD: {}", fd.getApplicationNo());

			String fdRequest = fd.getFdOpeningReq();
			if (fdRequest == null || fdRequest.trim().isEmpty()) {
				logger.error("FD Opening Request is null or empty for FD: {}", fd.getApplicationNo());
				otpservice.save(fd);
				return;
			}
			String cifResponse = fd.getCifResponse();
			if (cifResponse == null || cifResponse.trim().isEmpty()) {
				logger.error("CIF Response is null or empty for FD: {}", fd.getApplicationNo());
				otpservice.save(fd);
				return;
			}

			logger.debug("FD Request Payload (before CustomerId update): {}", fdRequest);
			logger.debug("CIF Response: {}", cifResponse);

			JSONObject cifRespJson = new JSONObject(cifResponse);
			JSONObject fdReqInJson = new JSONObject(fdRequest);

			// Replace CustomerId with UCIC from CIF response
			fdReqInJson.getJSONObject("Data").put("CustomerId", cifRespJson.getJSONObject("Data").getString("UCIC"));

			// Add AccountOpenDate if payment date is available
			if (fd.getPaymentDate() != null) {
				fdReqInJson.getJSONObject("Data").put("AccountOpenDate", fd.getPaymentDate().toLocalDate().toString());
			}

			logger.debug("FD Request Payload (final): {}", fdReqInJson);

			JSONObject createDeposit = fdservice.createDeposit(fdReqInJson, headers);

			if (createDeposit != null) {
				logger.debug("FD Creation Response: {}", createDeposit);

				JSONObject Data3 = new JSONObject(createDeposit.getString("data"));

				if (Data3.has("Data")) {
					fd.setIsFdCreated("Y");
					fd.setIsDmsUpload("N");

					String accountId = Data3.getJSONArray("Data").getJSONObject(0).getString("AccountId");
					fd.setFdAccNo(accountId);

					logger.debug("FD created successfully. AccountId: {}, FD: {}", accountId, fd.getApplicationNo());

					if ("Y".equalsIgnoreCase(fd.getIsPaymentDone())) {
						logger.debug("Payment is already done. Triggering payment transaction push for FD: {}",
								fd.getApplicationNo());
						// paymentTransactionPush(fd);
					}

				} else if (Data3.has("Error")) {
					fd.setIsFdCreated("N");
					logger.warn("FD creation failed with error response for FD: {}", fd.getApplicationNo());
				}

				fd.setFdOpeningReq(fdReqInJson.toString());
				fd.setFdOpeningResp(Data3.toString());

				logger.debug("FD object after processing: {}", fd);

				otpservice.save(fd);
				logger.debug("FD record saved for FD: {}", fd.getApplicationNo());

			} else {
				logger.error("FD Creation returned NULL response for FD: {}", fd.getApplicationNo());
			}

		} catch (Exception e) {
			logger.error("Exception during FD creation for FD: {}", fd.getApplicationNo(), e);
			fd.setIsFdCreated("N");
			otpservice.save(fd);
		}
	}

//	private JSONObject paymentTransactionPush(FdOpeningNTB fd) {
//
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//		String transactionDate = (fd.getPaymentDate()).format(dateTimeFormatter);
//		JSONObject pushPayload = new JSONObject();
//		JSONObject pushData = new JSONObject();
//		pushData.put("TransactionAmount", fd.getDepositAmount());
//		pushData.put("MobileNumber", fd.getMobileNo());
//		pushData.put("PGReferenceId", fd.getMihPayid());
//		pushData.put("ChannelCode", "WNT");
//		pushData.put("SettlementIndicator", "Y");
//		pushData.put("TransactionDate", transactionDate);
//		pushPayload.put("Data", pushData);
//
//		JSONObject resObj = refundFdService.paymentTransactionPushSer(pushPayload);
//		return resObj;
//
//	}

	@PostMapping(value = "/refundFdPayment", produces = "application/json")
	private void RefundNtbFdPayment() {
		logger.debug("Trigger Refund NTB Fd Payment Manually Start.............");

//		List<RefundNtbFd> refundFdList = refundFdService.getAllRefundNtbFdList("N");
		List<FdOpeningNTB> refundNtbFdList = refundFdService.findRefundNtbFdList("Y","N","Y","Y");
		logger.debug(" refundFdPayment  API CALL refundFdList  list :: " + refundNtbFdList.toString());

		if (!refundNtbFdList.isEmpty()) {
			for (FdOpeningNTB refundFd : refundNtbFdList) {

//				FdOpeningNTB fd = fdservice.fetchByApplicationNo(refundFd.getApplicationNo());


				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

				String transactionDate = (refundFd.getPaymentDate()).format(dateTimeFormatter);
				JSONObject pushPayload = new JSONObject();
				JSONObject pushData = new JSONObject();
				pushData.put("TransactionAmount", refundFd.getDepositAmount());
				pushData.put("MobileNumber", refundFd.getMobileNo());
				pushData.put("PGReferenceId", refundFd.getMihPayid());
				pushData.put("ChannelCode", "WNT");
				pushData.put("SettlementIndicator", "Y");
				pushData.put("TransactionDate", transactionDate);
				pushPayload.put("Data", pushData);
				logger.debug("refundFdPayment API CALL payment push payload:: " + pushPayload.toString());
				refundFd.setPaymentPushReq(pushPayload.toString());
				JSONObject resObj = refundFdService.paymentTransactionPushSer(pushPayload);
				logger.debug("refundFdPayment API CALL  payment push Response :: " + resObj.toString());
				refundFd.setPaymentPushResp(resObj.toString());

				JSONObject payload = new JSONObject();
				JSONObject data = new JSONObject();

				data.put("AccountNumber", "10000380170005");
				data.put("PGReferenceId", refundFd.getMihPayid());
				data.put("RefundAccountNumber", refundFd.getAccountNo());
				data.put("RefundAccountIfsc", refundFd.getIfsc());
				data.put("CustomerName", refundFd.getName());

				payload.put("Data", data);
				logger.debug("refundFdPayment API CALL  refund payload :: " + payload.toString());
				refundFd.setRefundReq(payload.toString());
				JSONObject paymentRefundResponse = refundFdService.paymentRefundTransactionRequest(payload);
				logger.debug("refundFdPayment API CALL  Payment Refund Response :: " + paymentRefundResponse.toString());
				String dataStr = paymentRefundResponse.optString("data", "{}");
				JSONObject innerData = new JSONObject(dataStr);

				if (innerData.has("Data")) {
					refundFd.setIsRefundDone("Y");
					refundFd.setIsActive("N");

					JSONObject successData = innerData.getJSONObject("Data");
					String transactionCode = successData.optString("TransactionCode", "");
					String message = successData.optString("Message", "");

					logger.debug("refundFdPayment API CALL  Refund Success");
					logger.debug("refundFdPayment API CALL TransactionCode: " + transactionCode);
					logger.debug("refundFdPayment API CALL  Message: " + message);

				} else if (innerData.has("Error")) {
					refundFd.setIsRefundDone("N");

					JSONObject error = innerData.getJSONObject("Error");
					String code = error.optString("Code", "");
					String description = error.optString("Description", "");
					logger.debug("refundFdPayment API CALL Refund Failed");
					logger.debug("refundFdPayment API CALL Error Code: " + code);
					logger.debug("refundFdPayment API CALL  Error Description: " + description);

				} else {

					logger.debug("refundFdPayment API CALL  refundFdPayment Unknown response: " + paymentRefundResponse.toString());
				}
				refundFd.setRefundResp(paymentRefundResponse.toString());
				otpservice.save(refundFd);
			}
		} else {
			logger.debug("No Refund records found to process");
		}
	}

//	@PostMapping(value = "/refundFdPayment", produces = "application/json")
//	private void triggerRefundNtbFdPaymentManually() {
//		logger.debug("Trigger Refund NTB Fd Payment Manually Start.............");
//
//		List<RefundNtbFd> refundFdList = refundFdService.getAllRefundNtbFdList("N");
//		logger.debug(" refundFdList  list :: " + refundFdList.toString());
//
//		if (!refundFdList.isEmpty()) {
//			for (RefundNtbFd refundFd : refundFdList) {
//
//				FdOpeningNTB fd = fdservice.fetchByApplicationNo(refundFd.getApplicationNo());
////				JSONObject paymentTransactionPush = paymentTransactionPush(fdOpening);
//
//				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//				String transactionDate = (fd.getPaymentDate()).format(dateTimeFormatter);
//				JSONObject pushPayload = new JSONObject();
//				JSONObject pushData = new JSONObject();
//				pushData.put("TransactionAmount", fd.getDepositAmount());
//				pushData.put("MobileNumber", fd.getMobileNo());
//				pushData.put("PGReferenceId", fd.getMihPayid());
//				pushData.put("ChannelCode", "WNT");
//				pushData.put("SettlementIndicator", "Y");
//				pushData.put("TransactionDate", transactionDate);
//				pushPayload.put("Data", pushData);
//				logger.debug("payment push payload:: " + pushPayload.toString());
//				refundFd.setPaymentPushReq(pushPayload.toString());
//				JSONObject resObj = refundFdService.paymentTransactionPushSer(pushPayload);
//				logger.debug("payment push Response :: " + resObj.toString());
//				refundFd.setPaymentPushResp(resObj.toString());
//
//				JSONObject payload = new JSONObject();
//				JSONObject data = new JSONObject();
//
//				data.put("AccountNumber", "10000380170005");
//				data.put("PGReferenceId", refundFd.getMihPayid());
//				data.put("RefundAccountNumber", refundFd.getAccountNo());
//				data.put("RefundAccountIfsc", refundFd.getIfsc());
//				data.put("CustomerName", refundFd.getName());
//
//				payload.put("Data", data);
//				logger.debug(" refund payload :: " + payload.toString());
//				refundFd.setRefundReq(payload.toString());
//				JSONObject paymentRefundResponse = refundFdService.paymentRefundTransactionRequest(payload);
//				logger.debug(" Payment Refund Response :: " + paymentRefundResponse.toString());
//				String dataStr = paymentRefundResponse.optString("data", "{}");
//				JSONObject innerData = new JSONObject(dataStr);
//
//				if (innerData.has("Data")) {
//					refundFd.setIsRefundDone("Y");
//
//					JSONObject successData = innerData.getJSONObject("Data");
//					String transactionCode = successData.optString("TransactionCode", "");
//					String message = successData.optString("Message", "");
//
//					logger.debug("Refund Success");
//					logger.debug("TransactionCode: " + transactionCode);
//					logger.debug("Message: " + message);
//
//				} else if (innerData.has("Error")) {
//					refundFd.setIsRefundDone("N");
//
//					JSONObject error = innerData.getJSONObject("Error");
//					String code = error.optString("Code", "");
//					String description = error.optString("Description", "");
//					logger.debug("Refund Failed");
//					logger.debug("Error Code: " + code);
//					logger.debug("Error Description: " + description);
//
//				} else {
//
//					logger.debug("refundFdPayment Unknown response: " + paymentRefundResponse.toString());
//				}
//				refundFd.setRefundResp(paymentRefundResponse.toString());
//				refundFdService.save(refundFd);
//			}
//		} else {
//			logger.debug("No records found for Refund Fd Payment process");
//		}
//	}

	@PostMapping(value = "/fdDmsUpload", produces = "application/json")
	public ResponseEntity<?> triggerfdDmsUploadManually() {
		logger.debug("Trigger fdDmsUpload Manually start..............");

		List<FdOpeningNTB> dmsUploadList = vkycservice.getAllDmsUploadList("N");
		logger.debug("fdDmsUpload list size: {}", dmsUploadList.size());
//		logger.debug(" DmsUploadCron  list :: " + dmsUploadList.toString());

		if (!dmsUploadList.isEmpty()) {
			for (FdOpeningNTB fd : dmsUploadList) {
				dmsService.FdDmsUpload(fd);
			}
		} else {
			logger.debug("No DMS records found to process");
		}

		JSONObject response = new JSONObject();
		response.put("status", "success");
		response.put("message", "fdDmsUpload triggered manually");
		return new ResponseEntity<>(response.toMap(), HttpStatus.OK);
	}

}
