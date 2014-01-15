/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 1, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 121
 * Descrição = Produção de obras teatrais em amostras/eventos oficiais locais/Regionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraTeatralAmostraEventoOficialLocalRegional extends
		AbstractMapperProducaoIntelectual<ProducaoArtisticaLiterariaVisual> {

	public MapperProducaoObraTeatralAmostraEventoOficialLocalRegional() {
		super(ProducaoArtisticaLiterariaVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ProducaoArtisticaLiterariaVisual p) {
		return (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                (p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_EXPOSICAO_APRESENTACAO_EVENTOS ||
                 p.getSubTipoArtistico().getId() == SubTipoArtistico.TEATRAL_PUBLICACAO_EVENTO);
	}
	
}