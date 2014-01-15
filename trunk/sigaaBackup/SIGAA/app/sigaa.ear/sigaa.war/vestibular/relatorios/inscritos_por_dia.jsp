<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Estat�sticas dos Candidatos Inscritos por Dia</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
	<tr>
		<th>Per�odo de Inscri��es:</th>
		<td> de
			<c:forEach items="${estatisticas}" var="item" end="0" >
				<ufrn:format type="data" valor="${item.data}" />
			</c:forEach>
			a
			<c:forEach items="${estatisticas}" var="item" begin="${fn:length(estatisticas) - 1}">
				<ufrn:format type="data" valor="${item.data}" />
			</c:forEach>
		</td>
	</tr>
</table>
</div>
<br/>

<table class="tabelaRelatorioBorda" align="center" width="50%">
	<c:forEach items="${relatoriosVestibular.isencaoVestibular}" var="item" >
		<tr>
			<td><b>${item.key}</b></td>
			<td style="text-align: right;">${item.value}</td>
		</tr>
	</c:forEach>	
</table>
<br />
<table class="tabelaRelatorioBorda" align="center" width="50%">
	<c:forEach items="${estatisticas}" var="item" >
		<thead>
		<tr>
			<th colspan="2">
				Data: <ufrn:format type="dia" valor="${item.data}" />/<ufrn:format type="mes_ano_numero" valor="${item.data}" />
			</th>
		</tr>
		</thead>
		<tr>
			<td>Total de inscri��es feitas:</td>
			<td style="text-align: right;">${item.totalInscricoes}</td>
		</tr>
		<tr>	
			<td>Inscri��es feitas at� �s 18 horas:</td>
			<td style="text-align: right;">${item.totalAte18Horas}</td>
		</tr>
		<tr>	
			<td>Total de inscri��es com CPF's distintos:</td>
			<td style="text-align: right;">${item.totalCandidatos}</td>
		</tr>
	</c:forEach>
	
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>