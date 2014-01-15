<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>



	<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto;">
	
		<thead>
			<tr>
				<th>Tipo de Empréstimo</th>
				<th style="text-align: right;">Quantidade</th>
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
					<c:if test="${ idFiltroBiblioteca != -1 }">
						<tr>
							<td style="width: 70%; font-weight: bold;">Total</td>
							<td style="text-align: right; width: 30%; font-weight: bold;"> ${totalPorBibliotecaRelatorio} </td>
						</tr>
						<c:set var="totalPorBibliotecaRelatorio" value="0" scope="request" />	
					</c:if>
					
					
					<c:set var="idFiltroBiblioteca" value="${resul[3]}" scope="request" />
					<tr>
						<th colspan="2" style="text-align: center; background-color: #828282; color: white;">${resul[4]}</th>
					</tr>
					
				</c:if>
			
				
				<c:if test="${ idFiltroTurno != resul[5] && _abstractRelatorioBiblioteca.dadoBooleano}" > <%-- Só exibido se o usuário escolheu mostrar por turno --%>
					
					<c:set var="idFiltroTurno" value="${resul[5]}"  scope="request" />
					<tr>
						<th colspan="2" style="text-align: center; background-color: #C0C0C0;">
							<c:choose>
								<c:when test="${resul[5] == _abstractRelatorioBiblioteca.turnoManha}">MANHÃ</c:when>
								<c:when test="${resul[5] == _abstractRelatorioBiblioteca.turnoTarde}">TARDE</c:when>
								<c:when test="${resul[5] == _abstractRelatorioBiblioteca.turnoNoite}">NOITE</c:when>
							</c:choose>
						</th>
					</tr>
				</c:if>
				
				
				<tr>
					<td style="width: 70%;"><h:outputText value="#{resul[2]}" /></td>
					<td style="text-align: right; width: 30%;"><h:outputText value="#{resul[0]}" /></td>
					
					<%-- Realiza a contagem por biblioteca --%>
					<c:set var="totalPorBibliotecaRelatorio" value="${totalPorBibliotecaRelatorio + resul[0]}" scope="request" />	
					
				</tr>
			</c:forEach>
			
			
			<%--   Imprime a contagem acumulada da última biblioteca --%>
			<tr>
				<td style="width: 70%; font-weight: bold;">Total</td>
				<td style="text-align: right; width: 30%; font-weight: bold;"> ${totalPorBibliotecaRelatorio} </td>
			</tr>
			
		</tbody>
		
	</table>
		
		
	
	
	<c:set var="totalGeralRelatorio" value="0" scope="request" />	
		
		
	
	
	
	
	<%--        S E M      S E R       A G R U P A D O        P O R       T U R N O             --%>
	
	<c:if test="${ ! _abstractRelatorioBiblioteca.dadoBooleano}" >
		
		<%-- Totalização por tipo de empréstimos  --%>
		
		<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto; margin-top: 20px;">
			
			<tbody>
			
				<tr> 
					<th colspan="2" style="text-align: center; background-color: #DEDFE3; font-weight: bold; border: 2px black solid;">
						Totais por Tipo de Empréstimo
					</th> 
				</tr>
				
				<c:forEach var="mapa" items="#{_abstractRelatorioBiblioteca.totalPorTipoEmpretimo }" >
					<tr>
						<td style="width: 70%;">${mapa.key}</td>
						<td style="width: 30%; text-align: right;">${mapa.value}</td>
						
						<c:set var="totalGeralRelatorio" value="${totalGeralRelatorio + mapa.value}" scope="request" />	
						
					</tr>
				</c:forEach>
			
			</tbody>
			
		</table>	
	</c:if>	
		
		
	
	
	
	<%--         A G R U P A D O        P O R       T U R N O             --%>
	
	<c:if test="${ _abstractRelatorioBiblioteca.dadoBooleano}" >
	
		<%-- Totalização por tipo de empréstimos  --%>
	
		<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto; margin-top: 30px;">
			
			<tbody>
			
				<tr> 
					<th colspan="2" style="text-align: center; background-color: #DEDFE3; font-weight: bold; border: 2px black solid;">
						Totais por Tipo de Empréstimo
					</th> 
				</tr>
				
				<c:forEach var="mapa" items="#{_abstractRelatorioBiblioteca.totalizacaoPorTipoEmprestimo }" >
					<tr>
					<td colspan="2" style="background-color: #828282; color: white;">${mapa.key}</td>
					
					<c:forEach var="mapaInterno" items="#{_abstractRelatorioBiblioteca.totalizacaoPorTipoEmprestimo[mapa.key]}" >
						<tr>
							<td style="text-align: left; width: 70%;">${mapaInterno.key}</td>
							<td style="text-align: right; width: 30%;">${mapaInterno.value}</td>
							<c:set var="totalGeralRelatorio" value="${totalGeralRelatorio + mapaInterno.value}" scope="request" />	
						</tr>
					</c:forEach>
					
					</tr>
				</c:forEach>
			
			</tbody>
			
		</table>
	
	
	
	
		<%-- Totalização por turno  --%>
	
		<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto; margin-top: 30px;">
			
			
			<tbody>
		
			
				<tr> 
					<th colspan="2" style="text-align: center; background-color: #DEDFE3; font-weight: bold; border: 2px black solid; margin-top: 20px;">
						Totais por Turno
					</th> 
				</tr>
				
				<c:forEach var="mapa" items="#{_abstractRelatorioBiblioteca.totalizacaoPorTurno }" >
					<tr>
					<td colspan="2" style="background-color: #828282; color: white;">${mapa.key}</td>
					
					<c:forEach var="mapaInterno" items="#{_abstractRelatorioBiblioteca.totalizacaoPorTurno[mapa.key]}" >
						<tr>
							<td style="text-align: left; width: 70%;">${mapaInterno.key}</td>
							<td style="text-align: right; width: 30%;">${mapaInterno.value}</td>
						</tr>
					</c:forEach>
					
					</tr>
				</c:forEach>
				
			</tbody>
		
			
		</table>
	</c:if>
	
	
	
	<%--         T O T A L I Z A C A O               G E R A L             --%>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto; margin-top: 20px;">
		<tr>
			<td style="border: 2px black solid; background-color: #DEDFE3; width: 70%; font-weight: bold;">TOTAL GERAL</td>
			<td style="border: 2px black solid; background-color: #DEDFE3; text-align: right; width: 30%; font-weight: bold;"> ${totalGeralRelatorio}  </td>
		</tr>
	</table>
	
	
	
	
	
	<c:if test="${ _abstractRelatorioBiblioteca.dadoBooleano}" >
		<div style="margin-top:20px;">
			<hr />
			<p>
				<strong>Observação:</strong> A quantidade mostrada inclui apenas as renovações realizadas presencialmente na biblioteca.
			</p>
		</div>
	</c:if>
	
</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>