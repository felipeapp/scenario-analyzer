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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 141
 * Descrição = Produção de obras de pintura em amostras/eventos oficiais nacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraPinturaAmostraEventoOficialNacional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraPinturaAmostraEventoOficialNacional() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.PINTURA_AUDIO_VISUAL;
	}
	
}