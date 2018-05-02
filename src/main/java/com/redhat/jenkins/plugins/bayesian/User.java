package com.redhat.jenkins.plugins.bayesian;


import java.util.List;


public class User {
	
	List <Data> data;
	
	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}

	class Attributes {
		public String email;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}			
		
	}
    
	class Data{
		Attributes attributes;

		public Attributes getAttributes() {
			return attributes;
		}

		public void setAttributes(Attributes attributes) {
			this.attributes = attributes;
		}	
		
		public String toString()
		{
			return getAttributes().getEmail();
		}
		
	}
}