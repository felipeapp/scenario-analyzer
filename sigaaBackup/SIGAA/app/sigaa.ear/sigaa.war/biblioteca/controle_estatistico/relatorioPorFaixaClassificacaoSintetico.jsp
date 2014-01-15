<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.tabelaRelatorioBorda tr{
		text-align:right;
	}
</style>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>


	<c:set var="totalTitulos" value="0" scope="request"/>
	
	
	<table class="tabelaRelatorioBorda" style="width: 100%; margin-top: 10px; margin-bottom: 20px;">
		<caption>Quantidade de T�tulos por Classifica��o</caption>
		<tr style="background-color: #C2C2C2;">
			<c:forEach items="${ _abstractRelatorioBiblioteca.resultadoTitulos }" var="linha">
				<th style="width: 8%;text-align: center;">${linha[0]}</th>
			</c:forEach>
			<th style="width: 10;text-align: right;"> Total</th>
		</tr>
		
		<tr style="background-color: #DEDFE3;">
			<c:forEach items="${ _abstractRelatorioBiblioteca.resultadoTitulos }" var="linha">
				<td> ${linha[1]}</td>
				<c:set var="totalTitulos" value="${totalTitulos + linha[1] }" scope="request"/>
			</c:forEach>
			<td style="font-weight: bold;">${totalTitulos}</td>
		</tr>
		
	</table>
	
	

	<c:set var="exibir_exemplar" value="${_abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatExemplares }" scope="request" />
	<c:set var="exibir_fasciculo" value="${_abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatTodos or _abstractRelatorioBiblioteca.ctgMaterial == _abstractRelatorioBiblioteca.ctgMatFasciculos }" scope="request" />

	<table class="tabelaRelatorioBorda" style="margin: auto; width: 100%;">
		
		<tbody>
		
			<c:set var="totalGeralTitulos" value="0" scope="request"/>
			<c:set var="totalGeralExemplares" value="0" scope="request"/>
			<c:set var="totalGeralFasciculos" value="0" scope="request"/>
		
		
		
		 	<%-- disposi��o dos dados quando o usu�rio escolhe apenas 1 agrupamento --%>	
		
		
			<c:if test="${ _abstractRelatorioBiblioteca.agrupamento2.semAgrupamento }">
				
				<tr  style="background-color: #C2C2C2;">
					<th style="text-align:left;width:50%;"> ${_abstractRelatorioBiblioteca.descricaoAgrupamento1}</th>
					<th style="text-align:right;width:16%;">T�tulos</th>
					<c:if test="${exibir_exemplar}">
						<th style="text-align:right;width:16%;">Exemplares</th>
					</c:if>
					<c:if test="${exibir_fasciculo}">
						<th style="text-align:right;width:16%;">Fasc�culos</th>
					</c:if>
				</tr>
				
				<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultado}">
				
					<tr>
						<td style="text-align: left;">${resultado[3]}</td> <%-- agrupamento  1 --%>	
						<td>${resultado[0]}</td> <%-- T�tulos --%>	
						<c:set var="totalGeralTitulos" value="${totalGeralTitulos + resultado[0] }" scope="request"/>
						
						<c:if test="${exibir_exemplar}">
							<td>${resultado[1]}</td> <%-- Exemplares --%>
							<c:set var="totalGeralExemplares" value="${totalGeralExemplares + resultado[1] }" scope="request"/>
						</c:if>	
						<c:if test="${exibir_fasciculo}">
							<td>${resultado[2]}</td> <%-- Fasciculos --%>
							<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos + resultado[2] }" scope="request"/>	
						</c:if>
					</tr>
				</c:forEach>
						
				
			</c:if>
			
		
		
		
		
			<%-- disposi��o dos dados quando o usu�rio escolhe 2 agrupamentos --%>	
		
			<c:if test="${ ! _abstractRelatorioBiblioteca.agrupamento2.semAgrupamento  }">
				
				<c:set var="totalTitulos" value="0" scope="request"/>
				<c:set var="totalExemplares" value="0" scope="request"/>
				<c:set var="totalFasciculos" value="0" scope="request"/>
				
				<c:set var="agrupamento1" value="" scope="request"/>
				
				<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultado}">
					
					
					
					<%-- Exibe a  Totaliza��o no final do agrupamento antes de exibir o cabe�alho do pr�ximo agrupamento  --%>	
					<c:if test="${agrupamento1 != resultado[3] && not empty agrupamento1}">
						<tr style="font-weight: bold;background-color: #DEDFE3;">
							<td style="text-align: left;">Total</td> <%-- agrupamento  2 --%>	
							<td>  ${totalTitulos} </td>   <%-- Totaliza��o de T�tulos por agrupamento 1  --%>	
							
							<c:if test="${exibir_exemplar}">
								<td>  ${totalExemplares} </td> <%-- Totaliza��o de Exemplares por agrupamento 1  --%>
							</c:if>	
							<c:if test="${exibir_fasciculo}">
								<td>  ${totalFasciculos}  </td> <%-- Totaliza��o de Fasciculos por agrupamento 1  --%>
							</c:if>							
						</tr>
					</c:if>
					
					
					
					<%-- Exibe o cabe�alho do pr�ximo agrupamento e zera os totais anteritores e executa a totaliza��o geral--%>	
					
					<c:if test="${ agrupamento1 != resultado[3]}">
						<c:set var="agrupamento1" value="${resultado[3]}" scope="request"/>
						
						<c:set var="totalGeralTitulos" value="${totalGeralTitulos + totalTitulos}" scope="request"/>
						<c:set var="totalGeralExemplares" value="${totalGeralExemplares + totalExemplares}" scope="request"/>
						<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos + totalFasciculos}" scope="request"/>
						
						<c:set var="totalTitulos" value="0" scope="request"/>
						<c:set var="totalExemplares" value="0" scope="request"/>
						<c:set var="totalFasciculos" value="0" scope="request"/>
						
						<tr>
							<td colspan="4" style="font-weight: bold; text-align: center; margin-top: 20px; background-color: #C2C2C2;"> ${_abstractRelatorioBiblioteca.descricaoAgrupamento1} : ${resultado[3]}</td> <%-- agrupamento  1 --%>	
						</tr>
						
						<tr style="font-weight: bold;">
							<td style="text-align: left; width: 50%"> ${_abstractRelatorioBiblioteca.descricaoAgrupamento2} </td>
							<td style="text-align:right;width:16%">T�tulos</td>
							
							<c:if test="${exibir_exemplar}">
								<td style="width:16%">Exemplares</td>
							</c:if>
							
							<c:if test="${exibir_fasciculo}">
								<td style="width:16%">Fasciculos</td>
							</c:if>
						</tr>
					
					</c:if>
					
					
					
					
					<tr>
						<td style="text-align: left;">${resultado[4]}</td> <%-- agrupamento  2 --%>	
						
						<c:set var="totalTitulos" value="${totalTitulos + resultado[0]}" scope="request"/>
						<td>${resultado[0]}</td> <%-- T�tulos --%>	
						
						
						<c:if test="${exibir_exemplar}">
							<c:set var="totalExemplares" value="${totalExemplares + resultado[1]}" scope="request"/>
							<td>${resultado[1]}</td> <%-- Exemplares --%>
						</c:if>	
						<c:if test="${exibir_fasciculo}">
							<c:set var="totalFasciculos" value="${totalFasciculos + resultado[2]}" scope="request"/>
							<td>${resultado[2]}</td> <%-- Fasciculos --%>
						</c:if>							
					</tr>
					
					
				</c:forEach>
				
				
				<%-- Exibe a �ltima Totaliza��o que ficou pendente depois que o intera��o acaba  --%>	
				
				<c:set var="totalGeralTitulos" value="${totalGeralTitulos + totalTitulos}" scope="request"/>
				<c:set var="totalGeralExemplares" value="${totalGeralExemplares + totalExemplares}" scope="request"/>
				<c:set var="totalGeralFasciculos" value="${totalGeralFasciculos + totalFasciculos}" scope="request"/>
							
				<tr style="font-weight: bold;background-color: #DEDFE3;">
					<td style="text-align: left;">Total</td> <%-- agrupamento  2 --%>	
					<td>  ${totalTitulos} </td>   <%-- Totaliza��o de T�tulos por agrupamento 1  --%>	
					
					<c:if test="${exibir_exemplar}">
						<td>  ${totalExemplares} </td> <%-- Totaliza��o de Exemplares por agrupamento 1  --%>
					</c:if>	
					<c:if test="${exibir_fasciculo}">
						<td>  ${totalFasciculos}  </td> <%-- Totaliza��o de Fasciculos por agrupamento 1  --%>
					</c:if>							
				</tr>
				
			</c:if>
			
		</tbody>
		
	</table>
	
	<table class="tabelaRelatorioBorda" style="margin: auto; width: 100%; margin-top: 20px;">
		<tr style="font-weight: bold; background-color: #DEDFE3;" >
			<td style="text-align: left; width:50%;;">Total Geral</td> <%-- agrupamento  2 --%>	
			<td style="width:16%;"> ${totalGeralTitulos}  </td>   <%-- Totaliza��o de T�tulos por agrupamento 1  --%>	
			
			<c:if test="${exibir_exemplar}">
				<td style="width:16%;"> ${totalGeralExemplares} </td> <%-- Totaliza��o de Exemplares por agrupamento 1  --%>
			</c:if>	
			<c:if test="${exibir_fasciculo}">
				<td style="width:16%;"> ${totalGeralFasciculos}  </td> <%-- Totaliza��o de Fasciculos por agrupamento 1  --%>
			</c:if>							
		</tr>
	</table>
	
	<p style="margin-top: 15px; margin-bottom: 15px;">
	&nbsp&nbsp&nbsp  <strong>Observa��o:</strong> A totaliza��o de T�tulos pode estar acima do n�mero real, pois se os materiais ligados a um
	mesmo T�tulo possu�rem as informa��es usadas no agrupamento diferentes, o T�tulo ser� contado mais de uma vez. <br/> 
	&nbsp&nbsp&nbsp&nbsp Por exemplo, se dois ou mais materiais de um mesmo T�tulo estiverem em cole��es diferentes, e o relat�rio for agrupado por 
	cole��o, o mesmo T�tulo ser� contado em todas as cole��es em que seus materiais estiverem.
	</p> 
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>