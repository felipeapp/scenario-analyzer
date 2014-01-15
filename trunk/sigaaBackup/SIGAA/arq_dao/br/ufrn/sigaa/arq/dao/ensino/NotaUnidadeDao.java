package br.ufrn.sigaa.arq.dao.ensino;

import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO com consultas relativas � entidade NotaUnidade.
 * 
 * @author Diego J�come
 *
 */
public class NotaUnidadeDao extends GenericSigaaDAO {

	/**
	 * Retorna o n�mero de notas unidades cadastradas para uma matr�cula a aleat�ria e consolidade
	 * de uma turma.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer countNotasUnidadesConsolidadasByTurma(Turma t) throws HibernateException, DAOException {
	
		ArrayList<Integer> idsTurma = new ArrayList<Integer>();
		if (!t.isAgrupadora())
			idsTurma.add(t.getId());
		else{
			for (Turma sub : t.getSubturmas())
				idsTurma.add(sub.getId());
		}
			
		String sql = " select count(n.id_matricula_componente) from ensino.nota_unidade n "+
					" join ensino.matricula_componente m on m.id_matricula_componente = n.id_matricula_componente "+
					" join ensino.turma t on t.id_turma = m.id_turma "+
					" where t.id_turma in "+UFRNUtils.gerarStringIn(idsTurma)+
					" and m.id_situacao_matricula in "+ UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesConcluidasNormal())+
					" and m.ano = " + t.getAno() +
					" and m.periodo = " + t.getPeriodo() +
					" group by n.id_matricula_componente ";
		
		Query q = getSession().createSQLQuery(sql);			
		q.setMaxResults(1);		
		Number n = ((Number) q.uniqueResult());
		
		if (n == null)
			return 0;
		else
			return n.intValue();
	}
	
	
}
