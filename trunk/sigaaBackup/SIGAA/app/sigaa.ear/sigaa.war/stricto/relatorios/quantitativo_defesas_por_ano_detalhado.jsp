<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>

<h2> Quantitativo Detalhado de Defesas no Ano</h2>
<f:view>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano:</th>
				<td>${relatoriosStricto.anoInicio}</td>
			</tr>
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda">
		<thead>
			<tr>
				<th style="text-align: left;">Curso</th>
				<th style="text-align: left;">Nível</th>
				<th style="text-align: right;">Defesas</th>
			</tr>
		</thead>
		<tbody>
		<c:set var="mestrado" value="<%=NivelEnsino.MESTRADO %>" />
		<c:set var="doutorado" value="<%=NivelEnsino.DOUTORADO %>" />
		<c:forEach var="resultado" items="#{relatoriosStricto.listaDiscente}" varStatus="status">
			<tr>
				<td>
		    		<h:outputText value="#{resultado.curso}"/> 
	    		</td>
			    <td style="text-align: left;"><h:outputText value="#{resultado.nivel}" converter="convertNivelEnsino"/></td>
			    <td style="text-align: right;" class="quant"><h:outputText value="#{resultado.total}"/></td>
			    	<c:if test="${resultado.nivel == mestrado}">
			    		<c:set var="totalMestrado" value="#{totalMestrado + resultado.total}"/>
			    	</c:if>
			    	<c:if test="${resultado.nivel == doutorado }">
			    		<c:set var="totalDoutorado" value="#{totalDoutorado + resultado.total}"/>
			    	</c:if>
			    <c:set var="total" value="#{total + resultado.total}"/>
			    <c:set var="subTotal" value="#{subTotal + resultado.total}"/>
			</tr>
		</c:forEach>
		</tbody>		
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: right;">Total Mestrado:</td>
				<td style="text-align: right;" class="quant"><h:outputText value="#{totalMestrado}" /></td>
			</tr>
			<tr>
				<td colspan="2" style="text-align: right;">Total Doutorado:</td>
				<td style="text-align: right;" class="quant"><h:outputText value="#{totalDoutorado}" /></td>
			</tr>
			<tr>
				<td colspan="2" style="text-align: right;">Total Geral:</td>
				<td style="text-align: right;" class="quant"><h:outputText value="#{total}" /></td>
			</tr>
		</tfoot>
	</table>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
