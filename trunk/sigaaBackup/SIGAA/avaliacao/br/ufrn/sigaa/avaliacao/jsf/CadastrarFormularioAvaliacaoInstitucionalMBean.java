/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 05/12/2011
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.ALTERADO_COM_SUCESSO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.CADASTRADO_COM_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.avaliacao.dominio.AlternativaPergunta;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.TipoPergunta;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Controller responsável pelo cadastro do formulário de Avaliação
 * Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("cadastrarFormularioAvaliacaoInstitucionalMBean")
@Scope("session")
public class CadastrarFormularioAvaliacaoInstitucionalMBean extends SigaaAbstractController<FormularioAvaliacaoInstitucional> {
	
	/** Tamanho máximo que uma pergunta pode ter. */
	public static final int TAMANHO_MAX_PERGUNTA = 400;
	/** Tamanho máximo que uma dimensão pode ter. */
	public static final int TAMANHO_MAX_DIMENSAO = 200;
	
	/** Avaliação Institucional a ser preenchida. */
	private AvaliacaoInstitucional avaliacao;
	
	/** Pergunta a ser incluída no formulário. */
	private Pergunta pergunta;
	
	/** Alternativa a ser incluída no formulário. */
	private AlternativaPergunta alternativa;
	
	/** Grupo de pergunta a ser incluído no formulário. */
	private GrupoPerguntas grupo;
	
	/** Aba selecionada, do grupo de perguntas, no formulário. */
	private String abaSelecionada;
	
	/** Form para o qual o usuário deverá ser redirecionado ao retornar da visualização do formulário. 
	 * É utilizado no caso em que este controller é invocado por outro para exibir o formulário. */
	private String formVoltar;
	
	/**
	 * Construtor padrão.
	 */
	public CadastrarFormularioAvaliacaoInstitucionalMBean() {
		obj = new FormularioAvaliacaoInstitucional();
		avaliacao = new AvaliacaoInstitucional();
	}
	
