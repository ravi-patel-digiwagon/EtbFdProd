package com.suryoday.EtbFdOpening.Pojo;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="tbl_craft_merchant")
public class CraftMerchantDetails {
	@Id
	private String mobNo;
	private String accountNo;
	private String aadharNo;
	private String aadharVerify;
	private String ifsc;
	private String corporateBc;
	@Lob
	private String remarks;
	@Lob
	private String leegalityReq;
	@Lob
	private String leegalityResp;
	@Lob
	private String fetchLeegalityResp;
	private String documentId;
	@Lob
	private byte[] qr;
	private String isQrGenerated;
	private String applicationNo;
	private String faceMatchResp;
	private String isFaceMatches;
	private String state;
	private String referredEmployeId;
	private String lgCode="RefferalCampaign";
	private LocalDateTime createdDate=LocalDateTime.now();
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAadharNo() {
		return aadharNo;
	}
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}
	public String getAadharVerify() {
		return aadharVerify;
	}
	public void setAadharVerify(String aadharVerify) {
		this.aadharVerify = aadharVerify;
	}
	public String getIfsc() {
		return ifsc;
	}
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	public String getLeegalityReq() {
		return leegalityReq;
	}
	public void setLeegalityReq(String leegalityReq) {
		this.leegalityReq = leegalityReq;
	}
	public String getLeegalityResp() {
		return leegalityResp;
	}
	public void setLeegalityResp(String leegalityResp) {
		this.leegalityResp = leegalityResp;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getIsQrGenerated() {
		return isQrGenerated;
	}
	public void setIsQrGenerated(String isQrGenerated) {
		this.isQrGenerated = isQrGenerated;
	}
	public byte[] getQr() {
		return qr;
	}
	public void setQr(byte[] qr) {
		this.qr = qr;
	}
	public String getFetchLeegalityResp() {
		return fetchLeegalityResp;
	}
	public void setFetchLeegalityResp(String fetchLeegalityResp) {
		this.fetchLeegalityResp = fetchLeegalityResp;
	}
	public String getCorporateBc() {
		return corporateBc;
	}
	public void setCorporateBc(String corporateBc) {
		this.corporateBc = corporateBc;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}
	public String getFaceMatchResp() {
		return faceMatchResp;
	}
	public void setFaceMatchResp(String faceMatchResp) {
		this.faceMatchResp = faceMatchResp;
	}
	public String getIsFaceMatches() {
		return isFaceMatches;
	}
	public void setIsFaceMatches(String isFaceMatches) {
		this.isFaceMatches = isFaceMatches;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getReferredEmployeId() {
		return referredEmployeId;
	}
	public void setReferredEmployeId(String referredEmployeId) {
		this.referredEmployeId = referredEmployeId;
	}
	public String getLgCode() {
		return lgCode;
	}
	public void setLgCode(String lgCode) {
		this.lgCode = lgCode;
	}
	@Override
	public String toString() {
		return "CraftMerchantDetails [mobNo=" + mobNo + ", accountNo=" + accountNo + ", aadharNo=" + aadharNo
				+ ", aadharVerify=" + aadharVerify + ", ifsc=" + ifsc + ", corporateBc=" + corporateBc + ", remarks="
				+ remarks + ", leegalityReq=" + leegalityReq + ", leegalityResp=" + leegalityResp
				+ ", fetchLeegalityResp=" + fetchLeegalityResp + ", documentId=" + documentId + ", qr="
				+ Arrays.toString(qr) + ", isQrGenerated=" + isQrGenerated + ", applicationNo=" + applicationNo
				+ ", faceMatchResp=" + faceMatchResp + ", isFaceMatches=" + isFaceMatches + ", state=" + state
				+ ", referredEmployeId=" + referredEmployeId + ", lgCode=" + lgCode + ", createdDate=" + createdDate
				+ "]";
	}
	
}
