<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
table.tabelaRelatorioBorda thead tr th, table.tabelaRelatorioBorda tbody tr td {
	text-align: right;
}
</style>

<f:view>

	<h2>Relatório de Público Estimado de Ações de Extensão Por Áreas Temáticas</h2>
	<br/>
	
	<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Tipo da Ação:&nbsp;</th>
			<td> 
				<h:outputText value="#{relatorioEquipeExtensao.labelTipoAtividade}" /> 
			</td>
		</tr>
		<tr>
			<th>Situação da Ação:&nbsp;</th>
			<td> 
				<h:outputText value="#{relatorioEquipeExtensao.labelSituacaoAtividade}" /> 
			</td>
		</tr>
		<c:if test="${relatorioEquipeExtensao.ano != null}">
		<tr>
			<th>Ano:&nbsp;</th>
			<td> 
				<h:outputText value="#{relatorioEquipeExtensao.ano}" /> 
			</td>
		</tr>
		</c:if>
		<c:if test="${relatorioEquipeExtensao.inicio != null and relatorioEquipeExtensao.fim != null }">
		<tr>
			<th>Período:&nbsp;</th>
			<td> 
				<h:outputText value="#{relatorioEquipeExtensao.inicio}" /> 
				<em>a</em>
				<h:outputText value="#{relatorioEquipeExtensao.fim}" /> 
			</td>
		</tr>
		</c:if>
		</table>
	</div>
	<br /><br />
	<table class="tabelaRelatorioBorda" width="100%">

		<thead>
			<tr>
				<th style="text-align: left">Área Temática</th>
				<th>Nº de Ações</th>
				<th>Público Estimado</th>
				<th>Público Atingido</th>
				<th>Nº de Docentes</th>
				<th>Nº de Bolsistas</th>
				<th>Nº de não-Bolsistas</th>
				<th>Nº de Alunos-Pós</th>
				<th>Nº de Técnicos</th>
				<th>Nº de Externos</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="res"
				items="${relatorioEquipeExtensao.listaEstatisticas}">
				<tr>
					<td style="text-align: left">${res.descricao}</td>
					<td>${res.acoes}</td>
					<td>${res.estimado}</td>
					<td>${res.atingido}</td>
					<td>${res.docentes}</td>
					<td>${res.bolsistas}</td>
					<td>${res.nao_bolsistas}</td>
					<td>${res.alunos_pos}</td>
					<td>${res.tecnicos}</td>
					<td>${res.externos}</td>
				</tr>

			</c:forEach>
		</tbody>
	</table>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>