<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>

	<a href="menu.jsf">Menu Principal</a> <br/><br/>

	
	<table class="listagemMobile">
		<caption>Dados do Discente</caption>
		<tbody>
			<tr>
				<th>Período Letivo:</th>
			</tr>
			<tr>	
				<td>
					<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.anoAtual}"/>.<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.periodoAtual}"/>
				</td>
			</tr>
			<tr>
				<th>Matrícula:</th>
			</tr>
			<tr>
				<td>
					<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.discente.matricula}"/>
				</td>
			</tr>	
			<tr>
				<th>Discente:</th>
			<tr>
				<td>
					<h:outputText value="#{usuario.pessoa.nome}"/>
				</td>
			</tr>	
		</tbody>
	</table>	
	
	<br/>
	<c:set var="matriculas"
		value="${ atestadoMatricula.disciplinasMatriculadas }" />
	
	<br/>
	<table class="listagemMobile">
		<caption>
			Turmas Matriculadas
			<c:if test="${atestadoMatricula.qtdAtividadesMatriculadas > 0}">
			(${atestadoMatricula.qtdAtividadesMatriculadas})
			</c:if>
		</caption>
		<tbody>
			<c:forEach var="item" items="${ matriculas }" varStatus="loop">
				<c:if test="${!item.componente.atividade}">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td class="subListagemMobile">${item.componenteCodigo} - ${item.componenteNome}</td>
					</tr>
					<tr>
						<th>Local:</th>
					</tr>
					<tr>
						<td>${item.turma.local}</td>
					</tr>
					<tr>
						<th>Descrição:</th>
					</tr>
					<tr>
						<td>${ item.situacaoMatricula.descricao }</td>
					</tr>
				</c:if>
				<c:if test="${item.componente.atividade}">
					<tr>
						<td>${ item.componenteCodigo } - ${item.componenteNome}</td>
					</tr>
				</c:if>	
			</c:forEach>
		</tbody>
	</table>
	
</f:view>
<%@include file="/mobile/commons/rodape.jsp" %>
