package com.suryoday.EtbFdOpening.Pojo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="tbl_fd_opening")
@Data
@ToString
public class FdOpening {
	@Id
	private long id;
	private String mobileNo;
	private String sessionId;
	private String depositAccountNo;
	private String depositAmount;
	private String tenure;
	private String maturityAmout;
	private String interestEarned;
	private String cifNo;
	private String roi;
	private String fromAccount;
	private String maturityDate;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private String status;
	@Lob
	private String FdRequest;
	@Lob
	private String FdResponse;
	private String custType;
	private String upiId;
	private String isUpiVerify;
	private String productCode;
	
	@Lob
	private String verifyUpiReq;
	@Lob
	private String verifyUpiResp;

	@Lob
	private String nomineeDetails;
	private String isPaymentDone;
	@Lob
	private String createOrderResp;
	@Lob
	private String paymentDetails;
	private String isAccountVerify;
	private String accountNo;
	private String ifsc;
	private String payuOrderId;
	private LocalDateTime paymentDate;
	private String mihPayid;
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
//	public String getMobileNo() {
//		return mobileNo;
//	}
//	public void setMobileNo(String mobileNo) {
//		this.mobileNo = mobileNo;
//	}
//	public String getSessionId() {
//		return sessionId;
//	}
//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//	}
//	public LocalDateTime getCreatedDate() {
//		return createdDate;
//	}
//	public void setCreatedDate(LocalDateTime createdDate) {
//		this.createdDate = createdDate;
//	}
//	public LocalDateTime getUpdatedDate() {
//		return updatedDate;
//	}
//	public void setUpdatedDate(LocalDateTime updatedDate) {
//		this.updatedDate = updatedDate;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	public String getDepositAccountNo() {
//		return depositAccountNo;
//	}
//	public void setDepositAccountNo(String depositAccountNo) {
//		this.depositAccountNo = depositAccountNo;
//	}
//	public String getDepositAmount() {
//		return depositAmount;
//	}
//	public void setDepositAmount(String depositAmount) {
//		this.depositAmount = depositAmount;
//	}
//	public String getTenure() {
//		return tenure;
//	}
//	public void setTenure(String tenure) {
//		this.tenure = tenure;
//	}
//	public String getMaturityAmout() {
//		return maturityAmout;
//	}
//	public void setMaturityAmout(String maturityAmout) {
//		this.maturityAmout = maturityAmout;
//	}
//	public String getInterestEarned() {
//		return interestEarned;
//	}
//	public void setInterestEarned(String interestEarned) {
//		this.interestEarned = interestEarned;
//	}
//	public String getRoi() {
//		return roi;
//	}
//	public void setRoi(String roi) {
//		this.roi = roi;
//	}
//	public String getFromAccount() {
//		return fromAccount;
//	}
//	public void setFromAccount(String fromAccount) {
//		this.fromAccount = fromAccount;
//	}
//	public String getMaturityDate() {
//		return maturityDate;
//	}
//	public void setMaturityDate(String maturityDate) {
//		this.maturityDate = maturityDate;
//	}
//
//	public String getFdRequest() {
//		return FdRequest;
//	}
//	public void setFdRequest(String fdRequest) {
//		FdRequest = fdRequest;
//	}
//	public String getFdResponse() {
//		return FdResponse;
//	}
//	public void setFdResponse(String fdResponse) {
//		FdResponse = fdResponse;
//	}
//	public String getCustType() {
//		return custType;
//	}
//	public void setCustType(String custType) {
//		this.custType = custType;
//	}
//	/**
//	 * @return the upiId
//	 */
//	public String getUpiId() {
//		return upiId;
//	}
//	/**
//	 * @param upiId the upiId to set
//	 */
//	public void setUpiId(String upiId) {
//		this.upiId = upiId;
//	}
//	/**
//	 * @return the isUpiVerify
//	 */
//	public String getIsUpiVerify() {
//		return isUpiVerify;
//	}
//	/**
//	 * @param isUpiVerify the isUpiVerify to set
//	 */
//	public void setIsUpiVerify(String isUpiVerify) {
//		this.isUpiVerify = isUpiVerify;
//	}
//	/**
//	 * @return the verifyUpiReq
//	 */
//	public String getVerifyUpiReq() {
//		return verifyUpiReq;
//	}
//	/**
//	 * @param verifyUpiReq the verifyUpiReq to set
//	 */
//	public void setVerifyUpiReq(String verifyUpiReq) {
//		this.verifyUpiReq = verifyUpiReq;
//	}
//	/**
//	 * @return the verifyUpiResp
//	 */
//	public String getVerifyUpiResp() {
//		return verifyUpiResp;
//	}
//	/**
//	 * @param verifyUpiResp the verifyUpiResp to set
//	 */
//	public void setVerifyUpiResp(String verifyUpiResp) {
//		this.verifyUpiResp = verifyUpiResp;
//	}
//	/**
//	 * @return the isPaymentDone
//	 */
//	public String getIsPaymentDone() {
//		return isPaymentDone;
//	}
//	/**
//	 * @param isPaymentDone the isPaymentDone to set
//	 */
//	public void setIsPaymentDone(String isPaymentDone) {
//		this.isPaymentDone = isPaymentDone;
//	}
//	/**
//	 * @return the createOrderResp
//	 */
//	public String getCreateOrderResp() {
//		return createOrderResp;
//	}
//	/**
//	 * @param createOrderResp the createOrderResp to set
//	 */
//	public void setCreateOrderResp(String createOrderResp) {
//		this.createOrderResp = createOrderResp;
//	}
//
//	/**
//	 * @return the paymentDetails
//	 */
//	public String getPaymentDetails() {
//		return paymentDetails;
//	}
//	/**
//	 * @param paymentDetails the paymentDetails to set
//	 */
//	public void setPaymentDetails(String paymentDetails) {
//		this.paymentDetails = paymentDetails;
//	}
//	@Override
//	public String toString() {
//		return "FdOpening [id=" + id + ", mobileNo=" + mobileNo + ", sessionId=" + sessionId + ", depositAccountNo="
//				+ depositAccountNo + ", depositAmount=" + depositAmount + ", tenure=" + tenure + ", maturityAmout="
//				+ maturityAmout + ", interestEarned=" + interestEarned + ", roi=" + roi + ", fromAccount=" + fromAccount
//				+ ", maturityDate=" + maturityDate + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate
//				+ ", status=" + status + ", FdRequest=" + FdRequest + ", FdResponse=" + FdResponse + ", custType="
//				+ custType + ", upiId=" + upiId + ", isUpiVerify=" + isUpiVerify + ", verifyUpiReq=" + verifyUpiReq
//				+ ", verifyUpiResp=" + verifyUpiResp + ", isPaymentDone=" + isPaymentDone + ", createOrderResp="
//				+ createOrderResp + ", paymentDetails=" + paymentDetails + "]";
//	}
	
	
}
