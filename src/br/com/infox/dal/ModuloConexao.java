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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de conexão com o banco de dados
 *
 * @author Danilo N. Benatti
 * @version 1.0
 */
public class ModuloConexao {

	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	private static String encoding;

	/**
	 * Método responsável pela conexão com o BD
	 *
	 * @return connection
	 */
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

	/**
	 * Método verifica quais as conexões existem com o banco de dados e as
	 * encerra
	 *
	 * @param conn
	 * @param preparedStatement
	 * @param resultSet
	 */
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

	/**
	 * Método que encerra conexões Connection, PreparedStatement, ResultSet.
	 *
	 * @param conn
	 * @param preparedStatement
	 * @param resultSet
	 */
	public static void fecharConexao(Connection conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		fechar(conn, preparedStatement, resultSet);
	}

	/**
	 * Método que encerra conexões Connection, PreparedStatement.
	 *
	 * @param conn
	 * @param preparedStatement
	 */
	public static void fecharConexao(Connection conn, PreparedStatement preparedStatement) {
		fechar(conn, preparedStatement, null);
	}
}
