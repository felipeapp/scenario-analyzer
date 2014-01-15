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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 147
 * Descrição = Composição Musical Nacional
 * @author Victor Hugo
 *
 */
public class MapperComposicaoMusicalNacional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperComposicaoMusicalNacional() {
		super(AudioVisual.class);
	}
	
	/**
	 * item 8.38 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.28)
	 */
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL;
	}
	
}
