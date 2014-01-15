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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 28
 * Descrição = Tradução de Artigo didático, cultural, artístico ou técnico (na área de atividade do docente)
 * @author Victor Hugo
 *
 */
public class MapperTraducaoArtigoDidaticoCulturalArtisticoTecnicoAreaDocente
		extends AbstractMapperProducaoIntelectual<Artigo> {

	public MapperTraducaoArtigoDidaticoCulturalArtisticoTecnicoAreaDocente() {
		super(Artigo.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Artigo a) {
		return a.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS;
	}

	@Override
	public Date getDataReferencia(Artigo a) {
		return a.getDataPublicacao();
	}
	
}