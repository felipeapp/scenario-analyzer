/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 03/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.graduacao.ReservaCursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dao.CalendarioAvaliacaoDao;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.MatriculaExtraordinariaDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;

/**
 * Controlador respons�vel por realizar a matr�cula extraordin�ria de alunos de
 * gradua��o em turmas com vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
@Component("matriculaExtraordinaria") @Scope("session")
public class MatriculaExtraordinariaMBean extends MatriculaGraduacaoMBean {

	/** Define o link para a tela de busca de turmas com vagas remanescentes para a matr�cula extraordin�ria */
	public static final String JSP_MATRICULA_EXTRAORDINARIA = "/graduacao/matricula/extraordinaria/matricula_extraordinaria.jsf";
	/** Define o link para a tela de confirma��o da matr�cula extraordin�ria realizada */
	public static final String JSP_CONFIRMACAO_MATRICULA_EXTRAORDINARIA = "/graduacao/matricula/extraordinaria/confirmacao.jsf";
	/** Atributo necess�rio para informar se a matricula extraordin�ria � para turmas do tipo F�rias.*/
	private boolean ferias = false;
	
	/** Armazena o resultado da busca de turmas */
	private Collection<Turma> resultadoTurmasBuscadas = new ArrayList<Turma>();
	
	/**
	 * Verifica se est� no per�odo de matr�cula extraordin�ria, carrega as
	 * informa��es necess�rias e encaminha o usu�rio para a tela de busca de
	 * turmas com vagas remanescentes.
	 * <br/>
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/matricula/extraordinaria/confirmacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		if(!getCalendarioParaMatricula().isPeriodoMatriculaExtraordinaria()){
			addMensagemErro("N�o est� no per�odo de matr�cula extraordin�ria.");
			return null;
		}
		
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas.");
			return null;
		}
		
		super.clear();
		resultadoTurmasBuscadas = new ArrayList<Turma>();
		ferias = false;
		
		setDiscente( getUsuarioLogado().getDiscenteAtivo() );
		
		if(!getDiscente().isGraduacao() || ( getDiscente().isGraduacao() && (getDiscente().isDiscenteEad()) )){
			addMensagemErro("A matr�cula extraordin�ria � somente para alunos de gradua��o presencial.");
			return null;
		}
		
		if ( getDiscente().getStatus() != StatusDiscente.ATIVO && getDiscente().getStatus() != StatusDiscente.FORMANDO ) {
			addMensagemErro("Apenas alunos ativos podem realizar matr�culas.");
			return null;
		}
		
		/* Regras de obrigatoriedade: <br/>
		 * RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010
		 * Art. 3o
		 * I - tratando-se de discente
		 * a) imposibilidade de se efetuar matricula em disciplina.
		 */
		try {
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(getDiscente().getPessoa().getId());
		} catch (NegocioException ne) {
			addMensagemErro(ne.getMessage());
			return null;
		}
		
		if (getDiscente().isGraduacao() && getDiscente().isRegular()) {
			// S� deixar o discente fazer a matr�cula se ele tiver preenchido a avalia��o institucional
			int ano = getCalendarioParaMatricula().getAnoAnterior();
			int periodo = getCalendarioParaMatricula().getPeriodoAnterior();

			AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class);
			if (discenteDeveriaFazerAvaliacaoInstitucional(ano, periodo)) {
				CalendarioAvaliacao calAvaliacao = AvaliacaoInstitucionalHelper.getCalendarioAvaliacaoAtivo(getDiscente());
				if (calAvaliacao != null && !avaliacaoDao
						.isAvaliacaoFinalizada(getDiscente().getDiscente(), ano, periodo, calAvaliacao.getFormulario().getId())) {
					addMensagemErro("N�o � poss�vel realizar a matr�cula ainda porque voc� n�o preencheu a Avalia��o Institucional " +
						"referente ao per�odo " + ano + "." + periodo);
					return null;
				}
				// turmas com doc�ncia assistida
				// est� no per�odo?
				CalendarioAvaliacaoDao calAvalDao = getDAO(CalendarioAvaliacaoDao.class);
				if (calAvalDao.hasCalendarioAtivo(TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA, getDiscente().isDiscenteEad())) { 
					if (!avaliacaoDao.isDocenciaAssistidaFinalizada(getDiscente().getDiscente(), ano, periodo)) {
						addMensagemErro("N�o � poss�vel realizar a matr�cula ainda porque voc� n�o preencheu a Avalia��o Institucional da Doc�ncia Assistida " +
								"referente ao per�odo " + ano + "." + periodo + ".");
							return null;
					}
				}
			}
		}
		
		prepareMovimento(SigaaListaComando.MATRICULAR_EXTRAORDINARIA);
		
		return forward(JSP_MATRICULA_EXTRAORDINARIA);
	}
	
	/**
	 * Verifica se est� no per�odo de matr�cula extraordin�ria para turmas de f�rias, carrega as
	 * informa��es necess�rias e encaminha o usu�rio para a tela de busca de
	 * turmas com vagas remanescentes.
	 * <br/>
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/matricula/extraordinaria/confirmacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarFerias() throws ArqException{
		
		if(!getCalendarioParaMatricula().isPeriodoMatriculaExtraordinariaFerias()){
			addMensagemErro("N�o est� no per�odo de matr�cula extraordin�ria, para turmas de f�rias. ");
			return null;
		}
		
		if( isPeriodoProcessamento() ){
			addMensagemErro("Nenhuma opera��o de matr�cula � permitida durante o per�odo de processamento de matr�culas.");
			return null;
		}
		
		super.clear();
		resultadoTurmasBuscadas = new ArrayList<Turma>();
		ferias = true;
		
		setDiscente( getUsuarioLogado().getDiscenteAtivo() );
		
		if(!getDiscente().isGraduacao() || (getDiscente().isGraduacao() && (getDiscente().isDiscenteEad() || !getDiscente().isRegular()))){
			addMensagemErro("A matr�cula extraordin�ria � somente para alunos regulares de gradua��o presencial.");
			return null;
		}
		
		if ( getDiscente().getStatus() != StatusDiscente.ATIVO && getDiscente().getStatus() != StatusDiscente.FORMANDO ) {
			addMensagemErro("Apenas alunos ativos podem realizar matr�culas.");
			return null;
		}
		
		/* Regras de obrigatoriedade: <br/>
		 * RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010
		 * Art. 3o
		 * I - tratando-se de discente
		 * a) impossibilidade de se efetuar matricula em disciplina.
		 */
		try {
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(getDiscente().getPessoa().getId());
		} catch (NegocioException ne) {
			addMensagemErro(ne.getMessage());
			return null;
		}
		
		prepareMovimento(SigaaListaComando.MATRICULAR_EXTRAORDINARIA);
		
		return forward(JSP_MATRICULA_EXTRAORDINARIA);
	}

	/**
	 * Busca por qualquer turma aberta de qualquer componente curricular que possua vagas remanescentes.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/matricula/extraordinaria/matricula_extraordinaria.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscarTurmasVagasRemanescentes() throws ArqException {

		ComponenteCurricular cc = getDadosBuscaTurma().getDisciplina();
		String codigo = null;
		String nomeComponente = null;
		String nomeDocente = null;
		String horario = null;
		TipoComponenteCurricular tipo = new TipoComponenteCurricular();
		Unidade unidade = new Unidade();
		Integer ano = null;
		Integer periodo = null;
		if (getBoolDadosBusca()[0])
			codigo = cc.getCodigo().trim();
		if (getBoolDadosBusca()[1])
			nomeComponente = cc.getNome().trim();
		if (getBoolDadosBusca()[2])
			horario = getDadosBuscaTurma().getDescricaoHorario().trim();
		if (getBoolDadosBusca()[3])
			nomeDocente = getDadosBuscaTurma().getNomesDocentes().trim();
		if (getBoolDadosBusca()[4])
			unidade = cc.getUnidade();
		if (getBoolDadosBusca()[5]){
			ano = getDadosBuscaTurma().getAno();
			periodo = getDadosBuscaTurma().getPeriodo();
		}
		

		if (codigo == null && nomeComponente == null && unidade == null && (ano == null || periodo == null)) {
			addMensagemErro("Por favor, escolha algum crit�rio de busca");
		} else {
			MatriculaExtraordinariaDao dao = getDAO(MatriculaExtraordinariaDao.class);
			try {
				if( !getBoolDadosBusca()[5] ){
					ano = isFerias() ? getCalendarioParaMatricula().getAnoFeriasVigente() : getCalendarioParaMatricula().getAno();
					periodo = isFerias() ? getCalendarioParaMatricula().getPeriodoFeriasVigente() : getCalendarioParaMatricula().getPeriodo();
				}

				// Apenas turmas regulares devem ser buscadas
				Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
				if (isFerias())
					tiposTurma.add(Turma.FERIAS);
				else
					tiposTurma.add(Turma.REGULAR);

				if (unidade == null) unidade = new Unidade();
				tipo = new TipoComponenteCurricular();
				resultadoTurmasBuscadas = dao.findAbertasComVagasRemanescentesByComponenteCurricular(null, nomeComponente,
						codigo, unidade.getId(), tipo.getId(), nomeDocente,  horario, null, null,
						ano , periodo, Boolean.TRUE, getNivelEnsino(), getDiscente().isDiscenteEad(), tiposTurma.toArray(new Integer[]{}) );
				if (isEmpty(resultadoTurmasBuscadas)) {
					addMensagemWarning("N�o foram encontradas turmas abertas com vagas remanescentes para os par�metros de busca especificados.");
				} else {
					validarVagasIngressantes(resultadoTurmasBuscadas);
				}
				
			} catch (LimiteResultadosException lre) {
				addMensagemErro("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
			} finally {
				dao.close();
			}
		}
		return null;
	}

	/**
	 * M�todo utilizado para validas as vagas dispon�veis para os aluno ingressantes, que n�o s�o abertas para os demais alunos.
	 * 
	 * @param resultadoTurmasBuscadas
	 * @throws DAOException 
	 */
	private void validarVagasIngressantes(Collection<Turma> resultadoTurmasBuscadas) throws DAOException {
		ReservaCursoDao dao = getDAO(ReservaCursoDao.class);
		MatriculaExtraordinariaDao meDao = getDAO(MatriculaExtraordinariaDao.class);
		List<Integer> idsTurma = new ArrayList<Integer>();
		Collection<ReservaCurso> reservasCurso = new ArrayList<ReservaCurso>();
		Collection<MatriculaComponente> matriculadosTurmas = new ArrayList<MatriculaComponente>();
		
		for (Turma turma : resultadoTurmasBuscadas) {
			idsTurma.add(turma.getId());
		}
		//Relaciona as reserva de vagas, com as respectivas turmas
		reservasCurso.addAll(dao.findByTurmas((List<Integer>) idsTurma));
		for (Turma turma : resultadoTurmasBuscadas) {
			for (ReservaCurso reserva : reservasCurso) {
				if (turma.getId() == reserva.getTurma().getId())
					turma.getReservas().add(reserva);
			}
		}	
		
		// Cole��o das matr�culas ativas para as turmas passadas por par�metro, onde ser�o classificados os 
		//alunos ocupantes das vagas destinadas aos ingressantes.  
		matriculadosTurmas.addAll(meDao.findMatriculadosByTurmas(idsTurma));
		//Mapa para armazenar matriculaComponente, usando o identificador da turma como chave.
		Map<Integer, Collection<MatriculaComponente>> mapTurmaMatriculas = new HashMap<Integer, Collection<MatriculaComponente>>();
		
		//Prepara um mapa das turmas com suas respectivas matriculaComponente.
		for (MatriculaComponente mc : matriculadosTurmas) {
			Turma turma = mc.getTurma();
			if ( !mapTurmaMatriculas.containsKey(turma.getId()) ){
				mapTurmaMatriculas.put(turma.getId(), new ArrayList<MatriculaComponente>());
			}
			mapTurmaMatriculas.get(turma.getId()).add(mc);	
		}
		
		//Cole��o utilizada para armazenar as turmas que ser�o removidas, por n�o haver vagas dispon�veis, 
		//no qual as vagas para ingressantes n�o ser�o consideradas.
		Collection<Turma> turmasRemover = new ArrayList<Turma>();
		
		for (Turma turma : resultadoTurmasBuscadas) {
			int qtdeMatriculaIngressante = 0;
			int qtdeMatriculaDemais = 0;
			int qtdeReservaIngressante = 0;
			for (ReservaCurso reserva : turma.getReservas()) {
				qtdeReservaIngressante += reserva.getVagasReservadasIngressantes(); 
			}
			
			if (mapTurmaMatriculas.containsKey(turma.getId())){
				Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
				matriculas.addAll(mapTurmaMatriculas.get(turma.getId()));
				
				for (MatriculaComponente matricula : matriculas) {
					if (matricula.isSelected()){ //atributo usado para indicar se o aluno foi matriculado pela vaga de ingressante;
						++qtdeMatriculaIngressante; 
					} else {
						++qtdeMatriculaDemais;
					}
				}
			}
			//Tratamento para o caso de todas as vagas de ingressantes ocupadas, e ter alunos ingressantes que ocupam as demais vagas.
			if (qtdeReservaIngressante - qtdeMatriculaIngressante < 0)
				qtdeMatriculaDemais += (qtdeMatriculaIngressante - qtdeReservaIngressante);
			
			if ( turma.getCapacidadeAluno() - (qtdeReservaIngressante + qtdeMatriculaDemais) < 1 ){
				turmasRemover.add(turma);
			} else {
				turma.setQtdVagasDisponiveis(turma.getCapacidadeAluno() - (qtdeReservaIngressante + qtdeMatriculaDemais));
			}
		}
		
		for (Iterator<Turma> iterator = resultadoTurmasBuscadas.iterator(); iterator.hasNext();) {
			Turma t = iterator.next();
			for (Turma tRemover : turmasRemover) {
				if (t.getId() == tRemover.getId())
					iterator.remove();
			}
		}
		
	}

	/**
	 * Invoca o processador para tentar efetuar a matr�cula extraordin�ria na
	 * turma selecionada. <br/>
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li>
	 * /sigaa.war/graduacao/matricula/extraordinaria/matricula_extraordinaria
	 * .jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String selecionarTurma() throws ArqException {
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		Integer idTurma = getParameterInt("idTurma");
		if(idTurma == null){
			addMensagemErro("Ocorreu um problema ao carregar a turma selecionada. Por favor, reinicie a opera��o.");
			return cancelar();
		}
		Turma turma = dao.findByPrimaryKey(idTurma, Turma.class);
		turma.setMatricular(true);
		turma.getHorarios().iterator();
		turma.getDocentesNomes();
		turma.getLocal();
		// evita lazy exception
		if (turma.getDisciplina().getPrograma() != null) turma.getDisciplina().getPrograma().getNumUnidades();
		getTurmas().clear();
		getTurmas().add(turma);
		setSolicitacaoConfirmada(false);
		return forward(JSP_CONFIRMACAO_MATRICULA_EXTRAORDINARIA);
	}

	/**
	 * Confirmar a matr�cula na turma. <br/>
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * <li>
	 * /sigaa.war/graduacao/matricula/extraordinaria/confirmacao.jsp
	 * .jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String confirmar() throws DAOException, ArqException {
		
		if (!confirmaSenha())
			return null;
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		
		MovimentoMatriculaGraduacao movimento = new MovimentoMatriculaGraduacao();
		movimento.setDiscente(dao.findByPK(getDiscente().getId()));
		movimento.setTurmas(getTurmas());
		movimento.setCalendarioAcademicoAtual(getCalendarioParaMatricula());
		movimento.setMatriculaEAD(false);
		movimento.setMatriculaConvenio(false);
		movimento.setMatriculaFerias(false);
		movimento.setCodMovimento(SigaaListaComando.MATRICULAR_EXTRAORDINARIA);

		try {
			execute(movimento);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			getTurmas().clear();
			return null;
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		setSolicitacaoConfirmada(true);
		return forward(JSP_CONFIRMACAO_MATRICULA_EXTRAORDINARIA);
	}
	
	/**
	 * Exibe o atestado de matr�cula.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/graduacao/matricula/extraordinaria/confirmacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	@Override
	public String exibirAtestadoMatricula() throws SegurancaException, DAOException {
		getCurrentSession().setAttribute("atestadoLiberado", getDiscente().getId());
		AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
		atestado.setDiscente(getDiscente());
		atestado.setAtestadoMatriculaFerias(isFerias());
		return atestado.selecionaDiscente();
	}

	public Collection<Turma> getResultadoTurmasBuscadas() {
		return resultadoTurmasBuscadas;
	}

	public void setResultadoTurmasBuscadas(Collection<Turma> resultadoTurmasBuscadas) {
		this.resultadoTurmasBuscadas = resultadoTurmasBuscadas;
	}

	public boolean isFerias() {
		return ferias;
	}

	public void setFerias(boolean ferias) {
		this.ferias = ferias;
	}

}
