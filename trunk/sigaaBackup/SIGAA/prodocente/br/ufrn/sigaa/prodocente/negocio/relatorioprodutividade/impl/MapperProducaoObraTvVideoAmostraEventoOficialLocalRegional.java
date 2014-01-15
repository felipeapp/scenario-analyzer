/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 127
 * Descrição = Produção de obras de TV/Vídeo em amostras/eventos oficiais locais/regionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraTvVideoAmostraEventoOficialLocalRegional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraTvVideoAmostraEventoOficialLocalRegional() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                p.getSubTipoArtistico().getId() == SubTipoArtistico.TELEVISAO_AUDIO_VISUAL;
	}
	
}