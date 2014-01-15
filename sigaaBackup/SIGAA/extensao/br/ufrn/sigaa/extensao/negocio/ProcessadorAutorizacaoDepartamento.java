package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.AutorizacaoDepartamentoDao;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**	
 * Processador usado nas opera��es de autoriza��o da atividade 
 * pelos chefes dos departamentos.
 * 
 * @author Ilueny Santos
 * @param <T>
 *
 */
public class ProcessadorAutorizacaoDepartamento extends AbstractProcessador {


	/**
	 * Executa determinada a~�ao de acordo com a op��o setada no MBean.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		AutorizacaoDepartamento auto = null;

		if ( mov.getCodMovimento().equals(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO) ) {
			switch( ( (MovimentoCadastro)mov ).getAcao() ){
			case MovimentoCadastro.ACAO_CRIAR:
				auto = (AutorizacaoDepartamento)criar(mov);
				break;
			case MovimentoCadastro.ACAO_ALTERAR:
				auto = (AutorizacaoDepartamento) alterar(mov);
				break;
			case MovimentoCadastro.ACAO_REMOVER:
				remover(mov);
				break;
			default:
				throw new NegocioException("Tipo de a��o desconhecida.");
			}
			
		}else if ( mov.getCodMovimento().equals(SigaaListaComando.REEDITAR_ATIVIDADE_EXTENSAO) ) {
			reeditarAtividadeExtensao(mov);
			
		} else if ( mov.getCodMovimento().equals(SigaaListaComando.REEDITAR_PROJETO_MONITORIA) ) {
			reeditarProjetoMonitoria(mov);
			
		} else if ( mov.getCodMovimento().equals(SigaaListaComando.INATIVAR_AUTORIZACOES_DEPARTAMENTOS) ) {
			inativarAutorizacoesDepartamentos(mov);
			
		}


		return auto;
	}


	/**
	 * M�todo respons�vel por criar uma solicita��o de autoriza��o da a��o.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object criar(Movimento mov) throws NegocioException, ArqException, RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		try {
		    MovimentoCadastro cMov = (MovimentoCadastro) mov;		
		    AutorizacaoDepartamento auto = (AutorizacaoDepartamento) cMov.getObjMovimentado();
		    auto.setAtivo(true);
		    dao.create(auto);
		    return auto;
		} finally {
			dao.close();
		}
	}

	/**
	 * Altera uma autoriza��o de a��o de extens�o.
	 * Este m�todo � utilizado principalmente durante a autoriza��o das
	 * a��es de extens�o pelos departamentos.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object alterar(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		AutorizacaoDepartamentoDao dao = getDAO(AutorizacaoDepartamentoDao.class, mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;

		// Departamentos autorizam ou negam a realiza��o da a��o de extens�o.
		AutorizacaoDepartamento auto = (AutorizacaoDepartamento) cMov.getObjMovimentado();
		dao.update(auto);

		/** @negocio: Quando n�o h� mais autoriza��es pendentes a situa��o da a��o de extens�o deve ser alterada. */
		if (dao.totalAutorizacoesPendentes(auto.getAtividade().getId()) == 0){
			AtividadeExtensao  atividade = dao.findByPrimaryKey(auto.getAtividade().getId(), AtividadeExtensao.class);

			/** 
			 * @negocio: 
			 * 	Quando todos os departamentos aprovam a execu��o da a��o sua situa��o passa a ser submetido
			 * 	e fica aguardando avalia��o pela PROEX (comit�).
			 */   
			if (dao.totalReprovacoesPelosDepatamentos(auto.getAtividade().getId()) == 0) { //N�o houve reprova��es
				atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_SUBMETIDO));
				
