/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 34
 * Descri��o = Editora��o de revistas cient�ficas e culturais internacionais
 * @author Victor Hugo
 *
 */
public class MapperEditoracaoRevistaCientificaCulturalInternacional extends
		AbstractMapperProducaoIntelectual<ParticipacaoComissaoOrgEventos> {

	public MapperEditoracaoRevistaCientificaCulturalInternacional() {
		super(ParticipacaoComissaoOrgEventos.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(ParticipacaoComissaoOrgEventos p) {
		return p.getAmbito().getId() == TipoRegiao.INTERNACIONAL && p.getTipoParticipacaoOrganizacao() != null &&
        p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.EDITOR_PERIODICOS_JORNAIS_SIMILARES;
	}
	
}