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

public class Tools {

	private static Connection connection;

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

	public static boolean isNumber(String string) {
		return string.chars().allMatch(Character::isDigit);
	}

	public static BigDecimal parseFormat(String amount, Locale locale) throws ParseException {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		if (numberFormat instanceof DecimalFormat) {
			((DecimalFormat) numberFormat).setParseBigDecimal(true);
		}
		return (BigDecimal) numberFormat.parse(amount.replaceAll("[^\\d.,]", ""));
	}

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
