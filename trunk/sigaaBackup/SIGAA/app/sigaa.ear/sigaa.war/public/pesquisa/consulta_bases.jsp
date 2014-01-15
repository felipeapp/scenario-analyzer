<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>
	 Consulta de Bases de Pesquisa
</h2>
<br>
<style>
	span.info {
		font-size: 0.8em;
		color: #444;
	}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h:outputText value="#{consultaBases.create}"/>
	<h:form id="formConsulta">

		<table class="formulario" align="center" width="60%">
		<caption class="listagem">Critérios de Busca das Bases</caption>

			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBases.filtroNome}" id="checkNome" styleClass="noborder"/> </td>
				<td width="22%">Nome:</td>
				<td>
					<h:inputText id="nome" value="#{consultaBases.obj.nome}" size="60" onfocus="$('formConsulta:checkNome').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBases.filtroCoordenador}" id="checkCoordenador" styleClass="noborder"/> </td>
				<td width="22%">Coordenador:</td>
				<td>
					<h:inputText id="nomeCoordenador"	value="#{consultaBases.obj.coordenador.pessoa.nome}" size="60" onchange="$('formConsulta:checkCoordenador').checked = true;"/>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{consultaBases.buscar}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{consultaBases.cancelar}" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
		</table>

	</h:form>

	<style>
		table.listagem tr.cota td {
			background-color: #C4D2EB;
			padding: 8px 10px 2px;
			border-bottom: 1px solid #BBB;
			font-variant: small-caps;

			font-style: italic;
		}
	</style>

	<c:set var="lista" value="${consultaBases.resultadosBusca}"/>

	<c:if test="${not empty lista}">
	<br>
	<table class="listagem" style="width: 90%;">
		<caption> Bases de Pesquisa encontradas </caption>
		<thead>
			<tr>
				<th width="10%"> Código </th>
				<th> Nome</th>
				<th> Coordenador </th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="base" items="${lista}" varStatus="status">
			<c:set var="stripes">${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }</c:set>

			<tr class="${stripes} topo">
				<td> ${base.codigo} </td>
				<td> ${base.nome} </td>
				<td valign="top"> ${base.coordenador.pessoa.nome }</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3" align="center"> <b>${fn:length(lista)} bases encontradas </b></td>
			</tr>
		</tfoot>
	</table>
	</c:if>
</f:view>
<br>
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;"><< voltar ao menu principal</a>
	</div>
<br>
<%@include file="/public/include/rodape.jsp" %>
