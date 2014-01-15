/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/** Classe responsável por consultas específicas à Matriz Curricular.
 * @author Édipo Elder F. Melo
 *
 */
public class MatrizCurricularDao extends GenericSigaaDAO {

	/** Indica se a matriz curricular informada já existe.
	 * @param matriz
	 * @return
	 * @throws DAOException
	 */
	public boolean jaExiste(MatrizCurricular matriz) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql
					.append("SELECT id FROM MatrizCurricular WHERE "
							+ "ativo = trueValue() and curso.id = :curso and curso.municipio.id=:municipio"
							+ " and turno.id = :turno and grauAcademico.id = :grau and id <> :matriz ");
			if (ValidatorUtil.isEmpty(matriz.getHabilitacao()))
				hql.append(" and habilitacao.id is null ");
			else
				hql.append(" and habilitacao.id = :habilitacao");
			if (ValidatorUtil.isEmpty(matriz.getEnfase()))
				hql.append(" and enfase.id is null ");
			else
				hql.append(" and enfase.id = :enfase");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("curso", matriz.getCurso().getId());
			q.setInteger("municipio", matriz.getCurso().getMunicipio().getId());
			q.setInteger("turno", matriz.getTurno().getId());
			q.setInteger("grau", matriz.getGrauAcademico().getId());
			q.setInteger("matriz", matriz.getId());
			if (!ValidatorUtil.isEmpty(matriz.getHabilitacao()))
				q.setInteger("habilitacao", matriz.getHabilitacao().getId());
			if (!ValidatorUtil.isEmpty(matriz.getEnfase()))
				q.setInteger("enfase", matriz.getEnfase().getId());
			@SuppressWarnings("unchecked")
			Collection<Integer> res = q.list();
			return res != null && res.size() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna as matrizes curriculares ativas do curso informado
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findAtivasByCurso(int idCurso)
			throws DAOException {
		return findByCurso(idCurso, true);
	}

