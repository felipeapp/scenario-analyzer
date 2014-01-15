/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '17/10/2006'
 *
 */

package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.jsf.ConfiguracoesAvaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ava.negocio.MovimentoImportacaoNotasPlanilha;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalValidator;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoValidator;
import br.ufrn.sigaa.ensino.negocio.TurmaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;
import br.ufrn.sigaa.ensino.stricto.negocio.ConceitoNotaHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.portal.jsf.PortalDocenteMBean;

/**
 * MBean para tratar o caso de uso de consolidar uma
 * turma.
 *
 * @author David Ricardo
 *
 */
@Component("consolidarTurma")
@Scope("session")
public class ConsolidarTurmaMBean extends ConsolidacaoMBean {

	/** Link para a página da importação da planilha. */
	public static final String PAGINA_FORM_IMPORTAR_PLANILHA = "/ensino/consolidacao/formImportarPlanilha.jsp";
	/** Link para a página do lançamento de notas. */
	public static final String PAGINA_DETALHES_TURMA = "/ensino/consolidacao/detalhesTurma.jsp";
	
	/** Indica se a consolidação é individual. */
	private boolean individual;

	/** Senha usada na autenticação do usuário. */
	private String senha;
	
	/** Objeto que guarda as informações sobre os parâmetros da unidade gestora da turma. */
	private ParametrosGestoraAcademica param;

	/** Objeto que guarda as informações das configurações da turma selecionada. */
	private ConfiguracoesAva config;
	
	// Atributos utilizados na importação.
	/** Matriculas utilizadas na importação. */
	private Map<Long, List<Double>> matriculasNotasImportacao;
	
	/** Nomes dos discentes utilizados antes de confirmar a importação. */
	private Map<Long, String> matriculasNomesImportacao;
	
	/** Nomes das avaliações lidas na importação. */
	private List <String> nomesAvaliacoes;
	
	/** Guarda as matrículas detectadas na importação. */
	private List <Long> matriculasImportacao;
	
	/** Arquivo da planilha de notas importada pelo professor. */
	private UploadedFile arquivoPlanilha;

	/** Indica se a consolidação é parcial */
	private boolean parcial;
	
	/** Variáveis usadas na reconsolidação de turma. */
	/** Coleção que mantém as notas antigas da turma. */
	private Collection<NotaUnidade> notasIniciaisTurma;
	/** Coleção de retificações das matrículas da turma que está sendo reconsolidada. */
	private Collection<RetificacaoMatricula> retificacoesReconsolidacao;

	public ConsolidarTurmaMBean(){
		initObj();
	}

	public List <SelectItem> getTurmasProfessorCombo () {
		return getTurmasSituacaoCombo (SituacaoTurma.ABERTA);
	}
	
	public Collection <Turma> getTurmasProfessor () {
		return getTurmasSituacao (SituacaoTurma.ABERTA);
	}
	
