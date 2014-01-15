/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Date;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 27
 * Descrição = Artigo de divulgação publicado em revistas ou jornais
 * @author Victor Hugo
 *
 */
public class MapperArtigoDivulgacaoPublicadoRevistaJornal extends
		AbstractMapperProducaoIntelectual<Artigo> {

	public MapperArtigoDivulgacaoPublicadoRevistaJornal() {
		super(Artigo.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Artigo a) {
		return a.getTipoPeriodico() != null && (a.getTipoPeriodico().getId() == TipoPeriodico.REVISTA_NAO_CIENTIFICA ||
	       a.getTipoPeriodico().getId() == TipoPeriodico.JORNAL_NAO_CIENTIFICO);
	}

	@Override
	public Date getDataReferencia(Artigo a) {
		return a.getDataPublicacao();
	}
	
}
