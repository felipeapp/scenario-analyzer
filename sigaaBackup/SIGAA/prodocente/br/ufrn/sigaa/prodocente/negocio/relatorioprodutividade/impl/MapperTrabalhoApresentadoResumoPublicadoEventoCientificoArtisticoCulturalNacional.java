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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 30
 * Descrição = Trabalho apresentado ou resumo publicado em eventos científicos ou artístico-culturais nacionais
 * @author Victor Hugo
 *
 */
public class MapperTrabalhoApresentadoResumoPublicadoEventoCientificoArtisticoCulturalNacional
		extends AbstractMapperProducaoIntelectual<Producao> {

	public MapperTrabalhoApresentadoResumoPublicadoEventoCientificoArtisticoCulturalNacional() {
		super(Producao.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Producao producao) {
		// item 3.18.1
        if ( producao instanceof PublicacaoEvento ) {
            PublicacaoEvento p = (PublicacaoEvento) producao;
            return   p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
            		// Contanto com Prof. Bernardete em 04/07/2007
            		//  ( p.getApresentado() != null && p.getApresentado() ) &&
            	   p.getNatureza() != null && (p.getTipoEvento() != null &&
                		  (p.getTipoEvento().getId() == TipoEvento.CONFERENCIA ||
                			p.getTipoEvento().getId() == TipoEvento.PAINEL ||
                			p.getTipoEvento().getId() == TipoEvento.CONGRESSO));
        }
        
		// item 3.18.3
        // item 3.18.4
        // item 3.18.5
        if ( producao instanceof ProducaoArtisticaLiterariaVisual ) {
            ProducaoArtisticaLiterariaVisual p = (ProducaoArtisticaLiterariaVisual) producao;

            return   p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
                    (p.getTipoProducao().equals(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS) 
                    		|| p.getTipoProducao().equals(TipoProducao.PROGRAMACAO_VISUAL)
                    		|| p.getTipoProducao().equals(TipoProducao.MONTAGENS));

        }
        
        return false;
	}
	
}