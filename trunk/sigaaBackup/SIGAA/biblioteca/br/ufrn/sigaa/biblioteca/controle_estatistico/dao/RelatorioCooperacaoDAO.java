/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;

/**
 * <p>DAO exclusivo para o relatório Cooperação.</p>
 * 
 * @author felipe
 */
public class RelatorioCooperacaoDAO extends GenericSigaaDAO {

	/**
	 * Retorna a quantidade de títulos que foram importados e os que foram criados
	 * diretamente no sistema.
	 * 
	 * @return { importados, não importados }
	 */
	public List<Object[]> findTitulosCooperacao(Collection<Integer> idsPessoa, Collection<Integer> idsBiblioteca, 
			Date periodoInicial, Date periodoFinal)
			throws HibernateException, DAOException {

		String  idsBibliotecaString = " ";
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			idsBibliotecaString = "AND b.id_biblioteca IN (" + StringUtils.join(idsBiblioteca, ',') + ") ";
		
		
		String idsPessoaString = "";
		if(idsPessoa != null && idsPessoa.size() > 0)
			idsPessoaString =  " AND pessoa.id_pessoa IN (" + StringUtils.join(idsPessoa, ',') + ") ";
		
		StringBuilder sql = new StringBuilder(
			" SELECT SUM(qtd_titulos_importados) as qtdImportados, SUM(qtd_titulos_exportados) as qtdExportados, biblioteca , pessoa "+	
			" FROM ( " + 
			" SELECT 	 " +  
			"	 COUNT(DISTINCT t.id_titulo_catalografico) AS qtd_titulos_importados, " +
			"    0 as qtd_titulos_exportados, "+
			"    b.descricao as biblioteca, "+
			"    pessoa.nome as pessoa "+
			" FROM biblioteca.titulo_catalografico t 	 " + 
			"	 INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = t.id_biblioteca_importacao " + 
			"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = t.id_registro_criacao " + 
			"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
			"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
			" WHERE t.ativo = trueValue() 	 " + 
			"    AND t.catalogado = trueValue() 	 " + 
			idsBibliotecaString + 
			idsPessoaString +
			"	 AND t.importado = trueValue() " + 
			"	 AND t.data_criacao >= :periodoInicial " + 
			"	 AND t.data_criacao <= :periodoFinal " + 
			"    GROUP BY biblioteca, pessoa " +
			" UNION ALL (" + 
			" SELECT 	 " +  
			"    0 as qtd_titulos_importados, "+
			"	 COUNT(DISTINCT t.id_titulo_catalografico) AS qtd_titulos_exportados, " +  
			"    b.descricao as biblioteca, "+
			"    pessoa.nome as pessoa "+
			" FROM biblioteca.titulo_catalografico t 	 " + 
			"	 INNER JOIN biblioteca.registro_exportacao_cooperacao_tecnica rect ON rect.id_titulo_catalografico = t.id_titulo_catalografico " +
			"	 INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = rect.id_biblioteca " +
			"    INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = rect.id_registro_criacao " + 
			"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
			"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
			" WHERE t.ativo = trueValue() 	 " + 
			idsBibliotecaString+
			idsPessoaString +
			"	 AND rect.data_exportacao >= :periodoInicial " +  
			"	 AND rect.data_exportacao <= :periodoFinal "+
			"    GROUP BY biblioteca, pessoa " +
			" ) "+ // UNION
			" ) as interna "+
			" GROUP BY biblioteca, pessoa "+
			" ORDER BY biblioteca, pessoa ");
		
		
		Query q = getSession().createSQLQuery( sql.toString() );
		
		q.setTimestamp("periodoInicial", CalendarUtils.configuraTempoDaData(periodoInicial, 0, 0, 0, 0) );
		q.setTimestamp("periodoFinal", CalendarUtils.configuraTempoDaData(periodoFinal, 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		return list;
	}
	
	/**
	 * Retorna a quantidade de autoridades autor que foram importadas e as que foram criadas
	 * diretamente no sistema.
	 * 
	 * @return { importados, não importados }
	 */
	public List<Object[]> findAutoridadesAutorCooperacao (Collection<Integer> idsPessoa, Collection<Integer> idsBiblioteca, 
			Date periodoInicial, Date periodoFinal)
			throws HibernateException, DAOException {
		
		String  idsBibliotecaString = " ";
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			idsBibliotecaString = "AND b.id_biblioteca IN (" + StringUtils.join(idsBiblioteca, ',') + ") ";
		
		
		String idsPessoaString = "";
		if(idsPessoa != null && idsPessoa.size() > 0)
			idsPessoaString =  " AND pessoa.id_pessoa IN (" + StringUtils.join(idsPessoa, ',') + ") ";
		
		StringBuilder sql = new StringBuilder(
			" SELECT SUM(qtd_autoridades_importadas) as qtdImportados, SUM(qtd_autoridades_exportadas) as qtdExportados, biblioteca , pessoa "+	
			" FROM ( " + 
			" SELECT 	 " + 
			"    COUNT(DISTINCT aut.id_autoridade) AS qtd_autoridades_importadas, " +
			"    0 as qtd_autoridades_exportadas, "+
			"    b.descricao as biblioteca, "+
			"    pessoa.nome as pessoa "+
			" FROM biblioteca.autoridade aut 	 " + 
			"	 INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = aut.id_biblioteca_importacao " + 
			"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = aut.id_registro_criacao " + 
			"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
			"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
			" WHERE aut.ativo = trueValue() 	 " + 
			"    AND aut.catalogada = trueValue() 	 " + 
			"	 AND aut.tipo = :tipo " + 
			idsBibliotecaString + 
			idsPessoaString +
			"	 AND aut.importada = trueValue() " + 
			"	 AND aut.data_criacao >= :periodoInicial " + 
			"	 AND aut.data_criacao <= :periodoFinal " + 
			"    GROUP BY biblioteca, pessoa " +
			" UNION (" + 
			" SELECT 	 " +
			"    0 as qtd_autoridades_importadas, "+
			"	 COUNT(DISTINCT aut.id_autoridade) AS qtd_autoridades_exportadas, " +  
			"    b.descricao as biblioteca, "+
			"    pessoa.nome as pessoa "+
			" FROM biblioteca.autoridade aut 	 " + 
			"	 INNER JOIN biblioteca.registro_exportacao_cooperacao_tecnica rect ON rect.id_autoridade = aut.id_autoridade " +
			"	 INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = rect.id_biblioteca " +
			"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = rect.id_registro_criacao " + 
			"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
			"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
			" WHERE aut.ativo = trueValue() 	 " + 
			"	 AND aut.tipo = :tipo " + 
			idsBibliotecaString + 
			idsPessoaString +
			"	 AND rect.data_exportacao >= :periodoInicial " +  
			"	 AND rect.data_exportacao <= :periodoFinal "+
			"    GROUP BY biblioteca, pessoa " +
			" ) "+ // UNION
			" ) as interna "+
			" GROUP BY biblioteca, pessoa "+
			" ORDER BY biblioteca, pessoa ");
		
		Query q = getSession().createSQLQuery( sql.toString() );

		q.setInteger("tipo", Autoridade.TIPO_AUTOR);
		q.setTimestamp("periodoInicial", periodoInicial);
		q.setTimestamp("periodoFinal", periodoFinal);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		
		return list;
	}
	
	/**
	 * Retorna a quantidade de autoridades assunto que foram importadas e as que foram criadas
	 * diretamente no sistema.
	 * 
	 * @return { importados, não importados }
	 */
	public List<Object[]> findAutoridadesAssuntoCooperacao (Collection<Integer> idsPessoa, Collection<Integer> idsBiblioteca, 
			Date periodoInicial, Date periodoFinal)
			throws HibernateException, DAOException {
		
		String  idsBibliotecaString = " ";
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			idsBibliotecaString = "AND b.id_biblioteca IN (" + StringUtils.join(idsBiblioteca, ',') + ") ";
		
		
		String idsPessoaString = "";
		if(idsPessoa != null && idsPessoa.size() > 0)
			idsPessoaString =  " AND pessoa.id_pessoa IN (" + StringUtils.join(idsPessoa, ',') + ") ";
		
		StringBuilder sql = new StringBuilder(
				" SELECT SUM(qtd_autoridades_importadas) as qtdImportados, SUM(qtd_autoridades_exportadas) as qtdExportados, biblioteca , pessoa "+	
				" FROM ( " + 
				" SELECT 	 " + 
				"    COUNT(DISTINCT aut.id_autoridade) AS qtd_autoridades_importadas, " +
				"    0 as qtd_autoridades_exportadas, "+
				"    b.descricao as biblioteca, "+
				"    pessoa.nome as pessoa "+
				" FROM biblioteca.autoridade aut 	 " + 
				"	 INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = aut.id_biblioteca_importacao " + 
				"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = aut.id_registro_criacao " + 
				"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
				"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
				" WHERE aut.ativo = trueValue() 	 " + 
				"    AND aut.catalogada = trueValue() 	 " + 
				"	 AND aut.tipo = :tipo " + 
				idsBibliotecaString + 
				idsPessoaString +
				"	 AND aut.importada = trueValue() " + 
				"	 AND aut.data_criacao >= :periodoInicial " + 
				"	 AND aut.data_criacao <= :periodoFinal " + 
				"    GROUP BY biblioteca, pessoa " +
				" UNION (" + 
				" SELECT 	 " +
				"    0 as qtd_autoridades_importadas, "+
				"	 COUNT(DISTINCT aut.id_autoridade) AS qtd_autoridades_exportadas, " +  
				"    b.descricao as biblioteca, "+
				"    pessoa.nome as pessoa "+
				" FROM biblioteca.autoridade aut 	 " + 
				"	 INNER JOIN biblioteca.registro_exportacao_cooperacao_tecnica rect ON rect.id_autoridade = aut.id_autoridade " +
				"	 INNER JOIN biblioteca.biblioteca b on b.id_biblioteca = rect.id_biblioteca " +
				"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = rect.id_registro_criacao " + 
				"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
				"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
				" WHERE aut.ativo = trueValue() 	 " + 
				"	 AND aut.tipo = :tipo " + 
				idsBibliotecaString + 
				idsPessoaString +
				"	 AND rect.data_exportacao >= :periodoInicial " +  
				"	 AND rect.data_exportacao <= :periodoFinal "+
				"    GROUP BY biblioteca, pessoa " +
				" ) "+ // UNION
				" ) as interna "+
				" GROUP BY biblioteca, pessoa "+
				" ORDER BY biblioteca, pessoa ");
		
		Query q = getSession().createSQLQuery( sql.toString() );

		q.setInteger("tipo", Autoridade.TIPO_ASSUNTO);
		q.setTimestamp("periodoInicial", periodoInicial);
		q.setTimestamp("periodoFinal", periodoFinal);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		
		return list;
	}
	
}
