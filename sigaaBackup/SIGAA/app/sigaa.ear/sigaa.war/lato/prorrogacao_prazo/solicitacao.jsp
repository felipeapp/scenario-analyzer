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
	<h2><ufrn:subSistema /> &gt; Solicitar Prorroga��o de Prazo</h2>
	<div class="descricaoOperacao">
		<p>
			<strong>Aten��o Coordenador!</strong> Esta opera��o s� pode ser realizada uma �nica vez.
			Preencha abaixo o novo prazo final para encerramento das atividades do seu curso juntamente
			com uma justificativa para tal solicita��o. Estes dados ser�o submetidos para an�lise da 
			Pr�-Reitoria de P�s-Gradua��o que determinar� o deferimento ou n�o do pedido.
			Lembre-se: ap�s solicitar uma prorroga��o de prazo, n�o ser� permitido solicitar novamente.
		</p>
	</div>
	<br/>
	<table class="visualizacao">
		<tr>
			<th>Curso:</th>
			<td><h:outputText id="nomeCurso" value="#{prorrogacaoLatoBean.curso.descricao}" /></td>
		</tr>
		<tr>
			<th>Per�odo:</th>
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
			<caption>Dados da solicita��o</caption>
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
						<h:commandButton id="btnSolicitar" value="Submeter Solicita��o" action="#{prorrogacaoLatoBean.solicitar}" /> 
						<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{confirm}" action="#{prorrogacaoLatoBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>