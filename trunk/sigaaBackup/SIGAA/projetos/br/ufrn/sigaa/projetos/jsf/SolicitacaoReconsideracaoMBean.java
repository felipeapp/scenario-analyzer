/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/04/2008
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.SolicitacaoReconsideracaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoSolicitacaoReconsideracao;


/**
 * Responsável por controlar o procedimento de solicitação e 
 * análise de reconsideração de avaliação de propostas de extensão.
 *
 * @author Ilueny Santos
 *
 */
@Component(value = "solicitacaoReconsideracao") @Scope(value = "session")
public class SolicitacaoReconsideracaoMBean extends SigaaAbstractController<SolicitacaoReconsideracao> {

    //Utilizado para armazenar informações de uma consulta ao banco
    public Collection<SolicitacaoReconsideracao> solicitacoesPendentes = new ArrayList<SolicitacaoReconsideracao>();
    
	//Utilizado para armazenar informações de uma consulta ao banco
    public Collection<Projeto> projetosReconsideraveis = new ArrayList<Projeto>();
    
    //Utilizado para armazenar informações de uma consulta ao banco
    public Collection<AtividadeExtensao> acoesExtensaoReconsideraveis = new ArrayList<AtividadeExtensao>();
    
    //Utilizado para armazenar informações de uma consulta ao banco
    public Collection<ProjetoEnsino> projetosMonitoriaReconsideraveis = new ArrayList<ProjetoEnsino>();
    
    
    /**
     * Construtor padrão.
     */
    public SolicitacaoReconsideracaoMBean() {
	obj = new SolicitacaoReconsideracao();
    }


    /**
     * Inicia o procedimento para solicitação de reconsideração de
     * avaliação de propostas de extensão.
     * 
     * Chamado por:
     * <ul>
     * 	<li> /sigaa.war/extensao/menu_ta.jsp </li>
     * 	<li> /sigaa.war/extensao/menu_docente.jsp </li>
     * </ul>
     * 
     * @return Retorna uma página com lista das propóstas passíveis de solicitação de reconsideração pelo coordenador.
     * @throws SegurancaException Somente coordenadores podem realizar esta operação.
     * @throws DAOException 
     */
    public String iniciarSolicitacaoExtensao() throws SegurancaException, DAOException {
    	acoesExtensaoReconsideraveis = getListaAcoesReconsideraveis();
    	return forward(ConstantesNavegacao.SOLICITACAORECONSIDERACAO_LISTA);
    }

    /**
     * Inicia procedimento para solicitação de reconsideração 
     * de avaliação pelos coordenadores de projetos.
     * 
     * Chamado por:
     * <ul>
     * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
 
     * @return
     * @throws SegurancaException
     * @throws DAOException 
     */
    public String iniciarSolicitacaoProjetos() throws SegurancaException, DAOException {
		checkDocenteRole();
    	projetosReconsideraveis = getListaProjetosReconsideraveis();
    	for (Projeto p : projetosReconsideraveis) {
    		p.setSolicitacoesReconsideracao(getGenericDAO().findByExactField(SolicitacaoReconsideracao.class, "projeto.id", p.getId()));
    	}	
    	return forward(ConstantesNavegacaoProjetos.SOLICITAR_RECONSIDERACAO_LISTA);
    }


    /**
     * Inicia procedimento para solicitação de reconsideração 
     * de avaliação pelos coordenadores de projetos de monitoria.
     * 
     * Chamado por:
     * <ul>
     * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
 
     * @return
     * @throws SegurancaException
     * @throws DAOException 
     */
    public String iniciarSolicitacaoProjetosMonitoria() throws SegurancaException, DAOException {
		checkDocenteRole();
    	projetosMonitoriaReconsideraveis = getListaProjetosMonitoriaReconsideraveis();
    	for (ProjetoEnsino pe : projetosMonitoriaReconsideraveis) {
    		pe.getProjeto().setSolicitacoesReconsideracao(getGenericDAO().findByExactField(SolicitacaoReconsideracao.class, "projeto.id", pe.getProjeto().getId()));
    	}	
    	return forward(ConstantesNavegacaoMonitoria.SOLICITACAO_RECONSIDERACAO_LISTA);
    }

    
    /**
     *  Lista todas as ações do usuário logado onde ele é coordenador
     *  e retorna somente os projetos em condição de reconsideração.
     *  
     * @return Página com lista de ações passíveis de solicitação de reconsideração.
     * @throws DAOException busca das ações.
     * @throws SegurancaException 
     * @throws SegurancaException Somente coordenadores podem realizar esta operação. 
     */
    private Collection<AtividadeExtensao> getListaAcoesReconsideraveis() throws DAOException, SegurancaException {
    	AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
    	return dao.findByPassiveisReconsideracao(getUsuarioLogado().getServidor().getId());
    }

