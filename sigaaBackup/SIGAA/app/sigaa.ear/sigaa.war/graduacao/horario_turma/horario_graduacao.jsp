<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="br.ufrn.sigaa.ensino.dominio.Horario"%>

<c:if test="${empty sufixo}">
	<c:set var="sufixo" value=""/>
</c:if>

<c:if test="${empty horarios}" >
	<h:outputText value="Não é possível exibir a grade de horários pois não há horários cadastrados para esta unidade." />
</c:if>
<c:if test="${not empty horarios}" >
<table width="65%" class="gradeHorarios" >
	<tr class="titulo">
		<td>Horários</td>
		<td width="10%">Dom</td>
		<td width="10%">Seg</td>
		<td width="10%">Ter</td>
		<td width="10%">Qua</td>
		<td width="10%">Qui</td>
		<td width="10%">Sex</td>
		<td width="10%">Sáb</td>
	</tr>
	
	<c:set var="ordemAnterior" value="0" />
	<c:set var="count" value="#{0}" />	
	<c:set var="disDomingo" value="#{!(habilitarFDS or habilitarDomingo)}" />
	<c:set var="disSabado" value="#{!(habilitarFDS or habilitarSabado)}" />	

	<c:forEach items="#{horarios}" var="h" varStatus="s">
		<c:set var="dia" value="#{1}" />
		<c:if test="${h.ordem < ordemAnterior and s.count > 1}">
		<tr><td colspan="7">&nbsp;</td></tr>
		</c:if>
		<tr>
			<td style="${h.ativo ? ' ' : 'color: red' }">${h.hoursDesc}</td>
			<!-- Domingo -->
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}" disabled="#{disDomingo}">
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
					 			  reRender="panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');"> 
						<f:param name="linha" value="#{linha}"/>
    				 </a4j:support> 
				</h:selectBooleanCheckbox>
			</td>			
			<!-- Segunda-Feira -->
			<c:set var="dia" value="#{dia + 1}" />
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}">
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
						reRender="formHorarios:panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');">  
						<f:param name="linha" value="#{linha}"/>
					 </a4j:support> 
				</h:selectBooleanCheckbox>			
			</td>
			<!-- Terça-Feira -->			
			<c:set var="dia" value="#{dia + 1}" />
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}">
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
						reRender="panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');">  
						<f:param name="linha" value="#{linha}"/>	
					 </a4j:support> 
				</h:selectBooleanCheckbox>
			</td>
			<!-- Quarta-Feira -->			
			<c:set var="dia" value="#{dia + 1}" />
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}" >
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
				 		reRender="panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');">  
						<f:param name="linha" value="#{linha}"/>	
					 </a4j:support> 
				</h:selectBooleanCheckbox>
			</td>
			<!-- Quinta-Feira -->			
			<c:set var="dia" value="#{dia + 1}" />
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}">
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
					 	reRender="panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');">  
						<f:param name="linha" value="#{linha}"/>	
					 </a4j:support> 
				</h:selectBooleanCheckbox>
			</td>
			<!-- Sexta-Feira -->			
			<c:set var="dia" value="#{dia + 1}" />
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}">
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
						reRender="panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');">  
						<f:param name="linha" value="#{linha}"/>	
					 </a4j:support> 
				</h:selectBooleanCheckbox>
			</td>
			<!-- Sábado -->	
			<c:set var="dia" value="#{dia + 1}" />
			<c:set var="linha" value="h_#{dia}_#{count}#{sufixo}" />
			<td>
				<h:selectBooleanCheckbox id="${linha}" disabled="#{disSabado}">
					 <a4j:support event="onclick" onsubmit="true" actionListener="#{horarioTurmaBean.calcularHorarioTurma}"
						reRender="panelDatas" oncomplete="slide('#{horarioTurmaBean.porcentagemAulas}');">  
						<f:param name="linha" value="#{linha}"/>	
					 </a4j:support> 
				</h:selectBooleanCheckbox>
			</td>
		</tr>
		<c:set var="ordemAnterior" value="${h.ordem }" />
		<c:if test="${!h.ativo}">
			<c:set var="avisoHorarioInativo" value="true" />
		</c:if>
		<c:set var="count" value="#{count+1}" />
	</c:forEach>
	<c:if test="${avisoHorarioInativo}">
		<tr><td colspan="7">OBS: os horários em vermelho não são mais ativos</td></tr>
	</c:if>
</table>

</c:if>


