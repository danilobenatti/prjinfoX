package br.com.infox.dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Tools {

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
}
