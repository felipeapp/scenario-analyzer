/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/11/2008
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.AtividadeAvaliavel;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** DAO responsável por consultas específicas à Avaliação dos discentes. 
 * @see Avaliacao
 * @author Édipo Elder F. Melo
 *
 */
public class AvaliacaoDao extends GenericSigaaDAO {

	/** Retorna uma coleção de datas de avaliação cadastradas dentro de um número de dias especificados.
	 * @param dias
	 * @param discente
	 * @param ano
	 * @param semestre
	 * @return
	 * @throws DAOException
	 */
	public Collection<DataAvaliacao> findAvaliacoesData(int diasInicio, int diasFim, Discente discente, int ano, int semestre) throws DAOException {

		Query q = getSession().createQuery("from DataAvaliacao a where a.ativo = true and a.turma in " +
				"( select m.turma from MatriculaComponente m where m.situacaoMatricula.id = " + SituacaoMatricula.MATRICULADO.getId() + " and m.turma.ano = " + ano +
				" and m.turma.periodo = " + semestre + " and m.discente = " + discente.getId() + ") and a.data >= :dataInicio and a.data <= :dataFim order by  a.data asc");

		Calendar calendario = Calendar.getInstance();
		calendario.add(Calendar.DAY_OF_YEAR, diasFim);

		q.setDate("dataFim", calendario.getTime());
		q.setDate("dataInicio", CalendarUtils.subtraiDias(new Date(),diasInicio));
		 
		@SuppressWarnings("unchecked")
		Collection <DataAvaliacao> rs = q.list();
		return rs;
	}
	
	/**
	 * Apaga as avaliações informadas, nescessário por questões de eficiência.
	 *
	 * @param idsAvaliacao
	 * @throws DAOException
	 */
	public void removerAvaliacoes (List <Integer> idsAvaliacao) throws DAOException{
		update("delete from ensino.avaliacao_unidade where id_avaliacao in "+UFRNUtils.gerarStringIn(idsAvaliacao)+"");
	}
	

