
package application;

import grpcDatenpakete.PeerClientGrpc;
import io.grpc.ManagedChannel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Peer {
	private static int idCount=1;
	private SimpleIntegerProperty id;
	private SimpleStringProperty ip;
	private SimpleStringProperty port;
	private SimpleStringProperty socket;
	private SimpleStringProperty name;
	private SimpleStringProperty hostName;
	private SimpleStringProperty os;
	private SimpleStringProperty publicKey;
	private SimpleStringProperty endian;
	private ManagedChannel gRPCPeerVerbindung;
	PeerClientGrpc.PeerClientStub PeerClientStub;
	private int peerSendCounter=0;
	private long lastHeartBeat=-1;

	
	public Peer(){
		
	}
	
	public Peer(int id, String ip, String port, String name, String hostName, String os, String publicKey, boolean endian) {
		super();
		this.id =new SimpleIntegerProperty(id);
		this.ip = new SimpleStringProperty(ip);
		this.port = new SimpleStringProperty(port);
		this.socket = new SimpleStringProperty(ip+":"+port);
		this.name = new SimpleStringProperty(name);
		this.hostName = new SimpleStringProperty(hostName);
		this.os = new SimpleStringProperty(os);
		this.publicKey = new SimpleStringProperty(publicKey);
		if(endian){
			this.endian=new SimpleStringProperty("Big Endian");
		}else{
			this.endian=new SimpleStringProperty("Little Endian");
		}
	}
	
	private void socket(){
		socket = new SimpleStringProperty(ip.get() + ":"+ port.get());
	}
	
	public String getIp() {
		socket();
		return ip.get();
	}
	public void setIp(String ip) {
		this.ip = new SimpleStringProperty(ip);
	}
	public String getPort() {
		socket();
		return port.get();
	}
	public void setPort(String port) {
		this.port = new SimpleStringProperty(port);
	}
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name = new SimpleStringProperty(name);
	}
	
	public String getHostName() {
		return hostName.get();
	}
	public void setHostName(String hostName) {
		this.hostName = new SimpleStringProperty(hostName);
	}
	
	public String getEndian() {
		return hostName.get();
	}
	public void setEndian(boolean endian) {
		if(endian){
			this.endian = new SimpleStringProperty("Big Endian");
		}else{
			this.endian = new SimpleStringProperty("Little Endian");
		}
	}
	
	public void setEndian(String endian) {
		this.endian = new SimpleStringProperty(endian);
	}
	
	public String getPublicKey() {
		return publicKey.get();
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = new SimpleStringProperty(publicKey);
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id = new SimpleIntegerProperty(id);
	}

	public String getSocket() {
		return socket.get();
	}

	public void setSocket(String socket) {
		this.socket = new SimpleStringProperty(socket);
	}

	public String getOs() {
		return os.get();
	}

	public void setOs(String os) {
		this.os = new SimpleStringProperty(os);
	}

	public void setIp(SimpleStringProperty ip) {
		this.ip = ip;
	}

	public void setPort(SimpleStringProperty port) {
		this.port = port;
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public void setOs(SimpleStringProperty os) {
		this.os = os;
	}

	public void setPublicKey(SimpleStringProperty publicKey) {
		this.publicKey = publicKey;
	}
	
	public ManagedChannel getgRPCPeerVerbindung() {
		return gRPCPeerVerbindung;
	}

	public void setgRPCPeerVerbindungList(ManagedChannel gRPCPeerVerbindung) {
		this.gRPCPeerVerbindung = gRPCPeerVerbindung;
	}

	public void setgRPCPeerVerbindung(ManagedChannel gRPCPeerVerbindung) {
		this.gRPCPeerVerbindung = gRPCPeerVerbindung;
	}

	public PeerClientGrpc.PeerClientStub getPeerClientStub() {
		return PeerClientStub;
	}

	public void setPeerClientStub(PeerClientGrpc.PeerClientStub peerClientStub) {
		PeerClientStub = peerClientStub;
	}

	public static int getIdCount() {
		return idCount;
	}

	public static void setIdCount(int idCount) {
		Peer.idCount = idCount;
	}

	public int getPeerSendCounter() {
		return peerSendCounter;
	}

	public void setPeerSendCounter(int peerSendCounter) {
		this.peerSendCounter = peerSendCounter;
	}

	public void setId(SimpleIntegerProperty id) {
		this.id = id;
	}

	public void setSocket(SimpleStringProperty socket) {
		this.socket = socket;
	}

	public long getLastHeartBeat() {
		return lastHeartBeat;
	}

	public void setLastHeartBeat(long lastHeartBeat) {
		this.lastHeartBeat = lastHeartBeat;
	}



}
