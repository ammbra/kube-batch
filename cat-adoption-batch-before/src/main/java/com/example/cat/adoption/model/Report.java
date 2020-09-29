package com.example.cat.adoption.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.example.cat.adoption.adapter.JaxbBooleanAdapter;
import com.example.cat.adoption.adapter.JaxbDateAdapter;

@XmlRootElement(name = "record")
public class Report {

	private int refId;
	private String name;
	private int age;
	private Date dob;
	private Boolean fivPositive;

	@XmlAttribute(name = "refId")
	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	@XmlElement(name = "age")
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlJavaTypeAdapter(JaxbDateAdapter.class)
	@XmlElement
	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@XmlJavaTypeAdapter(JaxbBooleanAdapter.class)
	@XmlElement
	public Boolean getFivPositive() {
		return fivPositive;
	}

	public void setFivPositive(Boolean fivPositive) {
		this.fivPositive = fivPositive;
	}

	public String getCsvDob() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(getDob());

	}

}