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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Dao exclusivo para o relatório de mesmo nome </p>
 * 
 * @author jadson
 *
 */
public class RelatorioEmprestimosPorCursoDao extends GenericSigaaDAO{

	
	/**
	 * Retorna a quantidade de empréstimos feitos pelos discentes de cada curso da instituição.
	 */
	public List< Object[] > findQtdEmprestimosByCurso(Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Integer tipoEmprestimo, Integer tipoMaterial, VinculoUsuarioBiblioteca vinculoUsuario )
			throws DAOException {
		
	
		
		String sqlBiblioteca = "";
		if ( bibliotecas != null && ! bibliotecas.isEmpty() )
			sqlBiblioteca = "	AND mi.id_biblioteca IN ( " + StringUtils.join(bibliotecas, ", ") + " ) ";
		
		
		String sqlTipoEmprestimo = "";
		if ( tipoEmprestimo != null && tipoEmprestimo > 0 )
			sqlTipoEmprestimo = "	AND pe.id_tipo_emprestimo = " + tipoEmprestimo + " ";
		
		String sqlTipoMaterial = "";
		if ( tipoMaterial != null && tipoMaterial > 0 )
			sqlTipoMaterial = "	AND mi.id_tipo_material = " + tipoMaterial + " ";
		
		String sqlCategoriaUsuario = "";
		if ( vinculoUsuario != null )
			sqlCategoriaUsuario = "	AND ub.vinculo = " + vinculoUsuario + " ";
		
		String sql =
//			"SELECT consultaInterna.nome, sum(consultaInterna.total) as totalGeral, consultaInterna.id_curso FROM "+
//			"("+ 
				// conta os empréstimos
				"( SELECT " +
				"	cs.nome, " +
				"	count(e.id_emprestimo) AS total, " +
				"	cs.id_curso as id_curso, " +
				"	0 as totalRenovacoes" +
				"   FROM biblioteca.emprestimo AS e " +
				"	INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo     = e.id_politica_emprestimo " +
				"	INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
				"	INNER JOIN biblioteca.material_informacional mi ON mi.id_material_informacional = e.id_material " +
				"	INNER JOIN discente                          dc ON ( ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosAluno()) + " AND ub.identificacao_vinculo = dc.id_discente )" +
				"	INNER JOIN  curso                            cs ON dc.id_curso                  = cs.id_curso " +
				"	INNER JOIN biblioteca.biblioteca             b  ON b.id_biblioteca              = mi.id_biblioteca " +
				"   WHERE " +
				"	e.ativo = trueValue() " +
					sqlBiblioteca +
					"	AND ( e.data_emprestimo BETWEEN :inicioPeriodo AND :fimPeriodo ) "+
					sqlTipoEmprestimo +
					sqlTipoMaterial +
					sqlCategoriaUsuario +
				"   GROUP BY cs.id_curso, cs.nome, totalRenovacoes " +
				"   ORDER BY total DESC ) "; //+
			
//				///// Conta as renovações /////
//				" UNION ALL ( "+
//					" SELECT "+
//					"	cs.nome, " +
//					"	count(pro.id_prorrogacao_emprestimo) AS total, " +
//					"	cs.id_curso " +
//					" FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
//					" INNER JOIN biblioteca.emprestimo             e  ON e.id_emprestimo = pro.id_emprestimo"+
//					" INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo     = e.id_politica_emprestimo " +
//					" INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
//					" INNER JOIN biblioteca.material_informacional AS mi ON e.id_material = mi.id_material_informacional"+ 
//					" INNER JOIN discente                          dc ON ( ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosAluno()) + " AND ub.identificacao_vinculo = dc.id_discente )" +
//					" INNER JOIN  curso                            cs ON dc.id_curso                  = cs.id_curso " +
//					" INNER JOIN biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+ 
//					" WHERE e.ativo = trueValue() AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+
//					sqlBiblioteca +
//					"	AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) " +
//					sqlTipoEmprestimo +
//					sqlTipoMaterial +
//					sqlCategoriaUsuario +
//					"GROUP BY cs.id_curso, cs.nome " +
//					"ORDER BY total DESC" +
//				")"+ 
//			")as consultaInterna"+
			
//			" GROUP BY nome, id_curso "+
//			" ORDER BY totalGeral DESC ";
		
		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
	
	/**
	 * Retorna a quantidade de empréstimos feitos pelos discentes de cada curso da instituição.
	 */
	public List< Object[] > findQtdRenovacoesByCurso(Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Integer tipoEmprestimo, Integer tipoMaterial, VinculoUsuarioBiblioteca vinculoUsuario )
			throws DAOException {
		
	
		
		String sqlBiblioteca = "";
		if ( bibliotecas != null && ! bibliotecas.isEmpty() )
			sqlBiblioteca = "	AND mi.id_biblioteca IN ( " + StringUtils.join(bibliotecas, ", ") + " ) ";
		
		
		String sqlTipoEmprestimo = "";
		if ( tipoEmprestimo != null && tipoEmprestimo > 0 )
			sqlTipoEmprestimo = "	AND pe.id_tipo_emprestimo = " + tipoEmprestimo + " ";
		
		String sqlTipoMaterial = "";
		if ( tipoMaterial != null && tipoMaterial > 0 )
			sqlTipoMaterial = "	AND mi.id_tipo_material = " + tipoMaterial + " ";
		
		String sqlCategoriaUsuario = "";
		if ( vinculoUsuario != null )
			sqlCategoriaUsuario = "	AND ub.vinculo = " + vinculoUsuario + " ";
		
		String sql =
			//"SELECT consultaInterna.nome, sum(consultaInterna.total) as totalGeral, consultaInterna.id_curso FROM "+
			//"("+ 
				
					///// Conta as renovações /////
					" SELECT "+
					"	cs.nome, " +
					"	count(DISTINCT pro.id_prorrogacao_emprestimo) AS total, " +
					"	cs.id_curso " +
					"  FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
					" INNER JOIN biblioteca.emprestimo             e  ON e.id_emprestimo = pro.id_emprestimo"+
					" INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo     = e.id_politica_emprestimo " +
					" INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
					" INNER JOIN biblioteca.material_informacional AS mi ON e.id_material = mi.id_material_informacional"+ 
					" INNER JOIN discente                          dc ON ( ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosAluno()) + " AND ub.identificacao_vinculo = dc.id_discente )" +
					" INNER JOIN  curso                            cs ON dc.id_curso                  = cs.id_curso " +
					" INNER JOIN biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+ 
					" WHERE e.ativo = trueValue() AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+
					sqlBiblioteca +
					"	AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) " +
					sqlTipoEmprestimo +
					sqlTipoMaterial +
					sqlCategoriaUsuario +
					" GROUP BY cs.id_curso, cs.nome " +
					" ORDER BY total DESC" ; //+
			//")as consultaInterna"+
			
			//" GROUP BY nome, id_curso "+
			//" ORDER BY totalGeral DESC ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
}
