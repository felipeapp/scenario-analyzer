<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style type="text/css">

	.bibliotecaOrigem{
		font-weight: bold;
		height: 30px;
		background-color: #C0C0C0;
	}
	
	.bibliotecaDestino{
		font-weight: bold;
		height: 30px;
	}
	
</style> 	

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

	<table class="tabelaRelatorioBorda" style="width:100%;">
		
		<caption> Exemplares transferidos entre Bibliotecas </caption>

		<thead>
			<tr>
				<th style="width: 15%; text-align: center;"> Código de Barras </th>
				<th style="width: 5%; text-align: right; "> Número do Sistema</th>
				<th style="width: 40%"> Título</th>
				<th style="width: 5%; text-align: center;""> Data da Movimentação</th>
				<th style="width: 25%"> Usuário Movimentou</th>
				<th style="width: 5%"> Gerou Chamado Patrimonial ?</th>
			</tr>
		</thead>

		<c:set var="id_biblioteca_origem" value="-1" scope="request" />
		<c:set var="id_biblioteca_destino" value="-1" scope="request" />
		
		<c:set var="total_geral" value="0" scope="request" />
		
		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultado}" >
			
			<c:if test="${id_biblioteca_origem != resultado[0]}">
				<c:set var="id_biblioteca_origem" value="${resultado[0]}" scope="request"/>
				<c:set var="id_biblioteca_destino" value="-1" scope="request" />   <%-- Quando mudar o origem muda automaticamente a destino.--%>
				
				<tr>
					<td colspan="6" class="bibliotecaOrigem">Biblioteca Origem: ${resultado[1]}   </td>
				</tr>
			</c:if>
			
			<c:if test="${id_biblioteca_destino != resultado[2]}">
				<c:set var="id_biblioteca_destino" value="${resultado[2]}" scope="request"/>
				<tr>
					<td colspan="6" class="bibliotecaDestino">Biblioteca Destino: ${resultado[3]}  </td>
				</tr>
			</c:if>
			
			<tr>
				<td style="text-align: center;"> ${resultado[4]} <br/>
					<c:if test="${not empty resultado[5] }"> [${resultado[5]}] </c:if> <c:if test="${ empty resultado[5] }"> [sem tombamento] </c:if>
				</td>	
				<td style="text-align: right;"> ${resultado[8]} </td>
				<td> ${resultado[9]} </td>
				<td style="text-align: center;"> <ufrn:format type="dataHora" valor="${resultado[6]}" /> </td>
				<td> ${resultado[7]} </td>
				<td> 
					<c:if test="${resultado[10] == true}">SIM </c:if>
					<c:if test="${resultado[10] == false}">NÃO </c:if>
					<c:if test="${empty resultado[10] }"> --- </c:if> 
				</td>
			</tr>
			
			<c:set var="total_geral" value="${total_geral+1}" scope="request" />
			
		</c:forEach>

		<tbody>
			<tr style="font-weight: bold; height: 30px;">
				<td colspan="5" style="text-align: right; background-color: #EAEAEA;">
				Total de Exemplares Transferidos:	
				</td>
				<td style="text-align: right; background-color: #EAEAEA;">
				${total_geral}
				</td>
			</tr>
			<tr style="font-weight: bold; height: 30px;">
				<td colspan="5" style="text-align: right; background-color: #EAEAEA;">
				Total de Títulos  cujos Exemplares foram Transferidos:	
				</td>
				<td style="text-align: right; background-color: #EAEAEA;">
				${_abstractRelatorioBiblioteca.quantidadeTitulosExemplares}
				</td>
			</tr>
			
		</tbody>

	</table>




	<table class="tabelaRelatorioBorda" style="width:100%; margin-top: 50px;">
		
		<caption> Fascículos transferidos entre Bibliotecas </caption>

		<thead>
			<tr>
				<th style="width: 10%; text-align: center;""> Código de Barras </th>
				<th style="width: 5%; text-align: right;"> Número do Sistema</th>
				<th style="width: 40%"> Título</th>
				<th style="width: 5%; text-align: center;""> Data da Movimentação</th>
				<th style="width: 15%"> Usuário Movimentou</th>
				<th style="width: 5%; text-align: center;""> Data da Autorização</th>
				<th style="width: 15%"> Usuário Autorizou</th>
			</tr>
		</thead>

		<c:set var="id_biblioteca_origem" value="-1" scope="request" />
		<c:set var="id_biblioteca_destino" value="-1" scope="request" />
		
		<c:set var="total_geral" value="0" scope="request" />
		<c:set var="total_por_biblioteca" value="0" scope="request" />
		
		<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultadoFasciculos}" >
			
			<c:if test="${id_biblioteca_origem != resultado[0]}">
				<c:set var="id_biblioteca_origem" value="${resultado[0]}" scope="request"/>
				<tr>
					<td colspan="7" class="bibliotecaOrigem">Biblioteca Origem: ${resultado[1]} <i> ( Assinatura origem: ${resultado[9]} )</i>  </td>
				</tr>
			</c:if>
			
			<c:if test="${id_biblioteca_destino != resultado[2]}">
				<c:set var="id_biblioteca_destino" value="${resultado[2]}" scope="request"/>
				<tr>
					<td colspan="7" class="bibliotecaDestino">Biblioteca Destino: ${resultado[3]} <i>( Assinatura destino: ${resultado[10]} )</i>  </td>
				</tr>
			</c:if>
			
			<tr>
				<td style="text-align: center;"> ${resultado[4]} </td>	
				<td style="text-align: right;"> ${resultado[11]} </td>
				<td> ${resultado[12]} </td>
				<td style="text-align: center;"> <ufrn:format type="dataHora" valor="${resultado[5]}" /> </td>
				<td> ${resultado[6]} </td>
				<td style="text-align: center;"> <ufrn:format type="dataHora" valor="${resultado[7]}" /> </td>
				<td> ${resultado[8]} </td>
			</tr>
			
			<c:set var="total_geral" value="${total_geral+1}" scope="request" />
			
		</c:forEach>

		<tbody>
			<tr style="font-weight: bold; height: 30px;">
				<td colspan="6" style="text-align: right; background-color: #EAEAEA;">
				Total de Fascículos Transferidos:	
				</td>
				<td style="text-align: right; background-color: #EAEAEA;">
				${total_geral}
				</td>
			</tr>
			<tr style="font-weight: bold; height: 30px;">
				<td colspan="6" style="text-align: right; background-color: #EAEAEA;">
				Total de Títulos cujos Fascículos foram Transferidos:	
				</td>
				<td style="text-align: right; background-color: #EAEAEA;">
				${_abstractRelatorioBiblioteca.quantidadeTitulosFasciculos}
				</td>
			</tr>
			
			
			
		</tbody>

	</table>



</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>