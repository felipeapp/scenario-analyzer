<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Lista de Mensalidades </h2>
<f:view>
<h:form id="form">

	<div class="descricaoOperacao">
		<p>Caro Discente,</p>
		<p>A lista abaixo exibe as mensalidades do seu curso
			permitindo-lhe gerar uma GRU (boleto) para pagamento.</p>
		<p>Há um período de processamento para os pagamentos e, por este
			motivo, uma mensalidade tenha sido paga poderá aparecer como em
			aberto. Aguarde o período de processamento antes de fazer qualquer
			reclamação.</p>
	</div>

	<div class="infoAltRem">
		<h:graphicImage value="/img/imprimir.gif" style="overflow: visible;"/>: Imprimir GRU
		<h:graphicImage value="/img/check.png" style="overflow: visible;"/>: GRU quitada 	
	</div>

	<table class="listagem">
	<caption class="listagem">Listagem das Mensalidades</caption>
	<thead>
		<tr>
			<td width="5%">Nº</td>
			<td>Vencimento</td>
			<td style="text-align: right;">Valor</td>
			<td style="text-align: right;">Valor Pago</td>
			<td>Situação</td>
			<td width="2%"></td>
		</tr>
	</thead>
	<c:set var="totalMensalidade" value="0.0" />
	<c:set var="totalPago" value="0.0" />
	<c:forEach items="#{mensalidadeCursoLatoMBean.resultadosBusca}" var="lista" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${lista.ordem}</td>
			<td><ufrn:format type="data" valor="${lista.vencimento}" /></td>
			<td style="text-align: right;"><ufrn:format type="moeda" valor="${lista.gru.valor}" /></td>
			<td style="text-align: right;"><ufrn:format type="moeda" valor="${lista.gru.valorPago}" /></td>
			<td>
				<h:outputText value="QUITADA" rendered="#{ lista.quitada }"/>						
				<h:outputText value="EM ABERTO" rendered="#{ !lista.quitada }"/>
			</td>
			<td>
				<h:commandLink title="Imprimir GRU" action="#{mensalidadeCursoLatoMBean.imprimirGRU}"  rendered="#{ !lista.quitada }">
					<h:graphicImage url="/img/imprimir.gif" />
					<f:param name="id" value="#{lista.id}"/>
				</h:commandLink>
				<h:graphicImage url="/img/check.png" rendered="#{ lista.quitada }" />
			</td>
			<c:set var="totalMensalidade" value="${ totalMensalidade + lista.gru.valor }" />
			<c:set var="totalPago" value="${ totalPago + lista.gru.valorPago }" />
		</tr>
    </c:forEach>
    <tr>
		<td colspan="2" style="text-align: right;font-weight: bold;">Total:</td>
		<td style="text-align: right;font-weight: bold;"><ufrn:format type="moeda" valor="${ totalMensalidade }" /></td>
		<td style="text-align: right;font-weight: bold;"><ufrn:format type="moeda" valor="${ totalPago }" /></td>
		<td></td>
		<td></td>
	</tr>
    <tfoot>
    	<tr>
    		<td colspan="6" style="text-align: center">
    			<h:commandButton value="Cancelar" action="#{ mensalidadeCursoLatoMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
    		</td>
    	</tr>
    </tfoot>							
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>