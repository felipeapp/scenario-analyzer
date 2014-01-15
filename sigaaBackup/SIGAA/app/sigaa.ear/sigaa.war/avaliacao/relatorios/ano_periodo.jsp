<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Resultado Sintético da Avaliação Institucional</h2>

	<a4j:keepAlive beanName="relatorioAvaliacaoMBean"></a4j:keepAlive>

	<div class="descricaoOperacao">
		Informe um Ano/Período para consultar o Relatório Sintético da Avaliação Institucional.
		Opcionalmente, selecione perguntas que gostaria de ter as médias incluídas no relatório. 
	</div>
	
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Informe os Dados para a Geração do Relatório</caption>
			<tbody>
				<c:if test="${acesso.diretorCentro}">
					<tr>
						<th width="30%" class="required">Departamento:</th>
						<td>
							<h:selectOneMenu
									id="departamentoSelect"
								value="#{relatorioAvaliacaoMBean.idUnidade}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{relatorioAvaliacaoMBean.unidadesCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<c:if test="${acesso.chefeDepartamento && !acesso.diretorCentro}">
					<tr>
						<th width="30%" class="required">Departamento:</th>
						<td>
							<h:outputText value="#{relatorioAvaliacaoMBean.unidade.nome}"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="required">Ano-Período:</th>
					<td>
						<h:selectOneMenu
							id="anoPeriodo"
							value="#{relatorioAvaliacaoMBean.anoPeriodo}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioAvaliacaoMBean.anoPeriodoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<c:if test="${acesso.avaliacao }" >
					<tr>
						<td colspan="2" class="subFormulario">Detalhar Médias das Perguntas</th>
					</tr>
					<tr>
						<td colspan="2">
							<h:selectManyCheckbox value="#{relatorioAvaliacaoMBean.perguntasSelecionadas}"
							layout="pageDirection">
								<f:selectItems value="#{relatorioAvaliacaoMBean.perguntasComboBox}"/>
							</h:selectManyCheckbox>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{ relatorioAvaliacaoMBean.relatorioSintetico }" id="Botaorelatorio" /> 
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