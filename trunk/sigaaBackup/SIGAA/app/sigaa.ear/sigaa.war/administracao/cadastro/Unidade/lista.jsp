<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2>Unidade</h2>
	<h:outputText value="#{unidade.create}" />


	<h:form>
	<table class="formulario">
	<caption class="listagem">Buscar Unidade</caption>
	<tr>
		<td> Nome: </td>
		<td> <h:inputText value="#{unidade.nome}"/> </td>
		<td> <h:commandButton actionListener="#{unidade.buscar}" value="Buscar"/> </td>
	</tr>
	</table>
	</h:form>
	<br>

	<table class="listagem" style="width:100%">
	<caption class="listagem">Lista de Unidades</caption>
	<thead>
		<tr>

			<td>Código</td>
			<td>Unidade</td>
			<td>Sigla</td>
			<td>Tipo</td>
			<td>Gestora Acadêmica</td>
			<td>Hierarquia</td>
		</tr>
		</thead>
		<c:forEach items="${unidade.lista}" var="item">
			<tr>
				<td><ufrn:format name="item" property="codigo" type="unidade"/></td>
				<td>${item.nome}</td>
				<td>${item.sigla}</td>
				<td>${item.tipoAcademicaDesc}</td>
				<td>${item.gestoraAcademica.codigo}-${item.gestoraAcademica.nome}</td>
				<td>${item.hierarquia}</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
