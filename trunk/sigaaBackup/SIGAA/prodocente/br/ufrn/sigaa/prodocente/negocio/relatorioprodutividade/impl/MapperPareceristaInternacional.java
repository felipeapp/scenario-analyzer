/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 1, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacaoOrganizacaoEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 173
 * Descrição = Parecerista (internacional)
 * @author Victor Hugo
 *
 */
public class MapperPareceristaInternacional extends
		AbstractMapperProducaoIntelectual<ParticipacaoComissaoOrgEventos> {

	public MapperPareceristaInternacional() {
		super(ParticipacaoComissaoOrgEventos.class);
	}
	
	/**
	 * item 8.60
	 */
	@Override
	protected boolean isAtendeCriterios(ParticipacaoComissaoOrgEventos p) {
		return p.getAmbito() != null
		&& p.getAmbito().getId() == TipoRegiao.INTERNACIONAL &&
		p.getTipoParticipacaoOrganizacao() != null &&
		(p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.REVISOR_PERIODICOS_JORNAIS_SIMILARES
				|| p.getTipoParticipacaoOrganizacao().getId() == TipoParticipacaoOrganizacaoEventos.CONSULTOR_AD_HOC_REVISTA_CIENTIFICA);
	}
	
}