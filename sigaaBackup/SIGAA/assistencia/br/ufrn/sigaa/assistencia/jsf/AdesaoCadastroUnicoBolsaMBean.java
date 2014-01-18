/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 01/04/2009
 *
 */
package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.negocio.MovimentoPerfilPessoa;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.dao.sae.CadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.cadunico.dominio.ContatoFamilia;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.cadunico.dominio.ItemConfortoFamiliar;
import br.ufrn.sigaa.assistencia.cadunico.dominio.QuantidadeItemConfortoCadastroUnico;
import br.ufrn.sigaa.assistencia.negocio.MovimentoAdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.portal.jsf.PerfilDiscenteMBean;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 * MBean respons�vel por gerenciar a ades�o do discente ao programa de cadastro de bolsa �nica
 * 
 * @author Henrique Andr�
 *
 */
@Component("adesaoCadastroUnico") @Scope("session")
public class AdesaoCadastroUnicoBolsaMBean extends SigaaAbstractController<AdesaoCadastroUnicoBolsa> implements OperadorDiscente {

	private final String VER_DADOS_QUESTIONARIO = "/sae/cadastro_unico/adesao/visualizar_respostas.jsf";
	private final String FORM_ALTERAR_JSP = "/sae/cadastro_unico/adesao/form_alterar_adesao.jsp";
	private final String LISTA_ADESOES_JSP = "/sae/cadastro_unico/adesao/lista_adesoes.jsp";
	private final String FORM_PERFIL_JSP = "/sae/cadastro_unico/adesao/form_perfil.jsp";
	private final String PROGRAMA_APRENSETACAO_JSP = "/sae/cadastro_unico/adesao/aprensentacao_programa.jsp";
	private final String FORMULARIO_QUESTIONARIO_JSP = "/sae/cadastro_unico/adesao/form_questionario.jsp";
	private final String ENDERECO_FAMILIA_JSP = "/sae/cadastro_unico/adesao/form_endereco_familia.jsp";
	
	private boolean exibirBotaoVoltar; 
	
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	private DiscenteAdapter discente;
	
	private Integer idDiscenteVisualizacaoCadastro;
	
	/**
	 * Atributo que indica se o usu�rio concorda com os termos de participa��o
	 */
	private boolean termoConcordancia;	

	/**
	 * Atributo que indica se o usu�rio est� tentando renovar a bolsa
	 */
	private boolean renovacaoBolsa;	

	/**
	 * Verificar se o endere�o da fam�lia � a mesma do discente
	 */
	private boolean mesmoEndereco;

	/**
	 * Listagem que representa os itens de conforto familiar e suas respectivas quantidades
	 */
	private List<QuantidadeItemConfortoCadastroUnico> lista;
	
	/**
	 * Discente pode criar/alterar seu perfil durante o cadastro �nico
	 */
	private PerfilPessoa perfil;
	
	/**
	 * Listagem de ades�es ao cadastro �nico
	 */
	private List<AdesaoCadastroUnicoBolsa> listaAdesao;
	
	/**
	 * Mbean que invocou a Ades�o
	 */
	private AdesaoCadastroUnicoBolsaFluxoNavegacao mBeanFluxo;
	
	/**
	 * Inicializa o objeto
	 */
	private void init() {
		termoConcordancia = false;
		obj = new AdesaoCadastroUnicoBolsa();
		obj.setContatoFamilia(new ContatoFamilia());
		obj.getContatoFamilia().clear();
	}

