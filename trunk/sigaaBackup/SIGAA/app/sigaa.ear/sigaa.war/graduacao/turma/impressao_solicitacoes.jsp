<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
<h2>Relação de Solicitações</h2>

	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Curso:</th>
				<td> 
					<h:outputText value="#{analiseSolicitacaoTurma.curso.descricaoCompleta}" rendered="#{not empty analiseSolicitacaoTurma.curso}" /> 
					<h:outputText value="TODOS" rendered="#{empty analiseSolicitacaoTurma.curso}" />
				</td>
			</tr>
			<tr>
				<th>Componente:</th>
				<td>
					<h:outputText value="#{analiseSolicitacaoTurma.componenteCurricular.descricao}" rendered="#{analiseSolicitacaoTurma.componenteCurricular != null}"/>
					<h:outputText value="TODOS" rendered="#{analiseSolicitacaoTurma.componenteCurricular == null}"/>
				</td>
			</tr>
			<c:if test="${not empty analiseSolicitacaoTurma.horario}">
				<tr>
					<th>Horário:</th>
					<td>
						<h:outputText value="#{analiseSolicitacaoTurma.horario}"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty analiseSolicitacaoTurma.docente.pessoa.nome}">
				<tr>
					<th>Docente:</th>
					<td><h:outputText value="#{analiseSolicitacaoTurma.docente.pessoa.nome}"/></td>
				</tr>
			</c:if>			
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda"  width="99%">
		<thead>
			<tr>
				<td>Componente Curricular</td>
				<td>Solicitante</td>
				<td style="text-align: center;">Data da Solicitação</td>
				<td>Tipo</td>
				<td>Horário</td>
				<td style="text-align: right;">Vagas</td>
				<td>Situação</td>
			</tr>
		</thead>

		<tbody>
		<c:forEach var="solicitacao" items="#{analiseSolicitacaoTurma.solicitacoes}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td valign="top">${solicitacao.componenteCurricular.codigo} - ${solicitacao.componenteCurricular.nome}</td>
				<td valign="top">${solicitacao.curso.descricao}</td>
				<td valign="top" style="text-align: center;"><ufrn:format type="data" valor="${solicitacao.dataCadastro}" /></td>
				<td valign="top">${solicitacao.tipoString}</td>
				<td valign="top">${solicitacao.horario}</td>
				<td valign="top" style="text-align: right;">${solicitacao.vagas}</td>
				<td valign="top">${solicitacao.situacaoString}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>