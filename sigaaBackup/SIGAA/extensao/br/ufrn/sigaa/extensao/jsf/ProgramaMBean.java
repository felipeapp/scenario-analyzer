/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/11/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ProgramaExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.AtividadeExtensaoValidator;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoValidator;

/**
 * MBean responsável por controlar parte do cadastro de programas de extensão. A
 * primeira parte do cadastro de um programa de extensão é controlada por
 * {@link AtividadeExtensaoMBean}, a parte especifica relativa ao programa é
 * controlada por esta classe. 
 * 
 * @author Ilueny Santos
 * 
 */
@Scope("session")
@Component("programaExtensao")
public class ProgramaMBean extends SigaaAbstractController<AtividadeExtensao> {

	/** Atributo que representa o título do programa */
	private String titulo; // para fazer a busca por atividades
	
	/**
	 * Limpa os objetos do Mbean e prepara para o inicio do cadastro do programa.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 */
	public void clear() {
	    obj = new AtividadeExtensao();
	    obj.setPermanente(false);
	    obj.setVinculoProgramaExtensao(false);
	    obj.setTipoAtividadeExtensao(new TipoAtividadeExtensao(TipoAtividadeExtensao.PROGRAMA));
	    obj.getProjeto().setCoordenador(new MembroProjeto());
	    obj.setProgramaExtensao(new ProgramaExtensao());	    
	    obj.setTipoRegiao(new TipoRegiao());
	}

	/**
	 * Inicia o cadastro do programa passando o controle para o {@link AtividadeExtensaoMBean}.
	 * Prepara o formulário para cadastro de dados gerais.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/seleciona_atividade.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
	    if ((obj != null) && (!obj.isProjetoAssociado())) {
		clear();
	    }
	    AtividadeExtensaoHelper.getAtividadeMBean().prepararFormulario(this);
	    return forward(ConstantesNavegacao.DADOS_GERAIS);
	}

	public ProgramaMBean() {
	    clear();
	}

	/**
	 * Vai para a tela de adição de atividades de extensão no programa.	 * 
	 * @return a pagina de adição de atividades de extensão no programa.
	 * 
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/programa.jsp
	 * 
	 * 
	 * @throws DAOException
	 * 
	 *  
	 */
	public String submeterPrograma() throws DAOException {
	    ListaMensagens mensagens = new ListaMensagens();
	    
		if (!obj.isProjetoAssociado()) {
		    // Validação do programa.
		    AtividadeExtensaoValidator.validaDadosPrograma(obj, mensagens);
		    if (!mensagens.isEmpty()) {
		    	addMensagens(mensagens);
		    	AtividadeExtensaoHelper.instanciarAtributos(getObj());
		    	return null;
		    }
		}

	    return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}
	
	
	/**
	 * Adiciona o coordenador do programa.
	 * 
	 */
	private void adicionarCoordenador() {
	    GenericDAO dao = getGenericDAO();
	    try {
	    	if (!obj.isProjetoAssociado()) {
		    //Carrega o servidor selecionado pelo usuário para criar o novo membro da equipe do projeto.
		    dao.initialize(obj.getProjeto().getCoordenador().getServidor());
	    	    obj.getProjeto().getCoordenador().setPessoa(obj.getProjeto().getCoordenador().getServidor().getPessoa());
	    	    obj.getProjeto().getCoordenador().setDiscente(null);
	    	    obj.getProjeto().getCoordenador().setDocenteExterno(null);
	    	    obj.getProjeto().getCoordenador().setDataInicio(obj.getProjeto().getDataInicio());
	    	    obj.getProjeto().getCoordenador().setDataFim(obj.getProjeto().getDataFim());
	    	    obj.getProjeto().getCoordenador().setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COORDENADOR));
	    	    obj.getProjeto().getCoordenador().setCategoriaMembro(new CategoriaMembro(CategoriaMembro.SERVIDOR));
       			
	    	    // Definindo o coordenador do projeto.
	    	    obj.getProjeto().getCoordenador().setProjeto(obj.getProjeto());	    	    	
	    	    obj.getProjeto().getEquipe().add(obj.getProjeto().getCoordenador());
	    	}
	    } catch (DAOException e) {
	    	tratamentoErroPadrao(e);
	    }

	}

	
	
	/**
	 * Valida as atividades adicionadas ao programa e vai para próximo passo
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/atividades.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String submeterAtividades() throws DAOException {
		// validacao
		ListaMensagens mensagens = new ListaMensagens();
		AtividadeExtensaoValidator.validaAtividadesPrograma(obj, mensagens);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}
