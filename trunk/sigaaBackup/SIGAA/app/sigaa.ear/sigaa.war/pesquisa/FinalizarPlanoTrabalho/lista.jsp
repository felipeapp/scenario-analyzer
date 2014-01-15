<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Finalizar Planos de Trabalho Sem Cota</h2>
	
	<h:form id="form">
		<table class="listagem" style="width: 90%">
		  	<caption class="listagem">Planos de Trabalho</caption>
			<thead>
				<tr>
					<td>Orientador</td>
					<td>Aluno</td>
					<td>Modalidade</td>
				</tr>
			</thead>
			<c:forEach items="#{finalizarPlanoTrabalho.planos}" var="plano" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${plano.orientador.pessoa.nome}</td>
					<td> 
						<c:if test="${ not empty plano.membroProjetoDiscente }">
							${plano.membroProjetoDiscente.discente.pessoa.nome}
						</c:if>
						<c:if test="${ empty plano.membroProjetoDiscente }">
							<em> não definido </em>
						</c:if>
					</td>
					<td> ${plano.tipoBolsaString}</td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr>
					<td align="center" colspan="3">
						<h:commandButton value="Confirmar Finalização" action="#{finalizarPlanoTrabalho.confirmar}" /> 
						<h:commandButton value="<< Selecionar Outra Bolsa" action="#{finalizarPlanoTrabalho.iniciar}" />
						<h:commandButton value="Cancelar" action="#{finalizarPlanoTrabalho.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>