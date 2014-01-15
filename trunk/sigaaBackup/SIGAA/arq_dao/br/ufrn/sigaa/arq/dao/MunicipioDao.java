/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/07/2007
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.UFRNUtils.toAsciiUpperUTF8;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Dao para consultas relacionadas à um município.
 * 
 * @author Andre Dantas
 *
 */
public class MunicipioDao extends GenericSigaaDAO{

	/**collec
	 * Construtor padrão.
	 */
	public MunicipioDao() {
		super();
	}
	
	/** Retorna o município da turma */
	public Municipio findByTurma(Turma turma ) throws DAOException{
		
		StringBuffer hql = new StringBuffer();
	
		hql.append(" SELECT ");
		hql.append(" CASE WHEN m2 <> NULL THEN m2.id ELSE (CASE WHEN m <> NULL THEN m.id ELSE m3.id END) END, ");
		hql.append(" CASE WHEN m2 <> NULL THEN m2.nome ELSE (CASE WHEN m <> NULL THEN m.nome ELSE m3.nome END) END, ");
		hql.append(" CASE WHEN m2 <> NULL THEN uf2.id ELSE (CASE WHEN uf <> NULL THEN uf.id ELSE uf3.id END) END, ");
		hql.append(" CASE WHEN m2 <> NULL THEN uf2.sigla ELSE (CASE WHEN uf <> NULL THEN uf.sigla ELSE uf3.sigla END) END ");
		hql.append(" FROM Turma t ");
		hql.append(" LEFT JOIN t.disciplina d LEFT JOIN d.unidade u LEFT JOIN t.campus c   ");
		hql.append(" LEFT JOIN u.municipio m  LEFT JOIN m.unidadeFederativa uf ");
		hql.append(" LEFT JOIN c.endereco e LEFT JOIN e.municipio m2  LEFT JOIN m2.unidadeFederativa uf2 ");
		hql.append(" LEFT JOIN t.curso c LEFT JOIN c.municipio m3 LEFT JOIN m3.unidadeFederativa uf3 ");
		hql.append(" WHERE t.id = " + turma.getId() );
		List<Object[]> objects = getSession().createQuery(hql.toString()).list();
		Object[] colunas = objects.get(0);
		Municipio municipio = null;
		Integer idMunicipio = (Integer) colunas[0];
		if( !isEmpty(idMunicipio) ){
			municipio  = new Municipio( (Integer) colunas[0]);
			municipio.setNome( (String) colunas[1] );
			municipio.setUnidadeFederativa( new UnidadeFederativa( (Integer) colunas[2], (String) colunas[3]) );
		}
		return  municipio;

	}
	
	/**
	 * Retorna uma coleção de municípios de acordo com os parâmetros passados.
	 * 
	 * @param codigo
	 * @param nome
	 * @param uf
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Municipio> findByCodigoNomeUF(String codigo, String nome, UnidadeFederativa uf, PagingInformation paging) throws DAOException {
		String hql = "from Municipio where 1=1 ";
		
		if(!isEmpty(codigo))
			hql += "and " + toAsciiUpperUTF8("codigo")+" like " + toAsciiUpperUTF8("'"+codigo+"%'");
		if(!isEmpty(nome))
			hql += "and sem_acento(nome) like sem_acento('"+nome+"%')";
		if(!isEmpty(uf))
			hql += "and unidadeFederativa.id = " + uf.getId();
		
		hql += " order by nome asc";
		
		Query q = getSession().createQuery(hql);
		
		if (paging != null) {
			paging.setTotalRegistros(count(q));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		@SuppressWarnings("unchecked")
		Collection<Municipio> lista = q.list();
		
		return lista;
	}

	
	/**
	 * Retorna uma coleção de municípios de acordo com a unidade federativa passada.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<Municipio> findByUF(int id, String... projections) throws DAOException {
		try {
			
			Criteria c = getSession().createCriteria(Municipio.class);
			
			ProjectionList projectionList  = Projections.projectionList();
			
			for (String projection : projections) {
				projectionList.add( Projections.property(projection) );
			}
			
			if(projectionList.getLength() > 0)
				c.setProjection( projectionList );
			
			c.add(Expression.eq("unidadeFederativa.id", id));
			c.add(Expression.eq("ativo", true));
			c.addOrder(Order.asc("nome"));
			
			Collection<Municipio> lista = new ArrayList<Municipio>();

			StringBuilder projecao = new StringBuilder();
			
			if(projectionList.getLength() > 0){ // com projeção monta apenas os dados da projeção.
				
				for ( int index = 0 ; index < projectionList.getLength(); index++ ) {
					Projection pro  = projectionList.getProjection(index);
					projecao.append(pro.toString()+", ");
				}
				
				projecao.deleteCharAt(projecao.length()-2);
				@SuppressWarnings("unchecked")
				Collection<Municipio> parseTo = HibernateUtils.parseTo(c.list(), projecao.toString(), Municipio.class);
				lista = parseTo ;
			}else{   // sem projeção retorna o objeto completo
				@SuppressWarnings("unchecked")
				List<Municipio> list = c.list();
				lista = list;
			}
				
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Retorna uma coleção de municípios de acordo com a unidade federativa passada.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Municipio> findAllMunicipiosCampus() throws DAOException {

		String hql = " SELECT DISTINCT municipio " + 
						" FROM CampusIes c INNER JOIN c.endereco e INNER JOIN e.municipio municipio " +
						" WHERE municipio.ativo = trueValue()  ORDER BY municipio.nome ASC";

		return getSession().createQuery(hql).list();
	}

	/**
	 * Retorna uma coleção de municípios cujos nomes comecem com a string passada como parâmetro.
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Municipio> findByNome(String nome) throws DAOException {
		try {
			String hql = "from Municipio where " +
					"ativo = trueValue() and " + toAsciiUpperUTF8("nome")+" like " + toAsciiUpperUTF8("'"+nome+"%'") +
					" order by nome asc";

			@SuppressWarnings("unchecked")
			Collection<Municipio> lista = getSession().createQuery(hql).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o primeiro município encontrado com o nome e sigla da unidade federativa passados.
	 * 
	 * @param nome
	 * @param uf
	 * @return
	 * @throws DAOException
	 */
	public Municipio findUniqueByNome(String nome, String uf) throws DAOException {
		try {
			String hql = "from Municipio where " +
					" ativo = trueValue() and " + toAsciiUpperUTF8("nome")+" like " + toAsciiUpperUTF8("'"+nome+"'") +
					" and unidadeFederativa.sigla = '" + uf.toUpperCase() + "'" +
					" order by nome asc";

			Municipio municipio = (Municipio) getSession().createQuery(hql).setMaxResults(1).uniqueResult();
			return municipio;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os municípios encontrados com a sigla da unidade federativa passada.
	 * 
	 * @param nome
	 * @param uf
	 * @return
	 * @throws DAOException
	 */
	public Collection<Municipio> findByUf(String uf) throws DAOException {
		try {
			String hql = "from Municipio where " +
					" ativo = trueValue() and unidadeFederativa.sigla = '" + uf.toUpperCase() + "'" +
					" order by nome asc";

			Collection<Municipio> lista = getSession().createQuery(hql).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}