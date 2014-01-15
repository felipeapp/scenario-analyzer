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
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("programacao")
@Scope("session")
public class ProgramacaoVisualMBean
		extends
		AbstractControllerProdocente<ProducaoArtisticaLiterariaVisual> {



	public ProgramacaoVisualMBean() {
		obj = new ProducaoArtisticaLiterariaVisual();
		obj.setTipoProducao(TipoProducao.PROGRAMACAO_VISUAL);
		obj.setSubTipoArtistico(new SubTipoArtistico(SubTipoArtistico.SUB_TIPO_ARTISTICO_GENERICO));
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ProducaoArtisticaLiterariaVisual.class, "id", "descricao");
	}
	
	public Collection<SelectItem> getTipoArtistico()
	{
		TipoArtisticoDao dao = getDAO(TipoArtisticoDao.class);
		try
		{
			return toSelectItems(dao.findTipoArtisticoByProducao(TipoProducao.PROGRAMACAO_VISUAL), "id", "descricao");
		} catch(Exception e)
		  { 
			  return new ArrayList<SelectItem>(); 
		  }
		
	}
	

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.PROGRAMACAO_VISUAL);
		obj.setServidor(getServidorUsuario());
		
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() {
		obj = new ProducaoArtisticaLiterariaVisual();
		obj.setTipoProducao(TipoProducao.PROGRAMACAO_VISUAL);
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(ProducaoArtisticaLiterariaVisual) p;
	}

	public String listar() {

		resetBean("paginacao");
		setDirBase("/prodocente/producao/");
		getCurrentSession().setAttribute("backPage", "ProgramacaoVisual/lista.jsp");
		return forward("/prodocente/producao/ProgramacaoVisual/lista.jsp");

	}

	public String preCadastrar() throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		setDirBase("/prodocente/producao/");
		return forward("/prodocente/producao/ProgramacaoVisual/form.jsp");
	}

	public Collection<Object> getAll() throws SegurancaException {

		setTamanhoPagina(20);
		ProducaoDao dao = getDAO(ProducaoDao.class);

		checkDocenteRole();

		try {
			return dao.findByTipoProducaoServidor(ProducaoArtisticaLiterariaVisual.class,getServidorUsuario() ,TipoProducao.PROGRAMACAO_VISUAL, getPaginacao());
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<Object>();
		}

	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.PROGRAMACAO_VISUAL);
	}
	
	@Override
	public String getDirBase()
	{
		return "/prodocente/producao/ProgramacaoVisual";
	}

}
