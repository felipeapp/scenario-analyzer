/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 115
 * Descri��o = Produ��o de obras Liter�rias em amostras/eventos oficiais locais/regionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraLiterariaAmostraEventoOficialLocalRegional extends
		AbstractMapperProducaoIntelectual<ProducaoArtisticaLiterariaVisual> {

	public MapperProducaoObraLiterariaAmostraEventoOficialLocalRegional() {
		super(ProducaoArtisticaLiterariaVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ProducaoArtisticaLiterariaVisual p) {
		return (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                (p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_PUBLICACAO_EVENTO ||
                 p.getSubTipoArtistico().getId() == SubTipoArtistico.LITERARIO_EXPOSICAO_APRESENTACAO_EVENTOS);
	}
	
}