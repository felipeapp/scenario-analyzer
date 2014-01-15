/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/02/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.TipoArtisticoDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("montagem")
@Scope("session")
public class MontagemMBean
		extends AbstractControllerProdocente<ProducaoArtisticaLiterariaVisual> {

	public MontagemMBean() {
		obj = new ProducaoArtisticaLiterariaVisual();
		obj.setTipoProducao(TipoProducao.MONTAGENS);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ProducaoArtisticaLiterariaVisual.class, "id", "descricao");
	}
	
	public Collection<SelectItem> getTipoArtistico() {
		TipoArtisticoDao dao = getDAO(TipoArtisticoDao.class);
		try {
			return toSelectItems(dao.findTipoArtisticoByProducao(TipoProducao.MONTAGENS), "id", "descricao");
		} catch(Exception e) { 
			return new ArrayList<SelectItem>(); 
	    }
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoArtistico(TipoArtistico.APRESENTACAO_EVENTO);
		obj.setTipoProducao(TipoProducao.MONTAGENS);
		obj.setServidor(getServidorUsuario());
		try {
			prepareMovimento(ArqListaComando.CADASTRAR);
		} catch (ArqException e) {
			e.printStackTrace();
		}		
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() {
		obj = new ProducaoArtisticaLiterariaVisual();
		obj.setTipoProducao(TipoProducao.MONTAGENS);
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(ProducaoArtisticaLiterariaVisual) p;
	}

	
	@Override
	public String getDirBase()
	{
		return "/prodocente/producao/Montagem";
	}
	
	
	public String listar() {

		resetBean("paginacao");
		setDirBase("/prodocente/producao/");
		getCurrentSession().setAttribute("backPage", "Montagem/lista.jsp");
		return forward("/prodocente/producao/Montagem/lista.jsp");

	}

	public String preCadastrar() throws ArqException {
		setDirBase("/prodocente/producao/");
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward("/prodocente/producao/Montagem/form.jsp");
	}
	
	public Collection<Object> getAll() throws SegurancaException {

		setTamanhoPagina(20);
		ProducaoDao dao = getDAO(ProducaoDao.class);

		checkDocenteRole();

		try {
			return dao.findByTipoProducaoServidor(ProducaoArtisticaLiterariaVisual.class,getServidorUsuario() ,TipoProducao.MONTAGENS, getPaginacao());
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Object>();
		}

	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.MONTAGENS);
	}
	
	

}
