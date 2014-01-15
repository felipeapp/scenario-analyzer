/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Controller respons�vel pela inscri��o no Vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("inscricaoVestibular")
@Scope("session")
public class InscricaoVestibularMBean extends SigaaAbstractController<InscricaoVestibular> {
	/**
	 * O diret�rio padr�o dos fontes para gerar o pdf das GRUs.
	 */
	private static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	
	/** ID da {@link MatrizCurricular} da primeira op��o. */
	private int idPrimeiraOpcaoCurso;
	/** ID da {@link MatrizCurricular} da segunda op��o. */
	private int idSegundaOpcaoCurso;
	/** Cole��o de cursos de primeira op��o. */
	private Collection<SelectItem> primeiraOpcaoCombo = new ArrayList<SelectItem>();
	/**
	 * Cole��o de cursos de segunda op��o. Esses cursos ir�o depender da
	 * �rea/cidade do curso de primeira op��o.
	 */
	private Collection<SelectItem> segundaOpcaoCombo = new ArrayList<SelectItem>();
	/** ID do munic�pio da primeira op��o. */
	private int idMunicipioOpcao1;
	/**
	 * ID do munic�pio da segunda op��o. Esse ID depender� do ID da primeira
	 * op��o.
	 */
	private int idMunicipioOpcao2;
	/** Lista de SelectItem de munic�pios de primeira op��o. */
	private ArrayList<SelectItem> municipiosPrimeiraOpcaoCombo;
	/** Lista de SelectItem de munic�pios de segunda op��o. */
	private ArrayList<SelectItem> municipiosSegundaOpcaoCombo;
	/**
	 * Indica se a l�ngua estrangeira � obrigat�ria. � o caso de, por exemplo,
	 * quem optar pelo curso de Letras-Franc�s e tem a obrigatoriedade de realizar
	 * a prova de l�ngua estrangeira em franc�s.
	 */
	private boolean linguaObrigatoria = false;
	
	/** Valor do bot�o submit no formul�rio. */
	private String submitButton = "Pr�ximo Passo >>";
	/** CPF do candidato para consulta de inscri��o realizada. */
	private Long cpfConsulta;

	/** Link para o formul�rio do question�rio s�cio-econ�mico. */
	private final String FORM_QUESTIONARIO_SOCIOECONOMICO = "/public/vestibular/inscricao/form_questionario.jsf";
	/** Link para o formul�rio de op��o de curso. */
	private final String FORM_OPCAO_CURSO = "/public/vestibular/inscricao/opcao_curso.jsf";
	/** Link para o formul�rio de dados pessoais. */
	private final String FORM_CONFIRMACAO = "/public/vestibular/inscricao/confirma.jsf";
	/** Link para o formul�rio de confirma��o de dados da inscri��o. */
	private final String FORM_COMPROVANTE_INSCRICAO = "/public/vestibular/inscricao/comprovante.jsf";
	/** Respostas do candidato ao question�rio s�cio-econ�mico. */
	private QuestionarioRespostas respostas;
	/** Senha a ser enviada ao candidato. */
	private String senha;
	
	/** Restriu��o de inscri��o exclusiva ao candidato. */
	Collection<RestricaoInscricaoVestibular> restricoesExclusivoA;
	/** Restriu��o de inscri��o exceto ao candidato. */
	Collection<RestricaoInscricaoVestibular> restricoesExcetoA;
	
	/** Matrizes Curriculares ofertadas no processo Seletivo. */
	private Collection<OfertaVagasCurso> vagasOfertadas;
	/** �rea de conhecimentos que possuem oferta de vagas no vestibular. */
	private Collection<AreaConhecimentoVestibular> areas;

	/** Construtor padr�o. */
	public InscricaoVestibularMBean() {
	}

