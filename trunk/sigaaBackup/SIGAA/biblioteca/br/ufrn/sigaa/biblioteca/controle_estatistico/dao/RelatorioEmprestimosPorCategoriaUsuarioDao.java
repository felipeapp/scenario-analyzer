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

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p> Dao exclusivo para a consulta do  relatório <strong> Relatório de Empréstimos por Categoria de Usuário </strong></p>
 * 
 * @author jadson
 *
 */
public class RelatorioEmprestimosPorCategoriaUsuarioDao extends GenericSigaaDAO{

	
	
	/**
	 * Método que retorna a quantidade de empréstimos + renovações separados por categorias de usuário e por meses num dado ano.
	 * 
	 * 
	 * @param idsBibliotecasEscolhidas  caso seja nulo ou de tamanho zero busca todas
	 * @param idsCatagoriaUsuarioEscolhidas filtra a categoria do usuário
	 * @param ano
	 * 
	 * @author Bráulio Bezerra
	 * @version 1.1 alterado (tarefa 22881)
	 * @version 1.2 Bráulio: corrigido para levar em consideração as renovações e somente os registros ativos
	 * @version 2.0 Jadson (tarefa 58611) adicionando a posibilidade de selecionar várias bibliotecas e várias categorias do usuário.
	 *         corrigindo o erro de essa consulta estava mostrado resultados diferente o mesmo período se o relatório fosse emitido em
	 *         datas diferentes (tarefa 60024).
	 */
	public Map<VinculoUsuarioBiblioteca, Integer[]> countEmprestimosERenovacoesPorCategoriaUsuario(
			Collection<Integer> idsBibliotecasEscolhidas, Collection<Integer> idsCatagoriaUsuarioEscolhidas, int ano ) throws DAOException {
		
		Map<VinculoUsuarioBiblioteca, Integer[]> resultado = new HashMap<VinculoUsuarioBiblioteca, Integer[]>();
		
		for ( Integer valorVinculo : idsCatagoriaUsuarioEscolhidas ) {
			resultado.put( VinculoUsuarioBiblioteca.getVinculo(valorVinculo), new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0} ); // Aloca espaço para os dados 13 zeros: 12 meses e mais o Total
		}
		
		Date dataInicio = CalendarUtils.configuraTempoDaData(CalendarUtils.createDate(1, 0, ano), 0, 0, 0, 0); // 1 de janeiro
		Date dataFim = CalendarUtils.configuraTempoDaData(CalendarUtils.createDate(31, 11, ano), 23, 59, 59, 999); // 31 de dezembro
		
		String sqlEmprestimos =
			"   SELECT (count(distinct e.id_emprestimo)) AS total, ub.vinculo AS categoria, EXTRACT(MONTH FROM e.data_emprestimo) AS mes " +
			"   FROM  biblioteca.emprestimo e " +
			"	INNER JOIN biblioteca.usuario_biblioteca     ub ON e.id_usuario_biblioteca      = ub.id_usuario_biblioteca ";
			
			if ( idsBibliotecasEscolhidas != null && idsBibliotecasEscolhidas.size() > 0 ) {	
				sqlEmprestimos+= "	INNER JOIN biblioteca.material_informacional mi ON mi.id_material_informacional = e.id_material " +
				"	INNER JOIN biblioteca.biblioteca             b  ON b.id_biblioteca              = mi.id_biblioteca ";
			}
			
			sqlEmprestimos+= "  WHERE e.ativo = trueValue() " + // Não foram estornados
			"	AND ( e.data_emprestimo between :inicio AND :fim ) " +  // conta pela data do empréstimo, isso não muda 
			"   AND ub.vinculo IS NOT NULL "; // Só os usuário que tem a informação do vúnculo, todos que fizeram empréstimos tem
			
	
		    
		
		if ( idsBibliotecasEscolhidas != null && idsBibliotecasEscolhidas.size() > 0 ) {	
			sqlEmprestimos += " AND b.id_biblioteca in " + UFRNUtils.gerarStringIn( idsBibliotecasEscolhidas )+ " ";	
		}

		if ( idsCatagoriaUsuarioEscolhidas != null && idsCatagoriaUsuarioEscolhidas.size() > 0 ) {	
			sqlEmprestimos += " AND ub.vinculo in " + UFRNUtils.gerarStringIn( idsCatagoriaUsuarioEscolhidas )+ " ";	
		}
		
