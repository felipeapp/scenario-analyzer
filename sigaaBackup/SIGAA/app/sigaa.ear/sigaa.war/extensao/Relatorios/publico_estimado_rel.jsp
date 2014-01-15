<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
table.listagem tr.curso td {
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}

</style>

<f:view>

	<h:outputText value="#{relatoriosAtividades.create}"/>
	<h2>Relat�rio de P�blico Estimado x P�blico Atendido de A��es de Extens�o</h2>
	<br/>

	<c:set var="result" value="${relatoriosAtividades.resultadoQuantitativo}"/>

	<c:if test="${empty result}">
	<center><i> Nenhuma a��o de extens�o localizada </i></center>
	</c:if>


	<c:if test="${not empty result}">

		<div id="parametrosRelatorio">
		<table>
			<c:if test="${relatoriosAtividades.checkBuscaPeriodo}">
				<tr>
					<th>Per�odo:</th>
					<td><h:outputText value="#{relatoriosAtividades.dataInicio}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText> a <h:outputText value="#{relatoriosAtividades.dataFim}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText></td>
				</tr>
			</c:if>

			<c:if test="${relatoriosAtividades.checkBuscaSituacaoAtividade}">
				<tr>
					<th>Situa��o da A��o:</th>
					<td>${relatoriosAtividades.buscaNomeSituacao}</td>
				</tr>
			</c:if>
		</table>
		</div>
		<br />
		<h3 class="tituloTabelaRelatorio">
		A��ES LOCALIZADAS <c:if test="${(relatoriosAtividades.checkBuscaSituacaoAtividade)}">COM SITUA��O '${relatoriosAtividades.situacaoAtividade.descricao}'</c:if>
		</h3>
		
		 <table class="tabelaRelatorio">
		      <thead>
		      	<tr>
					<th style="text-align:center">Ano</th>
		        	<th style="text-align:center">M�s Conclus�o</th>
		        	<th>Tipo de A��o</th>
		        	<th>Situa��o</th>
					<th style="text-align: right">N� de A��es</th>
		        	<th style="text-align: right">P�blico Estimado</th>
		        	<th style="text-align: right">P�blico Atendido</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	<c:set value="0" var="total_publico_estimado"/>
		 	<c:set value="0" var="total_publico_atendido"/>
		 	<c:set value="0" var="total_acoes"/>
	       	<c:forEach items="${result}" var="r" varStatus="status">
	       		
	       		<c:set value="${total_acoes + r[4]}" var="total_acoes"/>
	       		<c:set value="${total_publico_estimado + r[5]}" var="total_publico_estimado"/>
	       		<c:set value="${total_publico_atendido + r[6]}" var="total_publico_atendido"/>
	       		
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    <td style="text-align:center"> ${r[1]}</td>
	                    <td style="text-align:center"> ${r[2]}</td>
	                    <td> ${r[0]} </td>
	                    <td> ${r[3]}</td>                    
	                    <td style="text-align: right"><fmt:formatNumber value="${r[4]}"/></td>
	                    <td style="text-align: right"><fmt:formatNumber value="${r[5]}"/></td>
	                    <td style="text-align: right"><fmt:formatNumber value="${r[6]}"/></td>
	              </tr>
	          </c:forEach>
	              <tr class="linhaPar">
	              		<td><b>Total</b> </td>
	                    <td></td>
	                    <td></td>
	                    <td></td>
	                    <td style="text-align: right"><b><fmt:formatNumber value="${total_acoes}"/></b></td>
	                    <td style="text-align: right"><b><fmt:formatNumber value="${total_publico_estimado}"/></b></td>	                 
	                    <td style="text-align: right"><b><fmt:formatNumber value="${total_publico_atendido}"/></b></td>
	              </tr>
		 	</tbody>
		 	
		 </table>
	
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>