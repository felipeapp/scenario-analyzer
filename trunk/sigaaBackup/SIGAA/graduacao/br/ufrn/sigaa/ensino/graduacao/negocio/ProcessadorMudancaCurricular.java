/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * created on: 06/09/07
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Historico;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorCalculaHistorico;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Processador respons�vel por persistir a mudan�a do relacionamento entre um
 * discente de gradua��o e sua matriz curricular e ainda registrar essa mudan�a
 * atrav�s da entidade MudancaMatrizCurricular.
 *
 * @author Andr�
 *
 */
public class ProcessadorMudancaCurricular extends ProcessadorCadastro {

	/** Executa a mudan�a curricular do discente.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoMudancaCurricular movimento = (MovimentoMudancaCurricular) mov;
		MudancaCurricular mudanca = movimento.getObjMovimentado();
		mudanca.setData(new Date());
		mudanca.setEntrada(movimento.getUsuarioLogado().getRegistroEntrada());
		GenericDAO dao = getGenericDAO(movimento);
		Historico historico = null;
		try {
			 // Se for simula��o
			if (mudanca.isSimulacao()) {
				// realiza a mudan�a
				realizaMudanca(movimento, mudanca);
				// calcula o hist�rico simulado
				DiscenteGraduacao discente = dao.refresh(mudanca.getDiscente());
				MovimentoCalculoHistorico movHistorico = new MovimentoCalculoHistorico();
				movHistorico.setUsuario(mov.getUsuarioLogado());
				movHistorico.setUsuarioLogado(mov.getUsuarioLogado());
				movHistorico.setCodMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
				movHistorico.setRecalculaCurriculo(true);
				movHistorico.setDiscente(discente);
				ProcessadorCalculaHistorico processador = new ProcessadorCalculaHistorico();
				historico = (Historico) processador.execute(movHistorico);
				// desfaz a mudan�a
				desfazMudanca(movimento, mudanca);
				// calcula o hist�rico anterior
				discente = dao.refresh(mudanca.getDiscente());
				movHistorico.setDiscente(discente);
				processador = new ProcessadorCalculaHistorico();
				processador.execute(movHistorico);
			} else {
				dao.create(mudanca);
				realizaMudanca(movimento, mudanca);
				
				if(movimento.getObjAuxiliar() != null) {
					ObservacaoDiscente obs = new ObservacaoDiscente();
					obs.setSistema(mov.getSistema());
					obs.setUsuarioLogado(mov.getUsuarioLogado());
					obs.setDiscente(mudanca.getDiscente());
					obs.setObservacao(((ObservacaoDiscente) movimento.getObjAuxiliar()).getObservacao());
					obs.setCodMovimento(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE);
					
					new ProcessadorObservacaoDiscente().execute(obs);
				}
			}
		} finally {
			dao.close();
		}
		return historico;
	}

	/** Realiza a mudan�a de curr�culo e calcula os totais do discente..
	 * @param movimento
	 * @param mudanca
	 * @throws NegocioException
	 */
	private void realizaMudanca(Movimento movimento, MudancaCurricular mudanca) throws NegocioException {
		EstruturaCurricularDao dao = null;
		MatriculaComponenteDao mdao = null;
		try {
			dao = getDAO(EstruturaCurricularDao.class, movimento);
			mdao = getDAO(MatriculaComponenteDao.class, movimento);
			
			Curriculo curriculo = mudanca.getCurriculoNovo();
			if (mudanca.isMudancaMatriz() || mudanca.isMudancaEnfase()) {
				dao.updateField(DiscenteGraduacao.class, mudanca.getDiscente().getId(), "matrizCurricular", mudanca.getMatrizNova());
				curriculo = (ValidatorUtil.isEmpty(curriculo) ? dao.findMaisRecenteByMatriz(mudanca.getMatrizNova().getId()) : curriculo);
			} else if (mudanca.isMudancaCurso()) {
				dao.updateField(DiscenteGraduacao.class, mudanca.getDiscente().getId(), "matrizCurricular", mudanca.getMatrizNova());
				dao.updateField(Discente.class, mudanca.getDiscente().getId(), "curso", mudanca.getMatrizNova().getCurso());
				curriculo = (ValidatorUtil.isEmpty(curriculo) ? dao.findMaisRecenteByMatriz(mudanca.getMatrizNova().getId()) : curriculo);
			}
			mdao.zerarTiposIntegralizacao(mudanca.getDiscente());
			dao.updateField(Discente.class, mudanca.getDiscente().getId(), "curriculo", curriculo);
			int prazoMaximo = DiscenteHelper.somaSemestres(mudanca.getDiscente().getAnoIngresso(),
					mudanca.getDiscente().getPeriodoIngresso(),
					curriculo.getSemestreConclusaoMaximo() - 1);
			dao.updateField(Discente.class, mudanca.getDiscente().getId(), "prazoConclusao", prazoMaximo);
			DiscenteGraduacao dg = dao.findByPrimaryKey(mudanca.getDiscente().getId(), DiscenteGraduacao.class);
			// C�lculos do discente
			DiscenteCalculosHelper.atualizarTodosCalculosDiscente(dg, movimento);
		} catch(Exception e) {
			throw new NegocioException(e.getMessage());
		} finally {
			if (dao != null) dao.close();
			if (mdao != null) mdao.close();
		}
	}
	
