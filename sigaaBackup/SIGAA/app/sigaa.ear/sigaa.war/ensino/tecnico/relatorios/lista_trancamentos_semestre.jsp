<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	
	<table class="tabelaRelatorioBorda"; cellspacing="1" width="50%" style="font-size: 10px;" align="center">
		<caption>Trancamentos por Semestre</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Período</th>
					<th style="text-align: right;">Número de Trancamentos</th>
				</tr>
			</thead>
			<c:forEach items="${relatoriosTecnico.lista}" var="linha">
				<tr>
					<td style="text-align: center;">
						<b>${linha.ano}.${linha.periodo}</b>
					</td>
					<td style="text-align: right;">${linha.total}</td>
				</tr>
			</c:forEach>
			<tr>
	   			<td style="text-align: right;"><b>Total de Registros:</b></td>
	   			<td><b><h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></td>
   			</tr>
   	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>