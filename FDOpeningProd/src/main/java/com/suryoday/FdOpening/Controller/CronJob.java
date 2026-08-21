package com.suryoday.FdOpening.Controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.CustomerDetailsService;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.NtbFdDmsService;
import com.suryoday.FdOpening.Service.RefundNtbFdService;
import com.suryoday.FdOpening.Service.SendOtpService;
import com.suryoday.FdOpening.Service.VKYCService;

@Component
@RestController
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping(value = "/fdOpening")
public class CronJob {

	Logger logger = LoggerFactory.getLogger(CronJob.class);
	@Autowired
	VKYCService vkycservice;
	@Autowired
	FdOpeningService fdservice;
	@Autowired
	SendOtpService sendotpservice;
	@Autowired
	CustomerDetailsService custservice;
	@Autowired
	RefundNtbFdService refundFdService;

	@Autowired
	NtbFdDmsService dmsService;

	@Scheduled(cron = "0 0 0/3 * * ?")
	public void scheduledTaskOld() {
		LocalTime now = LocalTime.now();

		if (now.isBefore(LocalTime.of(6, 0)) || now.equals(LocalTime.MIDNIGHT)) {
			logger.debug("Skipping scheduled task between 12 AM and 6 AM");
			return;
		}

		logger.debug("Starting scheduled task");
		try {
			logger.debug("cron calling");
//			crone(); //OLD VKYC THING
			cifAccCreationPROD(); // NEW VKYC
			DmsUploadCron();
//			RefundNtbFdPayment();
		} catch (Exception e) {
			logger.debug("Exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	public ResponseEntity<Object> DmsUploadCron() {

		List<FdOpeningNTB> dmsUploadList = vkycservice.getAllDmsUploadList("N");
		logger.debug(" DmsUploadCron  list :: " + dmsUploadList.toString());
		logger.debug(" DmsUploadCron  list  Size :: " + dmsUploadList.size());
		if (!dmsUploadList.isEmpty()) {
			for (FdOpeningNTB fd : dmsUploadList) {
				dmsService.FdDmsUpload(fd);
			}
		} else {
			logger.debug("No DMS records found to process");
		}

		JSONObject response = new JSONObject();
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

	}
	
	
	public ResponseEntity<Object> cifAccCreationPROD() {

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

					if (vkycDetails.has("data")) {
						summary = new JSONObject(vkycDetails.getString("data")).getJSONObject("summary");
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

//							triggerfdDmsUploadManually();
						}

						if ("DECLINED".equalsIgnoreCase(vkycStatus)) {
							logger.info("VKYC Declined :: initiating refund :: applicationNo={}",
									fdData.getApplicationNo());
							fdData.setIsRefundDone("N");

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

					sendotpservice.save(fdData);

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

	public ResponseEntity<Object> crone() throws Exception {
		logger.debug("cifAccCreation cron start");

		try {
			JSONObject Header = new JSONObject();
			Header.put("X-Request-ID", "WNT");
			Header.put("timestamp", "1706508024779");
			Header.put("client-id", "101");
			List<String> list = vkycservice.getAllTrackingIds("Y");
			logger.debug(" cron list :: " + list.toString());
			List<FdOpeningNTB> partialVkyList = vkycservice.findByIsPartialVkycAndIsFdCreated("Y", "Y", "N");
			logger.debug(" partialVkyList  list :: " + partialVkyList.toString());
			if (!partialVkyList.isEmpty()) {
				for (FdOpeningNTB fd : partialVkyList) {
					processPartialVkyc(fd, Header);
				}
			} else {
				logger.debug("No partial VKYC records found to process");
			}

			for (int i = 0; i < list.size(); i++) {
				String trackingId = list.get(i);
				if (trackingId != null) {
					JSONObject getVkycDetails = vkycservice.getVkycDetails(trackingId, Header);
					if (getVkycDetails != null) {
						
						String Data2 = getVkycDetails.getString("data");
						if (Data2.startsWith("{")) {
							JSONObject Data1 = new JSONObject(Data2);
							String status = Data1.getString("status");
							FdOpeningNTB fetchByTrackingId = fdservice.fetchByTrackingId(trackingId);
							fetchByTrackingId.setVkycStatus(status);
							String auditorActionStr = "";
							if (Data1.has("auditorAction") && !Data1.isNull("auditorAction")) {
								auditorActionStr = Data1.getJSONObject("auditorAction").toString();
							}

							// Extract agentAction JSON as String (if present)
							String agentActionStr = "";
							if (Data1.has("agentAction") && !Data1.isNull("agentAction")) {
								agentActionStr = Data1.getJSONObject("agentAction").toString();
							}

							// Set extracted JSON strings in record
							fetchByTrackingId.setAuditorAction(auditorActionStr);
							fetchByTrackingId.setAgentAction(agentActionStr);
							sendotpservice.save(fetchByTrackingId);

//							if (status.equalsIgnoreCase("Rejected")) {
//								RefundNtbFd refundNtbFd = new RefundNtbFd();
//								refundNtbFd.setApplicationNo(fetchByTrackingId.getApplicationNo());
//								refundNtbFd.setAccountNo(fetchByTrackingId.getAccountNo());
//								refundNtbFd.setIfsc(fetchByTrackingId.getIfsc());
//								refundNtbFd.setMobileNo(fetchByTrackingId.getMobileNo());
//								refundNtbFd.setName(fetchByTrackingId.getName());
//								refundNtbFd.setPaymentDate(fetchByTrackingId.getPaymentDate());
//								refundNtbFd.setPayuOrderId(fetchByTrackingId.getPayuOrderId());
//								refundNtbFd.setIsRefundDone("N");
//								refundNtbFd.setMihPayid(fetchByTrackingId.getMihPayid());
//								refundNtbFd.setAmount(fetchByTrackingId.getDepositAmount());
//								refundFdService.save(refundNtbFd);
//
//							}

							if (status.equalsIgnoreCase("Approved")) {
								fetchByTrackingId.setVkycResp(Data1.toString());
								fetchByTrackingId.setIsVkycDone("Y");
								fetchByTrackingId.setVkycStatus("completed");
								sendotpservice.save(fetchByTrackingId);
								if (fetchByTrackingId.getIsCifCreated() == null
										|| fetchByTrackingId.getIsCifCreated().equalsIgnoreCase("N")) {
									createCif(fetchByTrackingId, Header);
								}
								if (fetchByTrackingId.getIsCifCreated().equalsIgnoreCase("Y")) {
									if (fetchByTrackingId.getIsFdCreated() == null
											|| fetchByTrackingId.getIsFdCreated().equalsIgnoreCase("N")) {
										createFd(fetchByTrackingId, Header);
									}
								}
							}

						}
						logger.debug(" Croan Data 2 " + Data2.toString());

					}
				}
			}
		} catch (Exception e) {
			logger.debug("Exception " + e.getMessage());
		}
		JSONObject response = new JSONObject();
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}

//	private JSONObject paymentTransactionPush(FdOpeningNTB fd) {
//
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//		String transactionDate = (fd.getPaymentDate()).format(dateTimeFormatter);
//		JSONObject payload = new JSONObject();
//		JSONObject data = new JSONObject();
//		data.put("TransactionAmount", fd.getDepositAmount());
//		data.put("MobileNumber", fd.getMobileNo());
//		data.put("PGReferenceId", fd.getMihPayid());
//		data.put("ChannelCode", "WNT");
//		data.put("SettlementIndicator", "Y");
//		data.put("TransactionDate", transactionDate);
//		payload.put("Data", data);
//
//		JSONObject resObj = refundFdService.paymentTransactionPushSer(payload);
//		return resObj;
//
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
//			sendotpservice.save(fd);
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
			    sendotpservice.save(fd);
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
			sendotpservice.save(fd);

	    } catch (Exception e) {
	        logger.error("Exception during CIF creation for FD: {}", fd.getApplicationNo(), e);
	        fd.setIsCifCreated("N");
	        sendotpservice.save(fd);
	    }
//	        if (cifCreation != null) {
//	            logger.debug("CIF Creation Response: {}", cifCreation.toString());
//
//	            JSONObject Data3 = new JSONObject(cifCreation.getString("data"));
//	            String matchStatus = Data3.getJSONObject("Data").optString("MatchStatus",null);
//	            if (Data3.has("Data") && "NoMatch".equalsIgnoreCase(matchStatus)) {
//	                fd.setIsCifCreated("Y");
//	                logger.debug("CIF created successfully for FD: {}", fd.getApplicationNo());
//	                String ucic = Data3.getJSONObject("Data").optString("UCIC",null);
//	                fd.setCifCustomerId(ucic);
//	            } else if (Data3.has("Error")) {
//	                fd.setIsCifCreated("N");
//	                logger.warn("CIF creation failed with Error response for FD: {}, Response: {}", fd.getApplicationNo(), Data3.toString());
//	            }
//
//	            fd.setCifResponse(Data3.toString());
//	            sendotpservice.save(fd);
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
//			sendotpservice.save(fd);
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
				sendotpservice.save(fd);
				return;
			}
			String cifResponse = fd.getCifResponse();
			if(cifResponse == null || cifResponse.trim().isEmpty()) {
	            logger.error("CIF Response is null or empty for FD: {}", fd.getApplicationNo());
	            sendotpservice.save(fd);
	            return;
			}

	        logger.debug("FD Request Payload (before CustomerId update): {}", fdRequest);
	        logger.debug("CIF Response: {}", cifResponse);

	        JSONObject cifRespJson = new JSONObject(cifResponse);
	        JSONObject fdReqInJson = new JSONObject(fdRequest);

	        // Replace CustomerId with UCIC from CIF response
	        fdReqInJson.getJSONObject("Data").put("CustomerId",cifRespJson.getJSONObject("Data").getString("UCIC"));

	        // Add AccountOpenDate if payment date is available
	        if (fd.getPaymentDate() != null) {
	            fdReqInJson.getJSONObject("Data").put("AccountOpenDate",fd.getPaymentDate().toLocalDate().toString());
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
	                    logger.debug("Payment is already done. Triggering payment transaction push for FD: {}", fd.getApplicationNo());
//	                    paymentTransactionPush(fd);
	                }

	            } else if (Data3.has("Error")) {
	                fd.setIsFdCreated("N");
	                logger.warn("FD creation failed with error response for FD: {}", fd.getApplicationNo());
	            }

	            fd.setFdOpeningReq(fdReqInJson.toString());
	            fd.setFdOpeningResp(Data3.toString());

	            logger.debug("FD object after processing: {}", fd);

	            sendotpservice.save(fd);
	            logger.debug("FD record saved for FD: {}", fd.getApplicationNo());

	        } else {
	            logger.error("FD Creation returned NULL response for FD: {}", fd.getApplicationNo());
	        }

	    } catch (Exception e) {
	        logger.error("Exception during FD creation for FD: {}", fd.getApplicationNo(), e);
	        fd.setIsFdCreated("N");
	        sendotpservice.save(fd);
	    }
	}


	private void RefundNtbFdPayment() {
		logger.debug("trigger refundFdPayment Manually start");

//		List<RefundNtbFd> refundFdList = refundFdService.getAllRefundNtbFdList("N");
		 List<FdOpeningNTB> refundNtbFdList = refundFdService.findRefundNtbFdList("Y","N","Y","Y");
		logger.debug(" refundFdList  list :: " + refundNtbFdList.toString());

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
				logger.debug("payment push payload:: " + pushPayload.toString());
				refundFd.setPaymentPushReq(pushPayload.toString());
				JSONObject resObj = refundFdService.paymentTransactionPushSer(pushPayload);
				logger.debug("payment push Response :: " + resObj.toString());
				refundFd.setPaymentPushResp(resObj.toString());

				JSONObject payload = new JSONObject();
				JSONObject data = new JSONObject();

				data.put("AccountNumber", "10000380170005");
				data.put("PGReferenceId", refundFd.getMihPayid());
				data.put("RefundAccountNumber", refundFd.getAccountNo());
				data.put("RefundAccountIfsc", refundFd.getIfsc());
				data.put("CustomerName", refundFd.getName());

				payload.put("Data", data);
				logger.debug(" refund payload :: " + payload.toString());
				refundFd.setRefundReq(payload.toString());
				JSONObject paymentRefundResponse = refundFdService.paymentRefundTransactionRequest(payload);
				logger.debug(" Payment Refund Response :: " + paymentRefundResponse.toString());
				String dataStr = paymentRefundResponse.optString("data", "{}");
				JSONObject innerData = new JSONObject(dataStr);

				if (innerData.has("Data")) {
					refundFd.setIsRefundDone("Y");
					refundFd.setIsActive("N");
					JSONObject successData = innerData.getJSONObject("Data");
					String transactionCode = successData.optString("TransactionCode", "");
					String message = successData.optString("Message", "");

					logger.debug("Refund Success");
					logger.debug("TransactionCode: " + transactionCode);
					logger.debug("Message: " + message);

				} else if (innerData.has("Error")) {
					refundFd.setIsRefundDone("N");

					JSONObject error = innerData.getJSONObject("Error");
					String code = error.optString("Code", "");
					String description = error.optString("Description", "");
					logger.debug("Refund Failed");
					logger.debug("Error Code: " + code);
					logger.debug("Error Description: " + description);

				} else {

					logger.debug("refundFdPayment Unknown response: " + paymentRefundResponse.toString());
				}
				refundFd.setRefundResp(paymentRefundResponse.toString());
				sendotpservice.save(refundFd);
			}
		} else {
			logger.debug("No Refund records found to process");
		}
	}
	
//	WORKING REFUND OLD AS RefundNtbFd Table CODE
//	private void RefundNtbFdPayment() {
//		logger.debug("trigger refundFdPayment Manually start");
//
//		List<RefundNtbFd> refundFdList = refundFdService.getAllRefundNtbFdList("N");
//		logger.debug(" refundFdList  list :: " + refundFdList.toString());
//
//		if (!refundFdList.isEmpty()) {
//			for (RefundNtbFd refundFd : refundFdList) {
//
//				FdOpeningNTB fd = fdservice.fetchByApplicationNo(refundFd.getApplicationNo());
//
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
//			logger.debug("No DMS records found to process");
//		}
//	}

}
