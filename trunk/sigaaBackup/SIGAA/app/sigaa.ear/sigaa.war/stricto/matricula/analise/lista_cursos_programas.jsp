<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2>
		<ufrn:subSistema /> > Programas
	</h2>
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>Selecione um curso para analisar as matrículas dos discentes.</p>
	</div>
	
	<h:form id="formListaRecomendacaoPrograma">
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" title="Selecionar" style="overflow: visible;" />: Selecionar
		</div>
		
		<table class="listagem">
			<caption class="listagem">Cursos</caption>
			<thead>
				<tr>
					<th>Curso</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{analiseSolicitacaoMatricula.dadosCursoUnidade}" var="item" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td><h:outputText value="#{item.nome}"/></td>
					<td>
						<h:commandLink action="#{analiseSolicitacaoMatricula.iniciarOrientadorStricto}" id="selecionar">
							<h:graphicImage value="/img/seta.gif"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>