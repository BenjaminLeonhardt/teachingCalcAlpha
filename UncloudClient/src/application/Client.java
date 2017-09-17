
package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.ByteString;

import grpcDatenpakete.PeerClientGrpc;
import grpcDatenpakete.Text;
import grpcDatenpakete.file;
import grpcDatenpakete.registerServerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Client extends grpcDatenpakete.PeerClientGrpc.PeerClientImplBase implements Runnable{
	
	private static String serverIP;
	private static int serverPort;
	private static String clientName;
	private static String shareOrdner;
	private static String OS;
	private static String eigeneIp="";
	private static String clientHostName;
	private static boolean clientEndian;
	private static boolean hb=true;
	private Server client;
	private String control;
	private static String clientInitStatus;
	private static int peerPort = 10011;
	
	private static ArrayList<File> listeFreigegebenerDateien = new ArrayList<>();
	private static ArrayList<String> dateinamenDerFreigegebenenDateienDesVerbundenenClients = new ArrayList<>();
	
	private static ArrayList<String> chatInhalt= new ArrayList<>();
	private static ArrayList<Chat> chatListe= new ArrayList<>();
	
	
	private static ArrayList<Peer> peerListe = new ArrayList<>();
	private static ArrayList<String> fileTransfehrListe = new ArrayList<>();
	private static long lastHeartBeat = -1;
	private static Lock peerListeLock = new ReentrantLock();
	public static Lock chatListeLock = new ReentrantLock();
	
	public static Lock fileWriteLock = new ReentrantLock();
	public static Lock fileTransLock = new ReentrantLock();

	public static ManagedChannel gRPCServerVerbindung;
	public static registerServerGrpc.registerServerStub sbs;
	
	public static boolean mitServerVerbunden=false;
	
	public Client(){
		
	}
	
	public Client(String ctrl){
		control=ctrl;
	}
	
	public io.grpc.stub.StreamObserver<grpcDatenpakete.Text> chat(
            io.grpc.stub.StreamObserver<grpcDatenpakete.Text> responseObserver) {
          return new StreamObserver<grpcDatenpakete.Text>() {
        	  SimpleDateFormat sdfmt = new SimpleDateFormat();
			Chat chat;
				@Override
				public void onNext(grpcDatenpakete.Text t){
					if(chat==null){
						String ipAddr = t.getNachricht().substring(0,t.getNachricht().indexOf("__"));
						boolean gefunden=false;
						chatListeLock.lock();
						for(Chat c:chatListe){
							if(c.getPeer().getIp().equals(ipAddr)){
								chat=c;
								gefunden=true;
								break;
							}
						}
						chatListeLock.unlock();
						if(!gefunden){
							peerListeLock.lock();
							Peer peerNeuerChat=null;
							for(Peer p:peerListe){
								if(p.getIp().equals(ipAddr)){
									peerNeuerChat=p;
									break;
								}
							}
							peerListeLock.unlock();
							if(peerNeuerChat!=null){
								chat = new Chat();
								chat.setPeer(peerNeuerChat);
								chatListe.add(chat);
								chat.setChatObserver(responseObserver);
								SampleController sc = new SampleController();
								sc.chatOeffnen(chat);
							}
						}else if(gefunden){
							if(chat.getChatController()==null){
								SampleController sc = new SampleController();
								sc.chatOeffnen(chat);
							}
						}
					}
					while(chat.getChatVerlauf()==null){
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					String tmp = chat.getChatVerlauf().getText()+chat.getPeer().getName()+": "+t.getNachricht().substring(t.getNachricht().indexOf("__")+2)+"\n";
					chat.addTextChatHistoryFile(chat.getPeer().getName()+": "+t.getNachricht().substring(t.getNachricht().indexOf("__")+2)+"\n");
					chat.getChatVerlauf().setText(tmp);					
					Platform.runLater(new Runnable(){			
						@Override
						public void run() {
							chat.getChatVerlauf().setScrollTop(Double.MAX_VALUE);
							chat.getChatStage().toFront();
						}});
					
				}
				@Override
				public void onError(Throwable t){
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client im Chat mit " + chat.getPeer().getIp() + " onError erhalten: " + sdfmt.format(new Date()) );
					t.printStackTrace();
				}
				@Override
				public void onCompleted(){
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client im Chat mit " + chat.getPeer().getIp() + " onCompleted erhalten: " + sdfmt.format(new Date()) );
				}
			};
        }

        public void fileTransfer(grpcDatenpakete.Text request,
            io.grpc.stub.StreamObserver<grpcDatenpakete.file> responseObserver) {
        	fileTransLock.lock();
        	if(!fileTransfehrListe.contains(request.getNachricht())){
        		fileTransfehrListe.add(request.getNachricht());
        		fileTransLock.unlock();
	        	try {
	        		String tmp = shareOrdner + "\\" +  request.getNachricht();
	        		DataInputStream dis = new DataInputStream(new BufferedInputStream(new  FileInputStream(tmp)));
	        		grpcDatenpakete.file fileMsg;
	        		byte[] byteArray;
					try {
	//					byteArray = new byte[dis.available()];
	//					dis.readFully(byteArray);
	
						int gesamtGroesse=dis.available();
						int partGroesse=4194280;
						int partAnzahl=(gesamtGroesse/partGroesse)+1;
						int offset=0;
						Runtime rt = Runtime.getRuntime();
						byteArray = new byte[partGroesse];
						for(int i=1;i<=partAnzahl;i++){
							if(offset>gesamtGroesse){
								offset=gesamtGroesse;
							}
							dis.read(byteArray, 0, partGroesse);
							if(partGroesse*(i)>gesamtGroesse){
								partGroesse=gesamtGroesse-(partGroesse*(i-1));
							}
							System.out.println("Versende Part " + i + " von " + partAnzahl + ". Größe " + partGroesse + " von "
									+ gesamtGroesse + ". Bisher versendet: " + ((i-1) * partGroesse) + " Free memory "+rt.freeMemory());
							
							fileMsg = grpcDatenpakete.file.newBuilder().setPartsize(partGroesse).setEndian(clientEndian)
									.setPart(i).setPartanzahl(partAnzahl).setFilesize(gesamtGroesse)
									.addDaten(ByteString.copyFrom(byteArray, 0, partGroesse)).build();
														
							responseObserver.onNext(fileMsg);
							offset+=partGroesse;
							Thread.sleep(50);
						}
						dis.close();
						fileTransLock.lock();
						fileTransfehrListe.remove(request.getNachricht());
						fileTransLock.unlock();
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	fileTransLock.unlock();
        }
        
        public static boolean downloadeDateiVonAnderemPeer(Datei datei, Peer peer){
        	checkChannel(peer);
        	
        	grpcDatenpakete.Text peerMsg = grpcDatenpakete.Text.newBuilder().setNachricht(datei.getDateiname()).build();
        	StreamObserver<grpcDatenpakete.file> responseObserver = new StreamObserver<grpcDatenpakete.file>(){
        		SimpleDateFormat sdfmt = new SimpleDateFormat();
        		int packetCounter;
        		Datei d=datei;
        		Peer p=peer;
				@Override
				public void onNext(file value) {
					try {
						System.out.println("Datei Empfangen Part " + value.getPart() + " von " + value.getPartanzahl() + 
								". Größe " + value.getPartsize() + " von "
								+ value.getFilesize() + ". Bisher empfangen: " + (value.getPart() * value.getPartsize()));
						writeFile(value);
					} catch (IOException | InterruptedException e) {						
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Error bei Part " + value.getPart() + " von " + value.getPartanzahl() + 
								". Größe " + value.getPartsize() + " von "
								+ value.getFilesize() + ". Bisher empfangen: " + (value.getPart() * value.getPartsize()));
					}
        			sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " Datenpaket " + packetCounter + " empfangen: " + sdfmt.format(new Date()) );					
				}

				private void writeFile(file value) throws InterruptedException, FileNotFoundException, IOException {
					DataOutputStream dos=null;
					try{
					System.out.println("Datei Empfangen Part " + value.getPart() + " von " + value.getPartanzahl() + 
								". Größe " + value.getPartsize() + " von "
								+ value.getFilesize() + ". Bisher empfangen: " + (value.getPart() * value.getPartsize()));
					String FilePathAndName = shareOrdner+"\\"+d.getDateiname();
					
					fileWriteLock.lock();					
					File file = new File(FilePathAndName);
					if(value.getPart()==1){
						file.exists();
						file.delete();
					}
					if(value.getPart()>1){
						if(!file.canWrite()){
							while(!file.canWrite()){
								Thread.sleep(5);
							}
						}
					}
					dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file,true)));
					List<ByteString> bytes = value.getDatenList();
//						byte fileBytes []= value.toByteArray();
//						dos.write(fileBytes,4,fileBytes.length-4);
					dos.write(bytes.get(0).toByteArray());
					dos.close();
					fileWriteLock.unlock();
					int fortschrittint=(int) ((((double)value.getPart())/((double)value.getPartanzahl()))*100);
					d.setFortschritt(new SimpleStringProperty(String.valueOf(fortschrittint+" %")));
					System.out.println();
					SampleController.changeDateiView(d);
					}catch(Exception e){
						dos.close();
						dos = null;
						Thread.sleep(5);
						System.out.println("Neuer Versuch von Part " + value.getPart() + " von " + value.getPartanzahl() + 
								". Größe " + value.getPartsize() + " von "
								+ value.getFilesize() + ". Bisher empfangen: " + (value.getPart() * value.getPartsize()));
						e.printStackTrace();
						writeFile(value);
					}
				}

				@Override
				public void onError(Throwable t) {
					fileWriteLock.unlock();
					t.printStackTrace();
        			sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onError bei downloadeDateiVonAnderemPeer empfangen: " + sdfmt.format(new Date()) );					
				}

				@Override
				public void onCompleted() {
					fileWriteLock.unlock();
        			sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onCompleted bei downloadeDateiVonAnderemPeer empfangen: " + sdfmt.format(new Date()) );
				}
        	};
        	peer.getPeerClientStub().fileTransfer(peerMsg, responseObserver);
        	return true;
        }
        
 

        public void fileListe(grpcDatenpakete.Text request,
            io.grpc.stub.StreamObserver<grpcDatenpakete.Text> responseObserver) {
        	for(File f:listeFreigegebenerDateien){
//        		try {
        			if(f.isFile()&&f.canRead()){
//						DataInputStream dis = new DataInputStream(new BufferedInputStream(new  FileInputStream(f)));
//						grpcDatenpakete.file fileMsg;
//						byte[] byteArray;
//						byteArray = new byte[dis.available()];
//						dis.readFully(byteArray);
//						int gesamtGroesse=byteArray.length;
						
						System.out.println("Sende " + f.getName()+"\\"+f.length());
		        		responseObserver.onNext(grpcDatenpakete.Text.newBuilder().setNachricht(f.getName()+"\\"+f.length()).build());    
		        		//dis.close();
        			}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
        	}
        }
        
        public io.grpc.stub.StreamObserver<grpcDatenpakete.Text> heartBeat(
                io.grpc.stub.StreamObserver<grpcDatenpakete.Text> responseObserver) {
        	SimpleDateFormat sdfmt = new SimpleDateFormat();

        	hb=false;
        	return new StreamObserver<grpcDatenpakete.Text>() {
        		SimpleDateFormat sdfmt = new SimpleDateFormat();
        		io.grpc.stub.StreamObserver<grpcDatenpakete.Text> reOb=responseObserver;
        		
        		@Override
				public void onNext(grpcDatenpakete.Text t){
        			if(mitServerVerbunden==false){
        				mitServerVerbunden=true;
        			}
					lastHeartBeat=System.currentTimeMillis();
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
//					System.out.println( "Client Heartbeat erhalten: " + sdfmt.format(new Date()) );
					try {
						Thread.sleep(1000);
						lastHeartBeat=System.currentTimeMillis();
						sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
						reOb.onNext(grpcDatenpakete.Text.newBuilder().setNachricht(eigeneIp).build());
//						System.out.println( "Client Heartbeat versandt: " + sdfmt.format(new Date()));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onError(Throwable t){
        			if(mitServerVerbunden==true){
        				mitServerVerbunden=false;
        			}
					System.out.println("Client Heartbeat onError.");
					peerListe.clear();
					SampleController sc = new SampleController();
					sc.removeAllFromTableView();
					sc.setStatus();
					if(gRPCServerVerbindung!=null){
						gRPCServerVerbindung.shutdownNow();
					}
					gRPCServerVerbindung=null;
				}
				@Override
				public void onCompleted(){
        			if(mitServerVerbunden==true){
        				mitServerVerbunden=false;
        			}
					System.out.println( "Client Heartbeat onComplited");
					peerListe.clear();
					SampleController sc = new SampleController();
					sc.removeAllFromTableView();
					sc.setStatus();
					if(gRPCServerVerbindung!=null){
						gRPCServerVerbindung.shutdownNow();
					}
					gRPCServerVerbindung=null;
				}
			};

        }
        
        public static boolean checkValidateOfIpAndPort(){ 
        	if(serverPort<1024||serverPort>49152){
        		return false;
        	}
        	int start=0;
        	ArrayList<String> parts= new ArrayList<>();
        	try{
	        	for(int i=0;i<serverIP.length();i++){
	        		if(serverIP.charAt(i)=='.'){
	        			parts.add(serverIP.substring(start, i));
	        			start=i+1;
	        		}
	        	}
	        	parts.add(serverIP.substring(start, serverIP.length()));
	        	int tmp=0;
	        	for(String s:parts){
	        		tmp=Integer.parseInt(s);
	        		if(tmp<1||tmp>254){
	        			return false;
	        		}
	        	}
        	}catch(Exception e){
        		e.printStackTrace();
        		return false;
        	}
        	return true;
        }
        
        private static void getNetworkStats(){
        	int count=0;
        	int index=0;
        	String serverTmp=null;
        	if(serverIP!=null){
	        	for(int i=0;i<serverIP.length();i++){
	        		if(serverIP.charAt(i)=='.'){
	        			count++;
	        			if(count==3){
	        				index=i;
	        			}
	        		}
	        	}
	        	serverTmp=serverIP.substring(0, index);
	        	System.out.println(serverTmp+" hier einmal debug bitte");
        	}
    		if(eigeneIp==null||eigeneIp.equals("")){
				Enumeration e;
				try {
					e = NetworkInterface.getNetworkInterfaces();
					while(e.hasMoreElements()){
					    NetworkInterface n = (NetworkInterface) e.nextElement();
					    Enumeration ee = n.getInetAddresses();
					    while (ee.hasMoreElements()){
					        InetAddress i = (InetAddress) ee.nextElement();
					        if(serverTmp!=null){
						        if(i.getHostAddress().startsWith(serverTmp)){					        	
						        	eigeneIp=i.getHostAddress();
						        	clientHostName=i.getHostName();
						        	System.out.println(eigeneIp + " " + clientHostName);
						        }
					        }
					    }
					}
				} catch (SocketException e1) {
					eigeneIp="";
					e1.printStackTrace();
				}
    		}
        }
        
        public static boolean checkConnectivity(){
        	SimpleDateFormat sdfmt = new SimpleDateFormat();
        	if(checkValidateOfIpAndPort()){
        		getNetworkStats();
	        	try{
	        		if(checkChannelServer()){
			        	grpcDatenpakete.Peer peerMsg = grpcDatenpakete.Peer.newBuilder().setIp(eigeneIp).setPort(serverPort).setName(clientName).setHostname(clientHostName).setOs(OS).setEndian(clientEndian).setAddOrDelete(true).build();
						sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
						System.out.println( "Client " + eigeneIp + " registriere bei Server "+serverIP + ": " + sdfmt.format(new Date()) );
			        	sbs.registrieren(peerMsg, new StreamObserver<grpcDatenpakete.Peer>() {
			        		SimpleDateFormat sdfmt = new SimpleDateFormat();
							@Override
							public void onNext(grpcDatenpakete.Peer p){
								Peer newPeer = new Peer(p.getId(),p.getIp(),String.valueOf(p.getPort()),p.getName(),p.getHostname(),p.getOs(),"",p.getEndian());
								if(p.getAddOrDelete()){
									peerListe.add(newPeer);
									SampleController sc = new SampleController();
									sc.setTableView(newPeer);
								}else{
									for(Peer pe:peerListe){
										if(pe.getIp().equals(p.getIp())){
											peerListe.remove(pe);
											SampleController sc = new SampleController();
											sc.removeFromTableView(pe);
											break;
										}
									}
								}
							}
							@Override
							public void onError(Throwable t){
								t.printStackTrace();
								sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
								System.out.println( "Client " + eigeneIp + " onError beim verbinden zum Server erhalten: " + sdfmt.format(new Date()) );
								peerListe.clear();
								SampleController sc = new SampleController();
								sc.removeAllFromTableView();
								sc.setStatus();
								if(gRPCServerVerbindung!=null){
									gRPCServerVerbindung.shutdownNow();
								}
								gRPCServerVerbindung=null;
							}
							@Override
							public void onCompleted(){
								sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
								System.out.println( "Client " + eigeneIp + " onCompleted beim verbinden zum Server erhalten: " + sdfmt.format(new Date()) );
								peerListe.clear();
								SampleController sc = new SampleController();
								sc.removeAllFromTableView();
								sc.setStatus();
								if(gRPCServerVerbindung!=null){
									gRPCServerVerbindung.shutdownNow();
								}
								gRPCServerVerbindung=null;
								try {
									BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
									Label lblStatus = (Label) root.lookup("#lblStatus");	
									lblStatus.setTextFill(Color.web("#ae0404"));
									lblStatus.setText("Verbindung beendet");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						});
			        	
						sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
						System.out.println( "Client " + eigeneIp + " starte heartbeat Thread zum Server "+serverIP + ": " + sdfmt.format(new Date()) );
			        	Thread heartBeat = new Thread(new Client("s"));
			        	heartBeat.start();
	        		}else{
	        			//TODO
	        		}
		        	return true;
	        	}catch(Exception ec){
	        		ec.printStackTrace();
	        		return false;
	        	}
        	}
        	return false;
        }
        
        public static void logout() throws Exception{
        	peerListe.clear();
        	SampleController sc = new SampleController();
        	sc.removeAllFromTableView();
        	checkChannelServer();
        	grpcDatenpakete.Peer peerMsg = grpcDatenpakete.Peer.newBuilder().setIp(eigeneIp).setPort(serverPort).setName(clientName).setHostname(clientHostName).setEndian(clientEndian).setOs(OS).build();
        	sbs.logout(peerMsg, new StreamObserver<grpcDatenpakete.Text>(){
        		SimpleDateFormat sdfmt = new SimpleDateFormat();
        		@Override
				public void onNext(grpcDatenpakete.Text t){
        			sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " logout empfangen: " + sdfmt.format(new Date()) );
					peerListe.clear();
					SampleController sc = new SampleController();
					sc.removeAllFromTableView();
					sc.setStatus();
					if(gRPCServerVerbindung!=null){
						gRPCServerVerbindung.shutdownNow();
					}
					gRPCServerVerbindung=null;
				}
				@Override
				public void onError(Throwable t){
					t.printStackTrace();
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onError bei logout empfangen: " + sdfmt.format(new Date()) );
					peerListe.clear();
					SampleController sc = new SampleController();
					sc.removeAllFromTableView();
					sc.setStatus();
					if(gRPCServerVerbindung!=null){
						gRPCServerVerbindung.shutdownNow();
					}
					gRPCServerVerbindung=null;
				}
				@Override
				public void onCompleted(){
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onCompleted bei logout empfangen: " + sdfmt.format(new Date()) +" leere Peerliste" );
					peerListe.clear();
					SampleController sc = new SampleController();
					sc.removeAllFromTableView();
					sc.setStatus();
					if(gRPCServerVerbindung!=null){
						gRPCServerVerbindung.shutdownNow();
					}
					gRPCServerVerbindung=null;
					}
        	});
        }

        public static boolean verbindeZuAnderemPeer(Peer peer) {
        	checkChannel(peer);
        	grpcDatenpakete.Text peerMsg = grpcDatenpakete.Text.newBuilder().setNachricht("init").build();
        	dateinamenDerFreigegebenenDateienDesVerbundenenClients = new ArrayList<>();
        	StreamObserver<grpcDatenpakete.Text> responseObserver = new StreamObserver<grpcDatenpakete.Text>(){
        		SimpleDateFormat sdfmt = new SimpleDateFormat();
				@Override
				public void onNext(Text value) {
					System.out.println(value.getNachricht());
					dateinamenDerFreigegebenenDateienDesVerbundenenClients.add(value.getNachricht().substring(0,value.getNachricht().indexOf("\\")));
					Datei datei = new Datei(new SimpleStringProperty(value.getNachricht().substring(0,value.getNachricht().indexOf("\\"))),new SimpleStringProperty(""),new SimpleStringProperty(value.getNachricht().substring(value.getNachricht().indexOf("\\")+1)));
        			SampleController.setDateiView(datei);
        			sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onNext bei verbindeZuAnderemPeer empfangen: " + sdfmt.format(new Date()) );
				}

				@Override
				public void onError(Throwable t) {
					t.printStackTrace();
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onError bei verbindeZuAnderemPeer empfangen: " + sdfmt.format(new Date()) );
				}

				@Override
				public void onCompleted() {
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onCompleted bei verbindeZuAnderemPeer empfangen: " + sdfmt.format(new Date()) );
				}
        		
        	};
        	
        	peer.getPeerClientStub().fileListe(peerMsg, responseObserver);
			return true;
		}

		private static void checkChannel(Peer peer) {
			if(peer.getgRPCPeerVerbindung()==null){
	        	ManagedChannel gRPCPeerVerbindung = ManagedChannelBuilder.forAddress(peer.getIp(), peerPort).usePlaintext(true).build();
	        	PeerClientGrpc.PeerClientStub pcs = PeerClientGrpc.newStub(gRPCPeerVerbindung);
	        	peer.setgRPCPeerVerbindung(gRPCPeerVerbindung);
	        	peer.setPeerClientStub(pcs);
        	}
		}
		
		private static boolean checkChannelServer() {
			if(gRPCServerVerbindung==null){
				gRPCServerVerbindung = ManagedChannelBuilder.forAddress(serverIP, serverPort).usePlaintext(true).build();
				sbs = registerServerGrpc.newStub(gRPCServerVerbindung);
				return true;
        	}
			return false;
		}
        
        
        public static boolean verbindeChatZuAnderemPeer(Peer peer) {
        	checkChannel(peer);
        	grpcDatenpakete.Text peerMsg = grpcDatenpakete.Text.newBuilder().setNachricht("chat").build();
        	StreamObserver<grpcDatenpakete.Text> responseChatObserver = new StreamObserver<grpcDatenpakete.Text>(){
        		SimpleDateFormat sdfmt = new SimpleDateFormat();
        		Chat ch;
        		
        		@Override
				public void onNext(Text value) {
        			if(ch==null){
        				for(Chat c: chatListe){
        					if(c.getPeer()==peer){
        						ch=c;
        					}
        				}
        			}
        			if(ch.getChatController()==null){
        				try {
	        				Stage chatStage = new Stage();
	    					AnchorPane root;
							root = (AnchorPane)FXMLLoader.load(getClass().getResource("Chat.fxml"));
	    					TextArea chatVerlauf = (TextArea) root.lookup("#chatVerlauf");
	    					chatVerlauf.setScrollTop(Double.MAX_VALUE);
	    					Scene chatScene = new Scene(root);
	    					ch.setChatVerlauf(chatVerlauf);
	    					chatScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    					chatStage.setTitle("Chat mit " + ch.getPeer().getName());
	    					chatStage.setScene(chatScene);
	    					chatStage.show();
	    					chatStage.setOnCloseRequest(e -> {
	    						e.consume();
	    						try{
	    							ch.getChatObserver().onCompleted();
	    							ch.setChatController(null);
	    						}catch(Exception ex){
	    							
	    						}
	    						chatStage.close();
	    					});
        				} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        			}
        			ch.getChatVerlauf().setText(ch.getChatVerlauf().getText()+ch.getPeer().getName()+": "+value.getNachricht().substring(value.getNachricht().indexOf("__")+2)+"\n");
        			ch.getChatVerlauf().setScrollTop(Double.MAX_VALUE);
					chatInhalt.add(value.getNachricht());

				}

				@Override
				public void onError(Throwable t) {
					t.printStackTrace();
					if(ch!=null){
						ch.getChatObserver().onCompleted();
					}
					ch.setChatObserver(null);
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onError bei verbindeChatZuAnderemPeer empfangen: " + sdfmt.format(new Date()) );
				}

				@Override
				public void onCompleted() {
					sdfmt.applyPattern( "EEEE', 'dd. MMMM yyyy hh:mm:ss" );
					System.out.println( "Client " + eigeneIp + " onCompleted bei verbindeChatZuAnderemPeer empfangen: " + sdfmt.format(new Date()) );
					if(ch.getChatObserver()!=null){
						ch.getChatObserver().onCompleted();
						ch.setChatObserver(null);
					}		
				}
        		
        	};
        	StreamObserver<grpcDatenpakete.Text>requestObserver = peer.getPeerClientStub().chat(responseChatObserver);
        	boolean chatSchonVorhanden=false;
        	Chat chat;
        	for(Chat c:Client.chatListe){
        		if(c.getPeer().getIp().equals(peer.getIp())){
        			c.setChatObserver(requestObserver);
        			chatSchonVorhanden=true;
        		}
        	}
        	if(!chatSchonVorhanden){
        		chat= new Chat(peer,requestObserver);
        		chatListe.add(chat);
        	}
			return true;
		}
        
		public static String getServerIP() {
			return serverIP;
		}

		public static void setServerIP(String serverIP) {
			Client.serverIP = serverIP;
		}

		public static int getServerPort() {
			return serverPort;
		}

		public static void setServerPort(int serverPort) {
			Client.serverPort = serverPort;
		}

		public static String getName() {
			return clientName;
		}

		public static void setName(String name) {
			Client.clientName = name;
		}

		public static String getShareOrdner() {
			return shareOrdner;
		}

		public static void setShareOrdner(String shareOrdner) {
			Client.shareOrdner = shareOrdner;
		}

		public static String getOS() {
			return OS;
		}

		public static void setOS(String oS) {
			OS = oS;
		}

		public static ArrayList<Peer> getPeerListe() {
			return peerListe;
		}

		public static void setPeerListe(ArrayList<Peer> peerListe) {
			Client.peerListe = peerListe;
		}

		public static ArrayList<File> getListeFreigegebenerDateien() {
			return listeFreigegebenerDateien;
		}

		public static void setListeFreigegebenerDateien(ArrayList<File> liste) {
			listeFreigegebenerDateien = liste;
		}

		public static String getClientName() {
			return clientName;
		}

		public static void setClientName(String clientName) {
			Client.clientName = clientName;
		}

		public static String getEigeneIp() {
			return eigeneIp;
		}

		public static void setEigeneIp(String eigeneIp) {
			Client.eigeneIp = eigeneIp;
		}

		public static boolean isHb() {
			return hb;
		}

		public static void setHb(boolean hb) {
			Client.hb = hb;
		}

		public Server getClient() {
			return client;
		}

		public void setClient(Server client) {
			this.client = client;
		}

		public String getControl() {
			return control;
		}

		public void setControl(String control) {
			this.control = control;
		}

		public static String getClientInitStatus() {
			return clientInitStatus;
		}

		public static void setClientInitStatus(String clientInitStatus) {
			Client.clientInitStatus = clientInitStatus;
		}

		public static int getPeerPort() {
			return peerPort;
		}

		public static void setPeerPort(int peerPort) {
			Client.peerPort = peerPort;
		}

		public static ArrayList<String> getDateinamenDerFreigegebenenDateienDesVerbundenenClients() {
			return dateinamenDerFreigegebenenDateienDesVerbundenenClients;
		}

		public static void setDateinamenDerFreigegebenenDateienDesVerbundenenClients(
				ArrayList<String> dateinamenDerFreigegebenenDateienDesVerbundenenClients) {
			Client.dateinamenDerFreigegebenenDateienDesVerbundenenClients = dateinamenDerFreigegebenenDateienDesVerbundenenClients;
		}

		public static ArrayList<String> getChatInhalt() {
			return chatInhalt;
		}

		public static void setChatInhalt(ArrayList<String> chatInhalt) {
			Client.chatInhalt = chatInhalt;
		}

		public static ArrayList<Chat> getChatListe() {
			return chatListe;
		}

		public static void setChatListe(ArrayList<Chat> chatListe) {
			Client.chatListe = chatListe;
		}

		public static long getLastHeartBeat() {
			return lastHeartBeat;
		}

		public static void setLastHeartBeat(long lastHeartBeat) {
			Client.lastHeartBeat = lastHeartBeat;
		}

		public static Lock getPeerListeLock() {
			return peerListeLock;
		}

		public static void setPeerListeLock(Lock peerListeLock) {
			Client.peerListeLock = peerListeLock;
		}
		
	
		public static void initClient(){
			getNetworkStats();
			Client.setOS(System.getProperty("os.name"));
			File folder = new File(System.getProperty("user.home")+"\\AppData\\Local\\BenShare");
			File config = new File(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\config.ini");
			
			if(!config.exists()){
				System.out.println("Keine Config Datei gefunden. Es werden die Standarteinstellungen geladen.");
				try {
					folder.mkdir();
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\config.ini"));
					bufferedWriter.write("Server IP:192.168.2.102");
					bufferedWriter.newLine();
					bufferedWriter.write("Server-Port:10010");
					bufferedWriter.newLine();
					bufferedWriter.write("Name:Ben");
					bufferedWriter.newLine();
					bufferedWriter.write("Up/Download-Ordner:" +System.getProperty("user.home")+"\\Downloads");
					bufferedWriter.newLine();
					bufferedWriter.write("Hostname:" + clientHostName);
					bufferedWriter.newLine();
					bufferedWriter.write("Endian:" + clientEndian);
					bufferedWriter.newLine();
					bufferedWriter.close();
					serverIP="192.168.2.104";
					serverPort=10010;
					clientName="Ben";
					shareOrdner=System.getProperty("user.home")+"\\Downloads";
					System.out.println("Neue Configdatei erstellt: " + System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\config.ini");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.out.println("Config.ini wird geladen");
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.home")+"\\AppData\\Local\\BenShare\\config.ini"));
					String serverIPConfig = bufferedReader.readLine();
					serverIP = serverIPConfig.substring(serverIPConfig.indexOf(":")+1, serverIPConfig.length());
					String serverPortConfig = bufferedReader.readLine();
					serverPort = Integer.valueOf(serverPortConfig.substring(serverPortConfig.indexOf(":")+1, serverPortConfig.length()));
					String clientNameConfig = bufferedReader.readLine();
					clientName = clientNameConfig.substring(clientNameConfig.indexOf(":")+1, clientNameConfig.length());
					String shareOrdnerConfig = bufferedReader.readLine();
					shareOrdner = shareOrdnerConfig.substring(shareOrdnerConfig.indexOf(":")+1, shareOrdnerConfig.length());
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				File ordner = new File(shareOrdner);
				if(ordner.isDirectory()){
					File[] fl = ordner.listFiles();
					ArrayList<File> fla=new ArrayList<File>();
					for(int i=0;i<fl.length;i++){
						fla.add(fl[i]);
					}
					Client.setListeFreigegebenerDateien(fla);
				}
			}
		}
		
		@Override
		public void run(){
			switch(control){
			case "s":
				try {
					clientInitStatus="starte";
					if(client!=null){
						client.shutdownNow();
						Thread.sleep(100);
						client=null;
					}
					if(client==null){
						client = ServerBuilder.forPort(peerPort).addService(new Client()).build().start();
						System.out.println("Server gestartet");
						clientInitStatus="gestartet";
						client.awaitTermination();
					}
					
				} catch (IOException | InterruptedException e) {
					clientInitStatus=e.getMessage();
					e.printStackTrace();
				}
				break;
			}
		}

}
