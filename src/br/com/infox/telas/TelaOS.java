package br.com.infox.telas;

import br.com.infox.dal.ModuloConexao;
import static br.com.infox.dal.ModuloConexao.fecharConexao;
import static br.com.infox.dal.Tools.printSQLException;
import static br.com.infox.dal.Tools.setValues;
import static br.com.infox.dal.Tools.isNumber;
import static br.com.infox.dal.Tools.parseFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class TelaOS extends javax.swing.JInternalFrame {

	private static Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private ResultSetMetaData resultSetMetaData;
	private String tipo;
	private String status;

	Locale locale = new Locale("pt", "BR");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);
	NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

	/**
	 * Creates new form TelaOS
	 */
	public TelaOS() {
		initComponents();
		clearOSFields();
	}

	private void clearOSFields() {
		jTextFieldNumeroOS.setText(null);
		jTextFieldDataOS.setText(null);
		jRadioButtonOrcamento.setSelected(true);
		jTextFieldPesquisaCliente.setText(null);
		jTextFieldClienteId.setText(null);
		((DefaultTableModel) jTableClients.getModel()).setRowCount(0);
		jComboBoxStatusOS.setSelectedIndex(0);
		jTextFieldEquipamentoOS.setText(null);
		jTextFieldDefeitoOS.setText(null);
		jTextFieldServicoOS.setText(null);
		jTextFieldTecnicoOS.setText(null);
		jFormattedTextFieldValorTotal.setText("0,00");
	}

	public void setFieldsClient() {
		int set = jTableClients.getSelectedRow();
		jTextFieldClienteId.setText(
			String.format("%04d", jTableClients.getModel().getValueAt(set, 0)));
		jTextFieldPesquisaCliente.setText(jTableClients.getModel().getValueAt(set, 1).toString());
		finderClientById(Integer.parseInt(jTextFieldClienteId.getText()));
	}

	//create
	private void addOS() {
		String insert = "INSERT INTO tbos "
			+ "(tipo, status, equipamento, defeito, servico, tecnico, valor, idcli) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(insert,
				Statement.RETURN_GENERATED_KEYS);
			setValues(preparedStatement, tipo, jComboBoxStatusOS.getSelectedIndex(),
				jTextFieldEquipamentoOS.getText(), jTextFieldDefeitoOS.getText(),
				jTextFieldServicoOS.getText(), jTextFieldTecnicoOS.getText(),
				(jFormattedTextFieldValorTotal.getText().isEmpty()
				|| jFormattedTextFieldValorTotal.getText() == null) ? 0.00
				: parseFormat(jFormattedTextFieldValorTotal.getText(), locale),
				Integer.parseInt(jTextFieldClienteId.getText()));
			int insertOk = preparedStatement.executeUpdate();
			if (insertOk > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				resultSetMetaData = resultSet.getMetaData();
				while (resultSet.next()) {
					System.out.printf("%s: %s\n",
						resultSetMetaData.getColumnName(1), resultSet.getInt(1));
					JOptionPane.showMessageDialog(null,
						"OS id(" + resultSet.getInt(1) + ") adicionada com sucesso!");
				}
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} catch (ParseException ex) {
			Logger.getLogger(TelaOS.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//read - Clientes por nome
	private void finderClientByName(String name) {
		String select = "SELECT idcli AS ID, nomecli AS Nome, "
			+ "fonecli AS Telefone, emailcli AS Email "
			+ "FROM tbclientes WHERE lower(nomecli) LIKE lower(?)";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(select);
			preparedStatement.setString(1, "%" + name + "%");
			resultSet = preparedStatement.executeQuery();
			jTableClients.setModel(DbUtils.resultSetToTableModel(resultSet));
		} catch (SQLException ex) {
			printSQLException(ex);
		} finally {
			fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//read - Cliente por id
	private void finderClientById(int id) {
		String select = "SELECT idcli AS ID, nomecli AS Nome, "
			+ "fonecli AS Telefone, emailcli AS Email "
			+ "FROM tbclientes WHERE idcli = ?";
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(select);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			jTableClients.setModel(DbUtils.resultSetToTableModel(resultSet));
		} catch (SQLException ex) {
			printSQLException(ex);
		} finally {
			fecharConexao(connection, preparedStatement, resultSet);
		}
	}

	//read - Ordem de Serviço OS por id
	private boolean finderOSById(int id) {
		String select = "SELECT idos, data_os, tipo, status, equipamento, "
			+ "defeito, servico, tecnico, valor, idcli "
			+ "FROM dbinfox.tbos WHERE idos = ?";
		boolean searchOS = true;
		connection = ModuloConexao.connection();
		try {
			preparedStatement = connection.prepareStatement(select);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				jTextFieldNumeroOS.setText(
					String.format("%04d", resultSet.getInt("idos")));
				jTextFieldDataOS.setText(
					dateFormat.format(resultSet.getDate("data_os")));
				String rbtTipo = resultSet.getString("tipo");
				switch (rbtTipo) {
					case "O":
						jRadioButtonOrcamento.setSelected(true);
						tipo = "O";
						break;
					case "S":
						jRadioButtonOrdemServ.setSelected(true);
						tipo = "S";
						break;
					default:
						throw new AssertionError();
				}
				jComboBoxStatusOS.setSelectedIndex(resultSet.getInt("status"));
				jTextFieldEquipamentoOS.setText(resultSet.getString("equipamento"));
				jTextFieldDefeitoOS.setText(resultSet.getString("defeito"));
				jTextFieldServicoOS.setText(resultSet.getString("servico"));
				jTextFieldTecnicoOS.setText(resultSet.getString("tecnico"));
				jFormattedTextFieldValorTotal.setValue(
					currencyFormat.format(resultSet.getDouble("valor")));
				jTextFieldClienteId.setText(
					String.format("%04d", resultSet.getInt("idcli")));
			} else {
				JOptionPane.showMessageDialog(null,
					"Ordem de serviço não encontrada", "Pesquisa de OS",
					JOptionPane.WARNING_MESSAGE);
				searchOS = false;
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement, resultSet);
		}
		return searchOS;
	}

	//update - OS por id
	private void updateOSById(int id) {
		String update = "UPDATE tbos SET tipo = ?, status = ?, equipamento = ?, "
			+ "defeito = ?, servico = ?, tecnico = ?, valor = ? WHERE idos= ?";
		connection = ModuloConexao.connection();
		try {
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(update);
			setValues(preparedStatement, tipo, jComboBoxStatusOS.getSelectedIndex(),
				jTextFieldEquipamentoOS.getText(), jTextFieldDefeitoOS.getText(),
				jTextFieldServicoOS.getText(), jTextFieldTecnicoOS.getText(),
				parseFormat(jFormattedTextFieldValorTotal.getText(), locale),
				Integer.parseInt(jTextFieldNumeroOS.getText()));
			int updateOk = preparedStatement.executeUpdate();
			if (updateOk > 0) {
				connection.commit();
				JOptionPane.showMessageDialog(null,
					"OS alterada com sucesso!");
			} else {
				connection.rollback();
				JOptionPane.showMessageDialog(null,
					"Alteração de OS não realizada!");
			}
		} catch (SQLException ex) {
			printSQLException(ex);
		} catch (ParseException ex) {
			Logger.getLogger(TelaOS.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			ModuloConexao.fecharConexao(connection, preparedStatement);
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

        buttonGroupTipoOS = new javax.swing.ButtonGroup();
        jPanelOS = new javax.swing.JPanel();
        jLabelNumeroOS = new javax.swing.JLabel();
        jTextFieldNumeroOS = new javax.swing.JTextField();
        jLabelDataOS = new javax.swing.JLabel();
        jTextFieldDataOS = new javax.swing.JTextField();
        jRadioButtonOrcamento = new javax.swing.JRadioButton();
        jRadioButtonOrdemServ = new javax.swing.JRadioButton();
        jLabelStatusOS = new javax.swing.JLabel();
        jComboBoxStatusOS = new javax.swing.JComboBox<>();
        jPanelCliente = new javax.swing.JPanel();
        jLabelPesquisaCliente = new javax.swing.JLabel();
        jTextFieldPesquisaCliente = new javax.swing.JTextField();
        jLabelClienteId = new javax.swing.JLabel();
        jTextFieldClienteId = new javax.swing.JTextField();
        jScrollPaneClientes = new javax.swing.JScrollPane();
        jTableClients = new javax.swing.JTable();
        jSeparatorFormOS = new javax.swing.JSeparator();
        jLabelEquipamentoOS = new javax.swing.JLabel();
        jTextFieldEquipamentoOS = new javax.swing.JTextField();
        jLabelDefeitoOS = new javax.swing.JLabel();
        jTextFieldDefeitoOS = new javax.swing.JTextField();
        jLabelServicoOS = new javax.swing.JLabel();
        jTextFieldServicoOS = new javax.swing.JTextField();
        jLabelTecnicoOS = new javax.swing.JLabel();
        jTextFieldTecnicoOS = new javax.swing.JTextField();
        jLabelValorTotal = new javax.swing.JLabel();
        jFormattedTextFieldValorTotal = new javax.swing.JFormattedTextField();
        jButtonOSNew = new javax.swing.JButton();
        jButtonOSCreate = new javax.swing.JButton();
        jButtonOSRead = new javax.swing.JButton();
        jButtonOSUpdate = new javax.swing.JButton();
        jButtonOSDelete = new javax.swing.JButton();
        jButtonOSPrint = new javax.swing.JButton();
        jLabelInfo = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro Ordens de Servi;os");
        setPreferredSize(new java.awt.Dimension(650, 450));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanelOS.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelNumeroOS.setLabelFor(jTextFieldNumeroOS);
        jLabelNumeroOS.setText("Nº OS");

        jTextFieldNumeroOS.setEditable(false);
        jTextFieldNumeroOS.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNumeroOS.setText("0001");
        jTextFieldNumeroOS.setToolTipText("Número da OS");
        jTextFieldNumeroOS.setPreferredSize(new java.awt.Dimension(65, 22));

        jLabelDataOS.setLabelFor(jTextFieldDataOS);
        jLabelDataOS.setText("Data");

        jTextFieldDataOS.setEditable(false);
        jTextFieldDataOS.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataOS.setToolTipText("Data de abertura da OS");
        jTextFieldDataOS.setPreferredSize(new java.awt.Dimension(100, 22));

        buttonGroupTipoOS.add(jRadioButtonOrcamento);
        jRadioButtonOrcamento.setText("Orçamento");
        jRadioButtonOrcamento.setToolTipText("Se tipo da OS for um orçamento");
        jRadioButtonOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonOrcamentoActionPerformed(evt);
            }
        });

        buttonGroupTipoOS.add(jRadioButtonOrdemServ);
        jRadioButtonOrdemServ.setText("Ordem Serviço");
        jRadioButtonOrdemServ.setToolTipText("Se tipo da OS for um serviço");
        jRadioButtonOrdemServ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonOrdemServActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelOSLayout = new javax.swing.GroupLayout(jPanelOS);
        jPanelOS.setLayout(jPanelOSLayout);
        jPanelOSLayout.setHorizontalGroup(
            jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelOSLayout.createSequentialGroup()
                        .addGroup(jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelNumeroOS)
                            .addComponent(jTextFieldNumeroOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldDataOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDataOS)))
                    .addGroup(jPanelOSLayout.createSequentialGroup()
                        .addComponent(jRadioButtonOrcamento)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButtonOrdemServ)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelOSLayout.setVerticalGroup(
            jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNumeroOS)
                    .addComponent(jLabelDataOS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNumeroOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDataOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelOSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonOrcamento)
                    .addComponent(jRadioButtonOrdemServ))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelStatusOS.setLabelFor(jComboBoxStatusOS);
        jLabelStatusOS.setText("Situação");

        jComboBoxStatusOS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Na bancada [0]", "Aguardando Aprovação [1]", "Aguardando Peça [2]", "Entrega OK [3]", "Orçamento Reprovado [4]", "Abandonado pelo cliente [5]", "Retornou [6]" }));
        jComboBoxStatusOS.setToolTipText("Status atual da OS");

        jPanelCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente"));

        jLabelPesquisaCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconSearch.png"))); // NOI18N
        jLabelPesquisaCliente.setLabelFor(jTextFieldPesquisaCliente);

        jTextFieldPesquisaCliente.setToolTipText("Pesquisa pelo nome do cliente");
        jTextFieldPesquisaCliente.setPreferredSize(new java.awt.Dimension(150, 22));
        jTextFieldPesquisaCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldPesquisaClienteFocusGained(evt);
            }
        });
        jTextFieldPesquisaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaClienteKeyReleased(evt);
            }
        });

        jLabelClienteId.setLabelFor(jTextFieldClienteId);
        jLabelClienteId.setText("* Id");

        jTextFieldClienteId.setEditable(false);
        jTextFieldClienteId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldClienteId.setToolTipText("Id de cliente selecionado");
        jTextFieldClienteId.setPreferredSize(new java.awt.Dimension(100, 22));

        jTableClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Telefone", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableClients.setToolTipText("Resultado pesquisa por cliente");
        jTableClients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableClientsMouseClicked(evt);
            }
        });
        jScrollPaneClientes.setViewportView(jTableClients);

        javax.swing.GroupLayout jPanelClienteLayout = new javax.swing.GroupLayout(jPanelCliente);
        jPanelCliente.setLayout(jPanelClienteLayout);
        jPanelClienteLayout.setHorizontalGroup(
            jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jTextFieldPesquisaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPesquisaCliente)
                        .addGap(30, 30, 30)
                        .addComponent(jLabelClienteId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldClienteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelClienteLayout.setVerticalGroup(
            jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldPesquisaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelPesquisaCliente))
                    .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelClienteId)
                        .addComponent(jTextFieldClienteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabelEquipamentoOS.setLabelFor(jTextFieldEquipamentoOS);
        jLabelEquipamentoOS.setText("* Equipamento");

        jTextFieldEquipamentoOS.setToolTipText("Descrição do equipamento");
        jTextFieldEquipamentoOS.setPreferredSize(new java.awt.Dimension(500, 22));
        jTextFieldEquipamentoOS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldEquipamentoOSFocusGained(evt);
            }
        });

        jLabelDefeitoOS.setLabelFor(jTextFieldDefeitoOS);
        jLabelDefeitoOS.setText("* Defeito");

        jTextFieldDefeitoOS.setToolTipText("Diagnótico de defeito");
        jTextFieldDefeitoOS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldDefeitoOSFocusGained(evt);
            }
        });

        jLabelServicoOS.setLabelFor(jTextFieldServicoOS);
        jLabelServicoOS.setText("Serviço");

        jTextFieldServicoOS.setToolTipText("Serviço previsto");
        jTextFieldServicoOS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldServicoOSFocusGained(evt);
            }
        });

        jLabelTecnicoOS.setLabelFor(jTextFieldTecnicoOS);
        jLabelTecnicoOS.setText("Técnico");

        jTextFieldTecnicoOS.setToolTipText("Técnico responsável");
        jTextFieldTecnicoOS.setPreferredSize(new java.awt.Dimension(200, 22));
        jTextFieldTecnicoOS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldTecnicoOSFocusGained(evt);
            }
        });

        jLabelValorTotal.setText("Valor Total");

        jFormattedTextFieldValorTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jFormattedTextFieldValorTotal.setToolTipText("Valor do serviço em Reais, Ex.: 2.500,50 ou 2500,00");
        jFormattedTextFieldValorTotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFormattedTextFieldValorTotalFocusGained(evt);
            }
        });

        jButtonOSNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconNew.png"))); // NOI18N
        jButtonOSNew.setToolTipText("Iniciar novo cadastro de OS");
        jButtonOSNew.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonOSNew.setPreferredSize(new java.awt.Dimension(64, 64));
        jButtonOSNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOSNewActionPerformed(evt);
            }
        });

        jButtonOSCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconCreate.png"))); // NOI18N
        jButtonOSCreate.setToolTipText("Salvar novo cadastro de OS");
        jButtonOSCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonOSCreate.setPreferredSize(new java.awt.Dimension(64, 64));
        jButtonOSCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOSCreateActionPerformed(evt);
            }
        });

        jButtonOSRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconRead.png"))); // NOI18N
        jButtonOSRead.setToolTipText("Buscar um cadastro de OS");
        jButtonOSRead.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonOSRead.setPreferredSize(new java.awt.Dimension(64, 64));
        jButtonOSRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOSReadActionPerformed(evt);
            }
        });

        jButtonOSUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconUpdate.png"))); // NOI18N
        jButtonOSUpdate.setToolTipText("Atualizar um cadastro de OS");
        jButtonOSUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonOSUpdate.setPreferredSize(new java.awt.Dimension(64, 64));
        jButtonOSUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOSUpdateActionPerformed(evt);
            }
        });

        jButtonOSDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconDelete.png"))); // NOI18N
        jButtonOSDelete.setToolTipText("Excluir um cadastro de OS");
        jButtonOSDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonOSDelete.setPreferredSize(new java.awt.Dimension(64, 64));

        jButtonOSPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icons/iconPrint.png"))); // NOI18N
        jButtonOSPrint.setToolTipText("Imprimir o cadastro de OS");
        jButtonOSPrint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonOSPrint.setPreferredSize(new java.awt.Dimension(64, 64));

        jLabelInfo.setText("* Campos obrigatórios");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparatorFormOS)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelStatusOS)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxStatusOS, 0, 1, Short.MAX_VALUE))
                            .addComponent(jPanelOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelEquipamentoOS, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDefeitoOS, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelServicoOS, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelTecnicoOS, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldServicoOS)
                            .addComponent(jTextFieldDefeitoOS)
                            .addComponent(jTextFieldEquipamentoOS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldTecnicoOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelValorTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jFormattedTextFieldValorTotal))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButtonOSNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonOSCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonOSRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonOSUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonOSDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonOSPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelInfo)))
                .addGap(20, 20, 20))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonOSCreate, jButtonOSDelete, jButtonOSNew, jButtonOSPrint, jButtonOSRead, jButtonOSUpdate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStatusOS)
                            .addComponent(jComboBoxStatusOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparatorFormOS, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelEquipamentoOS)
                    .addComponent(jTextFieldEquipamentoOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDefeitoOS)
                    .addComponent(jTextFieldDefeitoOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelServicoOS)
                    .addComponent(jTextFieldServicoOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTecnicoOS)
                    .addComponent(jTextFieldTecnicoOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelValorTotal)
                    .addComponent(jFormattedTextFieldValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonOSCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOSRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOSUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOSDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOSPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOSNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelInfo))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonOSCreate, jButtonOSDelete, jButtonOSNew, jButtonOSPrint, jButtonOSRead, jButtonOSUpdate});

        setBounds(0, 0, 650, 450);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPesquisaClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaClienteFocusGained
		jTextFieldPesquisaCliente.selectAll();
    }//GEN-LAST:event_jTextFieldPesquisaClienteFocusGained

    private void jTextFieldPesquisaClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaClienteKeyReleased
		finderClientByName(jTextFieldPesquisaCliente.getText());
    }//GEN-LAST:event_jTextFieldPesquisaClienteKeyReleased

    private void jTableClientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientsMouseClicked
		setFieldsClient();
    }//GEN-LAST:event_jTableClientsMouseClicked

    private void jRadioButtonOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonOrcamentoActionPerformed
		tipo = "O"; // [O]rçamento
    }//GEN-LAST:event_jRadioButtonOrcamentoActionPerformed

    private void jRadioButtonOrdemServActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonOrdemServActionPerformed
		tipo = "S"; // [S]erviço
    }//GEN-LAST:event_jRadioButtonOrdemServActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
		jRadioButtonOrcamento.setSelected(true);
		tipo = "O";
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButtonOSCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOSCreateActionPerformed
		if (!jTextFieldClienteId.getText().isEmpty()
			&& !jTextFieldEquipamentoOS.getText().isEmpty()
			&& !jTextFieldDefeitoOS.getText().isEmpty()) {
			addOS();
			clearOSFields();
		} else {
			JOptionPane.showMessageDialog(null, "Campos com (*) são obrigatórios!",
				"Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
		}
    }//GEN-LAST:event_jButtonOSCreateActionPerformed

    private void jButtonOSNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOSNewActionPerformed
		jTextFieldPesquisaCliente.setEnabled(true);
		jTableClients.setVisible(true);
		jButtonOSCreate.setEnabled(true);
		clearOSFields();
    }//GEN-LAST:event_jButtonOSNewActionPerformed

    private void jTextFieldEquipamentoOSFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldEquipamentoOSFocusGained
		jTextFieldEquipamentoOS.selectAll();
    }//GEN-LAST:event_jTextFieldEquipamentoOSFocusGained

    private void jTextFieldDefeitoOSFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDefeitoOSFocusGained
		jTextFieldDefeitoOS.selectAll();
    }//GEN-LAST:event_jTextFieldDefeitoOSFocusGained

    private void jTextFieldServicoOSFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldServicoOSFocusGained
		jTextFieldServicoOS.selectAll();
    }//GEN-LAST:event_jTextFieldServicoOSFocusGained

    private void jTextFieldTecnicoOSFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTecnicoOSFocusGained
		jTextFieldTecnicoOS.selectAll();
    }//GEN-LAST:event_jTextFieldTecnicoOSFocusGained

    private void jFormattedTextFieldValorTotalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextFieldValorTotalFocusGained
		jFormattedTextFieldValorTotal.selectAll();
    }//GEN-LAST:event_jFormattedTextFieldValorTotalFocusGained

    private void jButtonOSReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOSReadActionPerformed
		clearOSFields();
		String numberOS = JOptionPane.showInputDialog(null,
			"Informe um número de OS", "Pesquisar OS", JOptionPane.INFORMATION_MESSAGE);
		numberOS = numberOS != null ? numberOS : "";
		if (!numberOS.isEmpty() && isNumber(numberOS)) {
			jButtonOSCreate.setEnabled(false);
			jTextFieldPesquisaCliente.setText(null);
			jTextFieldPesquisaCliente.setEnabled(false);
			boolean searchOS = finderOSById(Integer.parseInt(numberOS));
			if (searchOS) {
				finderClientById(Integer.parseInt(this.jTextFieldClienteId.getText()));
			}
		} else {
			JOptionPane.showMessageDialog(null,
				"Valor para OS inválido!\nDeve ser um número inteiro, Ex.: 100, 101, 102...",
				"Pesquisar OS", JOptionPane.WARNING_MESSAGE);
		}
    }//GEN-LAST:event_jButtonOSReadActionPerformed

    private void jButtonOSUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOSUpdateActionPerformed
		if (!jTextFieldClienteId.getText().isEmpty()
			&& !jTextFieldEquipamentoOS.getText().isEmpty()
			&& !jTextFieldDefeitoOS.getText().isEmpty()) {
			updateOSById(Integer.parseInt(jTextFieldNumeroOS.getText()));
			clearOSFields();
			jButtonOSCreate.setEnabled(true);
			jTextFieldPesquisaCliente.setEnabled(true);
		} else {
			JOptionPane.showMessageDialog(null, "Campos com (*) são obrigatórios!",
				"Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
		}
    }//GEN-LAST:event_jButtonOSUpdateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupTipoOS;
    private javax.swing.JButton jButtonOSCreate;
    private javax.swing.JButton jButtonOSDelete;
    private javax.swing.JButton jButtonOSNew;
    private javax.swing.JButton jButtonOSPrint;
    private javax.swing.JButton jButtonOSRead;
    private javax.swing.JButton jButtonOSUpdate;
    private javax.swing.JComboBox<String> jComboBoxStatusOS;
    private javax.swing.JFormattedTextField jFormattedTextFieldValorTotal;
    private javax.swing.JLabel jLabelClienteId;
    private javax.swing.JLabel jLabelDataOS;
    private javax.swing.JLabel jLabelDefeitoOS;
    private javax.swing.JLabel jLabelEquipamentoOS;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelNumeroOS;
    private javax.swing.JLabel jLabelPesquisaCliente;
    private javax.swing.JLabel jLabelServicoOS;
    private javax.swing.JLabel jLabelStatusOS;
    private javax.swing.JLabel jLabelTecnicoOS;
    private javax.swing.JLabel jLabelValorTotal;
    private javax.swing.JPanel jPanelCliente;
    private javax.swing.JPanel jPanelOS;
    private javax.swing.JRadioButton jRadioButtonOrcamento;
    private javax.swing.JRadioButton jRadioButtonOrdemServ;
    private javax.swing.JScrollPane jScrollPaneClientes;
    private javax.swing.JSeparator jSeparatorFormOS;
    private javax.swing.JTable jTableClients;
    private javax.swing.JTextField jTextFieldClienteId;
    private javax.swing.JTextField jTextFieldDataOS;
    private javax.swing.JTextField jTextFieldDefeitoOS;
    private javax.swing.JTextField jTextFieldEquipamentoOS;
    private javax.swing.JTextField jTextFieldNumeroOS;
    private javax.swing.JTextField jTextFieldPesquisaCliente;
    private javax.swing.JTextField jTextFieldServicoOS;
    private javax.swing.JTextField jTextFieldTecnicoOS;
    // End of variables declaration//GEN-END:variables
}
