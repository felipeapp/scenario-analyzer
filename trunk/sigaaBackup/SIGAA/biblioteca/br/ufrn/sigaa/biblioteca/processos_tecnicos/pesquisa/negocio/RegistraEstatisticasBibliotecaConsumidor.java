/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Queue;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.RegistroEstatisticasConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.RegistroEstatisticasConsultaAcervo.TipoOperacaoRegistroConsultas;

/**
*
* <p> Classe respons�vel por salvar no banco os registros realizados no sistema. Deve existir apenas 1 inst�ncia dessa classe por servidor.</p>
*
* <p> Essa classe aqui � que registra o trabalho pesado, ent�o ela roda em um thread separado para n�o influenciar nas performance do sistema </p>
*
* <p> <i> Posteriormente existir� um timer agendado para processar esses registros. E atualizar o cache das buscas. </i> </p>
* 
* @author jadson
*
*/
public class RegistraEstatisticasBibliotecaConsumidor implements Runnable{

	
	/**  Fila dos registros das estatisticas do sistema, s� vai existir 1 fila por servidor da aplica��o, j� que s� � instanciado 1 produtor e o produtor instancia 1 cosumidor */
	private Queue<RegistroEstatisticasConsultaAcervo> filaRegistros;
	
	
	public RegistraEstatisticasBibliotecaConsumidor(Queue<RegistroEstatisticasConsultaAcervo> filaRegistros) {
		this.filaRegistros = filaRegistros;
	}
	
	
	/**
	 * Executa a a��o de consumir um registro da fila e salvar no banco
	 */
	public void run() {
		while (true) {
			RegistroEstatisticasConsultaAcervo registro = null;
			synchronized (filaRegistros) {
				if (!filaRegistros.isEmpty()) {          // Se n�o esta vazia, pega o primeiro e registra, at� a fila ficar fazia    
					registro = filaRegistros.poll();
				} else {                         // se a fila da vazia espera, vai ser acordado quando o produtor escrever algo na fila
					try {
						filaRegistros.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (registro != null) { // realiza o registro do primeiro da fila
					registrar(registro);
				}
				
			}
		}
	}
	
	
	/** Salva o registro realizado no banco*/
	public void registrar(RegistroEstatisticasConsultaAcervo registro){
		
		
		long initialTime = System.currentTimeMillis();
		
		Connection con = Database.getInstance().getSigaaConnection();
		
		MaterialInformacionalDao dao = null;
		
		
		String sqlAtualiza = " UPDATE biblioteca.registro_estatisticas_consultas_acervo_biblioteca SET quantidade = quantidade + 1 WHERE processado = falseValue() AND id_titulo_catalografico = ? AND tipo_operacao = ? ";
		String sqlInsere   = " INSERT INTO biblioteca.registro_estatisticas_consultas_acervo_biblioteca (id_registro_estatisticas_biblioteca , id_titulo_catalografico, data_registro, tipo_operacao, processado, quantidade ) "
							+ " values ( (select nextval('biblioteca.registro_estatisticas_consulta_acervo_sequence')), ?, ?, ?, ?, 1 )";
		try {
			
			
			PreparedStatement pstInsere = con.prepareStatement(sqlInsere);
			PreparedStatement pstAtualiza = con.prepareStatement(sqlAtualiza);
			
			
			if(registro.getOperacaoRegistro() == TipoOperacaoRegistroConsultas.REGISTRAR_CONSULTA){
				
				for (CacheEntidadesMarc cacheConsultada : registro.getTitulosConsultados()) {
					
					if(verificaRegistroExiste(con, cacheConsultada.getIdTituloCatalografico(), TipoOperacaoRegistroConsultas.REGISTRAR_CONSULTA)){
						
						pstAtualiza.setInt(1, cacheConsultada.getIdTituloCatalografico());
						pstAtualiza.setInt(2, registro.getOperacaoRegistro().ordinal());
						pstAtualiza.addBatch();
						
					}else{
						pstInsere.setInt(1, cacheConsultada.getIdTituloCatalografico());
						pstInsere.setTimestamp(2, new Timestamp(registro.getDataRegistro().getTime()));
						pstInsere.setInt(3, registro.getOperacaoRegistro().ordinal());
						pstInsere.setBoolean(4, false);
						pstInsere.addBatch();
					}
					
					
				}
				
			}
			
			if(registro.getOperacaoRegistro() == TipoOperacaoRegistroConsultas.REGISTRAR_VISUALIZACAO){
				
				if(verificaRegistroExiste(con, registro.getIdTituloVisualizado(), TipoOperacaoRegistroConsultas.REGISTRAR_VISUALIZACAO)){
					
					pstAtualiza.setInt(1, registro.getIdTituloVisualizado());
					pstAtualiza.setInt(2, registro.getOperacaoRegistro().ordinal());
					pstAtualiza.addBatch();
					
				}else{
				
					pstInsere.setInt(1, registro.getIdTituloVisualizado());
					pstInsere.setTimestamp(2, new Timestamp(registro.getDataRegistro().getTime()));
					pstInsere.setInt(3, registro.getOperacaoRegistro().ordinal());
					pstInsere.setBoolean(4, false);
					pstInsere.addBatch();
				}
			}
			
			if(registro.getOperacaoRegistro() == TipoOperacaoRegistroConsultas.REGISTRAR_EMPRESTIMO){
				
				dao = DAOFactory.getInstance().getDAO(MaterialInformacionalDao.class);
				
				List<Integer> idsTitulos =  dao.findIdsTitulosMateriais(registro.getIdsMateriaisEmprestados());
				
				for (Integer idTituloEmprestado : idsTitulos) {
					
					if(verificaRegistroExiste(con, idTituloEmprestado, TipoOperacaoRegistroConsultas.REGISTRAR_EMPRESTIMO)){
						
						pstAtualiza.setInt(1, idTituloEmprestado);
						pstAtualiza.setInt(2, registro.getOperacaoRegistro().ordinal());
						pstAtualiza.addBatch();
						
					}else{
					
						pstInsere.setInt(1, idTituloEmprestado);
						pstInsere.setTimestamp(2, new Timestamp(registro.getDataRegistro().getTime()));
						pstInsere.setInt(3, registro.getOperacaoRegistro().ordinal());
						pstInsere.setBoolean(4, false);
						pstInsere.addBatch();
					}
				}
				
			}
			
			
			
			pstInsere.executeBatch();
			pstInsere.close();
			
			pstAtualiza.executeBatch();
			pstAtualiza.close();
			
			System.out.println(">>>>>>>>  Execu��o do Consumidor das Estat�stica da Biblioteca Demorou: "+(System.currentTimeMillis()-initialTime)+" ms");
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(dao != null) dao.close();
			Database.getInstance().close(con);
		}
	}

	
	/** Verifica se um registro j� existe para o Titulo passado, sen�o existe insere um novo, se j� existe, atualiza a quantidade. */
	private boolean verificaRegistroExiste(Connection con, int idTitulo, TipoOperacaoRegistroConsultas tipoOperacao) throws SQLException {
		
		String sqlConsula  = " SELECT id_titulo_catalografico  FROM biblioteca.registro_estatisticas_consultas_acervo_biblioteca WHERE processado = falseValue() AND id_titulo_catalografico = ? AND  tipo_operacao = ? ";
		PreparedStatement pstConsulta  = con.prepareStatement(sqlConsula);
		
		try{
		
			pstConsulta.setInt(1, idTitulo);
			pstConsulta.setInt(2, tipoOperacao.ordinal());
			
			ResultSet result = pstConsulta.executeQuery();
			
			if(result.next()){
				return true;
			}else
				return false;
		}finally{
			pstConsulta.close();
		}
	}

}


