/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Os tipos serviços que uma biblioteca pode oferecer na seção de informações e referência.</p>
 * @author jadson
 *
 */
public enum TipoServicoInformacaoReferencia {

	
	/** Serviço de normalização de documentos, como teses, defesas e livros. */
	NORMALIZACAO(0, "Normalização"),
	/** Serviço de catalogação de produções internas da instituição (geração de ficha catalográfica de uma obra). */
	CATALOGACAO_NA_FONTE(1, "Catalogação na Fonte"),
	/** Serviço de orientação para normalização. */
	ORIENTACAO_NORMALIZACAO(2, "Orientação de Normalização"),
	/** Serviço de levantamento bibliográfico sobre um determinado assunto. */
	LEVANTAMENTO_BIBLIOGRAFICO(3, "Levantamento Bibliográfico"),
	/** Serviço de levantamento da infra-estrutura de uma biblioteca. */
	LEVANTAMENTO_INFRA_ESTRUTURA(4, "Levantamento de Infra-Estrutura");
	
	/**
	 * O valor numérico do item da enumeração
	 */
	private int valor;
	/**
	 * A descrição do item da enumeração
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
	 * Retorna o tipod e serviço do valor.
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
	 * <p>Retorna os tipos de serviços existente em informação e referência.</p>
	 * 
	 * <p> <strong>OBSERVAÇÃO:</strong> Por enquanto o sistema não está trabalhando com lavantamento bibliográfico e de infra estrutura.</p>
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
