/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Jul 20, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 29
 * Descri��o = Trabalho apresentado com ou sem resumo Expandido publicado em eventos cient�ficos ou art�stico-cultural internacionais (coreogr�fico, liter�rio, musical, outros)
 * @author Jean Guerethes
 *
 */
public class MapperResumoExpandidoAnaisEventoInternacional
		extends AbstractMapperProducaoIntelectual<Producao> {

	public MapperResumoExpandidoAnaisEventoInternacional() {
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
            		&& p.getNatureza() != null && p.getNatureza().equals('E');
		}
        
		return false;
	}
	

}
