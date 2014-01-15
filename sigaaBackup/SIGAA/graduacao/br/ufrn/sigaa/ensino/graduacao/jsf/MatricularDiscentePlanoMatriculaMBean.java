package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.jsf.PlanoMatriculaIngressantesMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Matricula um discente com status CADASTRADO em turmas utilizando um plano de
 * matr�culas ingressantes.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("matricularDiscentePlanoMatriculaMBean")
@Scope("session")
public class MatricularDiscentePlanoMatriculaMBean extends SigaaAbstractController<DiscenteGraduacao> implements OperadorDiscente {
	
	/** Plano de Matr�cula o qual o discente ser� matriculado. */
	private PlanoMatriculaIngressantes planoMatriculaIngressante;
	
	/** Cole��o de {@link PlanoMatriculaIngressantes} com as turmas em que o discente poder� ser matriculado. */
	private Collection<SelectItem> planoMatriculaIngressanteCombo;
	
	/** Outros v�nculos de discentes ativos que o candidato pode ter. */
	private Collection<DiscenteGraduacao> outrosVinculos;
	
	/** Indica se o discente possui matr�cula em componentes curriculares no ano-per�odo de ingresso.*/
	private boolean matriculado;
	
	/** Construtor padr�o. */
	public MatricularDiscentePlanoMatriculaMBean() {
	}
	
	/** Inicializa os atributos necess�rios para o cadastramento.
	 * @throws ArqException
	 */
	private void init() throws ArqException {
		obj = new DiscenteGraduacao();
		resultadosBusca = null;
		planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		matriculado = false;
	}
	
	/** Inicia o cadastramento do discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkChangeRole();
		init();
		prepareMovimento(SigaaListaComando.MATRICULAR_DISCENTE_PLANO_MATRICULA);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_DISCENTE_PLANO_MATRICULA.getId());
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MATRICULAR_DISCENTE_PLANO_MATRICULA);
		return buscaDiscenteMBean.popular();
	}
	
	/** 
	 * Seta o discente escolhido da lista de resultados da busca.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	public void setDiscente(DiscenteAdapter discente) {
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return;
		}
		obj = (DiscenteGraduacao) discente;
	}
	
	/** Seleciona uma convoca��o para cadastrar o discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/seleciona_convocado.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionaDiscente() throws ArqException {
		checkChangeRole();
		if (isEmpty(obj)) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		if (obj.getStatus() != StatusDiscente.CADASTRADO) {
			addMensagemErro("Esta opera��o � utilizada apenas para discentes com status CADASTRADO.");
			return null;
		}
		carregaPlanosMatricula();
		if (isEmpty(planoMatriculaIngressanteCombo)) {
			addMensagemErro("N�o h� planos de matr�cula cadastrados para a matriz curricular "
					+ obj.getMatrizCurricular().getDescricao()
					+ " no ano-per�odo "
					+ obj.getAnoIngresso()
					+ "."
					+ obj.getPeriodoIngresso() + ".");
			return null;
		}
		outrosVinculos = buscaOutrosVinculosAtivos(obj);
		return formPlanoMatricula();
	}
	
	
	/** Carrega os planos de matr�cula para o discente selecionado.
	 * @throws DAOException
	 */
	private void carregaPlanosMatricula() throws DAOException {
		planoMatriculaIngressanteCombo = new LinkedList<SelectItem>();
		planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		DiscenteGraduacao discente = obj;
		Collection<PlanoMatriculaIngressantes> planos = dao.findByMatrizCurricular(discente.getMatrizCurricular().getId(),
				discente.getAnoIngresso(), discente.getPeriodoIngresso());
		if (!isEmpty(planos)) {
			for (PlanoMatriculaIngressantes plano : planos) {
				SelectItem selectItem = new SelectItem(plano.getId(), 
						plano.getMatrizCurricular().getDescricao() + " - Plano " + plano.getDescricao()
						+ " (" + plano.getAno() + "." + plano.getPeriodo() +" / "
						+ plano.getVagas() + " vagas)");
				selectItem.setDisabled(!plano.hasVagas());
				planoMatriculaIngressanteCombo.add(selectItem);
				// seleciona automaticamente o primeiro plano que possui vagas. {
				if (plano.hasVagas() && planoMatriculaIngressante.getId() == 0) {
					planoMatriculaIngressante = plano;
					plano.getTurmas().iterator();
				}
			}
		}
	}
	
	/** Busca os dados do plao de matr�cula selecioando pelo usu�rio<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/status_matricula.jsp</li>
	 * </ul>
	 * @param evt
	 * @return
	 * @throws DAOException 
	 */
	public String carregaDadosPlanoMatricula(ValueChangeEvent evt) throws DAOException {
		planoMatriculaIngressante.setId((Integer) evt.getNewValue());
		if (planoMatriculaIngressante.getId() == 0)
			planoMatriculaIngressante = new PlanoMatriculaIngressantes();
		else
			planoMatriculaIngressante = getGenericDAO().refresh(planoMatriculaIngressante);
		return null;
	}
	
