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
 * <p>Dao Exclusivo para os empréstimos por unidade </p>
 *
 * 
 * @author jadson
 *
 */
public class RelatorioEmprestimosPorUnidadeDao extends GenericSigaaDAO {

	
	/**
	 * Retorna a quantidade de empréstimos feitos pelos servidores de cada uma das unidades da instituição.
	 */
	public List< Object[] > findQtdEmprestimosByUnidade(Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Integer tipoEmprestimo, Integer  tipoMaterial, VinculoUsuarioBiblioteca vinculo )
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
		if ( vinculo != null )
			sqlCategoriaUsuario = "	AND ub.vinculo = " + vinculo + " ";
		
		
		String sql =
	//		"SELECT consultaInterna.nome, sum(consultaInterna.total) as totalGeral, consultaInterna.id_unidade FROM "+
	//		"("+ 
				// conta os empréstimos
				"( SELECT " +
				"	un.nome, " +
				"	count(DISTINCT e.id_emprestimo) AS total, " +
				"	un.id_unidade, " +
				"	0 as totalRenovacoes " +
				"   FROM biblioteca.emprestimo AS e " +
				"	INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo     = e.id_politica_emprestimo " +
				"	INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
				"	INNER JOIN biblioteca.material_informacional mi ON mi.id_material_informacional = e.id_material " +
				"	INNER JOIN rh.servidor                       sv ON ( ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosServidor()) + " AND ub.identificacao_vinculo = sv.id_servidor )" +
				"   INNER JOIN  comum.unidade                     un ON sv.id_unidade                = un.id_unidade " +
				"	INNER JOIN biblioteca.biblioteca             b  ON b.id_biblioteca              = mi.id_biblioteca " +
				"   WHERE " +
				"	e.ativo = trueValue() " +
					sqlBiblioteca +
					"	AND ( e.data_emprestimo BETWEEN :inicioPeriodo AND :fimPeriodo ) "+
					sqlTipoEmprestimo +
					sqlTipoMaterial +
					sqlCategoriaUsuario +
				"   GROUP BY un.id_unidade, un.nome " +
				"   ORDER BY total DESC ) " //+
                ;
		
				///// Conta as renovações /////
//				" UNION ALL ( "+
//					" SELECT "+
//					"	un.nome, " +
//					"	count(e.id_emprestimo) AS total, " +
//					"	un.id_unidade " +
//					" FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
//					" INNER JOIN biblioteca.emprestimo             e  ON e.id_emprestimo = pro.id_emprestimo "+
//					" INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo     = e.id_politica_emprestimo " +
//					" INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
//					" INNER JOIN biblioteca.material_informacional AS mi ON e.id_material = mi.id_material_informacional"+ 
//					" INNER JOIN rh.servidor                       sv ON ( ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosServidor()) + " AND ub.identificacao_vinculo = sv.id_servidor )" +
//					" LEFT JOIN  comum.unidade                     un ON sv.id_unidade                = un.id_unidade " +
//					" INNER JOIN biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+ 
//					" WHERE e.ativo = trueValue() AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+
//					sqlBiblioteca +
//					"	AND ( pro.data_cadastro BETWEEN " + RelatoriosBibliotecaDao.formataIntervalo(inicioPeriodo, fimPeriodo) + " ) " +
//					sqlTipoEmprestimo +
//					sqlTipoMaterial +
//					sqlCategoriaUsuario +
//					" GROUP BY un.id_unidade, un.nome " +
//					" ORDER BY total DESC" +
//				")"+ 
//			")as consultaInterna"+
			
//			" GROUP BY nome, id_unidade "+
//			" ORDER BY totalGeral DESC ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
	
	/**
	 * Retorna a quantidade de renovações feitos pelos servidores de cada uma das unidades da instituição.
	 */
	public List< Object[] > findQtdRenovacoesByUnidade(Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Integer tipoEmprestimo, Integer  tipoMaterial, VinculoUsuarioBiblioteca vinculo )
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
		if ( vinculo != null )
			sqlCategoriaUsuario = "	AND ub.vinculo = " + vinculo + " ";
		
		
		String sql =
			//"SELECT consultaInterna.nome, sum(consultaInterna.total) as totalGeral, consultaInterna.id_unidade FROM "+
			//"("+ 
					" SELECT "+
					"	un.nome, " +
					"	count(DISTINCT pro.id_prorrogacao_emprestimo) AS total, " +
					"	un.id_unidade " +
					" FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
					" INNER JOIN biblioteca.emprestimo             e  ON e.id_emprestimo = pro.id_emprestimo "+
					" INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo     = e.id_politica_emprestimo " +
					" INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
					" INNER JOIN biblioteca.material_informacional AS mi ON e.id_material = mi.id_material_informacional"+ 
					" INNER JOIN rh.servidor                       sv ON ( ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosServidor()) + " AND ub.identificacao_vinculo = sv.id_servidor )" +
					" INNER JOIN  comum.unidade                     un ON sv.id_unidade                = un.id_unidade " +
					" INNER JOIN biblioteca.biblioteca AS b ON mi.id_biblioteca = b.id_biblioteca "+ 
					" WHERE e.ativo = trueValue() AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+
					sqlBiblioteca +
					" AND ( pro.data_cadastro BETWEEN :inicioPeriodo AND :fimPeriodo ) " +
					sqlTipoEmprestimo +
					sqlTipoMaterial +
					sqlCategoriaUsuario +
					" GROUP BY un.id_unidade, un.nome " +
					" ORDER BY total DESC" //+
					;
		//		")"+ 
		//	")as consultaInterna"+
			
		//	" GROUP BY nome, id_unidade "+
		//	" ORDER BY totalGeral DESC ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		
		return lista;
	}
	
}
