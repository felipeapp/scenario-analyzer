/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

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
import br.ufrn.sigaa.ensino.tecnico.negocio.MovimentoConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;
/**
 * Controlador responsável por gerar as convocações dos candidatos aprovados no
 * vestibular para as vagas remanescentes.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 * 
 */
@Component("convocacaoProcessoSeletivoTecnico") @Scope("session")
public class ConvocacaoProcessoSeletivoTecnicoMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivoTecnico> {

	/** Constante com o endereço da view do formulário. */
	private static final String JSP_FORM = "/tecnico/convocacao/form_convocacao.jsp";
	
	/** Constante com o endereço da view do resumo. */
	private static final String JSP_RESUMO = "/tecnico/convocacao/resumo_convocacao.jsp";
	
	/** Constante com o endereço da view da importação dos dados do vestibular. */
	private static final String JSP_CONVOCACAO_IMPORTACAO_VESTIBULAR = "/tecnico/convocacao/form_convocacao_importacao.jsp";
	
	/** ID do curso do IMD*/
	public static final int idCursoIMD = 88353771;
	
	/** Lista de novas convocações de candidatos. */
	private List<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes;
	
	/** Lista de cancelamentos de convocações anteriores gerados por reconvocações de candidatos. */
	private List<CancelamentoConvocacaoTecnico> cancelamentos;
	
	/** Lista de processos seletivos para o usuário escolher em qual será importado os dados. */
	private List<SelectItem> processosCombo;

	/** Quantidade de vagas ociosas com reserva */
	private int vagasOciosasComReserva;
	
	/** Quantidade de vagas ociosas sem reserva */
	private int vagasOciosasSemReserva;

	/** Mapa contendo o ID de cada convocação de aprovados para preenchimento de vagas. */
	private List<ResultadoClassificacaoCandidatoTecnico> suplentes = new ArrayList<ResultadoClassificacaoCandidatoTecnico>();
	
	/** Lista de IDs de convocados para primeira opção de curso. */
	private Collection<Integer> convocados = new HashSet<Integer>();
	
	/** Resumo das convocações de processo seletivo.. */
	private List <ResultadoClassificacaoCandidatoTecnico> resumoConvocacao;
	
	/** Resumo dos erros de validações para as convocações de processo seletivo, não sendo convocados. */
	private List<ResultadoPessoaConvocacaoTecnico> errosConvocacao;

	/** Comparador utilizado para ordenar as convocações de discentes em um processo seletivo. */
	private Comparator<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacaoDiscenteComparator = new Comparator<ConvocacaoProcessoSeletivoDiscenteTecnico>(){
		@Override
		public int compare(ConvocacaoProcessoSeletivoDiscenteTecnico o1, ConvocacaoProcessoSeletivoDiscenteTecnico o2) {
			return o1.getInscricaoProcessoSeletivo().getPessoa().getNome().compareTo(o2.getInscricaoProcessoSeletivo().getPessoa().getNome());
		}
	};
	
	/**
	 * Construtor padrão.
	 */
	public ConvocacaoProcessoSeletivoTecnicoMBean() {
		clear();
	}

	/**
	 * Inicializa as informações utilizadas em todo o caso de uso.
	 */
	private void clear() {
		obj = new ConvocacaoProcessoSeletivoTecnico ();
		obj.setOpcao(new OpcaoPoloGrupo());
	}

