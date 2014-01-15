/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.BloqueiosUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>Dao para buscar informações sobre os bloqueios dos usuários na biblioteca</p>
 * 
 * @author jadson
 *
 */
public class BloqueioUsuarioBibliotecaDao extends GenericSigaaDAO {

	/**
	 * 
	 * Método que verifica se o usuário biblioteca está bloqueado, caso afirmativo retorna o motivo do bloqueio
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public String isUsuarioBibliotecaBloqueado(Integer idPessoa, Integer idBiblioteca) throws DAOException{
		
		String hql = "SELECT bloqueio.tipo, bloqueio.motivo  "
		+" FROM BloqueiosUsuarioBiblioteca bloqueio "; 
		if( idPessoa != null)
			hql +=" WHERE  bloqueio.pessoa.id = " + idPessoa;
		if( idBiblioteca != null)
			hql +=" WHERE  bloqueio.biblioteca.id = " + idBiblioteca;
		
		hql +=" ORDER BY id DESC, data DESC ";
		Query q = getSession().createQuery(hql);
		q.setMaxResults(1);
		
		Object[] result = (Object[])q.uniqueResult();
		
		if(result == null) return null;
		
		if( ((Integer) result[0]).equals(BloqueiosUsuarioBiblioteca.BLOQUEIO ) )
			return  (String) result[1];
		else
			return null; /// Usuário não está bloqueado
	}
	
	
	
	/**
	 * 
	 * <p>Método que recupera o histórico de bloqueios de uma pessoa na biblioteca, ordenado pela data em que a operação foi criada<p>
	 *
	 * <p>Caso a última operação retornada seja um bloqueio, então essa pessoa está bloqueada </p>
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public List<BloqueiosUsuarioBiblioteca> findBloqueiosBibliotecaByPessoa(int idPessoa)throws DAOException{
		String hql = "SELECT bloqueio.tipo, bloqueio.motivo, bloqueio.data, bloqueio.usuarioRealizouOperacao.pessoa.nome "
		+" FROM BloqueiosUsuarioBiblioteca bloqueio " 
		+" WHERE  bloqueio.pessoa.id = " + idPessoa
		+" ORDER BY id DESC, data DESC ";
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Object> objetosHistorico = q.list();
		
		List<BloqueiosUsuarioBiblioteca> bloqueios = new ArrayList<BloqueiosUsuarioBiblioteca>();
		
		for (Object object : objetosHistorico) {
			Object[] temp = (Object[]) object;
			
			bloqueios.add(new BloqueiosUsuarioBiblioteca((Integer) temp[0], (String) temp[1], (Date) temp[2], new Usuario(0, (String) temp[3], ""), new Pessoa() ));
		}
		
		return bloqueios;
	}
	
	
	/**
	 * 
	 * <p>Método que recupera o histórico de bloqueios de uma biblioteca na biblioteca, ordenado pela data em que a operação foi criada </p>
	 *
	 * <p>Caso a última operação retornada seja um bloqueio, então essa biblioteca está bloqueada </p>
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public List<BloqueiosUsuarioBiblioteca> findBloqueiosBibliotecaByBiblioteca(int idBiblioteca)throws DAOException{
		String hql = "SELECT bloqueio.tipo, bloqueio.motivo, bloqueio.data, bloqueio.usuarioRealizouOperacao.pessoa.nome "
			+" FROM BloqueiosUsuarioBiblioteca bloqueio " 
			+" WHERE  bloqueio.biblioteca.id = " + idBiblioteca
			+" ORDER BY id DESC, data DESC ";
			Query q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			List<Object> objetosHistorico = q.list();
			
			List<BloqueiosUsuarioBiblioteca> bloqueios = new ArrayList<BloqueiosUsuarioBiblioteca>();
			
			for (Object object : objetosHistorico) {
				Object[] temp = (Object[]) object;
				
				bloqueios.add(new BloqueiosUsuarioBiblioteca((Integer) temp[0], (String) temp[1], (Date) temp[2], new Usuario(0, (String) temp[3], ""), new Pessoa() ));
			}
			
			return bloqueios;
	}
	
}
