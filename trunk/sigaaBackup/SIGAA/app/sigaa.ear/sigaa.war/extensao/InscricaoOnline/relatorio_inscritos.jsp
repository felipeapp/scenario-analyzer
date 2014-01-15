<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Relat�rio Quantitativo de Inscritos em Cursos e Eventos de Extens�o pela �rea P�blica</h2>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>C�digo:</th>
				<td><h:outputText value="#{inscricaoAtividade.atividade.codigo}" id="codigo" /></td>
			</tr>
			<tr>
				<th>T�tulo:</th>
				<td><h:outputText value="#{inscricaoAtividade.atividade.titulo}" id="titulo" /></td>
			</tr>
			<tr>
				<th>Coordena��o:</th>
				<td><h:outputText value="#{inscricaoAtividade.atividade.coordenacao.servidor.pessoa.nome}" id="coord" /></td>
			</tr>
			<tr>
				<th>Per�odo:</th>
				<td>
					<h:outputText value="#{inscricaoAtividade.atividade.dataInicio}" id="inicio"> 
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
					at� 
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
			<caption>Lista das Inscri��es nessa A��o</caption>
			<thead>
				<tr>
					<th>Nome Completo</th>
					<th>E-mail</th>
					<th>Institui��o</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="3" class="subFormulario">Inscri��es ACEITAS (${fn:length(aceitos)})</td>
				</tr>
				<c:if test="${empty aceitos}">
					<tr>
						<td colspan="3">N�o existem inscri��es <b>aceitas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${aceitos}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscri��es CONFIRMADAS (${fn:length(confirmados)})</td>
				</tr>
				<c:if test="${empty confirmados}">
					<tr>
						<td colspan="3">N�o existem inscri��es <b>confirmadas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${confirmados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscri��es RECUSADAS (${fn:length(recusados)})</td>
				</tr>
				<c:if test="${empty recusados}">
					<tr>
						<td colspan="3">N�o existem inscri��es <b>recusadas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${recusados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscri��es PENDENTES (${fn:length(inscritos)})</td>
				</tr>
				<c:if test="${empty inscritos}">
					<tr>
						<td colspan="3">N�o existem inscri��es <b>pendentes de confirma��o</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${inscritos}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="3" class="subFormulario">Inscri��es CANCELADAS (${fn:length(cancelados)})</td>
				</tr>
				<c:if test="${empty cancelados}">
					<tr>
						<td colspan="3">N�o existem inscri��es <b>canceladas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="${cancelados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>