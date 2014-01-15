/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino_rede.dominio.ComponenteCurricularRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteTurmaRede;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por consultas específicas às Turmas de ensino em rede.
 * @author Diego Jácome
 */
public class TurmaRedeDao extends GenericSigaaDAO  {

	
	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idCampus
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<TurmaRede> findTurmasAbertasByCampusAnoPeriodo(int idCampus, int ano, int periodo) throws HibernateException, DAOException {
		
		ArrayList<SituacaoTurma> situacoes = (ArrayList<SituacaoTurma>) SituacaoTurma.getSituacoesAbertas();
		return findTurmasByCampusAnoPeriodo(idCampus,ano,periodo,situacoes);
	}
	
	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idCampus
	 * @param ano
	 * @param periodo
	 * @param situacoes
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaRede> findTurmasByCampusAnoPeriodo(int idCampus, int ano, int periodo, ArrayList<SituacaoTurma> situacoes) throws HibernateException, DAOException {
	
		String sql = " select distinct t.id_turma_rede , t.ano , t.periodo , cc.id_disciplina , cc.codigo , cc.nome as cnome, "+
					 " dt.id_docente_turma_rede , dr.id_docente_rede , p.id_pessoa , p.nome as nomeDocente"+
					 " from ensino_rede.turma_rede t "+
					 " inner join ensino_rede.matricula_componente_rede m on m.id_turma = t.id_turma_rede "+
					 " inner join ensino_rede.discente_associado d on d.id_discente_associado = m.id_discente "+
					 " inner join ensino_rede.dados_curso_rede dc on dc.id_dados_curso_rede = d.id_dados_curso_rede "+
					 " inner join ensino_rede.componente_curricular_rede cc on cc.id_disciplina = t.id_componente_curricular "+
					 " left join ensino_rede.docente_turma_rede dt on dt.id_turma_rede = t.id_turma_rede "+
					 " left join ensino_rede.docente_rede dr on dr.id_docente_rede = dt.id_docente_rede "+
					 " left join comum.pessoa p on p.id_pessoa = dr.id_pessoa "+
					 " where t.ano = :ano and t.periodo = :periodo and dc.id_campus = :idCampus and t.id_situacao_turma in "+UFRNUtils.gerarStringIn(situacoes);

		Query q =  getSession().createSQLQuery(sql);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idCampus", idCampus);

		List <Object []> result = q.list();
		Collection<TurmaRede> turmas = new ArrayList<TurmaRede>();		
		TurmaRede t = null;
		
		Integer idTurmaAntiga = 0;
		Integer idTurmaNova = 0;
		
		for (Object [] linha : result) {
			
			int i = 0;
			idTurmaNova = (Integer) linha[i++];
			Boolean turmaAntiga = idTurmaAntiga.intValue() == idTurmaNova.intValue();

			if (turmaAntiga){
				i = 6;
			} else {
				t = new TurmaRede();
				t.setId(idTurmaNova);
				t.setAno((Integer) linha[i++]);
				t.setPeriodo((Short) linha[i++]);
				ComponenteCurricularRede cc = new ComponenteCurricularRede();
				cc.setId((Integer)linha[i++]);
				cc.setCodigo((String)linha[i++]);
				cc.setNome((String) linha[i++]);
				t.setComponente(cc);
				t.setDocentesTurmas(new HashSet<DocenteTurmaRede>());
				turmas.add(t);
			}
			
			DocenteTurmaRede dt = new DocenteTurmaRede();
			dt.setId((Integer)linha[i++]);
			DocenteRede dr = new DocenteRede();
			dr.setId((Integer)linha[i++]);
			Pessoa p = new Pessoa();
			p.setId((Integer)linha[i++]);
			p.setNome((String)linha[i++]);
			dr.setPessoa(p);
			dt.setDocente(dr);
			
			if ( t != null){
				t.getDocentesTurmas().add(dt);
			}
			idTurmaAntiga = idTurmaNova;
		}
		
		return turmas;
	}
	
	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idTurma
	 * @param situacoes
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<MatriculaComponenteRede> findParticipantesTurma(int idTurma, List<SituacaoMatricula> situacoes) throws HibernateException, DAOException {
	
		String sql = " select mc.id_matricula_componente , d.id_discente_associado , p.id_pessoa , p.nome , mc.id_situacao from ensino_rede.matricula_componente_rede mc "+
						" inner join ensino_rede.discente_associado d on d.id_discente_associado = mc.id_discente "+
						" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
						" inner join ensino_rede.turma_rede t on t.id_turma_rede = mc.id_turma "+
						" where id_turma = " +idTurma+ "and id_situacao in "+UFRNUtils.gerarStringIn(situacoes);
		
		List <Object []> result = getSession().createSQLQuery (sql).list();
		Collection<MatriculaComponenteRede> matriculas = new ArrayList<MatriculaComponenteRede>();
		

		for (Object [] linha : result) {
			
			int i = 0;
			
			MatriculaComponenteRede mc = new MatriculaComponenteRede();
			DiscenteAssociado d = new DiscenteAssociado();
			Pessoa p = new Pessoa();
			
			mc.setId((Integer)linha[i++]);
			d.setId((Integer)linha[i++]);
			p.setId((Integer)linha[i++]);
			p.setNome((String)linha[i++]);
			mc.setSituacao(SituacaoMatricula.getSituacao((Integer)linha[i++]));
			
			d.setPessoa(p);
			mc.setDiscente(d);
			matriculas.add(mc);
		}
		
		return matriculas;
	}
		
	
}
