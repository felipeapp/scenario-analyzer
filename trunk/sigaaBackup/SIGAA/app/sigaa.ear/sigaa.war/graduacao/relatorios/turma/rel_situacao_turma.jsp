<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema/> > Relatório Sintético de Turmas por Situação</h2>
	<h:form id="form">
	<h:inputHidden value="#{relatorioTurma.nivel}"/>
	<h:outputText value="#{relatorioTurma.create}" />
		<table class="formulario" width="50%" border="1">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<td align="right"><label>Ano-Período</label><span class="required">&nbsp;</span></td>
						<td align="left"><h:inputText value="#{relatorioTurma.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" />
						- <h:inputText value="#{relatorioTurma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);"/>
						</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{relatorioTurma.situacaoTurma}" id="btnSolicitar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" id="btnCancelar"/>
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