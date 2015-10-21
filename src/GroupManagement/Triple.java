package GroupManagement;

import java.io.Serializable;
import java.net.InetAddress;

	public class Triple implements Serializable{

		private int clientID;
		private String username;
		private InetAddress ip;
		private String group = null;

		public Triple (int clientID, String userName, InetAddress ip){
			this.clientID = clientID;
			this.username = userName;
			this.ip = ip;
		}

		public int getClientID() {
			return clientID;
		}

		public void setClientID(int clientID) {
			this.clientID = clientID;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public InetAddress getIp() {
			return ip;
		}

		public void setIp(InetAddress ip) {
			this.ip = ip;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}
	}