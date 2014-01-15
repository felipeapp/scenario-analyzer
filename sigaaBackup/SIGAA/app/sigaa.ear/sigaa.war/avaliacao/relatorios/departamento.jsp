<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Resultado Sintético da Avaliação Institucional</h2>

	<div class="descricaoOperacao">
		Selecione um Departamento e informe um Ano/Período para consultar o Relatório Sintético da Avaliação Institucional.
		<c:if test="${acesso.avaliacao }">Opcionalmente, selecione perguntas que gostaria de ter as médias incluídas no relatório.</c:if> 
	</div>

	<h:form id="form">
		<a4j:keepAlive beanName="relatorioAvaliacaoMBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Informe os Dados para a Geração do Relatório</caption>
			<tbody>
				<tr>
					<th width="30%" class="required">Departamento:</th>
					<td>
						<h:selectOneMenu
							id="escolha_relatorio"
							value="#{relatorioAvaliacaoMBean.idUnidade}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allDepartamentoUnidAcademicaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Ano-Período:</th>
					<td>
						<h:selectOneMenu
							id="anoPeriodo"
							onchange="submit()"
							valueChangeListener="#{relatorioAvaliacaoMBean.carregaPerguntasAnoPeriodo}"
							value="#{relatorioAvaliacaoMBean.anoPeriodo}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.anoPeriodoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<c:if test="${acesso.avaliacao }">
					<tr>
						<td colspan="2" class="subFormulario">Incluir no Relatório as Médias das Perguntas</th>
					</tr>
					<tr>
						<td colspan="2">
							<h:selectManyCheckbox value="#{relatorioAvaliacaoMBean.perguntasSelecionadas}"
							layout="pageDirection" id="perguntasSelecionadas">
								<f:selectItems value="#{relatorioAvaliacaoMBean.perguntasComboBox}"/>
							</h:selectManyCheckbox>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{ relatorioAvaliacaoMBean.relatorioPublico }" id="botaoRelatorio" /> 
						<h:commandButton value="Cancelar" action="#{ relatorioAvaliacaoMBean.cancelar }" id="botaoCancelar" onclick="#{confirm}" />
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