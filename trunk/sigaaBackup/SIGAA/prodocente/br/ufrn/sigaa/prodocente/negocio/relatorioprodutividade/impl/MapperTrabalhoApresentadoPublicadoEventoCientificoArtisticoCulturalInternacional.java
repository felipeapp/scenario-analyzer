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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 29
 * Descrição = Trabalho apresentado com ou sem resumo publicado em eventos científicos ou artístico-cultural internacionais (coreográfico, literário, musical, outros)
 * @author Victor Hugo
 *
 */
public class MapperTrabalhoApresentadoPublicadoEventoCientificoArtisticoCulturalInternacional
		extends AbstractMapperProducaoIntelectual<Producao> {

	public MapperTrabalhoApresentadoPublicadoEventoCientificoArtisticoCulturalInternacional() {
		super(Producao.class);
	}
	
	@Override
	protected boolean isTipoAtendeCriterios(Producao producao) {
		return ( PublicacaoEvento.class.isInstance(producao) && isAtendeCriterios(PublicacaoEvento.class.cast(producao)) ) ||
		 (PublicacaoEvento.class.isInstance(producao) && isAtendeCriterios(PublicacaoEvento.class.cast(producao)) );
	}
	
	@Override
	protected boolean isAtendeCriterios(Producao producao) {
		
		// item 3.17.1
		if ( producao instanceof PublicacaoEvento ) {
            PublicacaoEvento p = (PublicacaoEvento) producao;
            return p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL 
            		&& p.getNatureza() != null;
		}
        
        // item 3.17.3
        // item 3.17.4
        // item 3.17.5
        if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
            ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

            return  p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL &&
                    (p.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS) 
                    		|| p.getTipoProducao().equals(TipoProducao.PROGRAMACAO_VISUAL)
                    		|| p.getTipoProducao().equals(TipoProducao.MONTAGENS));
        }
        
		return false;
	}
	

}
