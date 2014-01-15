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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 145
 * Descri��o = Produ��o de obras de instala��o em amostras/eventos oficiais locais/regionais
 * @author Victor Hugo
 *
 */
public class MapperProducaoObraInstalacaoAmostraEventoOficialLocalRegional
		extends AbstractMapperProducaoIntelectual<AudioVisual> {

	public MapperProducaoObraInstalacaoAmostraEventoOficialLocalRegional() {
		super(AudioVisual.class);
	}
	
	/**
	 * item 8.36 (especifica��o de uma consulta j� existente subtipo art�stico especifico, subconjunto de 3.19)
	 */
	@Override
	protected boolean isAtendeCriterios(AudioVisual p) {
		return (p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||
                p.getTipoRegiao().getId() == TipoRegiao.LOCAL) &&
                p.getSubTipoArtistico().getId() == SubTipoArtistico.INSTALACAO_AUDIO_VISUAL;
	}
	
}