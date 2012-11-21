package org.comtor.comtoradmin;

public class User {
        private String apikey = new String();
        private String email = new String();
        private String host = new String();
        private String date = new String();
        private String ip = new String();

        public void setApikey(String apikey) {
                this.apikey = apikey;
        }

        public String getApikey() {
                return apikey;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getEmail() {
                return email;
        }

        public void setHost(String host) {
                this.host = host;
        }

        public String getHost() {
                return host;
        }
        
        public void setDate(String date) {
            	this.date = date;;
        }
        
        public String getDate() {
            	return date;
        }
        
        public void setIp(String ip) {
        	this.ip = ip;;
	    }
        
	    public String getIp() {
	        	return ip;
	    }
}
