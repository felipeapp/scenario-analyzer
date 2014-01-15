<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Distribuir Projeto de Pesquisa
</h2>

<c:if test="${empty areasConhecimento}">
	<div align="center" style="color: #F00;">	
		Não há Projetos para serem distribuídos
	</div>
</c:if>

<c:if test="${not empty areasConhecimento}">
	<html:form action="/pesquisa/distribuirProjetoPesquisa" method="post">
	<table class="listagem">
		<caption>Distribuição de Projetos para Avaliação</caption>
		<thead>
			<tr> 
	        	<th>Grande Área de Conhecimento</th>
	        	<th>Nº de Projetos</th>
	        	<th>Consultor</th>	        	
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty areasConhecimento}">
		
		<input type="hidden" name="totalAreas" value="${ fn:length(areasConhecimento) }" />
		
	    <c:forEach items="${areasConhecimento}" var="area" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td align="left">
					<input type="hidden" name="idGrandeArea${status.count}" value="${ area.id }" /> 	
					${area.nome} 
				</td>
				<td align="center"> ${area.qtdProjetos} </td>
				<td align="center"> 
					<c:if test="${not empty consultoresInternos}">
						<select name="consultor_${area.id}" style="width: 380px;">
	        				<option value="0">Escolha uma opção</option>
	        					<c:forEach items="${consultoresInternos}" var="consultor">
	        						<option value="${consultor.id}">
	        							<c:out value="${consultor.nome}"/>
	        						</option>
	        					</c:forEach>
	        			</select>
	        		</c:if>
				 </td>								 
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
		<tfoot>					
		<tr>
			<td colspan="5" align="center">
				<html:button dispatch="manualmente" value="Efetuar Distribuição"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
	</html:form>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>