	/** Cadastra o formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (getConfirmButton().equalsIgnoreCase("<< Voltar")) {
			if (!isEmpty(formVoltar))
				return redirectJSF(formVoltar);
			else 
				return listar();
		}
		checkChangeRole();
		// anula as alternativas de perguntas sim/não e nota
		for (GrupoPerguntas g : obj.getGrupoPerguntas()) {
			for (Pergunta p : g.getPerguntas()) {
				if (p.isNota() || p.isSimNao())
					p.setAlternativas(null);
			}
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		if (obj.getId() == 0) {
			if( !checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId()) )
				return cancelar();
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			try {
				execute(mov);
				addMensagem(CADASTRADO_COM_SUCESSO, "Formulário");
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return formCadastro();
			}
			return cancelar();
		} else if (getConfirmButton().equalsIgnoreCase("Alterar")) {
			if( !checkOperacaoAtiva(ArqListaComando.ALTERAR.getId()) )
				return cancelar();
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			try {
				execute(mov);
				addMensagem(ALTERADO_COM_SUCESSO, "Formulário");
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return formCadastro();
			}
			return cancelar();
		} else {
			return super.remover();
		}
	}
	
	/** Atualiza um formulário cadastrado.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		// verifica se não há avaliação preenchida para o formulário
		checkChangeRole();
		int idFormulario = getParameterInt("id", 0);
		if (hasAvaliacaoPreenchida(idFormulario)) {
			addMensagemWarning("Há Avaliações preenchidas para este formulário. Sua alteração poderá gerar inconsistências nas respostas às perguntas");
		}
		try {

			prepareMovimento(ArqListaComando.ALTERAR);
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			populateObj(true);
			// evita layzeException
			for (GrupoPerguntas g : obj.getGrupoPerguntas() ){
				for (Pergunta p : g.getPerguntas()) {
					if (p.isEscolhaUnica() || p.isMultiplaEscolha())
						p.getAlternativas().iterator();
					else
						p.setAlternativas(new ArrayList<AlternativaPergunta>());
				}
			}
			setReadOnly(false);
			setConfirmButton("Alterar");

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		return forward(getFormPage());
	}
	
	/** Visualiza um formulário cadastrado.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/lista.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	public String view() throws DAOException {
		populateObj(true);
		// evita layzeException
		for (GrupoPerguntas g : obj.getGrupoPerguntas() ){
			for (Pergunta p : g.getPerguntas()) {
				if (p.isEscolhaUnica() || p.isMultiplaEscolha())
					p.getAlternativas().iterator();
				else
					p.setAlternativas(new ArrayList<AlternativaPergunta>());
			}
		}
		setReadOnly(true);
		setConfirmButton("<< Voltar");
		formVoltar = getCurrentURL();
		
		if (obj.isAvaliacaoDiscente())
			return forward("/avaliacao/formulario_avaliacao/confirma_discente.jsp");
		else
			return forward("/avaliacao/formulario_avaliacao/confirma_docente.jsp");
	}
	
	/** Prepara para remover um formulário
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		try {
			// verifica se não há avaliação preenchida para o formulário
			int idFormulario = getParameterInt("id", 0);
			if (hasAvaliacaoPreenchida(idFormulario)) {
				addMensagemErro("Não é possível remover este formulário pois o mesmo possui Avaliações respondidas");
				return null;
			}
			populateObj(true);
			if (obj == null) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Grupo");
				obj = new FormularioAvaliacaoInstitucional();
				return null;
			}
			prepareMovimento(ArqListaComando.REMOVER);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());
			setReadOnly(true);
			setConfirmButton("Remover");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return null;
		}
		if (obj.isAvaliacaoDiscente())
			return forward("/avaliacao/formulario_avaliacao/confirma_discente.jsp");
		else
			return forward("/avaliacao/formulario_avaliacao/confirma_docente.jsp");
	}
	
	/** Indica se o formulário tem avaliação preenchida.
	 * @param idFormulario
	 * @return
	 */
	private boolean hasAvaliacaoPreenchida(int idFormulario) {
		long numAval = 0;
		numAval = getGenericDAO().count("from avaliacao.avaliacao_institucional where id_formulario_avaliacao = " + idFormulario);
		return numAval > 0;
	}

