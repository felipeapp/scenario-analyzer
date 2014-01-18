<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	<h:form>
		<c:if test="${(not empty relatoriosIMD.listaPeriodosTurma)}">
			
			<p><h2 align="center">PARTICIPAÇÃO SEMANAL DISCENTE</h2></p>
					
			<table class="tabelaRelatorio" style="width: 100%">
				<caption>
					${relatoriosIMD.turmaEntrada.cursoTecnico.nome} - TURMA: ${relatoriosIMD.turmaEntrada.especializacao.descricao} / ${relatoriosIMD.turmaEntrada.anoReferencia}.${relatoriosIMD.turmaEntrada.periodoReferencia} <br />
					OPÇÃO PÓLO GRUPO: ${relatoriosIMD.turmaEntrada.opcaoPoloGrupo.descricao} <br />
					ALUNO: ${relatoriosIMD.discenteTecnico.nome} <br />
				</caption>

				<thead>				
					<tr>
						<th>Notas</th>
						<c:forEach items="${relatoriosIMD.listaPeriodosTurma}" var="c" varStatus="contador">
							<th style="text-align: right; width: 5%;">S<fmt:formatNumber value="${contador.count}" pattern="00"/>	
				  		</c:forEach>
					</tr>
				</thead>
				
			  	<tbody>
			  		<tr>
			  			<td>Participação Presencial</td>
				  		<c:forEach items="${relatoriosIMD.listaAcompanhamentoSemanal}" var="c">
				  			<c:choose>
								<c:when test="${empty c.participacaoPresencial}">
									<td style="text-align: right; width: 5%;">--</td>
								</c:when>
								<c:otherwise>
									<td style="text-align: right; width: 5%;"><fmt:formatNumber value="${c.participacaoPresencial}" pattern="0.0"/>  </td>
								</c:otherwise>
							</c:choose>
				  		</c:forEach>
			  		</tr>
			  		
			  		<tr>
			  			<td>Participação Virtual</td>
				  		<c:forEach items="${relatoriosIMD.listaAcompanhamentoSemanal}" var="c">
				  			<c:choose>
								<c:when test="${empty c.participacaoVirtual}">
									<td style="text-align: right; width: 5%;">--</td>
								</c:when>
								<c:otherwise>
									<td style="text-align: right; width: 5%;"><fmt:formatNumber value="${c.participacaoVirtual}" pattern="0.0"/>  </td>
								</c:otherwise>
							</c:choose>
				  		</c:forEach>
			  		</tr>
			  	</tbody>
			  	<tfoot>	
			  		<tr>
			  			<td>Participação Total</td>
				  		<c:forEach items="${relatoriosIMD.listaAcompanhamentoSemanal}" var="c">
				  			<c:choose>
								<c:when test="${empty c.participacaoVirtual || empty c.participacaoPresencial}">
									<td style="text-align: right; width: 5%;">--</td>
								</c:when>
								<c:otherwise>
									<td style="text-align: right; width: 5%;"><fmt:formatNumber value="${(c.participacaoPresencial * 3  + c.participacaoVirtual*7 )/10}" pattern="0.0"/>  </td>
								</c:otherwise>
							</c:choose>
				  		</c:forEach>
			  		</tr>
			  	</tbody>
			</table>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>