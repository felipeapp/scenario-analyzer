<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
</style>
<f:view>
	
	<h2>Relatório de Bolsistas Contemplados</h2>
	
	<h:form>
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Ano.Período: </th>
					<td><h:outputText value="#{relatoriosSaeMBean.ano}.#{relatoriosSaeMBean.periodo}"/> </td>
				</tr>
			</table>
		</div>
		<br/>
		<c:if test="${not empty relatoriosSaeMBean.listaBolsistasContemplados}">
		
		<h3 class="tituloSubTabelaRelatorio">Bolsistas Contemplados pelo ${ configSistema['siglaSigaa'] }</h3>
		<br/>
		<table class="tabelaRelatorioBorda" width="100%">	
			
			<thead>
				<tr>
					<th width="12%" class="alinharCentro"> Matrícula </th>
					<th> Discente </th>
					<th width="18%"> Tipo de Bolsa </th>
				</tr>	
			</thead>	
						
			<tbody>
			<c:forEach var="item" items="#{relatoriosSaeMBean.listaBolsistasContemplados}">
				<tr>
					<td  class="alinharCentro"> ${item.discente.matricula} </td>
					<td> ${item.discente.nome} </td>										
					<td> ${item.tipoBolsaAuxilio.denominacao} </td>
				</tr>
			</c:forEach>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3" align="center">
						${fn:length(relatoriosSaeMBean.listaBolsistasContemplados)} bolsista(s) contemplados(s)
					</td>
				</tr>
			</tfoot>
			
		</table>		
		<br/><br/>
		
		</c:if>
		
		<c:if test="${not empty relatoriosSaeMBean.listaBolsistasSIPAC}">
			
			<h3 class="tituloSubTabelaRelatorio">Bolsistas Remunerados pelos pelo ${ configSistema['siglaSipac'] }</h3>
			<br/>
			<table class="tabelaRelatorioBorda" width="100%">	
				
				<thead>
					<tr>
						<th width="12%" class="alinharCentro"> Matrícula </th>
						<th> Discente </th>
						<th width="25%" class="alinharCentro"> Período </th>
					</tr>	
				</thead>				
				
				<tbody>
				<c:forEach var="item" items="#{relatoriosSaeMBean.listaBolsistasSIPAC}">
					<tr>
						<td class="alinharCentro"> ${item.discente.matricula} </td>
						<td> ${item.discente.nome} </td>										
						<td class="alinharCentro"> 
							<ufrn:format type="data" valor="${item.dataInicio}"></ufrn:format>
							a  <ufrn:format type="data" valor="${item.dataFim}"></ufrn:format>
						</td>
					</tr>
				</c:forEach>
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="3" align="center">
							${fn:length(relatoriosSaeMBean.listaBolsistasSIPAC)} bolsista(s) remunerado(s)
						</td>
					</tr>
				</tfoot>
				
			</table>		
			<br/>
			
		</c:if>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>