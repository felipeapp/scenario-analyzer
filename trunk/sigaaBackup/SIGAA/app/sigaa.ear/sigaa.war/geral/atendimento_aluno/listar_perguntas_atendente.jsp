<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
function habilitarDetalhes(id) {
	var linha = 'linha_'+ id;
	var icone = 'icone_'+ id;
	if ( $(linha).style.display != 'table-cell' ) {
		$(linha).style.display = 'table-cell';
		$(icone).src= '/sigaa/img/biblioteca/cima.gif';
	} else {
		$(linha).style.display = 'none';
		$(icone).src= '/sigaa/img/biblioteca/baixo.gif';
	}
}
</script>

<h2><ufrn:subSistema /> > Perguntas Respondidas</h2>

<f:view>

	<div class="infoAltRem">
		 <h:graphicImage value="/img/biblioteca/baixo.gif" style="overflow: visible;"/>: Ver Resposta
		 <h:graphicImage value="/img/biblioteca/cima.gif" style="overflow: visible;"/>: Ocultar Resposta
	</div>

	<br />

	<h:form id="lista">
		<table class="listagem" id="listagem" width="80%">
			<caption>Perguntas Respondidas</caption>
			<thead>
				<tr>
					<th>Titulo</th>
					<th>Data Atendimento</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{atendimentoAluno.listarPerguntasRespondidasAtendente}" var="pergunta" varStatus="status">
					 <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${ pergunta.titulo}</td>
						<td><fmt:formatDate pattern="dd/MM/yyyy hh:mm" value="${pergunta.dataAtendimento}" /></td>
						<td align="right">
							<a href="javascript:void(0)" onclick="habilitarDetalhes(${pergunta.id});"  title="Ver Resposta">
								<img src="${ctx}/img/biblioteca/baixo.gif" title="Abrir Pergunta" id="icone_${pergunta.id}"/>
							</a>
						</td>
					</tr>
					<tr>
						<td colspan="3" id="linha_${pergunta.id}" style="display: none;" >
							<table class="visualizacao" width="80%" id="dadosPergunta">
								<tr>
									<td colspan="2"><h2>Resposta</h2></td>
								</tr>
								<tr>
									<th>Atendente:</th>
									<td>${ pergunta.atendente.nome }</td>
								</tr>
								<tr>
									<th>Aluno:</th>
									<td>${ pergunta.discente.nome }</td>
								</tr>			
								<tr>
									<th>Título:</th>
									<td>${ pergunta.titulo }</td>
								</tr>	
								<tr>	
									<th>Pergunta:</th>
									<td>${ pergunta.pergunta }</td>
								</tr>
								<tr>
									<th>Data/Hora:</th>
									<td>  <fmt:formatDate pattern="dd/MM/yyyy hh:mm" value="${pergunta.dataAtendimento}" /></td>
								</tr>														
							</table>
							<br/>
							<table class="formulario" width="90%" id="conteudoPergunta">
								<tbody>
									<tr>
										<td>${pergunta.resposta}</td>
									</tr>
								</tbody>
							</table>	
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>