	/**
	 * Retorna uma lista de avaliaçõe de uma tarefa específica.
	 * @param idTarefa
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findAvaliacaoByTarefa(int idTarefa) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
	   		hql.append("select a.id, a.nota, a.peso, a.denominacao , a.abreviacao , a.notaMaxima, a.unidade.id, "+
	   					" a.unidade.unidade, a.unidade.matricula.id , a.atividadeQueGerou.id ");
	   		    
			hql.append("FROM Avaliacao a ");
			
			hql.append(" WHERE a.atividadeQueGerou.id = " + idTarefa );
				
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
				
			if ( resultado != null && !resultado.isEmpty() ){
				
				List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
				
				for ( Object[] o : resultado )
				{
					Integer i = 0;
					
					Avaliacao a = new Avaliacao();
					a.setId((Integer) o[i++]);
					a.setNota((Double) o[i++]);
					a.setPeso((Integer) o[i++]);
					a.setDenominacao((String) o[i++]);
					a.setAbreviacao((String) o[i++]);
					a.setNotaMaxima((Double) o[i++]);
					
					NotaUnidade unidade = new NotaUnidade();
					unidade.setId((Integer) o[i++]);
					unidade.setUnidade((Byte) o[i++]);
					MatriculaComponente matricula = new MatriculaComponente();
					matricula.setId((Integer) o[i++]);
					unidade.setMatricula(matricula);
					a.setUnidade(unidade);
					
					TarefaTurma tarefaQueGerou = new TarefaTurma();
					tarefaQueGerou.setId((Integer) o[i++]);
					a.setAtividadeQueGerou(tarefaQueGerou);
					
					avaliacoes.add(a);
				}	
				return avaliacoes;
			}			
			
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna uma lista de avaliaçõe de uma tarefa específica.
	 * @param idQuestionario
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findAvaliacoesByQuestionario (int idQuestionario) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
	   		hql.append("select a.id, a.nota, a.peso, a.denominacao, a.abreviacao, a.notaMaxima, a.unidade.id, "+
	   					" a.unidade.unidade, a.unidade.matricula.id , a.atividadeQueGerou.id ");
	   		    
			hql.append("FROM Avaliacao a ");
			
			hql.append(" WHERE a.atividadeQueGerou.id = " + idQuestionario );
				
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
				
			if ( resultado != null && !resultado.isEmpty() ){
				
				List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
				
				for ( Object[] o : resultado ) {
					Integer i = 0;
					
					Avaliacao a = new Avaliacao();
					a.setId((Integer) o[i++]);
					a.setNota((Double) o[i++]);
					a.setPeso((Integer) o[i++]);
					a.setDenominacao((String) o[i++]);
					a.setAbreviacao((String) o[i++]);
					a.setNotaMaxima((Double) o[i++]);
					
					NotaUnidade unidade = new NotaUnidade();
					unidade.setId((Integer) o[i++]);
					unidade.setUnidade((Byte) o[i++]);
					MatriculaComponente matricula = new MatriculaComponente();
					matricula.setId((Integer) o[i++]);
					unidade.setMatricula(matricula);
					a.setUnidade(unidade);
					
					QuestionarioTurma questionarioQueGerou = new QuestionarioTurma();
					questionarioQueGerou.setId((Integer) o[i++]);
					a.setAtividadeQueGerou(questionarioQueGerou);
					
					avaliacoes.add(a);
				}	
				return avaliacoes;
			}			
			
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/** Retorna uma lista de avaliações da unidade especificada.
	 * @param idNotaUnidade ID da unidade
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findByNotaUnidade(int idNotaUnidade) throws DAOException {
		Query q = getSession().createQuery("select a from Avaliacao a where a.unidade.id = ? order by a.id").setInteger(0, idNotaUnidade);
		
		@SuppressWarnings("unchecked")
		List <Avaliacao> rs = q.list();
		return rs;
	}

	/** Retorna uma lista de avaliações de uma turma especificada. 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Avaliacao> findAvaliacoes(Turma turma) throws DAOException {
		
		String projecao = " a.id_avaliacao , a.nota , a.denominacao , a.abreviacao , a.peso , a.nota_maxima , a.id_unidade , n.unidade , a.entidade , a.id_atividade_que_gerou ";
		
		Integer idTurma = turma.getId();
		if (turma.isAgrupadora())
			idTurma = ((Number) getSession().createQuery("select t.id from Turma t where t.turmaAgrupadora.id = "+idTurma).setMaxResults(1).uniqueResult()).intValue();
		
		String sql = "select " +projecao+ " from ensino.avaliacao_unidade a " +
					" inner join ensino.nota_unidade n on n.id_nota_unidade = a.id_unidade " +
					" inner join ensino.matricula_componente m on m.id_matricula_componente = n.id_matricula_componente " +
					" inner join ensino.turma t on t.id_turma = m.id_turma  " +
					" left join ava.atividade_avaliavel at on at.id_atividade_avaliavel = a.id_atividade_que_gerou " +
					" where t.id_turma in ("+idTurma+") " +
					" order by a.id_avaliacao asc ";
		
		Query q = getSession().createSQLQuery(sql);
		List<Object[]> resultado = q.list();
		
		if ( resultado != null && !resultado.isEmpty() ){
			
			List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
			
			for ( Object[] o : resultado )
			{
				Integer i = 0;
				
				Avaliacao a = new Avaliacao();

				a.setId((Integer) o[i++]);
				Number nota = (Number) o[i++];
				a.setNota( nota != null ? nota.doubleValue() : null );
				a.setDenominacao((String) o[i++]);
				a.setAbreviacao((String) o[i++]);
				a.setPeso(((Number) o[i++]).intValue());
				Number notaMax = (Number) o[i++];
				if (notaMax != null){
					Double notaMaxDouble = Math.round(notaMax.doubleValue() * 10D) / 10D;
					a.setNotaMaxima( notaMaxDouble );
				}
				
				NotaUnidade unidade = new NotaUnidade();
				unidade.setId((Integer) o[i++]);
				unidade.setUnidade(((Number) o[i++]).byteValue());
				a.setUnidade(unidade);
				
				a.setEntidade((Integer) o[i++]);
				if ( a.getEntidade() != null ) {
					AtividadeAvaliavel atividadeQueGerou = null;
					if ( a.getEntidade() == AtividadeAvaliavel.TIPO_TAREFA )
						atividadeQueGerou = new TarefaTurma();
					else if ( a.getEntidade() == AtividadeAvaliavel.TIPO_QUESTIONARIO )
						atividadeQueGerou = new QuestionarioTurma();
					a.setAtividadeQueGerou(atividadeQueGerou);
					a.getAtividadeQueGerou().setId((Integer) o[i++]);
				} else{
					i++;
				}
				avaliacoes.add(a);
			}
			return avaliacoes;
		}	
		return new ArrayList<Avaliacao>();
	}

	/** Retorna uma lista de avaliações de todas as subturmas de uma turma agrupadora. 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findAvaliacoesByTurmaAgrupadora(Turma turma) throws DAOException {
		
		Collection<Integer> ids = new ArrayList<Integer>();
		for ( Turma subturma : turma.getSubturmas() ){
			ids.add(subturma.getId());
		}
		
		Criteria c = getSession().createCriteria(Avaliacao.class);
		Criteria cAv = c.createCriteria("unidade");
		Criteria cMat = cAv.createCriteria("matricula");
		cMat.add(Restrictions.in("turma.id", ids));
		c.addOrder(Order.asc("id"));
		
		@SuppressWarnings("unchecked")
		List <Avaliacao> rs = c.list();
		return rs;
	}
	
	/** Retorna uma coleção de notas referente a uma unidade 
	 *  e a uma matrícula em um componente especificado.
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public Collection<NotaUnidade> findNotasByMatricula(MatriculaComponente matricula) throws DAOException {
		try {
			Query q = getSession().createQuery("select n from NotaUnidade n where n.matricula.id = ? and n.ativo = trueValue() order by n.unidade asc");
			q.setInteger(0, matricula.getId());
			
			@SuppressWarnings("unchecked")
			Collection <NotaUnidade> rs = q.list();
			
			// Itera sobre as avaliações das unidades para populá-las.
			for (NotaUnidade n : rs)
				for (Avaliacao a : n.getAvaliacoes())
					a.getId();
			
			return rs;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de notas das unidades de uma turma.
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	public Collection<NotaUnidade> findNotasByTurma(Turma t) throws DAOException {
		String idsTurmas = "(" + t.getId() + ")";
		if (t.isAgrupadora())
			idsTurmas = UFRNUtils.gerarStringIn(getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora in " + idsTurmas).list());
		
		Query q = getSession().createQuery("select new NotaUnidade(n.id,  n.faltas,  n.nota,  n.unidade, mat.id )" +
				" from NotaUnidade n join n.matricula mat join mat.turma t where n.ativo = trueValue() and t.id in " + idsTurmas +
				" order by n.unidade, mat.id asc");
		
		@SuppressWarnings("unchecked")
		Collection <NotaUnidade> rs = q.list();
		return rs;
	}
	
	/** Verifica se o docente já cadastrou alguma nota na unidade.
	 * @param t
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiNotasCadastradasNaUnidade(Turma t, int unidade) throws DAOException {
		
		String idsTurmas = "(" + t.getId() + ")";
		if (t.isAgrupadora())
			idsTurmas = UFRNUtils.gerarStringIn(getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora in " + idsTurmas).list());
		
		Query q = getSession().createQuery("select count(*) from NotaUnidade n " +
				" join n.matricula mat join mat.turma t " +
				" where n.ativo = trueValue() " +
				" and t.id in " + idsTurmas +
				" and n.unidade = " + unidade +
				" and n.nota is not null " +
				" and mat.situacaoMatricula.id = "+SituacaoMatricula.MATRICULADO.getId()
				);
		
		Integer res = ((Number) q.uniqueResult()).intValue();
		
		if ( res != null && res > 0 )
			return true;
		else
			return false;
	}

	/** Verifica se unidade foi dividida em avaliações.
	 * @param t
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiAvaliacoesNaUnidade(Turma t, int unidade) throws DAOException {
		
		String idsTurmas = "(" + t.getId() + ")";
		if (t.isAgrupadora())
			idsTurmas = UFRNUtils.gerarStringIn(getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora in " + idsTurmas).list());
		
		Query q = getSession().createQuery("select count(a) from Avaliacao a " +
				" join a.unidade n join n.matricula mat join mat.turma t " +
				" where n.ativo = trueValue() " +
				" and mat.turma.id in " + idsTurmas +
				" and n.unidade = " + unidade +
				" and mat.situacaoMatricula.id = "+SituacaoMatricula.MATRICULADO.getId()
				);
		
		Integer res = ((Number) q.uniqueResult()).intValue();
		
		if ( res != null && res > 0 )
			return true;
		else
			return false;
	}
	
	/** Retorna uma lista de avaliações de uma turma/unidade especificados.
	 * @param turma
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findByTurmaUnidade(Turma turma, int unidade) throws DAOException {
		Query q = getSession().createQuery("select new Avaliacao(a.id, a.nota, a.unidade, a.denominacao, a.abreviacao) " +
				"from Avaliacao a where a.unidade.matricula.turma.id = ? and a.unidade.unidade = ? order by a.id").setInteger(0, turma.getId()).setInteger(1, unidade);

		@SuppressWarnings("unchecked")
		List <Avaliacao> rs = q.list();
		return rs;
	}

	/** Retorna uma lista de avaliações de um discente ou discentes (se a tarefa for em grupo) que responderam a uma tarefa.
	 * @param resposta
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findByRespostaTarefa(RespostaTarefaTurma resposta) throws DAOException {
		
		Query q = null;
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(	"select a.id_avaliacao , a.nota from ensino.avaliacao_unidade as a " +
		"inner join ava.tarefa as t on t.id_tarefa_turma = a.id_atividade_que_gerou " +
		"inner join ava.resposta_tarefa_turma as r on r.id_tarefa = t.id_tarefa_turma " +
		"inner join ensino.nota_unidade as n on n.id_nota_unidade = a.id_unidade " +
		"inner join ensino.matricula_componente as m on m.id_matricula_componente = n.id_matricula_componente ");
		
		if ( resposta.getTarefa().isEmGrupo() && resposta.isExisteGrupo())
			sql.append("inner join ava.grupo_discentes as gd on gd.id_grupo_discentes = r.id_grupo_discentes " +
					"inner join ava.grupo_discentes_discente as gdd on gdd.id_grupo_discentes = gd.id_grupo_discentes " +
					"where n.ativo = trueValue() and r.id = " +resposta.getId()+ " and r.ativo = true and gdd.id_discente = m.id_discente" );
		else
			sql.append("inner join comum.usuario as u on u.id_usuario = r.id_usuario_envio " +
					"inner join discente as d on u.id_pessoa = d.id_pessoa " +
					"where n.ativo = trueValue() and r.id = " +resposta.getId()+ " and r.ativo = true and d.id_discente = m.id_discente" );
		

		q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		if ( result != null )
		{	
			ArrayList<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
			
			for ( Object[] linha : result )	{
				
				Avaliacao avaliacao = new Avaliacao();
				avaliacao.setId(( Integer ) linha[0]);
				
				if ( linha[1] != null )
					avaliacao.setNota( ((Number) linha[1]).doubleValue() );
				
				avaliacoes.add(avaliacao);
			}
			return avaliacoes;
		}	
		
		return null;
	}
	
	/** Retorna uma lista de avaliações dos discentes de um grupo.
	 * @param resposta
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findByRespostasDiscentes(RespostaTarefaTurma resposta , ArrayList<Integer> idsDiscentes) throws DAOException {
		
		Query q = null;
		
		String ids = UFRNUtils.gerarStringIn(idsDiscentes);
		
		q = getSession().createSQLQuery(
				"select a.id_avaliacao , a.nota from ensino.avaliacao_unidade as a " +
				"inner join ava.tarefa as t on t.id_tarefa_turma = a.id_atividade_que_gerou " +
				"inner join ava.resposta_tarefa_turma as r on r.id_tarefa = t.id_tarefa_turma " +
				"inner join ensino.nota_unidade as n on n.id_nota_unidade = a.id_unidade " +
				"inner join ensino.matricula_componente as m on m.id_matricula_componente = n.id_matricula_componente " +
				"inner join ava.grupo_discentes as gd on gd.id_grupo_discentes = r.id_grupo_discentes " +
				"inner join ava.grupo_discentes_discente as gdd on gdd.id_grupo_discentes = gd.id_grupo_discentes " +
				"where n.ativo = trueValue() and r.id = " +resposta.getId()+ " and r.ativo = true and gdd.id_discente = m.id_discente and gdd.id_discente in " +ids);
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		if ( result != null )
		{	
			ArrayList<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
			
			for ( Object[] linha : result )
			{
				Avaliacao avaliacao = new Avaliacao();
				avaliacao.setId(( Integer ) linha[0]);
				
				if ( linha[1] != null )
					avaliacao.setNota( ((Number) linha[1]).doubleValue() );
				
				avaliacoes.add(avaliacao);
			}
			return avaliacoes;
		}	
		
		return null;
	}
	
	/** Calcula a soma das notas das avaliações de uma turma/unidade.
	 * 
	 * @param idTurma
	 * @param unidade
	 * @return
	 * @throws DAOException 
	 */
	public double getSomaNotaAvaliacao(Turma turma, int unidade) throws DAOException {
		
		String idsTurmas = "";
		
		// Se for passada uma turma agrupadora, busca por suas subturmas
		if (turma.isAgrupadora()){
			@SuppressWarnings("unchecked")
			List <Integer> ids = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = " + turma.getId()).list();
			
			if (!ids.isEmpty()){
				for (Integer id : ids)
					idsTurmas += (idsTurmas.equals("") ? "" : ",") + id;
				idsTurmas = "(" + idsTurmas + ")";
			}
		}
		
		if (idsTurmas.equals(""))
			idsTurmas = "(" + turma.getId() + ")";
		
		String situacoes = "("+SituacaoMatricula.MATRICULADO.getId()+", "+SituacaoMatricula.APROVADO.getId()+", "+SituacaoMatricula.REPROVADO.getId()+", "+SituacaoMatricula.REPROVADO_FALTA.getId()+")";
		
		Double notaMaxima = getJdbcTemplate().queryForDouble("select sum(nota_maxima) from ensino.avaliacao_unidade a where id_unidade in ( "
				+ "select id_nota_unidade from ensino.nota_unidade where ativo = trueValue() and unidade = ? and id_matricula_componente in ( "
				+ "select id_matricula_componente from ensino.matricula_componente where id_turma in "+idsTurmas+" and id_situacao_matricula in " + situacoes + " " + BDUtils.limit(1) + "))", new Object[] { unidade });
	
		notaMaxima = Math.round(notaMaxima * 10D) / 10D;
		
		return notaMaxima;
	}

