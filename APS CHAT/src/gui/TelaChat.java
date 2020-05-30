package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import cliente.ClienteListenerSocket;
import main.Mensagem;
import main.Mensagem.Action;
import cliente.Cliente;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import java.awt.Toolkit;

@SuppressWarnings("serial")
public class TelaChat extends JFrame {

	private JPanel contentPane;
	private JTextArea textAreaMsg;
	private JLabel lblUserSelected;
	private JButton btnDesmarcar;
	private JButton btnLimpaChat;
	private JButton btnAnexo;
	private JButton btnEnviarMsg;
	private JEditorPane editorPaneConversa;
	private JList<Object> jListUsuariosOnline;

	private String user;
	private Socket socket;
	private Mensagem message;
	private Cliente cliente;

	private ArrayList<String> listaMsg = new ArrayList<String>();

	public TelaChat(String user, Socket socket, Mensagem message, Cliente cliente) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TelaChat.class.getResource("/imgs/icon.png")));
		
		String className = getLookAndFeelClassName("Windows");
		try {
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
		}

		this.user = user;
		this.socket = socket;
		this.message = message;
		this.cliente = cliente;

		setTitle(this.user);
		setResizable(false);
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		setBounds(100, 100, 1000, 700);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		this.iniciarComponentes();
		this.setVisible(true);

		new Thread(new ClienteListenerSocket(this.socket, this)).start();
		this.cliente.send(this.message);
	}

	public void iniciarComponentes() {
		JPanel panel_usuarios = new JPanel();
		panel_usuarios.setBackground(SystemColor.activeCaption);
		panel_usuarios.setBounds(0, 0, 280, 661);
		contentPane.add(panel_usuarios);
		panel_usuarios.setLayout(null);

		jListUsuariosOnline = new JList<Object>();
		jListUsuariosOnline.setToolTipText("Double click desmarca usuario");
		jListUsuariosOnline.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					jListUsuariosOnline.clearSelection();
					lblUserSelected.setText("> Conversando com todos <");
				} else {
					if (jListUsuariosOnline.getSelectedValue() != null) {
						lblUserSelected.setText("> Conversando com " + (String) jListUsuariosOnline.getSelectedValue() + " <");
					}
				}
			}
		});
		jListUsuariosOnline.setFont(new Font("Arial", Font.PLAIN, 16));
		jListUsuariosOnline.setBackground(SystemColor.activeCaption);

		JScrollPane scrollPaneUsers = new JScrollPane(jListUsuariosOnline);
		scrollPaneUsers.setBackground(SystemColor.activeCaption);
		scrollPaneUsers.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(153, 180, 209), new Color(0, 0, 0)), "Lista de Usu\u00E1rios", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		scrollPaneUsers.setBounds(10, 11, 260, 575);
		panel_usuarios.add(scrollPaneUsers);

		btnDesmarcar = new JButton("Desmarcar");
		btnDesmarcar.setToolTipText("Desmarca os usu\u00E1rios selecionados na lista");
		btnDesmarcar.setFocusPainted(false);
		btnDesmarcar.setBounds(65, 600, 150, 40);
		panel_usuarios.add(btnDesmarcar);
		btnDesmarcar.addActionListener(event ->  {
			this.jListUsuariosOnline.clearSelection();
			lblUserSelected.setText("> Conversando com todos <");
		});

		JPanel panel_chat = new JPanel();
		panel_chat.setBackground(Color.WHITE);
		panel_chat.setBounds(280, 0, 704, 661);
		contentPane.add(panel_chat);
		panel_chat.setLayout(null);

		lblUserSelected = new JLabel("> Conversando com todos <");
		lblUserSelected.setToolTipText("Mostra para quem voc\u00EA est\u00E1 enviando a mensagem");
		lblUserSelected.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserSelected.setFont(new Font("Arial", Font.PLAIN, 14));
		lblUserSelected.setBorder(new EtchedBorder(EtchedBorder.LOWERED, SystemColor.activeCaption, SystemColor.menuText));
		lblUserSelected.setBounds(0, 0, 704, 30);
		panel_chat.add(lblUserSelected);

		editorPaneConversa = new JEditorPane();
		editorPaneConversa.setEditable(false);
		editorPaneConversa.setContentType("text/html");

		JScrollPane scrollPaneConversa = new JScrollPane(editorPaneConversa);
		scrollPaneConversa.setBorder(new EtchedBorder(EtchedBorder.LOWERED, SystemColor.activeCaption, SystemColor.menuText));
		scrollPaneConversa.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneConversa.setBounds(0, 30, 704, 470);
		panel_chat.add(scrollPaneConversa);

		btnLimpaChat = new JButton("Limpar");
		btnLimpaChat.setToolTipText("Limpa o chat");
		btnLimpaChat.setFocusPainted(false);
		btnLimpaChat.setBounds(630, 500, 74, 53);
		panel_chat.add(btnLimpaChat);
		btnLimpaChat.addActionListener(event -> {
			
			listaMsg.clear();
			appendMsg("");
		});

		btnAnexo = new JButton("Anexo");
		btnAnexo.setToolTipText("Anexa um arquivo e envia");
		btnAnexo.setFocusPainted(false);
		btnAnexo.setBounds(630, 553, 74, 53);
		panel_chat.add(btnAnexo);
		btnAnexo.addActionListener(event -> {
			
			eventoEnviarAnexo();
		});

		btnEnviarMsg = new JButton("Enviar");
		btnEnviarMsg.setToolTipText("Envia uma mensagem");
		btnEnviarMsg.setBackground(Color.WHITE);
		btnEnviarMsg.setFocusPainted(false);
		btnEnviarMsg.setBounds(630, 606, 74, 55);
		panel_chat.add(btnEnviarMsg);
		btnEnviarMsg.addActionListener(event -> {
			
			eventoEnviar();
		});

		textAreaMsg = new JTextArea();
		textAreaMsg.setLineWrap(true);
		textAreaMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					
					eventoEnviar();
				}
			}
		});

		JScrollPane scrollPaneAreaMsg = new JScrollPane(textAreaMsg);
		scrollPaneAreaMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneAreaMsg.setBorder(new EtchedBorder(EtchedBorder.LOWERED, SystemColor.activeCaption, SystemColor.menuText));
		scrollPaneAreaMsg.setBounds(0, 500, 630, 161);
		panel_chat.add(scrollPaneAreaMsg);
	}

	public void enviar(String remetente, String message) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
		String msg = "<b>[" + df.format(new Date()) + "] " + remetente + ": </b>" + message + "<br>";
		appendMsg(msg);
	}

	public void appendMsg(String msgRecebida) {
		listaMsg.add(msgRecebida);
		String msg = "";
		for (String s : listaMsg) {
			msg += s;
		}
		editorPaneConversa.setText(msg);
		
	}
	
	public void eventoEnviar() {
		this.message = new Mensagem();
		
		if (this.jListUsuariosOnline.getSelectedIndex() != -1) {
			
			this.message.setDestinatario((String) this.jListUsuariosOnline.getSelectedValue());
			this.message.setAction(Action.SEND_ONE);
			this.message.setText("<i>(mensagem privada)</i> " + textAreaMsg.getText());
			
		} else {
			this.message.setAction(Action.SEND_ALL);
			this.message.setText(textAreaMsg.getText());
		}
		
		if (!textAreaMsg.getText().isEmpty()) {
			
			this.message.setName(user);
			this.enviar(user, textAreaMsg.getText());
			this.cliente.send(this.message);
		}
		
		this.textAreaMsg.setText("");
	}
	
	public void eventoEnviarAnexo() {
		File file = getFile();
		
		if (file != null) {
			this.message = new Mensagem();
			this.message.setName(user);
			this.message.setFile(file);
			
			if (this.jListUsuariosOnline.getSelectedIndex() != -1) {
				
				this.message.setDestinatario((String) this.jListUsuariosOnline.getSelectedValue());
				this.message.setAction(Action.SEND_ONE);
				this.message.setText("Você recebeu '" + this.message.getFile().getName() + "' de " + this.user + " de forma privada.");
			} else {
				this.message.setAction(Action.SEND_ALL);
				this.message.setText("Você recebeu '" + this.message.getFile().getName() + "' de " + this.user + ".");
			}
			
			this.enviar(user, "Arquivo '" + this.message.getFile().getName() + "' enviado com sucesso!");
			this.cliente.send(this.message);
		}
	}

	public void closeClient() {
		message.setAction(Action.DISCONNECT);
		cliente.send(message);
		
		this.dispose();
	}
	
	public File getFile() {
		File file = null;
		JFileChooser fc = new JFileChooser();
		
		int res = fc.showOpenDialog(this);

		if (res == JFileChooser.APPROVE_OPTION && !(new File(fc.getSelectedFile().getAbsolutePath())).exists()) {
			JOptionPane.showMessageDialog(this, "Arquivo não existe!");
		} else if (res == JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(this, "Voce escolheu o arquivo: " + fc.getSelectedFile().getName());
			file = fc.getSelectedFile();
		} else {
			JOptionPane.showMessageDialog(this, "Voce nao selecionou nenhum arquivo.");
		}
		return file;
	}

	public static String getLookAndFeelClassName(String nameSnippet) {
	    LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
	    for (LookAndFeelInfo info : plafs) {
	        if (info.getName().contains(nameSnippet)) {
	            return info.getClassName();
	        }
	    }
	    return null;
	}
	
	public Mensagem getMessage() {
		return message;
	}

	public void setMessage(Mensagem message) {
		this.message = message;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public JList<Object> getjListUsuariosOnline() {
		return jListUsuariosOnline;
	}

	public void setjListUsuariosOnline(JList<Object> jListUsuariosOnline) {
		this.jListUsuariosOnline = jListUsuariosOnline;
	}
	
}
