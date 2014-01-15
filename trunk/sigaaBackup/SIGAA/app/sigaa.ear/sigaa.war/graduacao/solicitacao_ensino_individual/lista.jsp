<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmCancel" value="if (!confirm('Tem certeza que deseja cancelar esta solicitação de turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}?')) return false" scope="request"/>


<f:view>
<%@include file="/portais/discente/menu_discente.jsp" %>

	<h:outputText value="#{solicitacaoEnsinoIndividual.create}" />
	<h2><ufrn:subSistema /> &gt; Solicitações de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</h2>

	<div class="descricaoOperacao">
	
	<p>Caro Aluno(a),</p><br/>

	<p>
		Aqui você pode acompanhar o andamento das solicitações de ${solicitacaoEnsinoIndividual.tipoSolicitacao} que você realizou.
		Sua solicitação de ${solicitacaoEnsinoIndividual.tipoSolicitacao} irá para análise da coordenação do curso que, caso aprove, 
		solicitará a criação da turma à chefia do departamento.
		A solicitação só estará atendida quando a turma for criada pelo chefe de departamento.
	</p>
	 
	<p> Na coluna <b>situação</b> você pode acompanhar a situação de sua solicitação. Abaixo você encontra o significado de cada situação: </p>
	
	<ul>
		<li> <b>Pendente: </b> Você realizou a solicitação e ela ainda está aguardando a análise da coordenação do curso. 
		</li>
		<li> <b>Cancelada:</b> Você desistiu da solicitação de ${solicitacaoEnsinoIndividual.tipoSolicitacao} e a cancelou. 
		</li>
		<li>
			<b>Solicitação Negada:</b> A solicitação de ${solicitacaoEnsinoIndividual.tipoSolicitacao} não poderá ser atendida e foi negada pela coordenação de curso.
		</li>
		<li>
			<b>Turma Solicitada:</b> A solicitação de turma foi realizada pela coordenação de curso e está aguardando a criação da turma pela chefia de departamento.
		</li>
		<li>
			<b>Atendida - Turma criada:</b> A solicitação de ${solicitacaoEnsinoIndividual.tipoSolicitacao} foi atendida e a turma foi criada.
		</li>
		<li>
			<b>Turma Negada:</b> A solicitação de turma foi realizada pela coordenação de curso porém a criação da turma foi negada pela chefia do departamento.
		</li>
	</ul>

	<p>	Em caso de dúvidas, contate seu orientador para maiores esclarecimentos. </p>
	</div>


	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/cronograma/remover.gif"style="overflow: visible;"/>: Cancelar Solicitação
	</div>
	</center>

	<c:set var="solicitacoes" value="${solicitacaoEnsinoIndividual.solicitacoesEnviadas}"/>

	<c:if test="${empty solicitacoes}">
		<br><div style="font-style: italic; text-align:center">Você não realizou nenhuma solicitação de ${solicitacaoEnsinoIndividual.tipoSolicitacao} neste período.</div>
	</c:if>
	<c:if test="${not empty solicitacoes}">

		<table class="listagem" style="width: 100%">
			<caption class="listagem">Solicitações de ${solicitacaoEnsinoIndividual.tipoSolicitacao} Realizadas</caption>
			<thead>
				<tr>
					<td>Componente</td>
					<td width="15%">Data Solicitação</td>
					<td width="18%">Situação</td>
					<td width="2%"></td>
					
				</tr>
			</thead>
			<c:forEach items="${solicitacoes}" var="solicitacao" varStatus="loop">
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
					<td>${solicitacao.componente}</td>
					<td><ufrn:format type="data" valor="${solicitacao.dataSolicitacao}" /></td>
					<td>${solicitacao.situacaoString}</td>
					<td>
						<c:if test="${solicitacao.solicitada}">
						<h:form>
							<input type="hidden" value="${solicitacao.id}" name="id" /> 
							<h:commandButton image="/img/cronograma/remover.gif" styleClass="noborder" alt="Cancelar Solicitação" title="Cancelar Solicitação" 
							onclick="#{confirmCancel}" action="#{solicitacaoEnsinoIndividual.cancelarSolicitacao}" />
						</h:form>
						</c:if>
					</td>
				</tr>
				<c:if test="${solicitacao.negada && not empty solicitacao.justificativaNegacao}">
				<tr>
					<td colspan="5" style="font-variant: small-caps; font-style: italic; font-weight: bold; " >
						Justificativa: ${solicitacao.justificativaNegacao}	
					</td>
				</tr>
				</c:if>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