	/** Submete o status do discente e o plano de matr�cula.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/status_matricula.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String confirmar() throws DAOException, SegurancaException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.MATRICULAR_DISCENTE_PLANO_MATRICULA.getId()))
			return cancelar();
		validateRequiredId(obj.getStatus(), "Status", erros);
		validateRequired(planoMatriculaIngressante, "PLano de Matr�cula", erros);
		if (hasErrors()) return null;
		GenericDAO dao = getGenericDAO();
		planoMatriculaIngressante = dao.refresh(planoMatriculaIngressante);
		// repassa o plano de matr�cula para o mbean auxiliar na exibi��o do quadro de hor�rio
		PlanoMatriculaIngressantesMBean mBean = getMBean("planoMatriculaIngressantesMBean");
		mBean.init();
		mBean.setObj(planoMatriculaIngressante);
		// verifica se o discente anterior tem v�nculo trancado
		for (DiscenteGraduacao outroVinculo : outrosVinculos)
			if (outroVinculo.isTrancado()) {
				addMensagemErro("O discente " + outroVinculo.getMatriculaNome() + " est� com o v�nculo TRANCADO. Retorne o discente do tracamento para ativo para concluir esta opera��o");
				return null;
			}
		return forward("/ensino/matricula_discente_plano_matricula/confirma.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de busca de convoca��es.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/status_matricula.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formSelecionaConvocado() {
		return forward("/ensino/matricula_discente_plano_matricula/seleciona_convocado.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de sele��o de status e plano de matr�cula.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/confirma.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formPlanoMatricula() {
		if (!checkOperacaoAtiva(SigaaListaComando.MATRICULAR_DISCENTE_PLANO_MATRICULA.getId()))
			return cancelar();
		return forward("/ensino/matricula_discente_plano_matricula/form.jsp");
	}
	
	/** Cadastra o discente e matricula nas turmas do plano de matr�cula escolhido.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/confirma.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.MATRICULAR_DISCENTE_PLANO_MATRICULA.getId()))
			return cancelar();
		MovimentoCadastro mov = new MovimentoCadastro(obj);
		mov.setCodMovimento(SigaaListaComando.MATRICULAR_DISCENTE_PLANO_MATRICULA);
		mov.setObjAuxiliar(planoMatriculaIngressante);
		DiscenteGraduacao discente = (DiscenteGraduacao) execute(mov);
		addMensagemInformation("Discente " + discente.getMatriculaNome() + " matriculado com sucesso!");
		removeOperacaoAtiva();
		getGenericDAO().initialize(obj);
		checkDiscenteMatriculadoComponente(obj);
		return forward("/ensino/matricula_discente_plano_matricula/comprovantes.jsp");
	}

	private void checkDiscenteMatriculadoComponente(DiscenteGraduacao discente)
			throws DAOException {
		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class);
		matriculado = matDao.countMatriculasByDiscente(discente, discente.getAnoIngresso(), discente.getPeriodoIngresso(), SituacaoMatricula.getSituacoesAtivasArray()) > 0;
	}

	/** Imprime o atestado de matr�cula do discente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/matricula_discente_plano_matricula/comprovantes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String atestadoMatricula() throws DAOException, SegurancaException {
		checkChangeRole();
		if (obj == null && obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		if (obj.getStatus() == StatusDiscente.CADASTRADO || obj.getStatus() == StatusDiscente.ATIVO) {
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			getCurrentSession().setAttribute("atestadoLiberado", obj.getId());
			atestado.setDiscente(obj);
			return atestado.selecionaDiscente();
		} else {
			addMensagemWarning("O discente n�o est� cadastrado ou ativo.");
			return null;
		}
	}
	
	/** Busca por outros v�nculos ativos de gradua��o do discente.
	 * @param discente
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	private Collection<DiscenteGraduacao> buscaOutrosVinculosAtivos(DiscenteGraduacao discente) throws LimiteResultadosException, DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		int[] tiposValidos = {Discente.REGULAR};
		int[] statusValidos = { StatusDiscente.ATIVO, StatusDiscente.FORMANDO,
				StatusDiscente.TRANCADO, StatusDiscente.CADASTRADO, StatusDiscente.ATIVO_DEPENDENCIA };
		@SuppressWarnings("unchecked")
		Collection<DiscenteGraduacao> discentes = (Collection<DiscenteGraduacao>) dao.findOtimizado(discente.getPessoa().getCpf_cnpj(), null, null, null, null, null, statusValidos, tiposValidos, 0, NivelEnsino.GRADUACAO, false);
		if (!isEmpty(discentes)) {
			Iterator<DiscenteGraduacao> iterator = discentes.iterator();
			while (iterator.hasNext()) {
				DiscenteGraduacao vinculoAnterior = iterator.next();
				if (vinculoAnterior.getId() == discente.getId())
					iterator.remove();
				else
					dao.initialize(vinculoAnterior.getMatrizCurricular());
			}
		}
		return discentes;
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO);
	}
	
	public Collection<SelectItem> getPlanoMatriculaIngressanteCombo(){
		return planoMatriculaIngressanteCombo;
	}
	
	public PlanoMatriculaIngressantes getPlanoMatriculaIngressante() {
		return planoMatriculaIngressante;
	}

	public void setPlanoMatriculaIngressante(
			PlanoMatriculaIngressantes planoMatriculaIngressante) {
		this.planoMatriculaIngressante = planoMatriculaIngressante;
	}

	public Collection<DiscenteGraduacao> getOutrosVinculos() {
		return outrosVinculos;
	}

	public boolean isMatriculado() {
		return matriculado;
	}

	public void setMatriculado(boolean matriculado) {
		this.matriculado = matriculado;
	}

}
