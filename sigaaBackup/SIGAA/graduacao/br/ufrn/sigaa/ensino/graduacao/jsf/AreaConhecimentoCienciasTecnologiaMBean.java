/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * ManagedBean responsável pelo cadastro de áreas de atuação em ciências e tecnologia
 * 
 * @author wendell
 *
 */
@Component("areaConhecimentoCienciasTecnologiaBean") @Scope("request")
public class AreaConhecimentoCienciasTecnologiaMBean extends SigaaAbstractController<AreaConhecimentoCienciasTecnologia> {

	/**
	 * Construtor Padrão
	 */
	public AreaConhecimentoCienciasTecnologiaMBean() {
		obj = new AreaConhecimentoCienciasTecnologia();
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
	public String listar() throws ArqException {
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getListPage());
	}
	
	/**
	 * Efetua o Cadastro
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/area_conhecimento_ciencias_tecnologia/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!ValidatorUtil.isEmpty(getGenericDAO().findAtivosByExactField(AreaConhecimentoCienciasTecnologia.class, "denominacao", obj.getDenominacao()))){
			addMensagemErro("Área de Atuação já Cadastrada!");
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
	 * Método chamado pela seguinte JSP:
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
	 * Não chamado por JSPs 
	 */
	@Override
	public String getListPage() {
		return getFormPage();
	}
	
	/**
	 * Cancelar a alteração de uma área de conhecimento, redirecionando o usuário à lista de áreas cadastradas
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
	 * Combo que retorna todas as áreas ativas.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllAtivosCombo() throws ArqException {
		return toSelectItems(getAllAtivos(), "id", "denominacao");
	}
}
