/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.List;
import java.util.Stack;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.arq.dao.Database;

/**
 * Lista utilizada para armazenar ids de estruturas curriculares cujas cargas
 * horárias serão recalculadas.
 * 
 * @author David Pereira
 * 
 */
public class ListaEstruturasCalcular {

	// array de estruturas curriculares
	static Stack<Integer> estruturas = new Stack<Integer>();

	public static int totalEstruturas = 0;

	public static int totalProcessadas = 0;

	public static synchronized boolean possuiEstruturas() {
		return estruturas.size() > 0;
	}

	// entrega a próxima estrutura curricular para ser processada
	public static synchronized Integer getProximaEstrutura() {
		return estruturas.pop();
	}

	public static synchronized void registraProcessada() {
		totalProcessadas++;
	}

	/**
	 * Carrega as estruturas a serem pré-processadas
	 */
	public static void carregarEstruturas() {
		estruturas.clear();
		
		List<Integer> estruturasBuscadas = new JdbcTemplate(Database.getInstance().getSigaaDs()).queryForList("select id_curriculo from graduacao.curriculo where id_matriz in (select id_matriz_curricular from graduacao.matriz_curricular where ativo = trueValue())", Integer.class);
		for ( Integer e : estruturasBuscadas )
			estruturas.add(e);

		totalProcessadas = 0;
		totalEstruturas = estruturas.size();
	}

}