<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px; }
</style>
<f:view>
	<h:form>
	<h2>Relatório de Acessos das Catracas do RU</h2>

	<c:if test="${empty relatorioAcessoRu.acessosRuDetalhes}">
	
	<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	
	</c:if>
	
	<c:if test="${not empty relatorioAcessoRu.acessosRuDetalhes}">
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Período:</th>
					<td> 
						<h:outputText value="#{relatorioAcessoRu.buscaDataInicio}" /> 
					</td>
						
					<td> 
						<h:outputText value="  a  " />
						<h:outputText value="#{relatorioAcessoRu.buscaDataFim}" /> 
					</td>
				</tr>
				
				
			</table>
		</div>
		<br/>
		<c:set var="totalDia" value="0" />
		<c:set var="dia" />
		<c:set var="refeicao" />
   		
		<table  cellspacing="1" width="100%" style="font-size: 11px;">
			<c:forEach var="acesso" items="#{relatorioAcessoRu.acessosRuDetalhes}" varStatus="indice">
					<c:set var="diaAtual" value="${acesso.data_acesso_ru}"/>
					<c:set var="refeicaoAtual" value="${acesso.turno}"/>
					
					<c:if  test="${dia != diaAtual}">
						<tr>
							<td colspan="5" align="center" style="font-size: 1.1em; font-weight: bold; border-top:2px black solid;">
								<ufrn:format type="data" valor="${acesso.data_acesso_ru}"/>
							</td>
						</tr>
					</c:if>
					<c:if test="${refeicao != refeicaoAtual}">
						<tr class="header">
							<td colspan="5" style="background-color: #c0c0c0; border: 1px solid #555; text-align: center;">
								<c:choose>
									<c:when test="${acesso.turno == 'C'}"> <b>CAFÉ</b> </c:when>
								</c:choose>
								<c:choose>
									<c:when test="${acesso.turno == 'A'}"> <b>ALMOÇO</b> </c:when>
								</c:choose>
								<c:choose>
									<c:when test="${acesso.turno == 'J'}"> <b>JANTAR</b> </c:when>
								</c:choose>
							</td>
						</tr>
					</c:if>
					
					<c:if test="${refeicao != refeicaoAtual}">
						<tr class="header">
							<td align="center">Matrícula</td>
							<td align="left">Discente</td>
							<td align="left"> Curso</td>
							<td align="left">Tipo de Bolsa</td>
							<td align="center"> Data e Hora </td>
						</tr>
					</c:if>
					<c:set var="refeicao" value="${refeicaoAtual}"/>
					<c:set var="dia" value="${diaAtual}"/>
					<tr class="componentes">
						<td align="center">${acesso.matricula}</td>
						<td align="left">${acesso.nome_discente}</td>
						<td align="left">${acesso.nome_curso}</td>
						<td align="left">${acesso.denominacao}</td>
						<td align="center"><ufrn:format type="datahora" valor="${acesso.data_hora}"/></td>
					</tr>
					<c:set var="proximo" value="${relatorioAcessoRu.acessosRuDetalhes[indice.index+1].turno}" />
					<c:if test="${refeicaoAtual != proximo}">
					<tr class="espaco">
						<td colspan="5">&nbsp;</td>
					</tr>
					</c:if>

				</c:forEach>
			</table>

		<table align="center">
			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center; font-weight: bold;">
						Total de acessos ao Restaurante Universitário pelas catracas: ${fn:length(relatorioAcessoRu.acessosRuDetalhes)}
					</td>
				</tr>
			</tfoot>
		</table>

		</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>