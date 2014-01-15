/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/11/2010
 *
 */


package br.ufrn.sigaa.arq.dao.ensino;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Dao responsável por consultas específicas aos horários das turmas.
 * @author Fred_Castro
 */
public class HorarioTurmaDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public HorarioTurmaDao() {
	
	}

	public List <HorarioTurma> findByIdsTurmas (List <Integer> idsTurmas) throws DAOException {
		@SuppressWarnings("unchecked")
		List <HorarioTurma> rs = getSession().createQuery("select h from HorarioTurma h where h.turma.id in " + UFRNUtils.gerarStringIn(idsTurmas)).list();
		
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public List <HorarioTurma> findByTurma (Turma turma) throws DAOException {
		Query q = getSession().createQuery("select h from HorarioTurma h where h.turma.id = :turma");
		q.setInteger("turma", turma.getId());
		
		return q.list();
	}	
	
}