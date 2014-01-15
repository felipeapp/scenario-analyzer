/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 117
 * Descrição = Produção de obras Musicais em amostras/eventos oficiais nacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraMusicalAmostraEventoOficialNacional extends
		AbstractMapperProducaoIntelectual<ProducaoArtisticaLiterariaVisual> {

	public MapperProducaoObraMusicalAmostraEventoOficialNacional() {
		super(ProducaoArtisticaLiterariaVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ProducaoArtisticaLiterariaVisual p) {
		return p.getTipoRegiao() != null &&  p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
        (p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                p.getSubTipoArtistico().getId() == SubTipoArtistico.MUSICAL_PUBLICACAO_EVENTO);
	}
	
}