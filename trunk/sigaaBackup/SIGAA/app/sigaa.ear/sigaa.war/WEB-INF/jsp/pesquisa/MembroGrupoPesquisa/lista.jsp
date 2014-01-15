<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.GrupoPesquisaForm"%>

<%
	GrupoPesquisaForm grupoForm = (GrupoPesquisaForm) request.getAttribute("formGrupoPesquisa");
	int finalidade = grupoForm.getFinalidadeBusca();
%>

<%@page import="br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa"%>
<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	
	<c:if test="<%= finalidade == GrupoPesquisaForm.DECLARACAO_COORDENADOR %>">
		Declaração de Coordenação de Base de Pesquisa
	</c:if>

	<c:if test="<%= finalidade == GrupoPesquisaForm.DECLARACAO_COLABORADOR %>">
		Declaração de Colaborador
	</c:if>
</h2>

<html:form action="/pesquisa/buscarMembrosGrupoPesquisa" method="post">
<html:hidden property="finalidadeBusca"/>

<table class="formulario" align="center" style="width: 99%">
	<caption class="listagem">Buscar Bases de Pesquisa</caption>

	<tr>
		<td> <html:checkbox property="filtros" styleId="nome" value="<%="" + GrupoPesquisaForm.GRUPO_PESQUISA_NOME%>" styleClass="noborder"/> </td>
		<td> <label for="nome">Nome:</label> </td>
		<td>
			<html:text property="obj.nome" size="60" onchange="$(nome).checked = true;"/>
		</td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="coordenador" value="<%="" + GrupoPesquisaForm.COORDENADOR%>" styleClass="noborder"/> </td>
		<td> <label for="coordenador">Coordenador:</label> </td>
		<td>
			<c:set var="idAjax" value="obj.coordenador.id"/>
			<c:set var="nomeAjax" value="obj.coordenador.pessoa.nome"/>
			<c:set var="todosDocentes" value="true"/>
			<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
			<script type="text/javascript">
				function docenteOnFocus() {
					getEl('coordenador').dom.checked = true;
				}
			</script>
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="listar" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<br/> <br/>

<c:choose>
<c:when test="${not empty lista }">
<table class="listagem">
	<caption>Bases encontradas</caption>
	<thead>
		<tr>
			<th> Código/Nome</th>
			<th> Coordenador </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="base" items="${lista}" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${base.codigo} - ${base.nome}</td>
			<td>${base.coordenador.pessoa.nome}</td>
			
			<c:if test="<%= finalidade == GrupoPesquisaForm.DECLARACAO_COORDENADOR %>">
			<td>
				<ufrn:link action="/pesquisa/buscarMembrosGrupoPesquisa" param="obj.id=${base.id}&dispatch=emitirDeclaracaoCoordenadorBase">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Emitir Declaração"
						title="Emitir Declaração" />
				</ufrn:link>
			</td>
			</c:if>
			
<%-- TODO: acrescentar a declaração de colaborador da base quando estiver definido no módulo de pesquisa.

			<c:if test="<%= finalidade == GrupoPesquisaForm.DECLARACAO_COLABORADOR %>">
 			<td>
				<ufrn:link action="/pesquisa/emitirDeclaracaoDocente" param="obj.id=${membro.id}&dispatch=emitirDeclaracaoOrientacoes">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Emitir Declaração"
						title="Emitir Declaração" />
				</ufrn:link>
			</td>
			</c:if>
--%>		</tr>
		</c:forEach>
	</tbody>
</table>
</c:when>
</c:choose>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
