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
	<h2><ufrn:subSistema /> &gt; Prorrogar Prazo de Curso Lato Sensu</h2>

	<h:form id="formulario">
		<table class="formulario">
			<caption>Dados da prorrogação</caption>
				<tr>
					<th class="required">Curso:</th>
					<td><a4j:region>
						<h:selectOneMenu value="#{prorrogacaoLatoBean.curso.id}" valueChangeListener="#{prorrogacaoLatoBean.carregarInfoCurso}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{lato.allCombo}" />
							<a4j:support event="onchange" reRender="dtInicio,dtFim,dtFinal,dtMax"/>
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region></td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Período Atual do Curso</td>
				</tr>
				<tr>
					<th>Início:</th>
					<td><h:outputText id="dtInicio" value="#{prorrogacaoLatoBean.curso.dataInicio}" /></td>
				</tr>
				<tr>
					<th>Fim:</th>
					<td><h:outputText id="dtFim" value="#{prorrogacaoLatoBean.curso.dataFim}" /></td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Atualizar Prazo Final</td>
				</tr>
				<tr>
					<th class="required">Novo Prazo Final:</th>
					<td>
						<t:inputCalendar id="dtFinal" value="#{prorrogacaoLatoBean.cursoOld.dataFim}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" size="10" title="Novo Prazo Final"
								onkeypress="return formataData(this,event)" maxlength="10" popupDateFormat="dd/MM/yyyy">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<small><h:outputText id="dtMax" value="#{prorrogacaoLatoBean.prazoMax}"/></small>
					</td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="cadastrar" value="Atualizar"
						action="#{prorrogacaoLatoBean.prorrogar}" /> <h:commandButton
						id="cancelar" value="Cancelar" onclick="#{confirm}"
						action="#{prorrogacaoLatoBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>