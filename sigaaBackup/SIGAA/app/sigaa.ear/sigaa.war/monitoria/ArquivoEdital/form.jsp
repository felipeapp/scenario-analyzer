<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Enviar Arquivo do Edital</h2>
	<br>
	<h:form enctype="multipart/form-data">

		<h:inputHidden value="#{editalMonitoria.confirmButton}" />
		<h:inputHidden value="#{editalMonitoria.obj.id}" />

		<table class="formulario" width="100%">
			<caption class="listagem">Enviar Arquivo para o Edital</caption>
            <tbody>
				<tr>
					<th width="20%" class="rotulo">Descrição:</th>
					<td><h:outputText value="#{editalMonitoria.obj.descricao}" />
					</td>
				</tr>
	
				<tr>
					<th width="20%" class="rotulo">Número do Edital:</th>
					<td>
						<h:outputText value="#{editalMonitoria.obj.numeroEdital}"/>
					</td>
				</tr>
	
				<tr>
					<th width="20%" class="rotulo">Número de Bolsas:</th>
					<td>
						<h:outputText value="#{editalMonitoria.obj.numeroBolsas}"/>
					</td>
				</tr>

				<tr>
					<th width="20%" class="rotulo">Iniciar Recebimento em:</th>
					<td>
						<h:outputText value="#{editalMonitoria.obj.inicioSubmissao}"/>
					</td>
				</tr>

				<tr>
					<th width="20%" class="rotulo">Finalizar Recebimento em:</th>
					<td>
						<h:outputText value="#{editalMonitoria.obj.fimSubmissao}"/>
					</td>
				</tr>
	
				<tr>
					<th width="20%" class="rotulo">Valor Financiamento(R$):</th>
					<td>
						<h:outputText value="#{editalMonitoria.obj.valorFinanciamento}">
							<f:convertNumber pattern="#,##0.00"/>
						</h:outputText>
					</td>
				</tr>
	
				<tr>
					<th width="20%">Arquivo:</th>
					<td>
						<t:inputFileUpload id="uFile" value="#{editalMonitoria.file}" storage="file" 
						required="true" readonly="#{editalMonitoria.readOnly}" size="70"/>
					</td>
				</tr>
            </tbody>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Enviar Arquivo"	action="#{editalMonitoria.enviarArquivo}" />
						<h:commandButton value="Cancelar" action="#{editalMonitoria.cancelar}"	onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>