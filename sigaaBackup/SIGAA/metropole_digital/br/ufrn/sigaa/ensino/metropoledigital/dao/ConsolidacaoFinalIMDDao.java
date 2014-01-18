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
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.SituacaoMatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class ConsolidacaoFinalIMDDao extends GenericSigaaDAO{

	/**
	 * Lista as matrículas vinculadas a turma selecionada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<MatriculaTurma> findMatriculasTurma(int idTurmaEntrada) throws HibernateException, DAOException{
		String projecao = "id, discente.id, discente.discente.id, discente.discente.matricula, discente.discente.pessoa.nome, chnf, notaParcial, notaFinal, situacaoMatriculaTurma.id";
		
		String hql = "SELECT "+projecao+" FROM MatriculaTurma " +
				" WHERE turmaEntrada.id = "+idTurmaEntrada+
				" ORDER BY discente.discente.pessoa.nome";		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<MatriculaTurma> listaMatriculasTurma = (List<MatriculaTurma>)HibernateUtils.parseTo(q.list(), projecao, MatriculaTurma.class);
		
		List<MatriculaComponente> listaMatriculasComponentes = findMatriculasComponentes(idTurmaEntrada);		
		
		for (MatriculaTurma mt : listaMatriculasTurma) {
			for (MatriculaComponente mc : listaMatriculasComponentes) {												
				if (mt.getDiscente().getDiscente().getId() == mc.getDiscente().getId()) {
					mt.getMatriculasComponentes().put(mc.getTurma().getId(), mc);
				}				
			}
		} 		
		return listaMatriculasTurma;
	}
	
	/**
	 * Lista as matrículas dos alunos em recuperação vinculadas a turma selecionada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<MatriculaTurma> findMatriculasTurmaRecuperacao(int idTurmaEntrada) throws HibernateException, DAOException{
		String projecao = "id, discente.id, discente.discente.id, discente.discente.matricula, discente.discente.pessoa.nome, chnf, notaParcial, notaFinal";
		
		String hql = "SELECT "+projecao+" FROM MatriculaTurma " +
				" WHERE turmaEntrada.id = "+idTurmaEntrada+
				" and (situacaoMatriculaTurma = "+ SituacaoMatriculaTurma.EM_RECUPERACAO +
				" or situacaoMatriculaTurma = " + SituacaoMatriculaTurma.REPROVADO_FREQUENCIA +")"+ 
				" ORDER BY discente.discente.pessoa.nome";		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<MatriculaTurma> listaMatriculasTurma = (List<MatriculaTurma>)HibernateUtils.parseTo(q.list(), projecao, MatriculaTurma.class);
		
		List<MatriculaComponente> listaMatriculasComponentes = findMatriculasComponentes(idTurmaEntrada);		
		
		for (MatriculaTurma mt : listaMatriculasTurma) {
			for (MatriculaComponente mc : listaMatriculasComponentes) {												
				if (mt.getDiscente().getDiscente().getId() == mc.getDiscente().getId()) {
					mt.getMatriculasComponentes().put(mc.getTurma().getId(), mc);
				}				
			}
		} 		
		return listaMatriculasTurma;
	}
	
	/**
	 * Retorna os dados da Tutoria
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<TutoriaIMD> findDadosTutoria(int idTurmaEntrada) throws HibernateException, DAOException{
		String projecao = "id, turmaEntrada.id, turmaEntrada.especializacao.descricao, turmaEntrada.anoReferencia, " +
				"turmaEntrada.periodoReferencia, turmaEntrada.dadosTurmaIMD.cronograma.modulo.id, turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao, turmaEntrada.dadosTurmaIMD.cronograma.modulo.cargaHoraria, turmaEntrada.dadosTurmaIMD.id, tutor.pessoa.nome";		
		String hql = "select "+ projecao + " from TutoriaIMD where dataFimTutoria is null and turmaEntrada.id = "+ idTurmaEntrada+
				" ORDER BY tutor.pessoa.nome";
		
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<TutoriaIMD> tutorias = (List<TutoriaIMD>) HibernateUtils.parseTo(q.list(), projecao, TutoriaIMD.class);
		if (tutorias.isEmpty()) {
			tutorias = Collections.emptyList(); 
		}
		return tutorias;
	}
	
	
	/**
	 * Retorna as entidades Matriculas Componentes vinculadas a turma de entrada informada.
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculasComponentes (int idTurmaEntrada) throws HibernateException, DAOException{	
		String projecao = "m.id, m.turma.id, m.situacaoMatricula.id, dt.discente.id, m.recuperacao";
		
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
			matricula.setId((Integer) reg[i++]);
			
			matricula.setTurma(new Turma());						
			if (reg[i]!=null) {
				matricula.getTurma().setId((Integer) reg[i++]);
			}
			
			matricula.setSituacaoMatricula(new SituacaoMatricula());			
			if (reg[i]!=null) {
				matricula.getSituacaoMatricula().setId((Integer) reg[i++]);
			}
			
			matricula.setDiscente(new Discente());
			if (reg[i]!=null) {
				matricula.getDiscente().setId((Integer)reg[i++]);
			}
			
			if (reg[i]!=null) {
				matricula.setRecuperacao((Double)reg[i++]);
			}
			
						
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
		String projecao = "t.id, t.disciplina, t.situacaoTurma.id";
		
		String hql = "SELECT "+ projecao +"  FROM Turma t, TurmaEntradaTecnico te" +
				" where t.especializacao.id = te.especializacao.id" +
				" and te.id = "+idTurmaEntrada+
				" and (t.situacaoTurma.id = "+SituacaoTurma.ABERTA+
				" or t.situacaoTurma.id = "+SituacaoTurma.A_DEFINIR_DOCENTE+")" +
				" ORDER BY t.id";
						
		Query q =getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		Collection<Object[]> res = q.list();
		
		List<Turma> listaTurmas = new ArrayList<Turma>();
		 for (Object[] reg : res) {
			 int i=0;
			 Turma t = new Turma();
			 t.setId((Integer) reg[i++]);
			 
			 t.setDisciplina((ComponenteCurricular)reg[i++]);			 			 
			 t.setSituacaoTurma(new SituacaoTurma());
			 t.getSituacaoTurma().setId((Integer)reg[i++]);
			 
			 listaTurmas.add(t);
		}						
		return listaTurmas;
	}
	
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
				"(SELECT n.nota FROM NotaUnidade n WHERE n.unidade = 3 and n.matricula.id = m.id )," + 
		
				//NOTA RECUPERACAO
				"m.recuperacao,"+
				
				//carga horária da disciplina
				"m.turma.disciplina.detalhes.chTotal";
				
		//CONSULTA GERAL
		String hql = "SELECT "+projecao+
				" FROM DiscenteTecnico d, MatriculaComponente m" +
				" WHERE  d.discente.id = m.discente.id " +
				" and d.turmaEntradaTecnico.id = "+idTurmaEntrada +
				" and d.discente.id ="+idDiscente +
				" and d.discente.status = "+ StatusDiscente.ATIVO+
				" and m.situacaoMatricula.id = 2"+ 
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
			Double notaRec = (Double)reg[col++];
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
			n.setProvaRecuperacao(notaRec);
			
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
}
