<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>

<style type="text/css">

	.tipoCooperacao{
		font-weight: bold; 
		color: white; 
		font-variant: small-caps !important; 
		text-align: center !important;  
		background-color: #828282;
		height: 30px;
	}

</style>


<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<a4j:keepAlive beanName="_abstractRelatorioBiblioteca" />
	
	<table id="tabela" class="tabelaRelatorioBorda" width="100%" style="margin: auto;">

		
		<tbody>
		
		
			<%--       C O O P E R A Ç Ã O      D  E         T Í T U L O S        --%>
		
			<c:set var="_biblioteca_cooperacao" value="" scope="request" />
			<c:set var="_pessoa_cooperacao" value="" scope="request" />
		
		
			<tr> <th colspan="4" class="tipoCooperacao"> Cooperação de Títulos </th> </tr>
			
			<c:forEach var="resultados" items="${_abstractRelatorioBiblioteca.resultadoTitulos}">
				
				<c:if test="${ _biblioteca_cooperacao != resultados[2]}">
					<c:set var="_biblioteca_cooperacao" value="${resultados[2]}" scope="request"/>
					<c:set var="_pessoa_cooperacao" value=" " scope="request"/>
					<tr>
						<td colspan="4" style="background-color: #DEDFE3; font-weight: bold;">${resultados[2]}</td>
					</tr>
				</c:if>
				
				<c:if test="${ _pessoa_cooperacao != resultados[3]}">
					<c:set var="_pessoa_cooperacao" value="${resultados[3]}" scope="request"/>
					<tr>
						<td colspan="4" style="font-weight: bold;">${resultados[3]}</td>
					</tr>
				</c:if>
				
				
				<tr>
					<th style="text-align: right; width: 30%;">Importados : </th>
					<td style="width: 20%;">${resultados[0]}</td>
					
					<th style="text-align: right; width: 30%;">Exportados : </th>
					<td style="width: 20%;">${resultados[1]}</td>
					
				</tr>
				
				
			</c:forEach>
			
			<tr>
				<th style="text-align: right; width: 30%;background-color: #DEDFE3;">Total Importados : </th>
				<td style="width: 20%;background-color: #DEDFE3;"> ${_abstractRelatorioBiblioteca.qtdImportadosTitulos} </td>
				<th style="text-align: right; width: 30%;background-color: #DEDFE3;">Total Exportados : </th>
				<td style="width: 20%;background-color: #DEDFE3;"> ${_abstractRelatorioBiblioteca.qtdExportadosTitulos} </td>
			</tr>
			
			
			
			
			
			
			<%--       C O O P E R A Ç Ã O      D  E         A U T O R I D A D E S       A U T O R E S        --%>
			
			
			<c:set var="_biblioteca_cooperacao" value="" scope="request" />
			<c:set var="_pessoa_cooperacao" value="" scope="request" />
			
			
			<tr> <th colspan="4"  class="tipoCooperacao"> Cooperação de Autoridades de Autores </th> </tr>
			
			<c:forEach var="resultados" items="${_abstractRelatorioBiblioteca.resultadoAutoridadeAutores}">
				
				<c:if test="${ _biblioteca_cooperacao != resultados[2]}">
					<c:set var="_biblioteca_cooperacao" value="${resultados[2]}" scope="request"/>
					<c:set var="_pessoa_cooperacao" value=" " scope="request"/>
					<tr>
						<td colspan="4" style="background-color: #DEDFE3; font-weight: bold;">${resultados[2]}</td>
					</tr>
				</c:if>
				
				<c:if test="${ _pessoa_cooperacao != resultados[3]}">
					<c:set var="_pessoa_cooperacao" value="${resultados[3]}" scope="request"/>
					<tr>
						<td colspan="4" style="font-weight: bold;">${resultados[3]}</td>
					</tr>
				</c:if>
				
				
				<tr>
				
					<th style="text-align: right; width: 30%;">Importadas :</th>
					<td style="width: 20%;">${resultados[0]}</td>
					<th style="text-align: right; width: 30%;">Exportadas : </th>
					<td style="width: 20%;">${resultados[1]}</td>
					
				</tr>
				
			</c:forEach>
			
			<tr>
				<th style="text-align: right; width: 30%;background-color: #DEDFE3;">Total Importados : </th>
				<td style="width: 20%;background-color: #DEDFE3;"> ${_abstractRelatorioBiblioteca.qtdImportadosAutor} </td>
				<th style="text-align: right; width: 30%;background-color: #DEDFE3;">Total Exportados : </th>
				<td style="width: 20%;background-color: #DEDFE3;"> ${_abstractRelatorioBiblioteca.qtdExportadosAutor} </td>
			</tr>
			
			
			
			
			
			
			<%--       C O O P E R A Ç Ã O      D  E         A U T O R I D A D E S       A S S U N T O S        --%>
			
			<c:set var="_biblioteca_cooperacao" value="" scope="request" />
			<c:set var="_pessoa_cooperacao" value="" scope="request" />
			
			<tr> <th colspan="4" class="tipoCooperacao"> Cooperação de Autoridades de Assunto </th> </tr>
			
			<c:forEach var="resultados" items="${_abstractRelatorioBiblioteca.resultadoAutoridadeAssuntos}">
				
				<c:if test="${ _biblioteca_cooperacao != resultados[2]}">
					<c:set var="_biblioteca_cooperacao" value="${resultados[2]}" scope="request"/>
					<c:set var="_pessoa_cooperacao" value=" " scope="request"/>
					<tr>
						<td colspan="4" style="background-color: #DEDFE3; font-weight: bold;">${resultados[2]}</td>
					</tr>
				</c:if>
				
				<c:if test="${ _pessoa_cooperacao != resultados[3]}">
					<c:set var="_pessoa_cooperacao" value="${resultados[3]}" scope="request"/>
					<tr>
						<td colspan="4" style="font-weight: bold;">${resultados[3]}</td>
					</tr>
				</c:if>
				
				
				<tr>
					<th style="text-align: right; width: 30%;">Importadas : </th>
					<td style="width: 20%;">${resultados[0]}</td>
					<th style="text-align: right; width: 30%;">Exportadas : </th>
					<td style="width: 20%;">${resultados[1]}</td>
					
				</tr>
				
				
			</c:forEach>
			
			<tr>
				<th style="text-align: right; width: 30%;background-color: #DEDFE3;">Total Importados : </th>
				<td style="width: 20%;background-color: #DEDFE3;"> ${_abstractRelatorioBiblioteca.qtdImportadosAssunto} </td>
				<th style="text-align: right; width: 30%;background-color: #DEDFE3;">Total Exportados : </th>
				<td style="width: 20%;background-color: #DEDFE3;"> ${_abstractRelatorioBiblioteca.qtdExportadosAssunto} </td>
			</tr>
			
		</tbody>
		
	</table>
	
	<%@include file="/biblioteca/controle_estatistico/rodape_impressao_relatorio_paginacao.jsp"%>
	
</f:view>