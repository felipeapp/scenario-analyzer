<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Turmas Pendentes de Consolida��o ou defini��o de docente</h2>
	<h:form>
		<table class="formulario" width="40%">
			<caption>Relat�rio de Turmas Pendentes</caption>
			<tr>
				<th width="15%" class="required">N�vel</th>
				<td>
					<h:selectOneMenu id="nivel" value="#{relatorioTurmasPendentes.nivel}" style="width:90%">
						<f:selectItems value="#{relatorioTurmasPendentes.comboNiveis}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="15%" class="required">In�cio</th>
				<td>
					<h:inputText id="inicio" value="#{relatorioTurmasPendentes.anoInicio}" size="4" maxlength="4" />
				</td>
			</tr>			
			<tr>
				<th width="15%" class="required">Fim</th>
				<td>
					<h:inputText id="fim" value="#{relatorioTurmasPendentes.anoFim}" size="4" maxlength="4" />
				</td>
			</tr>			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton action="#{relatorioTurmasPendentes.gerarRelatorio}" value="Gerar Relat�rio" />
						<h:commandButton action="#{relatorioTurmasPendentes.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" />
			<span class="fontePequena">Campos de preenchimento obrigat�rio.</span>
			<br>
			<br>
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>