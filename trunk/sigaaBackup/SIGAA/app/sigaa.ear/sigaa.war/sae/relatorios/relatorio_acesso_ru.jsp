<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h:form>
	
	<h2>RELAT�RIO DE ACESSO AO R.U.</h2>

	<c:if test="${empty buscaAcessoRUMBean.listagem}">
	
	<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os crit�rios de busca informados.</div>
	
	</c:if>
	
	<c:if test="${not empty buscaAcessoRUMBean.listagem}">
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Tipo da Bolsa:</th>
					<td> 
						<h:outputText value="#{buscaAcessoRUMBean.tipoBolsaAuxilio.denominacao}" /> 
					</td>
				</tr>
				
				<tr>
					<th>Tipo da Libera��o:</th>
					<td> <h:outputText value="#{buscaAcessoRUMBean.tipoLiberacaoAcessoRU.descricao}" /> </td>
				</tr>
				
				<tr>
					<th>Per�odo:</th>
					<td> 
						<h:outputText value="#{buscaAcessoRUMBean.dataInicio}" /> a <h:outputText value="#{buscaAcessoRUMBean.dataFinal}" /> 
					</td>
				</tr>
				
			</table>
		</div>
		<br/>
		
			<table class="tabelaRelatorioBorda" style="width:100%">
				<thead>
					<tr>
						<th style="text-align: center;"> Data/Hora </th>
						
						<c:if test="${ buscaAcessoRUMBean.tipoLiberacaoAcessoRU.discentePorDigital }">	
							<th> Discente </th>
						</c:if>
						
						<c:if test="${ !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.pagante && !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.usuarioEventual }">
							<th style="text-align: center;"> Matr�cula </th>
						</c:if>
						
						<c:if test="${ !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.discentePorDigital }"> 
							<th style="text-align: left;"> Usu�rio </th>
						</c:if>
						
						<c:if test="${ !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.discentePorDigital && !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.pagante }">
							<th> Justificativa </th>
						</c:if>
					</tr>
				</thead>
				<c:forEach var="item" items="#{buscaAcessoRUMBean.listagem}">
					<tbody>
						<tr>
							<td style="text-align: center;"> <fmt:formatDate pattern="dd/MM/yyyy HH:MM" value="${item.dataHora}"/> </td>
							
							<c:if test="${ buscaAcessoRUMBean.tipoLiberacaoAcessoRU.discentePorDigital }">
								<td> ${item.discente.nome} </td>
							</c:if>
							
							<c:if test="${ !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.pagante && !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.usuarioEventual }">
								<td style="text-align: center;"> ${item.discente.matricula} </td>
							</c:if>
							
							<c:if test="${ !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.discentePorDigital }">
							<td style="text-align: left;"> ${item.usuario.login} </td>
							</c:if>
							
							<c:if test="${ !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.discentePorDigital 
											&& !buscaAcessoRUMBean.tipoLiberacaoAcessoRU.pagante }">
								<td>
									${item.outraJustificativa}
								</td>
							</c:if>
						</tr>
					</tbody>
				</c:forEach>
		</table>
		
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(buscaAcessoRUMBean.listagem)} registro(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		
	</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>