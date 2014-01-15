<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Relatório de Docentes com Média Baixa por Pergunta</h2>

	<div class="descricaoOperacao">
		Selecione um Departamento, informe um Ano/Período, informe uma média e selecione uma ou mais perguntas para gerar o Relatório de Docentes com Média Baixa por Pergunta.
		Será listado o docente que obteve média inferior à informada em pelo menos uma das perguntas selecionadas. 
	</div>

	<h:form id="form">
		<a4j:keepAlive beanName="relatorioAvaliacaoMBean" />
		<table class="formulario" width="80%">
			<caption>Informe os Dados para a Geração do Relatório</caption>
			<tbody>
				<tr>
					<th width="30%">Departamento:</th>
					<td>
						<h:selectOneMenu
							id="escolha_relatorio"
							value="#{relatorioAvaliacaoMBean.idUnidade}">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
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
				<tr>
					<th class="required">Docentes com Média Abaixo de:</th>
					<td>
						<h:inputText value="#{relatorioAvaliacaoMBean.mediaMinima}" id="media" size="5" maxlength="4" style="text-align: right" onkeydown="return(formataValor(this, event, 1))">
							<f:convertNumber type="number" maxIntegerDigits="2" maxFractionDigits="1"/>
						</h:inputText>
					</td>
				</tr>
				<tr>
					<th><t:selectBooleanCheckbox id="mediaGeral" value="#{relatorioAvaliacaoMBean.mediaGeral}"/></th>
					<td>
						Incluir docentes que obtiveram médias gerais abaixo do valor informado acima. 
					</td>
				</tr>
				<c:if test="${ not empty relatorioAvaliacaoMBean.perguntasSelecionadas }" >
					<tr>
						<td class="subFormulario" colspan="2">Selecione uma ou mais Perguntas:<ufrn:help>Serão listados os docentes que obtiveram média inferior à indicada em pelo menos uma das perguntas.</ufrn:help></td>
					</tr>
					<tr>
						<td colspan="2">
							<h:selectManyCheckbox value="#{relatorioAvaliacaoMBean.perguntasSelecionadas}" layout="pageDirection" id="grupoPerguntas">
								<f:selectItems value="#{relatorioAvaliacaoMBean.perguntasComboBox}"/>
							</h:selectManyCheckbox>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{ relatorioAvaliacaoMBean.relatorioMediaBaixa }" id="Botaorelatorio" /> 
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