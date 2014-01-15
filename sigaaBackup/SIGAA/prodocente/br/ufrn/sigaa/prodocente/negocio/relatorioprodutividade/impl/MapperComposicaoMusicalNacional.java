/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * Descri��o = Composi��o Musical Nacional
 * @author Victor Hugo
 *
 */
public class MapperComposicaoMusicalNacional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperComposicaoMusicalNacional() {
		super(AudioVisual.class);
	}
	
	/**
	 * item 8.38 (especifica��o de uma consulta j� existente subtipo art�stico especifico, subconjunto do item 3.28)
	 */
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL;
	}
	
}
