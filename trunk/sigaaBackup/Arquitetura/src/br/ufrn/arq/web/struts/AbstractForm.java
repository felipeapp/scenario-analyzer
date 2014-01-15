/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 08/09/2004
 */
package br.ufrn.arq.web.struts;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe de Form que todas os Forms do sistema devem herdar.
 *
 * @author Gleydson Lima
 *
 */
public abstract class AbstractForm extends ActionForm {

	public Object getDominio() {
		return null;
	}

	/** Valor da ação da Constante */
	private int acao;

	/** Formatador Padrão de Datas */
	private SimpleDateFormat df;

	/** Número da página */
	private int pageNum;

	/** Tamanho máximo da página */
	private int pageSize;

	private String data;

	private boolean invalidDate;

	private DecimalFormat decF;

	private SimpleDateFormat horaFormat;

	private double valorDouble;

	private String valor;

	private boolean invalidValor;

	public static final String[] mesesArray = { "Janeiro", "Fevereiro",
			"Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro",
			"Outubro", "Novembro", "Dezembro" };

	List<String> meses;

	List<String> mesesSemInicial;

	public AbstractForm() {
		df = new SimpleDateFormat("dd/MM/yyyy");
		horaFormat = new SimpleDateFormat("HH:mm");
		decF = new DecimalFormat("#,##0.00");

		df.setLenient(false);
		horaFormat.setLenient(false);

		meses = new ArrayList<String>();
		meses.addAll(Arrays.asList(mesesArray));

		mesesSemInicial = new ArrayList<String>();
		mesesSemInicial.add("Informe o Mês");
		mesesSemInicial.addAll(Arrays.asList(mesesArray));
	}

	/**
	 * @return Retorna meses.
	 */
	public List<String> getMeses() {
		return meses;
	}

	public List<String> getMesesSemInicial() {
		return mesesSemInicial;
	}

	/**
	 * @param meses
	 *            The meses to set.
	 */
	public void setMeses(List<String> meses) {
		this.meses = meses;
	}

	/**
	 * @return Retorna acao.
	 */
	public int getAcao() {
		return acao;
	}

	/**
	 * @param acao
	 *            The acao to set.
	 */
	public void setAcao(int acao) {
		this.acao = acao;
	}

	/**
	 * @return Retorna data.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Este método faz o parsing da Data. Caso a mesma seja inválida, o boolean
	 * invalidDate é setado como true para que a Action possa testar se a data
	 * do Form é inválida independente de validate.
	 *
	 * @param data
	 *            The data to set.
	 */
	public void setData(String data) {

		try {
			this.data = data;
			df.parse(data);
			invalidDate = false;
		} catch (Exception e) {
			invalidDate = true;
		}
	}

