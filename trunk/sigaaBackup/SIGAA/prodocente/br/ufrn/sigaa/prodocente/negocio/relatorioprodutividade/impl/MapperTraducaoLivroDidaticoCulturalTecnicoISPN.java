/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * Descri��o = Tradu��o de livro did�tico, cultural ou t�cnico(na �rea de atividade acad�mica do docente) com ISPN
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