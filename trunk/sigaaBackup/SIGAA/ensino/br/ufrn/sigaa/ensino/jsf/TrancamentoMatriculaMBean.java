/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on May 29, 2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.Logger;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.TurmaDAO;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamento;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.negocio.TrancamentoMatriculaValidator;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.TrancamentoMatriculaMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosEAD;

/**
 * Managed Bean para o trancamento de matrículas
 * em componentes curriculares.
 *
 * @author Victor Hugo
 * @author David Pereira
 */
@Component("trancamentoMatricula") @Scope("session")
public class TrancamentoMatriculaMBean extends SigaaAbstractController<SolicitacaoTrancamentoMatricula> {

	/** Caminho da JSP do formulário de solicitação */
	private static final String FORM_SOLICITACAO = "/ensino/trancamento_matricula/solicitacao.jsp";
	/** Caminho da JSP do Comprovante de Solicitação */
	private static final String COMPROVANTE_SOLICITACAO = "/ensino/trancamento_matricula/comprovante_solicitacao.jsp";
	/** Caminho da JSP do Resumo da Solicitação */
	private static final String RESUMO_SOLICITACAO = "/ensino/trancamento_matricula/resumo_solicitacao.jsp";
	/** Caminho da JSP dos trancamentos de matriculas */
	private static final String MEUS_TRANCAMENTOS = "/ensino/trancamento_matricula/meus_trancamentos.jsp";
	/** Caminho da JSP do Resumo do Cancelamento */
	private static final String RESUMO_CANCELAMENTO = "/ensino/trancamento_matricula/resumo_cancelamento.jsp";

	/** Discente selecionado */
	private DiscenteAdapter discente;

	/** Solicitações de trancamentos de matricula */
	private Collection<SolicitacaoTrancamentoMatricula> solicitacoes;

	/**
	 * Utilizada na jsp de meus trancamentos para definir se
	 * as solicitações de trancamento podem ser canceladas.
	 * Se já tiver ultrapassado o prazo de trancamento as
	 * solicitações não podem ser canceladas.
	 */
	private boolean periodoTrancamentoTurmas = false;

	/**
	 * Todas as matrículas do discente escolhido
	 */
	private List<MatriculaComponente> matriculas;

	/**
	 * Matrículas escolhidas a serem alteradas
	 */
	private List<SolicitacaoTrancamentoMatricula> matriculasEscolhidas;

	/** Campo auxiliar utilizado para checar se o usuário já confirmou a solicitação */
	private boolean solicitado;

	/** Número da solicitação de trancamento gerada após a submissão do mesmo */
	private Integer numeroSolicitacao;

	/** Disciplinas selecionadas para trancar */
	private String[] disciplinasMarcadas;
	/** Indica se é a distância ou não */
	private boolean aDistancia;

	public TrancamentoMatriculaMBean() {
		obj = new SolicitacaoTrancamentoMatricula();
	}

