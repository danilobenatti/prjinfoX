package br.com.infox.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuloConexao {

	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	private static String encoding;

	public static Connection connection() {

		String connProp = "mysql";
		switch (connProp) {
			case "mysql":
				user = "root";
				password = "123456";
				encoding = "UTF8";
				driver = "com.mysql.cj.jdbc.Driver";
				url = "jdbc:mysql://localhost:3306/dbinfox?useTimezone=true&serverTimezone=UTC";
				break;
			case "postgresql":
				user = "postgres";
				password = "123456";
				encoding = "UTF8";
				driver = "org.postgresql.Driver";
				url = "jdbc:postgresql://localhost:5432/dbinfox";
				break;
			case "firebird":
				user = "SYSDBA";
				password = "123456";
				encoding = "UTF8";
				driver = "org.firebirdsql.jdbc.FBDriver";
				url = "jdbc:firebirdsql://localhost:3050/d:/data/dbinfox.fdb";
				break;
			default:
				break;
		}

		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		props.setProperty("encoding", encoding);

		try {
			Class.forName(driver).newInstance();
			return DriverManager.getConnection(url, props);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException ex) {
			Logger.getLogger(ModuloConexao.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private static void fechar(Connection conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			Logger.getLogger(ModuloConexao.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void fecharConexao(Connection conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		fechar(conn, preparedStatement, resultSet);
	}
}
