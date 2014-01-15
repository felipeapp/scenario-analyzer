/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
	 * Redireciona para página de alteração de projetos.
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
	 * <p>Método utilizado na preparação de uma proposta de ação acadêmica associada para receber
	 * recursos do comitê (bolsas, orçamento, etc).
	 * Esta operação só pode ser realizada por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método utilizado para concessão de recursos
	 * de uma proposta de ação acadêmica associada.
	 * Esta operação só pode ser realizada por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método utilizado para preparar a alteração compulsória da situação
	 * de uma proposta de ação acadêmica associada.
	 * Esta operação só pode ser realizada por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método utilizado para alteração compulsória da situação
	 * de uma proposta de ação acadêmica associada.
	 * Esta operação só pode ser realizada por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método utilizado para preparar a alteração compulsória do orçamento concedido
	 * de uma proposta de ação acadêmica associada.
	 * Esta operação só pode ser realizada por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método utilizado para alteração compulsória do orçamento concedido
	 * de uma proposta de ação acadêmica associada.
	 * Esta operação só pode ser realizada por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método que possibilita a alteração do cadastro
	 * da proposta de ação acadêmica por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * <p>Método que possibilita a alteração do cadastro
	 * das ações vinculadas a ação acadêmica por um membro do 
	 * comitê integrado de ensino, pesquisa e extensão.
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
	 * Redireciona o usuário para página de busca de projetos para vincular a unidade Orçamentária.
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
	 * <p>Método utilizado para preparar a vinculação de um projeto a uma unidade 
	 * orçamentaria.
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
			addMensagemErro("Este projeto não pode ser vinculado a uma unidade orçamentária. Verifique se o " +
					"projeto possui coordenação e unidade proponente definidas.");
			return null;
		} 
		
		if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria())){
			obj.setUnidadeOrcamentaria(new Unidade());
		}
		return forward(ConstantesNavegacaoProjetos.VINCULAR_UNIDADE_ORCAMENTARIA_FORM);
	}
	
	
	/**
	 * <p>Método utilizado para vinculação de um projeto a uma unidade 
	 * orçamentaria.
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
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Orçamentária");
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
