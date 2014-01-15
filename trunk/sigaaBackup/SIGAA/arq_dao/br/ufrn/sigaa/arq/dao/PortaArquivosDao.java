/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/12/2006
 *
 */
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/12/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;
import br.ufrn.sigaa.cv.dominio.ArquivoComunidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;


/**
 * DAO utilizado no porta-arquivos
 *
 * @author David Ricardo
 *
 */
public class PortaArquivosDao extends GenericSigaaDAO {

	/**
	 * Retorna as pastas do porta arquivos de um usuário.
	 * @param usr
	 * @throws DAOException 
	 */
	public List<PastaArquivos> findPastasByUsuario(Usuario usr) throws DAOException {
		return findPastasByUsuario(usr, null);
	}
	
	/**
	 * Retorna as pastas do porta arquivos de um usuário.
	 * @param usr
	 * @param turma
	 * @throws DAOException 
	 */
	public List<PastaArquivos> findPastasByUsuario(Usuario usr, Turma turma) throws DAOException {
		try {
			Criteria c = getCriteria(PastaArquivos.class);
			c.add(Expression.eq("usuario", usr));
			if (turma != null)
				c.add(Expression.eq("nome", turma.getDisciplina().getCodigo()));
			c.addOrder(Order.asc("id"));
			return c.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os arquivos de uma pasta
	 * @param idPasta
	 * @param idUsuario
	 * @throws DAOException 
	 */
	public List<ArquivoUsuario> findArquivosByPasta(int idPasta, int idUsuario) throws DAOException {
		try {
			Criteria c = getCriteria(ArquivoUsuario.class);
			
			if (idPasta == -1)
				c.add(Expression.isNull("pasta"));
			else
				c.add(Expression.eq("pasta.id", idPasta));
			
			c.add(Expression.eq("usuario.id", idUsuario));
			c.addOrder(Order.asc("nome"));
			return c.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o tamanho do porta arquivos do usuário
	 * @param idUsuario
	 * @throws DAOException 
	 */
	public long findTotalOcupadoByUsuario(int idUsuario) throws DAOException {
		Query q = getSession().createQuery("select sum(a.tamanho) from ArquivoUsuario a where a.usuario.id = :idUsuario");
		q.setInteger("idUsuario", idUsuario);
		
		Number n = (Number) q.uniqueResult();
		if (n == null)
			return 0;
		return n.longValue();
	}

	/**
	 * Retorna a pasta do usuário
	 * @param usuario
	 * @param nome
	 * @param pai
	 * @throws DAOException 
	 */
	public PastaArquivos findPastaByUsuarioNome(Usuario usuario, String nome, PastaArquivos pai) throws DAOException {
		Criteria c = getSession().createCriteria(PastaArquivos.class);
		c.add(Expression.eq("usuario.id", usuario.getId()));
		c.add(Expression.eq("nome", nome));
		if (pai != null) {
			c.add(Expression.eq("pai.id", pai.getId()));
		} else {
			c.add(Expression.isNull("pai"));
		}
		
		c.setMaxResults(1);
		List list = c.list();
		if (isEmpty(list))
			return null;
		return (PastaArquivos) list.get(0);
	}
	
	/**
	 * Retorna os arquivos da turma associados a um arquivo do usuário.
	 * @param arquivo
	 * @throws DAOException 
	 */
	public List<ArquivoTurma> findArquivosTurmaByArquivo(ArquivoUsuario arquivo) throws DAOException {
		Query q = getSession().createQuery("from ArquivoTurma at where at.arquivo.id = ?");
		q.setInteger(0, arquivo.getId());
		return q.list();
	}
	
	/**
	 * Retorna os arquivos da comunidade associados a um arquivo do usuário.
	 * @param arquivo
	 * @throws DAOException 
	 */
	public List<ArquivoComunidade> findArquivosComunidadeByArquivo(ArquivoUsuario arquivo) throws DAOException {
		Query q = getSession().createQuery("from ArquivoComunidade at where at.arquivo.id = ?");
		q.setInteger(0, arquivo.getId());
		return q.list();
	}
	
	/**
	 * Retorna os arquivos da Comunidade associados a uma pasta.
	 * @param arquivo
	 * @throws DAOException 
	 */
	public List<ArquivoComunidade> findArquivosComunidadeByPasta(PastaArquivos pasta) throws DAOException {
		Query q = getSession().createQuery("from ArquivoComunidade at where at.arquivo.pasta.id = ?");
		q.setInteger(0, pasta.getId());
		return q.list();
	}
}