	/** Desfaz a mudan�a de curr�culo e recalcula os totais do discente.
	 * @param movimento
	 * @param mudanca
	 * @throws NegocioException
	 */
	private void desfazMudanca(Movimento movimento, MudancaCurricular mudanca) throws NegocioException {
		EstruturaCurricularDao dao = null;
		MatriculaComponenteDao mdao = null;
		try {
			dao = getDAO(EstruturaCurricularDao.class, movimento);
			mdao = getDAO(MatriculaComponenteDao.class, movimento);
			
			Curriculo curriculo = mudanca.getCurriculoAntigo();
			if (mudanca.isMudancaMatriz() || mudanca.isMudancaEnfase()) {
				dao.updateField(DiscenteGraduacao.class, mudanca.getDiscente().getId(), "matrizCurricular", mudanca.getMatrizAntiga());
				curriculo = (ValidatorUtil.isEmpty(curriculo) ? dao.findMaisRecenteByMatriz(mudanca.getMatrizAntiga().getId()) : curriculo);
			} else if (mudanca.isMudancaCurso()) {
				dao.updateField(DiscenteGraduacao.class, mudanca.getDiscente().getId(), "matrizCurricular", mudanca.getMatrizAntiga());
				dao.updateField(Discente.class, mudanca.getDiscente().getId(), "curso", mudanca.getMatrizAntiga().getCurso());
				curriculo = (ValidatorUtil.isEmpty(curriculo) ? dao.findMaisRecenteByMatriz(mudanca.getMatrizAntiga().getId()) : curriculo);
			}
			mdao.zerarTiposIntegralizacao(mudanca.getDiscente());
			dao.updateField(Discente.class, mudanca.getDiscente().getId(), "curriculo", curriculo);
			int prazoMaximo = DiscenteHelper.somaSemestres(mudanca.getDiscente().getAnoIngresso(),
					mudanca.getDiscente().getPeriodoIngresso(),
					curriculo.getSemestreConclusaoMaximo() - 1);
			dao.updateField(Discente.class, mudanca.getDiscente().getId(), "prazoConclusao", prazoMaximo);
			DiscenteGraduacao dg = getGenericDAO(movimento).findByPrimaryKey(mudanca.getDiscente().getId(), DiscenteGraduacao.class);
			// C�lculos do discente
			DiscenteCalculosHelper.atualizarTodosCalculosDiscente(dg, movimento);
		} catch(Exception e) {
			throw new NegocioException(e);
		} finally {
			if (dao != null) dao.close();
			if (mdao != null) mdao.close();
		}
	}

	/** Valida a mudan�a curricular do discente.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[] {SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE}, mov);
		MudancaCurricular mudanca = (MudancaCurricular) ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens lista = new ListaMensagens();
		
		// Valida��o de mudan�a de matriz curricular
		if (mudanca.isMudancaMatriz()) {
			DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class, mov);

			// Inicializando matrizes escolhidas
			mudanca.setMatrizNova(dao.findByPrimaryKey(mudanca.getMatrizNova().getId(), MatrizCurricular.class));
			mudanca.setMatrizAntiga(dao.findByPrimaryKey(mudanca.getMatrizAntiga().getId(), MatrizCurricular.class));

			if (mudanca.isMudandoTurno()) {
				try {
					// Testa se o aluno j� mudou de turno mais de 1 vez e est� tentando mudar de turno de novo
					Collection<MudancaCurricular> mudancas = dao.findMudancasMatriz(mudanca.getDiscente());
					if (mudancas != null && mudancas.size() > 0) {
						int mudouDeTurno = 0;
						for (MudancaCurricular m : mudancas) {
							if (m.isMudandoTurno())
								mudouDeTurno++;
						}
						if (mudouDeTurno >= Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.QTD_MAXIMA_MUDANCA_TURNO))) {
							lista.addErro("O discente "+ mudanca.getDiscente().getMatriculaNome() +" n�o pode realizar essa mudan�a de matriz curricular.<br>" +
									"N�o � permitido mudar de turno mais de uma vez");
							throw new NegocioException(lista);
						}
					}


					//  Art. 266. A permuta de turno � concedida uma �nica vez e somente poder� ocorrer caso os
					//	interessados tenham integralizado pelo menos 15% (quinze por cento) da carga hor�ria m�nima da
					//	estrutura curricular a que estejam vinculados.
					if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
						int chPendenteDiscente = mudanca.getDiscente().getChTotalPendente();
						int chTotalCurriculo = mudanca.getDiscente().getCurriculo().getChTotalMinima();
						float integralizado = 100 - ((chPendenteDiscente * 100) / chTotalCurriculo);
						if (integralizado  < 15) {
							lista.addErro("O discente n�o pode realizar essa mudan�a de matriz curricular.<br>" +
									"S� � permitido mudan�a de turno � discentes que tenham integralizado pelo menos 15% da carga hor�ria " +
									"do seu atual curr�culo.<br>" +
									"O discente possui "+integralizado +"% da carga hor�ria do curr�culo integralizado");
							throw new NegocioException(lista);
						}
					}
			
				} finally {
					dao.close();
				}
			} else if (mudanca.isMudancaCurso()) {
				checkRole(SigaaPapeis.ADMINISTRADOR_DAE, mov);
			}
		}
	}

}
