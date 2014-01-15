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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 140
 * Descrição = Produção de obras de pinturas em amostras/eventos oficiais internacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraPinturaAmostraEventoOficialInternacional
		extends AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraPinturaAmostraEventoOficialInternacional() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.PINTURA_AUDIO_VISUAL;
	}
	
}