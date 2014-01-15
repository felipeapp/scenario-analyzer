/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 16/07/2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoCadastroOfertaVagasCurso;
import br.ufrn.sigaa.vestibular.dominio.GrupoCotaVagaCurso;

/** 
 * Controller responsável por operações relativas ao cadastro de oferta de vagas em um curso.
 * @author Édipo Elder F. Melo
 *
 */
@Component("cadastroOfertaVagasCurso")
@Scope("session")
public class CadastroOfertaVagasCursoMBean extends SigaaAbstractController<OfertaVagasCurso> {
	
	/** Indica se é para selecionar apenas as matrizes curriculares inativas */
	private static final Boolean MATRIZES_CURRICULARES_INATIVAS = false;
	/** Indica se é para selecionar apenas as matrizes curriculares ativas */
	private static final Boolean MATRIZES_CURRICULARES_ATIVAS = true;
	
	/** Lista de ofertas de vagas por curso a operar. */
	private List<OfertaVagasCurso> listaOfertaVagasCurso;
	
	/** Ano da oferta. */
	private int ano;
	
	/** Nível acadêmico dos cursos ofertados. */
	private char nivel;
	
	/** Ano da oferta. */
	private Integer modalidadeEducacao;
	
	/** Nível acadêmico dos cursos ofertados. */
	private Boolean apenasMatrizesAtivas;
	
	/** Unidade dos cursos ofertados. */
	private int idUnidade;

	/** Forma de ingresso da oferta do curso. */
	private FormaIngresso formaIngresso;
	
	/** Endereço do formulário de cadastro. */
	private String formPage;
	
	/** Grupos de cotas para o nível de ensino. */
	private Collection<GrupoCotaVagaCurso> gruposCotas;

	/** Retorna a unidade dos cursos ofertados. 
	 * @return
	 */
	public int getIdUnidade() {
		return idUnidade;
	}

	/** Seta a unidade dos cursos ofertados.
	 * @param idUnidade
	 */
	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	/** Retorna o ano da oferta.  
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano da oferta. 
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Inicializa alguns atributos do controller.
	 * @throws ArqException
	 */
	private void iniciarComum() throws ArqException {
		this.ano = CalendarUtils.getAnoAtual() + 1;
		this.obj = new OfertaVagasCurso();
		this.apenasMatrizesAtivas = true;
		this.modalidadeEducacao = ModalidadeEducacao.PRESENCIAL;
	}

