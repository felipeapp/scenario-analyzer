<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Qualifica��o</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoQualificacao.preCadastrar}"
			 value="Cadastrar Tipo de Qualifica��o" />
		</div>
	</h:form>
	<h:outputText value="#{tipoQualificacao.create}" />
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Qualifica��o</caption>
		<thead>
			<td>Descri��o</td>
		</thead>
		<c:forEach items="${tipoQualificacao.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
