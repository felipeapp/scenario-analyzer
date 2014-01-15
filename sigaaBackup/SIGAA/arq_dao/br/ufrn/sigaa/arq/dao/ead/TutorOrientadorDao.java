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
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Restrictions.eq;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.HorarioTutor;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.VinculoTutor;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para acesso aos dados de TutoriOrientador
 * @author Andre M Dantas
 *
 */
public class TutorOrientadorDao extends GenericSigaaDAO {


	/**
	 * Retorna uma coleção de {@link TutorOrientador} associados ao polo passado.
	 * 
	 * @param idPolo
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutorOrientador> findByPolo(int idPolo) throws DAOException {
		try {
			
			Query q = getSession().createQuery(" select t from TutorOrientador t " +
											   " left join t.poloCurso.polo p " +
											   " left join t.pessoa pess " +
											   " where p.id = ? and t.ativo = trueValue()" +
											   " order by pess.nome asc ");
			q.setInteger(0, idPolo);
			
			@SuppressWarnings("unchecked")
			Collection<TutorOrientador> lista = q.list();
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna uma coleção de {@link TutorOrientador} associados ao polo e curso passados.
	 * 
	 * @param idPolo
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutorOrientador> findByPoloCurso(Integer idPolo, Integer idCurso) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TutorOrientador.class);
			c.add(eq("ativo", true));
			Criteria subC = c.createCriteria("poloCurso");
			if (idPolo != null && idPolo > 0)
				subC.add(Expression.eq("polo.id", idPolo));
			if (idCurso != null && idCurso > 0)
				subC.add(Expression.eq("curso.id", idCurso));
			
			@SuppressWarnings("unchecked")
			Collection<TutorOrientador> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os horários de atendimento de um tutor.
	 * 
	 * @param tutor
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTutor> findHorariosByTutor(TutorOrientador tutor) throws DAOException {
		Criteria c = getSession().createCriteria(HorarioTutor.class);
		c.add(Expression.eq("tutor", tutor));

		@SuppressWarnings("unchecked")
		List<HorarioTutor> lista = c.list();
		return lista;
	}

	/**
	 * Busca pelos tutores de acordo com os parâmetros passados.
	 * 
	 * @param curso
	 * @param polo
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public List<TutorOrientador> findByCursoPoloNome(Curso curso, Polo polo, String nome, boolean somenteAtivos, boolean somenteComOrientados) throws DAOException {
		
		String count =  " select count(*) from graduacao.discente_graduacao dg "+
						 " join discente d on d.id_discente = dg.id_discente_graduacao "+ 
						 " where d.status in "+gerarStringIn(StatusDiscente.getStatusComVinculo())+" and d.id_curso = c.id_curso and dg.id_polo = pl.id_polo ";
		
		String sql = " select distinct t.id_tutor_orientador, t.tipo, t.ativo, p.id_pessoa , p.nome, u.login , u.inativo , pc.id_polo_curso , pl.id_polo , " +
					 " m.nome as mun , uf.sigla , c.id_curso , c.nome as cur , "+
					 " ( "+count+" ) as qtd "+
					 " from ead.tutor_orientador t "+
					 " join ead.tutor_polo_curso tpc on tpc.id_tutor_orientador = t.id_tutor_orientador "+
					 " join ead.polo_curso pc on pc.id_polo_curso = tpc.id_polo_curso "+
					 " join ead.polo pl on pl.id_polo = pc.id_polo "+
					 " left join comum.municipio m on m.id_municipio = pl.id_cidade "+
					 " left join comum.unidade_federativa uf on uf.id_unidade_federativa = m.id_unidade_federativa "+
					 " join curso c on c.id_curso = pc.id_curso "+
					 " left join comum.pessoa p on p.id_pessoa = t.id_pessoa "+
					 " left join comum.usuario u on u.id_pessoa = p.id_pessoa and u.inativo = falseValue() "+
					 " where 1 = 1 " +
					 (polo != null && polo.getId() > 0 ? "and pl.id_polo = :idPolo " : "") +
					 (curso != null && curso.getId() > 0 ? "and c.id_curso = :idCurso " : "") +
					 (!StringUtils.isEmpty(nome) ? "and p.nome_ascii ilike :nome " : "") +
					 (somenteAtivos ? "and t.ativo = trueValue() " : "") +
					 "order by p.nome";
				
		Query q = getSession().createSQLQuery(sql);
		
		if (polo != null && polo.getId() > 0)
			q.setInteger("idPolo", polo.getId());
		if (curso != null && curso.getId() > 0)
			q.setInteger("idCurso", curso.getId());
		if (!StringUtils.isEmpty(nome))
			q.setString("nome", StringUtils.toAscii(nome) + "%");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = q.list();
		List<TutorOrientador> listaTutores = new ArrayList<TutorOrientador>();
		TutorOrientador t = null;
		
		Integer idTutor = 0;
		if (ls!=null){
			for (Object [] linha : ls){
			
				Integer i = 0;
				Integer newIdTutor = (Integer)linha[i++];
				if (idTutor.intValue() == newIdTutor.intValue())
					i = 7;
				else {	
					
					t = new TutorOrientador();
					t.setId(newIdTutor);
					t.setTipo((Character)linha[i++]);
					t.setAtivo((Boolean)linha[i++]);
				
					Pessoa p = new Pessoa();
					p.setId((Integer)linha[i++]);
					p.setNome((String)linha[i++]);
				
					String login = (String)linha[i++];
					if (login!=null){
						Usuario u = new Usuario();
						u.setLogin(login);
						u.setInativo((Boolean)linha[i++]);
						t.setUsuario(u);
					} else
						i++;
					
					t.setPessoa(p);
					t.setTotalOrientandos((long) 0);
					t.setPoloCursos(new ArrayList<PoloCurso>());
					
					idTutor = newIdTutor;
					listaTutores.add(t);
				}
				
				PoloCurso pc = new PoloCurso();
				pc.setId((Integer)linha[i++]);
				
				Polo pl = new Polo();
				pl.setId((Integer)linha[i++]);
				pl.setCidade(new Municipio());
				pl.getCidade().setNome((String)linha[i++]);
				pl.getCidade().setUnidadeFederativa(new UnidadeFederativa());
				pl.getCidade().getUnidadeFederativa().setSigla((String)linha[i++]);
				pc.setPolo(pl);
				
				Curso c = new Curso();
				c.setId((Integer)linha[i++]);
				c.setNome((String)linha[i++]);
				pc.setCurso(c);
				
				if (t!=null){
					t.setPoloCurso(pc);
					t.getPoloCursos().add(pc);
					t.setTotalOrientandos(t.getTotalOrientandos()+((Number)linha[i++]).longValue());
				}
			}		
			
			if (somenteComOrientados){
				List<TutorOrientador> tutoresARemover = new ArrayList<TutorOrientador>();
				for (TutorOrientador tutor : listaTutores)
					if (tutor.getTotalOrientandos()==0)
						tutoresARemover.add(tutor);
				for (TutorOrientador tutor : tutoresARemover)
					listaTutores.remove(tutor);
			}
			return listaTutores;
		}
		return null;
	}

	/**
	 * Retorna os horários encontrados de acordo com os dados passados.
	 * 
	 * @param dia
	 * @param turno
	 * @param p
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTutor> findHorarios(Integer dia, Character turno, Polo p, Curso c) throws DAOException {
		
		String sql = " select h.id , h.id_tutor , p.nome , h.dia , h.hora_inicio , h.hora_fim  from ead.horario_tutor h "+
					 " join ead.tutor_orientador t on t.id_tutor_orientador = h.id_tutor "+
					 " join comum.pessoa p on p.id_pessoa = t.id_pessoa "+
					 " join ead.tutor_polo_curso tpc on tpc.id_tutor_orientador = t.id_tutor_orientador "+
					 " join ead.polo_curso pc on pc.id_polo_curso = tpc.id_polo_curso "+
					 " where 1=1 ";
		
		if (dia != null && dia > 0)
			sql+=" and h.dia = " + dia;
		if (p != null && p.getId() > 0)
			sql +=" and pc.id_polo = " + p.getId();
		if (c != null && c.getId() > 0)
			sql+=" and pc.id_curso = " + c.getId();
		
		if (turno != null) {
			if (turno == 'M')
				sql+=" and h.hora_inicio >= 7 or h.hora_fim <= 12";
			if (turno == 'T')
				sql+=" and h.hora_inicio >= 13 or h.hora_fim <= 18";
			if (turno == 'N')
				sql+=" and h.hora_inicio >= 19 or h.hora_fim <= 23";
		}
		
		sql+= "order by h.dia asc, h.hora_inicio asc, h.hora_fim asc, p.nome";
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		List<HorarioTutor> horarios = new ArrayList<HorarioTutor>();

		if (lista!= null){
			for (Object[] linha:lista){
				Integer i = 0;
				HorarioTutor h = new HorarioTutor();
				h.setId((Integer)linha[i++]);
				h.setTutor(new TutorOrientador());
				h.getTutor().setId((Integer)linha[i++]);
				h.getTutor().setPessoa(new Pessoa());
				h.getTutor().getPessoa().setNome((String)linha[i++]);
				h.setDia(((Number)linha[i++]).byteValue());
				h.setHoraInicio(((Number)linha[i++]).byteValue());
				h.setHoraFim(((Number)linha[i++]).byteValue());
				horarios.add(h);
			}
			
			return horarios;
		}
		return null;
	}

	
	/**
	 * Retorna uma lista de usuários dos tutores associados ao curso e polo passados.
	 * 
	 * @param curso
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	public List<Usuario> findUsuariosTutores(Curso curso, Polo polo) throws DAOException {
		
		String sql = "select u.id_usuario, u.login, p.nome " +
				"from comum.usuario u join comum.pessoa p using (id_pessoa) " +
				"join ead.tutor_orientador t using (id_pessoa) " +
				"join ead.tutor_polo_curso tpc on tpc.id_tutor_orientador = t.id_tutor_orientador " +
				"join ead.polo_curso pc on pc.id_polo_curso = tpc.id_polo_curso " +
				"where t.ativo = trueValue() and t.tipo = '"+TutorOrientador.TIPO_TUTOR_PRESENCIAL+ "' "+
				(!isEmpty(curso) ? "and pc.id_curso = " + curso.getId() + " " : "") +
				(!isEmpty(polo) ? "and pc.id_polo = " + polo.getId() + " " : "") +
				" group by u.id_usuario, u.login, p.nome " +
				" order by p.nome";

		@SuppressWarnings("unchecked")
		List<Object []> result = getSession().createSQLQuery(sql).list();
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		for (Iterator<Object []> it = result.iterator(); it.hasNext(); ) {
			Object[] linha = it.next();
			Usuario u = new Usuario();
			u.setId((Integer) linha[0]);
			u.setLogin((String) linha[1]);
			u.setPessoa(new Pessoa());
			u.getPessoa().setNome((String) linha[2]);
			usuarios.add(u);
		}
		
		return usuarios;
	}

	/**
	 * Retorna uma lista de usuários dos tutores associados ao curso e polo passados.
	 * 
	 * @param curso
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	public List<Usuario> findUsuariosTutoresDistancia() throws DAOException {
		
		String sql = "select u.id_usuario, u.login, p.nome " +
				"from comum.usuario u join comum.pessoa p using (id_pessoa) " +
				"join ead.tutor_orientador t using (id_pessoa) " +
				"where t.ativo = trueValue() and t.tipo = '"+TutorOrientador.TIPO_TUTOR_A_DISTANCIA+ "' "+
				" group by u.id_usuario, u.login, p.nome " +
				" order by p.nome";

		@SuppressWarnings("unchecked")
		List<Object []> result = getSession().createSQLQuery(sql).list();
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		for (Iterator<Object []> it = result.iterator(); it.hasNext(); ) {
			Object[] linha = it.next();
			Usuario u = new Usuario();
			u.setId((Integer) linha[0]);
			u.setLogin((String) linha[1]);
			u.setPessoa(new Pessoa());
			u.getPessoa().setNome((String) linha[2]);
			usuarios.add(u);
		}
		
		return usuarios;
	}

	
	/**
	 * Retorna o {@link TutorOrientador} associado a pessoa passada.
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public TutorOrientador findAtivoByDiscenteGraduacaoPessoa(int idDiscente, int idPessoaTutor) throws DAOException {
		Query q = getSession().createQuery("SELECT t.tutor.id " +
				" FROM TutoriaAluno t " +
				" WHERE t.aluno.id = :idDiscente " +
				" AND t.tutor.pessoa.id = :idPessoa " +
				" AND t.ativo = trueValue() ");
		q.setInteger("idDiscente", idDiscente);
		q.setInteger("idPessoa", idPessoaTutor);
		q.setMaxResults(1);
		int id = (Integer) q.uniqueResult();
		TutorOrientador retorno = new TutorOrientador();
		retorno.setId(id);
		return retorno;
	}
	
	/**
	 * Retorna uma lista de discentes associados ao tutor podendo serem filtrados pelo pólo ou curso.
	 * 
	 * @param curso
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findDiscentesByTutor(Integer idTutor, Integer idPolo, Integer idCurso) throws DAOException {
		
		String sql = "  select dg.id_discente_graduacao, d.matricula, p.nome, d.status, d.id_curso,  p.email, u.id_usuario " +
					 "  from graduacao.discente_graduacao dg " +
					 "	join discente d on d.id_discente = dg.id_discente_graduacao " +
					 "  join comum.pessoa p on d.id_pessoa = p.id_pessoa "+
					 "  join comum.usuario u on u.id_pessoa = p.id_pessoa "+
					 "	join ead.polo_curso pc on pc.id_polo = dg.id_polo and pc.id_curso = d.id_curso " +
					 "	join ead.tutor_polo_curso tpc on tpc.id_polo_curso = pc.id_polo_curso " +
					 "	join ead.tutor_orientador t on t.id_tutor_orientador = tpc.id_tutor_orientador " +
		 			 "  where t.id_tutor_orientador = " +idTutor+ "	and t.ativo = trueValue() " +
		 			 "  and d.status in " +gerarStringIn(StatusDiscente.getStatusComVinculo())+ 
		 			 "  and u.inativo = falseValue()";
		
		if (idPolo!=null)
			sql+=" and pc.id_polo = " +idPolo+ " ";
		if (idCurso!=null)
			sql+=" and pc.id_curso = " +idCurso+ " ";
		
		sql+=" order by p.nome ";

		Query q = getSession().createSQLQuery(sql);	
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.list();
		List<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
		if (results!=null){
			for (Object[] linha : results) {
				Integer i = 0;
				DiscenteGraduacao discente = new DiscenteGraduacao();
				discente.setPessoa(new Pessoa());
				discente.setId((Integer) linha[i++]);
				discente.setMatricula(((Number) linha[i++]).longValue());
				discente.getPessoa().setNome((String) linha[i++]);
				discente.setStatus(((Number) linha[i++]).intValue());
				discente.setCurso(new Curso());
				discente.getCurso().setId((Integer) linha[i++]);
				discente.getPessoa().setEmail((String) linha[i++]);
				discente.setUsuario(new Usuario());
				discente.getUsuario().setId((Integer) linha[i++]);
				discentes.add(discente);
			}
		}	
		return discentes;
	}
	
	/**
	 * Retorna o tutor de um discente verificando o pólo e o curso do discente.
	 * 
	 * @param curso
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	public TutorOrientador findTutorByDiscente(DiscenteAdapter d) throws DAOException {
		
		String sql = "  select t.id_tutor_orientador , t.tipo , t.id_vinculo , p.id_pessoa , p.nome from ead.tutor_orientador t "+
					 "  join comum.pessoa p on p.id_pessoa = t.id_pessoa " +
					 "	join ead.tutor_polo_curso tpc on tpc.id_tutor_orientador = t.id_tutor_orientador "+
					 "	join ead.polo_curso pc on pc.id_polo_curso = tpc.id_polo_curso "+
					 "	join ead.polo pl on pl.id_polo = pc.id_polo "+
					 "	join curso c on c.id_curso = pc.id_curso "+
					 "	join discente d on d.id_curso = c.id_curso "+
					 "	join graduacao.discente_graduacao dg on dg.id_discente_graduacao = d.id_discente and pl.id_polo = dg.id_polo "+
					 "	where d.id_discente = "+d.getId()+" and t.ativo = trueValue() ";

		Query q = getSession().createSQLQuery(sql);	
		q.setMaxResults(1);
		
		Object[] result = (Object[]) q.uniqueResult();
		if (result != null){
			TutorOrientador t = new TutorOrientador();
			Integer i = 0;
			t.setId((Integer) result[i++]);
			t.setTipo((Character) result[i++]);
			VinculoTutor v = new VinculoTutor();
			v.setId((Integer) result[i++]);
			Pessoa p = new Pessoa();
			p.setId((Integer) result[i++]);
			t.setVinculo(v);
			t.setPessoa(p);
			return t;
		}
		return null;
	}
	
	/**
	 * Retorna o tutor de um discente verificando o pólo e o curso do discente.
	 * 
	 * @param curso
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<TutorOrientador> findTutoresByDiscente(DiscenteAdapter d) throws DAOException {
		
		String sql = "  select t.id_tutor_orientador , t.tipo , t.id_vinculo , p.id_pessoa , p.nome from ead.tutor_orientador t "+
					 "  join comum.pessoa p on p.id_pessoa = t.id_pessoa " +
					 "	join ead.tutor_polo_curso tpc on tpc.id_tutor_orientador = t.id_tutor_orientador "+
					 "	join ead.polo_curso pc on pc.id_polo_curso = tpc.id_polo_curso "+
					 "	join ead.polo pl on pl.id_polo = pc.id_polo "+
					 "	join curso c on c.id_curso = pc.id_curso "+
					 "	join discente d on d.id_curso = c.id_curso "+
					 "	join graduacao.discente_graduacao dg on dg.id_discente_graduacao = d.id_discente and pl.id_polo = dg.id_polo "+
					 "	where d.id_discente = "+d.getId()+" and t.ativo = trueValue() "+
					 "  order by p.nome ";

		Query q = getSession().createSQLQuery(sql);	
		
		List <Object []> result = q.list();
		if (result != null){
			ArrayList<TutorOrientador> listaTutores = new ArrayList<TutorOrientador>();
			for (Object [] linha : result){
				Integer i = 0;
				TutorOrientador t = new TutorOrientador();
				t.setId((Integer)linha[i++]);
				t.setTipo((Character)linha[i++]);
				t.setVinculo(new VinculoTutor());
				t.getVinculo().setId((Integer)linha[i++]);
				t.setPessoa(new Pessoa());
				t.getPessoa().setId((Integer)linha[i++]);
				t.getPessoa().setNome((String)linha[i++]);
				listaTutores.add(t);	
			}
			return listaTutores;
		}
		return null;
	}
	
	/**
	 * Retorna os pólos-cursos que já possuem tutores diferentes do tutor passado como parâmetro .
	 * 
	 * @param curso
	 * @param polo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<PoloCurso> validarPoloCursosTutor(TutorOrientador t) throws DAOException {
		
		String sql = "  select pc.id_polo_curso, c.nome, m.nome as mcid , u.sigla from ead.tutor_polo_curso tpc " +
					 "  join ead.tutor_orientador t on t.id_tutor_orientador = tpc.id_tutor_orientador "+
					 "  join ead.polo_curso pc on tpc.id_polo_curso = pc.id_polo_curso "+
					 "  join ead.polo p on p.id_polo = pc.id_polo "+
					 "  join comum.municipio m on p.id_cidade = m.id_municipio "+
					 "  join comum.unidade_federativa u on u.id_unidade_federativa = m.id_unidade_federativa "+
					 "  join curso c on c.id_curso = pc.id_curso "+
					 "  where t.ativo = trueValue() and tpc.id_tutor_orientador != "+t.getId()+" and tpc.id_polo_curso in "+ gerarStringIn(t.getPoloCursos());

		Query q = getSession().createSQLQuery(sql);	
		
		List<Object[]> result = q.list();
		ArrayList<PoloCurso> pcursos = new ArrayList<PoloCurso>();
		if ( result != null)
			for (Object[] linha : result){
				Integer i = 0;
				PoloCurso pc = new PoloCurso();
				pc.setId((Integer) linha[i++]);
				Curso c = new Curso();
				c.setNome((String) linha[i++]);
				pc.setCurso(c);
				Polo p = new Polo();
				Municipio m = new Municipio();
				m.setNome((String) linha[i++]);
				UnidadeFederativa u = new UnidadeFederativa();
				u.setSigla((String) linha[i++]);
				m.setUnidadeFederativa(u);
				p.setCidade(m);
				pc.setPolo(p);
				pcursos.add(pc);
			}			
		return pcursos;
	}
	
	/**
	 * Retorna uma lista de discentes de acordo com matrícula,
	 * nome do discente, nome do curso ou pessoa do tutor.
	 */
	public Collection<DiscenteGraduacao> findDiscentesByMatriculaNomeCursoOuTutor(Long matricula, String nome, String curso, Pessoa pessoaTutor) throws DAOException {
		
		String sql = "select dg.id_discente_graduacao, d.matricula, pessoaDiscente.nome, d.status, curso.nome as nome_curso, ";
		
		if (ValidatorUtil.isNotEmpty(pessoaTutor))
			sql += " pessoaTutorDis.nome as nome_tutor "; 
		else
			sql+= " pessoaTutor.nome as nome_tutor ";
		
		sql += " from graduacao.discente_graduacao dg "
				+ " inner join ead.polo polo using (id_polo) "
				+ " inner join discente d on (d.id_discente = dg.id_discente_graduacao) "
				+ " inner join comum.pessoa pessoaDiscente using (id_pessoa)  "
				+ " inner join curso curso on (curso.id_curso = d.id_curso) "
				+ " inner join ead.polo_curso pc on (pc.id_polo = polo.id_polo and pc.id_curso = curso.id_curso)"
				+ "	left join ead.tutor_polo_curso tpc on (tpc.id_polo_curso = pc.id_polo_curso) "
				+ "	left join ead.tutor_orientador tutor on (tutor.id_tutor_orientador = tpc.id_tutor_orientador and tutor.ativo = trueValue()) "
				+ " left join comum.pessoa pessoaTutor on (pessoaTutor.id_pessoa = tutor.id_pessoa) ";
		
		if (ValidatorUtil.isNotEmpty(pessoaTutor)){
			sql+= "	left join ead.tutor_polo_curso tpcDis on (tpcDis.id_polo_curso = pc.id_polo_curso) "
					+ "	left join ead.tutor_orientador tutorDis on (tutorDis.id_tutor_orientador = tpcDis.id_tutor_orientador and tutorDis.ativo = trueValue()) "
					+ " left join comum.pessoa pessoaTutorDis on (pessoaTutorDis.id_pessoa = tutorDis.id_pessoa) ";
		}
		
		sql +=" where d.nivel = ? and polo.id_polo is not null and d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getValidos());
		
		if (ValidatorUtil.isNotEmpty(pessoaTutor))
			sql+=" and pessoaTutor.id_pessoa = " +  pessoaTutor.getId();
				
		if (matricula != null)
			sql+=" and d.matricula = ? ";
		if (nome != null)
			sql+=" and upper(pessoaDiscente.nome_ascii) like ? ";
		if (curso != null)
			sql+=" and upper(curso.nome_ascii) like ? ";

		if (ValidatorUtil.isNotEmpty(pessoaTutor))
			sql+=" order by pessoaDiscente.nome , pessoaTutorDis.nome asc";
		else
			sql+=" order by pessoaDiscente.nome , pessoaTutor.nome asc";
		
		Query q = getSession().createSQLQuery(sql);
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
		DiscenteGraduacao discente = null;
		Integer idDiscente = 0;
		if (result != null)
			for (Object[] linha : result ) {
				Integer i = 0;
				Integer newIdDiscente = (Integer)linha[i++];
				
				if (idDiscente.intValue() == newIdDiscente.intValue())
					i = 5;
				else {	
					discente = new DiscenteGraduacao();
					discente.setId(newIdDiscente);
					BigInteger mDiscente = (BigInteger) linha[i++];
					if (mDiscente != null)
						discente.setMatricula(mDiscente.longValue());
					discente.setPessoa(new Pessoa());
					discente.getPessoa().setNome((String) linha[i++]);
					discente.setStatus((Short) linha[i++]);
					discente.setCurso(new Curso());
					discente.getCurso().setNome((String) linha[i++]);
					discente.setTutores(new ArrayList<TutorOrientador>());
					idDiscente = newIdDiscente;
					discentes.add(discente);
				}
				
				TutorOrientador t = new TutorOrientador();
				t.setPessoa(new Pessoa());
				String pnome = (String) linha[i++];
				if (ValidatorUtil.isNotEmpty(pnome)){
					t.getPessoa().setNome(pnome);
					if (discente!=null)
						discente.getTutores().add(t);
				}	
			}
			
		return discentes;
	}
	
