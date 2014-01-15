<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>

	<a href="menu.jsf">Menu Principal</a><br/>
	<br/>
	<c:set var="avaliacoes" value="${ portalDiscente.proximasAtividades }"/>
	<c:choose>
		<c:when test="${ not empty avaliacoes }">
			<table class="listagemMobile">
				<caption>Próximas Avaliações</caption>		
				<tbody>		
			
					<c:forEach items="${avaliacoes}" var="avaliacao" varStatus="status">
						<tr>
							<td class="subListagemMobile"> ${avaliacao.turma.disciplina.detalhes.nome}</td>
						</tr>
						<tr>	
							<th>Data:</th>
						</tr>									
						<tr>	
							<td>
								<c:if test="${avaliacao.dias == 0}"> Hoje </c:if>
								<ufrn:format name="avaliacao" property="data" type="data"/>
							</td> 
						</tr>
						<tr>	
							<th>Descrição:</th>
						</tr>	
						<tr>
							<td>
								${avaliacao.descricao }
							</td>
						</tr>	
					</c:forEach>
				</tbody>
			</table>	
		</c:when>
		<c:otherwise>
			<p class="vazio">
				Nao ha avaliacoes cadastradas
			</p>
		</c:otherwise>
	</c:choose>
	
</f:view>
<%@include file="/mobile/commons/rodape.jsp" %>
