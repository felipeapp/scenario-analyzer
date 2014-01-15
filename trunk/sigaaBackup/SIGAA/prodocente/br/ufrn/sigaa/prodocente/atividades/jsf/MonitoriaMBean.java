/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Monitoria;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Managed Bean para o projeto de monitoria e central para navegação
 */
@Scope("request")
@Component("monitoria")
public class MonitoriaMBean
		extends AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.Monitoria> {
	
	public MonitoriaMBean() {
		obj = new Monitoria();
		obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		obj.setDepartamento(new Unidade());
		obj.setServidor(new Servidor());
		obj.setMonitor(new Discente(1));

	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Monitoria.class, "id", "descricao");
	}
	
	@Override
	protected void afterCadastrar() {
		obj = new Monitoria();
	}

	/**
	 * Checa se o usuário Logado tem permissão para usar o serviço.  
	 * Não invocado por JSPs
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		super.checkListRole();
	}
	
	/**
	 * Serve para evitar que a de operação já solicitada. O que acarretaria o não 
	 * inclusão do registro no banco de dados. 
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/prodocente/Atividades/Monitoria/form.jsp</li>
	 * <li>/sigaa.war/prodocente/Atividades/Monitoria/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}	
	
	@Override
	public String getFormPage() {
		return "/prodocente/atividades/Monitoria/form.jsf";
	}

	@Override
	public String getListPage() {
		return "/prodocente/atividades/Monitoria/lista.jsf";
	}
	
	/**
	 * Para quando ele voltar do cadastro ir para a tela, para efetuar a atualização dos 
	 * dados. 
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/prodocente/Atividades/Monitoria/listar.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		return forward(getFormPage());
	}

	/**
	 * Para redirecionar o usuário para a tela da listagem logo após uma remoção.
	 */
	@Override
	public String getUrlRedirecRemover(){
		return getListPage();
	}

	public boolean isFrequenciaMonitoria () {
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosMonitoria.FREQUENCIA_MONITORIA);
	}
	
}