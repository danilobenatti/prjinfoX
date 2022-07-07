package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class TelaUsuario extends javax.swing.JInternalFrame {

	private static Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private ResultSetMetaData resultSetMetaData;

	/**
	 * Creates new form TelaUsuario
	 */
	public TelaUsuario() {
		initComponents();
	}

	public static void setValues(PreparedStatement ps, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			ps.setObject(i + 1, values[i]);
		}
	}

	public static void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				e.printStackTrace(System.err);
				Throwable throwable = ex.getCause();
				while (throwable != null) {
					System.out.println("Cause: " + throwable);
					throwable = throwable.getCause();
				}
			}
		}
	}

	private void clearUserFields() {
		jTextFieldIdUser.setText(null);
		jTextFieldUsuario.setText(null);
		jTextFieldFone.setText(null);
		jTextFieldLogin.setText(null);
		jPasswordFieldSenha.setText(null);
	}

	//create
	private void addOneUser() {
		String insert = "INSERT INTO tbusuarios"
			+ " (usuario,fone,login,senha,perfil) VALUES (?,?,?,?,?)";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, jTextFieldUsuario.getText());
			preparedStatement.setString(2, jTextFieldFone.getText());
			preparedStatement.setString(3, jTextFieldLogin.getText());
			preparedStatement.setString(4, new String(jPasswordFieldSenha.getPassword()));
			preparedStatement.setString(5, jComboBoxPerfil.getSelectedItem().toString());
			int insertOk = preparedStatement.executeUpdate();
			if (insertOk > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				resultSetMetaData = resultSet.getMetaData();
				while (resultSet.next()) {
					System.out.printf("%s: %s\n",
						resultSetMetaData.getColumnName(1), resultSet.getInt(1));
					JOptionPane.showMessageDialog(null,
						"Usuário id(" + resultSet.getInt(1) + ") foi adicionado!");
				}
			}
		} catch (SQLIntegrityConstraintViolationException ex) {
			JOptionPane.showMessageDialog(null, "Login de usuário já existe!");
		} catch (SQLException ex) {
			printSQLException(ex);
		} catch (HeadlessException ex) {
			System.err.println("Error: " + ex.getMessage());
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//read
	private void finderOneUserById(Integer idUser) {
		String select = "SELECT * FROM tbusuarios WHERE iduser = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(select);
			preparedStatement.setInt(1, idUser);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				jTextFieldIdUser.setText(resultSet.getString("iduser"));
				jTextFieldUsuario.setText(resultSet.getString("usuario"));
				jTextFieldFone.setText(resultSet.getString("fone"));
				jTextFieldLogin.setText(resultSet.getString("login"));
				jPasswordFieldSenha.setText(resultSet.getString("senha"));
				jComboBoxPerfil.setSelectedItem(resultSet.getString("perfil"));
			} else {
				JOptionPane.showMessageDialog(null, "Usuário não encontrado!");
			}
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException ex) {
			Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//update
	private void updateOneUserById(Integer idUser) {
		String update = "UPDATE tbusuarios SET "
			+ "usuario = ?, fone = ?, login = ?, senha = ?, perfil = ? "
			+ "WHERE iduser = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(update,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			preparedStatement.setString(1, jTextFieldUsuario.getText());
			preparedStatement.setString(2, jTextFieldFone.getText());
			preparedStatement.setString(3, jTextFieldLogin.getText());
			preparedStatement.setString(4, new String(jPasswordFieldSenha.getPassword()));
			preparedStatement.setString(5, jComboBoxPerfil.getSelectedItem().toString());
			preparedStatement.setInt(6, idUser);
			int updateOk = preparedStatement.executeUpdate();
			if (updateOk > 0) {
				JOptionPane.showMessageDialog(null,
					"Usuário id(" + idUser + ") foi atualizado!");
			}
		} catch (SQLIntegrityConstraintViolationException ex) {
			JOptionPane.showMessageDialog(null, "Login de usuário já existe!");
		} catch (SQLException ex) {
			printSQLException(ex);
		} catch (HeadlessException ex) {
			System.err.println("Error: " + ex.getMessage());
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//delete
	private void deleteOneUserById(Integer idUser) {
		String delete = "DELETE FROM tbusuarios WHERE iduser = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(delete);
			preparedStatement.setInt(1, idUser);
			int deleteOk = preparedStatement.executeUpdate();
			if (deleteOk > 0) {
				JOptionPane.showMessageDialog(null, "Usuário removido.",
					"REMOÇÃO REALIZADA!", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
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

        jLabelIdUser = new javax.swing.JLabel();
        jTextFieldIdUser = new javax.swing.JTextField();
        jLabelUsuario = new javax.swing.JLabel();
        jTextFieldUsuario = new javax.swing.JTextField();
        jLabelFone = new javax.swing.JLabel();
        jTextFieldFone = new javax.swing.JTextField();
        jLabelLogin = new javax.swing.JLabel();
        jTextFieldLogin = new javax.swing.JTextField();
        jLabelSenha = new javax.swing.JLabel();
        jPasswordFieldSenha = new javax.swing.JPasswordField();
        jLabelPerfil = new javax.swing.JLabel();
        jComboBoxPerfil = new javax.swing.JComboBox<>();
        jButtonUserCreate = new javax.swing.JButton();
        jButtonUserRead = new javax.swing.JButton();
        jButtonUserUpdate = new javax.swing.JButton();
        jButtonUserDelete = new javax.swing.JButton();
        jLabelInfo = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Usuários");
        setPreferredSize(new java.awt.Dimension(650, 450));

        jLabelIdUser.setLabelFor(jTextFieldIdUser);
        jLabelIdUser.setText("Id");

        jTextFieldIdUser.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldIdUser.setPreferredSize(new java.awt.Dimension(100, 22));
        jTextFieldIdUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldIdUserFocusGained(evt);
            }
        });

        jLabelUsuario.setLabelFor(jTextFieldUsuario);
        jLabelUsuario.setText("* Usuario");

        jTextFieldUsuario.setPreferredSize(new java.awt.Dimension(280, 22));
        jTextFieldUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldUsuarioFocusGained(evt);
            }
        });

        jLabelFone.setLabelFor(jTextFieldFone);
        jLabelFone.setText("Fone");

        jTextFieldFone.setPreferredSize(new java.awt.Dimension(110, 22));
        jTextFieldFone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldFoneFocusGained(evt);
            }
        });

        jLabelLogin.setLabelFor(jTextFieldLogin);
        jLabelLogin.setText("* Login");

        jTextFieldLogin.setPreferredSize(new java.awt.Dimension(100, 22));
        jTextFieldLogin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldLoginFocusGained(evt);
            }
        });

        jLabelSenha.setLabelFor(jPasswordFieldSenha);
        jLabelSenha.setText("* Senha");

        jPasswordFieldSenha.setPreferredSize(new java.awt.Dimension(100, 22));
        jPasswordFieldSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordFieldSenhaFocusGained(evt);
            }
        });

        jLabelPerfil.setLabelFor(jComboBoxPerfil);
        jLabelPerfil.setText("* Perfil");

        jComboBoxPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "user", "guest" }));
        jComboBoxPerfil.setSelectedIndex(1);
        jComboBoxPerfil.setPreferredSize(new java.awt.Dimension(110, 22));

        jButtonUserCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconCreate.png"))); // NOI18N
        jButtonUserCreate.setToolTipText("Adicionar informações de um usuário");
        jButtonUserCreate.setBorder(null);
        jButtonUserCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonUserCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserCreateActionPerformed(evt);
            }
        });

        jButtonUserRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconRead.png"))); // NOI18N
        jButtonUserRead.setToolTipText("Visualizar informações de um usuário");
        jButtonUserRead.setBorder(null);
        jButtonUserRead.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonUserRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserReadActionPerformed(evt);
            }
        });

        jButtonUserUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconUpdate.png"))); // NOI18N
        jButtonUserUpdate.setToolTipText("Atualizar informações de um usuário");
        jButtonUserUpdate.setBorder(null);
        jButtonUserUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonUserUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserUpdateActionPerformed(evt);
            }
        });

        jButtonUserDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconDelete.png"))); // NOI18N
        jButtonUserDelete.setToolTipText("Apagar informações de um usuário");
        jButtonUserDelete.setBorder(null);
        jButtonUserDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonUserDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserDeleteActionPerformed(evt);
            }
        });

        jLabelInfo.setText("* Campos obrigatórios");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelIdUser, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelUsuario, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelLogin, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelSenha, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPasswordFieldSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextFieldLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelFone, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabelPerfil, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldFone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxPerfil, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jTextFieldIdUser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonUserCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonUserRead)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonUserUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonUserDelete)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                .addComponent(jLabelInfo)
                .addGap(20, 20, 20))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonUserCreate, jButtonUserDelete, jButtonUserRead, jButtonUserUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIdUser)
                    .addComponent(jTextFieldIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelInfo))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUsuario)
                    .addComponent(jTextFieldUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLogin)
                    .addComponent(jTextFieldFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFone))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordFieldSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSenha)
                    .addComponent(jComboBoxPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPerfil))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUserCreate)
                    .addComponent(jButtonUserRead)
                    .addComponent(jButtonUserUpdate)
                    .addComponent(jButtonUserDelete))
                .addContainerGap(173, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonUserCreate, jButtonUserDelete, jButtonUserRead, jButtonUserUpdate});

        setBounds(0, 0, 650, 450);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUserReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUserReadActionPerformed
		if (!jTextFieldIdUser.getText().isEmpty()) {
			finderOneUserById(Integer.parseInt(jTextFieldIdUser.getText()));
		} else {
			JOptionPane.showMessageDialog(null, "Necessário informar número id para pesquisa.");
			clearUserFields();
		}
    }//GEN-LAST:event_jButtonUserReadActionPerformed

    private void jButtonUserCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUserCreateActionPerformed
		if (!jTextFieldUsuario.getText().isEmpty()
			&& !jTextFieldLogin.getText().isEmpty()
			&& jPasswordFieldSenha.getPassword().length > 0
			&& !jComboBoxPerfil.getSelectedItem().equals("")) {
			addOneUser();
			clearUserFields();
		} else {
			JOptionPane.showMessageDialog(null, "Campos com (*) são obrigatórios!");
		}
    }//GEN-LAST:event_jButtonUserCreateActionPerformed

    private void jTextFieldIdUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldIdUserFocusGained
		this.jTextFieldIdUser.selectAll();
    }//GEN-LAST:event_jTextFieldIdUserFocusGained

    private void jTextFieldUsuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldUsuarioFocusGained
		this.jTextFieldUsuario.selectAll();
    }//GEN-LAST:event_jTextFieldUsuarioFocusGained

    private void jTextFieldFoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldFoneFocusGained
		this.jTextFieldFone.selectAll();
    }//GEN-LAST:event_jTextFieldFoneFocusGained

    private void jTextFieldLoginFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldLoginFocusGained
		this.jTextFieldLogin.selectAll();
    }//GEN-LAST:event_jTextFieldLoginFocusGained

    private void jPasswordFieldSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordFieldSenhaFocusGained
		this.jPasswordFieldSenha.selectAll();
    }//GEN-LAST:event_jPasswordFieldSenhaFocusGained

    private void jButtonUserUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUserUpdateActionPerformed
		if (!jTextFieldIdUser.getText().isEmpty()
			&& !jTextFieldUsuario.getText().isEmpty()
			&& !jTextFieldLogin.getText().isEmpty()
			&& jPasswordFieldSenha.getPassword().length > 0
			&& !jComboBoxPerfil.getSelectedItem().equals("")) {
			updateOneUserById(Integer.parseInt(jTextFieldIdUser.getText()));
			clearUserFields();
		} else {
			JOptionPane.showMessageDialog(null, "Campos com (*) são obrigatórios!");
		}
    }//GEN-LAST:event_jButtonUserUpdateActionPerformed

    private void jButtonUserDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUserDeleteActionPerformed
		if (!jTextFieldIdUser.getText().isEmpty()) {
			int idUser = Integer.parseInt(jTextFieldIdUser.getText());
			String nameUser = jTextFieldUsuario.getText();
			Object message = "Deseja remover '" + nameUser + "' id: " + idUser;
			int showConfirmDialog = JOptionPane.showConfirmDialog(null, message,
				"EXCLUIR USUÁRIO", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (showConfirmDialog == JOptionPane.YES_OPTION) {
				deleteOneUserById(idUser);
				clearUserFields();
			} else {
				JOptionPane.showMessageDialog(null, "Operação de exclusão cancelada");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Para excluir um usuário "
				+ "é necessário informar o id.");
		}
    }//GEN-LAST:event_jButtonUserDeleteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonUserCreate;
    private javax.swing.JButton jButtonUserDelete;
    private javax.swing.JButton jButtonUserRead;
    private javax.swing.JButton jButtonUserUpdate;
    private javax.swing.JComboBox<String> jComboBoxPerfil;
    private javax.swing.JLabel jLabelFone;
    private javax.swing.JLabel jLabelIdUser;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelLogin;
    private javax.swing.JLabel jLabelPerfil;
    private javax.swing.JLabel jLabelSenha;
    private javax.swing.JLabel jLabelUsuario;
    private javax.swing.JPasswordField jPasswordFieldSenha;
    private javax.swing.JTextField jTextFieldFone;
    private javax.swing.JTextField jTextFieldIdUser;
    private javax.swing.JTextField jTextFieldLogin;
    private javax.swing.JTextField jTextFieldUsuario;
    // End of variables declaration//GEN-END:variables
}