	/** Retorna o link para a lista de formulários cadastrados.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/avaliacao/formulario_avaliacao/lista.jsp";
	}
	
	/** Retorna um link para o formulário de cadastramento.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/avaliacao/formulario_avaliacao/form.jsp";
	}
	
	/** Inicia o cadastramento de um formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/administracao.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.COMISSAO_AVALIACAO);
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		obj = new FormularioAvaliacaoInstitucional();
		alternativa = new AlternativaPergunta();
		pergunta = new Pergunta();
		grupo = new GrupoPerguntas();
		avaliacao = new AvaliacaoInstitucional();
		avaliacao.setRespostas(new ArrayList<RespostaPergunta>());
		return formCadastro();
	}
	
	/** Redireciona o usuário para o formulário de cadastro.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_grupo.jsp</li>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String formCadastro() {
		return forward("/avaliacao/formulario_avaliacao/form.jsp");
	}

	/** Submete e valida os dados básicos do formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_grupo.jsp</li>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String submeterFormulario(){
		validateRequired(obj.getTitulo(), "Título do Formulário", erros);
		validateRequiredId(obj.getTipoAvaliacao(), "Tipo da Avaliação Institucional", erros);
		if (isEmpty(obj.getGrupoPerguntas().size()))
				addMensagemErro("Não há grupo de pergunta cadastrado.");
		for (GrupoPerguntas grupo : obj.getGrupoPerguntas()) {
			validateMinValue(grupo.getPerguntas().size(), 1, "Nº de Perguntas no Grupo \"" + grupo.getTitulo() + "\"", erros);
		}
		if (obj.getId() > 0 )
			setConfirmButton("Alterar");
		else 
			setConfirmButton("Cadastrar");
		if (hasErrors())
			return null;
		if (obj.isAvaliacaoDiscente())
			return forward("/avaliacao/formulario_avaliacao/confirma_discente.jsp");
		else
			return forward("/avaliacao/formulario_avaliacao/confirma_docente.jsp");
	}
	
	/** Direciona o usuário para o formulário de cadastramento de grupo de perguntas.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String formGrupoPergunta() {
		grupo = new GrupoPerguntas();
		setConfirmButton("Adicionar Grupo de Perguntas");
		return forward("/avaliacao/formulario_avaliacao/adicionar_grupo.jsp");
	}
	
	/** Direciona o usuário para o formulário de cadastramento de perguntas.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String formNovaPergunta() {
		String titulo = getParameter("grupo");
		grupo = new GrupoPerguntas();
		for (GrupoPerguntas g : obj.getGrupoPerguntas()) {
			if (g.getTitulo().equals(titulo)) {
				grupo.setTitulo(titulo);
				pergunta = new Pergunta();
				pergunta.setGrupo(grupo);
				alternativa = new AlternativaPergunta();
				pergunta.setAvaliarTurmas(g.isAvaliaTurmas());
				setConfirmButton("Adicionar Pergunta");
				return forward("/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp");
			}
		}
		addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Grupo");
		return redirectMesmaPagina();
	}
	
	public String diminuiOrdem() {
		return null;
	}
	
	public String aumentaOrdem() {
		return null;
	}
	
	/** Adiciona uma pergunta ao formulário
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String adicionaPergunta() {
		if (pergunta.getGrupo() == null || isEmpty(pergunta.getGrupo().getTitulo())) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Pergunta");
			return formCadastro();
		}
		validateRequired(pergunta.getDescricao(), "Pergunta", erros);
		if (!isEmpty(pergunta.getDescricao()))
			validateMaxLength(pergunta.getDescricao(), TAMANHO_MAX_PERGUNTA, "Pergunta", erros);
		if ((pergunta.isMultiplaEscolha() || pergunta.isEscolhaUnica())) {
			validateRequired(pergunta.getAlternativas(), "Alternativas de Resposta", erros);
		}
		if (hasErrors())
			return null;
		String titulo = pergunta.getGrupo().getTitulo();
		for (GrupoPerguntas grupo : obj.getGrupoPerguntas()) {
			if (grupo.getTitulo().equals(titulo)) {
				if (getConfirmButton().equalsIgnoreCase("Adicionar Pergunta")) {
					grupo.adicionaPergunta(pergunta);
					break;
				} else {
					// remove a pergunta anterior e adiciona a nova
					grupo.getPerguntas().remove(pergunta.getOrdem());
					grupo.getPerguntas().add(pergunta.getOrdem(), pergunta);
				}
			}
		}
		pergunta = new Pergunta();
		return formCadastro();
	}
	
	/** Altera umapergunta no formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String alteraPergunta() {
		String titulo = getParameter("grupo");
		alternativa = new AlternativaPergunta();
		pergunta = new Pergunta();
		int ordem = getParameterInt("ordem", -1);
		try {
			for (GrupoPerguntas g : obj.getGrupoPerguntas()) {
				if (StringUtils.toAscii(g.getTitulo()).equalsIgnoreCase(StringUtils.toAscii(titulo))) {
					for (Pergunta p : g.getPerguntas()) {
						if (p.getOrdem() == ordem) {
								this.pergunta = (Pergunta) BeanUtils.cloneBean(p);
								setConfirmButton("Alterar Pergunta");
								return forward("/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp");
						}
					}
				}
			}
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			e.printStackTrace();
		}
		addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Pergunta");
		return redirectMesmaPagina();
	}
	
	/** Altera um grupo de pergunta no formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String alteraGrupo() {
		grupo = new GrupoPerguntas();
		String titulo = getParameter("grupo");
		try {
			for (GrupoPerguntas grupo : obj.getGrupoPerguntas()) {
				if (grupo.getTitulo().equals(titulo)) {
					this.grupo = (GrupoPerguntas) BeanUtils.cloneBean(grupo);
					setConfirmButton("Alterar Grupo de Perguntas");
					return forward("/avaliacao/formulario_avaliacao/adicionar_grupo.jsp");
				}
			}
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			e.printStackTrace();
		}
		addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Grupo");
		return redirectMesmaPagina();
	}
	
	/** Remove um grupo de perguntas do formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String removeGrupo() {
		String titulo = getParameter("grupo");
		Iterator<GrupoPerguntas> iterator = obj.getGrupoPerguntas().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getTitulo().equals(titulo)) {
				iterator.remove();
				break;
			}
		}
		// Altera os títulos dos grupos 
		int i = 1;
		for (GrupoPerguntas grupo : obj.getGrupoPerguntas())
			grupo.setTitulo("Dimensão " + i++);
		return redirectMesmaPagina();
	}
	
	/** Adiciona uma alternativa de resposta à perguta.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String adicionaAlternativa() {
		validateRequired(alternativa.getDescricao(), "Alternativa", erros);
		if (hasErrors())
			return null;
		pergunta.adicionaAlternativa(alternativa);
		alternativa = new AlternativaPergunta();
		return null;
	}
	
	/** Remove uma alternativa de resposta da perguta.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String removeAlternativa() {
		int ordem = getParameterInt("ordem", -1);
		Iterator<AlternativaPergunta> iterator = pergunta.getAlternativas().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getOrdem() == ordem) {
				iterator.remove();
				return redirectMesmaPagina();
			}
		}
		addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Grupo");
		return redirectMesmaPagina();
	}
	
	/** Adiciona um grupo de resposta ao formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_grupo.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String adicionaGrupo() {
		validateRequired(this.grupo.getDescricao(), "Descrição", erros);
		if (!isEmpty(this.grupo.getDescricao()))
			validateMaxLength(this.grupo.getDescricao(), TAMANHO_MAX_DIMENSAO, "Descrição da Dimensão", erros);
		if (!isEmpty(obj.getGrupoPerguntas()) && getConfirmButton().equalsIgnoreCase("Adicionar Grupo de Perguntas"))
			for (GrupoPerguntas g : obj.getGrupoPerguntas())
				if (StringUtils.toAscii(g.getTitulo()).equalsIgnoreCase(StringUtils.toAscii(this.grupo.getTitulo())))
					addMensagemErro("Já existe um grupo com o mesmo título.");
		if (hasErrors())
			return null;
		if (getConfirmButton().equalsIgnoreCase("Adicionar Grupo de Perguntas")) {
			this.grupo.setTitulo("Dimensão " + (obj.getGrupoPerguntas().size() + 1));
			this.grupo.setPerguntas(new ArrayList<Pergunta>());
			obj.adicionaGrupoPergunta(this.grupo);
		} else {
			int index = -1;
			for (GrupoPerguntas g : obj.getGrupoPerguntas()) {
				index++;
				if (this.grupo.getTitulo().equals(g.getTitulo()))
					break;
			}
			obj.getGrupoPerguntas().remove(index);
			obj.getGrupoPerguntas().add(index, grupo);
		}
		this.grupo = new GrupoPerguntas();
		return forward("/avaliacao/formulario_avaliacao/form.jsp");
	}
	
	/** Remove uma pergunta do formulário.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String removePergunta() {
		String titulo = getParameter("grupo");
		int ordem = getParameterInt("ordem", -1);
		for (GrupoPerguntas g : obj.getGrupoPerguntas()) {
			if (StringUtils.toAscii(g.getTitulo()).equalsIgnoreCase(StringUtils.toAscii(titulo))) {
				Iterator<Pergunta> iterator = g.getPerguntas().iterator();
				while (iterator.hasNext()) {
					if (iterator.next().getOrdem() == ordem) {
						iterator.remove();
						break;
					}
				}
			}
		}
		return null;
	}
	
	/** Retorna uma coleção de selecItem de perfis de entrevistados.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Collection<SelectItem> getTiposAvaliacao() {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO, "Discente Graduação"));
		lista.add(new SelectItem(TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA, "Docência Assitida"));
		lista.add(new SelectItem(TipoAvaliacaoInstitucional.AVALIACAO_DOCENTE_GRADUACAO, "Docente de Graduação"));
		return lista;
	}

	/** Retorna uma coleção de selectItem com os tipos de pergunta.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>/sigaa.war/avaliacao/formulario_avaliacao/adicionar_pergunta.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Collection<SelectItem> getTipoPerguntaCombo() {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		for (TipoPergunta tipo : TipoPergunta.values()) {
			if (tipo.ordinal() != 0)
				lista.add(new SelectItem(tipo.name(), tipo.toString()));
		}
		return lista;
	}
	
	
	/** Retorna a lista de docentes das turmas para teste do formulário de Avaliação. 
	 * @return Lista de docentes das turmas em que o discente está matriculado. 
	 * @throws DAOException
	 */
	public List<DocenteTurma> getDocenteTurmasDiscenteMock() throws DAOException {
		List<DocenteTurma> lista = new ArrayList<DocenteTurma>();
		DocenteTurma dt;
		Turma turma;
		String nomeDocentes[] = {"JOHN DOE", "JOÃO DA SILVA", "MARY DOE", "MARIA DA SILVA"};
		for (int i = 0; i < nomeDocentes.length; i++) {
			turma = new Turma();
			dt = new DocenteTurma(1);
			turma = new Turma();
			turma.setDisciplina(new ComponenteCurricular(0, "TURMA " + (i + 1), "TESTE DE FORMULÁRIO DE AVALIAÇÃO INSTITUCIONAL"));
			dt.setTurma(turma);
			dt.getDocente().setId(1);
			dt.getDocente().getPessoa().setNome(nomeDocentes[i]);
			lista.add(dt);
		}
		return lista;
	}
	
