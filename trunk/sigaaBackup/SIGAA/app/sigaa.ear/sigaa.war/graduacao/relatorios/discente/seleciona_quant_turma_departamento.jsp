<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema/> > Relatório Quantitativos das Turmas não Consolidadas</h2>
	<h:form id="form">
	<h:outputText value="#{realtorioDiscente.create}" />
		<table class="formulario" width="50%">
			<caption class="formulario">Parâmetros do Relatório</caption>
			<tbody>
				<tr>
					<td align="right"><label>Ano-Período:</label><span class="required">&nbsp;</span></td>
						<td align="left"><h:inputText value="#{relatorioDiscente.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
						- <h:inputText value="#{relatorioDiscente.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						</td>
				</tr>
				<tr>
				  <th>Incluir Ead:</th>
					<td align="left">
						<h:selectOneRadio value="#{relatorioDiscente.todos}" id="todos">
							<f:selectItem itemLabel="Sim" itemValue="true" />
							<f:selectItem itemLabel="Não" itemValue="false" />
						</h:selectOneRadio>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioDiscente.relatorioQuantitativoTurmasDepartamento}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" onclick="#{confirm}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
  <br />
  <center>
	<html:img page="/img/required.gif" style="vertical-align: top;" />
	<span class="fontePequena">Campos de preenchimento obrigatório.</span>
	<br>
	<br>
  </center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>