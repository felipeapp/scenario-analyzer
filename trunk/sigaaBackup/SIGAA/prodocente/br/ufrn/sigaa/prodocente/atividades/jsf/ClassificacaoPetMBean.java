/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel por carregar as informa��es das p�ginas que mostram as informa��es 
 * sobre Classifica��o do PET.
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/Pet/view.jsp
	 * sigaa.war/prodocente/atividades/TutoriaPet/view.jsp
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(ClassificacaoPet.class, "id", "descricao");
	}

	/**
	 * Reinicializa os dados para uma nova opera��o.
	 * 
	 * @throws SegurancaException
	 */
	private void clear() throws SegurancaException {
		obj = new ClassificacaoPet();
		checkChangeRole();
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	protected void afterCadastrar() throws SegurancaException {
		clear();
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PROGRAD);
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getListPage() {
		return "/prodocente/atividades/ClassificacaoPet/lista.jsf";
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getFormPage() {
		return "/prodocente/atividades/ClassificacaoPet/form.jsf";
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getUrlRedirecRemover() {
		return getListPage();
	}
	
	/**
	 * Serve para alterar uma Classifica��o do PET.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/ClassificaoPet/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		return forward(getFormPage());
	}
	
	/**
	 * Serve para cadastrar nova Classifica��o do PET.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/ClassificaoPet/form.jsp
	 * sigaa.war/prodocente/atividades/ClassificaoPet/view.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	/**
	 * Cancelamento da opera��o atual. Para quando o usu�rio clicar em cancelar o mesmo vai 
	 * para a tela da listagem.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/ClassificaoPet/form.jsp
	 * sigaa.war/prodocente/atividades/ClassificaoPet/view.jsp
	 */
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
}