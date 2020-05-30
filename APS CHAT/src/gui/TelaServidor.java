package gui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import servidor.Servidor;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import java.awt.SystemColor;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.SwingConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class TelaServidor extends JFrame {

	private JPanel contentPane;
	private JButton btnParar;
	private JButton btnIniciar;
	private JEditorPane editorPaneLog;
	private JTextField tfPort;
	private Servidor servidor;
	
	private ArrayList<String> listaMsg = new ArrayList<String>();

	public TelaServidor() {
		setBackground(SystemColor.window);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 150, 1000, 510);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("activeCaption"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		setTitle("Servidor");
		setIconImage(Toolkit.getDefaultToolkit().getImage(TelaServidor.class.getResource("/imgs/icon.png")));
		setResizable(false);
		
		this.iniciarComponentes();
		this.setVisible(true);
	}

	public void iniciarComponentes() {
		contentPane.setLayout(null);
		
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
		tfPort.setFont(new Font("Arial", Font.PLAIN, 18));
		tfPort.setBounds(85, 410, 150, 30);
		contentPane.add(tfPort);
		tfPort.setColumns(10);
		tfPort.setText("12345");
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Arial", Font.PLAIN, 18));
		lblPort.setBounds(35, 410, 40, 30);
		contentPane.add(lblPort);
		
		btnIniciar = new JButton("START");
		btnIniciar.setFont(new Font("Arial", Font.PLAIN, 18));
		btnIniciar.setFocusPainted(false);
		btnIniciar.setBounds(350, 391, 240, 60);
		contentPane.add(btnIniciar);
		btnIniciar.addActionListener(event -> {
			if (tfPort.getText() != null || tfPort.getText() != "0") {
				try {
					if (Integer.parseInt(tfPort.getText()) > 1024 && Integer.parseInt(tfPort.getText()) < 49152) {
						servidor = new Servidor(Integer.parseInt(tfPort.getText()), this);

						new Thread(servidor).start();
					}
					else {
						JOptionPane.showMessageDialog(this, "Informe um valor entre 1024 e 49152", "Erro", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Informe um valor entre 1024 e 49152", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Informe uma porta!", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		btnParar = new JButton("STOP");
		btnParar.setEnabled(false);
		btnParar.setFont(new Font("Arial", Font.PLAIN, 18));
		btnParar.setFocusPainted(false);
		btnParar.setBounds(662, 391, 240, 60);
		contentPane.add(btnParar);
		btnParar.addActionListener(event -> {
			servidor.stop();
			tfPort.setEnabled(true);
			btnParar.setEnabled(false);
			btnIniciar.setEnabled(true);
		});
		
		editorPaneLog = new JEditorPane();
		editorPaneLog.setFont(new Font("Arial", Font.PLAIN, 12));
		editorPaneLog.setDisabledTextColor(SystemColor.text);
		editorPaneLog.setBackground(Color.WHITE);
		editorPaneLog.setEditable(false);
		editorPaneLog.setContentType("text/html");
		
		JScrollPane scrollPane = new JScrollPane(editorPaneLog);
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(270, 20, 700, 360);
		contentPane.add(scrollPane);
		
		JLabel lblLogo = new JLabel();
		lblLogo.setIcon(new ImageIcon(TelaServidor.class.getResource("/imgs/icon.png")));
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setBounds(20, 20, 230, 200);
		contentPane.add(lblLogo);
		
		JLabel lblNewLabel = new JLabel("Amazon Pretty Safe");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		lblNewLabel.setBounds(20, 231, 230, 30);
		contentPane.add(lblNewLabel);
		
	}
	
	public void sendLog(String message) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
		String msg = "<b>[" + df.format(new Date()) + "]:</b> " + message + "<br>";
		appendMsg(msg);
	}

	public void appendMsg(String msgRecebida) {
		listaMsg.add(msgRecebida);
		String msg = "";
		try {
			for (String s : listaMsg) {
				msg += s;
			}
		} catch (ConcurrentModificationException e) {
			// TODO: handle exception
		}
		editorPaneLog.setText(msg);
	}

	public JButton getBtnParar() {
		return btnParar;
	}

	public JButton getBtnIniciar() {
		return btnIniciar;
	}

	public JTextField getTfPort() {
		return tfPort;
	}
}
