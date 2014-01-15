package br.ufrn.arq.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import br.ufrn.arq.util.StringUtils;


public class Transforma2Latin9 {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		Class.forName("org.postgresql.Driver");

		String id = "id_material";
		String coluna = "denominacao";
		String tabela = "material";

		String senha = JOptionPane.showInputDialog("Senha do Banco");
		
		Connection con = DriverManager.getConnection("jdbc:postgresql://testes.info.ufrn.br:5432/administrativo", "sipac", senha);

		Statement st = con.createStatement();

		ResultSet  rs = st.executeQuery("select " + id + "," +  coluna + " from "  + tabela + " order by " + coluna);

		PreparedStatement pSt = con.prepareStatement("UPDATE " + tabela + " SET " + coluna + " = ? WHERE " + id + " = ? ");

		while ( rs.next()) {

			String original = rs.getString(coluna);
			String convertida = StringUtils.toLatin9(original);
			if ( !original.equals(convertida) ) {
				pSt.setString(1, convertida);
				pSt.setInt(2, rs.getInt(id));
				pSt.executeUpdate();
				System.out.println(original + " -> " + convertida );
			}
		}

	}
}