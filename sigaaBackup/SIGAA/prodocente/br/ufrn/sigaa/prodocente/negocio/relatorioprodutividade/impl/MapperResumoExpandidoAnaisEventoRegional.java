/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 31
 * Descrição = Trabalho apresentado ou resumo expandido publicado em eventos científicos ou artístico-culturais regionais ou locais
 * 
 * @author Jean Guerethes
 */
public class MapperResumoExpandidoAnaisEventoRegional
		extends AbstractMapperProducaoIntelectual<Producao> {

	public MapperResumoExpandidoAnaisEventoRegional() {
		super(Producao.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Producao producao) {
		if ( producao instanceof PublicacaoEvento ) {
            PublicacaoEvento p = (PublicacaoEvento) producao;
            // item 3.19.1
            return ( p.getTipoRegiao() == null || p.getTipoRegiao().getId() == TipoRegiao.REGIONAL ) 
            		&& p.getNatureza().equals('E');
        }
        
		return false;
	}
	
}