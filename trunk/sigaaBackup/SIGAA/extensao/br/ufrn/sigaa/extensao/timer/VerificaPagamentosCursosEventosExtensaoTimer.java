/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 03/05/2012
 * 
 */
package br.ufrn.sigaa.extensao.timer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;


/**
 *  <p> Timer de extens�o que verifica diarimente os <strong>pagamentos das GRUs</strong> geradas para os cursos e eventos e confirma essa pagamento no m�dulo de extens�o.</p>
 * 
 *  <p> Busca todos os curso e eventos n�o pagos de extens�o, a partir o id da GRU, verifica no banco comum se ela foi paga, em caso afirmativo,
 *  confirma esse pagamento da respectiva entidade dentro do m�dulo de extens�o.</p>
 *
 * <p>
 * <strong>Observa��o: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 3 par�metros: <br/>
 *  horaExecucao: 6h <br/>
 *  tipoReplicacao: D = Di�rio <br/>
 *  express�o cron: * 0 6 1/1 * ? * <br/>
 * </p>
 *
 *<pre>
 *
 * insert into infra.registro_timer (id, dia_execucao, hora_execucao, tempo, tipo_repeticao, classe, ativa
 * , servidor_execucao, servidor_restricao, dia_mes_execucao, executar_agora, em_execucao, expressao_cron)
 * values (200, 0, 6, 0, 'D', 'br.ufrn.sigaa.extensao.timer.VerificaPagamentosCursosEventosExtensaoTimer', true, 'sistemas1.sistemas1i1', 'sistemas1i1'
 * , 0, false, false, '* 0 6 1/1 * ? *');
 *
 *</pre>
 * 
 * 
 * @author jadson
 *
 */
public class VerificaPagamentosCursosEventosExtensaoTimer extends AbstractTimerExtensao{
	
	/** Descri��o padr�o da opera��o enviada para os emails dos administradores informando sobre a execu��o da opera��o. */
	public static final String DESCRICAO_OPERACAO = "VERIFICA��O DO PAGAMENTO DE CURSOS E VENTOS DE EXTENS�O";
	
