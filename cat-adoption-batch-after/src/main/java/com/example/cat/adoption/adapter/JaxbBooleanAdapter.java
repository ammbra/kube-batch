package com.example.cat.adoption.adapter;


import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbBooleanAdapter extends XmlAdapter<String, Boolean> {


	@Override
	public Boolean unmarshal(String s) throws Exception {
		return "yes".equals(s) || "on".equals(s) || "in".equals(s);
	}

	@Override
	public String marshal(Boolean b) throws Exception {
		if (b) {
			return "yes";
		}
		return "no";
	}


}