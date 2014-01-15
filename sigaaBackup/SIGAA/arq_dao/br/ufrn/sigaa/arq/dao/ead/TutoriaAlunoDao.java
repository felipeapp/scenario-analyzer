/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/03/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ilike;
import static org.hibernate.criterion.Restrictions.isNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.HorarioTutoria;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para acesso aos dados de tutoriais de alunos 
 * de Ensino a Distância.
 * 
 * @author Andre M Dantas
 * @author David Pereira
 *
 */
public class TutoriaAlunoDao extends GenericSigaaDAO {

	/**
	 * Busca a última tutoria ativa por aluno.
	 */
	public TutoriaAluno findUltimoByAluno(int aluno) throws DAOException {
		Criteria c = getSession().createCriteria(TutoriaAluno.class)
			.add(eq("aluno.id", aluno))
			.add(eq("ativo", true))
			.add(isNull("fim"));
		return (TutoriaAluno) c.uniqueResult();
	}


	/**
	 * Busca todas as tutoriais pelas quais o aluno já
	 * passou de acordo com o nome do aluno.
	 */
	public Collection<TutoriaAluno> findByAluno(String nome) throws DAOException {
		Criteria c = getSession().createCriteria(TutoriaAluno.class)
			.add(eq("ativo", true))
			.addOrder(desc("inicio"))
			.createCriteria("aluno").createCriteria("discente").createCriteria("pessoa")
			.add(ilike("nome", nome, MatchMode.ANYWHERE));

		@SuppressWarnings("unchecked")
		Collection<TutoriaAluno> lista = c.list();
		return lista;
	}

