/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 27/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Dao exclusivo para o relatório de usuários que fizeram empréstimos em um períodio. </p>
 * 
 * @author jadson
 *
 */
public class RelatorioDeUsuariosComEmprestimosPorPeriodoDao extends GenericSigaaDAO{

	
	
	/**
	 * Retorna a quantidade de usuários que fizeram algum empréstimo no período passado, agrupados
	 * por tipo de usuário.
	 * 
	 * @param vinculoUsuario caso o operador deseja os empréstimos de apenas um categoria de usuários
	 */
	public Map<VinculoUsuarioBiblioteca, Integer> findQtdUsuariosComEmprestimos(Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo, VinculoUsuarioBiblioteca vinculoUsuario)
			throws DAOException {
		
		String sql =
				"SELECT ub.vinculo, count(DISTINCT ub.id_usuario_biblioteca) AS total " +
				"FROM biblioteca.usuario_biblioteca AS ub " +
			    "INNER JOIN biblioteca.emprestimo as empr ON ub.id_usuario_biblioteca = empr.id_usuario_biblioteca "+
				
				( bibliotecas != null && bibliotecas.isEmpty() == false ?
				"	INNER JOIN biblioteca.material_informacional AS matinf ON matinf.id_material_informacional = empr.id_material "
						+" AND matinf.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") " : "") +
				
				" WHERE " +
				"	empr.ativo = trueValue() AND ub.vinculo IS NOT NULL " +
				"	AND ( empr.data_emprestimo BETWEEN '" +CalendarUtils.format(inicioPeriodo, "yyyy-MM-dd") + " 00:00:00' AND '" +CalendarUtils.format(fimPeriodo, "yyyy-MM-dd") + " 23:59:59' ) " +
				
				(vinculoUsuario != null ?  "	AND ub.vinculo = "+ vinculoUsuario +" " : "") +
				"GROUP BY ub.vinculo ";
		
		Map<VinculoUsuarioBiblioteca, Integer> resultados = new HashMap<VinculoUsuarioBiblioteca, Integer>();
		
		Query q = getSession().createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for ( Object[] row : lista ) {
			resultados.put((VinculoUsuarioBiblioteca.getVinculo( (Integer) row[0])), ((BigInteger)row[1]).intValue());
		}
		
		return resultados;
	}

	
}
