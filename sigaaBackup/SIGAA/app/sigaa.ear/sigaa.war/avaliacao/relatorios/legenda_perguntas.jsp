<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Legenda das Perguntas e Alternativas da Avalia��o Institucional</h2>

<table class="tabelaRelatorio" width="100%">
	<thead>
		<tr>
			<th>C�digo</th>
			<th>Descri��o</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{legendas}" var="linha">
			<tr>
				<td>${linha.key}</td>
				<td>${linha.value}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>