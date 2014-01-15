<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<h:form>
	<div class="secaoComunidade">
	<rich:panel header="#{ enqueteComunidadeMBean.object.pergunta }">
	
		<table class="listagem" width="80%" align="center">
		<caption>Visualizar Enquete</caption>
		
			<thead>
				<tr>
					<th>Resposta</th>
					<td style="text-align: right;">Votos</td>
					<td style="text-align: right;">%</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="resposta" items="${ enqueteComunidadeMBean.object.respostas }">
					<tr>
						<td class="first">${ resposta.resposta }</td>
						<td style="text-align: right;">${ resposta.totalVotos }</td>
						<td style="text-align: right;">${ resposta.porcentagemVotos }%</td>
					</tr>
				</c:forEach>
			</tbody>
	
		</table>
			<div class="voltar">
				<h:commandButton value="Voltar" action="#{ enqueteComunidadeMBean.listar }" immediate="true" />
			</div>
	</div>
	
	</rich:panel>
	</h:form>	
</f:view>
	
<%@include file="/cv/include/rodape.jsp" %>