			/** @negocio: 
			 * 	Algum departamento envolvido n�o autorizou o envio da a��o. 
			 *  	O coordenador da a��o pode recorrer solicitando uma reconsidera��o da avalia��o da a��o
			 *  	no per�odo definido pela Pr�-Reitoria de Extens�o. 
			 */ 
			}else{
				atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_NAO_AUTORIZADO_DEPARTAMENTOS));
			}

			dao.update(atividade);
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
			ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
		}
		return auto;
	}

	/**
	 * Remove uma solicita��o de autoriza��o feita a um departamento.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object remover(Movimento mov) throws NegocioException, ArqException,	RemoteException {
	    checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);
	    GenericDAO dao = getGenericDAO(mov);
	    try {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		AutorizacaoDepartamento auto = (AutorizacaoDepartamento) cMov.getObjMovimentado();
		auto.setAtivo(false);
		dao.update(auto);
		return auto;
	    } finally {
		dao.close();
	    }
	}

	/**
	 * Devolve a Atividade de EXTENS�O ao coordenador do projeto para reedi��o.
	 * Este procedimento ocorre geralmente quando o coordenador da a��o comete 
	 * algum erro durante o cadastro da proposta. 
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object reeditarAtividadeExtensao(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, 
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO, SigaaPapeis.CHEFE_DEPARTAMENTO, 
				SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE}, mov);		

		AutorizacaoDepartamentoDao dao = getDAO(AutorizacaoDepartamentoDao.class, mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		try {
        		AtividadeExtensao atividade = cMov.getObjMovimentado();		
        		Collection<AutorizacaoDepartamento> autorizacoes = dao.findByAtividade(atividade.getId());
        
        		/** @negocio: Inativa todas as autoriza��es e grava o registro de entrada do operador que devolveu a proposta */
        		for (AutorizacaoDepartamento auto : autorizacoes) {
    		    	dao.updateFields(AutorizacaoDepartamento.class, auto.getId(), 
    		    			new String[] {"ativo","registroEntradaDevolucao.id"}, 
    		    			new Object[] {false, mov.getUsuarioLogado().getRegistroEntrada().getId()});
        		}
        
        		/** @negocio: Registrando que a a��o foi devolvida para o coordenador, mas a situa��o final da a��o n�o deve ser esta. */
        		atividade = dao.findByPrimaryKey(atividade.getId(), AtividadeExtensao.class);
        		dao.refresh(atividade.getProjeto().getCoordenador());
        		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
        
        		/** @negocio: A��o de extens�o devolvida para o coordenador deve ficar como cadastro em andamento. */
        		atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));
        		dao.update(atividade);
        		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
        		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
        
        		return atividade;
		} finally {
		    dao.close();
		}
	}

	/**
	 * Devolve Projeto de Ensino de MONITORIA para o coordenador do projeto pra reedi��o.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object reeditarProjetoMonitoria(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);

		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		try {
		    ProjetoEnsino pm = cMov.getObjMovimentado();		
		    Collection<AutorizacaoProjetoMonitoria> autorizacoes = dao.findByProjetoEnsino(pm.getId());

		    /** @negocio: Inativa todas as autoriza��es e grava o registro de entrada do operador que devolveu a proposta */
		    for (AutorizacaoProjetoMonitoria auto : autorizacoes) {
		    	dao.updateFields(AutorizacaoProjetoMonitoria.class, auto.getId(), 
		    			new String[] {"ativo","registroEntradaDevolucao.id"}, 
		    			new Object[] {false, mov.getUsuarioLogado().getRegistroEntrada().getId()});
		    }

		    /** @negocio: Registrando que o projeto foi devolvido para o coordenador, mas a situa��o final do projeto N�O deve ser esta. */
		    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MONITORIA_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR, pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
		    
		    /** @negocio: Projeto devolvido para o coordenador deve ficar como cadastro em andamento. */
		    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO));
		    dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO);
		    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
		    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO, pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

		    return pm;
		} finally {
		    dao.close();
		}
	}

	
	
    /**
     * Inativa solicita��es de autoriza��o desnecess�rias aos departamentos.
     * 
     * Verifica se ainda existem membros no projeto para o departamento que foi solicitada a autoriza��o
     * e caso n�o haja mais membros do departamento na equipe, inativa a autoriza��o solicitada
     * ao departamento.
     * 
     * @throws DAOException 
     * 
     */
    private void inativarAutorizacoesDepartamentos(Movimento mov) throws DAOException {
    	HistoricoSituacaoProjetoDao dao = getDAO(HistoricoSituacaoProjetoDao.class, mov);
    	Projeto acao = ((MovimentoCadastro)mov).getObjMovimentado();

    	try {
    		
    		/** @negocio: Removendo autoriza��es de unidades que n�o possuem mais membros na equipe do projeto.
    		 *  Caso o projeto j� tenha entrado em execu��o as autoriza��es dos departamentos n�o devem ser removidas. 
    		 */
    		boolean projetoAprovado = dao.isSituacaoPresenteNoHistoricoProjeto(acao.getId(), TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO);
    		if (!projetoAprovado) {
    			
    			// Lista todas as unidades representadas na equipe do projeto.
    			Set<Unidade> unidadesNaEquipe = new HashSet<Unidade>();
    			for (MembroProjeto mp : acao.getEquipe()) {
    				if (mp.isAtivo() && (mp.getServidor() != null)	&& (mp.getServidor().getUnidade() != null)) {
    					unidadesNaEquipe.add(mp.getServidor().getUnidade());
    				}
    			}

    			// Verifica se todas as autoriza��es atuais possuem representantes na equipe.
    			// Caso n�o tenha inativa a autoriza��o.
    			acao.setAutorizacoesDepartamentos(dao.findAtivosByExactField(AutorizacaoDepartamento.class, "projeto.id", acao.getId()));
    			for (AutorizacaoDepartamento ad : acao.getAutorizacoesDepartamentos()) {
    				if (!unidadesNaEquipe.contains(ad.getUnidade()) && (ad.getId() > 0)) {
    					dao.updateField(AutorizacaoDepartamento.class, ad.getId(), "ativo", false);
    				}
    			}    			
    		}
    		
   		}finally {
   			dao.close();
   		}
    }

	
	
	
	/**
	 * Valida uma autoriza��o do departamento.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

    	Usuario usuario = (Usuario) mov.getUsuarioLogado();

    	// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
		}

		
		if ( mov.getCodMovimento().equals(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO) ) {
			//
		}else if ( mov.getCodMovimento().equals(SigaaListaComando.REEDITAR_ATIVIDADE_EXTENSAO) ) {
			checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, 
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO, SigaaPapeis.CHEFE_DEPARTAMENTO, 
				SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE}, mov);		

			MovimentoCadastro cMov = (MovimentoCadastro) mov;			
			AtividadeExtensao atividade = cMov.getObjMovimentado();		

			/**
			 * @negocio: A situa��o de a��es de extens�o s� deve ser alterada se o projeto for isolado.
			 * @negocio: A��es Associadas devem ter sua situa��o controlada atrav�s da edi��o do Projeto Base.
			 * @author Ilueny Santos
			 */ 
			if (atividade.isProjetoAssociado()) {
				throw new NegocioException("Esta � uma A��o de Extens�o Associada e sua gest�o n�o pode ser realizada atrav�s desta opera��o.");
			}

			/**
			 * @negocio: Somente nos casos onde a devolu��o da proposta estiver sendo realizada por um chefe de departamento.
			 * @negocio: Membros da Pr�-reitoria de extens�o podem devolver a proposta a qualquer momento para o coordenador. 
			 */
			if (mov.getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE)) {	    	    	    
				AutorizacaoDepartamentoDao dao = getDAO(AutorizacaoDepartamentoDao.class, mov);
				Collection<AutorizacaoDepartamento> autorizacoes = dao.findByAtividade(atividade.getId());

				for (AutorizacaoDepartamento a : autorizacoes) {
					if (!ValidatorUtil.isEmpty(a.getDataAutorizacao())){
						throw new NegocioException("Esta proposta n�o poder� retornar para reedi��o do coordenador porque j� foi autorizada por algum departamento envolvido.");
					}
				}
			}

		}
		else if ( mov.getCodMovimento().equals(SigaaListaComando.REEDITAR_PROJETO_MONITORIA) ) {
		    checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
		}

	}

}