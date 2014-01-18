<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form">

<h2> <ufrn:subSistema /> > ${relatorioBolsasStrictoBean.titulo} </h2>

<a4j:keepAlive beanName="relatorioBolsasStrictoBean"></a4j:keepAlive>

<table class="formulario" width="50%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th class="required">Programa: </th>
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
		<th class="required">Período: </th>
		<td>
			de <t:inputCalendar renderAsPopup="true"
					renderPopupButtonAsImage="true" size="10" maxlength="10"
					onkeypress="return formataData(this,event)"
					disabled="#{relatorioBolsasStrictoBean.readOnly}"
					value="#{relatorioBolsasStrictoBean.dataInicial}"
					id="inicio" /> até <t:inputCalendar
					renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
					maxlength="10" onkeypress="return formataData(this,event)"
					disabled="#{relatorioBolsasStrictoBean.readOnly}"
					value="#{relatorioBolsasStrictoBean.dataFinal}"
					id="fim" />
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioBolsasStrictoBean.gerarRelatorioBolsistas}" value="Gerar Relatório" id="btnGerarRelatorio"/>
			<h:commandButton action="#{relatorioBolsasStrictoBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>