/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.prodocente.AtividadesProdocenteDao;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoProdocente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacaoDocente;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Gerado pelo CrudBuilder
 */
public class OrientacaoProdocenteMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoProdocente> {
	private boolean tipoOrientacao=true;

	public OrientacaoProdocenteMBean() {
		obj = new OrientacaoProdocente();
		obj.setOrientacao(new TipoOrientacaoDocente());
		obj.setServidor(new Servidor());
		obj.setTipoOrientacao(new TipoOrientacao());
		obj.setAluno(new Discente());
		obj.setDepartamento(new Unidade());
		obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		obj.setIes(new InstituicoesEnsino());
		setBuscaServidor(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(OrientacaoProdocente.class, "id", "descricao");
	}
	@Override
	protected void afterCadastrar() {
		if(obj.getTipoOrientacao().getId()==TipoOrientacao.RESIDENCIA_MEDICA){
			tipoOrientacao=false;
		}
		obj = new OrientacaoProdocente();

	}
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		if(obj.getTipoOrientacao().getId()==0){
			obj.setTipoOrientacao(new TipoOrientacao(TipoOrientacao.RESIDENCIA_MEDICA));
		}
	}
	public String preCadastrarResidencia() {
		tipoOrientacao=false;
		obj.setTipoOrientacao(new TipoOrientacao(TipoOrientacao.RESIDENCIA_MEDICA));
		return super.preCadastrar();
	}

	public String listarResidencia() throws SegurancaException {
		tipoOrientacao=false;
		obj.setTipoOrientacao(new TipoOrientacao(TipoOrientacao.RESIDENCIA_MEDICA));
		destroyMBean("paginacao");
		setDirBase("/prodocente/atividades/");
		return forward(getListPage());
	}

	@Override
	public void buscar(ActionEvent e) throws DAOException {
		AtividadesProdocenteDao dao = (AtividadesProdocenteDao) getDAO(AtividadesProdocenteDao.class);
		setAtividades(dao.findByServidorTipoOrientacao(obj.getClass(), getIdServidor(),new TipoOrientacao(TipoOrientacao.RESIDENCIA_MEDICA)));
	}

	public boolean isTipoOrientacao() {
		return tipoOrientacao;
	}

	public void setTipoOrientacao(boolean tipoOrientacao) {
		this.tipoOrientacao = tipoOrientacao;
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		//checkRole(SigaaPapeis.PRODOCENTE_PPG);
	}


	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PPG);
	}


}
