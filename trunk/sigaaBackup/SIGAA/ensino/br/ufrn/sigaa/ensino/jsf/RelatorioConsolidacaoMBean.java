/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.dao.ead.FichaAvaliacaoEadDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MediaItem;
import br.ufrn.sigaa.ensino.dominio.MediaItemDiscente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieAnoDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para geração de relatórios de consolidação de turma
 *
 * @author David Pereira
 *
 */
@Component("relatorioConsolidacao")
@Scope("request")
public class RelatorioConsolidacaoMBean extends ConsolidacaoMBean {

	/** Lista contendo os itens programas de uma disciplina. */
	private List<ItemPrograma> listagemItensProgramaPorDisciplina;
	/** {@link DataModel} associado a {@link #listagemItensProgramaPorDisciplina}. */
	private DataModel dataModel;
	/** {@link MetodoAvaliacao} utilizada. */
	private MetodologiaAvaliacao metodologia;
	/** Matrículas associadas a turma. */
	private Collection<MatriculaComponente> matriculas;
	/** Metodologia para casos de matrícula EaD.*/
	private MetodologiaAvaliacao metodologiaEad;
	/** Lista de notas de uma turma. */
	private List <MediaItemDiscente> listaNotas = new ArrayList <MediaItemDiscente> ();
	/**Quantidade de semanas de avaliação existentes na {@link #listaNotas}. */
	private List<Integer> itensAvaliacao;
	/** Indica se todas as matriculas foram carregadas para exibição ao discente.  */
	private boolean todasMatriculasCarregadas = false;
	/**Estrutura das notas para o nivel médio.*/
	public List<RegraNota> regrasMedio;
	
	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(MetodologiaAvaliacao metodologia) {
		this.metodologia = metodologia;
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public List<Integer> getItensAvaliacao() {
		return itensAvaliacao;
	}

	public void setItensAvaliacao(List<Integer> itensAvaliacao) {
		this.itensAvaliacao = itensAvaliacao;
	}

	/**
	 * Inicia a visualização das notas dos alunos como discente.
	 * <br />
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/menu.jsp</li>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * <li>/sigaa.war/portais/turma/menu_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws NegocioException
	 */
	public String notasDiscente() throws ArqException, NegocioException {
		
		try{	
			notas(true);
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}
		
		if (!todasMatriculasCarregadas){
			boolean ok = false;
			if (!isEmpty(getNotas())){
				for (NotaUnidade n : getNotas()){
					if (ok)
						break;
					
					if (!isEmpty(n.getAvaliacoes())) {
						for (Avaliacao a : n.getAvaliacoes())
							if (a.getNota() != null){
								ok = true;
								break;
							}
					} else if (n.getNota() != null)
					    ok = true;
				}
			}
	
			if (!ok && metodoAvaliacao == MetodoAvaliacao.NOTA){
				if(!hasErrors())
					addMensagemWarning("Ainda não foram lançadas notas.");
				return null;
			}
		}	
		
		if (isMedio()) {
			return forward("/ensino/consolidacao/relatorio_medio.jsp");
		}
		
		return forward("/ensino/consolidacao/relatorio.jsp");
	}

	/**
	 * Visualiza as notas dos tutores de uma determinada disciplina.
	 * <br />
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ParseException
	 */
	public String notasTutoresPorDisciplina() throws SegurancaException, DAOException, NegocioException, ParseException {

		DocenteTurmaDao docenteTurmaDao = getDAO(DocenteTurmaDao.class);
		FichaAvaliacaoEadDao fichaAvaliacaoEadDao = getDAO(FichaAvaliacaoEadDao.class);

		if (getParameterInt("idTurma") != null) {
			turma.setId(getParameterInt("idTurma"));
			turma = docenteTurmaDao.refresh(turma);
		}
		
		carregarMetodologia();
		
		if(hasErrors())
			return null;
		
		listaNotas = fichaAvaliacaoEadDao.findByNotasUnidadesPorAluno(turma, metodologia);
		
		if(!isEmpty(listaNotas)) {
			itensAvaliacao = new ArrayList<Integer>();
			List<MediaItem> mediaItens = null;
			int semanaMaxima = 0;
			for (MediaItemDiscente mediaItemDiscente : listaNotas) {
				if(!isEmpty(mediaItemDiscente.getMedias())) {
					mediaItens = mediaItemDiscente.getMedias();
				}
			}
			if(isEmpty(mediaItens)) {
				addMensagemErro("Não existem notas de tutores cadastradas para esta turma.");
				return null;
			}
			for (MediaItem m : mediaItens) {
				if(m.getPeriodoAvaliacao() == 1)
					semanaMaxima++;
			}
			for(int i = 1;i <= semanaMaxima;i++) {
				itensAvaliacao.add(i);
			}
			
			calcularMediasGerais();
			
			if (turma != null) {
				List<DocenteTurma> lista = docenteTurmaDao.findDocentesByTurma(turma);		                           
				turma.setDocentesTurmas(CollectionUtils.toSet(lista));
				return forward("/ead/relatorios/relatorio_notas_tutores.jsp");
			}
		}
		addMensagemErro("Nenhuma avaliação de tutor encontrada.");
		return null;

	}

	/**
	 * Carrega a metodologia do curso através de uma matrícula aleatória na turma.
	 * 
	 * @throws DAOException
	 */
	private void carregarMetodologia() throws DAOException {
		Discente discente = null;
		
		FichaAvaliacaoEadDao fichaAvaliacaoEadDao = getDAO(FichaAvaliacaoEadDao.class);
		
		try {
			Integer matriculaAleatoria = null;
			
			if ( turma.isAgrupadora() )
				matriculaAleatoria = fichaAvaliacaoEadDao.findMatriculaAleatoriaByTurmaAgrupadora(turma);
			else
				matriculaAleatoria = fichaAvaliacaoEadDao.findMatriculaAleatoriaByTurma(turma);

			
			if (matriculaAleatoria != null)
				discente = fichaAvaliacaoEadDao.findByPrimaryKey( matriculaAleatoria, Discente.class, "id", "curso.id");
			else {
				addMensagemErro("Não foi encontrada nenhuma matrícula associada a esta turma.");
				return;
			}
	
			if (discente != null) {
				metodologia = MetodologiaAvaliacaoHelper.getMetodologia(discente.getCurso(), turma.getAno(), turma.getPeriodo());
				metodologiaAvaliacao = metodologia;
			} else {
				addMensagemErro("Nenhum discente encontrado com matrícula na turma!");
			}
		} finally {
			fichaAvaliacaoEadDao.close();
		}
	}

	/**
	 * Realiza o cálculo das médias gerais de cada período de avaliação.
	 * @throws NegocioException 
	 */
	private void calcularMediasGerais() throws NegocioException {
		BigDecimal auxMedia1 = null;
		BigDecimal auxMedia2 = null;
		for (MediaItemDiscente media : listaNotas) {
			auxMedia1 = new BigDecimal(0);
			auxMedia2 = new BigDecimal(0);
			if(!isEmpty(media.getMedias())) {
				for (MediaItem mediaItem : media.getMedias()) {
					if(mediaItem.getPeriodoAvaliacao() == 1) {
						auxMedia1 = auxMedia1.add(mediaItem.getMedia());
					}
					if(mediaItem.getPeriodoAvaliacao() == 2) {
						auxMedia2 = auxMedia2.add(mediaItem.getMedia());
					}
				}
				media.addMediaGeral(auxMedia1.divide(new BigDecimal(itensAvaliacao.size()), 1, RoundingMode.HALF_UP));
				media.addMediaGeral(auxMedia2.divide(new BigDecimal(itensAvaliacao.size()), 1, RoundingMode.HALF_UP));
			}
		}
	}

	/**
	 * Valida a nota passada como parâmetro, retornando o 
	 * valor <code>0.0</code> caso ela seja <code>null</code>.
	 * 
	 * @param nota
	 * @return
	 */
	private BigDecimal validarNotasLancadasTutor(BigDecimal nota) {

		if (nota == null) {
			return new BigDecimal(0.0);
		}
		else {
			return nota;
		}
	}

	/**
	 * Gera o diário de turma contendo todos os alunos.
	 * <br />
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String diarioTurma() throws SegurancaException, DAOException {		
		try{	
			notas(false);
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}		
		return forward("/ensino/consolidacao/relatorioComCapa.jsp");
	}

	/**
	 * Efetua a geração do diário de turma indicando se é para ser visualizado como discente ou não.
	 * 
	 * @param visualizandoComoDiscente
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private void notas(boolean visualizandoComoDiscente) throws NegocioException, ArqException {
		
		AvaliacaoDao aDao = null;
		TurmaDao dao = null;
		DocenteTurmaDao dDao = null;
		FrequenciaAlunoDao fDao = null;
		EADDao eDao = null;
		TurmaSerieDao tDao = null;
		MatriculaComponenteDao mDao = null;
		
		try
		{
			aDao = getDAO(AvaliacaoDao.class);
			dao = getDAO(TurmaDao.class);
			dDao = getDAO(DocenteTurmaDao.class);
			fDao = getDAO(FrequenciaAlunoDao.class);
			eDao = getDAO(EADDao.class);
			mDao = getDAO(MatriculaComponenteDao.class);
			tDao = getDAO(TurmaSerieDao.class);
			
			if (getParameterInt("idTurma") != null) {
				turma.setId(getParameterInt("idTurma"));
				//Em Lato, os parâmetros estão definidos na proposta do curso, logo, pega o curso para poder pegar os parâmetros mais a frente. 
				turma = dao.findAndFetch(turma.getId(), Turma.class, "curso");
			} else {
				TurmaVirtualMBean portalTurma = (TurmaVirtualMBean) getMBean("turmaVirtual");
				turma = portalTurma.getTurma();			
			}
			
			if (turma.isMedio()){
				TurmaSerieAno turmaSerieAno = tDao.findByTurma(turma);
				turma.setTurmaSerie(turmaSerieAno.getTurmaSerie());
			}
		
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
			turma.setDisciplina(getGenericDAO().refresh(turma.getDisciplina()));
			String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);
	
			this.metodoAvaliacao = param.getMetodoAvaliacao();
			carregarMetodologia();
			
			if(hasErrors())
				return;
	
			// Para diminuir as consultas, se for somente o discente visualizando e a turma estiver configurada
			// para que ele só visualize sua nota, filtra as matrículas na primeira consulta.
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			tBean.setTurma(turma);
			ConfiguracoesAva config = tBean.getConfig();
			
			boolean somenteDiscenteLogado = false;
			todasMatriculasCarregadas = false;
			
			if (config != null && !config.isMostrarTodasAsNotas())
				somenteDiscenteLogado = true;

			if (config != null && config.isMostrarMediaDaTurma()){
				matriculas = dao.findMatriculasAConsolidar(turma);
				todasMatriculasCarregadas = true;
			}	
			else if (visualizandoComoDiscente && somenteDiscenteLogado && getDiscenteUsuario() != null){
				matriculas = dao.findMatriculasAConsolidar(turma.getId(), getDiscenteUsuario().getId(), true);
			} else {
				matriculas = dao.findMatriculasAConsolidar(turma);
				todasMatriculasCarregadas = true;
			}
			HashMap<Integer,Integer> faltasAlunos = fDao.findFaltasAlunos(turma,null);
			//TODO: Ordenar por id_unidade e só percorrer até mudar a unidade
		
			List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
			if ( visualizandoComoDiscente && turma.isAgrupadora() )
				avaliacoes = aDao.findAvaliacoesByTurmaAgrupadora(turma);
			else
				avaliacoes = aDao.findAvaliacoes(turma);
	
			Collection<NotaUnidade> notas = aDao.findNotasByTurma(turma);
	
			EstrategiaConsolidacao estrategia = null;
			turma.setMatriculasDisciplina(mDao.findByTurma(turma));
			
			if (turma.getMatriculasDisciplina() == null || turma.getMatriculasDisciplina().isEmpty()) {
				throw new NegocioException("Não há alunos matriculados nessa turma."); 
			}
			
			turma.setCurso(param.getCurso());
			
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			estrategia = factory.getEstrategia(turma,param);
			
			// Setar pesos das notas
			if (!isEmpty(matriculas)) {
				MetodologiaAvaliacao metodologia = MetodologiaAvaliacaoHelper.getMetodologia(matriculas.iterator().next().getDiscente().getCurso(), turma.getAno(), turma.getPeriodo());
				
				for (MatriculaComponente matricula : matriculas) {
					matricula.setMetodoAvaliacao(metodoAvaliacao);
	
					List<NotaUnidade> notasMatricula = new ArrayList<NotaUnidade>();
					for ( NotaUnidade notaMatricula : notas) {
						if ( notaMatricula.getMatricula().getId() == matricula.getId() ) {
							notasMatricula.add(notaMatricula);
						}
					}
	
					if (isEmpty(notasMatricula) && matricula.isEad())
						carregarNotasMatricula(matricula, notasMatricula, metodologia);
	
					matricula.setNotas(notasMatricula);
					matricula.setEstrategia(estrategia);
					matricula.setFaltasCalculadas( faltasAlunos.get(matricula.getDiscente().getId() ) );
					
					// Define os pesos para cálculo da recuperacao
					String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
					matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
					matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));
	
					// TODO: Refatorar colocando para fora do FOR!!
					if (isEad() && !isEstagioEad() && !isLato() && !isTurmaFeriasEad() && metodologia.isPermiteTutor()) {
						
						if (metodologia.getMetodoAvaliacao() == br.ufrn.sigaa.ead.dominio.MetodoAvaliacao.UMA_PROVA) {
							matricula.setNotaTutor(eDao.findNotaTutor(matricula));
						} else {
							int aulasPrimeiraUnidade = metodologia.getNumeroAulasPrimeiraUnidade();
							int aulasSegundaUnidade = metodologia.getNumeroAulasByUnidade(2);
							int[] aulas = {1, aulasPrimeiraUnidade, //intervalo de semanas de avaliação da 1ª unidade
											aulasPrimeiraUnidade + 1, aulasPrimeiraUnidade + aulasSegundaUnidade}; //intervalo de semanas de avaliação da 2ª unidade
							matricula.setNotaTutor(eDao.findNotaTutorByIntervaloAulas(matricula, metodologia, aulas[0], aulas[1]));
							matricula.setNotaTutor2(eDao.findNotaTutorByIntervaloAulas(matricula, metodologia, aulas[2], aulas[3]));
						}
					}
	
					int unidade = 0;
					if (isMedio())
						popularRegrasMedio();
					for (NotaUnidade nota : matricula.getNotas()) {
						if (!isMedio() ) 
							nota.setPeso(pesosAvaliacoes[unidade++].trim());
						ArrayList<Avaliacao> avaliacoesDessaNota = new ArrayList<Avaliacao>();
	
						for ( Avaliacao av : avaliacoes ) {
							if ( av.getUnidade().getId() == nota.getId() ) {
								avaliacoesDessaNota.add(av);
							}
						}
						nota.setAvaliacoes(avaliacoesDessaNota);
					}
				}
			}
	
			List<DocenteTurma> lista = dDao.findDocentesByTurma(turma);
			turma.setDocentesTurmas(CollectionUtils.toSet(lista));
	
			turma.setMatriculasDisciplina(matriculas);
	
			buscarItensProgramaPorDisciplina();
		}finally {
			if (dao != null )
				dao.close();
			if (dDao != null )
				dDao.close();
			if ( mDao != null )
				mDao.close();
			if ( fDao != null)
				fDao.close();
			if ( eDao != null)
				eDao.close();
			if(aDao != null)
				aDao.close();
			if (tDao != null)
				tDao.close();
		}
	}

	/**
	 * Popula a {@link #listagemItensProgramaPorDisciplina} e o {@link #dataModel} de acordo com 
	 * a disciplina associada a turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @throws DAOException
	 */
	public void buscarItensProgramaPorDisciplina() throws DAOException {

		Integer idComponenteCurricular = Integer.valueOf( turma.getDisciplina().getId() );

		//ComponenteCurricularDao componenteCurricularDao = getDAO(ComponenteCurricularDao.class);
		//ComponenteCurricular componenteCurricular = (ComponenteCurricular) componenteCurricularDao.findByPrimaryKeyLock(idComponenteCurricular, ComponenteCurricular.class);

		CursoDao cursoDao = getDAO(CursoDao.class);
		listagemItensProgramaPorDisciplina = cursoDao.findListaItemProgramaByDisciplina(idComponenteCurricular);
		dataModel = new ListDataModel(listagemItensProgramaPorDisciplina);
	}

	/**
	 * Inicia a impressão do comprovante de consolidação da turma.
	 * <br />
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * <li>/sigaa.war/ensino/consolidacao/fim.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String imprimirComprovante() throws SegurancaException, DAOException, NegocioException {

		if (getParameterInt("idTurma") != null) {
			turma.setId(getParameterInt("idTurma"));
		} else {
			TurmaVirtualMBean portalTurma = (TurmaVirtualMBean) getMBean("turmaVirtual");
			turma = portalTurma.getTurma();	
		}
		return escolherTurma();
	}

	/**
	 * Monta e direciona o usuário para o comprovante de consolidação de uma turma 
	 * de acordo com a turma selecionada.
	 * <br />
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/consolidacao/confirmar.jsp</li>
	 * <li>/sigaa.war/ensino/consolidacao/selecionaTurma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String escolherTurma() throws SegurancaException, DAOException, NegocioException {
		TurmaDao dao = getDAO(TurmaDao.class);
		turma = dao.findByPrimaryKey(turma.getId(), Turma.class);

		//		checkParticipanteTurma(turma);

		ParametrosGestoraAcademica param = getParametrosAcademicos();
		if (param == null)
			throw new NegocioException("A sua unidade não está com os parâmetros configurados. Por favor, contacte o suporte do sistema.");

		if (param.getMetodoAvaliacao() == null) {
			throw new NegocioException("Não foi possível determinar o método de avaliação para essa turma. Por favor, reinicie o processo ou contate o suporte.");
		} else {
			this.metodoAvaliacao = param.getMetodoAvaliacao();
		}

		try {
			Collection<MatriculaComponente> matriculas = dao.findMatriculasAConsolidar(turma);

			// Busca alunos matriculados
			if ( turma.isEad() )
				turma.setMatriculasDisciplina(matriculas);
			String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);

			if (param.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && (pesosAvaliacoes == null || pesosAvaliacoes.length == 0)) {
				addMensagemErro("A sua unidade não está com os pesos das avaliações definidos. Por favor, entre em contato com a administração do sistema.");
				return null;
			}

			
			ParametrosGestoraAcademica paramTurma = ParametrosGestoraAcademicaHelper.getParametros(turma);
			String[] pesoMediaPesoRec = paramTurma.getArrayPesosMediaRec();		
			
			if ( turma.getDisciplina().getNivel() != NivelEnsino.STRICTO )
				carregaInfoTurma(turma, matriculas, pesosAvaliacoes, param.getMetodoAvaliacao(),pesoMediaPesoRec);
			else
				carregaInfoTurmaPos(turma, matriculas,pesosAvaliacoes,param.getMetodoAvaliacao(),pesoMediaPesoRec);

		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}

		return forward("/ensino/consolidacao/relatorio.jsp");
	}

	/**
	 * Carrega as notas da matricula passada de acordo com a metodologia definida.
	 * 
	 * @param matricula
	 * @param notas
	 * @throws ArqException 
	 */
	private void carregarNotasMatricula(MatriculaComponente matricula, List<NotaUnidade> notas, MetodologiaAvaliacao metodologiaEad) throws ArqException {
		try {
			if (matricula.isEad()) {
				if (metodologiaEad.isUmaProva()) {
					notas.add(new NotaUnidade(0, (short) 0, null, (byte) 1, matricula.getId()));
				} else {
					notas.add(new NotaUnidade(0, (short) 0, null, (byte) 1, matricula.getId()));
					notas.add(new NotaUnidade(0, (short) 0, null, (byte) 2, matricula.getId()));
				}
			} else {
				Integer numUnidades;
					numUnidades = TurmaUtil.getNumUnidadesDisciplina(matricula.getTurma());
				for (int i = 0; i < numUnidades; i++) {
					notas.add(new NotaUnidade(0, (short) 0, null, (byte) (i+1), matricula.getId()));
				}			
			}
		} catch (NegocioException e) {
			// TODO Auto-generated catch block
			tratamentoErroPadrao(e);
		}
	}

	/**
	 * Carrega as regras de notas para a consolidação do nível médio. 
	 */
	private void popularRegrasMedio() {
		
		RegraNotaDao rdao = getDAO(RegraNotaDao.class);
		TurmaSerieAnoDao sdao = getDAO(TurmaSerieAnoDao.class);
		try {
			TurmaSerieAno tsa = sdao.findByExactField(TurmaSerieAno.class, "turma.id", turma.getId(), true);
			for (MatriculaComponente m:matriculas) {
				m.setSerie(tsa.getTurmaSerie().getSerie());
			}
			regrasMedio = rdao.findByCurso(turma.getCurso() );
		}
		catch (DAOException e){
			
		}
		finally {
			if (rdao != null)
				rdao.close();
			if (sdao != null)
				sdao.close();
		}	
		
	}

	public List<ItemPrograma> getListagemItensProgramaPorDisciplina() {
		return listagemItensProgramaPorDisciplina;
	}


	public void setListagemItensProgramaPorDisciplina(
			List<ItemPrograma> listagemItensProgramaPorDisciplina) {
		this.listagemItensProgramaPorDisciplina = listagemItensProgramaPorDisciplina;
	}

	public MetodologiaAvaliacao getMetodologiaEad() {
		return metodologiaEad;
	}

	public void setMetodologiaEad(MetodologiaAvaliacao metodologiaEad) {
		this.metodologiaEad = metodologiaEad;
	}

	public List<MediaItemDiscente> getListaNotas() {
		return listaNotas;
	}

	public void setListaNotas(List<MediaItemDiscente> listaNotas) {
		this.listaNotas = listaNotas;
	}

	public List<RegraNota> getRegrasMedio() {
		return regrasMedio;
	}

	public void setRegrasMedio(List<RegraNota> regrasMedio) {
		this.regrasMedio = regrasMedio;
	}

}
