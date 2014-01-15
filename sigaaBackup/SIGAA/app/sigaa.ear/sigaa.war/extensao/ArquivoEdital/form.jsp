<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Enviar Arquivo do Edital</h2>
	<br>
	<h:form enctype="multipart/form-data" id="form">

		<h:inputHidden value="#{editalExtensao.confirmButton}" />
		<h:inputHidden value="#{editalExtensao.obj.id}" />

		<table class="formulario" width="100%">
			<caption class="listagem">Enviar Arquivo para o Edital</caption>

			<tr>
				<th width="20%">Descrição:</th>
				<td><h:outputText value="#{editalExtensao.obj.descricao}" />
				</td>
			</tr>

			<tr>
				<th width="20%">Número do Edital:</th>
				<td>
					<h:outputText value="#{editalExtensao.obj.numeroEdital}"/>
				</td>
			</tr>

			<tr>
				<th width="20%">Número de Bolsas:</th>
				<td>
					<h:outputText value="#{editalExtensao.obj.numeroBolsas}"/>
				</td>
			</tr>

			<tr>
				<th width="20%">Valor Financiamento:</th>
				<td><h:outputText value="#{editalExtensao.obj.valorFinanciamento}" /></td>
			</tr>

			<tr>
				<th width="20%">Arquivo:</th>
				<td>
					<t:inputFileUpload id="uFile" value="#{editalExtensao.file}" storage="file" required="true" readonly="#{editalExtensao.readOnly}" size="70"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Enviar Arquivo"	action="#{editalExtensao.enviarArquivo}" id="enviar"/>
						<h:commandButton value="Cancelar" action="#{editalExtensao.cancelar}" onclick="#{confirm}" id="cancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>

		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>