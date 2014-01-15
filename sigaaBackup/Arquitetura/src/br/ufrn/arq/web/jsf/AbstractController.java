/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/08/2007
 */
package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.PropertyNotWritableException;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.arq.seguranca.log.SessionLogger;
import br.ufrn.arq.tasks.TarefaAssincrona;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ErrorUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.web.captcha.Captcha;
import br.ufrn.arq.web.tags.SelectAnoTag;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * Classe que implementa o padrão Layer Supertype
 * (http://martinfowler.com/eaaCatalog/layerSupertype.html) e que
 * possui o comportamento que é comum a todos os controladores
 * dos sistemas institucionais.deta
 *
 * A implementação do Serializable é para utilizar replicação
 * ou técnicas de manutenção de requests como o a4j:keepAlive.
 *
 * @author Gleydson Lima
 * @author David Pereira
 * 
 */
public class AbstractController implements Serializable {

	
	/** Chave para atributo que contém o último comando realizado. */
	private static final String LAST_COMMAND = "lastCommand";

	/** Coleção de SelectItems padrão para campos de ano. */
	private static List<SelectItem> anos = new ArrayList<SelectItem>();

	/** Coleção de SelectItems padrão com os meses do ano. */
	private static List<SelectItem> meses = new ArrayList<SelectItem>();
	
	private static List <SelectItem> horas = new ArrayList <SelectItem> ();
	
	private static List <SelectItem> minutos = new ArrayList <SelectItem> ();

	/** Coleção de SelectItems padrão com os valores sim (true) e não (false). */
	private static List<SelectItem> simNao = new ArrayList<SelectItem>();

	/** Coleção de SelectItems padrão com os valores Masculino (M) e Feminino (F). */
	private static List<SelectItem> mascFem = new ArrayList<SelectItem>();

	/** Tamanho de uma página em uma listagem de dados. */
	private int tamanhoPagina = 0;

	/** Número total de páginas em uma listagem de dados. */
	private int totalPaginas;
	
	/** Coleção de erros para serem exibidos ao usuário. */
	protected ListaMensagens erros = new ListaMensagens();

	static {

		meses.add(new SelectItem("1", "Janeiro"));
		meses.add(new SelectItem("2", "Fevereiro"));
		meses.add(new SelectItem("3", "Março"));
		meses.add(new SelectItem("4", "Abril"));
		meses.add(new SelectItem("5", "Maio"));
		meses.add(new SelectItem("6", "Junho"));
		meses.add(new SelectItem("7", "Julho"));
		meses.add(new SelectItem("8", "Agosto"));
		meses.add(new SelectItem("9", "Setembro"));
		meses.add(new SelectItem("10", "Outubro"));
		meses.add(new SelectItem("11", "Novembro"));
		meses.add(new SelectItem("12", "Dezembro"));

		simNao.add(new SelectItem(String.valueOf(true), "Sim"));
		simNao.add(new SelectItem(String.valueOf(false), "Não"));

		mascFem.add(new SelectItem("F", "Feminino"));
		mascFem.add(new SelectItem("M", "Masculino"));

		for (int i = SelectAnoTag.ANO_INCIO_PADRAO; i <= CalendarUtils.getAnoAtual() + 4; i++) {
			anos.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}
		
		for (int i = 0; i < 24; i++)
			horas.add(new SelectItem(""+i, ""+i));
		
		for (int i = 0; i < 60; i++)
			minutos.add(new SelectItem(""+i, ""+i));
	}

	public String getMes(int mes) {
		return meses.get(mes).getLabel();
	}

	public String getPadraoData() {
		return "dd/MM/yyyy";
	}

	public String getPadraoNumero() {
		return "#,##0.00";
	}
	
	public List<SelectItem> getMeses() {
		return meses;
	}
	
	public List<SelectItem> getSimNao() {
		return simNao;
	}

	public List<SelectItem> getMascFem() {
		return mascFem;
	}
	
	public List<SelectItem> getAnos() throws Exception {
		return anos;
	}
	
	public List<SelectItem> getHoras() {
		return horas;
	}

	public List<SelectItem> getMinutos() {
		return minutos;
	}

	/**
	 * Retorna ano atual
	 * @deprecated Usar CalendarUtils.getAnoAtual()
	 * @return
	 */
	@Deprecated
	public int getAnoAtual() {
		return CalendarUtils.getAnoAtual();
	}

	/**
	 * retorna o período atual
	 * caso seja um mês superior a junho (6) retorna 2
	 * senão retorna 1
	 * @return
	 */
	public int getPeriodoAtual() {
		return (getMesAtual() + 1) < 7 ? 1 : 2;
	}

	/**
	 * Retorna mês atual
	 * @deprecated Usar CalendarUtils.getMesAtual()
	 * @return
	 */
	@Deprecated
	public int getMesAtual() {
		return CalendarUtils.getMesAtual();
	}

	/**
	 * Retorna mês atual como string ex: janeiro, fevereiro, etc
	 *
	 * @return
	 */
	public String getMesAtualString() {
		return getMes(CalendarUtils.getMesAtual());
	}
	

	/**
	 * Retorna o Delegate do Usuário e cria caso ainda não possua
	 *
	 * @param req
	 * @return
	 */
	protected FacadeDelegate getUserDelegate(HttpServletRequest req) throws ArqException {
		HttpSession session = req.getSession(true);
		if (session == null) {
			throw new ArqException("Sessão não ativada");
		} else {
			FacadeDelegate facade = (FacadeDelegate) session.getAttribute("userFacade");
			
			if (facade == null) {
				String jndiName = (String) req.getSession().getServletContext().getAttribute("jndiName");
				facade = new FacadeDelegate(jndiName);
				session.setAttribute("userFacade", facade);
			}
			
			return facade;
		}
	}

	/**
	 * Chama o processador baseado no movimento passado
	 *
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 *
	 */
	@SuppressWarnings("unchecked")
	public <V> V execute(Movimento mov, HttpServletRequest req) throws NegocioException, ArqException {
		forceCloseSession();
		mov.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()));
		return (V) getUserDelegate(req).execute(mov, req);
	}

	/**
	 * Chama o processador baseado no movimento passado
	 *
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public <V> V execute(Movimento mov) throws NegocioException, ArqException {
		return execute(mov, getCurrentRequest());
	}
	
	/**
	 * Chama o processador utilizando o facade passado como parâmetro.
	 * @param mov
	 * @param facade
	 * @param usuario
	 * @param sistema
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public <V> V execute(Movimento mov, FacadeDelegate facade, UsuarioGeral usuario, Integer sistema) throws NegocioException, ArqException  {
		forceCloseSession();
		return (V) facade.execute(mov, usuario, sistema);
	}

	/**
	 * Chama o processador sem fechar a conexão. 
	 * Deprecated: utilizar o execute.
	 * 
	 * @param mov
	 * @param req
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	public <V> V executeWithoutClosingSession(Movimento mov, HttpServletRequest req) throws NegocioException, ArqException {
		mov.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()));
		return (V) getUserDelegate(req).execute(mov, req);
	}

	/**
	 * Chama o processador sem fechar a conexão. 
	 * Deprecated: utilizar o execute.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public <V> V executeWithoutClosingSession(Movimento mov) throws NegocioException, ArqException {
		return executeWithoutClosingSession(mov, getCurrentRequest());
	}

	/**
	 * Chama o processador sem fechar a conexão. 
	 * Deprecated: utilizar o execute.
	 * 
	 * @param mov
	 * @param facade
	 * @param usuario
	 * @param sistema
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public Object executeWithoutClosingSession(Movimento mov, FacadeDelegate facade, UsuarioGeral usuario, Integer sistema) throws NegocioException, ArqException  {
		return facade.execute(mov, usuario, sistema);
	}

	public final void forceCloseSession() {
		Session session = getSessionRequest();
		if (session != null) {
			try {
				session.close();
				SessionLogger.log(session, 'C');
			} catch (Exception e) {

			}
		}
	}

	// método usado pelas JSTL e contextos não Faces para criar Managed Beans
	public String getCreate() {
		return "";
	}

	/**
	 * Desbloqueia um comando para que ele possa ser executado.
	 * @param comando
	 * @throws ArqException
	 */
	public void prepareMovimento(Comando comando) throws ArqException {
		prepareMovimento(comando, getCurrentRequest());
	}

	/**
	 * Desbloqueia um comando para que ele possa ser executado.
	 * @param comando
	 * @param req
	 * @throws ArqException
	 */
	public void prepareMovimento(Comando comando, HttpServletRequest req) throws ArqException {
		try {
			req.getSession().setAttribute(LAST_COMMAND, comando);
			getUserDelegate(req).prepare(comando.getId(), req);
		} catch (NegocioException e) {
			// essa exceção não vem no prepare
		}
	}

	/**
	 * Desbloqueia um comando para que ele possa ser executado.
	 * @param comando
	 * @param facade
	 * @param usuario
	 * @param sistema
	 * @throws ArqException
	 */
	public void prepareMovimento(Comando comando, FacadeDelegate facade, UsuarioGeral usuario, Integer sistema) throws ArqException {
		try {
			facade.prepare(comando.getId(), usuario, sistema);
		} catch (NegocioException e) {
			// essa exceção não vem no prepare
		}
	}

	public void inicializarTarefaAssincrona(TarefaAssincrona<?> tarefa, HttpServletRequest req) throws ArqException {
		tarefa.initialize(getUserDelegate(req), (UsuarioGeral) req.getSession(true).getAttribute("usuario"), (Integer) req.getAttribute("sistema"));
	}

	/**
	 * Retorna o subsistema que está sendo utilizado pelo usuário.
	 */
	public SubSistema getSubSistema() {
		SubSistema subsistema = null;

		if (getCurrentSession().getAttribute("subsistema") instanceof String) {
			GenericSharedDBDao genDAO = null;
			String idSubsistema = (String) getCurrentSession().getAttribute("subsistema");

			try {
				genDAO = new GenericSharedDBDao();
				subsistema = genDAO.findByPrimaryKey(Integer.parseInt(idSubsistema), SubSistema.class);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if (genDAO != null) genDAO.close();
			}

		} else {
			subsistema = (SubSistema) getCurrentSession().getAttribute("subsistema");
		}
		
		return subsistema;
	}


	

	/**
	 * Informa o diretório base de redirecionamento
	 *
	 * @param dirBase
	 */
	public void setDirBase(String dirBase) {
		getCurrentSession().setAttribute("dirBase", dirBase);
	}

	

	/**
	 * Seta o subsistema atual na sessão
	 */
	public void setSubSistemaAtual(SubSistema sub) {
		getCurrentSession().removeAttribute("subsistema");
		getCurrentSession().setAttribute("subsistema", sub);
	}
	

	/**
	 * Retorna o sistema atual (SIPAC, SIGAA, SIGRH, etc).
	 * @see Sistema
	 */
	public final int getSistema() {
		return (Integer) getCurrentRequest().getAttribute("sistema");
	}

	/**
	 * Adiciona uma linha no cabeçalho de um relatório
	 */
	@SuppressWarnings("unchecked")
	public void addComplementoPrint(String mensagem, HttpServletRequest req) {

		ArrayList<String> mensagens = null;
		if (req.getAttribute("complemento") == null) {
			mensagens = new ArrayList<String>();
			req.setAttribute("complemento", mensagens);
		} else {
			mensagens = (ArrayList<String>) req.getAttribute("complemento");
		}
		mensagens.add(mensagem);
	}


	/**
	 * Verifica se o conteúdo digitado pelo usuário é igual a imagem de um Captcha
	 */
	public boolean validaCaptcha(String captcha) {
		try {
			return Captcha.getInstance().getService().validateResponseForID(getCurrentSession().getId(), captcha);
		} catch(CaptchaServiceException e) {
			e.printStackTrace();
			// Esta exceção ocorre quando o usuário perde a sessão. Assim, o id da sessão não é válido. Tratado para, nesses casos, retornar false.  
			return false;
		}
	}



	/**
	 * Define uma operação ativa durante a execução do caso de uso.
	 *
	 * @param operacao -
	 *            Operaçao executada no momento, pode ser definida partir de uma
	 *            classe de constantes, por exemplo:
	 *            br.ufrn.sigrh.avaliacao.dominio.OperacoesAvaliacao.
	 * @param mensagemOpAtiva -
	 *            Mensagem que será exibida ao usuário caso não a operação tenha
	 *            sido finalizada.
	 * @param session
	 */
	public void setOperacaoAtiva(Integer operacao, String mensagemOpAtiva) {

		getCurrentSession().setAttribute("operacaoAtiva", operacao);
		getCurrentSession().setAttribute("mensagemOperacaoAtiva", mensagemOpAtiva);
	}

	public void setOperacaoAtiva(Integer operacao) {
		setOperacaoAtiva(operacao, "A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
	}

	/**
	 * Método para remoção da operação ativa da sessão para evitar erros de
	 * nullpointer devido aos usuários utilizarem o VOLTAR do browser
	 */
	public void removeOperacaoAtiva() {
		getCurrentSession().removeAttribute("operacaoAtiva");
	}


	/** 
	 * Verifica se há ou não uma operação ativa, dentro da lista de operações passadas como parâmetro. O
	 * redirect deve ser dado após a chamado e se o resultado por false.
	 * @param operacoes
	 * @return false, caso não tenha operação ativa setada, ou caso a operação ativa não seja nenhuma das passadas como parâmetro.
	 */
	public boolean checkOperacaoAtiva(Integer ...operacoes) {
		
		boolean ativa = isOperacaoAtiva(operacoes);
		
		if (!ativa) {
			String msgSessao = (String) getCurrentSession().getAttribute("mensagemOperacaoAtiva");
			if( msgSessao != null ) {
				addMensagemErro(msgSessao);
				getCurrentSession().removeAttribute("mensagemOperacaoAtiva");
			} else {
				addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
			}
			return false;
		}
		
		return true;
	}

	/**
	 * Identifica se um subsistema passado como parâmetro está ativo ou não.
	 * @param subsistema
	 * @return
	 * @throws DAOException
	 */
	public boolean isSubSistemaAtivo(int subsistema) throws DAOException {
		GenericDAO dao = new GenericSharedDBDao(Sistema.COMUM);
		try {
			SubSistema ss = dao.findByPrimaryKey(subsistema, SubSistema.class);
			return ss.isAtivo();
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Apenas verifica se as operações passadas como parâmetro são ativas. <br>
	 * <b>Não insere nenhuma mensagem de erro na lista de erros!</b>
	 * @return false, caso não tenha operação ativa setada, ou caso a operação ativa não seja nenhuma das passadas como parâmetro.
	 * @param operacoes
	 * @return
	 */
	public boolean isOperacaoAtiva(Integer ...operacoes) {
		boolean ativa = false;

		Integer operacaoAtiva = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		if (operacaoAtiva != null) {
			for (Integer operacao : operacoes) {
				if (operacaoAtiva.equals(operacao)) {
					ativa = true;
					break;
				}
			}
		}
		
		return ativa;
	}
	
	public Comando getUltimoComando() {
		return (Comando) getCurrentSession().getAttribute(LAST_COMMAND);
	}

	/**
	 * Transforma um mapa em uma coleção de SelectItem. A key de uma Entry
	 * será utilizado como value do SelectItem e o value de uma Entry 
	 * como label.
	 */
	public static List<SelectItem> toSelectItems(Map<?, ?> map) {
		List<SelectItem> itens = new ArrayList<SelectItem>();

		for (Object name : map.entrySet()) {
			Entry<?, ?> entry = (Entry<?, ?>) name;
			itens.add(new SelectItem(entry.getKey().toString(), entry.getValue().toString()));
		}
		return itens;
	}

	 /**
	  * Transforma uma coleção de objetos em uma lista de SelectItems
	  * @param col Coleção a ser transformada em SelectItems
	  * @param value Atributo que será utilizado no value do option
	  * @param showText Valor que será exibido ao usuário
	  */
	public static List<SelectItem> toSelectItems(Collection<?> col, String value, String showText) {

		ArrayList<OrderedSelectItem> itensOrdenaveis = new ArrayList<OrderedSelectItem>();
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();

		try {
			if (col != null) {
				for (Object obj : col) {
					Object id = ReflectionUtils.evalProperty(obj, value);
					Object text = ReflectionUtils.evalProperty(obj, showText);
					if (text == null) {
						text = "";
					}
					OrderedSelectItem item = new OrderedSelectItem(id.toString(), text
							.toString());
					itensOrdenaveis.add(item);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(itensOrdenaveis);
		for ( OrderedSelectItem item : itensOrdenaveis ) {
			itens.add(item.toSelectItem());
		}

		return itens;

	}

	/**
	  * Transforma uma coleção de objetos em uma lista de SelectItems utilizando
	  * o próprio objeto como valor do SelectItem.
	  * @param col Coleção a ser transformada em SelectItems
	  * @param showText Valor que será exibido ao usuário
	  */
	public static List<SelectItem> toSelectItems(Collection<?> col, String showText) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		
		try {
			if (col != null) {
				for (Object obj : col) {
					items.add(new SelectItem(obj, BeanUtils.getProperty(obj, showText)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}
	
	
	/**
	 * Método para entrar no sistema comum
	 *
	 * @param url
	 * @param sistemaOrigem
	 * @param usuario
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws IOException
	 */
	public void logarSistema(String url, int sistemaOrigem, int sistemaDestino,
			UsuarioGeral usuario, HttpServletRequest req,
			HttpServletResponse res) throws NegocioException, ArqException,
			IOException {

		PassaporteLogon passaporte = new PassaporteLogon();
		passaporte.setHora(new Date());
		passaporte.setSistemaAlvo(sistemaDestino);
		passaporte.setSistemaOrigem(sistemaOrigem);
		req.setAttribute("sistema", sistemaOrigem);

		passaporte.setLogin(usuario.getLogin());
		prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE, req);
		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);
		execute(passaporte, req);

		res.sendRedirect(url);
	}

	/**
	 * ----------------------------------------------------
	 * - MANIPULAÇÃO DE MANAGED BEANS
	 * ----------------------------------------------------
	 */
	
	/**
	 * Remove do container o managed bean que chamar o método.
	 */
	public void resetBean() {
		/** removendo operação ativa */
		getCurrentSession().removeAttribute("operacaoAtiva");
		
		Component anotComponent = getClass().getAnnotation(Component.class);
		if (anotComponent != null) {
			String mbeanName = anotComponent.value();
			if (isEmpty(mbeanName)) mbeanName = StringUtils.uncapitalize(getClass().getSimpleName());
			resetBean(mbeanName);
		}
	}


	/**
	 * Remove do container um managed bean cujo nome for passado como parâmetro.
	 */
	public static void resetBean(String name) {
		if (!isEmpty(name)) {
			Object mbean = getMBean(name);
			if(mbean != null) {
				try {
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getExpressionFactory().createValueExpression(fc.getELContext(), "#{" + name + "}", getMBean(name).getClass()).setValue(fc.getELContext(), null);
				} catch(PropertyNotWritableException e) {
					Object clean = ReflectionUtils.instantiateClass(mbean.getClass());
					ReflectionUtils.copyProperties(clean, mbean);
				}
			}
		}
	}
	
	/**
	 * Retorna um managed-bean existente no container do JavaServer Faces.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getMBean(String mbeanName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		return (T) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, mbeanName);
	}

	/**
	 * Cria um Managed Bean no container do JavaServer Faces com o nome e objeto passados
	 * como parâmetro. 
	 * @deprecated
	 */
	@Deprecated
	public void createMBean(String mBeanName, Object mbeanObject) {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().createValueBinding("#{" + mBeanName + "}").setValue(fc, mbeanObject);
	}

	/**
	 * Remove um managed bean do container.
	 */
	@Deprecated
	public static void destroyMBean(String mBeanName) {
		resetBean(mBeanName);
	}
	
	/**
	 * limpa um managed bean do container.
	 */
	public void clearMBean(String mBeanName, Object mbeanObject) {
		resetBean(mBeanName);
		createMBean(mBeanName, mbeanObject);
	}
	
	
	/**
	 * ----------------------------------------------------
	 * - MÉTODOS RELACIONADOS A DAOS
	 * ----------------------------------------------------
	 */
	public GenericDAO getGenericDAO() {
		return DAOFactory.getInstance().getDAO(GenericDAOImpl.class, getCurrentRequest());
	}
	
	/**
	 * Retorna uma instância de um DAO com a sessão que está ativa
	 * em request (OpenSessionInView).
	 */
	public <U extends GenericDAO> U getDAO(Class<U> daoClass, Integer sistema) {
		return DAOFactory.getInstance().getDAO(daoClass, sistema == null ? -1 : sistema, getCurrentRequest());
	}

	/**
	 * Retorna uma instância de um DAO com a sessão que está ativa
	 * em request (OpenSessionInView), setando nele o sistema passado como parâmetro.
	 */
	public <U extends GenericDAO> U getDAO(Class<U> daoClass) {
		return getDAO(daoClass, null);
	}
	
	/**
	 * Retorna a sessao do hibernate existente em request. Implementação
	 * do padrão OpenSessionInView. 
	 * @param sistema 
	 */
	public Session getSessionRequest(Integer sistema) {
		return DAOFactory.getInstance().getSessionRequest(sistema, getCurrentRequest());
	}
	
	/**
	 * Retorna a sessao do hibernate existente em request. Implementação
	 * do padrão OpenSessionInView. 
	 * @param sistema 
	 */
	public Session getSessionRequest() {
		return DAOFactory.getInstance().getSessionRequest(getSistema(), getCurrentRequest());
	}
	
	/**
	 * Retorna uma lista de SelectItem com todos os objetos da classe passada.
	 */
	public Collection<SelectItem> getAll(Class<?> classe, String value, String text) {
		GenericDAO dao = null;
		try {
			dao = new GenericDAOImpl(getSistema());
			return toSelectItems(dao.findAll(classe, text, "asc"), value, text);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Retorna uma lista de SelectItem com todos os objetos da classe passada que
	 * possuirem o atributo ativo = true. 
	 */
	public Collection<SelectItem> getAllAtivo(Class<?> classe, String value, String text) {
		GenericDAO dao = null;
		try {
			dao = new GenericDAOImpl(getSistema());
			return toSelectItems(dao.findByExactField(classe, "ativo", Boolean.TRUE, null, text, "asc"), value, text);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * Retorna o findAll do GenericDAO
	 */
	public <T> Collection<T> getAllObj(Class<T> classe, int sistema) {
		GenericDAO dao = null;
		try {
			dao = new GenericDAOImpl(sistema);
			return dao.findAll(classe);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<T>();
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 * Retorna a informação de paginação. A paginação é um MBean com
	 * escopo de sessão.
	 */
	public PagingInformation getPaginacao() {

		PagingInformation paginacao = (PagingInformation) getMBean("paginacao");
		// Tamanho de pagina setado no MBean. Se o programador nao definir, pega
		// o valor padrao
		if (tamanhoPagina > 0) {
			paginacao.setTamanhoPagina(tamanhoPagina);
		}
		
		return paginacao;
	}
	
	public int getTamanhoPagina() {
		return tamanhoPagina;
	}

	public void setTamanhoPagina(int tamanhoPagina) {
		this.tamanhoPagina = tamanhoPagina;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public void setTotalPaginas(int totalPaginas) {
		this.totalPaginas = totalPaginas;
	}
	
	
	/**
	 * ----------------------------------------------------
	 * - MÉTODOS PARA EXIBIÇÃO DE MENSAGENS
	 * ----------------------------------------------------
	 */
	
	/**
	 * Coloca os erros em sessão
	 */
	public void attachErrorInSession() {
		HttpSession session = getCurrentSession();

		if (session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION) == null)
			session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		else {
			ListaMensagens errosAnt = (ListaMensagens) session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
			if (errosAnt != null) {
				erros.addAll(errosAnt.getMensagens());
				session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
			}
		}
	}
	
	/**
	 * Teste se tem erros na lista de mensagens em sessão 
	 * para determinar validação. 
	 */
	public boolean hasErrors() {
		
		attachErrorInSession();
		
		boolean hasErros = erros.isErrorPresent();
		erros = new ListaMensagens();
		return hasErros;
	}

	/**
	 * Limpa a lista de mensagens em sessão.
	 */
	public void clearMensagens() {
		getCurrentSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
	}

	/**
	 * Retorna true apenas se a lista de mensagens em sessão 
	 * tiver mensagens do tipo TipoMensagemUFRN.ERROR
	 */
	public boolean hasOnlyErrors() {

		attachErrorInSession();

		boolean hasErros = false;
		for (MensagemAviso msg : erros.getMensagens()) {
			if (msg.getTipo() == TipoMensagemUFRN.ERROR) {
				hasErros = true;
				break;
			}
		}
		erros = new ListaMensagens();
		return hasErros;
	}
	
	/**
	 * Adiciona uma mensagem ao sistema buscando-a no banco de dados,
	 * de acordo com o código passado como parâmetro.
	 * @param codigo
	 */
	public void addMensagem(String codigo, Object... params) {
		MensagemAviso msg = UFRNUtils.getMensagem(codigo, params);
		erros.addMensagem(msg);
		hasErrors();
	}
	
	/**
	 * Adiciona uma mensagem ao sistema buscando-a no banco de dados,
	 * de acordo com o código passado como parâmetro e um tipo.
	 * @param codigo
	 */
	public void addMensagem(String codigo, TipoMensagemUFRN tipo, Object... params) {
		MensagemAviso msg = UFRNUtils.getMensagem(codigo, params);
		addMessage(msg.getMensagem(), tipo);
	}

	/**
	 * Adiciona a msg de erro padrão do sistema para exceções que nao sejam de 
	 * negócio na lista de msg para ser exibido ao usuário
	 */
	public void addMensagemErroPadrao() {
		addMensagemErro("Um erro ocorreu durante a execução desta operação. "
				+ "Por favor, contacte o suporte através do \"Abrir Chamado\".");
	}

	/**
	 * Adiciona uma mensagem de erro para ser exibida no sistema.
	 */
	public void addMensagemErro(String mensagem) {
		addMessage(mensagem, TipoMensagemUFRN.ERROR);
	}

	/**
	 * Adiciona uma mensagem de aviso para ser exibida no sistema. 
	 */
	public void addMensagemWarning(String mensagem) {
		addMessage(mensagem, TipoMensagemUFRN.WARNING);
	}

	/**
	 * Adiciona uma mensagem de informação para ser exibida no sistema.
	 */
	public void addMensagemInformation(String mensagem) {
		addMessage(mensagem, TipoMensagemUFRN.INFORMATION);
	}

	/**
	 * Adiciona uma mensagem de erro de um tipo especificado para ser exibida no sistema.
	 * @param mensagem Mensagem a ser exibida
	 * @param tipo Tipo da Mensagem
	 */
	public void addMessage(String mensagem, TipoMensagemUFRN tipo) {
		erros.addMensagem(new MensagemAviso(mensagem, tipo));
		hasErrors();
	}
	
	/**
	 * Adiciona uma coleção de objetos MensagemAviso para que as
	 * mensagens sejam exibidas no sistema.
	 */
	@Deprecated
	public void addMensagens(Collection<MensagemAviso> mensagens) {
		for (MensagemAviso msg : mensagens)
			erros.addMensagem(msg);
		hasErrors();
	}

	/**
	 * Adiciona um objeto ListaMensagem para que as
	 * mensagens sejam exibidas no sistema.
	 */
	public boolean addMensagens(ListaMensagens listaMensagens) {
		for (MensagemAviso msg : listaMensagens.getMensagens())
			erros.addMensagem(msg);
		return hasErrors();
	}
	
	/**
	 * Adiciona um objeto ListaMensagem para as que
	 * mensagens sejam exibidas no sistema em páginas que usam Ajax.
	 */
	public void addMensagensAjax(ListaMensagens listaMensagens) {
		erros.addAll(listaMensagens);
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		erros = new ListaMensagens();
	}
	
	
	/**
	 *  Adiciona uma mensagem de informação para as páginas que usam ajax. Para funcionar é preciso incluir 
	 *  /WEB-INF/jsp/include/errosAjax.jsp 
	 *
	 * @param mensagem
	 */
	public void addMensagemInfoAjax(String mensagem){
		erros.addInformation(mensagem);
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		erros = new ListaMensagens();
	}
	
	/**
	 * Adiciona uma mensagem de warning para as páginas que usam ajax. Para funcionar é preciso incluir 
	 *  /WEB-INF/jsp/include/errosAjax.jsp 
	 *  
	 * @param mensagem
	 */
	public void addMensagemWarningAjax(String mensagem){
		erros.addWarning(mensagem);
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		erros = new ListaMensagens();
	}
	
	
	/**
	 * Adiciona uma mensagem de erro para as páginas que usam ajax. Para funcionar é preciso incluir 
	 *  /WEB-INF/jsp/include/errosAjax.jsp 
	 *  
	 * @param mensagem
	 */
	public void addMensagemErroAjax(String mensagem){
		erros.addErro(mensagem);
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		erros = new ListaMensagens();
	}
	
	/**
	 * Adiciona uma mensagem ao sistema buscando-a no banco de dados,
	 * de acordo com o código passado como parâmetro.
	 * @param codigo
	 */
	public void addMensagemAjax(String codigo, Object... params) {
		MensagemAviso msg = UFRNUtils.getMensagem(codigo, params);
		
		erros.addMensagem(msg);
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		erros = new ListaMensagens();
	}
	
	/**
	 * ----------------------------------------------------
	 * - MÉTODOS PARA TRATAMENTO DE EXCEÇÕES
	 * ----------------------------------------------------
	 */
	
	/**
	 * Analisa a exceção e notifica o erro por e-mail
	 */
	public void analisaExcecao(Throwable e, HttpServletRequest req,
			HttpServletResponse res) throws IOException, ServletException {
		ErrorUtils.enviaAlerta(e, req);
	}
	
	/**
	 * Notifica o erro em produção através do envio de e-mails.
	 */
	public void notifyError(Exception e) {

		try {
			analisaExcecao(e, getCurrentRequest(), getCurrentResponse());
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		e.printStackTrace();
	}
	
	/**
	 * Método que faz o tratamento padrão de exceções no controlador
	 * pode personalizar a msg do usuário através do parâmetro msg.
	 * Adiciona mensagem de erro padrão e envia e-mail de erro.
	 */
	public String tratamentoErroPadrao(Exception e, String msg){
		notifyError(e);
		if( isEmpty(msg) )
			addMensagemErroPadrao();
		else
			addMensagemErro(msg);
		e.printStackTrace();
		return null;
	}
	
	/**
	 * Método que faz o tratamento padrão de exceções do controlador.
	 * Adiciona mensagem de erro padrão e envia e-mail de erro.
	 */
	public String tratamentoErroPadrao(Exception e){
		return tratamentoErroPadrao(e, null);
	}
	
	
	/**
	 * ----------------------------------------------------
	 * - MÉTODOS PARA NAVEGAÇÃO ENTRE PÁGINAS
	 * ----------------------------------------------------
	 */
	
	/**
	 * Redireciona o usuário para uma url concatenando
	 * o contexto da aplicação.
	 */
	public String redirect(String url) {
		String context = getContextPath();
		if (!url.startsWith(context) && !url.startsWith("http://"))
			url = context + url;
		return redirectSemContexto(url);
	}

	/**
	 * Redireciona o usuário para uma url sem concatenar
	 * o contexto.
	 */
	public String redirectSemContexto(String url) {
		
		try {
			getCurrentResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Redireciona para páginas concatenando o contexto
	 * e renomeando a extensão de jsp para jsf.
	 */
	public String redirectJSF(String url) {
		
		if (url.indexOf(getContextPath()) == -1)
			url = getContextPath() + "/"+ url;
		if ( url.endsWith("jsp") )
			url = url.substring(0,url.length() -3 ) + "jsf";
		
		redirectSemContexto(url);
		return null;
	}
	
	/**
	 * Redireciona para a mesma página carregada.
	 */
	public String redirectMesmaPagina() {
		redirectJSF(getCurrentURL());
		return null;
	}
	
	/**
	 * Realiza um forward para a jsp passada como parâmetro.
	 */
	public String forward(String url) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		UIViewRoot view = app.getViewHandler().createView(context, url);
		context.setViewRoot(view);
		context.renderResponse();

		// Retorna null para evitar o return null no action do Managed Bean
		return null;
	}

	/**
	 * Remove o bean da sessão e redireciona o usuário para
	 * a página inicial do subsistema atual.
	 */
	public String cancelar() {
		// removendo da sessao
		resetBean();
		
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * ----------------------------------------------------
	 * - MÉTODOS PARA ACESSO À OBJETOS DA API DE SERVLETS -
	 * ----------------------------------------------------
	 */
	
	
	/**
	 * Possibilita o acesso ao HttpServletRequest.
	 */
	public HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	/**
	 * Possibilita o acesso ao HttpServletResponse.
	 */
	public HttpServletResponse getCurrentResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}

	/**
	 * Possibilita o acesso ao HttpSession.
	 */
	public HttpSession getCurrentSession() {
		return getCurrentRequest().getSession(true);
	}

	/**
	 * Retorna o contexto da aplicação WEB
	 */
	public String getContextPath() {
		return RequestUtils.getContextPath(getCurrentRequest());
	}

	/**
	 * Retorna a URL atual
	 */
	public String getCurrentURL() {
		return RequestUtils.getCurrentURL(getCurrentRequest());
	}

	/**
	 * Pega um parâmetro em request
	 * @param param Nome do parâmetro
	 * @return valor do parâmetro em request 
	 */
	public String getParameter(String param) {
		return RequestUtils.getParameter(param, getCurrentRequest());
	}

	/**
	 * Retorna um array com o conjunto de valores para um parâmetro em request
	 * @param param Nome do parâmetro
	 * @return array com todos os valores do parâmetro.
	 */
	public String[] getParameterValues(String param) {
		return RequestUtils.getParameterValues(param, getCurrentRequest());
	}
	
	/**
	 * Retorna um array de inteiros com o conjunto de valores 
	 * para um parâmetro em request.
	 */
	public Integer[] getParameterIntValues(String param) {
		return RequestUtils.getParameterIntValues(param, getCurrentRequest());
	}

	/**
	 * Pega um parâmetro em request e converte para Integer. Caso
	 * o parâmetro não exista, retorna null.
	 */
	public Integer getParameterInt(String param) {
		return RequestUtils.getParameterInt(param, getCurrentRequest());
	}

	/**
	 * Pega um parâmetro em request e converte para Integer. Caso
	 * o parâmetro não exista, retorna o valor passado como valor padrão.
	 * @param param Nome do parâmetro
	 * @param defaultValue Valor padrão, caso o parâmetro seja null.
	 */
	public Integer getParameterInt(String param, Integer defaultValue) {
		return RequestUtils.getParameterInt(param, defaultValue, getCurrentRequest());
	}

	/**
	 * Pega um parâmetro em request e converte para Long. Caso
	 * o parâmetro não exista, retorna null.
	 */
	public Long getParameterLong(String param) {
		return RequestUtils.getParameterLong(param, getCurrentRequest());
	}

	/**
	 * Pega um parâmetro em request e converte para Long. Caso
	 * o parâmetro não exista, retorna o valor passado como valor padrão.
	 * @param param Nome do parâmetro
	 * @param defaultValue Valor padrão, caso o parâmetro seja null.
	 */
	public Long getParameterLong(String param, Long defaultValue) {
		return RequestUtils.getParameterLong(param, defaultValue, getCurrentRequest());
	}

	/**
	 * Pega um parâmetro em request e converte para char. Se
	 * o parâmetro for null, retorna espaço.
	 */
	public char getParameterChar(String param){
		return RequestUtils.getParameterChar(param, getCurrentRequest());
	}
	
	/**
	 * Pega um parâmetro em request e converte para char. Se
	 * o parâmetro for null, retorna o char passado como parâmetro.
	 */
	public char getParameterChar(String param, char defaultValue){
		return RequestUtils.getParameterChar(param, defaultValue, getCurrentRequest());
	}
	
	/**
	 * Pega um parâmetro em request e converte para Boolean. Retorna
	 * true se o valor do parâmetro for a String "true" (não case sensitive)
	 * e false para qualquer outro caso, inclusive null.
	 */
	public Boolean getParameterBoolean(String param) {
		return RequestUtils.getParameterBoolean(param, getCurrentRequest());
	}

	/**
	 * Remove todos os atributos da sessão, com exceção do usuário logado e
	 * do Acesso Menu.
	 */
	@SuppressWarnings("unchecked")
	protected void removerAtributosSessao() {
		Enumeration<String> atributos = getCurrentSession().getAttributeNames();
		while(atributos.hasMoreElements()) {
			String atributo = atributos.nextElement();
			if (!"usuario".equals(atributo) && !"acesso".equals(atributo)) {
				getCurrentSession().removeAttribute(atributo);
			}
		}
	}
	
	/**
	 * Acessa o external context do JavaServer Faces
	 **/
	private ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
	
	/**
	 * Retorna o usuário logado.
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <V extends UsuarioGeral> V getUsuarioLogado() {
		return (V) getCurrentRequest().getSession().getAttribute("usuario");
	}
	
	/**
	 * Verifica se o usuário logado tem o papel passado no parâmetro
	 * 
	 * @param papel
	 * @throws SegurancaException
	 */
	public void checkRole(int papel) throws SegurancaException {
		UFRNUtils.checkRole(getUsuarioLogado(), papel);
	}

	/**
	 * Verifica se o usuário logado tem algum dos papéis passados nos parâmetros
	 * 
	 * @param papeis
	 * @throws SegurancaException
	 */
	public void checkRole(int[]... papeis) throws SegurancaException {
		
		int[] res = ArrayUtils.EMPTY_INT_ARRAY;
		
		for (int[] is : papeis) {
			res = ArrayUtils.addAll(res, is);
		}
		
		UFRNUtils.checkRole(getUsuarioLogado(), res);
	}
	
	
	/**
	 * Verifica se o usuário logado tem algum dos papéis passados nos parâmetros
	 * 
	 * @param papeis
	 * @throws SegurancaException
	 */
	public void checkRole(int... papeis) throws SegurancaException {
		UFRNUtils.checkRole(getUsuarioLogado(), papeis);
	}
	
	/**
	 * Verifica se o usuário logado tem os papéis passados como parâmetro
	 * e se esses papéis são válidos para a unidade do parâmetro.
	 * @throws SegurancaException 
	 */
	public void checkRole(UnidadeGeral unidade, int... papeis) throws SegurancaException {
		UFRNUtils.checkRole(getUsuarioLogado(), unidade, papeis);
	}
	
	/**
	 * Verifica se o usuário logado tem os papéis passados como parâmetro
	 * e se esses papéis são válidos para a unidade (considerando a hierarquia) do parâmetro.
	 * @param unidade
	 * @param papeis
	 * @throws SegurancaException
	 */
	public void checkHierarchyRole(UnidadeGeral unidade, int... papeis) throws SegurancaException {
		UFRNUtils.checkHierarchyRole(getUsuarioLogado(), unidade, papeis);
	}

	/**
	 * Retorna true se usuário possuir alguns dos papeis passados como
	 * parâmetro.
	 * @param papeis
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public boolean isUserInRole(int[]... papeis) {

		int[] res = ArrayUtils.EMPTY_INT_ARRAY;
		
		for (int[] is : papeis) {
			res = ArrayUtils.addAll(res, is);
		}
		
		UsuarioGeral user = getUsuarioLogado();
		for (int papel : res) {
			if (user.isUserInRole(papel))
				return true;
		}

		return false;
	}
	
	/**
	 * Retorna true se usuário possuir alguns dos papeis passados como
	 * parâmetro.
	 * @param papeis
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public boolean isUserInRole(int... papeis) {

		UsuarioGeral user = getUsuarioLogado();
		for (int papel : papeis) {
			if (user.isUserInRole(papel))
				return true;
		}

		return false;
	}

	/**
	 * Retorna o registro de entrada do usuário logado.
	 * @return
	 */
	public RegistroEntrada getRegistroEntrada() {
		UsuarioGeral usuarioLogado = getUsuarioLogado();
		if (usuarioLogado != null)
			return usuarioLogado.getRegistroEntrada();
		else
			return null;
	}
	
	/**
	 * Retorna o registro de acesso público da pessoa que está
	 * acessando a área pública dos sistemas.
	 * @return
	 */
	public RegistroAcessoPublico getAcessoPublico() {
		return (RegistroAcessoPublico) getCurrentSession().getAttribute("REGISTRO_ACESSO_PUBLICO");
	}
	
	/**
	 * Possibilita alterar o tempo de duração da sessão para o usuário
	 * logado. O tempo deve ser informado em minutos.
	 * 
	 * @param tempoMinutos
	 */
	public void setTempoSessao(int tempoMinutos) {
		getCurrentSession().setMaxInactiveInterval(tempoMinutos * 60);
	}
	
}