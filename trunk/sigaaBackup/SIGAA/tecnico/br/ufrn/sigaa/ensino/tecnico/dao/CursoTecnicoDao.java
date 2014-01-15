/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '11/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;

/**
 * Dao com consultas sobre os cursos técnicos
 * @author Andre M Dantas
 *
 */
public class CursoTecnicoDao extends CursoDao {


	/**
	 * Busca os cursos da unidade gestora acadêmica e do nível de ensino informados.
	 * O nível é usado pois tanto o técnico como o médio são representados pela mesma entidade.
	 * 
	 */
	public Collection<CursoTecnico> findByUnidadeNivel(int unidId, char nivel) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(CursoTecnico.class);
			if (unidId > 0) {
				c.add(Restrictions.eq("unidade", new Unidade(unidId)));
			}
			if (nivel != 0) {
				c.add(Restrictions.eq("nivel", nivel));
			}
			c.add(Restrictions.eq("ativo", Boolean.TRUE));

			@SuppressWarnings("unchecked")
			List<CursoTecnico> lista =  c.list();
			return lista;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	

	/**
	 * Busca os cursos técnicos da unidade gestora acadêmica e do nível de ensino informados
	 * que possuam módulos cadastrados na sua estrutura curricular ativa.
	 * 
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<CursoTecnico> findComModulos(int unidadeId, char nivel) throws DAOException {

		try {
			Collection<CursoTecnico> resultado = new HashSet<CursoTecnico>();
			Collection<CursoTecnico> cursos = findByUnidadeNivel(unidadeId, nivel);
			for (CursoTecnico curso : cursos) {
				if ( curso.getEstruturaAtiva() != null  && curso.getEstruturaAtiva().getModulos() != null 
						&& !curso.getEstruturaAtiva().getModulos().isEmpty())
					resultado.add(curso);
			}
			return resultado;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Retorna todas as especializações de turmas de entrada disponíveis para o cadastro de turmas de entrada.
	 * Algumas especializações de turma de entrada devem estar disponíveis somente no cadastro de turmas regulares, mas não no cadastro de turmas de entrada.
	 * 
	 * @param unidadeId
	 * @return
	 * @throws DAOException
	 */
	public Collection<EspecializacaoTurmaEntrada> findEspecializacoesParaTurmaEntrada(int unidadeId) throws DAOException {
		@SuppressWarnings("unchecked")
		List<EspecializacaoTurmaEntrada> lista = getSession().createQuery("from EspecializacaoTurmaEntrada where unidade.id ="+unidadeId+" and somenteTurma = falseValue() order by descricao asc").list();
		return  lista;
	}

	/**
	 * Verifica se a disciplina informada pertence a algum dos cursos técnicos informados.
	 * 
	 * @param idDisciplina
	 * @param idCursos
	 * @return
	 * @throws DAOException
	 */
	public boolean contemDisciplina(int idDisciplina, int...idCursos) throws DAOException {
		String hql = "select id_disciplina" +
				" from tecnico.disciplina_complementar" +
				"  join tecnico.estrutura_curricular_tecnica using (id_estrutura_curricular)" +
				" where id_curso in " + UFRNUtils.gerarStringIn(idCursos) +
				" and id_disciplina = " + idDisciplina +
				" union" +
				" select id_disciplina" +
				" from tecnico.modulo_disciplina" +
				"  join tecnico.modulo_curricular using (id_modulo)" +
				"  join tecnico.estrutura_curricular_tecnica using (id_estrutura_curricular)" +
				" where id_curso in " + UFRNUtils.gerarStringIn(idCursos) +
				" and id_disciplina = "+ idDisciplina;
		
		Query q = getSession().createSQLQuery(hql.toString());
		
		if (q.uniqueResult() != null)
			return true;

		return false;
	}
	
	/**
	 * Retorna o curso dentre os informados que está cadastrado na unidade informada.
	 * 
	 * @param unidade
	 * @param idsCursos
	 * @return
	 * @throws DAOException
	 */
	public Curso findCursoNaUnidade(Unidade unidade, int[] idsCursos) throws DAOException {
		String hql = "from Curso where unidade.id = :idUnidade and id in " + UFRNUtils.gerarStringIn(idsCursos);
		return (Curso) getSession().createQuery(hql).setInteger("idUnidade", unidade.getId()).setMaxResults(1).uniqueResult();
	}
}
