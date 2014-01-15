/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Enum utilizado para manter os idiomas utilizado na internacionaliza��o de documentos da institui��o.
 * 
 * @author Rafael Gomes
 */
public enum IdiomasEnum {
	
	
	/**
	 * Status da inscri��o do participante.
	 */
	PORTUGUES("pt"), INGLES("en"), FRANCES("fr"), ESPANHOL("es");

	/**
	 * Define o valor do status.
	 */
	private String id;

	/**
	 * Define o nome para visualiza��o do status.
	 */
	private String denominacao;
	
	/** Mapa com todos poss�veis status de uma inscri��o. */
	public static final Map<String, String> descricaoIdiomas = new HashMap<String, String>(){{
		put(PORTUGUES.getId(), "Portugu�s");
		put(INGLES.getId(), "Ingl�s");
		put(FRANCES.getId(), "Franc�s");
		put(ESPANHOL.getId(), "Espanhol");
	}};

	private IdiomasEnum(String id) {
		this.id = id;
	}
	
	private IdiomasEnum(String id, String denominacao) {
		this.id = id;
		this.denominacao = denominacao;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	/**
	 * Retorna o status por extenso.
	 * @return
	 */
	public static Map<String, String> getDescricaoIdiomas() {
		return descricaoIdiomas;
	}
	
	/**
	 * Retorna a cole��o de todos os idiomas ativos neste enum.
	 * @return
	 */
	public static Collection<String> getAll(){
		Collection<String> lista = new ArrayList<String>();
		Set<String> chaves = descricaoIdiomas.keySet();  
		for (String chave : chaves){
			if(chave != null){   
				lista.add(chave);
			}	
		}
		return lista;
	}
	

}
