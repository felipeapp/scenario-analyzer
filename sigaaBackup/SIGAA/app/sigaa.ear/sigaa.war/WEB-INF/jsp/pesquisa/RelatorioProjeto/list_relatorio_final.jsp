<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.RelatorioProjetoForm"%>

<h2>
	<ufrn:subSistema /> &gt; Relatórios Anuais de Projetos
</h2>

<html:form action="/pesquisa/avaliarRelatorioProjeto" method="post">
<table class="formulario" align="center" style="width: 99%">
	<caption class="listagem">Critérios de Busca dos Relatórios </caption>

	<tr>
		<td> <html:checkbox property="filtros" styleId="anoProjeto" value="<%="" + RelatorioProjetoForm.BUSCA_ANO%>" styleClass="noborder" /></td>
		<td> <label for="anoProjeto">Ano dos Projetos:</label> </td>
		<td>
			<html:text property="obj.projetoPesquisa.ano" size="6" maxlength="5" onchange="$(anoProjeto).checked = true;"/>
		</td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="coordenador" value="<%="" + RelatorioProjetoForm.BUSCA_COORDENADOR%>" styleClass="noborder"/> </td>
		<td> <label for="coordenador">Coordenador de Projeto:</label> </td>
		<td>
			<c:set var="idAjax" value="obj.projetoPesquisa.coordenador.id"/>
			<c:set var="nomeAjax" value="obj.projetoPesquisa.coordenador.pessoa.nome"/>
			<c:set var="todosDocentes" value="true"/>
			<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
			<script type="text/javascript">
				function docenteOnFocus() {
					getEl('coordenador').dom.checked = true;
				}
			</script>
		</td>
	</tr>

	<tr>
		<td> <html:checkbox property="filtros" styleId="areaConhecimento"  value="<%="" +  RelatorioProjetoForm.BUSCA_AREA_CONHECIMENTO%>" styleClass="noborder" /> </td>
		<td> <label for="areaConhecimento"> Área de Conhecimento: </label> </td>
		<td>
			<html:select property="obj.projetoPesquisa.areaConhecimentoCnpq.id" style="width:90%" onchange="$(areaConhecimento).checked = true;">
		        <html:option value="-1"> -- SELECIONE UMA ÁREA DE CONHECIMENTO -- </html:option>
		        <html:options collection="areasConhecimento" property="id" labelProperty="nome" />
	        </html:select>
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="listRelatoriosFinais" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<c:forEach items="${projetoPesquisaForm.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer"/>
	<c:if test="<%= filtro.intValue() == RelatorioProjetoForm.BUSCA_ANO%>">
 		<script> $('anoProjeto').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioProjetoForm.BUSCA_COORDENADOR%>">
 		<script> $('coordenador').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioProjetoForm.BUSCA_AREA_CONHECIMENTO%>">
 		<script> $('areaConhecimento').checked = true;</script>
 	</c:if>
</c:forEach>

<br/> <br/>

<c:choose>
<c:when test="${not empty lista }">

<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
	 : Visualizar Relatório
	 <c:if test="${acesso.pesquisa}">
	<html:img page="/img/pesquisa/avaliar.gif" style="overflow: visible;"/>
	 : Avaliar Relatório
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>
	 : Alterar Relatório
	<html:img page="/img/delete.gif" style="overflow: visible;"/>
	 : Remover Relatório
</c:if>
</div>

<table class="listagem">
	<caption>Relatórios encontrados (${fn:length(lista)})</caption>
	<thead>
		<tr>
			<th colspan="2"> Projeto de Pesquisa</th>
			<th> Enviado em </th>
			<th> Situação </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="relatorio" items="${lista}" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${relatorio.projetoPesquisa.codigo}</td>
			<td>
				${relatorio.projetoPesquisa.titulo} <br />
				<em> Coordenador: ${relatorio.projetoPesquisa.coordenador} </em>
			</td>
			<td>
				<c:choose>
					<c:when test="${not empty relatorio.dataEnvio}">
						<ufrn:format type="datahora" name="relatorio" property="dataEnvio" />
					</c:when>
					<c:otherwise><em>indisponível</em></c:otherwise>
				</c:choose>
			</td>
			<td> ${ relatorio.statusString } </td>
			<td>
				<ufrn:link action="/pesquisa/cadastroRelatorioProjeto" param="id=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Visualizar Relatório"
						title="Visualizar Relatório" />
				</ufrn:link>
			</td>
			 <c:if test="${acesso.pesquisa}">
			<td>
				<ufrn:link action="/pesquisa/avaliarRelatorioProjeto" param="obj.id=${relatorio.id}&dispatch=edit">
					<img src="${ctx}/img/pesquisa/avaliar.gif"
						alt="Avaliar Relatório"
						title="Avaliar Relatório" />
				</ufrn:link>
			</td>
			<td>
				<ufrn:link action="/pesquisa/avaliarRelatorioProjeto" param="obj.id=${relatorio.id}&dispatch=carregarRelatorio">
					<img src="${ctx}/img/alterar.gif"
						alt="Alterar Relatório"
						title="Alterar Relatório" />
				</ufrn:link>
			</td>
			<td>
				<ufrn:link action="/pesquisa/relatorioProjeto" param="obj.id=${relatorio.id}&dispatch=remove">
					<img src="${ctx}/img/delete.gif"
						alt="Remover Relatório"
						title="Remover Relatório" />
				</ufrn:link>
			</td>
			</c:if>
		</tr>
		</c:forEach>
	</tbody>
</table>
</c:when>
</c:choose>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
