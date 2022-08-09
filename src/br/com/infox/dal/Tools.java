/*
 * The MIT License
 *
 * Copyright 2022 Eng. Danilo Navarro Benatti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.infox.dal;

import br.com.infox.telas.TelaPrincipal;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Classe com métodos auxiliares para tratamento e monipulação de dados e/ou
 * excessões. e impressão de relatórios.
 *
 * @author danil
 * @version 1.0
 */
public class Tools {

	private static Connection connection;

	/**
	 * Método auxiliar para formação do PreparedStatement
	 *
	 * @param ps
	 * @param values
	 * @throws SQLException
	 */
	public static void setValues(PreparedStatement ps, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			ps.setObject(i + 1, values[i]);
		}
	}

	/**
	 * Método auxiliar para tratamento de erros e excessões
	 *
	 * @param ex
	 */
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

	/**
	 * Verifica se um dado string pode ser um número.
	 *
	 * @param string
	 * @return boolean
	 */
	public static boolean isNumber(String string) {
		return string.chars().allMatch(Character::isDigit);
	}

	/**
	 * Método auxiliar para converter um valor em string para decimal levando em
	 * conta a locale
	 *
	 * @param amount
	 * @param locale
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal parseFormat(String amount, Locale locale) throws ParseException {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		if (numberFormat instanceof DecimalFormat) {
			((DecimalFormat) numberFormat).setParseBigDecimal(true);
		}
		return (BigDecimal) numberFormat.parse(amount.replaceAll("[^\\d.,]", ""));
	}

	/**
	 * Gerador de relatórios de clientes e serviços cadastrados.
	 *
	 * @param pathFile
	 */
	public static void printReport(String pathFile) {
		int showConfirmDialog = JOptionPane.showConfirmDialog(null,
				"Confirma impressão", "Relatório",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (showConfirmDialog == JOptionPane.YES_OPTION) {
			try {
				connection = ModuloConexao.connection();
				File file = new File(pathFile);
				JasperPrint jasperPrint = JasperFillManager.fillReport(file.getPath(), null, connection);
				JasperViewer.viewReport(jasperPrint, false);
				connection.close();
			} catch (SQLException | JRException ex) {
				Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Gera relatório de OS a partir de um número de OS.
	 *
	 * @param pathFile
	 * @param id
	 */
	public static void printReport(String pathFile, int id) {
		int showConfirmDialog = JOptionPane.showConfirmDialog(null,
				"Confirma impressão", "Relatório",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (showConfirmDialog == JOptionPane.YES_OPTION) {
			try {
				connection = ModuloConexao.connection();
				File file = new File(pathFile);
				HashMap filtro = new HashMap();
				filtro.put("idos", id);
				JasperPrint jasperPrint = JasperFillManager.fillReport(file.getPath(), filtro, connection);
				JasperViewer.viewReport(jasperPrint, false);
				connection.close();
			} catch (SQLException | JRException ex) {
				Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
