/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 06/09/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Historico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoMudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMudancaCurricularColetiva;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * MBean respons�vel pela mudan�a de uma matriz curricular de um discente de
 * gradua��o
 * 
 * @author Andr�
 */
@Component("mudancaCurricular") @Scope("session")
public class MudancaCurricularMBean extends SigaaAbstractController<DiscenteGraduacao> implements
		OperadorDiscente {

	// Constantes das views
	/** View da mudan�a individual. */
	public static final String MUDANCA = "/graduacao/mudanca_curricular/mudanca_matriz.jsp";
	/** View da mudan�a coletiva. */
	public static final String MUDANCA_COLETIVA = "/graduacao/mudanca_curricular/mudanca_coletiva.jsp";

	/** Matriz Curricular atual do discente. */
	private MatrizCurricular matrizAtual;

	/** Matriz Curricular de destino do discente. */
	private MatrizCurricular matrizNova;

	/** Curr�culo atual do discente. */
	private Curriculo curriculoAtual;

	/** Curr�culo de destino do discente. */
	private Curriculo curriculoNovo;

	/** Tipo da mudan�a curricular. 
	 * @see TipoMudancaCurricular*/
	private Integer tipoMudanca;
	
	/** Curso de destino do discente. */
	private Curso curso;
	
	/** Ano/per�odo de ingresso do discente. */
	private Integer anoIngresso, periodoIngresso;
	
	/**
	 * Indica que a mudan�a curricular � apenas uma simula��o.
	 * Todos os c�lculos s�o persistidos, mas a MudancaCurricular n�o.
	 * Cuidado quando for usar.
	 */
	private boolean simulacao = false;
	
	/** Cole��o de matrizes curriculares de destino para o discente. */
	private List<SelectItem> matrizes = new ArrayList<SelectItem>();
	
	/** Cole��o de curr�culos de origem do discente. */
	private List<SelectItem> curriculosOrigem = new ArrayList<SelectItem>();
	
	/** Cole��o de curr�culos de destino do discente. */
	private List<SelectItem> curriculosDestino = new ArrayList<SelectItem>();
	
	/** Cole��o de cursos do discente. */
	private Collection<Curso> cursos;
	/** Discente resultante da simula��o da mudan�a curricular. */ 
	private DiscenteGraduacao discenteSimulacao;
	
	/** Indica se o usu�rio optou por cadastrar a {@link ObservacaoDiscente} na mudan�a de curso/matriz curricular. */
	private boolean cadastrarObservacaoDiscente;
	/** Observa��o feita para o hist�rico do discente na mudan�a de curso/matriz curricular. */
	private ObservacaoDiscente observacaoDiscente;
	
	/** Indica se a opera��o selecionada pelo usu�rio foi a de mudan�a de curso/matriz curricular. */
	private boolean mudancaCursoMatriz;
	
	/** Indica se ser� necess�rio realizar a sele��o de uma nova matriz curricular dentre as listadas. */
	private boolean selecionarNovaMatriz;
	
	/** Construtor padr�o. */
	public MudancaCurricularMBean() {
		obj = new DiscenteGraduacao();
		matrizAtual = new MatrizCurricular();
		matrizNova = new MatrizCurricular();
		curriculoAtual = new Curriculo();
		curriculoNovo = new Curriculo();
		discenteSimulacao = null;
	}

	/**
	 * Seleciona o discente ap�s a busca e encaminha para a opera��o de mudan�a
	 * curricular.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/busca_discente.jsp</li
	 * </ul>
	 */
	public String selecionaDiscente()  {
		
		try {
			curso.setId(obj.getCurso().getId());
			if (obj.getCurriculo() == null) {
				addMensagemErro("Esse discente n�o possui uma estrutura curricular associada");
				return null;
			}
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);

			curriculoAtual = obj.getCurriculo();
			if (obj.getMatrizCurricular() == null && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
				addMensagemErro("Esse discente n�o possui uma matriz curricular associada");
				return null;
			}
			matrizAtual = obj.getMatrizCurricular();

			Collection<MatrizCurricular> matrizes;
			cursos = dao.findAllAtivos(Curso.class, "nome");
			matrizes = getMatrizesCurso(matrizAtual.getCurso().getId());

			tipoMudanca = 0;
			selecionarNovaMatriz = true;
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
			return null;
		}

		return forward(MUDANCA);
	}

	@Override
	protected void beforeAtualizar() throws ArqException {
		super.beforeAtualizar();
	}
	
	/**
	 * M�todo que realiza todas as valida��es antes de acessar
	 * formul�rio de mudan�a de �nfase.
	 * @throws DAOException
	 */
	private void beforeMudancaEnfase() throws DAOException {
		boolean matrizesPossuiEnfase = false;
		Collection<MatrizCurricular> matrizesDisponiveis = new ArrayList<MatrizCurricular>();
		Collection<MatrizCurricular> matrizesCurso = getMatrizesCurso(obj.getCurso().getId());
		for (MatrizCurricular mc: matrizesCurso ){
			Enfase enfase = mc.getEnfase();
			Enfase enfaseDiscente = obj.getCurriculo().getMatriz().getEnfase();
			if( !isEmpty(enfase) && !isEmpty(enfaseDiscente) && enfaseDiscente.getId() != enfase.getId() ){
				matrizesDisponiveis.add(mc); 
				matrizesPossuiEnfase = true;
			}
		}
		if( !matrizesPossuiEnfase ){
			addMensagem(MensagensGraduacao.MATRIZ_CURRICULAR_NAO_POSSUI_ENFASE);
		}else if ( isEmpty(matrizesDisponiveis) && !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) ) {
			addMensagem(MensagensGraduacao.MATRIZ_CURRICULAR_POSSUI_APENAS_UMA_ENFASE);
		}
	}

	/**
	 * Retorna todas as matrizes do curso ao discente.
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> getMatrizesCurso(Integer idCurso) throws DAOException {
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		return dao.findAtivasByCurso(idCurso);
	}

	/**
	 * Popula o discente selecionado para ser trabalhado durante o caso de uso.
	 * Chamado na classe:
	 * br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean.<br>M�todo n�o invocado por JSP�s.
	 */
	public void setDiscente(DiscenteAdapter discente) {
		try {
			GenericDAO dao = getGenericDAO();
			obj = dao.findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("N�o foi poss�vel carregar o discente escolhido");
		}
	}

	/**
	 * Carrega os poss�veis curr�culos de destino para os quais pode-se alterar
	 * o aluno a partir do atual.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPossiveisCurriculos() throws DAOException {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Collection<Curriculo> curriculos = new ArrayList<Curriculo>(0);

		if ( ValidatorUtil.isNotEmpty(matrizNova) ){
			curriculos = dao.findByMatriz(matrizNova.getId(), true);
			curriculoNovo = dao.findMaisRecenteByMatriz(matrizNova.getId());
		} else {
			if( obj != null && obj.getId() != 0)
				if(curriculoAtual != null && curriculoAtual.getMatriz() != null) 
					curriculos = dao.findByMatriz(curriculoAtual.getMatriz().getId());
			if( curriculos.size() > 0 )
				curriculos.remove( curriculoAtual );
		}	
		return toSelectItems(curriculos, "id", "descricao");
	}
	
	/**
	 * Carrega os poss�veis curr�culos de destino para os quais pode-se alterar
	 * de acordo com a nova matriz curricular selecionada.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPossiveisCurriculosByNovaMatriz() throws DAOException {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Collection<Curriculo> curriculos = new ArrayList<Curriculo>(0);

		if( obj != null && obj.getId() != 0)
			if(matrizNova != null) 
				curriculos = dao.findByMatriz(matrizNova.getId());
		if( curriculos.size() > 0 )
			curriculos.remove( curriculoAtual );

		return toSelectItems(curriculos, "id", "descricao");
	}

	/**
	 * Carrega os poss�veis curr�culos de destino para os quais pode-se alterar
	 * o aluno a partir do atual.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPossiveisEnfases() throws DAOException {
		Collection<MatrizCurricular> matrizes = new ArrayList<MatrizCurricular>();

		for (MatrizCurricular matriz : getMatrizesCurso(curso.getId())) {
			if( matriz.getId() != matrizAtual.getId() &&
					matriz.getEnfase() != null ) {
				matrizes.add(matriz);
			}
		}
		return toSelectItems(matrizes, "id", "descricao");
	}

	/**
	 * Carrega as poss�veis matrizes curriculares de destino para as quais
	 * pode-se alterar o aluno a partir da sua matriz atual.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPossiveisMatrizes() throws DAOException {
		Collection<MatrizCurricular> matrizes = new ArrayList<MatrizCurricular>();

		if( obj != null && obj.getId() != 0){
			matrizes = getMatrizesCurso(curso.getId());
		}

		List<SelectItem> matrizesPossiveis = toSelectItems(matrizes, "id", "descricao");
		
		return matrizesPossiveis;
	}

	/**
	 * Finaliza a opera��o de mudan�a chamando o processador.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String simularMudanca() throws ArqException {
		setSimulacao(true);
		return cadastrar();
	}
	
	/**
	 * Finaliza a opera��o de mudan�a chamando o processador.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String registrarMudanca() throws ArqException {
		setSimulacao(false);
		return cadastrar();
	}
	
	/**
	 * Finaliza a opera��o de mudan�a chamando o processador.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException {
		boolean warning = false;
		
		if ( isMudancaCurriculo() && curriculoNovo.getId() == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Estrutura Curricular");
		} else if (isMudancaEnfase() && matrizNova.getId() == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova �nfase");
		} else if (!isMudancaCurriculo() && !isMudancaEnfase()){
			if(curso.getId() == 0) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Novo Curso");
			}
			if(matrizNova.getId() == 0) {
				addMensagemWarning("N�o � poss�vel realizar a mudan�a sem uma matriz curricular.");
				warning = true;
			}
		}
		
		if(isCadastrarObservacaoDiscente() && (observacaoDiscente == null || isEmpty(observacaoDiscente.getObservacao()))) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observa��es");
		}
		
		if(hasErrors() || warning)
			return null;
		
		if (!isMudancaCurriculo() && tipoMudanca != null && tipoMudanca == 0){
			addMensagemErro("Selecione um tipo de mudan�a de matriz curricular");
			return null;
		}

		MudancaCurricular mudanca = new MudancaCurricular();
		mudanca.setTipoMudanca(tipoMudanca);
		mudanca.setDiscente(obj);
		mudanca.setSimulacao(simulacao);
		
		if (mudanca.isMudancaCurriculo()) {
			mudanca.setCurriculoAntigo(curriculoAtual);
			mudanca.setCurriculoNovo(curriculoNovo);
		} else {
			mudanca.setMatrizAntiga(matrizAtual);
			mudanca.setMatrizNova(matrizNova);
			if ( ValidatorUtil.isEmpty(curriculoNovo) ){
				curriculoNovo = getDAO(EstruturaCurricularDao.class).findMaisRecenteByMatriz(matrizNova.getId());
				if (curriculoNovo == null) {
					addMensagemErro("N�o h� curriculo cadastrado para a matriz curricular selecionada");
					curriculoNovo = new Curriculo();
					return null;
				}
			}	
			mudanca.setCurriculoAntigo(obj.getDiscente().getCurriculo());
			mudanca.setCurriculoNovo(curriculoNovo);
		}
		prepareMovimento(SigaaListaComando.MUDANCA_CURRICULAR);
		
		MovimentoMudancaCurricular mov = new MovimentoMudancaCurricular();
		mov.setCodMovimento(SigaaListaComando.MUDANCA_CURRICULAR);
		mov.setObjMovimentado(mudanca);
		if(cadastrarObservacaoDiscente)
			mov.setObjAuxiliar(observacaoDiscente);
		mov.setRequest(getCurrentRequest());
		mov.setResponse(getCurrentResponse());
		mov.setUsuarioLogado(getUsuarioLogado());
		Historico historico;
		try {
			historico = (Historico) executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		}
		// se for uma simula��o, retorna para o formul�rio e exibe o hist�rico simulado do discente 
		if (isSimulacao()) {
			HistoricoMBean historicoMBean = new HistoricoMBean();
			List<Historico> historicos = new ArrayList<Historico>();
			historicos.add(historico);
			try {
				return historicoMBean.geraArquivo(historicos, historico.getNivel());
			} catch (Exception e) {
				tratamentoErroPadrao(e);
				return null;
			}
		} else {
			afterCadastrar();
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, getDescricaoTipoMudanca());
			return cancelar();
		}
	}

	/**
	 * Determina o tipo de mudan�a baseado nos dados novos e dados atuais.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void determinaTipoMudanca(ValueChangeEvent evt) throws DAOException {
		Integer novoValor = (Integer) evt.getNewValue();
		if (novoValor != null && novoValor != 0) {
			this.matrizNova = getGenericDAO().findByPrimaryKey(novoValor, MatrizCurricular.class);
			determinaTipoMudanca();
		} else {
			tipoMudanca = 0;
		}
	}
	
	/**
	 * Determina o tipo de mudan�a baseado nos dados novos e dados atuais utilizando ActionEvent e Ajax.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void determinaTipoMudanca(ActionEvent evt) throws DAOException {
		
		if (matrizNova.getId() != 0) {
			this.matrizNova = getGenericDAO().findByPrimaryKey(matrizNova.getId(), MatrizCurricular.class);
			determinaTipoMudanca();
		} else {
			tipoMudanca = 0;
		}
	}

	/** Determina o tipo de mudan�a baseado nos dados novos e dados atuais. 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�O CHAMADO POR JSP, apenas outra classe.</li>
	 * </ul>
	 * */
	public void determinaTipoMudanca() {
		if (obj.getCurso().getId() != curso.getId()){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_CURSO;
		
		} else if ( curso.getModalidadeEducacao() != null && 
				!obj.getCurso().getModalidadeEducacao().equals(curso.getModalidadeEducacao())){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_MODALIDADE;
		
		} else if (obj.getMatrizCurricular().getHabilitacao() == null && matrizNova.getHabilitacao() != null
				|| obj.getMatrizCurricular().getHabilitacao() != null && matrizNova.getHabilitacao() == null
				|| obj.getMatrizCurricular().getHabilitacao() != null && matrizNova.getHabilitacao() != null 
					&& obj.getMatrizCurricular().getHabilitacao().getId() != matrizNova.getHabilitacao().getId()) {
			tipoMudanca = TipoMudancaCurricular.MUDANCA_HABILITACAO;
		
		} else if (obj.getMatrizCurricular().getEnfase() == null && matrizNova.getEnfase() != null
				 || obj.getMatrizCurricular().getEnfase() != null && matrizNova.getEnfase() == null
				 || obj.getMatrizCurricular().getEnfase() != null && matrizNova.getEnfase() != null 
				&& obj.getMatrizCurricular().getEnfase().getId() != matrizNova.getEnfase().getId() ) {
			tipoMudanca = TipoMudancaCurricular.MUDANCA_ENFASE;
		
		} else if (obj.getMatrizCurricular().getGrauAcademico() == null && matrizNova.getGrauAcademico() != null
				 || obj.getMatrizCurricular().getGrauAcademico() != null && matrizNova.getGrauAcademico() == null
				 || obj.getMatrizCurricular().getGrauAcademico() != null && matrizNova.getGrauAcademico() != null 
			    && obj.getMatrizCurricular().getGrauAcademico().getId() != matrizNova.getGrauAcademico().getId() ){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_GRAU_ACADEMICO;
		
		} else if (matrizNova.getTurno()!= null && obj.getMatrizCurricular().getTurno().getId() != matrizNova.getTurno().getId()){
			tipoMudanca = TipoMudancaCurricular.MUDANCA_TURNO;
		
		} else if (obj.getCurriculo().getId() != this.curriculoNovo.getId()) {
			tipoMudanca = TipoMudancaCurricular.MUDANCA_CURRICULO;
		}
	}
	
	/**
	 * Determina o tipo de mudan�a baseado nos dados novos e dados atuais.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void determinaTipoMudancaPorCurriculo(ValueChangeEvent evt) throws DAOException {
		Integer novoValor = (Integer) evt.getNewValue();
		if (novoValor != null && novoValor != 0) {
			this.curriculoNovo = getGenericDAO().findByPrimaryKey(novoValor, Curriculo.class);
			this.matrizNova = curriculoNovo.getMatriz();
			determinaTipoMudanca();
		} else {
			tipoMudanca = 0;
		}
	}
	
	/** Retorna a representa��o textual do tipo de mudan�a curricular.
	 * @return
	 */
	public String getDescricaoTipoMudanca() {
		return tipoMudanca != 0 ? TipoMudancaCurricular.descricaoTipoMudanca.get(tipoMudanca) : "N�o Localizada";
	}

	/**
	 * Inicia a opera��o de mudan�a de curr�culo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String iniciarMudancaCurriculo() throws Exception {
		tipoMudanca = TipoMudancaCurricular.MUDANCA_CURRICULO;
		return iniciar();
	}

	/**
	 * Inicia a opera��o de mudan�a de curr�culo.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String iniciarMudancaEnfase() throws Exception {
		tipoMudanca = TipoMudancaCurricular.MUDANCA_ENFASE;
		return iniciar();
	}

	/**
	 * Inicia a opera��o de mudan�a de matriz curricular.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String iniciarMudancaMatriz() throws Exception {
		tipoMudanca = null;
		mudancaCursoMatriz = true;
		return iniciar();
	}
	
	/**
	 * Efetua opera��es comuns da inicializa��o do caso de uso e redireciona
	 * para o MBean que efetua a busca de discentes.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String iniciar() throws Exception {
		curso = new Curso();
		matrizNova = new MatrizCurricular();
		observacaoDiscente = new ObservacaoDiscente();
		curriculoNovo = new Curriculo();
	
		cadastrarObservacaoDiscente = false;
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE);
		prepareMovimento(SigaaListaComando.MUDANCA_CURRICULAR);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MUDANCA_CURRICULAR);
		buscaDiscenteMBean.setEad(false);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Popula combo com os tipos de mudan�as.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getMudancasMatriz() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>(0);
		combo.add(new SelectItem(TipoMudancaCurricular.MUDANCA_MODALIDADE, "MUDAR MODALIDADE"));
		combo.add(new SelectItem(TipoMudancaCurricular.MUDANCA_HABILITACAO, "MUDAR HABILITA��O"));
		combo.add(new SelectItem(TipoMudancaCurricular.MUDANCA_TURNO, "MUDAR TURNO"));
		return combo;
	}

	// M�todos da Mudan�a Curricular Coletiva de Todos os Alunos Ativos num Curso

	/**
	 * Popula os dados iniciais para a opera��o de mudan�a de curr�culo
	 * coletiva.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/administracao.jsp</li>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 */
	public String iniciarMudancaColetiva() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP);
		prepareMovimento(SigaaListaComando.MUDANCA_CURRICULAR_COLETIVA);
		curso = new Curso();
		anoIngresso = null;
		periodoIngresso = null;
		return forward(MUDANCA_COLETIVA);
	}

	/**
	 * Carrega as matrizes curriculares a partir de um curso selecionado.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca_coletiva.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarMatrizes(ValueChangeEvent evt) throws DAOException {
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		Collection<MatrizCurricular> matrizesCurso;
		if(evt == null){
			matrizesCurso = getMatrizesCurso(curso.getId());
			matrizes = toSelectItems(matrizesCurso, "id", "descricao");
		}else{
			if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				curso = dao.findByPrimaryKey((Integer) evt.getNewValue(), Curso.class);
				matrizesCurso = getMatrizesCurso(curso.getId());
				matrizes = toSelectItems(matrizesCurso, "id", "descricao");
			}
		}
		selecionarNovaMatriz = false;
		tipoMudanca = 0;
		determinaTipoMudanca();
		curriculosOrigem = new ArrayList<SelectItem>();
	}

	/**
	 * Se existirem matrizes carregadas na listagem passada, 
	 * garante que a primeira listada ser� selecionada por default.
	 */
	private void selecionarMatrizCarregada(Collection<MatrizCurricular> matrizes) {
		if(!isEmpty(matrizes)) {
			for (MatrizCurricular m : matrizes) {
				matrizNova = m;
				break;
			}
		}
		else {
			matrizNova.setId(0);
		}
		
		selecionarNovaMatriz = false;
		
		determinaTipoMudanca();
	}

	/**
	 * Carrega os curr�culos a partir de uma matriz selecionada.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca_coletiva.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarCurriculosOrigem(ValueChangeEvent evt) throws DAOException {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		if(evt == null){
			curriculosOrigem = toSelectItems(dao.findByMatriz(matrizAtual.getId()), "id", "descricao");
		}else{
			if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				MatrizCurricular matriz = new MatrizCurricular((Integer) evt.getNewValue());
				curriculosOrigem = toSelectItems(dao.findByMatriz(matriz.getId()), "id", "descricao");
			}
		}
	}

	/**
	 * Carrega os poss�veis curr�culos de destino a partir de um curr�culo de
	 * origem selecionado.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca_coletiva.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarCurriculosDestino(ValueChangeEvent evt) throws DAOException {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Collection<Curriculo> col = new ArrayList<Curriculo>(0);
		if(evt != null){
			col = dao.findByMatriz(matrizAtual.getId());
			if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				Curriculo curriculo = new Curriculo((Integer) evt.getNewValue());				
				if(col.size() > 0)
					col.remove( curriculo );
			}
		}
		
		curriculosDestino = toSelectItems(col, "id", "descricao");
	}
	
	/**
	 * Carrega os dados do �ltimo curr�culo ativo para a �nfase curricular selecionada.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarDadosCurriculo(ValueChangeEvent evt) throws DAOException {
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			carregarDadosCurriculo((Integer) evt.getNewValue());
		} else {
			carregarDadosCurriculo(obj.getMatrizCurricular().getId());
		}
	}
	
	/**
	 * Carrega os dados do �ltimo curr�culo ativo para matriz curricular passada por par�metro.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca.jsp</li>
	 * </ul>
	 * 
	 * @param idMatriz
	 * @throws DAOException
	 */
	public void carregarDadosCurriculo(int idMatriz) throws DAOException {
		if (idMatriz != 0) {
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			Collection<Curriculo> col = new ArrayList<Curriculo>(0);
			
			try {
				matrizNova = dao.findByPrimaryKeyLock(idMatriz, MatrizCurricular.class);
				curriculoNovo = dao.findMaisRecenteByMatriz(matrizNova.getId());
				col = dao.findByMatriz(matrizNova.getId());
				if ( ValidatorUtil.isEmpty(curriculoNovo) )
					curriculoNovo = new Curriculo();
				determinaTipoMudanca();
				if (obj.getCurriculo() != null && col.size() > 0){
					col.remove( obj.getCurriculo() );
				}
				curriculosDestino = toSelectItems(col, "id", "descricao");
			} finally {
				dao.close();
			}
		} 
	}

	/**
	 * Finaliza a opera��o de mudan�a coletiva chamando o processador
	 * {@link br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorMudancaCurricularColetiva}.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/mudanca_curricular/mudanca_coletiva.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String registrarMudancaColetiva() throws ArqException {
		erros = new ListaMensagens();

		ValidatorUtil.validateRequired(curso, "Curso", erros);
		ValidatorUtil.validateRequired(matrizAtual, "Matriz Curricular", erros);
		ValidatorUtil.validateRequired(curriculoAtual, "Curr�culo de origem", erros);
		ValidatorUtil.validateRequired(curriculoNovo, "Curr�culo de destino", erros);

		ValidatorUtil.validaIntGt(anoIngresso, "Ano de Ingresso", erros);
		if(anoIngresso != null && anoIngresso > 0)
			ValidatorUtil.validateRequired(periodoIngresso, "Per�odo de Ingresso", erros);
		
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}

		MovimentoMudancaCurricularColetiva mov = new MovimentoMudancaCurricularColetiva();
		mov.setCodMovimento(SigaaListaComando.MUDANCA_CURRICULAR_COLETIVA);
		mov.setCurriculoOrigem(curriculoAtual);
		mov.setCurriculoDestino(curriculoNovo);
		mov.setAnoIngresso(anoIngresso);
		mov.setPeriodoIngresso(periodoIngresso);
		try {
			mov = (MovimentoMudancaCurricularColetiva) executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		afterCadastrar();
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Mudan�a Coletiva de Curr�culo (" +mov.getQtdAlunos()+" alunos)");
		return cancelar();
	}
	
	/* Getters and Setters */

	/** Retorna a Matriz Curricular atual do discente. 
	 * @return Matriz Curricular atual do discente. 
	 */
	public MatrizCurricular getMatrizAtual() {
		return matrizAtual;
	}

	/** Seta a Matriz Curricular atual do discente. 
	 * @param matrizAtual Matriz Curricular atual do discente. 
	 */
	public void setMatrizAtual(MatrizCurricular matrizAtual) {
		this.matrizAtual = matrizAtual;
	}

	/** Retorna a Matriz Curricular de destino do discente. 
	 * @return Matriz Curricular de destino do discente. 
	 */
	public MatrizCurricular getMatrizNova() {
		return matrizNova;
	}

	/** Seta a Matriz Curricular de destino do discente. 
	 * @param matrizNova Matriz Curricular de destino do discente. 
	 */
	public void setMatrizNova(MatrizCurricular matrizNova) {
		this.matrizNova = matrizNova;
	}

	/** Indica se a mudan�a � do tipo MUDANCA_CURRICULO
	 * @return
	 */
	public boolean isMudancaCurriculo() {
		return tipoMudanca != null && tipoMudanca == TipoMudancaCurricular.MUDANCA_CURRICULO;
	}
	
	/** Indica se a mudan�a � do tipo MUDANCA_ENFASE
	 * @return
	 */
	public boolean isMudancaEnfase() {
		return tipoMudanca != null && tipoMudanca == TipoMudancaCurricular.MUDANCA_ENFASE;
	}

	/** Retorna o Curr�culo atual do discente. 
	 * @return Curr�culo atual do discente. 
	 */
	public Curriculo getCurriculoAtual() {
		return curriculoAtual;
	}

	/** Seta o Curr�culo atual do discente. 
	 * @param curriculoAtual Curr�culo atual do discente. 
	 */
	public void setCurriculoAtual(Curriculo curriculoAtual) {
		this.curriculoAtual = curriculoAtual;
	}

	/** Retorna o Curr�culo de destino do discente. 
	 * @return Curr�culo de destino do discente. 
	 */
	public Curriculo getCurriculoNovo() {
		return curriculoNovo;
	}

	/** Seta o Curr�culo de destino do discente. 
	 * @param curriculoNovo Curr�culo de destino do discente. 
	 */
	public void setCurriculoNovo(Curriculo curriculoNovo) {
		this.curriculoNovo = curriculoNovo;
	}

	/** Retorna o Tipo da mudan�a curricular. 
	 * @return Tipo da mudan�a curricular. 
	 */
	public Integer getTipoMudanca() {
		return tipoMudanca;
	}

	/** Seta o Tipo da mudan�a curricular. 
	 * @param tipoMudancaTipo da mudan�a curricular
	 */
	public void setTipoMudanca(Integer tipoMudanca) {
		this.tipoMudanca = tipoMudanca;
	}

	/** Retorna o curso de destino do discente. 
	 * @return Curso de destino do discente. 
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Retorna o curso de destino do discente.
	 * @param curso Curso de destino do discente. 
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna o ano de ingresso do discente. 
	 * @return Ano de ingresso do discente. 
	 */
	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	/** Seta o ano de ingresso do discente.
	 * @param anoIngresso Ano de ingresso do discente. 
	 */
	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	/** Retorna o per�odo de ingresso do discente. 
	 * @return Per�odo de ingresso do discente. 
	 */
	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	/** Seta o per�odo de ingresso do discente.
	 * @param periodoIngresso Per�odo de ingresso do discente. 
	 */
	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	/** Retorna uma cole��o de matrizes curriculares de destino para o discente. 
	 * @return Cole��o de matrizes curriculares de destino para o discente. 
	 */
	public List<SelectItem> getMatrizes() {
		return matrizes;
	}

	/** Seta a cole��o de matrizes curriculares de destino para o discente.
	 * @param matrizes Cole��o de matrizes curriculares de destino para o discente.
	 */
	public void setMatrizes(List<SelectItem> matrizes) {
		this.matrizes = matrizes;
	}

	/** Retorna uma cole��o de curr�culos de origem do discente. 
	 * @return Cole��o de curr�culos de origem do discente. 
	 */
	public List<SelectItem> getCurriculosOrigem() {
		return curriculosOrigem;
	}

	/** Seta uma cole��o de curr�culos de origem do discente.
	 * @param curriculosOrigem Cole��o de curr�culos de origem do discente.
	 */
	public void setCurriculosOrigem(List<SelectItem> curriculosOrigem) {
		this.curriculosOrigem = curriculosOrigem;
	}

	/** Retorna a cole��o de curr�culos de destino do discente. 
	 * @return Cole��o de curr�culos de destino do discente. 
	 */
	public List<SelectItem> getCurriculosDestino() {
		return curriculosDestino;
	}

	/** Seta a cole��o de curr�culos de destino do discente.
	 * @param curriculosDestino Cole��o de curr�culos de destino do discente. 
	 */
	public void setCurriculosDestino(List<SelectItem> curriculosDestino) {
		this.curriculosDestino = curriculosDestino;
	}

	/**
	 * Indica se a mudan�a curricular � apenas uma simula��o. Todos os c�lculos
	 * s�o persistidos, mas a MudancaCurricular n�o. Cuidado quando for usar.
	 * 
	 * @return
	 */
	public boolean isSimulacao() {
		return simulacao;
	}

	/**
	 * Seta se a mudan�a curricular � apenas uma simula��o. Todos os c�lculos
	 * s�o persistidos, mas a MudancaCurricular n�o. Cuidado quando for usar.
	 * 
	 * @param simulacao
	 */
	public void setSimulacao(boolean simulacao) {
		this.simulacao = simulacao;
	}

	/** Retorna a Cole��o de cursos do discente.  
	 * @return Cole��o de cursos do discente.
	 */
	public Collection<Curso> getCursos() {
		return cursos;
	}

	/** Seta a cole��o de cursos do discente.
	 * @param cursos Cole��o de cursos do discente.
	 */
	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	/** Retorna o discente resultante da simula��o da mudan�a curricular.
	 * @return
	 */
	public DiscenteGraduacao getDiscenteSimulacao() {
		return discenteSimulacao;
	}

	/** Seta o discente resultante da simula��o da mudan�a curricular. 
	 * @param discenteSimulacao
	 */
	public void setDiscenteSimulacao(DiscenteGraduacao discenteSimulacao) {
		this.discenteSimulacao = discenteSimulacao;
	}

	public boolean isCadastrarObservacaoDiscente() {
		return cadastrarObservacaoDiscente;
	}

	public void setCadastrarObservacaoDiscente(boolean cadastrarObservacaoDiscente) {
		this.cadastrarObservacaoDiscente = cadastrarObservacaoDiscente;
	}

	public ObservacaoDiscente getObservacaoDiscente() {
		return observacaoDiscente;
	}

	public void setObservacaoDiscente(ObservacaoDiscente observacaoDiscente) {
		this.observacaoDiscente = observacaoDiscente;
	}

	public boolean isMudancaCursoMatriz() {
		return mudancaCursoMatriz;
	}

	public void setMudancaCursoMatriz(boolean mudancaCursoMatriz) {
		this.mudancaCursoMatriz = mudancaCursoMatriz;
	}

}