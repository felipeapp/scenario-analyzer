<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="width:100%; margin-left:auto; margin-right:auto;">
	
		<thead>
			<tr>
				<th>
					<c:if test="${_abstractRelatorioBiblioteca.turno == _abstractRelatorioBiblioteca.turnoTodos}">
						Turno
					</c:if>
					<c:if test="${_abstractRelatorioBiblioteca.turno != _abstractRelatorioBiblioteca.turnoTodos}">
						Biblioteca
					</c:if>
				</th>
				<th style="text-align: right;">Quantidade</th>
			</tr>
		</thead>
	
		<tbody>
			<c:set var="idFiltroBiblioteca" value="-1" />
			
			<c:forEach var="biblioteca" items="#{ _abstractRelatorioBiblioteca.resultados }" >
				
				<c:if test="${_abstractRelatorioBiblioteca.turno == _abstractRelatorioBiblioteca.turnoTodos}">
					<tr>
						<th colspan="2">${biblioteca.key}</th>
					</tr>
					
					<c:forEach var="turno" items="#{ biblioteca.value }" >
						<tr>
							<td>
								<c:choose>
									<c:when test="${turno.key == _abstractRelatorioBiblioteca.turnoManha}">MANHÃ</c:when>
									<c:when test="${turno.key == _abstractRelatorioBiblioteca.turnoTarde}">TARDE</c:when>
									<c:when test="${turno.key == _abstractRelatorioBiblioteca.turnoNoite}">NOITE</c:when>
									<c:otherwise>ERRO</c:otherwise>
								</c:choose>
							</td>
							<td style="text-align: right;"><h:outputText value="#{turno.value}" /></td>
						</tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${_abstractRelatorioBiblioteca.turno != _abstractRelatorioBiblioteca.turnoTodos}">
					<tr>
						<th>${biblioteca.key}</th>
				
						<td style="text-align: right;"><h:outputText value="#{biblioteca.value[_abstractRelatorioBiblioteca.turno]}" /></td>
					</tr>
				</c:if>
				
			</c:forEach>
		</tbody>
		
		<tfoot>
			<tr> 
				<th colspan="2" style="text-align: center; background-color: #DEDFE3; font-weight: bold; border: 2px black solid;">
					TOTAIS 
				</th> 
			</tr>
			
			<c:if test="${_abstractRelatorioBiblioteca.turno == _abstractRelatorioBiblioteca.turnoTodos}">
				<tr>
					<td>MANHÃ</td>
					<td style="text-align: right;">${_abstractRelatorioBiblioteca.totalManha}</td>
				</tr>
				<tr>
					<td>TARDE</td>
					<td style="text-align: right;">${_abstractRelatorioBiblioteca.totalTarde}</td>
				</tr>
				<tr>
					<td>NOITE</td>
					<td style="text-align: right;">${_abstractRelatorioBiblioteca.totalNoite}</td>
				</tr>
			</c:if>
			
			<tr>
				<td style="border: 2px black solid;">TOTAL GERAL *</td>
				<td style="border: 2px black solid; text-align: right;">${_abstractRelatorioBiblioteca.total}</td>
			</tr>
		</tfoot>
		
	</table>
	
	<div style="margin-top:20px;">
		<hr />
		<h4>Observação:</h4>
		<p>* A quantidade mostrada não inclui as renovações realizadas pelo próprio usuário, somente as renovações 
			feitas presencialmente na biblioteca.</p>
	</div>
	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>