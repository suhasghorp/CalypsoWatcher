package com.ab.calypsowatcher;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class SocketListener implements Callable<String> {
	private static ServerSocket server;
	
	public SocketListener(int port) throws Exception {
		server = new ServerSocket(port);
	}
	
	@Override
	public String call(){
		while(true){
			try {
            System.out.println("Waiting for client request");
            Socket socket = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message Received: " + message);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("Hi Client "+message);
            ois.close();
            oos.close();
            socket.close();
            if(message.equalsIgnoreCase("STOP")) break;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
        }
		return "STOP";
	}

}