	/**
	 * Prepara a iniciação do caso de uso de solicitar trancamento
	 * de matrícula realizado pelo DISCENTE
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String popularSolicitacao() throws NegocioException, ArqException {
		discente = getDiscenteUsuario();
		return dadosDiscente();
	}

	/**
	 * Prepara a iniciação do caso de uso de solicitar trancamento
	 * de matrícula realizado por OUTRAS PESSOAS E NÃO O DISCENTE.
	 * O discente aqui será injetado por outro managed bean.
	 * <br/><br/>
	 * Método não chamado por JSPs.
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String popularSolicitacaoDiscenteInjetado() throws NegocioException, ArqException {
		return dadosDiscente();
	}

	/**
	 * Realiza verificações em discente para identificar se ele está apto
	 * a ter matrículas trancadas.
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private String dadosDiscente() throws NegocioException, ArqException {
		if( discente == null ){
			addMensagemErro("Você não pode realizar esta operação.");
			return null;
		}

		if( !discente.isGraduacao() && !discente.isStricto()){
			addMensagemErro("Discentes do nível " + discente.getNivelDesc() +" não estão autorizados a efetuar trancamentos on-line.");
			return null;
		}

		if( discente.getCurso() != null && discente.getCurso().isADistancia() && !aDistancia && !isPermiteDiscenteTrancarComponenteEAD() ){
			addMensagemErro("Procure seu tutor para realizar o trancamento das disciplinas que deseja.");
			return null;
		}

		// Verifica se o trancamento está sendo feito dentro do prazo
		// fora do prazo, o trancamento só poderá ser feito para módulos.
		if ((getCalendarioVigente().getInicioTrancamentoTurma()!=null && getCalendarioVigente().getFimTrancamentoTurma()!=null) && !getCalendarioVigente().isPeriodoTrancamentoTurmas() 
				&& !aDistancia && !discente.isStricto()) {
			//addMensagemErro("Não é permitido o trancamento da matrícula fora do período definido no calendário universitário. depois de decorridos 2/3 (dois terços) do período letivo.");
			Formatador fmt = Formatador.getInstance();
			addMensagemWarning(" O período oficial para trancamento da matrícula estende-se de " +
					fmt.formatarData(getCalendarioVigente().getInicioTrancamentoTurma()) + " a " + fmt.formatarData(getCalendarioVigente().getFimTrancamentoTurma()) + ".");
		}

		//Validar se o discente está ativo
		if (discente.getStatus() != StatusDiscente.ATIVO  && discente.getStatus() != StatusDiscente.FORMANDO) {
			addMensagemErro("Somente Discentes com status ATIVO ou FORMANDO podem ter a matrícula em disciplinas trancada.");
			return null;
		}

		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		CalendarioAcademico cal = getCalendarioVigente();

		try {
			matriculas = (List<MatriculaComponente>) matriculaDao.findByDiscenteComTurmaParaTrancamento(discente, cal.getAno(), cal.getPeriodo(), SituacaoMatricula.MATRICULADO);
			//matriculas = (List<MatriculaComponente>) matriculaDao.findByDiscenteComTurma(discente, null, null, SituacaoMatricula.MATRICULADO);

			solicitacoes = dao.findByDiscente(discente.getId(), SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);

			for ( MatriculaComponente mc : matriculas ) {
				int idSituacao = mc.getSituacaoMatricula().getId();
				mc.setSituacaoMatricula(SituacaoMatricula.getSituacao(idSituacao));

				for ( SolicitacaoTrancamentoMatricula sol : solicitacoes ) {
					if ( mc.getId() == sol.getMatriculaComponente().getId() ) {
						mc.setSolicitacaoTrancamento(sol);
					}
				}
				
				Date dataLimite = null;
				if (mc.getTurma() != null && mc.getTurma().getDisciplina().isPermiteHorarioFlexivel()) {
					Turma t = dao.refresh(mc.getTurma());
					mc.getTurma().setHorarios(t.getHorarios());
				}
				
				if (discente.isStricto() || (discente.isGraduacao() && mc.getComponente().isModulo())) {
					dataLimite = TrancamentoMatriculaUtil.calcularPrazoLimiteTrancamentoTurma(mc.getTurma(), cal);
				} else {
					CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente);
					Date fimTrancamentoTurma = calendario.getFimTrancamentoTurma();
					
					if ( !isEmpty(fimTrancamentoTurma)) {
						dataLimite = fimTrancamentoTurma;
					}
				}
				
				mc.setDataLimiteTrancamento(dataLimite);
			}


			if (matriculas == null || matriculas.isEmpty()) {
				addMensagemErro("O discente selecionado não encontra-se matriculado em disciplinas");
			}

		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		if( hasOnlyErrors() )
			return null;

		setOperacaoAtiva( SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA.getId(), "O trancamento já foi realizado, se deseja realizar outro trancamento por favor, reinicie os procedimentos." );
		prepareMovimento(SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA);
		solicitado = false;
		return forward(FORM_SOLICITACAO);
	}
	
	
	/**
	 * Valida os componentes selecionados, verifica os co-requisitos matriculados e vai para a tela de resumo
	 * JSPs:
	 * /SIGAA/app/sigaa.ear/sigaa.war/ensino/trancamento_matricula/solicitacao.jsp
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String resumirSolicitacao() throws ArqException, NegocioException{

		try {
		
		if( !checkOperacaoAtiva(SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA.getId()) )
			return cancelar();
		
		// Verificar a existência do discente. Caso que ocorre quando o usuário utiliza o botão voltar
		if ( ValidatorUtil.isEmpty(getDiscente()) ) {
			addMensagemErro("Atenção!! É necessário reiniciar o processo de trancamento (provavelmente você utilizou o botão 'voltar' do navegador).");
			return cancelar();
		}

		String[] linhas = getCurrentRequest().getParameterValues("matriculas");
		if (linhas == null) {
			addMensagemErro("Ao menos uma matrícula deve ser escolhida.");
			return null;
		}
		
		disciplinasMarcadas = linhas;

		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente);
		
		GenericDAO dao = getGenericDAO();
		ArrayList<SolicitacaoTrancamentoMatricula> solicitacoes = new ArrayList<SolicitacaoTrancamentoMatricula>(0);
		ArrayList<MatriculaComponente> matSelecionadas = new ArrayList<MatriculaComponente>();

		// utilizado para fazer o log dos componentes que serão trancados
		StringBuffer logComponentes = new StringBuffer(200);
		
		for (String linha : linhas) {

			int idMatricula = Integer.parseInt(linha);

			SolicitacaoTrancamentoMatricula sol = new SolicitacaoTrancamentoMatricula();
			MatriculaComponente mat = dao.findByPrimaryKey(idMatricula, MatriculaComponente.class);
			mat.setSituacaoMatricula( dao.refresh(mat.getSituacaoMatricula()) );

			logComponentes.append(mat.getComponenteCodigoNome() + "\n");
			
			if ( discente.isStricto() || (discente.isGraduacao() && mat.getComponente().isModulo()) ) {
				
				TrancamentoMatriculaValidator.validarDataLimite(mat.getTurma(), calendario);
				
			} else if ( (getCalendarioVigente().getInicioTrancamentoTurma()!=null && getCalendarioVigente().getFimTrancamentoTurma()!=null) 
					&& !getCalendarioVigente().isPeriodoTrancamentoTurmas() && !aDistancia ) {
				Formatador fmt = Formatador.getInstance();
				addMensagemErro("Não é possível solicitar o trancamento do componente "+mat.getComponenteDescricaoResumida()+": o período oficial para trancamento da matrícula estende-se de "
						+ fmt.formatarData(getCalendarioVigente()
								.getInicioTrancamentoTurma())
						+ " a "
						+ fmt.formatarData(getCalendarioVigente()
								.getFimTrancamentoTurma()) + ".");
			}
			
			
			String just = getParameter("txtMotivo" + idMatricula);

			Integer motivo = getParameterInt("motivo" + idMatricula);
			if( motivo == null || motivo <= 0 ){
				addMensagemErro("É necessário informar o motivo do trancamento de cada uma das disciplinas trancadas.");
				return null;
			}

			sol.setMatriculaComponente(mat);
			sol.setJustificativa(just);
			sol.setMotivo( new MotivoTrancamento(motivo) );

			//matriculas.set(i, mat);
			matSelecionadas.add(mat);
			solicitacoes.add(sol);
			
			
		}
		
		Logger.debug("Tentativa de trancamento do aluno ("  + discente.getMatriculaNome() + ") nos componentes " + logComponentes.toString() );

		/**
		 * Verificando se já foi solicitado trancamento ou trancada
		 * alguma das matrículas selecionadas.
		 */
		TrancamentoMatriculaValidator.validarSolicitacao(getDiscente(), matSelecionadas,
				getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), erros);

		if (hasErrors())
			return null;

		matriculasEscolhidas = solicitacoes;

		/**
		 * Verificando se as matrículas selecionadas possuem co-requisitos para que estes sejam trancados tambem
		 */
		List<MatriculaComponente> listaCorequisitos = TrancamentoMatriculaValidator.verificarCorequisitos(discente, matSelecionadas);
		if( !isEmpty( listaCorequisitos ) ){
			StringBuffer msg = new StringBuffer("As seguintes matrículas também serão trancadas pois são co-requisitos de disciplinas trancadas: \n");
			for( MatriculaComponente mc : listaCorequisitos ){
				mc = dao.refresh(mc);
				SolicitacaoTrancamentoMatricula sol = new SolicitacaoTrancamentoMatricula();
				sol.setMatriculaComponente(mc);
				sol.setMotivo( new MotivoTrancamento(MotivoTrancamento.TRANCOU_COREQUISITO) );
				matriculasEscolhidas.add(sol);
				msg.append( mc.getComponente().getDescricaoResumida() + ";<br>" );
			}
			addMessage(msg.toString(), TipoMensagemUFRN.WARNING);
		}

		/**
		 * Verificando se as matrículas selecionadas são de subunidades de bloco,
		 * se sim o discente tem que trancar todas
		 */
		Collection<MatriculaComponente> listaSubunidades = TrancamentoMatriculaValidator.verificarSubunidades(matSelecionadas);
		if( !isEmpty( listaSubunidades ) ){
			StringBuffer msg = new StringBuffer("As seguintes matrículas também serão trancadas pois são subunidades de blocos: \n");
			for( MatriculaComponente mc : listaSubunidades ){
				mc = dao.refresh(mc);
				SolicitacaoTrancamentoMatricula sol = new SolicitacaoTrancamentoMatricula();
				sol.setMatriculaComponente(mc);
				sol.setMotivo( new MotivoTrancamento(MotivoTrancamento.TRANCOU_BLOCO) );
				matriculasEscolhidas.add(sol);
				msg.append( mc.getComponente().getDescricaoResumida() + ";<br>" );
			}
			addMessage(msg.toString(), TipoMensagemUFRN.WARNING);
		}

		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(getDiscente());
		if (param == null) {
			throw new NegocioException("Os Parâmetros Acadêmicos da sua unidade não foram definidos. Por favor, contate o suporte do sistema.");
		}
		
		/* Verifica se a disciplina é a única ativa, se for não permite o seu trancamento */
		if (!param.isPermiteTrancarTodasDisciplinas()){				
			SolicitacaoTrancamentoMatriculaDao solTrancDao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
			Integer totalMatriculadasDisponiveis = solTrancDao.countSolicitacaoTrancamentoByDiscente(getDiscente().getDiscente(), 
					getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), 
					SituacaoMatricula.getSituacoesMatriculadas(), SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);
			
			if (discente.isStricto()){
				MatriculaComponenteDao matriculaComponenteDao = getDAO(MatriculaComponenteDao.class);
				Collection<MatriculaComponente> renovacoes = matriculaComponenteDao.findRenovadasByDiscenteAnoPeriodo(getDiscente(), 
						getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
				
				if (!ValidatorUtil.isEmpty(renovacoes))
					totalMatriculadasDisponiveis += renovacoes.size();				
			}
			
			if (totalMatriculadasDisponiveis <= matriculasEscolhidas.size()){
				addMensagemErro("Caro discente, o trancamento de sua matrícula nas turmas selecionadas o deixará sem nenhum vínculo com a instituição. " +
						"Caso deseje continuar, será necessário efetuar o trancamento de programa.");
				return null;									
			}
		}		
		
		/**
		 * validação
		 */
		ListaMensagens erros = new ListaMensagens();
		for (MatriculaComponente mat : matSelecionadas ) {
			TrancamentoMatriculaValidator.validar(mat, erros);
		}

		if( validacaoDados(erros) )
			return null;

		setConfirmButton("Confirmar Solicitação");		
		return forward(RESUMO_SOLICITACAO);
		
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}

	}

	/**
	 * Chama o modelo para persistir as solicitações de trancamento selecionadas
	 * JSPs:
	 * /SIGAA/app/sigaa.ear/sigaa.war/ensino/trancamento_matricula/resumo_solicitacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarSolicitacao() throws ArqException{
		
		if( !checkOperacaoAtiva(SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA.getId()) )
			return cancelar();
		
		if( matriculasEscolhidas == null || matriculasEscolhidas.size() <= 0 ){
			addMensagemErro("Ao menos uma matrícula deve ser escolhida.");
			return null;
		}

		if( !aDistancia && !confirmaSenha() )
			return null;

		try {
			TrancamentoMatriculaMov mov = new TrancamentoMatriculaMov();
			mov.setCalendarioAcademicoAtual(getCalendarioVigente());
			mov.setSolicitacoes( matriculasEscolhidas );
			mov.setDiscente( getDiscente() );
			mov.setADistancia(aDistancia);
			mov.setCodMovimento( SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA );
			numeroSolicitacao = (Integer) execute( mov, getCurrentRequest() );
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			return null;
		}

		addMessage("Trancamento(s) solicitado(s) com sucesso!", TipoMensagemUFRN.INFORMATION);
		solicitado = true;
		
		Logger.debug("EFETIVAÇÃO TRANCAMENTO DO ALUNO: [ " + discente.getMatriculaNome() + " ] ID DISCENTE: [ " + discente.getId() + " ]");
		
		return forward(RESUMO_SOLICITACAO);
	}

	/**
	 * inicia o caso de uso de o aluno visualizar os seus trancamentos solicitados
	 * JSPs:
	 * /SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public String iniciarMeusTrancamentos() throws DAOException, NegocioException{
		if( getDiscenteUsuario() == null ) {
			addMensagemErro("Está operação só pode ser realizada por discentes.");
			return null;
		}
		discente = getDiscenteUsuario();
		Date dataLimite = null;		
		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		solicitacoes = dao.findByDiscenteAnoPeriodo(getDiscenteUsuario().getId(),
				getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), null);

		if( solicitacoes == null || solicitacoes.isEmpty() ){
			addMensagemErro("Você não realizou nenhuma solicitação de trancamento.");
			return null;
		}
		TurmaDAO tDAO = getDAO(TurmaDAO.class);
		if (discente.isStricto()){
			periodoTrancamentoTurmas = true;
			for(SolicitacaoTrancamentoMatricula stm :solicitacoes){
				stm.getMatriculaComponente().setTurma(tDAO.findByPrimaryKey(stm.getMatriculaComponente().getTurma().getId(), Turma.class));
				dataLimite = TrancamentoMatriculaUtil.calcularPrazoLimiteTrancamentoTurma(stm.getMatriculaComponente().getTurma(), getCalendarioVigente());
				stm.setPrazoLimeTrancamento(dataLimite);
			}
		}else{
			if( getCalendarioVigente().isPeriodoTrancamentoTurmas() )
				periodoTrancamentoTurmas = true;
			else
				periodoTrancamentoTurmas = false;
		}
		return forward(MEUS_TRANCAMENTOS);
	}

	/**
	 * cancela uma solicitação de trancamento
	 * A solicitação só pode ser cancelada pelo próprio aluno que a fez até no MÁXIMO 7 (sete) dias depois de tê-la feito.
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/trancamento_matricula/meus_trancamentos.jsp
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String resumirCancelamentoSolicitacao() throws ArqException, NegocioException{
		Integer id = getParameterInt("id");

		if( id == null || id == 0 ){
			addMensagemErro("É necessário selecionar uma solicitação de trancamento para cancelá-la.");
			return null;
		}

		Formatador fmt = Formatador.getInstance();
		//Validar se o trancamento está sendo feito dentro do prazo
		if (!discente.isStricto() && !getCalendarioVigente().isPeriodoTrancamentoTurmas()) {
			//addMensagemErro("Não é permitido o trancamento da matrícula depois de decorridos 2/3 (dois terços) do período letivo.");
			addMensagemErro(" O período oficial para trancamento da matrícula estende-se de " +
					fmt.formatarData(getCalendarioVigente().getInicioTrancamentoTurma()) + " a " + fmt.formatarData(getCalendarioVigente().getFimTrancamentoTurma()) + ".");
			return null;
		}
		if(discente.isStricto()){
			SolicitacaoTrancamentoMatricula sol = getDAO(SolicitacaoTrancamentoMatriculaDao.class).findByPrimaryKey(id, SolicitacaoTrancamentoMatricula.class);
			Turma t = getDAO(TurmaDAO.class).findByPrimaryKey(sol.getMatriculaComponente().getTurma().getId(), Turma.class);
			
			Date dataLimite = TrancamentoMatriculaUtil.calcularPrazoLimiteTrancamentoTurma(t, getCalendarioVigente());
			if(!sol.isDentroPrazoTrancamento(dataLimite))
				addMensagemErro("O prazo máximo para trancamento desta turma era " + dataLimite);
		
		}

		solicitacoes = new ArrayList<SolicitacaoTrancamentoMatricula>();

		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		obj = dao.findByPrimaryKey(id, SolicitacaoTrancamentoMatricula.class);

		ListaMensagens erros = new ListaMensagens();
		List<SolicitacaoTrancamentoMatricula> corequisitos = TrancamentoMatriculaValidator.validaCancelamentoSolicitacao(obj, getCalendarioVigente() , erros);

		if( erros != null && !erros.isEmpty() ){
			addMensagens(erros);
			return iniciarMeusTrancamentos();
		}

		solicitacoes.add(obj);

		if( corequisitos != null && !corequisitos.isEmpty() ){
			StringBuilder nomes = new StringBuilder("Ao cancelar o trancamento nesta disciplina, também será cancelado o trancamento daquelas que a possuem como co-requisito listadas a seguir: <br>");
			for( SolicitacaoTrancamentoMatricula stm : corequisitos ){
				nomes.append(stm.getMatriculaComponente().getComponenteDescricaoResumida() + ";<br>");
			}
			addMensagemWarning(nomes.toString());
		}

		if( corequisitos != null && !corequisitos.isEmpty() )
			solicitacoes.addAll(corequisitos);

		List<SolicitacaoTrancamentoMatricula> subunidades = TrancamentoMatriculaValidator.validaCancelamentoBloco(obj, erros);
		if( subunidades != null && !subunidades.isEmpty() )
			solicitacoes.addAll(subunidades);

		if( !isEmpty( erros ) )
			addMensagens(erros);

		setConfirmButton("Cancelar Solicitação");
		prepareMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_TRANCAMENTO);
		return forward(RESUMO_CANCELAMENTO);
	}

	/**
	 * Chama o processador e persiste o cancelamento da solicitação de trancamento de matrícula
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_matricula/resumo_cancelamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String gravarCancelamentoSolicitacao() throws ArqException, NegocioException{

		if( !confirmaSenha() )
			return null;
			

		TrancamentoMatriculaMov mov = new TrancamentoMatriculaMov();
		mov.setCalendarioAcademicoAtual(getCalendarioVigente());
		mov.setCodMovimento( SigaaListaComando.CANCELAR_SOLICITACAO_TRANCAMENTO );
		mov.setSolicitacao(obj);
		if( solicitacoes != null && !solicitacoes.isEmpty() )
			mov.setSolicitacoes( solicitacoes );
		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			e.printStackTrace();
			return null;
		}

		addMessage("Solicitação de trancamento de matrícula cancelado com sucesso!", TipoMensagemUFRN.INFORMATION);
		return iniciarMeusTrancamentos();

	}

	/** 
	 * Redireciona para a tela do comprovante 
	 * da solicitação de trancamento de matrícula 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_matricula/resumo_solicitacao.jsp</li>
	 * </ul>
	 **/
	public String telaComprovante(){
		return forward(COMPROVANTE_SOLICITACAO);
	}

	/** 
	 * Informa se alunos dos cursos de ensino a distância podem realizar o trancamento de matrícula.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 **/
	public boolean isPermiteDiscenteTrancarComponenteEAD(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.PERMITE_TRANCAR_COMPONENTE);
	}
	
	/** 
	 * Informa se alunos podem realizar o trancamento de matrícula.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 **/
	public boolean isPermiteTrancarComponente(){
		
		if (getUsuarioLogado().getDiscenteAtivo() != null && getUsuarioLogado().getDiscenteAtivo().isDiscenteEad() && isPermiteDiscenteTrancarComponenteEAD() )
			return true;
		else if ( getUsuarioLogado().getDiscenteAtivo() != null && (getUsuarioLogado().getDiscenteAtivo().isGraduacao() || getUsuarioLogado().getDiscenteAtivo().isStricto()) && !getUsuarioLogado().getDiscenteAtivo().isDiscenteEad() )
			return true;
		else 
			return false;
	}
	
	
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public List<SolicitacaoTrancamentoMatricula> getMatriculasEscolhidas() {
		return matriculasEscolhidas;
	}

	public void setMatriculasEscolhidas(
			List<SolicitacaoTrancamentoMatricula> matriculasEscolhidas) {
		this.matriculasEscolhidas = matriculasEscolhidas;
	}

	public boolean isSolicitado() {
		return solicitado;
	}

	public void setSolicitado(boolean solicitado) {
		this.solicitado = solicitado;
	}

	public Collection<SolicitacaoTrancamentoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(
			Collection<SolicitacaoTrancamentoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public boolean isPeriodoTrancamentoTurmas() {
		return periodoTrancamentoTurmas;
	}

	public void setPeriodoTrancamentoTurmas(boolean periodoTrancamentoTurmas) {
		this.periodoTrancamentoTurmas = periodoTrancamentoTurmas;
	}

	public String[] getDisciplinasMarcadas() {
		return disciplinasMarcadas;
	}

	public void setDisciplinasMarcadas(String[] disciplinasMarcadas) {
		this.disciplinasMarcadas = disciplinasMarcadas;
	}

	public boolean isTutorEad() {
		return aDistancia;
	}

	public void setTutorEad(boolean tutorEad) {
		aDistancia = tutorEad;
	}

}
