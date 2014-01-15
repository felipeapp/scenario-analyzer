<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > ${relatoriosVestibular.nomeRelatorio}</h2>

	<c:if test="${relatoriosVestibular.selecionaFormato}">
		<div class="descricaoOperacao">Este formulário permite exportar ${relatoriosVestibular.nomeRelatorio}.
			Os dados são exportados com a codificação de caracteres <b>ISO-8859-15</b>. 
		</div>
	</c:if>

	<h:form id="form">
		<h:inputHidden value="#{relatoriosVestibular.nomeRelatorio}" />
		<table align="center" class="formulario" width="73%">
			<caption class="listagem">Dados do Relatório</caption>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td><h:selectOneMenu
					value="#{relatoriosVestibular.idProcessoSeletivo}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<c:if test="${relatoriosVestibular.coletaDataAplicacao}">
				<tr>
					<th class="required">Data da Aplicação:</th>
					<td><t:inputCalendar
						value="#{relatoriosVestibular.dataAplicacao}"
						disabled="#{relatoriosVestibular.readOnly}" renderAsPopup="true"
						size="10" maxlength="10"
						onkeypress="return(formataData(this,  event))"
						renderPopupButtonAsImage="true" converter="convertData" 
						popupDateFormat="dd/MM/yyyy"/></td>
					</td>
				</tr>
			</c:if>
			<c:if test="${relatoriosVestibular.coletaTipoDemanda}">
				<tr>
					<td align="right"><h:selectBooleanCheckbox value="#{relatoriosVestibular.demandaFinal}" id="checkDemandaFinal" styleClass="noborder" /></td>
					<td>
						<label for="checkDemandaFinal" 
							onclick="$('form:checkDemandaFinal').checked = !$('form:checkDemandaFinal').checked;">Demanda Final</label>
					</td>
				</tr>
			</c:if>
			<c:if test="${relatoriosVestibular.selecionaFormato}">
				<tr>
					<th class="required">Formato do Relatório:</th>
					<td>
						<h:selectOneMenu value="#{ relatoriosVestibular.formatoArquivo }">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ relatoriosVestibular.formatosRelatorio }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2" align="center"><h:commandButton
						id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatoriosVestibular.gerarRelatorio}" /> <h:commandButton
						value="Cancelar" action="#{relatoriosVestibular.cancelar}"
						onclick="#{confirm}"
						id="cancelar" /></td>
				</tr>
			</tfoot>
		</table>
		
		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		</center>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>