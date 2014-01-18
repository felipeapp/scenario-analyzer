package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO Responsável por realizar as consultas referente a rotina de consolidação
 * parcial de notas do IMD.
 * 
 * @author Rafael Silva
 * 
 */
public class ConsolidacaoParcialIMDDao extends GenericSigaaDAO {
		
	/**
	 * Detalha as notas do discente.
	 *  
	 * @param idTurmaEntrada
	 * @param idDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<NotaIMD> findNotasAluno(int idTurmaEntrada, int idDiscente) throws HibernateException, DAOException{									
		// Lista os discentes matriculados na turma
		String projecao =
				//DADOS DO DISCENTE
				"d.discente.matricula, d.discente.pessoa.nome, m.id, m.turma.id, m.turma.disciplina.detalhes.nome," +
						
				//PARTICIPAÇÃO PRESENCIAL
				" (SELECT (sum(a.participacaoPresencial)/count(*)) " +
				" FROM AcompanhamentoSemanalDiscente a, CargaHorariaSemanalDisciplina c " +
				" where a.periodoAvaliacao.id = c.periodoAvaliacao.id " +
				" and c.disciplina.id = m.turma.disciplina.id"+
				" and a.discente.id=d.discente.id" +	
				" and c.cargaHoraria != 0), "+

				//PARTICIPAÇÃO VIRTUAL
				" (SELECT (sum(a.participacaoVirtual)/count(*)) " +
				" FROM AcompanhamentoSemanalDiscente a, CargaHorariaSemanalDisciplina c " +
				" where a.periodoAvaliacao.id = c.periodoAvaliacao.id " +
				" and c.disciplina.id = m.turma.disciplina.id"+
				" and a.discente.id=d.discente.id" +
				" and c.cargaHoraria != 0), " +

				//FREQUENCIA
				" m.numeroFaltas," +
								
				//PARTICIPACAO TOTAL
				"(SELECT n.nota FROM NotaUnidade n WHERE n.unidade = 1 and n.matricula.id = m.id )," +
				
				//ATIVIDADES ONLINE
				"(SELECT n.nota FROM NotaUnidade n WHERE n.unidade = 2 and n.matricula.id = m.id )," +
				
				//PROVA ESCRITA
				"(SELECT n.nota FROM NotaUnidade n WHERE n.unidade = 3 and n.matricula.id = m.id ),"+
				
				//carga horária da disciplina
				"m.turma.disciplina.detalhes.chTotal";
				
		//CONSULTA GERAL
		String hql = "SELECT "+projecao+
				" FROM DiscenteTecnico d, MatriculaComponente m" +
				" WHERE  d.discente.id = m.discente.id " +
				" and d.turmaEntradaTecnico.id = "+idTurmaEntrada +
				" and d.discente.id ="+idDiscente +
				" and d.discente.status = "+ StatusDiscente.ATIVO+
				" and m.situacaoMatricula.id = 2"+ //Matriculado 
				" ORDER BY m.componente.detalhes.nome";
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		Collection<Object[]> res = q.list();
		
		List<NotaIMD> notas = new ArrayList<NotaIMD>();		
		for (Object[] reg : res) {
			int col = 0;
			NotaIMD n = new NotaIMD(); 
			Long matriculaDiscente = (Long) reg[col++];
			String nomeDiscente = (String) reg[col++];			
			Integer matriculaComponente = (Integer)reg[col++];
			Integer idTurma = (Integer) reg[col++];
			String nomeComponente = (String) reg[col++];
			Double notaPP = (Double)reg[col++];
			Double notaPV= (Double)reg[col++];
			Integer chnf = (Integer)reg[col++];
			Double notaPT = (Double)reg[col++];
			Double notaAE =  (Double)reg[col++];
			Double notaPE = (Double)reg[col++];
			Integer chTotalDisciplina = (Integer)reg[col++];
									
			n.setDiscente(new DiscenteTecnico());
			n.getDiscente().setDiscente(new Discente());
			n.getDiscente().getDiscente().setMatricula(matriculaDiscente);	
			
			n.getDiscente().getDiscente().setPessoa(new Pessoa());
			n.getDiscente().getDiscente().getPessoa().setNome(nomeDiscente);
			
			n.setMatriculaComponente(new MatriculaComponente());
			n.getMatriculaComponente().setId(matriculaComponente);
			
			n.getMatriculaComponente().setTurma(new Turma());
			n.getMatriculaComponente().getTurma().setId(idTurma);
			
			n.getMatriculaComponente().getTurma().setDisciplina(new ComponenteCurricular());			
			n.getMatriculaComponente().getTurma().getDisciplina().setNome(nomeComponente);
			
			n.getMatriculaComponente().getTurma().getDisciplina().getDetalhes().setChTotal(chTotalDisciplina);
			
			n.setParticipacaoPresencial(notaPP);
			n.setParticipacaoVirtual(notaPV);
			
			if (n.getParticipacaoPresencial() != null && n.getParticipacaoVirtual() != null && notaPV != null) {
				n.getParticipacaoTotal().setNota((notaPP+notaPV)/2);
			}
			
			n.getAtividadeOnline().setNota(notaAE);
			n.getProvaEscrita().setNota(notaPE);

			n.setChnf(chnf);			
			
			notas.add(n);
		}		
		return notas;	
	}
	

	/**
	 * Retorna as notas dos alunos matriculados na turma de entrada informada
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
//	public List<MatriculaTurma> findAlunosTurma(int idTurmaEntrada)
//			throws HibernateException, DAOException {
//		String hql = "select dc.id, dc.discente.matricula, dc.discente.pessoa.nome, " +
//				" (sum(mc.mediaFinal * mc.detalhesComponente.chTotal)/sum(mc.detalhesComponente.chTotal)), sum(mc.numeroFaltas), dc.discente.id"+
//				" from DiscenteTecnico dc, MatriculaComponente mc " +
//				" where dc.discente.id = mc.discente.id " +
//				" and dc.turmaEntradaTecnico.id = "+ idTurmaEntrada+
//				" and dc.discente.status=" + StatusDiscente.ATIVO+
//				" and mc.situacaoMatricula.id = 2"+ 
//				" group by dc.id, dc.discente.matricula, dc.discente.pessoa.nome " +
//				" order by dc.discente.pessoa.nome"; 
//		Query q = getSession().createQuery(hql);
//		
//		@SuppressWarnings("unchecked")
//		Collection<Object[]> res = q.list();
//		List<MatriculaTurma> matriculas = new ArrayList<MatriculaTurma>(0);
//		if (res != null) {
//			MatriculaTurma mt;
//			for (Object[] reg : res) {
//				int i = 0;
//				mt = new MatriculaTurma();
//				mt.getDiscente().setId((Integer) reg[i++]);
//				mt.getDiscente().setMatricula((Long)reg[i++]);
//				mt.getDiscente().setPessoa(new Pessoa());
//				mt.getDiscente().getPessoa().setNome((String)reg[i++]);
//				mt.setNotaParcial((Double)reg[i++]);
//				mt.setChnf((Long)reg[i++]);
//				mt.getDiscente().getDiscente().setId((Integer) reg[i++]);
//			
//				matriculas.add(mt);
//			}
//		}
//		return matriculas;
//	}
	
	
	
	public List<MatriculaTurma> findAlunosTurma(int idTurmaEntrada)
			throws HibernateException, DAOException {
		String hql = "select dc.id, dc.discente.matricula, dc.discente.pessoa.nome, " +
				" sum(mc.numeroFaltas), dc.discente.id"+
				" from DiscenteTecnico dc, MatriculaComponente mc " +
				" where dc.discente.id = mc.discente.id " +
				" and dc.turmaEntradaTecnico.id = "+ idTurmaEntrada+
				" and dc.discente.status=" + StatusDiscente.ATIVO+
				" and mc.situacaoMatricula.id = 2"+ 
				" group by dc.id, dc.discente.matricula, dc.discente.pessoa.nome " +
				" order by dc.discente.pessoa.nome"; 
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		Collection<Object[]> res = q.list();
		List<MatriculaTurma> matriculas = new ArrayList<MatriculaTurma>(0);
		if (res != null) {
			MatriculaTurma mt;
			for (Object[] reg : res) {
				int i = 0;
				mt = new MatriculaTurma();
				mt.getDiscente().setId((Integer) reg[i++]);
				mt.getDiscente().setMatricula((Long)reg[i++]);
				mt.getDiscente().setPessoa(new Pessoa());
				mt.getDiscente().getPessoa().setNome((String)reg[i++]);				
				mt.setChnf((Long)reg[i++]);
				mt.getDiscente().getDiscente().setId((Integer) reg[i++]);
				
				//calcula a média do aluno no módulo
				mt.setNotaParcial(mt.getMediaCalculada(findNotasAluno(idTurmaEntrada, mt.getDiscente().getId())));
				

				matriculas.add(mt);
			}
		}
		return matriculas;
	}
	
	/**
	 * Retorna as tutorias ativas de uma turma de entrada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<TutoriaIMD> findDadosTutoria(int idTurmaEntrada) throws HibernateException, DAOException{
		String projecao = "id, turmaEntrada.id, turmaEntrada.especializacao.descricao, turmaEntrada.anoReferencia, " +
				"turmaEntrada.periodoReferencia, turmaEntrada.dadosTurmaIMD.cronograma.modulo.id, turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao, turmaEntrada.dadosTurmaIMD.cronograma.modulo.cargaHoraria, turmaEntrada.dadosTurmaIMD.id, tutor.pessoa.nome";
		
		String hql = "select "+ projecao + " from TutoriaIMD where dataFimTutoria is null and turmaEntrada.id = "+ idTurmaEntrada;
		
		Query q = getSession().createQuery(hql);
		
		List<TutoriaIMD> tutorias = (List<TutoriaIMD>) HibernateUtils.parseTo(q.list(), projecao, TutoriaIMD.class);
		if (tutorias.isEmpty()) {
			tutorias = Collections.emptyList(); 
		}
		return tutorias;
	}
	
	/**
	 * Retorna true caso a turma de entrada possua acompanhamentos pendentes.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean possuiAcompanhamentosPendentes(int idTurmaEntrada) throws HibernateException, DAOException{
		String hql = "SELECT count(*) FROM AcompanhamentoSemanalDiscente a, CargaHorariaSemanalDisciplina c " +
				" WHERE (a.participacaoPresencial is null or a.participacaoVirtual is null or a.frequencia is null) " +
				" and c.periodoAvaliacao.id = a.periodoAvaliacao.id"+
				" and c.cargaHoraria > 0"+
				" and a.discente.turmaEntradaTecnico.id = "+idTurmaEntrada +
				" and a.discente.discente.status = "+StatusDiscente.ATIVO;		
		Query q = getSession().createQuery(hql);
		
		
		Long count = (Long) q.uniqueResult();
				
		if (count>0) {
			return true;
		}		
		return false;
	}
	
	
	/**
	 * Retorna as entidades Matriculas Componentes vinculadas a turma de entrada informada.
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculasComponentes (int idTurmaEntrada) throws HibernateException, DAOException{	
		String projecao = "m.id, m.turma.id, m.situacaoMatricula.id, dt.discente.id";
		
		String hql = "SELECT "+projecao+" FROM MatriculaComponente m, DiscenteTecnico dt" +
				" where m.discente.id = dt.discente.id" +
				" and dt.turmaEntradaTecnico.id = "+idTurmaEntrada+
				" and dt.discente.status=" + StatusDiscente.ATIVO+
				" and m.situacaoMatricula.id = 2"; 
					
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		Collection<Object[]> res = q.list();
		
		List<MatriculaComponente> listaMatriculas= new ArrayList<MatriculaComponente>();
		
		for (Object[] reg : res) {
			Integer i = 0;
			MatriculaComponente matricula = new MatriculaComponente();
			matricula.setId((Integer) reg[i]);
			
			matricula.setTurma(new Turma());
			matricula.getTurma().setId((Integer) reg[i++]);
			
			matricula.setSituacaoMatricula(new SituacaoMatricula());			
			if (reg[i++]!=null) {
				matricula.getSituacaoMatricula().setId((Integer) reg[i++]);
			}
			
			matricula.setDiscente(new Discente());
			matricula.getDiscente().setId((Integer)reg[i++]);
						
			listaMatriculas.add(matricula);			
		}				
		return listaMatriculas;
	}
	
	/**
	 * Retorna as Turmas componentes vinculadas a turma de entrada informada. 
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTurmasComponente(int idTurmaEntrada) throws DAOException{
		String projecao = "t.id, t.disciplina.id, t.situacaoTurma.id";
		String hql = "SELECT "+ projecao +"  FROM Turma t, TurmaEntradaTecnico te" +
				" where t.especializacao.id = te.especializacao.id" +
				" and te.id = "+idTurmaEntrada;
						
		Query q =getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		Collection<Object[]> res = q.list();
		
		List<Turma> listaTurmas = new ArrayList<Turma>();
		 for (Object[] reg : res) {
			 int i=0;
			 Turma t = new Turma();
			 t.setId((Integer) reg[i]);
			 
			 t.setDisciplina(new ComponenteCurricular());
			 t.getDisciplina().setId((Integer)reg[i++]);
			 
			 t.setSituacaoTurma(new SituacaoTurma());
			 t.getSituacaoTurma().setId((Integer)reg[i++]);
			 
			 listaTurmas.add(t);
		}						
		return listaTurmas;
	}
	
	/**
	 * Retorno true caso a turma já tenha sido consolidada parcialmente. Caso contrário false. 
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Boolean isConsolidadoParcialmente(int idTurmaEntrada) throws HibernateException, DAOException{
		String projecao = "dadosTurmaIMD.consolidadoParcialmente";		
		String hql = "SELECT "+projecao+" FROM TurmaEntradaTecnico" +				
				" where id = "+ idTurmaEntrada;				
		Query q = getSession().createQuery(hql);
		if (q.uniqueResult()!=null) {
			return (Boolean)q.uniqueResult();
		}
		return false;		
	}	
}
