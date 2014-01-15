/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Dao responsável por consultas em relatórios de discente de graduação
 * 
 * @author Arlindo
 *
 */
public class RelatorioDiscenteGraduacaoDao extends GenericSigaaDAO {
	
	/**
	 * Busca todos os alunos com o vínculo concluído do curso informado
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findDiscentesConcluidosByCurso(int idCurso, Integer anoConclusao, Integer periodoConclusao)	throws DAOException {		
		String projecao = "id, pessoa.nome, pessoa.email, matricula, curso.id, anoIngresso, periodoIngresso, status, nivel";
		
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT "+projecao+" FROM Discente d ");
		hql.append("WHERE curso.id=:curso AND status = " + StatusDiscente.CONCLUIDO);
		hql.append(" AND d.nivel = '"+NivelEnsino.GRADUACAO+"'" );
		hql.append(" AND exists ( "); 
		hql.append(" 	SELECT ma.discente.id FROM MovimentacaoAluno ma ");
		hql.append(" 	INNER JOIN ma.discente dis ");
		hql.append(" 	INNER JOIN ma.tipoMovimentacaoAluno ");
		hql.append(" 	WHERE ma.tipoMovimentacaoAluno.id = "+TipoMovimentacaoAluno.CONCLUSAO);
		hql.append(" 	 AND  ma.discente.id = d.id ");
		hql.append(" 	 AND  ma.anoReferencia = "+anoConclusao);
		hql.append(" 	 AND  ma.periodoReferencia = "+periodoConclusao);
		hql.append(" ) ");
		hql.append("ORDER BY anoIngresso, periodoIngresso, pessoa.nome");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("curso", idCurso);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao, Discente.class);
	}		

}
