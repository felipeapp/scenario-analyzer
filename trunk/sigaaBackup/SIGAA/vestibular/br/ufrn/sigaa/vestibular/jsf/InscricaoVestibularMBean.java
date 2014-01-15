/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/08/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.ConfiguracaoGRUFactory;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.arq.dao.vestibular.AreaConhecimentoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.EscolaInepDao;
import br.ufrn.sigaa.arq.dao.vestibular.IsentoTaxaInscricaoDao;
import br.ufrn.sigaa.arq.dao.vestibular.RestricaoInscricaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;
import br.ufrn.sigaa.vestibular.dominio.EscolaInep;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.IsentoTaxaVestibular;
import br.ufrn.sigaa.vestibular.dominio.LinguaEstrangeira;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.RegiaoPreferencialProva;
import br.ufrn.sigaa.vestibular.dominio.RestricaoInscricaoVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoInscricaoVestibular;

/**
 * Controller responsável pela inscrição no Vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("inscricaoVestibular")
@Scope("session")
public class InscricaoVestibularMBean extends SigaaAbstractController<InscricaoVestibular> {
	/**
	 * O diretório padrão dos fontes para gerar o pdf das GRUs.
	 */
	private static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	
	/** ID da {@link MatrizCurricular} da primeira opção. */
	private int idPrimeiraOpcaoCurso;
	/** ID da {@link MatrizCurricular} da segunda opção. */
	private int idSegundaOpcaoCurso;
	/** Coleção de cursos de primeira opção. */
	private Collection<SelectItem> primeiraOpcaoCombo = new ArrayList<SelectItem>();
	/**
	 * Coleção de cursos de segunda opção. Esses cursos irão depender da
	 * área/cidade do curso de primeira opção.
	 */
	private Collection<SelectItem> segundaOpcaoCombo = new ArrayList<SelectItem>();
	/** ID do município da primeira opção. */
	private int idMunicipioOpcao1;
	/**
	 * ID do município da segunda opção. Esse ID dependerá do ID da primeira
	 * opção.
	 */
	private int idMunicipioOpcao2;
	/** Lista de SelectItem de municípios de primeira opção. */
	private ArrayList<SelectItem> municipiosPrimeiraOpcaoCombo;
	/** Lista de SelectItem de municípios de segunda opção. */
	private ArrayList<SelectItem> municipiosSegundaOpcaoCombo;
	/**
	 * Indica se a língua estrangeira é obrigatória. É o caso de, por exemplo,
	 * quem optar pelo curso de Letras-Francês e tem a obrigatoriedade de realizar
	 * a prova de língua estrangeira em francês.
	 */
	private boolean linguaObrigatoria = false;
	
	/** Valor do botão submit no formulário. */
	private String submitButton = "Próximo Passo >>";
	/** CPF do candidato para consulta de inscrição realizada. */
	private Long cpfConsulta;

	/** Link para o formulário do questionário sócio-econômico. */
	private final String FORM_QUESTIONARIO_SOCIOECONOMICO = "/public/vestibular/inscricao/form_questionario.jsf";
	/** Link para o formulário de opção de curso. */
	private final String FORM_OPCAO_CURSO = "/public/vestibular/inscricao/opcao_curso.jsf";
	/** Link para o formulário de dados pessoais. */
	private final String FORM_CONFIRMACAO = "/public/vestibular/inscricao/confirma.jsf";
	/** Link para o formulário de confirmação de dados da inscrição. */
	private final String FORM_COMPROVANTE_INSCRICAO = "/public/vestibular/inscricao/comprovante.jsf";
	/** Respostas do candidato ao questionário sócio-econômico. */
	private QuestionarioRespostas respostas;
	/** Senha a ser enviada ao candidato. */
	private String senha;
	
	/** Restriução de inscrição exclusiva ao candidato. */
	Collection<RestricaoInscricaoVestibular> restricoesExclusivoA;
	/** Restriução de inscrição exceto ao candidato. */
	Collection<RestricaoInscricaoVestibular> restricoesExcetoA;
	
	/** Matrizes Curriculares ofertadas no processo Seletivo. */
	private Collection<OfertaVagasCurso> vagasOfertadas;
	/** Área de conhecimentos que possuem oferta de vagas no vestibular. */
	private Collection<AreaConhecimentoVestibular> areas;

	/** Construtor padrão. */
	public InscricaoVestibularMBean() {
	}

	/**
	 * Cadastra a inscrição para o vestibular.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/confirma.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,	NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR.getId())) {
			return cancelar();
		}
		verificaInscricaoAberta();
		if (hasErrors()) return null;
		preencherNulos();
		obj.setDataInscricao(new Date());
		MovimentoInscricaoVestibular mov = new MovimentoInscricaoVestibular();
		mov.setInscricaoVestibular(obj);
		mov.setRespostasQuestionario(respostas);
		mov.setSenha(senha);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR);
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		addMensagemInformation("Sua inscrição foi gravada com sucesso.");
		removeOperacaoAtiva();
		return forward(FORM_COMPROVANTE_INSCRICAO);
	}

	/**
	 * Carrega a lista de cursos para escolha da primeira opção.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/form_cursos.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void carregaListaPrimeiraOpcao() throws DAOException {
		int k = 0;
		primeiraOpcaoCombo = new ArrayList<SelectItem>();
		if (idMunicipioOpcao1 == 0) {
			primeiraOpcaoCombo.add(new SelectItem("0",
					"-- SELECIONE UM MUNICÍPO PRIMEIRO --"));
		} else {
			if (areas != null) {
				// agrupa as ofertas por área
				if (vagasOfertadas != null) {
					// para cada área
					for (AreaConhecimentoVestibular area : areas) {
						Collection<OfertaVagasCurso> ofertasArea = new ArrayList<OfertaVagasCurso>();
						// verifica se a oferta é da área
						for (OfertaVagasCurso oferta : vagasOfertadas) {
							if (oferta.getCurso().getMunicipio().getId() == idMunicipioOpcao1 &&
									oferta.getCurso().getAreaVestibular().getId() == area.getId())
								ofertasArea.add(oferta);
						}
						// monta o select com os cursos da área.
						if (ofertasArea != null && !ofertasArea.isEmpty()) {
							primeiraOpcaoCombo.add(new SelectItem(k--, "-- Área " + area.getDescricao() + " --"));
							for (OfertaVagasCurso oferta : ofertasArea) {
								primeiraOpcaoCombo.add(new SelectItem(oferta.getMatrizCurricular().getId(), oferta.getDescricaoTotalVagas()));
							}
						}
					}
				}
			}
		}
		if (primeiraOpcaoCombo.isEmpty())
			primeiraOpcaoCombo.add(new SelectItem("0", "-- SEM OFERTA DE CURSOS --"));
		idPrimeiraOpcaoCurso = 0;
		carregaMunicipiosSegundaOpcao();
	}
	
	/**
	 * Carrega os cursos para escolha de segunda opção. Tais cursos dependerão
	 * da escolha da primeira opção (mesma área / Campus).<br/>Método não invocado por JSP´s.
	 * 
	 * @throws DAOException
	 */
	public void carregaListaSegundaOpcao() throws DAOException {
		segundaOpcaoCombo = new ArrayList<SelectItem>();
		if (idMunicipioOpcao2 == 0) {
			segundaOpcaoCombo.add(new SelectItem("0", "-- SELECIONE UM MUNICÍPO --"));
			idSegundaOpcaoCurso = 0;
		} else {
			// Da matriz Curricular da primeira opção
			MatrizCurricular matriz = null;
			for (OfertaVagasCurso oferta : vagasOfertadas)
				if (oferta.getMatrizCurricular().getId() == idPrimeiraOpcaoCurso)
					matriz = oferta.getMatrizCurricular();
			if (matriz == null || matriz.getCurso() == null || matriz.getCurso().getUnidade() == null)
				return;
			// Recupere a área
			AreaConhecimentoVestibular areaSelecionada = null;
			for (AreaConhecimentoVestibular area : areas) {
				if (matriz.getCurso().getAreaVestibular().getId() == area.getId())
					areaSelecionada = area;
			}
			if (areaSelecionada == null) return;
			// carrrega as opções de curso para a mesma área da primeira opção
			Collection<OfertaVagasCurso> ofertasSegundaOpcao = new ArrayList<OfertaVagasCurso>();
			for (OfertaVagasCurso oferta : vagasOfertadas) {
				if (oferta.getCurso().getMunicipio().getId() == idMunicipioOpcao2 
						&& oferta.getCurso().getAreaVestibular().getId() == areaSelecionada.getId())
					ofertasSegundaOpcao.add(oferta);
			}
			// monta o combo
			if (!isEmpty(ofertasSegundaOpcao)) {
				segundaOpcaoCombo.add(new SelectItem("0", "-- Área " + areaSelecionada.getDescricao() + " --"));
				for (OfertaVagasCurso oferta : ofertasSegundaOpcao) {
					segundaOpcaoCombo.add(new SelectItem(oferta.getMatrizCurricular().getId(), oferta.getDescricaoTotalVagas()));
				}
			} else {
				segundaOpcaoCombo.add(new SelectItem("0", "-- SEM OFERTA DE CURSOS --"));
			}
		}
	}
	
	/** Verifica as restrições de inscrição de exclusividade na inscrição, removendo da lista as ofertas restritas.
	 * @param ofertas
	 * @throws DAOException
	 */
	private void verificaRestricoes(Collection<OfertaVagasCurso> ofertas, Long cpf) throws DAOException {
		if (ofertas == null) return;
		Iterator<OfertaVagasCurso> ofertaIterator = ofertas.iterator();
		while (ofertaIterator.hasNext()) {
			OfertaVagasCurso oferta = ofertaIterator.next();
			// exclusivo A
			if (restricoesExclusivoA != null) {
				for (RestricaoInscricaoVestibular restricao : restricoesExclusivoA) {
					// se a matriz é a mesma da restrição, mas o CPF não está na restrição, remove a oferta da lista.
					if (restricao.getMatrizCurricular().getId() == oferta.getMatrizCurricular().getId()
							&& !restricao.getCpfs().contains(cpf)) {
						ofertaIterator.remove();
						break;
					}
				}
			}
			// exceto A
			if (restricoesExcetoA != null) {
				for (RestricaoInscricaoVestibular restricao : restricoesExcetoA) {
					// se a matriz é a mesma da restrição e o CPF não na restrição, remove a oferta da lista.
					if (restricao.getMatrizCurricular().getId() == oferta.getMatrizCurricular().getId()) {
						ofertaIterator.remove();
						break;
					}
				}
			}
		}
	}

	/**
	 * Cria uma lista de municípios para escolha da primeira opção. Com isso, o
	 * usuário tem uma lista reduzida de cursos, evitando confusão consequente
	 * escolha de curso equivocada para outra cidade.<br/>Método não invocado por JSP´s.
	 * 
	 * @throws DAOException
	 */
	public void carregaMunicipiosPrimeiraOpcao() throws DAOException {
		municipiosPrimeiraOpcaoCombo = new ArrayList<SelectItem>();
		municipiosPrimeiraOpcaoCombo.add(new SelectItem("0", "-- SELECIONE --"));
		TreeSet<Integer> inserido = new TreeSet<Integer>();
		for (OfertaVagasCurso oferta : vagasOfertadas) {
			Municipio municipio = oferta.getCurso().getMunicipio();
			if (!inserido.contains(municipio.getId())) {
				municipiosPrimeiraOpcaoCombo.add(new SelectItem(municipio.getId(),municipio.getNome()));
				inserido.add(municipio.getId());
			}
		}
		idPrimeiraOpcaoCurso = 0;
		carregaListaPrimeiraOpcao();
	}
	
	/**
	 * Cria uma lista de municípios para escolha da segunda opção dependendo da
	 * escolha da primeira opção.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/form_cursos.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void carregaMunicipiosSegundaOpcao() throws DAOException {
		municipiosSegundaOpcaoCombo = new ArrayList<SelectItem>();
		if (idPrimeiraOpcaoCurso == 0 || idPrimeiraOpcaoCurso <= 0) {
			idMunicipioOpcao2 = 0;
			idSegundaOpcaoCurso = 0;
			municipiosSegundaOpcaoCombo.add(new SelectItem("0", "-- SELECIONE A PRIMEIRA OPÇÃO DE CURSO --"));
		} else {
			MatrizCurricular matriz = null;
			for (OfertaVagasCurso oferta : vagasOfertadas)
				if (oferta.getMatrizCurricular().getId() == idPrimeiraOpcaoCurso)
					matriz = oferta.getMatrizCurricular();
			if (matriz == null || matriz.getCurso() == null || matriz.getCurso().getUnidade() == null)
				return;
			TreeSet<Integer> inserido = new TreeSet<Integer>();
			for (OfertaVagasCurso oferta : vagasOfertadas) {
				Municipio municipio = oferta.getCurso().getMunicipio();
				if (inserido.contains(municipio.getId())) continue;
				// município da mesma unidade (CERES)
				if (// ceres
					(matriz.getCurso().getUnidade().getId() == Unidade.CERES && oferta.getCurso().getUnidade().getId() == Unidade.CERES)
					// mesmo município
					|| (matriz.getCurso().getMunicipio().getId() == municipio.getId()) ) {
					municipiosSegundaOpcaoCombo.add(new SelectItem(municipio.getId(), municipio.getNome()));
					inserido.add(municipio.getId());
				}
			}
			idMunicipioOpcao2 = idMunicipioOpcao1;
			verificaLinguaEstrangeira();
			
		}
		carregaListaSegundaOpcao();
	}

	/** Verifica se a língua estrangeira é obrigatória. <br/>Método não invocado por JSP´s.
	 * @throws DAOException */
	public void verificaLinguaEstrangeira() throws DAOException {
		this.linguaObrigatoria = false;
		MatrizCurricularDao matrizDao = getDAO(MatrizCurricularDao.class);
		MatrizCurricular matriz = matrizDao.findByPrimaryKey(idPrimeiraOpcaoCurso, MatrizCurricular.class);
		// verifica se a língua da primeira opção é obrigatória.
		if (matriz != null && matriz.getHabilitacao() != null && matriz.getHabilitacao().getLinguaObrigatoriaVestibular() != null) {
			this.obj.getLinguaEstrangeira().setId(matriz.getHabilitacao().getLinguaObrigatoriaVestibular().getId());
			this.linguaObrigatoria = true;
		} else {
			// verifica se a língua da segunda opção é obrigatória.
			 matriz = matrizDao.findByPrimaryKey(idSegundaOpcaoCurso, MatrizCurricular.class);
			 if (matriz != null && matriz.getHabilitacao() != null && matriz.getHabilitacao().getLinguaObrigatoriaVestibular() != null) {
				this.obj.getLinguaEstrangeira().setId(matriz.getHabilitacao().getLinguaObrigatoriaVestibular().getId());
				this.linguaObrigatoria = true;
			}
		}
	}

	/**
	 * Redireciona o usuário para o formulário de opções de curso. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/confirma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String editarOpcoesCurso() {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR.getId())) {
			return cancelar();
		}
		return forward(FORM_OPCAO_CURSO);
	}

	/**
	 * Inicia o processo de inscrição.<br/>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarInscricao(ProcessoSeletivoVestibular processoSeletivo, PessoaVestibular pessoaVestibular, String senha) throws ArqException {
		// recupera o ID do processo seletivo
		prepareMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR.getId());
		ValidatorUtil.validateRequired(processoSeletivo, "Processo Seletivo", erros);
		ValidatorUtil.validateRequired(pessoaVestibular, "Dados Pessoais", erros);
		if (hasOnlyErrors()) return null;
		// inicializa os atributos do controller e do objeto.
		initObj();
		GenericDAO dao = getGenericDAO();
		processoSeletivo = dao.refresh(processoSeletivo);
		obj.setProcessoSeletivo(processoSeletivo);
		verificaInscricaoAberta();
		if (hasOnlyErrors()) return null;
		obj.setPessoa(pessoaVestibular);
		this.senha = senha;
		ConfiguracaoGRU configuracaoGRU = ConfiguracaoGRUFactory.getInstance().getConfiguracaoGRUById(obj.getProcessoSeletivo().getIdConfiguracaoGRU());
		obj.getProcessoSeletivo().setConfiguracaoGRU(configuracaoGRU);
		// abre o formulário de dados pessoais
		Questionario questionario = dao.refresh(processoSeletivo.getQuestionario());
		obj.getProcessoSeletivo().setQuestionario(questionario);
		getQuestionarioRespostasMBean().inicializar(questionario);
		getQuestionarioRespostasMBean().getObj().setInscricaoVestibular(obj);
		// carrega os dados que serão necessários para escolha de curso
		carregaDadosSelecaoCurso();
		if (isEmpty(questionario)) {
			return forward(FORM_OPCAO_CURSO);
		} else {
			return forward(FORM_QUESTIONARIO_SOCIOECONOMICO);
		}
	}

	/** Carrega os dados que serão utilizados na inscrição do candidato. */
	private void carregaDadosSelecaoCurso() throws DAOException {
		OfertaVagasCursoDao ofertaCursoDao = getDAO(OfertaVagasCursoDao.class);
		AreaConhecimentoVestibularDao areaDao = getDAO(AreaConhecimentoVestibularDao.class);
		// restrições a seleção de curso.
		RestricaoInscricaoVestibularDao restricaoDao = getDAO(RestricaoInscricaoVestibularDao.class);
		restricoesExclusivoA = restricaoDao.findAllExclusivoAByProcessoSeletivo(obj.getProcessoSeletivo().getId());
		restricoesExcetoA = restricaoDao.findAllExcetoAByProcessoSeletivoCpf(obj.getProcessoSeletivo().getId(), obj.getPessoa().getCpf_cnpj());
		// municípios com oferta de vagas
		vagasOfertadas = ofertaCursoDao.findAllByAnoFormaIngresso(
				obj.getProcessoSeletivo().getAnoEntrada(), 
				obj.getProcessoSeletivo().getFormaIngresso().getId(),
				NivelEnsino.GRADUACAO);
		// verifica as restrições de exclusividade de inscrição
		verificaRestricoes(vagasOfertadas, obj.getPessoa().getCpf_cnpj());
		// áreas de conhecimento
		areas = areaDao.findByFormaIngressoAnoPeriodo(obj.getProcessoSeletivo().getFormaIngresso().getId(), obj.getProcessoSeletivo().getAnoEntrada(), obj.getProcessoSeletivo().getPeriodoEntrada());
		carregaMunicipiosPrimeiraOpcao();
	}

	/**
	 * Inicializa os dados do utilizados na inscrição.
	 * 
	 * @throws DAOException
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void initObj() throws DAOException {
		obj = new InscricaoVestibular();
		obj.prepararDados();
		idMunicipioOpcao1 = 0;
		idMunicipioOpcao2 = 0;
		idPrimeiraOpcaoCurso = 0;
		idSegundaOpcaoCurso = 0;
		linguaObrigatoria = false;
	}

	

	/**
	 * Valida as opções de curso. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/opcao_curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterOpcoes() throws DAOException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR.getId())) {
			return cancelar();
		}
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		verificaInscricaoAberta();
		validacaoOpcoesCurso(erros);
		verificaLinguaEstrangeira();
		if (hasErrors())
			return null;
		// complementa as informações
		obj.getOpcoesCurso()[0] = dao.findByPrimaryKey(idPrimeiraOpcaoCurso,
				MatrizCurricular.class);
		if (idSegundaOpcaoCurso != 0) {
			obj.getOpcoesCurso()[1] = dao.findByPrimaryKey(idSegundaOpcaoCurso,
					MatrizCurricular.class);
		} else {
			obj.getOpcoesCurso()[1] = null;
		}
		obj.setLinguaEstrangeira(dao.findByPrimaryKey(obj
				.getLinguaEstrangeira().getId(), LinguaEstrangeira.class));
		obj.setRegiaoPreferencialProva(dao.findByPrimaryKey(obj
				.getRegiaoPreferencialProva().getId(),
				RegiaoPreferencialProva.class));
		return confirmaInscricao();
	}

	/**
	 * Verifica se está no período de inscrição do Vestibular.
	 */
	private void verificaInscricaoAberta() {
		if (!obj.getProcessoSeletivo().isInscricoesCandidatoAbertas()) {
			addMensagemErro(String.format("Não está no período de inscrições: de %1$td/%1$tm/%1$tY à %2$td/%2$tm/%2$tY", obj.getProcessoSeletivo().getInicioInscricaoCandidato(), obj.getProcessoSeletivo().getFimInscricaoCandidato()));
		}
	}

	/**
	 * Confirma os dados da inscrição.<br/>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String confirmaInscricao() throws DAOException {
		// determina o valor da inscrição
		IsentoTaxaInscricaoDao isentoDao = getDAO(IsentoTaxaInscricaoDao.class);
		Collection<IsentoTaxaVestibular> lista = isentoDao.findByCpfProcessoSeletivo(obj.getPessoa().getCpf_cnpj(), obj.getProcessoSeletivo().getId());
		Double valor = null;
		if (lista != null && !lista.isEmpty()) {
			IsentoTaxaVestibular isento = lista.iterator().next();
			if (isento.isIsentoTotal()) valor = 0.0;
			else valor = isento.getValor();
		}
		if (valor != null) obj.setValorInscricao(valor);
		else obj.setValorInscricao(obj.getProcessoSeletivo().getValorInscricao());
		return forward(FORM_CONFIRMACAO);
	}

	/**
	 * Valida as respostas ao questionário sócio-econômico.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/form_questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterSocioEconomico() throws DAOException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_VESTIBULAR.getId())) {
			return cancelar();
		}
		verificaInscricaoAberta();
		getQuestionarioRespostasMBean().validarRepostas();
		if (hasErrors()) {
			return null;
		}
		respostas = getQuestionarioRespostasMBean().getObj();
		carregaMunicipiosPrimeiraOpcao();
		return forward(FORM_OPCAO_CURSO);
	}

	/**
	 * Verifica os atributos nulos e, se necessário, instancia-os.
	 * 
	 */
	protected void verificaNulos() {
		if (obj.getPessoa().getMunicipio() == null)
			obj.getPessoa().setMunicipio(new Municipio());
		if (obj.getPessoa().getTipoRaca() == null)
			obj.getPessoa().setTipoRaca(new TipoRaca());
	}

	/**
	 * Verifica quais atributos não são utilizados e, se necessário, anula-os.
	 * 
	 * @throws DAOException
	 */
	protected void preencherNulos() throws DAOException {
		obj.getPessoa().anularAtributosVazios();
		obj.getPessoa().setTipoRedeEnsino(null);
		if(obj.getPessoa().getPais().getId() == Pais.BRASIL)
			obj.getPessoa().getMunicipio().setUnidadeFederativa(
				obj.getPessoa().getUnidadeFederativa());
	}

	/**
	 * Valida as opções de curso.
	 * 
	 * @param lista
	 */
	private void validacaoOpcoesCurso(ListaMensagens lista) {
		validateRequiredId(idPrimeiraOpcaoCurso, "Curso de Primeira Opção", lista);
		validateRequired(obj.getLinguaEstrangeira(), "Língua Estrangeira", lista);
		validateRequired(obj.getRegiaoPreferencialProva(), "Região Preferencial de Prova", lista);
		if (obj.getProcessoSeletivo().isOpcaoBeneficioInclusao())
			validateRequired(obj.getOptouBeneficioInclusao(), "Benefício de Inclusão", lista);
	}

	/** Retorna o ID da MatrizCurricular da primeira opção. 
	 * @return
	 */
	public int getIdPrimeiraOpcaoCurso() {
		return idPrimeiraOpcaoCurso;
	}

	/** Seta o ID da MatrizCurricular da primeira opção. 
	 * @param idPrimeiraOpcaoCurso
	 */
	public void setIdPrimeiraOpcaoCurso(int idPrimeiraOpcaoCurso) {
		this.idPrimeiraOpcaoCurso = idPrimeiraOpcaoCurso;
	}

	/** Retorna o ID da MatrizCurricular da segunda opção. 
	 * @return
	 */
	public int getIdSegundaOpcaoCurso() {
		return idSegundaOpcaoCurso;
	}

	/** Seta o ID da MatrizCurricular da segunda opção.  
	 * @param idSegundaOpcaoCurso
	 */
	public void setIdSegundaOpcaoCurso(int idSegundaOpcaoCurso) {
		this.idSegundaOpcaoCurso = idSegundaOpcaoCurso;
	}

	/** Retorna a coleção de cursos de primeira opção. 
	 * @return
	 */
	public Collection<SelectItem> getPrimeiraOpcaoCombo() {
		return primeiraOpcaoCombo;
	}

	/** Seta a coleção de cursos de primeira opção. 
	 * @param primeiraOpcaoCombo
	 */
	public void setPrimeiraOpcaoCombo(Collection<SelectItem> primeiraOpcaoCombo) {
		this.primeiraOpcaoCombo = primeiraOpcaoCombo;
	}

	/** Retorna a coleção de cursos de segunda opção. Esses cursos irão depender da área/cidade do curso de primeira opção.
	 * @return
	 */
	public Collection<SelectItem> getSegundaOpcaoCombo() {
		return segundaOpcaoCombo;
	}

	/** Seta a coleção de cursos de segunda opção. Esses cursos irão depender da área/cidade do curso de primeira opção.
	 * @param segundaOpcaoCombo
	 */
	public void setSegundaOpcaoCombo(Collection<SelectItem> segundaOpcaoCombo) {
		this.segundaOpcaoCombo = segundaOpcaoCombo;
	}

	/** Retorna o ID do município da primeira opção. 
	 * @return
	 */
	public int getIdMunicipioOpcao1() {
		return idMunicipioOpcao1;
	}

	/** Seta o ID do município da primeira opção. 
	 * @param idMunicipioOpcao1
	 */
	public void setIdMunicipioOpcao1(int idMunicipioOpcao1) {
		this.idMunicipioOpcao1 = idMunicipioOpcao1;
	}

	/** Retorna o ID do município da segunda opção. Esse ID dependerá do ID da primeira opção.
	 * @return
	 */
	public int getIdMunicipioOpcao2() {
		return idMunicipioOpcao2;
	}

	/** Seta o ID do município da segunda opção. Esse ID dependerá do ID da primeira opção.
	 * @param idMunicipioOpcao2
	 */
	public void setIdMunicipioOpcao2(int idMunicipioOpcao2) {
		this.idMunicipioOpcao2 = idMunicipioOpcao2;
	}

	/** Retorna a lista de SelectItem de municípios de primeira opção. 
	 * @return
	 * @throws DAOException 
	 */
	public ArrayList<SelectItem> getMunicipiosPrimeiraOpcaoCombo() throws DAOException {
		if (isEmpty(municipiosPrimeiraOpcaoCombo))
			carregaMunicipiosPrimeiraOpcao();
		return municipiosPrimeiraOpcaoCombo;
	}

	/** Seta a lista de SelectItem de municípios de primeira opção.
	 * @param municipiosPrimeiraOpcaoCombo
	 */
	public void setMunicipiosPrimeiraOpcaoCombo(
			ArrayList<SelectItem> municipiosPrimeiraOpcaoCombo) {
		this.municipiosPrimeiraOpcaoCombo = municipiosPrimeiraOpcaoCombo;
	}

	/** Retorna a lista de SelectItem de municípios de segunda opção. 
	 * @return
	 */
	public ArrayList<SelectItem> getMunicipiosSegundaOpcaoCombo() {
		return municipiosSegundaOpcaoCombo;
	}

	/** Seta a lista de SelectItem de municípios de segunda opção.
	 * @param municipiosSegundaOpcaoCombo
	 */
	public void setMunicipiosSegundaOpcaoCombo(
			ArrayList<SelectItem> municipiosSegundaOpcaoCombo) {
		this.municipiosSegundaOpcaoCombo = municipiosSegundaOpcaoCombo;
	}

	/**
	 * Retorna uma coleção de EscolaInep para o recurso de autocompletar do
	 * formulário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<EscolaInep> autocompleteEscolaConclusao(Object event)
			throws DAOException {
		String nome = event.toString();
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		return dao.findByNomeUF(nome, obj.getPessoa()
				.getUfConclusaoEnsinoMedio().getId());
	}

	/** Retorna o Valor do botão submit no formulário. 
	 * @return
	 */
	public String getSubmitButton() {
		return submitButton;
	}

	/** Seta o Valor do botão submit no formulário. 
	 * @param submitButton
	 */
	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	/** Retorna o CPF do candidato para consulta de inscrição realizada. 
	 * @return
	 */
	public Long getCpfConsulta() {
		return cpfConsulta;
	}

	/** Seta o CPF do candidato para consulta de inscrição realizada.
	 * @param cpfConsulta
	 */
	public void setCpfConsulta(Long cpfConsulta) {
		this.cpfConsulta = cpfConsulta;
	}

	/** Indica se a língua estrangeira é obrigatória. É o caso de, por exemplo, quem optar pelo curso de Letras-Francês e tem a obrigatoriedade de realizar a prova de língua estrangeira em francês.
	 * @return
	 */
	public boolean isLinguaObrigatoria() {
		return linguaObrigatoria;
	}

	/** Seta se a língua estrangeira é obrigatória. É o caso de, por exemplo, quem optar pelo curso de Letras-Francês e tem a obrigatoriedade de realizar a prova de língua estrangeira em francês.
	 * @param linguaObrigatoria
	 */
	public void setLinguaObrigatoria(boolean linguaObrigatoria) {
		this.linguaObrigatoria = linguaObrigatoria;
	}

	/**
	 * Retorna o controller das respostas do questionário aplicado durante a
	 * inscrição.
	 * 
	 * @return
	 */
	private QuestionarioRespostasMBean getQuestionarioRespostasMBean() {
		return (QuestionarioRespostasMBean) getMBean("questionarioRespostasBean");
	}

	/**
	 * Cancela o processo de inscrição.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/confirma.jsp</li>
	 * <li>/sigaa.war/public/vestibular/inscricao/dados_pessoais.jsp</li>
	 * <li>/sigaa.war/public/vestibular/inscricao/form_questionario.jsp</li>
	 * <li>/sigaa.war/public/vestibular/inscricao/opcao_curso.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		obj = null;
		super.cancelar();
		AcompanhamentoVestibularMBean mBean = getMBean("acompanhamentoVestibular");
		return mBean.paginaAcompanhamento();
	}

	/**
	 * Visualiza o comprovante da inscrição selecionada. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * <li>/sigaa.war/public/vestibular/inscricao/lista_inscricoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String verComprovante() throws DAOException {
		// o ID e o número de inscrição servem para evitar scripts para gerar consultas às inscrições
		obj = new InscricaoVestibular();
		int idInscricao = getParameterInt("id", 0);
		int numeroInscricao = getParameterInt("inscricao", 0);
		return verComprovante(idInscricao, numeroInscricao);
	}
	
	/**
	 * Visualiza o comprovante da inscrição selecionada. <br/>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String verComprovante(int idInscricao, int numeroInscricao) throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoVestibular.class);
		if (idInscricao == 0 || numeroInscricao == 0 || obj != null && obj.getNumeroInscricao() != numeroInscricao) {
			addMensagemErro("Erro na identificação da inscrição.");
			return null;
		}
		getCurrentRequest().setAttribute("visualizacao", true);
		return forward(FORM_COMPROVANTE_INSCRICAO);
	}

	/**
	 * Imprime o termo de responsabilidade necessário para o candidato obter o benefício de inclusão. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String imprimirTermoResponsabilidadeBeneficio() throws ArqException, NegocioException {
		// o ID e o número de inscrição servem para evitar scripts para gerar consultas às inscrições
		obj = new InscricaoVestibular();
		int idInscricao = getParameterInt("id", 0);
		int numeroInscricao = getParameterInt("inscricao", 0);
		try {
			obj = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoVestibular.class);
			if (idInscricao == 0 || numeroInscricao == 0 || obj != null && obj.getNumeroInscricao() != numeroInscricao) {
				addMensagemErro("Erro na identificação da inscrição.");
				return null;
			}
			if (hasOnlyErrors()) 
				return redirectMesmaPagina();
			// parâmetros do relatório
			Map<String, Object> map = new HashMap<String, Object>();
			
			ArrayList<InscricaoVestibular> lista =  new ArrayList<InscricaoVestibular>();
			lista.add(obj);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			InputStream report = null;
			report = JasperReportsUtil.getReport(BASE_REPORT_PACKAGE, "TermoResponsabilidadeBeneficioInclusao.jasper");
			JasperPrint prt = JasperFillManager.fillReport(report, map, jrds);
			JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=TermoResponsabilidade.pdf");
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			tratamentoErroPadrao(e);
		} catch (JRException e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/**
	 * Imprime a GRU para pagamento da taxa de inscrição do vestibular. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String imprimirGRU() throws ArqException, NegocioException {
		// o ID e o número de inscrição servem para evitar scripts para gerar consultas às inscrições
		obj = new InscricaoVestibular();
		int idInscricao = getParameterInt("id", 0);
		int numeroInscricao = getParameterInt("inscricao", 0);
		return imprimirGRU(idInscricao, numeroInscricao);
	}
	
	/** Imprime a GRU para pagamento da taxa de inscrição do vestibular.
	 * <br/>Método não invocado por JSP´s.
	 * @param idInscricao
	 * @param numeroInscricao
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public  String imprimirGRU(int idInscricao, int numeroInscricao) throws ArqException, NegocioException {
		try {
			obj = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoVestibular.class);
			if (idInscricao == 0 || numeroInscricao == 0 || obj != null && obj.getNumeroInscricao() != numeroInscricao) {
				addMensagemErro("Erro na identificação da inscrição.");
				return null;
			}
			if (hasErrors()) 
				return null;
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=GRU.pdf");
			Date vencimento = obj.getProcessoSeletivo().getDataVencimentoBoleto();
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.getGRUByID(obj.getIdGRU());
			if (!vencimento.after(gru.getVencimento()))
				vencimento = null;
			GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), obj.getIdGRU(), vencimento);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/** Indica se o candidato pode imprimir a GRU, verificando se o prazo de vencimento expirou.
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPermiteImpressaoGRU() throws DAOException {
		if (obj == null) return false;
		ProcessoSeletivoVestibular ps = getGenericDAO().refresh(obj.getProcessoSeletivo());
		if (ps == null) return false;
		Date vencimento = ps.getDataVencimentoBoleto();
		if (vencimento != null)
			return !CalendarUtils.estorouPrazo(vencimento, new Date()) && obj != null && !obj.isValidada() && obj.getValorInscricao() > 0;
		else return false;
	}

	/** Retorna a data a partir do qual o candidato isento pode consultar a validação da inscrição no vestibular.
	 * Por regra, é o dia seguinte ao dia do fim da inscrição do vestibular.
	 * @return
	 */
	public Date getDataValidacaoIsento() {
		return CalendarUtils.adicionaUmDia(obj.getProcessoSeletivo().getFimInscricaoCandidato());
	}
	
	@Override
	public List<SelectItem> getSimNao() {
		List<SelectItem> simNao = new ArrayList<SelectItem>();
		simNao.add(new SelectItem(new Boolean(true), "SIM, desejo concorrer COM o benefício do Argumento de Inclusão."));
		simNao.add(new SelectItem(new Boolean(false), "NÃO, desejo concorrer SEM o benefício do Argumento de Inclusão."));
		return simNao;
	}
}
