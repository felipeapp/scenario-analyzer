<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema/> > Relatório de Turmas não Consolidadas</h2>
	<h:form id="form">
	<h:inputHidden value="#{relatorioTurma.nivel}"/>
	<h:outputText value="#{relatorioTurma.create}" />
		<table class="formulario" width="50%">
			<caption class="formulario">Parâmetros do Relatório</caption>
			<tbody>
				<tr>
					<td align="right"><label>Ano-Período:</label><span class="required">&nbsp;</span></td>
						<td align="left"><h:inputText value="#{relatorioTurma.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" />
						- <h:inputText value="#{relatorioTurma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);"/>
						</td>
				</tr>
				<tr>
				  <th>Incluir Ead:</th>
					<td align="left">
						<h:selectOneRadio value="#{relatorioTurma.ead}" id="ead">
							<f:selectItem itemLabel="Sim" itemValue="true" />
							<f:selectItem itemLabel="Não" itemValue="false" />
						</h:selectOneRadio>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioTurma.relatorioTurmaNaoConsolidada}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancelar"/>
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