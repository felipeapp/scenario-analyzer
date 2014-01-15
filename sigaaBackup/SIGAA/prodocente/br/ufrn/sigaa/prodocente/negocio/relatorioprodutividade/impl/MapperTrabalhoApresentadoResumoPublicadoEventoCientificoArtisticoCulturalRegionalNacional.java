/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 31
 * Descrição = Trabalho apresentado ou resumo publicado em eventos científicos ou artístico-culturais regionais ou locais
 * @author Victor Hugo
 */
public class MapperTrabalhoApresentadoResumoPublicadoEventoCientificoArtisticoCulturalRegionalNacional
		extends AbstractMapperProducaoIntelectual<Producao> {

	public MapperTrabalhoApresentadoResumoPublicadoEventoCientificoArtisticoCulturalRegionalNacional() {
		super(Producao.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Producao producao) {
		if ( producao instanceof PublicacaoEvento ) {
            PublicacaoEvento p = (PublicacaoEvento) producao;
            // item 3.19.1
            return ( p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL || p.getTipoRegiao().getId() == TipoRegiao.LOCAL);
        }
        
        if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
            ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;
            // item 3.19.3
            // item 3.19.4
            // item 3.19.5
            return ( p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ||  p.getTipoRegiao().getId() == TipoRegiao.LOCAL)
                && (p.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS) 
                		|| p.getTipoProducao().equals(TipoProducao.PROGRAMACAO_VISUAL)
                		|| p.getTipoProducao().equals(TipoProducao.MONTAGENS));
        }
        
		return false;
	}
	
}