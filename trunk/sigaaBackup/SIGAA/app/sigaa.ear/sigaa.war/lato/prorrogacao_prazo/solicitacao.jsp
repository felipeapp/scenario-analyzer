<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	text-align: center;
	border-spacing: 3px;
}
-->
</style>
<f:view>
	<h2><ufrn:subSistema /> &gt; Solicitar Prorrogação de Prazo</h2>
	<div class="descricaoOperacao">
		<p>
			<strong>Atenção Coordenador!</strong> Esta operação só pode ser realizada uma única vez.
			Preencha abaixo o novo prazo final para encerramento das atividades do seu curso juntamente
			com uma justificativa para tal solicitação. Estes dados serão submetidos para análise da 
			Pró-Reitoria de Pós-Graduação que determinará o deferimento ou não do pedido.
			Lembre-se: após solicitar uma prorrogação de prazo, não será permitido solicitar novamente.
		</p>
	</div>
	<br/>
	<table class="visualizacao">
		<tr>
			<th>Curso:</th>
			<td><h:outputText id="nomeCurso" value="#{prorrogacaoLatoBean.curso.descricao}" /></td>
		</tr>
		<tr>
			<th>Período:</th>
			<td>
				<h:outputText id="dtInicioCurso" value="#{prorrogacaoLatoBean.curso.dataInicio}" /> a
				<h:outputText id="dtFimCurso" value="#{prorrogacaoLatoBean.curso.dataFim}" />
			</td>
		</tr>
	</table>
	<br/>
	<h:form id="formulario">
		<h:inputHidden id="idCurso" value="#{prorrogacaoLatoBean.curso.id}" />
		<table class="formulario" width="80%">
			<caption>Dados da solicitação</caption>
				<tr>
					<th class="required">Novo Prazo Final:</th>
					<td>
						<t:inputCalendar id="dtFinalNova" value="#{prorrogacaoLatoBean.dataFim}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
								onkeypress="return formataData(this,event)" maxlength="10" title="Novo Prazo Final">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<small><h:outputText id="dtMax" value="#{prorrogacaoLatoBean.prazoMax}"/></small>
					</td>
				</tr>
				<tr>
					<th class="required">Justificativa:</th>
					<td><h:inputTextarea id="justificativa" value="#{prorrogacaoLatoBean.justificativa}" cols="60" rows="10"/></td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnSolicitar" value="Submeter Solicitação" action="#{prorrogacaoLatoBean.solicitar}" /> 
						<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{confirm}" action="#{prorrogacaoLatoBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>