	/**
	 * Retorna as turmas abertas de um curso de pós-graduação
	 * lato-sensu para que o coordenador possa realizar a consolidação
	 * delas.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/selecionaTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTurmasCoordenadorLato() {
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			Collection<Turma> turmas = dao.findByCursoLatoSituacao(getCursoAtualCoordenacao().getId(), SituacaoTurma.ABERTA);
			return toSelectItems(turmas, "id", "descricaoSemDocente");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}

	/**
	 * Pega um id de uma turma em request e a seleciona para a realização
	 * da consolidação.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 *<ul>
	 * <li>sigaa.war/ensino/consolidacao/selecionaTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String turmaEscolhida() throws ArqException {
		turma.setId(getParameterInt("idTurma"));
		return escolherTurma();
	}

	/**
	 * Método usado para entrar na consolidação de turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * 		<li>sigaa.war/ensino/consolidacao/selecionaTurma.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String escolherTurma() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CONSOLIDAR_TURMA);
		prepareMovimento(SigaaListaComando.REMOVER_AVALIACAO);
		
		// verifica se o usuário possui permissão para consolidar a turma
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			turma = dao.refresh(turma);
			
			// Garante que o programa não será lazy.
			if (turma.getDisciplina().getPrograma() != null)
				turma.getDisciplina().setPrograma(dao.refresh(turma.getDisciplina().getPrograma()));
			
			// Garante que a unidade não será lazy.
			if (turma.getDisciplina().getUnidade() != null)
				turma.getDisciplina().setUnidade(dao.refresh(turma.getDisciplina().getUnidade()));
			
			checkTurmaResponsaveisRole(turma, true);
			validarAvaliacaoInstitucional();
			prepararTurma(turma);
	
			if (hasOnlyErrors())
				return null;
			
			if (turma.isMigradoGraduacao()) {
				addMensagemInformation("Caro usuário, não será necessário lançar as notas porque esta é uma turma migrada do Ponto A.");
				return forward("/ensino/consolidacao/confirmar.jsp");
			}
			
			redirectJSF(getCurrentRequest().getContextPath() + "/ensino/consolidacao/detalhesTurma.jsf");
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	private void validarAvaliacaoInstitucional() throws DAOException {
		// verifica se há avaliação institucional para responder
		boolean avaliacaoDocenteAtiva = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		if (!getAcessoMenu().isAdministradorDAE() 
				&& avaliacaoDocenteAtiva 
				// TODO Precisa otimizar este método------v
				&& !AvaliacaoInstitucionalValidator.podeConsolidarTurma(AvaliacaoInstitucionalHelper.getAvaliacao(getUsuarioLogado(), turma), turma) 
				&& !isPortalEscolasEspecializadas()) {
			addMensagemWarning("Antes de efetivar a consolidação de sua turma é necessário primeiro avaliá-la.");
		}
	}

	/**
	 * Carrega ConfiguracoesAva e os parametros da gestora academica
	 * 
	 * JSP: Não invocado por jsp
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void prepararTurma(Turma turma) throws DAOException, SegurancaException, ArqException {
		setOperacaoAtiva(1, "Nenhuma consolidação ativa. Por favor, reinicie o processo.");
		
		if (turma == null || turma.getId() == 0) {
			addMensagem(MensagensTurmaVirtual.NENHUMA_TURMA_SELECIONADA);
			return;
		}
		
		// busca as informações referente a turma na base de dados
		TurmaDao dao = getDAO(TurmaDao.class);
		MatriculaComponenteDao daoMatricula = getDAO(MatriculaComponenteDao.class);
		turma.getDisciplina().getId();
		if (turma.getDisciplina().getPrograma() != null)
			turma.getDisciplina().getPrograma().getId();
		turma.getDescricaoSemDocente();
		if (turma.getDisciplina().getUnidade().getGestoraAcademica() != null)
			turma.getDisciplina().getUnidade().getGestoraAcademica().getId();

		config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
		
		// recupera os parâmetros acadêmicos para saber a média mínima  
		param = getParametrosAcademicos();
		if (param == null) {
			addMensagem(MensagensTurmaVirtual.PARAMEROS_PARA_NIVEL_NAO_CONFIGURADOS, turma.getDisciplina().getNivelDesc());
			return;
		}
		
		try {
			// Busca alunos matriculados
			Collection<MatriculaComponente> matriculas = dao.findMatriculasAConsolidar(turma);
			
			carregarNivelDiscente(turma, matriculas);
			
			if (isEmpty(matriculas)) {
				addMensagemErro("Esta turma não possui matrículas a serem consolidadas.");
				return;
			}
			
			// Setando as matriculas para verificação da quantidade de unidades
			if ( turma.isEad() )
				turma.setMatriculasDisciplina(matriculas);
			
			String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);
			String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();

			if (param.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && (pesosAvaliacoes == null || pesosAvaliacoes.length == 0)) {
				addMensagem(MensagensTurmaVirtual.UNIDADE_SEM_PESOS_AVALIACOES);
				return;
			}
			
			metodoAvaliacao = param.getMetodoAvaliacao();
			if (isLato()) {
				CursoLato cursoLato = dao.findByPrimaryKey(turma.getCurso().getId(), CursoLato.class);
				turma.setCurso(cursoLato);
				metodoAvaliacao = getPropostaCursoLato().getMetodoAvaliacao();
			}
			if ( turma.getDisciplina().getNivel() == NivelEnsino.STRICTO) {
				carregaInfoTurmaPos(turma, matriculas, pesosAvaliacoes, param.getMetodoAvaliacao(), pesoMediaPesoRec);
				metodoAvaliacao = param.getMetodoAvaliacao();
			} else {
				carregaInfoTurma(turma, matriculas, pesosAvaliacoes, metodoAvaliacao, pesoMediaPesoRec);
			}
			
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			
			EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {			    
				matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
				matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));
				matricula.setEstrategia(estrategia);
				
				if (turma.isSubTurma() || turma.isAgrupadora())
					matricula.setCodigoSubturma(turma.getCodigo());
			}
			
			retificacoesReconsolidacao = new ArrayList<RetificacaoMatricula>();
			Boolean reconsolidacaoTurma = TurmaHelper.isReconsolidacaoTurma(turma);			
			if(reconsolidacaoTurma) {				
				Map<Integer,Integer> mapaMatriculaSituacaoAnteriorMaisRecente = daoMatricula.getMapaSituacaoAnteriorMaisRecenteMacricula(turma.getMatriculasDisciplina());
				for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {
					if ( mapaMatriculaSituacaoAnteriorMaisRecente.get(matricula.getId()) == null || matricula.getMediaFinal() == null)
						continue;
					RetificacaoMatricula retificacao = new RetificacaoMatricula();
					retificacao.setMediaFinalAntiga(matricula.getMediaFinal().doubleValue());
					retificacao.setNumeroFaltasAntigo(matricula.getNumeroFaltas());
					retificacao.setConceitoAntigo(null);
					retificacao.setSituacaoAntiga(new SituacaoMatricula (mapaMatriculaSituacaoAnteriorMaisRecente.get(matricula.getId())));
					retificacao.setMatriculaAlterada(matricula);
					retificacoesReconsolidacao.add(retificacao);				
				}
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return;
		}
	}

	/** 
	 * Carrega o nível de ensino do discente.
	 * @param turma
	 * @param matriculas
	 * @throws DAOException
	 */
	private void carregarNivelDiscente(Turma turma, Collection<MatriculaComponente> matriculas) throws DAOException {
		if (isNotEmpty(matriculas)) {
			List<Integer> lista = new ArrayList<Integer>();
			for (MatriculaComponente mc : matriculas) {
				lista.add(mc.getDiscente().getId());
			}
			
			DiscenteDao ddao = getDAO(DiscenteDao.class);
			List<DiscenteAdapter> discentesPorNivel = ddao.findDetalhesByDiscente(lista, turma.getDisciplina().getNivel());
			
			
			for (MatriculaComponente mc : matriculas) {
				for (DiscenteAdapter discenteAdapter : discentesPorNivel) {
					if (discenteAdapter.getDiscente().getId() == mc.getDiscente().getId()) {
						mc.setDiscente(discenteAdapter);
					}
				}

			}
		}
	}
	
