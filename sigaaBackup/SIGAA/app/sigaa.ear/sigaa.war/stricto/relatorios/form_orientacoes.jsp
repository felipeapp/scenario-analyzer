<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioOrientacoes.create}" />

<h2> <ufrn:subSistema /> > Relatório de Orientações </h2>

<h:form id="form">
<table class="formulario" style="width: 70%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
	<c:if test="${relatorioOrientacoes.portalCoordenadorStricto}">
		<th><b>Programa:</b></th>
	</c:if>
	<c:if test="${acesso.ppg and relatorioOrientacoes.portalPpg}">
		<th>Programa:</th>
	</c:if>
		<td>
		<c:if test="${acesso.ppg and relatorioOrientacoes.portalPpg }">
			<h:selectOneMenu id="programa" value="#{relatorioOrientacoes.unidade.id}">
				<f:selectItem itemLabel=" Todos " itemValue="-1"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</c:if>
		<c:if test="${relatorioOrientacoes.portalCoordenadorStricto}">
			${relatorioOrientacoes.programaStricto.nome}
		</c:if>
		</td>
	</tr>
	
	<c:if test="${relatorioOrientacoes.menuTecnico}">
		<th class="obrigatorio">Curso: </th>
		<td>
			<h:selectOneMenu id="curso" value="#{relatorioOrientacoes.curso.id}" >
				<f:selectItem itemLabel=" >> Selecione " itemValue="-1"/>
				<f:selectItems value="#{curso.allCursoTecnicoCombo}"/>
			</h:selectOneMenu>
		</td>
	</c:if>
	
	<tr>
		<th>Status da Orientação: </th>
		<td>
			<h:selectOneMenu id="statusOrientacao" value="#{relatorioOrientacoes.ativo}">
				<f:selectItem itemLabel=" -- Todos -- " itemValue=""/>
				<f:selectItem itemLabel="Ativos" itemValue="true"/>
				<f:selectItem itemLabel="Finalizados" itemValue="false"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th>Status do Discente: </th>
		<td>
			<h:selectOneMenu id="situacao" value="#{relatorioOrientacoes.statusDiscente.id}">
				<f:selectItem itemLabel=" -- Todos -- " itemValue="0"/>
				<f:selectItems value="#{statusDiscente.allCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th>Tipo: </th>
		<td>
			<h:selectOneRadio value="#{relatorioOrientacoes.sintetico}" id="tipo">
				<f:selectItem itemLabel="Analítico" itemValue="false" />
				<f:selectItem itemLabel="Sintético" itemValue="true" />
			</h:selectOneRadio>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioOrientacoes.gerarRelatorio}" value="Emitir Relatório" id="btnGerar"/>
			<h:commandButton action="#{relatorioOrientacoes.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>
<div class="obrigatorio">Campo de preenchimento obrigatório	</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>