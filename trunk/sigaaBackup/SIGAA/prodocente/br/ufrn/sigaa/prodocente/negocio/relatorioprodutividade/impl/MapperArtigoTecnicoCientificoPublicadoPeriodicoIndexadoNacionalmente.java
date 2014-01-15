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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 24
 * Descrição = Artigo técnico-científico publicado em periódico indexado nacionalmente
 * @author Victor Hugo
 *
 */
public class MapperArtigoTecnicoCientificoPublicadoPeriodicoIndexadoNacionalmente
		extends AbstractMapperProducaoIntelectual<Artigo> {

	public MapperArtigoTecnicoCientificoPublicadoPeriodicoIndexadoNacionalmente() {
		super(Artigo.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Artigo a) {
		return a.getTipoRegiao() != null && 
		a.getTipoRegiao().getId() == TipoRegiao.NACIONAL &&
		a.getTipoPeriodico().getId() != TipoPeriodico.ANAIS &&
 	   (a.getTipoPeriodico() == null ||
               (a.getTipoPeriodico().getId() != TipoPeriodico.REVISTA_NAO_CIENTIFICA &&
               a.getTipoPeriodico().getId() != TipoPeriodico.JORNAL_NAO_CIENTIFICO));
	}
	
	@Override
	public Date getDataReferencia(Artigo a) {
		return a.getDataPublicacao();
	}
	
}