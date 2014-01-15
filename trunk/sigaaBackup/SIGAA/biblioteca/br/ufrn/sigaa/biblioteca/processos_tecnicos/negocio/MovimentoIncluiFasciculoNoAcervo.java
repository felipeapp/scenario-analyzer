/*
 * MovimentoIncluiFasciculoNoAcervo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 * Passa os dados para o processador que inclui um novo fascículo no acervo
 *
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoIncluiFasciculoNoAcervo extends AbstractMovimentoAdapter{

	private Fasciculo fasciculo;
	private Assinatura assinatura;
	private TituloCatalografico titulo;
	//private int idLivro = 0;
	
	
	/**
	 * Construtor padrao
	 * @param fasciculo
	 * @param assinatura
	 */
	public MovimentoIncluiFasciculoNoAcervo(Fasciculo fasciculo, Assinatura assinatura, TituloCatalografico titulo) {
		this.fasciculo = fasciculo;
		this.assinatura = assinatura;
		this.titulo = titulo;
	}
	
	
//	/**
//	 * @param fasciculo
//	 * @param assinatura
//	 */
//	public MovimentoIncluiFasciculoNoAcervo(Fasciculo fasciculo, Assinatura assinatura, TituloCatalografico titulo, int idLivro) {
//		this.fasciculo = fasciculo;
//		this.assinatura = assinatura;
//		this.titulo = titulo;
//		this.idLivro = idLivro;
//	}
	
	
	public Fasciculo getFasciculo() {
		return fasciculo;
	}
	public Assinatura getAssinatura() {
		return assinatura;
	}

	public TituloCatalografico getTitulo() {
		return titulo;
	}

//	public int getIdLivro() {
//		return idLivro;
//	}
//	
	
}
