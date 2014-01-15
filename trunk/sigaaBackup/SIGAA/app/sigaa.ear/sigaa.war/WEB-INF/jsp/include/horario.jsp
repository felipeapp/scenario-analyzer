<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="br.ufrn.sigaa.ensino.dominio.Horario"%>

<c:if test="${empty sufixo}">
	<c:set var="sufixo" value=""/>
</c:if>

<c:if test="${empty horarios}" >
	<h:outputText value="Não é possível exibir a grade de horários pois não há horários cadastrados para esta unidade." />
</c:if>
<c:if test="${not empty horarios}" >
<table width="65%" class="gradeHorarios">
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
	<c:forEach items="#{horarios}" var="h" varStatus="s">
		<c:set var="dia" value="1" />
		<c:if test="${h.ordem < ordemAnterior and s.count > 1}">
		<tr><td colspan="7">&nbsp;</td></tr>
		</c:if>
		<tr>
			<td style="${h.ativo ? ' ' : 'color: red' }">${h.hoursDesc}</td>
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}"
				id="${linha}" ${(habilitarFDS or habilitarDomingo)?'':'disabled=disabled' }></td>
			<c:set var="dia" value="${dia + 1}" />
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}" id="${linha}"></td>
			<c:set var="dia" value="${dia + 1 }" />
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}" id="${linha}"></td>
			<c:set var="dia" value="${dia + 1 }" />
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}" id="${linha}"></td>
			<c:set var="dia" value="${dia + 1 }" />
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}" id="${linha}"></td>
			<c:set var="dia" value="${dia + 1 }" />
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}" id="${linha}"></td>
			<c:set var="dia" value="${dia + 1 }" />
			<c:set var="linha" value="h_${dia}_${s.count-1}${sufixo}" />
			<td><input type="checkbox" name="horEscolhidos${sufixo}" style="border: 0;" value="${linha}"
				id="${linha}" ${(habilitarFDS or habilitarSabado)?'':'disabled=disabled' }></td>
		</tr>
		<c:set var="ordemAnterior" value="${h.ordem }" />
		<c:if test="${!h.ativo}">
			<c:set var="avisoHorarioInativo" value="true" />
		</c:if>
	</c:forEach>
	<c:if test="${avisoHorarioInativo}">
		<tr><td colspan="7">OBS: os horários em vermelho não são mais ativos</td></tr>
	</c:if>
</table>
<c:forEach items="${horariosMarcados}" var="marcado">
	<script type="text/javascript">
		marcaCheckBox('${marcado}${sufixo}');
	</script>
</c:forEach>
</c:if>