	/**
	 * Cadastra a inscri��o para o vestibular.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
		addMensagemInformation("Sua inscri��o foi gravada com sucesso.");
		removeOperacaoAtiva();
		return forward(FORM_COMPROVANTE_INSCRICAO);
	}

	/**
	 * Carrega a lista de cursos para escolha da primeira op��o.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
					"-- SELECIONE UM MUNIC�PO PRIMEIRO --"));
		} else {
			if (areas != null) {
				// agrupa as ofertas por �rea
				if (vagasOfertadas != null) {
					// para cada �rea
					for (AreaConhecimentoVestibular area : areas) {
						Collection<OfertaVagasCurso> ofertasArea = new ArrayList<OfertaVagasCurso>();
						// verifica se a oferta � da �rea
						for (OfertaVagasCurso oferta : vagasOfertadas) {
							if (oferta.getCurso().getMunicipio().getId() == idMunicipioOpcao1 &&
									oferta.getCurso().getAreaVestibular().getId() == area.getId())
								ofertasArea.add(oferta);
						}
						// monta o select com os cursos da �rea.
						if (ofertasArea != null && !ofertasArea.isEmpty()) {
							primeiraOpcaoCombo.add(new SelectItem(k--, "-- �rea " + area.getDescricao() + " --"));
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
	 * Carrega os cursos para escolha de segunda op��o. Tais cursos depender�o
	 * da escolha da primeira op��o (mesma �rea / Campus).<br/>M�todo n�o invocado por JSP�s.
	 * 
	 * @throws DAOException
	 */
	public void carregaListaSegundaOpcao() throws DAOException {
		segundaOpcaoCombo = new ArrayList<SelectItem>();
		if (idMunicipioOpcao2 == 0) {
			segundaOpcaoCombo.add(new SelectItem("0", "-- SELECIONE UM MUNIC�PO --"));
			idSegundaOpcaoCurso = 0;
		} else {
			// Da matriz Curricular da primeira op��o
			MatrizCurricular matriz = null;
			for (OfertaVagasCurso oferta : vagasOfertadas)
				if (oferta.getMatrizCurricular().getId() == idPrimeiraOpcaoCurso)
					matriz = oferta.getMatrizCurricular();
			if (matriz == null || matriz.getCurso() == null || matriz.getCurso().getUnidade() == null)
				return;
			// Recupere a �rea
			AreaConhecimentoVestibular areaSelecionada = null;
			for (AreaConhecimentoVestibular area : areas) {
				if (matriz.getCurso().getAreaVestibular().getId() == area.getId())
					areaSelecionada = area;
			}
			if (areaSelecionada == null) return;
			// carrrega as op��es de curso para a mesma �rea da primeira op��o
			Collection<OfertaVagasCurso> ofertasSegundaOpcao = new ArrayList<OfertaVagasCurso>();
			for (OfertaVagasCurso oferta : vagasOfertadas) {
				if (oferta.getCurso().getMunicipio().getId() == idMunicipioOpcao2 
						&& oferta.getCurso().getAreaVestibular().getId() == areaSelecionada.getId())
					ofertasSegundaOpcao.add(oferta);
			}
			// monta o combo
			if (!isEmpty(ofertasSegundaOpcao)) {
				segundaOpcaoCombo.add(new SelectItem("0", "-- �rea " + areaSelecionada.getDescricao() + " --"));
				for (OfertaVagasCurso oferta : ofertasSegundaOpcao) {
					segundaOpcaoCombo.add(new SelectItem(oferta.getMatrizCurricular().getId(), oferta.getDescricaoTotalVagas()));
				}
			} else {
				segundaOpcaoCombo.add(new SelectItem("0", "-- SEM OFERTA DE CURSOS --"));
			}
		}
	}
	
