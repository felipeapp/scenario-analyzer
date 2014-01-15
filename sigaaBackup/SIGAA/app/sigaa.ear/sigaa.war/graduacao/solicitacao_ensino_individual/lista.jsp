<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmCancel" value="if (!confirm('Tem certeza que deseja cancelar esta solicita��o de turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}?')) return false" scope="request"/>


<f:view>
<%@include file="/portais/discente/menu_discente.jsp" %>

	<h:outputText value="#{solicitacaoEnsinoIndividual.create}" />
	<h2><ufrn:subSistema /> &gt; Solicita��es de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</h2>

	<div class="descricaoOperacao">
	
	<p>Caro Aluno(a),</p><br/>

	<p>
		Aqui voc� pode acompanhar o andamento das solicita��es de ${solicitacaoEnsinoIndividual.tipoSolicitacao} que voc� realizou.
		Sua solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} ir� para an�lise da coordena��o do curso que, caso aprove, 
		solicitar� a cria��o da turma � chefia do departamento.
		A solicita��o s� estar� atendida quando a turma for criada pelo chefe de departamento.
	</p>
	 
	<p> Na coluna <b>situa��o</b> voc� pode acompanhar a situa��o de sua solicita��o. Abaixo voc� encontra o significado de cada situa��o: </p>
	
	<ul>
		<li> <b>Pendente: </b> Voc� realizou a solicita��o e ela ainda est� aguardando a an�lise da coordena��o do curso. 
		</li>
		<li> <b>Cancelada:</b> Voc� desistiu da solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} e a cancelou. 
		</li>
		<li>
			<b>Solicita��o Negada:</b> A solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} n�o poder� ser atendida e foi negada pela coordena��o de curso.
		</li>
		<li>
			<b>Turma Solicitada:</b> A solicita��o de turma foi realizada pela coordena��o de curso e est� aguardando a cria��o da turma pela chefia de departamento.
		</li>
		<li>
			<b>Atendida - Turma criada:</b> A solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} foi atendida e a turma foi criada.
		</li>
		<li>
			<b>Turma Negada:</b> A solicita��o de turma foi realizada pela coordena��o de curso por�m a cria��o da turma foi negada pela chefia do departamento.
		</li>
	</ul>

	<p>	Em caso de d�vidas, contate seu orientador para maiores esclarecimentos. </p>
	</div>


	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/cronograma/remover.gif"style="overflow: visible;"/>: Cancelar Solicita��o
	</div>
	</center>

	<c:set var="solicitacoes" value="${solicitacaoEnsinoIndividual.solicitacoesEnviadas}"/>

	<c:if test="${empty solicitacoes}">
		<br><div style="font-style: italic; text-align:center">Voc� n�o realizou nenhuma solicita��o de ${solicitacaoEnsinoIndividual.tipoSolicitacao} neste per�odo.</div>
	</c:if>
	<c:if test="${not empty solicitacoes}">

		<table class="listagem" style="width: 100%">
			<caption class="listagem">Solicita��es de ${solicitacaoEnsinoIndividual.tipoSolicitacao} Realizadas</caption>
			<thead>
				<tr>
					<td>Componente</td>
					<td width="15%">Data Solicita��o</td>
					<td width="18%">Situa��o</td>
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
							<h:commandButton image="/img/cronograma/remover.gif" styleClass="noborder" alt="Cancelar Solicita��o" title="Cancelar Solicita��o" 
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
