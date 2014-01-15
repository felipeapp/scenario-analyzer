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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 35
 * Descrição = Editoração de revistas científicas e culturais nacional
 * @author Victor Hugo
 *
 */
public class MapperEditoracaoRevistaCientificaCulturalNacional extends
		AbstractMapperProducaoIntelectual<ParticipacaoComissaoOrgEventos> {

	public MapperEditoracaoRevistaCientificaCulturalNacional() {
		super(ParticipacaoComissaoOrgEventos.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ParticipacaoComissaoOrgEventos p) {
		return p.getAmbito().getId() == TipoRegiao.NACIONAL &&
    	p.getTipoParticipacaoOrganizacao() != null &&
    	p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.EDITOR_PERIODICOS_JORNAIS_SIMILARES;
	}
	
}