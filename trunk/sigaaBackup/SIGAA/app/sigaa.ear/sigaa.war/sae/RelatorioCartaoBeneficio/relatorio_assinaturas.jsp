<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h:form>
	
	<h2>Relatório para Assinaturas de Discentes com Bolsa Alimentação</h2>

	<c:if test="${empty relatorioCartaoBeneficio.bolsas}">
	
	<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	
	</c:if>
	
	<c:if test="${not empty relatorioCartaoBeneficio.bolsas}">
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Período:</th>
					<td> 
						<h:outputText value="#{relatorioCartaoBeneficio.ano} - #{relatorioCartaoBeneficio.periodo}" /> 
					</td>
				</tr>
				
				
			</table>
		</div>
		<br/>
		
			<table class="tabelaRelatorioBorda" style="width:100%">
				<thead>
					<tr>
							<th style="text-align: center;"> Matrícula </th>
							<th> Discente </th>
							<th> Assinatura </th>
						
					</tr>
				</thead>
				<c:forEach var="cartao" items="#{relatorioCartaoBeneficio.bolsas}">
					<tbody>
						<tr>
							<td style="text-align: center;">
							 <h:outputText value="#{cartao.discente.matricula}" />
							</td>
							<td >
								<h:outputText value="#{cartao.discente.pessoa.nome}" />
							</td>
							<td width="40%">
								<h:outputText value=" " />
							</td>
						</tr>
					</tbody>
				</c:forEach>
		</table>
		
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(relatorioCartaoBeneficio.bolsas)} registro(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		
	</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>