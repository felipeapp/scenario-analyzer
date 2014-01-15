/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/03/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;

/**
 * DAO para busca de Polos
 * 
 * @author Andre M Dantas *
 */
public class PoloDao extends GenericSigaaDAO {

	
	/**
	 * Retorna todos os pólos dos ids passados no parâmetro.
	 * 
	 * @param idpolo
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer,Polo> findByPrimaryKeyOtimizado(List<Integer> ids) throws DAOException {
		Map<Integer, Polo> lista = new HashMap<Integer, Polo>();
		if(ids != null && ids.size() > 0){
			@SuppressWarnings("unchecked")
			List<Polo> q = getSession().createQuery("select pc from Polo pc left join pc.cidade c left join c.unidadeFederativa uf "
					+ "where pc.id in " + UFRNUtils.gerarStringIn(ids) +"order by c.nome asc").list();
			
			for(Polo polo:q){
				lista.put(polo.getId(),polo);
			}
		}
		return lista;
	}
	/**
	 * Retorna todos os pólos do curso passado no parâmetro.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public List<Polo> findAllPolos(Curso curso) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Polo> lista = getSession().createQuery("select pc from Polo pc left join pc.cidade c left join c.unidadeFederativa uf left join pc.polosCursos cursos "
				+ "where cursos.curso.id = ? order by c.nome asc").setParameter(0, curso.getId()).list();
		return lista;
	}
	/**
	 * Retorna todos os polos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Polo> findAllPolos() throws DAOException {
		@SuppressWarnings("unchecked")
		List<Polo> lista = getSession().createQuery("select pc from Polo pc left join pc.cidade c left join c.unidadeFederativa uf order by c.nome asc").list();
		return lista;
	}
	/**
	 * Retorna todos os polosdo nivel passado por parâmetro.
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public List<Polo> findPolosByNivel(char nivel) throws DAOException {
		
		@SuppressWarnings("unchecked")
		List<Polo> lista = getSession().createQuery("select distinct p from Polo p left join p.cidade c left join c.unidadeFederativa uf " +
				"left join p.polosCursos pc left join pc.curso crs " +
				"where crs.nivel = '"+nivel+"'").list();
		return lista;
	}
	/**
	 * Retorna todos os polos do polo passado no parâmetro.
	 * 
	 * @param pólo
	 * @return
	 * @throws DAOException
	 */
	public List<PoloCurso> findPolosCurso(Polo polo) throws DAOException {
		@SuppressWarnings("unchecked")
		List<PoloCurso> lista = getSession().createQuery("from PoloCurso pc where pc.polo.id = ?").setInteger(0, polo.getId()).list();
		return lista;
	}
	/**
	 * retorna os pólos do pólo informado
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	public List<Curso> findCursosByPolo(Polo polo) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Curso> lista = getSession().createQuery("select distinct pc.curso from PoloCurso pc where pc.polo.id = ?").setInteger(0, polo.getId()).list();
		return lista;
	}
	/**
	 * retorna os pólos do curso e pólo informado
	 * @param polo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public PoloCurso findPoloCurso(Polo polo, Curso c) throws DAOException {
		return (PoloCurso) getSession().createQuery("from PoloCurso pc where pc.polo.id = ? and pc.curso.id = ?")
				.setInteger(0, polo.getId()).setInteger(1, c.getId()).uniqueResult();
	}

	/**
	 * retorna os pólos do curso informado
	 * @param idPolo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public List<Polo> findByCurso(int idCurso) throws HibernateException, DAOException{

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT p FROM Polo p, PoloCurso pc ");
		hql.append(" WHERE p.id = pc.polo.id ");
		hql.append(" AND pc.curso.id = :idCurso ");

		Query q = getSession().createQuery( hql.toString() );

		q.setInteger("idCurso", idCurso);

		@SuppressWarnings("unchecked")
		List<Polo> lista = q.list();
		return lista;

	}
	/**
	 * retorna os coordenadores do polo e curso informados.
	 * @param polo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public List<Usuario> findUsuariosCoordenadoresPolo(Polo polo, Curso curso) throws DAOException {
		String hql = "select distinct u, c.pessoa.nome from CoordenacaoPolo c left join c.usuario u left join c.polo p left join p.polosCursos pc left join pc.curso cu where 1 = 1 ";
		if (!isEmpty(polo)) hql += " and c.polo.id = " + polo.getId();
		if (!isEmpty(curso)) hql += " and cu.id = " + curso.getId();
		hql += " order by c.pessoa.nome asc";
		List list = getSession().createQuery(hql).list();
		List<Usuario> result = new ArrayList<Usuario>();
		for (Iterator<?> it = list.iterator(); it.hasNext(); ) {
			Object[] linha = (Object[]) it.next();
			result.add((Usuario) linha[0]);
		}
		return result;
	}

}

