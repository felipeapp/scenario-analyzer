/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */

package br.ufrn.sigaa.ensino.tecnico.dominio;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe de objeto responsável por armazenar informações a respeito 
 * do resultado de uma convocação de aprovação de Processo Seletivo.
 * 
 * @author Rafael Gomes
 * @author Fred_Castro
 *
 */
public class ResultadoPessoaConvocacaoTecnico {
	
	/** Referência ao objeto Pessoa utilizado na listagem de resultado da convocação do processo seletivo*/
	private Pessoa pessoa;
	/** Relação das listas de erro dos dados pessoais do candidato. */
	private ListaMensagens listaMensagens;
	/** Matriz curricular referente a opção de aprovação do candidato.*/
	private MatrizCurricular matriz;

	
	
	/**
	 * Minimal Constructor
	 */
	public ResultadoPessoaConvocacaoTecnico() {
		super();
	}

	/**
	 * Constructor
	 * @param pessoa
	 * @param listaMensagens
	 */
	public ResultadoPessoaConvocacaoTecnico(Pessoa pessoa, ListaMensagens listaMensagens) {
		super();
		this.pessoa = pessoa;
		this.listaMensagens = listaMensagens;
	}

	/**
	 * @return the pessoa
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/**
	 * @param pessoa the pessoa to set
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/**
	 * @return the listaMensagens
	 */
	public ListaMensagens getListaMensagens() {
		return listaMensagens;
	}

	/**
	 * @param listaMensagens the listaMensagens to set
	 */
	public void setListaMensagens(ListaMensagens listaMensagens) {
		this.listaMensagens = listaMensagens;
	}

	/**
	 * @return the matriz
	 */
	public MatrizCurricular getMatriz() {
		return matriz;
	}

	/**
	 * @param matriz the matriz to set
	 */
	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}
	
	
}