	/**
	 * Recarrega as matrículas de uma turma.<br/><br/>
	 * 
	 * Não é utilizado por JSPs.
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	public void recarregarMatriculas() throws ArqException, NegocioException {
		TurmaDao dao = getDAO(TurmaDao.class);
		Collection<MatriculaComponente> matriculas = dao.findMatriculasAConsolidar(turma);		
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();		
		if ( turma.getDisciplina().getNivel() == NivelEnsino.STRICTO) {			
			carregaInfoTurmaPos(turma, matriculas, TurmaUtil.getArrayPesosUnidades(turma), param.getMetodoAvaliacao(),pesoMediaPesoRec);
			metodoAvaliacao = param.getMetodoAvaliacao();			
		} else {
			String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);
			carregaInfoTurma(turma, matriculas, pesosAvaliacoes, metodoAvaliacao,pesoMediaPesoRec);
		}
	}

	/**
	 * Atualiza as matrículas da turma.
	 * Método chamado pelas seguintes JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * 		<li>sigaa.war/ensino/consolidacao/relatorio.jsp</li>
	 * 		<li>sigaa.war/ensino/consolidacao/relatorioComCapa.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getAtualizarTurma() {
		TurmaDao dao = getDAO(TurmaDao.class);

		try {
			// Busca alunos matriculados
			Collection<MatriculaComponente> matriculas = dao.findMatriculasByTurma(turma.getId());
			turma.setMatriculasDisciplina(matriculas);
		} catch(Exception e) {
			notifyError(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Vai para a tela de confirmação da consolidação da turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @return
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		parcial = false;
		MatriculaComponenteDao dao = null;
		try {
			
			if (turma.isAgrupadora() || turma.getTurmaAgrupadora() != null)
				prepararTurma(turma);
						
			if (!checkOperacaoAtiva(1)) cancelar();
			salvarNotas(false);
			
			checkTurmaResponsaveisRole(turma, false);
			
			// Se a turma é agrupadora ou é uma subturma, busca todas as matrículas das subturmas envolvidas, pois estas serão consolidadas também.
			if (turma.isAgrupadora()){
				turma.setMatriculasDisciplina(new ArrayList <MatriculaComponente> ());
				dao = getDAO(MatriculaComponenteDao.class);
				
				// Recupera as subturmas ordenadas pelo código
				List <Turma> subturmas = (List<Turma>) dao.findByExactField(Turma.class, "turmaAgrupadora.id", turma.isAgrupadora() ? turma.getId() : turma.getTurmaAgrupadora().getId(), "ASC", "codigo");
				
				if (!isEmpty(subturmas))
					for (Turma t : subturmas){
						// Recupera as notas dos discentes da subturma e adiciona suas matrículas à lista de matrículas a consolidar
						prepararTurma(t);
						turma.getMatriculasDisciplina().addAll(t.getMatriculasDisciplina());
					}
			}
			
			ConsolidacaoValidator.validar(dao, turma, param, metodoAvaliacao, SigaaListaComando.CONSOLIDAR_TURMA, isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE), parcial, config, getUsuarioLogado());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			prepararTurma(turma);
			redirectJSF(getCurrentRequest().getContextPath() + "/ensino/consolidacao/detalhesTurma.jsf");
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/ensino/consolidacao/confirmar.jsp");
	}

	/**
	 * Volta para a tela de seleção de turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarTurma() {
		if(isPortalGraduacao() || SigaaSubsistemas.SEDIS.getId() == getSubSistema().getId() || isFormacaoComplementar()) {
			return forward("/ensino/turma/busca_turma.jsp");
		}
		return forward("/ensino/consolidacao/selecionaTurma.jsp");
	}

	/**
	 * Volta para a tela de seleção de turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarLancarNotas() {
		return redirectJSF(getCurrentRequest().getContextPath() + "/ensino/consolidacao/detalhesTurma.jsf");
	}
	
	/**
	 * Cancela o processo de consolidação de uma turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * 		<li>sigaa.war/ensino/consolidacao/fim.jsp</li>
	 * 		<li>sigaa.war/ensino/consolidacao/selecionaTUrma.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		
		boolean agrupadora = turma.getTurmaAgrupadora() != null ? true : false;
		initObj();
		removeOperacaoAtiva();
		
		if (getSubSistema().getId() == SigaaSubsistemas.PORTAL_TURMA.getId()) {
			resetBean();
			if ( agrupadora )
				return redirect(getContextPath() + "/ensino/consolidacao/subturmas.jsf");
			else	
				return redirect(getContextPath() + "/ava/index.jsf");
		} else if(getSubSistema().getId() == SigaaSubsistemas.TECNICO.getId()
				&& getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TECNICO)) {
			redirectJSF(getCurrentRequest().getContextPath() + "/ensino/menus/menu_tecnico.jsf");
			return null;
		} else if(getSubSistema().getId() == SigaaSubsistemas.LATO_SENSU.getId()
				&& getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO)) {
			redirectJSF(getCurrentRequest().getContextPath() + "/ensino/menus/menu_lato.jsf");
			return null;
		} else if(getSubSistema().getId() == SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId()
				&& getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO)) {
			redirectJSF(getCurrentRequest().getContextPath() + "/lato/coordenador.jsf");
			return null;
		} else if(SigaaSubsistemas.SEDIS.getId() == getSubSistema().getId()) {
			return forward("/ensino/turma/busca_turma.jsp");
		} else {
			redirectJSF(getCurrentRequest().getContextPath() + "/portais/docente/docente.jsf");
			return null;
		}
	}

	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la e as oculta para os alunos.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String iniciarConfiguracoesAva() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		return cBean.configurar(null);
	}
	
	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la e as oculta para os alunos.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String salvarOcultar() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		
		if ( config == null ) {
			config = cBean.criar(turma);
			addMensagemWarning("Para poder ocultar as notas, as configurações desta turma virtual foram salvas automaticamente. "+ 
								"Você pode verificar a configuração da turma acessando Turma Virtual->Menu Turma Virtual->Configurações->Configurar Turma.");
		}

		config.setOcultarNotas(true);
		cBean.setConfig(config);
		cBean.salvar();
 		
		return salvar();
	}

	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la e as publica para os alunos.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String salvarPublicar() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		
		if ( config == null ) {
			config = cBean.criar(turma);
			addMensagemWarning("Para poder  publicar as notas, as configurações desta turma virtual foram salvas automaticamente. "+ 
								"Você pode verificar a configuração da turma acessando Turma Virtual->Menu Turma Virtual->Configurações->Configurar Turma.");
		}

		config.setOcultarNotas(false);
		cBean.setConfig(config);
		cBean.salvar();
 		
		return salvar();
	}	
	
	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String salvar() throws ArqException {
		try {
			
			registrarAcaoTurmaVirtual(AcaoAva.INICIAR_SALVAR);
			if (!checkOperacaoAtiva(1)) return cancelar();
			salvarNotas(false);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			registrarAcaoTurmaVirtual(AcaoAva.SALVAR);
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return redirect("/ensino/consolidacao/detalhesTurma.jsf");
	}

	/**
	 * Registra se o docente tentou salvar as notas na turma virtual.<br/><br/>
	 * Método não invocado por JSPs
	 * @return
	 * @throws ArqException 
	 */
	private void registrarAcaoTurmaVirtual(AcaoAva acao) {
		TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
		//Verifica se está acessando ou não pela turma virtual.
		if (tvBean != null && tvBean.getTurma() != null && tvBean.getTurma().equals(turma))
			tvBean.registrarAcao(null, EntidadeRegistroAva.NOTAS, acao, tvBean.getTurma().getId());
	}
	