	/** Verifica as restri��es de inscri��o de exclusividade na inscri��o, removendo da lista as ofertas restritas.
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
					// se a matriz � a mesma da restri��o, mas o CPF n�o est� na restri��o, remove a oferta da lista.
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
					// se a matriz � a mesma da restri��o e o CPF n�o na restri��o, remove a oferta da lista.
					if (restricao.getMatrizCurricular().getId() == oferta.getMatrizCurricular().getId()) {
						ofertaIterator.remove();
						break;
					}
				}
			}
		}
	}

	/**
	 * Cria uma lista de munic�pios para escolha da primeira op��o. Com isso, o
	 * usu�rio tem uma lista reduzida de cursos, evitando confus�o consequente
	 * escolha de curso equivocada para outra cidade.<br/>M�todo n�o invocado por JSP�s.
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
	 * Cria uma lista de munic�pios para escolha da segunda op��o dependendo da
	 * escolha da primeira op��o.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			municipiosSegundaOpcaoCombo.add(new SelectItem("0", "-- SELECIONE A PRIMEIRA OP��O DE CURSO --"));
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
				// munic�pio da mesma unidade (CERES)
				if (// ceres
					(matriz.getCurso().getUnidade().getId() == Unidade.CERES && oferta.getCurso().getUnidade().getId() == Unidade.CERES)
					// mesmo munic�pio
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

	/** Verifica se a l�ngua estrangeira � obrigat�ria. <br/>M�todo n�o invocado por JSP�s.
	 * @throws DAOException */
	public void verificaLinguaEstrangeira() throws DAOException {
		this.linguaObrigatoria = false;
		MatrizCurricularDao matrizDao = getDAO(MatrizCurricularDao.class);
		MatrizCurricular matriz = matrizDao.findByPrimaryKey(idPrimeiraOpcaoCurso, MatrizCurricular.class);
		// verifica se a l�ngua da primeira op��o � obrigat�ria.
		if (matriz != null && matriz.getHabilitacao() != null && matriz.getHabilitacao().getLinguaObrigatoriaVestibular() != null) {
			this.obj.getLinguaEstrangeira().setId(matriz.getHabilitacao().getLinguaObrigatoriaVestibular().getId());
			this.linguaObrigatoria = true;
		} else {
			// verifica se a l�ngua da segunda op��o � obrigat�ria.
			 matriz = matrizDao.findByPrimaryKey(idSegundaOpcaoCurso, MatrizCurricular.class);
			 if (matriz != null && matriz.getHabilitacao() != null && matriz.getHabilitacao().getLinguaObrigatoriaVestibular() != null) {
				this.obj.getLinguaEstrangeira().setId(matriz.getHabilitacao().getLinguaObrigatoriaVestibular().getId());
				this.linguaObrigatoria = true;
			}
		}
	}

	/**
	 * Redireciona o usu�rio para o formul�rio de op��es de curso. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Inicia o processo de inscri��o.<br/>M�todo n�o invocado por JSP�s.
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
		// abre o formul�rio de dados pessoais
		Questionario questionario = dao.refresh(processoSeletivo.getQuestionario());
		obj.getProcessoSeletivo().setQuestionario(questionario);
		getQuestionarioRespostasMBean().inicializar(questionario);
		getQuestionarioRespostasMBean().getObj().setInscricaoVestibular(obj);
		// carrega os dados que ser�o necess�rios para escolha de curso
		carregaDadosSelecaoCurso();
		if (isEmpty(questionario)) {
			return forward(FORM_OPCAO_CURSO);
		} else {
			return forward(FORM_QUESTIONARIO_SOCIOECONOMICO);
		}
	}

	/** Carrega os dados que ser�o utilizados na inscri��o do candidato. */
	private void carregaDadosSelecaoCurso() throws DAOException {
		OfertaVagasCursoDao ofertaCursoDao = getDAO(OfertaVagasCursoDao.class);
		AreaConhecimentoVestibularDao areaDao = getDAO(AreaConhecimentoVestibularDao.class);
		// restri��es a sele��o de curso.
		RestricaoInscricaoVestibularDao restricaoDao = getDAO(RestricaoInscricaoVestibularDao.class);
		restricoesExclusivoA = restricaoDao.findAllExclusivoAByProcessoSeletivo(obj.getProcessoSeletivo().getId());
		restricoesExcetoA = restricaoDao.findAllExcetoAByProcessoSeletivoCpf(obj.getProcessoSeletivo().getId(), obj.getPessoa().getCpf_cnpj());
		// munic�pios com oferta de vagas
		vagasOfertadas = ofertaCursoDao.findAllByAnoFormaIngresso(
				obj.getProcessoSeletivo().getAnoEntrada(), 
				obj.getProcessoSeletivo().getFormaIngresso().getId(),
				NivelEnsino.GRADUACAO);
		// verifica as restri��es de exclusividade de inscri��o
		verificaRestricoes(vagasOfertadas, obj.getPessoa().getCpf_cnpj());
		// �reas de conhecimento
		areas = areaDao.findByFormaIngressoAnoPeriodo(obj.getProcessoSeletivo().getFormaIngresso().getId(), obj.getProcessoSeletivo().getAnoEntrada(), obj.getProcessoSeletivo().getPeriodoEntrada());
		carregaMunicipiosPrimeiraOpcao();
	}

