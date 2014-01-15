<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Parâmetros do Relatório</h2>
	<h:outputText value="#{ relatoriosCenso.create }" />
		<h:form id="anoPeriodoForm">
			<h:inputHidden value="#{relatoriosCenso.tipoRelatorio}"/>
			<table class="formulario" width="100%">
				<caption>Por favor informe o Ano</caption>
				<tr>
					<th width="50%" class="required">Ano:</th>
					<td> 
						<h:inputText value="#{relatoriosCenso.ano}" size="4" maxlength="4" id="ano" onkeyup="formatarInteiro(this);"/>
					</td>
				</tr>
				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton value="Enviar"	action="#{relatoriosCenso.submeterAno}" id="submeterAnoPeriodo" /> 
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatoriosCenso.cancelar}" id="cancelarAnoPeriodo" />
						</td>
					</tr>
				</tfoot>
			</table>
			<br/>
			<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
