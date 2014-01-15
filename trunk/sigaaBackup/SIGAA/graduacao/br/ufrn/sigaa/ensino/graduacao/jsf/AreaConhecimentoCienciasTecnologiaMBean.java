/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.AreaConhecimentoCienciasTecnologia;

/**
 * ManagedBean respons�vel pelo cadastro de �reas de atua��o em ci�ncias e tecnologia
 * 
 * @author wendell
 *
 */
@Component("areaConhecimentoCienciasTecnologiaBean") @Scope("request")
public class AreaConhecimentoCienciasTecnologiaMBean extends SigaaAbstractController<AreaConhecimentoCienciasTecnologia> {

	/**
	 * Construtor Padr�o
	 */
	public AreaConhecimentoCienciasTecnologiaMBean() {
		obj = new AreaConhecimentoCienciasTecnologia();
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
	public String listar() throws ArqException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	}
	
	/**
	 * Efetua o Cadastro
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/area_conhecimento_ciencias_tecnologia/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!ValidatorUtil.isEmpty(getGenericDAO().findAtivosByExactField(AreaConhecimentoCienciasTecnologia.class, "denominacao", obj.getDenominacao()))){
			addMensagemErro("�rea de Atua��o j� Cadastrada!");
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
	 * 	<li>/sigaa.war/graduacao/area_conhecimento_ciencias_tecnologia/form.jsp</li>
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
	 * 	<li>/sigaa.war/graduacao/area_conhecimento_ciencias_tecnologia/form.jsp</li>
	 * </ul>
	 */	
	@Override
	public String inativar() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.DESATIVAR);
		return super.inativar();
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
	 * Cancelar a altera��o de uma �rea de conhecimento, redirecionando o usu�rio � lista de �reas cadastradas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/area_conhecimento_ciencias_tecnologia/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String cancelarAlteracao() {
		if (obj.getId() > 0){
			resetBean();
			obj = new AreaConhecimentoCienciasTecnologia();
			return forward(getFormPage());			
		} else 
			return cancelar();
	}
	

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
	}
	
	@Override
	public String getFormPage() {
		return "/graduacao/area_conhecimento_ciencias_tecnologia/form.jsf";
	}
	
	/**
	 * Combo que retorna todas as �reas ativas.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllAtivosCombo() throws ArqException {
		return toSelectItems(getAllAtivos(), "id", "denominacao");
	}
}
