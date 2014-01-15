/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 29/01/2009
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.FormaAtuacaoDocenciaAssistida;

/**
 * ManagedBean respons�vel pelo cadastro de formas de atua��o de bolsistas REUNI
 * 
 * @author wendell
 *
 */
@Component("formaAtuacaoDocenciaAssistidaBean") @Scope("request")
public class FormaAtuacaoDocenciaAssistidaMBean extends SigaaAbstractController<FormaAtuacaoDocenciaAssistida> {

	/**
	 * Construtor Padr�o da Classe
	 */
	public FormaAtuacaoDocenciaAssistidaMBean() {
		obj = new FormaAtuacaoDocenciaAssistida();
	}
	
	/**
	 * Iniciar o Cadastro
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	}
	
	/**
	 * Efetua o Cadastro
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/forma_atuacao_docencia_assistida/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!ValidatorUtil.isEmpty(getGenericDAO().findAtivosByExactField(FormaAtuacaoDocenciaAssistida.class, "descricao", obj.getDescricao()))){
			addMensagemErro("Forma de Atua��o j� Cadastrada!");
			return null;
		}
		
		return super.cadastrar();
	}
	
	/**
	 * Executa depois de cadastro
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		super.afterCadastrar();
	}
	
	/**
	 * Atualiza o registro
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/forma_atuacao_docencia_assistida/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		return super.atualizar();
	}
	
	/**
	 * Inativa o registro
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/forma_atuacao_docencia_assistida/form.jsp</li>
	 * </ul>
	 */	
	@Override
	public String inativar() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.DESATIVAR);
		return super.inativar();
	}

	/**
	 * Verifica as permiss�es
	 * <br/><br/>
	 * M�todo n�o chamado por JSPs.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
	}
	
	/**
	 * Caminho do Formul�rio
	 * <br/><br/>
	 * N�o chamado por JSPs
	 */
	@Override
	public String getFormPage() {
		return "/stricto/forma_atuacao_docencia_assistida/form.jsf";
	}
	
	/**
	 * Caminho da Lista
	 * <br/><br/>
	 * N�o chamado por JSPs 
	 */
	@Override
	public String getListPage() {
		return getFormPage();
	}
	
	/**
	 * Cancelar a altera��o de uma forma de atua��o, redirecionando o usu�rio � lista de formas cadastradas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/forma_atuacao_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String cancelarAlteracao() {
		if (obj.getId() > 0){
			resetBean();
			obj = new FormaAtuacaoDocenciaAssistida();
			return forward(getFormPage());			
		} else 
			return cancelar();		
	}
	
}
