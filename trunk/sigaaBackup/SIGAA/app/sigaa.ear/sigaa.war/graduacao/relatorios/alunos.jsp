<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2>Lista de Discentes de Graduação</h2>
<%--	<h:outputText value="#{discenteGraduacao.create}" /> --%>
	<h:form id="busca">
		<table class="formulario" width="70%">
			<caption>Dados da Busca</caption>
			<tbody>
				<tr>
					<td><label>Curso</label></td>
					<td>
						<h:inputHidden id="idCurso" value="#{discenteGraduacao.obj.curso.id}"/>
						<h:inputText id="nomeCurso" value="#{discenteGraduacao.obj.curso.nome}" size="50" onkeyup="CAPS(this)" />

						<ajax:autocomplete source="busca:nomeCurso" target="busca:idCurso"
								baseUrl="/sigaa/ajaxCurso" className="autocomplete"
								indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=G"
								parser="new ResponseXmlToHtmlListParser()" />

						<span id="indicatorCurso" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
				<tr>
					<td><label>Ano-Período</label></td>
					<td>
						<h:inputText value="#{discenteGraduacao.obj.anoIngresso}" size="4" maxlength="4" id="anoIngresso" />
						- <h:inputText value="#{discenteGraduacao.obj.periodoIngresso}" maxlength="1" size="1" id="periodoIngresso" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{discenteGraduacao.buscar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{discenteGraduacao.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty discenteGraduacao.resultadosBusca}">
		<br>

		<table class=listagem>
			<caption class="listagem">Lista de Discentes de Graduação Encontrados</caption>
			<thead>
				<tr>
					<td>Matrícula</td>
					<td>Nome</td>
				</tr>
			</thead>
			<c:forEach items="${discenteGraduacao.resultadosBusca}" var="item">
				<tr>
					<td>${item.matricula}</td>
					<td>${item.nome}</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
