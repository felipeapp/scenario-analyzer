/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 24/02/2010
 */
package br.ufrn.comum.sincronizacao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;

/**
 * 
 * Classe para realizar a sincronização dos dados de designações dos servidores
 * da base de dados do SIGRH para o SIGAA.
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 *
 */
public class SincronizaDesignacao {

	public static void sincronizar(Connection sigaaCon, Connection sipacCon)
			throws Exception {

		//Data da ultima atualização de designações no SIGAA
		java.util.Date dataUltimaAtualizacao = null;
		PreparedStatement pStUltimaAtualizacao = sigaaCon.prepareStatement("select max(ultima_atualizacao) from rh.designacao");
		ResultSet rsUltimaAtualizacao = pStUltimaAtualizacao.executeQuery();
		if (rsUltimaAtualizacao.next()) {
			dataUltimaAtualizacao = rsUltimaAtualizacao.getDate(1);
		}
		System.out.println("ÚLTIMA ATUALIZAÇÃO NO SIGAA DAS DESIGNACOES: " + 
				Formatador.getInstance().formatarData(dataUltimaAtualizacao));
		
		if (dataUltimaAtualizacao == null ){
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, 6);
			cal.set(Calendar.YEAR, 2007);
			
			dataUltimaAtualizacao = cal.getTime();
		}
	
		/*
		 * Somente migra dados das designações com algum dado alterado após a última atualização da base de dados
		 * vinda do SIGRH para o SIGAA.
		 */

		
		// consulta designações na base de dados do SIGRH com alterações após a data da última atualização no SIGAA
		String sqlConsultaDesignacoesSIGRH = 
				"select * FROM  rh.designacao d where (d.ultima_atualizacao >= ?  or d.data_cadastro >= ?) and d.id_unidade is not null ";
		PreparedStatement pStConsultaServidoresSIGRH = sipacCon.prepareStatement(sqlConsultaDesignacoesSIGRH);
		pStConsultaServidoresSIGRH.setDate(1, new java.sql.Date(dataUltimaAtualizacao.getTime()));
		pStConsultaServidoresSIGRH.setDate(2, new java.sql.Date(dataUltimaAtualizacao.getTime()));
		ResultSet rsDesignacoesSipac = pStConsultaServidoresSIGRH.executeQuery();
		
		PreparedStatement pStInsertSigaa = sigaaCon
				.prepareStatement("INSERT INTO rh.Designacao(ID_DESIGNACAO, ID_SERVIDOR, ID_ATIVIDADE, " +
								  "INICIO, FIM, ORIGEM, UORG, NOME_UNIDADE, ID_UNIDADE, GERENCIA, " +
								  "REMUNERACAO, ULTIMA_ATUALIZACAO) VALUES(?,?,?,?,?,?,?,?,?,?,?,now())") ;

		String updateSql = "UPDATE rh.Designacao SET ID_SERVIDOR = ?, ID_ATIVIDADE = ?, INICIO = ?, FIM = ?, ORIGEM = ?, " +
								"UORG=?, NOME_UNIDADE = ?, ID_UNIDADE = ?, GERENCIA = ?, " +
								"REMUNERACAO = ?, ultima_atualizacao = now() " +
								"WHERE ID_DESIGNACAO = ?";

		PreparedStatement pStUpdateSigaa = sigaaCon.prepareStatement(updateSql);
		PreparedStatement pStSelectSigaa = sigaaCon.prepareStatement("select * from rh.designacao where id_designacao = ?");

		int inseridos = 0;
		int atualizados = 0;
		int erros = 0;