    /**
     * Retorna lista de projetos que podem solicitar reconsideração.
     * 
     * @return
     * @throws DAOException
     * @throws SegurancaException
     */
    private Collection<Projeto> getListaProjetosReconsideraveis() throws DAOException, SegurancaException {
    	ProjetoDao dao = getDAO(ProjetoDao.class);
    	return dao.findByPassiveisReconsideracao(getUsuarioLogado().getServidor().getId());
    }

    /**
     * Retorna lista de projetos de monitoria que podem solicitar reconsideração.
     * 
     * @return
     * @throws DAOException
     * @throws SegurancaException
     */
    private Collection<ProjetoEnsino> getListaProjetosMonitoriaReconsideraveis() throws DAOException {
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		return dao.findByPassiveisReconsideracao(getUsuarioLogado().getServidor().getId());
    }
    
    /**
     * Carrega a ação selecionada na sessão.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/extensao/SolicitacaoReconsideracao/lista.jsp </li></ul>  
     *
     * @return Página com formulário de solicitação da reconsideração.
     * @throws ArqException Prepara para execução do comando. Somente coordenadores de ações realizam esta operação.
     */
    public String solicitarReconsideracaoExtensao() throws ArqException {

	int id = getParameterInt("id");
	AtividadeExtensao atividade = getGenericDAO().findByPrimaryKey(id, AtividadeExtensao.class);
	obj = new SolicitacaoReconsideracao();
	obj.setAtividade(atividade);
	obj.setProjeto(atividade.getProjeto());	
	obj.setRegistroEntradaSolicitacao(getUsuarioLogado().getRegistroEntrada());
	obj.setTipo(new TipoSolicitacaoReconsideracao(TipoSolicitacaoReconsideracao.SOLICITACAO_ACAO_EXTENSAO));
	obj.setProjetoMonitoria(null);
	
	// Permite ao coordenador visualizar o resultado das avaliações
	// durante o cadastro da solicitação.
	atividade.getAvaliacoes().iterator();			
	prepareMovimento(SigaaListaComando.CADASTRAR_RECONSIDERACAO_EXTENSAO);
	return forward(ConstantesNavegacao.SOLICITACAORECONSIDERACAO_FORM);

    }
    
    
    /***
     * Prepara uma solicitação de reconsideração para o 
     * projeto selecionado e envia para o formulário de 
     * confirmação do cadastro. 
     * 
     * @return
     * @throws ArqException
     */
    public String solicitarReconsideracaoProjeto() throws ArqException {

	int id = getParameterInt("id");
	Projeto projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
	obj = new SolicitacaoReconsideracao();
	obj.setAtividade(null);
	obj.setProjeto(projeto);	
	obj.setRegistroEntradaSolicitacao(getUsuarioLogado().getRegistroEntrada());
	obj.setTipo(new TipoSolicitacaoReconsideracao(TipoSolicitacaoReconsideracao.SOLICITACAO_ACAO_ACADEMICA_ASSOCIADA));
	
	// Permite ao coordenador visualizar o resultado das avaliações durante o cadastro da solicitação.
	projeto.getAvaliacoes().iterator();				
	prepareMovimento(SigaaListaComando.SOLICITAR_RECONSIDERACAO_PROJETO_BASE);
	return forward(ConstantesNavegacaoProjetos.SOLICITAR_RECONSIDERACAO_FORM);

    }