	/** Inicia o processo de cadastro de ofertas de cursos de graduação.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarGraduacao() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		iniciarComum();
		this.nivel = NivelEnsino.GRADUACAO;
		this.formaIngresso = FormaIngresso.VESTIBULAR;
		this.idUnidade = 0;
		gruposCotas = getGenericDAO().findByExactField(GrupoCotaVagaCurso.class, "nivel", nivel, "asc", "descricao");
		carregaListaOfertaVagasCursoGraduacao();
		this.formPage = "/graduacao/oferta_vagas_curso/form_cotas.jsp";
		return forward(formPage);
	}
	
	/** Inicia o processo de cadastro de ofertas de cursos de graduação.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String listarGraduacao() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		iniciarComum();
		this.nivel = NivelEnsino.GRADUACAO;
		this.formaIngresso = FormaIngresso.VESTIBULAR;
		this.idUnidade = 0;
		carregaListaOfertaVagasCursoGraduacao();
		gruposCotas = getGenericDAO().findByExactField(GrupoCotaVagaCurso.class, "nivel", nivel, "asc", "descricao");
		this.formPage = "/graduacao/oferta_vagas_curso/lista.jsp";
		return forward(formPage);
	}

	/** Inicia o processo de cadastro de ofertas de cursos de stricto sensu.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarStricto() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_POS);
		iniciarComum();
		this.nivel = NivelEnsino.STRICTO;
		this.formaIngresso = FormaIngresso.SELECAO_POS_GRADUACAO;
		this.idUnidade = getProgramaStricto().getId();
		carregaListaOfertaVagasCursoStricto();
		this.formPage = "/stricto/oferta_vagas_curso/form.jsp";
		return forward(formPage);
	}

	/** Inicia o processo de cadastro de ofertas de cursos de lato sensu.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarLato() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);
		iniciarComum();
		this.nivel = NivelEnsino.LATO;
		this.formaIngresso = FormaIngresso.SELECAO_POS_GRADUACAO;
		this.idUnidade = getCursoAtualCoordenacao().getUnidade().getId();
		carregaListaOfertaVagasCursoLato();
		this.formPage = "/lato/oferta_vagas_curso/form.jsp";
		return forward(formPage);
	}

	/** Listener responsável por carregar a lista de oferta de vagas quando há mudança
	 * no valor do ano ou processo seletivo.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/oferta_vagas_curso/form.jsp</li>
	 * </ul>
	 * @param evento
	 * @return
	 * @throws DAOException
	 */
	public String carregaListaOfertaVagasCursoGraduacao(ValueChangeEvent evento)
			throws DAOException {
		UIComponent c = evento.getComponent();
		if (c.getId().equalsIgnoreCase("ano")) {
			this.ano = (Integer) evento.getNewValue();
		} else if (c.getId().equalsIgnoreCase("formaIngresso")) {
			this.formaIngresso.setId((Integer) evento.getNewValue());
		} else if (c.getId().equalsIgnoreCase("unidade")) {
			this.idUnidade = (Integer) evento.getNewValue();	
		} else if (c.getId().equalsIgnoreCase("matriz")) {
			this.apenasMatrizesAtivas = (Boolean) evento.getNewValue();
		} else if (c.getId().equalsIgnoreCase("modalidadeEducacao")) {
			this.modalidadeEducacao = (Integer) evento.getNewValue();
		}
		carregaListaOfertaVagasCursoGraduacao();
		return redirectMesmaPagina();
	}

	/** Carrega a lista de oferta de vagas de cursos de graduação.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/stricto/oferta_vagas_curso/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void carregaListaOfertaVagasCursoGraduacao() throws DAOException {
		listaOfertaVagasCurso = new ArrayList<OfertaVagasCurso>();
		// para carregar a lista é necessário ter uma unidade
		if (idUnidade == 0) {
			return;
		}
		MatrizCurricularDao matrizCurricularDao = getDAO(MatrizCurricularDao.class);
		OfertaVagasCursoDao ofertaVagasCursoDao = getDAO(OfertaVagasCursoDao.class);
		// se for entrada sem processo seletivo
		Collection<OfertaVagasCurso> listaOfertaCadastradas;
		listaOfertaCadastradas = ofertaVagasCursoDao .findAllByAnoFormaIngressoUnidadeProcessoSeletivo(ano, null,
				formaIngresso.getId(), idUnidade, modalidadeEducacao , apenasMatrizesAtivas);
		Collection<PoloCurso> listaPoloCurso = null;
		Collection<OfertaVagasCurso> todasOfertas = new LinkedList<OfertaVagasCurso>();
		if (isEAD())
			listaPoloCurso = ofertaVagasCursoDao.findAll(PoloCurso.class);
		for (MatrizCurricular matrizCurricular : matrizCurricularDao.findByUnidade(idUnidade, modalidadeEducacao , apenasMatrizesAtivas)) {
			if (listaPoloCurso != null) {
				for (PoloCurso poloCurso : listaPoloCurso) {
					if (poloCurso.getCurso().getId() == matrizCurricular.getCurso().getId()) {
						OfertaVagasCurso oferta = new OfertaVagasCurso();
						oferta.setCurso(matrizCurricular.getCurso());
						oferta.setMatrizCurricular(matrizCurricular);
						oferta.setPolo(poloCurso.getPolo());
						todasOfertas.add(oferta);
						// evita lazyException
						poloCurso.getPolo().getCidade().getNomeUF();
					}
				}
			} else {
				OfertaVagasCurso oferta = new OfertaVagasCurso();
				oferta.setCurso(matrizCurricular.getCurso());
				oferta.setMatrizCurricular(matrizCurricular);
				todasOfertas.add(oferta);
			}
		}
		// faz um merge das ofertas cadastradas e das a serem cadastradas
		for (OfertaVagasCurso oferta : todasOfertas) {
			// oferta já cadastrada?
			boolean cadastrada = false;
			for (OfertaVagasCurso ofertaCadastrada : listaOfertaCadastradas) {
				if (ofertaCadastrada.equals(oferta)) {
					listaOfertaVagasCurso.add(ofertaCadastrada);
					cadastrada = true;
					break;
				}
			}
			if (!cadastrada) {
				listaOfertaVagasCurso.add(oferta);
			}
		}
		// recupera as informações de cotas de vagas
		carregaCotasVagasCurso();
	}

	/** Carrega os grupos de cotas das oferta de vagas
	 * @throws DAOException
	 */
	private void carregaCotasVagasCurso() throws DAOException {
		if (gruposCotas != null) { 
			for (OfertaVagasCurso oferta : listaOfertaVagasCurso) {
				for (GrupoCotaVagaCurso grupoCota : gruposCotas) {
					boolean contem = false;
					for (CotaOfertaVagaCurso cota : oferta.getCotas()) {
						if (cota.getGrupoCota().getId() == grupoCota.getId()) {
							contem = true; break;
						}
					}
					if (!contem) {
						CotaOfertaVagaCurso cota = new CotaOfertaVagaCurso();
						cota.setGrupoCota(grupoCota);
						cota.setOfertaVagasCurso(oferta);
						oferta.addCota(cota);
					}
				}
			}
		}
	}
	