	/**
	 * Inicializa os dados do utilizados na inscri��o.
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
	 * Valida as op��es de curso. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
		// complementa as informa��es
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
	 * Verifica se est� no per�odo de inscri��o do Vestibular.
	 */
	private void verificaInscricaoAberta() {
		if (!obj.getProcessoSeletivo().isInscricoesCandidatoAbertas()) {
			addMensagemErro(String.format("N�o est� no per�odo de inscri��es: de %1$td/%1$tm/%1$tY � %2$td/%2$tm/%2$tY", obj.getProcessoSeletivo().getInicioInscricaoCandidato(), obj.getProcessoSeletivo().getFimInscricaoCandidato()));
		}
	}

	/**
	 * Confirma os dados da inscri��o.<br/>M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String confirmaInscricao() throws DAOException {
		// determina o valor da inscri��o
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
	 * Valida as respostas ao question�rio s�cio-econ�mico.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Verifica os atributos nulos e, se necess�rio, instancia-os.
	 * 
	 */
	protected void verificaNulos() {
		if (obj.getPessoa().getMunicipio() == null)
			obj.getPessoa().setMunicipio(new Municipio());
		if (obj.getPessoa().getTipoRaca() == null)
			obj.getPessoa().setTipoRaca(new TipoRaca());
	}

	/**
	 * Verifica quais atributos n�o s�o utilizados e, se necess�rio, anula-os.
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
	 * Valida as op��es de curso.
	 * 
	 * @param lista
	 */
	private void validacaoOpcoesCurso(ListaMensagens lista) {
		validateRequiredId(idPrimeiraOpcaoCurso, "Curso de Primeira Op��o", lista);
		validateRequired(obj.getLinguaEstrangeira(), "L�ngua Estrangeira", lista);
		validateRequired(obj.getRegiaoPreferencialProva(), "Regi�o Preferencial de Prova", lista);
		if (obj.getProcessoSeletivo().isOpcaoBeneficioInclusao())
			validateRequired(obj.getOptouBeneficioInclusao(), "Benef�cio de Inclus�o", lista);
	}

	/** Retorna o ID da MatrizCurricular da primeira op��o. 
	 * @return
	 */
	public int getIdPrimeiraOpcaoCurso() {
		return idPrimeiraOpcaoCurso;
	}

	/** Seta o ID da MatrizCurricular da primeira op��o. 
	 * @param idPrimeiraOpcaoCurso
	 */
	public void setIdPrimeiraOpcaoCurso(int idPrimeiraOpcaoCurso) {
		this.idPrimeiraOpcaoCurso = idPrimeiraOpcaoCurso;
	}

	/** Retorna o ID da MatrizCurricular da segunda op��o. 
	 * @return
	 */
	public int getIdSegundaOpcaoCurso() {
		return idSegundaOpcaoCurso;
	}

	/** Seta o ID da MatrizCurricular da segunda op��o.  
	 * @param idSegundaOpcaoCurso
	 */
	public void setIdSegundaOpcaoCurso(int idSegundaOpcaoCurso) {
		this.idSegundaOpcaoCurso = idSegundaOpcaoCurso;
	}

	/** Retorna a cole��o de cursos de primeira op��o. 
	 * @return
	 */
	public Collection<SelectItem> getPrimeiraOpcaoCombo() {
		return primeiraOpcaoCombo;
	}

	/** Seta a cole��o de cursos de primeira op��o. 
	 * @param primeiraOpcaoCombo
	 */
	public void setPrimeiraOpcaoCombo(Collection<SelectItem> primeiraOpcaoCombo) {
		this.primeiraOpcaoCombo = primeiraOpcaoCombo;
	}

	/** Retorna a cole��o de cursos de segunda op��o. Esses cursos ir�o depender da �rea/cidade do curso de primeira op��o.
	 * @return
	 */
	public Collection<SelectItem> getSegundaOpcaoCombo() {
		return segundaOpcaoCombo;
	}