	/**
	 * Inicia a executa��o em paralelo
	 */
	@Override
	public void run() {
		try {	
			verificaPagamentosCursosEventosExtensao();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cont�m a l�gica principal para atualizar os pagamentos de cursos e eventos de extens�o
	 */
	private void verificaPagamentosCursosEventosExtensao() {
		
		try{
		
			// As GRUs que est�o abertas no m�dulo de extens�o //
			List<Integer> idsGRUsCursosEEventosAbertas = buscaGRUsInscricoesCursosEventosNaoPagas();
			
			// Retorna as GRUs pagas dentre as que estavam abertas//
			List<Integer> idsGRUsCursosEEventosQuitadas = retornaGRUsCursosEventosQuitadas(idsGRUsCursosEEventosAbertas);
			
			// confirma o pagamentos das que estavam abertas e foram quitadas dentro do m�dulo extens�o //
			confirmaPagamentosGRUs(idsGRUsCursosEEventosQuitadas);
			
			enviaEmailNotificacaoAdministradorSistema(DESCRICAO_OPERACAO, "Total de inscri��es em cursos e eventos cujos pagamentos foram confirmados : "+idsGRUsCursosEEventosQuitadas.size());
			
		}catch(Exception ex){
			enviaEmailErroAdministradorSistema(DESCRICAO_OPERACAO, "Erro durante a execu��o da verifica��o dos pagamentos de extens�o", ex);
		}
		
	}

	
	
	
	/**
	 * Busca no m�dulo de extens�o as inscri��es em os cursos e eventos que ainda n�o foram pagas 
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	private List<Integer> buscaGRUsInscricoesCursosEventosNaoPagas() throws DAOException {
		
		String hql = " SELECT inscricao.idGRUPagamento "
					+" FROM InscricaoAtividadeParticipante inscricao "
					+" WHERE statusInscricao NOT IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusInativos()) // e ainda est� ativa
					+" AND idGRUPagamento IS NOT NULL " // cuja GRU foi gerada
					+" AND statusPagamento = "+StatusPagamentoInscricao.EM_ABERTO; // que ainda n�o foi paga
		
		GenericDAO dao =  null;
		
		try{
			
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			Query q = dao.getSession().createQuery(hql);
			return (List<Integer>) q.list();
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	
	/**
	 * Retorna os ids das GRUs que foram quitas a partir dos ids das GRUs das atividades abertas no sistema.
	 * 
	 * Essa busca � realizada no bancao comum.
	 * 
	 * @param idsGRUsCursosEEventosAbertas
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Integer> retornaGRUsCursosEventosQuitadas(List<Integer> idsGRUsCursosEEventosAbertas){
		
		if(idsGRUsCursosEEventosAbertas == null || idsGRUsCursosEEventosAbertas.size() == 0)
			return new ArrayList<Integer>();
		
		List<Integer> idsGRUsCursosEEventosQuitadas = new ArrayList<Integer>();
		
		JdbcTemplate template = null;
		
		try{
		
			String sqlGRUsPagas = "select gru.id_gru "
				+" FROM gru.guia_recolhimento_uniao gru "
				+" WHERE gru.id_gru in "+UFRNUtils.gerarStringIn(idsGRUsCursosEEventosAbertas)
				+" AND gru.quitada = trueValue() AND gru.id_tipo_arrecadacao = "+TipoArrecadacao.CURSO_E_EVENTOS_EXTENSAO;
			
			
			template = new JdbcTemplate( Database.getInstance().getComumDs());
			
			Collection cursosEEventosQuitados = template.queryForList(sqlGRUsPagas);
			Iterator it = cursosEEventosQuitados.iterator();
			
			Map<String, Object> mapa = null;
			
			while(it.hasNext()){
				
				mapa= (Map) it.next();
				
				
				int idGRU = (Integer) mapa.get("id_gru");
				idsGRUsCursosEEventosQuitadas.add(idGRU);
			}
		
		}finally {
			try {
				
				if(template != null) template.getDataSource().getConnection().close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				
				/** Se der algum erro nesta rotina manda email pra administra��o pra notificar do erro. */
				e.printStackTrace();
				enviaEmailErroAdministradorSistema(DESCRICAO_OPERACAO, " N�o foi poss�vel fechar a conex�o do JdbcTemplate", e);
				
			}
			
		}
		
		return idsGRUsCursosEEventosQuitadas;
	}
	
	
	
	/**
	 * Confirma no m�dulo de extens�o a pagamento da GRU que foi registrado na parte de GRU no banco comum.
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	private void confirmaPagamentosGRUs(List<Integer> idsGRUsCursosEEventosQuitadas) throws DAOException {
		
		
		if(idsGRUsCursosEEventosQuitadas != null && idsGRUsCursosEEventosQuitadas.size() > 0){ // s� se existem GRUs quitadas
			
			String sql =
				" UPDATE extensao.inscricao_atividade_participante SET status_pagamento = "+StatusPagamentoInscricao.CONFIRMADO_AUTOMATICAMENTE
				+" WHERE status_pagamento = "+StatusPagamentoInscricao.EM_ABERTO+" AND id_gru_pagamento in ( :idsGRUsPagas ) ";
			
			
			GenericDAO dao =  null;
			
			try{
				dao = DAOFactory.getGeneric(Sistema.SIGAA);
				Query q = dao.getSession().createSQLQuery(sql);
				
				q.setParameterList("idsGRUsPagas", idsGRUsCursosEEventosQuitadas);
				
				if (q.executeUpdate() < 1)
					throw new DAOException ("Ocorreu um erro ao confirmar o pagamento da atividades de extens�o automaticamente.");
				
			}finally{
				if(dao != null) dao.close();
			}
		}
	
	}
	
	
}
