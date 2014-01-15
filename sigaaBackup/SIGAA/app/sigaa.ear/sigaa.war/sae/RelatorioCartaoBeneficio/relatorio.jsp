<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h:form>
	
	<h2>Relatório de Discentes que Possuem Cartão Benefício</h2>

	<c:if test="${empty relatorioCartaoBeneficio.cartoes}">
	
	<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	
	</c:if>
	
	<c:if test="${not empty relatorioCartaoBeneficio.cartoes}">
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Matrícula:</th>
					<td> 
						<h:outputText value="#{relatorioCartaoBeneficio.buscaMatricula}" /> 
					</td>
				</tr>
				
				<tr>
					<th>Discente:</th>
					<td> <h:outputText value="#{relatorioCartaoBeneficio.buscaNomeDiscente}" /> </td>
				</tr>
				
				
			</table>
		</div>
		<br/>
		
			<table class="tabelaRelatorioBorda" style="width:100%">
				<thead>
					<tr>
							<th style="text-align: center;"> Matrícula </th>
							<th> Discente </th>
							<th> Curso </th>
							<th nowrap="nowrap"> Código do Cartão </th>
							<th> Status Cartão </th>
							<th style="text-align: center;"> Cartão Bloqueado </th>
						
					</tr>
				</thead>
				<c:forEach var="cartao" items="#{relatorioCartaoBeneficio.cartoes}">
					<tbody>
						<tr>
							<td style="text-align: center;" nowrap="nowrap">
							 <h:outputText value="#{cartao.discente.matricula}" />
							</td>
							<td nowrap="nowrap">
								<h:outputText value="#{cartao.discente.pessoa.nome}" />
							</td>
							<td>
								<h:outputText value="#{cartao.discente.curso.nome}" />
							</td>
							<td style="text-align: right;">
								<h:outputText  value="#{cartao.cartaoBolsaAlimentacao.codigo}" />
							</td>
							<td>
								<h:outputText value="#{cartao.statusCartaoBeneficio.descricao}" />
							</td>
							<td style="text-align: center;">
								<h:outputText rendered="#{cartao.cartaoBolsaAlimentacao.bloqueado}" value="SIM" />
								<h:outputText rendered="#{cartao.cartaoBolsaAlimentacao.bloqueado == false}" value="NÃO" />
							</td>
						</tr>
					</tbody>
				</c:forEach>
		</table>
		
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(relatorioCartaoBeneficio.cartoes)} registro(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		
	</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>