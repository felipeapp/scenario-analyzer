/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Enum utilizado para manter os idiomas utilizado na internacionalização de documentos da instituição.
 * 
 * @author Rafael Gomes
 */
public enum IdiomasEnum {
	
	
	/**
	 * Status da inscrição do participante.
	 */
	PORTUGUES("pt"), INGLES("en"), FRANCES("fr"), ESPANHOL("es");

	/**
	 * Define o valor do status.
	 */
	private String id;

	/**
	 * Define o nome para visualização do status.
	 */
	private String denominacao;
	
	/** Mapa com todos possíveis status de uma inscrição. */
	public static final Map<String, String> descricaoIdiomas = new HashMap<String, String>(){{
		put(PORTUGUES.getId(), "Português");
		put(INGLES.getId(), "Inglês");
		put(FRANCES.getId(), "Francês");
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
	 * Retorna a coleção de todos os idiomas ativos neste enum.
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
