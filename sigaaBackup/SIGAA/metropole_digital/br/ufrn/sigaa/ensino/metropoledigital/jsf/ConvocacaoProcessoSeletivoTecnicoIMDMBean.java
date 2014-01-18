package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConvocacaoIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoConvocacaoProcessoSeletivoTecnicoIMD;
import br.ufrn.sigaa.ensino.tecnico.dao.ConvocacaoProcessoSeletivoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.MotivoCancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoPessoaConvocacaoTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;

/**
 * Controlador respons�vel por gerar as convoca��es dos candidatos aprovados no
 * na sele��o de discentes para o IMD.
 * 
 * @author Rafael Barros
 * 
 */
@Component("convocacaoProcessoSeletivoTecnicoIMD") @Scope("session")
public class ConvocacaoProcessoSeletivoTecnicoIMDMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivoTecnico> {

	/** Constante com o endere�o da view do formul�rio. */
	private static final String JSP_FORM = "/metropole_digital/convocacao/form_convocacao.jsp";
	
	/** Constante com o endere�o da view do resumo. */
	private static final String JSP_RESUMO = "/metropole_digital/convocacao/resumo_convocacao.jsp";
	
	/** Constante com o endere�o da view da importa��o dos dados do vestibular. */
	private static final String JSP_CONVOCACAO_IMPORTACAO_VESTIBULAR = "/metropole_digital/convocacao/form_convocacao_importacao.jsp";
	
	/**ID do curso do IMD*/
	public static final int idCursoIMD = 88353771;
	
	/** Lista de novas convoca��es de candidatos. */
	private List<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes;
	
	/** Lista de cancelamentos de convoca��es anteriores gerados por reconvoca��es de candidatos. */
	private List<CancelamentoConvocacaoTecnico> cancelamentos;
	
	/** Lista de processos seletivos para o usu�rio escolher em qual ser� importado os dados. */
	private List<SelectItem> processosCombo;

	/**Quantidade de vagas ociosas com reserva*/
	private int vagasOciosasComReserva;
	
	/**Quantidade de vagas ociosas sem reserva*/
	private int vagasOciosasSemReserva;

	/** Mapa contendo o ID de cada convoca��o de aprovados para preenchimento de vagas. */
	private List<ResultadoClassificacaoCandidatoTecnico> suplentes = new ArrayList<ResultadoClassificacaoCandidatoTecnico>();
	
	/** Lista de IDs de convocados para primeira op��o de curso. */
	private Collection<Integer> convocados = new HashSet<Integer>();
	
	/** Resumo das convoca��es de processo seletivo.. */
	private List <ResultadoClassificacaoCandidatoTecnico> resumoConvocacao;
	
	/** Resumo dos erros de valida��es para as convoca��es de processo seletivo, n�o sendo convocados. */
	private List<ResultadoPessoaConvocacaoTecnico> errosConvocacao;
	
	/**Lista dos grupos de reserva de vagas para o combo box*/
	private Collection<SelectItem> gruposCombo = new ArrayList<SelectItem>();
	
	/**Lista das op��es polo grupo para o combo box*/
	private Collection<SelectItem> polosCombo = new ArrayList<SelectItem>();
	
	/**Objeto que representa a op��o polo grupo selecionada*/
	private OpcaoPoloGrupo opcaoSelecionada = new OpcaoPoloGrupo();
	
	/**Representa a quantidade de vagas que a op��o selecionada possui em um determinado processo seletivo*/
	private Long qtdVagasOpcaoSelecionada = (long) 0;
	
	/**Representa a quantidade de convocados que a op��o selecionada possui em um determinado processo seletivo*/
	private Long qtdConvocadosOpcaoSelecionada = (long) 0;

	/**Objeto que representa o PS selecionado para a convoca��o*/
	private ProcessoSeletivoTecnico psSelecionado = new ProcessoSeletivoTecnico();
	
	/**Atributo que armazena a quantidade de suplentes de um determinado p�lo*/
	private Long qtdSuplentePolo = (long) 0;
	
	/**Atributo que armazena a quantidade de geral de vagas dispon�veis*/
	private Long qtdDisponiveis = (long) 0;
	
	/**Atributo que armazena a quantidade de geral de excluidos ou desistentes*/
	private Long qtdExcluidos = (long) 0;
	
