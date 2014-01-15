/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.ControleFluxoAtividadeExtensao;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.extensao.negocio.ProcessadorAtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.monitoria.negocio.ProcessadorCadastroProjeto;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaMov;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.ProcessadorAlteracaoSituacaoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.ProcessadorProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.jsf.ControleFluxo;

/**
 * Processador para realizar o cadastro de uma atividade de extensão.
 * 
 * @author ilueny santos
 * 
 */
public class ProcessadorProjetoBase extends AbstractProcessador {

    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

	MovimentoCadastro movC = (MovimentoCadastro) mov;
	validate(movC);

	if (mov.getCodMovimento().equals(SigaaListaComando.GRAVAR_RASCUNHO_PROJETO_BASE)) {
	    return gravarTemporariamente(movC, true); // não grava situações repetidas no histórico
	} else if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_PROJETO_BASE)) {
	    return salvar(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_PROJETO_BASE)) {
	    return enviar(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.CONCLUIR_PROJETO_BASE)) {
	    return concluir(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PROJETO_BASE)) {
	    remover(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_BASE)) {
	    alterarSituacaoProjeto(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.EXECUTAR_PROJETO_BASE)){
	    coordenacaoExecutarProjetoBase(movC);
	}else if (mov.getCodMovimento().equals(SigaaListaComando.NAO_EXECUTAR_PROJETO_BASE)){
	    coordenacaoNaoExecutarProjetoBase(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ORCAMENTO_PROJETO_BASE)) {
	    alterarOrcamento(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.CONCEDER_RECURSOS_PROJETO)) {
	    concederRecursos(movC);
	}


	return movC.getObjMovimentado();
    }

    /**
     * Registra no histórico que o coordenador aceitou a execução do projeto.
     * 
     * @param mov
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private void coordenacaoExecutarProjetoBase(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
	GenericDAO dao = getGenericDAO(mov);
	try{
	    Projeto projeto = mov.getObjMovimentado();
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_ACEITOU_EXECUCAO, projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
	    executarProjetoBase(mov);
	}finally {
	    dao.close();
	}
    }
    
    /**
     * Status de projetos aprovados com ou sem recursos passam a ter status de em execução.
     *  
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void executarProjetoBase(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
    	try{
    		Projeto projeto = mov.getObjMovimentado();
    		projeto.getSituacaoProjeto().setId(TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
    		for (MembroProjeto membro : projeto.getEquipe()) {
    			if (membro.getDataInicio() == null) { membro.setDataInicio(projeto.getDataInicio()); }
    			if (membro.getDataFim() == null) { membro.setDataFim(projeto.getDataFim()); }
    			dao.update(membro);
    		}
    		dao.updateField(Projeto.class, projeto.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
    		processarProjetosAssociados(mov, Boolean.TRUE);	    	
    	}finally{
    		dao.close();
    	}
    }
    
    /**
     * Registra no histórico que o coordenador Não aceitou a execução do projeto.
     * 
     * @param mov
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private void coordenacaoNaoExecutarProjetoBase(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
    	try{
    		Projeto projeto = mov.getObjMovimentado();
    		projeto.getSituacaoProjeto().setId(TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO);
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO, projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
    		naoExecutarProjetoBase(mov);
    	}finally {
    		dao.close();
    	}
    }

    
    /**
     * Status de projetos aprovados com ou sem recursos passam a ter status de em execução.
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void naoExecutarProjetoBase(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
    	try{
    		Projeto projeto = mov.getObjMovimentado();
    		projeto.getSituacaoProjeto().setId(TipoSituacaoProjeto.PROJETO_BASE_CANCELADO);
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
    		dao.updateField(Projeto.class,projeto.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_CANCELADO);
    		for (MembroProjeto mp : projeto.getEquipe()) {
    			dao.updateFields(MembroProjeto.class, mp.getId(), new String [] {"dataInicio", "dataFim"}, new Object [] {null, null});
    		}
    		processarProjetosAssociados(mov, Boolean.FALSE);
    	}finally{
    		dao.close();
    	}
    }

    
    
    /**
     * Realiza a execução de todos os projetos associados ao projeto base. 
     * 
     * @param mov
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private void processarProjetosAssociados(MovimentoCadastro mov, boolean executar) throws NegocioException, ArqException, RemoteException {
    	Projeto projeto = mov.getObjMovimentado();
    	if(projeto.isProjetoAssociado()){
    		if(projeto.isEnsino()) {
    			processarEnsinoAssociado(mov, executar);
    		}
    		if(projeto.isPesquisa()) {
    			processarPesquisaAssociado(mov, executar);
    		}
    		if(projeto.isExtensao()) {
    			processarExtensaoAssociada(mov, executar);
    		}
    	}	
    }
     
    /**
     * Executa projeto de ensino associado.
     * 
     * @param projeto
     * @param mov
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private void processarEnsinoAssociado(MovimentoCadastro mov, boolean executar) throws DAOException {    	
    	ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, mov);
    	try { 
    		Projeto projetoBase = mov.getObjMovimentado();

    		Collection<ProjetoEnsino> projetos = new ArrayList<ProjetoEnsino>(); 
    		if (projetoBase.isProgramaMonitoria()) {
    			projetos.addAll(dao.findByProjetosBaseAndTipo(projetoBase.getId(), TipoProjetoEnsino.PROJETO_DE_MONITORIA));
    		}
    		
    		if (projetoBase.isMelhoriaQualidadeEnsino()) { 
    			projetos.addAll(dao.findByProjetosBaseAndTipo(projetoBase.getId(), TipoProjetoEnsino.PROJETO_PAMQEG));
    		}

    		for (ProjetoEnsino pm : projetos) {
    			if (pm.getDataEnvio() != null && executar){
    				pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_EM_EXECUCAO));
    				ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
    				dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.MON_EM_EXECUCAO);
    			}
    			if (!executar){     		    
    				pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CANCELADO));
    				ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
    				dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.MON_CANCELADO);
    			}
    		}
    	}finally{
    		dao.close();
    	}
    }

    /**
     * Executa um projeto de pesquisa associado.
     * 
     * @param mov
     * @throws DAOException
     */
    private void processarPesquisaAssociado(MovimentoCadastro mov, boolean executar) throws DAOException {
	//TODO: execuçao de projeto de pesquisa
    }
    
    /**
     * Faz com que um projeto entre em execução.
     * 
     * @param projeto
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void processarExtensaoAssociada(MovimentoCadastro mov, boolean executar) throws NegocioException, ArqException, RemoteException {
    	AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov );
    	try {
    		Projeto projeto = mov.getObjMovimentado();
    		Collection<AtividadeExtensao> atividades =  dao.findByProjetoBase(projeto.getId());
    		ProcessadorAtividadeExtensao proAcao = new ProcessadorAtividadeExtensao();
    		for (AtividadeExtensao atv : atividades) {
    			atv = dao.findByPrimaryKey(atv.getId(), AtividadeExtensao.class);
    			CadastroExtensaoMov mov1 = new CadastroExtensaoMov();
    			mov1.setSistema(mov.getSistema());
    			mov1.setAtividade(atv);
    			mov1.setUsuarioLogado(mov.getUsuarioLogado());
    			if(atv.getDataEnvio() != null && executar) {
    				mov1.setCodMovimento(SigaaListaComando.EXECUTAR_ATIVIDADE_EXTENSAO);
    				proAcao.execute(mov1);
    			}
    			if(!executar) {
    				mov1.setCodMovimento(SigaaListaComando.NAO_EXECUTAR_ATIVIDADE_EXTENSAO);
    				proAcao.execute(mov1);
    			}
    		} 
    	}finally {
    		dao.close();
    	}		
    }
    

    
    /**
     * Marca um projeto como removido.
     * 
     * @param mov
     * @return
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void remover(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
    	try {
    		// Definir a situação do projeto como removido
    		Projeto projeto = mov.getObjMovimentado();
    		projeto = dao.findByPrimaryKey(projeto.getId(), Projeto.class);
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO, projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
    		projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO));
    		projeto.setAtivo(false);
    		dao.update(projeto);

    		for (MembroProjeto mp : projeto.getEquipe()) {
    			mp.setAtivo(false);
    			mp.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
    			dao.update(mp);
    		}

    		//Removendo projetos de ensino
    		Collection<ProjetoEnsino> projetos = dao.findByExactField(ProjetoEnsino.class, "projeto.id", projeto.getId());
    		for (ProjetoEnsino projetoEnsino : projetos) {
    			ProjetoMonitoriaMov mov1 = new ProjetoMonitoriaMov();
    			mov1.setSistema(mov.getSistema());
    			mov1.setUsuarioLogado(mov.getUsuarioLogado());
    			mov1.setAcao(ProjetoMonitoriaMov.ACAO_DESATIVAR_PROJETO);
    			mov1.setObjMovimentado(projetoEnsino);
    			ProcessadorCadastroProjeto proc1 = new ProcessadorCadastroProjeto();
    			proc1.execute(mov1);
    		}

    		//Removendo ações de extensão
    		Collection<AtividadeExtensao> atividades = dao.findByExactField(AtividadeExtensao.class, "projeto.id", projeto.getId());
    		for (AtividadeExtensao atividade : atividades) {
    			CadastroExtensaoMov mov2 = new CadastroExtensaoMov();
    			mov2.setSistema(mov.getSistema());
    			mov2.setUsuarioLogado(mov.getUsuarioLogado());
    			mov2.setCodMovimento(SigaaListaComando.REMOVER_ATIVIDADE_EXTENSAO);
    			mov2.setAtividade(atividade);
    			ProcessadorAtividadeExtensao proc2 = new ProcessadorAtividadeExtensao();
    			proc2.execute(mov2);
    		}

    		//Removendo projetos de pesquisa
    		Collection<ProjetoPesquisa> pesquisas = dao.findByExactField(ProjetoPesquisa.class, "projeto.id", projeto.getId());
    		for (ProjetoPesquisa pesquisa : pesquisas) {
    			pesquisa.getProjeto().setUsuarioLogado(mov.getUsuarioLogado());
    			MovimentoProjetoPesquisa mov3 = new MovimentoProjetoPesquisa();
    			mov3.setSistema(mov.getSistema());
    			mov3.setUsuarioLogado(mov.getUsuarioLogado());
    			mov3.setCodMovimento(SigaaListaComando.REMOVER_PROJETO_PESQUISA);
    			mov3.setProjeto(pesquisa);
    			ProcessadorProjetoPesquisa proc3 = new ProcessadorProjetoPesquisa();
    			proc3.execute(mov3);
    		}

    	} finally {
    		dao.close();
    	}
    }

    /**
     * Finaliza um projeto.
     * Projeto passa para a situação de concluído e todos os membros da equipe
     * são finalizados com a data atual.
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     * 
     */
    private Projeto concluir(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
    	try {
    		Projeto projeto = dao.refresh((Projeto)mov.getObjMovimentado());
    		Date hoje =  DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

    		//Concluíndo projetos de ensino
    		Collection<ProjetoEnsino> projetos = dao.findByExactField(ProjetoEnsino.class, "projeto.id", projeto.getId());
    		for (ProjetoEnsino projetoEnsino : projetos) {
    			ProjetoMonitoriaMov mov1 = new ProjetoMonitoriaMov();
    			mov1.setSistema(mov.getSistema());
    			mov1.setUsuarioLogado(mov.getUsuarioLogado());
    			mov1.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
    			mov1.setAcao(ProjetoMonitoriaMov.ACAO_CONCLUIR_PROJETO);
    			mov1.setObjMovimentado(projetoEnsino);
    			ProcessadorCadastroProjeto proc1 = new ProcessadorCadastroProjeto();
    			proc1.execute(mov1);
    		}
    		dao.clearSession();

    		//Concluindo ações de extensão
    		Collection<AtividadeExtensao> atividades = dao.findByExactField(AtividadeExtensao.class, "projeto.id", projeto.getId());
    		for (AtividadeExtensao atividade : atividades) {
    			CadastroExtensaoMov mov2 = new CadastroExtensaoMov();
    			mov2.setSistema(mov.getSistema());
    			mov2.setUsuarioLogado(mov.getUsuarioLogado());
    			mov2.setCodMovimento(SigaaListaComando.CONCLUIR_ATIVIDADE_EXTENSAO);
    			mov2.setAtividade(atividade);
    			ProcessadorAtividadeExtensao proc2 = new ProcessadorAtividadeExtensao();
    			proc2.execute(mov2);
    		}
    		dao.clearSession();

    		//Concluindo projetos de pesquisa
    		Collection<ProjetoPesquisa> pesquisas = dao.findByExactField(ProjetoPesquisa.class, "projeto.id", projeto.getId());
    		for (ProjetoPesquisa pesquisa : pesquisas) {
    			MovimentoProjetoPesquisa mov3 = new MovimentoProjetoPesquisa();
    			mov3.setSistema(mov.getSistema());
    			mov3.setUsuarioLogado(mov.getUsuarioLogado());
    			mov3.setCodMovimento(SigaaListaComando.FINALIZAR_PROJETO_PESQUISA);
    			mov3.setProjeto(pesquisa);
    			ProcessadorAlteracaoSituacaoProjetoPesquisa proc3 = new ProcessadorAlteracaoSituacaoProjetoPesquisa();
    			proc3.execute(mov3);
    		}
    		dao.clearSession();
    		

    		// Ajustando data fim do projeto
			if ((projeto.getDataFim() == null) || (hoje.before(projeto.getDataFim()))) {
				projeto.setDataFim(new Date());
			}
    		
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO, projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
    		dao.updateFields(Projeto.class, projeto.getId(),new String[] {"situacaoProjeto.id", "ativo", "dataFim"}, new Object[] {TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO, Boolean.TRUE, projeto.getDataFim()});
    		
    		//Finalizando equipe
    		Collection<MembroProjeto> equipe = dao.findByExactField(MembroProjeto.class, "projeto.id", projeto.getId());
    		for (MembroProjeto mp : equipe) {
    			if ((mp.getDataFim() == null) || (hoje.before(mp.getDataFim()))) {
    				dao.updateField(MembroProjeto.class, mp.getId(), "dataFim", projeto.getDataFim());
    			}
    		}	    
    		
    		// Finalizando todos os discentes e planos de trabalho da ação.
    		Collection<DiscenteProjeto> discentes = dao.findByExactField(DiscenteProjeto.class, "projeto.id", projeto.getId());
    		for (DiscenteProjeto dp : discentes) {
    			
    			if (dp.getSituacaoDiscenteProjeto().getId() != TipoSituacaoDiscenteProjeto.FINALIZADO 
    					&& dp.getSituacaoDiscenteProjeto().getId() != TipoSituacaoDiscenteProjeto.EXCLUIDO) {
    				dao.updateFields(DiscenteProjeto.class, dp.getId(), new String[] {"dataFim","situacaoDiscenteProjeto.id"}, new Object[] {projeto.getDataFim(), TipoSituacaoDiscenteProjeto.FINALIZADO});
    			}
    			
    			//Finalizando o plano do discente.
    			PlanoTrabalhoProjeto pt = dp.getPlanoTrabalhoProjeto();
    			if (pt.getDataFim() == null || hoje.before(pt.getDataFim())) {
    				dao.updateField(PlanoTrabalhoProjeto.class, pt.getId(), "dataFim", projeto.getDataFim());
    			}
    		}

    		return projeto;
    	} finally {
    		dao.close();
    	}
    }
    
    /**
     * Envia o projeto para os departamentos envolvidos.
     * Altera a situação do projeto base para submetido.
     * 
     * @param mov
     * @return
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private Projeto enviar(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	try {

	    Projeto projeto = mov.getObjMovimentado();
	    if (projeto.isInterno()) {
	    	projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO));
	    }else { //projetos externos

	    	if (projeto.isConvenio()) {
	    		//@negocio: Projetos externos com convênio devem ser cadastrado como REGISTRADO e só entrará EM EXECUÇÃO após a finalização dos instrumentos legais.
	    		projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_REGISTRADO));
	    		
	    	}else {
	    		//@negocio: Projetos que foram aprovados junto ao CNPQ, FAPERN, etc a relação é diretamente entre o órgão financiador e o pesquisador. 
	    		//          Não há recebimento de recursos nem execução na IFES.
	    		//			Nestes casos, o projeto já será considerado EM EXECUÇÂO.
	    		projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO));
	    	}
	    }
	    mov.setObjMovimentado(projeto);
	    projeto = salvar(mov);
	    
	    //enviarProjetoBaseDepartamentos(mov);
	    //enviarProjetosAssociadosDepartamentos(mov);
	    notificarChefesDepartamentos(mov);
	    
	    alterarHistoricoSituacao(dao, mov, projeto.getSituacaoProjeto().getId(), false);
	    return projeto;

	} finally {
	    dao.close();
	}
    }
    
    /**
     * Método utilizado para notificar por e-mail os Chefes dos departamentos envolvidos.
     * 
     * @param mov
     * @throws DAOException
     */
    public void notificarChefesDepartamentos(MovimentoCadastro mov) throws DAOException {
	Projeto projeto = mov.getObjMovimentado();
	if (!ValidatorUtil.isEmpty(projeto.getEquipe())) {
        	UnidadeDao dao = getDAO(UnidadeDao.class, mov);
        	try {
        	    /** @negocio: Um email com os dados do projeto deve ser enviado para os deptos. de todos os servidores envolvidos. 
        	     * Sem duplicação no envio, caso tenha mais de um servidor do mesmo departamento. */
        	    Set<Integer> unidadesParticipantes = new HashSet<Integer>();        	    
        	    for (MembroProjeto mp : projeto.getEquipe()) {
        		if (!ValidatorUtil.isEmpty(mp.getServidor()) && !ValidatorUtil.isEmpty(mp.getServidor().getUnidade())) {
        		    unidadesParticipantes.add(mp.getServidor().getUnidade().getId());
        		}
        	    }
        	    
        	    List<Responsavel> responsaveis = dao.findResponsaveisByUnidades(unidadesParticipantes, new char[] {NivelResponsabilidade.CHEFE} );
        	    EnvioMensagemHelper.comunicarResponsaveisUnidade(projeto, responsaveis);
        	    
        	}finally {
        	    dao.close();
        	}
	}
    }
    
    /**
     * Distribui o projeto para os departamentos envolvidos.
     * Gera uma autorização que deverá se aprovada ou reprovada pelos
     * diretores dos centros de todos os docentes envolvidos no projeto.
     * 
     * @param dao
     * @param acao
     * @throws DAOException
     */
    public void enviarProjetoBaseDepartamentos(MovimentoCadastro mov) throws DAOException {
	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	try {
	    Projeto projeto = mov.getObjMovimentado();
	    Set<Unidade> unidadesParticipantes = new HashSet<Unidade>();

	    // Recuperando todas as unidades que já estão no banco. evitando duplicação de autorizações.
	    Collection<AutorizacaoDepartamento> autorizacoesNoBanco = new ArrayList<AutorizacaoDepartamento>();
	    autorizacoesNoBanco = dao.findByExactField(AutorizacaoDepartamento.class, "projeto.id", projeto.getId());
	    for (AutorizacaoDepartamento autorizacao : autorizacoesNoBanco) {
		if (autorizacao.isAtivo()) {
		    unidadesParticipantes.add(autorizacao.getUnidade());
		}
	    }

	    /** @negocio: O projeto deve ser distribuído para os deptos. de todos os servidores envolvidos. */
	    for (MembroProjeto mp : projeto.getEquipe()) {
		if (!ValidatorUtil.isEmpty(mp.getServidor()) && !ValidatorUtil.isEmpty(mp.getServidor().getUnidade())) {
		    if (unidadesParticipantes.add(mp.getServidor().getUnidade())) {
			AutorizacaoDepartamento auto = new AutorizacaoDepartamento();
			auto.setUnidade(mp.getServidor().getUnidade());
			auto.setProjeto(projeto);
			auto.setTipoAutorizacao(null);
			auto.setAtivo(true);
			dao.create(auto);
		    }
		}
	    }
	}finally {
	    dao.close();
	}
    }


    /**
     * Submete todos os projetos associados ao base para os seus devidos departamentos.
     * A submissão aos departamentos gera uma solicitação de autorização por parte dos 
     * departamentos envolvidos para a execução do projeto.
     * 
     * @param mov
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    @SuppressWarnings("unused")
    private void enviarProjetosAssociadosDepartamentos(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
	Projeto projeto = mov.getObjMovimentado();
	
	//Ensino
	if (projeto.isEnsino()) {
	    ProcessadorCadastroProjeto procMonitoria = new ProcessadorCadastroProjeto();
	    ProjetoMonitoriaDao daoM = getDAO(ProjetoMonitoriaDao.class, mov);
	    try {
		if (projeto.isProgramaMonitoria()) {
		    ProjetoEnsino projetoMonitoria =  daoM.findByProjetoBaseAndTipo(projeto.getId(), TipoProjetoEnsino.PROJETO_DE_MONITORIA);
			ProjetoMonitoriaMov mov1 = new ProjetoMonitoriaMov();
			mov1.setSistema(mov.getSistema());
			mov1.setUsuarioLogado(mov.getUsuarioLogado());
			mov1.setObjMovimentado(projetoMonitoria);
			mov1.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
			mov1.setAcao(ProjetoMonitoriaMov.ACAO_ENVIAR_PROPOSTA_ASSOCIADA_AOS_DEPARTAMENTOS);
			procMonitoria.execute(mov1);
		}        	    
		if (projeto.isMelhoriaQualidadeEnsino()) {        		
		    ProjetoEnsino projetoPAMQEG =  daoM.findByProjetoBaseAndTipo(projeto.getId(), TipoProjetoEnsino.PROJETO_PAMQEG);
			ProjetoMonitoriaMov mov2 = new ProjetoMonitoriaMov();
			mov2.setSistema(mov.getSistema());
			mov2.setUsuarioLogado(mov.getUsuarioLogado());
			mov2.setObjMovimentado(projetoPAMQEG);
			mov2.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
			mov2.setAcao(ProjetoMonitoriaMov.ACAO_ENVIAR_PROPOSTA_ASSOCIADA_AOS_DEPARTAMENTOS);
			procMonitoria.execute(mov2);
		}
	    }finally {
		daoM.close();
	    }
	}	    	

	//Pesquisa
	// Neste ponto o projeto de pesquisa deveria ter sua situação alterada.
	// A situação do projeto de pesquisa é compartilhada com a situação do Projeto Base, então, uma alteração no 
	// Projeto Base altera a situação do projeto de pesquisa


	//Extensão
	if (projeto.isExtensao()) {
	    ProcessadorAtividadeExtensao proAcao = new ProcessadorAtividadeExtensao();
	    AtividadeExtensaoDao daoA = getDAO(AtividadeExtensaoDao.class, mov);
	    try {
		if (projeto.isProgramaExtensao()) {
		    AtividadeExtensao programaEx = daoA.findAcaoByProjetoAndTipoAcao(projeto.getId(), TipoAtividadeExtensao.PROGRAMA);
		    CadastroExtensaoMov mov3 = new CadastroExtensaoMov();
		    mov3.setSistema(mov.getSistema());
		    mov3.setAtividade(programaEx);
		    mov3.setUsuarioLogado(mov.getUsuarioLogado());
		    mov3.setCodMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_ASSOCIADA_EXTENSAO);
		    proAcao.execute(mov3);
		}

		if (projeto.isProjetoExtensao()) {
		    AtividadeExtensao projetoEx = daoA.findAcaoByProjetoAndTipoAcao(projeto.getId(), TipoAtividadeExtensao.PROJETO);
		    CadastroExtensaoMov mov4 = new CadastroExtensaoMov();
		    mov4.setSistema(mov.getSistema());
		    mov4.setAtividade(projetoEx);
		    mov4.setUsuarioLogado(mov.getUsuarioLogado());
		    mov4.setCodMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_ASSOCIADA_EXTENSAO);
		    proAcao.execute(mov4);
		}

		if (projeto.isCursoExtensao()) {
		    AtividadeExtensao cursoEx = daoA.findAcaoByProjetoAndTipoAcao(projeto.getId(), TipoAtividadeExtensao.CURSO);
		    CadastroExtensaoMov mov5 = new CadastroExtensaoMov();
		    mov5.setSistema(mov.getSistema());
		    mov5.setAtividade(cursoEx);
		    mov5.setUsuarioLogado(mov.getUsuarioLogado());
		    mov5.setCodMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_ASSOCIADA_EXTENSAO);
		    proAcao.execute(mov5);
		}

		if (projeto.isEventoExtensao()) {
		    AtividadeExtensao eventoEx = daoA.findAcaoByProjetoAndTipoAcao(projeto.getId(), TipoAtividadeExtensao.EVENTO);
		    CadastroExtensaoMov mov6 = new CadastroExtensaoMov();
		    mov6.setSistema(mov.getSistema());
		    mov6.setAtividade(eventoEx);
		    mov6.setUsuarioLogado(mov.getUsuarioLogado());
		    mov6.setCodMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_ASSOCIADA_EXTENSAO);
		    proAcao.execute(mov6);
		}
	    }finally {
		daoA.close();
	    }
	}	    	
    }
    
    

    /**
     * Grava atividade durante o cadastro.
     * 
     * @param mov
     * @return
     * @throws NegocioException
     * @throws DAOException
     */
    private Projeto gravarTemporariamente(MovimentoCadastro mov, Boolean verificarHistoricoRepetido) throws NegocioException, DAOException {
	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	try {
	    Projeto projeto = mov.getObjMovimentado();

	    /** @negocio: Ações alteradas através do menu de gestão de ações acadêmicas, por um membro do CIEPE, não podem ter sua situação alterada para cadastro em andamento. */
	    /** @negocio: Projetos na situação em execução não voltam para situação cadastro em andamento. */
	    /** @negocio: A situação de cadastro em andamento só é definida para a ação em fluxo normal de cadastro de proposta para ser aprovada por membros do comitê. */
	    if ((mov.getAcao() != SigaaListaComando.ALTERAR_CADASTRO_PROJETO_BASE.getId()) 
		    && (projeto.getSituacaoProjeto().getId() !=  TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO) ) {
		projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO));
	    }

	    projeto = salvar(mov);
	    alterarHistoricoSituacao(dao, mov, projeto.getSituacaoProjeto().getId(), verificarHistoricoRepetido);
	    return projeto;

	} finally {
	    dao.close();
	}

    }

    /**
     * Cadastro de Equipe, Orçamento, Arquivos, etc. 
     * 
     * @param projeto
     * @param dao
     * @throws DAOException
     */
    private Projeto salvar(MovimentoCadastro mov) throws DAOException {
    	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
    	try {
    		Projeto projeto = mov.getObjMovimentado();

    		Projeto pOld = dao.findByPrimaryKey(projeto.getId(), Projeto.class, "id");
    		if (projeto.getId() == 0 || (projeto.getAno() != ( (pOld != null && pOld.getAno() != null) ? pOld.getAno() : 0) ) ) {
    			projeto.setNumeroInstitucional(getDAO(ProjetoDao.class, projeto).findNextNumeroInstitucional(projeto.getAno()));
    		}

    		//Só atualiza o cronograma se estiver no passo do cronograma
    		if(projeto.getTipoProjeto().getId() == TipoProjeto.ASSOCIADO){
    			ControleFluxo controleFluxo = (ControleFluxo) mov.getObjAuxiliar();
    			if (controleFluxo != null && controleFluxo.getPassos()[controleFluxo.getPassoAtual()].contains("Cronograma") ) {
    				removerCronogramaAntigo(mov);
    			}
    		}
    		if(projeto.getTipoProjeto().getId() == TipoProjeto.EXTENSAO){
    			ControleFluxoAtividadeExtensao controleFluxo = (ControleFluxoAtividadeExtensao) mov.getObjAuxiliar();
    			if (controleFluxo != null && controleFluxo.getPassos()[controleFluxo.getPassoAtual()].contains("Cronograma") ) {
    				removerCronogramaAntigo(mov);
    			}
    		}

    		//evitando erro de 'no object references an unsaved transient instance'
    		if (ValidatorUtil.isEmpty(projeto.getClassificacaoFinanciadora())) {
    			projeto.setClassificacaoFinanciadora(null);
    		}

    		if (ValidatorUtil.isEmpty(projeto.getSituacaoProjeto()))  {
    			projeto.setSituacaoProjeto(null);
    		}

    		if (ValidatorUtil.isEmpty(projeto.getUnidadeOrcamentaria()))  {
    			projeto.setUnidadeOrcamentaria(null);
    		}

    		if (ValidatorUtil.isEmpty(projeto.getAreaConhecimentoCnpq())) {
    			projeto.setAreaConhecimentoCnpq(null);
    		}

    		if (ValidatorUtil.isEmpty(projeto.getEdital())) {
    			projeto.setEdital(null);
    		}

    		if (projeto.getCoordenador() != null && ValidatorUtil.isEmpty(projeto.getCoordenador().getServidor())) {
    			projeto.setCoordenador(null);
    		}

    		dao.detach(pOld);
    		dao.createOrUpdate(projeto);
    		
    		// Equipe
    		if (!ValidatorUtil.isEmpty(projeto.getEquipe())) {
    			for (MembroProjeto mp : projeto.getEquipe()) {
    				dao.createOrUpdate(mp);
    				if(mp.isCoordenadorAtivo()) {
    					dao.updateField(Projeto.class, mp.getProjeto().getId(), "coordenador.id", mp.getId());
    				}
    				dao.detach(mp);
    			}
    		}

    		// Orçamento
    		if (!ValidatorUtil.isEmpty(projeto.getOrcamento())) {
    			for (OrcamentoDetalhado od : projeto.getOrcamento()) {
    				dao.createOrUpdate(od);
    			}
    		}

    		// Orçamento Consolidado
    		if (!ValidatorUtil.isEmpty(projeto.getOrcamentoConsolidado())) {
    			for (OrcamentoConsolidado oc : projeto.getOrcamentoConsolidado()) {
    				dao.createOrUpdate(oc);
    			}
    		}

    		// Arquivos
    		if (!ValidatorUtil.isEmpty(projeto.getArquivos())) {
    			for (ArquivoProjeto aa : projeto.getArquivos()) {
    				aa.setProjeto(projeto);
    				dao.createOrUpdate(aa);
    			}
    		}

    		// Fotos
    		if (!ValidatorUtil.isEmpty(projeto.getFotos())) {
    			for (FotoProjeto af : projeto.getFotos()) {
    				af.setProjeto(projeto);
    				dao.createOrUpdate(af);
    			}
    		}

    		return projeto;
    	} finally {
    		dao.close();
    	}

    }

	/**
     * Responsável pela remoção de dados antigos do cronograma da ação.
     * @param projeto
     * @param mov
     * @throws DAOException
     */
    private void removerCronogramaAntigo(MovimentoCadastro mov) throws DAOException {
    	GenericDAO dao = getGenericDAO(mov);
    	try {
    		Projeto projeto = mov.getObjMovimentado();	    
    		if( projeto.getCronograma() != null || ValidatorUtil.isNotEmpty(projeto.getCronograma())
    				&& projeto.getCronograma().get(0).getId() == 0 ) {

    			Projeto projetoAntigo = dao.findByPrimaryKey(projeto.getId(), Projeto.class);
    			if(projetoAntigo.getCronograma() != null) {
    				projetoAntigo.getCronograma().iterator();
    			}
    			dao.detach(projetoAntigo);

    			// Remover cronograma do projeto
    			for (CronogramaProjeto cronogramaAntigo : projetoAntigo.getCronograma() ) {
    				cronogramaAntigo.setProjeto(null);
    				dao.remove(cronogramaAntigo);
    			}
    			projetoAntigo.setId(0);
    		}
    	} finally {
    		dao.close();
    	}
    }

    public void validate(Movimento mov) throws NegocioException, ArqException {
    	MovimentoCadastro movC = (MovimentoCadastro) mov;
    	ListaMensagens mensagens = new ListaMensagens();
    	Projeto projeto = movC.getObjMovimentado();


    	// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
    	Usuario usuario = (Usuario) mov.getUsuarioLogado();
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			mensagens.addErro("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
		}
    	
    	if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_PROJETO_BASE)) {
    		ProjetoBaseValidator.validaPendenciasProjetoBase(projeto, mensagens);

    	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ORCAMENTO_PROJETO_BASE)) {
    		if (projeto.isProjetoAssociado()) {
    			mov.getUsuarioLogado().isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
    		}
    		
    	} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PROJETO_BASE)) {
    		if (!movC.getUsuarioLogado().isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO)) {
    			ProjetoBaseValidator.validaRemoverProjeto(projeto, mov.getUsuarioLogado().getPessoa(), mensagens);
    		}
    		
    	} else if (mov.getCodMovimento().equals(SigaaListaComando.CONCLUIR_PROJETO_BASE)) {
    		//TODO: verificação de pendências dos professores quando ao envio de relatórios, etc

    	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_BASE)) {
    		if ((projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO)
    				|| (projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO)) {

    			ProjetoDao dao = getDAO(ProjetoDao.class, mov);	
    			try {
    				// @negocio: Validações realizadas somente para projetos que nunca foram executados.
    				if (!dao.isProjetoPassouPorSituacao(projeto.getId(),  TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO)) {
    					ProjetoBaseValidator.validaPendenciasProjetoBase(projeto, mensagens);
    				}
    			}finally {
    				dao.close();
    			}

    		} else if(projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO) {
    			//TODO: verificação de pendências dos professores quando ao envio de relatórios, etc
    		}
    		
    	} else if (mov.getCodMovimento().equals(SigaaListaComando.CONCEDER_RECURSOS_PROJETO)) {
    		mov.getUsuarioLogado().isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
    		
    	} else if (mov.getCodMovimento().equals(SigaaListaComando.EXECUTAR_PROJETO_BASE) || mov.getCodMovimento().equals(SigaaListaComando.NAO_EXECUTAR_PROJETO_BASE) ) {
    		if (!movC.getUsuarioLogado().isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO)) {
    			ProjetoBaseValidator.validaExecucaoProjeto(projeto, mov.getUsuarioLogado().getPessoa(), mensagens);
    		}
    	}

    	checkValidation(mensagens);
    }

    /**
     * Altera a situação do projeto e grava no histórico a mudança
     * 
     * @param dao
     * @param mov
     * @param idSituacaoAtual
     *            situação atual do projeto. Verifica se esta situação já está
     *            gravada no histórico.
     * @param verificarRepetido
     *            se TRUE verifica se já tem a situação no histórico. FALSE
     *            grava novo registro no histórico independe de já existir.
     * 
     * @throws DAOException
     */
    private void alterarHistoricoSituacao(ProjetoDao dao, MovimentoCadastro mov, final int idSituacaoAtual, boolean verificarRepetido) throws DAOException {
	//última situação do histórico do projeto 
	int ultimaSituacao = dao.findLastSituacaoProjeto(mov.getObjMovimentado().getId(), null);

	// verifica se a situação mudou, se mudou grava novo registro no histórico
	if ((ultimaSituacao == 0) || (ultimaSituacao != idSituacaoAtual) || (!verificarRepetido)) {		
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(idSituacaoAtual, ((Projeto) mov.getObjMovimentado()).getId(), 
		    mov.getUsuarioLogado().getRegistroEntrada());
	}
    }

    
    /**
     * Alteração compulsória da situação do projeto.
     * Este procedimento é realizado somente por 
     * membros do comitê integrado de ensino, pesquisa e extensão.
     * 
     * @param mov
     * @return
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private Object alterarSituacaoProjeto(MovimentoCadastro mov) throws NegocioException, ArqException,	RemoteException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    Projeto projeto = (Projeto) mov.getObjMovimentado();		
	    if (ValidatorUtil.isEmpty(projeto)) {
		return null;
	    }else {

		if(projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO){
		    remover(mov);
		    
		}else if(projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO) {
		    executarProjetoBase(mov);
		    
		}else if(projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_CANCELADO) {
		    naoExecutarProjetoBase(mov);
		    
		}else if(projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO) {
		    concluir(mov);
		    
		}else {		
		    dao.update(projeto);
		    ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov.getUsuarioLogado().getRegistroEntrada());
		}
		return projeto;
	    }
	} finally {
	    dao.close();	
	}
    }


    
    /**
     * Alteração compulsória do orçamento concedido ao projeto.
     * Este procedimento é realizado somente por 
     * membros do comitê integrado de ensino, pesquisa e extensão.
     * 
     * @param mov
     * @return
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private Object alterarOrcamento(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    Projeto projeto = mov.getObjMovimentado();
	    if (projeto.getId() != 0) {
        	// Alterando orçamento consolidado
        	if (projeto.getOrcamentoConsolidado() != null) {
        	    for (OrcamentoConsolidado oc : projeto.getOrcamentoConsolidado()) {
        		dao.createOrUpdate(oc);
        	    }
        	}
	    }
	    return projeto;
	} finally {
	    dao.close();	
	}
	
    }
    
    
    /**
     * Permite alteração do orçamento concedido, situação e outros recursos solicitados
     * pela coordenação da ação. 
     *  
     * @return
     * @throws NegocioException
     * @throws ArqException
     * @throws RemoteException
     */
    private Object concederRecursos(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	Projeto projeto = mov.getObjMovimentado();
    	GenericDAO dao = getGenericDAO(mov);
    	try {
    		dao.updateField(Projeto.class, projeto.getId(),"bolsasConcedidas", projeto.getBolsasConcedidas());
    	} finally {
    	    dao.close();	
    	}    		
    	projeto = (Projeto) alterarOrcamento(mov);
    	projeto = (Projeto) alterarSituacaoProjeto(mov);
    	return projeto;
    }
    
}
