package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;

/**
 *  DAO com consultas relativas à entidade NotaUnidade.
 * 
 * @author Henrique André
 *
 */
public class CadastroNotasUnidadesDao extends GenericSigaaDAO {

	public boolean existeMatriculasConsolidadas(Turma turma) throws DAOException {
		
		String sql = "select mc.id from MatriculaComponente mc " +
				" 	join mc.turma t " +
				" where t.id = :turmaId " +
				"	and mc.situacaoMatricula in " + gerarStringIn(SituacaoMatricula.getSituacoesConcluidas());
		
		Query q = getSession().createQuery(sql);
		q.setInteger("turmaId", turma.getId());
		
		Integer resultado = (Integer) q.setMaxResults(1).uniqueResult();
		
		if(isEmpty(resultado))
			return false;
		
		return true;
		
	}
	
	/**
	 * Retorna a quantidade de unidades agrupadas por matricula
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<Integer> countNotaUnidadeByTurma(Turma turma) throws DAOException {
		String sql = "select distinct count(nu.id_nota_unidade) from ensino.matricula_componente mc " +
				"	left join ensino.nota_unidade nu on (nu.id_matricula_componente = mc.id_matricula_componente)" +
				"	where ( nu.id_nota_unidade is null or nu.ativo = trueValue() ) and mc.id_turma = " + turma.getId() + " and mc.id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar()) + " group by mc.id_matricula_componente";
		
		@SuppressWarnings("unchecked")
		List<BigInteger> list = getSession().createSQLQuery(sql).list();
		
		List<Integer> resultado = new ArrayList<Integer>();

		for (BigInteger bi : list) {
			resultado.add(bi.intValue());
		}
		
		return resultado;
	}

	/**
	 * Deleta as notas unidades da matricula componente onde a unidade é maior que a unidade informada.
	 * 
	 * @param mat
	 * @param unidade
	 * @throws DAOException 
	 * @throws  
	 */
	public int inativarNotasUnidadesByMatriculaAndUnidade(List<MatriculaComponente> mats, int unidade) throws DAOException {
		
		String hql = "update NotaUnidade set ativo = falseValue(), dataInativacao = current_timestamp() where ativo = trueValue() and matricula.id in " + UFRNUtils.gerarStringIn(mats) + " and unidade > :unidade";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("unidade", unidade);
		
		return q.executeUpdate();
	}
	
}
