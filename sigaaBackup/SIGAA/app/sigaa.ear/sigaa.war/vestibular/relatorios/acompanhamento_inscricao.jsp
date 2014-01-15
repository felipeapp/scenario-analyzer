<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>${relatoriosVestibular.nomeRelatorio }</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
	<tr>
		<th>Período de Inscrições:</th>
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

  <c:set var="_semana" />
  <c:forEach items="${estatisticas}" var="item" >
   <c:set var="semanaAtual" value="${item.numeroSemana}"/>
	<c:if test="${_semana != semanaAtual}">
	 	<br />
		<h3 class="tituloTabelaRelatorio">
			${item.numeroSemana}
		</h3>
		<table class="tabelaRelatorioBorda" align="center" width="100%">
			<thead>
				<tr>
					<th rowspan="2" width="35%" style="text-align: left;">Intervalo de Inscrição</th>
					<th colspan="${fn:length(estatisticas)}" style="text-align: center;">Quantidade de Inscritos / Dia</th> 
				</tr>
				<tr>	
					<c:set value="${item.numeroSemana}" var="_semana"/>
					<c:forEach items="${estatisticas}" var="data" >
					 <c:set var="semanaAtual" value="${data.numeroSemana}"/>
					  <c:if test="${_semana == semanaAtual}">
					   	<th style="text-align: right;">
						  <ufrn:format type="dia" valor="${data.data}" />
				        </th>
				        <c:set var="semanaAtual" value="${semanaAtual}" />
				      </c:if>
			        </c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td style="text-align: left;">de 00h00min à 05h59min</td>
					<c:forEach items="${estatisticas}" var="data" >
					<c:set var="semanaAtual" value="${data.numeroSemana}"/>
						<c:if test="${_semana == semanaAtual}">
							<td style="text-align: right;">${data.primeiroQuartoDia}</td>	
						</c:if>
					</c:forEach>
				</tr>
				<tr>
					<td style="text-align: left;">de 06h00min à 11h59min</td>
					<c:forEach items="${estatisticas}" var="data" >
					<c:set var="semanaAtual" value="${data.numeroSemana}"/>
						<c:if test="${_semana == semanaAtual}">
							<td style="text-align: right;">${data.segundoQuartoDia}</td>	
						</c:if>
					</c:forEach>
				</tr>
				<tr>
					<td style="text-align: left;">
						de 12h00min à 17h59min<br/>
						(total acumulado até as 18 horas)
					</td>
					<c:forEach items="${estatisticas}" var="data" >
					<c:set var="semanaAtual" value="${data.numeroSemana}"/>
						<c:if test="${_semana == semanaAtual}">
							<td style="text-align: right;">${data.terceiroQuartoDia} <br/>
							(${data.totalAte18Horas})
							</td>	
						</c:if>
					</c:forEach>
				</tr>
				<tr>
					<td style="text-align: left;">de 18h00min à 23h59min</td>
					<c:forEach items="${estatisticas}" var="data" >
					<c:set var="semanaAtual" value="${data.numeroSemana}"/>
						<c:if test="${_semana == semanaAtual}">
							<td style="text-align: right;">${data.quartoQuartoDia}</td>	
						</c:if>
					</c:forEach>
				</tr>
				<tr>
					<td style="text-align: left;"><b>Total de Candidatos Inscritos:</b></td>
					<c:forEach items="${estatisticas}" var="data" >
					<c:set var="semanaAtual" value="${data.numeroSemana}"/>
						<c:if test="${_semana == semanaAtual}">
							<td style="text-align: right;">${data.totalCandidatos}</td>
						</c:if>
					</c:forEach>
				</tr>
				<tr>
					<td style="text-align: left;"> <b>Total de Inscrições:
					<br/> (Isentos / Pagantes)</b></td>
					<c:forEach items="${estatisticas}" var="data" >
					<c:set var="semanaAtual" value="${data.numeroSemana}"/>
					  <c:if test="${_semana == semanaAtual}">
						<td style="text-align: right;">${data.totalInscricoes}<br/> 
							(${data.totalCandidatosIsentos} / ${ data.totalInscricoes - data.totalCandidatosIsentos })
						</td>
					  </c:if>
					</c:forEach>
				</tr>
	 	  </tbody>
		</table>
		<c:set value="${item.numeroSemana}" var="_semana" />
	</c:if>
  </c:forEach>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
