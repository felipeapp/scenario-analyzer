<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
-->
</style>

<f:view>
	<h:outputText value="#{relatoriosTecnico.create}"/>
	<c:set value="${relatoriosTecnico.relatorio}" var="relatorio_"/>
	<hr>
	<table width="100%">
		<caption><b>Quantitativo de Alunos Aprovados, Reprovados e Trancados por Disciplina</b></caption>
			<tr>
				<th>Unidade:</th>
				<td> <b>${usuario.vinculoAtivo.unidade.nome}</b></td>
				<th>Ano-Período:</th>
					<td><b><h:outputText value="#{relatoriosTecnico.ano}"/>.<h:outputText value="#{relatoriosTecnico.periodo}"/></b>
				</td>
			</tr>
	</table>
	<hr>
	<table width="100%" style="font-size: 10px">
		<caption><b>Legenda</b></caption>
			<tr>
				<td><b>Disciplina:</b><i> Código e Nome da Disciplina</i> </td>
				<td><b>Tu:</b> <i>Nº de turmas daquela disciplina</i></td>
			</tr>
			<tr>
				<td><b>AP:</b> <i>Nº de alunos com aprovação/aproveitamentos</i></td>
				<td><b>RP:</b> <i>Nº de alunos reprovados por nota/falta</i></td>
			</tr>
			<tr>
				<td><b>TR:</b> <i>Nº de alunos trancados</i></td>
				<td><b>Total:</b> <i>Total de matrículas na disciplina</i></td>
			</tr>
			<tr>
				<td colspan="2"><b>MAT:</b> <i>Nº de alunos com matrícula ativa na disciplina no presente momento</i></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Número de Disciplinas: ${fn:length(relatorio_)}</b></caption>
	<thead>
		<tr>
			<th style="text-align: left;">Disciplina</th>
			<th style="text-align: right;">Tu</th>
			<th style="text-align: right;">MAT</th>
			<th style="text-align: right;">AP</th>
			<th style="text-align: right;">RP</th>
			<th style="text-align: right;">TR</th>
			<th style="text-align: right;">Total</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${relatorio_}" var="linha">
		<tr class="componentes">
			<td style="text-align: left;">${linha.value.codigo} - ${linha.value.nome}</td>
			<td style="text-align: right;">${linha.value.turmas}</td>
			<td style="text-align: right;">${linha.value.matriculados}</td>
			<td style="text-align: right;">${linha.value.aprovados}</td>
			<td style="text-align: right;">${linha.value.reprovados}</td>
			<td style="text-align: right;">${linha.value.trancados}</td>
			<td style="text-align: right;">${linha.value.matriculados + linha.value.aprovados + linha.value.reprovados + linha.value.trancados}</td>
		</tr>
	</c:forEach>
		<tr>
   			<td colspan=7><hr></td>
   		</tr>
   	</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
