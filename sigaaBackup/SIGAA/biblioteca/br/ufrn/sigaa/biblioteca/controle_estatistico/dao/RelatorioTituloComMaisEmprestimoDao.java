/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/08/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 *
 * <p>Dao específico para o relatório de títulos com mais empréstimos. </p>
 * 
 * @author jadson
 *
 */
public class RelatorioTituloComMaisEmprestimoDao extends  GenericSigaaDAO{

	/**
	 * Retorna os títulos mais emprestados no período passado.
	 *
	 * @param idBiblioteca
	 * @param dataInicio null se não for utilizado
	 * @param dataFim null se não for utilizado
	 * @param limite o limite de títulos retornados
	 * @return mapeamento entre id do título catalográfico e a quantidade de vezes que ele foi emprestado
	 */
	public SortedMap<Integer, Number[]> findTitulosComMaisEmprestimos( Collection<Integer> bibliotecas,
			Date dataInicio, Date dataFim, int tipoEmprestimo, int limite )
			throws DAOException {
		
		String sql =
				"SELECT " +
				"	tit.id_titulo_catalografico, " +
				"	count(*) AS total, " +
				"	cem.quantidade_materiais_ativos_titulo " +
				"FROM " +
				"	           biblioteca.emprestimo             AS emp " +
				"	INNER JOIN biblioteca.politica_emprestimo    AS pol USING (id_politica_emprestimo) " +
				"	INNER JOIN biblioteca.material_informacional AS mat ON emp.id_material = mat.id_material_informacional " +
				"	LEFT  JOIN biblioteca.exemplar               AS exe ON mat.id_material_informacional = exe.id_exemplar " +
				"	LEFT  JOIN biblioteca.fasciculo              AS fas ON emp.id_material = fas.id_fasciculo " +
				"	LEFT  JOIN biblioteca.assinatura             AS ass USING (id_assinatura) " +
				"	INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico ) " +
				"	INNER JOIN biblioteca.cache_entidades_marc   AS cem ON cem.id_titulo_catalografico = tit.id_titulo_catalografico " +
				"WHERE " +
				"	emp.ativo = :true AND tit.ativo = :true ";
		
		if ( tipoEmprestimo > 0 )
			sql += "	AND pol.id_tipo_emprestimo = " + tipoEmprestimo + "  ";

		// filtra por biblioteca
		if ( bibliotecas != null && bibliotecas.size() > 0  ) {
			sql += "	AND mat.id_biblioteca in " + UFRNUtils.gerarStringIn(bibliotecas) + " \n";
		}

		// filtra por período
		if ( dataInicio != null && dataFim != null ) {
			sql += "	AND emp.data_emprestimo BETWEEN '" +
					CalendarUtils.format(dataInicio, "yyyy-MM-dd") + " 00:00:00' AND '" +
					CalendarUtils.format(dataFim,    "yyyy-MM-dd") + " 23:59:59'  ";
		}

		sql +=
				"GROUP BY tit.id_titulo_catalografico, cem.quantidade_materiais_ativos_titulo " +
				"ORDER BY total DESC " +
				BDUtils.limit(limite);

		Query q = getSession().createSQLQuery(sql);
		q.setBoolean("true", true);
		
		TreeMap<Integer, Number[]> r = new TreeMap<Integer, Number[]>();

		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();

		int idTituloCatalografico;;
		int total;
		int qtdMateriais;
		double mediaEmprestimos;
		
		for ( Object[] linha : linhas ) {
			
			idTituloCatalografico = (Integer)linha[0];
			total = ((BigInteger)linha[1]).intValue();
			qtdMateriais = (Integer) linha[2];
			mediaEmprestimos = ((double) total) / ((double) qtdMateriais);
			
			r.put(idTituloCatalografico, new Number[] { total, qtdMateriais, mediaEmprestimos });
		}
		
		return r;
	}
	
}