		while (rsDesignacoesSipac.next()) {
			
			//Varrendo designações no SIGRH
			
			//Identificador da designação sigaa = sigrh
			int idDesignacao = rsDesignacoesSipac.getInt("ID_DESIGNACAO");
			
			//Identificador da atividade no sigaa = sigrh
			int idAtividadeSipac = rsDesignacoesSipac.getInt("ID_ATIVIDADE");
										
			//Identificador do servidor no sigaa
			int idServidorSipac = RhUtil.getIdServidor(rsDesignacoesSipac.getInt("ID_SERVIDOR"), sigaaCon, sipacCon);
			
			pStSelectSigaa.setInt(1, idDesignacao);
			ResultSet rsSelectSigaa = pStSelectSigaa.executeQuery();
			
			if (idServidorSipac > 0 && !rsSelectSigaa.next()){
				
				//Ainda NAO cadastrada no SIGAA
				int i = 0;
				
				pStInsertSigaa.setInt(++i, idDesignacao);
				pStInsertSigaa.setInt(++i, idServidorSipac);
				pStInsertSigaa.setInt(++i, idAtividadeSipac);
				pStInsertSigaa.setDate(++i, rsDesignacoesSipac.getDate("INICIO"));
				pStInsertSigaa.setDate(++i, rsDesignacoesSipac.getDate("FIM"));
				pStInsertSigaa.setString(++i, rsDesignacoesSipac.getString("ORIGEM"));
				pStInsertSigaa.setString(++i, rsDesignacoesSipac.getString("UORG"));
				pStInsertSigaa.setString(++i, rsDesignacoesSipac.getString("NOME_UNIDADE"));
				pStInsertSigaa.setInt(++i, rsDesignacoesSipac.getInt("ID_UNIDADE"));					
				pStInsertSigaa.setString(++i, rsDesignacoesSipac.getString("GERENCIA"));
				pStInsertSigaa.setBoolean(++i, rsDesignacoesSipac.getBoolean("REMUNERACAO"));
				
				try {
					pStInsertSigaa.executeUpdate();
					inseridos++;
				} catch (Exception e) {
					if (!e.getMessage().startsWith("ERROR: insert or update on table") && !e.getMessage().startsWith("ERROR: duplicate key")) {
						System.err.println(e.getMessage());
					}
					erros++;
				}
				pStInsertSigaa.clearParameters();
				
			}else{
				
				// Dados no sipac
				Date dataInicioSipac = rsDesignacoesSipac.getDate("inicio");
				Date dataFimSipac = rsDesignacoesSipac.getDate("FIM");
				int idUnidadeSipac = rsDesignacoesSipac.getInt("ID_UNIDADE"); 
				
				// Já cadastrada no SIGAA, atualiza
				int idServidorSigaa = rsSelectSigaa.getInt("id_servidor");
				int idAtividadeSigaa = rsSelectSigaa.getInt("id_atividade");
				Date dataInicioSigaa = rsSelectSigaa.getDate("inicio");
				Date dataFimSigaa = rsSelectSigaa.getDate("FIM");
				int idUnidadeSigaa = rsSelectSigaa.getInt("ID_UNIDADE"); 
				
				
				if (	idServidorSipac != idServidorSigaa || idAtividadeSipac != idAtividadeSigaa || 
						( dataInicioSipac == null && dataInicioSigaa != null) || (dataInicioSigaa == null && dataInicioSipac != null) || 
						( dataInicioSipac != null && dataInicioSigaa != null && !dataInicioSipac.equals(dataInicioSigaa)) || 
						( dataFimSipac == null && dataFimSigaa != null) || (dataFimSigaa == null && dataFimSipac != null) || 
						( dataFimSipac != null && dataFimSigaa != null && !dataFimSipac.equals(dataFimSigaa)) ||
						idUnidadeSipac != idUnidadeSigaa){
				
					
					int i = 0;				
					pStUpdateSigaa.setInt(++i, idServidorSipac);
					pStUpdateSigaa.setInt(++i, idAtividadeSipac);
					pStUpdateSigaa.setDate(++i, dataInicioSipac);
					pStUpdateSigaa.setDate(++i, dataFimSipac);
					pStUpdateSigaa.setString(++i, rsDesignacoesSipac.getString("ORIGEM"));
					pStUpdateSigaa.setString(++i, rsDesignacoesSipac.getString("UORG"));
					pStUpdateSigaa.setString(++i, rsDesignacoesSipac.getString("NOME_UNIDADE"));
					pStUpdateSigaa.setInt(++i, idUnidadeSipac);				
					pStUpdateSigaa.setString(++i, rsDesignacoesSipac.getString("GERENCIA"));
					pStUpdateSigaa.setBoolean(++i, rsDesignacoesSipac.getBoolean("REMUNERACAO"));
					
					pStUpdateSigaa.setInt(++i, idDesignacao);
	
					try {
						pStUpdateSigaa.executeUpdate();
						pStUpdateSigaa.clearParameters();
						atualizados++;
					} catch (Exception e) {
						if (!e.getMessage().startsWith("ERROR: insert or update on table") && !e.getMessage().startsWith("ERROR: duplicate key")) {
							System.err.println(e.getMessage());
						}
						erros++;
					}
				}
				
			}
			
		}
		System.out.println("Total INSERIDOS: " + inseridos);
		System.out.println("Total ATUALIZADOS: " + atualizados);
		System.out.println("Total ERRO: " + erros);
	}
	
	
	/**
	 * Método para remover designações nos bancos acadêmico e sistemas comum.
	 */
	public static void removerDesignacao(Connection con, int idDesignacao) throws Exception {
		try {
			PreparedStatement st = con.prepareStatement("delete from rh.designacao where id_designacao = ? ");
			st.setInt(1, idDesignacao);
			st.executeUpdate();
		} catch(Exception e) {
			throw new DAOException("Erro na sincronização com os bancos de dados SIPAC/SIGAA", e);
		} finally {
			con.close();
		}
	}

	public static void main(String[] args) throws Exception {
		sincronizar(Database.getInstance().getSigaaConnection(),Database.getInstance().getSipacConnection());
	}


}
