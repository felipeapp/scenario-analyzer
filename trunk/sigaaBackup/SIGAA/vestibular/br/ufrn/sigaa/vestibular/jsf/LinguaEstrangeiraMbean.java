/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.vestibular.dominio.LinguaEstrangeira;

/** Controller respons�vel pelas opera��es sobre l�ngua estrangeira.
 * @author �dipo Elder F. Melo
 *
 */
@Component("linguaEstrangeira")
@Scope("session")
public class LinguaEstrangeiraMbean extends
		SigaaAbstractController<LinguaEstrangeira> {

	/**Construtor padr�o. */
	public LinguaEstrangeiraMbean() {
		obj = new LinguaEstrangeira();
	}

	/** Verifica os pap�is: VESTIBULAR.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Redireciona para /vestibular/LinguaEstrangeira/lista.jsp ap�s cadastrar.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return forward("/vestibular/LinguaEstrangeira/lista.jsp");
	}

	/** Redireciona para /vestibular/LinguaEstrangeira/lista.jsp ap�s remover. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardRemover()
	 */
	@Override
	protected String forwardRemover() {
		return forward("/vestibular/LinguaEstrangeira/lista.jsp");
	}

	/** Cole��o de SelectItem de l�nguas estrangeiras.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (LinguaEstrangeira lingua : getAll()) {
			combo.add(new SelectItem(lingua.getId(), lingua.getDenominacao()));
		}
		return combo;
	}
}
