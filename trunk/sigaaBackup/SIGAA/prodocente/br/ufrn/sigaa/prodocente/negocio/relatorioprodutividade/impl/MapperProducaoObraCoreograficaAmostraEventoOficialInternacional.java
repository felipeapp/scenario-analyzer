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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 110
 * Descrição = Produção de obras Coreográficas em amostras/eventos oficiais internacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraCoreograficaAmostraEventoOficialInternacional
		extends AbstractMapperProducaoIntelectual<ProducaoArtisticaLiterariaVisual> {

	public MapperProducaoObraCoreograficaAmostraEventoOficialInternacional() {
		super(ProducaoArtisticaLiterariaVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ProducaoArtisticaLiterariaVisual p) {
		return  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
        (p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFIA_MONTAGENS ||
                p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_EXPOSICAO_APRESENTACAO_EVENTOS ||
                p.getSubTipoArtistico().getId() == SubTipoArtistico.COREOGRAFO_PUBLICACAO_EVENTO);
	}
	
}