		sqlEmprestimos +=
			"GROUP BY mes, ub.vinculo \n" +
			"ORDER BY ub.vinculo ASC, mes ASC";
		
		
		
		/*
		 * *************************************** 
		 * Conta as prorrogações 
		 * *************************************** 
		 */
		
		String sqlProrrogacoes =
			"   SELECT (count(distinct pe.id_prorrogacao_emprestimo)) AS total, ub.vinculo AS categoria, EXTRACT(MONTH FROM pe.data_cadastro) AS mes " +
			"   FROM  biblioteca.prorrogacao_emprestimo pe " +
			"   INNER JOIN biblioteca.emprestimo e ON e.id_emprestimo = pe.id_emprestimo " +
			"	INNER JOIN biblioteca.usuario_biblioteca     ub ON e.id_usuario_biblioteca  = ub.id_usuario_biblioteca ";
			
			if ( idsBibliotecasEscolhidas != null && idsBibliotecasEscolhidas.size() > 0 ) {	
				sqlProrrogacoes+= "	INNER JOIN biblioteca.material_informacional mi ON mi.id_material_informacional = e.id_material " +
				"	INNER JOIN biblioteca.biblioteca             b  ON b.id_biblioteca              = mi.id_biblioteca ";
			}
		
			sqlProrrogacoes+= "  WHERE e.ativo = trueValue() AND pe.tipo = " + TipoProrrogacaoEmprestimo.RENOVACAO + // Somente as renovações
			"	AND ( pe.data_cadastro between :inicio AND :fim ) " +                      // a data do cadastro da renovação não muda
			"   AND ub.vinculo IS NOT NULL ";            // Só os usuário que tem a informação do vúnculo, todos que fizeram empréstimos tem
		
			if ( idsBibliotecasEscolhidas != null && idsBibliotecasEscolhidas.size() > 0 ) {	
				sqlProrrogacoes += " AND b.id_biblioteca in " + UFRNUtils.gerarStringIn( idsBibliotecasEscolhidas )+ " ";	
			}

			if ( idsCatagoriaUsuarioEscolhidas != null && idsCatagoriaUsuarioEscolhidas.size() > 0 ) {	
				sqlProrrogacoes += " AND ub.vinculo in " + UFRNUtils.gerarStringIn( idsCatagoriaUsuarioEscolhidas )+ " ";	
			}
			
			sqlProrrogacoes +=
				"GROUP BY mes, ub.vinculo \n" +
				"ORDER BY ub.vinculo ASC, mes ASC";
		
			
			
		// Executa consulta empréstimos
		Query q = getSession().createSQLQuery( sqlEmprestimos );
		q.setTimestamp("inicio", dataInicio);
		q.setTimestamp("fim", dataFim);
		
		for ( Object linha : q.list() ) {
			Object[] values = (Object[])linha;
			int total     = ((BigInteger)values[0]).intValue();
			Integer vinculo = (Integer)values[1];
			int mes = ((Double) values[2]).intValue();
			
			resultado.get(VinculoUsuarioBiblioteca.getVinculo(vinculo))[mes - 1] = resultado.get(VinculoUsuarioBiblioteca.getVinculo(vinculo))[mes - 1] + total;
		}
		
		// Executa consulta prorrogações
		Query q2 = getSession().createSQLQuery( sqlProrrogacoes );
		q2.setTimestamp("inicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0));
		q2.setTimestamp("fim", CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999) );
		
		for ( Object linha : q2.list() ) {
			Object[] values = (Object[])linha;
			int total     = ((BigInteger)values[0]).intValue();
			Integer vinculo = (Integer)values[1];
			int mes = ((Double) values[2]).intValue();
			
			resultado.get(VinculoUsuarioBiblioteca.getVinculo(vinculo))[mes - 1] = resultado.get(VinculoUsuarioBiblioteca.getVinculo(vinculo))[mes - 1] + total;
		}
		
		/*
		 * calcula os totais por tipo de usuário e coloca na posição 12 de cada linha
		 */
		for ( Integer[] t : resultado.values() ) {
			int soma = 0;
			for ( int i = 0; i < 12; i++ )
				soma += t[i];
			t[12] = soma;
		}
		
		return resultado;
	}
	
}
