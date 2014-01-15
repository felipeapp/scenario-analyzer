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
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.ClassificacaoPet;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * MBean responsável por carregar as informações das páginas que mostram as informações 
 * sobre Classificação do PET.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("classificacaoPet")
public class ClassificacaoPetMBean extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.ClassificacaoPet> {
	
	
	public ClassificacaoPetMBean() throws SegurancaException {
		clear();
	}

	/**
	 * Retorna todos os itens da entidade ClassificacaoPet
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/Pet/view.jsp
	 * sigaa.war/prodocente/atividades/TutoriaPet/view.jsp
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(ClassificacaoPet.class, "id", "descricao");
	}

	/**
	 * Reinicializa os dados para uma nova operação.
	 * 
	 * @throws SegurancaException
	 */
	private void clear() throws SegurancaException {
		obj = new ClassificacaoPet();
		checkChangeRole();
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	protected void afterCadastrar() throws SegurancaException {
		clear();
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PROGRAD);
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public String getListPage() {
		return "/prodocente/atividades/ClassificacaoPet/lista.jsf";
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public String getFormPage() {
		return "/prodocente/atividades/ClassificacaoPet/form.jsf";
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public String getUrlRedirecRemover() {
		return getListPage();
	}
	
	/**
	 * Serve para alterar uma Classificação do PET.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/ClassificaoPet/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		return forward(getFormPage());
	}
	
	/**
	 * Serve para cadastrar nova Classificação do PET.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/ClassificaoPet/form.jsp
	 * sigaa.war/prodocente/atividades/ClassificaoPet/view.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	/**
	 * Cancelamento da operação atual. Para quando o usuário clicar em cancelar o mesmo vai 
	 * para a tela da listagem.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/ClassificaoPet/form.jsp
	 * sigaa.war/prodocente/atividades/ClassificaoPet/view.jsp
	 */
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
}