	/** Comparador utilizado para ordenar as convoca��es de discentes em um processo seletivo. */
	private Comparator<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacaoDiscenteComparator = new Comparator<ConvocacaoProcessoSeletivoDiscenteTecnico>(){
		@Override
		public int compare(ConvocacaoProcessoSeletivoDiscenteTecnico o1, ConvocacaoProcessoSeletivoDiscenteTecnico o2) {
			return o1.getInscricaoProcessoSeletivo().getPessoa().getNome().compareTo(o2.getInscricaoProcessoSeletivo().getPessoa().getNome());
		}
	};
	
	/**
	 * Construtor padr�o.
	 */
	public ConvocacaoProcessoSeletivoTecnicoIMDMBean() {
		clear();
	}

	/**
	 * Inicializa as informa��es utilizadas em todo o caso de uso.
	 */
	private void clear() {
		obj = new ConvocacaoProcessoSeletivoTecnico ();
		obj.setQuantidadeDiscentesSemReserva(null);
		obj.setOpcao(new OpcaoPoloGrupo());
	}

	/**
	 * Inicializa as informa��es utilizadas no processamento da convoca��o.
	 */
	private void clearProcessamento() {
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
		cancelamentos = new ArrayList<CancelamentoConvocacaoTecnico>();
		
		suplentes = new ArrayList<ResultadoClassificacaoCandidatoTecnico>();
		
		convocados = new HashSet<Integer>();
		
		vagasOciosasComReserva = 0;
		vagasOciosasSemReserva = 0;
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
		checkRole(SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD);
		clear();
		prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES);
		setOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES.getId());
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
	public String iniciarConvocacaoImportacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD);
		clear();
		return forward("/metropole_digital/convocacao/form_inicial.jsp");
	}
	
	/** Voltar o form para a tela inicial
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/form_convocacao_importacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String voltarFormInicial() throws SegurancaException {
		return forward("/metropole_digital/convocacao/form_inicial.jsp");
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
		
		ConvocacaoProcessoSeletivoTecnicoDao dao = getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class);
		
		ValidatorUtil.validateRequiredId(obj.getProcessoSeletivo().getId(), "Processo Seletivo Vestibular", erros);
		ValidatorUtil.validateRequired(obj.getDescricao(), "Descri��o", erros);
		validateMinValue(obj.getQuantidadeDiscentesComReserva(), 0, "Quantidade de discentes com reserva de vaga", erros);
		validateMinValue(obj.getQuantidadeDiscentesComReserva(), 0, "Quantidade de discentes sem reserva de vaga", erros);
		dao.initialize(obj.getProcessoSeletivo());
		
		if(hasErrors())
			return null;
		
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoTecnico.class));
		//vagasOciosas = dao.findVagasOciosas(obj.getProcessoSeletivo());
		//vagasOciosas += obj.getPercentualAdicionalVagas() * vagasOciosas / 100;
		
		//if(vagasOciosas > 0){
			carregarSuplentes(dao);
			popularVagas();
		//} else {
		//	addMensagemWarning("Todas vagas ofertadas para o Processo Seletivo "+obj.getProcessoSeletivo().getNome()+" foram preenchidas.");
		//	return null;
		//}
		
		Collections.sort(convocacoes, convocacaoDiscenteComparator);
		return telaResumo();
	}

	/** Carrega a lista de candidatos suplentes.
	 * @param dao
	 * @param idMatrizes 
	 * @throws DAOException
	 */
	private void carregarSuplentes (ConvocacaoProcessoSeletivoTecnicoDao dao) throws DAOException {
		suplentes = dao.findSuplentesByPoloGrupo(obj.getProcessoSeletivo(), obj.getOpcao());
	}

	/**
	 * Preenche as vagas remanescentes buscando os pr�ximos suplentes.
	 * 
	 * @param dao
	 * @param vagasOciosas
	 * @throws DAOException
	 */
	private void popularVagas () throws DAOException {
		if (!isEmpty(suplentes)){
			for (Iterator <ResultadoClassificacaoCandidatoTecnico> iterator = suplentes.iterator(); iterator.hasNext();) {
				ResultadoClassificacaoCandidatoTecnico resultadoClassificacaoCandidatoTecnico = iterator.next();

				if (resultadoClassificacaoCandidatoTecnico.getInscricaoProcessoSeletivo().isReservaVagas())
					vagasOciosasComReserva --;
				else
					vagasOciosasSemReserva --;
				
				criarConvocacao(resultadoClassificacaoCandidatoTecnico);
				iterator.remove();
				
				if(vagasOciosasComReserva + vagasOciosasSemReserva == 0)
					break;
			}
		}
	}

	/**
	 * Gera uma convoca��o do tipo informado a partir das informa��es contidas
	 * no resultado do candidato.
	 * 
	 * @param ResultadoClassificacaoCandidatoTecnico
	 * @param tipo
	 * @throws DAOException 
	 */
	private void criarConvocacao (ResultadoClassificacaoCandidatoTecnico resultadoClassificacaoCandidatoTecnico) throws DAOException {
		ConvocacaoProcessoSeletivoDiscenteTecnico c = new ConvocacaoProcessoSeletivoDiscenteTecnico();
		c.setConvocacaoProcessoSeletivo(obj);
		c.setInscricaoProcessoSeletivo(resultadoClassificacaoCandidatoTecnico.getInscricaoProcessoSeletivo());
		c.setResultado(resultadoClassificacaoCandidatoTecnico);
		
		for (Iterator <ConvocacaoProcessoSeletivoDiscenteTecnico> iterator = convocacoes.iterator(); iterator.hasNext();) {
			ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoExistente = iterator.next();
			if (convocacaoExistente.getInscricaoProcessoSeletivo().getNumeroInscricao() == c.getInscricaoProcessoSeletivo().getNumeroInscricao()) {
				if (convocacaoExistente.getInscricaoProcessoSeletivo().isReservaVagas())
					vagasOciosasComReserva ++;
				else
					vagasOciosasSemReserva ++;
				
				iterator.remove();
				break;
			}
		}
		
		Pessoa pessoa = new Pessoa();
		pessoa.setCpf_cnpj(resultadoClassificacaoCandidatoTecnico.getInscricaoProcessoSeletivo().getPessoa().getCpf_cnpj());
		pessoa.setNome(resultadoClassificacaoCandidatoTecnico.getInscricaoProcessoSeletivo().getPessoa().getNome());
		
		DiscenteTecnico discente = resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior() == null ? new DiscenteTecnico () : copiaDiscente(resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior().getDiscente());
		discente.setPessoa(pessoa);
		discente.setAnoIngresso( obj.getProcessoSeletivo().getAnoEntrada() );
		
		discente.setPeriodoIngresso(1);
		
		discente.setCurso(new Curso(88353771));
		
		discente.setNivel( NivelEnsino.TECNICO );
		discente.setStatus(StatusDiscente.PENDENTE_CADASTRO);
		discente.setTipo(Discente.REGULAR);
		discente.setFormaIngresso(obj.getProcessoSeletivo().getFormaIngresso());
		
		c.setDiscente(discente);
		c.setConvocacaoAnterior(resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior());
		convocacoes.add(c);
		
		convocados.add(c.getInscricaoProcessoSeletivo().getId());
		
		if (resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior() != null){
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class); 
			Collection <MatriculaComponente> matriculasDiscente = matriculaDao.findByDiscenteOtimizado(resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior().getDiscente(), TipoComponenteCurricular.getAll(), SituacaoMatricula.getSituacoesTodas());
			boolean cancela = true;
			if (!isEmpty(matriculasDiscente)) {
				for (MatriculaComponente mc : matriculasDiscente) {
					if (!SituacaoMatricula.getSituacoesNegativas().contains(mc.getSituacaoMatricula()))
						cancela = false; 
				}
			}
			
			if (cancela)
				criarCancelamento (resultadoClassificacaoCandidatoTecnico, MotivoCancelamentoConvocacaoTecnico.RECONVOCACAO_PRIMEIRA_OPCAO);
			else {
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
	private DiscenteTecnico copiaDiscente(DiscenteTecnico discente) {
		DiscenteTecnico copia = new DiscenteTecnico(discente.getId(), discente.getMatricula(), discente.getNome(), discente.getStatus());
		copia.setCurso(new Curso(discente.getCurso().getId()));
			
		return copia;
	}

	/**
	 * Gera um cancelamento para a convoca��o do discente informado atrav�s do
	 * resultado com o respectivo motivo do cancelamento.
	 * 
	 * @param idInscricaoVestibular
	 * @param motivo
	 */
	private void criarCancelamento (ResultadoClassificacaoCandidatoTecnico resultadoClassificacaoCandidatoTecnico, MotivoCancelamentoConvocacaoTecnico motivo) {
		if (resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior().getDiscente().getStatus() == StatusDiscente.EXCLUIDO)
			return;
		
		CancelamentoConvocacaoTecnico c = new CancelamentoConvocacaoTecnico ();
		c.setMotivo(motivo);
		c.setConvocacao(resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior());
		
		// O cancelamento gerado abre uma vaga.
		if (resultadoClassificacaoCandidatoTecnico.getInscricaoProcessoSeletivo().isReservaVagas())
			vagasOciosasComReserva ++;
		else
			vagasOciosasSemReserva ++;
	
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
	@SuppressWarnings("unchecked")
	public String cadastrarConvocacoesImportacaoTecnico () throws SegurancaException, ArqException, NegocioException {
		ConvocacaoProcessoSeletivoTecnicoDao dao = getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class);
		
		if (obj.getProcessoSeletivo().getId() == 0) {
			erros.addErro("Selecione um Processo Seletivo: ");
		}
		
		if (obj.getOpcao().getId() == 0) {
			erros.addErro("Selecione uma Op��o - P�lo - Grupo");
		}
		
		
		
		validateRequired( obj.getDescricao(), "Descri��o da Convoca��o", erros );
		
		if( hasErrors() ) {
			return forward("/metropole_digital/convocacao/form_convocacao_importacao.jsp");
		}
		
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoTecnico.class));

		if( hasErrors() ) {
			return forward("/metropole_digital/convocacao/form_convocacao_importacao.jsp");
		}

		resumoConvocacao = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
		errosConvocacao = new ArrayList <ResultadoPessoaConvocacaoTecnico> ();
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO_IMD);
			prepareMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO_IMD);
			Object [] resumo = execute(mov);
			
			if (ValidatorUtil.isNotEmpty(resumo[1])){
				errosConvocacao.add((ResultadoPessoaConvocacaoTecnico) resumo[1]);
			}

			resumoConvocacao = (List<ResultadoClassificacaoCandidatoTecnico>) resumo[0];
			
			// Se convocou algum candidato, envia email notificando. 
			if (resumoConvocacao != null && !resumoConvocacao.isEmpty()){
				List <String> emails = new ArrayList <String> ();
				for (ResultadoClassificacaoCandidatoTecnico r : resumoConvocacao){
					String email = r.getInscricaoProcessoSeletivo().getPessoa().getEmail();
					if (!StringUtils.isEmpty(email)){
						emails.add(email);
					}
				}
				
				if (!emails.isEmpty()){
					String assunto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.ASSUNTO_EMAIL_CONVOCACAO);
					String texto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.TEXTO_EMAIL_CONVOCACAO);
					notificarConvocados(assunto, texto, emails);
				}
				addMensagemInformation("Os discentes que informaram o email no processo seletivo foram notificados sobre a convoca��o");
			}
		} catch (NegocioException e ){
			addMensagens (e.getListaMensagens());
			return redirectMesmaPagina();
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convoca��o: " + e.getMessage());
			notifyError(e);
			return redirectMesmaPagina();
		}
		
		addMensagemInformation("Processamento conclu�do com sucesso.");
		return forward("/metropole_digital/convocacao/fim_processamento.jsp");
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
	@SuppressWarnings("unchecked")
	public String cadastrarConvocacoesImportacaoTecnicoNew () throws SegurancaException, ArqException, NegocioException {
		ConvocacaoProcessoSeletivoTecnicoDao dao = getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class);
		
		if (obj.getProcessoSeletivo().getId() == 0) {
			erros.addErro("Processo Seletivo: Campo obrigat�rio n�o informado.");
		}
		
		if (obj.getOpcao().getId() == 0) {
			erros.addErro("P�lo / Grupo: Campo obrigat�rio n�o informado.");
		}
		
		if (obj.getQuantidadeDiscentesSemReserva() == null || obj.getQuantidadeDiscentesSemReserva() == 0) {
			obj.setQuantidadeDiscentesSemReserva(null);
			erros.addErro("Quantidade de discentes a serem convocados: Campo obrigat�rio n�o informado.");
		}
		
		
		validateRequired( obj.getDescricao(), "Descri��o da Convoca��o", erros );
				
		if( hasErrors() ) {
			return forward("/metropole_digital/convocacao/form_convocacao_importacao.jsp");
		} else {
		
			obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoTecnico.class));
	
			resumoConvocacao = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
			errosConvocacao = new ArrayList <ResultadoPessoaConvocacaoTecnico> ();
			try {
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO_IMD);
				prepareMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO_IMD);
				Object [] resumo = execute(mov);
				
				if (ValidatorUtil.isNotEmpty(resumo[1])){
					errosConvocacao.add((ResultadoPessoaConvocacaoTecnico) resumo[1]);
				}
	
				resumoConvocacao = (List<ResultadoClassificacaoCandidatoTecnico>) resumo[0];
				
				// Se convocou algum candidato, envia email notificando. 
				if (resumoConvocacao != null && !resumoConvocacao.isEmpty()){
					List <String> emails = new ArrayList <String> ();
					for (ResultadoClassificacaoCandidatoTecnico r : resumoConvocacao){
						String email = r.getInscricaoProcessoSeletivo().getPessoa().getEmail();
						if (!StringUtils.isEmpty(email)){
							emails.add(email);
						}
					}
					
					if (!emails.isEmpty()){
						String assunto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.ASSUNTO_EMAIL_CONVOCACAO);
						String texto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.TEXTO_EMAIL_CONVOCACAO);
						notificarConvocados(assunto, texto, emails);
					}
					addMensagemInformation("Os discentes que informaram o e-mail no processo seletivo foram notificados sobre a convoca��o.");
				}
			} catch (NegocioException e ){
				addMensagens (e.getListaMensagens());
				return redirectMesmaPagina();
			} catch (Exception e) {
				addMensagemErro("Houve um erro durante o processamento da convoca��o: " + e.getMessage());
				notifyError(e);
				return redirectMesmaPagina();
			}
			
			addMensagemInformation("Processamento conclu�do com sucesso.");
			return forward("/metropole_digital/convocacao/fim_processamento.jsp");
		}
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
		if (!checkOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES.getId()))
			return null;
		
		try {
			// separa as listas
			List<ConvocacaoProcessoSeletivoDiscenteTecnico> subListaConvocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
			List<CancelamentoConvocacaoTecnico> subListaCancelamentos = new ArrayList<CancelamentoConvocacaoTecnico>();

			subListaConvocacoes.addAll(convocacoes);
			subListaCancelamentos.addAll(cancelamentos);
					
			prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES);
			MovimentoConvocacaoProcessoSeletivoTecnicoIMD mov = new MovimentoConvocacaoProcessoSeletivoTecnicoIMD ();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obj);
			mov.setConvocacoes(subListaConvocacoes);
			mov.setCancelamentos(subListaCancelamentos);
			execute(mov);
			
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convoca��o: " + e.getMessage());
			notifyError(e);
			return redirectMesmaPagina();
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
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
	public List<SelectItem> getProcessosCombo() throws DAOException{
		if (processosCombo == null){
			processosCombo = toSelectItems(getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class).findProcessosSeletivos(), "id", "nome");
		}
		return processosCombo;
	}

	public List<CancelamentoConvocacaoTecnico> getCancelamentos() {
		return cancelamentos;
	}

	public void setCancelamentos(List<CancelamentoConvocacaoTecnico> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public List<ConvocacaoProcessoSeletivoDiscenteTecnico> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(List <ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes) {
		this.convocacoes = convocacoes;
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
		return 0;
	}
	
	/** Retorna uma estimativa do tempo restante para o fim do processamento. 
	 * @return Estimativa do tempo restante para o fim do processamento.
	 */
	public String getMensagemProgresso() {
		return "Mensagem de progresso";
	}

	public void setProcessosCombo(List<SelectItem> processosCombo) {
		this.processosCombo = processosCombo;
	}

	public int getVagasOciosasComReserva() {
		return vagasOciosasComReserva;
	}

	public void setVagasOciosasComReserva(int vagasOciosasComReserva) {
		this.vagasOciosasComReserva = vagasOciosasComReserva;
	}

	public int getVagasOciosasSemReserva() {
		return vagasOciosasSemReserva;
	}

	public void setVagasOciosasSemReserva(int vagasOciosasSemReserva) {
		this.vagasOciosasSemReserva = vagasOciosasSemReserva;
	}

	public List<ResultadoClassificacaoCandidatoTecnico> getSuplentes() {
		return suplentes;
	}

	public void setSuplentes(List<ResultadoClassificacaoCandidatoTecnico> suplentes) {
		this.suplentes = suplentes;
	}

	public Collection<Integer> getConvocados() {
		return convocados;
	}

	public void setConvocados(Collection<Integer> convocados) {
		this.convocados = convocados;
	}

	public List<ResultadoClassificacaoCandidatoTecnico> getResumoConvocacao() {
		return resumoConvocacao;
	}

	public void setResumoConvocacao(
			List<ResultadoClassificacaoCandidatoTecnico> resumoConvocacao) {
		this.resumoConvocacao = resumoConvocacao;
	}

	public List<ResultadoPessoaConvocacaoTecnico> getErrosConvocacao() {
		return errosConvocacao;
	}

	public void setErrosConvocacao(
			List<ResultadoPessoaConvocacaoTecnico> errosConvocacao) {
		this.errosConvocacao = errosConvocacao;
	}

	public Comparator<ConvocacaoProcessoSeletivoDiscenteTecnico> getConvocacaoDiscenteComparator() {
		return convocacaoDiscenteComparator;
	}

	public void setConvocacaoDiscenteComparator(
			Comparator<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacaoDiscenteComparator) {
		this.convocacaoDiscenteComparator = convocacaoDiscenteComparator;
	}
	
	public OpcaoPoloGrupo getOpcaoSelecionada() {
		return opcaoSelecionada;
	}

	public void setOpcaoSelecionada(OpcaoPoloGrupo opcaoSelecionada) {
		this.opcaoSelecionada = opcaoSelecionada;
	}

	public Long getQtdVagasOpcaoSelecionada() {
		return qtdVagasOpcaoSelecionada;
	}

	public void setQtdVagasOpcaoSelecionada(Long qtdVagasOpcaoSelecionada) {
		this.qtdVagasOpcaoSelecionada = qtdVagasOpcaoSelecionada;
	}

	public Long getQtdConvocadosOpcaoSelecionada() {
		return qtdConvocadosOpcaoSelecionada;
	}

	public void setQtdConvocadosOpcaoSelecionada(Long qtdConvocadosOpcaoSelecionada) {
		this.qtdConvocadosOpcaoSelecionada = qtdConvocadosOpcaoSelecionada;
	}
	
	public ProcessoSeletivoTecnico getPsSelecionado() {
		return psSelecionado;
	}

	public void setPsSelecionado(ProcessoSeletivoTecnico psSelecionado) {
		this.psSelecionado = psSelecionado;
	}
	
	public Long getQtdSuplentePolo() {
		return qtdSuplentePolo;
	}

	public void setQtdSuplentePolo(Long qtdSuplentePolo) {
		this.qtdSuplentePolo = qtdSuplentePolo;
	}
	
	public Long getQtdDisponiveis() {
		return qtdDisponiveis;
	}

	public void setQtdDisponiveis(Long qtdDisponiveis) {
		this.qtdDisponiveis = qtdDisponiveis;
	}

	public Long getQtdExcluidos() {
		return qtdExcluidos;
	}

	public void setQtdExcluidos(Long qtdExcluidos) {
		this.qtdExcluidos = qtdExcluidos;
	}

	/**
	 * M�todo GET para o atributo gruposCombo. Tamb�m efetua o preenchimento do atributo caso esteja vazio
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGruposCombo() throws DAOException {
		if(gruposCombo.isEmpty()) {
			gruposCombo = toSelectItems(getGenericDAO().findAll(ReservaVagaGrupo.class), "id", "denominacao");
		}
		return gruposCombo;
	}

	public void setGruposCombo(Collection<SelectItem> gruposCombo) {
		this.gruposCombo = gruposCombo;
	}
	
	/**
	 * M�todo GET para o atributo polosCombo. Tamb�m efetua o preenchimento do atributo caso esteja vazio
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPolosCombo() throws DAOException {
		if(polosCombo.isEmpty()) {
			polosCombo = toSelectItems(getGenericDAO().findAll(OpcaoPoloGrupo.class), "id", "descricao");
		}
		return polosCombo;
	}

	public void setPolosCombo(Collection<SelectItem> polosCombo) {
		this.polosCombo = polosCombo;
	}

	protected void notificarConvocados (String assunto, String texto, List <String> emails) {
		if (!isEmpty(emails)) {
			for (String email : emails) {
				MailBody body = new MailBody();

				if (!StringUtils.isEmpty(email)) {
					body.setAssunto(assunto);
					body.setMensagem((texto));
					body.setFromName("SIGAA - IMD");
					body.setContentType(MailBody.HTML);
					body.setEmail(email);
					
					Mail.send(body);
				}
			}
		}
	}
	
	// AJAX:
	/**
	 * Carrega as convoca��es que fazem parte do processo seletivo selecionado
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/convocacao/form_cadastramento.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void carregarVagas(ValueChangeEvent e) throws DAOException {

		
		ConvocacaoIMDDao dao = null;

		try {
			if(obj.getProcessoSeletivo().getId() > 0 && obj.getOpcao().getId() > 0) {
				dao = getDAO(ConvocacaoIMDDao.class);
				qtdVagasOpcaoSelecionada = dao.findTotalVagasReservaVagaPS(obj.getProcessoSeletivo().getId(), obj.getOpcao().getId());
				qtdConvocadosOpcaoSelecionada = (qtdVagasOpcaoSelecionada) - (dao.findTotalRemanescentePoloByPSAndPolo(obj.getProcessoSeletivo().getId(), obj.getOpcao().getId(), StatusDiscente.EXCLUIDO));
			}


		} finally {
			if (dao != null) {
				dao.close();
			}
		}

	}
	
	/**
	 * Carrega os dados relacionados as vagas para a convoca��o
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/metropole_digital/convocacao/form_cadastramento.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public String buscarDadosVagas() throws DAOException{
		ConvocacaoIMDDao dao = new ConvocacaoIMDDao();
		
		try {
			if (obj.getProcessoSeletivo().getId() == 0) {
				erros.addErro("Processo Seletivo: Campo obrigat�rio n�o informado.");
			}
			
			if (obj.getOpcao().getId() == 0) {
				erros.addErro("P�lo / Grupo: Campo obrigat�rio n�o informado.");
			}
			
			validateRequired( obj.getDescricao(), "Descri��o da Convoca��o", erros );
			
			if( hasErrors() ) {
				return forward("/metropole_digital/convocacao/form_inicial.jsp");
			} else {
				
				opcaoSelecionada = (OpcaoPoloGrupo) getGenericDAO().findByPrimaryKey(obj.getOpcao().getId(), OpcaoPoloGrupo.class);
				psSelecionado = (ProcessoSeletivoTecnico) getGenericDAO().findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoTecnico.class);
				
				qtdVagasOpcaoSelecionada = dao.findTotalVagasReservaVagaPS(obj.getProcessoSeletivo().getId(), opcaoSelecionada.getPolo().getId());
				
				if(qtdVagasOpcaoSelecionada != null) {
					
					Long qtdVagasRemanescentes = dao.findTotalRemanescentePoloByPSAndPolo(obj.getProcessoSeletivo().getId(), opcaoSelecionada.getPolo().getId(), StatusDiscente.EXCLUIDO);
					qtdConvocadosOpcaoSelecionada = dao.findTotalConvocadosByPSAndPolo(obj.getProcessoSeletivo().getId(), opcaoSelecionada.getPolo().getId());
					qtdExcluidos = dao.findTotalExcluidosByPSAndPolo(obj.getProcessoSeletivo().getId(), opcaoSelecionada.getPolo().getId());
				
					//qtdSuplentePolo = dao.findTotalSuplentesByPSAndPolo(psSelecionado.getId(), opcaoSelecionada.getPolo().getId());
					
					qtdSuplentePolo = dao.findTotalSuplentesOpcaoByPSAndOpcao(psSelecionado.getId(), opcaoSelecionada.getId());
					
					qtdDisponiveis = (qtdVagasOpcaoSelecionada - qtdConvocadosOpcaoSelecionada) + qtdExcluidos;
					//Long qtdSuplenteOpcao = dao.findTotalSuplentesOpcaoByPSAndOpcao(psSelecionado.getId(), opcaoSelecionada.getId());
					
					if(qtdSuplentePolo > 0) {
						if(qtdDisponiveis <= 0) {
							addMensagemWarning("A op��o - p�lo - grupo selecionada n�o possui nenhuma vaga dispon�vel.");
							qtdDisponiveis = (long) 0;
						}
						
						return forward("/metropole_digital/convocacao/form_convocacao_importacao.jsp");
					} else {
						addMensagemErro("N�o h� nenhum candidato suplente para os crit�rios informados.");
						return forward("/metropole_digital/convocacao/form_inicial.jsp");
					}
				}
				else {
					addMensagemErro("O processo seletivo n�o possui nenhum grupo de reserva de vagas cadastrado.");
					return forward("/metropole_digital/convocacao/form_inicial.jsp");
				}
			}
			
		} finally {
			dao.close();
		}
		
		
	}
}
