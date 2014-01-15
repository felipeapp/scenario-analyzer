/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/08/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 *
 * <p>DAO com as consultas exclusivas dos vínculos dos usuários para a biblioteca. </p>
 * 
 * @author jadson
 *
 */
public class VinculoUsuariosBibliotecaDao  extends GenericSigaaDAO{

	/**
	 * Retorna as informaçoes dos vinculos no sistema da pessoa passada
	 * 
	 * <p>Utilizado apenas para os bibliotecários poderem visualizar as informações dos vínculos dos 
	 * usuário e saber porque ele não estão conseguindo utilizar a biblioteca.</p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Object []> findInformacoesVinculosDiscente(int idPessoa) throws DAOException{
		
		String sql =
			"  SELECT d.id_discente, d.matricula, d.nivel, d.status, d.tipo, forma.permite_emprestimo_biblioteca "
			+" FROM comum.pessoa p " 
			+" INNER JOIN  discente d on d.id_pessoa = p.id_pessoa "
			+" LEFT JOIN ensino.forma_ingresso forma on d.id_forma_ingresso = forma.id_forma_ingresso "
			+" WHERE  p.id_pessoa =  "+idPessoa;
		
		Query q = getSession().createSQLQuery(sql);

		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Retorna as informaçoes dos vinculos no sistema da pessoa passada
	 * 
	 * <p>Utilizado apenas para os bibliotecários poderem visualizar as informações dos vínculos dos 
	 * usuário e saber porque ele não estão conseguindo utilizar a biblioteca.</p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Object []>  findInformacoesVinculosServidor(int idPessoa) throws  DAOException{
	
		String sql =
			"  SELECT servidor.id_servidor, servidor.siape, categoria.id_categoria, categoria.descricao as descricaoCategoria, ativo.id_ativo, ativo.descricao as descricaoStatus "
			+" FROM comum.pessoa p " 
			+" INNER JOIN rh.servidor servidor on servidor.id_pessoa = p.id_pessoa"
			+" INNER JOIN rh.categoria categoria on categoria.id_categoria = servidor.id_categoria "
			+" INNER JOIN rh.ativo ativo on ativo.id_ativo = servidor.id_ativo "
			+" WHERE  p.id_pessoa =  "+idPessoa;
		
		Query q = getSession().createSQLQuery(sql);

		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		return lista;
	}

	
	/**
	 * Retorna as informaçoes dos vinculos no sistema da pessoa passada
	 * 
	 * <p>Utilizado apenas para os bibliotecários poderem visualizar as informações dos vínculos dos 
	 * usuário e saber porque ele não estão conseguindo utilizar a biblioteca.</p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Object []> findInformacoesVinculosUsuarioExterno(int idPessoa) throws  DAOException{
		
		String sql =
			"  SELECT ube.id_usuario_externo_biblioteca, p.cpf_cnpj, ube.prazo_vinculo, ube.cancelado "
			+" FROM comum.pessoa p "
			+" INNER JOIN biblioteca.usuario_biblioteca ub on ub.id_pessoa = p.id_pessoa "
			+" INNER JOIN biblioteca.usuario_externo_biblioteca  ube on ube.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" WHERE  p.id_pessoa =  "+idPessoa;
	
		Query q = getSession().createSQLQuery(sql);

		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Retorna as informaçoes dos vinculos no sistema da pessoa passada
	 * 
	 * <p>Utilizado apenas para os bibliotecários poderem visualizar as informações dos vínculos dos 
	 * usuário e saber porque ele não estão conseguindo utilizar a biblioteca.</p>
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Object []> findInformacoesVinculosDocenteExterno(int idPessoa) throws  DAOException{
		
		String sql =
			"  SELECT docente.id_docente_externo, p.cpf_cnpj, docente.matricula, docente.prazo_validade, docente.ativo"
			+" FROM comum.pessoa p " 
			+" INNER JOIN ensino.docente_externo docente on p.id_pessoa = docente.id_pessoa "
			+" WHERE  p.id_pessoa =  "+idPessoa;
		
		Query q = getSession().createSQLQuery(sql);
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = q.list();
		return lista;
	}
}
