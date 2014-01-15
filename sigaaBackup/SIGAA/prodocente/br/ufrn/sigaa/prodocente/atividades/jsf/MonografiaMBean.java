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

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Monografia;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;

/**
 * Gerado pelo CrudBuilder
 */
public class MonografiaMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.Monografia> {
	public MonografiaMBean() {
		obj = new Monografia();
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setDepartamento(new Unidade());
		obj.setServidor(new Servidor());
		obj.setSubArea(new AreaConhecimentoCnpq());
		obj.setTipoOrientacao(new TipoOrientacao());
		obj.setAluno(new Discente());
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Monografia.class, "id", "titulo");
	}

	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException,
			DAOException {
		obj.setServidor(getServidorUsuario());

		if (obj.getDiscenteExterno())
			obj.setAluno(null);
	}

	@Override
	public void afterAtualizar()
	{
		if (obj.getAluno() == null)
			obj.setAluno(new Discente());
	}


	@Override
	protected void afterCadastrar() {
		obj = new Monografia();
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setDepartamento(new Unidade());
		obj.setServidor(new Servidor());
		obj.setSubArea(new AreaConhecimentoCnpq());
		obj.setTipoOrientacao(new TipoOrientacao());
		obj.setAluno(new Discente());

	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		super.checkDocenteRole();
	}

	/**
	 * Carrega as sub-areas da área cadastrada
	 *
	 * @throws DAOException
	 */
	public void carregaSubAreas() throws DAOException {

		AreaConhecimentoCnpqDao dao = (AreaConhecimentoCnpqDao) getDAO(AreaConhecimentoCnpqDao.class);
		if (obj.getArea() != null && obj.getArea().getId() != 0 ) {
			subArea = toSelectItems(dao.findAreas(obj.getArea()), "id", "nome");
			AbstractControllerProdocente bean = new AbstractControllerProdocente();
			bean.setSubArea(subArea);
			createMBean("producao", bean);
		}

	}

	@Override
	public String getUrlRedirecRemover()
	{
		return "/sigaa/prodocente/atividades/Monografia/lista.jsf";
	}


}
