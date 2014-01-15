<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioTotalTurmasHorarioMBean.create}" />
	<%@include file="/portais/docente/menu_docente.jsp"%>

<h2> <ufrn:subSistema /> > Relatório Total de Turmas por Horários de Aula </h2>

<h:form id="form">
<table class="formulario" style="width: 80%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<c:if test="${relatorioTotalTurmasHorarioMBean.buscarPorCentro == true}">
		<tr>
			<th class="required" width="35%">Centro / Unidade Acadêmica Especializada: </th>
			<td>
				<h:selectOneMenu id="centro" value="#{relatorioTotalTurmasHorarioMBean.centro}" style="width:95%"
					onchange="submit()" valueChangeListener="#{relatorioTotalTurmasHorarioMBean.centroListener}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>
			
	<c:if test="${relatorioTotalTurmasHorarioMBean.buscarPorDepartamento == true}">
		<tr>
			<th class="required">Departamento: </th>
			<td>
				<c:choose>
					<c:when test="${relatorioTotalTurmasHorarioMBean.acessoCompleto}">
						<h:selectOneMenu id="departamento" value="#{relatorioTotalTurmasHorarioMBean.departamento.id}" style="width:95%">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{relatorioTotalTurmasHorarioMBean.departamentoCombo}"/>
						</h:selectOneMenu>
					</c:when>
					<c:otherwise>
						${usuario.vinculoAtivo.unidade.codigoNome}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</c:if>
	
	<tr>
			<th>Ano: </th>
			<td><h:inputText id="ano" value="#{relatorioTotalTurmasHorarioMBean.ano}"
			size="5" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			
			Período: <h:inputText
			id="semestre" value="#{relatorioTotalTurmasHorarioMBean.periodo}" size="2"
			maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /></td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioTotalTurmasHorarioMBean.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatorioTotalTurmasHorarioMBean.cancelar}" onclick="#{confirm}" value="Cancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<br />
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>