	/**
	 * Salva as notas digitadas para a turma.<br/><br/>
	 * 
	 * Não é utilizado por JSPs.
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public void salvarNotas(boolean tarefa) throws NegocioException, ArqException {
		
		AvaliacaoDao daoAvaliacao = getDAO(AvaliacaoDao.class);
		
		notasIniciaisTurma = daoAvaliacao.findNotasByTurma(turma);
		popularNotas(tarefa);

		prepareMovimento(SigaaListaComando.SALVAR_CONSOLIDACAO_TURMA);

		TurmaMov mov = new TurmaMov();
		mov.setTurma(turma);
		mov.setMetodoAvaliacao(metodoAvaliacao);
		mov.setConfig(config);
		mov.setCodMovimento(SigaaListaComando.SALVAR_CONSOLIDACAO_TURMA);
		mov.setNotasIniciaisTurma(notasIniciaisTurma);
		
		execute(mov, getCurrentRequest());

	}

	/**
	 * Consolida a turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * </ul>
	 * @return
	 * @throws IOException
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String consolidar() throws ArqException {
		if (!checkOperacaoAtiva(1)) return cancelar();
		
		if (!UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha)) {
			addMensagem(MensagensTurmaVirtual.SENHA_INVALIDA_CONSOLIDAR_TURMA);
			return null;
		}
		// as notas serão salvas antes da validação
		try {
			salvarNotas(false);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			notifyError(e);
			return null;
		}
		

		if(hasOnlyErrors())
			return null;
		

		try {
			MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
			// verifica se o docente precisa avaliar/avaliou a turma.
			ConsolidacaoValidator.validar(dao , turma, param, metodoAvaliacao, SigaaListaComando.CONSOLIDAR_TURMA, isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE), parcial, config, getUsuarioLogado());
			
			TurmaMov mov = new TurmaMov();
			mov.setTurma(turma);
			mov.setConsolidacaoIndividual(individual);
			mov.setMetodoAvaliacao(metodoAvaliacao);
			mov.setConfig(config);
			mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_TURMA);
			mov.setRetificacoesReconsolidacao(retificacoesReconsolidacao);
			execute(mov, getCurrentRequest());
			retificacoesReconsolidacao = new ArrayList<RetificacaoMatricula>();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			
			// Se conseguiu consolidar a turma, deve limpar a lista de turmas ativas no portal do docente e corrigir a situação da turma aberta na turma virtual.
			TurmaVirtualMBean tBean = (TurmaVirtualMBean) getMBean ("turmaVirtual");
			if (tBean.getTurma() != null) {
				if (turma.getTurmaAgrupadora() == null)
					tBean.getTurma().consolidar();
				else { 
					for (Turma st:tBean.getTurma().getSubturmas()) {
						if (st.equals(turma)) {
							st.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.CONSOLIDADA, "CONSOLIDADA"));
						}	
					}
				}
				
				
			}
			
			((PortalDocenteMBean) getMBean ("portalDocente")).setTurmasAbertas(null);
	
			int idTurma = turma.getId();
	
			initObj();
			resetBean();
	
			if (isUserInRole(SigaaPapeis.GESTOR_TECNICO) && isMenuTecnico())
				return redirectJSF(getCurrentRequest().getContextPath() + "/ensino/menus/menu_tecnico.jsf");
			else {
				String url = getCurrentRequest().getContextPath() + "/ensino/consolidacao/fim.jsf?idTurma=" + idTurma;
				if (isFromMenuGestor())
					url += "&gestor=true";
				
				redirectJSF(url);
				return null;
			}
			
		
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * Vai para a tela de confirmação da consolidação parcial da turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmarParcial() throws ArqException {
		parcial = true;
		MatriculaComponenteDao dao = null;
		try {
			
			if (!checkOperacaoAtiva(1)) cancelar();
			salvarNotas(false);
			
			checkTurmaResponsaveisRole(turma, false);
			
			if (turma.isAgrupadora() || turma.getTurmaAgrupadora() != null)
				dao = getDAO(MatriculaComponenteDao.class);
			
			ConsolidacaoValidator.validar(dao, turma, param, metodoAvaliacao, SigaaListaComando.CONSOLIDAR_TURMA, isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE), parcial, config, getUsuarioLogado());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());	
			prepararTurma(turma);
			redirectJSF(getCurrentRequest().getContextPath() + "/ensino/consolidacao/detalhesTurma.jsf");
			return null;		
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/ensino/consolidacao/confirmar.jsp");
	}
	
	/**
	 * Realiza a consolidação parcial da turma, consolidando as matrículas dos alunos aprovados por média.
	 * e deixando a turma em aberto caso hajam alunos em recuperação. <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String parcial() throws ArqException {
		if (!checkOperacaoAtiva(1)) return cancelar();
		
		if (!UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha)) {
			addMensagem(MensagensTurmaVirtual.SENHA_INVALIDA_CONSOLIDAR_TURMA);
			return null;
		}

		TurmaMov mov = new TurmaMov();
		mov.setTurma(turma);
		mov.setConsolidacaoIndividual(individual);
		mov.setMetodoAvaliacao(metodoAvaliacao);
		mov.setConfig(config);
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_TURMA);
		mov.setConsolidacaoPartial(parcial);
		
		try {
			execute(mov, getCurrentRequest());
		
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			
			// Se conseguiu consolidar a turma, deve limpar a lista de turmas ativas no portal do docente e corrigir a situação da turma aberta na turma virtual.
			TurmaVirtualMBean tBean = (TurmaVirtualMBean) getMBean ("turmaVirtual");
			if (tBean.getTurma() != null)
				tBean.getTurma().consolidar();
			
			((PortalDocenteMBean) getMBean ("portalDocente")).setTurmasAbertas(null);
	
			int idTurma = turma.getId();
	
			initObj();
			resetBean();
	
			String url = getCurrentRequest().getContextPath() + "/ensino/consolidacao/fim.jsf?parcial=true&idTurma=" + idTurma;
			if (isFromMenuGestor())
				url += "&gestor=true";
	
			redirectJSF(url);
			return null;
		
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}

	/**
	 * Método chamado a partir do Portal da Turma para lançamento das notas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String consolidaTurmaPortal() throws ArqException {

		TurmaVirtualMBean portalTurma = (TurmaVirtualMBean) getMBean("turmaVirtual");
		
		if (isEmpty(turma))
			turma = portalTurma.getTurma();
		
		fromPortalTurma = true;
		return escolherTurma();
	}

	/**
	 * Caso a consolidação seja iniciada a partir da turma virtual
	 * e a turma for uma turma agrupadora, a consolidação deverá acontecer sub-turma
	 * por sub-turma. Este método é chamado para listar as sub-turmas da turma agrupadora.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 */
	public String listaSubTurmas() {
		return forward("/ensino/consolidacao/subturmas.jsp");
	}
	
