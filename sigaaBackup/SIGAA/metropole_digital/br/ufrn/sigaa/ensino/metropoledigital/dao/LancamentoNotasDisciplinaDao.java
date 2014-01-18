package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class LancamentoNotasDisciplinaDao extends GenericSigaaDAO{
	/**
	 * Lista as Disciplinas Vinculadas a Turma de Entrada
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Turma> findDisciplinasByEspecializacao(int idEspecializacao) throws HibernateException, DAOException{
		String projecao = "id, codigo, disciplina.detalhes.nome, ano, periodo";
		
		String hql = "SELECT "+ projecao + " FROM Turma " +
				"WHERE especializacao.id = " + idEspecializacao +
				" and (situacaoTurma.id = "+ SituacaoTurma.ABERTA +
				" or situacaoTurma.id = "+SituacaoTurma.A_DEFINIR_DOCENTE+")"; 
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Turma> lista = (List<Turma>) HibernateUtils.parseTo(q.list(), projecao, Turma.class);
		
		return 	lista;
	}
	
	public Turma findTurmaEntradaById(){
		return null;
	}
	
	/**
	 * Lista as notas dos alunos por disciplina
	 * 
	 * @param idTurmaEntrada
	 * @param turma
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<NotaIMD> findNotasAlunos(int idTurmaEntrada, Turma turma) throws HibernateException, DAOException{		
		// retorna a Carga Horária do Componente Curricular
		String hql = "SELECT detalhes.chTotal FROM ComponenteCurricular WHERE id = " + turma.getDisciplina().getId();
		Query q = getSession().createQuery(hql);
		Integer chTotal = (Integer)q.uniqueResult();
		
		//retorna as notas dos alunos matriculados na turma
		List<NotaUnidade> notasUnidades = (List<NotaUnidade>) findNotasByTurma(turma);
					
		// Lista os discentes matriculados na turma
		String projecao =
				//DADOS DO DISCENTE
				"d.discente.matricula, d.discente.pessoa.nome, m.id, " +
						
				//PARTICIPAÇÃO PRESENCIAL
				" (SELECT (sum(a.participacaoPresencial)/count(*)) " +
				" FROM AcompanhamentoSemanalDiscente a, CargaHorariaSemanalDisciplina c " +
				" where a.periodoAvaliacao.id = c.periodoAvaliacao.id " +
				" and c.disciplina.id = "+turma.getDisciplina().getId() +
				" and a.discente.id=d.discente.id" +
				" and c.cargaHoraria != 0), "+

				//PARTICIPAÇÃO VIRTUAL
				" (SELECT (sum(a.participacaoVirtual)/count(*)) " +
				" FROM AcompanhamentoSemanalDiscente a, CargaHorariaSemanalDisciplina c " +
				" where a.periodoAvaliacao.id = c.periodoAvaliacao.id " +
				" and c.disciplina.id = "+turma.getDisciplina().getId() +
				" and a.discente.id=d.discente.id" +
				" and c.cargaHoraria != 0), " +

				//FREQUENCIA
				" (SELECT (sum(a.frequencia * c.cargaHoraria)) " +
				" FROM AcompanhamentoSemanalDiscente a, CargaHorariaSemanalDisciplina c " +
				" where a.periodoAvaliacao.id = c.periodoAvaliacao.id " +
				" and c.disciplina.id = "+turma.getDisciplina().getId() +
				" and a.discente.id=d.discente.id" +
				" and c.cargaHoraria != 0)," +
				
				//ID DO DISCENTE
				" d.discente.id";
			
		//CONSULTA GERAL
		hql = "SELECT "+projecao+
				" FROM DiscenteTecnico d, MatriculaComponente m" +
				" WHERE  d.discente.id = m.discente.id " +
				" and d.turmaEntradaTecnico.id = "+idTurmaEntrada +
				" and m.turma.id = "+ turma.getId()+
				" and m.situacaoMatricula.id = 2"+ 
				" ORDER BY d.discente.pessoa.nome";
		q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		Collection<Object[]> res = q.list();
		
		List<NotaIMD> notas = new ArrayList<NotaIMD>();		
		for (Object[] reg : res) {
			int col = 0;
			NotaIMD n = new NotaIMD(); 
			Long matriculaDiscente = (Long) reg[col++];
			String nome = (String) reg[col++];
			Integer matriculaComponente = (Integer)reg[col++];
			Double participacaoPresencial = (Double)reg[col++];
			Double participacaoVirtual = (Double)reg[col++];
			Double chnfTemp = (Double)reg[col++];
			Integer idDiscente = (Integer)reg[col++];
			
			Double participacaoTotal = null;
			if (participacaoPresencial!=null && participacaoVirtual!=null) {
				participacaoTotal = (participacaoPresencial*3 + participacaoVirtual * 7)/10;				
			}	
			
			Integer chnf = chTotal;
			if (chnfTemp!=null) {
				chnf = chTotal - chnfTemp.intValue();				
			}						
			
			n.setDiscente(new DiscenteTecnico());
			n.getDiscente().setDiscente(new Discente());
			n.getDiscente().getDiscente().setMatricula(matriculaDiscente);	
			
			n.getDiscente().getDiscente().setPessoa(new Pessoa());
			n.getDiscente().getDiscente().getPessoa().setNome(nome);
			n.getDiscente().setId(idDiscente);
			
			n.setMatriculaComponente(new MatriculaComponente());
			n.getMatriculaComponente().setId(matriculaComponente);
			
			n.setParticipacaoPresencial(participacaoPresencial);
			n.setParticipacaoVirtual(participacaoVirtual);
			n.setChnf(chnf);
			
			//ADICIONA AS NOTAS DE AE E PE A ENTIDADE NOTASIMD.
			for (NotaUnidade notaUnidade : notasUnidades) {	
				if (notaUnidade.getMatricula().getId() == matriculaComponente && notaUnidade.getUnidade() == 1) {
					n.setParticipacaoTotal(notaUnidade);
				}
				
				if (notaUnidade.getMatricula().getId() == matriculaComponente && notaUnidade.getUnidade() == 2) {
					n.setAtividadeOnline(notaUnidade);
				}
				
				if (notaUnidade.getMatricula().getId() == matriculaComponente && notaUnidade.getUnidade() == 3) {
					n.setProvaEscrita(notaUnidade);
				}
			}
			
			if (n.getParticipacaoTotal() == null ) {
				n.setParticipacaoTotal(new NotaUnidade());
				if (participacaoTotal!=null) {					
					n.getParticipacaoTotal().setNota(participacaoTotal);
				}															
			}
			n.getParticipacaoTotal().setNota(participacaoTotal);
			
			if (n.getAtividadeOnline()==null) {
				n.setAtividadeOnline(new NotaUnidade());
			}
			
			if (n.getProvaEscrita()==null) {
				n.setProvaEscrita(new NotaUnidade());
			}
			
			notas.add(n);
		}		
		return notas;	
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
}
