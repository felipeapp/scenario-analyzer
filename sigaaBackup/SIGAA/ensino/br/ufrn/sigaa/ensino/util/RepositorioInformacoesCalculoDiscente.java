/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.sigaa.ensino.util;

import java.util.Hashtable;

import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;

/**
 * Cache de informações sobre os discentes. Utilizado, por exemplo,
 * para a busca das datas de entrada e saída de um aluno, que são 
 * informações importantes para a busca de das equivalências de um
 * componente para um discente. 
 * 
 * @author David Pereira
 *
 */
public enum RepositorioInformacoesCalculoDiscente {

	/** 
	 * Instância única do repositório. Implementação do singleton utilizando enums no Java 6, 
	 * conforme recomendação do livro Effective Java.
	 */
	INSTANCE;
	
	/** Hashtable que armazena o cache das informações dos discentes */
	private static Hashtable<Integer, DadosCalculosDiscente> infoDiscente = new Hashtable<Integer, DadosCalculosDiscente>();
	
	
	/**
	 * Busca as informações do discente com base no id passado como parâmetro.
	 * @param idDiscente
	 * @return
	 */
	public synchronized DadosCalculosDiscente buscarInformacoes(Integer idDiscente) {
		DadosCalculosDiscente dados = infoDiscente.get(idDiscente);
		
		if (dados == null) {
			DiscenteDao dao = new DiscenteDao();
			
			try {
				dados = dao.findDadosCalculosByDiscente(idDiscente);
			} finally {
				dao.close();
			}
			
			infoDiscente.put(idDiscente, dados);
		}
		
		return dados;
	}
	
	/**
	 * Adiciona as informações do discente passadas como parâmetro ao cache.
	 * @param idDiscente
	 * @param equivalencia
	 */
	public synchronized void adicionarInformacoes(Integer idDiscente, DadosCalculosDiscente dados) {
		infoDiscente.put(idDiscente, dados);
	}
	
}
