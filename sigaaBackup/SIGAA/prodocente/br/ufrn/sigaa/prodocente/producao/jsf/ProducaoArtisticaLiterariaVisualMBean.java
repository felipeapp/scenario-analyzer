/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
public class ProducaoArtisticaLiterariaVisualMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual> {
	public ProducaoArtisticaLiterariaVisualMBean() {
		obj = new ProducaoArtisticaLiterariaVisual();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ProducaoArtisticaLiterariaVisual.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.OUTROS);
		//colocar o tipo artístico para setar
		if(obj.getTipoArtistico().getId()==1)
			obj.setTipoProducao(TipoProducao.AUDIO_VISUAIS);
		else if(obj.getTipoArtistico().getId()==2)
			obj.setTipoProducao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS);
		else if(obj.getTipoArtistico().getId()==3)
			obj.setTipoProducao(TipoProducao.MONTAGENS);
		else if(obj.getTipoArtistico().getId()==4)
			obj.setTipoProducao(TipoProducao.PROGRAMACAO_VISUAL);

		obj.setServidor(getServidorUsuario());
		
		super.beforeCadastrarAndValidate();
	}
	

	@Override
	protected void afterCadastrar() {
		obj = new ProducaoArtisticaLiterariaVisual();
	}

	@Override
	public void popularObjeto(Producao p) {
		obj =(ProducaoArtisticaLiterariaVisual) p;
	}
}
