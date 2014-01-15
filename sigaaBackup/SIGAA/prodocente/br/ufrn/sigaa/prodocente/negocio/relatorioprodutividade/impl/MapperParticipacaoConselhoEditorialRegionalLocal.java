/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacaoOrganizacaoEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 39
 * Descrição = Participação em Conselho Editorial Regional ou Local
 * @author Victor Hugo
 *
 */
public class MapperParticipacaoConselhoEditorialRegionalLocal extends
		AbstractMapperProducaoIntelectual<ParticipacaoComissaoOrgEventos> {

	public MapperParticipacaoConselhoEditorialRegionalLocal() {
		super(ParticipacaoComissaoOrgEventos.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ParticipacaoComissaoOrgEventos p) {
		return (p.getAmbito().getId() == TipoRegiao.REGIONAL || p.getAmbito().getId() == TipoRegiao.LOCAL) &&
		p.getTipoParticipacaoOrganizacao() != null 
		&& p.getTipoParticipacaoOrganizacao().getId() ==TipoParticipacaoOrganizacaoEventos.MEMBRO_CONSELHO_EDITORIAL;
	}
	
}