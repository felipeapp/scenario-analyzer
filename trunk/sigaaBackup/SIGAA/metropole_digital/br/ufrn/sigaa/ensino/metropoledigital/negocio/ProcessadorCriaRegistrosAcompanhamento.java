package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.metropoledigital.dao.AcompanhamentoSemanalDiscenteDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Processador para criação da estrutura dos acompanhamentos de frequencia e notas semanais dos discentes do IMD.
 * 
 * @author Rafael Barros
 * 
 */
public class ProcessadorCriaRegistrosAcompanhamento extends AbstractProcessador {

	/**
	 * Função que executa a listagem dos discentes, períodos de avaliação e os
	 * registros de frequencia da Turma de Entrada selecionada. A função também
	 * verifica se os registros da frequencia da turma já foram criados, caso
	 * não tenham sido criados, a função chama uma nova função que irá efetuar o
	 * procedimento de criar os registros da frequencia da turma
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return Página referente à operação.
	 * @throws ArqException, NegocioException
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCriacaoRegistrosAcompanhamento movFreq = (MovimentoCriacaoRegistrosAcompanhamento) mov;

		Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos = movFreq.getListaGeralAcompanhamentos();

		Collection<DiscenteTecnico> listaDiscentesTurma = movFreq.getListaDiscentesTurma();

		Collection<PeriodoAvaliacao> listaPeriodosTurma = movFreq.getListaPeriodosTurma();

		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {

			TurmaEntradaTecnico turmaEntradaSelecionada = acompDao.findByPrimaryKeyLock(movFreq.getTurmaSelecionada()
					.getId(), TurmaEntradaTecnico.class);

			listaGeralAcompanhamentos = acompDao.findAcompanhamentosByTurmaEntradaProjetado(turmaEntradaSelecionada
					.getId());
			if (!(listaDiscentesTurma.size() * listaPeriodosTurma.size() == listaGeralAcompanhamentos.size())) {

				boolean existeAcomp = false;
				for (DiscenteTecnico discente : listaDiscentesTurma) {

					for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {
						if (acomp.getDiscente().getId() == discente.getId()) {
							existeAcomp = true;
							break;
						}
					}

					if (!existeAcomp) {
						criarRegistrosFrequenciaPorDiscente(discente, listaPeriodosTurma);
					}
					existeAcomp = false;
				}
			}

			acompDao.update(turmaEntradaSelecionada);

		} finally {
			acompDao.close();
		}

		return movFreq;

	}

	/**
	 * Função que cria os registros da frequencia da turma por discente
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * @param discente: discente no qual os acompanhamentos serão criados; listaPeriodosTurma: lista de periodos nos quais os acompanhamentos serão criados.
	 * @throws DAOException
	 * @return
	 **/
	public void criarRegistrosFrequenciaPorDiscente(DiscenteTecnico discente,
			Collection<PeriodoAvaliacao> listaPeriodosTurma) throws DAOException {

		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();

		try {
			for (PeriodoAvaliacao periodo : listaPeriodosTurma) {
				AcompanhamentoSemanalDiscente acomp = new AcompanhamentoSemanalDiscente();
				acomp.setPeriodoAvaliacao(periodo);
				acomp.setDiscente(discente);
				acompDao.createOrUpdate(acomp);
			}
		} finally {
			acompDao.close();
		}
	}

	/**
	 * Método padrão herdado da super-classe
	 * 
	 * @param 
	 * @throws 
	 * @return
	 **/
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub

	}

}
