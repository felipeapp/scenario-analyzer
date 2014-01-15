<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Relatórios para a Avaliação Institucional</h2>

	<h:messages showDetail="true" />
	<h:outputText value="#{relatorioAvaliacaoMBean.create}" />

	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Relatórios para a Avaliação Institucional</caption>
			<tbody>
					<tr>
						<th>Tipo de Relatório:</th>
						<td colspan="1" width="70%">
							<h:selectOneMenu id="escolha_relatorio" value="#{relatorioAvaliacaoMBean.escolhaRelatorio}">
							  <f:selectItem itemValue="1"
								itemLabel="Quantidade de Trancamentos no Período" />
							  <f:selectItem itemValue="2"
								itemLabel="Quantidade de Trancamentos por Departamento / Centro" />
							  <f:selectItem itemValue="3"
								itemLabel="Quantitativo de Alunos Reprovados por Média ou por Falta" />
							</h:selectOneMenu>
						</td>
					</tr> 
					<tr>
						<th width="25%"> Ano-Período: </th>
						<td>
							<h:inputText value="#{relatorioAvaliacaoMBean.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/> .
							<h:inputText value="#{relatorioAvaliacaoMBean.periodo}" id="periodo" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						</td>
					</tr>
			</tbody>
			<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Gerar Relatório" action="#{ relatorioAvaliacaoMBean.relatorio }"
							id="Botaorelatorio" /> 
							<h:commandButton value="Cancelar" action="#{ relatorioAvaliacaoMBean.cancelar }" 
							id="BotaoCancelar" onclick="#{confirm}" />
						</td>
					</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>