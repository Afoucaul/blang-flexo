package org.blangflexo.plugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.blangflexo.core.ApiAbstractorException;

public class OperationListener extends Thread {
	private int port;
	
	public OperationListener(int port) {
		super();
		this.port = port;
	}
	
	@Override
	public void run() {
		String instruction;
		String response;
		ServerSocket socket;
		BufferedReader inFromClient;
		DataOutputStream outToClient;
		
		try {
			System.out.println("Opening TCP connection on port " + port + "...");
			socket = new ServerSocket(port);
		} catch (IOException ex) {
			System.out.println("Could not open socket");
			ex.printStackTrace();
			return;
		}
		
		try {
			System.out.println("Waiting for a connection...");
			Socket connectionSocket = socket.accept();
			System.out.println("Accepted a connection from port " + connectionSocket.getPort());
			
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
			System.out.println("Now listening on port " + port + ".");
			while (true) {
				instruction = inFromClient.readLine();
				System.out.println("Received: " + instruction);
				boolean success = ApiOperationDispatcher.execute(instruction);
				response = success ? "Success" : "Failure";
				outToClient.writeBytes(response);
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Could not close socket");
				e.printStackTrace();
			}
		}
	}
}
