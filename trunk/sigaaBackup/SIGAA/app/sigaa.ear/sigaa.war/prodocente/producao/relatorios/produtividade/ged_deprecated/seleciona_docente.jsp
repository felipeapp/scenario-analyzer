<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Produção Intectual - Relatório de Desempenho Docente </h2>
<h:form styleClass="formulario" id="form">
<table align="center" class="formulario">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Docente: </th>

		<%--<c:if test="${acesso.prodocente}">--%>

		<td><h:inputHidden id="id" value="#{relatorioProdocente.obj.docente.id}"></h:inputHidden>
				<h:inputText id="nome"
					value="#{relatorioProdocente.obj.docente.pessoa.nome}" size="80" /> <ajax:autocomplete
					source="form:nome" target="form:id"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=D"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

		<%--</c:if>--%>

		<c:if test="${!acesso.prodocente}">
			<h:inputHidden id="id" value="#{relatorioProdocente.obj.docente.id}"/>
		</c:if>
	</tr>
	<tr>
		<th>Ano Base: </th>
		<td><h:selectOneMenu id="anoBase" value="#{relatorioProdocente.obj.anoVigencia}">
						<f:selectItems value="#{relatorioProdocente.anos}"/>
					</h:selectOneMenu> </td>
	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relátorio"
						action="#{relatorioProdocente.gerarRelatorio}"/> <h:commandButton
						value="Cancelar" action="#{relatorioProdocente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>