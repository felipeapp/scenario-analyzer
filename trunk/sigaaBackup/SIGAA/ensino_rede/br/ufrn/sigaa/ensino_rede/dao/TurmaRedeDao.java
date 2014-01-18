/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino_rede.dominio.ComponenteCurricularRede;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteTurmaRede;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
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
	public Collection<TurmaRede> findTurmasAbertasByCampusAnoPeriodo(int idCampus, int idPrograma, Integer ano, Integer periodo) throws HibernateException, DAOException {
		
		ArrayList<SituacaoTurma> situacoes = (ArrayList<SituacaoTurma>) SituacaoTurma.getSituacoesAbertas();
		return findTurmasByCampusAnoPeriodo(idCampus,idPrograma,ano,periodo,situacoes);
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
	public Collection<TurmaRede> findTurmasByCampusAnoPeriodo(int idCampus, int idPrograma, Integer ano, Integer periodo, ArrayList<SituacaoTurma> situacoes) throws HibernateException, DAOException {
	
		String sql = " select distinct t.id_turma_rede , t.ano , t.periodo , t.codigo,  cc.id_disciplina , cc.codigo , cc.nome as cnome, "+
					 " dt.id_docente_turma_rede , dr.id_docente_rede , p.id_pessoa , p.nome as nomeDocente"+
					 " from ensino_rede.turma_rede t "+
					 " inner join ensino_rede.matricula_componente_rede m on m.id_turma = t.id_turma_rede "+
					 " inner join ensino_rede.discente_associado d on d.id_discente_associado = m.id_discente "+
					 " inner join ensino_rede.dados_curso_rede dc on dc.id_dados_curso_rede = d.id_dados_curso_rede "+
					 " inner join ensino_rede.componente_curricular_rede cc on cc.id_disciplina = t.id_componente_curricular "+
					 " left join ensino_rede.docente_turma_rede dt on dt.id_turma_rede = t.id_turma_rede "+
					 " left join ensino_rede.docente_rede dr on dr.id_docente_rede = dt.id_docente_rede "+
					 " left join comum.pessoa p on p.id_pessoa = dr.id_pessoa "+
					 " where dc.id_programa_rede = :idPrograma and dc.id_campus = :idCampus and t.id_situacao_turma in "+UFRNUtils.gerarStringIn(situacoes);

		if (!isEmpty(ano))
			sql += " and t.ano = :ano ";
		if (!isEmpty(periodo))
			sql += " and t.periodo = :periodo ";
		
		sql += " order by  t.ano , t.periodo , cc.codigo , cc.nome  ";
		
		Query q =  getSession().createSQLQuery(sql);
		
		q.setInteger("idCampus", idCampus);
		q.setInteger("idPrograma", idPrograma);
	
		if (!isEmpty(ano))
			q.setInteger("ano", ano);
		if (!isEmpty(periodo))
			q.setInteger("periodo", periodo);


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
				i = 7;
			} else {
				t = new TurmaRede();
				t.setId(idTurmaNova);
				t.setAno((Integer) linha[i++]);
				t.setPeriodo((Short) linha[i++]);
				t.setCodigo((String) linha[i++]);
				ComponenteCurricularRede cc = new ComponenteCurricularRede();
				cc.setId((Integer)linha[i++]);
				cc.setCodigo((String)linha[i++]);
				cc.setNome((String) linha[i++]);
				t.setComponente(cc);
				t.setDocentesTurmas(new HashSet<DocenteTurmaRede>());
				turmas.add(t);
			}
			
			Integer idDocente = (Integer)linha[i++];
			
			if (idDocente != null){
				
				DocenteTurmaRede dt = new DocenteTurmaRede();
				dt.setId(idDocente);
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
	public Collection<MatriculaComponenteRede> findParticipantesTurma(int idTurma, Collection<SituacaoMatricula> situacoes) throws HibernateException, DAOException {
	
		String sql = " select mc.id_matricula_componente , d.id_discente_associado , d.ano_ingresso, d.periodo_ingresso , d.id_status, p.id_pessoa , p.nome , mc.id_situacao from ensino_rede.matricula_componente_rede mc "+
						" inner join ensino_rede.discente_associado d on d.id_discente_associado = mc.id_discente "+
						" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
						" inner join ensino_rede.turma_rede t on t.id_turma_rede = mc.id_turma "+
						" where mc.id_turma = " +idTurma+ "and mc.id_situacao in "+UFRNUtils.gerarStringIn(situacoes)+" and d.id_status in "+UFRNUtils.gerarStringIn(StatusDiscenteAssociado.getStatusAtivos())+
						" order by p.nome ";
		
		List <Object []> result = getSession().createSQLQuery (sql).list();
		Collection<MatriculaComponenteRede> matriculas = new ArrayList<MatriculaComponenteRede>();
		

		for (Object [] linha : result) {
			
			int i = 0;
			
			MatriculaComponenteRede mc = new MatriculaComponenteRede();
			DiscenteAssociado d = new DiscenteAssociado();
			Pessoa p = new Pessoa();
			
			mc.setId((Integer)linha[i++]);
			d.setId((Integer)linha[i++]);
			d.setAnoIngresso((Integer)linha[i++]);
			d.setPeriodoIngresso((Integer)linha[i++]);
			d.setStatus(new StatusDiscenteAssociado());
			Integer idStatus = (Integer)linha[i++];
			d.getStatus().setId(idStatus);
			d.getStatus().setDescricao(StatusDiscenteAssociado.getDescricao(idStatus));
			p.setId((Integer)linha[i++]);
			p.setNome((String)linha[i++]);
			mc.setSituacao(SituacaoMatricula.getSituacao((Integer)linha[i++]));
			
			d.setPessoa(p);
			mc.setDiscente(d);
			matriculas.add(mc);
		}
		
		return matriculas;
	}
		
	/**
	 * Retorna a quantidade de turmas existentes para o componente, ano, período informados
	 *
	 * @param idComponente
	 * @param campus 
	 * @param ano
	 * @param periodo
	 * @return A quantidade de turmas existentes
	 * @throws DAOException
	 */
	public int countTurmasByComponenteAnoPeriodo(int idComponente, int idCampus, int ano, int periodo) throws DAOException{

		try {
			String sql = "select count(distinct t) from ensino_rede.turma_rede t "+
						 " inner join ensino_rede.componente_curricular_rede d on d.id_disciplina = t.id_componente_curricular "+
						 " inner join ensino_rede.matricula_componente_rede m on m.id_turma = t.id_turma_rede "+
						 " inner join ensino_rede.discente_associado da on da.id_discente_associado = m.id_discente "+
						 " inner join ensino_rede.dados_curso_rede dc on dc.id_dados_curso_rede = da.id_dados_curso_rede "+
						 " where d.id_disciplina = "+idComponente+" and dc.id_campus = "+idCampus+" and t.ano = "+ano+" and t.periodo = "+periodo;
			
			Query q = getSession().createSQLQuery(sql);
			return ((Number) q.uniqueResult()).intValue();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todas as turmas de acordo com os parâmetros passados  
	 *
	 * @param idProgramaRede
	 * @param idIes
	 * @param ano
	 * @param periodo
	 * @param codigoComponente
	 * @param nomeComponente
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 

	 */
	@SuppressWarnings("unchecked")
	public List<TurmaRede> buscar(Integer idProgramaRede, Integer idIes, Integer ano, Integer periodo, String codigoComponente, String nomeComponente) throws HibernateException, DAOException {
		
		String sql = " select distinct t.id_turma_rede, t.ano, t.periodo, t.codigo, s.id_situacao_turma, s.descricao, " +
					 " c.id_disciplina, c.codigo as ccodigo, c.nome, dc.id_dados_curso_rede , cies.id_campus , cies.nome as campus" +
					 " from ensino_rede.turma_rede t "+
					 " inner join ensino.situacao_turma s on s.id_situacao_turma = t.id_situacao_turma "+
					 " inner join ensino_rede.componente_curricular_rede c on c.id_disciplina = t.id_componente_curricular "+
					 " inner join ensino_rede.matricula_componente_rede m on m.id_turma = t.id_turma_rede "+
					 " inner join ensino_rede.discente_associado d on d.id_discente_associado = m.id_discente  "+
					 " inner join ensino_rede.dados_curso_rede dc on dc.id_dados_curso_rede = d.id_dados_curso_rede "+
					 " inner join comum.campus_ies cies on cies.id_campus = dc.id_campus  "+
					 " where c.id_programa = " +idProgramaRede;

		if (!isEmpty(idIes))
			sql += " and cies.id_ies = "+idIes;
		if (!isEmpty(ano))
			sql += " and t.ano = "+ano;
		if (!isEmpty(periodo))
			sql += " and t.periodo = "+periodo;
		if (!isEmpty(codigoComponente))
			sql += " and c.codigo ilike '%"+ StringUtils.toAscii(codigoComponente.toUpperCase())+"%' ";
		if (!isEmpty(nomeComponente))
			sql += " and sem_acento(upper(c.nome)) ilike '%"+ StringUtils.toAscii(nomeComponente.toUpperCase())+"%' ";
		
		sql += " order by cies.id_campus, c.nome , c.codigo , t.codigo ";
		
		List <Object []> result = getSession().createSQLQuery (sql).list();
		List<TurmaRede> turmas = new ArrayList<TurmaRede>();
		

		for (Object [] linha : result) {
			
			int i = 0;
			
			TurmaRede t = new TurmaRede();
			SituacaoTurma s = new SituacaoTurma();
			ComponenteCurricularRede c = new ComponenteCurricularRede();
			DadosCursoRede dc = new DadosCursoRede();
			CampusIes cies = new CampusIes();
			
			t.setId((Integer)linha[i++]);
			t.setAno((Integer)linha[i++]);
			t.setPeriodo((Short)linha[i++]);
			t.setCodigo((String)linha[i++]);
			s.setId((Integer)linha[i++]);
			s.setDescricao((String)linha[i++]);
			c.setId((Integer)linha[i++]);
			c.setCodigo((String)linha[i++]);
			c.setNome((String)linha[i++]);
			dc.setId((Integer)linha[i++]);
			cies.setId((Integer)linha[i++]);
			cies.setNome((String)linha[i++]);
			
			dc.setCampus(cies);
			t.setDadosCurso(dc);
			t.setComponente(c);
			t.setSituacaoTurma(s);
			turmas.add(t);
		}		
		
		return turmas;
	}
	
	/**
	 * Retorna os docentesTurma de uma turma
	 *
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Set<DocenteTurmaRede> findDocentesTurmaByTurma (Integer idTurma) throws DAOException {
		
		ArrayList<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add(SituacaoDocenteRede.ATIVO);
		
		String hql = " select dt from DocenteTurmaRede dt " +
					 " join fetch dt.docente dr "+
					 " join fetch dt.docente.pessoa p "+
					 " join fetch dr.tipo t "+
					 " join fetch dr.situacao s "+
					 " where dt.turma.id = "+idTurma+
					 " and s.id in " + UFRNUtils.gerarStringIn(situacoes);
		hql += " order by dr.pessoa.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		ArrayList<DocenteTurmaRede> docentesTurma = (ArrayList<DocenteTurmaRede>) q.list();
		HashSet<DocenteTurmaRede> result = new HashSet<DocenteTurmaRede>();
		result.addAll(docentesTurma);
		
		return result;
	}
	
}
