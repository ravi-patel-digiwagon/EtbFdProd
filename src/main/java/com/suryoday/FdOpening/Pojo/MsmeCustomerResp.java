package com.suryoday.FdOpening.Pojo;

public class MsmeCustomerResp {
	private String agentType;
	private String agentName;
	private String agentMobile;
	private String custAppNo;
	private String custName;
	private String custMobNo;
	private String custBranchCode;
	private String custBranchName;
	private String applicationStatus;
	private String dateOfCustomerLogin;
	private String dateOfSanction;
	private String disbDate;
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentMobile() {
		return agentMobile;
	}
	public void setAgentMobile(String agentMobile) {
		this.agentMobile = agentMobile;
	}
	public String getCustAppNo() {
		return custAppNo;
	}
	public void setCustAppNo(String custAppNo) {
		this.custAppNo = custAppNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustMobNo() {
		return custMobNo;
	}
	public void setCustMobNo(String custMobNo) {
		this.custMobNo = custMobNo;
	}
	public String getCustBranchCode() {
		return custBranchCode;
	}
	public void setCustBranchCode(String custBranchCode) {
		this.custBranchCode = custBranchCode;
	}
	public String getCustBranchName() {
		return custBranchName;
	}
	public void setCustBranchName(String custBranchName) {
		this.custBranchName = custBranchName;
	}
	public String getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public String getDateOfCustomerLogin() {
		return dateOfCustomerLogin;
	}
	public void setDateOfCustomerLogin(String dateOfCustomerLogin) {
		this.dateOfCustomerLogin = dateOfCustomerLogin;
	}
	
	public String getDateOfSanction() {
		return dateOfSanction;
	}
	public void setDateOfSanction(String dateOfSanction) {
		this.dateOfSanction = dateOfSanction;
	}
	public String getDisbDate() {
		return disbDate;
	}
	public void setDisbDate(String disbDate) {
		this.disbDate = disbDate;
	}
	@Override
	public String toString() {
		return "MsmeCustomerResp [agentType=" + agentType + ", agentName=" + agentName + ", agentMobile=" + agentMobile
				+ ", custAppNo=" + custAppNo + ", custName=" + custName + ", custMobNo=" + custMobNo
				+ ", custBranchCode=" + custBranchCode + ", custBranchName=" + custBranchName + ", applicationStatus="
				+ applicationStatus + ", dateOfCustomerLogin=" + dateOfCustomerLogin + ", dateOfSanction="
				+ dateOfSanction + ", disbDate=" + disbDate + "]";
	}
	
	
	
	
}
