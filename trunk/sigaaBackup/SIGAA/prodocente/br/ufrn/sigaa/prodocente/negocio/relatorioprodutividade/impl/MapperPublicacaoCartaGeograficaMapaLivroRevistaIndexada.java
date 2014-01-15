/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 43
 * Descrição = Publicação de Cartas Geográficas, mapa ou similar, em livros ou revistas indexadas
 * @author Victor Hugo
 *
 */
public class MapperPublicacaoCartaGeograficaMapaLivroRevistaIndexada extends
		AbstractMapperProducaoIntelectual<ProducaoTecnologica> {

	public MapperPublicacaoCartaGeograficaMapaLivroRevistaIndexada() {
		super(ProducaoTecnologica.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ProducaoTecnologica p) {
		return p.getTipoProducaoTecnologica().getId() != TipoProducaoTecnologica.SOFTWARE
        && p.getTipoProducao().getId() == TipoProducao.MAQUETES_PROTOTIPOS_OUTROS.getId();
	}
	
}