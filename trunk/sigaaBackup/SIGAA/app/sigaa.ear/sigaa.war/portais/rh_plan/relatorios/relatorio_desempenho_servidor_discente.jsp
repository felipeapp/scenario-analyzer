<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
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
.tabelaRelatorioBorda tbody tr td{ text-transform:uppercase;}
</style>
<style media="print">
.icone{	display: none;}
</style>
<f:view>
	<h:outputText value="#{relatorioDesempenhoServidorDiscente.create}"></h:outputText>
	<h2>Relatório de Acompanhamento Acadêmico de Servidores que são Alunos de Pós-Graduação</h2>

	<c:set value="0" var="totalRegistros"/>
	<c:set value="0" var="totalPrograma"/>
	<h:form id="relatorioDesempenhoSD">	

		<c:if test="${not empty relatorioDesempenhoServidorDiscente.lista}">
			<c:forEach items="#{relatorioDesempenhoServidorDiscente.lista}" var="linha" varStatus="indice">
	
					<c:if test="${ultimoPrograma!=linha[0]}">

						<c:if test="${!indice.first}">
								
								</tbody>
								<tfoot>
									<tr>
										<td colspan="6" class="alinharDireita">Total de Discentes Por Programa:</td>
										<td  class="alinharDireita">${totalPrograma}</td>
									</tr>
								</tfoot>
							
							</table>
						
							</br>
							<c:set var="totalPrograma" value="0"/>
						</c:if>
						
						<table class="tabelaRelatorioBorda" align="center" width="100%">
							
							<thead>
								<tr class="destaque">
									<th colspan="7"><h:outputText value="#{linha[0]}"/></th>
								</tr>
								<tr>
									<th class="alinharDireita" width="7%">Siape</th>
									<th class="alinharEsquerda" width="30%">Nome</th>
									<th class="alinharEsquerda" width="15%">Categoria</th>
									<th class="alinharEsquerda">Lotação</th>
									<th width="10%">Curso</th>
									<th width="2%">Tipo</th>
									<th width="7%" class="alinharDireita">CR</th>
								</tr>
							</thead>
							<tbody>
					</c:if>
				
					<tr>
						<td class="alinharDireita"><h:outputText value="#{linha[3]}"/></td>
						<td class="alinharEsquerda"><h:outputText value="#{linha[4]}"/></td>
						<td class="alinharEsquerda"><h:outputText value="#{linha[5]}" /></td>
						<td class="alinharEsquerda"><h:outputText value="#{linha[6]}"/></td>
						<td class="alinharEsquerda">
						<h:outputText value="MESTRADO" rendered="#{linha[2]=='E'}" />
						<h:outputText value="DOUTORADO" rendered="#{linha[2]=='D'}" />
						</td> 
						<td class="alinharCentro">
							<h:outputText value="#{linha[9]}"/>
						</td> 
						<td class="alinharDireita">
							<fmt:formatNumber minFractionDigits="2" value="${linha[7]}"></fmt:formatNumber>
							&nbsp;
							<h:commandLink styleClass="icone"
								action="#{relatorioDesempenhoServidorDiscente.verHistorico}" 
								target="_blank" title="Visualizar Histórico" id="verHistorico">
								<f:param name="idDiscente" value="#{linha[8]}"/>
								<h:graphicImage value="/img/report.png"/>
							</h:commandLink>
						</td>
					</tr>
					<c:set var ="ultimoPrograma" value="#{linha[0]}"/>
					<c:set var="totalRegistros" value="${totalRegistros + 1}"/>
					<c:set var="totalPrograma" value="${totalPrograma + 1}"/>	
			</c:forEach>
		</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6" class="alinharDireita">Total de Discentes Por Programa:</td>
					<td  class="alinharDireita">${totalPrograma}</td>
				</tr>
			</tfoot>
	</table>
	
	</h:form>
		
	<table class="tabelaRelatorioBorda" width="100%">
		<tfoot>
			<tr>
				<td align="center"> Total de Discentes: <ufrn:format type="valorint" valor="${totalRegistros}" /> </td>
			</tr>
		</tfoot>
	</table>
	<br>
		
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>Legenda</caption>
		<tbody>
			<tr>
				<th width="5%">R</th>
				<td>Regular</td>
			</tr>
			<tr>
				<th>E</th>
				<td>Especial</td>
			</tr>
			
		</tbody>
	</table>
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>