	/**
	 * Retorna as matrizes do curso informado
	 * 
	 * @param idCurso
	 * @param ativo
	 *            TRUE caso deva retornar apenas as matrizes ativas, FALSE caso
	 *            deva retornar apenas as inativas, NULL caso deva retornar
	 *            todas
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findByCurso(int idCurso, Boolean ativo)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MatrizCurricular.class);
			if (ativo == null) {
				c.createCriteria("curso").add(Expression.eq("id", idCurso));
			} else if (ativo) {
				c.add(Expression.eq("ativo", Boolean.TRUE)).createCriteria(
						"curso").add(Expression.eq("id", idCurso)).add(
						Expression.eq("ativo", Boolean.TRUE));
			} else if (!ativo) {
				c.add(Expression.eq("ativo", Boolean.FALSE)).createCriteria(
						"curso").add(Expression.eq("id", idCurso));
			}
			@SuppressWarnings("unchecked")
			Collection<MatrizCurricular> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna as matrizes do curso informado
	 * 
	 * @param idCurso
	 * @param ativo
	 *            TRUE caso deva retornar apenas as matrizes ativas, FALSE caso
	 *            deva retornar apenas as inativas, NULL caso deva retornar
	 *            todas
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findByCurso(String nomeCurso)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MatrizCurricular.class);
			c.add(Expression.eq("ativo", Boolean.TRUE)).createCriteria("curso")
					.add(Expression.ilike("nomeAscii", "%" + nomeCurso + "%"))
					.add(Expression.eq("ativo", Boolean.TRUE));
			@SuppressWarnings("unchecked")
			Collection<MatrizCurricular> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/** Retorna as matrizes curriculares.
	 * @param somenteAtivas caso true, retornará somente as matrizes ativas.
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findAtivos(boolean somenteAtivas)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MatrizCurricular.class);
			if (somenteAtivas) {
				c.add(Expression.eq("ativo", Boolean.TRUE));
			}
			c.createAlias("curso", "curso");
			c.addOrder(Order.asc("curso.nome"));
			c.addOrder(Order.asc("curso.municipio"));
			@SuppressWarnings("unchecked")
			Collection<MatrizCurricular> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna as matrizes curriculares de uma unidade.
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findByUnidade(int idUnidade, Integer modalidadeEducacao, Boolean apenasMatrizesAtivas)
			throws DAOException {
		StringBuilder hql = new StringBuilder("select matrizCurricular" +
				" from MatrizCurricular matrizCurricular" +
				" left join fetch matrizCurricular.habilitacao habilitacao" +
				" left join fetch matrizCurricular.atualizadoPor atualizadoPor" +
				" left join fetch matrizCurricular.criadoPor criadoPor" +
				" inner join fetch matrizCurricular.curso curso" +
				" inner join fetch curso.unidade unidade" +
				" where unidade.id = :idUnidade" +
				" and matrizCurricular.permiteColacaoGrau = trueValue()"); 
			if (modalidadeEducacao != null)
				hql.append(" and curso.modalidadeEducacao.id = :idModalidadeEducacao");
			if (apenasMatrizesAtivas != null)
				hql.append(" and matrizCurricular.ativo = :ativo");
			hql.append(" order by matrizCurricular.curso.municipio.nome, matrizCurricular.curso.nome");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUnidade", idUnidade);
		if (modalidadeEducacao != null)
			q.setInteger("idModalidadeEducacao", modalidadeEducacao);
		if (apenasMatrizesAtivas != null)
			q.setBoolean("ativo", apenasMatrizesAtivas);
		
		@SuppressWarnings("unchecked")
		Collection<MatrizCurricular> lista = q.list();
		return lista;
	}
	
	/** Retorna o ID e a Descrição de todas as matrizes curriculares.
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findAllOtimizado() throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT distinct m.id, m.curso.nome, enfase.nome , habilitacao.nome, m.turno.sigla, m.grauAcademico.descricao");
		hql.append(" , m.curso.municipio.nome ");
		hql.append(" FROM MatrizCurricular m");
		hql.append(" LEFT JOIN m.enfase enfase");
		hql.append(" LEFT JOIN m.habilitacao habilitacao");
		hql.append(" ORDER BY m.curso.nome ");
				
		Query query = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();
		
		List<MatrizCurricular> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<MatrizCurricular>();
			}
			
			int col = 0;
			
			Object[] colunas = (Object[]) lista.get(a);
			MatrizCurricular matrizCurricular = new MatrizCurricular();
			matrizCurricular = new MatrizCurricular();
		
			matrizCurricular.setId((Integer) colunas[col++]);
			
			matrizCurricular.setCurso(new Curso());
			matrizCurricular.getCurso().setNome((String) colunas[col++]);
			
			matrizCurricular.setEnfase(new Enfase());
			matrizCurricular.getEnfase().setNome((String) colunas[col++]);
			
			matrizCurricular.setHabilitacao(new Habilitacao());
			matrizCurricular.getHabilitacao().setNome((String) colunas[col++]);
			
			matrizCurricular.setTurno(new Turno());
			matrizCurricular.getTurno().setSigla((String) colunas[col++]);
			
			matrizCurricular.setGrauAcademico(new GrauAcademico());
			matrizCurricular.getGrauAcademico().setDescricao((String) colunas[col++]);
			
			matrizCurricular.getCurso().setMunicipio(new Municipio());
			matrizCurricular.getCurso().getMunicipio().setNome((String) colunas[col++]);
			
			result.add(matrizCurricular);
		}
		
		return result;		
	}
	
	
	/** 
	 * Retorna as matrizes curriculares com Ênfase referente ao curso informado.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<MatrizCurricular> findMatrizComEnfaseByCurso(int idCurso)
			throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT distinct m.id, m.curso.nome, enfase.nome , habilitacao.nome, m.turno.sigla, m.grauAcademico.descricao"); 
		hql.append(" FROM MatrizCurricular m");
		hql.append(" LEFT JOIN m.enfase enfase");
		hql.append(" LEFT JOIN m.habilitacao habilitacao");
		hql.append(" where m.possuiEnfase = trueValue() ");
		hql.append("   and m.curso.id = :idCurso ");
		hql.append(" ORDER BY m.curso.nome ");
				
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", idCurso);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
		
		List<MatrizCurricular> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<MatrizCurricular>();
			}
			
			int col = 0;
			
			Object[] colunas = (Object[]) lista.get(a);
			MatrizCurricular matrizCurricular = new MatrizCurricular();
			matrizCurricular = new MatrizCurricular();
		
			matrizCurricular.setId((Integer) colunas[col++]);
			
			matrizCurricular.setCurso(new Curso());
			matrizCurricular.getCurso().setNome((String) colunas[col++]);
			
			matrizCurricular.setEnfase(new Enfase());
			matrizCurricular.getEnfase().setNome((String) colunas[col++]);
			
			matrizCurricular.setHabilitacao(new Habilitacao());
			matrizCurricular.getHabilitacao().setNome((String) colunas[col++]);
			
			matrizCurricular.setTurno(new Turno());
			matrizCurricular.getTurno().setSigla((String) colunas[col++]);
			
			matrizCurricular.setGrauAcademico(new GrauAcademico());
			matrizCurricular.getGrauAcademico().setDescricao((String) colunas[col++]);
			
			result.add(matrizCurricular);
		}
		
		return result;	
	}
	
	/**
	 * Cria um mapa com o par (ID da Matriz Curricular, Matriz Curricular) para
	 * ser usado como cache em operações de processamento.
	 * 
	 * @param listaIDMatrizes
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, MatrizCurricular> criaCache(Collection<Integer> listaIDMatrizes) throws DAOException {
		Map<Integer, MatrizCurricular> mapa = new TreeMap<Integer, MatrizCurricular>();
		if (!isEmpty(listaIDMatrizes)) {
			Criteria c = getSession().createCriteria(MatrizCurricular.class);
			c.add(Restrictions.in("id", listaIDMatrizes));
			@SuppressWarnings("unchecked")
			Collection<MatrizCurricular> matrizes = c.list();
			for (Integer id : listaIDMatrizes) {
				mapa.put(id, null);
				for (MatrizCurricular p : matrizes) {
					if (p.getId() == id) {
						mapa.put(id, p);
					}
				}
			}
		}
		return mapa;
	}
}
