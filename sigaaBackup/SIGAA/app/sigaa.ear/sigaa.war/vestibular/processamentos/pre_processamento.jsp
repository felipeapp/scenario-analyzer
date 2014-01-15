<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
function aguarde() {
	document.getElementById("wait").style.display="inline";
	document.getElementById("botoes").style.display="none";
}
</script>

<f:view>
	<h:form id="form">
		<h2><ufrn:subSistema /> > Processamento de Seleção de
		Fiscais</h2>
		<div class="descricaoOperacao">Verifique se este Resumo
		Quantitativo do Processamento, com o número de fiscais selecionados
		por município e curso/servidores, está dentro do desejado. Para
		consolidar o resultado, clique no botão <b>Consolidar o Resultado
		da Simulação</b>, localizado no final do resumo.<br>
		Caso deseje realizar uma nova simulação do processamento, clique em <b><<
		Voltar</b></div>
		<c:set var="_grupoAtual"/>
		<c:set var="totalTitular" value="0" />
		<c:set var="totalReserva" value="0" />
		<c:set var="totalInscritos" value="0" />
		<table class="formulario" width="75%" id="resumoGeral">
			<caption>Resumo quantitativo do processamento</caption>
				<tbody>
				<c:forEach items="#{processamentoSelecaoFiscal.resumoProcessamento}" var="resumo" >
					<c:set var="_grupoLoop" value="${resumo.grupoSelecao}" />
					<c:if test="${_grupoLoop != _grupoAtual}">
						<c:set var="_grupoAtual" value="${_grupoLoop}"/>
						<c:if test="${totalTitular != 0 || totalReserva != 0 || totalInscritos != 0}">
							<tr class="caixaCinzaMedio">
								<td style="text-align: right;font-weight: bold;">Total:</td>
								<td style="text-align: right;font-weight: bold;">${totalInscritos}</td>
								<td style="text-align: right;font-weight: bold;">${totalTitular}</td>
								<td style="text-align: right;font-weight: bold;">${totalReserva}</td>
								<td></td>
								<td></td>
							</tr>
						</c:if>
						<tr>
							<td class="subFormulario" colspan="6">${_grupoAtual}</td>
						</tr>
						<tr class="caixaCinzaMedio">
							<td style="font-weight: bold;">Grupo de Seleção (Curso / Residente / Servidor)</td>
							<td style="text-align: right;font-weight: bold;">Inscritos</td>
							<td style="text-align: right;font-weight: bold;" width="15%">Nº de Titulares</td>
							<td style="text-align: right;font-weight: bold;" width="15%">Nº de Reservas</td>
							<td style="text-align: right;font-weight: bold;">IRA Máximo</td>
							<td style="text-align: right;font-weight: bold;">IRA Mínimo</td>
						</tr>
						<c:set var="index" value="0" />
						<c:set var="totalInscritos" value="0" />
						<c:set var="totalTitular" value="0" />
						<c:set var="totalReserva" value="0" />
					</c:if>
					<tr class="${index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${resumo.subgrupoSelecao}</td>
						<td style="text-align: right;">${resumo.inscritos}</td>
						<td style="text-align: right;">${resumo.titulares}
							(<ufrn:format type="valor" valor="${resumo.titulares / resumo.inscritos * 100}" />%)</td>
						<td style="text-align: right;">${resumo.reservas}
							(<ufrn:format type="valor" valor="${resumo.reservas / resumo.inscritos * 100}" />%)</td>
						<td style="text-align: right;"><ufrn:format type="valor4" valor="${resumo.iraMaximo}" /></td>
						<td style="text-align: right;"><ufrn:format type="valor4" valor="${resumo.iraMinimo}" /></td>
						<c:set var="totalInscritos" value="${totalInscritos + resumo.inscritos}" />
						<c:set var="totalTitular" value="${totalTitular + resumo.titulares}" />
						<c:set var="totalReserva" value="${totalReserva + resumo.reservas}" />
					</tr>
					<c:set var="index" value="${index + 1}" />
				</c:forEach>
				</tbody>
		      	<tfoot>
			      	<tr>
			      	<td colspan="6">
			      		<div id="wait" style="display: none;"><html:img	page="/img/indicator.gif" align="middle" />Processando...</div>
			      		<div id="botoes" style="border: 0;">
				      		<h:commandButton value="Consolidar o Resultado da Simulação" id="consolidar"
				      			onclick="aguarde()" action="#{processamentoSelecaoFiscal.consolidarProcessamento}" />
				      		<h:commandButton value="<< Voltar" id="telaProcessamento" 
				      			action="#{processamentoSelecaoFiscal.telaProcessamento}" />
			      			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ processamentoSelecaoFiscal.cancelar }" />
			      		</div>
					</td>
					</tr>
				</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>