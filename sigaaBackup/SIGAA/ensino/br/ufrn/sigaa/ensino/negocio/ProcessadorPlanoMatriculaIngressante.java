/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/11/2012
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.Turma;

/** Valida e persiste um plano de matr�cula de turmas em lote.
 * @author �dipo Elder F. de Melo
 *
 */
public class ProcessadorPlanoMatriculaIngressante extends AbstractProcessador {

	/** Persiste um plano de matr�cula de turmas em lote.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		PlanoMatriculaIngressantes obj = movimento.getObjMovimentado();
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class, movimento);
		try {
			if (mov.getCodMovimento().getId() == SigaaListaComando.REMOVER_PLANO_MATRICULA_INGRESSANTE.getId()) {
				// antes de remover, zera a capacidade para que sejam atualizadas as reservas de vagas nas turmas.
				obj.setCapacidade(0);
				dao.atualizaReservaVagasIngressantes(obj);
				dao.remove(obj);
			} else {
				validate(mov);
				// atualiza a reserva na turma para ingressantes
				dao.atualizaReservaVagasIngressantes(obj);
				// calcula o pr�ximo c�digo
				if (obj.getId() == 0) {
					int nextCodigo = 1;
					Collection<Integer> codigos = dao.findAllCodigosByAnoPeriodoMatriz(obj.getAno(), obj.getPeriodo(), obj.getMatrizCurricular().getId());
					while (codigos != null && codigos.contains(nextCodigo))
						nextCodigo++;
					obj.setDescricao(String.format("%02d", nextCodigo));
				}
				dao.createOrUpdate(obj);
			}
		} finally {
			dao.close();
		}
		return obj;
	}

	/** Valida um plano de matr�cula de turmas em lote.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		PlanoMatriculaIngressantes obj = movimento.getObjMovimentado();
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class, movimento);
		try {
			Collection<Turma> turmas = dao.verificaEstouroCapacidadeTurma(obj);
			if (!isEmpty(turmas)) {
				StringBuilder descTurmas = new StringBuilder();
				for (Turma turma : turmas)
					descTurmas.append(turma.getDescricaoCodigo()).append(", ");
				descTurmas.delete(descTurmas.lastIndexOf(","), descTurmas.length());
				throw new NegocioException("A capacidade informada para o Plano de Matr�cula ultrapassa, quando somadas as reservas, a capacidade total da(s) turma(s): " + descTurmas); 
			}
		} finally {
			dao.close();
		}
	}

}
