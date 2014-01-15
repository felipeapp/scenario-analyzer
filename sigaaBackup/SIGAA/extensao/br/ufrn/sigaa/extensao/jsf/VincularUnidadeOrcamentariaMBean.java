/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.negocio.ProjetoBaseValidator;

/*******************************************************************************
 * 
 * Classe respons�vel por controlar a vincula��o de uma A��o de extens�o a
 * uma Unidade Or�ament�ria.
 * 
 ******************************************************************************/
@Component("vincularUnidadeOrcamentariaMBean")
@Scope("request")
public class VincularUnidadeOrcamentariaMBean extends AbstractControllerCadastro<Projeto> {

	public VincularUnidadeOrcamentariaMBean() {
		obj = new Projeto();
	}


	/**
	 * Redireciona o usu�rio para p�gina de busca de projetos para vincular a unidade Or�ament�ria.
	 * @return
	 * @throws ArqException
	 */
	public String listar() throws ArqException {
	    checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
	    return forward(ConstantesNavegacao.VINCULAR_UNIDADE_ORCAMENTARIA_LISTA);
	}
	
	
	/**
	 * <p>M�todo utilizado para preparar a vincula��o de um projeto a uma unidade 
	 * or�amentaria.
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/projetos/AlteracaoProjeto/lista.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		
		checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
		prepareMovimento(SigaaListaComando.VINCULAR_PROJETO_UNIDADE_ORCAMENTARIA);
		int id = getParameterInt("id",0);
		
		GenericDAO dao = null;
		
		try{
			
			dao =  getGenericDAO();
			
			obj = dao.findByPrimaryKey(id, Projeto.class, "id", "titulo", "unidade.id", "unidade.nome", "unidade.sigla", 
					"dataInicio", "dataFim", "ano", "numeroInstitucional", "coordenador.id", "coordenador.pessoa.nome", "coordenador.ativo", 
					"situacaoProjeto.id", "situacaoProjeto.descricao");
	
			Projeto projetoUnidade = dao.findByPrimaryKey(id, Projeto.class, "unidadeOrcamentaria.id");
			
			if(projetoUnidade != null)
				obj.setUnidadeOrcamentaria(projetoUnidade.getUnidadeOrcamentaria());
			
			if (obj == null) {
				addMensagemErro("Este projeto n�o pode ser vinculado a uma unidade or�ament�ria. Verifique se o " +
						"projeto possui coordena��o e unidade proponente definidas.");
				return null;
			} 
	
			if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria())){
				obj.setUnidadeOrcamentaria(new Unidade());
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(ConstantesNavegacao.VINCULAR_UNIDADE_ORCAMENTARIA_FORM);
	}
	
	
	/**
	/**
	 * <p>M�todo utilizado para vincula��o de uma a��o a uma unidade 
	 * or�amentaria.
	 * 
	 * </p> 
	 * Chamado por: 
	 * <ul><li>sigaa.war/extensao/VincularUnidadeOrcamentaria/form.jsp</li></ul> 
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException 
	 */
	public String confirmarVinculo() throws ArqException {
	    checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setCodMovimento(SigaaListaComando.VINCULAR_PROJETO_UNIDADE_ORCAMENTARIA);
	    mov.setObjMovimentado(obj);
	    try {		
		ListaMensagens lista = new ListaMensagens();
		ProjetoBaseValidator.validaVincularProjetoUnidadeOrcamentaria(obj, lista);
		ValidatorUtil.validateRequired(obj.getUnidadeOrcamentaria(), "Unidade Or�ament�ria", lista);
		
		if (!lista.isEmpty()) {
		  addMensagens(lista);
		  return null;
		} else {
		    execute(mov, getCurrentRequest());
		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		}
		
	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
		return null;
	    } 
	    ((AtividadeExtensaoMBean) getMBean("atividadeExtensao")).localizar();
	    return forward(ConstantesNavegacao.VINCULAR_UNIDADE_ORCAMENTARIA_LISTA);
	}
	
	
}
