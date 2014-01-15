<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ relatorioResumoCotaMBean.create }" />

<h2> <ufrn:subSistema /> &gt; Relatório Quantitativo de Cotas de Bolsas </h2>

	<h:form id="filtro">
		<table class="formulario" width="75%">
		
			<caption>Selecione os filtros para emissão do relatório</caption>
			
			<tr>
				<th align="left" class="required">Edital:</th>
				<td>
					<h:selectOneMenu style="width:100%;" id="edital" value="#{relatorioResumoCotaMBean.editalSelecionado}">
						<f:selectItems value="#{relatorioResumoCotaMBean.editais}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="3" class="subFormulario"> Tipo de bolsas <html:img page="/img/required.gif" style="vertical-align: top;" /> </td>
			</tr>
			
			<tr>
				<th> </th>
				<td colspan="2" id="tiposBolsa">
					<t:selectManyCheckbox value="#{relatorioResumoCotaMBean.tiposBolsaSelecionados}" layoutWidth="2" layout="pageDirection">
						<f:selectItems value="#{relatorioResumoCotaMBean.tiposBolsa}"/> 
					</t:selectManyCheckbox>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar relatório" action="#{relatorioResumoCotaMBean.gerarRelatorio}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioResumoCotaMBean.cancelar}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
