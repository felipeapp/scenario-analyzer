/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.sigaa.ensino.util;

import java.util.Hashtable;

import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;

/**
 * Cache de informa��es sobre os discentes. Utilizado, por exemplo,
 * para a busca das datas de entrada e sa�da de um aluno, que s�o 
 * informa��es importantes para a busca de das equival�ncias de um
 * componente para um discente. 
 * 
 * @author David Pereira
 *
 */
public enum RepositorioInformacoesCalculoDiscente {

	/** 
	 * Inst�ncia �nica do reposit�rio. Implementa��o do singleton utilizando enums no Java 6, 
	 * conforme recomenda��o do livro Effective Java.
	 */
	INSTANCE;
	
	/** Hashtable que armazena o cache das informa��es dos discentes */
	private static Hashtable<Integer, DadosCalculosDiscente> infoDiscente = new Hashtable<Integer, DadosCalculosDiscente>();
	
	
	/**
	 * Busca as informa��es do discente com base no id passado como par�metro.
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
	 * Adiciona as informa��es do discente passadas como par�metro ao cache.
	 * @param idDiscente
	 * @param equivalencia
	 */
	public synchronized void adicionarInformacoes(Integer idDiscente, DadosCalculosDiscente dados) {
		infoDiscente.put(idDiscente, dados);
	}
	
}
