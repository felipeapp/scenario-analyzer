package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;


/**
 * Dao responsável pelas consultas das turmas componentes.
 * Equivale as disciplinas do curso técnico do IMD
 * 
 * @author Rafael Silva
 *
 */
public class TurmaDao extends GenericSigaaDAO{
	
	/**
	 * Pesquisa as turmas que possuem o id da especialização informado. 
	 * 
	 * @param idEspecializacao
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findByEspecializacao(int idEspecializacao) throws DAOException{
		Criteria c = getSession().createCriteria(Turma.class);
		Criteria cEspecializacao = c.createCriteria("especializacao");
		cEspecializacao.add(Expression.eq("id", idEspecializacao));
		
		
		
		return (List<Turma>) c.list();
	}
	
	/**
	 * Realiza pesquisa através da chave primária da entidade turma.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public Turma findByPrimaryKey(int idTurma) throws DAOException{
		Criteria c = getSession().createCriteria(Turma.class);
		c.createCriteria("especializacao");
		
		c.add(Expression.eq("id", idTurma));

		return (Turma) c.uniqueResult();
	}
	
	/**
	 * Retorna os discentes matriculados na disciplina com o ID informado.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente>getMatriculasDisciplina(int idDisciplina) throws DAOException{
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		
		c.add(Expression.eq("turma.id", idDisciplina));
		c.add(Expression.eq("situacaoMatricula.id", 2));
		
		return c.list();
		
	}	
}
