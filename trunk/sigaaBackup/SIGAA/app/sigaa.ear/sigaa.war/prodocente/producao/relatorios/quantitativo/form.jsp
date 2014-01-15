<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" />
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	<h2><ufrn:subSistema /> > Relatório Quantitativo de Produção Acadêmica por Unidade</h2>
	<br>

	<h:form id="form">
		<h:outputText value="#{prodQuantitativo.create}" />
		<table class="formulario" width="100%">
			<caption>Geração de Relatório Quantitativo de Produção Acadêmica</caption>
			<tbody>
				<tr>
					<td> Unidade: </td>
					<td style="width: 10px;">
						<h:selectOneMenu id="unidade" value="#{prodQuantitativo.unidade.id}">
							<f:selectItem itemLabel="-- CONSIDERAR TODAS AS UNIDADES -- " itemValue="-1"/>
							<f:selectItems value="#{unidade.allDeptosEscolasCoordCursosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td>Período a considerar:</td>
					<td>
						<h:selectOneMenu id="mesInicial" value="#{prodQuantitativo.mesInicial}">
							<f:selectItems value="#{prodQuantitativo.meses}" />
						</h:selectOneMenu> 					
						<h:selectOneMenu id="anoInicial" value="#{prodQuantitativo.anoInicial}">
							<f:selectItems value="#{prodQuantitativo.anos}" />
						</h:selectOneMenu> 
						a <h:selectOneMenu id="mesFinal" value="#{prodQuantitativo.mesFinal}">
							<f:selectItems value="#{prodQuantitativo.meses}" />
						</h:selectOneMenu> 
						<h:selectOneMenu id="anoFinal" value="#{prodQuantitativo.anoFinal}">
							<f:selectItems value="#{prodQuantitativo.anos}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td align="right"><h:selectBooleanCheckbox id="detalhar" value="#{prodQuantitativo.detalharDocentes}" styleClass="noborder" /> </td>
					<td colspan="2">Detalhar Quantitativos para Cada Docente da Unidade:</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Gerar Relatório" action="#{prodQuantitativo.gerarRelatorio}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>