package br.ufrn.sigaa.monitoria.migracao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.ufrn.arq.dao.Database;

/**
 * Atualização dos bolsistas de monitoria
 *
 * @author Gleydson
 *
 */
public class AtualizaStatusMonitores {

	public static void main(String[] args) throws Exception {

		//Connection monitoriaCon = ConectionUtil.getMonitoriaConnection();
		Connection monitoriaCon = (Connection) new Object();

		Database.setDirectMode();
		Connection sigaaCon = Database.getInstance().getSigaaConnection();

		PreparedStatement stMonitores = monitoriaCon
				.prepareStatement("select * from monitores");
		ResultSet rs = stMonitores.executeQuery();

		PreparedStatement stUpdate = sigaaCon
				.prepareStatement("UPDATE MONITORIA.DISCENTE_MONITORIA SET " +
						"id_situacao_discente_monitoria = ? ,  tipo_monitoria = ?" +
						"where codmerg = ?");

		int FINALIZADO = 6;
		int ABERTO = 5;

		int BOLSISTA = 2;
		int NAO_REMUNERADO = 1;

		int total = 0;
		while (rs.next()) {

			try {
				int idMonitor = rs.getInt("id");
				int estado = rs.getInt("estado");
				int status = rs.getInt("status");

				int ativo = 0;
				int vinculo = 0;

				if ( estado == 0 ) {
					ativo = FINALIZADO;
				} else {
					ativo = ABERTO;
				}

				if ( status == 0 ) {
					vinculo = NAO_REMUNERADO;
				} else {
					vinculo = BOLSISTA;
				}

				stUpdate.setInt(1, ativo);
				stUpdate.setInt(2, vinculo);
				stUpdate.setString(3, idMonitor + "MO");

				total += stUpdate.executeUpdate();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			System.out.println("Foi " + total);
		}

		monitoriaCon.close();
		sigaaCon.close();
	}

}
