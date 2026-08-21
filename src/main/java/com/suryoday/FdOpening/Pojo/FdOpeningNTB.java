package com.suryoday.FdOpening.Pojo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_fd_opening_ntb")
public class FdOpeningNTB {
    @Id
    private long applicationNo;
    private LocalDateTime createdDate;
    private String mobileNo;
    private LocalDateTime updatedDate;
    private String status;
    @Lob
    private String ekycRequest;
    private String panNo;
    @Lob
    private String ekycResponse;
    @Lob
    private String ekycDetails;
    @Lob
    private String accountDetails;
    @Lob
    private String cifRequest;
    @Lob
    private String cifResponse;
    @Lob
    private String accCreationRequest;
    @Lob
    private String accCreationResponse;
    @Lob
    private String verifyUpiReq;
    @Lob
    private String verifyUpiResp;
    private String isEkyc;
    private String upiId;
    private String isUpiVerify;
    private String isAccountVerify;
    private String custType;
    private String isCifCreated;
    private String isAccCreated;
    private String isPaymentDone;
    @Lob
    private String createOrderResp;
    @Lob
    private String paymentDetails;
    @Lob
    private String vkycReq;
    @Lob
    private String vkycResp;
    private String vkycTrackingId;
    private String isVkycDone;
    @Lob
    private String fdOpeningReq;
    @Lob
    private String fdOpeningResp;
    private String isFdCreated;
    private String depositAccountNo;
    private String depositAmount;
    private String tenure;
    private String maturityAmout;
    private String interestEarned;
    private String roi;
    private String fromAccount;
    private String maturityDate;
    private String isActive = "Y";
    private String name = "";
    private String orderId = "";
    private String accountNo = "";
    private String ifsc = "";
    private String fdAccNo = "";
    private String flowStaus = "";
    private String payuOrderId = "";
    private String mihPayid = "";
    private LocalDateTime paymentDate;
    private String vkycStatus;
    private String lgCode;
    private String isPartialVkyc;
    private String auditorAction;
    private String agentAction;
    private String checkerAction;
    private String amlResp;

    private String isDmsUpload;
    @Lob
    private String dmsUploadResp;
    @Lob
    private String dmsUploadReq;

    private String cifCustomerId;

    @Lob
    private String nomineesDetails;
    private String isNomineeUpdate;

    private String isRefundProcessedApproved;

    private String isRefundDone;

    @Lob
    private String paymentPushReq;
    @Lob
    private String paymentPushResp;

    @Lob
    private String refundResp;
    @Lob
    private String refundReq;


    public String getIsRefundProcessedApproved() {
        return isRefundProcessedApproved;
    }

    public void setIsRefundProcessedApproved(String isRefundProcessedApproved) {
        this.isRefundProcessedApproved = isRefundProcessedApproved;
    }

    public String getPaymentPushReq() {
        return paymentPushReq;
    }

    public void setPaymentPushReq(String paymentPushReq) {
        this.paymentPushReq = paymentPushReq;
    }

    public String getPaymentPushResp() {
        return paymentPushResp;
    }

    public void setPaymentPushResp(String paymentPushResp) {
        this.paymentPushResp = paymentPushResp;
    }

    public String getRefundResp() {
        return refundResp;
    }

    public void setRefundResp(String refundResp) {
        this.refundResp = refundResp;
    }

    public String getRefundReq() {
        return refundReq;
    }

    public void setRefundReq(String refundReq) {
        this.refundReq = refundReq;
    }

    public String getIsRefundDone() {
        return isRefundDone;
    }

    public void setIsRefundDone(String isRefundDone) {
        this.isRefundDone = isRefundDone;
    }

    public String getCheckerAction() {
        return checkerAction;
    }

    public void setCheckerAction(String checkerAction) {
        this.checkerAction = checkerAction;
    }

    public String getMihPayid() {
        return mihPayid;
    }

    public void setMihPayid(String mihPayid) {
        this.mihPayid = mihPayid;
    }

    public String getAmlResp() {
        return amlResp;
    }

