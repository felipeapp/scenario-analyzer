<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório de Vagas Ofertadas</h2>
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioVagasOfertadas.create}" />
		<table class="formulario" width="80%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{relatorioVagasOfertadas.filtroAno}" id="checkAno" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkAno" onclick="$('formConsulta:checkAno').checked = !$('formConsulta:checkAno').checked;">Ano:</label>
					</td>
					<td>
						<h:selectOneMenu id="Ano" value="#{relatorioVagasOfertadas.oferta.ano}" onfocus="$('formConsulta:checkAno').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioVagasOfertadas.anosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{relatorioVagasOfertadas.filtroFormaIngresso}" id="checkForma" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkForma" onclick="$('formConsulta:checkForma').checked = !$('formConsulta:checkForma').checked;">Forma de Ingresso:</label>
					</td>
					<td>
						<h:selectOneMenu id="formaIngresso" value="#{relatorioVagasOfertadas.formaIngresso.id}" onfocus="$('formConsulta:checkForma').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioVagasOfertadas.allTipoEntradaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioVagasOfertadas.gerarRelatorioVagasOfertadas}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>