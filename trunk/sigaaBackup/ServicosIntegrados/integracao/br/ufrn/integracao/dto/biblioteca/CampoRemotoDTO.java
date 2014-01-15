/*
 * CampoRemotoDTO.java
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
package br.ufrn.integracao.dto.biblioteca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *      Classe que transfere as informacoes de um titulo para o SIPAC
 *
 * @author jadson
 * @since 09/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class CampoRemotoDTO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7943887484589966832L;
	
	
	public String cabecalho;  // cabecalho é etique + idicadores dos campos
	public List<Dados> dados = new ArrayList<Dados>();
	
	
	public CampoRemotoDTO(String cabecalho){
		this.cabecalho = cabecalho;
	}
	
	public CampoRemotoDTO(String cabecalho, String dados){
		this.cabecalho = cabecalho;
		this.dados.add(new Dados(dados));
	}
	
	
	public void addDado(String dados){
		this.dados.add(new Dados(dados));
	}
	
	public String getCabecalho() {
		return cabecalho;
	}
	public List<Dados> getDados() {
		return dados;
	}
	
	public class Dados implements Serializable{
		public String dado;

		public Dados(String dado){
			this.dado = dado;
		}
		
		public String getDado() {
			return dado;
		}
	}
	
}