	/**
	 * Inicia o processo de ades�o do aluno ao programa de 
	 * cadastro �nico de bolsa
	 * JSP: sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String apresentacaoCadastroUnicoRenovacao() throws ArqException {
		init();
		renovacaoBolsa = true;
		return iniciarAdesao();
	}
	
	/**
	 * Inicia o processo de ades�o do aluno ao programa de 
	 * cadastro �nico de bolsa
	 * JSP: sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String apresentacaoCadastroUnico() throws ArqException {
		init();
		renovacaoBolsa = false;
		return iniciarAdesao();
	}

	private String iniciarAdesao() throws DAOException, ArqException {
		if (getCalendarioVigente() == null) {
			addMensagemErro("O Calend�rio Acad�mico n�o foi localizado.");
			return null;
		}
		
		if (!getDiscenteUsuario().isAtivo() && !getDiscenteUsuario().isCadastrado(getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo())) {
			addMensagemErro("Somente discente ativos ou cadastrados podem realizar a ades�o.");
			return cancelar();
		}
		
		if (isCadastroUnico()) {
			addMensagemErro("Caro aluno, sua ades�o ao cadastro �nico j� foi realizada.");
			return null;
		}
		
		CadastroUnicoBolsaDao dao = getDAO(CadastroUnicoBolsaDao.class);
		FormularioCadastroUnicoBolsa emVigor = dao.findUnicoAtivo();
		
		if (isEmpty(emVigor)) {
			addMensagemErro("SAE ainda n�o abriu o Cadastro �nico.");
			return cancelar();
		}				
		
		Questionario questionario = emVigor.getQuestionario();
		
		if (isEmpty(questionario)) {
			addMensagemErro("A ades�o ao cadastro �nico n�o pode ser iniciado porque n�o existe question�rio definido.");
			return cancelar();
		}		
		
		obj.setAno(getCalendarioVigente().getAno());
		obj.setPeriodo(getCalendarioVigente().getPeriodo());
		obj.setCadastroUnico(emVigor);
		obj.setDiscente(getDiscenteUsuario().getDiscente());		
		
		prepareMovimento(SigaaListaComando.ADESAO_CADASTRO_UNICO);
		return forward(PROGRAMA_APRENSETACAO_JSP);
	}
	

	/**
	 * Informa o endere�o da fam�lia
	 * JSP: N�o invocado por jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarEndereco() throws DAOException {
		
		carregarMunicipiosEndereco(null);
		
		discente = getDiscenteUsuario();
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		discente.setPessoa(pessoaDao.findCompleto(discente.getPessoa().getId()));

		return forward(ENDERECO_FAMILIA_JSP);
	}
	
	/**
	 * Inicia o processo de defini��o do perfil do aluno.
	 * JSP: /sigaa.war/sae/cadastro_unico/aprensentacao_programa.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarPerfil() throws ArqException {
		
		if (!termoConcordancia) {
			addMensagemErro("Para continuar � necess�rio concordar com os termos.");
			return null;
		}
		
		prepareMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		
		PerfilDiscenteMBean mBean = getMBean("perfilDiscente");
		perfil = mBean.getPerfilUsuario();
		
		if (isEmpty(perfil)) 
			perfil = new PerfilPessoa();
		
		return forward(FORM_PERFIL_JSP);
	}
	
	/**
	 * Atualiza o perfil do aluno
	 * JSP: /sigaa.war/sae/cadastro_unico/form_perfil.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gravarPerfil() throws ArqException {
		
		ListaMensagens listaErros = new ListaMensagens(); 
		listaErros = validarPerfil();
		
		if (!listaErros.isEmpty()){
			addMensagens(listaErros);
			return null;
		}
		
		PerfilDiscenteMBean mBean = getMBean("perfilDiscente");
		
		prepareMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		mBean.setTipoPerfil(perfil);

		MovimentoPerfilPessoa perfilMov = new MovimentoPerfilPessoa();
		perfilMov.setCodMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		perfilMov.setPerfil(perfil);
		
		try {
			execute(perfilMov, getCurrentRequest());
		}catch (NegocioException e) {
			return tratamentoErroPadrao(e, "Ocorreu um erro ao tentar atualizar seu perfil.");
		}

		mBean.setPerfilPortal(perfil);

		return iniciarEndereco();
	}
	
	/**
	 * Verifica se o discente deixou campos vazios no perfil
	 */
	private ListaMensagens validarPerfil() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(perfil.getDescricao(), "Descri��o", lista);
		ValidatorUtil.validateRequired(perfil.getAreas(), "�reas de Interesse", lista);
		if(!perfil.getEnderecoLattes().isEmpty()){
			ValidatorUtil.validateUrl(perfil.getEnderecoLattes(), "Curr�culo Lattes", lista);
		}
		
