<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<h2><ufrn:subSistema /> > Validação de GRUs Pagas</h2>
		<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
			<p>Este formulário permite verificar no sistema quais inscrições
				tiveram sua GRU quitada. Esta informação será então registrada na
				inscrição do candidato.</p>
		</div>
		<table class="formulario" width="75%">
			<caption>Selecione um Processo Seletivo</caption>
			<tbody>
				<tr>
					<th class="required">Processo Seletivo:</th>
					<td><h:selectOneMenu
						value="#{processaPagamentoGRUMBean.obj.processoSeletivo.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar GRUs Pagas" id="buscar" action="#{ processaPagamentoGRUMBean.buscar }" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ processaPagamentoGRUMBean.cancelar }" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br />
		</center>
		<br />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>