	/** Lista de oferta de vagas de cursos de graduação.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/stricto/oferta_vagas_curso/lista.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public String listarOfertaVagasCursoGraduacao() throws DAOException {
		listaOfertaVagasCurso = new ArrayList<OfertaVagasCurso>();
		// para carregar a lista é necessário ter uma unidade
		OfertaVagasCursoDao ofertaVagasCursoDao = getDAO(OfertaVagasCursoDao.class);
		// se for entrada sem processo seletivo
		listaOfertaVagasCurso = (List<OfertaVagasCurso>) ofertaVagasCursoDao.findAllByAnoFormaIngressoUnidadeProcessoSeletivo(ano, null, formaIngresso.getId(), idUnidade, modalidadeEducacao , apenasMatrizesAtivas);
		
		if (isEmpty(listaOfertaVagasCurso))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		else if (isEAD()){
			// evita lazyexception
			for (OfertaVagasCurso oferta : listaOfertaVagasCurso)
				oferta.getPolo().getCidade().getNome();
		}
		return redirectMesmaPagina();
	}

	/** Carrega a lista de oferta de vagas de cursos de stricto sensu.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/stricto/oferta_vagas_curso/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void carregaListaOfertaVagasCursoStricto() throws DAOException {
		listaOfertaVagasCurso = new ArrayList<OfertaVagasCurso>();
		CursoDao cursoDao = getDAO(CursoDao.class);
		OfertaVagasCursoDao ofertaVagasCursoDao = getDAO(OfertaVagasCursoDao.class);
		Collection<OfertaVagasCurso> listaOfertaCadastradas = ofertaVagasCursoDao
				.findAllByAnoFormaIngressoUnidade(ano, formaIngresso.getId(), NivelEnsino.STRICTO, idUnidade);
		for (Curso curso : cursoDao.findByNivel(NivelEnsino.STRICTO, true,
				false, getProgramaStricto())) {
			// oferta já cadastrada?
			boolean cadastrada = false;
			for (OfertaVagasCurso ofertaCadastrada : listaOfertaCadastradas) {
				if (ofertaCadastrada.getCurso().getId() == curso.getId()) {
					listaOfertaVagasCurso.add(ofertaCadastrada);
					cadastrada = true;
					break;
				}
			}
			if (!cadastrada) {
				OfertaVagasCurso oferta = new OfertaVagasCurso();
				oferta.setCurso(curso);
				listaOfertaVagasCurso.add(oferta);
			}
		}
	}

	/** Carrega a lista de oferta de vagas de curso de lato sensu.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/lato/oferta_vagas_curso/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void carregaListaOfertaVagasCursoLato() throws DAOException {
		listaOfertaVagasCurso = new ArrayList<OfertaVagasCurso>();
		OfertaVagasCursoDao ofertaVagasCursoDao = getDAO(OfertaVagasCursoDao.class);
		Collection<OfertaVagasCurso> listaOfertaCadastradas = ofertaVagasCursoDao
				.findAllByAnoFormaIngressoUnidade(ano, formaIngresso.getId(), NivelEnsino.LATO, idUnidade);
		Curso curso = getCursoAtualCoordenacao();
		formaIngresso = ofertaVagasCursoDao.findByPrimaryKey(formaIngresso
				.getId(), FormaIngresso.class);
		// oferta já cadastrada?
		boolean cadastrada = false;
		for (OfertaVagasCurso ofertaCadastrada : listaOfertaCadastradas) {
			if (ofertaCadastrada.getCurso().getId() == curso.getId()) {
				listaOfertaVagasCurso.add(ofertaCadastrada);
				cadastrada = true;
				break;
			}
		}
		if (!cadastrada) {
			OfertaVagasCurso oferta = new OfertaVagasCurso();
			oferta.setCurso(curso);
			listaOfertaVagasCurso.add(oferta);
		}
	}

	/** Cadastra as ofertas de vagas
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/oferta_vagas_curso/form.jsp</li>
	 * 		<li>/sigaa.war/lato/oferta_vagas_curso/form.jsp</li>
	 * 		<li>/sigaa.war/stricto/oferta_vagas_curso/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		if (obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		validacaoDados(erros);
		if (hasErrors()) return null;
		MovimentoCadastroOfertaVagasCurso movimento = new MovimentoCadastroOfertaVagasCurso();
		movimento.setListaOfertaVagasCurso(listaOfertaVagasCurso);
		movimento.setAno(ano);
		movimento.setFormaIngresso(formaIngresso);
		movimento
				.setCodMovimento(SigaaListaComando.CADASTRAR_OFERTA_VAGAS_CURSO);
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_OFERTA_VAGAS_CURSO);
			executeWithoutClosingSession(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (this.nivel == NivelEnsino.GRADUACAO)
			carregaListaOfertaVagasCursoGraduacao();
		else if (this.nivel == NivelEnsino.LATO)
			carregaListaOfertaVagasCursoLato();
		else if (this.nivel == NivelEnsino.STRICTO)
			carregaListaOfertaVagasCursoStricto();
		addMensagemInformation("Oferta de vagas cadastradas com sucesso.");
		return forward(formPage);
	}

	/** Retorna uma coleção de SelectItem de formas de ingresso.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTipoEntradaCombo() throws DAOException {
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (FormaIngresso formaIngresso : dao.findAll(FormaIngresso.class,
				"descricao", "asc")) {
			combo.add(new SelectItem(formaIngresso.getId(), formaIngresso
					.getDescricao()));
		}
		return combo;
	}

	/** Retorna uma coleção de SelectItem dos anos cadastradas, incluindo o ano corrente e o próximo ano.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAnosCombo() throws DAOException {
		Collection<SelectItem> combo = new ArrayList<SelectItem>(); 
		int proximoAno = CalendarUtils.getAnoAtual() + 1;
		for (int i = proximoAno; i >= 2000; i--) {
			combo.add(new SelectItem(new Integer(i), String.valueOf(i)));
		}
		return combo;
	}

	/** Retorna a forma de ingresso da oferta do curso. 
	 * @return
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/** Seta a forma de ingresso da oferta do curso.
	 * @param formaIngresso
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}
	
	/** Valida os dados: número de vagas ofertadas para entrada no primeiro e segundo período.  
	 * Não invocado por JSP(s)
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		int anoAtual = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
		if (listaOfertaVagasCurso != null) {
			for (OfertaVagasCurso oferta : listaOfertaVagasCurso) {
				if (oferta.getVagasPeriodo1() < 0 || oferta.getVagasPeriodo2() < 0 ||
						oferta.getVagasOciosasPeriodo1() < 0 || oferta.getVagasOciosasPeriodo2() < 0) {
					mensagens.addErro("Infome um valor válido para o número de vagas de " + oferta.getMatrizCurricular().getDescricao());					
				}
				if ( ano >= anoAtual ) {
					if ( oferta.getCurso().isGraduacao() && !oferta.getMatrizCurricular().getAtivo() ){
										
						if ( oferta.getVagasPeriodo1() > 0 || oferta.getVagasOciosasPeriodo1() > 0 ||
							 oferta.getVagasPeriodo2() > 0 || oferta.getVagasOciosasPeriodo2() > 0
						   )
							mensagens.addWarning("O Curso / Matriz Curricular: " + oferta.getMatrizCurricular().getDescricao() + " está inativo, " +
									"no entanto, foram cadastradas vagas para ele.");
					}
				}
				int cotasPeriodo1 = 0, cotasPeriodo2 = 0;
				for (CotaOfertaVagaCurso cota : oferta.getCotas()) {
					if (cota.getVagasPeriodo1() < 0 || cota.getVagasPeriodo2() < 0) {
						mensagens.addErro("Infome um valor válido para as cotas de " + oferta.getMatrizCurricular().getDescricao());					
					}
					cotasPeriodo1 += cota.getVagasPeriodo1();
					cotasPeriodo2 += cota.getVagasPeriodo2();
				}
				if (cotasPeriodo1 > oferta.getVagasPeriodo1() || cotasPeriodo2 > oferta.getVagasPeriodo2()) {
					mensagens.addErro("O total de vagas nas cotas é maior que o total de vagas ofertadas no curso "  + oferta.getMatrizCurricular().getDescricao());
				}
			}
		}
		return mensagens.isErrorPresent();
	}

	/**
	 * Retorna combo com as situações das Matrizes Curriculares.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> sigaa.war/graduacao/oferta_vagas_curso/form.jsp
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getMatrizCurricularCombo() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(MATRIZES_CURRICULARES_INATIVAS, "INATIVAS"));
		result.add(new SelectItem(MATRIZES_CURRICULARES_ATIVAS, "ATIVAS"));
		return result;
	}
	
	/**
	 * Retorna combo com as modalidades de educação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> sigaa.war/graduacao/oferta_vagas_curso/form.jsp
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getModalidadeEducacaoCombo() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(ModalidadeEducacao.PRESENCIAL, "PRESENCIAL"));
		result.add(new SelectItem(ModalidadeEducacao.A_DISTANCIA, "À DISTÂNCIA"));
		result.add(new SelectItem(ModalidadeEducacao.SEMI_PRESENCIAL, "SEMI-PRESENCIAL"));

		return result;
	}

	public void setApenasMatrizesAtivas(Boolean apenasMatrizesAtivas) {
		this.apenasMatrizesAtivas = apenasMatrizesAtivas;
	}

	public Boolean getApenasMatrizesAtivas() {
		return apenasMatrizesAtivas;
	}

	public void setModalidadeEducacao(Integer modalidadeEducacao) {
		this.modalidadeEducacao = modalidadeEducacao;
	}

	public Integer getModalidadeEducacao() {
		return modalidadeEducacao;
	}

	public boolean isEAD() {
		return modalidadeEducacao != null && modalidadeEducacao == ModalidadeEducacao.A_DISTANCIA;
	}

	public List<OfertaVagasCurso> getListaOfertaVagasCurso() {
		return listaOfertaVagasCurso;
	}

	public Collection<GrupoCotaVagaCurso> getGruposCotas() {
		return gruposCotas;
	}
}
