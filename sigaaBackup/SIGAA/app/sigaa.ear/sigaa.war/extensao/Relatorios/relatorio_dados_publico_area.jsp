<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
table.tabelaRelatorioBorda thead tr th, table.tabelaRelatorioBorda tbody tr td {
	text-align: right;
}
</style>

<f:view>

	<h2>Relat�rio de P�blico Estimado de A��es de Extens�o Por �reas Tem�ticas</h2>
	<br/>
	
	<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Tipo da A��o:&nbsp;</th>
			<td> 
				<h:outputText value="#{relatorioEquipeExtensao.labelTipoAtividade}" /> 
			</td>
		</tr>
		<tr>
			<th>Situa��o da A��o:&nbsp;</th>
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
			<th>Per�odo:&nbsp;</th>
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
				<th style="text-align: left">�rea Tem�tica</th>
				<th>N� de A��es</th>
				<th>P�blico Estimado</th>
				<th>P�blico Atingido</th>
				<th>N� de Docentes</th>
				<th>N� de Bolsistas</th>
				<th>N� de n�o-Bolsistas</th>
				<th>N� de Alunos-P�s</th>
				<th>N� de T�cnicos</th>
				<th>N� de Externos</th>
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