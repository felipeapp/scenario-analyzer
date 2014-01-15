/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 19/09/2007
*
*/
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.CancelamentoBolsa;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;


/**
 * Processa o cancelamento de bolsas de monitoria.
 * 
 * 
 * @author ilueny santos
 *
 */
public class ProcessadorCancelamentoBolsasMonitoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);		
		
		if( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CANCELAR_BOLSAS_PROJETO_MONITORIA)){
				cancelarBolsasNaoEnviouAtividades((MovimentoCadastro)mov);
		}
		
		return null;
	}

	
	
	
	/**
	 *  Cancela bolsas de monitores que não enviaram o relatório de frequência
	 *  por X meses consecutivos.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("deprecation")
	private void cancelarBolsasNaoEnviouAtividades(MovimentoCadastro mov) throws ArqException{
		
		//TODO verificar necessidade de trancamento no sipac		
		//TODO verificar se tem que retirar a bolsa do projeto (atualizar campo 'bolsas_canceladas') ou se só é pra retirar a bolsa do discente permitindo
		// que o coordenador indique outro bolsista.
		
		AtividadeMonitorDao dao = null;
		
		CancelamentoBolsa cancelarBolsa = (CancelamentoBolsa) mov.getObjMovimentado();
		
		dao = getDAO(AtividadeMonitorDao.class, mov);
		
		
			try {

					
					Collection<DiscenteMonitoria> discentesInadimplentes =  dao.findByDiscentesSemAtividadeNoPeriodo(cancelarBolsa.getMesInicio(), cancelarBolsa.getMesFim(), cancelarBolsa.getAno(), null);
					
					Connection conn = dao.getSession().connection();
					Statement stmt = conn.createStatement();					
					
					for (Object obj: discentesInadimplentes.toArray()) {
						
						Object[] obj1 = (Object[]) obj;
						
						//finalizando as orientações
						stmt.addBatch("UPDATE monitoria.orientacao SET " +
								"ativo = falseValue(), " +
								"data_fim = '" + new Date() + "'," +
								"id_registro_entrada_finalizacao = "+ mov.getUsuarioLogado().getRegistroEntrada().getId() + 
								" WHERE id_discente_monitoria = " + obj1[2].toString() + " AND ativo = trueValue()");
						
						//finalizando o discente...
						stmt.addBatch("UPDATE monitoria.discente_monitoria SET " +
								"ativo = trueValue()," +
								"id_situacao_discente_monitoria = " + SituacaoDiscenteMonitoria.MONITORIA_CANCELADA + "," +
								"data_fim = '" + new Date() + "'  " +
								"WHERE id_discente_monitoria = " + obj1[2].toString());
						
						
						//criando histórico...
						stmt.addBatch("INSERT INTO monitoria.historico_situacao_discente_monitoria" +
								" (data, id_discente_monitoria, id_situacao_discente_monitoria, id_registro_entrada, tipo_monitoria) " +
								"VALUES (" +
								"'"  + new Date() + 
								"'," + obj1[2].toString() + 
								","  + SituacaoDiscenteMonitoria.MONITORIA_CANCELADA +
								","  + mov.getUsuarioLogado().getRegistroEntrada().getId() + 
								","  + obj1[7].toString() +	") ");						
						
					}
					
					stmt.executeBatch();
				
		}catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Erro ao tentar finalizar discentes de monitoria!");					
		}finally {
			dao.close();
		}
		
	}

							
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
}