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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 160
 * Descri��o = Organiza��o de livro did�tico, cultural ou t�cnico (na �rea de atividade acad�mica do docente) internacional
 * @author Victor Hugo
 *
 */
public class MapperOrganizacaoLivroDidaticoCulturalTecnicoInternacionalISBN
		extends AbstractMapperProducaoIntelectual<Livro> {

	public MapperOrganizacaoLivroDidaticoCulturalTecnicoInternacionalISBN() {
		super(Livro.class);
	}
	
	/**
	 * item 8.51(item que não pertence ao relat�rio padr�o, servindo como subconjunto do item 3.8)
	 */
	@Override
	protected boolean isAtendeCriterios(Livro l) {
		return l.getTipoParticipacao() != null &&
   	 l.getTipoParticipacao().getId() == TipoParticipacao.ORGANIZADOR_LIVRO &&
	 l.getTipoRegiao() != null &&
	 l.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL ;
	}
	
}