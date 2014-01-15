<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" />
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	<h2><ufrn:subSistema /> &gt; Relatório para Avaliação dos Grupos de Pesquisa</h2>
	<br>

	<h:form id="form">
		<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.create}" />
		<table class="formulario" width="600">
			<caption>Geração de Relatório para Avaliação dos Grupos de Pesquisa</caption>
			<tbody>
				<tr>
					<td>Período a considerar:</td>
					<td>
						<h:selectOneMenu id="mesInicial" value="#{relatorioAvaliacaoGrupoPesquisaMBean.mesInicial}">
							<f:selectItems value="#{relatorioAvaliacaoGrupoPesquisaMBean.meses}" />
						</h:selectOneMenu> 					
						<h:selectOneMenu id="anoInicial" value="#{relatorioAvaliacaoGrupoPesquisaMBean.anoInicial}">
							<f:selectItems value="#{relatorioAvaliacaoGrupoPesquisaMBean.anos}" />
						</h:selectOneMenu> 
						a <h:selectOneMenu id="mesFinal" value="#{relatorioAvaliacaoGrupoPesquisaMBean.mesFinal}">
							<f:selectItems value="#{relatorioAvaliacaoGrupoPesquisaMBean.meses}" />
						</h:selectOneMenu> 
						<h:selectOneMenu id="anoFinal" value="#{relatorioAvaliacaoGrupoPesquisaMBean.anoFinal}">
							<f:selectItems value="#{relatorioAvaliacaoGrupoPesquisaMBean.anos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{relatorioAvaliacaoGrupoPesquisaMBean.gerarRelatorioTodosGrupos}" />
						<h:commandButton value="Cancelar" action="#{relatorioAvaliacaoGrupoPesquisaMBean.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>