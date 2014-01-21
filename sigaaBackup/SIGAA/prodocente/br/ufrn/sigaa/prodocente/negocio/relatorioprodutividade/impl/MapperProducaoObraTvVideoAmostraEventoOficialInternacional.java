/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 1, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 125
 * Descri��o = Produ��o de obras de TV/V�deo em amostras/eventos oficiais internacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraTvVideoAmostraEventoOficialInternacional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraTvVideoAmostraEventoOficialInternacional() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.TELEVISAO_AUDIO_VISUAL;
	}
	
}