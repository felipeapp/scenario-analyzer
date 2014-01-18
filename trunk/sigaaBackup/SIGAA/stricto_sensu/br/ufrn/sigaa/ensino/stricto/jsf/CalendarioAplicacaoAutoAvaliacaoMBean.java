/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 09/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dao.CalendarioAplicacaoAutoAvaliacaoDao;
import br.ufrn.sigaa.ensino.stricto.dao.RespostasAutoAvaliacaoDao;
import br.ufrn.sigaa.ensino.stricto.dominio.CalendarioAplicacaoAutoAvaliacao;
import br.ufrn.sigaa.ensino.stricto.dominio.RespostasAutoAvaliacao;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 * Controller respons�vel por cadastrar/alterar calend�rios de aplica��o da auto avalia��o do stricto sensu.
 * @author �dipo Elder F. de Melo
 *
 */
@Component("calendarioAplicacaoAutoAvaliacaoMBean") @Scope("request")
public class CalendarioAplicacaoAutoAvaliacaoMBean extends SigaaAbstractController<CalendarioAplicacaoAutoAvaliacao> {
	
	/** Cole��o de {@link SelectItem} de question�rios de auto avalia��o do stricto sensu. */
	private Collection<SelectItem> questionariosCombo;
	
	/** Dados do relat�rio de preenchimento da Auto Avalia��o. */
	private List<RespostasAutoAvaliacao> dadosRelatorio;
	
	/** Programa ser adicionado. */
	private Unidade programa;
	
	/** Curso a ser adicionado. */
	private Curso curso;
	
	
	/** Construtor padr�o. */
	public CalendarioAplicacaoAutoAvaliacaoMBean() {
		obj = new CalendarioAplicacaoAutoAvaliacao();
		programa = new Unidade();
		curso = new Curso();
	}
	
