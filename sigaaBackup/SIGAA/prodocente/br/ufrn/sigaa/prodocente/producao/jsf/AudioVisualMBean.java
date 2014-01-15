/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
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
import br.ufrn.sigaa.arq.dao.TipoArtisticoDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Gerado pelo CrudBuilder
 */

@Component("audioVisual")
@Scope("session")
public class AudioVisualMBean extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual> {
	
	public AudioVisualMBean() {
		obj = new AudioVisual();
		obj.setTipoProducao(TipoProducao.AUDIO_VISUAIS);
		obj.setArea(new AreaConhecimentoCnpq(1));
		obj.setSubArea(new AreaConhecimentoCnpq(1));
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(AudioVisual.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.AUDIO_VISUAIS);
		obj.setServidor(getServidorUsuario());
		super.beforeCadastrarAndValidate();
	}

	@Override
	public String atualizar() throws ArqException {
		super.atualizar();
		if (obj.getTipoParticipacao() == null) 
			obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		if (obj.getTipoRegiao() == null) 
			obj.setTipoRegiao(new TipoRegiao());
		if (obj.getTipoArtistico() == null) 
			obj.setTipoArtistico(new TipoArtistico());
		if (obj.getSubTipoArtistico() == null) 
			obj.setSubTipoArtistico(new SubTipoArtistico());
		return forward(getFormPage());
	}
	
	@Override
	protected void afterCadastrar() {
		obj = new AudioVisual();
		obj.setArea(new AreaConhecimentoCnpq(1));
		obj.setSubArea(new AreaConhecimentoCnpq(1));
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(AudioVisual) p;

	}

	/**
	 * Seleciona o tipo artístico a partir da produção
	 * @return
	 */
	public Collection<SelectItem> getTipoArtistico(){
		TipoArtisticoDao dao = getDAO(TipoArtisticoDao.class);
		try
		{
			return toSelectItems(dao.findTipoArtisticoByProducao(TipoProducao.AUDIO_VISUAIS), "id", "descricao");
		} catch(Exception e)
		  {
			e.printStackTrace();
			  return new ArrayList<SelectItem>();
		  }

	}
	
	/***
	 * Seleciona uma participação, a partir do tipo de produção.
	 */
	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.AUDIO_VISUAIS);
	}

	/**
	 * Seleciona uma classificação a partir de uma subTipoArtistico.
	 */
	public List<SelectItem> getTipo(){
		try {
			return toSelectItems(getGenericDAO().findByExactField(SubTipoArtistico.class, new String[]{"tipoProducao.id", "ativo"}, new Object[]{obj.getTipoProducao().getId(), true}), "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			 return new ArrayList<SelectItem>();
		}
	}
}