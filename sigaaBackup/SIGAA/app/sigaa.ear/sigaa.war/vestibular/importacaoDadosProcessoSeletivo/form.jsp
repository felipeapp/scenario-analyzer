<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Importação de dados dos candidatos do Vestibular</h2>

	<h:form id="form" enctype="multipart/form-data">
		
	<table class=formulario width="100%">
		<caption class="listagem">Dados do Vestibular</caption>
		<tr>
		<th class="obrigatorio">Processo Seletivo Vestibular:</th>
			<td>
				<h:selectOneMenu id="selectPSVestibular" value="#{processoImportacaDadosProcessoSeletivo.processoSeletivo.id}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems id="listaAnosReferencia" value="#{processoImportacaDadosProcessoSeletivo.processoSeletivoVestibularCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Importar dados de candidatos do XML:</th>
			<td> <t:inputFileUpload value="#{processoImportacaDadosProcessoSeletivo.xml}" style="width:95%;"/> </td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
				<h:commandButton value="Importar Dados" action="#{processoImportacaDadosProcessoSeletivo.cadastrar}" id="processar" /> 
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{processoImportacaDadosProcessoSeletivo.cancelar}" immediate="true" id="cancelar" /></td>
			</tr>
		</tfoot>
	</table>
	<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>