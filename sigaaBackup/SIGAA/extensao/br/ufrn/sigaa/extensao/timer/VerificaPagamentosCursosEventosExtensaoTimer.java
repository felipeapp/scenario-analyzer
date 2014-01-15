/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *  <p> Timer de extensão que verifica diarimente os <strong>pagamentos das GRUs</strong> geradas para os cursos e eventos e confirma essa pagamento no módulo de extensão.</p>
 * 
 *  <p> Busca todos os curso e eventos não pagos de extensão, a partir o id da GRU, verifica no banco comum se ela foi paga, em caso afirmativo,
 *  confirma esse pagamento da respectiva entidade dentro do módulo de extensão.</p>
 *
 * <p>
 * <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 3 parâmetros: <br/>
 *  horaExecucao: 6h <br/>
 *  tipoReplicacao: D = Diário <br/>
 *  expressão cron: * 0 6 1/1 * ? * <br/>
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
	
	/** Descrição padrão da operação enviada para os emails dos administradores informando sobre a execução da operação. */
	public static final String DESCRICAO_OPERACAO = "VERIFICAÇÃO DO PAGAMENTO DE CURSOS E VENTOS DE EXTENSÃO";
	
	/**
	 * Inicia a executação em paralelo
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
	 * Contém a lógica principal para atualizar os pagamentos de cursos e eventos de extensão
	 */
	private void verificaPagamentosCursosEventosExtensao() {
		
		try{
		
			// As GRUs que estão abertas no módulo de extensão //
			List<Integer> idsGRUsCursosEEventosAbertas = buscaGRUsInscricoesCursosEventosNaoPagas();
			
			// Retorna as GRUs pagas dentre as que estavam abertas//
			List<Integer> idsGRUsCursosEEventosQuitadas = retornaGRUsCursosEventosQuitadas(idsGRUsCursosEEventosAbertas);
			
			// confirma o pagamentos das que estavam abertas e foram quitadas dentro do módulo extensão //
			confirmaPagamentosGRUs(idsGRUsCursosEEventosQuitadas);
			
			enviaEmailNotificacaoAdministradorSistema(DESCRICAO_OPERACAO, "Total de inscrições em cursos e eventos cujos pagamentos foram confirmados : "+idsGRUsCursosEEventosQuitadas.size());
			
		}catch(Exception ex){
			enviaEmailErroAdministradorSistema(DESCRICAO_OPERACAO, "Erro durante a execução da verificação dos pagamentos de extensão", ex);
		}
		
	}

	
	
	
	/**
	 * Busca no módulo de extensão as inscrições em os cursos e eventos que ainda não foram pagas 
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	private List<Integer> buscaGRUsInscricoesCursosEventosNaoPagas() throws DAOException {
		
		String hql = " SELECT inscricao.idGRUPagamento "
					+" FROM InscricaoAtividadeParticipante inscricao "
					+" WHERE statusInscricao NOT IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusInativos()) // e ainda está ativa
					+" AND idGRUPagamento IS NOT NULL " // cuja GRU foi gerada
					+" AND statusPagamento = "+StatusPagamentoInscricao.EM_ABERTO; // que ainda não foi paga
		
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
	 * Essa busca é realizada no bancao comum.
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
				
				/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
				e.printStackTrace();
				enviaEmailErroAdministradorSistema(DESCRICAO_OPERACAO, " Não foi possível fechar a conexão do JdbcTemplate", e);
				
			}
			
		}
		
		return idsGRUsCursosEEventosQuitadas;
	}
	
	
	
	/**
	 * Confirma no módulo de extensão a pagamento da GRU que foi registrado na parte de GRU no banco comum.
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	private void confirmaPagamentosGRUs(List<Integer> idsGRUsCursosEEventosQuitadas) throws DAOException {
		
		
		if(idsGRUsCursosEEventosQuitadas != null && idsGRUsCursosEEventosQuitadas.size() > 0){ // só se existem GRUs quitadas
			
			String sql =
				" UPDATE extensao.inscricao_atividade_participante SET status_pagamento = "+StatusPagamentoInscricao.CONFIRMADO_AUTOMATICAMENTE
				+" WHERE status_pagamento = "+StatusPagamentoInscricao.EM_ABERTO+" AND id_gru_pagamento in ( :idsGRUsPagas ) ";
			
			
			GenericDAO dao =  null;
			
			try{
				dao = DAOFactory.getGeneric(Sistema.SIGAA);
				Query q = dao.getSession().createSQLQuery(sql);
				
				q.setParameterList("idsGRUsPagas", idsGRUsCursosEEventosQuitadas);
				
				if (q.executeUpdate() < 1)
					throw new DAOException ("Ocorreu um erro ao confirmar o pagamento da atividades de extensão automaticamente.");
				
			}finally{
				if(dao != null) dao.close();
			}
		}
	
	}
	
	
}
