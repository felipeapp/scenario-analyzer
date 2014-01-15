<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="cancelamento" value="if (!confirm('Deseja cancelar esse trancamento?')) return false" scope="request"/>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{movimentacaoAluno.create}" />
	<h2 class="title"><ufrn:subSistema /> > ${movimentacaoAluno.tituloOperacao} &gt; Informe os Dados</h2>
	
	<div class="descricaoOperacao" id="painel-ajuda" >
		<div id="painel-msg">
			<p>Caro Usuário(a),</p><br/>
			<p><b>
				ATENÇÃO! Caso haja trancamentos em semestres POSTERIORES ao trancamento selecionado, eles TAMBÉM SERÃO CANCELADOS. 
			</b></p>
			
		</div>
	
	</div>	

	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	
	<c:if test="${empty movimentacaoAluno.historicoMovimentacoes}">
		<div>
		Esse discente não possui trancamentos "agendados" para semestres futuros
		</div>
	</c:if>
	<c:if test="${not empty movimentacaoAluno.historicoMovimentacoes}">
		<table class="formulario" width="70%">
			<caption>Trancamentos do Discente Para Semestres Futuros</caption>
			<thead>
				<td width="20%">Ano-Período</td>
				<td>Data</td>
				<td width="10%"></td>
			</thead>
			<c:forEach items="${movimentacaoAluno.historicoMovimentacoes}" var="mov" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>${mov.anoPeriodoReferencia }</td>
					<td><ufrn:format type="data" valor="${mov.dataOcorrencia}" /></td>
					<td>
					<h:form prependId="false">
					<input type="hidden" name="id" value="${mov.id }" >
					<h:commandButton value="Cancelar Trancamento" id="cancelarTrancamento"
					action="#{movimentacaoAluno.cancelarTrancamento}" onclick="#{cancelamento}" />
					</h:form>
					</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td colspan="3"><i>${mov.observacao}</i></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<br>
	<center>
		<h:form prependId="false">
		<h:commandButton value="Selecionar Outro Discente" onclick="#{confirm}" id="cancelarOperacao"
			action="#{movimentacaoAluno.iniciarCancelamentoTrancamento}" />
		</h:form>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
