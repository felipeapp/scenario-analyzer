<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Relatório de Entrada de Notas</h2>
	<h:form id="formRelatorio">
		<a4j:keepAlive beanName="relatorioEntradaNotasMBean" />
		<table class="formulario" width="50%">
			<caption>Dados do Relatório</caption>
			<tr>
				<th class="obrigatorio">Ano-Período:</th>
				<td><h:inputText id="txtAno"
					value="#{relatorioEntradaNotasMBean.ano}" size="4" maxlength="4"
					onkeyup="formatarInteiro(this);" /> -<h:inputText id="txtPeriodo"
					value="#{relatorioEntradaNotasMBean.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this);" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Módulo:</th>
				<td><h:selectOneMenu id="selectModulo"
					value="#{relatorioEntradaNotasMBean.modulo.id}">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{relatorioEntradaNotasMBean.modulosCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center"><h:commandButton
						id="btnGerarRelatorio" value="Gerar Relatório"
						action="#{relatorioEntradaNotasMBean.buscarTurmasTecnico}" /> <h:commandButton
						id="btnCancelar" value="Cancelar"
						action="#{relatorioEntradaNotasMBean.cancelar}" immediate="true"
						onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>