    public void setAmlResp(String amlResp) {
        this.amlResp = amlResp;
    }

    public String getCifCustomerId() {
        return cifCustomerId;
    }

    public void setCifCustomerId(String cifCustomerId) {
        this.cifCustomerId = cifCustomerId;
    }

    public String getDmsUploadResp() {
        return dmsUploadResp;
    }

    public void setDmsUploadResp(String dmsUploadResp) {
        this.dmsUploadResp = dmsUploadResp;
    }

    public String getDmsUploadReq() {
        return dmsUploadReq;
    }

    public void setDmsUploadReq(String dmsUploadReq) {
        this.dmsUploadReq = dmsUploadReq;
    }

    public String getIsDmsUpload() {
        return isDmsUpload;
    }

    public void setIsDmsUpload(String isDmsUpload) {
        this.isDmsUpload = isDmsUpload;
    }

    public String getAuditorAction() {
        return auditorAction;
    }

    public void setAuditorAction(String auditorAction) {
        this.auditorAction = auditorAction;
    }

    public String getAgentAction() {
        return agentAction;
    }

    public void setAgentAction(String agentAction) {
        this.agentAction = agentAction;
    }

    public String getIsPartialVkyc() {
        return isPartialVkyc;
    }

    public void setIsPartialVkyc(String isPartialVkyc) {
        this.isPartialVkyc = isPartialVkyc;
    }

    public String getIsAccountVerify() {
        return isAccountVerify;
    }

    public void setIsAccountVerify(String isAccountVerify) {
        this.isAccountVerify = isAccountVerify;
    }

    public String getLgCode() {
        return lgCode;
    }

    public void setLgCode(String lgCode) {
        this.lgCode = lgCode;
    }