	/**
	 * Verifica se a denominação e a abreviação estão disponíveis para o cadastro da avaliação na turma.
	 * 
	 * @param turma
	 * @param denominacao
	 * @param abreviacao
	 * @return
	 * @throws DAOException 
	 */
	public boolean isNomeAvaliacaoDisponivel(Turma turma, String denominacao, String abreviacao) throws DAOException {
		
		String idTurmas = "";
		
		if (turma.isAgrupadora()){
			@SuppressWarnings("unchecked")
			List <Integer> ids = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = " + turma.getId()).list();
			
			for (Integer id : ids)
				idTurmas += (idTurmas.equals("") ? "" : ",") + id;
		}
		
		if (idTurmas.equals(""))
			idTurmas = "" + turma.getId();
		
		idTurmas = "("+idTurmas+")";
		
		Number count = (Number) getSession().createSQLQuery (
				"select count (*) from ensino.avaliacao_unidade a join ensino.nota_unidade n on n.id_nota_unidade = a.id_unidade " +
				"join ensino.matricula_componente m using (id_matricula_componente) where n.ativo = trueValue() and m.id_turma in "+idTurmas+" and a.denominacao ilike '"+denominacao.trim().replace("'", "''")+"' " +
				"and a.abreviacao ilike '"+abreviacao.trim().replace("'", "''")+"'"
		).uniqueResult();
		
		return count.intValue() == 0;
	}
	
	
	/**
	 * Deleta todas as avaliações de uma matrícula
	 * @param mat
	 * @return
	 * @throws DAOException
	 */
	public int deleteAvaliacaoByMatricula(Collection<MatriculaComponente> matriculas, int numUnidade) throws DAOException {
		
		String hql = "delete Avaliacao aval " +
				"	where aval.id in " +
				"			(select aval.id from Avaliacao aval " +
				"			where aval.unidade.matricula.id in " + UFRNUtils.gerarStringIn(matriculas) + 
				"			and aval.unidade.unidade > :numUnidade)";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("numUnidade", numUnidade);
		
		return q.executeUpdate();
	}	
	
