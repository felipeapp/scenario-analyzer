<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Enviar Arquivo do Edital</h2>
	<br>
	<h:form id="form" enctype="multipart/form-data">

		<h:inputHidden value="#{editalPesquisaMBean.confirmButton}" />
		<h:inputHidden value="#{editalPesquisaMBean.obj.id}" />

		<table id="tableArquivo" class="formulario" width="100%">
			<caption class="listagem">Enviar Arquivo para o Edital</caption>
            <tbody>
				<tr>
					<th width="20%"><b>Descrição:</b></th>
					<td><h:outputText value="#{editalPesquisaMBean.obj.descricao}" />
					</td>
				</tr>
	
				<tr>
					<th class="required" width="20%">Arquivo:</th>
					<td>
						<t:inputFileUpload id="uFile" value="#{editalPesquisaMBean.arquivoEdital}" storage="file" readonly="#{editalPesquisaMBean.readOnly}" size="70"/>
					</td>
				</tr>
            </tbody>

			<tfoot>
				<tr>
					<td colspan="2">
					   	<h:commandButton id="cadastrar" value="#{editalPesquisaMBean.confirmButton}" action="#{editalPesquisaMBean.cadastrar}" />
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{editalPesquisaMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>