	/**
	 * Seleciona a sub-turma caso a turma da turma virtual seja agrupadora.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ensino/consolidacao/subturmas.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String escolherSubTurma() throws ArqException {
		turma = getDAO(TurmaDao.class).findByPrimaryKey(getParameterInt("id"), Turma.class);
		if (turma.isSubTurma()) turma.getTurmaAgrupadora().getId();
		fromPortalTurma = true;
		return escolherTurma();
	}

	/**
	 * Busca notas em request e popula na coleção de matrículas.
	 * Método não invocado por JSPs.
	 * @throws ParseException
	 * @throws NumberFormatException
	 * @throws DAOException
	 */
	private void popularNotas(boolean tarefa) throws NegocioException {

		if (!isEmpty(turma.getMatriculasDisciplina())) {
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {
	
				if (!matricula.isConsolidada()) {
				
					if (!tarefa){
						String faltasStr = getParameter("faltas_" + matricula.getId());
						Integer faltas = 0;
						if (!StringUtils.isEmpty(faltasStr))
							faltas = convertFalta(faltasStr);
						
						if (faltas < 0)
							throw new NegocioException (UFRNUtils.getMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Faltas").getMensagem());
			
						matricula.setNumeroFaltas(faltas);
						
						String paramRecuperacao = "recup_" + matricula.getId();
						if (existeNota(paramRecuperacao))
							matricula.setRecuperacao(getNota(paramRecuperacao));
					}
				
					matricula.getNotas().iterator();
					for (NotaUnidade nota : matricula.getNotas()) {
						if (!tarefa) {
							String paramNota = "nota_" + nota.getId();
							if (existeNota(paramNota))
								nota.setNota(getNota(paramNota));
						}
						
						if (nota.getAvaliacoes() != null && !nota.getAvaliacoes().isEmpty()) {
							if (config != null && config.getTipoMediaAvaliacoes(nota.getUnidade()) != null) {
								if (config.isAvaliacoesMediaPonderada(nota.getUnidade())) {
									double notas = 0.0;
									double pesos = 0.0;
		
									for (Avaliacao avaliacao : nota.getAvaliacoes()) {
										
										if (!tarefa) {
											String paramAvaliacao = "aval_" + avaliacao.getId();
											if (existeNota(paramAvaliacao))
												avaliacao.setNota(getNota(paramAvaliacao));
										}
										
										if (avaliacao.getNota() != null) {
											notas += avaliacao.getNota() * 10D * avaliacao.getPeso();
											pesos += avaliacao.getPeso();
										}
									}
									
									double n = Math.round (notas / (pesos == 0 ? 10 : pesos * 10) * 10D) / 10D;
									nota.setNota(n);
								} else if (config.isAvaliacoesMediaAritmetica(nota.getUnidade())) {
									double notas = 0.0;
									
									for (Avaliacao avaliacao : nota.getAvaliacoes()) {
										
										if (!tarefa) {
											String paramAvaliacao = "aval_" + avaliacao.getId();
											if (existeNota(paramAvaliacao))
												avaliacao.setNota(getNota(paramAvaliacao));
										}
										
										if (avaliacao.getNota() != null)
											notas += avaliacao.getNota() * 10D;
									}
									
									double n = Math.round(notas / (nota.getAvaliacoes().size() * 10D) * 10D ) / 10D;
									nota.setNota(n);
								} else if (config.isAvaliacoesSoma(nota.getUnidade())) {
									double notas = 0.0;
									for (Avaliacao avaliacao : nota.getAvaliacoes()) {
										
										if ( !tarefa ) {
											String paramAvaliacao = "aval_" + avaliacao.getId();
											if (existeNota(paramAvaliacao))
												avaliacao.setNota(getNota(paramAvaliacao));
										}
										
										if (avaliacao.getNota() != null)
											notas += avaliacao.getNota() * 10D;
									}
									
									nota.setNota(notas > 100D ? 10D : notas / 10D);
								}
							}
						}
		
					}
		
					if (!tarefa) {
						if (isCompetencia()) {
							String competencia = getParameter("competencia_" + matricula.getId());
							if ("true".equals(competencia))
								matricula.setApto(true);
							else if ("false".equals(competencia))
								matricula.setApto(false);
							else
								matricula.setApto(null);
						} else if (isConceito()) {
							String conceito = getParameter("conceito_" + matricula.getId());
							if (conceito != null) {
								Double conceitoD = Double.parseDouble(conceito);
								if (conceitoD < 0) {
									matricula.setConceito(null);
								} else {
									matricula.setConceito(conceitoD);
								}
							} else {
								matricula.setConceito(null);
							}
						} else {
							matricula.setMediaFinal(matricula.calculaMediaFinal());
						}
					}
				}
			}
		}
	}

	/**
	 * Pega uma nota em request. Se a nota não existir, retorna nulo.
	 * Usado pelo popularNotas().<br/><br/>
	 * 
	 * <strong>Cuidado ao utilizar este método:</strong> Caso uma nota não tenha sido enviada,
	 * o objeto pode receber a nota nula erroneamente.<br/>
	 * Para evitar este problema, deve-se chamar o método existeNota(nota) para verificar se 
	 * foi enviado um novo valor para o objeto.
	 * 
	 * @param paramName
	 * @return
	 * @throws NegocioException 
	 * @throws ParseException
	 */
	private Double getNota(String paramName) throws NegocioException {
		String param = getParameter(paramName);

		if (param != null && !"".equals(param.trim())) {
			param = param.trim().replaceAll(",", ".");
			
			try {
				Double nota = Double.parseDouble(param);
				
				// Garante que as notas estão entre zero e dez.
				if (nota > 10 || nota < 0)
					throw new Exception ();
				
				return nota;
			} catch(Exception e) {
				throw new NegocioException("Nota inválida: " + getParameter(paramName));
			}
		}

		return null;
	}
	
	/**
	 * Indica se foi enviado um novo valor para a nota passada.
	 * 
	 * @param paramName
	 * @return
	 * @throws NegocioException
	 */
	private boolean existeNota (String paramName) throws NegocioException {
		return getParameter(paramName) != null;
	}

	/**
	 * Converte as faltas de String para Integer.
	 * Usado pelo popularNotas().
	 * @param paramName
	 * @return
	 * @throws NegocioException 
	 * @throws ParseException
	 */
	private Integer convertFalta(String falta) throws NegocioException {

		try {
			Integer faltas = Integer.parseInt(falta); 
			
			return faltas;
		} catch(Exception e) {
			throw new NegocioException("Faltas inválidas: " + falta);
		}
	}
	
	/**
	 * Retorna o booleano que indica se a consolidação é individual.
	 * @return the individual
	 */
	public boolean isIndividual() {
		return individual;
	}

	/**
	 * Seta o booleano que indica se a consolidação é individual.
	 * @param individual the individual to set
	 */
	public void setIndividual(boolean individual) {
		this.individual = individual;
	}

	/** 
	 * Inicializa o bean, método chamado pelo construtor.
	 * Método não invocado por JSPs,
	 */
	public void initObj() {
		turma = new Turma();
		turma.setDisciplina(new ComponenteCurricular());

		conceitos = new ArrayList<ConceitoNota>();
		individual = false;

		numeroUnidades = 0;
		metodoAvaliacao = 0;
		fromPortalTurma = false;
	}

	/**
	 * Imprime o relatório de notas da turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 *		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String imprimir() throws ArqException {

		try {
			if (!checkOperacaoAtiva(1)) return cancelar();
			salvarNotas(false);
			
			TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
			if (tvBean.getTurma() == null)
				tvBean.setTurma(turma);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		return forward("/ensino/consolidacao/relatorio.jsp");
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * Identifica se a turma selecionada é do ensino técnico de música.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isMusica() {
		Unidade unidade = turma.getDisciplina().getUnidade();
		if (turma.isTecnico())
			return ((unidade != null && unidade.getId() == UnidadeGeral.ESCOLA_MUSICA) || (unidade != null && unidade.getGestoraAcademica() != null && unidade.getGestoraAcademica().getId() == UnidadeGeral.ESCOLA_MUSICA));
		else
			return (unidade != null && unidade.getGestoraAcademica() != null && unidade.getGestoraAcademica().getId() == UnidadeGeral.ESCOLA_MUSICA);
	}
	
	/**
	 * Identifica se a turma selecionada é uma subturma.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isSubturma() {
		return turma.getTurmaAgrupadora() != null;
	}

	/**
	 * Identifica se o processo de consolidação começou a partir do menu gestor de ensino técnico.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/selecionarTurma.jsp</li>
	 *		<li>sigaa.war/ensino/consolidacao/selecionarTurmaPlanoCurso.jsp</li>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isFromMenuGestor() {
		return (getParameter("gestor") != null && "true".equals(getParameter("gestor")));
	}

	/**
	 * Retorna o número máximo de faltas para a turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/detalhesTurma.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getMaxFaltas() throws DAOException {
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		if (isLato()) {
			float maximo = getPropostaCursoLato().getFreqObrigatoria();
			return turma.getDisciplina().getMaximoFaltasPermitido(maximo, param.getMinutosAulaRegular());
		} else {
			if (param == null) throw new RuntimeNegocioException("Não existe um processo de consolidação ativo.");
			return turma.getDisciplina().getMaximoFaltasPermitido(param.getFrequenciaMinima(), param.getMinutosAulaRegular());			
		}
	}

	private PropostaCursoLato getPropostaCursoLato() {
		return ((CursoLato) turma.getCurso()).getPropostaCurso();
	}
	
	/**
	 * Retorna o número total de aulas. Utilizado na validação do número de faltas,
	 * para que não seja maior que o número total de aulas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public int getMaxFaltasTotal(){
		return turma.getDisciplina().getChTotalAula();
	}

	/**
	 * Retorna a média mínima de aprovação, após todas as notas serem preenchidas, incluindo a recuperação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinima(){
		if (isLato()) {
			return getPropostaCursoLato().getMediaMinimaAprovacao();
		} else {			
			return param.getMediaMinimaAprovacao();
		}
	}
	
	
	/**
	 * Retorna a média mínima para a turma.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinimaPossibilitaRecuperacao(){
		if (isLato()) {
			return getPropostaCursoLato().getMediaMinimaAprovacao();
		} else {			
			return param.getMediaMinimaPossibilitaRecuperacao();
		}
	}
	
	/**
	 * Peso dado a média no calculo da média final, usado quando o discente encontra-se em recuperação
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPesoMedia() {
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
		return (new Integer(pesoMediaPesoRec[0].trim())); 
	}
	
	/**
	 * Peso dado a Recuperação no calculo da média final
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPesoRecuperacao() {
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
		return (new Integer(pesoMediaPesoRec[1].trim())); 
	}
	
	/**
	 * Retorna a média mínima para passar por média.
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinimaPassarPorMedia(){
		if (isLato()) {
			return getPropostaCursoLato().getMediaMinimaAprovacao();
		} else {			
			return param.getMediaMinimaPassarPorMedia();
		}
	}
	
	/**
	 * Retorna a média mínima para ser aprovado
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinimaAprovacao() {
		if (isLato()) {
			return getPropostaCursoLato().getMediaMinimaAprovacao();
		} else {
			return param.getMediaMinimaAprovacao();
		}
	}	
	
	
	/**
	 * Retorna se é permitido fazer a recuperação
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isPermiteRecuperacao() {		
		return param.isPermiteRecuperacao();		
	}	
	
	public ConfiguracoesAva getConfig() {
		return config;
	}

	public void setConfig(ConfiguracoesAva config) {
		this.config = config;
	}
	
	public UploadedFile getArquivoPlanilha() {
		return arquivoPlanilha;
	}

	public void setArquivoPlanilha(UploadedFile arquivoPlanilha) {
		this.arquivoPlanilha = arquivoPlanilha;
	}
	
	/**
	 * Exporta as notas em formato de planilha xls.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String exportarPlanilha () throws ArqException{
		
		try {
			// Recupera os valores digitados pelo usuário.
			popularNotas(false);
		
			new NotasExcelMBean (this).buildSheet();
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		} catch (Exception e){
			throw new ArqException (e);
		}
		
		return null;
	}
	
	/**
	 * Exibe o formulário para importação da planilha de notas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/detalhesTurma.jsp
	 * @return
	 */
	public String preImportarPlanilha (){
		nomesAvaliacoes = new ArrayList <String> ();
		
		return forward(PAGINA_FORM_IMPORTAR_PLANILHA);
	}
	
	/**
	 * Lê as notas e frequências contidas na planilha importada os exibe para
	 * que o professor confirme a importação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String importarPlanilha () throws ArqException , IllegalArgumentException{
		
		if (arquivoPlanilha == null){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo");			
			nomesAvaliacoes.clear();
			return null;
		}
		
		try {
			new NotasExcelMBean (this).importarPlanilha();
			
			// Caso o professor apague uma matricula da planilha - Exibe Mensagem Vermelha Padrão
			if ( matriculasNotasImportacao != null && matriculasNotasImportacao.size() < turma.getMatriculasDisciplina().size() )
				throw new NegocioException ("O arquivo enviado é inválido.");

		} catch (NegocioException e){
			nomesAvaliacoes.clear();
			addMensagens(e.getListaMensagens());
		} 
		catch (OfficeXmlFileException e){
			nomesAvaliacoes.clear();
			addMensagem(MensagensArquitetura.ARQUIVO_UPLOAD_INVALIDO);
			return null;
		}	
		catch (IllegalArgumentException e){
				nomesAvaliacoes.clear();
				addMensagem(MensagensArquitetura.ARQUIVO_UPLOAD_INVALIDO);
				return null;	
		}catch (IOException e){
			addMensagem(MensagensArquitetura.ARQUIVO_UPLOAD_INVALIDO);
			nomesAvaliacoes.clear();
			return null;
		}
		
		prepareMovimento(SigaaListaComando.IMPORTAR_NOTAS_PLANILHA);
		
		return PAGINA_FORM_IMPORTAR_PLANILHA;
	}
	
	/**
	 * Salva os valores importados pela planilha preenchida pelo professor, após sua confirmação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp 
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarImportacaoPlanilha () throws ArqException{
		
		List <Avaliacao> avaliacoesAAtualizar = new ArrayList <Avaliacao> ();
		List <NotaUnidade> notasAAtualizar = new ArrayList <NotaUnidade> ();
		List <MatriculaComponente> matriculasAAtualizar = new ArrayList <MatriculaComponente> ();

		if (matriculasNotasImportacao == null || matriculasNotasImportacao.isEmpty()){
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Dados para importação");
			return null;
		}
		
		// Para todos os discentes da turma, verifica quais tiveram alteração nas notas
		for (MatriculaComponente mc : turma.getMatriculasDisciplina()){
			List <Double> valores = matriculasNotasImportacao.get(mc.getDiscente().getMatricula());
			
			if (valores != null && !mc.isConsolidada()){
				for (int i = 0; i < nomesAvaliacoes.size() ; i++){
					
					String avaliacao = nomesAvaliacoes.get(i);
					
					Double valor = valores.get(i);
					
					boolean ok = false;
					
					if (valor > -1)
						ok = true;
					
					if (avaliacao.equals("Rec."))
						mc.setRecuperacao(ok ? valor.doubleValue() : null);
					else if (avaliacao.equals("Faltas")){
						mc.setNumeroFaltas(ok ? valor.intValue() : null);
					} 
					else if (isConceito() && avaliacao.equals("Resultado")){
						mc.setMediaFinal(ok ? valor.doubleValue() : null);
					}else if (isCompetencia() && avaliacao.equals("Resultado"))
					{
						if ( ok && valor == 1)
							mc.setApto(true);
						else if (ok && valor == 0)
							mc.setApto(false);
					}else {
						List <NotaUnidade> notas = (List<NotaUnidade>) mc.getNotas();
						
						for (NotaUnidade nu : notas){
							
							if (avaliacao.startsWith("Nota ") || avaliacao.startsWith("Unid. ")){
								String unidade = avaliacao.split(" ")[1];
								if (nu.getUnidade() == Integer.parseInt(unidade)){
									nu.setNota(ok ? Double.parseDouble(""+valor) : null);
									notasAAtualizar.add(nu);
								}
							}
							
							List <Avaliacao> avaliacoes = nu.getAvaliacoes();
							//Evitar erro de NP.
							if(avaliacoes != null) {						
								for (Avaliacao a : avaliacoes) {								
									if (a.getAbreviacao().equals(avaliacao)){									
										a.setNota(ok ? Double.parseDouble(""+valor) : null);									
										avaliacoesAAtualizar.add(a);								
									}						
								}								
							}
						}
					}
				}
				
				matriculasAAtualizar.add(mc);
			}
		}
		
		try {
			MovimentoImportacaoNotasPlanilha mov = new MovimentoImportacaoNotasPlanilha(matriculasAAtualizar, notasAAtualizar, avaliacoesAAtualizar);		
			execute (mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return forward(PAGINA_DETALHES_TURMA);
			
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	/**
	 * Transforma os valores das notas em conceitos. Usado quando o método de avaliação é conceito.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp 
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public Map<Long, List<String>> getMatriculasConceito () throws DAOException, NegocioException
	{	
		Map<Long, List<String>> matriculasConceitos = new LinkedHashMap<Long,List<String>>();
		
		for (MatriculaComponente mc : turma.getMatriculasDisciplina())
		{
			List <Double> notas = matriculasNotasImportacao.get(mc.getDiscente().getMatricula());
			ArrayList<String> conceitos = new ArrayList<String>();
			
			int i = 0;
			
			if ( notas == null )
				throw new NegocioException ("O arquivo enviado é inválido.");
			
			for ( Double nota  : notas )
			{
				if ( i != notas.size()-1 )
				{
					if ( nota != -1 )
						conceitos.add(ConceitoNotaHelper.getDescricaoConceito( nota.doubleValue() ));
					else 
						conceitos.add("-");
				}
				// A última nota é a quantidade de Faltas
				else
				{	
					if ( nota == -1 )
						conceitos.add( "" );
					else
						conceitos.add( Double.toString(Math.round(nota) ) );
				}
				i++;
			}
			
			matriculasConceitos.put(mc.getDiscente().getMatricula(), conceitos);
		}
		return matriculasConceitos;
	}
	
	/**
	 * Transforma os valores das notas em Apto ou Não Apto. Usado quando o método de avaliação é Competência.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp 
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public Map<Long, List<String>> getMatriculasCompetencia () throws DAOException, NegocioException
	{	
		Map<Long, List<String>> matriculasCompetencia = new LinkedHashMap<Long,List<String>>();
		
		for (MatriculaComponente mc : turma.getMatriculasDisciplina())
		{
			List <Double> notas = matriculasNotasImportacao.get(mc.getDiscente().getMatricula());
			ArrayList<String> competencia = new ArrayList<String>();
			
			int i = 0;
			
			if ( notas == null )
				throw new NegocioException ("O arquivo enviado é inválido.");
			
			for ( Double nota  : notas )
			{
				if ( i != notas.size()-1 )
				{
					if ( nota == 1 )
						competencia.add("Apto");
					else if ( nota == 0 )
						competencia.add("Não Apto");
					else 
						competencia.add("--");
				}
				// A última nota é a quantidade de Faltas
				else
				{
					if ( nota == -1 )
						competencia.add( "" );
					else
						competencia.add( Double.toString(Math.round(nota) ) );
				}
				i++;
			}
			
			matriculasCompetencia.put(mc.getDiscente().getMatricula(), competencia);
		}
		return matriculasCompetencia;
	}
	
	/**
	 * Transforma os valores ponto flutuante das notas em String. Usado quando o método de avaliação é Notas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp 
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public Map<Long, List<String>> getMatriculasNotas () throws DAOException, NegocioException
	{	
		LinkedHashMap<Long, List<String>> matriculasNotas = new LinkedHashMap<Long,List<String>>();
		
		for (MatriculaComponente mc : turma.getMatriculasDisciplina())
		{
			List <Double> notas = matriculasNotasImportacao.get(mc.getDiscente().getMatricula());
			ArrayList<String> notasString = new ArrayList<String>();

			int i = 0;
		
			if ( notas == null )
				throw new NegocioException ("O arquivo enviado é inválido.");
				
			for ( Double nota  : notas )
			{
				if ( i != notas.size()-1 )
				{
					String valor = "-";
					
					if (nota != -1)
						valor = Double.toString(nota); 
					
					if ( valor.length() > 4 )
						valor = valor.substring(0,4);
					
					notasString.add( valor );
				}
				// A última nota é a quantidade de Faltas
				else
				{
					if ( nota == -1 )
						notasString.add("-");
					else
						notasString.add( Double.toString(Math.round(nota) ));
				}	
				i++;
			}
			
			matriculasNotas.put(mc.getDiscente().getMatricula(), notasString);
		}
		return matriculasNotas;
	}
	
	public void setNomesAvaliacoesImportacao(List<String> nomesAvaliacoes) {
		this.nomesAvaliacoes = nomesAvaliacoes;
	}

	public void setMatriculasNotasImportacao(Map<Long, List<Double>> matriculasNotasImportacao) {
		this.matriculasNotasImportacao = matriculasNotasImportacao;
	}

	public Map<Long, List<Double>> getMatriculasNotasImportacao() {
		return matriculasNotasImportacao;
	}

	public List<String> getNomesAvaliacoes() {
		return nomesAvaliacoes;
	}

	public void setNomesAvaliacoes(List<String> nomesAvaliacoes) {
		this.nomesAvaliacoes = nomesAvaliacoes;
	}

	public List<Long> getMatriculasImportacao() {
		return matriculasImportacao;
	}

	public void setMatriculasImportacao(List<Long> matriculasImportacao) {
		this.matriculasImportacao = matriculasImportacao;
	}

	public boolean isParcial() {
		return parcial;
	}

	public void setParcial(boolean parcial) {
		this.parcial = parcial;
	}

	public void setMatriculasNomesImportacao(
			Map<Long, String> matriculasNomesImportacao) {
		this.matriculasNomesImportacao = matriculasNomesImportacao;
	}
	
	

	public Collection<NotaUnidade> getNotasIniciaisTurma() {
		return notasIniciaisTurma;
	}

	public void setNotasIniciaisTurma(Collection<NotaUnidade> notasIniciaisTurma) {
		this.notasIniciaisTurma = notasIniciaisTurma;
	}

	/**
	 * Retorna o nome dos discentes.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp 
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public Map<Long, String> getMatriculasNomesImportacao() {
		
		if ( matriculasNomesImportacao == null ) {	
			matriculasNomesImportacao = new LinkedHashMap<Long,String>();
			
			for ( MatriculaComponente m : turma.getMatriculasDisciplina() ) {
				matriculasNomesImportacao.put(m.getDiscente().getMatricula(), m.getDiscente().getNome());
			}
		}
		
		return matriculasNomesImportacao;
	}

	public Collection<RetificacaoMatricula> getRetificacoesReconsolidacao() {
		return retificacoesReconsolidacao;
	}

	public void setRetificacoesReconsolidacao(
			Collection<RetificacaoMatricula> retificacoesReconsolidacao) {
		this.retificacoesReconsolidacao = retificacoesReconsolidacao;
	}
	
	/**
	 * Checa se o usuário atual é um dos responsáveis pela turma passada por
	 * parâmetro. <br/><br/>
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>Não Invocada por JSPs.</li>
	 * </ul>
	 *
	 * @param turma
	 * @param comPermissao Indica se deve verificar as permissões configuradas por um docente para o usuário logado. False indica que mesmo que o usuário tenha recebido permissão de docente, não poderá realizar a operação se não tiver um dos outros vínculos.
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void checkTurmaResponsaveisRole(Turma turma, boolean comPermissao)
			throws SegurancaException, DAOException {
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		
		turma = dao.refresh(turma);

		// Professores que ministram as aulas
		if (getServidorUsuario() != null || getUsuarioLogado().getDocenteExterno() != null ) {
			for (Docente professor : turma.getDocentes()) {
				if ( getServidorUsuario() != null && professor != null && professor.getId() == getServidorUsuario().getId()) {
					return;
				}else if ( getServidorUsuario() == null && professor != null && professor.getId() == getUsuarioLogado().getDocenteExterno().getId()) {
					return;
				}
			}
		}
		
		if (turma.getTurmaAgrupadora() != null)
			turma = turma.getTurmaAgrupadora();

		// Usuários que receberam permissão do docente para administrar a turma virtual
		if (comPermissao){
			PermissaoAva permissao = dao.findPermissaoByPessoaTurma(getUsuarioLogado().getPessoa(), turma);
			if (permissao != null) return;
		}

		// Gestores
		if (getUsuarioLogado().isUserInRole( new int[]{SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_LATO, 
				SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR}))
			return;

		throw new SegurancaException(
				"Usuário não autorizado a realizar esta operação");
	}

	/**
	 * Verifica se a turma vai ser finalizada na consolidação parcial.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *		<li>sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isTurmaFinalizadaConsolidacaoParcial () {
		for ( MatriculaComponente matricula : turma.getMatriculasDisciplina() )
			if ( matricula.isEmRecuperacao() )
				return false;
		return true;
	}
}