	/** Seta a cole��o de cursos de segunda op��o. Esses cursos ir�o depender da �rea/cidade do curso de primeira op��o.
	 * @param segundaOpcaoCombo
	 */
	public void setSegundaOpcaoCombo(Collection<SelectItem> segundaOpcaoCombo) {
		this.segundaOpcaoCombo = segundaOpcaoCombo;
	}

	/** Retorna o ID do munic�pio da primeira op��o. 
	 * @return
	 */
	public int getIdMunicipioOpcao1() {
		return idMunicipioOpcao1;
	}

	/** Seta o ID do munic�pio da primeira op��o. 
	 * @param idMunicipioOpcao1
	 */
	public void setIdMunicipioOpcao1(int idMunicipioOpcao1) {
		this.idMunicipioOpcao1 = idMunicipioOpcao1;
	}

	/** Retorna o ID do munic�pio da segunda op��o. Esse ID depender� do ID da primeira op��o.
	 * @return
	 */
	public int getIdMunicipioOpcao2() {
		return idMunicipioOpcao2;
	}

	/** Seta o ID do munic�pio da segunda op��o. Esse ID depender� do ID da primeira op��o.
	 * @param idMunicipioOpcao2
	 */
	public void setIdMunicipioOpcao2(int idMunicipioOpcao2) {
		this.idMunicipioOpcao2 = idMunicipioOpcao2;
	}

	/** Retorna a lista de SelectItem de munic�pios de primeira op��o. 
	 * @return
	 * @throws DAOException 
	 */
	public ArrayList<SelectItem> getMunicipiosPrimeiraOpcaoCombo() throws DAOException {
		if (isEmpty(municipiosPrimeiraOpcaoCombo))
			carregaMunicipiosPrimeiraOpcao();
		return municipiosPrimeiraOpcaoCombo;
	}

	/** Seta a lista de SelectItem de munic�pios de primeira op��o.
	 * @param municipiosPrimeiraOpcaoCombo
	 */
	public void setMunicipiosPrimeiraOpcaoCombo(
			ArrayList<SelectItem> municipiosPrimeiraOpcaoCombo) {
		this.municipiosPrimeiraOpcaoCombo = municipiosPrimeiraOpcaoCombo;
	}

	/** Retorna a lista de SelectItem de munic�pios de segunda op��o. 
	 * @return
	 */
	public ArrayList<SelectItem> getMunicipiosSegundaOpcaoCombo() {
		return municipiosSegundaOpcaoCombo;
	}

	/** Seta a lista de SelectItem de munic�pios de segunda op��o.
	 * @param municipiosSegundaOpcaoCombo
	 */
	public void setMunicipiosSegundaOpcaoCombo(
			ArrayList<SelectItem> municipiosSegundaOpcaoCombo) {
		this.municipiosSegundaOpcaoCombo = municipiosSegundaOpcaoCombo;
	}

	/**
	 * Retorna uma cole��o de EscolaInep para o recurso de autocompletar do
	 * formul�rio.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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

	/** Retorna o Valor do bot�o submit no formul�rio. 
	 * @return
	 */
	public String getSubmitButton() {
		return submitButton;
	}

	/** Seta o Valor do bot�o submit no formul�rio. 
	 * @param submitButton
	 */
	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	/** Retorna o CPF do candidato para consulta de inscri��o realizada. 
	 * @return
	 */
	public Long getCpfConsulta() {
		return cpfConsulta;
	}

	/** Seta o CPF do candidato para consulta de inscri��o realizada.
	 * @param cpfConsulta
	 */
	public void setCpfConsulta(Long cpfConsulta) {
		this.cpfConsulta = cpfConsulta;
	}

	/** Indica se a l�ngua estrangeira � obrigat�ria. � o caso de, por exemplo, quem optar pelo curso de Letras-Franc�s e tem a obrigatoriedade de realizar a prova de l�ngua estrangeira em franc�s.
	 * @return
	 */
	public boolean isLinguaObrigatoria() {
		return linguaObrigatoria;
	}

