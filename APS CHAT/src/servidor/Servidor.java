package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import gui.TelaServidor;

public class Servidor implements Runnable {
	
	public static final String IP = "127.0.0.1";
	
	private ServerSocket serverSocket;
	private Socket socket;
	private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
	
	private boolean running;
	private TelaServidor gui;
	
	private ArrayList<Socket> socketList = new ArrayList<Socket>();
	
	public Servidor(int port, TelaServidor gui) {
		running = true;
		this.gui = gui;
		
		try {
			serverSocket = new ServerSocket(port);
			
			gui.getTfPort().setEnabled(false);
			gui.getBtnIniciar().setEnabled(false);
			gui.getBtnParar().setEnabled(true);
			
			gui.sendLog("<font color=blue>> Servidor Online - PORTA: " + serverSocket.getLocalPort() + ".</font>");
			
		} catch (IOException e) {
			running = false;
			JOptionPane.showMessageDialog(null, "Não foi possível estabelecer a conexão nessa porta!", null, JOptionPane.ERROR_MESSAGE);
			gui.sendLog("<font color=red>> Não foi possível estabelecer a conexão na porta: " + port + ".</font>");
		}
	}
	
	public Map<String, ObjectOutputStream> retornarListaUsuariosOnline() {
		return mapOnlines;
	}

	public void removeCliente(String user) {
		mapOnlines.remove(user);
	}
	
	public void addCliente(String user, ObjectOutputStream output) {
		mapOnlines.put(user, output);
	}
	
	public ObjectOutputStream retornaOutputUsuario(String user) {
		for (String client : mapOnlines.keySet()) {
			if (client.equals(user)) {
				return mapOnlines.get(user);
			}
		}
		return null;
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				socket = serverSocket.accept();
				
				socketList.add(socket);
				
				new Thread(new ListenerSocket(socket, this)).start();
			} catch (IOException e) {
				e.getMessage();
			}
		}
	}
	
	public void stop() {
		try {
			running = false;
			if (serverSocket != null) {
				gui.sendLog("<font color=blue>> Servidor Offline.</font>");
				serverSocket.close();
			}
			for (Socket socket : socketList) {
				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();
			}
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public TelaServidor getGui() {
		return gui;
	}

}
