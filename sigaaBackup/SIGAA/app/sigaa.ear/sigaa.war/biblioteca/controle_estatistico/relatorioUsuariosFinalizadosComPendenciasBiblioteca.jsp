<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>


<style type="text/css">

	.linhaTipoUsuario{
		font-weight: bold; 
		background-color: #DEDFE3; 
		text-align: center; 
		font-variant: small-caps;  
		height: 25px;
	}

</style>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="width: 100%;">
		<thead>
			<tr>
				<th style="width: 10%">Material</th>
				<th style="width: 26%">Título</th>
				<th style="width: 10%">Matrícula</th>
				<th style="width: 30%">Nome</th>
				<th style="width: 12%; text-align: center;">Data do Empréstimo</th>
				<th style="width: 12%; text-align: center;">Prazo</th>
			</tr>
		</thead>
		
		<tbody>
		
			<c:set var="biblioteca" scope="request" value="" />
			<c:set var="vinculo" scope="request" value="" /> 
			 
			 <tr>
				<td colspan="8" class="linhaTipoUsuario"> Discentes </td>
			</tr>
			 
			<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultadosDiscente}">
				
				<c:if test="${biblioteca != resultado[0] }">
					<tr>
						<td colspan="8" style="font-weight: bold; background-color: #828282; color: white;"><h:outputText value="#{resultado[0]}" /> </td> <%-- biblioteca --%>
					</tr>
					<c:set var="biblioteca" scope="request" value="${resultado[0]}" />
					<c:set var="vinculo" scope="request" value="" />
				</c:if>
				
				<c:if test="${vinculo != resultado[1] }">
					<tr>
						<td colspan="8" style="font-weight: bold; font-style:italic; background-color: #EEEEEE"><h:outputText value="#{resultado[1]}" /> </td> <%-- vinculo --%>
					</tr>
					<c:set var="vinculo" scope="request" value="${resultado[1]}" /> 
				</c:if>
				
				<tr>
					<td> <h:outputText value="#{resultado[2]}" /></td> <%-- código de barras --%>
					<td> <h:outputText value="#{resultado[3]}" /> - <h:outputText value="#{resultado[4]}" /> </td> <%-- autor, titulo --%>
					<td> <h:outputText value="#{resultado[5]}" /> </td> <%-- Matrícula --%>
					<td> <h:outputText value="#{resultado[6]}" /></td>  <%-- nome --%>
					<td style="text-align: center;"> <ufrn:format type="data" valor="${resultado[7]}" /> </td>  <%-- data emprestimo --%>
					<td style="font-weight: bold; color: red;  text-align: center;"> <ufrn:format type="data" valor="${resultado[8]}" /> </td>  <%-- prazo --%>
				</tr>
			</c:forEach>
			
			<c:set var="biblioteca" scope="request" value="" />
			<c:set var="vinculo" scope="request" value="" /> 
			
			<tr>
				<td colspan="8" class="linhaTipoUsuario"> Servidores </td>
			</tr>
			
			<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultadosServidor}">
				
				<c:if test="${biblioteca != resultado[0] }">
					<tr>
						<td colspan="8" style="font-weight: bold; background-color: #828282; color: white;"><h:outputText value="#{resultado[0]}" /> </td> <%-- biblioteca --%>
					</tr>
					<c:set var="biblioteca" scope="request" value="${resultado[0]}" />
					<c:set var="vinculo" scope="request" value="" />
				</c:if>
				
				<c:if test="${vinculo != resultado[1] }">
					<tr>
						<td colspan="8" style="font-weight: bold; font-style:italic; background-color: #EEEEEE"><h:outputText value="#{resultado[1]}" /> </td> <%-- vinculo --%>
					</tr>
					<c:set var="vinculo" scope="request" value="${resultado[1]}" /> 
				</c:if>
				
				<tr>
					<td> <h:outputText value="#{resultado[2]}" /></td> <%-- código de barras --%>
					<td> <h:outputText value="#{resultado[3]}" /> - <h:outputText value="#{resultado[4]}" /> </td> <%-- autor, titulo --%>
					<td> <h:outputText value="#{resultado[5]}" /> </td> <%-- Matrícula --%>
					<td> <h:outputText value="#{resultado[6]}" /></td>  <%-- nome --%>
					<td style="text-align: center;"> <ufrn:format type="data" valor="${resultado[7]}" /> </td>  <%-- data emprestimo --%>
					<td style="font-weight: bold; color: red;  text-align: center;"> <ufrn:format type="data" valor="${resultado[8]}" /> </td>  <%-- prazo --%>
				</tr>
			</c:forEach>
			
			
			<c:set var="biblioteca" scope="request" value="" />
			<c:set var="vinculo" scope="request" value="" /> 
			
			<tr>
				<td colspan="8" class="linhaTipoUsuario"> Bibliotecas </td>
			</tr>
					
			<c:forEach var="resultado" items="#{_abstractRelatorioBiblioteca.resultadosBiblioteca}">
				
				<c:if test="${biblioteca != resultado[0] }">
					<tr>
						<td colspan="8" style="font-weight: bold; background-color: #828282; color: white;"><h:outputText value="#{resultado[0]}" /> </td> <%-- biblioteca --%>
					</tr>
					<c:set var="biblioteca" scope="request" value="${resultado[0]}" />
					<c:set var="vinculo" scope="request" value="" />
				</c:if>
				
				<c:if test="${vinculo != resultado[1] }">
					<tr>
						<td colspan="8" style="font-weight: bold; font-style:italic; background-color: #EEEEEE"><h:outputText value="#{resultado[1]}" /> </td> <%-- vinculo --%>
					</tr>
					<c:set var="vinculo" scope="request" value="${resultado[1]}" /> 
				</c:if>
				
				<tr>
					<td> <h:outputText value="#{resultado[2]}" /></td> <%-- código de barras --%>
					<td> <h:outputText value="#{resultado[3]}" /> - <h:outputText value="#{resultado[4]}" /> </td> <%-- autor, titulo --%>
					<td> <h:outputText value="#{resultado[5]}" /> </td> <%-- Matrícula --%>
					<td> <h:outputText value="#{resultado[6]}" /></td>  <%-- nome --%>
					<td style="text-align: center;"> <ufrn:format type="data" valor="${resultado[7]}" /> </td>  <%-- data emprestimo --%>
					<td style="font-weight: bold; color: red; text-align: center;"> <ufrn:format type="data" valor="${resultado[8]}" /> </td>  <%-- prazo --%>
				</tr>
			</c:forEach>
			
			
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="8" style="font-variant: small-caps; text-align: center;"> Total de Materiais não Devolvidos: <h:outputText value="#{_abstractRelatorioBiblioteca.totalResultados}" /> </td> <%-- total --%>
			</tr>
		</tfoot>
		
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>