	/**
	 * Retorna uma coleção de {@link TutorOrientador} associados ao polo passado.
	 * 
	 * @param idPolo
	 * @return
	 * @throws DAOException
	 */
	public Collection<TutorOrientador> findADistanciaByNomeAtivo(String nome, String nomeDisciplina, boolean somenteAtivos, Integer ano, Integer periodo) throws DAOException {
		try {
			
			Query q = getSession().createSQLQuery(" select distinct t.id_tutor_orientador , t.tipo , t.ativo , p.id_pessoa , p.nome , u.id_usuario, u.login , u.inativo ," +
												   " pl.id_polo , m.nome as mnome , uf.sigla , turma.id_turma , d.id_disciplina , d.codigo, cpd.nome as dnome " +
												   " from ead.tutor_orientador t " +
												   " inner join comum.pessoa p on p.id_pessoa = t.id_pessoa " +
												   " left join comum.usuario u on u.id_pessoa = t.id_pessoa and u.inativo = falseValue() " +
												   " left join ava.permissao_ava pa on pa.id_pessoa = t.id_pessoa " +
												   " left join ensino.turma turma on turma.id_turma = pa.id_turma and (turma.id_polo is not null or turma.distancia = trueValue())" +
												   " left join ensino.componente_curricular d on d.id_disciplina = turma.id_disciplina " +  
												   " left join ensino.componente_curricular_detalhes cpd on cpd.id_componente_detalhes = d.id_detalhe " +
												   " left join ead.polo pl on pl.id_polo = turma.id_polo " +
												   " left join comum.municipio m on m.id_municipio = pl.id_cidade " +
												   " left join comum.unidade_federativa uf on uf.id_unidade_federativa = m.id_unidade_federativa " +
												   " where " +
												   " t.tipo = '"+TutorOrientador.TIPO_TUTOR_A_DISTANCIA+"' "+
												   (!isEmpty(ano) && !isEmpty(periodo) ? " and turma.ano = "+ano+" and turma.periodo = "+periodo+" " : "") +
												   (!StringUtils.isEmpty(nome) ? " and p.nome_ascii ilike :nome " : "") +
												   (!StringUtils.isEmpty(nomeDisciplina) ? " and sem_acento(upper(cpd.nome))"+" like :nomeDisciplina " : "") +
												   (somenteAtivos ? " and t.ativo = trueValue() " : "") +
												   " order by p.nome , u.login , cpd.nome asc ");
	
			if (!StringUtils.isEmpty(nome))
				q.setString("nome", "%" + StringUtils.toAscii(nome) + "%");
			if (!StringUtils.isEmpty(nomeDisciplina))
				q.setString("nomeDisciplina", "%" + StringUtils.toAscii(nomeDisciplina.toUpperCase()) + "%");
			
			@SuppressWarnings("unchecked")
			List <Object []> ls = q.list();
			List<TutorOrientador> listaTutores = new ArrayList<TutorOrientador>();
			TutorOrientador t = null;
			
			Integer idTutor = 0;
			Integer idUsuario = 0;
			Integer idPolo = 0;
			if (ls != null)
				for (Object [] linha : ls){
				
					Integer i = 0;
					Integer newIdTutor = (Integer)linha[i++];
					Integer newIdUsuario = (Integer)linha[5];
					Boolean tutorAntigo = idTutor.intValue() == newIdTutor.intValue();
					Boolean usuarioAntigo = idUsuario != null && newIdUsuario != null && idUsuario.intValue() == newIdUsuario.intValue();
					if (tutorAntigo)
						i = 8;
					else {	
						
						t = new TutorOrientador();
						t.setId(newIdTutor);
						t.setTipo((Character)linha[i++]);
						t.setAtivo((Boolean)linha[i++]);
					
						Pessoa p = new Pessoa();
						p.setId((Integer)linha[i++]);
						p.setNome((String)linha[i++]);
					
						if (newIdUsuario!=null){
							Usuario u = new Usuario();
							u.setId((Integer)linha[i++]);
							u.setLogin((String)linha[i++]);
							u.setInativo((Boolean)linha[i++]);
							t.setUsuario(u);
						}
						t.setPessoa(p);
						t.setPoloCursos(new ArrayList<PoloCurso>());
						t.setTurmas(new ArrayList<Turma>());
						
						idTutor = newIdTutor;
						idUsuario = newIdUsuario;
						listaTutores.add(t);
					}
					
					PoloCurso pc = new PoloCurso();			
					Polo pl = new Polo();
					Integer newIdPolo = (Integer)linha[i++];
					
					if (idPolo == null || newIdPolo == null || (tutorAntigo && usuarioAntigo && idPolo.intValue() == newIdPolo))
						i = 11;
					else {	
						
						pl.setCidade(new Municipio());
						pl.getCidade().setNome((String)linha[i++]);
						pl.getCidade().setUnidadeFederativa(new UnidadeFederativa());
						pl.getCidade().getUnidadeFederativa().setSigla((String)linha[i++]);

						
						pl.setId(newIdPolo);
						pc.setPolo(pl);
						idPolo = newIdPolo;
						
						if (t!=null){
							t.setPoloCurso(pc);
							t.getPoloCursos().add(pc);
						}
					}
					
					Integer idTurma = (Integer)linha[i++];
					if (idTurma!=null){
						Turma turma = new Turma();
						turma.setId(idTurma);
						ComponenteCurricular d = new ComponenteCurricular();
						d.setId((Integer)linha[i++]);
						d.setCodigo((String)linha[i++]);
						ComponenteDetalhes cpd = new ComponenteDetalhes();
						cpd.setNome((String)linha[i++]);
						d.setDetalhes(cpd);
						turma.setDisciplina(d);
						
						if (t!=null)
							t.getTurmas().add(turma);
					}
					
				}		
						
			return listaTutores;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
