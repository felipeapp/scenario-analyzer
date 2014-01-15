package br.ufrn.sigaa.arq.dao.ead;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * DAO para buscas dos Polos dos Cursos
 * 
 * @author guerethes
 */
public class PoloCursoDao extends GenericSigaaDAO {

	/**
	 * Retorna todos os polos do curso passado no parâmetro.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<PoloCurso> findAllCursoPolo(CursoLato curso) throws DAOException {

		String hql = "from PoloCurso pc left join pc.polo p left join p.cidade c "+
				"where pc.curso.id = " + curso.getId();
		
		Collection<PoloCurso> poloCursos = new ArrayList<PoloCurso>();
		
		List<Object[]> list = getSession().createQuery(hql).list(); 

		for (Object[] item : list) {
			PoloCurso poloCurso = new PoloCurso();
			poloCurso.setCurso(new Curso());
			poloCurso.setPolo(new Polo());
			poloCurso.getPolo().setCidade(new Municipio());
			poloCurso = (PoloCurso) item[0];
			poloCurso.setPolo((Polo) item[1]);
			poloCurso.getPolo().setCidade((Municipio) item[2]);
			poloCursos.add(poloCurso);
		}
		return poloCursos;
	}
	
	/**
	 * Retorna os cursos associados ao polo do PoloCurso.
	 * 
	 * @param idPolo
	 * @return
	 * @throws DAOException
	 */
	public Collection <Curso> findCursosByPolo (int idPolo) throws HibernateException, DAOException {
		String sql = "select c.id_curso, c.nome from curso c join ead.polo_curso pc on pc.id_curso = c.id_curso where pc.id_polo = " + idPolo;
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = getSession().createSQLQuery (sql).list();
		
		List <Curso> rs = new ArrayList <Curso> ();
		
		for (Object [] l : lista){
			Curso c = new Curso(((Number)l[0]).intValue());
			c.setNome((String) l[1]);
			rs.add(c);
		}
		
		return rs;
	}
	
	public ArrayList <PoloCurso> findPoloCursosByPolo (int idPolo) throws HibernateException, DAOException {
		String sql = "select pc.id_polo_curso , c.nome as cnome , m.nome as mnome , u.sigla from ead.polo_curso pc "+
					" join ead.polo pl on pl.id_polo = pc.id_polo "+
					" join curso c on c.id_curso = pc.id_curso "+
					" left join comum.municipio m on c.id_municipio = m.id_municipio "+
					" left join comum.unidade u on u.id_unidade = c.id_unidade "+
					" where pc.id_polo = " + idPolo;
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = getSession().createSQLQuery (sql).list();
		
		ArrayList <PoloCurso> rs = new ArrayList <PoloCurso> ();
		
		for (Object [] linha : lista){
			Integer i = 0;
			PoloCurso pc = new PoloCurso();
			pc.setId((Integer)linha[i++]);
			Curso c = new Curso();
			c.setNome((String) linha[i++]);
			Municipio m = new Municipio();
			m.setNome((String) linha[i++]);
			Unidade u = new Unidade();
			u.setSigla((String) linha[i++]);
			c.setUnidade(u);
			c.setMunicipio(m);
			pc.setCurso(c);
			rs.add(pc);
		}
		
		return rs;
	}
	
	/**
	 * Retorna todos os polos do curso passado no parâmetro.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<PoloCurso> findAllByIds(List<Integer> ids) throws DAOException {

		String hql = "select pc from PoloCurso pc where pc.id in " + UFRNUtils.gerarStringIn(ids);
			
		ArrayList<PoloCurso> poloCursos = (ArrayList<PoloCurso>) getSession().createQuery(hql).list(); 
		return poloCursos;
	}
	
	/**
	 * Retorna todos os polos das turmas passadas no parâmetro.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<PoloCurso> findByIdsTurma(List<Integer> ids) throws DAOException {

		String sql = "select pc.id_polo_curso from ead.polo_curso pc " +
				" join curso c on c.id_curso = pc.id_curso " +
				" join ensino.componente_curricular d on d.id_unidade = c.id_unidade" +
				" join ensino.turma t on t.id_polo = pc.id_polo and t.id_disciplina = d.id_disciplina" +
				" where t.id_turma in " + UFRNUtils.gerarStringIn(ids);
			
		ArrayList<PoloCurso> poloCursos = new ArrayList<PoloCurso>();
		ArrayList<Integer> rs = (ArrayList<Integer>) getSession().createSQLQuery(sql).list(); 
		
		for (Integer linha : rs){
			PoloCurso pc = new PoloCurso();
			pc.setId(linha);
			poloCursos.add(pc);
		}
		
		return poloCursos;
	}
}