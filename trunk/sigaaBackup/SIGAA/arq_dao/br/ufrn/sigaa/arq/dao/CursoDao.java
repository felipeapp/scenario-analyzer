/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.LinhaListaCursos;
import br.ufrn.sigaa.ensino.dominio.NaturezaCurso;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/** Classe responsável por consultas específicas à cursos.
 * @author Andre M Dantas
 *
 */
public class CursoDao extends GenericSigaaDAO {

	/**
	 * Busca os cursos ATIVOS por nome
	 * @param nome
	 * @param unid
	 * @param cursoSubClasse
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNome(String nome, int unid, Class<?> cursoSubClasse, char nivel, PagingInformation paging) throws DAOException {
		return findByNome(nome, unid, cursoSubClasse, nivel, Boolean.TRUE, paging);
	}
	/**
	 * Busca TODOS os cursos por nome
	 * @param nome
	 * @param unid
	 * @param cursoSubClasse
	 * @param nivel
	 * @param ativo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNome(String nome, int unid, Class<?> cursoSubClasse, char nivel, Boolean ativo, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			String classe = (cursoSubClasse != null)?cursoSubClasse.getSimpleName():"Curso";
			hql.append("from "+classe+" where ");
			if (unid > 0) {
				hql.append("unidade.id = " + unid + " and ");
			}
			if (nivel != ' ' && nivel != 'S') {
				hql.append(" nivel = '" + nivel + "' and ");
			} else if(nivel == 'S') {
				hql.append(" nivel in ('E', 'D') and ");
			}
			if (ativo != null) {
				hql.append(" ativo = " + ativo+ " and ");
			}
			if (nome != null && !nome.trim().equals("")) {
				hql.append( "nome_ascii like :nome");
			}

			if (hql.toString().endsWith("and ")) {
				hql.delete(hql.lastIndexOf("and "), hql.length());
			}
			hql.append(" order by nome asc");

			Query q = getSession().createQuery(hql.toString());
			if (nome != null && !nome.trim().equals("")) {
				q.setString("nome", "%"+ StringUtils.toAscii(nome.trim().toUpperCase()) + "%");
			}

			@SuppressWarnings("unchecked")
			Collection<Curso> list = q.list();
			
			return list;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os cursos ATIVOS do convênio e nível de ensino passado por parâmetro.
	 * 
	 * @param convenio
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByConvenioAcademico(int convenio, char nivel) throws DAOException {
		try {
			Query q = getSession().createQuery(" SELECT new Curso(id, nome, unidade.id, municipio.nome, nivel) FROM Curso" +
					" WHERE ativo = trueValue() and convenio.id = :convenio and nivel=:nivel " +
					" ORDER BY nome");
			q.setInteger("convenio", convenio);
			q.setCharacter("nivel", nivel);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os cursos ATIVOS que possui 2 ciclos e nível de ensino passado por parâmetro.
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByCicloFormacao(int tipoCiclo, char nivel) throws DAOException {
		try {
			Query q = getSession().createQuery(" SELECT new Curso(id, nome, unidade.id, municipio.nome, nivel) FROM Curso" +
					" WHERE ativo = trueValue() and tipoCicloFormacao.id = :tipocCiclo and nivel=:nivel " +
					" ORDER BY nome");
			q.setInteger("tipocCiclo", tipoCiclo);
			q.setCharacter("nivel", nivel);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e);
		}
	}	

	/**
	 * Busca todos os cursos ativos por modalidade e nível de ensino
	 * 
	 * @param modalidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByModalidadeEducacao(int modalidade, char nivel) throws DAOException {
		try {
			Query q = getSession().createQuery(" SELECT new Curso(id, nome, unidade.id, municipio.nome, nivel) FROM Curso" +
					" WHERE ativo = trueValue() and modalidadeEducacao.id = :modalidade and nivel=:nivel " +
					" ORDER BY nome");
			q.setInteger("modalidade", modalidade);
			q.setCharacter("nivel", nivel);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e);
		}
	}

	/**
	 * Busca cursos ATIVOS por Código
	 * @param codigo
	 * @param unid
	 * @param cursoSubClasse
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByCodigo(String codigo, int unid, Class<?> cursoSubClasse, PagingInformation paging) throws DAOException {
		return findByCodigo(codigo, unid, cursoSubClasse, Boolean.TRUE, paging);
	}


	/**
	 * Busca por TODOS os cursos por código
	 * @param codigo
	 * @param unid
	 * @param cursoSubClasse
	 * @param ativo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByCodigo(String codigo, int unid, Class<?> cursoSubClasse, Boolean ativo, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from "+cursoSubClasse.getCanonicalName()+" where ");
			if (unid > 0) {
				hql.append("unidade.id = " + unid );
			}
			if (ativo != null) {
				hql.append(" and ativo = " + ativo);
			}
			if (codigo != null && !codigo.trim().equals("")) {
				hql.append(" and codigo = '"+codigo+"' ");
			}
			hql.append("order by nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca todos os cursos ATIVOS
	 * @param unidId
	 * @param nivel
	 * @param cursoSubClasse
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findAll(int unidId, char nivel, Class<?> cursoSubClasse, PagingInformation paging) throws DAOException {
		return findAll(unidId, nivel, cursoSubClasse, Boolean.TRUE, paging);
	}

	/**
	 * Busca por TODOS os cursos
	 * @param unidId
	 * @param nivel
	 * @param cursoSubClasse
	 * @param ativo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findAll(int unidId, char nivel, Class<?> cursoSubClasse, Boolean ativo, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from "+cursoSubClasse.getCanonicalName()+" where ");
			if (unidId > 0) {
				hql.append("unidade.id = " + unidId + " and ");
			}
			if (nivel != 0) {
				hql.append("nivel = '" + nivel + "' ");
			}
			if (ativo != null) {
				hql.append(" and ativo = " + ativo);
			}
			hql.append(" order by nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Verifica se já existe um curso de graduação com um determinado nome,
	 * município e uma das unidades sedes passadas. O id do curso é passado para
	 * caso seja maior que zero, a verificação deve considerar que exista pelo
	 * menos um curso assim.
	 *
	 * @param nome
	 * @param idMunicipio
	 * @param unidadesSedes
	 * @param cursoId
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCursoGraduacao(Curso curso) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT id FROM Curso c "
					+ "WHERE c.id <> :curso and c.nome=:nome and c.ativo=trueValue() " +
							"and c.municipio.id=:municipio and c.unidade.id=:unidade");
			Query q = getSession().createQuery(hql.toString());
			q.setString("nome", curso.getNome());
			q.setInteger("curso", curso.getId());
			q.setInteger("municipio", curso.getMunicipio().getId());
			q.setInteger("unidade", curso.getUnidade().getId());
			Collection<?> res = q.list();
			return res != null && res.size() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Verifica se já existe um curso de stricto com um determinado nome,
	 * município, unidades sede e tipo de curso stricto. O id do curso é passado para
	 * caso seja maior que zero, a verificação deve considerar que exista pelo
	 * menos um curso assim.
	 *
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCursoStricto(Curso curso) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT id FROM Curso c "
					+ "WHERE c.id <> :curso and c.nome = :nome and c.ativo = trueValue() " +
							"and c.municipio.id = :municipio and c.unidade.id = :unidade and c.tipoCursoStricto.id = :tipoStricto");
			Query q = getSession().createQuery(hql.toString());
			q.setString("nome", curso.getNome());
			q.setInteger("curso", curso.getId());
			q.setInteger("municipio", curso.getMunicipio().getId());
			q.setInteger("unidade", curso.getUnidade().getId());
			q.setInteger("tipoStricto", curso.getTipoCursoStricto().getId());
			Collection<?> res = q.list();
			return res != null && res.size() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Retorna todos os cursos por Nível de ensino que sejam ativos e possuam convênios 
	 * 
	 * @param n
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNivel(char n) throws DAOException {
		return findByNivel( n, true, true, null);
	}

	/**
	 * Retorna todos os cursos por Centro e Nível de ensino informado que sejam ativos e possuam convênios
	 *  
	 * @param n
	 * @param centro
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNivel(char n, Unidade centro) throws DAOException {
		return findByNivel( n, true, true, centro);
	}

	/**
	 * Busca todos os cursos pelo nível de ensino e status (ativo ou inativo) informado que possuam convênios
	 * 
	 * @param n
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNivel(char n, Boolean ativo) throws DAOException {
		return findByNivel( n, ativo, true, null);
	}

	/**
	 * Busca todos os cursos informando o nível de ensino, status (ativo ou inativo) e se possuem convênio
	 * 
	 * @param n
	 * @param ativo
	 * @param convenio
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNivel(char n, Boolean ativo, Boolean convenio) throws DAOException {
		return findByNivel( n, ativo, convenio, null);
	}

	/**
	 * Busca por nível considerando ou não os cursos com convênio
	 * @param n
	 * @param ativo
	 * @param convenio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByNivel(char n, Boolean ativo, Boolean convenio, Unidade centro) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Curso.class);
			if (NivelEnsino.isAlgumNivelStricto(n)) {
				c.add(Restrictions.in("nivel", NivelEnsino.getNiveisStricto()));
			} else if( n != ' ') {
				c.add(Restrictions.eq("nivel", n));
			}

			if (ativo != null) {
				c.add(Restrictions.eq("ativo", ativo));
			}
			if (convenio != null && !convenio){
				c.add(Restrictions.isNull("convenio"));
			}
			if (centro != null) {
				c.add(Restrictions.eq("unidade.id", centro.getId()));
			}

			c.addOrder( Order.asc("nome") );
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca os cursos de Lato onde a situação da proposta está aceita
	 * @param n
	 * @param ativo
	 * @param convenio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByNivelAceito(char n, Boolean ativo, Boolean convenio) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder(); 
			hql.append("from Curso c where ");
			
			if (n != 0)
				hql.append("nivel = '" + n + "' and ");
			if (ativo)
				hql.append("ativo = '" + ativo + "' and ");
			if (convenio)
				hql.append("convenio = '" + convenio + "' and ");
			hql.append("c.propostaCurso.id in (select p.id from PropostaCursoLato p where p.situacaoProposta.id = 5)");
			
			Query q = getSession().createQuery(hql.toString());
			
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Consulta útil para montar a combobox de curso por nível. 
	 * 
	 * @param n
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNivelWithProjection(char n) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		String projecao = "curso.id, curso.nome, curso.unidade.sigla, curso.municipio.nome, curso.tipoCursoStricto.descricao," +
				" curso.modalidadeEducacao, curso.convenio.id, curso.convenio.descricao";
		hql.append("select ");
		hql.append(projecao);
		hql.append(" from Curso curso " +
				" left join curso.modalidadeEducacao mod" +
				" left join curso.municipio m " +
				" left join curso.convenio convenio " +
				" left join curso.tipoCursoStricto tc " +
				" left join curso.unidade u" +
				" where curso.ativo = trueValue() ");
		
		if (NivelEnsino.isAlgumNivelStricto(n)) 
			hql.append(" and curso.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()));
		else if( n != ' ') 
			hql.append(" and curso.nivel = '" + n + "'");
		
		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		Collection<Curso> resultado = HibernateUtils.parseTo(lista, projecao, Curso.class, "curso");
		
		return resultado;
	}
	

	/**
	 * Retorna todos os cursos de um centro
	 * 
	 * @param centro
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByCentro(int centro) throws DAOException{
		return findByUnidade(centro, ' ');
	}

	/**
	 * Busca todos os cursos da unidade desejada que possuam o nível de ensino informado
	 * 
	 * @param idUnidade
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByUnidade(int idUnidade, char nivelEnsino) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(Curso.class);
			c.createCriteria("unidade").add(Restrictions.eq("id", idUnidade));
			if (NivelEnsino.isValido(nivelEnsino)) {
				if (nivelEnsino == NivelEnsino.STRICTO) {
					c.add(Restrictions.in("nivel", new Character[] {NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO}));
				} else {
					c.add(Restrictions.eq("nivel", nivelEnsino));
				}
			}
			c.add(Restrictions.eq("ativo", true));
			c.addOrder(Order.asc("nivel"));
			c.addOrder(Order.asc("nome"));
			@SuppressWarnings("unchecked")
			Collection<Curso> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	

	/**
	 * Busca todos os cursos da unidade desejada que possuam o nível de ensino informado
	 * 
	 * @param centro
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByCentro(int centro, char nivelEnsino) throws DAOException{
		return findByUnidade(centro, nivelEnsino);
	}

	/**
	 * Busca cursos por centro
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<Unidade, LinhaListaCursos> findCursosCentro(char nivel) throws DAOException {

		Map<Unidade, LinhaListaCursos> lista = new TreeMap<Unidade, LinhaListaCursos>();

		StringBuilder hql = new StringBuilder();
		
		hql.append(" select c.unidade.gestora.id, c.id, c.unidade.gestora.nomeCapa, c.nome ");
		hql.append(" from Curso c ");
		hql.append(" where c.nivel = :nivel and c.convenio is null ");
		hql.append(" group by c.unidade.gestora.id, c.id, c.unidade.gestora.nomeCapa, c.nome ");
		hql.append(" order by c.unidade.gestora.id, c.id, c.unidade.gestora.nomeCapa, c.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setCharacter("nivel", nivel);

		List<Object> cursos = q.list();
		Iterator<Object> it = cursos.iterator();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		while (it.hasNext()) {
			int col = 0;
			Object[] colunas = (Object[]) it.next();

			int idUnidade = (Integer) colunas[col++];
			Unidade unidade = dao.findByPrimaryKey(idUnidade, Unidade.class);
			LinhaListaCursos linha = lista.get( unidade );
			if ( linha == null ) {
				linha = new LinhaListaCursos();
			}

			linha.setUnidade(unidade);
			int idCurso = (Integer) colunas[col++];
			Curso curso = dao.findByPrimaryKey(idCurso, Curso.class);
			
			linha.getCursos().add(curso);
			
			Collections.sort((List<Curso>) linha.getCursos());
			
			lista.put(unidade, linha);
		}
		dao.close();
		return lista;
	}

	/**
	 * Pega todos os cursos que a modalidade é ensino a distância
	 *  
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> findAllCursosADistancia() throws DAOException {

		try {
			Query q = getSession().createQuery("select distinct c from Curso c where c.modalidadeEducacao.id=? order by c.nome asc");
			q.setInteger(0, ModalidadeEducacao.A_DISTANCIA);
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todos os cursos de residência em saúde de uma determinada unidade responsável, geralmente um hospital.
	 *  
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> findAllCursosResidenciaByUnidadeResponsavel(int idUnidadeResponsavel) throws DAOException {
		
		try {
			Query q = getSession().createQuery("select distinct c from Curso c join c.unidade u " +
					" where c.nivel = ? and u.unidadeResponsavel.id = ?" +
					" order by c.nome asc");
			q.setCharacter(0, NivelEnsino.RESIDENCIA);
			q.setInteger(1, idUnidadeResponsavel);
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Pega todos os cursos de residência médica
	 *  
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> findAllCursosResidenciaMedica() throws DAOException {

		try {
			Query q = getSession().createQuery("select distinct c from Curso c where c.nivel = ? order by c.nome asc");
			q.setCharacter(0, NivelEnsino.RESIDENCIA);
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Pega todos os curso de graduação do EAD
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> findAllCursosGraduacaoADistancia() throws DAOException {

		try {
			Query q = getSession().createQuery("select distinct c from Curso c where c.modalidadeEducacao.id=? and c.nivel=? order by c.nome asc");
			q.setInteger(0, ModalidadeEducacao.A_DISTANCIA);
			q.setCharacter(1, NivelEnsino.GRADUACAO);
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}	

	/**
	 * Busca todos os cursos presenciais de graduação 
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Curso> findAllCursosGraduacaoPresenciais() throws DAOException {
		
		try {
			Query q = getSession().createQuery("select distinct c from Curso c where c.modalidadeEducacao.id=? and c.nivel=? and convenio is null order by c.nome asc");
			q.setInteger(0, ModalidadeEducacao.PRESENCIAL);
			q.setCharacter(1, NivelEnsino.GRADUACAO);
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Método responsável pelo retorno de todos os cursos por nível de ensino
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findAllOtimizado(char nivel, String nome) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
		
			hql.append(" select distinct c.id, c.nome, c.nivel, c.unidade.sigla, cc.id, cc.servidor.pessoa.nome, cc.ativo, cc.cargoAcademico.id, cc.dataInicioMandato, cc.dataFimMandato, c.nomeAscii ");
			
			if (nivel != NivelEnsino.DOUTORADO && nivel != NivelEnsino.MESTRADO && nivel != NivelEnsino.STRICTO ) {
				hql.append(" from Curso c " +
						   " left outer join c.coordenacoesCursos cc ");
				hql.append(" where cc is null or (  cc.dataInicioMandato < now() and cc.dataFimMandato > now())");
				hql.append(" and cc is null or (cc.cargoAcademico.id = "+ CargoAcademico.COORDENACAO + " and cc.ativo = trueValue()  )");
				hql.append(" and c.ativo = trueValue() ");
			} else {
				hql.append(" from Curso c, CoordenacaoCurso cc ");				
				hql.append(" where c.unidade = cc.unidade and cc.dataInicioMandato < now() and cc.dataFimMandato > now() ");
				hql.append(" and cc.cargoAcademico.id = "+ CargoAcademico.COORDENACAO );
				hql.append(" and c.ativo = trueValue() ");
			}		
			
			
			if(nivel == 'S') 
				hql.append(" and c.nivel in ('"+NivelEnsino.MESTRADO+"','"+NivelEnsino.DOUTORADO+"')");	
			else
				hql.append(" and c.nivel = '"+nivel+"'");			
			
			if (nome != null && !nome.trim().equals("")) {
				hql.append( " and c.nomeAscii like :nome ");
			}	
							
			hql.append(" order by c.nome ");			

			Query q = getSession().createQuery(hql.toString());
			
			if (nome != null && !nome.trim().equals("")) {
				q.setString("nome", "%"+ StringUtils.toAscii(nome.trim().toUpperCase()) + "%");
			}
			
			List<?> lista = q.list();
			ArrayList<Curso> result = new ArrayList<Curso>();

			for (int a = 0; a < lista.size(); a++) {
				Curso curso = new Curso();
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				boolean achou = false;

				curso.setId((Integer) colunas[col++]);

				if ( result.contains(curso) ) {
					curso = result.get(result.indexOf(curso));
					achou = true;
				}

				curso.setNome((String) colunas[col++]);
				curso.setNivel((Character) colunas[col++]);

				Unidade unidade = new Unidade();
				unidade.setSigla((String) colunas[col++]);
				curso.setUnidade(unidade);

				Set<CoordenacaoCurso> coordenadores = null;
				if ( achou ) {
					coordenadores = curso.getCoordenacoesCursos();
				} else {
					coordenadores = new HashSet<CoordenacaoCurso>();
				}
				CoordenacaoCurso cc = new CoordenacaoCurso();
				cc.setId((Integer) colunas[col++]);
				cc.getServidor().getPessoa().setNome((String) colunas[col++]);
				cc.setAtivo((Boolean) colunas[col++] );
				
				CargoAcademico ca = new CargoAcademico();
				ca.setId((Integer) colunas[col++]);
				
				cc.setCargoAcademico(ca);
				
				cc.setDataInicioMandato((Date) colunas[col++]);
				
				cc.setDataFimMandato((Date) colunas[col++]);

				coordenadores.add(cc);
				curso.setCoordenacoesCursos(coordenadores);
				
				if (!achou)
					result.add(curso);
			}

			return result;
		} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Método responsável por retornar todos os cursos stricto ativos da unidade especificada
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByPrograma(int idUnidade) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Curso.class);
			c.add(Restrictions.in("nivel", NivelEnsino.getNiveisStricto()));
			c.add(Restrictions.eq("ativo", true));
			c.add(Restrictions.eq("unidade.id", idUnidade));
			c.addOrder(Order.asc("nome"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os cursos que serão exibidos na parte pública do SIGAA.
	 * Lato Sensu e Técnico não possuem Município associado.
	 * Stricto Sensu: a coordenação do curso está associada ao programa.
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findConsultaPublica(Integer idUnidade, Character nivel, String nome, ModalidadeEducacao modalidade) throws DAOException {

		ResultSetExtractor cursoRSE = new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				
				Set<Curso> resultado = new LinkedHashSet<Curso>();
			
				while(rs.next()) {
					
					Curso curso = new Curso();
					curso.setId(rs.getInt("id_curso"));
					curso.setCodigo(rs.getString("codigo"));
					curso.setAtivo(rs.getBoolean("ativo"));
					curso.setNivel(rs.getString("nivel").charAt(0)); 
					curso.setNome((NivelEnsino.TECNICO == curso.getNivel() ? (curso.getCodigo() != null ? curso.getCodigo()+ " - " : ""): "")+rs.getString("nome"));
					
					Unidade unidade = new Unidade();
					unidade.setId(rs.getInt("id_unidade"));
					unidade.setNome(rs.getString("nomeUnidade"));
					unidade.setSigla(rs.getString("sigla"));
					
					NaturezaCurso naturezaCurso = new NaturezaCurso();
					naturezaCurso.setId(rs.getInt("id_natureza_curso"));
					
					// Tecnico não tem modalidade
					if (NivelEnsino.TECNICO != curso.getNivel()) {
						ModalidadeEducacao modalidadeEducacao = new ModalidadeEducacao();
						modalidadeEducacao.setId(rs.getInt("id_modalidade_educacao"));
						modalidadeEducacao.setDescricao(rs.getString("descricaoModalidade"));
						curso.setModalidadeEducacao(modalidadeEducacao);
					}
					
					AreaConhecimentoCnpq areaCurso = new AreaConhecimentoCnpq();
					Integer idAreaCurso = rs.getInt("id_area_conhecimento_cnpq");
					if (idAreaCurso == null) {
						idAreaCurso=0;
					}
					areaCurso.setId(idAreaCurso);
					
					TipoCursoStricto tipoCursoStricto = new TipoCursoStricto();
					Integer idTipoCursoStricto = rs.getInt("id_tipo_curso_stricto");
					if (idTipoCursoStricto == null) {
						idTipoCursoStricto=0;
					}
					tipoCursoStricto.setId(idTipoCursoStricto);
					
	
					//Nem Lato nem Tecnico possuem Municipio associado
					if (curso.getNivel() !=  NivelEnsino.LATO  && curso.getNivel() != NivelEnsino.TECNICO)
					{
						Municipio municipio = new Municipio();
						municipio.setId(rs.getInt("id_municipio"));
						municipio.setNome(rs.getString("nomeMunicipio"));
						curso.setMunicipio(municipio);
					}
					
					CoordenacaoCurso coordenacaoAtual = new CoordenacaoCurso();
					coordenacaoAtual.setId(rs.getInt("id_coordenacao_curso"));
					
					Servidor coordenadorServidor = new Servidor();
					coordenadorServidor.setId(rs.getInt("id_servidor"));
					Pessoa coordenadorPessoa = new Pessoa();
					coordenadorPessoa.setId(rs.getInt("id_pessoa"));
					coordenadorPessoa.setNome(rs.getString("nomePessoa"));
					coordenadorServidor.setSiape(rs.getInt("siape"));
					
					//Relacionando os Objetos
					coordenadorServidor.setPessoa(coordenadorPessoa);
					coordenacaoAtual.setServidor(coordenadorServidor);
					
					curso.setUnidade(unidade);
					curso.setNaturezaCurso(naturezaCurso);
					curso.setAreaCurso(areaCurso);
					curso.setTipoCursoStricto(tipoCursoStricto);
					curso.setCoordenacaoAtual(coordenacaoAtual);
					resultado.add(curso);
					
				}
				return resultado;
			}
		};
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT ");
			if (nivel.equals(NivelEnsino.TECNICO)) {
				//Nivel técnico não tem modalidade educação
				sql.append(" c.id_curso, c.codigo, c.nome, c.ativo, c.nivel, u.id_unidade, u.nome as nomeUnidade, u.sigla, " +
						" nc.id_natureza_curso, ac.id_area_conhecimento_cnpq, tcs.id_tipo_curso_stricto, ");
			}else {
				sql.append(" c.id_curso, c.codigo, c.nome, c.ativo, c.nivel, u.id_unidade, u.nome as nomeUnidade, u.sigla, " +
				" nc.id_natureza_curso, me.id_modalidade_educacao, me.descricao as descricaoModalidade, ac.id_area_conhecimento_cnpq," +
				" tcs.id_tipo_curso_stricto, ");
			}
			
			//Nem Lato nem Tecnico possuem Municipio associado
			if (!nivel.equals(NivelEnsino.LATO) && !nivel.equals(NivelEnsino.TECNICO)) 
				sql.append(" mu.id_municipio, mu.nome as nomeMunicipio, ");
			
			sql.append(" cd.id_coordenacao_curso, s.id_servidor, p.id_pessoa, p.nome as nomePessoa, s.siape ");
			sql.append(" FROM curso c  LEFT JOIN ensino.coordenacao_curso cd ON ");
			
			sql.append(" ( c.id_curso = cd.id_curso AND ( cd.data_fim_mandato is null OR cd.data_fim_mandato >= now() ) ");
			sql.append(" 	AND ( cd.id_cargo_academico IS NULL OR id_cargo_academico = " + CargoAcademico.COORDENACAO + ") )");
			
			if ( nivel.equals(NivelEnsino.LATO) ){ 
				sql.append(" INNER JOIN lato_sensu.curso_lato cl ON(cl.id_curso = c.id_curso) ");
				sql.append(" INNER JOIN lato_sensu.proposta_curso_lato pcl USING(id_proposta) ");
			}
			sql.append(" INNER JOIN comum.unidade u ON c.id_unidade = u.id_unidade  ");
			sql.append(" LEFT JOIN ensino.natureza_curso nc ON nc.id_natureza_curso = c.id_natureza_curso ");
			sql.append(" LEFT JOIN comum.modalidade_educacao me ON me.id_modalidade_educacao = c.id_modalidade_educacao ");
			sql.append(" LEFT JOIN comum.area_conhecimento_cnpq ac ON ac.id_area_conhecimento_cnpq = c.id_area_curso ");
			sql.append(" LEFT JOIN stricto_sensu.tipo_curso_stricto tcs ON tcs.id_tipo_curso_stricto = c.id_tipo_curso_stricto ");
			sql.append(" LEFT JOIN rh.servidor s ON s.id_servidor = cd.id_servidor ");
			sql.append(" LEFT JOIN comum.pessoa p ON s.id_pessoa = p.id_pessoa ");
			sql.append(" LEFT JOIN comum.municipio mu ON mu.id_municipio = c.id_municipio ");
			sql.append(" WHERE  c.ativo = trueValue() ");
						
			if (idUnidade != null) 
				sql.append(" AND u.id_unidade =" + idUnidade); 
			
			//Se for especificado stricto sensu pega tanto mestrado quanto doutorado
			if (nivel.equals(NivelEnsino.STRICTO)) {
				sql.append(" AND (c.nivel ='E' OR c.nivel ='D' OR c.nivel ='S') ");
			} else if (NivelEnsino.getDescricao(nivel)!="DESCONHECIDO") {
				sql.append(" AND c.nivel = '"+nivel+"'");
			}
			
			if (nivel.equals(NivelEnsino.LATO)) {
				sql.append(" AND ( cl.data_fim is null OR cl.data_fim >= NOW() ) ");
				sql.append(" AND pcl.id_situacao_proposta = " + SituacaoProposta.ACEITA ); 
			}
			
			if (nome != null && !nome.trim().equals("")) {
				sql.append( " AND " + UFRNUtils.toAsciiUpperUTF8("c.nome_ascii") + " LIKE  '%" + StringUtils.toAscii(nome.trim().toUpperCase())  + "%'");
			}
			if (modalidade != null && !nivel.equals(NivelEnsino.TECNICO)) {
				sql.append( " AND me.id_modalidade_educacao = " + modalidade.getId());
			}

			//Nem Lato nem Tecnico possuem Municipio associado
			//Técnico não tem modalidadeEducacao
			if (nivel.equals(NivelEnsino.TECNICO)) {
				sql.append(" ORDER BY u.sigla, c.nome_ascii asc");
			} else if( nivel.equals(NivelEnsino.LATO)) {
				sql.append(" ORDER BY u.sigla, c.nome_ascii asc, me.descricao");
			} else {
				sql.append(" ORDER BY u.sigla, c.nome_ascii asc, me.descricao, mu.nome");
			}

			return (Collection<Curso>) getJdbcTemplate(Sistema.SIGAA).query(sql.toString(), cursoRSE);
		
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	
	
	/**
	 * Todos os componentes curriculares do curso especificado
	 */
	@SuppressWarnings("unchecked")
	public List<ComponenteCurricular> findDisciplinasCursoDistanciaPorCurso(int idCurso) throws HibernateException, DAOException {

		String sql = 	" select distinct(cc.id_disciplina), ccdetalhes.nome, cc.codigo  " +
						" from graduacao.matriz_curricular matrizcompon " +
						" inner join graduacao.curriculo graduacurr ON graduacurr.id_matriz = matrizcompon.id_matriz_curricular " +
						" inner join graduacao.curriculo_componente curricompon ON curricompon.id_curriculo = graduacurr.id_curriculo " +
						" inner join curso curso ON curso.id_curso = graduacurr.id_curso " +
						" inner join ensino.componente_curricular cc ON cc.id_disciplina = curricompon.id_componente_curricular " +
						" left join ensino.componente_curricular_detalhes ccdetalhes ON ccdetalhes.id_componente = cc.id_disciplina " +
						" where curso.id_curso = ?" +
						"order by ccdetalhes.nome";

		return getJdbcTemplate().query(sql, new Object[] { idCurso }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				ComponenteCurricular cc = new ComponenteCurricular( rs.getInt("id_disciplina") );
				cc.setCodigo(rs.getString("codigo"));
				
				ComponenteDetalhes componenteDetalhes = new ComponenteDetalhes();
				componenteDetalhes.setNome( rs.getString("nome") + " - " + cc.getCodigo() );
				
				cc.setDetalhes(componenteDetalhes);

				return cc;
			}
		});

	}

	/**
	 * Todos os itens programas da disciplina especificada
	 * 
	 * @param idDisciplina
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ItemPrograma> findListaItemProgramaByDisciplina(int idDisciplina) {

		String sql = " select itemPrograma.aula, itemPrograma.conteudo, itemPrograma.id " +
					" from ensino.item_programa itemPrograma " +
					" inner join ensino.componente_curricular cc ON cc.id_disciplina = itemPrograma.id_componente_curricular " +
					" inner join ensino.componente_curricular_detalhes cdet ON cdet.id_componente_detalhes = cc.id_detalhe " +
					" where cc.id_disciplina = ?";

		return getJdbcTemplate().query(sql, new Object[] { idDisciplina }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				 ItemPrograma itemPrograma = new ItemPrograma();
				 itemPrograma.setId(rs.getInt("id"));
	 			 itemPrograma.setAula( rs.getInt("aula") );
	 			 itemPrograma.setConteudo( rs.getString("conteudo") );

	 			 return itemPrograma;

			}
		});

	}

	/**
	 * Retorna o item programa que possui o id especificado
	 * 
	 * @param idItemPrograma
	 * @return
	 * @throws DAOException
	 */
	public ItemPrograma findItemProgramaById(int idItemPrograma) throws DAOException {
		Criteria c = getSession().createCriteria(ItemPrograma.class);
				 c.add(Restrictions.eq("id", idItemPrograma));
				 return (ItemPrograma) c.uniqueResult();
	}
	
	
	
	/**
	 * Método que realiza a consulta sql para um relatório, e retorna uma Lista
	 * das linhas da consulta
	 *
	 * @param consultaSql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	public List<HashMap<String, Object>> executeSql(String consultaSql)
			throws SQLException, HibernateException, DAOException {

		PreparedStatement prepare = Database.getInstance().getSigaaConnection().prepareStatement(consultaSql);
		System.out.println("Relatório: "+consultaSql);
		ResultSet rs = prepare.executeQuery();
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		while (rs.next()) {
			result.add(UFRNUtils.resultSetToHashMap(rs));
		}
		return result;
	}
	
	/**
	 * Gera um relatório dos programas que não fizeram matricula on-line.
	 * Tendo como parâmetro de entrada o ano e o período.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<HashMap<String, Object>> relatorioProgramasNFezMatriculaOnline(Integer ano, Integer periodo) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(
				"select distinct u.nome as nome_programa"+
				" from curso c"+
				" inner join comum.unidade u using(id_unidade)"+
				" where c.nivel in ('S','E','D')"+
				" and c.id_unidade not in"+
				" (select distinct u1.id_unidade"+
				" from graduacao.solicitacao_matricula sm"+
				" inner join discente d1 on d1.id_discente = sm.id_discente"+
				" inner join curso c1 on c1.id_curso = d1.id_curso"+
				" inner join comum.unidade u1 on u1.id_unidade = c1.id_unidade"+
				" where d1.nivel in ('S','E','D')"+
				" and date_part('year', data_solicitacao) = "+ano+"");
			if (periodo == 1 ) {
				sqlconsulta.append(" and date_part('months', data_solicitacao) >=1 "+
						"and date_part('months', data_solicitacao)<= 6)");
			}else{
				sqlconsulta.append(" and date_part('months', data_solicitacao) >= 7"+
						"and date_part('months', data_solicitacao) <= 12) ");
			}

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Gera um relatório dos programas que não realizaram processo seletivo.
	 * Tendo como parâmetro de entrada o ano e o período.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<HashMap<String, Object>> relatorioProgramasNFezProcessoSeletivo(Integer ano, Integer periodo) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(
				"select distinct u.nome as nome_programa"+
				" from curso c"+
				" inner join comum.unidade u using(id_unidade)"+
				" where c.nivel in ('S','E','D')"+
				" and c.id_unidade not in "+
				" ( select u1.id_unidade"+
				" from ensino.processo_seletivo ps"+
				" inner join curso c1 on c1.id_curso = ps.id_curso"+
				" inner join comum.unidade u1 on u1.id_unidade = c1.id_unidade"+
				" where date_part('year', inicio_inscricoes) = "+ano+"");
			if (periodo == 1) {
			    sqlconsulta.append(" and date_part('months', inicio_inscricoes) >= 1"+
			    		" and date_part('months', inicio_inscricoes) <= 6)");
			}else {
				sqlconsulta.append(" and date_part('months', inicio_inscricoes) >=7 " +
						"and date_part('months', inicio_inscricoes) <= 12)");
			}
				sqlconsulta.append(" order by u.nome");
	
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	/**
	 * <p>
	 * Pesquisa por todos os cursos com reserva na graduação nos quais o docente especificado atua, ou seja, 
	 * ministra aula em alguma turma, sendo essa do ano e período letivo igual ao informado. 
	 * </p>
	 * 
	 * @param docente - Servidor ou Docente Externo
	 * @param ano - Ano letivo da turma
	 * @param periodo - Período letivo da turma
	 * 
	 * @return
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findCursosReservaNaGraduacaoByDocenteAnoPeriodo(Docente docente, int ano, int periodo) throws DAOException {
		
		String jdbcSql = "select distinct c.id_curso, c.nome, c.nivel, u.sigla, me.descricao, mu.nome as municipio from graduacao.reserva_curso rc "+
							"inner join graduacao.matriz_curricular m on rc.id_matriz_curricular = m.id_matriz_curricular "+
							"inner join curso c on m.id_curso = c.id_curso "+
							"inner join comum.unidade u on c.id_unidade = u.id_unidade "+
							"inner join comum.modalidade_educacao me on c.id_modalidade_educacao = me.id_modalidade_educacao "+
							"inner join comum.municipio mu on c.id_municipio = mu.id_municipio "+
							"left join ensino.turma t on rc.id_turma = t.id_turma "+
							"left join ensino.docente_turma dt on t.id_turma = dt.id_turma "+
							"where c.ativo = trueValue() and m.ativo = trueValue() and c.nivel = 'G' "+
							"and dt.id_docente = "+docente.getId()+" ";
		if(ano > 0){
			jdbcSql += "and t.ano = "+ano+" ";
		}
		if(periodo > 0){
			jdbcSql += "and t.periodo = "+periodo+" ";
		}
		
		return getJdbcTemplate().query(jdbcSql, new RowMapper() {
					
					public Object mapRow(ResultSet rs, int row) throws SQLException {
		
						Curso curso = new Curso(rs.getInt("id_curso"));
						curso.setNome(rs.getString("nome"));
						curso.setNivel(rs.getString("nivel").trim().charAt(0));
						
						curso.setUnidade(new Unidade());
						curso.getUnidade().setSigla(rs.getString("sigla"));
						
						curso.setModalidadeEducacao(new ModalidadeEducacao());
						curso.getModalidadeEducacao().setDescricao(rs.getString("descricao"));
						
						curso.setMunicipio(new Municipio());
						curso.getMunicipio().setNome(rs.getString("municipio"));
						
						return curso;
					}
				});	
	}
	
	/**
	 * <p>
	 * Pesquisa por todos os cursos ministrados pelo docente informado de acordo com o ano e período da turma.
	 * </p>
	 * 
	 * @param docente - Servidor ou Docente Externo
	 * @param ano - Ano letivo da turma
	 * @param periodo - Período letivo da turma
	 * 
	 * @return
	 * 
	 * @throws DAOException
	 */
	public Collection<Curso> findAllCursosByDocente(Docente docente, int ano, int periodo) throws DAOException {
		Collection<Curso> cursosAtuados = findCursosReservaNaGraduacaoByDocenteAnoPeriodo(docente, ano, periodo);
		
		String sql = "select distinct c.id_curso, c.nome, c.nivel, u.sigla, me.descricao, mu.nome as municipio " +
							"from ensino.docente_turma dt "+
							"join ensino.turma t using (id_turma) "+
							"inner join ensino.matricula_componente mc on (mc.id_turma=t.id_turma) " +
							"inner join discente d on (d.id_discente = mc.id_discente) " +
							"inner join curso c on (c.id_curso = d.id_curso) "+
							"join comum.unidade u on (u.id_unidade = c.id_unidade) "+
							"join comum.modalidade_educacao me on c.id_modalidade_educacao = me.id_modalidade_educacao "+
							"left join comum.municipio mu on c.id_municipio = mu.id_municipio ";
		
		sql += "where (id_docente = " + docente.getId() + " or id_docente_externo = " + docente.getId() + ") and c.ativo = true ";
		sql += "and d.status in " + UFRNUtils.gerarStringIn( StatusDiscente.getValidos() ) + " ";
		sql += "and mc.id_situacao_matricula in " + UFRNUtils.gerarStringIn( SituacaoMatricula.getSituacoesAtivas() ) + " ";
		
		if(ano > 0){
			sql += "and t.ano = " + ano + " and mc.ano = " + ano + " ";
		}
		if(periodo > 0){
			sql += "and t.periodo = " + periodo + " and mc.periodo = " + periodo + " ";
		}
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			rs = st.executeQuery();
			
			while(rs.next()){
				Curso curso = new Curso(rs.getInt("id_curso"));
				curso.setNome(rs.getString("nome"));
				curso.setNivel(rs.getString("nivel").trim().charAt(0));
				
				curso.setUnidade(new Unidade());
				curso.getUnidade().setSigla(rs.getString("sigla"));
				
				curso.setModalidadeEducacao(new ModalidadeEducacao());
				curso.getModalidadeEducacao().setDescricao(rs.getString("descricao"));
				
				curso.setMunicipio(new Municipio());
				curso.getMunicipio().setNome(rs.getString("municipio"));
				
				cursosAtuados.add(curso);
			}
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
		
		return cursosAtuados;
	}
	
	/**
	 * <p>
	 * Pesquisa por todos os cursos com reserva na graduação nos quais o docente especificado <strong>não</strong> atua, ou seja, 
	 * não ministra aula em alguma turma, sendo essa do ano e período letivo igual ao informado. 
	 * </p>
	 * 
	 * @param docente - Servidor ou Docente Externo
	 * @param ano - Ano letivo da turma
	 * @param periodo - Período letivo da turma
	 * 
	 * @return
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findCursosReservaNaGraduacaoSemParticipacaoDoDocenteNoAnoPeriodo(Docente docente, int ano, int periodo) throws DAOException {
		
		Collection<Curso> cursosComParticipacaoDoDocente = findCursosReservaNaGraduacaoByDocenteAnoPeriodo(docente, ano, periodo);
		StringBuilder cursosIds = new StringBuilder();
		
		cursosIds.append("0");
		for (Curso curso : cursosComParticipacaoDoDocente) {
			cursosIds.append(",");
			cursosIds.append(curso.getId());
		}
		
		//Para fins de otimização, esta consulta está sendo feita com JDBC
		String jdbcSql = "select distinct c.id_curso, c.nome,u.sigla, me.descricao, mu.nome as municipio from graduacao.reserva_curso rc " +
							"inner join graduacao.matriz_curricular m on rc.id_matriz_curricular = m.id_matriz_curricular " +
							"inner join curso c on m.id_curso = c.id_curso " +
							"inner join comum.unidade u on c.id_unidade = u.id_unidade " +
							"inner join comum.modalidade_educacao me on c.id_modalidade_educacao = me.id_modalidade_educacao " +
							"inner join comum.municipio mu on c.id_municipio = mu.id_municipio " +
							"left join ensino.turma t on rc.id_turma = t.id_turma " +
							"left join ensino.docente_turma dt on t.id_turma = dt.id_turma " +
							"where c.ativo = trueValue() and m.ativo = trueValue() and c.nivel = 'G' " + 
							"and c.id_curso not in (" + cursosIds.toString() + ")";	
		
		return getJdbcTemplate().query(jdbcSql, new RowMapper() {
			
			public Object mapRow(ResultSet rs, int row) throws SQLException {

				Curso curso = new Curso(rs.getInt("id_curso"));
				curso.setNome(rs.getString("nome"));
				
				curso.setUnidade(new Unidade());
				curso.getUnidade().setSigla(rs.getString("sigla"));
				
				curso.setModalidadeEducacao(new ModalidadeEducacao());
				curso.getModalidadeEducacao().setDescricao(rs.getString("descricao"));
				
				curso.setMunicipio(new Municipio());
				curso.getMunicipio().setNome(rs.getString("municipio"));
				
				return curso;
			}
		});
	}
	
	
	/**
	 * <p>
	 * Lista os e-mails dos discentes matriculados no curso.
	 * </p>
	 * 
	 * @param idCurso
	 * @return
	 * 
	 * @throws DAOException , HibernateException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> findEmailDiscenteByCurso ( int idCurso ) throws HibernateException, DAOException
	{
		Query q = getSession().createSQLQuery("select p.email from comum.usuario as u " +
												"inner join discente as d on u.id_pessoa = d.id_pessoa " +
												"inner join comum.pessoa as p on p.id_pessoa = d.id_pessoa " +
												"inner join curso using ( id_curso ) " +
												"where id_curso = "+idCurso+" and d.status in  "+gerarStringIn(StatusDiscente.getStatusComVinculo()));		
		List<Object[]> result = q.list();
	
		if ( result != null )
		{	
			ArrayList<String> emails = new ArrayList<String>();
			for ( Object linha : result )
				emails.add(linha.toString());
			
			return emails;
		}	

		return null;
	}
	
	/**
	 * Retornar os cursos com o nome e/ou código informado.
	 * 
	 * @param nome
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByNomeOrCodigo(String nome, String codigo, char nivelEnsino) throws DAOException {
		return findByNomeOrCodigo(nome, codigo, nivelEnsino, 0);
	}
	
	/**
	 * Retornar os cursos com o nome e/ou código informado.
	 * 
	 * @param nome
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByNomeOrCodigo(String nome, String codigo, char nivelEnsino, int idUnidade) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Curso.class);
			if (nome != null && !nome.equals("")) 
				c.add(Restrictions.ilike("nome", "%" + nome + "%"));
			if (codigo != null && !codigo.equals(""))
				c.add(Restrictions.ilike("codigo", codigo));
			if(idUnidade > 0)
				c.add(Restrictions.eq("unidade.id", idUnidade));

			c.add(Restrictions.eq("nivel", nivelEnsino));
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			c.addOrder(Order.asc("nome"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todos os cursos da unidade desejada que possuam o nível de ensino informado
	 * 
	 * @param idUnidade
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findByUnidade(int idUnidade, char nivelEnsino, Boolean ativo) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(Curso.class);
			c.createCriteria("unidade").add(Restrictions.eq("id", idUnidade));
			if (NivelEnsino.isValido(nivelEnsino)) {
				if (nivelEnsino == NivelEnsino.STRICTO) {
					c.add(Restrictions.in("nivel", new Character[] {NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO}));
				} else {
					c.add(Restrictions.eq("nivel", nivelEnsino));
				}
			}
			if (ativo != null)
				c.add(Restrictions.eq("ativo", true));
			c.addOrder(Order.asc("nivel"));
			c.addOrder(Order.asc("nome"));
			@SuppressWarnings("unchecked")
			Collection<Curso> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}