/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 20/12/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MudancaCurricularColetivaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMudancaCurricular;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * MBean responsável pela mudança coletiva uma matriz curricular para discentes de
 * graduação
 * 
 * @author Rafael Gomes
 *
 */
@Component("mudancaColetivaMatrizCurricular") @Scope("session")
public class MudancaColetivaMatrizCurricularMBean extends MudancaCurricularMBean{

	
	//Definições das Views
	/** JSP de escolha da matriz e currículo de origem da mudança coletiva. */
	public static final String JSP_MATRIZ_ORIGEM = "/graduacao/mudanca_coletiva_matriz/matriz_origem.jsp";
	/** JSP de escolha da matriz e currículo de destino da mudança coletiva. */
	public static final String JSP_MATRIZ_DESTINO = "/graduacao/mudanca_coletiva_matriz/matriz_destino.jsp";
	/** JSP de escolha de alunos que terão a matriz e currículo modificados. */
	public static final String JSP_ALUNOS = "/graduacao/mudanca_coletiva_matriz/alunos.jsp";
	/** Comprovante da mudança coletiva de matriz curricular. */
	public static final String JSP_COMPROVANTE = "/graduacao/mudanca_coletiva_matriz/comprovante.jsp";
	
	// Coleções utilizadas para a mudança coletiva
	/** Conjunto de discentes da matriz de origem selecionada. */
	private Collection<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
	/** Conjunto de discentes selecionados para a mudança de matriz. */
	private Collection<DiscenteGraduacao> discentesSelecionados = new ArrayList<DiscenteGraduacao>();
	
	/** Matriz Curricular atual do discente. */
	private MatrizCurricular matrizOrigem;
	
	/** 
	 * Default Constructor 
	 * */
	public MudancaColetivaMatrizCurricularMBean() {	}