	/** Seta se a l�ngua estrangeira � obrigat�ria. � o caso de, por exemplo, quem optar pelo curso de Letras-Franc�s e tem a obrigatoriedade de realizar a prova de l�ngua estrangeira em franc�s.
	 * @param linguaObrigatoria
	 */
	public void setLinguaObrigatoria(boolean linguaObrigatoria) {
		this.linguaObrigatoria = linguaObrigatoria;
	}

	/**
	 * Retorna o controller das respostas do question�rio aplicado durante a
	 * inscri��o.
	 * 
	 * @return
	 */
	private QuestionarioRespostasMBean getQuestionarioRespostasMBean() {
		return (QuestionarioRespostasMBean) getMBean("questionarioRespostasBean");
	}

	/**
	 * Cancela o processo de inscri��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Visualiza o comprovante da inscri��o selecionada. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * <li>/sigaa.war/public/vestibular/inscricao/lista_inscricoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String verComprovante() throws DAOException {
		// o ID e o n�mero de inscri��o servem para evitar scripts para gerar consultas �s inscri��es
		obj = new InscricaoVestibular();
		int idInscricao = getParameterInt("id", 0);
		int numeroInscricao = getParameterInt("inscricao", 0);
		return verComprovante(idInscricao, numeroInscricao);
	}
	
	/**
	 * Visualiza o comprovante da inscri��o selecionada. <br/>M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String verComprovante(int idInscricao, int numeroInscricao) throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoVestibular.class);
		if (idInscricao == 0 || numeroInscricao == 0 || obj != null && obj.getNumeroInscricao() != numeroInscricao) {
			addMensagemErro("Erro na identifica��o da inscri��o.");
			return null;
		}
		getCurrentRequest().setAttribute("visualizacao", true);
		return forward(FORM_COMPROVANTE_INSCRICAO);
	}

	/**
	 * Imprime o termo de responsabilidade necess�rio para o candidato obter o benef�cio de inclus�o. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String imprimirTermoResponsabilidadeBeneficio() throws ArqException, NegocioException {
		// o ID e o n�mero de inscri��o servem para evitar scripts para gerar consultas �s inscri��es
		obj = new InscricaoVestibular();
		int idInscricao = getParameterInt("id", 0);
		int numeroInscricao = getParameterInt("inscricao", 0);
		try {
			obj = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoVestibular.class);
			if (idInscricao == 0 || numeroInscricao == 0 || obj != null && obj.getNumeroInscricao() != numeroInscricao) {
				addMensagemErro("Erro na identifica��o da inscri��o.");
				return null;
			}
			if (hasOnlyErrors()) 
				return redirectMesmaPagina();
			// par�metros do relat�rio
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
	 * Imprime a GRU para pagamento da taxa de inscri��o do vestibular. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String imprimirGRU() throws ArqException, NegocioException {
		// o ID e o n�mero de inscri��o servem para evitar scripts para gerar consultas �s inscri��es
		obj = new InscricaoVestibular();
		int idInscricao = getParameterInt("id", 0);
		int numeroInscricao = getParameterInt("inscricao", 0);
		return imprimirGRU(idInscricao, numeroInscricao);
	}
	
	/** Imprime a GRU para pagamento da taxa de inscri��o do vestibular.
	 * <br/>M�todo n�o invocado por JSP�s.
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
				addMensagemErro("Erro na identifica��o da inscri��o.");
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

	/** Retorna a data a partir do qual o candidato isento pode consultar a valida��o da inscri��o no vestibular.
	 * Por regra, � o dia seguinte ao dia do fim da inscri��o do vestibular.
	 * @return
	 */
	public Date getDataValidacaoIsento() {
		return CalendarUtils.adicionaUmDia(obj.getProcessoSeletivo().getFimInscricaoCandidato());
	}
	
	@Override
	public List<SelectItem> getSimNao() {
		List<SelectItem> simNao = new ArrayList<SelectItem>();
		simNao.add(new SelectItem(new Boolean(true), "SIM, desejo concorrer COM o benef�cio do Argumento de Inclus�o."));
		simNao.add(new SelectItem(new Boolean(false), "N�O, desejo concorrer SEM o benef�cio do Argumento de Inclus�o."));
		return simNao;
	}
}
