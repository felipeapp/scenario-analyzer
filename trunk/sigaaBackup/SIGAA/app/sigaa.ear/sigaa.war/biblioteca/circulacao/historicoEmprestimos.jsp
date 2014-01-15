<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.linhaImpar{
		background:#EEEEEE;
	}
	
	.linhaImpar td, .linhaImpar th{
		background:#EEEEEE;
	}
</style>

<f:view>

	<h:form>

		<h2>HISTÓRICO DE EMPRÉSTIMOS DE UM USUÁRIO</h2>
		
		<%-- Exibe as informações do usuário. --%>
		<c:set var="_infoUsuarioCirculacao" value="${emiteHistoricoEmprestimosMBean.infoUsuarioBiblioteca}" scope="request"/>
		<c:set var="_transparente" value="true" scope="request" />
		<c:set var="_mostrarFoto" value="false" scope="request" />	
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
		<br/>
		
		<table id="parametrosRelatorio" style="margin-bottom:10px;"> 
			
			<tr>
				<th width="100">Período:</th>
				<td>
					<c:if test="${emiteHistoricoEmprestimosMBean.dataInicio != null && emiteHistoricoEmprestimosMBean.dataFinal == null}">
						<span>A partir de </span> <h:outputText value="#{emiteHistoricoEmprestimosMBean.dataInicio}" converter="convertData">
													    <f:convertDateTime pattern="dd/MM/yyyy"/>
												   </h:outputText>
					</c:if>
					<c:if test="${emiteHistoricoEmprestimosMBean.dataInicio == null && emiteHistoricoEmprestimosMBean.dataFinal != null}">
						<span>Até </span> <h:outputText value="#{emiteHistoricoEmprestimosMBean.dataFinal}" converter="convertData">
													    <f:convertDateTime pattern="dd/MM/yyyy"/>
												    </h:outputText>
					</c:if>
					<c:if test="${emiteHistoricoEmprestimosMBean.dataInicio != null && emiteHistoricoEmprestimosMBean.dataFinal != null}">
						<h:outputText value="#{emiteHistoricoEmprestimosMBean.dataInicio}" converter="convertData">
							    <f:convertDateTime pattern="dd/MM/yyyy"/>
						   </h:outputText>
					  a  
					 		<h:outputText value="#{emiteHistoricoEmprestimosMBean.dataFinal}" converter="convertData">
							    <f:convertDateTime pattern="dd/MM/yyyy"/>
						   </h:outputText>
					</c:if>
				</td>
			</tr>
			
		</table>
	
	
	
	
	
		<table class="tabelaRelatorioBorda" style="width:100%;">
			
			<thead>
				<tr>
					<th>Tipo do Empréstimo</th>
					<th style="text-align:center;width:120px;">Data de Empréstimo</th>
					<th style="text-align:center;width:120px;">Data de Renovação</th>
					<th style="text-align:center;width:120px;">Prazo para Devolução</th>
					<th style="text-align:center;width:120px;">Data de Devolução</th>
					<th style="text-align:center;">Em Aberto</th>
				</tr>
			</thead>

				<tbody>
					<c:forEach var="emprestimo" varStatus="linha" items="#{emiteHistoricoEmprestimosMBean.emprestimos}">
						
						
							<%-- Informações do Empéstimo --%>		
							<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
								<td>
									<c:out value="${emprestimo.politicaEmprestimo.tipoEmprestimo.descricao}"/>
								</td>
		
								<td style="text-align:center;">  
									<h:outputText value="#{emprestimo.dataEmprestimo}" converter="convertData">
								    		<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
							   		</h:outputText>
								</td>
								<td style="text-align:center;">
									<h:outputText value="#{emprestimo.dataRenovacao}" converter="convertData">
							    		<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
						   			</h:outputText>
								</td>
								<td style="text-align:center;">
									<c:if test="${emprestimo.atrasado == true}">
										<h:outputText value="#{emprestimo.prazo}" converter="convertData" style="color:red;font-weight:bold" >
									   		<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
								   		</h:outputText>
									</c:if>
									<c:if test="${emprestimo.atrasado == false}">
										<h:outputText value="#{emprestimo.prazo}" converter="convertData">
									   		<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
								   		</h:outputText>
									</c:if>
								</td>
								<td style="text-align:center;">
									<c:if test="${emprestimo.finalizadoComAtraso == true}">
										<h:outputText value="#{emprestimo.dataDevolucao}" converter="convertData" style="color:red">
								    		<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
							   			</h:outputText>
									</c:if>
									<c:if test="${emprestimo.finalizadoComAtraso == false}">
										<h:outputText value="#{emprestimo.dataDevolucao}" converter="convertData">
								    		<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
							   			</h:outputText>
									</c:if>
								</td>
								<td style="text-align:center;">
									<c:if test="${emprestimo.dataDevolucao == null}">
										<h:outputText value="SIM" style="font-weight:bold;color:#006600"></h:outputText>
									</c:if>
									<c:if test="${emprestimo.dataDevolucao != null}">
										<h:outputText value="NÃO"></h:outputText>
									</c:if>
								</td>
		
							</tr>
		
							<%-- Informações do material desse empréstimo --%>
		
							<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
								<td colspan="6">
									${emprestimo.material.informacao}
								</td>
							</tr>
							
							<tr><td colspan="6" style="border:none;">&nbsp;</td></tr>
		
					</c:forEach>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						Total de Empréstimos: ${fn:length( emiteHistoricoEmprestimosMBean.emprestimos) }
					</td>
				</tr>
			</tfoot>
		</table>


	</h:form>

</f:view>





<div style="margin-top:20px;">
		<hr  />
		<h4>ATENÇÃO</h4>
		<p>Empréstimos em aberto aparecem com o <i>status</i> na cor <span style="color:#006600">verde.</span></p>
		<p>Empréstimos que estão atrasados aparecem com o prazo na cor <span style="color:red"> vermelha.</span> </p>
		<p>Empréstimos que foram devolvidos com atraso aparecem com a data de devolução na cor <span style="color:red"> vermelha.</span> </p>
		
</div>


<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>