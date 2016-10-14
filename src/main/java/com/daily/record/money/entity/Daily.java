package com.daily.record.money.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class Daily implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3745247153723999743L;
	private Integer date;
	private Integer id;
	private BigDecimal breakfast;
	private BigDecimal lunch;
	private BigDecimal dinner;
	private BigDecimal totle_shuai;
	private BigDecimal totle_notshuai;
	private BigDecimal other;
	private String operation;
	private Integer index;
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getBreakfast() {
		return breakfast;
	}
	public void setBreakfast(BigDecimal breakfast) {
		this.breakfast = breakfast;
	}
	public BigDecimal getLunch() {
		return lunch;
	}
	public void setLunch(BigDecimal lunch) {
		this.lunch = lunch;
	}
	public BigDecimal getDinner() {
		return dinner;
	}
	public void setDinner(BigDecimal dinner) {
		this.dinner = dinner;
	}
	public BigDecimal getTotle_shuai() {
		return totle_shuai;
	}
	public void setTotle_shuai(BigDecimal totle_shuai) {
		this.totle_shuai = totle_shuai;
	}
	public BigDecimal getTotle_notshuai() {
		return totle_notshuai;
	}
	public void setTotle_notshuai(BigDecimal totle_notshuai) {
		this.totle_notshuai = totle_notshuai;
	}
	public BigDecimal getOther() {
		return other;
	}
	public void setOther(BigDecimal other) {
		this.other = other;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
}
