/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 40
 * Descri��o = Autoria de Partitura Musical
 * @author Victor Hugo
 *
 */
public class MapperAutoriaPartituraMusical extends
		AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperAutoriaPartituraMusical() {
		super(AudioVisual.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return p.getSubTipoArtistico().getId() == SubTipoArtistico.PARTITURA_MUSICAL_AUDIO_VISUAL;
	}
	

}