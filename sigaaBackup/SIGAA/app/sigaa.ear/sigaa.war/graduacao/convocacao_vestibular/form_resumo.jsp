<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="resumoConvocacaoVestibularMBean"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Consultar Resumo de Convocação de Candidatos </h2>
<div class="descricaoOperacao">
	<p> Esta operação permite consultar resumos de convocações de candidatos realizadas anteriormente. </p>
</div>

<h:form id="form">

<table class="formulario" width="80%">
	<caption>Dados da Convocação</caption>
	<tr>
		<th class="obrigatorio">Processo Seletivo: </th>
		<td width="70%" style="padding-left: 5px;">
			<a4j:region>
				<h:selectOneMenu id="processoSeletivo" immediate="true"
					value="#{resumoConvocacaoVestibularMBean.idProcessoSeletivo}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					<a4j:support event="onchange" onsubmit="true"
						reRender="form" 
						action="#{resumoConvocacaoVestibularMBean.carregarChamadas}" />
				</h:selectOneMenu>
				<a4j:status>
					<f:facet name="start">
						<h:graphicImage value="/img/indicator.gif">Carregando as chamadas para o processo seletivo escolhido...</h:graphicImage>
					</f:facet>
				</a4j:status>
			</a4j:region>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Chamada: </th>
		<td width="70%">
			<h:panelGrid id="fasePanel">
				<h:selectOneMenu id="fase" value="#{resumoConvocacaoVestibularMBean.idChamada}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{resumoConvocacaoVestibularMBean.chamadasCombo}" />
				</h:selectOneMenu>
			</h:panelGrid>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Resumo da Convocação" action="#{resumoConvocacaoVestibularMBean.buscar}"/>
				<h:commandButton value="Cancelar" action="#{ resumoConvocacaoVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>