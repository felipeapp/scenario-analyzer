<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

<h2>Relatório Quantitativo de Alunos Reprovados e Desnivelados </h2>

<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Unidade das Disciplinas:</th>
			<td>
				<h:outputText value="#{relatorioDiscente.centro.nome}"/>
			</td>
		</tr>
		<tr>
			<th>Unidade dos Alunos:</th>
			<td>
				<h:outputText value="#{relatorioDiscente.departamento.nome}"/>
			</td>
		</tr>
	</table>
</div>
<br/>
<table class="tabelaRelatorio" width="100%">
	<thead>
		<tr>
			<td>Componente Curricular</td>
			<td>Quantidade</td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<tr>
				<td>${linha.codigo}-${linha.nome}</td>
				<td style="text-align: right;" >${linha.count}</td>
			<tr>
		</c:forEach>
	</tbody>
</table>
<br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
