/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/11/2009
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Gerado pelo CrudBuilder
 * Controlador para cadastro de editais associados
 * 
 * @author Ilueny
 */
@Component
@Scope("request")
public class AlteracaoProjetoMBean extends SigaaAbstractController<Projeto> {

	private Collection<ProjetoEnsino> projetosEnsino;
	private Collection<AtividadeExtensao> acoesExtensao;

	/*
	 * Inicializa um novo projeto.
	 */
	private void iniciar() {
		obj = new Projeto();
	}
	
	public AlteracaoProjetoMBean() {
		iniciar();
	}

	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/*
	 * Redireciona para p�gina de altera��o de projetos.
	 * 
	 * Chamado por: 
	 * sigaa.war/portais/docente/menu_docente.jsp
	 */
	public String iniciarAlterarProjeto() throws SegurancaException {
	    checkChangeRole();
	    ((BuscaAcaoAssociadaMBean)getMBean("buscaAcaoAssociada")).setResultadosBusca(new ArrayList<Projeto>());
	    return forward(ConstantesNavegacaoProjetos.ALTERAR_PROJETO_LISTA);
	}

	/**
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String listaConcederRecursos() throws SegurancaException {
	    checkChangeRole();
	    ((BuscaAcaoAssociadaMBean)getMBean("buscaAcaoAssociada")).setResultadosBusca(new ArrayList<Projeto>());
	    return forward(ConstantesNavegacaoProjetos.CONCEDER_RECURSOS_LISTA);
	}

	/**
	 * <p>M�todo utilizado na prepara��o de uma proposta de a��o acad�mica associada para receber
	 * recursos do comit� (bolsas, or�amento, etc).
	 * Esta opera��o s� pode ser realizada por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * 
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/Avaliacao/ConcederRecursos/lista.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarConcederRecursos() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    prepareMovimento(SigaaListaComando.CONCEDER_RECURSOS_PROJETO);
	    int id = getParameterInt("id",0);
	    obj = getGenericDAO().findByPrimaryKey(id, Projeto.class);
	    
	    //evitar erro de lazy
	    obj.getEquipe().iterator();
	    obj.getCronograma().iterator();
	    obj.getArquivos().iterator();
	    obj.getOrcamento().iterator();
	    obj.getOrcamentoConsolidado().iterator();
	    
	    return forward(ConstantesNavegacaoProjetos.CONCEDER_RECURSOS_FORM);
	}

	/**
	 * <p>M�todo utilizado para concess�o de recursos
	 * de uma proposta de a��o acad�mica associada.
	 * Esta opera��o s� pode ser realizada por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/Avaliacao/ConcederRecursos/form.jsp</li></ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException 
	 */
	public String concederRecursos() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setCodMovimento(SigaaListaComando.CONCEDER_RECURSOS_PROJETO);
	    mov.setObjMovimentado(obj);
	    try {		
	    	execute(mov, getCurrentRequest());
	    	addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    	((BuscaAcaoAssociadaMBean)getMBean("buscaAcaoAssociada")).localizar();
		    return forward(ConstantesNavegacaoProjetos.CONCEDER_RECURSOS_LISTA);
	    } catch (NegocioException e) {
	    	addMensagemErro(e.getMessage());
	    	return null;
	    }
	}

	
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	}
	
	/**
	 * <p>M�todo utilizado para preparar a altera��o compuls�ria da situa��o
	 * de uma proposta de a��o acad�mica associada.
	 * Esta opera��o s� pode ser realizada por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/AlteracaoProjeto/lista.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterarSituacao() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    prepareMovimento(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_BASE);
	    int id = getParameterInt("id",0);
	    obj = getGenericDAO().findByPrimaryKey(id, Projeto.class);
	    
	    //evitar erro de lazy
	    obj.getEquipe().iterator();
	    obj.getCronograma().iterator();
	    obj.getArquivos().iterator();
	    
	    return forward(ConstantesNavegacaoProjetos.ALTERAR_SITUACAO_FORM);
	}
	
	
	/**
	 * <p>M�todo utilizado para altera��o compuls�ria da situa��o
	 * de uma proposta de a��o acad�mica associada.
	 * Esta opera��o s� pode ser realizada por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/AlteracaoProjeto/form_situacao.jsp</li></ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException 
	 */
	public String alterarSituacao() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setCodMovimento(SigaaListaComando.ALTERAR_SITUACAO_PROJETO_BASE);
	    mov.setObjMovimentado(obj);
	    try {		
		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);		
	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
		return null;
	    }
	    return cancelar();
	}

	
	
	/**
	 * <p>M�todo utilizado para preparar a altera��o compuls�ria do or�amento concedido
	 * de uma proposta de a��o acad�mica associada.
	 * Esta opera��o s� pode ser realizada por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/AlteracaoProjeto/lista.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterarOrcamento() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
	    prepareMovimento(SigaaListaComando.ALTERAR_ORCAMENTO_PROJETO_BASE);
	    int id = getParameterInt("id",0);
	    obj = getGenericDAO().findByPrimaryKey(id, Projeto.class);
	    
	    //evitar erro de lazy
	    obj.getOrcamento().iterator();
	    obj.getOrcamentoConsolidado().iterator();
	    
	    return forward(ConstantesNavegacaoProjetos.ALTERAR_ORCAMENTO_FORM);
	}
	
	
	/**
	 * <p>M�todo utilizado para altera��o compuls�ria do or�amento concedido
	 * de uma proposta de a��o acad�mica associada.
	 * Esta opera��o s� pode ser realizada por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p> 
	 * Chamado por: <ul><li>sigaa.war/projetos/AlteracaoProjeto/form_situacao.jsp</li></ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	public String alterarOrcamento() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setCodMovimento(SigaaListaComando.ALTERAR_ORCAMENTO_PROJETO_BASE);
	    mov.setObjMovimentado(obj);
	    try {
		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
		return null;
	    } catch (ArqException e) {
		addMensagemErro(e.getMessage());
		return null;
	    }
	    return cancelar();
	}

	
	/**
	 * <p>M�todo que possibilita a altera��o do cadastro
	 * da proposta de a��o acad�mica por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p>
	 * Chamado por: <ul><li>sigaa.war/projetos/AlteracaoProjeto/lista.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String alterarProjetoBase() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    getCurrentRequest().getSession(true).setAttribute("CIEPE_ALTERANDO_CADASTRO", Boolean.TRUE);
	    return ((ProjetoBaseMBean)getMBean("projetoBase")).preAtualizar();
	}

	/**
	 * <p>M�todo que possibilita a altera��o do cadastro
	 * das a��es vinculadas a a��o acad�mica por um membro do 
	 * comit� integrado de ensino, pesquisa e extens�o.
	 * </p>
	 * Chamado por: <ul><li>sigaa.war/projetos/AlteracaoProjeto/lista.jsp</li></ul>
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String alterarAcoesVinculadas() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    int id = getParameterInt("id",0);
	    obj = getGenericDAO().findByPrimaryKey(id, Projeto.class);	    
	    //evitar erro de lazy
	    obj.getOrcamento().iterator();
	    obj.getOrcamentoConsolidado().iterator();
	    projetosEnsino = getDAO(ProjetoMonitoriaDao.class).findByProjetoBase(id);
	    acoesExtensao = getDAO(AtividadeExtensaoDao.class).findByProjetoBase(id);
	    return forward(ConstantesNavegacaoProjetos.ALTERAR_PROJETOS_VINCULADOS_LISTA);
	}
	
	/**
	 * Redireciona o usu�rio para p�gina de busca de projetos para vincular a unidade Or�ament�ria.
	 * 
	 * Chamado por: 
	 * <ul><li>sigaa.war/portais/docente/menu_docente.jsp</li></ul> 

	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listaVincularUnidadeOrcamentaria() throws ArqException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    return forward(ConstantesNavegacaoProjetos.VINCULAR_UNIDADE_ORCAMENTARIA_LISTA);
	}
	
	
	/**
	 * <p>M�todo utilizado para preparar a vincula��o de um projeto a uma unidade 
	 * or�amentaria.
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/VincularUnidadeOrcamentaria/lista.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarVincularUnidadeOrcamentaria() throws ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		prepareMovimento(SigaaListaComando.VINCULAR_PROJETO_UNIDADE_ORCAMENTARIA);
		int id = getParameterInt("id",0);
		obj = getGenericDAO().findByPrimaryKey(id, Projeto.class, "id", "titulo", "unidade.id", "unidade.nome", "unidade.sigla", 
				"dataInicio", "dataFim", "ano", "numeroInstitucional", "coordenador.id", "coordenador.pessoa.nome", "coordenador.ativo", 
				"situacaoProjeto.id", "situacaoProjeto.descricao");

		if (obj == null) {
			addMensagemErro("Este projeto n�o pode ser vinculado a uma unidade or�ament�ria. Verifique se o " +
					"projeto possui coordena��o e unidade proponente definidas.");
			return null;
		} 
		
		if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria())){
			obj.setUnidadeOrcamentaria(new Unidade());
		}
		return forward(ConstantesNavegacaoProjetos.VINCULAR_UNIDADE_ORCAMENTARIA_FORM);
	}
	
	
	/**
	 * <p>M�todo utilizado para vincula��o de um projeto a uma unidade 
	 * or�amentaria.
	 * 
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/VincularUnidadeOrcamentaria/form.jsp</li></ul> 
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException 
	 */
	public String vincularUnidadeOrcamentaria() throws ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.VINCULAR_PROJETO_UNIDADE_ORCAMENTARIA);
		mov.setObjMovimentado(obj);
		try {		
			if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria())) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Or�ament�ria");
				return null;
			}else {
				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			}
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		((BuscaAcaoAssociadaMBean) getMBean("buscaAcaoAssociada")).localizar();
		return forward(ConstantesNavegacaoProjetos.VINCULAR_UNIDADE_ORCAMENTARIA_LISTA);
	}
	
	

	public Collection<ProjetoEnsino> getProjetosEnsino() {
	    return projetosEnsino;
	}

	public void setProjetosEnsino(Collection<ProjetoEnsino> projetosEnsino) {
	    this.projetosEnsino = projetosEnsino;
	}

	public Collection<AtividadeExtensao> getAcoesExtensao() {
	    return acoesExtensao;
	}

	public void setAcoesExtensao(Collection<AtividadeExtensao> acoesExtensao) {
	    this.acoesExtensao = acoesExtensao;
	}
	

}