	/** Retorna o link para a p�gina de listagem de calend�rios de aplica��o da auto avalia��o do stricto sensu.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/stricto/auto_avaliacao/lista.jsp";
	}
	
	/** Retorna o link para a p�gina de listagem de calend�rios de aplica��o da auto avalia��o do stricto sensu ap�s o cadastro de um calend�rio.
	 * <br/> M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/** Prepara para inativar um calend�rio
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeInativar()
	 */
	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
			setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		} catch (ArqException e) {
			notifyError(e);
		}
	}
	
	/** Retorna o link para a p�gina de cadastro de calend�rios de aplica��o da auto avalia��o do stricto sensu.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/stricto/auto_avaliacao/form.jsp";
	}
	
	/** Retorna para o usu�rio uma visualiz�o da Auto Avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException {
		GenericDAO dao = getGenericDAO();
		int id = getParameterInt("id", 0);
		obj = dao.findByPrimaryKey(id, CalendarioAplicacaoAutoAvaliacao.class);
		QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
		mBean.inicializar(obj.getQuestionario());
		mBean.setReadOnly(true);
		RespostasAutoAvaliacaoMBean rmBean = getMBean("respostasAutoAvaliacaoMBean");
		rmBean.setReadOnly(true);
		rmBean.setCalendarioAutoAvaliacao(obj);
		return forward("/stricto/auto_avaliacao/responder.jsp");
	}
	
	/** Retorna uma cole��o de {@link SelectItem} de question�rios de auto avalia��o do stricto sensu.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getQuestionariosCombo() throws HibernateException, DAOException{
		if (questionariosCombo == null) {
			QuestionarioDao dao = getDAO(QuestionarioDao.class);
			if (isPortalPpg())
				questionariosCombo = toSelectItems(dao.findByTipo(TipoQuestionario.AUTO_AVALIACAO_STRICTO_SENSU), "id", "titulo");
			if (isPortalLatoSensu())
				questionariosCombo = toSelectItems(dao.findByTipo(TipoQuestionario.AUTO_AVALIACAO_LATO_SENSU), "id", "titulo");
		}
		return questionariosCombo;
	}
	
	/** Retorna todos os calend�rios de aplica��o da Auto Avalia��o
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<CalendarioAplicacaoAutoAvaliacao> getAll() throws ArqException {
		if (all == null) {
			CalendarioAplicacaoAutoAvaliacaoDao dao = getDAO(CalendarioAplicacaoAutoAvaliacaoDao.class);
			int tipo = isPortalPpg() ? TipoQuestionario.AUTO_AVALIACAO_STRICTO_SENSU : TipoQuestionario.AUTO_AVALIACAO_LATO_SENSU;
			all = dao.findAllOtimizado(tipo);
		}
		return all;
	}
	
	/** Gera um relat�rio de preenchimento da auto avalia��o
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String relatorioPreenchido() throws DAOException {
		int id = getParameterInt("id", 0);
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		obj = dao.findByPrimaryKey(id, CalendarioAplicacaoAutoAvaliacao.class);
		if (isPortalPpg())
			dadosRelatorio = dao.findDadosRelatorioPreenchimentoStricto(id);
		else 
			dadosRelatorio = dao.findDadosRelatorioPreenchimentoLato(id);
		return forward("/stricto/auto_avaliacao/relatorio_preenchimento.jsp");
	}
	
	/** For�a o carregamento da lista de cursos e/ou programas ao qual o calend�rio � aplicado.
	 * <br/>M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj.getCursos() != null) obj.getCursos().iterator();
		if (obj.getProgramas() != null) obj.getProgramas().iterator();
	}
	
	/** Cadastra/Altera o calend�rio de aplica��o da auto Avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (obj.isAplicavelATodos()) {
			obj.setProgramas(null);
			obj.setCursos(null);
		} else {
			getGenericDAO().initialize(obj.getQuestionario());
		}
		return super.cadastrar();
	}
	
	/** Adiciona um programa � lista de programas ao qual o calend�rio � aplicado.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String adicionaPrograma() throws DAOException {
		if (obj.getProgramas() == null) obj.setProgramas(new LinkedList<Unidade>());
		for (Unidade inserido : obj.getProgramas())
			if (inserido.getId() == programa.getId()) {
				addMensagemErro("Este programa j� foi inserido anteriormente. Por favor, selecione outro programa.");
				return null;
			}
		programa = getGenericDAO().refresh(programa);
		obj.getProgramas().add(programa);
		programa = new Unidade();
		return null;
	}
	
	/** Remove um programa da lista de programas ao qual o calend�rio � aplicado.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String removePrograma() {
		int id = getParameterInt("id", 0);
		if (!isEmpty(obj.getProgramas())) {
			Iterator<Unidade> iterator = obj.getProgramas().iterator();
			while (iterator.hasNext())
				if (iterator.next().getId() == id) {
					iterator.remove();
					addMensagemInformation("Programa Removido Com Sucesso!");
					return null;
				}
		}
		addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "programa", "removido");
		return null;
	}
	
	/** Adiciona um curso � lista de programas ao qual o calend�rio � aplicado.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String adicionaCurso() throws DAOException{
		if (obj.getCursos() == null) obj.setCursos(new LinkedList<Curso>());
		for (Curso inserido : obj.getCursos())
			if (inserido.getId() == curso.getId()) {
				addMensagemErro("Este curso j� foi inserido anteriormente. Por favor, selecione outro curso.");
				return null;
			}
		curso = getGenericDAO().refresh(curso);
		obj.getCursos().add(curso);
		curso = new Curso();
		return null;
	}
	
	/** Remove um programa da lista de cursos ao qual o calend�rio � aplicado.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String removeCurso() {
		int id = getParameterInt("id", 0);
		if (!isEmpty(obj.getCursos())) {
			Iterator<Curso> iterator = obj.getCursos().iterator();
			while (iterator.hasNext())
				if (iterator.next().getId() == id) {
					iterator.remove();
					addMensagemInformation("Curso Removido Com Sucesso!");
					return null;
				}
		}
		addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "curso", "removido");
		return null;
	}

	public List<RespostasAutoAvaliacao> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<RespostasAutoAvaliacao> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

}
