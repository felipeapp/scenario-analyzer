/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/03/2011
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Enumeração que serve como as possíveis tela que a proposta pode está. 
 * 
 * @author Jean Guerethes
 */
public enum TipoPassoProjetoNovoPesquisador {

	TELA_DADOS_GERAIS("DADOS GERAIS"),
	TELA_CRONOGRAMA_PROJETO("CRONOGRAMA DO PROJETO"),
	TELA_ORCAMENTO_PROJETO("ORÇAMENTO DO PROJETO"),
	TELA_RESUMO_PROJETO("RESUMO DO PROJETO");
	

	/** Descrição do tipo de passo da proposta Lato Sensu */
	private String label;
	/** Descrição do Enum */
	private int valor;
	/** Lista que armazena todo os Tipos de é passos da proposta Curso Lato Sensu */
	private List<TipoPassoProjetoNovoPesquisador> passosProjetoNovosPesquisadores = new ArrayList<TipoPassoProjetoNovoPesquisador>();

	/** Retorna o valor do ordinal informado */
	public static TipoPassoProjetoNovoPesquisador valueOf(int ordinal) {
		return values()[ordinal];
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
	private TipoPassoProjetoNovoPesquisador() {
		label = name();
	}
	
	private TipoPassoProjetoNovoPesquisador(String label){
		this.label = label;
	}
	
	public String label() {
		return label;
	}
	
	/** Adiciona todos os passos do cadastro da proposta */
	public Collection<TipoPassoProjetoNovoPesquisador> getAll(){	
		
		passosProjetoNovosPesquisadores.clear();
		passosProjetoNovosPesquisadores.add(TELA_DADOS_GERAIS);
		passosProjetoNovosPesquisadores.add(TELA_CRONOGRAMA_PROJETO);
		passosProjetoNovosPesquisadores.add(TELA_ORCAMENTO_PROJETO);
		passosProjetoNovosPesquisadores.add(TELA_RESUMO_PROJETO);
		return passosProjetoNovosPesquisadores; 

	}
	
}