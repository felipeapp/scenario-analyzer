/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Controller responsável pelo cadastro, listagem e remoção de Áreas de
 * Conhecimento do Vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("areaConhecimentoVestibular")
@Scope("session")
public class AreaConhecimentoVestibularMBean extends
		SigaaAbstractController<AreaConhecimentoVestibular> {

	/** Construtor padrão. */
	public AreaConhecimentoVestibularMBean() {
		this.obj = new AreaConhecimentoVestibular();
	}

	/** Verifica se possui os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Retorna uma lista de SelectItem de áreas de conhecimentos ativas.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivoCombo() throws DAOException {
		AreaConhecimentoVestibularDao dao = getDAO(AreaConhecimentoVestibularDao.class);
		return toSelectItems(dao.findAll(AreaConhecimentoVestibular.class,
				"descricao", "asc"), "id", "descricao");
	}

	/** Retorna uma lista de áreas.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<AreaConhecimentoVestibular> getAll() throws ArqException {
		AreaConhecimentoVestibularDao dao = getDAO(AreaConhecimentoVestibularDao.class);
		return dao
				.findAll(AreaConhecimentoVestibular.class, "descricao", "asc");
	}

	/**
	 * Redireciona, após cadastrar uma nova área, para
	 * /vestibular/AreaConhecimentoVestibular/lista.jsp
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return forward("/vestibular/AreaConhecimentoVestibular/lista.jsp");
	}

	/** Redireciona, após remover uma área, para
	 * /vestibular/AreaConhecimentoVestibular/lista.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardRemover()
	 */
	@Override
	protected String forwardRemover() {
		return forward("/vestibular/AreaConhecimentoVestibular/lista.jsp");
	}

}
