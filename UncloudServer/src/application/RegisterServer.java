package application;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import grpcDatenpakete.PeerClientGrpc;
import grpcDatenpakete.PeerClientGrpc.PeerClientStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class RegisterServer extends grpcDatenpakete.registerServerGrpc.registerServerImplBase implements Runnable {
	public static int port = 10010;
	private static ArrayList<Peer> PeerListe = new ArrayList<>();
	private static ArrayList<io.grpc.stub.StreamObserver<grpcDatenpakete.Peer>> ObserverListe = new ArrayList<>();
	public static Server registerServer;
	private String control;
	private String hbIP;
	private String fehlermeldung;
	private Peer hbPeer;
	private static String serverInitStatus="";
	private static Lock peerListeLock = new ReentrantLock();
	
	private static boolean addPeerByPush = true;
	private static boolean delPeerByPush = false;

	public RegisterServer() {
	}

	public RegisterServer(String ctrl) {
		if(ctrl.length()>1){
			hbIP = ctrl.substring(1, ctrl.length());
			control = ctrl.substring(0,1);
		}else{
			control = ctrl;
		}
	}

	@Override
	public void registrieren(grpcDatenpakete.Peer request,
	        io.grpc.stub.StreamObserver<grpcDatenpakete.Peer> responseObserver) {
		SimpleDateFormat sdfmt = new SimpleDateFormat();
		sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
		System.out.println( "Client " + request.getIp() + " registrierung erhalten: " + sdfmt.format(new Date()) );
		boolean gefunden = false;
		peerListeLock.lock();
		for (Peer peers : PeerListe) {
			if (request.getIp().equals(peers.getIp())) {
				gefunden = true;
				System.out.println("Ein Client mit dieser IP existiert schon in der Liste.");
				break;
			}
		}
		
		if (!gefunden) {
			Peer newPeer = new Peer(Peer.getIdCount(),request.getIp(),String.valueOf(request.getPort()),request.getName(),request.getHostname(),request.getOs(),request.getPublicKey(),request.getEndian());
			Peer.setIdCount(Peer.getIdCount()+1);
			ServerController sc = new ServerController();
			sc.setTableView(newPeer);
			PeerListe.add(newPeer);
			ObserverListe.add(responseObserver);
			peerListeLock.unlock();
			pushPeers(addPeerByPush, newPeer);
			pushPeersRemove(addPeerByPush, request);
			System.out.println("Client der Liste hinzugef√ºgt!");
			Thread heartBeat = new Thread(new RegisterServer("h"+request.getIp()));
			heartBeat.start();
			return;
		}
		peerListeLock.unlock();
	}
	
	@Override
	public void logout(grpcDatenpakete.Peer request,
	        io.grpc.stub.StreamObserver<grpcDatenpakete.Text> responseObserver) {
		System.out.println("Client logout empfangen!");
		int index=-1;
		boolean gefunden = false;
		grpcDatenpakete.Peer rmPeer=null;
		peerListeLock.lock();
		for (Peer peers : PeerListe) {
			if (request.getIp().equals(peers.getIp())) {
				gefunden = true;
				System.out.println("Client gefunden.");
				index=PeerListe.indexOf(peers);
				rmPeer = grpcDatenpakete.Peer.newBuilder()
						.setId(peers.getId())
						.setId(peers.getId())
						.setIp(peers.getIp())
						.setPort(Integer.parseInt(peers.getPort()))
						.setName(peers.getName())
						.setHostname(peers.getHostName())
						.setEndian(peers.getEndian().equals("Big Endian")?true:false)
						.setOs(peers.getOs())
						.setAddOrDelete(false).build();
				break;
			}
		}
		
		if (gefunden) {
			ServerController sc = new ServerController();
			sc.removeFromTableView(index);
			PeerListe.remove(index);
			
			try{
				ObserverListe.get(index).onCompleted();
			}catch(Exception e){
				
			}
			ObserverListe.remove(index);
			peerListeLock.unlock();
			pushPeersRemove(delPeerByPush,rmPeer);
			System.out.println("Client entfernt!");
		}
		peerListeLock.unlock();
		if(!gefunden){
			System.out.println("Client konnte nicht gefunden werden!");
		}
		responseObserver.onCompleted();
	}
	

	public void logout(String ip) {
		System.out.println("Client logout empfangen!");
		int index=-1;
		boolean gefunden = false;
		grpcDatenpakete.Peer rmPeer=null;
		peerListeLock.lock();
		for (Peer peers : PeerListe) {
			if (ip.equals(peers.getIp())) {
				gefunden = true;
				System.out.println("Client gefunden.");
				index=PeerListe.indexOf(peers);
				rmPeer = grpcDatenpakete.Peer.newBuilder()
						.setId(peers.getId())
						.setId(peers.getId())
						.setIp(peers.getIp())
						.setPort(Integer.parseInt(peers.getPort()))
						.setName(peers.getName())
						.setHostname(peers.getHostName())
						.setEndian(peers.getEndian().equals("Big Endian")?true:false)
						.setOs(peers.getOs())
						.setAddOrDelete(false).build();
				break;
			}
		}
		
		if (gefunden) {
			ServerController sc = new ServerController();
			sc.removeFromTableView(index);
			PeerListe.remove(index);
			try{
				ObserverListe.get(index).onCompleted();
			}catch(Exception e){
				
			}
			ObserverListe.remove(index);
			peerListeLock.unlock();
			pushPeersRemove(delPeerByPush,rmPeer);
			System.out.println("Client entfernt!");
			return;
		}
		peerListeLock.unlock();
		if(!gefunden){
			System.out.println("Client konnte nicht gefunden werden!");
		}
		
	}
	
	public void pushPeers(boolean addOrDelete, Peer peer){
		SimpleDateFormat sdfmt = new SimpleDateFormat();
		sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
		System.out.println( "Server sendet Peers " + sdfmt.format(new Date()) );
		peerListeLock.lock();
		int index = PeerListe.indexOf(peer);
		for(int i=0;i<PeerListe.size();i++){
			if(i!=index){
				System.out.println("sende "+ PeerListe.get(i).getIp() +" an "+ peer.getIp());
				ObserverListe.get(index).onNext(grpcDatenpakete.Peer.newBuilder()
						.setId(PeerListe.get(i).getId())
						.setId(PeerListe.get(i).getId())
						.setIp(PeerListe.get(i).getIp())
						.setPort(Integer.parseInt(PeerListe.get(i).getPort()))
						.setName(PeerListe.get(i).getName())
						.setHostname(PeerListe.get(i).getHostName())
						.setEndian(PeerListe.get(i).getEndian().equals("Big Endian")?true:false)
						.setOs(PeerListe.get(i).getOs())
						.setAddOrDelete(addOrDelete).build());
			}
		}
		peerListeLock.unlock();
	}
	
	public void pushPeersRemove(boolean addOrDelete, grpcDatenpakete.Peer peer){
		SimpleDateFormat sdfmt = new SimpleDateFormat();
		sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
		System.out.println( "Server sendet Peer " + peer.getIp() + (addOrDelete?"hinzuf¸gen ":"entfernen: ") + sdfmt.format(new Date()) );
		peerListeLock.lock();
		if(peer!=null){
			for(int i=0;i<PeerListe.size();i++){
				if(!peer.getIp().equals(PeerListe.get(i).getIp())){
					System.out.println("sende "+ peer.getIp()+" an "+  PeerListe.get(i).getIp()  );
					ObserverListe.get(i).onNext(peer);
					PeerListe.get(i).setPeerSendCounter(PeerListe.get(i).getPeerSendCounter()-1);
				}
				
			}
		}
		peerListeLock.unlock();
	}


	public boolean serverStarten() {
		Thread t1 = new Thread(new RegisterServer("s"));
		t1.start();
		int i = 0;
		while (!serverInitStatus.equals("gestartet")) {
			i++;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			if(i>100){
				System.out.println("Starten des Server dauert l‰nger wie normal... breche ab!");
				return false;
			}
		}
		return true;
	}



	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		RegisterServer.port = port;
	}

	public static ArrayList<Peer> getPeerListe() {
		return PeerListe;
	}

	public static void setPeerListe(ArrayList<Peer> peerListe) {
		PeerListe = peerListe;
	}

	public static Server getRegisterServer() {
		return registerServer;
	}

	public static void setRegisterServer(Server registerServer) {
		RegisterServer.registerServer = registerServer;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getFehlermeldung() {
		return fehlermeldung;
	}

	public void setFehlermeldung(String fehlermeldung) {
		this.fehlermeldung = fehlermeldung;
	}

	public static String getServerInitStatus() {
		return serverInitStatus;
	}

	public static void setServerInitStatus(String serverInitStatus) {
		RegisterServer.serverInitStatus = serverInitStatus;
	}

	@Override
	public void run(){
		switch(control){
		case "s":
			try {
				serverInitStatus="starte";
				registerServer = ServerBuilder.forPort(port).addService(new RegisterServer()).build().start();
				System.out.println("Server gestartet");
				serverInitStatus="gestartet";
				registerServer.awaitTermination();
			} catch (IOException | InterruptedException e) {
				serverInitStatus=e.getMessage();
				e.printStackTrace();
			}
			break;
		case "h":
			SimpleDateFormat sdfmt = new SimpleDateFormat();
			boolean b=true;
			peerListeLock.lock();
			for(Peer p:PeerListe){
				if(p.getIp().equals(hbIP)){
					hbPeer=p;
				}
			}
			peerListeLock.unlock();
			StreamObserver<grpcDatenpakete.Text> responseObserver=null; 
			ManagedChannel gRPCHeartBeatVerbindung = ManagedChannelBuilder.forAddress(hbIP,10011).usePlaintext(true).build();
	    	PeerClientStub cs = PeerClientGrpc.newStub(gRPCHeartBeatVerbindung);
	    	grpcDatenpakete.Text hbMsg = grpcDatenpakete.Text.newBuilder().setNachricht("1").build();
			while(b){
		    	if(responseObserver==null){
			    	responseObserver = cs.heartBeat(new StreamObserver<grpcDatenpakete.Text>() {
			    		Peer hpP=hbPeer;
			    		SimpleDateFormat sdfmt = new SimpleDateFormat();
						@Override
						public void onNext(grpcDatenpakete.Text t){
							hbPeer.setLastHeartBeat(System.currentTimeMillis());
							sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
//							System.out.println( "Server Heartbeat von " + hpP.getIp() + " erhalten: " + sdfmt.format(new Date()) );
						}
		
						@Override
						public void onError(Throwable t) {
							sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
							System.out.println( "Server Heartbeat onError von " + hpP.getIp() + " erhalten: " + sdfmt.format(new Date()) );
							t.printStackTrace();
							logout(hbIP);
						}
		
						@Override
						public void onCompleted() {
							sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
							System.out.println( "Server Heartbeat onComplited von " + hpP.getIp() + " erhalten: " + sdfmt.format(new Date()) );
							logout(hbIP);
						}
					});
		    	}
		    	sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
//				System.out.println( "Server sende Heartbeat an " + hbPeer.getIp() + " erhalten: " + sdfmt.format(new Date()) );
		    	responseObserver.onNext(hbMsg);
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(System.currentTimeMillis()-hbPeer.getLastHeartBeat()>10000){
					if(hbPeer.getLastHeartBeat()==-1){
						
					}else{
						logout(hbIP);
						break;
					}
				}
			}
			break;
		default:break;
		}
		
	}

}
