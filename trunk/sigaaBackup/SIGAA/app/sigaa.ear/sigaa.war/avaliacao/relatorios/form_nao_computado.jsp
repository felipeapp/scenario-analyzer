<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Relat�rio Quantitativo das Avalia��os N�o Computadas</h2>

	<div class="descricaoOperacao">
		Este relat�rio lista as quantidades de Avalia��es Institucionais n�o computadas no c�lculo das m�dias por docente. 
	</div>
	
	<h:form id="form">

		<a4j:keepAlive beanName="relatorioAvaliacaoMBean"/>
		<table class="formulario" width="80%">
			<caption>Selecione os Par�metros do Relat�rio</caption>
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
					<th class="required">Ano-Per�odo:</th>
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
					<th class="required">Formul�rio:</th>
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
						<h:commandButton value="Gerar Relat�rio" action="#{ relatorioAvaliacaoMBean.relatorioQuantitativoNaoComputado }" id="Botaorelatorio" /> 
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