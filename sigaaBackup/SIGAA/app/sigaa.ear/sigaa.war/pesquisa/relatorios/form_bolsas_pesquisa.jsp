<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ relatorioBolsasPesquisaMBean.create }" />

<h2> <ufrn:subSistema /> &gt; Relatório Quantitativo de Bolsas de Pesquisa Ativas  </h2>

	<h:form id="filtroBolsas">
		<table class="formulario" width="75%">
		
			<caption>Selecione os filtros para emissão do relatório</caption>
			
			<tr>
				<th align="left">Centro:</th>
				<td>
					<h:selectOneMenu id="filtarPor" value="#{relatorioBolsasPesquisaMBean.filtrarPor}">
						<f:selectItem itemLabel="-- TODOS --" itemValue="-1"/>
						<f:selectItems value="#{relatorioBolsasPesquisaMBean.centros}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th align="left">Ordenar por:</th>
				<td>
					<h:selectOneMenu id="ordenarPor" value="#{relatorioBolsasPesquisaMBean.ordenarPor}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItem itemLabel="Centro" itemValue="1"/>
						<f:selectItem itemLabel="Departamento" itemValue="2"/>
						<f:selectItem itemLabel="Docente" itemValue="3"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="3" class="subFormulario"> Tipo de bolsas </td>
			</tr>
			
			<tr>
				<th> </th>
				<td colspan="2" id="tiposBolsa">
					<t:selectManyCheckbox value="#{relatorioBolsasPesquisaMBean.tiposBolsaSelecionados}" layoutWidth="4" layout="pageDirection">
						<f:selectItems value="#{relatorioBolsasPesquisaMBean.tiposBolsa}"/> 
					</t:selectManyCheckbox>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar relatório" action="#{relatorioBolsasPesquisaMBean.gerarRelatorio}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioBolsasPesquisaMBean.cancelar}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
