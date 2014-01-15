/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.AreaConhecimentoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;

/**
 * Controller respons�vel pelo cadastro, listagem e remo��o de �reas de
 * Conhecimento do Vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("areaConhecimentoVestibular")
@Scope("session")
public class AreaConhecimentoVestibularMBean extends
		SigaaAbstractController<AreaConhecimentoVestibular> {

	/** Construtor padr�o. */
	public AreaConhecimentoVestibularMBean() {
		this.obj = new AreaConhecimentoVestibular();
	}

	/** Verifica se possui os pap�is: VESTIBULAR.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Retorna uma lista de SelectItem de �reas de conhecimentos ativas.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivoCombo() throws DAOException {
		AreaConhecimentoVestibularDao dao = getDAO(AreaConhecimentoVestibularDao.class);
		return toSelectItems(dao.findAll(AreaConhecimentoVestibular.class,
				"descricao", "asc"), "id", "descricao");
	}

	/** Retorna uma lista de �reas.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<AreaConhecimentoVestibular> getAll() throws ArqException {
		AreaConhecimentoVestibularDao dao = getDAO(AreaConhecimentoVestibularDao.class);
		return dao
				.findAll(AreaConhecimentoVestibular.class, "descricao", "asc");
	}

	/**
	 * Redireciona, ap�s cadastrar uma nova �rea, para
	 * /vestibular/AreaConhecimentoVestibular/lista.jsp
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return forward("/vestibular/AreaConhecimentoVestibular/lista.jsp");
	}

	/** Redireciona, ap�s remover uma �rea, para
	 * /vestibular/AreaConhecimentoVestibular/lista.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardRemover()
	 */
	@Override
	protected String forwardRemover() {
		return forward("/vestibular/AreaConhecimentoVestibular/lista.jsp");
	}

}