    public long getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(long applicationNo) {
        this.applicationNo = applicationNo;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEkycRequest() {
        return ekycRequest;
    }

    public void setEkycRequest(String ekycRequest) {
        this.ekycRequest = ekycRequest;
    }

    public String getEkycResponse() {
        return ekycResponse;
    }

    public void setEkycResponse(String ekycResponse) {
        this.ekycResponse = ekycResponse;
    }

    public String getEkycDetails() {
        return ekycDetails;
    }

    public void setEkycDetails(String ekycDetails) {
        this.ekycDetails = ekycDetails;
    }

    public String getCifRequest() {
        return cifRequest;
    }

    public void setCifRequest(String cifRequest) {
        this.cifRequest = cifRequest;
    }

    public String getCifResponse() {
        return cifResponse;
    }

    public void setCifResponse(String cifResponse) {
        this.cifResponse = cifResponse;
    }

    public String getAccCreationRequest() {
        return accCreationRequest;
    }

    public void setAccCreationRequest(String accCreationRequest) {
        this.accCreationRequest = accCreationRequest;
    }

    public String getAccCreationResponse() {
        return accCreationResponse;
    }

    public void setAccCreationResponse(String accCreationResponse) {
        this.accCreationResponse = accCreationResponse;
    }

    public String getIsEkyc() {
        return isEkyc;
    }

    public void setIsEkyc(String isEkyc) {
        this.isEkyc = isEkyc;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(String accountDetails) {
        this.accountDetails = accountDetails;
    }

    public String getVerifyUpiReq() {
        return verifyUpiReq;
    }

    public void setVerifyUpiReq(String verifyUpiReq) {
        this.verifyUpiReq = verifyUpiReq;
    }

    public String getVerifyUpiResp() {
        return verifyUpiResp;
    }

    public void setVerifyUpiResp(String verifyUpiResp) {
        this.verifyUpiResp = verifyUpiResp;
    }

    public String getIsUpiVerify() {
        return isUpiVerify;
    }

    public void setIsUpiVerify(String isUpiVerify) {
        this.isUpiVerify = isUpiVerify;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getIsCifCreated() {
        return isCifCreated;
    }

    public void setIsCifCreated(String isCifCreated) {
        this.isCifCreated = isCifCreated;
    }

    public String getIsAccCreated() {
        return isAccCreated;
    }

    public void setIsAccCreated(String isAccCreated) {
        this.isAccCreated = isAccCreated;
    }

    public String getIsPaymentDone() {
        return isPaymentDone;
    }

    public void setIsPaymentDone(String isPaymentDone) {
        this.isPaymentDone = isPaymentDone;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getCreateOrderResp() {
        return createOrderResp;
    }

    public void setCreateOrderResp(String createOrderResp) {
        this.createOrderResp = createOrderResp;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getVkycReq() {
        return vkycReq;
    }

    public void setVkycReq(String vkycReq) {
        this.vkycReq = vkycReq;
    }

    public String getVkycResp() {
        return vkycResp;
    }

    public void setVkycResp(String vkycResp) {
        this.vkycResp = vkycResp;
    }

    public String getVkycTrackingId() {
        return vkycTrackingId;
    }

    public void setVkycTrackingId(String vkycTrackingId) {
        this.vkycTrackingId = vkycTrackingId;
    }

    public String getIsVkycDone() {
        return isVkycDone;
    }

    public void setIsVkycDone(String isVkycDone) {
        this.isVkycDone = isVkycDone;
    }

    public String getFdOpeningReq() {
        return fdOpeningReq;
    }

    public void setFdOpeningReq(String fdOpeningReq) {
        this.fdOpeningReq = fdOpeningReq;
    }

    public String getFdOpeningResp() {
        return fdOpeningResp;
    }

    public void setFdOpeningResp(String fdOpeningResp) {
        this.fdOpeningResp = fdOpeningResp;
    }

    public String getIsFdCreated() {
        return isFdCreated;
    }

    public void setIsFdCreated(String isFdCreated) {
        this.isFdCreated = isFdCreated;
    }

    public String getDepositAccountNo() {
        return depositAccountNo;
    }

    public void setDepositAccountNo(String depositAccountNo) {
        this.depositAccountNo = depositAccountNo;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getMaturityAmout() {
        return maturityAmout;
    }

    public void setMaturityAmout(String maturityAmout) {
        this.maturityAmout = maturityAmout;
    }

    public String getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(String interestEarned) {
        this.interestEarned = interestEarned;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    /**
     * @return the isActive
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * @return the ifsc
     */
    public String getIfsc() {
        return ifsc;
    }

    /**
     * @param ifsc the ifsc to set
     */
    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    /**
     * @return the fdAccNo
     */
    public String getFdAccNo() {
        return fdAccNo;
    }

    /**
     * @param fdAccNo the fdAccNo to set
     */
    public void setFdAccNo(String fdAccNo) {
        this.fdAccNo = fdAccNo;
    }

    /**
     * @return the flowStaus
     */
    public String getFlowStaus() {
        return flowStaus;
    }

    /**
     * @param flowStaus the flowStaus to set
     */
    public void setFlowStaus(String flowStaus) {
        this.flowStaus = flowStaus;
    }

    /**
     * @return the payuOrderId
     */
    public String getPayuOrderId() {
        return payuOrderId;
    }

    /**
     * @param payuOrderId the payuOrderId to set
     */
    public void setPayuOrderId(String payuOrderId) {
        this.payuOrderId = payuOrderId;
    }

    /**
     * @return the paymentDate
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * @param paymentDate the paymentDate to set
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * @return the vkycStatus
     */
    public String getVkycStatus() {
        return vkycStatus;
    }

    /**
     * @param vkycStatus the vkycStatus to set
     */
    public void setVkycStatus(String vkycStatus) {
        this.vkycStatus = vkycStatus;
    }

    public String getNomineesDetails() {
        return nomineesDetails;
    }

    public void setNomineesDetails(String nomineesDetails) {
        this.nomineesDetails = nomineesDetails;
    }

    public String getIsNomineeUpdate() {
        return isNomineeUpdate;
    }

    public void setIsNomineeUpdate(String isNomineeUpdate) {
        this.isNomineeUpdate = isNomineeUpdate;
    }

    @Override
    public String toString() {
        return "FdOpeningNTB{" +
                "applicationNo=" + applicationNo +
                ", createdDate=" + createdDate +
                ", mobileNo='" + mobileNo + '\'' +
                ", updatedDate=" + updatedDate +
                ", status='" + status + '\'' +
                ", ekycRequest='" + ekycRequest + '\'' +
                ", panNo='" + panNo + '\'' +
                ", ekycResponse='" + ekycResponse + '\'' +
                ", ekycDetails='" + ekycDetails + '\'' +
                ", accountDetails='" + accountDetails + '\'' +
                ", cifRequest='" + cifRequest + '\'' +
                ", cifResponse='" + cifResponse + '\'' +
                ", accCreationRequest='" + accCreationRequest + '\'' +
                ", accCreationResponse='" + accCreationResponse + '\'' +
                ", verifyUpiReq='" + verifyUpiReq + '\'' +
                ", verifyUpiResp='" + verifyUpiResp + '\'' +
                ", isEkyc='" + isEkyc + '\'' +
                ", upiId='" + upiId + '\'' +
                ", isUpiVerify='" + isUpiVerify + '\'' +
                ", isAccountVerify='" + isAccountVerify + '\'' +
                ", custType='" + custType + '\'' +
                ", isCifCreated='" + isCifCreated + '\'' +
                ", isAccCreated='" + isAccCreated + '\'' +
                ", isPaymentDone='" + isPaymentDone + '\'' +
                ", createOrderResp='" + createOrderResp + '\'' +
                ", paymentDetails='" + paymentDetails + '\'' +
                ", vkycReq='" + vkycReq + '\'' +
                ", vkycResp='" + vkycResp + '\'' +
                ", vkycTrackingId='" + vkycTrackingId + '\'' +
                ", isVkycDone='" + isVkycDone + '\'' +
                ", fdOpeningReq='" + fdOpeningReq + '\'' +
                ", fdOpeningResp='" + fdOpeningResp + '\'' +
                ", isFdCreated='" + isFdCreated + '\'' +
                ", depositAccountNo='" + depositAccountNo + '\'' +
                ", depositAmount='" + depositAmount + '\'' +
                ", tenure='" + tenure + '\'' +
                ", maturityAmout='" + maturityAmout + '\'' +
                ", interestEarned='" + interestEarned + '\'' +
                ", roi='" + roi + '\'' +
                ", fromAccount='" + fromAccount + '\'' +
                ", maturityDate='" + maturityDate + '\'' +
                ", isActive='" + isActive + '\'' +
                ", name='" + name + '\'' +
                ", orderId='" + orderId + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", ifsc='" + ifsc + '\'' +
                ", fdAccNo='" + fdAccNo + '\'' +
                ", flowStaus='" + flowStaus + '\'' +
                ", payuOrderId='" + payuOrderId + '\'' +
                ", mihPayid='" + mihPayid + '\'' +
                ", paymentDate=" + paymentDate +
                ", vkycStatus='" + vkycStatus + '\'' +
                ", lgCode='" + lgCode + '\'' +
                ", isPartialVkyc='" + isPartialVkyc + '\'' +
                ", auditorAction='" + auditorAction + '\'' +
                ", agentAction='" + agentAction + '\'' +
                ", checkerAction='" + checkerAction + '\'' +
                ", amlResp='" + amlResp + '\'' +
                ", isDmsUpload='" + isDmsUpload + '\'' +
                ", dmsUploadResp='" + dmsUploadResp + '\'' +
                ", dmsUploadReq='" + dmsUploadReq + '\'' +
                ", cifCustomerId='" + cifCustomerId + '\'' +
                ", nomineesDetails='" + nomineesDetails + '\'' +
                ", isNomineeUpdate='" + isNomineeUpdate + '\'' +
                ", isRefundProcessedApproved='" + isRefundProcessedApproved + '\'' +
                ", isRefundDone='" + isRefundDone + '\'' +
                ", paymentPushReq='" + paymentPushReq + '\'' +
                ", paymentPushResp='" + paymentPushResp + '\'' +
                ", refundResp='" + refundResp + '\'' +
                ", refundReq='" + refundReq + '\'' +
                '}';
    }
}
