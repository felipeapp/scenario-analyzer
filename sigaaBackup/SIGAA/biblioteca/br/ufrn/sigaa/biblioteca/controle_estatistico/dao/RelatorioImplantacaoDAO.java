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
 * <p>DAO exclusivo para o relatório Implantação.</p>
 * 
 * @author felipe
 */
public class RelatorioImplantacaoDAO extends GenericSigaaDAO {

	/**
	 * Retorna a quantidade de títulos que foram criados
	 * diretamente no sistema.
	 * 
	 * @return { importados, não importados }
	 */
	public List<Object[]> findTitulosImplantacao(Collection<Integer> idsPessoa, 
			Date periodoInicial, Date periodoFinal)
			throws HibernateException, DAOException {		
		
		String idsPessoaString = "";
		if(idsPessoa != null && idsPessoa.size() > 0)
			idsPessoaString =  " AND pessoa.id_pessoa IN (" + StringUtils.join(idsPessoa, ',') + ") ";
		
		StringBuilder sql = new StringBuilder(
			" SELECT 	 " +  
			"	 COUNT(DISTINCT t.id_titulo_catalografico) AS qtd_titulos_implantados, " +
			"    pessoa.nome as pessoa "+
			" FROM biblioteca.titulo_catalografico t 	 " +  
			"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = t.id_registro_criacao " + 
			"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
			"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
			" WHERE t.ativo = trueValue() 	 " + 
			"    AND t.catalogado = trueValue() 	 " + 
			idsPessoaString +
			"	 AND t.importado = falseValue() " + 
			"	 AND t.data_criacao >= :periodoInicial " + 
			"	 AND t.data_criacao <= :periodoFinal " + 
			"    GROUP BY pessoa ");
		
		
		Query q = getSession().createSQLQuery( sql.toString() );
		
		q.setTimestamp("periodoInicial", CalendarUtils.configuraTempoDaData(periodoInicial, 0, 0, 0, 0) );
		q.setTimestamp("periodoFinal", CalendarUtils.configuraTempoDaData(periodoFinal, 23, 59, 59, 999) );
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		return list;
	}
	
	/**
	 * Retorna a quantidade de autoridades autor que foram criadas
	 * diretamente no sistema.
	 * 
	 * @return { importados, não importados }
	 */
	public List<Object[]> findAutoridadesAutorImplantacao (Collection<Integer> idsPessoa, 
			Date periodoInicial, Date periodoFinal)
			throws HibernateException, DAOException {
		
		String idsPessoaString = "";
		if(idsPessoa != null && idsPessoa.size() > 0)
			idsPessoaString =  " AND pessoa.id_pessoa IN (" + StringUtils.join(idsPessoa, ',') + ") ";
		
		StringBuilder sql = new StringBuilder(
			" SELECT 	 " + 
			"    COUNT(DISTINCT aut.id_autoridade) AS qtd_autoridades_implantadas, " +
			"    pessoa.nome as pessoa "+
			" FROM biblioteca.autoridade aut 	 " + 
			"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = aut.id_registro_criacao " + 
			"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
			"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
			" WHERE aut.ativo = trueValue() 	 " + 
			"    AND aut.catalogada = trueValue() 	 " + 
			"	 AND aut.tipo = :tipo " + 
			idsPessoaString +
			"	 AND aut.importada = falseValue() " + 
			"	 AND aut.data_criacao >= :periodoInicial " + 
			"	 AND aut.data_criacao <= :periodoFinal " + 
			"    GROUP BY pessoa ");
		
		Query q = getSession().createSQLQuery( sql.toString() );

		q.setInteger("tipo", Autoridade.TIPO_AUTOR);
		q.setTimestamp("periodoInicial", periodoInicial);
		q.setTimestamp("periodoFinal", periodoFinal);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		
		return list;
	}
	
	/**
	 * Retorna a quantidade de autoridades assunto que foram criadas
	 * diretamente no sistema.
	 * 
	 * @return { importados, não importados }
	 */
	public List<Object[]> findAutoridadesAssuntoImplantacao (Collection<Integer> idsPessoa, 
			Date periodoInicial, Date periodoFinal)
			throws HibernateException, DAOException {
		
		String idsPessoaString = "";
		if(idsPessoa != null && idsPessoa.size() > 0)
			idsPessoaString =  " AND pessoa.id_pessoa IN (" + StringUtils.join(idsPessoa, ',') + ") ";
		
		StringBuilder sql = new StringBuilder(
				" SELECT 	 " + 
				"    COUNT(DISTINCT aut.id_autoridade) AS qtd_autoridades_implantadas, " +
				"    pessoa.nome as pessoa "+
				" FROM biblioteca.autoridade aut 	 " + 
				"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = aut.id_registro_criacao " + 
				"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
				"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
				" WHERE aut.ativo = trueValue() 	 " + 
				"    AND aut.catalogada = trueValue() 	 " + 
				"	 AND aut.tipo = :tipo " + 
				idsPessoaString +
				"	 AND aut.importada = falseValue() " + 
				"	 AND aut.data_criacao >= :periodoInicial " + 
				"	 AND aut.data_criacao <= :periodoFinal " + 
				"    GROUP BY pessoa ");
		
		Query q = getSession().createSQLQuery( sql.toString() );

		q.setInteger("tipo", Autoridade.TIPO_ASSUNTO);
		q.setTimestamp("periodoInicial", periodoInicial);
		q.setTimestamp("periodoFinal", periodoFinal);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		
		return list;
	}
	
}
