<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<h2><ufrn:subSistema /> > Valida��o de GRUs Pagas</h2>
		<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
			<p>As seguintes inscri��es ser�o marcadas como tendo a GRU paga. Por favor, revise a lista e, case de acordo, confirme a mcarca��o das inscri��es com GRUs pagas.</p>
		</div>
		<table class="formulario" width="75%">
		<caption>Inscri��es com GRU quitadas: (${fn:length(processaPagamentoGRUMBean.resultadosBusca) })</caption>
			<thead>
				<tr>
					<th style="text-align: right;" width="10%">Inscri��o</th>
					<th style="text-align: left;">Nome</th>
					<th style="text-align: center;" width="20%">CPF</th>
				</tr>
			</thead>
			<c:forEach var="inscritos" items="#{ processaPagamentoGRUMBean.resultadosBusca }" varStatus="status">
				<tr>
					<td style="text-align: right;"> ${ inscritos.numeroInscricao } </td>
					<td style="text-align: left;"> ${ inscritos.pessoa.nome } </td>
					<td style="text-align: center;"> <ufrn:format type="cpf_cnpj" valor="${ inscritos.pessoa.cpf_cnpj }"/> </td>
				</tr>
			</c:forEach>
		<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Marcar Inscri��es com GRUs Pagas" id="confirmar" action="#{ processaPagamentoGRUMBean.confirmar }" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ processaPagamentoGRUMBean.cancelar }" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigat�rio. </span> <br />
		</center>
		<br />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>