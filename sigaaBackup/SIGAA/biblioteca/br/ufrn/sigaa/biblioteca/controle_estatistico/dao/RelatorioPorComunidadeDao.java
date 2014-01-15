/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Dao com a consulta do relatório por comunidade. </p>
 * 
 * @author jadson
 *
 */
public class RelatorioPorComunidadeDao extends GenericSigaaDAO{

	
	/**
	 * <p>Gera um relatório quantitativo dos empréstimos realizados pelas comunidades interna e externa,
	 * tendo como parâmetro o período inicial e final. Renovações também são levadas em conta. </p>
	 * 
	 * <p>Obs.: consulta demora um pouco para um período > 2 anos. de  30s a 40s </p>
	 * 
	 * @version 1.1 Bráulio: corrigido para levar em consideração somente as prorrogações do tipo Renovação
	 *          1.2 Separando em duas consultas, uma para empréstimos e outras para as renovações
	 * @throws DAOException 
	 * @throws  
	 */
	public List<Object[]> findEmprestimoComunidade(Date inicioPeriodo, Date fimPeriodo) throws DAOException {
		
		String sqlconsulta =
			
			"SELECT consultaInterna.descricaoBiblioteca, sum(consultaInterna.qtdInterna) as qtdInterna, sum(consultaInterna.qtdExterna) as qtdExterna " +
			"FROM ("+ 
				" SELECT "+
				" b.descricao as descricaoBiblioteca, "+
				" COUNT (DISTINCT emp.id_emprestimo) as qtdInterna, " +
				" 0 AS qtdExterna "+	
				" FROM biblioteca.emprestimo AS emp "+
				" INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional "+
				" INNER JOIN  biblioteca.usuario_biblioteca usu ON emp.id_usuario_biblioteca = usu.id_usuario_biblioteca 	"+	 
				" INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+
				" WHERE emp.ativo = trueValue()  "+ 
				" AND usu.vinculo IN "+ UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.COMUNIDADE_INTERNA)+
				" AND ( emp.data_emprestimo BETWEEN :inicioPeriodo AND :fimPeriodo )  "+
				" GROUP BY b.descricao  "+
				
				" UNION ALL ( "+
					" SELECT "+
					" b.descricao as descricaoBiblioteca, "+
					" 0 AS qtdInterna, " +
					" COUNT (DISTINCT emp.id_emprestimo) AS qtdExterna "+
				    " FROM biblioteca.emprestimo AS emp "+  
					" INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional "+
					" INNER JOIN  biblioteca.usuario_biblioteca usu ON emp.id_usuario_biblioteca = usu.id_usuario_biblioteca 	"+	 
					" INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+
					" WHERE emp.ativo = trueValue()  "+ 
					" AND usu.vinculo IN "+ UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.COMUNIDADE_EXTERNA)+
					" AND ( emp.data_emprestimo BETWEEN :inicioPeriodo AND :fimPeriodo )  "+
					" GROUP BY b.descricao  "+
					
				" ) "+ // UNION
				" UNION ALL ( "+
					" SELECT "+
					" b.descricao as descricaoBiblioteca,"+ 
					" COUNT (DISTINCT pro.id_prorrogacao_emprestimo ) as qtdInterna, " +
					" 0 AS qtdExterna "+	
					" FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
					" INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"+
					" INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"+ 
					" INNER JOIN  biblioteca.usuario_biblioteca usu ON emp.id_usuario_biblioteca = usu.id_usuario_biblioteca"+ 		
					" INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+ 
					" WHERE emp.ativo = trueValue()  " + 
					" AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+
					" AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "+
					" AND usu.vinculo IN "+ UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.COMUNIDADE_INTERNA)+
					" GROUP BY b.descricao "+
				")"+ 
				" UNION ALL ( "+
					" SELECT "+
					" b.descricao as descricaoBiblioteca,"+ 
					" 0 as qtdInterna, " +
					" COUNT (DISTINCT pro.id_prorrogacao_emprestimo ) as qtdExterna " +
					" FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
					" INNER JOIN biblioteca.emprestimo AS emp  ON emp.id_emprestimo = pro.id_emprestimo"+
					" INNER JOIN biblioteca.material_informacional AS mi ON emp.id_material = mi.id_material_informacional"+ 
					" INNER JOIN  biblioteca.usuario_biblioteca usu ON emp.id_usuario_biblioteca = usu.id_usuario_biblioteca"+ 		
					" INNER JOIN  biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+ 
					" WHERE emp.ativo = trueValue()  " + 
					" AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+
					" AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) "+
					" AND usu.vinculo IN "+ UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.COMUNIDADE_EXTERNA)+
					" GROUP BY b.descricao "+
				" ) "+ // UNION
				
				
			") as consultaInterna"+
			" GROUP BY consultaInterna.descricaoBiblioteca"+
			" ORDER BY consultaInterna.descricaoBiblioteca";

		
		Query q = getSession().createSQLQuery(sqlconsulta);
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
	}
	
}
