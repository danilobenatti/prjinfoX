package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import net.proteanit.sql.DbUtils;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TelaCliente extends javax.swing.JInternalFrame {

	private static Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private ResultSetMetaData resultSetMetaData;

	/**
	 * Creates new form TelaCliente
	 */
	public TelaCliente() {
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

	private void clearClientFields() {
		jTextFieldIdClient.setText(null);
		jTextFieldNome.setText(null);
		jTextFieldEndereco.setText(null);
		jTextFieldTelefone.setText(null);
		jTextFieldEmail.setText(null);
		jTextFieldPesquisar.setText(null);
		((DefaultTableModel) jTableClients.getModel()).setRowCount(0);
	}

	//create
	private void addOneClient() {
		String insert = "INSERT INTO tbclientes"
			+ " (nomecli,endcli,fonecli,emailcli) VALUES (?,?,?,?)";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, jTextFieldNome.getText());
			preparedStatement.setString(2, jTextFieldEndereco.getText());
			preparedStatement.setString(3, jTextFieldTelefone.getText());
			preparedStatement.setString(4, jTextFieldEmail.getText());
			int insertOk = preparedStatement.executeUpdate();
			if (insertOk > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				resultSetMetaData = resultSet.getMetaData();
				while (resultSet.next()) {
					System.out.printf("%s: %s\n",
						resultSetMetaData.getColumnName(1), resultSet.getInt(1));
					JOptionPane.showMessageDialog(null,
						"Cliente id(" + resultSet.getInt(1) + ") foi adicionado!");
				}
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} catch (HeadlessException ex) {
			System.err.println("Error: " + ex.getMessage());
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//read
	private void finderOneClientByName(String name) {
		String select = "SELECT idcli AS ID, nomecli AS Nome, "
			+ "endcli AS Endereço, fonecli AS Telefone, emailcli AS Email "
			+ "FROM tbclientes WHERE lower(nomecli) LIKE lower(?)";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(select);
			preparedStatement.setString(1, "%" + name + "%");
			resultSet = preparedStatement.executeQuery();
			jTableClients.setModel(DbUtils.resultSetToTableModel(resultSet));
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException ex) {
			Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//update
	private void updateOneClientById(Integer idClient) {
		String update = "UPDATE tbclientes SET "
			+ "nomecli = ?, endcli = ?, fonecli = ?, emailcli = ? WHERE idcli = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(update,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			preparedStatement.setString(1, jTextFieldNome.getText());
			preparedStatement.setString(2, jTextFieldEndereco.getText());
			preparedStatement.setString(3, jTextFieldTelefone.getText());
			preparedStatement.setString(4, jTextFieldEmail.getText());
			preparedStatement.setInt(5, idClient);
			int updateOk = preparedStatement.executeUpdate();
			if (updateOk > 0) {
				JOptionPane.showMessageDialog(null,
					"Cliente id(" + idClient + ") foi atualizado!");
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} catch (HeadlessException ex) {
			System.err.println("Error: " + ex.getMessage());
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//delete
	private void deleteOneClientById(Integer idClient) {
		String delete = "DELETE FROM tbclientes WHERE idcli = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(delete);
			preparedStatement.setInt(1, idClient);
			int deleteOk = preparedStatement.executeUpdate();
			if (deleteOk > 0) {
				JOptionPane.showMessageDialog(null, "Cliente removido.",
					"REMOÇÃO REALIZADA!", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	public void setFieldsClient() {
		int set = jTableClients.getSelectedRow();
		jTextFieldIdClient.setText(jTableClients.getModel().getValueAt(set, 0).toString());
		jTextFieldNome.setText(jTableClients.getModel().getValueAt(set, 1).toString());
		jTextFieldEndereco.setText(jTableClients.getModel().getValueAt(set, 2).toString());
		jTextFieldTelefone.setText(jTableClients.getModel().getValueAt(set, 3).toString());
		jTextFieldEmail.setText(jTableClients.getModel().getValueAt(set, 4).toString());
		jButtonClientCreate.setEnabled(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelIdClient = new javax.swing.JLabel();
        jTextFieldIdClient = new javax.swing.JTextField();
        jLabelNome = new javax.swing.JLabel();
        jTextFieldNome = new javax.swing.JTextField();
        jLabelEndereco = new javax.swing.JLabel();
        jTextFieldEndereco = new javax.swing.JTextField();
        jLabelTelefone = new javax.swing.JLabel();
        jTextFieldTelefone = new javax.swing.JTextField();
        jLabelEmail = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jButtonClientCreate = new javax.swing.JButton();
        jButtonClientUpdate = new javax.swing.JButton();
        jButtonClientDelete = new javax.swing.JButton();
        jLabelPesquisar = new javax.swing.JLabel();
        jTextFieldPesquisar = new javax.swing.JTextField();
        jLabelPesquisarIcone = new javax.swing.JLabel();
        jScrollPaneClients = new javax.swing.JScrollPane();
        jTableClients = new javax.swing.JTable();
        jLabelInfo = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Clientes");
        setPreferredSize(new java.awt.Dimension(650, 450));

        jLabelIdClient.setText("Id");

        jTextFieldIdClient.setEditable(false);
        jTextFieldIdClient.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldIdClient.setPreferredSize(new java.awt.Dimension(100, 22));

        jLabelNome.setLabelFor(jLabelNome);
        jLabelNome.setText("* Nome");

        jTextFieldNome.setToolTipText("Nome completo do cliente");
        jTextFieldNome.setPreferredSize(new java.awt.Dimension(280, 22));
        jTextFieldNome.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldNomeFocusGained(evt);
            }
        });

        jLabelEndereco.setLabelFor(jTextFieldEndereco);
        jLabelEndereco.setText("Endereço");

        jTextFieldEndereco.setToolTipText("Endereço atual para contato");
        jTextFieldEndereco.setPreferredSize(new java.awt.Dimension(384, 22));
        jTextFieldEndereco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldEnderecoFocusGained(evt);
            }
        });

        jLabelTelefone.setLabelFor(jTextFieldTelefone);
        jLabelTelefone.setText("* Telefone");

        jTextFieldTelefone.setToolTipText("Principal telefone para contato");
        jTextFieldTelefone.setPreferredSize(new java.awt.Dimension(128, 22));
        jTextFieldTelefone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldTelefoneFocusGained(evt);
            }
        });

        jLabelEmail.setLabelFor(jTextFieldEmail);
        jLabelEmail.setText("* E-mail");

        jTextFieldEmail.setToolTipText("Principal e-mail para contato");
        jTextFieldEmail.setPreferredSize(new java.awt.Dimension(192, 22));
        jTextFieldEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldEmailFocusGained(evt);
            }
        });

        jButtonClientCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconCreate.png"))); // NOI18N
        jButtonClientCreate.setToolTipText("Adicionar um cliente");
        jButtonClientCreate.setBorder(null);
        jButtonClientCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonClientCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClientCreateActionPerformed(evt);
            }
        });

        jButtonClientUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconUpdate.png"))); // NOI18N
        jButtonClientUpdate.setToolTipText("Atualizar um cliente");
        jButtonClientUpdate.setBorder(null);
        jButtonClientUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonClientUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClientUpdateActionPerformed(evt);
            }
        });

        jButtonClientDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconDelete.png"))); // NOI18N
        jButtonClientDelete.setToolTipText("Excluir um cliente");
        jButtonClientDelete.setBorder(null);
        jButtonClientDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonClientDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClientDeleteActionPerformed(evt);
            }
        });

        jLabelPesquisar.setLabelFor(jTextFieldPesquisar);
        jLabelPesquisar.setText("Pesquisar");

        jTextFieldPesquisar.setToolTipText("Pesquisar cliente por nome");
        jTextFieldPesquisar.setPreferredSize(new java.awt.Dimension(256, 22));
        jTextFieldPesquisar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldPesquisarFocusGained(evt);
            }
        });
        jTextFieldPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisarKeyReleased(evt);
            }
        });

        jLabelPesquisarIcone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconSearch.png"))); // NOI18N
        jLabelPesquisarIcone.setLabelFor(jTextFieldPesquisar);

        jTableClients = new javax.swing.JTable() {
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        jTableClients.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTableClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Endereço", "Telefone", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTableClients.setFocusable(false);
        jTableClients.getTableHeader().setReorderingAllowed(false);
        jTableClients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableClientsMouseClicked(evt);
            }
        });
        jTableClients.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTableClientsKeyReleased(evt);
            }
        });
        jScrollPaneClients.setViewportView(jTableClients);

        jLabelInfo.setText("* Campos obrigatórios");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelTelefone)
                            .addComponent(jLabelEndereco)
                            .addComponent(jLabelNome))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelEmail)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonClientCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jButtonClientUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jButtonClientDelete)
                        .addContainerGap(366, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabelPesquisar)
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabelIdClient)
                                .addGap(18, 18, 18)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldIdClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelInfo))
                            .addComponent(jScrollPaneClients)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPesquisarIcone)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(20, 20, 20))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonClientCreate, jButtonClientDelete, jButtonClientUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabelInfo)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelIdClient)
                            .addComponent(jTextFieldIdClient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNome)
                    .addComponent(jTextFieldNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelEndereco)
                    .addComponent(jTextFieldEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTelefone)
                    .addComponent(jTextFieldTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEmail)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClientCreate)
                    .addComponent(jButtonClientUpdate)
                    .addComponent(jButtonClientDelete))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPesquisar)
                    .addComponent(jLabelPesquisarIcone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneClients, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonClientCreate, jButtonClientDelete, jButtonClientUpdate});

        setBounds(0, 0, 650, 450);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNomeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldNomeFocusGained
		this.jTextFieldNome.selectAll();
    }//GEN-LAST:event_jTextFieldNomeFocusGained

    private void jTextFieldEnderecoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldEnderecoFocusGained
		this.jTextFieldEndereco.selectAll();
    }//GEN-LAST:event_jTextFieldEnderecoFocusGained

    private void jTextFieldTelefoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTelefoneFocusGained
		this.jTextFieldTelefone.selectAll();
    }//GEN-LAST:event_jTextFieldTelefoneFocusGained

    private void jTextFieldEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldEmailFocusGained
		this.jTextFieldEmail.selectAll();
    }//GEN-LAST:event_jTextFieldEmailFocusGained

    private void jTextFieldPesquisarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldPesquisarFocusGained
		this.jTextFieldPesquisar.selectAll();
    }//GEN-LAST:event_jTextFieldPesquisarFocusGained

    private void jButtonClientCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClientCreateActionPerformed
		if (!jTextFieldNome.getText().isEmpty()
			&& !jTextFieldTelefone.getText().isEmpty()
			&& !jTextFieldEmail.getText().isEmpty()) {
			addOneClient();
			clearClientFields();
		} else {
			JOptionPane.showMessageDialog(null, "Campos com (*) são obrigatórios!");
		}
    }//GEN-LAST:event_jButtonClientCreateActionPerformed

    private void jButtonClientUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClientUpdateActionPerformed
		if (!jTextFieldNome.getText().isEmpty()
			&& !jTextFieldTelefone.getText().isEmpty()
			&& !jTextFieldEmail.getText().isEmpty()) {
			updateOneClientById(Integer.parseInt(jTextFieldIdClient.getText()));
			clearClientFields();
			jButtonClientCreate.setEnabled(true);
		} else {
			JOptionPane.showMessageDialog(null, "Campos com (*) são obrigatórios!");
		}
    }//GEN-LAST:event_jButtonClientUpdateActionPerformed

    private void jButtonClientDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClientDeleteActionPerformed
		if (!jTextFieldIdClient.getText().isEmpty()) {
			int idClient = Integer.parseInt(jTextFieldIdClient.getText());
			String nameClient = jTextFieldNome.getText();
			Object message = "Deseja remover '" + nameClient + "' id: " + idClient;
			int showConfirmDialog = JOptionPane.showConfirmDialog(null, message,
				"EXCLUIR CLIENTE", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (showConfirmDialog == JOptionPane.YES_OPTION) {
				deleteOneClientById(idClient);
				clearClientFields();
				jButtonClientCreate.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(null, "Operação de exclusão cancelada");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Para excluir um cliente "
				+ "é necessário informar o id.");
		}
    }//GEN-LAST:event_jButtonClientDeleteActionPerformed

    private void jTextFieldPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisarKeyReleased
		finderOneClientByName(this.jTextFieldPesquisar.getText());
    }//GEN-LAST:event_jTextFieldPesquisarKeyReleased

    private void jTableClientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientsMouseClicked
		setFieldsClient();
    }//GEN-LAST:event_jTableClientsMouseClicked

    private void jTableClientsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableClientsKeyReleased
		setFieldsClient();
    }//GEN-LAST:event_jTableClientsKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClientCreate;
    private javax.swing.JButton jButtonClientDelete;
    private javax.swing.JButton jButtonClientUpdate;
    private javax.swing.JLabel jLabelEmail;
    private javax.swing.JLabel jLabelEndereco;
    private javax.swing.JLabel jLabelIdClient;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelNome;
    private javax.swing.JLabel jLabelPesquisar;
    private javax.swing.JLabel jLabelPesquisarIcone;
    private javax.swing.JLabel jLabelTelefone;
    private javax.swing.JScrollPane jScrollPaneClients;
    private javax.swing.JTable jTableClients;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldEndereco;
    private javax.swing.JTextField jTextFieldIdClient;
    private javax.swing.JTextField jTextFieldNome;
    private javax.swing.JTextField jTextFieldPesquisar;
    private javax.swing.JTextField jTextFieldTelefone;
    // End of variables declaration//GEN-END:variables
}
