/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Date;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDidatico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoInstancia;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 21
 * Descri��o = Publica��o de texto did�tico com a aprova��o de Conselho Editorial ou de comiss�o constitu�da para esse fim
 * @author Victor Hugo
 *
 */
public class MapperPublicacaoTextoDidaticoAprovacaoConselhoOuComissao extends
		AbstractMapperProducaoIntelectual<TextoDidatico> {

	public MapperPublicacaoTextoDidaticoAprovacaoConselhoOuComissao() {
		super(TextoDidatico.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(TextoDidatico t) {
		return t.getTipoInstancia() != null && t.getTipoInstancia().getId() == TipoInstancia.CONSELHO_EDITORIAL;
	}
	
	@Override
	public Date getDataReferencia(TextoDidatico t) {
		return t.getDataPublicacao();
	}
	
}