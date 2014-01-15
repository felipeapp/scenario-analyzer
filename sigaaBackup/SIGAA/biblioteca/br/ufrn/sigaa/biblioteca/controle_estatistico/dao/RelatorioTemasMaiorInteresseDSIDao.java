/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/12/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 *
 * <p> Dao expecífico para as consultas dos relatórios da parte de DSI </p>
 *
 * 
 * @author jadson
 *
 */
public class RelatorioTemasMaiorInteresseDSIDao extends GenericSigaaDAO{

	/**
	 * <p>Conta a quantidade de usuários que se cadastraram para receber o informativo de novas aquisições </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalUsuariosRecebendoInformativoNovasAquisicoes() throws DAOException {

		String hql =" SELECT  COUNT(DISTINCT perfil.id)" +  
					" FROM PerfilInteresseUsuarioBiblioteca perfil "+
					" INNER JOIN perfil.usuario usuarioBiblioteca "+
					" WHERE perfil.receberInformativoNovasAquisicoes = :true "+
					" AND usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false ";   // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
			
		Query q = getSession().createQuery(hql);
		q.setBoolean("true", true);
		q.setBoolean("false", false);
		return (Long) q.uniqueResult();	
		
	}
	
	
	/**
	 * <p> Retorna uma listagem das grandes áreas do CNPQ junto com a quantidade de usuários que cadatram interesse nelas, ordenadas pela quantidade. </p>  
	 * 
	 * @throws DAOException 
	 */
	public List<Object[]> listaGrandesAreasDeMaiorInteresseDosUsuarios() throws DAOException {

		String hql =" SELECT  areaConecimentoInformativo.nome, COUNT( areaConecimentoInformativo.id)" +  
					" FROM PerfilInteresseUsuarioBiblioteca perfil "+
					" INNER JOIN perfil.usuario usuarioBiblioteca "+
					" INNER JOIN perfil.areaDoInformativo areaConecimentoInformativo "+
					" WHERE perfil.receberInformativoNovasAquisicoes = :true "+
					" AND usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false "+  // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
					" GROUP BY areaConecimentoInformativo.nome "+
					" ORDER BY 2 DESC ";
		
		Query q = getSession().createQuery(hql);
		q.setBoolean("true", true);
		q.setBoolean("false", false);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
		
	}


	/**
	 * Retorna os assuntos de maior interesse dos usuários do sistema.
	 *  
	 *
	 * @return
	 */
	public List<Object[]> listaAssuntosDeMaiorInteresseDosUsuarios(Collection<Integer> idsBibliotecas, final int limiteResultado) throws DAOException{
		
		// Sql porque não existe um relacionamento entre chache -> autoridade
		StringBuilder sql = new StringBuilder(" SELECT  c.entrada_autorizada_assunto, COUNT( c.id_autoridade )" +  
		" FROM biblioteca.perfil_interesse_usuario_biblioteca perfil "+
		" INNER JOIN biblioteca.usuario_biblioteca usuarioBiblioteca                 ON usuarioBiblioteca.id_usuario_biblioteca = perfil.id_usuario_biblioteca "+
		" INNER JOIN biblioteca.perfil_interesse_x_autoridade_assunto relacionamento ON relacionamento.id_perfil_interesse_usuario_biblioteca = perfil.id_perfil_interesse_usuario_biblioteca "+
		" INNER JOIN biblioteca.autoridade assunto                                   ON assunto.id_autoridade = relacionamento.id_autoridade "+
		" INNER JOIN biblioteca.cache_entidades_marc c                               ON c.id_autoridade = assunto.id_autoridade ");
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0 ){
			sql.append(" INNER JOIN biblioteca.perfil_interesse_x_biblioteca relacionamento2             ON relacionamento2.id_perfil_interesse_usuario_biblioteca = perfil.id_perfil_interesse_usuario_biblioteca "+
					   " INNER JOIN biblioteca.biblioteca biblioteca                                     ON biblioteca.id_biblioteca = relacionamento2.id_biblioteca ");
		}
		
		
		sql.append(" WHERE usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false "); // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0 ){
			sql.append(" AND biblioteca.id_biblioteca IN "+UFRNUtils.gerarStringIn(idsBibliotecas));
		}
		
		sql.append(" GROUP BY c.entrada_autorizada_assunto  ORDER BY 2 DESC ");

		Query q = getSession().createSQLQuery(sql.toString());
		q.setBoolean("true", true);
		q.setBoolean("false", false);
		q.setMaxResults(limiteResultado);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
		
	}


	/**
	 * Retorna os assuntos de maior interesse dos usuários do sistema.
	 *  
	 *
	 * @return
	 */
	public List<Object[]> listaAutoresDeMaiorInteresseDosUsuarios(Collection<Integer> idsBibliotecas, final int limiteResultado) throws DAOException{
		
		
		// Sql porque não existe um relacionamento entre chache -> autoridade
		StringBuilder sql = new StringBuilder(" SELECT  c.entrada_autorizada_autor, COUNT( c.id_autoridade )" +  
		" FROM biblioteca.perfil_interesse_usuario_biblioteca perfil "+
		" INNER JOIN biblioteca.usuario_biblioteca usuarioBiblioteca                 ON usuarioBiblioteca.id_usuario_biblioteca = perfil.id_usuario_biblioteca "+
		" INNER JOIN biblioteca.perfil_interesse_x_autoridade_autor relacionamento   ON relacionamento.id_perfil_interesse_usuario_biblioteca = perfil.id_perfil_interesse_usuario_biblioteca "+
		" INNER JOIN biblioteca.autoridade autor                                     ON autor.id_autoridade = relacionamento.id_autoridade "+
		" INNER JOIN biblioteca.cache_entidades_marc c                               ON c.id_autoridade = autor.id_autoridade ");
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0 ){
			sql.append(" INNER JOIN biblioteca.perfil_interesse_x_biblioteca relacionamento2             ON relacionamento2.id_perfil_interesse_usuario_biblioteca = perfil.id_perfil_interesse_usuario_biblioteca "+
					   " INNER JOIN biblioteca.biblioteca biblioteca                                     ON biblioteca.id_biblioteca = relacionamento2.id_biblioteca ");
		}
		
		sql.append(" WHERE usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false ");  // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
		
		if(idsBibliotecas != null && idsBibliotecas.size() > 0 ){
			sql.append(" AND biblioteca.id_biblioteca IN "+UFRNUtils.gerarStringIn(idsBibliotecas));
		}
		
		sql.append(" GROUP BY c.entrada_autorizada_autor ORDER BY 2 DESC ");

		Query q = getSession().createSQLQuery(sql.toString());
		q.setBoolean("true", true);
		q.setBoolean("false", false);
		q.setMaxResults(limiteResultado);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
	}
	
	
}
