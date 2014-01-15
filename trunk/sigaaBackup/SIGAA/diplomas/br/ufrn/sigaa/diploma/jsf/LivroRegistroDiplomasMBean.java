/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/06/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.diploma.dao.FolhaRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.LivroRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.FolhaRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/** Controller responsável pelas operações de cadastro / alteração de livros de registro de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("livroRegistroDiplomas")
@Scope("session")
public class LivroRegistroDiplomasMBean extends SigaaAbstractController<LivroRegistroDiploma> {
	
	/** ID do curso a ser adicionado na lista de cursos registrados no livro. */
	private int idCurso;
	/** Indica que a busca por livros deve restringir por título. */
	private boolean buscaTitulo;
	/** Indica que a busca por livros deve restringir por curso. */
	private boolean buscaCurso;
	/** Indica que a busca por livros deve restringir por situação (aberto/fechado). */
	private boolean buscaSituacao;
	/** Indica que a busca por livros deve restringir por tipo interno/externo. */
	private boolean buscaExterno;
	/** Indica que a busca por livros deve restringir por livro antigo/novo. */
	private boolean buscaAntigo;
	/** Indica que a busca por livros deve restringir por nível de ensino. */
	private boolean buscaNivel;
	
	/** Construtor padrão. */
	public LivroRegistroDiplomasMBean() {
		obj = new LivroRegistroDiploma();
		initObj();
	}
	
	/** Link do formulário de cadastro de livro. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/diplomas/livro_registro_diploma/form.jsp";
	}
	
	/** Link da lista de livros. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/diplomas/livro_registro_diploma/lista.jsp";
	}
	
	/** Link do formulário de visualização de dados do livro. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getViewPage()
	 */
	@Override
	public String getViewPage() {
		return "/diplomas/livro_registro_diploma/view.jsp";
	}

	/**
	 * Adiciona um curso à lista de cursos registrados no livro.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void adicionarCurso() throws DAOException {
		ValidatorUtil.validateRequiredId(idCurso, "Curso", erros);
		if (hasErrors())
			return;
		// verifica se existe livro aberto que registra o curso escolhido
		LivroRegistroDiplomaDao livroDao = getDAO(LivroRegistroDiplomaDao.class);
		LivroRegistroDiploma livro = livroDao.findByCurso(idCurso, true, this.obj.isLivroAntigo());
		if (livro != null && livro.getId() != obj.getId()) {
			Curso curso = livroDao.findByPrimaryKey(idCurso, Curso.class);
			addMensagem(MensagensGraduacao.CURSO_REGISTRADO_EM_OUTRO_LIVRO_ABERTO, curso.getDescricao(), livroDao.findByCurso(idCurso, true, obj.isLivroAntigo()).getTitulo());
			return;
		}
		// adiciona o curso à lista
		if (obj.getCursosRegistrados() == null) {
			obj.setCursosRegistrados(new ArrayList<Curso>());
		}
		Curso curso = getGenericDAO().findByPrimaryKey(idCurso, Curso.class);
		if (obj.getCursosRegistrados().contains(curso)) {
			addMensagem(MensagensGraduacao.CURSO_JA_INCLUIDO);
			return;
		}
		obj.getCursosRegistrados().add(curso);
	}
	
	/** Seleciona o nível de ensino no formulário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/form.jsp</li>
	 * </ul>
	 * @param evt
	 */
	public String nivelListener(ValueChangeEvent evt) {
		obj.setNivel((Character) evt.getNewValue());
		return forward("/diplomas/livro_registro_diploma/form.jsp");
	}
		
	
	/**
	 * Inicia o processo de cadastro de um livro.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		checkChangeRole();
		return super.preCadastrar();
	}
	
	/**
	 * Inicializa os atributos do controller.  
	 */
	private void initObj(){
		this.buscaTitulo = false;
		this.buscaCurso = false;
		this.buscaSituacao = false;
		this.buscaExterno = false;
		this.buscaAntigo = false;
		this.buscaNivel = false;
		this.idCurso = 0;
		this.obj = new LivroRegistroDiploma();
		this.resultadosBusca = new ArrayList<LivroRegistroDiploma>();
		// valor padrão de registros por folha:
		this.obj.setNumeroRegistroPorFolha(4);
		if (getNiveisHabilitados().length == 1)
			obj.setNivel(getNiveisHabilitados()[0]);
	}

	/**
	 * Remove um curso da lista de cursos registrados no livro. O curso será
	 * removido do livro somente se não houver diploma de aluno do mesmo curso
	 * registrado.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/form.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String removerCurso() throws ArqException{
		if (obj == null || obj.getCursosRegistrados() == null || obj.getCursosRegistrados().isEmpty()) {
			return forward(getFormPage());
		}
		String id = getCurrentRequest().getParameter("id");
		if (id == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		int idCurso = Integer.parseInt(id);
		Curso curso = getGenericDAO().findByPrimaryKey(idCurso, Curso.class);
		// verifica se há registro de diploma de algum aluno do curso a ser removido
		for (FolhaRegistroDiploma folha : obj.getFolhas()){
			for (RegistroDiploma registro : folha.getRegistros())
				if (registro.getDiscente() != null && registro.getDiscente().getCurso().getId() == curso.getId()) {
					addMensagemErro("Não foi possível remover o curso "+curso.getDescricao()+": existe um ou mais diplomas de alunos do mesmo registrado no livro.");
					return null;
				}
		}
		obj.getCursosRegistrados().remove(curso);
		return forward(getFormPage());
	}

	/** Retorna o ID do curso a ser adicionado na lista de cursos registrados no livro.  
	 * @return
	 */
	public int getIdCurso() {
		return idCurso;
	}

	/** Seta o ID do curso a ser adicionado na lista de cursos registrados no livro.
	 * @param idCurso
	 */
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	/**
	 * Fecha o livro, isto é, não permite mais a inserção de registros nele.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String fecharLivro() throws SegurancaException, ArqException, NegocioException {
		populateObj(true);
		if (!obj.isAtivo()) {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA);
			return null;
		}
		// otimizando a consulta do número de páginas.
		FolhaRegistroDiplomaDao dao = getDAO(FolhaRegistroDiplomaDao.class);
		Collection<FolhaRegistroDiploma> folhas = dao.findByLivro(obj);
		obj.setFolhas(folhas);
		obj.setAtivo(false);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		setConfirmButton("Fechar livro");
		return forward(getViewPage());
	}

	/**
	 * Imprime as folhas de um livro.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String imprimirLivro() throws DAOException{
		populateObj(true);
		if (obj != null) {
			// Gerar relatório
			Connection con = null;
			try {
				// Popular parâmetros do relatório
				HashMap<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("idLivro", obj.getId());
				parametros.put("ifes", RepositorioDadosInstitucionais.get("nomeInstituicao").toUpperCase());
				InputStream relatorio;
				if (obj.isRegistroCertificado()) {
					relatorio = JasperReportsUtil.getReportSIGAA("Livro_Registro_Certificado.jasper");
				} else {
					relatorio = JasperReportsUtil.getReportSIGAA("Livro_Registro_Diploma.jasper");
				}
				// Preencher relatório
				con = Database.getInstance().getSigaaConnection();
				JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con);
				if (prt.getPages().size() == 0) {
					addMensagem(MensagensGerais.RELATORIO_VAZIO, "livro");
					return null;
				}
				// Exportar relatório de acordo com o formato escolhido
				String nomeArquivo = obj.getTitulo() + ".pdf";
				JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				tratamentoErroPadrao(e);
				notifyError(e);
				return null;
			} finally {
				Database.getInstance().close(con);
			}
		}
		return null;		
	}

	/**
	 * Retorna o link para a página de listagem de livros.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		return super.listar();
	}

	/**
	 * Atualiza os dados do livro de registro de diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
			LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
			obj = new LivroRegistroDiploma();
			setId();
			obj = dao.findByPrimaryKey(obj.getId());
			if (obj == null ) {
				addMensagemErro(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			if (obj.getCursosRegistrados() == null)
				obj.setCursosRegistrados(new ArrayList<Curso>());
			else
				obj.getCursosRegistrados().iterator();
			setReadOnly(false);
			setConfirmButton("Alterar");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		return forward(getFormPage());
	}
	
	/** Remove um livro de diplomas. O livro será removido do banco se não houver diploma registrado nele. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String remover() throws ArqException {
		setOperacaoAtiva(ArqListaComando.REMOVER.getId());
		prepareMovimento(ArqListaComando.REMOVER);
		GenericDAO dao = getGenericDAO();
		obj = new LivroRegistroDiploma();
		setId();
		obj = dao.findAndFetch(obj.getId(), LivroRegistroDiploma.class, "folhas");
		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			obj = new LivroRegistroDiploma();
			return null;
		} else {
			if (!obj.isVazio()) {
				addMensagemErro("Não é possível remover o livro: há diplomas registrados no mesmo.");
				return null;
			}
			try {
				MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);
				obj.setCursosRegistrados(null);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			} catch (Exception e) {
				throw new ArqException(e);
			}
			return cancelar();
		}
	}

	/**
	 * Realiza uma busca por livros de registro de diplomas conforme os
	 * parâmetros informados pelo usuário.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		String titulo = null;
		int idCurso = 0;
		Boolean ativo = null;
		Boolean externo = null;
		Boolean antigo = null;
		Character nivelEnsino = null; 
		if (buscaTitulo) {
			validateRequired(obj.getTitulo(), "Título", erros);
			titulo = obj.getTitulo();
		}
		if (buscaCurso) {
			validateRequired(this.idCurso, "Título", erros);
			idCurso = this.idCurso;
		}
		if (buscaSituacao) {
			validateRequired(obj.isAtivo(), "Situação", erros);
			ativo = obj.isAtivo();
		}
		if (buscaExterno) {
			validateRequired(obj.isRegistroExterno(), "Forma de Registro", erros);
			externo = obj.isRegistroExterno();
		}
		if (buscaAntigo) {
			validateRequired(obj.isLivroAntigo(), "Antigo", erros);
			antigo = obj.isLivroAntigo();
		}
		if (getNiveisHabilitados().length == 1) {
			nivelEnsino = getNiveisHabilitados()[0];
		} else  if (!NivelEnsino.isValido(obj.getNivel())) {
			addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO,"Nível de Ensino");
		} else nivelEnsino = obj.getNivel();
		if (isEmpty(titulo) && idCurso == 0 && isEmpty(ativo) && isEmpty(externo) && isEmpty(antigo) && isEmpty(nivelEnsino)) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		if (hasErrors()) {
			return null;
		}
		LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
		this.resultadosBusca = dao.findByTituloCurso(titulo, idCurso, ativo, externo, antigo, nivelEnsino);
		if (isEmpty(this.resultadosBusca))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return null;
	}
	
	/** Valida os dados do cadastro do livro. 
	 * <br/>Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		try {
			mensagens.addAll(obj.validate().getMensagens());
			// verifica se já existe livro com o mesmo título
			LivroRegistroDiplomaDao livroDao = getDAO(LivroRegistroDiplomaDao.class);
			if (obj.getTitulo() != null) {
				LivroRegistroDiploma livro = livroDao.findByTitulo(obj.getTitulo(), obj.getNivel());
				if (livro != null && livro.getId() != obj.getId())
					addMensagem(MensagensGerais.CONFLITO_TITULO_LIVRO);
			}
			if (obj.getId() == 0 && (obj.isStrictoSensu()||obj.isLatoSensu())) {
				// verifica se existe livro aberto (só pode haver um aberto por vez para os cursos de stricto sensu
				if (livroDao.hasLivroStrictoAberto(obj.isLivroAntigo(), obj.getNivel()))
					addMensagemErro("Não pode haver dois livros abertos para registro de diplomas de cursos "+obj.getNivelDesc()+". Por favor, feche o livro aberto antes de abrir um novo.");
			}
		} catch (DAOException e) {
			addMensagemErroPadrao();
			notifyError(e);
			return false;
		}
		return mensagens.size() > 0;
	}

	/**
	 * Cadastra / altera o livro.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/form.jsp</li>
	 * <li>/sigaa.war/diplomas/livro_registro_diploma/view.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		Integer operacaoAtiva = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		if( operacaoAtiva == null || (ArqListaComando.ALTERAR.getId() != operacaoAtiva && ArqListaComando.CADASTRAR.getId() != operacaoAtiva) ) {
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		}
		validacaoDados(erros);
		if (hasErrors())
			return null;
		boolean voltaLista = false;
		// não registrar os cursos no livro, caso seja livro de cursos stricto sensu ou de registro externo
		if (obj.isRegistroExterno() || obj.isStrictoSensu() || obj.isLatoSensu()) {
			obj.setCursosRegistrados(null);
		}
		if (getConfirmButton().equalsIgnoreCase("Fechar livro") || getConfirmButton().equalsIgnoreCase("Alterar")) {
			setConfirmButton("Alterar");
			voltaLista = true;
		} else {
			// caso esteja cadastrando livro seja antigo, cria as páginas no banco.
			if (obj.isLivroAntigo() && obj.getId() == 0) {
				obj.setFolhas(new ArrayList<FolhaRegistroDiploma>());
				for (int i = 1; i <= obj.getNumeroSugeridoFolhas(); i++) {
					FolhaRegistroDiploma novaFolha = obj.novaFolha();
					novaFolha.setNumeroFolha(i);
					obj.getFolhas().add(novaFolha);
				}
			}
			//se é registro de diplomas da UFRN,
			if (!obj.isRegistroExterno()){
				int codigoUnidade = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.CODIGO_INSTITUICAO);
				UnidadeGeral instituicao = getDAO(UnidadeDao.class).findByCodigo(codigoUnidade);
				obj.setInstituicao(instituicao.getNome());
			}
		}
		// a fim de evitar updates desnecessários em cascada das folhas e registros (que não são modificados aqui)
		if (obj.getId() != 0 && getConfirmButton().equalsIgnoreCase("Alterar"))
			obj.setFolhas(null);
		// mantêm o resultado da busca
		super.cadastrar();
		if (voltaLista) {
			return listar();
		} else {
			return preCadastrar();
		}
	}
	
	/** Retorna uma coleção de SelectItem de livros que não possuem cursos especificados para registro de diplomas.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getLivrosSemCurso() throws DAOException{
		Collection<SelectItem>livros = new ArrayList<SelectItem>();
		LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
		for (LivroRegistroDiploma livro : dao.findAllAtivos(LivroRegistroDiploma.class, "titulo")){
			if (!livro.isRegistroExterno()
					&& (livro.getCursosRegistrados() == null
							|| livro.getCursosRegistrados().size() == 0)) {
				livros.add(new SelectItem(livro.getId(), livro.getTitulo() + " ("+livro.getInstituicao()+")"));
			}
		}
		return livros;
	}
	
	/** Retorna uma coleção de livros abertos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getLivrosAbertos() throws DAOException{
		Collection<SelectItem>livros = new ArrayList<SelectItem>();
		LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
		for (LivroRegistroDiploma livro : dao.findAllAtivos(LivroRegistroDiploma.class, "titulo")){
			livros.add(new SelectItem(livro.getId(), livro.getTitulo() + (livro.isRegistroExterno()?" ("+livro.getInstituicao()+")":"")));
		}
		return livros;
	}
	
	/**
	 * Retorna os últimos livros criados. Número de livros a retornar é definido
	 * no parâmetro
	 * {@link ParametrosGerais#QUANTIDADE_ULTIMOS_LIVROS_REGISTRADOS}.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<LivroRegistroDiploma> getUltimosLivros() throws ArqException {
		LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
		int quant = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QUANTIDADE_ULTIMOS_LIVROS_REGISTRADOS);
		if (obj.isGraduacao())
			return dao.findUltimosLivros(quant, NivelEnsino.GRADUACAO);
		else if (obj.isLatoSensu())
			return dao.findUltimosLivros(quant, NivelEnsino.LATO);
		else
			return dao.findUltimosLivros(quant, NivelEnsino.STRICTO);
	}

	/** Indica que a busca por livros deve restringir por título. 
	 * @return
	 */
	public boolean isBuscaTitulo() {
		return buscaTitulo;
	}

	/** Seta que a busca por livros deve restringir por título. 
	 * @param buscaTitulo
	 */
	public void setBuscaTitulo(boolean buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}

	/** Indica que a busca por livros deve restringir por curso. 
	 * @return
	 */
	public boolean isBuscaCurso() {
		return buscaCurso;
	}

	/** Seta que a busca por livros deve restringir por curso. 
	 * @param buscaCurso
	 */
	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}

	/** Indica que a busca por livros deve restringir por situação (aberto/fechado). 
	 * @return
	 */
	public boolean isBuscaSituacao() {
		return buscaSituacao;
	}

	/** Seta que a busca por livros deve restringir por situação (aberto/fechado). 
	 * @param buscaSituacao
	 */
	public void setBuscaSituacao(boolean buscaSituacao) {
		this.buscaSituacao = buscaSituacao;
	}

	/** Indica que a busca por livros deve restringir por tipo interno/externo. 
	 * @return
	 */
	public boolean isBuscaExterno() {
		return buscaExterno;
	}

	/** Seta que a busca por livros deve restringir por tipo interno/externo. 
	 * @param buscaExterno
	 */
	public void setBuscaExterno(boolean buscaExterno) {
		this.buscaExterno = buscaExterno;
	}

	/** Indica que a busca por livros deve restringir por livro antigo/novo. 
	 * @return
	 */
	public boolean isBuscaAntigo() {
		return buscaAntigo;
	}

	/** Seta que a busca por livros deve restringir por livro antigo/novo. 
	 * @param buscaAntigo
	 */
	public void setBuscaAntigo(boolean buscaAntigo) {
		this.buscaAntigo = buscaAntigo;
	}
	
	/** Verifica as permissões do usuário.
	 * <br/>Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO);
	}

	public boolean isBuscaNivel() {
		return buscaNivel;
	}

	public void setBuscaNivel(boolean buscaNivel) {
		this.buscaNivel = buscaNivel;
	}

}
