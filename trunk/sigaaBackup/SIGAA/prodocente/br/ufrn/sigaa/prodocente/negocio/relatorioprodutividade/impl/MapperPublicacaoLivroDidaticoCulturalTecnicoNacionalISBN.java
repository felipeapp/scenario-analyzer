/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 1, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 155
 * Descri��o = Publica��o de livro did�tico, cultural ou t�cnico (na �rea de atividade acad�mica do docente)�nacional com ISBN
 * @author Victor Hugo
 *
 */
public class MapperPublicacaoLivroDidaticoCulturalTecnicoNacionalISBN extends
		AbstractMapperProducaoIntelectual<Livro> {

	public MapperPublicacaoLivroDidaticoCulturalTecnicoNacionalISBN() {
		super(Livro.class);
	}
	
	/**
	 * item 8.46 (utilizado para tipos de consultas de tipo de regi�o especificas, ou seja subconjunto do item 3.4)
     * Participa��es como EQUIPE adicionadas em 05/07/2007 ap�s conversa com prof. Bernardete
	 */
	@Override
	protected boolean isAtendeCriterios(Livro l) {
		return l.getTipoParticipacao() != null &&
		(l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_LIVRO ||
				l.getTipoParticipacao().getId() == TipoParticipacao.TRABALHO_INDIVIDUAL_ARTIGO_PERIODICOS_JORNAIS ||
				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_ARTIGO_PERIODICOS_JORNAIS ||
				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_COLABORADOR_LIVRO ||
				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_ARTIGO_PERIODICOS_JORNAIS ||
				l.getTipoParticipacao().getId() == TipoParticipacao.EQUIPE_RESPONSAVEL_LIVRO)
			&& l.getTipoRegiao() != null && l.getTipoRegiao().getId() == TipoRegiao.NACIONAL;
	}
	
}