package com.redhat.jenkins.plugins.bayesian;

import java.util.List;

public class Codebase {
    List <Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    
    static class Data{
        Attributes attributes;

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public String toString()
        {
            return getAttributes().getCveScan();
        }

    }
    
    static class Attributes {

        private String cvescan;

        public String getCveScan() {
            return cvescan;
        }

        public void setCveScan(String cvescan) {
            this.cvescan = cvescan;
        }

    }
}
