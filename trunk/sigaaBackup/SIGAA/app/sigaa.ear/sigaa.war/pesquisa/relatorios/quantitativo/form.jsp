<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" />
	
	<h2> <ufrn:subSistema /> &gt; Relatório Quantitativos de Pesquisa</h2>
	<br>

	<h:form id="form">
		<h:outputText value="#{prodQuantitativo.create}" />
		<table class="formulario" width="600">
			<caption>Geração de Relatório Quantitativos de Pesquisa</caption>
			<tbody>
				<tr>
					<td> </td>
					<td> Unidade: </td>
					<td>
						<h:selectOneMenu id="unidade" value="#{quantPesquisa.unidade.id}">
							<f:selectItem itemLabel="-- CONSIDERAR TODAS AS UNIDADES -- " itemValue="-1"/>
							<f:selectItems value="#{unidade.allDeptosEscolasCoordCursosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td> </td>
					<td>Período a considerar:</td>
					<td>
						<h:selectOneMenu id="mesInicial" value="#{quantPesquisa.mesInicial}">
							<f:selectItems value="#{prodQuantitativo.meses}" />
						</h:selectOneMenu> 
						<h:selectOneMenu id="anoInicial" value="#{quantPesquisa.anoInicial}">
							<f:selectItems value="#{prodQuantitativo.anos}" />
						</h:selectOneMenu> 
						a <h:selectOneMenu id="mesFinal" value="#{quantPesquisa.mesFinal}">
							<f:selectItems value="#{prodQuantitativo.meses}" />
						</h:selectOneMenu>
						<h:selectOneMenu id="anoFinal" value="#{quantPesquisa.anoFinal}">
							<f:selectItems value="#{prodQuantitativo.anos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Gerar Relatório" action="#{quantPesquisa.gerarRelatorio}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
