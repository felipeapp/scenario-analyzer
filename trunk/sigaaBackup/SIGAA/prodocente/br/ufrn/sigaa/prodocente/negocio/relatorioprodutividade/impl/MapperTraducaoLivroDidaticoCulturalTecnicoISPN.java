/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 28, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Date;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 17
 * Descrição = Tradução de livro didático, cultural ou técnico(na área de atividade acadêmica do docente) com ISPN
 * @author Victor Hugo
 *
 */
public class MapperTraducaoLivroDidaticoCulturalTecnicoISPN extends AbstractMapperProducaoIntelectual<Livro>{

	public MapperTraducaoLivroDidaticoCulturalTecnicoISPN() {
		super(Livro.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Livro l) {
		return l.getTipoParticipacao() != null &&
		(l.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_LIVRO ||
                l.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS);
	}
	
	@Override
	public Date getDataReferencia(Livro l) {
		return l.getDataPublicacao();
	}

}