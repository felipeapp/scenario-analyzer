<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	<a4j:keepAlive beanName="relatoriosLato"></a4j:keepAlive>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Curso:</th>
				<td> 
					<h:outputText value="#{relatoriosLato.curso}" /> 
				</td>
			</tr>
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">
	<caption>Lista de Discentes com Mensalidades Pagas</caption>
	<thead>
		<tr>
			<th rowspan="2">Matrícula</th>
			<th rowspan="2">Nome</th>
			<c:forEach items="#{ relatoriosLato.dataVencimentos }" var="vencimento" end="1">
				<c:set var="anoVencimento" value="${ fn:substring(vencimento,6, 10) }" />
			</c:forEach>
			<c:set var="colSpan" value="1" />
			<c:forEach items="#{ relatoriosLato.dataVencimentos }" var="vencimento">
				<c:choose>
					<c:when test="${ anoVencimento != fn:substring(vencimento,6, 10) }">
						<th colspan="${colSpan}" style="text-align: center">${ anoVencimento }</th>
						<c:set var="anoVencimento" value="${ fn:substring(vencimento,6, 10) }" />
						<c:set var="colSpan" value="1" />
					</c:when>
					<c:otherwise>
						<c:set var="colSpan" value="${ colSpan + 1 }" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
			 <th colspan="${colSpan - 1}" style="text-align: center">${ anoVencimento }</th>
			<th rowspan="2" style="text-align: right">Total</th>
		</tr>
		<tr>
			<c:forEach items="#{ relatoriosLato.dataVencimentos }" var="vencimento">
				<th width="5%" style="font-size: 75%; text-align: center">${ fn:substring(vencimento,0, 5) }</th>
			</c:forEach>
		</tr>
	</thead>
	<c:forEach items="${relatoriosLato.lista}" var="linha">
		<c:set var="idDiscente" value="total_${ linha[ \"idDiscente\" ] }"/>
		<tr>
			<td>${linha["matricula"]}</td>
			<td>${linha["nome"]}</td>
			<c:forEach items="#{ relatoriosLato.dataVencimentos }" var="vencimento">
				<td style="text-align: center;"><c:if test="${ linha[vencimento] }"> <h:graphicImage value="/img/check.png"/></c:if></td>
			</c:forEach>
			<td style="text-align: right;">
				<c:forEach items="${relatoriosLato.listaAuxiliar}" var="linhaAuxiliar">
					<ufrn:format type="moeda" valor="${ linhaAuxiliar[ idDiscente] }"/>
				</c:forEach>
			</td>
		</tr>	
	</c:forEach>
	<tr>
		<td colspan="2" style="text-align: right; font-weight: bold;">Total:</td>
		<c:forEach items="${relatoriosLato.listaAuxiliar}" var="linhaAuxiliar">
			<c:set var="totalGeral" value="0.0" />
			<c:forEach items="#{ relatoriosLato.dataVencimentos }" var="vencimento">
				<td style="text-align: right;"><ufrn:format type="moeda" valor="${ linhaAuxiliar[vencimento] }"/></td>
				<c:set var="totalGeral" value="${ totalGeral + linhaAuxiliar[vencimento] }" />
			</c:forEach>
		</c:forEach>
		<td style="text-align: right;"><ufrn:format type="moeda" valor="${ totalGeral }"/></td>
	</tr>	
	</table>
	<ul><b>Legenda:</b>
		<li><h:graphicImage value="/img/check.png"/>: mensalidade referente ao mês foi paga</li>
	</ul>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
