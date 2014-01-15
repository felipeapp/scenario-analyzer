
<%-- Nome do relat�rio --%>
<h2><h:outputText value="#{ _abstractRelatorioBiblioteca.titulo }"/></h2>

<%-- P�gina para ser inclu�da em todos os relat�rios da biblioteca, mostra os filtros utilizados pelo usu�rio.--%>

<div id="parametrosRelatorio" style="margin-bottom:10px;">
	<table style="width: 100%">		 	
 		<c:forEach var="param" items="#{_abstractRelatorioBiblioteca.parametrosEscolhidos}" >
			<tr>
				<th style="width: 1%; white-space: nowrap; padding-right: 10px;">  <c:out value="${fn:substring(param.key, 4, fn:length(param.key))}"/> : </th>
				<td> <h:outputText escape="false" value="#{param.value}" /> </td>
			</tr>
		</c:forEach>
	</table>
</div>

<%-- Os bot�es para fazer a pagina��o, caso o relat�rio utilize  --%>

<c:if test="${_abstractRelatorioBiblioteca.utilizandoPaginacao}">
	
	<h:form id="formPaginacaoCabecalho">
		<div class="naoImprimir" style="text-align:center;margin-top:10px;">
			<table style="border-bottom: solid 1px; margin-bottom: 5px; width: 100%;">
				<tr>
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual > 1}">
						<td style="vertical-align: middle; text-align: left; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarPrimeiraPaginaCabecalho" >
								Primeira P�gina
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{1}"/>
							</h:commandLink>
						</td>
						<td style="vertical-align: middle; text-align: left; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarPaginaAnteriorCabecalho" >
								<< P�gina Anterior
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{_abstractRelatorioBiblioteca.paginaAtual - 1}"/>
							</h:commandLink>
						</td>
					</c:if>
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual == 1}">
						<td style="width: 50%;" colspan="2"></td>
					</c:if>
					
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual >= _abstractRelatorioBiblioteca.numeroTotalDePaginas}">
						<td style="width: 50%;" colspan="2"></td>
					</c:if>
					
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual < _abstractRelatorioBiblioteca.numeroTotalDePaginas}">
						<td style="vertical-align: middle; text-align: right; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarProximaPaginaCabecalho" >
								Pr�xima P�gina >>
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{_abstractRelatorioBiblioteca.paginaAtual + 1}"/>
							</h:commandLink>
						</td>
						<td style="vertical-align: middle; text-align: right; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarUltimaPaginaCabecalho" >
								�ltima P�gina
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{_abstractRelatorioBiblioteca.numeroTotalDePaginas}"/>
							</h:commandLink>
						</td>
					</c:if>
				</tr>
			</table>
		</div>
	</h:form>

</c:if>
	