<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Relatório Quantitativo de Inscritos em Cursos e Eventos de Extensão pela Área Pública</h2>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Código:</th>
				<td><h:outputText value="#{inscricaoAtividade.atividade.codigo}" id="codigo" /></td>
			</tr>
			<tr>
				<th>Título:</th>
				<td><h:outputText value="#{inscricaoAtividade.atividade.titulo}" id="titulo" /></td>
			</tr>
			<tr>
				<th>Coordenação:</th>
				<td><h:outputText value="#{inscricaoAtividade.atividade.coordenacao.servidor.pessoa.nome}" id="coord" /></td>
			</tr>
			<tr>
				<th>Período:</th>
				<td>
					<h:outputText value="#{inscricaoAtividade.atividade.dataInicio}" id="inicio"> 
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
					até 
					<h:outputText value="#{inscricaoAtividade.atividade.dataFim}" id="fim">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
				</td>
			</tr>
		</table>
	</div>

    <c:set value="#{inscricaoAtividade.inscricoesAceitas}" var="aceitos" />
    <c:set value="#{inscricaoAtividade.inscricoesConfirmadas}" var="confirmados" />
    <c:set value="#{inscricaoAtividade.inscricoesCanceladas}" var="cancelados" />
    <c:set value="#{inscricaoAtividade.inscricoesRecusadas}" var="recusados" />
    <c:set value="#{inscricaoAtividade.inscricoesNaoConfirmadas}" var="inscritos" />
	
	<table class="tabelaRelatorio" width="100%">
			<caption>Lista das Inscrições nessa Ação</caption>
			<thead>
				<tr>
					<th>Nome Completo</th>
					<th>E-mail</th>
					<th>Instituição</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="3" class="subFormulario">Inscrições ACEITAS (${fn:length(aceitos)})</td>
				</tr>
				<c:if test="${empty aceitos}">
					<tr>
						<td colspan="3">Não existem inscrições <b>aceitas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${aceitos}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscrições CONFIRMADAS (${fn:length(confirmados)})</td>
				</tr>
				<c:if test="${empty confirmados}">
					<tr>
						<td colspan="3">Não existem inscrições <b>confirmadas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${confirmados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscrições RECUSADAS (${fn:length(recusados)})</td>
				</tr>
				<c:if test="${empty recusados}">
					<tr>
						<td colspan="3">Não existem inscrições <b>recusadas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${recusados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscrições PENDENTES (${fn:length(inscritos)})</td>
				</tr>
				<c:if test="${empty inscritos}">
					<tr>
						<td colspan="3">Não existem inscrições <b>pendentes de confirmação</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${inscritos}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscrições CANCELADAS (${fn:length(cancelados)})</td>
				</tr>
				<c:if test="${empty cancelados}">
					<tr>
						<td colspan="3">Não existem inscrições <b>canceladas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${cancelados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>