	/**
	 * Retorna a avaliação de uma resposta de um questionário.
	 * 
	 * @param tarefa
	 * @return
	 * @throws DAOException 
	 */
	public ArrayList<Avaliacao> findByRespostaQuestionario ( EnvioRespostasQuestionarioTurma respostaQuestionario ) throws HibernateException, DAOException {
		Query q = null;
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(	"select a.id_avaliacao , a.nota from ensino.avaliacao_unidade as a " +
		"inner join ava.questionario_turma as q on q.id_questionario_turma = a.id_atividade_que_gerou " +
		"inner join ava.envio_respostas_questionario_turma as r on r.id_questionario_turma = q.id_questionario_turma " +
		"inner join ensino.nota_unidade as n on n.id_nota_unidade = a.id_unidade " +
		"inner join ensino.matricula_componente as m on m.id_matricula_componente = n.id_matricula_componente " +
		"inner join comum.usuario as u on u.id_usuario = r.id_usuario_envio " +
		"inner join discente as d on u.id_pessoa = d.id_pessoa " +
		"where n.ativo = trueValue() and r.id_envio_respostas_questionario_turma = " +respostaQuestionario.getId()+ " and r.ativo = true and d.id_discente = m.id_discente" );
		

		q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		if ( result != null )
		{	
			ArrayList<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
			
			for ( Object[] linha : result )	{
				
				Avaliacao avaliacao = new Avaliacao();
				avaliacao.setId(( Integer ) linha[0]);
				
				if ( linha[1] != null )
					avaliacao.setNota( ((Number) linha[1]).doubleValue() );
				
				avaliacoes.add(avaliacao);
			}
			return avaliacoes;
		}	
		
		return null;
	}
	
