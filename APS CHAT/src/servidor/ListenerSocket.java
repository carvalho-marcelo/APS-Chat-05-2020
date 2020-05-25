package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.Mensagem;
import main.Mensagem.Action;

public class ListenerSocket implements Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;

	private Servidor servidor;
	private Socket socket;
	
	private boolean running = false;

	public ListenerSocket(Socket socket, Servidor servidor) {
		this.socket = socket;
		this.servidor = servidor;
		try {
			this.output = new ObjectOutputStream(socket.getOutputStream());
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
					boolean isConnected = this.connect(message, output);
					if (isConnected) {
						servidor.getGui().sendLog("> Usuario conectado - PORTA: " + socket.getPort());
						servidor.addCliente(message.getName(), output);
						sendOnlines();
					} else {
						servidor.getGui().sendLog("<font color=red>> Conexão recusada: Alguem já está conectado com esse nome de usuário.</font>");
						running = false;
					}
				} else if (action.equals(Action.DISCONNECT)) {
					
				} else if (action.equals(Action.SEND_ONE)) {
					this.sendOne(message);
				} else if (action.equals(Action.SEND_ALL)) {
					this.sendAll(message);
				} else if (action.equals(Action.USERS_ONLINE)) {
				}
			}
		} catch (IOException e){
			//QUANDO É FECHADO PELO [X] O CHAT É GERADO UMA EXCEÇÃO E ESSA EXCEÇÃO É ONDE TRATAMOS O PROCESSO DE DESCONECTAR UM USUARIO
			Mensagem cm = new Mensagem();
			cm.setName(message.getName());
			disconnect(cm, output);
			sendOnlines();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
	}

	private boolean connect(Mensagem message, ObjectOutputStream output) {
		//VERIFICA SE O NOME DE USUARIO JA EXISTE NA LISTA DE USUARIOS CONECTADOS E RETORNA TRUE OU FALSE, ALEM DE ENVIAR UMA MENSAGEM SIM OU NAO PARA A THREAD DO CLIENTE LER
		
		if ((servidor.retornarListaUsuariosOnline()).size() == 0) {
			message.setText("YES");
			this.send(message, output);
			return true;
		}

		if (servidor.retornarListaUsuariosOnline().containsKey(message.getName())) {
			message.setText("NO");
			this.send(message, output);
			return false;
		}

		message.setText("YES");
		this.send(message, output);
		return true;
	}

	private void disconnect(Mensagem message, ObjectOutputStream output) {
		//FAZ O PROCESSO DE DESCONECTAR O USUARIO, REMOVENDO ELE DA LISTA E MOSTRANDO UMA MENSAGEM PARA TODOS OS OUTROS USUARIOS QUE ELE SE DESCONECTOU
		
		servidor.getGui().sendLog("> Usuário " + message.getName() + " desconectou do servidor.");
		
		this.servidor.removeCliente(message.getName());
		
		message.setText("deixou o chat!");

		message.setAction(Action.SEND_ONE);
		sendAll(message);
	}
	
	private void send(Mensagem message, ObjectOutputStream output) {
		try {
			output.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendOne(Mensagem message) {
		for (Map.Entry<String, ObjectOutputStream> cliente : this.servidor.retornarListaUsuariosOnline().entrySet()) {
			if (cliente.getKey().equals(message.getDestinatario())) {
				
				if (message.getFile() != null) {
					message.setAction(Action.SEND_FILE);
					servidor.getGui().sendLog("> " + cliente.getKey() + " recebeu um arquivo de forma privada.");
				}
				
				try {
					cliente.getValue().writeObject(message);
					servidor.getGui().sendLog("> " + cliente.getKey() + " recebeu uma mensagem de forma privada.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sendAll(Mensagem message) {
		String remetente = null;
		try {
			for (Map.Entry<String, ObjectOutputStream> cliente : this.servidor.retornarListaUsuariosOnline().entrySet()) {
				if (!cliente.getKey().equals(message.getName())) {
					
					if (message.getFile() != null) {
						message.setAction(Action.SEND_FILE);
						try {
							cliente.getValue().writeObject(message);
						} catch (Exception e) {
							//e.printStackTrace();
						}
					} else {
						message.setAction(Action.SEND_ONE);
						try {
							cliente.getValue().writeObject(message);
						} catch (Exception e) {
							//e.printStackTrace();
						}
					}
				}else {
					remetente = cliente.getKey();
				}
			}
		} catch (ConcurrentModificationException e) {
			// TODO: handle exception
		}
		if (remetente != null) {
			if (message.getAction().equals(Action.SEND_FILE)) {
				servidor.getGui().sendLog("> " + remetente + " enviou um arquivo para todos.");
			} else if (message.getAction().equals(Action.SEND_ONE)) {
				servidor.getGui().sendLog("> " + remetente + " enviou uma mensagem para todos.");
			}
		}
	}

	private void sendOnlines() {
		Set<String> setNames = new HashSet<String>();
		for (Map.Entry<String, ObjectOutputStream> cliente : this.servidor.retornarListaUsuariosOnline().entrySet()) {
			setNames.add(cliente.getKey());
		}

		Mensagem message = new Mensagem();
		message.setAction(Action.USERS_ONLINE);
		message.setSetOnlines(setNames);

		for (Map.Entry<String, ObjectOutputStream> cliente : this.servidor.retornarListaUsuariosOnline().entrySet()) {
			message.setName(cliente.getKey());
			try {
				cliente.getValue().writeObject(message);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
}
