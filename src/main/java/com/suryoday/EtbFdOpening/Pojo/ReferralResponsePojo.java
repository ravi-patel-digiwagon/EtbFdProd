package com.suryoday.EtbFdOpening.Pojo;

public class ReferralResponsePojo {
	public String AgentType;
	public String No_of_Agents_onboarded;
	public String Count_of_Referrals;
	public String No_of_Disbursements;
	public String Amount_of_Disbursement;
	public String Count_of_Inactive_Agent;
	public String getAgentType() {
		return AgentType;
	}
	public void setAgentType(String agentType) {
		AgentType = agentType;
	}
	public String getNo_of_Agents_onboarded() {
		return No_of_Agents_onboarded;
	}
	public void setNo_of_Agents_onboarded(String no_of_Agents_onboarded) {
		No_of_Agents_onboarded = no_of_Agents_onboarded;
	}
	public String getCount_of_Referrals() {
		return Count_of_Referrals;
	}
	public void setCount_of_Referrals(String count_of_Referrals) {
		Count_of_Referrals = count_of_Referrals;
	}
	public String getNo_of_Disbursements() {
		return No_of_Disbursements;
	}
	public void setNo_of_Disbursements(String no_of_Disbursements) {
		No_of_Disbursements = no_of_Disbursements;
	}
	public String getAmount_of_Disbursement() {
		return Amount_of_Disbursement;
	}
	public void setAmount_of_Disbursement(String amount_of_Disbursement) {
		Amount_of_Disbursement = amount_of_Disbursement;
	}
	
	public String getCount_of_Inactive_Agent() {
		return Count_of_Inactive_Agent;
	}
	public void setCount_of_Inactive_Agent(String count_of_Inactive_Agent) {
		Count_of_Inactive_Agent = count_of_Inactive_Agent;
	}
	@Override
	public String toString() {
		return "ReferralResponsePojo [AgentType=" + AgentType + ", No_of_Agents_onboarded=" + No_of_Agents_onboarded
				+ ", Count_of_Referrals=" + Count_of_Referrals + ", No_of_Disbursements=" + No_of_Disbursements
				+ ", Amount_of_Disbursement=" + Amount_of_Disbursement + ", Count_of_Inactive_Agent="
				+ Count_of_Inactive_Agent + "]";
	}
		
}
