<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatoriosDepartamentoCpdi.create}" />
	<%@include file="/portais/docente/menu_docente.jsp"%>

<h2> <ufrn:subSistema /> > Relatório de Turmas por Departamento </h2>

<div class="descricaoOperacao">
${relatoriosDepartamentoCpdi.descricaoOperacao}
</div>

<h:form id="form">
<a4j:keepAlive beanName="relatoriosDepartamentoCpdi"></a4j:keepAlive>
${relatoriosDepartamentoCpdi.unidadeIntegracao}
<table class="formulario" style="width: 95%">
<caption> Informe os Critérios para a Emissão do Relatório </caption>
	<h:inputHidden value="#{relatoriosDepartamentoCpdi.departamento.nome}"/>
	<tr>
		<c:choose>
			<c:when test="${relatoriosDepartamentoCpdi.acessoCompleto}">
				<th class="required">Departamento: </th>
				<td>
					<h:selectOneMenu id="departamento" value="#{relatoriosDepartamentoCpdi.departamento.id}" style="width:95%">
						<f:selectItem itemLabel=" -- SELECIONE UM DEPARTAMENTO -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allDeptosProgramasEscolasCombo}"/>
					</h:selectOneMenu>
				</td>
			</c:when>
			<c:otherwise>
				<th>Departamento: </th>
				<td>
					${usuario.unidade.codigoNome}
					<h:inputHidden value="#{relatoriosDepartamentoCpdi.departamento.id}"/>
				</td>
			</c:otherwise>
		</c:choose>
	</tr>
	<c:if test="${relatoriosDepartamentoCpdi.necessitaPeriodo}">
		<tr>
			<th class="required"> Período (anos): </th>
			<td>
				<t:inputCalendar id="periodoAnoInicio" value="#{relatoriosDepartamentoCpdi.anoInicio}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
				a <t:inputCalendar id="periodoAnoFim" value="#{relatoriosDepartamentoCpdi.anoFim}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
	</c:if>
	<c:if test="${not relatoriosDepartamentoCpdi.necessitaPeriodo}">
		<tr>
			<th class="required"> Ano: </th>
			<td>
				<h:inputText id="anoInicio" value="#{relatoriosDepartamentoCpdi.anoInicio}" size="5" maxlength="4"  onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
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
				<f:selectItem itemLabel="3" itemValue="3"/>
				<f:selectItem itemLabel="4" itemValue="4"/> 
			</h:selectOneMenu>
		</td>
	</tr>
	</c:if>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosDepartamentoCpdi.gerarRelatorioTurmasDocenteDepartamento}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatoriosDepartamentoCpdi.cancelar}" value="Cancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>