	/**
	 * Inicializa as informações utilizadas no processamento da convocação.
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
	 * Popula as informações necessárias e inicia o caso de uso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO);
		clear();
		prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES);
		setOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES.getId());
		return telaFormulario();
	}
	
	/** Inicia a importação de convocações para o vestibular.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menu_servidor.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarConvocacaoImportacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO);
		clear();
		return telaConvocacaoImportacaoVestibular();
	}

	/**
	 * Procura vagas ociosas e as informações dos próximos classificados para
	 * preenchê-las. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		ValidatorUtil.validateRequired(obj.getDescricao(), "Descrição", erros);
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
	
	public List <SelectItem> getPolosCombo () {
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			return toSelectItems(dao.findAll(OpcaoPoloGrupo.class), "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Preenche as vagas remanescentes buscando os próximos suplentes.
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
	 * Gera uma convocação do tipo informado a partir das informações contidas
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
		
		DiscenteTecnico discente = resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior() == null 
									? new DiscenteTecnico () 
									: copiaDiscente(resultadoClassificacaoCandidatoTecnico.getConvocacaoAnterior().getDiscente());
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
				// como será pendente do cancelamento, será criado um novo discente, e não mantêm o ID do discente da convocação anterior
				c.getDiscente().setId(0);
				c.getDiscente().getDiscente().setId(0);
			}
		}
	}
	
	/**
	 * Cria uma cópia do conteúdo do objeto {@link DiscenteGraduacao discente} passado retornando uma nova referência.
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
	 * Gera um cancelamento para a convocação do discente informado através do
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
	 * Método responsável por realizar a convocação da 1ª chamada dos candidatos importados
	 * do processo seletivo, utilizando threads para o processamento da convocação e inserção de 
	 * discente com situação de pendentes de cadastro.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		
		if (obj.getProcessoSeletivo().getId() == 0) erros.addErro("Selecione um Processo Seletivo");
		validateRequired( obj.getDescricao(), "Descrição da Convocação", erros );
		if( hasErrors() ) return forward("/tecnico/convocacao/form_convocacao_importacao.jsp");
		
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoTecnico.class));

		if( hasErrors() )
			return forward("/tecnico/convocacao/form_convocacao_importacao.jsp");

		resumoConvocacao = new ArrayList <ResultadoClassificacaoCandidatoTecnico> ();
		errosConvocacao = new ArrayList <ResultadoPessoaConvocacaoTecnico> ();
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO);
			prepareMovimento(SigaaListaComando.CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO);
			Object [] resumo = execute(mov);
			
			if (ValidatorUtil.isNotEmpty(resumo[1]))
				errosConvocacao.add((ResultadoPessoaConvocacaoTecnico) resumo[1]);

			resumoConvocacao = (List<ResultadoClassificacaoCandidatoTecnico>) resumo[0];
			
			// Se convocou algum candidato, envia email notificando. 
			if (resumoConvocacao != null && !resumoConvocacao.isEmpty()){
				List <String> emails = new ArrayList <String> ();
				for (ResultadoClassificacaoCandidatoTecnico r : resumoConvocacao){
					String email = r.getInscricaoProcessoSeletivo().getPessoa().getEmail();
					if (!StringUtils.isEmpty(email))
						emails.add(email);
				}
				
				if (!emails.isEmpty()){
					String assunto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.ASSUNTO_EMAIL_CONVOCACAO);
					String texto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.TEXTO_EMAIL_CONVOCACAO);
					notificarConvocados(assunto, texto, emails);
				}
				addMensagemInformation("Os discentes que informaram o email no processo seletivo foram notificados sobre a convocação");
			}
		} catch (NegocioException e ){
			addMensagens (e.getListaMensagens());
			return redirectMesmaPagina();
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convocação: " + e.getMessage());
			notifyError(e);
			return redirectMesmaPagina();
		}
		
		addMensagemInformation("Processamento concluído com sucesso.");
		return forward("/tecnico/convocacao/fim_processamento.jsp");
	}
	
	/** Cancela o cadastramento dos discentes.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Invoca o processador para persistir as informações da convocação.
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			MovimentoConvocacaoProcessoSeletivoTecnico mov = new MovimentoConvocacaoProcessoSeletivoTecnico ();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obj);
			mov.setConvocacoes(subListaConvocacoes);
			mov.setCancelamentos(subListaCancelamentos);
			execute(mov);
			
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convocação: " + e.getMessage());
			notifyError(e);
			return redirectMesmaPagina();
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/**
	 * Encaminha para a tela do formulário da convocação.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaFormulario() {
		return forward(JSP_FORM);
	}
	
	/**
	 * Encaminha para a tela de resumo da convocação.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaResumo() {
		return forward(JSP_RESUMO);
	}
	
	/** Redireciona para a tela de importação de convocação do vestibular.<br/>Método não invocado por JSP´s.
	 * <br/>Método não invocado por JSP´s.
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

	/** Retorna uma coleção de SelectItem com os opções de convocação por semestre.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
}
