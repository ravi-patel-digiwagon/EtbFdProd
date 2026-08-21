package com.suryoday.EtbFdOpening.Pojo;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
@Entity
@Table(name="Merchant_Consent_Details")
public class MerchantTCDetails {
	@Id
	private long applicationNo;
	private String mobileNo;
	private String customerId;
	private String name;
	private String accountNo;
	@Lob
	private byte[] merchantPdf;
	@Lob
	private String dmsUploadReq="";
	@Lob
	private String dmsUploadResp="";
//	private boolean isDmsUpload=false;
	private LocalDateTime createdDate=LocalDateTime.now();
	
	public MerchantTCDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MerchantTCDetails(long applicationNo, String mobileNo, String customerId, String name, String accountNo,
			byte[] merchantPdf, LocalDateTime createdDate) {
		super();
		this.applicationNo = applicationNo;
		this.mobileNo = mobileNo;
		this.customerId = customerId;
		this.name = name;
		this.accountNo = accountNo;
		this.merchantPdf = merchantPdf;
		this.createdDate = createdDate;
	}

	/**
	 * @return the applicationNo
	 */
	public long getApplicationNo() {
		return applicationNo;
	}
	/**
	 * @param applicationNo the applicationNo to set
	 */
	public void setApplicationNo(long applicationNo) {
		this.applicationNo = applicationNo;
	}
	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}
	/**
	 * @param mobileNo the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
	 * @return the merchantPdf
	 */
	public byte[] getMerchantPdf() {
		return merchantPdf;
	}
	/**
	 * @param merchantPdf the merchantPdf to set
	 */
	public void setMerchantPdf(byte[] merchantPdf) {
		this.merchantPdf = merchantPdf;
	}
	/**
	 * @return the createdDate
	 */
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
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
	 * @return the dmsUploadReq
	 */
	public String getDmsUploadReq() {
		return dmsUploadReq;
	}

	/**
	 * @param dmsUploadReq the dmsUploadReq to set
	 */
	public void setDmsUploadReq(String dmsUploadReq) {
		this.dmsUploadReq = dmsUploadReq;
	}

	/**
	 * @return the dmsUploadResp
	 */
	public String getDmsUploadResp() {
		return dmsUploadResp;
	}

	/**
	 * @param dmsUploadResp the dmsUploadResp to set
	 */
	public void setDmsUploadResp(String dmsUploadResp) {
		this.dmsUploadResp = dmsUploadResp;
	}

	/**
	 * @return the isDmsUpload
	 */
//	public boolean isDmsUpload() {
//		return isDmsUpload;
//	}

	/**
	 * @param isDmsUpload the isDmsUpload to set
	 */
//	public void setDmsUpload(boolean isDmsUpload) {
//		this.isDmsUpload = isDmsUpload;
//	}

	@Override
	public String toString() {
		return "MerchantTCDetails [applicationNo=" + applicationNo + ", mobileNo=" + mobileNo + ", customerId="
				+ customerId + ", name=" + name + ", accountNo=" + accountNo + ", merchantPdf="
				+ Arrays.toString(merchantPdf) + ", dmsUploadReq=" + dmsUploadReq + ", dmsUploadResp=" + dmsUploadResp
				 + ", createdDate=" + createdDate + "]";
	}

	
	
	
	
}
