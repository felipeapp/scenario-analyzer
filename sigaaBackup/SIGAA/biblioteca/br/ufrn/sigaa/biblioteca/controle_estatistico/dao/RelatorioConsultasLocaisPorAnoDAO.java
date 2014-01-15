/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>DAO utilizado exclusivamente para o relatório de consultas locais por turno. </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioConsultasLocaisPorAnoDAO extends GenericSigaaDAO {


	/**
	 * <p>Método que retorna um map contendo a contagem de todas as consultas locais a
	 * separados por mês e classe CDU/Black, das biblioteca passadas. Meses começam em 1.
	 */
	public Map<Integer, Map<String, Integer>> findConsultas(
			Collection<Integer> bibliotecas, int ano, FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException {
		
		String sql =
				"SELECT \n" +
				"\t EXTRACT(MONTH FROM rcdm.data_consulta) AS mes, \n" +
				"\t COALESCE( c." + classificacao.getColunaClassificacao() + ", 'Sem' ) AS classe, \n" +
				"\t SUM(c.quantidade) AS quantidade \n" +
				"FROM \n" +
				"\t biblioteca.registro_consultas_diarias_materiais AS rcdm \n" +
				"\t INNER JOIN biblioteca.classe_material_consultado AS c USING (id_registro_consultas_diarias_materiais) \n";
		
		sql +=
				"WHERE \n" +
				"\t c.ativo = trueValue() \n" +
				"\t AND c." + classificacao.getColunaClassificacao() + " IS NOT NULL \n" +
				"\t AND extract(year from rcdm.data_consulta) = " + ano + " \n";
		
		if ( bibliotecas != null && !!! bibliotecas.isEmpty() ) {
			sql += "\t AND rcdm.id_biblioteca IN (" + StringUtils.join(bibliotecas, ", ") + ") \n";
		}
		
		sql +=
				"GROUP BY mes, c." + classificacao.getColunaClassificacao() + " \n" +
				"ORDER BY mes, c." + classificacao.getColunaClassificacao() + " ";

		Query q = getSession().createSQLQuery( sql );
		
		@SuppressWarnings("unchecked")
		List <Object[]> rs = q.list();
		
		return ArrayUtils.agrupar( rs, Integer.class, String.class, Integer.class );
	}

}
