<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Resultado Sint�tico da Avalia��o Institucional</h2>

	<a4j:keepAlive beanName="relatorioAvaliacaoMBean"></a4j:keepAlive>

	<div class="descricaoOperacao">
		Informe um Ano/Per�odo para consultar o Relat�rio Sint�tico da Avalia��o Institucional.
		Opcionalmente, selecione perguntas que gostaria de ter as m�dias inclu�das no relat�rio. 
	</div>
	
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Informe os Dados para a Gera��o do Relat�rio</caption>
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
					<th class="required">Ano-Per�odo:</th>
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
						<td colspan="2" class="subFormulario">Detalhar M�dias das Perguntas</th>
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
						<h:commandButton value="Gerar Relat�rio" action="#{ relatorioAvaliacaoMBean.relatorioSintetico }" id="Botaorelatorio" /> 
						<h:commandButton value="Cancelar" action="#{ relatorioAvaliacaoMBean.cancelar }" id="BotaoCancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>