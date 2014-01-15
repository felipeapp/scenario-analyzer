<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Listagem dos Horários</h2>
	
	<table class="formulario" width="55%">
	  <caption>Horários Cadastrados</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Hora Início</th>
				<th style="text-align: center;">Hora Fim</th>
				<th style="text-align: left;">Turno</th>
				<th style="text-align: left;">Ordem</th>
			</tr>
		</thead>
	<c:choose>
		<c:when test="${not empty horario.horariosDaUnidade}">	
			   <c:forEach var="linha" items="#{horario.horariosDaUnidade}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td style="text-align: center;">${linha.inicio}</td>
						<td style="text-align: center;">${linha.fim}</td>
						<td style="text-align: left;">${linha.turno}</td>
						<td style="text-align: left;">${linha.ordemFormatado}</td>
					</tr>
			   </c:forEach>
		</c:when>
		<c:otherwise>
				 	<tr>
				 		<td colspan="5" align="center">Nenhum horário cadastrado.</td>
				 	</tr>			
		</c:otherwise>
	</c:choose>
	</table>
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>