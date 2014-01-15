<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />

	<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto;">
	
		<thead>
			<tr>
				<th style="width: 10%">Tipo de Empréstimo</th>
				<th style="width: 10%">Código de Barras</th>
				<th style="width: 30%">Operador</th>
				<th style="width: 30%">Usuário</th>
				<th style="width: 10%">Data</th>
			</tr>
		</thead>
	
		<tbody>
		
		
			<c:set var="idFiltroBiblioteca" value="-1" scope="request" />
			<c:set var="idFiltroTurno" value="-1" scope="request" />
		
		
			<c:set var="totalPorBibliotecaRelatorio" value="0" scope="request" />	
		
		
		
		
			<c:forEach var="resul" items="#{ _abstractRelatorioBiblioteca.resultado }" >
				
				
				<%-- Se mudou a biblioteca  --%>
				<c:if test="${ idFiltroBiblioteca != resul[3]}">
					
					
					<%--   Imprime a contagem acumulada, ou seja da biblioteca anterior --%>
					<%-- <c:if test="${ idFiltroBiblioteca != -1 }">
						<tr>
							<td style="width: 70%; font-weight: bold;">Total</td>
							<td style="text-align: right; width: 30%; font-weight: bold;"> ${totalPorBibliotecaRelatorio} </td>
						</tr>
						<c:set var="totalPorBibliotecaRelatorio" value="0" scope="request" />
					</c:if> --%>
					
					
					<c:set var="idFiltroBiblioteca" value="${resul[3]}" scope="request" />
					<tr>
						<th colspan="5" style="text-align: center; background-color: #828282; color: white;">${resul[4]}</th>
					</tr>
					
				</c:if>
			
				
				<c:if test="${ idFiltroTurno != resul[8] && _abstractRelatorioBiblioteca.dadoBooleano}" > <%-- Só exibido se o usuário escolheu mostrar por turno --%>
					
					<c:set var="idFiltroTurno" value="${resul[8]}"  scope="request" />
					<tr>
						<th colspan="5" style="text-align: center; background-color: #C0C0C0;">
							<c:choose>
								<c:when test="${resul[8] == _abstractRelatorioBiblioteca.turnoManha}">MANHÃ</c:when>
								<c:when test="${resul[8] == _abstractRelatorioBiblioteca.turnoTarde}">TARDE</c:when>
								<c:when test="${resul[8] == _abstractRelatorioBiblioteca.turnoNoite}">NOITE</c:when>
							</c:choose>
						</th>
					</tr>
				</c:if>
				
				
				<tr>
					<td style="width: 10%;"><h:outputText value="#{resul[2]}" /></td>
					<td style="width: 10%;"><h:outputText value="#{resul[5]}" /></td>
					<td style="width: 25%;"><h:outputText value="#{resul[6]}" /></td>
					<td style="width: 25%;"><h:outputText value="#{resul[7]}" /></td>
					<%-- <td style="width: 10%;"><h:outputText value="#{resul[0]}" /></td> --%>
					<td style="width: 20%;"><ufrn:format type="dataHora" valor="${resul[0]}" /></td>
					
					<%-- Realiza a contagem por biblioteca --%>
					<%-- <c:set var="totalPorBibliotecaRelatorio" value="${totalPorBibliotecaRelatorio + resul[0]}" scope="request" /> --%>	
					
				</tr>
			</c:forEach>
			
			
			<%--   Imprime a contagem acumulada da última biblioteca --%>
			<%-- <tr>
				<td style="width: 70%; font-weight: bold;">Total</td>
				<td style="text-align: right; width: 30%; font-weight: bold;"> ${totalPorBibliotecaRelatorio} </td>
			</tr> --%>
			
		</tbody>
		
	</table>
	
	<c:if test="${ _abstractRelatorioBiblioteca.dadoBooleano}" >
		<div style="margin-top:20px;">
			<hr />
			<p>
				<strong>Observação:</strong> A listagem mostrada inclui apenas as renovações realizadas presencialmente na biblioteca.
			</p>
		</div>
	</c:if>
	
</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>