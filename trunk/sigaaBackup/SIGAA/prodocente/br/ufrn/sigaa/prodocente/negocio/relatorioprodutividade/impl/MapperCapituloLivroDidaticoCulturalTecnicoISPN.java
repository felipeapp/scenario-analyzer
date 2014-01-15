/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 28, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 18
 * Descri��o = Cap�tulo de livro did�tico, cultural ou t�cnico (na �rea de atividadde acad�mica do docente) com ISPN
 * 
 * @author Victor Hugo
 */
public class MapperCapituloLivroDidaticoCulturalTecnicoISPN extends AbstractMapperProducaoIntelectual<Capitulo> {

	public MapperCapituloLivroDidaticoCulturalTecnicoISPN() {
		super(Capitulo.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Capitulo c) {
		return c.getTipoParticipacao() != null && (c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO ||
                c.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTROS_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTRA_PUBLICACAO_EVENTO ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_MONTAGEM ||
                c.getTipoParticipacao().getId() != TipoParticipacao.COLABORADOR_PROGRAMACAO_VISUAL ||
                c.getTipoParticipacao().getId() != TipoParticipacao.OUTRO_AUDIO_VISUAL ||

                c.getTipoParticipacao().getId() != TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
                c.getTipoParticipacao().getId() != TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS);
	}
	
}