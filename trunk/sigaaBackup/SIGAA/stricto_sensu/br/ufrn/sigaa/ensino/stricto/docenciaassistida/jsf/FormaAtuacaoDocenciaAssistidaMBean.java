/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * ManagedBean responsável pelo cadastro de formas de atuação de bolsistas REUNI
 * 
 * @author wendell
 *
 */
@Component("formaAtuacaoDocenciaAssistidaBean") @Scope("request")
public class FormaAtuacaoDocenciaAssistidaMBean extends SigaaAbstractController<FormaAtuacaoDocenciaAssistida> {

	/**
	 * Construtor Padrão da Classe
	 */
	public FormaAtuacaoDocenciaAssistidaMBean() {
		obj = new FormaAtuacaoDocenciaAssistida();
	}
	
	/**
	 * Iniciar o Cadastro
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/forma_atuacao_docencia_assistida/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!ValidatorUtil.isEmpty(getGenericDAO().findAtivosByExactField(FormaAtuacaoDocenciaAssistida.class, "descricao", obj.getDescricao()))){
			addMensagemErro("Forma de Atuação já Cadastrada!");
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
	 * Método chamado pela seguinte JSP:
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
	 * Método chamado pela seguinte JSP:
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
	 * Verifica as permissões
	 * <br/><br/>
	 * Método não chamado por JSPs.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
	}
	
	/**
	 * Caminho do Formulário
	 * <br/><br/>
	 * Não chamado por JSPs
	 */
	@Override
	public String getFormPage() {
		return "/stricto/forma_atuacao_docencia_assistida/form.jsf";
	}
	
	/**
	 * Caminho da Lista
	 * <br/><br/>
	 * Não chamado por JSPs 
	 */
	@Override
	public String getListPage() {
		return getFormPage();
	}
	
	/**
	 * Cancelar a alteração de uma forma de atuação, redirecionando o usuário à lista de formas cadastradas
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