	/**
	 * Retorna a avaliação de uma resposta de um questionário.
	 * 
	 * @param tarefa
	 * @return
	 * @throws DAOException 
	 */
	public ArrayList<Avaliacao> findByConjuntoRespostaQuestionario ( ConjuntoRespostasQuestionarioAluno conjunto) throws HibernateException, DAOException {
		Query q = null;
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(	"select a.id_avaliacao , a.nota from ensino.avaliacao_unidade as a " +
		"inner join ava.questionario_turma as q on q.id_questionario_turma = a.id_atividade_que_gerou " +
		"inner join ava.conjunto_respostas_questionario as c on c.id_questionario_turma = q.id_questionario_turma " +
		"inner join ava.envio_respostas_questionario_turma as r on r.id_conjunto_respostas_questionario = c.id_conjunto_respostas_questionario " +
		"inner join ensino.nota_unidade as n on n.id_nota_unidade = a.id_unidade " +
		"inner join ensino.matricula_componente as m on m.id_matricula_componente = n.id_matricula_componente " +
		"inner join comum.usuario as u on u.id_usuario = r.id_usuario_envio " +
		"inner join discente as d on u.id_pessoa = d.id_pessoa " +
		"where n.ativo = trueValue() and c.id_conjunto_respostas_questionario = " +conjunto.getId()+ " and c.ativo = trueValue() and r.ativo = trueValue() and d.id_discente = m.id_discente" );
		

		q = getSession().createSQLQuery(sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		if ( result != null )
		{	
			ArrayList<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
			
			for ( Object[] linha : result )	{
				
				Avaliacao avaliacao = new Avaliacao();
				avaliacao.setId(( Integer ) linha[0]);
				
				if ( linha[1] != null )
					avaliacao.setNota( ((Number) linha[1]).doubleValue() );
				
				avaliacoes.add(avaliacao);
			}
			return avaliacoes;
		}	
		
		return null;
	}
	
	/**
	 * Retorna uma lista de avaliaçõe de uma atividade específica.
	 * @param idTarefa
	 * @return
	 * @throws DAOException
	 */
	public List<Avaliacao> findAvaliacaoByAtividade(int idAtividade) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
	   		hql.append("select a.id, a.nota, a.peso, a.denominacao , a.abreviacao , a.notaMaxima, a.unidade.id, "+
	   					" a.unidade.unidade, a.unidade.nota , a.unidade.matricula.id , a.unidade.matricula.situacaoMatricula.id , a.atividadeQueGerou.id ");
	   		    
			hql.append("FROM Avaliacao a ");
			
			hql.append(" WHERE a.atividadeQueGerou.id = " + idAtividade );
				
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
				
			if ( resultado != null && !resultado.isEmpty() ){
				
				List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
				
				for ( Object[] o : resultado )
				{
					Integer i = 0;
					
					Avaliacao a = new Avaliacao();
					a.setId((Integer) o[i++]);
					a.setNota((Double) o[i++]);
					a.setPeso((Integer) o[i++]);
					a.setDenominacao((String) o[i++]);
					a.setAbreviacao((String) o[i++]);
					a.setNotaMaxima((Double) o[i++]);
					
					NotaUnidade unidade = new NotaUnidade();
					unidade.setId((Integer) o[i++]);
					unidade.setUnidade((Byte) o[i++]);
					unidade.setNota((Double) o[i++]);
					MatriculaComponente matricula = new MatriculaComponente();
					matricula.setId((Integer) o[i++]);
					unidade.setMatricula(matricula);
					SituacaoMatricula situacao = new SituacaoMatricula((Integer) o[i++]);
					unidade.getMatricula().setSituacaoMatricula(situacao);
					a.setUnidade(unidade);
					
					TarefaTurma tarefaQueGerou = new TarefaTurma();
					tarefaQueGerou.setId((Integer) o[i++]);
					a.setAtividadeQueGerou(tarefaQueGerou);
					
					avaliacoes.add(a);
				}	
				return avaliacoes;
			}			
			
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/** Verifica se foram cadastradas mais de uma avaliação para uma unidade.
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	public boolean isMaisDeUmaAvaliacao(Integer idAvaliacao , Integer idNotaUnidade) throws DAOException {
		try {
			Query q = getSession().createQuery("SELECT count(*) FROM Avaliacao a WHERE a.unidade.id = " +idNotaUnidade+ " and a.id != " +idAvaliacao);
			boolean res = ((Long) q.uniqueResult() != 0);
			
			return res;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/** Retorna uma lista das diferentes avaliações cadastradas na turma  
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Avaliacao> findAvaliacoesDistintasByTurma(Turma turma) throws DAOException {
		try {
			
			Turma t = null;
			if ( turma.getTurmaAgrupadora() != null )
				t = turma.getTurmaAgrupadora();
			else
				t = turma;
			
			String idsTurmas = "";
			
			// Se for passada uma turma agrupadora, busca por suas subturmas
			if (t.isAgrupadora()){
				List <Integer> ids = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = " + t.getId()).list();
				
				if (!ids.isEmpty()){
					for (Integer id : ids)
						idsTurmas += (idsTurmas.equals("") ? "" : ",") + id;
					idsTurmas = "(" + idsTurmas + ")";
				}
			}
			
			if (idsTurmas.equals(""))
				idsTurmas = "(" + t.getId() + ")";
			
			Query q = getSession().createSQLQuery("select a.denominacao , a.abreviacao , a.peso , a.nota_maxima , a.entidade , a.id_atividade_que_gerou , n.unidade from ensino.avaliacao_unidade a " +
													" inner join ensino.nota_unidade as n on n.id_nota_unidade = a.id_unidade " +
													" inner join ensino.matricula_componente m using ( id_matricula_componente ) " +
													" where m.id_turma in " +idsTurmas+ 
													" group by a.denominacao , a.abreviacao , a.peso , a.nota_maxima , a.entidade , a.id_atividade_que_gerou , n.unidade " +
													" order by min(a.id_avaliacao)");
			List<Object[]> resultado = q.list();
			
			if ( resultado != null && !resultado.isEmpty() ){
				
				List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
				
				for ( Object[] o : resultado )
				{
					Integer i = 0;
					
					Avaliacao a = new Avaliacao();

					a.setDenominacao((String) o[i++]);
					a.setAbreviacao((String) o[i++]);
					a.setPeso(((Number) o[i++]).intValue());
					Number nota = (Number) o[i++];
					a.setNotaMaxima( nota != null ? nota.doubleValue() : null );
					a.setEntidade((Integer) o[i++]);
					
					if ( a.getEntidade() != null ) {
						AtividadeAvaliavel atividadeQueGerou = null;
						if ( a.getEntidade() == AtividadeAvaliavel.TIPO_TAREFA )
							atividadeQueGerou = new TarefaTurma();
						else if ( a.getEntidade() == AtividadeAvaliavel.TIPO_QUESTIONARIO )
							atividadeQueGerou = new QuestionarioTurma();
						a.setAtividadeQueGerou(atividadeQueGerou);
						a.getAtividadeQueGerou().setId((Integer) o[i++]);
					} else
						i++;
					NotaUnidade unidade = new NotaUnidade();
					unidade.setUnidade(((Number) o[i++]).byteValue());
					a.setUnidade(unidade);
					
					avaliacoes.add(a);
				}	
				return avaliacoes;
			}
			
			return null;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
}
