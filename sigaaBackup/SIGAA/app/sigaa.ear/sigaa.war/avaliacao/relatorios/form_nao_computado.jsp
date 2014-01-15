<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Relatório Quantitativo das Avaliaçãos Não Computadas</h2>

	<div class="descricaoOperacao">
		Este relatório lista as quantidades de Avaliações Institucionais não computadas no cálculo das médias por docente. 
	</div>
	
	<h:form id="form">

		<a4j:keepAlive beanName="relatorioAvaliacaoMBean"/>
		<table class="formulario" width="80%">
			<caption>Selecione os Parâmetros do Relatório</caption>
			<tbody>
				<tr>
					<th width="30%">Centro:</th>
					<td>
						<h:selectOneMenu
							id="escolha_relatorio"
							value="#{relatorioAvaliacaoMBean.idUnidade}">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
							<f:selectItems value="#{unidade.allCentroCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Ano-Período:</th>
					<td>
						<h:selectOneMenu
							id="anoPeriodo"
							value="#{relatorioAvaliacaoMBean.anoPeriodo}"
							valueChangeListener="#{relatorioAvaliacaoMBean.carregaFormularios}"
							onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.anoPeriodoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Formulário:</th>
					<td>
						<h:selectOneMenu
							id="formulario"
							value="#{relatorioAvaliacaoMBean.obj.formulario.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.formularioCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{ relatorioAvaliacaoMBean.relatorioQuantitativoNaoComputado }" id="Botaorelatorio" /> 
						<h:commandButton value="Cancelar" action="#{ relatorioAvaliacaoMBean.cancelar }" id="BotaoCancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>