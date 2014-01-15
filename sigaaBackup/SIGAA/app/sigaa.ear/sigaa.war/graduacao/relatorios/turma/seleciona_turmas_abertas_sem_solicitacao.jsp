<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório De Turmas Abertas sem Solicitação</h2>
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioTurma.create}" />
		<table class="formulario" width="34%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<td align="right" colspan="2"><label>Ano-Período:</label><span class="required">&nbsp;</span></td>
						<td align="left"><h:inputText value="#{relatorioTurma.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" />
						. <h:inputText value="#{relatorioTurma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);"/>
						</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{relatorioTurma.filtroSituacaoTurma}" id="checkSituacao" styleClass="noborder"/>
					</th>
					<td width="130px">
						<label for="checkSituacao" onclick="$('formConsulta:Situacao').checked = !$('formConsulta:checkSituacao').checked;">Situação da Turma:</label>
					</td>
					<td>
						<h:selectOneMenu id="situacao" value="#{relatorioTurma.situacaoTurma.id}"  onchange="$('formConsulta:checkSituacao').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
							<f:selectItems value="#{situacaoTurma.allSituacoesCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th align="left">
						<h:selectBooleanCheckbox value="#{relatorioTurma.filtroContabilizarEnsinoDistancia}" id="checkEaD" styleClass="noborder"/>
					</th>
					<td align="left" colspan="2">
						<label for="checkEaD" onclick="$('formConsulta:EaD').checked = !$('formConsulta:checkEaD').checked;">Contabilizar Turmas de Ensino a Distância</label>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioTurma.relatorioTurmaAbertaSemSolicitacao}" id="btnRelatorioAberto"/> 
						<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}"/>
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