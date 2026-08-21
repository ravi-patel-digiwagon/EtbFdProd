package com.suryoday.EtbFdOpening.Pojo;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbl_fdopening_interestrates")
public class InterestRates {
	@Id
	private int id;
	private String tenure;
	private String interestRateNC;
	private String interestRateSC;
	private LocalDate createdDate;
	private LocalDate updatedDate;
	private String years;
	private String months;
	private String days;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTenure() {
		return tenure;
	}
	public void setTenure(String tenure) {
		this.tenure = tenure;
	}
	public String getInterestRateNC() {
		return interestRateNC;
	}
	public void setInterestRateNC(String interestRateNC) {
		this.interestRateNC = interestRateNC;
	}
	public String getInterestRateSC() {
		return interestRateSC;
	}
	public void setInterestRateSC(String interestRateSC) {
		this.interestRateSC = interestRateSC;
	}
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
	public LocalDate getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(LocalDate updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
	public String getMonths() {
		return months;
	}
	public void setMonths(String months) {
		this.months = months;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	
}
