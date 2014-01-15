/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Tag para exibir uma tabela de informações
 * com opções de personalização.
 * 
 * @author Gleydson Lima
 *
 */
public class TableTag extends TagSupport {

	private Collection<?> collection;

	private String properties;

	private String propertiesLinks;

	private int[][] propertiesLinksRoles;

	private int[][] linksRoles;

	private int[] crudRoles;

	private String headers;

	private String sizes;

	private String title;

	// private String cssLines;

	private int pageSize;

	private int actualPage;

	private boolean crud;

	private String links;

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public boolean isCrud() {
		try {
			return (crud  && isAllowed(getCrudRoles()));
		} catch (Exception e) {
			return false;
		}
	}

	public void setCrud(boolean crud) {
		this.crud = crud;
	}

	public Collection<?> getCollection() {
		return collection;
	}

	public void setCollection(Collection<?> collection) {
		this.collection = collection;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			if (collection == null || collection.size() == 0) {
				out.print("<br><div style=\"font-style: italic; text-align:center\">" +
						"Nenhum registro encontrado de acordo com os critérios de busca informados.</div>");
				return SKIP_BODY;
			}
			Collection<?> l = collection;


			PagingInformation paging = (PagingInformation) pageContext
					.getRequest().getAttribute("pagingInformation");

			/*if (pageSize > 0 && paging != null) {
				paging.setTamanhoPagina(pageSize);
			}*/

			int totalPaginas = 0;
			int totalColunas = 0;

			if (paging != null) {
				totalPaginas = paging.getTotalPaginas();
			}

			String context = ((HttpServletRequest) pageContext.getRequest())
					.getContextPath();

			if (isCrud()) {
				out.print("<div class=infoAltRem>");
				out.println("<img src='" + context
						+ "/img/alterar.gif'>: Alterar " + title);
				out.println("<img src='" + context
						+ "/img/delete.gif'>: Remover " + title);
				out.println("<img src='" + context
						+ "/img/view.gif'>: Visualizar " + title);
				out.println("</div>");
			}

			pageContext.getOut().print(
					"<table class=listagem cellspacing=\"0\" cellpadding=\"2\">");

			if (title != null) {
				out.println("<caption>" + title + "</caption>");
			}

			StringTokenizer stHeader = new StringTokenizer(headers, ",");
			StringTokenizer stSizes  = null;
			if (sizes != null)
				stSizes = new StringTokenizer(sizes, ",");
			if (stHeader.countTokens() > 0) {
				out.println("<thead><tr>");
				while (stHeader.hasMoreTokens()) {
					String th = "<th style=\"text-align:left\"";
					if (sizes != null && stSizes != null)
						th+="width=\""+stSizes.nextToken()+"\"";
					th += "> "+stHeader.nextToken() + "</th>";
					out.println(th);
				}
				if (isCrud()) {
					out.println("<th colspan=\"2\"></th>");
				}
				if ( showLinks() ) {
					StringTokenizer st = new StringTokenizer(links,";");
					int tam = st.countTokens();
					for ( int a = 0; a < tam; a++) {
						if (getLinksRoles() == null || (getLinksRoles() != null && isAllowed(getLinksRoles()[a])))
							out.println("<th></th>");
					}
				}
				out.println("</tr></thead>");
			}

			Iterator<?> it = l.iterator();
			int a = 0;
			while (it.hasNext()) {

				Object item = it.next();
				StringTokenizer st = new StringTokenizer(properties, ",");
				totalColunas = st.countTokens();
				out.print("<tr class=\"");
				out.println( (a++ % 2 == 0 ? "linhaPar" : "linhaImpar") + "\">");

				StringTokenizer stPropLinks = null;
				if (propertiesLinks != null)
					stPropLinks = new StringTokenizer(propertiesLinks, ";");
				while (st.hasMoreTokens()) {
					out.println("<td>");
					String val = evalProperty(item, st.nextToken());
					String link = (stPropLinks != null && stPropLinks.hasMoreTokens())?stPropLinks.nextToken():null;
					if (link != null && !link.trim().equals("")) {
						out.println(getLinkRenderized(item, val, link));
					} else
						out.println(val);
					out.println("</td>");
				}
				if (isCrud()) {
					totalColunas += 2;
					String pagina = pageContext.getRequest().getParameter(
							"page");

					out.println("<td width=20>");
					out.println("<a href='?dispatch=edit&id="
							+ evalProperty(item, "id") + "&page=" + pagina
							+ "'>");
					out.println("<img src='" + context
							+ "/img/alterar.gif' alt='Alterar " + title
							+ "' title='Alterar " + title + "' border=0/>");
					out.println("</a></td>");

					out.println("<td width=20>");
					out.println("<a href='?dispatch=remove&id="
							+ evalProperty(item, "id") + "&page=" + pagina
							+ "'>");
					out.println("<img src='" + context
							+ "/img/delete.gif' alt='Remover " + title
							+ "' title='Remover " + title + "' border=0/>");
					out.println("</a></td>");

				}

				if (showLinks()) {
					StringTokenizer stLinks = new StringTokenizer(links,";");
					int tam = stLinks.countTokens();
					totalColunas += tam;
					for ( int i = 0; i < tam; i++) {
						String link = stLinks.nextToken();
						if (getLinksRoles() == null || (getLinksRoles() != null && isAllowed(getLinksRoles()[i]))) {
							out.println("<td width=\"10\">");
							out.println(getLinksRenderized(item, link));
							out.println("</td>");
						}
					}

				}

				out.println("</tr>");
			}
			if (paging != null) {
				out.println("<tfoot><tr><td colspan=" + totalColunas
						+ " align=\"center\">");

				HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
				if (totalPaginas > 1) {
					String queryString = req.getQueryString();
					try {
						int index = queryString.indexOf("page=");
						queryString = queryString.substring(0, index - 1);
					} catch (Exception e) { }
					out.println("<form action=\"?"+queryString+"\" method=\"post\">");
					if (!paging.isPrimeiraPagina())
						out.println("<input type=\"image\" title=\"Primeira Página\" name=\"\" src=\""+req.getContextPath()+"/img/primeira.gif\" " +
							"value=\"0\" onclick=\"submitTablePage(this)\" style=\"border: none\">");
					else
						out.println("<img src=\""+req.getContextPath()+"/img/primeira_des.gif\" />");
					out.print("&nbsp;&nbsp;");
					if (!paging.isPrimeiraPagina())
						out.println("<input type=\"image\" title=\"Página Anterior\" name=\"\" src=\""+req.getContextPath()+"/img/voltar.gif\" " +
							"value=\""+(paging.getPaginaAtual()-1)+"\" onclick=\"submitTablePage(this)\" style=\"border: none\">");
					else
						out.println("<img src=\""+req.getContextPath()+"/img/voltar_des.gif\" />");
					out.print("&nbsp;&nbsp;");
					out.println("<select name=\"page\" onChange=\"javascript:this.form.submit()\">");
					for (int i = 0; i < totalPaginas; i++) {
						out.println("<option value=\""+i+"\" "+((i == paging.getPaginaAtual())?"selected":"")+"" +">");
						out.println("Pág. "+ (i + 1));
					}
					out.println("</select>");
					out.print("&nbsp;&nbsp;");
					if (!paging.isUltimaPagina())
						out.println("<input type=\"image\" title=\"Próxima Página\" name=\"\" src=\""+req.getContextPath()+"/img/avancar.gif\" " +
							"value=\""+(paging.getPaginaAtual()+1)+"\" onclick=\"submitTablePage(this)\" style=\"border: none\">");
					else
						out.println("<img src=\""+req.getContextPath()+"/img/avancar_des.gif\" />");
					out.print("&nbsp;&nbsp;");
					if (!paging.isUltimaPagina())
						out.println("<input type=\"image\" title=\"Última Página\" name=\"\" src=\""+req.getContextPath()+"/img/ultima.gif\" " +
							"value=\""+(paging.getTotalPaginas()-1)+"\" onclick=\"submitTablePage(this)\" style=\"border: none\">");
					else
						out.println("<img src=\""+req.getContextPath()+"/img/ultima_des.gif\" />");
					out.println("</form>");
				} else {

					out.println("<i>Todos os resultados estão acima</i>");
				}
				out.println("</td></tr></tfoot>");
			}
			out.println("</table>");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	private boolean isAllowed(int[] roles) throws Exception {
		if (roles == null) return true;
		UsuarioGeral usuario =  (UsuarioGeral) pageContext.getSession().getAttribute("usuario");
		return usuario.isUserInRole(roles);
	}

	public boolean showLinks() throws Exception {
		if (links != null && links.length() > 0 && !links.contains(",") && isAllowed(getLinksRoles()[0]))
			return true;
		return (links != null && links.length() > 0 && links.contains(","));

	}

	public boolean showPropertiesLinks() throws Exception {
		return (propertiesLinks != null && propertiesLinks.length() > 0 && isAllowed(getPropertiesLinksRoles()[0]));
	}

	public String evalProperty(Object obj, String property) {
		try {
			Object atual = PropertyUtils.getProperty(obj, property.trim());
			if ( atual instanceof Date) {
				Date d = (Date) atual;
				if ( d.getTime() < 60*60*1000*24*2 ) {
					return  Formatador.getInstance().formatarHora((Date)atual);
				} else {
					return  Formatador.getInstance().formatarData((Date)atual);
				}
			} else if (atual instanceof Boolean){
				Boolean b = (Boolean) atual;
				if (b)
					return "Sim";
				else
					return "Não";
			}



			return atual.toString();

			// Analisa data


			/*
			 *
			 * property = property.substring(0, 1).toUpperCase() +
			 * property.substring(1); Method method =
			 * obj.getClass().getMethod("get" + property, null); Object retorno =
			 * method.invoke(obj, null); if ( retorno instanceof Date) { return
			 * Formatador.getInstance().formatarData((Date)retorno); } else {
			 * return retorno.toString(); }
			 */
		} catch (Exception e) {
			return "";
		}
	}
	public int getActualPage() {
		return actualPage;
	}

	public void setActualPage(int actualPage) {
		this.actualPage = actualPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getSizes() {
		return sizes;
	}

	public void setSizes(String sizes) {
		this.sizes = sizes;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPropertiesLinks() {
		return propertiesLinks;
	}

	public void setPropertiesLinks(String propertiesLinks) {
		this.propertiesLinks = propertiesLinks;
	}

	public int[] getCrudRoles() {
		return crudRoles;
	}

	public void setCrudRoles(int[] crudRoles) {
		this.crudRoles = crudRoles;
	}

	public int[][] getLinksRoles() {
		return linksRoles;
	}

	public void setLinksRoles(int[][] linksRoles) {
		this.linksRoles = linksRoles;
	}

	public int[][] getPropertiesLinksRoles() {
		return propertiesLinksRoles;
	}

	public void setPropertiesLinksRoles(int[][] propertiesLinksRoles) {
		this.propertiesLinksRoles = propertiesLinksRoles;
	}

	public String getLinksRenderized(Object item, String linkStr) {

		StringBuffer rendered = new StringBuffer(200);

		StringTokenizer link = new StringTokenizer(linkStr, ",");

		String imagem = link.nextToken();

		String queryString = link.nextToken();

		String descricao = null;
		try {
			descricao = link.nextToken();
		} catch (NoSuchElementException e) {}

		StringTokenizer tokenQueryString = new StringTokenizer(queryString, "?");
		String action = tokenQueryString.nextToken();
		String parameters = null;
		if ( tokenQueryString.hasMoreElements() ) {
			parameters = tokenQueryString.nextToken();
		} else {
			parameters = action; // nenhuma action informada, então ele é o token do parametro
			action = "";
		}

		StringTokenizer tokenParameters = new StringTokenizer(parameters, "&");
		String paramReplaced = "";

		while (tokenParameters.hasMoreElements()) {

			StringTokenizer tokenParam = new StringTokenizer(tokenParameters
					.nextToken(), "=");
			String paramName = tokenParam.nextToken();
			String paramValue = tokenParam.nextToken();

			if (paramValue.startsWith("{")) {
				paramValue = paramValue.substring(1, paramValue.length() - 1);
				paramReplaced += paramName + "="
						+ evalProperty(item, paramValue) + "&";
			} else {
				paramReplaced += paramName + "=" + paramValue + "&";
			}

		}

		rendered.append("<a href='" + action + "?" + paramReplaced + "'>");
		rendered.append("<img " + imagem);
		if (descricao != null) {
			rendered.append(" alt=\"" + descricao + "\" ");
			rendered.append(" title=\"" + descricao + "\" ");
		}
		rendered.append("></a>");

		return rendered.toString();
	}

	/**
	 * Renderiza um simples link com um conteudo
	 * @param val
	 * @param linkStr
	 * @return
	 */
	public String getLinkRenderized(Object item, String val, String linkStr) {
		StringBuffer rendered = new StringBuffer();

		String queryString = linkStr;

		StringTokenizer tokenQueryString = new StringTokenizer(queryString, "?");
		String action = tokenQueryString.nextToken();
		String parameters = null;
		if ( tokenQueryString.hasMoreElements() ) {
			parameters = tokenQueryString.nextToken();
		} else {
			parameters = action; // nenhuma action informada, então ele é o token do parametro
			action = "";
		}

		StringTokenizer tokenParameters = new StringTokenizer(parameters, "&");
		String paramReplaced = "";

		while (tokenParameters.hasMoreElements()) {

			StringTokenizer tokenParam = new StringTokenizer(tokenParameters
					.nextToken(), "=");
			String paramName = tokenParam.nextToken();
			String paramValue = tokenParam.nextToken();

			if (paramValue.startsWith("{")) {
				paramValue = paramValue.substring(1, paramValue.length() - 1);
				paramReplaced += paramName + "="
						+ evalProperty(item, paramValue) + "&";
			} else {
				paramReplaced += paramName + "=" + paramValue + "&";
			}

		}

		rendered.append("<a href='" + action + "?" + paramReplaced + "'>");
		rendered.append(val);
		rendered.append("</a>");

		return rendered.toString();
	}

	// "img=img/teste.jpg,detalhar.do?id={id}&dispatch=listar"

	public static void main(String[] args) {

		String teste = "tessdsd?id=${id}";

		Pattern p = Pattern.compile("{\\w}");
		Matcher m = p.matcher(teste);
		MatchResult mr = m.toMatchResult();
		for (int a = 0; a < mr.groupCount(); a++) {
			System.out.println(mr.group(a));
		}

	}

}
