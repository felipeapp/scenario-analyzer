/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 1, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 157
 * Descrição = Capítulo de livro didático, cultural ou técnico (na área de atividade acadêmica do docente) nacional com ISBN
 * @author Victor Hugo
 *
 */
public class MapperCapituloLivroDidaticoCulturalTecnicoNacionalISBN extends
		AbstractMapperProducaoIntelectual<Capitulo> {

	public MapperCapituloLivroDidaticoCulturalTecnicoNacionalISBN() {
		super(Capitulo.class);
	}
	
	/**
	 * item 8.48(item especifico para produções nacionais, SUB CONJUNTO DO ITEM 3.6)
	 */
	@Override
	protected boolean isAtendeCriterios(Capitulo c) {
		return c.getTipoParticipacao() != null && ( c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO ||
                c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTROS_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTRA_PUBLICACAO_EVENTO ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_MONTAGEM ||
                c.getTipoParticipacao().getId() != TipoParticipacao.COLABORADOR_PROGRAMACAO_VISUAL ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_AUDIO_VISUAL ||

                c.getTipoParticipacao().getId() != TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS) &&
                c.getTipoRegiao().getId() == TipoRegiao.NACIONAL;
	}
	
}