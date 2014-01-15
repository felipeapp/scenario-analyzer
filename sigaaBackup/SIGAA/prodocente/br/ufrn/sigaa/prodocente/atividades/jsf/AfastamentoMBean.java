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
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoAfastamento;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * MBean responsável por carregar as informações das páginas que mostram as informações 
 * sobre afastamento.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("afastamentoProdocente")
public class AfastamentoMBean extends 
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoAfastamento> {
	
	public AfastamentoMBean() {
		obj = new TipoAfastamento();
	}

	/**
	 * Retorna os tipos de afastamentos existentes na base de dados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/Licenca/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoAfastamento.class, "id", "descricao");
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		obj = new TipoAfastamento();
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PRH);
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public String getUrlRedirecRemover(){
		return "/sigaa/prodocente/atividades/TipoAfastamento/lista.jsf";
	}

	/**
	 * Serve para evitar que a de operação já solicitada. O que acarretaria o não 
	 * inclusão do registro no banco de dados. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/TipoAfastamento/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
	
		if (getConfirmButton().equals("Cadastrar")) {
			prepareMovimento(ArqListaComando.CADASTRAR);
			setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		}
		super.cadastrar();
		return forward("/sigaa/prodocente/atividades/TipoAfastamento/lista.jsf");
	}
 	
	/**
	 * Para quando o usuário cancelar um cadastro o mesmo ser redirecionado para a tela 
	 * da listagem.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/TipoAfastamento/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
	
}