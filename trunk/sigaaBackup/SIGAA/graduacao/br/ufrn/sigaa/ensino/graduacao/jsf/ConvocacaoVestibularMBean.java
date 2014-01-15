/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 16/12/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.ConvocacaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoConvocacaoVestibular;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.ResultadoPessoaConvocacao;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;
import br.ufrn.sigaa.vestibular.dominio.SituacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;

/**
 * Controlador respons�vel por gerar as convoca��es dos candidatos aprovados no
 * vestibular para as vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
@Component("convocacaoVestibular") @Scope("request")
public class ConvocacaoVestibularMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivo> {

	/** Constante com o endere�o da view do formul�rio. */
	private static final String JSP_FORM = "/graduacao/convocacao_vestibular/form_convocacao.jsp";
	/** Constante com o endere�o da view do resumo. */
	private static final String JSP_RESUMO = "/graduacao/convocacao_vestibular/resumo_convocacao.jsp";
	/** Constante com o endere�o da view da importa��o dos dados do vestibular. */
	private static final String JSP_CONVOCACAO_IMPORTACAO_VESTIBULAR = "/graduacao/convocacao_vestibular/form_convocacao_importacao.jsp";
	
	/** Lista de novas convoca��es de candidatos. */
	private List<ConvocacaoProcessoSeletivoDiscente> convocacoes;
	/** Lista de cancelamentos de convoca��es anteriores gerados por reconvoca��es de candidatos. */
	private List<CancelamentoConvocacao> cancelamentos;
	/** Lista de processos seletivos para o usu�rio escolher em qual ser� importado os dados. */
	private List<SelectItem> processosCombo;

	/**
	 * Mapa contendo o ID de cada {@link MatrizCurricular matriz} ofertada no
	 * {@link ProcessoSeletivoVestibular vestibular} selecionado e a sua
	 * respectiva quantidade de vagas remanescentes.
	 */
	private Map<Integer, Integer> mapaVagasPorMatriz;
	/**
	 * Mapa contendo o ID de cada {@link MatrizCurricular matriz} ofertada para
	 * entrada no primeiro semestre do {@link ProcessoSeletivoVestibular
	 * vestibular} selecionado e a sua respectiva quantidade de vagas
	 * remanescentes.
	 */
	private Map<Integer, Integer> mapaVagasPrimeiroSemestre;
	/**
	 * Mapa contendo o ID de cada {@link MatrizCurricular matriz} ofertada para
	 * entrada em um semestre do {@link ProcessoSeletivoVestibular
	 * vestibular} selecionado e a sua respectiva quantidade de vagas
	 * remanescentes.
	 */
	private Map<Integer, Integer> mapaSemestreIngressoPorMatriz;
	/** Mapa contendo o ID de cada convoca��o de aprovados para preenchimento de vagas no segundo semestre. */
	private Map<Integer, List<ResultadoOpcaoCurso>> convocadosSegundoSemestre = new LinkedHashMap<Integer, List<ResultadoOpcaoCurso>>();
	/** Mapa contendo o ID de cada convoca��o de aprovados para preenchimento de vagas da primeira op��o. */
	private Map<Integer, List<ResultadoOpcaoCurso>> suplentesPrimeiraOpcao = new LinkedHashMap<Integer, List<ResultadoOpcaoCurso>>();
	/** Mapa contendo o ID de cada convoca��o de aprovados para preenchimento de vagas de turno distinto. */
	private Map<Integer, List<ResultadoOpcaoCurso>> suplentesTurnoDistinto = new LinkedHashMap<Integer, List<ResultadoOpcaoCurso>>();
	/** Mapa contendo o ID de cada convoca��o de aprovados para preenchimento de vagas da segunda op��o. */
	private Map<Integer, List<ResultadoOpcaoCurso>> suplentesSegundaOpcao = new LinkedHashMap<Integer, List<ResultadoOpcaoCurso>>();
	/** Mapa contendo o ID de cada convoca��o de suplentes sem argumento m�nimo de aprova��o. */
	private Map<Integer, List<ResultadoOpcaoCurso>> suplentesSemArgumentoMinimoAprovacao = new LinkedHashMap<Integer, List<ResultadoOpcaoCurso>>();
	
	/** Lista de IDs de convocados para primeira op��o de curso. */
	private Collection<Integer> convocadosPrimeiraOpcao = new HashSet<Integer>();
	/** Lista de IDs de convocados para curso de turno distinto. */
	private Collection<Integer> convocadosTurnoDistinto = new HashSet<Integer>();
	
	/** Indica se a matriz curricular teve todas as oferta de vagas preenchidas. */
	private boolean preencheuVaga = false;
	/** Resumo das convoca��es de processo seletivo.. */
	private Map<MatrizCurricular, Integer> resumoConvocacao;
	
	/** Resumo dos erros de valida��es para as convoca��es de processo seletivo, n�o sendo convocados. */
	private Map<MatrizCurricular, List<ResultadoPessoaConvocacao>> errosConvocacao;

	/** Comparador utilizado para ordenar as convoca��es de discentes em um processo seletivo. */
	private Comparator<ConvocacaoProcessoSeletivoDiscente> convocacaoDiscenteComparator = new Comparator<ConvocacaoProcessoSeletivoDiscente>(){
		@Override
		public int compare(ConvocacaoProcessoSeletivoDiscente o1, ConvocacaoProcessoSeletivoDiscente o2) {
			MatrizCurricular matriz1 = o1.getDiscente().getMatrizCurricular();
			MatrizCurricular matriz2 = o2.getDiscente().getMatrizCurricular();
			if (matriz1.getCurso().getNome().compareTo(matriz2.getCurso().getNome()) != 0) {
				return matriz1.getCurso().getNome().compareTo(matriz2.getCurso().getNome());
			} else if (matriz1.getHabilitacao() != null && matriz2.getHabilitacao() != null 
					&& matriz1.getHabilitacao().getNome().compareTo(matriz2.getHabilitacao().getNome()) != 0) {
				return matriz1.getHabilitacao().getNome().compareTo(matriz2.getHabilitacao().getNome());
			} else if (matriz1.getTurno().getSigla().compareTo(matriz2.getTurno().getSigla()) != 0) {
				 return matriz1.getTurno().getSigla().compareTo(matriz2.getTurno().getSigla());
			} else {
				return matriz1.getGrauAcademico().getDescricao().compareTo(matriz2.getGrauAcademico().getDescricao());
			}
		}
	};
	
	/** Matrizes curriculares a importar. */
	private Collection<MatrizCurricular> matrizes;
	
	/** Quantidade de matrizes curriculares processadas. */
	private int quantidadeProcessado;
	
	/**
	 * Construtor padr�o.
	 */
	public ConvocacaoVestibularMBean() {
		clear();
	}

	/**
	 * Inicializa as informa��es utilizadas em todo o caso de uso.
	 */
	private void clear() {
		obj = new ConvocacaoProcessoSeletivo();
	}

	/**
	 * Inicializa as informa��es utilizadas no processamento da convoca��o.
	 */
	private void clearProcessamento() {
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
		cancelamentos = new ArrayList<CancelamentoConvocacao>();
		
		convocadosSegundoSemestre = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		suplentesPrimeiraOpcao = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		suplentesTurnoDistinto = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		suplentesSegundaOpcao = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		suplentesSemArgumentoMinimoAprovacao = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		
		convocadosPrimeiraOpcao = new HashSet<Integer>();
		convocadosTurnoDistinto = new HashSet<Integer>();
		
		mapaSemestreIngressoPorMatriz = new HashMap<Integer, Integer>();
		mapaVagasPorMatriz = new HashMap<Integer, Integer>();
		mapaVagasPrimeiroSemestre  = new HashMap<Integer, Integer>();
		matrizes = null;
		resumoConvocacao = null;
	}

	/**
	 * Popula as informa��es necess�rias e inicia o caso de uso.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES);
		setOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId());
		return telaFormulario();
	}
	
	/** Inicia a importa��o de convoca��es para o vestibular.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menu_servidor.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarConvocacaoImportacaoVestibular() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		return telaConvocacaoImportacaoVestibular();
	}

	/**
	 * Procura vagas ociosas e as informa��es dos pr�ximos classificados para
	 * preench�-las. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	@Override
	public String buscar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId()))
			return null;
		clearProcessamento();
		
		ConvocacaoVestibularDao dao = getDAO(ConvocacaoVestibularDao.class);
		
		ValidatorUtil.validateRequiredId(obj.getProcessoSeletivo().getId(), "Processo Seletivo Vestibular", erros);
		ValidatorUtil.validateRequired(obj.getDescricao(), "Descri��o", erros);
		ValidatorUtil.validateRequired(obj.getDataConvocacao(), "Data da Convoca��o", erros);
		ValidatorUtil.validateRequired(obj.getSemestreConvocacao(), "Semestre a Convocar", erros);
		validateMinValue(obj.getPercentualAdicionalVagas(), 0, "Percentual de Vagas Adicionais", erros);
		dao.initialize(obj.getProcessoSeletivo());
		
		checarPreCondicoes(dao);
		
		if(hasErrors())
			return null;
		
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoVestibular.class));
		mapaVagasPorMatriz = dao.findVagasOciosas(obj.getProcessoSeletivo(), obj.getSemestreConvocacao());
		// aplica o percentual de aumento de vagas
		if (obj.getPercentualAdicionalVagas() > 0) {
			for(Integer idMatriz: mapaVagasPorMatriz.keySet()) {
				Integer vagas = mapaVagasPorMatriz.get(idMatriz);
				vagas = (int) ((100 + obj.getPercentualAdicionalVagas()) * vagas / 100);
				mapaVagasPorMatriz.put(idMatriz, vagas);
			}
		}
		// vagas ociosas
		if (obj.getSemestreConvocacao() != SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE) {
			mapaVagasPrimeiroSemestre = dao.findVagasOciosas(obj.getProcessoSeletivo(), SemestreConvocacao.CONVOCA_APENAS_PRIMEIRO_SEMESTRE);
		} else {
			mapaVagasPrimeiroSemestre = new HashMap<Integer, Integer>();
		}
		if(hasVagasOciosas()){
			carregarSuplentes(dao, mapaVagasPorMatriz.keySet());
		
			for(TipoConvocacao tipo: TipoConvocacao.values()){
				if(tipo.equals(TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE)) continue;
				popularVagas(tipo);
			}
			
			while(hasVagasOciosas() && preencheuVaga) {
				popularVagas(TipoConvocacao.CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO);
			}
		} else {
			addMensagemWarning("Todas vagas ofertadas para o Processo Seletivo "+obj.getProcessoSeletivo().getNome()+" foram preenchidas.");
			return null;
		}
		
		Collections.sort(convocacoes, convocacaoDiscenteComparator);
		return telaResumo();
	}

	/** Carrega a lista de candidatos suplentes.
	 * @param dao
	 * @param idMatrizes 
	 * @throws DAOException
	 */
	private void carregarSuplentes(ConvocacaoVestibularDao dao, Set<Integer> idMatrizes) throws DAOException {
		mapaSemestreIngressoPorMatriz = dao.findMapaSemestreIngresso(obj.getProcessoSeletivo(), null);
		convocadosSegundoSemestre = dao.findConvocadosSegundoSemestre(obj.getProcessoSeletivo(), mapaSemestreIngressoPorMatriz.keySet());
		suplentesPrimeiraOpcao = dao.findSuplentesByOpcao(obj.getProcessoSeletivo(), ResultadoOpcaoCurso.PRIMEIRA_OPCAO);
		if (ParametroHelper.getInstance().getParametroBoolean(ParametrosVestibular.CONVOCAR_APROVADOS_TURNO_DISTINTO))
			suplentesTurnoDistinto = dao.findSuplentesTurnoDistinto(obj.getProcessoSeletivo());
		suplentesSegundaOpcao = dao.findSuplentesByOpcao(obj.getProcessoSeletivo(), ResultadoOpcaoCurso.SEGUNDA_OPCAO);
		suplentesSemArgumentoMinimoAprovacao = dao.findSuplentesSemArgumentoMinimoAprovacao(obj.getProcessoSeletivo());
		// restringe o percentual de vagas o limite d o candidato convocado para segunda op��o.
		// com isto, n�o gera a expectativa de preconvoc�-lo para primeira op��o e n�o ser realmente chamado
		List<ConvocacaoProcessoSeletivoDiscente> convocadosSegundaOpcao = dao.findConvocadosSegundaOpcao(obj.getProcessoSeletivo(), idMatrizes);
		if (obj.getPercentualAdicionalVagas() > 0 && !isEmpty(convocadosSegundaOpcao) && !isEmpty(convocadosSegundaOpcao)) {
			for (Integer idMatriz : idMatrizes) {
				if (suplentesPrimeiraOpcao.get(idMatriz) != null) {
					Iterator<ResultadoOpcaoCurso> iterator = suplentesPrimeiraOpcao.get(idMatriz).iterator();
					boolean remove = false;
					while (iterator.hasNext()) {
						ResultadoOpcaoCurso resultado = iterator.next();
						if (remove) 
							iterator.remove();
						else {
							for (ConvocacaoProcessoSeletivoDiscente convocado : convocadosSegundaOpcao) {
								if (convocado.getInscricaoVestibular().getId() == resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getId()) {
									iterator.remove();
									remove = true;
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Verifica se a opera��o pode ser realizada. Uma convoca��o s� pode ser
	 * gerada se a etapa de cadastramento estiver conclu�da, ou seja, n�o pode
	 * haver alunos convocados para o processo seletivo em quest�o ainda com
	 * status {@link StatusDiscente.PENDENTE_CADASTRO}.
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	private void checarPreCondicoes(ConvocacaoVestibularDao dao) throws DAOException {
//		Collection<MatrizCurricular> matrizesComAlunosPendentesCadastro = dao.findMatrizesComAlunosPendentesCadastro(obj.getProcessoSeletivo());
//		
//		if(!isEmpty(matrizesComAlunosPendentesCadastro)) {
//			StringBuilder sb = new StringBuilder();
//			for(MatrizCurricular m: matrizesComAlunosPendentesCadastro){
//				sb.append(m.getDescricao() + "<br/>");
//			}
//			addMensagemErro("N�o deve ser gerada uma convoca��o pois o processo seletivo " +
//					obj.getProcessoSeletivo().getNome() +
//					", ainda possui discente(s) com status PENDENTE CADASTRO nas seguintes matrizes curriculares: <br/>" + sb.toString()
//					+ "Acesse a opera��o de Gerenciar Cadastramento de Discentes para concluir o cadastramento desses alunos.");
//		}
	}

	/**
	 * Preenche as vagas remanescentes buscando os pr�ximos suplentes de acordo
	 * com o tipo de convoca��o.
	 * 
	 * @param dao
	 * @param mapaVagasPorMatriz
	 * @throws DAOException
	 */
	private void popularVagas(TipoConvocacao tipo) throws DAOException {
		if(!hasVagasOciosas())
			return;
		preencheuVaga = false;
		Map<Integer, List<ResultadoOpcaoCurso>> proximosSuplentes = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		
		if (obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_TODOS_SEMESTRES ||
				obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_APENAS_PRIMEIRO_SEMESTRE)
			popularVagasMudancaSemestre();

		switch(tipo) {		
		case CONVOCACAO_PRIMEIRA_OPCAO:
			proximosSuplentes = suplentesPrimeiraOpcao;
			break;
		case CONVOCACAO_TURNO_DISTINTO:
			proximosSuplentes = suplentesTurnoDistinto;
			break;
		case CONVOCACAO_SEGUNDA_OPCAO:
			proximosSuplentes = suplentesSegundaOpcao;
			break;
		case CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO:
			proximosSuplentes = suplentesSemArgumentoMinimoAprovacao;
			break;
		}
		
		Set<Integer> matrizes = new HashSet<Integer>(mapaVagasPorMatriz.keySet());
		Map<Integer, Integer> mapa = new HashMap<Integer, Integer>(mapaVagasPorMatriz);
		for(Integer matriz: matrizes) {
			Integer vagas = mapa.get(matriz);
			if(vagas > 0) {
				List<ResultadoOpcaoCurso> lista = proximosSuplentes.get(matriz);
				
				if(!isEmpty(lista)){
					for (Iterator<ResultadoOpcaoCurso> iterator = lista.iterator(); iterator.hasNext();) {
						ResultadoOpcaoCurso resultadoOpcaoCurso = iterator.next();
						/* Procedimento para evitar que um candidato j� convocado 
						 * durante o processamento atual para um n�vel superior da hierarquia
						 * seja convocado para um n�vel inferior. 
						 */
						int idInscricao = resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular().getId();
						switch(tipo) {		
						case CONVOCACAO_TURNO_DISTINTO:
							if(convocadosPrimeiraOpcao.contains(idInscricao)){
								iterator.remove();								
								continue;
							}
							break;
						case CONVOCACAO_SEGUNDA_OPCAO:
							if(convocadosPrimeiraOpcao.contains(idInscricao)
							|| convocadosTurnoDistinto.contains(idInscricao)){
								iterator.remove();								
								continue;
							}
							break;
						case CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO:
							if(convocadosPrimeiraOpcao.contains(idInscricao)){
								iterator.remove();								
								continue;
							}
							if(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getSituacaoCandidato().equals(SituacaoCandidato.APROVADO)
									&& matriz.equals(resultadoOpcaoCurso.getMatrizCurricular().getId()) && resultadoOpcaoCurso.getOrdemOpcao() == ResultadoOpcaoCurso.SEGUNDA_OPCAO)
								continue;
							
							if (isJaConvocado(matriz, resultadoOpcaoCurso))
									continue;
							
							break;
						}
						
						criarConvocacao(resultadoOpcaoCurso, tipo);
						iterator.remove();
						vagas = mapaVagasPorMatriz.get(matriz);
						mapaVagasPorMatriz.put(matriz, --vagas);
						mapa.put(matriz, vagas);
						if(vagas == 0)
							break;
					}
				}
			}
		}
	}

	/** Indica se o discente j� foi convocado.
	 * @param matriz
	 * @param resultadoOpcaoCurso
	 * @return
	 */
	private boolean isJaConvocado(Integer matriz, ResultadoOpcaoCurso resultadoOpcaoCurso) {
		return resultadoOpcaoCurso.getConvocacaoAnterior() != null 
				&& matriz.equals(resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente().getMatrizCurricular().getId())
				&& mapaSemestreIngressoPorMatriz.get(resultadoOpcaoCurso.getMatrizCurricular().getId()).equals(resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente().getPeriodoIngresso());
	}
	
	/**
	 * Verifica se h� vagas ociosas no primeiro semestre, convocando os
	 * candidatos aprovados no segundo semestre para o primeiro, caso existam.
	 * 
	 * @throws DAOException
	 */
	private void popularVagasMudancaSemestre() throws DAOException {
		if(!hasVagasOciosasPrimeiroSemestre())
			return;
			
		for(Integer matriz: mapaVagasPrimeiroSemestre.keySet()) {
			Integer vagas = mapaVagasPrimeiroSemestre.get(matriz);
			if(vagas > 0) {
				List<ResultadoOpcaoCurso> lista = convocadosSegundoSemestre.get(matriz);
				
				if(!isEmpty(lista)){
					for (Iterator<ResultadoOpcaoCurso> iterator = lista.iterator(); iterator.hasNext();) {
						ResultadoOpcaoCurso resultadoOpcaoCurso = iterator.next();
						criarConvocacao(resultadoOpcaoCurso, TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE);
						iterator.remove();
						mapaVagasPrimeiroSemestre.put(matriz, --vagas);
						if(vagas == 0)
							break;
					}
				}
			}
		}
	}

	/**
	 * Gera uma convoca��o do tipo informado a partir das informa��es contidas
	 * no resultado do candidato.
	 * 
	 * @param resultadoOpcaoCurso
	 * @param tipo
	 * @throws DAOException 
	 */
	private void criarConvocacao(ResultadoOpcaoCurso resultadoOpcaoCurso, TipoConvocacao tipo) throws DAOException {
		ConvocacaoProcessoSeletivoDiscente c = new ConvocacaoProcessoSeletivoDiscente();
		c.setConvocacaoProcessoSeletivo(obj);
		c.setInscricaoVestibular(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular());
		c.setResultado(resultadoOpcaoCurso);
		
		if(tipo.equals(TipoConvocacao.CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO)){
			switch(resultadoOpcaoCurso.getOrdemOpcao()){
			case ResultadoOpcaoCurso.PRIMEIRA_OPCAO:
				tipo = TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO;
				break;
			case ResultadoOpcaoCurso.SEGUNDA_OPCAO:
				tipo = TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO;
				break;
			}
		}
		
		for (Iterator<ConvocacaoProcessoSeletivoDiscente> iterator = convocacoes.iterator(); iterator.hasNext();) {
			ConvocacaoProcessoSeletivoDiscente convocacaoExistente = iterator.next();
			if (convocacaoExistente.getInscricaoVestibular().getNumeroInscricao() == c.getInscricaoVestibular().getNumeroInscricao()) {
				if (tipo == TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO) {
					return;
				} else {
					int idMatriz = convocacaoExistente.getDiscente().getMatrizCurricular().getId();
					Integer vagas = mapaVagasPorMatriz.get(idMatriz);
					if (vagas == null) vagas = 0;
					mapaVagasPorMatriz.put(idMatriz, ++vagas);
					iterator.remove();
				}
				break;
			}
			
		}
		
		c.setTipo(tipo);
		
		Pessoa pessoa = new Pessoa();
		pessoa.setCpf_cnpj(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa().getCpf_cnpj());
		pessoa.setNome(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa().getNome());
		
		DiscenteGraduacao discente = resultadoOpcaoCurso.getConvocacaoAnterior() == null 
									? new DiscenteGraduacao() 
									: copiaDiscente(resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente(), tipo);
		discente.setPessoa(pessoa);
		discente.setAnoIngresso( obj.getProcessoSeletivo().getAnoEntrada() );
		
		// Se a convoca��o � uma mudan�a de semestre, o per�odo de ingresso � sempre o 1� per�odo.
		if(tipo.equals(TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE))
			discente.setPeriodoIngresso(1);
		else
			discente.setPeriodoIngresso( mapaSemestreIngressoPorMatriz.get(resultadoOpcaoCurso.getMatrizCurricular().getId()) );
		
		discente.setMatrizCurricular( resultadoOpcaoCurso.getMatrizCurricular() );
		discente.setCurso( resultadoOpcaoCurso.getMatrizCurricular().getCurso() );
		
		discente.setNivel( NivelEnsino.GRADUACAO );
		discente.setStatus(StatusDiscente.PENDENTE_CADASTRO);
		discente.setTipo(Discente.REGULAR);
		discente.setFormaIngresso(obj.getProcessoSeletivo().getFormaIngresso());
		
		c.setDiscente(discente);
		c.setConvocacaoAnterior(resultadoOpcaoCurso.getConvocacaoAnterior());
		convocacoes.add(c);
		preencheuVaga = true;
		
		switch(tipo) {		
		case CONVOCACAO_PRIMEIRA_OPCAO:
			convocadosPrimeiraOpcao.add(c.getInscricaoVestibular().getId());
			break;
		case CONVOCACAO_TURNO_DISTINTO:
			convocadosTurnoDistinto.add(c.getInscricaoVestibular().getId());
			break;
		case CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO:
			if(resultadoOpcaoCurso.getOrdemOpcao().equals(ResultadoOpcaoCurso.PRIMEIRA_OPCAO))
				convocadosPrimeiraOpcao.add(c.getInscricaoVestibular().getId());	
		}
		
		// Se j� possui uma convoca��o anterior, cria um cancelamento para essa convoca��o anterior.
		// caso j� esteja matriculado, n�o ser� gerado o cancelamento e o v�nculo ser� ativado ap�s o cancelamento da convoca��o anterior
		if(resultadoOpcaoCurso.getConvocacaoAnterior() != null){
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class); 
			Collection<MatriculaComponente> matriculasDiscente = matriculaDao.findByDiscenteOtimizado(resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente(), TipoComponenteCurricular.getAll(), SituacaoMatricula.getSituacoesTodas());
			boolean cancela = true;
			if (!isEmpty(matriculasDiscente)) {
				for (MatriculaComponente mc : matriculasDiscente) {
					if (!SituacaoMatricula.getSituacoesNegativas().contains(mc.getSituacaoMatricula()))
						cancela = false; 
				}
			}
			if (cancela) {
				switch(tipo) {		
				case CONVOCACAO_PRIMEIRA_OPCAO:
					criarCancelamento(resultadoOpcaoCurso, MotivoCancelamentoConvocacao.RECONVOCACAO_PRIMEIRA_OPCAO);
					break;
				case CONVOCACAO_TURNO_DISTINTO:
					criarCancelamento(resultadoOpcaoCurso, MotivoCancelamentoConvocacao.RECONVOCACAO_OUTRO_TURNO);
					break;
				}
			} else {
				c.setPendenteCancelamento(true);
				// como ser� pendente do cancelamento, ser� criado um novo discente, e n�o mant�m o ID do discente da convoca��o anterior
				c.getDiscente().setId(0);
				c.getDiscente().getDiscente().setId(0);
			}
		}
	}
	
	/**
	 * Cria uma c�pia do conte�do do objeto {@link DiscenteGraduacao discente} passado retornando uma nova refer�ncia.
	 * 
	 * @param discente
	 * @return
	 */
	private DiscenteGraduacao copiaDiscente(DiscenteGraduacao discente, TipoConvocacao tipo) {
		DiscenteGraduacao copia = new DiscenteGraduacao(discente.getId(), discente.getMatricula(), discente.getNome());
		
		if(!tipo.equals(TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE)){
			MatrizCurricular matrizCopia = new MatrizCurricular(discente.getMatrizCurricular().getId());
			matrizCopia.setCurso(new Curso(discente.getMatrizCurricular().getCurso().getId()));
			matrizCopia.setTurno(new Turno(discente.getMatrizCurricular().getTurno().getId()));
			matrizCopia.setGrauAcademico(new GrauAcademico(discente.getMatrizCurricular().getGrauAcademico().getId()));
			matrizCopia.setHabilitacao(discente.getMatrizCurricular().getHabilitacao() != null ? new Habilitacao(discente.getMatrizCurricular().getHabilitacao().getId()) : null);
			
			copia.setMatrizCurricular(matrizCopia);
		}
		return copia;
	}

	/**
	 * Gera um cancelamento para a convoca��o do discente informado atrav�s do
	 * resultado com o respectivo motivo do cancelamento.
	 * 
	 * @param idInscricaoVestibular
	 * @param motivo
	 */
	private void criarCancelamento(ResultadoOpcaoCurso resultadoOpcaoCurso, MotivoCancelamentoConvocacao motivo) {
		if(resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente().getStatus() == StatusDiscente.EXCLUIDO)
			return;
		CancelamentoConvocacao c = new CancelamentoConvocacao();
		c.setMotivo(motivo);
		c.setConvocacao(resultadoOpcaoCurso.getConvocacaoAnterior());
		
		// O cancelamento gerado abre uma vaga na matriz da qual o aluno saiu.
		// somente se a convoca��o for para os dois per�odos.
		if (obj.getSemestreConvocacao() != SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE) {
			int idMatriz = resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente().getMatrizCurricular().getId();
			Integer vagas = mapaVagasPorMatriz.get(idMatriz);
			if(vagas == null)
				vagas = 0;
			mapaVagasPorMatriz.put(idMatriz, ++vagas);
		
			/*
			 * Se a matriz na qual abriu-se vaga possui oferta de vagas nos dois
			 * semestres e a convoca��o cancelada foi de um discente que ocupava
			 * vaga no primeiro semestre, incrementa-se o n�mero de vagas
			 * dispon�veis no primeiro semestre para aquela matriz.
			 */
			if (obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_APENAS_PRIMEIRO_SEMESTRE ||
					obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_TODOS_SEMESTRES) {
				Integer vagasPrimeiroSemestre = mapaVagasPrimeiroSemestre.get(idMatriz);
				if(vagasPrimeiroSemestre != null && resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente().getPeriodoIngresso() == 1){
					mapaVagasPrimeiroSemestre.put(idMatriz, ++vagasPrimeiroSemestre);
				}
			}
		}
		cancelamentos.add(c);
	}
	
	/**
	 * M�todo respons�vel por realizar a convoca��o da 1� chamada dos candidatos importados
	 * do processo seletivo, utilizando threads para o processamento da convoca��o e inser��o de 
	 * discente com situa��o de pendentes de cadastro.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao_importacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void cadastrarConvocacoesImportacaoVestibular() throws SegurancaException, ArqException, NegocioException {
		ConvocacaoProcessoSeletivoDao dao = getDAO(ConvocacaoProcessoSeletivoDao.class);
		
		validateRequired( obj.getProcessoSeletivo().getId(), "Processo Seletivo", erros );
		validateRequired( obj.getDescricao(), "Descri��o da Convoca��o", erros );
		validateRequired( obj.getDataConvocacao(), "Data da Convoca��o", erros );
		if( hasErrors() ) redirectMesmaPagina();
		
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoVestibular.class));
		matrizes = dao.findMatrizesConvocacao(obj.getProcessoSeletivo().getId());
		if (isEmpty(matrizes)) {
			erros.addErro( "N�o foram encontradas as Matrizes Curriculares necess�rias para convoca��o dos alunos. Confirme se o Resultado de Classificaca��o dos Candidatos foi importado com sucesso.");
		}
		if( hasErrors() ) {
			matrizes = null;
			redirectMesmaPagina();
		}
		quantidadeProcessado = 0;
		resumoConvocacao = new LinkedHashMap<MatrizCurricular, Integer>();
		errosConvocacao = new HashMap<MatrizCurricular, List<ResultadoPessoaConvocacao>>();
		MatrizCurricular processando = null;
		try {
			for (MatrizCurricular matriz : matrizes) {
				processando = matriz;
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setObjAuxiliar(matriz);
				mov.setCodMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE);
				prepareMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE);
				Map<Integer, List<ResultadoPessoaConvocacao>> resumoMatriz = execute(mov);
				quantidadeProcessado++;
				int quantidade = 0;
				for (Integer i : resumoMatriz.keySet()) {
					quantidade = i;
					if ( ValidatorUtil.isNotEmpty(resumoMatriz.get(i)) )
						errosConvocacao.put(matriz, resumoMatriz.get(i));
				}
				resumoConvocacao.put(matriz, quantidade);
			}
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convoca��o para o curso "
					+ (processando != null ? " " + processando.getDescricaoSemEnfase():"")
					+ ": " + e.getMessage());
			notifyError(e);
			quantidadeProcessado = matrizes.size()+1;
			redirectMesmaPagina();
		} finally {
			quantidadeProcessado = matrizes.size()+1;
		}
		addMensagemInformation("Processamento conclu�do com sucesso.");
//		forward("/graduacao/convocacao_vestibular/fim_processamento.jsp");
	}
	
	/** Cancela o cadastramento dos discentes.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/resumo_convocacao.jsp</li>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao_importacao.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		matrizes = null;
		quantidadeProcessado = 0;
		return super.cancelar();
	}
	
	/**
	 * Invoca o processador para persistir as informa��es da convoca��o.
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/resumo_convocacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId()))
			return null;
		
		// separar o processamento por matriz curricular
		TreeSet<MatrizCurricular> matrizesOrdenadas = new TreeSet<MatrizCurricular>(new Comparator<MatrizCurricular>() {
			@Override
			public int compare(MatrizCurricular o1, MatrizCurricular o2) {
				return o1.getId() - o2.getId();
			}
		});
		
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
			matrizesOrdenadas.add(convocacao.getDiscente().getMatrizCurricular());
		}
		for (CancelamentoConvocacao cancelamento : cancelamentos) {
			matrizesOrdenadas.add(cancelamento.getConvocacao().getDiscente().getMatrizCurricular());
		}
		this.matrizes = CollectionUtils.toList(matrizesOrdenadas);
		MatrizCurricular processando = null;
		try {
			// processa por matriz curricular
			quantidadeProcessado = 0;
			for (MatrizCurricular matriz : this.matrizes) {
				processando = matriz;
				// separa as listas
				List<ConvocacaoProcessoSeletivoDiscente> subListaConvocacaoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
				List<CancelamentoConvocacao> subListaCancelamentos = new ArrayList<CancelamentoConvocacao>();
				for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
					if (convocacao.getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaConvocacaoes.add(convocacao);
					}
				}
				for (CancelamentoConvocacao cancelamento : cancelamentos) {
					if (cancelamento.getConvocacao().getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaCancelamentos.add(cancelamento);
					}
				}
				prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES);
				MovimentoConvocacaoVestibular mov = new MovimentoConvocacaoVestibular();
				mov.setCodMovimento(getUltimoComando());
				mov.setObjMovimentado(obj);
				mov.setConvocacoes(subListaConvocacaoes);
				mov.setCancelamentos(subListaCancelamentos);
				execute(mov);
				quantidadeProcessado++;
			}
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convoca��o para o curso "
					+ (processando != null ? " " + processando.getDescricaoSemEnfase():"")
					+ ": " + e.getMessage());
			notifyError(e);
			quantidadeProcessado = matrizes.size();
			return redirectMesmaPagina();
		} finally {
			quantidadeProcessado = matrizes.size();
			matrizes = null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/**
	 * Checa se h� vagas dispon�veis nas matrizes curriculares.
	 * 
	 * @return
	 */
	private boolean hasVagasOciosas() {
		for(Integer vagas: mapaVagasPorMatriz.values())
			if(vagas > 0)
				return true;
		return false;
	}

	/**
	 * Checa se h� vagas dispon�veis no primeiro semestre das matrizes
	 * curriculares com oferta de vagas nos dois semestres.
	 * 
	 * @return
	 */
	private boolean hasVagasOciosasPrimeiroSemestre() {
		if (mapaVagasPrimeiroSemestre != null) {
			for(Integer vagas: mapaVagasPrimeiroSemestre.values())
				if(vagas > 0)
					return true;
		}
		return false;
	}

	/**
	 * Encaminha para a tela do formul�rio da convoca��o.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String telaFormulario() {
		return forward(JSP_FORM);
	}
	
	/**
	 * Encaminha para a tela de resumo da convoca��o.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String telaResumo() {
		return forward(JSP_RESUMO);
	}
	
	/** Redireciona para a tela de importa��o de convoca��o do vestibular.<br/>M�todo n�o invocado por JSP�s.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String telaConvocacaoImportacaoVestibular() {
		return forward(JSP_CONVOCACAO_IMPORTACAO_VESTIBULAR);
	}
	
	/** Lista de SelectItem de Processos Seletivos do Vestibular.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getProcessoSeletivoVestibularCombo() throws DAOException{
		if (processosCombo == null){
			processosCombo = toSelectItems(getDAO(ConvocacaoVestibularDao.class).findProcessosSeletivos(), "id", "nome");
		}
		return processosCombo;
	}

	public List<CancelamentoConvocacao> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(List<CancelamentoConvocacao> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public List<ConvocacaoProcessoSeletivoDiscente> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(
			List<ConvocacaoProcessoSeletivoDiscente> convocacoes) {
		this.convocacoes = convocacoes;
	}

	public Map<MatrizCurricular, Integer> getResumoConvocacao() {
		return resumoConvocacao;
	}

	public void setResumoConvocacao(Map<MatrizCurricular, Integer> resumoConvocacao) {
		this.resumoConvocacao = resumoConvocacao;
	}

	public Map<MatrizCurricular, List<ResultadoPessoaConvocacao>> getErrosConvocacao() {
		return errosConvocacao;
	}

	public void setErrosConvocacao(Map<MatrizCurricular, List<ResultadoPessoaConvocacao>> errosConvocacao) {
		this.errosConvocacao = errosConvocacao;
	}

	/** Retorna uma cole��o de SelectItem com os op��es de convoca��o por semestre.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public Collection<SelectItem> getSemestresConvocacaoCombo() {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(SemestreConvocacao.CONVOCA_TODOS_SEMESTRES, SemestreConvocacao.CONVOCA_TODOS_SEMESTRES.getDescricao()));
		itens.add(new SelectItem(SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE, SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE.getDescricao()));
		return itens;
	}
	
	/** Retorna o percentual do processamento. 
	 * @return Percentual do processamento. Ao fim do processamento, retorna o valor 101. 
	 */
	public int getPercentualProcessado() {
		if (matrizes != null && matrizes.size() <= quantidadeProcessado)
			return 101;
		if (matrizes != null && matrizes.size() > quantidadeProcessado) {
			int percentual = 100 * (quantidadeProcessado + 1) / matrizes.size();
			return percentual == 0 ? 1 : percentual;
		} else 
			return 0;
	}
	
	/** Retorna uma estimativa do tempo restante para o fim do processamento. 
	 * @return Estimativa do tempo restante para o fim do processamento.
	 */
	public String getMensagemProgresso() {
		if (matrizes != null && matrizes.size() > quantidadeProcessado) {
			StringBuilder msg = new StringBuilder("Processando ")
			.append(quantidadeProcessado + 1)
			.append(" de ")
			.append(matrizes.size())
			.append(" (")
			.append(((List<MatrizCurricular>)matrizes).get(quantidadeProcessado).getDescricaoSemEnfase())
			.append(")");
			return msg.toString();
		} else {
			return null;
		}
	}

}