    /***
     * Prepara uma solicitação de reconsideração para o 
     * projeto selecionado e envia para o formulário de 
     * confirmação do cadastro. 
     * 
     * @return
     * @throws ArqException
     */
    public String solicitarReconsideracaoMonitoria() throws ArqException {
    	int id = getParameterInt("id");
    	ProjetoEnsino pm = getGenericDAO().findByPrimaryKey(id, ProjetoEnsino.class);
    	obj = new SolicitacaoReconsideracao();
    	obj.setAtividade(null);
    	obj.setProjetoMonitoria(pm);
    	obj.setProjeto(pm.getProjeto());	
    	obj.setRegistroEntradaSolicitacao(getUsuarioLogado().getRegistroEntrada());
    	obj.setTipo(new TipoSolicitacaoReconsideracao(TipoSolicitacaoReconsideracao.SOLICITACAO_MONITORIA));

    	// Permite ao coordenador visualizar o resultado das avaliações durante o cadastro da solicitação.
    	pm.getAvaliacoes().iterator();				
    	prepareMovimento(SigaaListaComando.CADASTRAR_RECONSIDERACAO_MONITORIA);
    	return forward(ConstantesNavegacaoMonitoria.SOLICITACAO_RECONSIDERACAO_FORM);
    }
    
    /**
     * Lista todas as solicitação de reconsideração pendentes de análise pela pró-reitoria de extensão.
     * <br><br>
     * Chamado pela JSP: sigaa.war/extensao/menu.jsp
     * 
     * @return Página com lista de solicitações.
     * @throws SegurancaException Somente membros da pró-reitoria podem realizar esta operação.
     * @throws DAOException Busca das solicitações pendentes.
     */
    public String listarSolicitacoesPendentesExtensao() throws SegurancaException, DAOException {

	checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	SolicitacaoReconsideracaoDao dao = getDAO(SolicitacaoReconsideracaoDao.class);
	solicitacoesPendentes = dao.findAllSolicitacoesPendentes(TipoSolicitacaoReconsideracao.SOLICITACAO_ACAO_EXTENSAO);
	return forward(ConstantesNavegacao.ANALISARSOLICITACAORECONSIDERACAO_LISTA);

    }

