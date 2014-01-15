<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<div id="pesquisa-docente">
	<h4>Projetos de Pesquisa</h4>

	<c:set var="projetos" value="${portal.projetosPesquisa}" />

	<c:if test="${not empty projetos}">
		<table class="listagem">
			<thead>
				<tr>
					<th colspan="2">Projeto de Pesquisa</th>
					<th>Área de Conhecimento</th>
				</tr>
			</thead>

			<tbody>
				<c:set var="ano" value=""/>
				<c:forEach var="projeto" items="${projetos}" varStatus="loop">

				<c:if test="${ano != projeto.codigo.ano}">
					<c:set var="ano" value="${projeto.codigo.ano}"/>

					<c:if test="${not loop.first}">
						<tr> <td class="spacer" colspan="5"> </td> </tr>
					</c:if>

					<tr><td class="ano" colspan="5"> ${ano}</td></tr>
				</c:if>
				<tr>
					<td class="codigo"> ${projeto.codigo} </td>
					<td> ${projeto.titulo} </td>
					<td class="area"> ${projeto.projeto.areaConhecimentoCnpq.nome} </td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

	<c:if test="${empty projetos}">
		<p class="vazio">
			Nenhum projeto de pesquisa cadastrado
		</p>
	</c:if>
</div>


</f:view>
<%@include file="/public/include/rodape.jsp" %>
