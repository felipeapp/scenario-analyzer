package br.ufrn.sigaa.ensino.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * DAO responsável por realizar consultas referentes ao fechamento de turmas.
 * 
 * @author Bernardo
 *
 */
public class FecharTurmaDao extends GenericSigaaDAO {

	/**
	 * Verifica se, na turma passada, existem alunos nos seguintes status:
	 * <ul>
	 * <li>MATRICULADO</li>
	 * <li>APROVADO</li>
	 * <li>REPROVADO</li>
	 * <li>REPROVADO POR FALTA</li>
	 * <li>REPROVADO POR MEDIA E FALTA</li>
	 * <li>EM ESPERA</li>
	 * </ul>
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public boolean containsAlunosAtivos(int turma) throws DAOException {
		String hql = "select count(*) " +
						" from MatriculaComponente mc " +
							" join mc.turma t " +
						" where t.id = :turma and id_situacao_matricula in " + UFRNUtils.gerarStringIn(new SituacaoMatricula[]{SituacaoMatricula.MATRICULADO, 
																																SituacaoMatricula.APROVADO, 
																																SituacaoMatricula.REPROVADO, 
																																SituacaoMatricula.REPROVADO_FALTA, 
																																SituacaoMatricula.REPROVADO_MEDIA_FALTA, 
																																SituacaoMatricula.EM_ESPERA});
		Query q = getSession().createQuery(hql);
		
		q.setInteger("turma", turma);
		
		Long total = (Long) q.uniqueResult();
		
		return total > 0;
	}
}
