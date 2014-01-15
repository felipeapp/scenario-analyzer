/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.ensino.dao.FecharTurmaDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * Processador responsável por fechar diretamente turmas que estejam abertas mas 
 * com todos as matrículas consolidadas ou não matriculadas.
 * 
 * @author leonardo
 *
 */
public class ProcessadorFecharTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		Turma turma = (Turma) movc.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			turma = dao.findByPrimaryKey(turma.getId(), Turma.class);
			turma.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.CONSOLIDADA));
			dao.update(turma);
		} finally {
			dao.close();
		}
		
		return mov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[] {SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO}, mov);
		
		FecharTurmaDao dao = getDAO(FecharTurmaDao.class, mov);
		SolicitacaoMatriculaDao daoSol = getDAO(SolicitacaoMatriculaDao.class, mov);
		ListaMensagens erros = new ListaMensagens();
		
		try{
			MovimentoCadastro movc = (MovimentoCadastro) mov;
			Turma turma = (Turma) movc.getObjMovimentado();
			
			if(turma.getId() == 0)
				erros.addErro("Turma inexistente.");
			
			if(turma.getSituacaoTurma() != null && turma.getSituacaoTurma().getId() != SituacaoTurma.ABERTA)
				erros.addErro("A turma deve estar aberta para ser fechada.");
			
			if(dao.containsAlunosAtivos(turma.getId()))
				erros.addErro("A turma não deve possuir nenhum aluno matriculado.");
			
			Collection<SolicitacaoMatricula> solicitacoes = daoSol.findByTurma(turma.getId(), true, (Integer[]) SolicitacaoMatricula.STATUS_ATIVOS_GRAD_EAD.toArray());
			
			if(solicitacoes != null && !solicitacoes.isEmpty())
				erros.addErro("A turma não deve possuir nenhum aluno com solicitação de matrícula.");
		} finally {
			dao.close();
			daoSol.close();
		}
		
		checkValidation(erros);
	}

}
