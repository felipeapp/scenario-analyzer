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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 123
 * Descrição = Produção de obras de cinema em amostras/eventos oficiais nacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraCinemaAmostraEventoOficialNacional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraCinemaAmostraEventoOficialNacional() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.CINEMA_AUDIO_VISUAL;
	}
	
}