/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/03/2007
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioProjetoMonitorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.HistoricoSituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.RelatorioMonitor;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoSituacaoProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processa o cadastro de discentes em bolsas de Monitoria.
 * 
 * 
 * @author ilueny santos
 * @author Victor Hugo 
 * 
 */
public class ProcessadorDiscenteMonitoria extends AbstractProcessador {

    /** Executa as operações com discentes de monitoria
     * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
     */
    public Object execute(Movimento mov) throws NegocioException, ArqException {

		DiscenteMonitoriaMov dmMov = (DiscenteMonitoriaMov) mov;
		DiscenteMonitoria monitorResult = null;
		
		//cadastrando o resultado de todos os discentes inscritos.
		if (dmMov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RESULTADO_SELECAO_MONITORIA)) {
		    cadastrarResultadoSelecaoMonitoria(dmMov);
		// Finaliza a participação do discente no projeto.
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA)) {
		    monitorResult = finalizarDiscenteMonitoria(dmMov, monitorResult);
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.EXCLUIR_DISCENTEMONITORIA)) {
		    monitorResult = excluirDiscenteMonitoria(dmMov);	    
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.ALTERAR_VINCULO_DISCENTEMONITORIA)) {
			monitorResult =  alterarVinculoDiscenteMonitoria(dmMov);
		
		/* @negocio: Uma monitoria pode ser reativada quando a sua finalização foi realizada por engano. */    
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.REATIVAR_MONITORIA)) {	    
		    monitorResult = reativarMonitoria(dmMov);
	
		/* @negocio: A alteração da nota pode ser realizada caso o coordenador cadastre uma nota errada. */
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.ALTERAR_NOTA_SELECAO_MONITORIA)) {
		    monitorResult = alterarNotaSelecaoMonitoria(dmMov);
		    
		/* @negocio: Todos os discentes de monitoria devem aceitar a bolsa antes de iniciar os trabalhos. */
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.ACEITAR_OU_RECUSAR_MONITORIA)) {
		    monitorResult = aceitarRecusarMonitoria(mov, dmMov);
		} else if (dmMov.getCodMovimento().equals(SigaaListaComando.NOTIFICAR_INSCRITO_SELECAO)) {
			notificarInscricaoSelecao(dmMov);
		}
		return (monitorResult);
    }

	/** Altera a nota de seleção da monitoria.
	 * @param dmMov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private DiscenteMonitoria alterarNotaSelecaoMonitoria(
			DiscenteMonitoriaMov dmMov) throws NegocioException, ArqException,
			DAOException {
		DiscenteMonitoria monitorResult;
		validate(dmMov);
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
		try {
			    monitorResult = dmMov.getDiscenteMonitoria();
			    dao.update(monitorResult);
			    gravarHistoricoDiscente(dmMov, dao, monitorResult);
		}finally {
		    dao.close();
		}
		return monitorResult;
	}

	/** Exclui um discente de monitoria.
	 * @param dmMov
	 * @return
	 * @throws DAOException
	 */
	private DiscenteMonitoria excluirDiscenteMonitoria(
			DiscenteMonitoriaMov dmMov) throws DAOException {
		DiscenteMonitoria monitorResult;
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
		try {
			    /* @negocio: Definindo situação como excluído. Geralmente por erro de migração. */
			    monitorResult = dmMov.getDiscenteMonitoria();
			    monitorResult.setAtivo(false);
			    monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.EXCLUIDO));
			    dao.update(monitorResult);
		
			    /* @negocio: Ao remover um discente todas as suas orientações também devem ser removidas. */
			    for (Orientacao ori : monitorResult.getOrientacoes()) {
				ori.setAtivo(false);
				dao.update(ori);
			    }
			    gravarHistoricoDiscente(dmMov, dao, monitorResult);
		}finally {
			dao.close();
		}
		return monitorResult;
	}

	/** Notifica discentes inscritos
	 * @param dmMov
	 */
	private void notificarInscricaoSelecao(DiscenteMonitoriaMov dmMov) {
		for (InscricaoSelecaoMonitoria inscSelMonitor : dmMov.getProvaSelecao().getDiscentesInscritos()) {
			if ( inscSelMonitor.getNotificacao().isEnviarEmail() )
				notificarPorEmail( inscSelMonitor.getNotificacao(), dmMov.getUsuarioLogado(), inscSelMonitor);

			if ( inscSelMonitor.getNotificacao().isEnviarMensagem() )
				notificarPorMensagem( inscSelMonitor.getNotificacao(), dmMov.getUsuarioLogado() );
		}
	}

	/** Aceita ou recusa discentes de monitoria.
	 * @param mov
	 * @param dmMov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private DiscenteMonitoria aceitarRecusarMonitoria(Movimento mov, DiscenteMonitoriaMov dmMov) throws DAOException, NegocioException, ArqException {
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		DiscenteMonitoria monitorResult;
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
	    try {

	    	ProvaSelecao prova = new ProvaSelecao();
	    	monitorResult = dmMov.getDiscenteMonitoria();

	    	//Utiliza o campo 'selecionado' do discente monitoria para 
	    	//Identificar quando o discente aceita a vaga de monitor. (selecionado = aceitou monitoria)
	    	if (monitorResult.isSelecionado()) {

	    		monitorResult.setAtivo(true);
	    		monitorResult.setDataInicio(hoje);
	    		monitorResult.setDataFim(monitorResult.getProjetoEnsino().getProjeto().getDataFim());

	    		validate(dmMov);

	    		ProjetoEnsino pm = monitorResult.getProjetoEnsino();

	    		/* @negocio: Se o projeto ainda não estiver em execução o projeto vai para a situação de 'EM EXECUÇÃO'. */
	    		if (pm.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_EM_EXECUCAO) {								
	    			pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_EM_EXECUCAO));
	    			pm.setAtivo(true);
	    			dao.update(pm);
	    			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
	    			ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), dmMov.getUsuarioLogado().getRegistroEntrada());
	    			dao.clearSession();
	    		}

	    		monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA));

	    		//Evitar erro de lazy. Utilizado na alteração da situação da prova seletiva para concluída
	    		prova = dao.findByPrimaryKey(monitorResult.getProvaSelecao().getId(), ProvaSelecao.class);	
	    		prova.setResultadoSelecao(dao.findByProvaSeletiva(monitorResult.getProvaSelecao().getId()));

	    		/* @negocio: Quando todos os discentes são validados (aceitam/recusam) a bolsa, a prova deve ser concluída. */
	    		if (prova.isValidacoesFinalizadas()) {
	    			prova.setSituacaoProva(new TipoSituacaoProvaSelecao(TipoSituacaoProvaSelecao.CONCLUIDA));
	    		} else {
	    			prova.setSituacaoProva(new TipoSituacaoProvaSelecao(TipoSituacaoProvaSelecao.VALIDACAO_EM_ANDAMENTO));
	    		}					    
	    		dao.update(prova);

	    		/* @negocio: Todas as orientações dos discentes devem ser inicializadas quando ele aceita a bolsa. */
	    		for (Orientacao ori : monitorResult.getOrientacoes()) {
	    			if (ori.getDataInicio() == null) {
	    				ori.setDataInicio(hoje);
	    			}
	    			if (ori.getDataFim() == null) {
	    				ori.setDataFim(ori.getDiscenteMonitoria().getProjetoEnsino().getProjeto().getDataFim());
	    			}
	    			ori.setAtivo(true);
	    			dao.update(ori);
	    		}

	    		for (DiscenteMonitoria discente : prova.getResultadoSelecao()) {
	    			if(discente.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.CONVOCADO_MAS_REJEITOU_MONITORIA ){
	    				discente.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO) );
	    				discente.setDiscenteConvocado(false);
	    				dao.update(discente);
	    				dao.update(prova);
	    				gravarHistoricoDiscente((DiscenteMonitoriaMov) mov, dao, discente);
	    			}
	    		} 	

	    		//Recusou a vaga...
	    	}else {
    			monitorResult.setAtivo(true);
    			monitorResult.setDataInicio(null);
    			monitorResult.setDataFim(null);
    			monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MAS_REJEITOU_MONITORIA));
    			// inicializando orientações que ainda estão em aberto
    			for (Orientacao ori : monitorResult.getOrientacoes()) {
    				ori.setDataInicio(null);
    				ori.setDataFim(null);
    				dao.update(ori);
    			}

	    		// seta como convocado o monitor que rejeitou a vaga. (controla a ordem de chamada dos monitores convocados mais de 1 vez).
	    		// convocando o próximo monitor melhor colocado. 		
	    		monitorResult.setDiscenteConvocado(true);
	    		if(monitorResult.getProvaSelecao() != null) {
	    			prova = dao.findByPrimaryKey(monitorResult.getProvaSelecao().getId(), ProvaSelecao.class, "id","vagasRemuneradas","vagasNaoRemuneradas");	    
	    			prova.setResultadoSelecao(dao.findByProvaSeletiva(prova.getId()));
	    			convocarDiscenteMonitoriaPosConvocacao(dmMov, prova, monitorResult.getTipoVinculo().getId());
	    		}
	    	}

	    	dao.update(monitorResult);
	    	gravarHistoricoDiscente(dmMov, dao, monitorResult);	 

	    }finally {
	    	dao.close();
	    }
		return monitorResult;
	}

	/** Reativa uma monitoria
	 * @param dmMov
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private DiscenteMonitoria reativarMonitoria(DiscenteMonitoriaMov dmMov)
			throws DAOException, ArqException, NegocioException {
		DiscenteMonitoria monitorResult;
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
	    try {
		
	    	monitorResult = dmMov.getDiscenteMonitoria();
	    	monitorResult.setDataFim(monitorResult.getProjetoEnsino().getProjeto().getDataFim());
	    	monitorResult.setAtivo(true);			
	    	monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA));

	    	//Validação...
	    	ListaMensagens mensagens = new ListaMensagens();
	    	DiscenteMonitoriaValidator.validaDiscenteAssumirMonitoria(monitorResult, mensagens);

	    	if (!mensagens.isEmpty()) {
	    		NegocioException ne = new NegocioException();
	    		ne.setListaMensagens(mensagens);
	    		throw ne;
	    	}

	    	dao.update(monitorResult);
	    	gravarHistoricoDiscente(dmMov, dao, monitorResult);
        	
	    }finally {
	        dao.close();
	    }
		return monitorResult;
	}

	/** Altera um vínculo de monitoria
	 * @param dmMov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private DiscenteMonitoria alterarVinculoDiscenteMonitoria(
			DiscenteMonitoriaMov dmMov) throws NegocioException,
			ArqException, DAOException {
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		DiscenteMonitoria monitorResult;
		validate(dmMov);
	    DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
	    try {
        	    monitorResult = dmMov.getDiscenteMonitoria();
        	    int novoTipoMonitoria = dmMov.getNovoTipoMonitoria();
        
        	    // AGUARDANDO CONVOCAÇÃO DA PROGRAD -->> ASSUMIU MONITORIA
        	    if ((monitorResult.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO)
        		    && ((novoTipoMonitoria == TipoVinculoDiscenteMonitoria.BOLSISTA) || (novoTipoMonitoria == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO))) {
        
        		monitorResult.setAtivo(true);
        		monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA));
        		monitorResult.setDataInicio(hoje);
        		monitorResult.setDataFim(monitorResult.getProjetoEnsino().getProjeto().getDataFim());
        		monitorResult.getTipoVinculo().setId(novoTipoMonitoria);
        		dao.update(monitorResult);
        
        		// iniciando orientações que ainda estão em inativas
        		for (Orientacao ori : monitorResult.getOrientacoes()) {
        		    // inicia a orientação na data atual
        		    ori.setDataInicio(hoje); 
        		    ori.setDataFim(monitorResult.getProjetoEnsino().getProjeto().getDataFim());
        		    ori.setAtivo(true);
        		    dao.update(ori);
        		}
        
        		gravarHistoricoDiscente(dmMov, dao, monitorResult);
        
        		// ---------------JÁ É MONITOR ATIVO------------------
        		// Monitor já é ativo, vai só mudar de vínculo
        	    } else if ((monitorResult.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA)
        		    && ((monitorResult.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) 
        			    || (monitorResult.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO))) {

        		 
        
        		/* @negocio: Finaliza o discente atual para criar outro com o novo vínculo. */
        		monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
        		monitorResult.setDataFim(hoje);
        		dao.update(monitorResult);
        
        		// guarda orientações antes de finalizar, para poder clonar no novo discente
        		Collection<Orientacao> orientacoesNaoFinalizadas = monitorResult.getOrientacoes();
        
        		/* @negocio: Quando o discente é finalizado, suas orientações também devem ser finalizadas. */
        		for (Orientacao ori : monitorResult.getOrientacoes()) {
        		    /* @negocio: Finaliza a orientação na data que finaliza o discente. */ 
        		    if (!ori.isFinalizada()) {
        			ori.setDataFim(hoje);
        		    }
        		    ori.setRegistroEntradaFinalizacao(dmMov.getUsuarioLogado().getRegistroEntrada());
        		    dao.update(ori);
        		}
        
        		gravarHistoricoDiscente(dmMov, dao, monitorResult);
        
        
        		// -------- cadastrando o novo discente como bolsista ----------//
        		dao.detach(monitorResult);
        		DiscenteMonitoria newDiscente = new DiscenteMonitoria();
        		newDiscente.setId(0);
        		newDiscente.setAtivo(true);
        		newDiscente.setAgencia(monitorResult.getAgencia());
        		newDiscente.setBanco(monitorResult.getBanco());
        		newDiscente.setConta(monitorResult.getConta());
        		newDiscente.setOperacao(monitorResult.getOperacao());
        		newDiscente.setDataCadastro(new Date());
        		newDiscente.setDataInicio(hoje);
        		newDiscente.setDataFim(monitorResult.getProjetoEnsino().getProjeto().getDataFim());
        		newDiscente.setDiscente(monitorResult.getDiscente());
        		newDiscente.setNota(monitorResult.getNota());
        		newDiscente.setNotaProva(monitorResult.getNotaProva());
        		newDiscente.setProjetoEnsino(monitorResult.getProjetoEnsino());
        		newDiscente.setClassificacao(monitorResult.getClassificacao());
        		newDiscente.setProvaSelecao(monitorResult.getProvaSelecao());
        		newDiscente.getTipoVinculo().setId(dmMov.getNovoTipoMonitoria());
        		newDiscente.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA));
        
        		// gravando histórico
        		HistoricoSituacaoDiscenteMonitoria newHistorico = new HistoricoSituacaoDiscenteMonitoria();
        		newHistorico.setData(new Date());
        		newHistorico.setDiscenteMonitoria(newDiscente);
        		newHistorico.setSituacaoDiscenteMonitoria(newDiscente.getSituacaoDiscenteMonitoria());
        		newHistorico.setRegistroEntrada(dmMov.getUsuarioLogado().getRegistroEntrada());
        		newHistorico.setTipoVinculo(newDiscente.getTipoVinculo());
        
        		// criando orientações do novo monitor
        		Collection<Orientacao> newOrientacoes = new ArrayList<Orientacao>();
        		for (Orientacao ori : orientacoesNaoFinalizadas) {
        		    Orientacao o = new Orientacao();
        		    o.setId(0);
        		    o.setDiscenteMonitoria(newDiscente);
        		    o.setAtivo(ori.isAtivo());
        		    o.setDataInicio(ori.getDataInicio());
        		    o.setDataFim(ori.getDataFim());
        		    o.setEquipeDocente(ori.getEquipeDocente());
        		    o.setRegistroEntrada(dmMov.getUsuarioLogado().getRegistroEntrada());
        		    newOrientacoes.add(o);	
        		}
        		newDiscente.setOrientacoes(newOrientacoes);
        		
        		//Validação...
        		ListaMensagens mensagens = new ListaMensagens();
        		DiscenteMonitoriaValidator.validaDiscenteAssumirMonitoria(newDiscente, mensagens);
        
        		if (!mensagens.isEmpty()) {
        		    NegocioException ne = new NegocioException();
        		    ne.setListaMensagens(mensagens);
        		    throw ne;
        		}
        
        		dao.create(newDiscente);
        		dao.create(newHistorico);
        		return (newDiscente);
        	    }
	    }finally {
	        dao.close();
	    }
		return monitorResult;
	}

	/** Finaliza uma monitoria
	 * @param dmMov
	 * @param monitorResult
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private DiscenteMonitoria finalizarDiscenteMonitoria(
			DiscenteMonitoriaMov dmMov, DiscenteMonitoria monitorResult)
			throws NegocioException, ArqException, DAOException {
		validate(dmMov);
	    DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
	    try {
        	    monitorResult = dmMov.getDiscenteMonitoria();
        	    int tipoVinculo = monitorResult.getTipoVinculo().getId();
        	    monitorResult.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
        	    if (!monitorResult.isFinalizado()) {
        		monitorResult.setDataFim(CalendarUtils.descartarHoras(new Date()));
        	    }
        	    
        	    dao.update(monitorResult);
        
        	    /* @negocio: Quando o discente é finalizado, suas orientações também devem ser finalizadas. */
        	    for (Orientacao ori : monitorResult.getOrientacoes()) {
        	    	if (!ori.isFinalizada()) {
        	    		ori.setDataFim(monitorResult.getDataFim());
        	    	}        	    	
        	    	ori.setRegistroEntradaFinalizacao(dmMov.getUsuarioLogado().getRegistroEntrada());
        	    	dao.update(ori);
        	    }
        	    gravarHistoricoDiscente(dmMov, dao, monitorResult);
        	    
        	    /* @negocio: Verifica se existem discentes 'EM ESPERA' na mesma prova do discente que está sendo finalizado. */
        	    if(monitorResult.getProvaSelecao() != null) {
        	    	ProvaSelecao prova = dao.findByPrimaryKey(monitorResult.getProvaSelecao().getId(), ProvaSelecao.class, "id","vagasRemuneradas","vagasNaoRemuneradas");	    
        	    	prova.setResultadoSelecao(dao.findByProvaSeletiva(prova.getId()));
        	    
        	    	/* @negocio: Se não tem discente em espera deve ser retirada uma vaga reservada da prova seletiva liberando a vaga para o cadastro de uma nova prova.*/
        	    	if(!prova.isExisteDiscenteEmEsperaNaoRemunerado()){
        	    		//libera vaga de bolsista
        	    		if (monitorResult.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) {
        	    			int vagasRemuneradas = prova.getVagasRemuneradas() - 1;
        	    			dao.updateField(ProvaSelecao.class, prova.getId(), "vagasRemuneradas", vagasRemuneradas);
        	    		}
        	    	}
        	    	if (!prova.isPossuiDiscenteEmEspera()) {        	    		
        	    		//libera vaga de não remunerado
        	    		if (monitorResult.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO) {
        	    			int vagasNaoRemuneradas = prova.getVagasNaoRemuneradas() - 1;
        	    			dao.updateField(ProvaSelecao.class, prova.getId(), "vagasNaoRemuneradas", vagasNaoRemuneradas);		    
        	    		}
        	    	}        	    	
        	    	convocarDiscenteMonitoria(dmMov, prova, tipoVinculo);
        	    }
	    }catch (NegocioException e) {
	    	e.getListaMensagens().addErro("Erro ao finalizar monitoria do discente atual.");
	    	throw e;
	    	
	    }finally {
	    	dao.close();
	    }
		return monitorResult;
	}

	/** Cadastra o resultado de uma seleção de monitoria.
	 * @param dmMov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void cadastrarResultadoSelecaoMonitoria(DiscenteMonitoriaMov dmMov) throws NegocioException,
			ArqException, DAOException {
		validate(dmMov);
	    DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
	    try {
	    
        	    /* @negocio: Discente deve acessar o sigaa e confirmar sua participação no projeto. */  	
        	    /* @negocio: A Pró-retoria de Graduação deve solicitar o cadastro dos bolsistas no SIPAC (homologar bolsa de monitoria). */ 
        	    for (DiscenteMonitoria dm : dmMov.getProvaSelecao().getResultadoSelecao()) {
        		dm.setAtivo(true);
        		dm.setDataInicio(null);
        		dm.setDataFim(null);
        		dao.createOrUpdate(dm);
        		gravarHistoricoDiscente(dmMov, dao, dm);
        	    }
        
        	    /* @negocio: Permitido remover porque os discentes ainda não foram efetivados. */
        	    for (Orientacao orientacao : dmMov.getOrientacoesRemovidas()) {
        		if (orientacao.getId() != 0) {
        		    dao.remove(orientacao);
        		}
        	    }
        
        	    /* @negocio: Permitido remover porque os discentes ainda não foram efetivados. */
        	    for (DiscenteMonitoria dm : dmMov.getSelecoesRemovidas()) {
        		if (dm.getId() != 0) {
        		    dm.setHistoricoSituacaoDiscenteMonitoria(getGenericDAO(dmMov).findByExactField(HistoricoSituacaoDiscenteMonitoria.class,"discenteMonitoria.id" , dm.getId()));					
        		    for (HistoricoSituacaoDiscenteMonitoria h : dm.getHistoricoSituacaoDiscenteMonitoria()) { 
        			dao.remove(h);
        		    }
        		    dm.setOrientacoes(getGenericDAO(dmMov).findByExactField(Orientacao.class,"discenteMonitoria.id" , dm.getId()));					
        		    for (Orientacao o : dm.getOrientacoes()) { 
        			dao.remove(o);
        		    }
        		    dao.remove(dm);
        		}
        	    }
        
        
        	    /* @negocio: Quando o resultado de todos os discentes são cadastrados a prova vai para o estado de 'concluída'. */
        	    ProvaSelecao prova = dmMov.getProvaSelecao();
        	    if (prova.getSituacaoProva().getId() == TipoSituacaoProvaSelecao.AGUARDANDO_INSCRICAO){
	        		prova.setSituacaoProva(new TipoSituacaoProvaSelecao(TipoSituacaoProvaSelecao.CONCLUIDA));
	        		dao.update(prova);
        	    }
        	    
        	    prova.setResultadoSelecao(dao.findByProvaSeletiva(prova.getId()));

        	    if(!prova.isExisteDiscenteEmEsperaNaoRemunerado()){
        	    	//libera vaga de bolsista
        	    	if (prova.getResultadoSelecao().size() < prova.getVagasRemuneradas()) {
        	    		int vagasRemuneradas = prova.getResultadoSelecao().size();
        	    		dao.updateField(ProvaSelecao.class, prova.getId(), "vagasRemuneradas", vagasRemuneradas);
        	    	}
        	    }
        	    
        	    if (!prova.isPossuiDiscenteEmEspera()) {
        	    	//libera vaga de não remunerado
        	    	if (prova.getResultadoSelecao().size() < prova.getVagasNaoRemuneradas()) {
        	    		int vagasNaoRemuneradas = prova.getResultadoSelecao().size();
        	    		dao.updateField(ProvaSelecao.class, prova.getId(), "vagasNaoRemuneradas", vagasNaoRemuneradas);		    
        	    	}
        	    }
        
        	    /* @negocio: Uma solicitação de confirmação (aceite) da bolsa é enviada para os discentes aprovados. */	    
        	    Collection<Destinatario> destinatarios = new ArrayList<Destinatario>();
        	    UsuarioDao usuarioDao = getDAO(UsuarioDao.class, dmMov);
        	    ProjetoMonitoriaDao pmDao = getDAO(ProjetoMonitoriaDao.class, dmMov);
        	    try {
        	    	for (DiscenteMonitoria discenteMonitoria : dmMov.getProvaSelecao().getResultadoSelecao()) {
        	    		if (discenteMonitoria.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO && 
        	    				discenteMonitoria.getClassificacao() <= ( prova.getVagasNaoRemuneradas() + prova.getVagasRemuneradas() ) ) {
        	    			
        	    			Usuario usuarioDiscente = usuarioDao.findLeveByDiscente(discenteMonitoria.getDiscente().getDiscente());
        	    			if (usuarioDiscente != null) {
        	    				Destinatario destinatario = new Destinatario();
        	    				destinatarios = new ArrayList<Destinatario>();
	        	    			destinatario.setEmail(usuarioDiscente.getEmail());
	        	    			destinatario.setUsuario(usuarioDiscente);
	        	    			destinatarios.add(destinatario);
	            	    		
	        	    			CalendarioMonitoria cm = pmDao.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());	        	    
	        	    			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	        	    			Notificacao notificacao = new Notificacao();
	        	    			notificacao.setContentType(MailBody.HTML);
	        	    			notificacao.setTitulo("Resultado da Seleção (Programa de Monitoria).");
	        	    			notificacao.setMensagem("Você foi aprovado(a) na prova seletiva do projeto de monitoria: '" 
	        	    					+ prova.getProjetoEnsino().getAnoTitulo() 
	        	    					+ "'.<br/> Para confirmar sua participação no projeto acesse: " 
	        	    					+ "<b><i>Portal do Discente -> Monitoria -> Meus Projetos de Monitoria.</i></b><br/> "
	        	    					+ "Esta confirmação deverá ser realizada no período de " 
	        	    					+ sd.format(cm.getInicioConfirmacaoMonitoria()) +" até " + sd.format(cm.getFimConfirmacaoMonitoria()) + ".<br/>"
	        	    					+ " Caso tenha sido classificado(a) para receber Bolsa Remunerada, lembre-se de cadastrar" 
	        	    					+ " os seus dados bancários.");
	        	    			notificacao.setDestinatariosEmail(destinatarios);
	        	    			notificacao.setDestinatariosMensagem(destinatarios);
	        	    			notificarPorEmail(notificacao, dmMov.getUsuarioLogado(), null);
	        	    			notificarPorMensagem(notificacao, dmMov.getUsuarioLogado());
	        	    			dao.updateField(DiscenteMonitoria.class, discenteMonitoria.getId(), "situacaoDiscenteMonitoria", SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA);		    
        	    			}
        	    		}
        	    	}

        	    }finally {
        	    	usuarioDao.close();
        	    	pmDao.close();
        	    }
        	    
	    }finally {
	    	dao.close();
	    }
	}

    /**
     * Grava histórico de situação do monitor.
     * 
     * @param mov
     * @param dao
     * @param discenteMonitoria
     * @throws DAOException
     */
    private void gravarHistoricoDiscente(DiscenteMonitoriaMov mov,  GenericDAO dao, DiscenteMonitoria discenteMonitoria) throws DAOException {	
	HistoricoSituacaoDiscenteMonitoria historico = new HistoricoSituacaoDiscenteMonitoria();
	historico.setData(new Date());
	historico.setDiscenteMonitoria(discenteMonitoria);
	historico.setSituacaoDiscenteMonitoria(discenteMonitoria.getSituacaoDiscenteMonitoria());
	historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
	historico.setTipoVinculo(discenteMonitoria.getTipoVinculo());
	dao.create(historico);
    }
    
    
    /**
     * Faz a validação Final antes de cadastrar.
     * 
     * @throws NegocioException
     * @throws ArqException
     */
    public void validate(Movimento mov) throws NegocioException, ArqException {

    	DiscenteMonitoriaMov dmov = (DiscenteMonitoriaMov) mov;
    	ListaMensagens mensagens = new ListaMensagens();
    	NegocioException negocioException = new NegocioException();

    	/* @negocio: Operadores com o papel GESTOR_MONITORIA podem solicitar a não validação do discente. */
    	if (dmov.isValidar()) {

    		/* @negocio: Só finaliza o discente se o Calendário de monitoria existir para o ano atual*/
    		if (mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA)) {
    			checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);			

    			CalendarioMonitoria cm = getDAO(ProjetoMonitoriaDao.class, dmov).findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());
    			if (ValidatorUtil.isEmpty(cm)){
    				mensagens.addErro("Para finalizar este monitor, é necessário ter o Calendário de Monitoria do ano de "+CalendarUtils.getAnoAtual()+" cadastrado.");
    			}

    			/* @negocio: Garante que o monitor só seja finalizado no SIGAA quando for finalizado antes no SIPAC. */
    			RelatorioMonitoriaValidator.validaDiscenteComBolsaMonitoriaAtivaSIPAC(dmov.getDiscenteMonitoria(), mensagens );
    			
    			/* @negocio: Só finaliza o discente se o mesmo enviar o relatório de desligamento. */
    			if (dmov.isValidarRelatorios()) {
    				validaRelatorioDesligamento(mov, mensagens);
    			}
    		}

    		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RESULTADO_SELECAO_MONITORIA)) {		
    			DiscenteMonitoriaValidator.validaIniciarCadastroSelecao(dmov.getProvaSelecao(), CalendarUtils.getAnoAtual(), mensagens);		
    			for (DiscenteMonitoria d : dmov.getProvaSelecao().getResultadoSelecao()) {
    				if (d.getDiscente() != null) {
    					DiscenteMonitoriaValidator.validaAdicaoSelecao(d, dmov.getProvaSelecao(), mensagens, dmov.getCalendarioAcademico());
    				}
    			}
    		}

    		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_VINCULO_DISCENTEMONITORIA)) {
    			DiscenteMonitoriaValidator.validaDadosPrincipais(dmov.getDiscenteMonitoria(), mensagens);
    			
    			DiscenteMonitoria dm = dmov.getDiscenteMonitoria();
    			int novoTipoMonitoria = ((DiscenteMonitoriaMov) mov).getNovoTipoMonitoria();

    			// AGUARDANDO CONVOCAÇÃO DA PROGRAD -->> ASSUMIU MONITORIA 
    			if ((dm.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO)
    					&& ((novoTipoMonitoria == TipoVinculoDiscenteMonitoria.BOLSISTA) || (novoTipoMonitoria == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO))) {
    				//TODO implementar: verificação se o próximo monitor é ele mesmo.
    				DiscenteMonitoriaValidator.validaDiscenteAssumirMonitoria(dm,	mensagens);
    			}		
    		}

    		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_NOTA_SELECAO_MONITORIA)) {
    			DiscenteMonitoria dm = dmov.getDiscenteMonitoria();
    			if (dm.getNota() == null || dm.getNotaProva() == null) {
    				mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nota");
    			}else {
    				ValidatorUtil.validateMinValue(dm.getNota(), 0.0, "Nota Final", mensagens);
    				ValidatorUtil.validateMaxValue(dm.getNota(), 10.0, "Nota Final", mensagens);
    				ValidatorUtil.validateMinValue(dm.getNotaProva(), 0.0, "Nota da Prova", mensagens);
    				ValidatorUtil.validateMaxValue(dm.getNotaProva(), 10.0, "Nota da Prova", mensagens);
    			}
    		}


    		if (mov.getCodMovimento().equals(SigaaListaComando.ACEITAR_OU_RECUSAR_MONITORIA)) {
    			DiscenteMonitoria dm = dmov.getDiscenteMonitoria();
    			//aceitou a monitoria
    			if (dm.isSelecionado()) {
    				DiscenteMonitoriaValidator.validaDiscenteAssumirMonitoria(dm,	mensagens);
    				if (dm.isVinculoBolsista()) {
    					DiscenteMonitoriaValidator.validaDadosBancarios(dm, mensagens);
    				}
    				if (!dm.isConvocado()) {
    					mensagens.addErro("Somente discentes convocados podem realizar esta operação.");
    				}
    			}else {
    				ValidatorUtil.validateRequired(dm.getObservacao(), "Observação", mensagens);
    			}
    		}

    		if (!mensagens.isEmpty()) {
    			negocioException.setListaMensagens(mensagens);
    			throw negocioException; 
    		}

    	}	    
    }
    
    /** 
     * Verifica se o discente enviou o relatório de desligamento.
     *  
     * @throws DAOException 
     */
    private void validaRelatorioDesligamento(Movimento mov, ListaMensagens mensagens) throws DAOException {
    	DiscenteMonitoriaMov dmov = (DiscenteMonitoriaMov) mov;
		RelatorioProjetoMonitorDao daoRel = getDAO(RelatorioProjetoMonitorDao.class, mov);
		try {

			RelatorioMonitor relatorioDesligamento = daoRel.findByDiscenteMonitoriaTipoRelatorio(dmov.getDiscenteMonitoria(), TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR);

			if (relatorioDesligamento == null) {
				mensagens.addErro("Esta operação só poderá ser realizada após o envio do Relatório de Desligamento do Monitor.");
				mensagens.addErro(dmov.getDiscenteMonitoria().getDiscente().getNome()	+ " ainda não enviou o seu Relatório de Desligamento.");
			} else {
				if ((relatorioDesligamento.getCoordenacaoValidouDesligamento() == null) || (relatorioDesligamento.getProgradValidouDesligamento() == null)
						|| (!relatorioDesligamento.getCoordenacaoValidouDesligamento()) || (!relatorioDesligamento.getProgradValidouDesligamento())) {
					mensagens.addMensagem(MensagensMonitoria.RELATORIO_DESLIGAMENTO_NAO_VALIDADO);
				}
			}
		} finally {
			daoRel.close();
		}
    }
    


    /**
     * Notifica todos os destinatários selecionados por email.
     *
     * @param notificacao
     * @param remetente
     */
    private void notificarPorEmail(Notificacao notificacao, UsuarioGeral remetente, InscricaoSelecaoMonitoria inscr) {
	
		for (Destinatario destinatario : notificacao.getDestinatariosEmail()) {
		    MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setReplyTo("noReply@ufrn.br");
		    mail.setEmail(destinatario.getEmail());
	
		    mail.setAssunto(notificacao.getTitulo());
		    
		    String text = "";
		    if (inscr != null) {
		    	text += "Projeto de Monitoria: "  + inscr.getProjetoEnsino().getProjeto().getTitulo() + " <br /> ";
		    	text += "Prova Seleção: "  + inscr.getProvaSelecao().getTitulo() + " <br /> ";
		    	text += "Enviado por: "  + remetente.getNome() + " <br /> <br /> ";
			}
		    
		    text += notificacao.getMensagem();
		    text += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
		    		RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>"; 
		    
		    mail.setMensagem(text);
		    Mail.send(mail);
		}
    }

    /**
     * Notifica todos os destinatários selecionados por mensagens
     * no sistema
     * @param remetente
     *
     * @param destinatario
     */
    private void notificarPorMensagem(Notificacao notificacao, UsuarioGeral remetente) {

	Collection<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
	for (Destinatario destinatario : notificacao.getDestinatariosMensagem()) {
	    usuarios.add(destinatario.getUsuario());
	}

	Mensagem mensagem = new Mensagem();
	mensagem.setTitulo(notificacao.getTitulo());
	mensagem.setMensagem(StringUtils.stripHtmlTags(notificacao.getMensagem()));
	mensagem.setRemetente(remetente);
	mensagem.setAutomatica(true);
	mensagem.setLeituraObrigatoria(true);
	mensagem.setConfLeitura(true);
	mensagem.setEnviarEmail(true);

	ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(mensagem, remetente, usuarios);
    }
    
    /**
     * Convoca próximo discente de acordo com classificação no processo seletivo de monitoria
     * @param dmMov
     * @param prova
     * @param tipoBolsa
     * @throws NegocioException
     * @throws ArqException
     */
    private void convocarDiscenteMonitoria(DiscenteMonitoriaMov dmMov, ProvaSelecao prova, int tipoBolsa ) throws NegocioException, ArqException{
    	
    	if (ValidatorUtil.isEmpty(prova)) {
    		throw new NegocioException("Discente não está vinculado a uma Prova Seletiva. Convocação de um discente substituto não foi possível.");
    	}
    	
    	UsuarioDao dao = getDAO(UsuarioDao.class, dmMov);
    	try {
	    	DiscenteMonitoria discenteMonitoria = null;
	    	Collection<DiscenteMonitoria> convocados = new ArrayList<DiscenteMonitoria>();
	 
	    	if (dmMov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA)) {
	    	    validate(dmMov);
	    	    
	    	    // Verifica se os discentes pertencentes ao resultado da Seleção ainda estão ativos e remove os inativos do resultado.
	    	    Collection<DiscenteMonitoria> resultSelecao = new ArrayList<DiscenteMonitoria>();
	    	    for(DiscenteMonitoria d : prova.getResultadoSelecao()){
	    	    	Usuario usu = dao.findPrimeiroUsuarioByPessoa(d.getDiscente().getDiscente().getPessoa().getId());
	    	    	if(usu != null){
	    	    		resultSelecao.add(d);
	    	    	}
	    	    }
	    	    prova.setResultadoSelecao(resultSelecao);
	    	    
	    	    if(prova!= null && prova.isPossuiDiscenteEmEsperaNaoRemunerado()){
	    	    	if(tipoBolsa == TipoVinculoDiscenteMonitoria.BOLSISTA){
	    	    		if(prova.getMelhorNaoRemunerado() != null){
		    	    		discenteMonitoria = prova.getMelhorNaoRemunerado();
		    	    		discenteMonitoria.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA));
		    	    		discenteMonitoria.getTipoVinculo().setId(TipoVinculoDiscenteMonitoria.BOLSISTA);
		    	    		
		    	    		dao.update(discenteMonitoria);
		    	    		gravarHistoricoDiscente(dmMov, dao, discenteMonitoria);
		    	    		convocados.add(discenteMonitoria);
		    	    		
		    	    		convocar(convocados, dmMov, prova);
	    	    		}else if(prova.getMelhorEmEspera() != null){
	    	    			discenteMonitoria = prova.getMelhorEmEspera();
		    	    		discenteMonitoria.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA));
		    	    		discenteMonitoria.getTipoVinculo().setId(TipoVinculoDiscenteMonitoria.BOLSISTA);
		    	    		dao.update(discenteMonitoria);
		    	    		gravarHistoricoDiscente(dmMov, dao, discenteMonitoria);
		    	    		convocados.add(discenteMonitoria);
		    	    		
		    	    		convocar(convocados, dmMov, prova);
	    	    		}
	    	    		enviaEmailDesligamentoMonitoria(dmMov, convocados);
	    	    	}
	    	    	if(tipoBolsa == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO){
	    	    		if(prova.getMelhorEmEspera() != null){
		    	    		discenteMonitoria = prova.getMelhorEmEspera();
		    	    		discenteMonitoria.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA));
		    	    		discenteMonitoria.getTipoVinculo().setId(TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
		    	    		dao.update(discenteMonitoria);
		    	    		gravarHistoricoDiscente(dmMov, dao, discenteMonitoria);
		    	    		convocados.add(discenteMonitoria);
		    	    		
		    	    		convocar(convocados, dmMov, prova);
	    	    		}
	    	    	}
	    	    
	    	    }
	    	}
    	}finally {
    		dao.close();
    	}
    }

    /**
     * Método responsável por enviar um email de comunicação no momento do desligamento de um monitor bolsita.
     * @param dmMov
     * @param convocados
     * @throws DAOException
     */
	private void enviaEmailDesligamentoMonitoria(DiscenteMonitoriaMov dmMov, Collection<DiscenteMonitoria> convocados) throws DAOException {
		String emailDesligamentoMonitoria = ParametroHelper.getInstance().getParametro(ParametrosMonitoria.EMAIL_DESLIGAMENTO_MONITORIA);
		if (ValidatorUtil.isNotEmpty(emailDesligamentoMonitoria)) {
			UsuarioDao usuarioDao = getDAO(UsuarioDao.class, dmMov);
			Collection<Destinatario> destinatarios = null;
			try {
				String monitorConvocado = "";
			    for (DiscenteMonitoria discente : convocados) {
		    		if (discente.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA) {
		    		    Usuario usuarioMonitorConvocado = usuarioDao.findLeveByDiscente(discente.getDiscente().getDiscente());
		    		    monitorConvocado = usuarioMonitorConvocado.getPessoa().getNome();
		    		}
		    	}
			    
			    Destinatario monitorDesligado = new Destinatario();
			    Usuario usuarioMonitorDesligado = usuarioDao.findLeveByDiscente(dmMov.getDiscenteMonitoria().getDiscente().getDiscente());
			    monitorDesligado.setEmail(usuarioMonitorDesligado.getEmail());
			    monitorDesligado.setUsuario(usuarioMonitorDesligado);
			    
			    destinatarios = new ArrayList<Destinatario>();
			    Destinatario prograd = new Destinatario();
			    prograd.setEmail(emailDesligamentoMonitoria);
			    destinatarios.add(prograd);
			    
			    Notificacao notificacaoPrograd = new Notificacao();
			    notificacaoPrograd.setContentType(MailBody.HTML);
			    notificacaoPrograd.setTitulo("Troca de Vínculo (Programa de Monitoria).");
			    notificacaoPrograd.setMensagem("Houve uma mudança de vínculo de monitoria. O monitor " 
			    		+ usuarioMonitorDesligado.getPessoa().getNome()  
			    		+ " foi desligado.<br />Em seu lugar entrará o monitor " + monitorConvocado 
			    		+ ".<br />Favor, proceder o desligamento do antigo e cadastro do novo monitor no " 
			    		+ RepositorioDadosInstitucionais.get("siglaSipac") + ".");
			    notificacaoPrograd.setDestinatariosEmail(destinatarios);
			    notificarPorEmail(notificacaoPrograd, dmMov.getUsuarioLogado(), null);
			    
			    destinatarios = new ArrayList<Destinatario>();
			    destinatarios.add(monitorDesligado);
			    
			    Notificacao notificacaoMonitorDesligado = new Notificacao();
			    notificacaoMonitorDesligado.setContentType(MailBody.HTML);
			    notificacaoMonitorDesligado.setTitulo("Troca de Vínculo (Programa de Monitoria).");
			    notificacaoMonitorDesligado.setMensagem("Houve uma mudança de vínculo de monitoria. O monitor " 
			    		+ usuarioMonitorDesligado.getPessoa().getNome()  
			    		+ " foi desligado.<br />Em seu lugar entrará o monitor " + monitorConvocado + ".");
			    notificacaoMonitorDesligado.setDestinatariosEmail(destinatarios);
			    notificarPorEmail(notificacaoMonitorDesligado, dmMov.getUsuarioLogado(), null);
			} finally {
				usuarioDao.close();
			}
		}
	}
    
    /**
     * Convoca próximo discente de acordo com classificação no processo seletivo de monitoria após a rejeição de algum convocado.
     * @param dmMov
     * @param prova
     * @param tipoBolsa
     * @throws NegocioException
     * @throws ArqException
     */
    private void convocarDiscenteMonitoriaPosConvocacao(DiscenteMonitoriaMov dmMov, ProvaSelecao prova, int tipoBolsa ) throws NegocioException, ArqException{
    	
    	if (ValidatorUtil.isEmpty(prova)) {
    		throw new NegocioException("Discente não está vinculado a uma Prova Seletiva. Convocação de um discente substituto não foi possível.");
    	}

    	DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, dmMov);
    	try {
	    	DiscenteMonitoria discenteMonitoria = null;
	    	Collection<DiscenteMonitoria> convocados = new ArrayList<DiscenteMonitoria>();
	 
	    	if (dmMov.getCodMovimento().equals(SigaaListaComando.ACEITAR_OU_RECUSAR_MONITORIA)) {
	    	    validate(dmMov);
	    	    if(prova.isPossuiDiscenteEmEsperaNaoRemunerado()){
	    	    	if(tipoBolsa == TipoVinculoDiscenteMonitoria.BOLSISTA){
	    	    		if(prova.getMelhorNaoRemunerado() != null){
		    	    		discenteMonitoria = prova.getMelhorNaoRemunerado();
		    	    		discenteMonitoria.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA));
		    	    		discenteMonitoria.getTipoVinculo().setId(TipoVinculoDiscenteMonitoria.BOLSISTA);
		    	    		
		    	    		dao.update(discenteMonitoria);
		    	    		gravarHistoricoDiscente(dmMov, dao, discenteMonitoria);
		    	    		convocados.add(discenteMonitoria);
		    	    		
		    	    		convocar(convocados, dmMov, prova);
	    	    		}else if(prova.getMelhorEmEspera() != null){
	    	    			discenteMonitoria = prova.getMelhorEmEspera();
		    	    		discenteMonitoria.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA));
		    	    		discenteMonitoria.getTipoVinculo().setId(TipoVinculoDiscenteMonitoria.BOLSISTA);
		    	    		dao.update(discenteMonitoria);
		    	    		gravarHistoricoDiscente(dmMov, dao, discenteMonitoria);
		    	    		convocados.add(discenteMonitoria);
		    	    		
		    	    		convocar(convocados, dmMov, prova);
	    	    		}
	    	    		
	    	    	}
	    	    	if(tipoBolsa == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO){
	    	    		if(prova.getMelhorEmEspera() != null){
		    	    		discenteMonitoria = prova.getMelhorEmEspera();
		    	    		discenteMonitoria.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA));
		    	    		discenteMonitoria.getTipoVinculo().setId(TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
		    	    		dao.update(discenteMonitoria);
		    	    		gravarHistoricoDiscente(dmMov, dao, discenteMonitoria);
		    	    		convocados.add(discenteMonitoria);
		    	    		
		    	    		convocar(convocados, dmMov, prova);
	    	    		}
	    	    	}
	    	    
	    	    }
	    	    
	    	}
    	}finally {
    		dao.close();
    	}
    }
    
    /**
     * Convoca os discentes a participarem de projetos de monitoria através de notificações de email e mensagem no sistema.
     * @throws DAOException 
     * @throws NegocioException 
     */
    private void convocar(Collection<DiscenteMonitoria> convocados, DiscenteMonitoriaMov dmMov, ProvaSelecao prova) throws DAOException, NegocioException{
    	
    	/** @negocio: Uma solicitação de confirmação (aceite) da bolsa é enviada para os discentes aprovados. */	    
	    Collection<Destinatario> destinatarios = new ArrayList<Destinatario>();
	    UsuarioDao usuarioDao = getDAO(UsuarioDao.class, dmMov);
	    try {
		    for (DiscenteMonitoria discente : convocados) {
	    		if (discente.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA) {
	    		    Destinatario destinatario = new Destinatario();
	    		    Usuario usuarioDiscente = usuarioDao.findLeveByDiscente(discente.getDiscente().getDiscente());
	    		    destinatario.setEmail(usuarioDiscente.getEmail());
	    		    destinatario.setUsuario(usuarioDiscente);
	    		    destinatarios.add(destinatario);
	    		}
	    	}
	
		    CalendarioMonitoria cm = getDAO(ProjetoMonitoriaDao.class, dmMov).findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());
			if (ValidatorUtil.isEmpty(cm)){
				throw new NegocioException("Não foi possível convocar o próximo monitor(a), pois o Calendário de Monitoria do ano de "+CalendarUtils.getAnoAtual()+" não está definido.");
			}

		    SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		    Notificacao notificacao = new Notificacao();
		    notificacao.setContentType(MailBody.HTML);
		    notificacao.setTitulo("Resultado da Seleção (Programa de Monitoria).");
		    notificacao.setMensagem("Você foi aprovado(a) na prova seletiva do projeto de monitoria: '" 
			    + prova.getProjetoEnsino().getAnoTitulo() 
			    + "'.<br/> Para confirmar sua participação no projeto acesse: " 
			    + "<b><i>Portal do Discente -> Monitoria -> Meus Projetos de Monitoria.</i></b><br/> "
			    + "Esta confirmação deverá ser realizada no período de " 
			    + sd.format(cm.getInicioConfirmacaoMonitoria()) +" até " + sd.format(cm.getFimConfirmacaoMonitoria()) + ".<br/>"
			    + " Caso tenha sido classificado(a) para receber Bolsa Remunerada, lembre-se de cadastrar" 
			    + " os seus dados bancários.");
		    notificacao.setDestinatariosEmail(destinatarios);
		    notificacao.setDestinatariosMensagem(destinatarios);
		    notificarPorEmail(notificacao, dmMov.getUsuarioLogado(), null);
		    notificarPorMensagem(notificacao, dmMov.getUsuarioLogado());
		    
	    }finally {
	    	usuarioDao.close();
	    }
    }


}
