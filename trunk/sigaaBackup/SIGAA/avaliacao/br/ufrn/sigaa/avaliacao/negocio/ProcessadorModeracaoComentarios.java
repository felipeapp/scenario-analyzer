/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/05/2010
 *
 */
package br.ufrn.sigaa.avaliacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.ComentarioAvaliacaoModerado;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/** Processador responsável por persistir as observações moderadas da Avaliação Institucional.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorModeracaoComentarios extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		if (mov.getCodMovimento().equals(SigaaListaComando.LIBERAR_CONSULTA_AO_RESULTADO_DA_AVALIACAO)) {
			return liberaSupendeConsulta(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.GRAVAR_OBSERVACOES_DOCENTE_TURMA_MODERADA)
				|| mov.getCodMovimento().equals(SigaaListaComando.GRAVAR_OBSERVACOES_TRANCAMENTO_MODERADA)) {
			GenericDAO dao = getGenericDAO(mov);
			gravaObservacoes(mov, dao);
			dao.close();
		} else if (mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_OBSERVACOES_DOCENTE_TURMA_MODERADA)) {
			GenericDAO dao = getGenericDAO(mov);
			gravaObservacoes(mov, dao);
			@SuppressWarnings("unchecked")
			Iterator<ObservacoesDocenteTurma> iterator = (Iterator<ObservacoesDocenteTurma>)(((MovimentoCadastro) mov).getColObjMovimentado()).iterator();
			DocenteTurma docenteTurma = iterator.next().getDocenteTurma();
			
			ComentarioAvaliacaoModerado comentario = new ComentarioAvaliacaoModerado();
			comentario.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
			comentario.setDocenteTurma(docenteTurma);
			comentario.setDataAtualizacao(new Date());
			comentario.setTurma(null);
			comentario.setObservacaoDiscente((Boolean) ((MovimentoCadastro) mov).getObjAuxiliar());
			dao.createNoFlush(comentario);
			dao.close();
		} else if (mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_OBSERVACOES_TRANCAMENTO_MODERADA)) {
			GenericDAO dao = getGenericDAO(mov);
			gravaObservacoes(mov, dao);
			@SuppressWarnings("unchecked")
			Iterator<ObservacoesTrancamento> iterator = (Iterator<ObservacoesTrancamento>)(((MovimentoCadastro) mov).getColObjMovimentado()).iterator();
			Turma turma = iterator.next().getTurma();
			
			ComentarioAvaliacaoModerado comentario = new ComentarioAvaliacaoModerado();
			comentario.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
			comentario.setTurma(turma);
			comentario.setDataAtualizacao(new Date());
			comentario.setDocenteTurma(null);
			comentario.setObservacaoDiscente(true);
			dao.createNoFlush(comentario);
			dao.close();
		}
		return null;
	}

	/** Libera/suspende a consulta ao resultado da Avaliação Institucional
	 * @param mov
	 * @return
	 * @throws DAOException 
	 */
	private Object liberaSupendeConsulta(Movimento mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		ParametroProcessamentoAvaliacaoInstitucional parametro = ((MovimentoCadastro) mov).getObjMovimentado();
		dao.updateField(ParametroProcessamentoAvaliacaoInstitucional.class, parametro.getId(), "consultaDiscenteLiberada", parametro.isConsultaDiscenteLiberada());
		dao.updateField(ParametroProcessamentoAvaliacaoInstitucional.class, parametro.getId(), "consultaDocenteLiberada", parametro.isConsultaDocenteLiberada());
		dao.close();
		return null;
	}

	/** Persiste as observações moderadas.
	 * @param mov
	 * @param dao
	 * @throws DAOException
	 */
	private void gravaObservacoes(Movimento mov, GenericDAO dao)
			throws DAOException {
		Collection<? extends PersistDB> observacoes = ((MovimentoCadastro) mov).getColObjMovimentado();
		for (PersistDB obs : observacoes) {
			dao.updateNoFlush(obs);
		}
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
