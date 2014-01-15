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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 37
 * Descrição = Participação em Conselho Editorial Internacional
 * @author Victor Hugo
 *
 */
public class MapperParticipacaoConselhoEditorialInternacional extends
		AbstractMapperProducaoIntelectual<ParticipacaoComissaoOrgEventos> {

	public MapperParticipacaoConselhoEditorialInternacional() {
		super(ParticipacaoComissaoOrgEventos.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ParticipacaoComissaoOrgEventos p) {
		return p.getAmbito().getId() == TipoRegiao.INTERNACIONAL &&
    	p.getTipoParticipacaoOrganizacao() != null &&
        p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.MEMBRO_CONSELHO_EDITORIAL;
	}
	
}