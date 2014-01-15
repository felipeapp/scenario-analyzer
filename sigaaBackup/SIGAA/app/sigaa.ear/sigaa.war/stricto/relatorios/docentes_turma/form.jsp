<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{relatorioDocentesTurmaBean.create}" />
<h2> <ufrn:subSistema /> &gt; Relatório de docentes por turma </h2>

<h:form id="form">
<table class="formulario" style="width: 100%">
	<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th width="30%">Programa: </th>
		<td>
			<c:if test="${empty relatorioDocentesTurmaBean.programaStricto}">
				<h:selectOneMenu id="programa" value="#{relatorioDocentesTurmaBean.programa.id}">
					<f:selectItem itemLabel=" Todos " itemValue="-1"/>
					<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
				</h:selectOneMenu>
			</c:if>
			<c:if test="${not empty relatorioDocentesTurmaBean.programaStricto}">
				${ relatorioDocentesTurmaBean.programa.nome }
			</c:if>
		</td>
	</tr>
	
	<tr>
		<th>Ano-Período:</th>
		<td>
			<h:inputText value="#{relatorioDocentesTurmaBean.ano}" id="txtAno" maxlength="4" size="5" onkeyup="return formatarInteiro(this);"/> -
			<h:inputText value="#{relatorioDocentesTurmaBean.periodo}" id="txtPeriodo" maxlength="1" size="2" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioDocentesTurmaBean.gerarRelatorio}" value="Emitir Relatório" id="btnGerar"/>
			<h:commandButton action="#{relatorioDocentesTurmaBean.cancelar}" value="Cancelar" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>