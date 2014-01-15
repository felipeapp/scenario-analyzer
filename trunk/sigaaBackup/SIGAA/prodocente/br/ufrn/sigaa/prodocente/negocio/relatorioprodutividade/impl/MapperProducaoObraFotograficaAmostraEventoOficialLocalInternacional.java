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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 134
 * Descrição = Produção de obras fotográficas em amostras/eventos oficiais internacionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraFotograficaAmostraEventoOficialLocalInternacional
		extends AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraFotograficaAmostraEventoOficialLocalInternacional() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.FOTOGRAFIAS_AUDIO_VISUAL;
	}

}