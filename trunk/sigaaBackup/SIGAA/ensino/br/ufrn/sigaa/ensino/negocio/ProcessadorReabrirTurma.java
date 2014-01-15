/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/12/2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador responsável por efetuar a reabertura de uma turma.
 * 
 * @author leonardo
 *
 */
public class ProcessadorReabrirTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		
		try {
			validate(mov);
			
			MovimentoCadastro movc = (MovimentoCadastro) mov;
			Turma turma = (Turma) movc.getObjMovimentado();			
			turma = dao.findByPrimaryKey(turma.getId(), Turma.class);
			
			turma.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.ABERTA));
			TurmaHelper.criarAlteracaoStatusTurma(turma, movc);
			dao.update(turma);
			
			List<MatriculaComponente> matriculas = dao.findMatriculasConsolidadas(turma);
			if (!isEmpty(matriculas)) {
				for (MatriculaComponente mc : matriculas) {				
					MatriculaComponenteHelper.alterarSituacaoMatricula(mc, SituacaoMatricula.MATRICULADO, mov, dao);
				}
			}
		} finally{
			dao.close();
		}
		
		return mov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		Turma turma = (Turma) movc.getObjMovimentado();
		
		if (!OperacaoTurmaValidator.isPermiteReabrirTurma(turma)) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
		}
		
		ListaMensagens erros = new ListaMensagens();
		
		if (turma.getId() == 0)
			erros.addErro("Turma inexistente.");
		
		turma = getDAO(MatriculaComponenteDao.class, mov).findByPrimaryKey(turma.getId(), Turma.class);
		
		if (turma.getSituacaoTurma() != null && turma.getSituacaoTurma().getId() != SituacaoTurma.CONSOLIDADA)
			erros.addErro("A turma deve estar consolidada para ser reaberta.");
		
		if (turma.getDisciplina().getNivel() == NivelEnsino.LATO)
			if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO))
				erros.addErro("Essa operação é restrita a gestores do Lato Sensu.");
		else if (turma.getDisciplina().getNivel() == NivelEnsino.STRICTO)
			if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.PPG))
				erros.addErro("Essa operação é restrita a gestores do Stricto Sensu.");
		else if (turma.getDisciplina().getNivel() == NivelEnsino.GRADUACAO)
			if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
				erros.addErro("Essa operação é restrita a gestores da Graduação.");
		
		checkValidation(erros.getMensagens());
	}

}
