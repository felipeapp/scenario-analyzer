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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Dao co a consulta exclusiva para o relatório de mesmo nome </p>
 *
 * 
 * @author jadson
 *
 */
public class RelatorioUsuariosComMaisEmprestimosDao extends GenericSigaaDAO{

	
	/**
	 * Retorna os usuários com mais empréstimos + renovações, obedecendo aos filtros passados como parâmetros.
	 *
	 * @param maxUsuarios o máximo de usuários retornados pela consulta.
	 * @return os usuários com mais empréstimos + renovações, ordenados do maior para o menor número de empréstimos
	 */
	public List< Object[] > findUsuariosComMaisEmprestimosERenovacoes(int maxUsuarios, VinculoUsuarioBiblioteca vinculo,
			Collection<Integer> bibliotecas, int idCurso, Date periodoDe, Date periodoAte, int tipoEmprestimo, int tipoDeMaterial )throws DAOException {
		
		List< Object[] > resultado = new ArrayList< Object[] >();
		
		String sql =
				" SELECT " +
				"	p.nome as nome, " +
				"	COUNT( * ) as total_emprestimos," +
				"	COALESCE(dc.matricula, sv.siape) AS matricula_ou_siape," +
				"	COALESCE(cs.nome, un.nome) AS curso_ou_unidade ";
		
		String from =
				"FROM biblioteca.emprestimo e " +
				"	INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
				"	INNER JOIN comum.pessoa                      p  ON p.id_pessoa                  = ub.id_pessoa " +
				"	INNER JOIN biblioteca.material_informacional mi ON mi.id_material_informacional = e.id_material " +
				"	INNER JOIN biblioteca.biblioteca             b  ON b.id_biblioteca              = mi.id_biblioteca " +
				"	INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo    = e.id_politica_emprestimo " +
				"	LEFT JOIN  discente                          dc ON ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosAluno())+" AND ub.identificacao_vinculo = dc.id_discente " +
				"	LEFT JOIN  rh.servidor                       sv ON ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosServidor())+" AND ub.identificacao_vinculo = sv.id_servidor  " +
				"	LEFT JOIN  curso                             cs ON dc.id_curso                  = cs.id_curso " +
				"	LEFT JOIN  comum.unidade                     un ON sv.id_unidade                = un.id_unidade ";
	
		String from2 =
			" FROM biblioteca.prorrogacao_emprestimo AS pro 	"+
			"	INNER JOIN biblioteca.emprestimo              e  ON e.id_emprestimo = pro.id_emprestimo "+
			"	INNER JOIN biblioteca.usuario_biblioteca     ub ON ub.id_usuario_biblioteca     = e.id_usuario_biblioteca " +
			"	INNER JOIN comum.pessoa                      p  ON p.id_pessoa                  = ub.id_pessoa " +
			"	INNER JOIN biblioteca.material_informacional mi ON mi.id_material_informacional = e.id_material " +
			"	INNER JOIN biblioteca.biblioteca             b  ON b.id_biblioteca              = mi.id_biblioteca " +
			"	INNER JOIN biblioteca.politica_emprestimo    pe ON pe.id_politica_emprestimo    = e.id_politica_emprestimo " +
			"	LEFT JOIN  discente                          dc ON ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosAluno())+" AND ub.identificacao_vinculo = dc.id_discente " +
			"	LEFT JOIN  rh.servidor                       sv ON ub.vinculo IN " +UFRNUtils.gerarStringIn(VinculoUsuarioBiblioteca.getVinculosServidor())+" AND ub.identificacao_vinculo = sv.id_servidor  " +
			"	LEFT JOIN  curso                             cs ON dc.id_curso                  = cs.id_curso " +
			"	LEFT JOIN  comum.unidade                     un ON sv.id_unidade                = un.id_unidade ";
		
		String where = " WHERE e.ativo = trueValue() "; // recupera os não extornados
		
		
		String where2 = " WHERE e.ativo = trueValue() AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO ; // recupera os não extornados
		
		// filtragem por curso
		if ( idCurso != 0 ){
			from +="INNER JOIN discente disc ON ( disc.id_discente = e.identificacao_usuario AND disc.id_curso = " + idCurso + ") ";
			from2 +="INNER JOIN discente disc ON ( disc.id_discente = e.identificacao_usuario AND disc.id_curso = " + idCurso + ") ";
		}
		
		// filtragem por biblioteca
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ){
			where += "	AND b.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ";
			where2 += "	AND b.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ";
		}
			
		// filtragem por categoria de usuário
		if ( vinculo != null ){
			where += "	AND ub.vinculo = " + vinculo + " ";
			where2 += "	AND ub.vinculo = " + vinculo + " ";
		}
		
		// filtragem por tipo de empréstimo
		if ( tipoEmprestimo > 0 ){
			where += "	AND pe.id_tipo_emprestimo = " + tipoEmprestimo + " ";
			where2 += "	AND pe.id_tipo_emprestimo = " + tipoEmprestimo + " ";
		}
			
		// filtragem por tipo de material
		if ( tipoDeMaterial > 0 ){
			where += "	AND mi.id_tipo_material = " + tipoDeMaterial + " ";
			where2 += "	AND mi.id_tipo_material = " + tipoDeMaterial + " ";
		}
		
		// filtragem por período
		where += "AND ( e.data_emprestimo BETWEEN "+ RelatoriosBibliotecaDao.formataIntervalo(periodoDe, periodoAte)+" )" ;
		where2 += "	AND ( pro.data_cadastro BETWEEN " + RelatoriosBibliotecaDao.formataIntervalo(periodoDe, periodoAte) + " ) ";
		
		// Empréstimos
		String sqlEmps =
			"SELECT consultaInterna.nome as nome, sum(consultaInterna.total_emprestimos) as totalGeral, consultaInterna.matricula_ou_siape, consultaInterna.curso_ou_unidade FROM "+
			"("+ 
				"( "+sql +
				from +
				where +
				"GROUP BY " +
				"	p.id_pessoa, " +
				"	p.nome, " +
				"	COALESCE(dc.matricula, sv.siape), " +
				"	COALESCE(cs.nome, un.nome) " +
				"ORDER BY total_emprestimos DESC, p.nome ASC " +
				
				" )"+
				" UNION ALL  ( "+
					//  Informações das renovações 
					sql+
					from2+
					where2+
					"GROUP BY " +
					"	p.id_pessoa, " +
					"	p.nome, " +
					"	COALESCE(dc.matricula, sv.siape), " +
					"	COALESCE(cs.nome, un.nome) " +
					"ORDER BY total_emprestimos DESC, p.nome ASC " +
					
				") "+
			") as consultaInterna "+

		" GROUP BY nome, matricula_ou_siape, curso_ou_unidade "+
		" ORDER BY totalGeral DESC "+
		BDUtils.limit(maxUsuarios);
		
		Query q = getSession().createSQLQuery( sqlEmps );
		
		// coloca os resultados em 'resultado'
		for ( Object res : q.list() ) {
			Object[] coluna = (Object[])res;
			resultado.add( new Object[] { coluna[0], coluna[1], coluna[2], coluna[3] } );
		}

		return resultado;
	}
	
}
