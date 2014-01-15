<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${acesso.coordenadorPolo}">
	<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
</c:if>

	<h2><ufrn:subSistema /> > Relatório de Horários de Tutores e Discentes</h2>
	<h:outputText value="#{relatorioHorario.create}" />
	<h:messages showDetail="true"/>
	<h:form id="busca">
		<table class="formulario" width="70%">
			<caption>Dados do Relatório</caption>
			<tbody>
				<tr>
					<td class="required">Curso: </td>
					<td>
						<h:selectOneMenu value="#{relatorioHorario.curso.id}">
						<f:selectItem itemValue="0" itemLabel="Escolha um curso" />
						<f:selectItems value="#{tutorOrientador.cursos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				
				<c:if test="${acesso.coordenadorPolo == true}">
				<tr>
					<td>Pólo: </td>
					<td>
						<h:selectOneMenu disabled="true" id="poloSelecionadoA" value="#{relatorioHorario.polo.id}">
						<f:selectItems value="#{relatorioHorario.polos}" />
						<h:inputHidden id="poloSelecionadoB" value="#{relatorioHorario.polo.id}"></h:inputHidden>
						</h:selectOneMenu> 
					</td>
				</tr>
				</c:if>
				
				<c:if test="${acesso.coordenadorPolo == false}">
				<tr>
					<td>Pólo: </td>
					<td>
						<h:selectOneMenu value="#{relatorioHorario.polo.id}">
						<f:selectItem itemValue="0" itemLabel="Escolha um pólo" />
						<f:selectItems value="#{tutorOrientador.polos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				</c:if>
				
				<tr>
					<td>Dia da Semana: </td>
					<td>
						<h:selectOneMenu value="#{relatorioHorario.dia}">
						<f:selectItem itemValue="0" itemLabel="Escolha um dia da semana" />
						<f:selectItems value="#{tutorOrientador.diasSemana}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				<tr>
					<td>Turno: </td>
					<td>
						<h:selectOneMenu value="#{relatorioHorario.turno}">
						<f:selectItem itemValue="-" itemLabel="Escolha um turno" />
						<f:selectItem itemValue="M" itemLabel="Manhã" />
						<f:selectItem itemValue="T" itemLabel="Tarde" />
						<f:selectItem itemValue="N" itemLabel="Noite" />
						</h:selectOneMenu> 
					</td>
					
				</tr>
				<tr>
					<td class="required">Agrupar por:</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Tutores: </td>
					<td><h:selectBooleanCheckbox value="#{ relatorioHorario.tutores }"/></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Discentes: </td>
					<td><h:selectBooleanCheckbox value="#{ relatorioHorario.discentes }"/></td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{relatorioHorario.gerar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{tutorOrientador.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
