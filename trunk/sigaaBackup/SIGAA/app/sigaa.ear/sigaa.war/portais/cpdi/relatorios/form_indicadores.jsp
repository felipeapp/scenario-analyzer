<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatoriosDepartamentoCpdi.create}" />
	<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > ${relatoriosDepartamentoCpdi.titulo} </h2>

<div class="descricaoOperacao">${relatoriosDepartamentoCpdi.descricaoOperacao}</div>

<h:form id="form">
<a4j:keepAlive beanName="relatoriosDepartamentoCpdi" />

<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th class="required">Departamento: </th>
		<td>
			<c:choose>
				<c:when test="${relatoriosDepartamentoCpdi.acessoCompleto}">
					<h:selectOneMenu id="departamento" value="#{relatoriosDepartamentoCpdi.departamento.id}" style="width:95%">
						<f:selectItem itemLabel=" -- SELECIONE UM DEPARTAMENTO -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allDepartamentoUnidAcademicaCombo}"/>
					</h:selectOneMenu>
				</c:when>
				<c:otherwise>
					${relatoriosDepartamentoCpdi.departamento.codigoNome}
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<c:if test="${relatoriosDepartamentoCpdi.necessitaPeriodo}">
		<tr>
			<th class="required"> Período (anos): </th>
			<td>
				de <h:inputText id="periodoAnoInicio" value="#{relatoriosDepartamentoCpdi.anoInicio}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
				a <h:inputText id="periodoAnoFim" value="#{relatoriosDepartamentoCpdi.anoFim}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
	</c:if>
	<c:if test="${relatoriosDepartamentoCpdi.necessitaAno}">
	<tr>
		<th class="required"> Ano: </th>
		<td>
			<h:inputText id="anoInicio" value="#{relatoriosDepartamentoCpdi.anoInicio}" size="5" maxlength="4" />
		</td>
	</tr>
	</c:if>

	<c:if test="${relatoriosDepartamentoCpdi.necessitaSemestre}">
	<tr>
		<th class="required"> Período(s): </th>
		<td>
			<h:selectOneMenu id="periodo" value="#{relatoriosDepartamentoCpdi.periodo}">
				<f:selectItem itemLabel="TODOS" itemValue="-1"/> 
				<f:selectItem itemLabel="1" itemValue="1"/> 
				<f:selectItem itemLabel="2" itemValue="2"/>
				<f:selectItem itemLabel="3 (Período de Férias)" itemValue="3"/>
				<f:selectItem itemLabel="4 (Período de Férias)" itemValue="4"/> 
			</h:selectOneMenu>
		</td>
	</tr>
	</c:if>
	
<%-- 	<c:if test="${relatoriosDepartamentoCpdi.necessitaPeriodoData}">
	<tr>
		<th class="required"> Perdas entre: </th>
		<td>
			<t:inputCalendar id="dataInicio" value="#{relatoriosDepartamentoCpdi.dataInicio}" size="10" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="formataData(this,event)"  maxlength="10" />
			a <t:inputCalendar id="dataFim" value="#{relatoriosDepartamentoCpdi.dataFim}" size="10" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="formataData(this,event)"  maxlength="10"/>
		</td>
	</tr>
	</c:if>
--%>
	<c:if test="${relatoriosDepartamentoCpdi.selecaoTipo}">
	<tr>
		<th class="required"> Tipo do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatoriosDepartamentoCpdi.analitico}" id="analitico">
				<f:selectItem itemValue="true" itemLabel="Analítico" />
				<f:selectItem itemValue="false" itemLabel="Sintético" />
			</h:selectOneRadio>
		</td>
	</tr>
	</c:if>

	<tr>
		<th class="required"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatoriosDepartamentoCpdi.formato}" id="formato">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosDepartamentoCpdi.gerarRelatorio}" value="Emitir Relatório" id="btnGerar"/>
			<h:commandButton action="#{relatoriosDepartamentoCpdi.cancelar}" value="Cancelar" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>