	/** Retorna a lista de turmas avaliadas para teste do formulário de Avaliação.  
	 * @return Lista de turmas avaliadas. 
	 */
	public List<Turma> getTurmasDiscenteComMaisDeUmDocenteMock()  {
		List<Turma> lista = new ArrayList<Turma>();
		Turma turma = new Turma();
		turma.setDisciplina(new ComponenteCurricular(0, "TURMA 1", "TESTE DE FORMULÁRIO DE AVALIAÇÃO INSTITUCIONAL"));
		DocenteTurma dt;
		dt = new DocenteTurma(1);
		dt.setTurma(turma);
		dt.getDocente().setId(1);
		dt.getDocente().getPessoa().setNome("JOHN DOE");
		turma.addDocenteTurma(dt);
		dt = new DocenteTurma(1);
		dt.setTurma(turma);
		turma.addDocenteTurma(dt);
		dt.getDocente().setId(1);
		dt.getDocente().getPessoa().setNome("JOÃO DA SILVA");
		lista.add(turma);
		return lista;
	}
	
	/** Retorna a lista de turmas que o discente trancou para teste do formulário de Avaliação. 
	 * @return Lista de turmas que o discente trancou. 
	 */
	public List<Turma> getTrancamentosDiscenteMock()  {
		List<Turma> lista = new ArrayList<Turma>();
		Turma turma = new Turma();
		turma.getDisciplina().setCodigo("TESTE DE TRANCAMENTO");
		DocenteTurma dt;
		dt = new DocenteTurma(1);
		dt.setTurma(turma);
		dt.getDocente().setId(1);
		dt.getDocente().getPessoa().setNome("JOHN DOE");
		turma.addDocenteTurma(dt);
		lista.add(turma);
		return lista;
	}

	/** Verifica se o usuário tem permisão para cadastrar/alterar.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.COMISSAO_AVALIACAO);
	}
	
	public AvaliacaoInstitucional getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoInstitucional avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public AlternativaPergunta getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(AlternativaPergunta alternativa) {
		this.alternativa = alternativa;
	}

	public GrupoPerguntas getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoPerguntas grupo) {
		this.grupo = grupo;
	}

	public String getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(String abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

}
