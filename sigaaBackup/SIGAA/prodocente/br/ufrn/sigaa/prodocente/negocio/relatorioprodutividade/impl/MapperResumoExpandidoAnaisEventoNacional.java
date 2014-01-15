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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 30
 * Descrição = Trabalho apresentado ou resumo expandido publicado em eventos científicos ou artístico-culturais nacionais
 * @author Jean Guerethes 
 *
 */
public class MapperResumoExpandidoAnaisEventoNacional
		extends AbstractMapperProducaoIntelectual<Producao> {

	public MapperResumoExpandidoAnaisEventoNacional() {
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
            	   p.getNatureza() != null && p.getNatureza().equals('E')
            	   && (p.getTipoEvento() != null &&
                		  (p.getTipoEvento().getId() == TipoEvento.CONFERENCIA ||
                			p.getTipoEvento().getId() == TipoEvento.PAINEL ||
                			p.getTipoEvento().getId() == TipoEvento.CONGRESSO));
        }
        
		return false;
	}
	
}