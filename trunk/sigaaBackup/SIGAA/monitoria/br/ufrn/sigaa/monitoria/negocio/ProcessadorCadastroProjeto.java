/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio; 

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ReconsideracaoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocenteComponente;
import br.ufrn.sigaa.monitoria.dominio.HistoricoSituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.ProcessadorProjetoBase;

/**
 * Processador usado nas operações de cadastro dos projetos de monitoria. 
 * 
 *
 * @author David Ricardo
 * @author ilueny santos
 *
 */
public class ProcessadorCadastroProjeto extends AbstractProcessador {

	/**
 	 * Executa uma ação de acordo com o parâmetro. 
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
	
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

	    validate(mov);
		ProjetoEnsino pm = null;
		ProjetoMonitoriaMov pMov = (ProjetoMonitoriaMov) mov;

		switch(((ProjetoMonitoriaMov) mov).getAcao()){
			case ProjetoMonitoriaMov.ACAO_GRAVAR_TEMPORARIAMENTE:
				pm = gravarTemporariamente(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_ENVIAR_PROPOSTA_AOS_DEPARTAMENTOS:
				pm = enviarPropostaIsolada(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_ENVIAR_PROPOSTA_ASSOCIADA_AOS_DEPARTAMENTOS:
				pm = enviarPropostaAssociada(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_ALTERAR_COORDENADOR:
			    	processarAlteracaoCoordenador(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_ALTERAR_DOCENTE_PROGRAD:
				pm = alterarDocentesPrograd(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_ALTERAR_COMPONENTES_OBRIGATORIOS:
				pm = alterarComponentesObrigatorios(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_ALTERAR_SITUACAO_PROJETO:
				pm = alterarSituacaoProjeto(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_DESATIVAR_PROJETO:
				pm = desativar(pMov);
				break;
			case ProjetoMonitoriaMov.ACAO_CONCLUIR_PROJETO:
				pm = concluir(pMov);
				break;				
			default:
				throw new NegocioException("Tipo de ação desconhecida!");
		}

		return pm;

	}

	
	/**
	 * Método usado para desativar um projeto base de monitoria.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException 
	 * @throws RemoteException 
	 * @throws ArqException 
	 */
	private ProjetoEnsino desativar(ProjetoMonitoriaMov mov) throws NegocioException, ArqException, RemoteException {
	    ProjetoEnsino pm = mov.getObjMovimentado();
	    ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, mov);
	    try {

		/** @negocio: Inativa as autorizações. */
		Collection<AutorizacaoProjetoMonitoria> autorizacoesNoBanco = new ArrayList<AutorizacaoProjetoMonitoria>();
		autorizacoesNoBanco = dao.findByExactField(AutorizacaoProjetoMonitoria.class,"projetoEnsino.id" , pm.getId());
		for (AutorizacaoProjetoMonitoria autorizacao : autorizacoesNoBanco) {
		    dao.updateField(AutorizacaoProjetoMonitoria.class, autorizacao.getId(), "ativo", Boolean.FALSE);
		}

		/** @negocio: Inativa todos os discentes de monitoria. */
		Collection<DiscenteMonitoria> discentesM = dao.findByExactField(DiscenteMonitoria.class, "projetoEnsino.id", pm.getId());
		for(DiscenteMonitoria dm : discentesM ) {
		    ProcessadorDiscenteMonitoria procDis = new ProcessadorDiscenteMonitoria(); 
		    DiscenteMonitoriaMov movD = new DiscenteMonitoriaMov();
		    movD.setSistema(mov.getSistema());
		    movD.setDiscenteMonitoria(dm);
		    movD.setUsuarioLogado(mov.getUsuarioLogado());
		    movD.setCodMovimento(SigaaListaComando.EXCLUIR_DISCENTEMONITORIA);
		    procDis.execute(movD);	    
		}

		/** @negocio: Inativa todos os docentes. */
		Collection<EquipeDocente> equipesDocentes = dao.findByExactField(EquipeDocente.class, "projetoEnsino.id", pm.getId()); 
		for(EquipeDocente ed : equipesDocentes) {
		    dao.updateField(EquipeDocente.class, ed.getId(), "ativo", Boolean.FALSE);
		}
		
		/** @negocio: Inativa todos os componentes curriculares. */
		Collection<ComponenteCurricularMonitoria> componentes = dao.findByExactField(ComponenteCurricularMonitoria.class, "projetoEnsino.id", pm.getId());
		for (ComponenteCurricularMonitoria ccm : componentes) {
			dao.updateField(ComponenteCurricularMonitoria.class, ccm.getId(), "ativo", Boolean.FALSE);
		}

		/** @negocio: Inativa todas as avaliações. */
		Collection<AvaliacaoMonitoria> avaliacoes = dao.findByExactField(AvaliacaoMonitoria.class, "projetoEnsino.id", pm.getId());
		for (AvaliacaoMonitoria av : avaliacoes) {
			dao.updateField(AvaliacaoMonitoria.class, av.getId(), "ativo", Boolean.FALSE);
		}

		/** @TODO Inativar outros componentes que fazem parte do projeto. */
		
		dao.clearSession();

		pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_REMOVIDO));
		pm.setAtivo(false);
		dao.updateFields(ProjetoEnsino.class, pm.getId(), new String[] {"situacaoProjeto.id","ativo"}, new Object[] {pm.getSituacaoProjeto().getId(), pm.isAtivo()});
		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

	    }finally{
		dao.close();
	    }

	    return null;
	}
	
	
	/**
	 * Distribui o projeto de apoio e melhoria da qualidade do ensino
	 * para o chefe do departamento do coordenador do projeto.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private ProjetoEnsino distribuirDepartamentoCoordenador(ProjetoMonitoriaMov mov) throws NegocioException, ArqException, RemoteException {
	    ReconsideracaoDao dao = getDAO(ReconsideracaoDao.class, mov);
	    try {
		    ProjetoEnsino pm = mov.getObjMovimentado();
		    /**
		     * @negocio: Distribuições já realizadas anteriormente devem ser recuperadas do banco de dados evitando
		     * 		 duplicidade de solicitação de autorização em um departamento (utilizada em casos de alteração do projeto).
		     */
		    Collection<AutorizacaoProjetoMonitoria> autorizacoesNoBanco = new ArrayList<AutorizacaoProjetoMonitoria>();
		    autorizacoesNoBanco = dao.findByExactField(AutorizacaoProjetoMonitoria.class,"projetoEnsino.id" , pm.getId());
		    Set<Unidade> unidadesParticipantes = new HashSet<Unidade>();
		    for (AutorizacaoProjetoMonitoria autorizacao : autorizacoesNoBanco) {
			if (autorizacao.isAtivo()) {
			    unidadesParticipantes.add(autorizacao.getUnidade());
			}
		    }

		    MembroProjeto coordenador = pm.getProjeto().getCoordenador();
		    if (coordenador != null) {
    		    	if (unidadesParticipantes.add(coordenador.getServidor().getUnidade())) {
    			    AutorizacaoProjetoMonitoria auto = new AutorizacaoProjetoMonitoria();
    			    /** @negocio: A autorização é solicitada a unidade do coordenador. */
    			    auto.setUnidade(coordenador.getServidor().getUnidade());
    			    auto.setProjetoEnsino(pm);
    			    auto.setTipoAutorizacao(null);
    			    auto.setAtivo(true);
    			    dao.create(auto);
    			    dao.updateField(ProjetoEnsino.class, pm.getId(), "dataEnvio", new Date());
    			}
		    }
		    return pm;
	    }finally {
		dao.close();
	    }
	}
	
	
	
	
	/**
	 * Método utilizado para criar/recriar autorizações
	 * de projetos de monitoria enviados aos departamentos.
	 * 
	 * Distribui o projeto para os departamentos de todos os componentes curriculares
	 * envolvidos no projeto. 
	 * 
	 * @param projeto
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private ProjetoEnsino distribuirDepartamentosComponentesCurriculares(ProjetoMonitoriaMov mov) throws NegocioException, ArqException, RemoteException {
		ProjetoDao dao = getDAO(ProjetoDao.class, mov);
		try {

			/**
			 * Criando as autorizações dos componentes curriculares envolvidos para os chefes das unidades participantes autorizarem.
			 * 		    
			 * @negocio: Projetos submetidos pela primeira vez (não solicitaram reconsideração) devem considerar os departamentos 
			 * 	     já distribuídos e só complementar com novos departamentos (caso o projeto tenha incluido um novo componente).  
			 */

			ProjetoEnsino pm = mov.getObjMovimentado();
			//Recuperando as distribuições já realizadas anteriormente (para os casos de alteração do projeto)
			Collection<AutorizacaoProjetoMonitoria> autorizacoesNoBanco = 
					dao.findByExactField(AutorizacaoProjetoMonitoria.class, 
							new String[] { "projetoEnsino.id", "ativo" }, new Object[] { pm.getId(), true });

			//Criando novas autorizações
			for (ComponenteCurricularMonitoria comp : pm.getComponentesCurriculares()) {
				comp.setDisciplina(dao.findByPrimaryKey(comp.getDisciplina().getId(), ComponenteCurricular.class));
				if ( !autorizacoesNoBanco.contains( comp.getDisciplina().getUnidade() ) ){
					AutorizacaoProjetoMonitoria auto = new AutorizacaoProjetoMonitoria();
					/** @negocio:  Autorização solicitada a unidade do componente curricular do projeto.*/
					auto.setUnidade(comp.getDisciplina().getUnidade());
					auto.setProjetoEnsino(pm);
					auto.setTipoAutorizacao(null);
					auto.setAtivo(true);
					dao.create(auto);
				}
			}

			dao.updateField(ProjetoEnsino.class, pm.getId(), "dataEnvio", new Date());
			return pm;

		}finally {
			dao.close();
		}
	}
	
	/**
	 * Envia a proposta de projeto para o departamento ou para a 
	 * Pró-reitoria de graduação.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private ProjetoEnsino enviarPropostaIsolada(ProjetoMonitoriaMov mov) throws NegocioException, ArqException, RemoteException {
	    ProjetoEnsino pm = mov.getObjMovimentado();
	    /** 
	     * @negocio: Projetos isolados são distribuídos diretamente para os departamentos.
	     * @negocio: Projetos associados são distribuídos para os departamentos SOMENTE quando a proposta é cadastradas COMPLETAMENTE, sem pendências.
	     */
	    if (pm.isProjetoAssociado()) {
		throw new NegocioException("Este não é um Projeto de Monitoria isolado.");
	    }

	    ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, mov);    
	    try {    
		// Situação Padrão
		pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS));
		pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS));
		// Cria ou altera um projeto
		if (pm.getId() == 0) {
		    pm = criar(mov);
		}else {
		    pm = alterar(mov);
		}

		/** @negocio: Solicitações realizadas pela Pró-Reitoria de Graduação NÃO devem ser distribuídas para os departamentos */
		if (!mov.isSolicitacaoPrograd()){
		    if (pm.isProjetoMonitoria()) {
			pm = distribuirDepartamentosComponentesCurriculares(mov);        
		    }else if (pm.isProjetoPAMQEG() || pm.isAmbosProjetos()) {
			pm = distribuirDepartamentoCoordenador(mov);
		    }		    
		}

		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());        
		return pm;

	    }finally {
		dao.close();
	    }
	}

	
	/**
	 * Envia proposta associada para os departamentos envolvidos.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private ProjetoEnsino enviarPropostaAssociada(ProjetoMonitoriaMov mov) throws NegocioException, ArqException, RemoteException {
	    ProjetoEnsino pm = mov.getObjMovimentado();	    
	    if (pm.isProjetoIsolado()) {
		throw new NegocioException("Esta não é uma Ação Acadêmica Associada.");
	    }
	    if (pm.isProjetoMonitoria()) {
		pm = distribuirDepartamentosComponentesCurriculares(mov);		    
	    }else if (pm.isProjetoPAMQEG() || pm.isAmbosProjetos()) {
		pm = distribuirDepartamentoCoordenador(mov);
	    }
	    return pm;
	}

	
	
	/**
	 * Cria o projeto de monitoria temporariamente.
	 * Permite a conclusão do projeto depois.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private ProjetoEnsino gravarTemporariamente(ProjetoMonitoriaMov mov) throws NegocioException, ArqException, RemoteException {
	    GenericDAO dao = getGenericDAO(mov);
	    try {

		ProjetoEnsino pm = mov.getObjMovimentado();
		
		/** @negocio: Projetos em execução e alterados por gestores NÃO devem ter sua situação alterada para cadastro em andamento. */
		if ((pm.getSituacaoProjeto() != null) 
			&& (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)
				&& (pm.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_EM_EXECUCAO)
				&& (pm.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_RECOMENDADO))){
		    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO));
		    pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO));
		}		    

		/** @negocio: Situação padrão de criação de projeto novo de monitoria. */
		if (ValidatorUtil.isEmpty(pm.getSituacaoProjeto())){
		    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO));
		}
		if (ValidatorUtil.isEmpty(pm.getProjeto().getSituacaoProjeto())){
		    pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO));
		}

		/** 
		 * @negocio: Para gravar temporiamente, caso ainda não se tenha definido uma unidade, 
		 * a unidade do projeto será igual a unidade de lotação do usuário que está cadastrando o projeto.
		 * A regra de negócio define que a unidade do projeto é a unidade de lotação do coordenador.
		 */
		
		Projeto p = pm.getProjeto();
		if (ValidatorUtil.isEmpty(p.getUnidade())) {
			if ( ValidatorUtil.isNotEmpty(p.getCoordenador()) ) {
				p.setUnidade(p.getCoordenador().getUnidade());
			}else{
				p.setUnidade((Unidade) mov.getUsuarioLogado().getUnidade());
			}
		}
		
		if (pm.getId() == 0) {
		    pm = criar(mov);
		}else {
		    pm = alterar(mov);
		}

		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
		return pm;

	    }finally {
		dao.close();
	    }
	}
	
	/**
 	 * Cria um novo projeto de monitoria. 
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
	private ProjetoEnsino criar(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
	    
	    /** @regra: Somente docentes podem solicitar cadastros de Projetos de Apoio a Melhoria da Qualidade do Ensino. */
	    ProjetoEnsino pm = mov.getObjMovimentado();
	    if (pm.isProjetoPAMQEG()) {
		checkRole(new int[]{SigaaPapeis.DOCENTE}, mov);
	    }
	    
	    ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	    try {	    	

		persistirProjetoBase(mov);
		dao.create(pm);			
		
		for (ComponenteCurricularMonitoria comp : pm.getComponentesCurriculares()) {
		    dao.create(comp);
		    for (EquipeDocenteComponente edc : comp.getDocentesComponentes()) {
			dao.create(edc.getEquipeDocente());
			dao.create(edc);
		    }
		}
		return pm;
	    } finally {
		dao.close();
	    }
	}
	
	/**
	 * Persiste informações sobre o projeto base.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void persistirProjetoBase(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
	    ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	    try {

		ProjetoEnsino pm = mov.getObjMovimentado();
		Projeto projeto = pm.getProjeto();

		// Criando ou atualizando o projeto base
		if (ValidatorUtil.isEmpty(pm.getEditalMonitoria())) {
		    pm.setEditalMonitoria(null);

		    //Ações Com financiamento e isoladas possuem o edital principal = edital da ação.
		} else if (projeto.isProjetoIsolado()) {
		    dao.initialize(pm.getEditalMonitoria());
		    projeto.setEdital(pm.getEditalMonitoria().getEdital());
		}

		// Salvando projeto Base...
		ProcessadorProjetoBase procBase = new ProcessadorProjetoBase(); 
		MovimentoCadastro movP = new MovimentoCadastro();
		movP.setSistema(mov.getSistema());
		movP.setObjMovimentado(projeto);
		movP.setUsuarioLogado(mov.getUsuarioLogado());
		movP.setCodMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
		procBase.execute(movP);	    

	    }finally {
		dao.close();
	    }		
	}
	


	/**
 	 * Processa alterações em um projeto de monitoria. 
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
 	private ProjetoEnsino alterar(ProjetoMonitoriaMov mov) throws NegocioException, ArqException,	RemoteException {

		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class, mov);
		ProjetoEnsino pm = mov.getObjMovimentado();

		try {
		    persistirProjetoBase(mov);
			dao.update(pm);			
			
			// --->ComponenteCurricularMonitoria
			//	----> EquipeDocenteComponente
			//          ---->EquipeDocente


			//Removendo componentes
			if (ValidatorUtil.isNotEmpty(mov.getComponentesCurricularesRemovidos())){
				//Removendo o componente
				for (ComponenteCurricularMonitoria comp : mov.getComponentesCurricularesRemovidos()) {
					for (EquipeDocenteComponente edc : comp.getDocentesComponentes()) {
						dao.updateField(EquipeDocenteComponente.class, edc.getId(), "ativo", false);
					}
					dao.updateField(ComponenteCurricularMonitoria.class, comp.getId(), "ativo", false);
				}
			}


			// Verifica se tem docentes para remover do projeto
			if (ValidatorUtil.isNotEmpty(mov.getEquipesDocenteComponenteRemovidos())){
				//Removendo o docente do componente
				for (EquipeDocenteComponente edc : mov.getEquipesDocenteComponenteRemovidos()) {
					dao.updateField(EquipeDocenteComponente.class, edc.getId(), "ativo", false);  //Aqui, teoricamente, os docente deste componente ficarão orfãos
				}
			}
			
			// Todo docente deve, obrigatoriamente, estar associado a um componente curricular no projeto.
			// Removendo docentes órfão (sem componentes) do projeto de monitoria. 
			Collection<EquipeDocente> docentesSemComponentes = dao.findByEquipeDocenteSemComponentes(pm.getId());
			if (ValidatorUtil.isNotEmpty(docentesSemComponentes)){
				for (EquipeDocente ed : docentesSemComponentes) {
					dao.updateField(EquipeDocente.class, ed.getId(), "ativo", false);
				}
			}

			
			//Criando novos e atualizando componentes curriculares
			for (ComponenteCurricularMonitoria comp : pm.getComponentesCurriculares()) {
				dao.createOrUpdate(comp);
				
				for (EquipeDocenteComponente edc : comp.getDocentesComponentes()) {
					if (edc.getId() != 0) {						
						dao.update(edc);
					}else {
						if (edc.getEquipeDocente().getId() == 0 ) {
						    dao.create(edc.getEquipeDocente());
						}
						dao.create(edc);
					}
				}
			}
			
			for (EquipeDocente eqp : pm.getEquipeDocentes()) {
			    dao.createOrUpdate(eqp);
			}
			
			processarAlteracaoCoordenador(mov);

		}finally {
			dao.close();
		}

		return pm;

	}


 	
 	/**
 	 * Altera os docentes de um projeto de monitoria. Somente a Prograd pode realizar esta operação.  
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
 	private ProjetoEnsino alterarDocentesPrograd(ProjetoMonitoriaMov mov) throws NegocioException, ArqException,	RemoteException {
 	    EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class, mov);
 	    try {

 		ProjetoEnsino pm = mov.getObjMovimentado();
 		//removendo componentes
 		if (!ValidatorUtil.isEmpty(mov.getComponentesCurricularesRemovidos())){
 		    //removendo o componente
 		    for (ComponenteCurricularMonitoria comp : mov.getComponentesCurricularesRemovidos()) {
 			for (EquipeDocenteComponente edc : comp.getDocentesComponentes()) {
 			    dao.remove(edc);
 			}
 			dao.remove(comp);
 		    }
 		}

 		// verifica se tem docentes para remover do projeto
 		if (!ValidatorUtil.isEmpty(mov.getEquipesDocenteComponenteRemovidos())){
 		    //removendo o docente do componente
 		    for (EquipeDocenteComponente edc : mov.getEquipesDocenteComponenteRemovidos()) {
 			dao.remove(edc);  //aqui, teoricamente, os docente deste componente ficarão orfãos.
 		    }
 		}

 		//criando novos e atualizando componentes curriculares
 		for (ComponenteCurricularMonitoria comp : pm.getComponentesCurriculares()) {
 		    dao.createOrUpdate(comp);
 		    for (EquipeDocenteComponente edc : comp.getDocentesComponentes()) {
 			if (edc.getId() != 0) {
 			    dao.update(edc);
 			} else {
 			    if (edc.getEquipeDocente().getId() == 0) {
 				dao.create(edc.getEquipeDocente());
 			    }
 			    dao.create(edc);
 			}
 		    }
 		}
 		return pm;
 	    }finally {
 		dao.close();
 	    }
	}

 	
 	/**
 	 * 
 	 * Faz alterações somente nos componentes curriculares do projeto de monitoria.
 	 * Usado quando a prograd vai retira obrigatoriedade de disciplinas do projeto 
 	 * para prova de seleção de monitores.
 	 * 
 	 * @param mov
 	 * @return
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
 	private ProjetoEnsino alterarComponentesObrigatorios(ProjetoMonitoriaMov mov) throws NegocioException, ArqException,	RemoteException {
 	    EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class, mov);
 	    try {
 		ProjetoEnsino pm = mov.getObjMovimentado();
 		//atualizando componentes curriculares
 		for (ComponenteCurricularMonitoria comp : pm.getComponentesCurriculares()) {
 		    dao.update(comp);
 		}
 		return pm;
 	    }finally {
 		dao.close();
 	    }
	}


 	/**
 	 * Faz alteração somente no status do projeto de monitoria.
 	 * Operação realizada exclusivamente pela prograd.
 	 *  
 	 * 
 	 * @param mov
 	 * @return
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
 	private ProjetoEnsino alterarSituacaoProjeto(ProjetoMonitoriaMov mov) throws NegocioException, ArqException,	RemoteException {
 	    EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class, mov);
 	    try {
 		ProjetoEnsino pm = mov.getObjMovimentado();
         	if (!ValidatorUtil.isEmpty(pm)){		    	
         	    if ((pm.getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_REMOVIDO) ||
         	    		(pm.getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_CANCELADO) ){         		
         	    	desativar(mov);
         	    	
         	    }else if(pm.getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_CONCLUIDO){
         	    	concluir(mov);                 	
         	    	
         	    }else {
    				dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", pm.getSituacaoProjeto().getId());
         	    	ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);         	    
         	    	ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());         	
         	    }
         	}
         	return pm; 	    
 	    }finally { 		
 	    	dao.close(); 	    
 	    }	
 	}

 	
 	
 	/**
 	 * Validações relativas a alterações dos projetos de monitoria.
 	 * 
 	 * @see br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaValidator
 	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {		
		ProjetoEnsino pm = (ProjetoEnsino) ((ProjetoMonitoriaMov)mov).getObjMovimentado();
		ListaMensagens lista = new ListaMensagens();
		
		switch( ((ProjetoMonitoriaMov)mov).getAcao() ){
		case ProjetoMonitoriaMov.ACAO_ENVIAR_PROPOSTA_AOS_DEPARTAMENTOS:
		    if (pm.isProjetoMonitoria()) {
        		    /** @negocio: Só envia para o departamento se estiver validado */
        		    CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
        		    if ((cal != null) && (cal.getId() > 0)) {		
        			ProjetoMonitoriaValidator.validaComponentesCurriculares(pm, pm.getComponentesCurriculares(), cal.getAno(), cal.getPeriodo(), lista);
        		    }					
        		    ProjetoMonitoriaValidator.validaComponentesCurricularesSemDocentes(pm, lista);
		    }
		    ProjetoMonitoriaValidator.validaDadosProjeto(pm, lista);
		    ProjetoMonitoriaValidator.validaCoordenador(pm, pm.getCoordenacao(), lista);
		    
		    break;
		case ProjetoMonitoriaMov.ACAO_GRAVAR_TEMPORARIAMENTE:
		    break;		    
		case MovimentoCadastro.ACAO_REMOVER:
		    ProjetoMonitoriaValidator.validaRemoverProjeto(pm, lista);
		    break;
		case ProjetoMonitoriaMov.ACAO_DESATIVAR_PROJETO:
		    ProjetoMonitoriaValidator.validaRemoverProjeto(pm, lista);
		    break;		    
		case ProjetoMonitoriaMov.ACAO_ALTERAR_COORDENADOR:
		    Servidor novoCoord = new Servidor(((ProjetoMonitoriaMov)mov).getIdNovoCoordenador());
		    ProjetoMonitoriaValidator.validaCoordenador(pm, novoCoord, lista);
		    break;
		case ProjetoMonitoriaMov.ACAO_ALTERAR_DOCENTE_PROGRAD:
		    checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
		    break;
		case ProjetoMonitoriaMov.ACAO_ALTERAR_COMPONENTES_OBRIGATORIOS:
		    break;
		case ProjetoMonitoriaMov.ACAO_ALTERAR_SITUACAO_PROJETO:
		    checkRole(new int[] {SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO}, mov);
		    if(pm.getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_REMOVIDO){
			ProjetoMonitoriaValidator.validaRemoverProjeto(pm, lista);
		    }
		    break;
		}
		
		checkValidation(lista);

	}
 	
 	/**
 	 * Finaliza todos participantes do projeto com suas devidas orientações e discentes.
 	 * Método utilizado quando o projeto é concluído, cancelado ou não recomendado. 
 	 * 
 	 * @param mov
 	 * @param dao
 	 * @throws DAOException
 	 */
	private ProjetoEnsino concluir(ProjetoMonitoriaMov mov) throws DAOException{
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, mov);
		try {
			ProjetoEnsino pm = mov.getObjMovimentado();		
			/** @negocio: O encerramento do projeto deve finalizar todos os docentes envolvidos.*/
			pm.setEquipeDocentes(dao.findByExactField(EquipeDocente.class,  "projetoEnsino.id" , pm.getId()));
			for (EquipeDocente dc : pm.getEquipeDocentes()) {
				if (dc.isAtivo()){

					/** @negocio: Se o docente sair antes do fim considera a data de saída do docente como a data atual.
					 * 		  Senão, a data de saída é igual a data de finalização do projeto. */
					if (new Date().before(pm.getProjeto().getDataFim())) {
						dc.setDataSaidaProjeto(new Date());
					}else {
						dc.setDataSaidaProjeto(pm.getProjeto().getDataFim());
					}

					/** @negocio: Caso o docente seja coordenador a coordenação deve ser finalizada também. */
					if (dc.isCoordenador()){				
						dc.setDataFimCoordenador(dc.getDataSaidaProjeto());
						if(dc.getDataInicioCoordenador() == null) {
							dc.setDataInicioCoordenador(dc.getDataEntradaProjeto());
						}
					}

					/** @negocio: O encerramento do projeto deve finalizar todas as orientações do docente. */
					for (Orientacao o: dc.getOrientacoes()){
						if (!o.isFinalizada()){
							dao.updateFields(Orientacao.class, o.getId(), 
									new String[] {"dataFim","registroEntradaFinalizacao.id"}, 
									new Object[] {new Date(), mov.getUsuarioLogado().getRegistroEntrada().getId()});
						}						
					}
					dao.update(dc);
				}			
			}

			/** @negocio: O encerramento do projeto deve finalizar todos os discentes ativos. */
			Collection<DiscenteMonitoria> discentesAtivos = dao.findAtivosByProjeto(pm.getId());
			for (DiscenteMonitoria dsc : discentesAtivos) {
				dao.updateFields(DiscenteMonitoria.class, dsc.getId(), 
						new String[] {"dataFim","situacaoDiscenteMonitoria.id"}, 
						new Object[] {new Date(), SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA});

				HistoricoSituacaoDiscenteMonitoria historico = new HistoricoSituacaoDiscenteMonitoria();
				historico.setData(new Date());
				historico.setDiscenteMonitoria(dsc);
				historico.setSituacaoDiscenteMonitoria(new SituacaoDiscenteMonitoria(SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
				historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				historico.setTipoVinculo(dsc.getTipoVinculo());
				dao.create(historico);

				/** @TODO implementar: se o discente for bolsista deve ser solicitada a finalização da bolsa no SIPAC. */
			}

			pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CONCLUIDO));
			pm.setAtivo(true);
			
			dao.updateFields(ProjetoEnsino.class, pm.getId(), 
					new String[] {"situacaoProjeto.id","ativo"}, new Object[] {pm.getSituacaoProjeto().getId(), pm.isAtivo()});
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);         	    
			ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
			return pm;
	    }finally {
	    	dao.close();
	    } 	    	
	}
	
	
	/**
 	 *  Processa a alteração do coordenador de um projeto de monitoria.
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 * 
 	 */
	private void processarAlteracaoCoordenador(ProjetoMonitoriaMov mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			ProjetoEnsino p = (ProjetoEnsino) mov.getObjMovimentado();
			Integer idServidor = mov.getIdNovoCoordenador();

			if ((idServidor != null) && (idServidor > 0)) {
				ArrayList<EquipeDocente> docentesDoProjeto = new ArrayList<EquipeDocente>(0);

				//verifica os docentes do componentes curriculares....no caso de vir cadastro de projeto
				for (ComponenteCurricularMonitoria ccm : p.getComponentesCurriculares()) {
					for(EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
						if (!docentesDoProjeto.contains(edc.getEquipeDocente()))
							docentesDoProjeto.add(edc.getEquipeDocente());
					}
				}

				//verifica os docentes direto do projeto....no caso de querer somente alterar o coordenador
				//pode querer incluir um coordenador que não está associado a nenhum componente curricular....
				for(EquipeDocente ed : p.getEquipeDocentes()) {
					if (!docentesDoProjeto.contains(ed)) {
						docentesDoProjeto.add(ed);
					}
				}

				//percorre lista de docentes do projeto....
				for (EquipeDocente ed : docentesDoProjeto) {
					
					//achou o servidor que será o novo coordenador.
					if (ed.getServidor().getId() == idServidor) {
						
						//**********Atualizando o coordenador em monitoria*********
						if (!ed.isCoordenador()) {
							
							//finalizando coordenação atual
							EquipeDocente coordenadorAtual = p.getCoordenacaoEquipeDocente();
							if (ValidatorUtil.isNotEmpty(coordenadorAtual)) {
								dao.updateFields(EquipeDocente.class, coordenadorAtual.getId(), new String[] {"dataFimCoordenador", "coordenador"}, new Object[] {new Date(), false});
							}

							//definindo nova coordenação
							dao.updateFields(EquipeDocente.class, ed.getId(), 
									new String[] {"dataInicioCoordenador", "dataFimCoordenador", "coordenador"}, 
									new Object[] {new Date(), ed.getProjetoEnsino().getProjeto().getDataFim(),  true});
						}
												
						//**********Atualizando o coordenador em projetos *********						
						if (!p.isProjetoAssociado()) {
							dao.initialize(ed.getProjetoEnsino().getProjeto());
							MembroProjeto coordOld = ed.getProjetoEnsino().getProjeto().getCoordenador();
							
							//finalizando coordenação atual
							if (ValidatorUtil.isNotEmpty(coordOld) && coordOld.getServidor().getId() != idServidor) {
								dao.updateField(MembroProjeto.class, coordOld.getId(), "dataFim", new Date());
							}

							if (ValidatorUtil.isEmpty(coordOld) || coordOld.getServidor().getId() != idServidor) {
								MembroProjeto coordNew = new MembroProjeto();
								coordNew.setPessoa(ed.getServidor().getPessoa());
								coordNew.setServidor(ed.getServidor());
								coordNew.getServidor().setFormacao(null);
								coordNew.getPessoa().setFormacao(null);
								coordNew.setDiscente(null);
								coordNew.setDocenteExterno(null);
								coordNew.setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COORDENADOR));
								coordNew.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DOCENTE));
								coordNew.setDataInicio(new Date());
								coordNew.setDataFim(ed.getProjetoEnsino().getProjeto().getDataFim());
								coordNew.setAtivo(true);					    
								coordNew.setProjeto(ed.getProjetoEnsino().getProjeto());
								dao.create(coordNew);
								dao.updateField(Projeto.class, coordNew.getProjeto().getId(), "coordenador.id", coordNew.getId());
							}
						}

						
						
						break;
					}
				}
			}
		}finally {
			dao.close();
		}

	}
	
}