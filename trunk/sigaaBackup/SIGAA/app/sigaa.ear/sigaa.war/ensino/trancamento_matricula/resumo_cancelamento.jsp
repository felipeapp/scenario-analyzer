<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.ensino.dominio.MotivoTrancamento"%>
<f:view>
	<h:outputText value="#{trancamentoMatricula.create}"/>

	<h2> Cancelar Solicitação de Trancamento de Matrícula </h2>

	<div class="descricaoOperacao">
		<p>
		Caro Aluno,
		</p><br>
		<p>
		Confirme se realmente deseja cancelar a solicitação de trancamento desta disciplina.
		</p>
		<p>
		Observe que se você estiver cancelando a solicitação de trancamento de uma disciplina que é co-requisito
		de outra que também está solicitada para trancamento, a solicitação de trancamento desta outra também será cancelado.
		</p>
	</div>

	<c:set var="discente" value="#{trancamentoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<h:form>
	<table class="listagem">
		<caption>Solicitações de Trancamento que Serão Canceladas</caption>
		<thead>
		<tr>
			<th> Ano.Periodo </th>
			<th> Componente </th>
			<th> Turma</th>
			<th> Status</th>
			<th> Data Solicitação</th>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="solicitacao" items="${trancamentoMatricula.solicitacoes}" varStatus="status">

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${solicitacao.matriculaComponente.anoPeriodo } </td>
					<td> ${solicitacao.matriculaComponente.componenteDescricaoResumida} </td>
					<td align="center">${solicitacao.matriculaComponente.turma.codigo}</td>
					<td align="center">${solicitacao.matriculaComponente.situacaoMatricula.descricao}</td>
					<td>
						<ufrn:format type="data" valor="${solicitacao.dataCadastro}" />
					</td>
				</tr>

				<c:if test="${not empty solicitacao.replica}">
				<tr>
					<td><b>Orientação do Coordenador:</b></td>
					<td colspan="5"><b>${solicitacao.replica}<b	></td>
				</tr>
				</c:if>

			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6" align="center">

				</td>
			</tr>
		</tfoot>
	</table>

	<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>

		<div align="center">
	<h:commandButton id="gravarCancelamentoSolicitacao" value="#{trancamentoMatricula.confirmButton}" action="#{trancamentoMatricula.gravarCancelamentoSolicitacao}"/>
	<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/>
	<h:commandButton id="desistirCancelamentoSolicitacao" value="Cancelar" onclick="#{confirm}" action="#{trancamentoMatricula.cancelar}" />
	<div align="center">
	</div>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>