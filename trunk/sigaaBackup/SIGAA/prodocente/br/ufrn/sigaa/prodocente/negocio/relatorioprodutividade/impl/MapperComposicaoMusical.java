/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 41
 * Descrição = Composição Musical
 * @author Victor Hugo
 *
 */
public class MapperComposicaoMusical extends AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperComposicaoMusical() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getSubTipoArtistico().getId() == SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL;
	}
	
}