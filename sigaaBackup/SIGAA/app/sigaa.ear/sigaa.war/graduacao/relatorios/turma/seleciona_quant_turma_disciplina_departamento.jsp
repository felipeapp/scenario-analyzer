<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema/> > Relatório Quantitativos das Turmas e Disciplinas por Departamento</h2>
	<h:form id="form">
	<div class="descricaoOperacao">
		Este relatório tem como objetivo contabilizar os totais de componentes curriculares formadores de turmas (disciplinas, módulos e atividades coletivas) 
		que foram ofertados no ano/período informados. Esses totais serão agrupados pela unidade responsável pelo componente e seus respectivos centros, quando se aplicar.
	</div>
	<h:outputText value="#{relatorioTurma.create}" />
		<table class="formulario" width="50%">
			<caption class="formulario">Parâmetros do Relatório</caption>
			<tbody>
				<tr align="center">
					<td align="right" width="50%">
						<label class="required">Ano-Período:</label>
					</td>
					<td align="left" width="50%">
						<h:inputText value="#{relatorioTurma.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" />
						- <h:inputText value="#{relatorioTurma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioTurma.relatorioQuantTurmaDisciplinaPorDepto}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" onclick="#{confirm}" id="btnCancelar"/>
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