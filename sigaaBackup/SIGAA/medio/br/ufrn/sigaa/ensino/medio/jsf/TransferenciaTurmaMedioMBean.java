/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TransferenciaTurmaMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.negocio.MovimentoTransferenciaTurmasMedio;
import br.ufrn.sigaa.ensino.medio.negocio.TransferenciaTurmasMedioValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean para tratar da transferência de alunos entre turmas do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
@Component("transferenciaTurmaMedioMBean")
@Scope("session")
public class TransferenciaTurmaMedioMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	//Definições das Views
	/** JSP de escolha da turma de origem da transferência. */
	public static final String JSP_TURMA_ORIGEM = "/medio/transferencia_turmas_medio/turma_origem.jsp";
	/** JSP de escolha da turma de destino da transferência. */
	public static final String JSP_TURMA_DESTINO = "/medio/transferencia_turmas_medio/turma_destino.jsp";
	/** JSP de escolha de alunos que serão transferidos. */
	public static final String JSP_ALUNOS = "/medio/transferencia_turmas_medio/alunos.jsp";
	/** Comprovante da transferência entre turmas. */
	public static final String JSP_COMPROVANTE = "/medio/transferencia_turmas_medio/comprovante.jsp";
	/** Comprovante de transferência das turmas do discente. */
	public static final String JSP_COMPROVANTE_DISCENTE = "/medio/transferencia_turmas_medio/comprovante_discente.jsp";
	/** JSP com informações do discente e possíveis turmas para transferência. */
	public static final String JSP_TRANSFERENCIA_TURMAS = "/medio/transferencia_turmas_medio/transferencia_turmas_discente.jsp";
	
	/** Turma de origem da transferência. */
	private TurmaSerie turmaSerieOrigem;
	/** Turma destino da transferência. */
	private TurmaSerie turmaSerieDestino;
	/** Resultado da busca das turmas de origem. */
	private Collection<TurmaSerie> turmasOrigem;
	/** Resultado da busca das turmas de destino. */
	private Collection<TurmaSerie> turmasDestino;
	/** Entidade da série de ensino médio para busca de turmas. */
	private Serie serie;
	/** Componentes listados para a seleção. */
	private Collection<SelectItem> series;
	/** Marca se se a transferência é automática ou manual. */
	private boolean automatica;

	// Campos utilizados para a transferência automática
	/** Quantidade de alunos matriculados a serem transferidos. */
	private Integer qtdMatriculas;
	/** Número de solicitações a transferir. */
	private Integer qtdSolicitacoes;

	// Coleções utilizadas para a transferência manual
	/** Conjunto de matrículas da turma de origem selecionada. */
	private Collection<MatriculaDiscenteSerie> matriculas = new ArrayList<MatriculaDiscenteSerie>();
	/** Conjunto de solicitações de matrículas não analisadas para a turma de origem selecionada. */
	private Collection<SolicitacaoMatricula> solicitacoes = new ArrayList<SolicitacaoMatricula>();
	
	/** Ano da turmaSerie de origem. */
	private Integer ano;
	/** Discente alvo da transferência. */
	private DiscenteMedio discente;
	/** Lista em dataModel das turmas de Origem e Destino para a transferência individual de turmas. */
	private ListDataModel turmasDataModel;
	
	
	public TransferenciaTurmaMedioMBean() {
		initObj();
	}

	/**
	 * Limpa dados do MBean
	 */
	private void initObj() {
		clearTurmaOrigem();
		turmaSerieDestino = new TurmaSerie();
		
		turmasOrigem = new ArrayList<TurmaSerie>();
		turmasDestino = new ArrayList<TurmaSerie>();
		
		serie = new Serie();
		serie.setCursoMedio(new CursoMedio());
		series = new ArrayList<SelectItem>();
		qtdMatriculas = 0;
		qtdSolicitacoes = 0;
		ano = null;
		
		matriculas = new ArrayList<MatriculaDiscenteSerie>();
		solicitacoes = new ArrayList<SolicitacaoMatricula>();
		
	}
	
	/**
	 * Método responsável por limpar o objeto da turma de origem.
	 * */
	private void clearTurmaOrigem() {
		turmaSerieOrigem = new TurmaSerie();
	}

	/**
	 * Método responsável por setar os atributos iniciais e iniciar a operação de transferência de turmas. 
	 * @throws DAOException
	 */
	private void iniciar() throws DAOException {
		initObj();
		ano = getCalendarioVigente().getAno();
	}
	
	/**
	 * Inicia caso de uso de transferência automática
	 * <br>
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/menus/aluno.jsp</li> 
	 *  <li>/sigaa.war/medio/menus/turma.jsp</li> 
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAutomatica() throws ArqException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO });

		setAutomatica(true);
		iniciar();
		
		prepararMovimento(automatica);
		return forward(JSP_TURMA_ORIGEM);
	}
	
	/**
	 * Inicia a transferência manual
	 * <br />
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/medio/menus/aluno.jsp</li> 
	 *  <li>/sigaa.war/medio/menus/turma.jsp</li>  
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarManual() throws ArqException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO });
		
		setAutomatica(false);
		iniciar();
		
		prepararMovimento(automatica);
		return forward(JSP_TURMA_ORIGEM);
	}
	
	/**
	 * Método responsável por iniciar e preparar os movimentos da transferência de turmas para o processador.
	 * @param automatica
	 * @throws ArqException
	 */
	private void prepararMovimento(boolean automatica) throws ArqException {
		if (automatica) {
			prepareMovimento(SigaaListaComando.TRANSFERENCIA_AUTOMATICA_MEDIO);
			setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_AUTOMATICA_MEDIO.getId());
		} else {
			prepareMovimento(SigaaListaComando.TRANSFERENCIA_MANUAL_MEDIO);
			setOperacaoAtiva(SigaaListaComando.TRANSFERENCIA_MANUAL_MEDIO.getId());
		}
	}
	
	/**
	 * Buscar todas as turmas abertas para o componente curricular selecionado
	 * no ano-período definido.
	 * <br />
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/transferencia_turmas_medio/turma_origem.jsp</li>
	 * </ul>
	 *
	 * @param evt
	 */
	public void buscarTurmasSerie(ActionEvent evt) throws DAOException, LimiteResultadosException {
		if (isEmpty(ano)) addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (hasErrors()) return;
		
		// Buscar as turma disponibilizadas para a série e ano informados
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		turmasOrigem = dao.findByCursoSerieAno(serie.getCursoMedio(), 
					serie, ano, false, getNivelEnsino(), false);
		
		if (ValidatorUtil.isEmpty(turmasOrigem)) {
			addMensagemWarning("Não foram encontradas turmas para o período corrente.");
		}
	}	
		
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/matriculaMedio/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		if( e != null && (Integer)e.getNewValue() > 0 )
			serie.setCursoMedio( dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class) );
		else {
			series = new ArrayList<SelectItem>(0);
			return;
		}	
		serie.getCursoMedio().setNivel(getNivelEnsino());
		if (serie.getCursoMedio() != null)
			series = toSelectItems(dao.findByCurso(serie.getCursoMedio()), "id", "descricaoCompleta");
	}
	
	/**
	 * Seleciona a turma de origem, realiza as validações necessárias
	 * e busca as turmas de destino candidatas.
	 * <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/transferencia_turmas_medio/turma_origem.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String selecionarTurmaOrigem() {
		TransferenciaTurmaMedioDao dao = getDAO(TransferenciaTurmaMedioDao.class);
		
		try {
			// Buscar dados da turma de origem
			turmaSerieOrigem = dao.findByPrimaryKey(getParameterInt("id", 0), TurmaSerie.class);
			turmaSerieOrigem.setQtdMatriculados(getDAO(TurmaSerieDao.class).findQtdeAlunosByTurma(turmaSerieOrigem));

			if (!automatica) {
				matriculas = getDAO(TurmaSerieDao.class).findParticipantesByTurma(turmaSerieOrigem.getId());
			}
			
			// Realizar validações
			TransferenciaTurmasMedioValidator.validaTurmaSerieOrigem(turmaSerieOrigem, erros);
			if (hasErrors()){
				addMensagens(erros);
				return null;
			}
			
			// Buscar turmas de destino candidatas
			turmasDestino = dao.findDestinosTransferencia(turmaSerieOrigem);
			if (turmasDestino == null || turmasDestino.isEmpty()) {
				addMensagemErro("Não há turmas de destino disponíveis para efetuar a transferência.");
				return null;
			}			
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		return forward(JSP_TURMA_DESTINO);
	}

	/**
	 * Seleciona a turma de destino, realiza as validações necessárias 
	 * e redireciona para a tela de definição dos alunos a serem transferidos
	 * <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/transferencia_turmas_medio/turma_destino.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarTurmaDestino() {
		TransferenciaTurmaMedioDao dao = getDAO(TransferenciaTurmaMedioDao.class);
		
		try {
			// Buscar dados da turma de origem
			turmaSerieDestino = dao.findByPrimaryKey(getParameterInt("id", 0), TurmaSerie.class);
			turmaSerieDestino.setQtdMatriculados(getDAO(TurmaSerieDao.class).findQtdeAlunosByTurma(turmaSerieDestino));
			erros = new ListaMensagens();
			TransferenciaTurmasMedioValidator.validaTurmaSerieDestino(turmaSerieOrigem, turmaSerieDestino, erros);
			if (hasErrors()){
				addMensagens(erros);
				return null;
			}

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		
		return forward(JSP_ALUNOS);
	}
	
	/**
	 * Chama o processador para efetivar a transferência entre turmas
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/medio/transferencia_turmas_medio/alunos.jsp</li></ul>
	 * @return
	 * @throws ArqException 
	 */
	public String efetuarTransferencia() throws ArqException {
		if (!checkOperacaoAtiva(getUltimoComando().getId())) {
			return cancelar();
		}

		Collection<MatriculaDiscenteSerie> matriculas = carregaMatriculasSelecionadas();
		Collection<SolicitacaoMatricula> solicitacoes = carregaSolicitacoesSelecionadas();
		
		// Validação dos dados do formulário
		if (isAutomatica()) {
			if (qtdMatriculas == null || qtdMatriculas == 0) {
				addMensagemErro("Favor informar a quantidade de alunos para a transferência entre turmas automática");
			}
			if (qtdSolicitacoes == null) {
				qtdSolicitacoes = 0;
			}
		}
		
		erros = new ListaMensagens();
		TransferenciaTurmasMedioValidator.validaAlunos(isAutomatica(), qtdMatriculas, 
				turmaSerieOrigem, turmaSerieDestino, matriculas, erros);
		if (hasErrors())
			return null;

		// Criação do movimento
		MovimentoTransferenciaTurmasMedio mov = new MovimentoTransferenciaTurmasMedio();

		if (isAutomatica()) {
			mov.setCodMovimento(SigaaListaComando.TRANSFERENCIA_AUTOMATICA_MEDIO);
			mov.setQtdMatriculas(qtdMatriculas);
			mov.setQtdSolicitacoes(qtdSolicitacoes);
		} else {
			mov.setCodMovimento(SigaaListaComando.TRANSFERENCIA_MANUAL_MEDIO);
			mov.setMatriculas( matriculas);
			mov.setSolicitacoes(solicitacoes);
		}
		mov.setTurmaSerieOrigem(turmaSerieOrigem);
		mov.setTurmaSerieDestino(turmaSerieDestino);
	
		// Chamar processador
		List<Discente> discentes = null;
		try {
			discentes  = execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		if (discentes == null || discentes.isEmpty()) {
			//addMensagemErro("Atenção! Não foi possível transferir nenhum discente devido a choque de horários com outras turmas matriculadas.");
			prepararMovimento(isAutomatica());
			return null;
		}
		
		// Preparar dados para o comprovante
		TurmaSerieDao turmaDao = getDAO( TurmaSerieDao.class );
		
		turmaSerieOrigem.setQtdMatriculados(turmaDao.findQtdeAlunosByTurma(turmaSerieOrigem));
		turmaSerieDestino.setQtdMatriculados(turmaDao.findQtdeAlunosByTurma(turmaSerieDestino));
		
		getCurrentRequest().setAttribute("turmaSerieOrigem", turmaSerieOrigem);
		getCurrentRequest().setAttribute("turmaSerieDestino", turmaSerieDestino);
		getCurrentRequest().setAttribute("discentes", discentes);
		addMessage("Transferência realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		addMensagemWarning(" Atenção! Somente os discentes que não possuíam choque de horário com a turma de destino foram transferidos! ");
		
		removeOperacaoAtiva();
		return forward(JSP_COMPROVANTE);
	}
	
	/**
	 * Método responsável por carregar e setar numa coleção de matriculas, as que foram selecionadas pelo usuário.
	 * @return
	 */
	private Collection<MatriculaDiscenteSerie> carregaMatriculasSelecionadas() {
		Collection<MatriculaDiscenteSerie> resultados = new HashSet<MatriculaDiscenteSerie>();
		if (matriculas == null) return resultados;
		
		for (MatriculaDiscenteSerie matricula : matriculas) {
			if (matricula.isSelected())
				resultados.add(matricula);
		}
		return resultados;
	}
	
	/**
	 * Método responsável por carregar e setar numa coleção de solicitações de matricula, as que foram selecionadas pelo usuário.
	 * @return
	 */
	
	private Collection<SolicitacaoMatricula> carregaSolicitacoesSelecionadas() {
		Collection<SolicitacaoMatricula> resultados = new HashSet<SolicitacaoMatricula>();
		if (solicitacoes == null) return resultados;
		
		for (SolicitacaoMatricula solicitacao : solicitacoes) {
			if (solicitacao.isSelected())
				resultados.add(solicitacao);
		}
		return resultados;
	}
	
	/**
	 * String que armazena a página de destino da operação voltar turma de origem.
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/medio/transferencia_turmas_medio/turma_destino.jsp</li></ul>
	 * @return
	 */
	public String voltarTurmaOrigem() {
		return forward(JSP_TURMA_ORIGEM);
	}

	/**
	 * String que armazena a página de destino da operação voltar turma de destino.
	 * <br>
	 * Chamado pela JSP:
	 * <ul><li>/sigaa.war/medio/transferencia_turmas_medio/alunos.jsp</li></ul>
	 * @return
	 */
	public String voltarTurmaDestino() {
		return forward(JSP_TURMA_DESTINO);
	}

	public String getDescricaoTipo() {
		return isAutomatica() ? "(Automática) " : "(Manual) ";
	}
	
	public TurmaSerie getTurmaSerieOrigem() {
		return turmaSerieOrigem;
	}

	public void setTurmaSerieOrigem(TurmaSerie turmaSerieOrigem) {
		this.turmaSerieOrigem = turmaSerieOrigem;
	}

	public TurmaSerie getTurmaSerieDestino() {
		return turmaSerieDestino;
	}

	public void setTurmaSerieDestino(TurmaSerie turmaSerieDestino) {
		this.turmaSerieDestino = turmaSerieDestino;
	}

	public Collection<TurmaSerie> getTurmasOrigem() {
		return turmasOrigem;
	}

	public void setTurmasOrigem(Collection<TurmaSerie> turmasOrigem) {
		this.turmasOrigem = turmasOrigem;
	}

	public Collection<TurmaSerie> getTurmasDestino() {
		return turmasDestino;
	}

	public void setTurmasDestino(Collection<TurmaSerie> turmasDestino) {
		this.turmasDestino = turmasDestino;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public Collection<SelectItem> getSeries() {
		return series;
	}

	public void setSeries(Collection<SelectItem> series) {
		this.series = series;
	}

	public boolean isAutomatica() {
		return automatica;
	}

	public void setAutomatica(boolean automatica) {
		this.automatica = automatica;
	}

	public Integer getQtdMatriculas() {
		return qtdMatriculas;
	}

	public void setQtdMatriculas(Integer qtdMatriculas) {
		this.qtdMatriculas = qtdMatriculas;
	}

	public Integer getQtdSolicitacoes() {
		return qtdSolicitacoes;
	}

	public void setQtdSolicitacoes(Integer qtdSolicitacoes) {
		this.qtdSolicitacoes = qtdSolicitacoes;
	}

	public Collection<MatriculaDiscenteSerie> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaDiscenteSerie> matriculas) {
		this.matriculas = matriculas;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public DiscenteMedio getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteMedio discente) {
		this.discente = discente;
	}

	public ListDataModel getTurmasDataModel() {
		return turmasDataModel;
	}

	public void setTurmasDataModel(ListDataModel turmasDataModel) {
		this.turmasDataModel = turmasDataModel;
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		return null;
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		
	}

}
