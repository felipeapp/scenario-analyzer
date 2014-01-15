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
}
</style>
<f:view>
	<a4j:keepAlive beanName="relatoriosDigitalMBean" />
	<h:form>

	<c:set var="discComDigi" value="#{relatoriosDigitalMBean.discentesComDigital}" />
		
		<c:if test="${!relatoriosDigitalMBean.relatorioSae}">
			<center><h2>Relatório de Discentes Contemplados Com Digitais</h2></center>
			<br>		
			<div id="parametrosRelatorio">
			<table>
				<tr>
					<th style="font-weight: bold">Curso: </th>
					<td> <h:outputText value="#{relatoriosDigitalMBean.curso.descricao}"/> </td>
				</tr>
			</table>
			</div>
		</c:if>
	
		<c:if test="${relatoriosDigitalMBean.relatorioSae}">
			<center><center><h2>Relatório de Discentes Contemplados Com Digitais</h2></center></center>
			<div id="parametrosRelatorio">
			<table>
				<tr>
					<th><b>Ano-Período:</b> </th>
					<td> <h:outputText value="#{relatoriosDigitalMBean.ano}"/>.<h:outputText value="#{relatoriosDigitalMBean.periodo}"/></td>
				</tr>
			</table>
		</div>
		</c:if>
			
			<br>
			<div class="fonte">		
				<table class="tabelaRelatorioBorda" width="100%">	
					<tr>
						<th class="alinharCentro"> Matricula </th>
						<th> Discente </th>
						<th> CPF </th>
					</tr>
					<c:forEach var="item" items="#{discComDigi}">
						<tr>
							<td align="Center"> ${item.matricula} </td>						
							<td> ${item.nome} </td>										
							<td> ${item.cpf} </td>
						</tr>
					</c:forEach>
				</table>		
			</div>
		
			<c:if test="${empty discComDigi}">
				<p style="text-align: center; margin: 5px 0;">Nenhuma pessoa localizada.</p>
			</c:if>
		
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(relatoriosDigitalMBean.discentesComDigital)} discente(s) localizado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>