    /***
     * Lista das as solicitações de reconsideração
     * pendentes de análise pelos membro do CIEPE.
     * 
     * @return
     * @throws SegurancaException
     * @throws DAOException
     */
    public String listarSolicitacoesPendentesProjeto() throws SegurancaException, DAOException {

	checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	SolicitacaoReconsideracaoDao dao = getDAO(SolicitacaoReconsideracaoDao.class);
	solicitacoesPendentes = dao.findAllSolicitacoesPendentes(TipoSolicitacaoReconsideracao.SOLICITACAO_ACAO_ACADEMICA_ASSOCIADA);
	return forward(ConstantesNavegacaoProjetos.ANALISAR_SOLICITACAO_RECONSIDERACAO_LISTA);

    }

    
    /***
     * Lista das as solicitações de reconsideração
     * pendentes de análise pelos membro do Comitê de Extensão.
     * 
     * @return
     * @throws SegurancaException
     * @throws DAOException
     */
    public String listarSolicitacoesPendentesMonitoria() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		SolicitacaoReconsideracaoDao dao = getDAO(SolicitacaoReconsideracaoDao.class);
		solicitacoesPendentes = dao.findAllSolicitacoesPendentes(TipoSolicitacaoReconsideracao.SOLICITACAO_MONITORIA);
		return forward(ConstantesNavegacaoMonitoria.ANALISAR_SOLICITACAO_RECONSIDERACAO_LISTA);
    }

    /**
     * Inicia o procedimento de validação da solicitação de reconsideração.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/extensao/AnalisarSolicitacaoReconsideracao/lista.jsp </li></ul>
     * 
     * @return Página com formulário para validação da solicitação de reconsideração.
     * @throws ArqException Prepara o movimento para execução do comando.
     */
    public String iniciarAnalisarSolicitacaoExtensao() throws ArqException {
	checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	setId();
	getGenericDAO().initialize(obj);
	obj.getAtividade().setAvaliacoes(getGenericDAO().findByExactField(AvaliacaoAtividade.class, "atividade.id" , obj.getAtividade().getId()));
	if (obj.getDataParecer() ==  null) {
	    obj.setAprovado(true);
	}
	prepareMovimento(SigaaListaComando.ANALISAR_RECONSIDERACAO_EXTENSAO);
	return forward(ConstantesNavegacao.ANALISARSOLICITACAORECONSIDERACAO_FORM);

    }
    
    
    /**
     * Inicia o procedimento de análise das solicitações de reconsideração 
     * pelos membros do CIEPE.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/projetos/AnalisarSolicitacaoReconsideracao/lista.jsp </li></ul>
     * 
     * @return
     * @throws ArqException
     */
    public String iniciarAnalisarSolicitacaoProjeto() throws ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		setId();
		getGenericDAO().initialize(obj);		
		obj.getProjeto().setAvaliacoes(getDAO(AvaliacaoDao.class).findByProjeto(obj.getProjeto()));
		
		if (obj.getDataParecer() ==  null) {
		    obj.setAprovado(true);
		}
		prepareMovimento(SigaaListaComando.ANALISAR_RECONSIDERACAO_PROJETO_BASE);
		return forward(ConstantesNavegacaoProjetos.ANALISAR_SOLICITACAO_RECONSIDERACAO_FORM);

    }

    /**
     * Inicia o procedimento de validação da solicitação de reconsideração.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/monitoria/AnalisarSolicitacaoReconsideracao/lista.jsp </li></ul>
     * 
     * @return Página com formulário para validação da solicitação de reconsideração.
     * @throws ArqException Prepara o movimento para execução do comando.
     */
    public String iniciarAnalisarSolicitacaoMonitoria() throws ArqException {
    	checkRole(SigaaPapeis.GESTOR_MONITORIA);
    	setId();
    	getGenericDAO().initialize(obj);
    	obj.getProjetoMonitoria().setAvaliacoes((List<AvaliacaoMonitoria>) getGenericDAO().findByExactField(AvaliacaoMonitoria.class, "projetoEnsino.id" , obj.getProjetoMonitoria().getId()));
    	if (obj.getDataParecer() ==  null) {
    		obj.setAprovado(true);
    	}
    	prepareMovimento(SigaaListaComando.ANALISAR_RECONSIDERACAO_MONITORIA);
    	return forward(ConstantesNavegacaoMonitoria.ANALISAR_SOLICITACAO_RECONSIDERACAO_FORM);
    }
    
    /**
     * Análise realizada por membro da pró-reitoria de extensão, se aprovado o projeto é reaberto para que o 
     * coordenador faça as alterações solicitadas e posteriormente o projeto é reenviado para avaliação.
     * 
     * Chamado por:
     * <ul>
     * 	<li> /sigaa.war/extensao/AnalisarSolicitacaoReconsideracao/form.jsp </li>
     * 	<li> /sigaa.war/extensao/menu.jsp </li> 
     * </ul>
     * 
     * @return Página do menu principal da pró-reitoria de extensão. 
     * @throws ArqException Execução do processador.
     * @throws NegocioException 
     */
    public String analisarSolicitacaoExtensao() throws ArqException {

		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		String parecer = new String(obj.getParecer());
		boolean aprovado = new Boolean(obj.isAprovado());
		
		obj =  getGenericDAO().findByPrimaryKey(obj.getId(), SolicitacaoReconsideracao.class);		
		obj.setParecer(parecer);
		obj.setDataParecer(new Date());
		obj.setAprovado(aprovado);
		
		//Evitando erro de lazy
		getGenericDAO().initialize(obj.getAtividade().getProjeto());
		obj.getAtividade().getProjeto().getCoordenador();
		obj.getAtividade().getAvaliacoes().iterator();
		
		
		try {
			CadastroExtensaoMov mov = new CadastroExtensaoMov();
			mov.setCodMovimento(SigaaListaComando.ANALISAR_RECONSIDERACAO_EXTENSAO);		
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return listarSolicitacoesPendentesExtensao();
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return null;

    }

    /**
     * Persiste o resultado da análise realizada por um membro do CIEPE
     * para uma solicitação de reconsideração. 
     * 
     * @return
     * @throws ArqException
     * @throws NegocioException
     */
    public String analisarSolicitacaoProjeto() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		try {
			String parecer = new String(obj.getParecer());
			boolean aprovado = new Boolean(obj.isAprovado());	
			obj =  getGenericDAO().findByPrimaryKey(obj.getId(), SolicitacaoReconsideracao.class);		
			getGenericDAO().initialize(obj.getProjeto().getEdital());
			obj.setParecer(parecer);
			obj.setDataParecer(new Date());
			obj.setAprovado(aprovado);
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ANALISAR_RECONSIDERACAO_PROJETO_BASE);		
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return cancelar();
		}catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}
    }

    /**
     * Persiste o resultado da análise realizada por um membro do comitê de monitoria
     * para uma solicitação de reconsideração. 
     * 
     * @return
     * @throws ArqException
     * @throws NegocioException
     */
    public String analisarSolicitacaoMonitoria() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		try {
			String parecer = new String(obj.getParecer());
			boolean aprovado = new Boolean(obj.isAprovado());	
			obj =  getGenericDAO().findByPrimaryKey(obj.getId(), SolicitacaoReconsideracao.class);		
			getGenericDAO().initialize(obj.getProjeto().getEdital());
			obj.setParecer(parecer);
			obj.setDataParecer(new Date());
			obj.setAprovado(aprovado);
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ANALISAR_RECONSIDERACAO_MONITORIA);		
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return cancelar();
		}catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}
    }

    
    
    /**
     * Cadastra a solicitação de reconsideração para avaliação pela pró-reitoria de extensão.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/extensao/SolicitacaoReconsideracao/form.jsp </li></ul>
     * 
     * @return Página que permite o cadastro de nova solicitação.
     * @throws ArqException chama o processador. Somente coordenadores de ações podem cadastrar reconsiderações.
     * @throws NegocioException 
     */
    public String cadastrarSolicitacaoExtensao() throws ArqException, NegocioException {
    	try {
    		CadastroExtensaoMov mov = new CadastroExtensaoMov();
    		mov.setCodMovimento(SigaaListaComando.CADASTRAR_RECONSIDERACAO_EXTENSAO);
    		mov.setObjMovimentado(obj);
    		execute(mov, getCurrentRequest());
    		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		return cancelar();
    	}catch (NegocioException e) {
    		addMensagemErro(e.getMessage());
    	}
    	return null;
    }

    /**
     * Cadastra uma solicitação de reconsideração para ser analisada por
     * membros do CIEPE.
     * 
     * @return
     * @throws ArqException
     * @throws NegocioException
     */
    public String cadastrarSolicitacaoProjeto() throws ArqException {
    	checkDocenteRole();
    	try {
    		CadastroExtensaoMov mov = new CadastroExtensaoMov();
    		mov.setCodMovimento(SigaaListaComando.SOLICITAR_RECONSIDERACAO_PROJETO_BASE);
    		mov.setObjMovimentado(obj);
    		execute(mov, getCurrentRequest());
    		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		return cancelar();
    	}catch (NegocioException e) {
    		addMensagemErro(e.getMessage());
    	}
    	return null;
    }

    
    /**
     * Cadastra a solicitação de reconsideração para avaliação pela pró-reitoria de graduação.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/monitoria/SolicitacaoReconsideracao/form.jsp </li></ul>
     * 
     * @return Página que permite o cadastro de nova solicitação.
     * @throws ArqException chama o processador. Somente coordenadores de ações podem cadastrar reconsiderações.
     * @throws NegocioException 
     */
    public String cadastrarSolicitacaoMonitoria() throws ArqException, NegocioException {
    	checkDocenteRole();	
    	try {
    		CadastroExtensaoMov mov = new CadastroExtensaoMov();
    		mov.setCodMovimento(SigaaListaComando.CADASTRAR_RECONSIDERACAO_MONITORIA);
    		mov.setObjMovimentado(obj);
    		execute(mov, getCurrentRequest());
    		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		return cancelar();
    	}catch (NegocioException e) {
    		addMensagemErro(e.getMessage());
    	}
    	return null;
    }

    
    /**
     * Carrega reconsideração e prepara MBeans para visualização.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/extensao/SolicitacaoReconsideracao/lista_reconsideracoes.jsp </li></ul>
     * 
     * @return Página com detalhes da reconsideração.
     * @throws DAOException busca a reconsideração.
     */
    public String view() throws DAOException {
    	Integer id = getParameterInt("idReconsideracao");
    	obj = getGenericDAO().findByPrimaryKey(id, SolicitacaoReconsideracao.class);			
    	return forward(ConstantesNavegacao.SOLICITACAORECONSIDERACAO_VIEW);
    }

    /**
     * Recupera a ação e encaminha para a página de visualização das reconsiderações
     * solicitadas.
     * 
     * sigaa.war/extensao/SolicitacaoReconsideracao/lista.jsp
     * 
     * @return Página com a lista de solicitações realizadas pelo coordenador.
     * @throws DAOException Busca solicitações.
     */
    public String visualizarSolicitacoesExtensao() throws DAOException {
    	Integer id = getParameterInt("id");
    	obj = new SolicitacaoReconsideracao();
    	obj.setAtividade(getGenericDAO().findByPrimaryKey(id,	AtividadeExtensao.class));
    	return forward(ConstantesNavegacao.SOLICITACAORECONSIDERACAO_MINHAS_SOLICITACOES);
    }

    /**
     * Lista todas as solicitações de reconsideração para a atividade selecionada.
     * 
     * @return
     * @throws DAOException
     */
    public String visualizarSolicitacoesProjeto() throws DAOException {
    	Integer id = getParameterInt("id");
    	obj = new SolicitacaoReconsideracao();
    	obj.setProjeto(getGenericDAO().findByPrimaryKey(id, Projeto.class));
    	return forward(ConstantesNavegacaoProjetos.SOLICITAR_RECONSIDERACAO_MINHAS_SOLICITACOES);
    }

    /**
     * Lista todas as solicitações de reconsideração para o projeto de monitoria selecionado.
     * 
     * @return
     * @throws DAOException
     */
    public String visualizarSolicitacoesMonitoria() throws DAOException {
    	Integer id = getParameterInt("id");
    	obj = new SolicitacaoReconsideracao();
    	obj.setProjetoMonitoria(getGenericDAO().findByPrimaryKey(id, ProjetoEnsino.class));
    	return forward(ConstantesNavegacaoMonitoria.SOLICITACAO_RECONSIDERACAO_MINHAS);
    }


    
    /**
     * Lista todas as solicitações de reconsideração pendentes de aprovação.
     * 
     * Chamado por:
     * <ul><li> /sigaa.war/extensao/AnalisarSolicitacaoReconsideracao/lista.jsp </li></ul>
     * 
     */
    public Collection<SolicitacaoReconsideracao> getSolicitacoesPendentes() {
        return solicitacoesPendentes;
    }

    public void setSolicitacoesPendentes(
    	Collection<SolicitacaoReconsideracao> solicitacoesPendentes) {
        this.solicitacoesPendentes = solicitacoesPendentes;
    }


    public Collection<Projeto> getProjetosReconsideraveis() {
        return projetosReconsideraveis;
    }


    public void setProjetosReconsideraveis(
    	Collection<Projeto> projetosReconsideraveis) {
        this.projetosReconsideraveis = projetosReconsideraveis;
    }


    public Collection<AtividadeExtensao> getAcoesExtensaoReconsideraveis() {
        return acoesExtensaoReconsideraveis;
    }


    public void setAcoesExtensaoReconsideraveis(
    	Collection<AtividadeExtensao> acoesExtensaoReconsideraveis) {
        this.acoesExtensaoReconsideraveis = acoesExtensaoReconsideraveis;
    }


	public Collection<ProjetoEnsino> getProjetosMonitoriaReconsideraveis() {
		return projetosMonitoriaReconsideraveis;
	}


	public void setProjetosMonitoriaReconsideraveis(
			Collection<ProjetoEnsino> projetosMonitoriaReconsideraveis) {
		this.projetosMonitoriaReconsideraveis = projetosMonitoriaReconsideraveis;
	}
    
}
