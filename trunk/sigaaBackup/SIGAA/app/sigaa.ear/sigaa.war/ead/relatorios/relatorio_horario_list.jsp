<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style type="text/css">
caption {
	text-align: center; 
	font-weight: bold;
	padding: 2px;
	border-top: 1px solid #444;
	border-bottom: 1px solid #444;
}

p {
	text-align: center;
	font-weight: bold;
	font-style: italic;
	padding: 3px;
}
</style>
<f:view>
	<h2>Relatório de Horários de Tutores e Discentes</h2>
	<h:outputText value="#{relatorioHorario.create}" />


	
	<%-- SEGUNDA --%>
	<c:if test="${ (not empty relatorioHorario.horariosTutores['2']) or (not empty relatorioHorario.horariosDiscentes['2']) }">
	<table width="100%">
	<caption>Segunda-Feira</caption>
	
	<tr><td><p>Manhã</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['2'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['2'] }">
			<c:if test="${ horarioLoop.horaInicio >= 7 or horarioLoop.horaFim <= 12 }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['2'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['2'] }">
			<c:if test="${ horarioLoop.matutino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Tarde</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['2'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['2'] }">
			<c:if test="${ horarioLoop.horaInicio >= 13 or (horarioLoop.horaFim >= 13 and horarioLoop.horaFim <= 18) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['2'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['2'] }">
			<c:if test="${ horarioLoop.vespertino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Noite</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['2'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['2'] }">
			<c:if test="${ horarioLoop.horaInicio >= 19 or (horarioLoop.horaFim >= 19 and horarioLoop.horaFim <= 23) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['2'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['2'] }">
			<c:if test="${ horarioLoop.noturno }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	</table>
	</c:if>
	
	<%-- TERCA --%>
	<c:if test="${ (not empty relatorioHorario.horariosTutores['3']) or (not empty relatorioHorario.horariosDiscentes['3']) }">
	<table width="100%">
	<caption>Terça-Feira</caption>
	
	<tr><td><p>Manhã</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['3'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['3'] }">
			<c:if test="${ horarioLoop.horaInicio >= 7 or horarioLoop.horaFim <= 12 }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['3'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['3'] }">
			<c:if test="${ horarioLoop.matutino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Tarde</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['3'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['3'] }">
			<c:if test="${ horarioLoop.horaInicio >= 13 or (horario.horaFim >= 13 and horarioLoop.horaFim <= 18) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['3'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['3'] }">
			<c:if test="${ horarioLoop.vespertino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Noite</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['3'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['3'] }">
			<c:if test="${ horarioLoop.horaInicio >= 19 or ( horarioLoop.horaFim >= 19 and horarioLoop.horaFim <= 23) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['3'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['3'] }">
			<c:if test="${ horarioLoop.noturno }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr></table>
	</c:if>
	
	<%-- QUARTA --%>
	<c:if test="${ (not empty relatorioHorario.horariosTutores['4']) or (not empty relatorioHorario.horariosDiscentes['4']) }">
	<table width="100%">
	<caption>Quarta-Feira</caption>
	
	<tr><td><p>Manhã</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['4'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['4'] }">
			<c:if test="${ horarioLoop.horaInicio >= 7 or horarioLoop.horaFim <= 12 }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['4'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['4'] }">
			<c:if test="${ horarioLoop.matutino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Tarde</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['4'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['4'] }">
			<c:if test="${ horarioLoop.horaInicio >= 13 or (horarioLoop.horaFim >= 13 and horarioLoop.horaFim <= 18) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['4'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['4'] }">
			<c:if test="${ horarioLoop.vespertino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Noite</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['4'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['4'] }">
			<c:if test="${ horarioLoop.horaInicio >= 19 or (horarioLoop.horaFim >= 19 and horarioLoop.horaFim <= 23) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['4'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['4'] }">
			<c:if test="${ horarioLoop.noturno }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr></table>
	</c:if>
	
	<%-- QUINTA --%>
	<c:if test="${ (not empty relatorioHorario.horariosTutores['5']) or (not empty relatorioHorario.horariosDiscentes['5']) }">
	<table width="100%">
	<caption>Quinta-Feira</caption>
	
	<tr><td><p>Manhã</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['5'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['5'] }">
			<c:if test="${ horarioLoop.horaInicio >= 7 or horarioLoop.horaFim <= 12 }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['5'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['5'] }">
			<c:if test="${ horarioLoop.matutino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Tarde</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['5'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['5'] }">
			<c:if test="${ horarioLoop.horaInicio >= 13 or (horarioLoop.horaFim >= 13 and horarioLoop.horaFim <= 18) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['5'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['5'] }">
			<c:if test="${ horarioLoop.vespertino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Noite</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['5'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['5'] }">
			<c:if test="${ horarioLoop.horaInicio >= 19 or (horarioLoop.horaFim >= 19 and horarioLoop.horaFim <= 23) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['5'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['5'] }">
			<c:if test="${ horarioLoop.noturno }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr></table>
	</c:if>
	
	<%-- SEXTA --%>
	<c:if test="${ (not empty relatorioHorario.horariosTutores['6']) or (not empty relatorioHorario.horariosDiscentes['6']) }">
	<table width="100%">
	<caption>Sexta-Feira</caption>
	
	<tr><td><p>Manhã</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['6'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['6'] }">
			<c:if test="${ horarioLoop.horaInicio >= 7 or horarioLoop.horaFim <= 12 }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['6'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['6'] }">
			<c:if test="${ horarioLoop.matutino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Tarde</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['6'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['6'] }">
			<c:if test="${ horarioLoop.horaInicio >= 13 or (horarioLoop.horaFim >= 13 and horarioLoop.horaFim <= 18) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['6'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['6'] }">
			<c:if test="${ horarioLoop.vespertino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Noite</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['6'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['6'] }">
			<c:if test="${ horarioLoop.horaInicio >= 19 or (horarioLoop.horaFim >= 19 and horarioLoop.horaFim <= 23) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['6'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['6'] }">
			<c:if test="${ horarioLoop.noturno }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr></table>
	</c:if>
	
	<%-- SABADO --%>
	<c:if test="${ (not empty relatorioHorario.horariosTutores['7']) or (not empty relatorioHorario.horariosDiscentes['7']) }">
	<table width="100%">
	<caption>Sábado</caption>
	
	<tr><td><p>Manhã</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['7'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['7'] }">
			<c:if test="${ horarioLoop.horaInicio >= 7 or horarioLoop.horaFim <= 12 }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['7'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['7'] }">
			<c:if test="${ horarioLoop.matutino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Tarde</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['7'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['7'] }">
			<c:if test="${ horarioLoop.horaInicio >= 13 or (horarioLoop.horaFim >= 13 and horarioLoop.horaFim <= 18) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['7'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['7'] }">
			<c:if test="${ horarioLoop.vespertino }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	
	<tr><td><p>Noite</p></td></tr>
	<tr><td>
	<table width="100%"><tr>
	<c:if test="${ not empty relatorioHorario.horariosTutores['7'] }">
		<td width="50%">
		<strong>Tutores</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosTutores['7'] }">
			<c:if test="${ horarioLoop.horaInicio >= 19 or (horarioLoop.horaFim >= 19 and horarioLoop.horaFim <= 23) }">
			${ horarioLoop.tutor.pessoa.nome } - ${ horarioLoop.horaInicio }h a ${ horarioLoop.horaFim }h<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	<c:if test="${ not empty relatorioHorario.horariosDiscentes['7'] }">
		<td width="50%">
		<strong>Discentes</strong><br/>
		<c:forEach var="horarioLoop" items="${ relatorioHorario.horariosDiscentes['7'] }">
			<c:if test="${ horarioLoop.noturno }">
			${ horarioLoop.tutoria.aluno.pessoa.nome }<br/>
			</c:if>
		</c:forEach>
		</td>
	</c:if>
	</tr></table>
	</td></tr>
	</c:if>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
