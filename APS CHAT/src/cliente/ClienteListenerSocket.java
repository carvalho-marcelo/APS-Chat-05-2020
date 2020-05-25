package cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import gui.TelaChat;
import gui.TelaLogin;
import main.Mensagem;
import main.Mensagem.Action;

public class ClienteListenerSocket implements Runnable {

	private ObjectInputStream input;
	private TelaChat telaChat;
	
	private boolean running = false;
	
	public ClienteListenerSocket(Socket socket, TelaChat telaChat) {

		this.telaChat = telaChat;
		try {
			this.input = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		Mensagem message = null;

		running = true;
		try {
			while (running) {
				message = (Mensagem) input.readObject();
				Action action = message.getAction();

				if (action.equals(Action.CONNECT)) {
					this.connected(message);
				} else if (action.equals(Action.DISCONNECT)) {
					
				} else if (action.equals(Action.SEND_ONE)) {
					this.receive(message);
				} else if (action.equals(Action.USERS_ONLINE)) {
					this.refreshOnlines(message);
				} else if (action.equals(Action.SEND_FILE)) {
					this.salvarArq(message);
				}
			}
		} catch (IOException e){
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void connected(Mensagem message) {
		if (message.getText().equals("NO")) {
			// NÃO FOI REALIZADA A CONEXÃO COM ESSE NOME DE USUARIO
			
			disconnected();
			
			return;
		}

		telaChat.setMessage(message);

	}

	private void disconnected() {
		
		JOptionPane.showMessageDialog(telaChat, "Não foi possível estabelecer a conexão com esse nome de usuário\nTente novamente", "Erro", JOptionPane.ERROR_MESSAGE);
		
		running = false;
		telaChat.closeClient();

		new TelaLogin();
	}

	private void receive(Mensagem message) {
		this.telaChat.enviar(message.getName(), message.getText());
	}
	
	private void salvarArq(Mensagem message) {
		JOptionPane.showMessageDialog(telaChat, message.getName() + " te enviou o arquivo: " + message.getFile().getName() + "\nSelecione o local que deseja salvar ou clique em cancel para não receber", null, JOptionPane.INFORMATION_MESSAGE);
		
		String file = null;
		
		JFileChooser fc = new JFileChooser();
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int res = fc.showOpenDialog(telaChat);
		
		if (res == JFileChooser.APPROVE_OPTION && (new File(fc.getSelectedFile().getAbsolutePath())).exists()) {
			JOptionPane.showMessageDialog(telaChat, "Voce escolheu o diretorio: " + fc.getSelectedFile().getName());
			file = fc.getSelectedFile().getAbsolutePath();
			
			try {
				FileInputStream fileInputStream = new FileInputStream(message.getFile());
				FileOutputStream fileOutputStream = new FileOutputStream(file + "\\" + message.getFile().getName());
				
				FileChannel fcIn = fileInputStream.getChannel();
				FileChannel fcOut = fileOutputStream.getChannel();
				
				long size = fcIn.size();
				
				fcIn.transferTo(0, size, fcOut);
				
				fileInputStream.close();
				fileOutputStream.close();
				
				this.receive(message);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(telaChat, "Voce cancelou o recebimento do arquivo.");
		}
		
	}

	private void refreshOnlines(Mensagem message) {
		Set<String> names = message.getSetOnlines();
		
		names.remove(message.getName());
		
		String[] vetor = names.toArray(new String[names.size()]);
		
		telaChat.getjListUsuariosOnline().setListData(vetor);
		telaChat.getjListUsuariosOnline().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		telaChat.getjListUsuariosOnline().setLayoutOrientation(JList.VERTICAL);
	}

}
