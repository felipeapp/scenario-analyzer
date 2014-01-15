<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatoriosJasper.create}" />

<h2> <ufrn:subSistema /> &gt; ${relatoriosJasper.titulo} </h2>

<h:form id="form">
<a4j:keepAlive beanName="relatoriosJasper"/>
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<h:inputHidden value="#{relatoriosJasper.exibeDepartamento}"/>
	<h:inputHidden value="#{relatoriosJasper.exibeCentro}"/>
	<h:inputHidden value="#{relatoriosJasper.ocultaAnoPeriodo}"/>
	<h:inputHidden value="#{relatoriosJasper.relatorio}"/>
	<h:inputHidden value="#{relatoriosJasper.titulo}"/>

	<c:if test="${relatoriosJasper.exibeDepartamento}">
	<tr>
		<th class="required">Departamento: </th>
		<td>
			<h:selectOneMenu id="departamento" value="#{relatoriosJasper.departamento.id}" style="width:95%">
				<f:selectItem itemLabel=" -- SELECIONE UM DEPARTAMENTO -- " itemValue="0"/>
				<f:selectItems value="#{unidade.allDepartamentoCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	</c:if>
	
	<c:if test="${relatoriosJasper.exibeCentro}">
	<tr>
		<th>Centro/Unidade Acadêmica Especializada: </th>
		<td>
			<h:selectOneMenu id="centro" value="#{relatoriosJasper.departamento.id}" >
				<f:selectItem itemLabel=" -- TODOS -- " itemValue="-1"/>
				<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>	
	</c:if>

	<c:if test="${not relatoriosJasper.ocultaAnoPeriodo}">
	<tr>
		<th class="required"> Ano-Período: </th>
		<td>
			<h:inputText id="ano" value="#{relatoriosJasper.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);" />
			-<h:inputText id="periodo" value="#{relatoriosJasper.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this);" />
		</td>
	</tr>
	</c:if>
	
	<c:if test="${relatoriosJasper.exibeTipoNecessidade}">
	<tr>
		<th>Tipo de Necessidade Especial: </th>
		<td>
			<h:selectOneMenu id="tipoNecessidade" value="#{relatoriosJasper.tipoNecessidade.id}" >
				<f:selectItem itemLabel=" -- TODAS -- " itemValue="-1"/>
				<f:selectItems value="#{dadosPessoais.allTipoNecessidadeEspecialCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	</c:if>
	
	<c:if test="${relatoriosJasper.exibeTipoComponente}">
	<tr>
		<th>Tipo de Componente: </th>
		<td>
			<h:selectOneMenu id="tipoComponente" value="#{relatoriosJasper.tipoComponente.id}" >
				<f:selectItem itemLabel=" -- TODOS -- " itemValue="-1"/>
				<f:selectItems value="#{componenteCurricular.tiposComponentes}"/>
			</h:selectOneMenu>
		</td>
	</tr>	
	</c:if>

	<tr>
		<th class="required"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatoriosJasper.formato}" id="formato">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosJasper.gerarRelatorio}" value="Emitir Relatório" id="btnEmitir"/>
			<h:commandButton action="#{relatoriosJasper.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar" immediate="true"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>