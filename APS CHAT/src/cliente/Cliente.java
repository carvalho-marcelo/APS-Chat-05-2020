package cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import gui.TelaLogin;
import main.Mensagem;
import servidor.Servidor;

public class Cliente {
	
	private Socket socket;
	private ObjectOutputStream output;
	
	public Socket connect(int port, TelaLogin gui) {
		try {
			this.socket = new Socket(Servidor.IP, port);
			this.output = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(gui, "Servidor não está aberto nessa porta", "Erro de conexão", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		return socket;
	}
	
	public void send(Mensagem message) {
		try {
			output.writeObject(message);
		} catch (IOException e) {
			System.exit(0);
		}
	}
}
