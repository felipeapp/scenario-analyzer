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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 146
 * Descrição = Composição Musical Internacional
 * @author Victor Hugo
 *
 */
public class MapperComposicaoMusicalInternacional extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperComposicaoMusicalInternacional() {
		super(AudioVisual.class);
	}
	
	/**
	 * item 8.37 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.28)
	 */
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
        p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL;
	}
	
}