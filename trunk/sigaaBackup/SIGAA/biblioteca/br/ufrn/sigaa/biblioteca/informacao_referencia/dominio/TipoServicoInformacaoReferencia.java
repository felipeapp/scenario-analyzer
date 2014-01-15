/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * <p> Os tipos servi�os que uma biblioteca pode oferecer na se��o de informa��es e refer�ncia.</p>
 * @author jadson
 *
 */
public enum TipoServicoInformacaoReferencia {

	
	/** Servi�o de normaliza��o de documentos, como teses, defesas e livros. */
	NORMALIZACAO(0, "Normaliza��o"),
	/** Servi�o de cataloga��o de produ��es internas da institui��o (gera��o de ficha catalogr�fica de uma obra). */
	CATALOGACAO_NA_FONTE(1, "Cataloga��o na Fonte"),
	/** Servi�o de orienta��o para normaliza��o. */
	ORIENTACAO_NORMALIZACAO(2, "Orienta��o de Normaliza��o"),
	/** Servi�o de levantamento bibliogr�fico sobre um determinado assunto. */
	LEVANTAMENTO_BIBLIOGRAFICO(3, "Levantamento Bibliogr�fico"),
	/** Servi�o de levantamento da infra-estrutura de uma biblioteca. */
	LEVANTAMENTO_INFRA_ESTRUTURA(4, "Levantamento de Infra-Estrutura");
	
	/**
	 * O valor num�rico do item da enumera��o
	 */
	private int valor;
	/**
	 * A descri��o do item da enumera��o
	 */
	private String descricao;
	
	private TipoServicoInformacaoReferencia(int valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}
	
	public int getValor() {
		return valor;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public String getNome() {
		return this.name();
	}

	@Override
	public String toString() {
		return String.valueOf(this.valor);
	}
	
	/**
	 * Retorna o tipod e servi�o do valor.
	 * @param status
	 * @return
	 */
	public static TipoServicoInformacaoReferencia getTipoServico(Integer valorTipoServico) {
		if(valorTipoServico == NORMALIZACAO.valor)
			return  NORMALIZACAO;
		if(valorTipoServico == CATALOGACAO_NA_FONTE.valor)
			return  CATALOGACAO_NA_FONTE;
		if(valorTipoServico == ORIENTACAO_NORMALIZACAO.valor)
			return  ORIENTACAO_NORMALIZACAO;
		if(valorTipoServico == LEVANTAMENTO_BIBLIOGRAFICO.valor)
			return  LEVANTAMENTO_BIBLIOGRAFICO;
		if(valorTipoServico == LEVANTAMENTO_INFRA_ESTRUTURA.valor)
			return  LEVANTAMENTO_INFRA_ESTRUTURA;
		return null;
	}
	
	/**
	 * <p>Retorna os tipos de servi�os existente em informa��o e refer�ncia.</p>
	 * 
	 * <p> <strong>OBSERVA��O:</strong> Por enquanto o sistema n�o est� trabalhando com lavantamento bibliogr�fico e de infra estrutura.</p>
	 *
	 * @return
	 */
	public static List<TipoServicoInformacaoReferencia> getTipoServicoInformacaoReferencia(){
		
		List<TipoServicoInformacaoReferencia> lista = new ArrayList<TipoServicoInformacaoReferencia>();
		lista.add(TipoServicoInformacaoReferencia.NORMALIZACAO);
		lista.add(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE);
		lista.add(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO);
		return lista;
	}
}
