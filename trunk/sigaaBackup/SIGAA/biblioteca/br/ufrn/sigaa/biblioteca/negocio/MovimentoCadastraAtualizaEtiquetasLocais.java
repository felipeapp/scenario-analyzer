/*
 * MovimentoCadastraAtualizaEtiquetasLocais.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;

/**
 *
 *    Movimento que passa os dados para o processador de cadastro de etiquetas locais 
 *
 * @author Jadson
 * @since 26/06/2009
 * @version 1.0 Criação da classe
 *
 */
public class MovimentoCadastraAtualizaEtiquetasLocais extends AbstractMovimentoAdapter{

	private Etiqueta etiqueta;

	/** Indica se está atualizando o campo. */
	private boolean atualizando;
	
	/** Indica se está removendo o campo */
	private boolean removendo;
	
	/** Indica se o usuário tem permissão para alterar apenas campos locais. */
	private boolean campoLocal = true;   
	
	/**
	 * Construtor para o cadastro ou atualização.
	 * 
	 * @param etiqueta
	 * @param atualizando
	 */
	public MovimentoCadastraAtualizaEtiquetasLocais(Etiqueta etiqueta, boolean atualizando, boolean campoLocal) {
		super();
		this.etiqueta = etiqueta;
		this.atualizando = atualizando;
		this.campoLocal = campoLocal;
	}
	
	/**
	 *   Construtor para remoção. Vai atualizar a etiqueta colocando ativo para false mas
	 * mas não precisa validar dos dados.
	 * 
	 * @param removendo
	 * @param etiqueta
	 */
	public MovimentoCadastraAtualizaEtiquetasLocais(boolean removendo, Etiqueta etiqueta) {
		super();
		this.etiqueta = etiqueta;
		this.removendo = removendo;
	} 
	
	
	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public boolean isAtualizando() {
		return atualizando;
	}

	public boolean isRemovendo() {
		return removendo;
	}

	public boolean isCampoLocal() {
		return campoLocal;
	}
	
	
	
}
