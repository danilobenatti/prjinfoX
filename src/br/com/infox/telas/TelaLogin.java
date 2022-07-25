package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class TelaLogin extends javax.swing.JFrame {

	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	public void logar() {
		String sql = "SELECT * FROM tbusuarios WHERE login = ? AND senha = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, this.jTextFieldLoginUsuario.getText());
			preparedStatement.setString(2, new String(this.jPasswordFieldLoginSenha.getPassword()));
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				TelaPrincipal principal = new TelaPrincipal();
				TelaPrincipal.jLabelUserName.setText(resultSet.getString("usuario"));
				switch (resultSet.getString("perfil")) {
					case "admin":
						principal.setVisible(true);
						TelaPrincipal.jMenuCadastroUsuarios.setEnabled(true);
						TelaPrincipal.jMenuRelatorio.setEnabled(true);
						TelaPrincipal.jLabelUserName.setForeground(Color.red);
						this.dispose();
						break;
					case "user":
						principal.setVisible(true);
						TelaPrincipal.jMenuCadastroUsuarios.setEnabled(true);
						TelaPrincipal.jLabelUserName.setForeground(Color.blue);
						this.dispose();
						break;
					case "guest":
						principal.setVisible(true);
						TelaPrincipal.jLabelUserName.setForeground(Color.gray);
						this.dispose();
						break;
					default:
						throw new AssertionError();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Usuário e/ou senha inválido(s)!",
					"Autenticação inválida!", JOptionPane.WARNING_MESSAGE);
			}
		} catch (SQLException ex) {
			Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	private void checkLoginData() {
		if (!this.jTextFieldLoginUsuario.getText().isEmpty()
			&& this.jPasswordFieldLoginSenha.getPassword().length > 0) {
			logar();
		} else {
			JOptionPane.showMessageDialog(null,
				"Necessário informar login e senha", "Dados necessários",
				JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Creates new form TelaLogin
	 */
	public TelaLogin() {
		initComponents();
		this.jTextFieldLoginUsuario.grabFocus();
		try {
			connection = ModuloConexao.connection();
			if (connection != null) {
				this.jLabelStatusConnection.setIcon(
					new ImageIcon(getClass().getResource("/br/com/infox/icons/checkOK.png")));
				this.jLabelStatusConnection.setText("Database OK!");
			} else {
				this.jLabelStatusConnection.setIcon(
					new ImageIcon(getClass().getResource("/br/com/infox/icons/checkError.png")));
				this.jLabelStatusConnection.setText("Database Falhou!");
			}
		} catch (Exception ex) {
			Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			ModuloConexao.fecharConexao(connection, null);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelLoginUsuario = new javax.swing.JLabel();
        jTextFieldLoginUsuario = new javax.swing.JTextField();
        jLabelLoginSenha = new javax.swing.JLabel();
        jPasswordFieldLoginSenha = new javax.swing.JPasswordField();
        btnLoginEntrar = new javax.swing.JButton();
        jLabelStatusConnection = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("X System - Login");
        setPreferredSize(new java.awt.Dimension(365, 200));
        setResizable(false);

        jLabelLoginUsuario.setText("Usuário");

        jTextFieldLoginUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldLoginUsuarioKeyPressed(evt);
            }
        });

        jLabelLoginSenha.setText("Senha");

        jPasswordFieldLoginSenha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordFieldLoginSenhaKeyPressed(evt);
            }
        });

        btnLoginEntrar.setText("Entrar");
        btnLoginEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginEntrarActionPerformed(evt);
            }
        });

        jLabelStatusConnection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/checkQuestion.png"))); // NOI18N
        jLabelStatusConnection.setText("Status of DB Conection");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelStatusConnection)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelLoginUsuario)
                            .addComponent(jLabelLoginSenha))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPasswordFieldLoginSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldLoginUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnLoginEntrar, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldLoginUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLoginUsuario))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordFieldLoginSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLoginSenha))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoginEntrar)
                    .addComponent(jLabelStatusConnection))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(312, 205));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginEntrarActionPerformed
		checkLoginData();
    }//GEN-LAST:event_btnLoginEntrarActionPerformed

    private void jPasswordFieldLoginSenhaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldLoginSenhaKeyPressed
		if (evt.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
			checkLoginData();
		}
		if (evt.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
    }//GEN-LAST:event_jPasswordFieldLoginSenhaKeyPressed

    private void jTextFieldLoginUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldLoginUsuarioKeyPressed
		if (evt.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
			if (!this.jTextFieldLoginUsuario.getText().isEmpty()) {
				this.jPasswordFieldLoginSenha.grabFocus();
			} else {
				JOptionPane.showMessageDialog(null,
					"Necessário informar login do usuário", "Informar Login!",
					JOptionPane.WARNING_MESSAGE);
			}
		}
    }//GEN-LAST:event_jTextFieldLoginUsuarioKeyPressed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(() -> {
			new TelaLogin().setVisible(true);
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoginEntrar;
    private javax.swing.JLabel jLabelLoginSenha;
    private javax.swing.JLabel jLabelLoginUsuario;
    private javax.swing.JLabel jLabelStatusConnection;
    private javax.swing.JPasswordField jPasswordFieldLoginSenha;
    private javax.swing.JTextField jTextFieldLoginUsuario;
    // End of variables declaration//GEN-END:variables
}