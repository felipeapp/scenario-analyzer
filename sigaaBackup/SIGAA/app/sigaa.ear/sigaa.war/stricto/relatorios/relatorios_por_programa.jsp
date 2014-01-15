<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioBolsasStrictoBean.create}" />

<h2> <ufrn:subSistema /> > ${relatorioBolsasStrictoBean.titulo} </h2>

<h:form id="form">
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th width="30%">Programa: </th>
		<td>
			<c:if test="${relatorioBolsasStrictoBean.passivelSelecionarTodas}">
				<h:selectOneMenu id="programa" value="#{relatorioBolsasStrictoBean.unidade.id}">
					<f:selectItem itemLabel=" Todos " itemValue="-1"/>
					<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
				</h:selectOneMenu>
			</c:if>
			<c:if test="${!relatorioBolsasStrictoBean.passivelSelecionarTodas}">
				${ relatorioBolsasStrictoBean.unidade.nome }
			</c:if>
		</td>
	</tr>
	
	<tr>
		<th class="required"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatorioBolsasStrictoBean.formato}" id="formatoRelBolsas">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioBolsasStrictoBean.gerarRelatorioExterno}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatorioBolsasStrictoBean.cancelar}" value="Cancelar"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>