	/**
	 * Prepara os dados iniciais para a operação de mudança coletiva de Matriz Curricular. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/administracao.jsp</li>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 */
	public String iniciarMudancaColetivaMatriz() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP);
		prepareMovimento(SigaaListaComando.MUDANCA_COLETIVA_MATRIZ_CURRICULAR);
		setOperacaoAtiva(SigaaListaComando.MUDANCA_COLETIVA_MATRIZ_CURRICULAR.getId());
		setMatrizAtual(new MatrizCurricular());
		getMatrizAtual().setCurso(new Curso());
		setCurriculoAtual(new Curriculo());
		setAnoIngresso(null);
		setPeriodoIngresso(null);
		return forward(JSP_MATRIZ_ORIGEM);
	}
	
	/**
	 * Buscar todas os discentes ativos de acordo com os filtros selecionados de tela da matriz de origem.
	 * <br />
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/matriz_origem.jsp</li>
	 * </ul>
	 *
	 * @param evt
	 */
	public String buscarDiscentes() throws DAOException, LimiteResultadosException {
		MudancaCurricularColetivaDao dao = getDAO(MudancaCurricularColetivaDao.class);
		matrizOrigem = null;
		int idMatriz = getMatrizAtual().getId();
		int idCurriculo = getCurriculoAtual().getId();
		
		if ( idMatriz == 0 )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matriz Curricular");
		if ( getMatrizAtual() != null && getMatrizAtual().getCurso().getId() == 0 )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (hasErrors()) return null;
		
		setMatrizAtual(dao.findByPrimaryKey(getMatrizAtual().getId(), MatrizCurricular.class));
		
		if (idCurriculo != 0 ) 
			setCurriculoAtual(dao.findByPrimaryKey(getCurriculoAtual().getId(), Curriculo.class));
		
		getObj().setCurso(getMatrizAtual().getCurso());
		getObj().setMatrizCurricular(getMatrizAtual());
		getObj().getMatrizCurricular().setEnfase(getMatrizAtual().getEnfase() != null ? getMatrizAtual().getEnfase() : new Enfase());
		getObj().getMatrizCurricular().setHabilitacao(getMatrizAtual().getHabilitacao() != null ? getMatrizAtual().getHabilitacao() : null);
		getObj().setCurriculo(getCurriculoAtual());
		
		discentes = dao.findDiscentesByMatriz(idMatriz, idCurriculo, getAnoIngresso(), null);
		
		if (discentes.isEmpty()) {
			addMensagemErro("Não foram encontradas discente para a matriz curricular selecionada.");
		}
		if (hasErrors()) return null;
		
		return forward(JSP_ALUNOS);
	}

	/** Método responsável por selecionar os discente para a mudança de matriz curricular. 
	 *  Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/alunos.jsp</li>
	 * </ul>
	 *
	 * @throws DAOException */
	public String efetuarMudancaMatriz() throws DAOException{
		setMatrizNova(new MatrizCurricular());
		getMatrizNova().setCurso(getMatrizAtual().getCurso());
		setCurriculoNovo(new Curriculo());
		setObservacaoDiscente(new ObservacaoDiscente());
		setTipoMudanca(0);
		discentesSelecionados = new ArrayList<DiscenteGraduacao>();
		for (DiscenteGraduacao dg : discentes) {
			if ( dg.isSelecionado() ) discentesSelecionados.add(dg);
		}
		
		if (discentesSelecionados.isEmpty()) 
			addMensagemErro("Não foram selecionados discentes para a mudança de matriz curricular.");
	
		if (hasErrors()) return null;
		
		carregarMatrizesDestino(getMatrizNova().getCurso().getId());
		return forward(JSP_MATRIZ_DESTINO);
	}
	
	/**
	 * Finaliza a operação de mudança chamando o processador.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/matriz_destino.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String cadastrar() throws ArqException {
		boolean warning = false;
		
		if (getCurriculoNovo().getId() == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Estrutura Curricular");
		} else if (getMatrizNova().getId() == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Matriz Curricular");
		} else if(getMatrizNova().getCurso().getId() == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Novo Curso");
		}
		
		if(isCadastrarObservacaoDiscente() && (getObservacaoDiscente() == null || ValidatorUtil.isEmpty(getObservacaoDiscente().getObservacao()))) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observações");
		}
		
		if (getTipoMudanca() != null && getTipoMudanca() == 0){
			addMensagemErro("Não é possível realizar a mudança de matriz curricular, " +
				"pois não há diferença entre os dados de origem com os de destino.");
		}

		if(hasErrors() || warning) return null;
		
		if (ValidatorUtil.isEmpty(getCurriculoNovo())) {
			setCurriculoNovo( getDAO(EstruturaCurricularDao.class).findMaisRecenteByMatriz(getMatrizNova().getId()) );
			if (getCurriculoNovo() == null) {
				addMensagemErro("Não há curriculo cadastrado para a matriz curricular selecionada");
				return null;
			}
		}	
		setSimulacao(false);
		Collection<MudancaCurricular> mudancasCurriculares = new ArrayList<MudancaCurricular>();
		for (DiscenteGraduacao discente : discentesSelecionados) {
			MudancaCurricular mudanca = new MudancaCurricular();
			mudanca.setTipoMudanca(getTipoMudanca());
			mudanca.setDiscente(discente);
			mudanca.setSimulacao(isSimulacao());
			
			if (mudanca.isMudancaCurriculo()) {
				mudanca.setCurriculoAntigo(discente.getCurriculo());
				mudanca.setCurriculoNovo(getCurriculoNovo());
			} else {
				mudanca.setMatrizAntiga(getMatrizAtual());
				mudanca.setMatrizNova(getMatrizNova());
				mudanca.setCurriculoAntigo(discente.getCurriculo());
				mudanca.setCurriculoNovo(getCurriculoNovo());
			}
			mudancasCurriculares.add(mudanca);
		}
		
		for (MudancaCurricular mudancaCurricular : mudancasCurriculares) {
			prepareMovimento(SigaaListaComando.MUDANCA_CURRICULAR);
			
			MovimentoMudancaCurricular mov = new MovimentoMudancaCurricular();
			mov.setCodMovimento(SigaaListaComando.MUDANCA_CURRICULAR);
			mov.setObjMovimentado(mudancaCurricular);
			if(isCadastrarObservacaoDiscente())
				mov.setObjAuxiliar(
						getObservacaoDiscente());
			mov.setRequest(getCurrentRequest());
			mov.setResponse(getCurrentResponse());
			mov.setUsuarioLogado(getUsuarioLogado());
		
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, getDescricaoTipoMudanca());
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
			}
		}
	
		afterCadastrar();
		return cancelar();
		
	}
	
	/**
	 * Carrega as matrizes curriculares a partir de um curso selecionado.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/matriz_destino.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarMatrizesDestino(Integer idCurso) throws DAOException {
		if (idCurso > 0){
			MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
			Collection<MatrizCurricular> matrizesCurso;
			getCurso().setId(idCurso);
			setCurso( dao.findByPrimaryKey(idCurso, Curso.class, "id") );
			matrizesCurso = getMatrizesCurso(idCurso);
			setMatrizes( toSelectItems(matrizesCurso, "id", "descricao") );
			setCurriculosDestino(new ArrayList<SelectItem>());
			setCurriculoNovo(new Curriculo());
			setTipoMudanca(0);
			determinaTipoMudanca();
		}	
	}
	
	/**
	 * Carrega as matrizes curriculares a partir de um curso selecionado.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/matriz_destino.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarMatrizesDestino(ValueChangeEvent evt) throws DAOException {
		if (matrizOrigem == null)
			matrizOrigem = getMatrizAtual();
		obj.setCurso(matrizOrigem.getCurso());
		carregarMatrizesDestino((Integer) evt.getNewValue());
	}
	
	@Override
	public void carregarCurriculosDestino(ValueChangeEvent evt)	throws DAOException {
		int idMatriz = ((Integer) evt.getNewValue() != null ? (Integer) evt.getNewValue() : 0);
		setTipoMudanca(0);
		if (idMatriz != 0) {
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			Collection<Curriculo> col = new ArrayList<Curriculo>(0);
			try {
				setMatrizNova((MatrizCurricular) dao.findByPrimaryKeyLock(idMatriz, MatrizCurricular.class));
				col = dao.findByMatriz(getMatrizNova().getId());
				setCurriculosDestino( toSelectItems(col, "id", "descricao") );
				if ( ValidatorUtil.isEmpty(getCurriculoNovo()) )
					setCurriculoNovo(new Curriculo());
				
			} finally {
				dao.close();
			}
		} else {
			setCurriculosDestino(new ArrayList<SelectItem>());
			setCurriculoNovo(new Curriculo());
		}
		determinaTipoMudanca();
	}
	
	/**
	 * Método responsável pelo controle do atributo de cadastro de observação 
	 * para os discentes em processo de mudança de matriz curricular.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/matriz_destino.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarObservacaoDiscente(ValueChangeEvent evt) throws DAOException {
		if (((Boolean) evt.getNewValue()) != null && ((Boolean) evt.getNewValue())) {
			setCadastrarObservacaoDiscente((Boolean) evt.getNewValue());
		}
	}
	
	/** Método responsável por direcionar o usuário para a tela de seleção da matriz de origem. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/alunos.jsp</li>
	 * </ul>
	 * */
	public String voltarMatrizOrigem(){
		return forward(JSP_MATRIZ_ORIGEM);
	}
	
	/** Método responsável por direcionar o usuário para a tela de seleção de alunos. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mudanca_coletiva_matriz/matriz_destino.jsp</li>
	 * </ul>
	 * */
	public String voltarAlunos(){
		return forward(JSP_ALUNOS);
	}
	
	public Collection<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}
	public void setDiscentes(Collection<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}
	public Collection<DiscenteGraduacao> getDiscentesSelecionados() {
		return discentesSelecionados;
	}
	public void setDiscentesSelecionados(
			Collection<DiscenteGraduacao> discentesSelecionados) {
		this.discentesSelecionados = discentesSelecionados;
	}

	public MatrizCurricular getMatrizOrigem() {
		return matrizOrigem;
	}

	public void setMatrizOrigem(MatrizCurricular matrizOrigem) {
		this.matrizOrigem = matrizOrigem;
	}
}