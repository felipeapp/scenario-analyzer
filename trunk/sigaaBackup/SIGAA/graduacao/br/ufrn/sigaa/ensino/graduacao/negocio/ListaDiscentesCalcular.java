/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.List;
import java.util.Stack;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Lista utilizada para armazenar ids de discentes cujos
 * cálculos serão realizados
 * 
 * @author David Pereira
 * 
 */
public class ListaDiscentesCalcular {

	// array de discentes
	static Stack<Integer> discentes = new Stack<Integer>();

	public static int totalDiscentes = 0;

	public static int totalProcessados = 0;

	public static synchronized boolean possuiDiscentes() {
		return discentes.size() > 0;
	}

	// entrega o próximo discente para ser processado
	public static synchronized Integer getProximoDiscente() {
		return discentes.pop();
	}

	public static synchronized void registraProcessado() {
		totalProcessados++;
	}

	/**
	 * Carrega as estruturas a serem pré-processadas
	 */
	public static void carregarDiscentes(String sqlRestricao) {
		discentes.clear();
		
		if (ValidatorUtil.isEmpty(sqlRestricao)) {
			sqlRestricao = " ultima_atualizacao_totais is null";
		}
		
		
		String sql = "select id_discente_graduacao " 
				+ " from graduacao.discente_graduacao dg " 
				+ " join discente d on (d.id_discente = dg.id_discente_graduacao) " 
				+ " where d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo())
				+ " and d.tipo <> " + Discente.ESPECIAL
				+ " and " + sqlRestricao;
		
		List<Integer> discentesBuscados = new JdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql, Integer.class);
		for ( Integer e : discentesBuscados )
			discentes.add(e);

		totalProcessados = 0;
		totalDiscentes = discentes.size();
	}

	public static void carregarDiscentes() {
		carregarDiscentes(null);
	}

}