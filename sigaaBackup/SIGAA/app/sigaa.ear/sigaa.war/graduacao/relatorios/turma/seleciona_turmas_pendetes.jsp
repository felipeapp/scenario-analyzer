<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Turmas Pendentes de Consolidação ou definição de docente</h2>
	<h:form>
		<table class="formulario" width="40%">
			<caption>Relatório de Turmas Pendentes</caption>
			<tr>
				<th width="15%" class="required">Nível</th>
				<td>
					<h:selectOneMenu id="nivel" value="#{relatorioTurmasPendentes.nivel}" style="width:90%">
						<f:selectItems value="#{relatorioTurmasPendentes.comboNiveis}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="15%" class="required">Início</th>
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
						<h:commandButton action="#{relatorioTurmasPendentes.gerarRelatorio}" value="Gerar Relatório" />
						<h:commandButton action="#{relatorioTurmasPendentes.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" />
			<span class="fontePequena">Campos de preenchimento obrigatório.</span>
			<br>
			<br>
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>