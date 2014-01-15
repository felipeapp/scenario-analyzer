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
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 20
 * Descrição = Organização de livro didático, cultural ou técnico(na área de atividade acadêmica do docente) com ISPN
 * @author Victor Hugo
 */
public class MapperOrganizacaoLivroDidaticoCulturalTecnicoISPN extends
		AbstractMapperProducaoIntelectual<Livro> {

	public MapperOrganizacaoLivroDidaticoCulturalTecnicoISPN() {
		super(Livro.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Livro l) {
		return l.getTipoParticipacao() != null && l.getTipoParticipacao().getId() == TipoParticipacao.EDITOR_LIVRO;
	}
	
	@Override
	public Date getDataReferencia(Livro l) {
		return l.getDataPublicacao();
	}

}
