<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório Quantitativo de Resumos CIC</h2>
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioResumosCic.create}" />
		<table class="formulario">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{relatorioResumosCic.filtroCongresso}" id="checkCongresso" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkCongresso" onclick="$('formConsulta:checkCongresso').checked = !$('formConsulta:checkCongresso').checked;">Congresso de Inicição Científica:</label>
					</td>
					<td>
						<h:selectOneMenu id="Congresso" value="#{relatorioResumosCic.congresso.id}" onfocus="$('formConsulta:checkCongresso').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{congressoIniciacaoCientifica.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioResumosCic.gerarRelatorioResumosCic}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioResumosCic.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>