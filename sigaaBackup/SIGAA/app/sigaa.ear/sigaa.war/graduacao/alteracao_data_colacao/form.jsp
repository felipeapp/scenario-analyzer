<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
	<h2><ufrn:subSistema /> > Alterar Dados de Saída do Aluno</h2>
	<a4j:keepAlive beanName="alteracaoDataColacao"></a4j:keepAlive>
		<table class="formulario" width="70%">
			<caption class="formulario">
			Dados da Alteração
			</caption>
			<tr>
				<th><b>Matrícula:</b></th>
				<td>
					<h:outputText value="#{alteracaoDataColacao.obj.discente.matricula}"/>
				</td>
			</tr>
			<tr>
				<th><b>Aluno:</b></th>
				<td>
					<h:outputText value="#{alteracaoDataColacao.obj.discente.pessoa.nome}"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano-Período de Saída:</th>
				<td>
					<h:inputText id="ano" value="#{alteracaoDataColacao.obj.anoReferencia}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/> - 
					<h:inputText id="periodo" value="#{alteracaoDataColacao.obj.periodoReferencia}" size="1" maxlength="1" onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Data de Colação de Grau:</th>
				<td>
					<t:inputCalendar id="data"
						value="#{alteracaoDataColacao.obj.discente.dataColacaoGrau}"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
						
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{alteracaoDataColacao.confirmButton}" action="#{alteracaoDataColacao.alterarData}" id="btnAlterar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoDataColacao.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