	/**
	 * @return Retorna dataObj.
	 */
	public Date getDataObj() {
		try {
			return df.parse(data);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return Retorna invalidDate.
	 */
	public boolean isInvalidDate() {
		return invalidDate;
	}

	/**
	 * Retorna a data contida na String. Caso a data seja inválida, é retornado
	 * null
	 *
	 * @param data
	 * @return
	 */
	public Date parseDate(String data) {
		return Formatador.getInstance().parseDate(data);
	}

	public String formataDate(Date data) {
		return Formatador.getInstance().formatarData(data);
	}

	public Integer parseInt(String string) {
		Integer i = null;
		try {
			i = Integer.parseInt(string.trim());
		} catch (Exception e) {
			// Silencia
		}

		return i;
	}

	public static Date descartarHoras(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);

		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	public Double parseValor(String valor) {
		try {
			return new Double(decF.parse(valor).doubleValue());
		} catch (Exception e) {
			return new Double(0);
		}
	}

	public Double parseValorNull(String valor) {
		try {
			return new Double(decF.parse(valor).doubleValue());
		} catch (Exception e) {
			return null;
		}
	}

	public Float parseQuantidade(String quantidade) {
		try {
			if (quantidade.length() <= 3 && quantidade.contains(".")) {
				return null;
			}
			return new Float(decF.parse(quantidade).floatValue());
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Este método retira pontos, traço e barras de um CPF ou CNPJ e o
	 * transforma em uma String
	 *
	 * @param req
	 */
	public String parseStringCPFCNPJ(String cpfString) {

		return Formatador.getInstance().parseStringCPFCNPJ(cpfString);
	}

	/**
	 * Valida se o ano de entrada é aceitável, baseado em regras gerais.
	 *
	 * @param ano
	 * @return
	 */

	// TODO: Gleydson - Melhorar a validação do ano.
	public boolean validaAno(int ano) {

		int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

		if (ano <= 1900 || ano > anoAtual) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Valida endereços de mail
	 *
	 * @param email
	 * @return true se email for válido
	 */
	public boolean validaEmail(String email) {
		return (!isEmpty(email) && email.trim().equals(email)
				&& email.contains(".") && email.contains("@") && !email
				.contains(" "));
	}

	/**
	 * @return Retorna valor.
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor
	 *            The valor to set.
	 */
	public void setValor(String valor) {

		try {
			valorDouble = decF.parse(valor).doubleValue();
			invalidValor = false;
			this.valor = valor;
		} catch (ParseException e) {
			valorDouble = 0;
			invalidValor = true;
		}
	}

	/**
	 * @return Retorna invalidValor.
	 */
	public boolean isInvalidValor() {
		return invalidValor;
	}

	/**
	 * @return Retorna valorDouble.
	 */
	public double getValorDouble() {
		return valorDouble;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public boolean validaHora(String hora) {
		try {
			horaFormat.parse(hora);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * Valida se string é diferente de null e não é vazia
	 *
	 * @return
	 */
	public final boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}

	/**
	 * Verifica se objeto é nulo eu possui id nulo
	 *
	 * @param persistDB
	 * @return
	 */
	public final boolean isEmpty(PersistDB persistDB) {
		return persistDB == null || persistDB.getId() <= 0;

	}

	/**
	 * Retorna o ano atual
	 *
	 * @return
	 */
	public int getAnoAtual() {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR);

	}

	/**
	 * Retorna o mes atual
	 *
	 * @return
	 */
	public int getMesAtual() {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.MONTH) + 1;

	}

	/**
	 * Verifica se uma operação está ativa dentro de um form, para evitar a
	 * validação em operações inativas.
	 */
	public boolean checkOperacaoAtivaForm(HttpServletRequest req,
			String operacao) {
		if (req.getSession().getAttribute("operacaoAtiva") == null)
			return false;
		else if (!req.getSession().getAttribute("operacaoAtiva").toString()
				.equals(operacao))
			return false;

		return true;
	}

	/**
	 * Verifica se uma operação está ativa dentro de um form usado por várias
	 * operações, para evitar a validação em operações inativas.
	 */
	public boolean checkOperacoesAtivasForm(HttpServletRequest req,
			String[] operacoes) {

		if (req.getSession().getAttribute("operacaoAtiva") == null) {
			return false;
		} else {
			for (int i = 0; i < operacoes.length; i++) {
				if (req.getSession().getAttribute("operacaoAtiva").toString()
						.equals(operacoes[i]))
					return true;
			}
		}

		return false;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	protected void addErro(ActionErrors erros, String mensagem) {
		erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(mensagem));
	}

	protected void addErro(ActionErrors erros, String mensagem, Object... args) {
		erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(mensagem,
				args));
	}

	/**
	 * Retorna o usuário logado
	 *
	 * @param req
	 * @return Usuario
	 */
	public UsuarioGeral getUsuarioLogado(HttpServletRequest req)
			throws ArqException {

		UsuarioGeral user = (UsuarioGeral) req.getSession(false).getAttribute(
				"usuario");

		if (user == null) {
			throw new ArqException("Usuário não logado");
		}
		return user;

	}

	public void erroDAO(ActionErrors erros, Exception e) {
		erros.clear();
		addErro(erros, "dao.generic.desconhecido");

		Mail.sendAlertaAdmin("Erro ao acessar banco em form", e);
		System.err.println("Erro ao acessar banco em form");
		e.printStackTrace();
	}

	public boolean horaValida(String hora) {
		try {
			horaFormat.parse(hora);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public void validateRequired(String valor, String campo,
			HttpServletRequest req) {

		if (valor == null || valor.equals("") || valor.trim().equals("")) {
			addMensagemErro(campo + ": Campo obrigatório não informado", req);
		}
	}



	/** Métodos de validação */

	public Date validaData(String data, String campo, HttpServletRequest req) {
		Date dataObj = parseDate(data);
		if (dataObj != null) {
			return dataObj;
		} else {
			addMensagemErro(campo + ": Formato de Data Inválido", req);
			return null;
		}
	}

	public void validaInt(int valor, String campo, HttpServletRequest req) {
		if (valor <= 0) {
			addMensagemErro(campo + ": O valor deve ser maior que zero", req);
		}
	}

	public void validaIntGt(int valor, String campo, HttpServletRequest req) {
		if (valor < 0) {
			addMensagemErro(campo + ": O valor deve ser maior ou igual a zero",
					req);
		}
	}
	
	/**
	 * Adiciona uma mensagem ao sistema buscando-a no banco de dados,
	 * de acordo com o código passado como parâmetro.
	 * @param req
	 * @param codigo
	 * @param params
	 */
	public void addMensagem(HttpServletRequest req, String codigo, Object... params) {
		MensagemAviso msg = UFRNUtils.getMensagem(codigo, params);
		addMensagem(msg, req);

	}

	/**
	 * Adiciona mensagem na lista
	 * @param mensagem
	 * @param req
	 */
	public void addMensagem(MensagemAviso mensagem, HttpServletRequest req) {

		ListaMensagens lista = getMensagens(req);
		if (lista == null) {
			lista = new ListaMensagens();
			req.getSession().setAttribute("mensagensAviso", lista);
		}
		lista.addMensagem(mensagem);

	}

	public void addMensagemErro(String msgConteudo, HttpServletRequest req) {
		MensagemAviso msg = new MensagemAviso(msgConteudo,
				TipoMensagemUFRN.ERROR);
		addMensagem(msg, req);
	}

	/**
	 * Adiciona mensagem na lista
	 * @param msgConteudo
	 * @param tipo
	 * @param req
	 */
	public void addMensagem(String msgConteudo, TipoMensagemUFRN tipo,
			HttpServletRequest req) {
		MensagemAviso msg = new MensagemAviso(msgConteudo, tipo);
		addMensagem(msg, req);
	}

	/**
	 * Testa se existe erro em request
	 *
	 * @param req
	 * @return
	 */
	public boolean hasError(HttpServletRequest req) {
		ListaMensagens listaMensagens = getMensagens(req);
		return (listaMensagens != null && listaMensagens.getErrorMessages() != null && listaMensagens
				.getErrorMessages().size() > 0);
	}
	
	public ActionErrors errors(HttpServletRequest req) {
		if (hasError(req)) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("empty"));
			return errors;
		} else {
			return new ActionErrors();
		}
	}

	/**
	 * Retorna a lista de mensagens armanzeada em request
	 * @param req
	 * @return
	 */
	public ListaMensagens getMensagens(HttpServletRequest req) {
		ListaMensagens lista = (ListaMensagens) req
				.getSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);

		return lista;
	}


	/**
	 * Verifica se o usuï¿½rio logado tem o papel informado
	 *
	 * @param papel
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRole(int papel, HttpServletRequest req)
			throws SegurancaException, ArqException {

		UsuarioGeral user = getUsuarioLogado(req);
		if (!user.isUserInRole(papel)) {
			throw new SegurancaException(
					"Usuário não autorizado a realizar a operação");
		}
	}

	/**
	 * Verifica se o usuï¿½rio logado possui um dos papï¿½is informados no array
	 *
	 * @param papeis
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRole(int[] papeis, HttpServletRequest req)
			throws SegurancaException, ArqException {

		UsuarioGeral user = getUsuarioLogado(req);

		for (int a = 0; a < papeis.length; a++) {
			if (user.isUserInRole(papeis[a])) {
				return;
			}
		}
		throw new SegurancaException(
				"Usuário não autorizado a realizar esta operação");
	}
	/**
	 * Retorna true se usuï¿½rio possuir alguns dos papeis passados como
	 * parï¿½metro
	 *
	 * @param req
	 * @param papeis
	 * @return true se usuï¿½rio possuir papeis
	 * @throws SegurancaException
	 *             se nï¿½o houver usuï¿½rio logado
	 */
	public boolean isUserInRole(HttpServletRequest req, int... papeis)
			throws SegurancaException, ArqException {

		UsuarioGeral user = getUsuarioLogado(req);
		for (int papel : papeis) {
			if (user.isUserInRole(papel))
				return true;
		}

		return false;
	}

	public static int getSubSistemaAtual(HttpServletRequest req) {
		Object subsistema = null;
		HttpSession session = req.getSession(false);

		if (session != null)
			subsistema = session.getAttribute("subsistema");

		if (subsistema instanceof SubSistema) {
			return ((SubSistema) subsistema).getId();
		} else {
			return (subsistema == null) ? 0 : Integer.parseInt(subsistema
					.toString());
		}
	}


}