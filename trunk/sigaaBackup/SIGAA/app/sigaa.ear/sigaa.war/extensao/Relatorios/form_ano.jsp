<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Parâmetros do Relatório</h2>
	<h:outputText value="#{ relatoriosExtensaoCenso.create }" />
		<h:form id="anoPeriodoForm">
			<h:inputHidden value="#{relatoriosExtensaoCenso.tipoRelatorio}"/>
			<h:inputHidden value="#{relatoriosExtensaoCenso.formato}"/>
			<h:inputHidden value="#{relatoriosExtensaoCenso.escolheFormato}"/>
			<table class="formulario" width="100%">
				<caption>Por favor informe o Ano</caption>
				<tr>
					<th width="50%" class="required">Ano:</th>
					<td> <h:inputText value="#{relatoriosExtensaoCenso.ano}" size="4" maxlength="4" id="ano"/> </td>
				</tr>
				<c:if test="${relatoriosExtensaoCenso.escolheFormato}">
				<tr>
					<th class="obrigatorio">Formato do Relatório:</th>
					<td>
						<h:selectOneRadio value="#{relatoriosExtensaoCenso.formato}">
							<f:selectItem itemValue="pdf" itemLabel="PDF" />
							<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
							<f:selectItem itemValue="html" itemLabel="HTML" />
						</h:selectOneRadio>
					</td>
				</tr>
				</c:if>
				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton value="Enviar"	action="#{relatoriosExtensaoCenso.submeterAno}" id="submeterAnoPeriodo" /> 
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatoriosExtensaoCenso.cancelar}" id="cancelarAnoPeriodo" />
						</td>
					</tr>
				</tfoot>
			</table>
			<br>
			<center><html:img page="/img/required.gif"
				style="vertical-align: top;" /> <span class="fontePequena">
			Campos de preenchimento obrigatório. </span> <br>
			<br>
			</center>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
