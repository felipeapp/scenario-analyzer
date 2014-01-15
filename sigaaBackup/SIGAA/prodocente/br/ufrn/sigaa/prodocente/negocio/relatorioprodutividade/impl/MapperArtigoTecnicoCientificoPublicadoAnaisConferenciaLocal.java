/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 1, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 153
 * Descrição = Artigo técnico-científico publicado em anais de conferência local
 * @author Victor Hugo
 *
 */
public class MapperArtigoTecnicoCientificoPublicadoAnaisConferenciaLocal extends
		AbstractMapperProducaoIntelectual<PublicacaoEvento> {

	public MapperArtigoTecnicoCientificoPublicadoAnaisConferenciaLocal() {
		super(PublicacaoEvento.class);
	}
	
	/**
	 * item 8.44 (especificação de uma consulta já existente subtipo artístico especifico, subconjunto do item 3.13)
	 */
	@Override
	protected boolean isAtendeCriterios(PublicacaoEvento p) {
		return p.getTipoRegiao().getId() == TipoRegiao.LOCAL &&
        ( p.getTipoEvento() != null &&
            	(p.getTipoEvento().getId() == TipoEvento.CONFERENCIA ||
                p.getTipoEvento().getId() ==TipoEvento.SEMINARIO ||
                p.getTipoEvento().getId() ==TipoEvento.WORKSHOP ||
                p.getTipoEvento().getId() ==TipoEvento.CONGRESSO)) &&
                p.getNatureza().equals('T');
	}
	
}