	/**
	 * Busca todas as tutoriais ativas de um tutor. 
	 */
	public Collection<TutoriaAluno> findByTutor(String nome) throws DAOException {
		Criteria c = getSession().createCriteria(TutoriaAluno.class)
			.add(eq("ativo", true))
			.addOrder(desc("inicio"))
			.createCriteria("tutor").createCriteria("pessoa")
			.add(ilike("nome", nome, MatchMode.ANYWHERE));

		@SuppressWarnings("unchecked")
		Collection<TutoriaAluno> lista = c.list();
		return lista;
	}

	
	/**
	 * Busca as tutoriais ativas de um tutor de acordo com o id
	 * da pessoa do tutor.
	 */
	public Collection<TutoriaAluno> findByTutor(Pessoa pessoa) throws DAOException {
		Query q = getSession().createQuery("select t1.id, t1.aluno.id, d.matricula, d.pessoa.nome, d.status " +
				" from TutoriaAluno t1 left join t1.tutor tu left join tu.pessoa p1, "
				+ "TutoriaAluno t2 left join t2.aluno al left join al.discente d left join d.pessoa p2 "
				+ "where t1.id = t2.id and t1.ativo = trueValue() and p1.id = ? and d.status = 1 order by p2.nome asc");
		q.setInteger(0, pessoa.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		List<TutoriaAluno> tutorias = new ArrayList<TutoriaAluno>();
		for (Iterator<Object[]> it = result.iterator(); it.hasNext(); ) {
			Object[] linha =  it.next();
			int a = 0;
			TutoriaAluno tutoria = new TutoriaAluno();
			tutoria.setId((Integer) linha[a++]);
			DiscenteGraduacao discente = new DiscenteGraduacao();
			discente.setPessoa(new Pessoa());
			discente.setId((Integer) linha[a++]);
			discente.setMatricula((Long) linha[a++]);
			discente.getPessoa().setNome((String) linha[a++]);
			discente.setStatus((Integer) linha[a++]);
			tutoria.setAluno(discente);
			tutorias.add(tutoria);
		}
			
		return tutorias;
	}


	/**
	 * Retorna uma lista de discentes de acordo com matrícula,
	 * nome do discente, nome do curso ou pessoa do tutor.
	 */
	public Collection<DiscenteGraduacao> findDiscentesByMatriculaNomeCursoOuTutor(Long matricula, String nome, String curso, Pessoa pessoaTutor) throws DAOException {
		
		StringBuilder sql = new StringBuilder("select dg.id_discente_graduacao, d.matricula, pessoaDiscente.nome, d.status, curso.nome as nome_curso, pessoaTutor.nome as nome_tutor ");
		
		sql.append(" from graduacao.discente_graduacao dg "
				+ " inner join ead.polo polo using (id_polo) "
				+ " inner join discente d on (d.id_discente = dg.id_discente_graduacao) "
				+ " inner join comum.pessoa pessoaDiscente using (id_pessoa)  "
				+ " inner join curso curso on (curso.id_curso = d.id_curso) "
				+ "	left join ead.tutoria_aluno ta on (ta.id_discente = dg.id_discente_graduacao and ta.ativo = true) "
				+ "	left join ead.tutor_orientador tutor on (tutor.id_tutor_orientador = ta.id_tutor) "
				+ " left join comum.pessoa pessoaTutor on (pessoaTutor.id_pessoa = tutor.id_pessoa) ");
		
		sql.append(" where d.nivel = ? and polo.id_polo is not null and d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getValidos()) );
		
		if (ValidatorUtil.isNotEmpty(pessoaTutor))
			sql.append(" and pessoaTutor.id_pessoa = " +  pessoaTutor.getId());
				
		if (matricula != null)
			sql.append(" and d.matricula = ? ");
		if (nome != null)
			sql.append(" and upper(pessoaDiscente.nome_ascii) like ? ");
		if (curso != null)
			sql.append(" and upper(curso.nome_ascii) like ? ");

		sql.append(" order by pessoaDiscente.nome asc");

		Query q = getSession().createSQLQuery(sql.toString());
		int indice = 0;
		
		q.setCharacter(indice++, NivelEnsino.GRADUACAO);
		
		if (matricula != null) 
			q.setLong(indice++, matricula);
		if (nome != null) 
			q.setString(indice++, StringUtils.toAsciiAndUpperCase(nome) + "%");
		if (curso != null) 
			q.setString(indice++, StringUtils.toAsciiAndUpperCase(curso) + "%");
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		List<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
		for (Iterator<Object[]> it = result.iterator(); it.hasNext(); ) {
			Object[] linha =  it.next();
			DiscenteGraduacao discente = new DiscenteGraduacao();
			discente.setPessoa(new Pessoa());
			discente.setId((Integer) linha[0]);
			
			
			if (((BigInteger) linha[1]) != null)
				discente.setMatricula(((BigInteger) linha[1]).longValue());
			
			discente.getPessoa().setNome((String) linha[2]);
			discente.setStatus((Short) linha[3]);
			discente.setCurso(new Curso());
			discente.getCurso().setNome((String) linha[4]);
			TutorOrientador tutor = new TutorOrientador();
			tutor.setPessoa(new Pessoa());
			if (ValidatorUtil.isNotEmpty((String) linha[5])){
				tutor.getPessoa().setNome((String) linha[5]);
				discente.setTutores(new ArrayList<TutorOrientador>());
				discente.getTutores().add(tutor);
			}	
			discentes.add(discente);
		}
			
		return discentes;
	}
	
	/**
	 * Retorna uma coleção de discentes cuja tutoria está ativa
	 * de acordo com a pessoa do tutor.
	 */	
	public Collection<DiscenteGraduacao> findDiscentesByTutor(Integer idPessoa) throws DAOException {
		Query q = getSession().createQuery("select a.id, a.matricula, pa.nome, a.status,  pa.email " +
				" from  TutoriaAluno ta1 left join ta1.aluno grad left join grad.discente a "
				+ "left join a.pessoa pa, TutoriaAluno ta2 left join ta2.tutor tu left join tu.pessoa pt "
				+ "where ta1.id = ta2.id and ta1.ativo = trueValue() and pt.id = ? and a.status in " + gerarStringIn(StatusDiscente.getStatusComVinculo()) + " order by pa.nome asc");
		q.setInteger(0, idPessoa);
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		List<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
		for (Iterator<Object[]> it = result.iterator(); it.hasNext(); ) {
			Object[] linha =  it.next();
			DiscenteGraduacao discente = new DiscenteGraduacao();
			discente.setPessoa(new Pessoa());
			discente.setId((Integer) linha[0]);
			discente.setMatricula((Long) linha[1]);
			discente.getPessoa().setNome((String) linha[2]);
			discente.setStatus((Integer) linha[3]);
			discente.getPessoa().setEmail((String) linha[4]);
			discentes.add(discente);
		}
			
		return discentes;
	}
	
	/**
	 * Retorna todos os discentes associados a um curso.
	 */	
	public Collection<DiscenteGraduacao> findDiscentesByCurso(Curso curso) throws DAOException {
		Query q = getSession().createQuery("select d.id, d.matricula, p.nome, d.status,  p.email " +
				" from  Discente d join d.pessoa p "
				+ "where d.curso.id = ? and d.status in " + gerarStringIn(StatusDiscente.getStatusComVinculo()) + " order by p.nome asc");
		q.setInteger(0, curso.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		List<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
		for (Iterator <Object[]> it = result.iterator(); it.hasNext(); ) {
			Object[] linha =  it.next();
			DiscenteGraduacao discente = new DiscenteGraduacao();
			discente.setPessoa(new Pessoa());
			discente.setId((Integer) linha[0]);
			discente.setMatricula((Long) linha[1]);
			discente.getPessoa().setNome((String) linha[2]);
			discente.setStatus((Integer) linha[3]);
			discente.getPessoa().setEmail((String) linha[4]);
			discentes.add(discente);
		}
			
		return discentes;
	}

	/**
	 * Retorna todos os discentes associados a um curso.
	 */	
	public Collection<DiscenteGraduacao> findDiscentesByCursoPolo(Curso curso , Polo polo) throws DAOException {
		Query q = getSession().createQuery("select d.id, d.matricula, p.nome, d.status,  p.email " +
				" from  DiscenteGraduacao dg join dg.discente d join d.pessoa p "
				+ "where d.curso.id = ? and dg.polo.id = ? and d.status in " + gerarStringIn(StatusDiscente.getStatusComVinculo()) + " order by p.nome asc");
		q.setInteger(0, curso.getId());
		q.setInteger(1, polo.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		List<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
		for (Iterator <Object[]> it = result.iterator(); it.hasNext(); ) {
			Object[] linha =  it.next();
			DiscenteGraduacao discente = new DiscenteGraduacao();
			discente.setPessoa(new Pessoa());
			discente.setId((Integer) linha[0]);
			discente.setMatricula((Long) linha[1]);
			discente.getPessoa().setNome((String) linha[2]);
			discente.setStatus((Integer) linha[3]);
			discente.getPessoa().setEmail((String) linha[4]);
			discentes.add(discente);
		}
			
		return discentes;
	}
	
	
	/**
	 * Retorna as tutorias ativas paginadas, independente de tutor
	 * ou de discente.
	 */
	public Collection<TutoriaAluno> findTutoriaPaginadas(int primeiroRegistro, int registroPorPagina) throws DAOException {
		Criteria c = getSession().createCriteria(TutoriaAluno.class)
			.add(eq("ativo", true))
			.addOrder(asc("tutor"))
			.addOrder(asc("aluno"))
			.addOrder(desc("inicio"));
		
		if (registroPorPagina != 0) {
			c.setFirstResult(primeiroRegistro);
			c.setMaxResults(registroPorPagina);
		}

		@SuppressWarnings("unchecked")
		Collection<TutoriaAluno> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna a quantidade total de tutorias 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer countTutorias() throws HibernateException, DAOException {
		Query q = getSession().createSQLQuery("select count(*) from ead.tutoria_aluno " +
			"where ativo = trueValue()");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}
	
	/**
	 * Retorna tutoriais ativas por polo.
	 */
	public Collection<TutoriaAluno> findAtivoByPolo(Polo polo) {
		return null;
	}

	/**
	 * Retorna tutoriais ativas por tutor e curso.
	 */
	public Collection<TutoriaAluno> findAtivoByTutorCurso(Polo polo, int id) {
		return null;
	}

	/**
	 * Retorna tutoriais ativas por discente e curso.
	 */
	public Collection<TutoriaAluno> findAtivoByDiscenteCurso(Polo polo, int id) {
		return null;
	}

	/**
	 * Retorna os horários de tutoria de acordo
	 * com as informações de pólo e curso passadas. 
	 */
	public List<HorarioTutoria> findHorarios(Integer dia, Character turno, Polo p, Curso c) throws DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append("from HorarioTutoria h where 1 = 1 ");

		if (dia != null && dia > 0)
			sb.append(" and h.diaSemana = " + dia);
		if (p != null && p.getId() > 0)
			sb.append(" and h.tutoria.aluno.polo.id = " + p.getId());
		if (c != null && c.getId() > 0)
			sb.append(" and h.tutoria.aluno.discente.curso.id = " + c.getId());

		if (turno != null) {
			if (turno == 'M')
				sb.append(" and h.matutino = trueValue()");
			if (turno == 'T')
				sb.append(" and h.vespertino = trueValue()");
			if (turno == 'N')
				sb.append(" and h.noturno = trueValue()");
		}

		sb.append( " order by h.diaSemana asc, h.matutino asc, h.vespertino asc, h.noturno asc, h.tutoria.aluno.discente.pessoa.nome");
		
		@SuppressWarnings("unchecked")
		List<HorarioTutoria> lista = getSession().createQuery(sb.toString()).list();
		return lista;
	}

}