		return lista;
	}

	/**
	 * Inicia o question�rio s�cio-econ�mico.
	 * Se o aluno tiver uma ades�o anterior e o question�rio for igual, aproveitamos as respostas
	 * 
	 * JSP: /sae/cadastro_unico/aprensentacao_questionario.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarQuestionario() throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		
		if (mesmoEndereco) {
			validarEndereco();
		} else {
			if (getDiscenteUsuario().getPessoa().getEnderecoContato() != null) {
				obj.getContatoFamilia().setEndereco(getDiscenteUsuario().getPessoa().getEnderecoContato());
			}
		}
		
		if (hasErrors())
			return null;
		
		AdesaoCadastroUnicoBolsa adesaoAnterior = localizarUltimaAdesao();
		
		Questionario q = null;
		if (adesaoAnterior != null) {
			QuestionarioRespostasDao qRDao = null;
			try {
				qRDao = getDAO(QuestionarioRespostasDao.class);
				QuestionarioRespostas qR = qRDao.findByAdesao(adesaoAnterior);
				q = qR.getQuestionario();
				
			} finally {
				if (qRDao != null)
						qRDao.close();
			}
		}

		// Evitar lazy
		obj.setCadastroUnico(dao.findByPrimaryKey(obj.getCadastroUnico().getId(), FormularioCadastroUnicoBolsa.class));
		
		if (isMesmoQuestionario(q, obj.getQuestionario())) {
			getQuestionarioRespostasMBean().inicializarReaproveitarAdesao(obj, adesaoAnterior);
			reaproveitarConfortoFamiliar(adesaoAnterior);
		}
		else {
			Collection<ItemConfortoFamiliar> itensConforto = getGenericDAO().findAllAtivos(ItemConfortoFamiliar.class, "item");
			populaListaItensConfortoFamiliar(itensConforto);
			getQuestionarioRespostasMBean().inicializar(obj);
		}
		
		return forward(FORMULARIO_QUESTIONARIO_JSP);
	}

	/**
	 * Reaproveita as respostas do conforto familiar da ades�o passado
	 * 
	 * @param adesaoAnterior
	 */
	private void reaproveitarConfortoFamiliar(AdesaoCadastroUnicoBolsa adesaoAnterior) {
		lista = adesaoAnterior.getListaConfortoFamiliar();
		
		for (QuantidadeItemConfortoCadastroUnico qtd  : lista) {
			qtd.setAdesao(obj);
			qtd.setId(0);
		}
	}

	/**
	 * Verifica se a ades�o passada possui o question�rio igual ao atual
	 * 
	 * @param adesaoAnterior
	 * @param obj
	 * @return
	 */
	private boolean isMesmoQuestionario(Questionario qAdesaoAnterior, Questionario qObj) {
		
		if (qAdesaoAnterior == null || qObj == null)
			return false;
		
		return qAdesaoAnterior.equals(obj.getCadastroUnico().getQuestionario());
	}

	/**
	 * Localiza a �ltima ades�o do discente
	 * 
	 * @return
	 * @throws DAOException
	 */
	private AdesaoCadastroUnicoBolsa localizarUltimaAdesao() throws DAOException {
		int anoPeriodo = new Integer(getCalendarioVigente().getAno() + "" + getCalendarioVigente().getPeriodo());
		AdesaoCadastroUnicoBolsaDao adesaoDao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		int id = adesaoDao.findUltimaAdesao(getDiscenteUsuario(), anoPeriodo);
		
		if (isEmpty(id))
			return null;
		
		AdesaoCadastroUnicoBolsa adesaoAnterior = getGenericDAO().findByPrimaryKey(id, AdesaoCadastroUnicoBolsa.class);
		return adesaoAnterior;
	}

	/**
	 * Popula a lista com os itens do conforto familiar
	 * 
	 * @param itensConforto
	 */
	private void populaListaItensConfortoFamiliar(Collection<ItemConfortoFamiliar> itensConforto) {
		
		lista = new ArrayList<QuantidadeItemConfortoCadastroUnico>();
		
		for (ItemConfortoFamiliar itemConfortoFamiliar : itensConforto) {
			 QuantidadeItemConfortoCadastroUnico qtd = new QuantidadeItemConfortoCadastroUnico();
			 qtd.setAdesao(obj);
			 qtd.setItem(itemConfortoFamiliar);
			 qtd.setQuantidade(0);
			 lista.add(qtd);
		}
	}
	
	/**
	 * Validar o endere�o da fam�lia
	 * 
	 * @param lista
	 */
	private void validarEndereco() {
		ValidatorUtil.validateRequired(obj.getContatoFamilia().getEndereco().getCep(), "CEP", erros);
		ValidatorUtil.validateRequired(obj.getContatoFamilia().getEndereco().getLogradouro(), "Logradouro", erros);
		ValidatorUtil.validateRequired(obj.getContatoFamilia().getEndereco().getBairro(), "Bairro", erros);
		ValidatorUtil.validateRequiredId(obj.getContatoFamilia().getEndereco().getTipoLogradouro().getId(), "Tipo de Logradouro", erros);
		ValidatorUtil.validateRequiredId(obj.getContatoFamilia().getEndereco().getUnidadeFederativa().getId(), "UF", erros);
		ValidatorUtil.validateRequiredId(obj.getContatoFamilia().getEndereco().getMunicipio().getId(), "Municipio", erros);
	}

	/**
	 * Discente conclui o question�rio de ades�o
	 * JSP: /sigaa.war/sae/cadastro_unico/form_questionario.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String finalizarQuestionario() throws ArqException {

		// Caso tente voltar pelo browser
		if (obj == null) {
			addMensagemErro("Caro aluno, sua ades�o j� foi realizada.");
			return cancelar();
		}		
		
		getQuestionarioRespostasMBean().validarRepostas();

		if (hasErrors()) {
			return null;
		}
		
		obj.setListaConfortoFamiliar(lista);
		QuestionarioRespostas respostas = getQuestionarioRespostasMBean().getObj();
		
		MovimentoAdesaoCadastroUnicoBolsa mov = new MovimentoAdesaoCadastroUnicoBolsa();
		mov.setAdesaoCadatro(obj);
		mov.setRespostas( respostas );
		mov.setCodMovimento(SigaaListaComando.ADESAO_CADASTRO_UNICO);
		
		try {
			execute(mov);
			addMensagemInformation("Ades�o ao Cadastro �nico efetuada com sucesso. Agora � poss�vel inscrever-se em processos seletivos de bolsas.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			return null;
		}
		resetBean();
		
		if ( renovacaoBolsa ) {
			return forward("/sae/BolsaAuxilio/renovar_bolsa.jsf");
		}
		
		if (mBeanFluxo != null)
			return mBeanFluxo.getUrlDestino();
		
		return cancelar();
		
	}


	
	/**
	 * Iniciar o processo de alterar as informa��es do cadastro �nico do discente
	 * JSP: sigaa.war/sae/menu.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterar() throws ArqException {
		init();
		prepareMovimento(SigaaListaComando.ALTERAR_ADESAO_CADASTRO_UNICO);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DADOS_BOLSISTA_CADASTRO_UNICO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Escolhe o aluno para realizar as altera��es da ades�o
	 * M�todo n�o invocado por jsp's
	 */
	public String selecionaDiscente() throws ArqException {
		
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		listaAdesao = dao.findAllByDiscente(discente.getId());
		
		if (isEmpty(listaAdesao)) {
			addMensagemErro(this.discente.getNome() + " n�o fez o cadastro de ades�o ao Programa de Cadastro �nico em nenhum per�odo.");
			return null;
		}
		
		return forward(LISTA_ADESOES_JSP);
	}

	/**
	 * Escolhe uma ades�o do discente
	 * 
	 * /SIGAA/app/sigaa.ear/sigaa.war/sae/cadastro_unico/adesao/lista_adesoes.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String escolherAdesao() throws DAOException {
		
		Integer id = getParameterInt("id", 0);
		
		AdesaoCadastroUnicoBolsa adesao = getGenericDAO().findByPrimaryKey(id, AdesaoCadastroUnicoBolsa.class);
		
		if (isEmpty(adesao)) {
			addMensagemErro("N�o foi poss�vel carregar a ades�o deste discente.");
			return null;
		}
		
		obj = adesao;
		
		getQuestionarioRespostasMBean().popularVizualicacaoRespostas(adesao);
		obj.getContatoFamilia().clear();
		carregarMunicipiosEndereco(null);
		return forward(FORM_ALTERAR_JSP);
	}
	
	/**
	 * Persiste a altera��o feita no discente
	 * JSP: /sigaa.war/sae/cadastro_unico/form_endereco_familia.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String alterarAdesaoDiscente() throws ArqException {
		
		getQuestionarioRespostasMBean().validarRepostas();

		if (hasErrors()) {
			return null;
		}
		
		QuestionarioRespostas respostas = getQuestionarioRespostasMBean().getObj();
		
		MovimentoAdesaoCadastroUnicoBolsa mov = new MovimentoAdesaoCadastroUnicoBolsa();
		mov.setAdesaoCadatro(obj);
		mov.setRespostas( respostas );
		mov.setCodMovimento(SigaaListaComando.ALTERAR_ADESAO_CADASTRO_UNICO);
		try {
			execute(mov);
			addMensagemInformation("Altera��o da ades�o feita com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			return null;
		}		
		
		
		return cancelar();
	}
	
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
	}
	
	/**
	 * Pega as respostas do question�rio
	 * 
	 * @return
	 */
	private QuestionarioRespostasMBean getQuestionarioRespostasMBean() {
		return (QuestionarioRespostasMBean) getMBean("questionarioRespostasBean");
	}

	public boolean isTermoConcordancia() {
		return termoConcordancia;
	}

	public void setTermoConcordancia(boolean termoConcordancia) {
		this.termoConcordancia = termoConcordancia;
	}

	public boolean isMesmoEndereco() {
		return mesmoEndereco;
	}

	public void setMesmoEndereco(boolean mesmoEndereco) {
		this.mesmoEndereco = mesmoEndereco;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/**
	 * Carrega munic�pios na tela de cadastro
	 * JSP: sigaa.war/sae/cadastro_unico/form.jsp
	 * /sigaa.war/sae/cadastro_unico/form_endereco_familia.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {

		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();

			if (selectId.toLowerCase().contains("ufend")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}	
	
	/**
	 * Carrega munic�pios de um estado passado como par�metro
	 * JSP: sigaa.war/sae/cadastro_unico/form.jsp
	 * /sigaa.war/sae/cadastro_unico/form_endereco_familia.jsp
	 *
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {

		if (idUf == null) {
			idUf = obj.getContatoFamilia().getEndereco().getUnidadeFederativa().getId();
		}
		
		
		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf
				.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));

		obj.getContatoFamilia().getEndereco().setUnidadeFederativa(uf);
	
	}

	/**
	 * Visualizar os dados que o discente respondeu durante a ades�o do cadastro �nico
	 * N�o invado por JSP
	 * 
	 * @param id 
	 * @param req
	 * @param res
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String visualizarQuestionarioDiscente() throws DAOException, SegurancaException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);

		Discente aluno = dao.findByPrimaryKey(idDiscenteVisualizacaoCadastro, Discente.class);
		
		CalendarioAcademico calendarioAcademico = CalendarioAcademicoHelper.getCalendario(aluno);
		
		int anoPeriodo = new Integer(calendarioAcademico.getAno() + "" + calendarioAcademico.getPeriodo());
		
		int idAdesao = dao.findUltimaAdesao(aluno, anoPeriodo);
		
		if (isEmpty(idAdesao)) {
			addMensagemErro("Este aluno n�o possui ades�o ao cadastro �nico.");
			return redirectSemContexto("/sipac/telaInicial.do");
		}
		
		AdesaoCadastroUnicoBolsa adesao = dao.findByPrimaryKey(idAdesao, AdesaoCadastroUnicoBolsa.class);
		adesao.getListaConfortoFamiliar().iterator();
		getQuestionarioRespostasMBean().popularVizualicacaoRespostas(adesao);
		
		lista = adesao.getListaConfortoFamiliar();
		
		return redirect(VER_DADOS_QUESTIONARIO);
	}	

	/**
	 * Exibe p�gina para visualiza��o das respostas dos alunos ao question�rio do Cadastro �nico.
	 * A princ�pio usada por usu�rios com papel SAE_VISUALIZAR_QUESTIONARIO_ADESAO_DISCENTE
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String visualizarQuestionario() throws DAOException, SegurancaException {
		Integer id = getParameterInt("id", 0);
		AdesaoCadastroUnicoBolsa adesao = getGenericDAO().findByPrimaryKey(id, AdesaoCadastroUnicoBolsa.class);
		
		if (isEmpty(adesao)) {
			addMensagemErro("N�o foi poss�vel carregar a ades�o deste discente.");
			return null;
		}
		
		getQuestionarioRespostasMBean().popularVizualicacaoRespostas(adesao);
		lista = adesao.getListaConfortoFamiliar();
		
		setExibirBotaoVoltar(true);
		return forward(VER_DADOS_QUESTIONARIO);
	}
	
	/**
	 * Redireciona usu�rio para o SIPAC
	 * JSP: /sigaa.war/sae/cadastro_unico/adesao/visualizar_respostas.jsp
	 * @return
	 */
	public String voltarAdministrativo() {
		redirect("/entrarSistema.do?sistema=sipac&url=/sipac/portal_administrativo/index.jsf");
		return null;
	}	
	
	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	public List<QuantidadeItemConfortoCadastroUnico> getLista() {
		return lista;
	}

	public void setLista(List<QuantidadeItemConfortoCadastroUnico> lista) {
		this.lista = lista;
	}

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public List<AdesaoCadastroUnicoBolsa> getListaAdesao() {
		return listaAdesao;
	}

	public void setListaAdesao(List<AdesaoCadastroUnicoBolsa> listaAdesao) {
		this.listaAdesao = listaAdesao;
	}

	public AdesaoCadastroUnicoBolsaFluxoNavegacao getMBeanFluxo() {
		return mBeanFluxo;
	}

	public void setMBeanFluxo(AdesaoCadastroUnicoBolsaFluxoNavegacao beanFluxo) {
		mBeanFluxo = beanFluxo;
	}

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public Integer getIdDiscenteVisualizacaoCadastro() {
		return idDiscenteVisualizacaoCadastro;
	}

	public void setIdDiscenteVisualizacaoCadastro(
			Integer idDiscenteVisualizacaoCadastro) {
		this.idDiscenteVisualizacaoCadastro = idDiscenteVisualizacaoCadastro;
	}

	public boolean isRenovacaoBolsa() {
		return renovacaoBolsa;
	}

	public void setRenovacaoBolsa(boolean renovacaoBolsa) {
		this.renovacaoBolsa = renovacaoBolsa;
	}
	
}
