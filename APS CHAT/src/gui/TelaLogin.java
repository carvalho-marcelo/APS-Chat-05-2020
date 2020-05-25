package gui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextField;

import cliente.Cliente;
import main.Mensagem;
import main.Mensagem.Action;

import java.awt.Font;
import java.net.Socket;
import javax.swing.SwingConstants;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class TelaLogin extends JFrame {

	private JPanel contentPane;
	private JTextField tfNome;
	private JButton btnLogin;

	private Socket socket;
	private Mensagem message;
	private Cliente service;
	private JTextField tfPort;

	public TelaLogin() {
		setTitle("Cliente");
		setIconImage(Toolkit.getDefaultToolkit().getImage(TelaLogin.class.getResource("/imgs/icon.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		this.iniciarComponentes();
		this.setVisible(true);
	}

	public void iniciarComponentes() {
		
		JLabel lblNome = new JLabel("Username:");
		lblNome.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNome.setFont(new Font("Arial", Font.PLAIN, 20));
		lblNome.setBounds(30, 50, 100, 40);
		contentPane.add(lblNome);

		btnLogin = new JButton("Login");
		btnLogin.setFocusPainted(false);
		btnLogin.setFont(new Font("Arial", Font.PLAIN, 18));
		btnLogin.setBounds(270, 210, 140, 40);
		btnLogin.setActionCommand("login");
		contentPane.add(btnLogin);
		btnLogin.addActionListener(event -> {

			String nome = tfNome.getText();

			if (!nome.isEmpty() && !tfPort.getText().isEmpty()) {
				try {
					if (Integer.parseInt(tfPort.getText()) > 1024 && Integer.parseInt(tfPort.getText()) < 49152) {
						this.message = new Mensagem();
						this.message.setAction(Action.CONNECT);
						this.message.setName(nome);

						this.service = new Cliente();
						this.socket = this.service.connect(Integer.parseInt(tfPort.getText()), this);

						this.dispose();
						new TelaChat(tfNome.getText(), socket, message, service);
					} else {
						JOptionPane.showMessageDialog(this, "Informe um valor para porta entre 1024 e 49152", "Erro", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Informe um valor para porta entre 1024 e 49152", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Por favor informe um nome de Usuário e/ou porta", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		});

		tfNome = new JTextField();
		tfNome.setToolTipText("Informe um nome de usu\u00E1rio \u00FAnico");
		tfNome.setFont(new Font("Arial", Font.PLAIN, 20));
		tfNome.setBounds(140, 50, 270, 40);
		contentPane.add(tfNome);
		tfNome.setColumns(10);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPort.setFont(new Font("Arial", Font.PLAIN, 20));
		lblPort.setBounds(30, 101, 100, 40);
		contentPane.add(lblPort);

		tfPort = new JTextField();
		tfPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String caracteres="0987654321";
				if(!caracteres.contains(e.getKeyChar()+"")){
				e.consume();
				}
			}
		});
		tfPort.setToolTipText("Informe um valor entre 1024 e 49152");
		tfPort.setText("12345");
		tfPort.setFont(new Font("Arial", Font.PLAIN, 20));
		tfPort.setColumns(10);
		tfPort.setBounds(140, 101, 270, 40);
		contentPane.add(tfPort);

	}
}
