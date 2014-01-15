/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 01/09/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;

/**
 *
 * <p>Dao exclusivo para o relatório de renovações por módulo de acesso </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class RelatorioRenovacoesPorModuloAcessoDao extends RelatoriosBibliotecaDao{

	
	/**
	 * Retorna a quantidade de renovações por módulo de acesso (web, móvel ou desktop).
	 */
	public List<Object[]> findQtdRenovacoesPorModulo( Collection<Integer> bibliotecas, Date inicio, Date fim )
			throws DAOException {
		
		String sql =
				"SELECT " +
				"	CAST ( COALESCE( ren.canal, 'Desconhecido') as TEXT ) as canal_acesso, " +
				"	count( DISTINCT pro.id_prorrogacao_emprestimo ) " +
				"   FROM biblioteca.prorrogacao_emprestimo AS pro " +
				"	LEFT JOIN comum.registro_entrada AS ren ON ren.id_entrada = pro.id_registro_cadastro " +
				"	INNER JOIN biblioteca.emprestimo AS emp ON emp.id_emprestimo = pro.id_emprestimo " +
				"	INNER JOIN biblioteca.material_informacional AS mat ON mat.id_material_informacional = emp.id_material " +
				"   WHERE emp.ativo = trueValue() AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO
				+"  AND pro.data_cadastro BETWEEN :dataInicio AND :dataFim ";
		
		if ( bibliotecas != null && !!! bibliotecas.isEmpty() ){
			sql += " AND mat.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ";
		}
		
		sql += " GROUP BY canal_acesso ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("dataInicio",  CalendarUtils.configuraTempoDaData(inicio, 0, 0, 0, 0));
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fim, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
}
