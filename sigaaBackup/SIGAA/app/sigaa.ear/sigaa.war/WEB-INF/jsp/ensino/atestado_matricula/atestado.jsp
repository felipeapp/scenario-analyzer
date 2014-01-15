<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
table caption {
	text-align: left;
}
table.formulario th{
	font-weight: bold;
}
table.listagem th {
	text-align: center;
	font-weight: bold;
}
table.listagem td {
	text-align: left;
	vertical-align: top;
}
table.listagem {
	border-left: none;
	border-right: none;
	width: 100%;
}
.linhaAtestado{
	border-bottom: thin dashed gray;
}
-->
</style>

<h2 style="font-size: 10pt; text-align: center">
	Atestado de Matrícula
</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
	  <th width="100">Matrícula:</th>
	  <td>${discente.matricula}</td>
	</tr>
	<tr>
	  <th>Nome:</th>
	  <td>${discente.pessoa.nome}</td>
	</tr>
	<tr>
		<th>Curso:</th>
		<td>${nomeCurso}</td>
		<%--<ufrn:subSistema teste="tecnico">
			<td>${discente.turmaEntradaTecnico.estruturaCurricularTecnica.cursoTecnico.nome}</td>
		</ufrn:subSistema>
		<ufrn:subSistema teste="lato">
			<td>${discente.turmaEntrada.cursoLato.nome}</td>
		</ufrn:subSistema>--%>
	</tr>
</table>
</div>
<br>

<c:if test="${empty matriculasAtestado}">
<center>
<font color="red">O discente não possui turmas matriculadas no momento.</font>
</center>
</c:if>

<c:if test="${not empty matriculasAtestado}">
	<table class="listagem">
		<caption>Turmas Matriculadas (${ fn:length(matriculasAtestado) })</caption>
			<th>Ano.Per</th>
			<th style="text-align: left;">Disciplina / Docente (s)</th>
      		<th width="30">Turma</th>
	       	<th width="30">CH</th>
	   	   	<th width="60">Situação</th>
		<tbody>
		<c:forEach items="${matriculasAtestado}" var="matricula">
	    	    <tr class="linhaAtestado">
	    	        <td>${matricula.anoPeriodo}</td>
	   	    	    <td>
	   	    	    	${matricula.componenteDescricaoResumida}<br>
						${matricula.turma.docentesNomes}<br>
	   	    	    	Local: ${matricula.turma.local } &nbsp;&nbsp; Horário: ${matricula.turma.descricaoHorario }
	   	    	    </td>
	       	    	<td align="center">${matricula.turma.codigo}</td>
	              	<td align="center">${matricula.componenteCHTotal}</td>
	  	            <td align="center">${matricula.situacaoMatricula.descricao}</td>
	   	    	</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>
<br>
<hr>
<c:if test="${not empty horariosTurmas}">
	<table width="80%" class="formulario" align="center">
		<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold">
			<td align="center">Horários</td>
			<td width="10%" align="center">Dom</td>
			<td width="10%" align="center">Seg</td>
			<td width="10%" align="center">Ter</td>
			<td width="10%" align="center">Qua</td>
			<td width="10%" align="center">Qui</td>
			<td width="10%" align="center">Sex</td>
			<td width="10%" align="center">Sab</td>
		</tr>
		<c:forEach items="${horarios}" var="horario" varStatus="s">
			<c:set var="dia" value="1" />
			<c:if test="${horario.ordem == 1 and s.count > 1}">
			<tr><td colspan="7">&nbsp;</td></tr>
			</c:if>
			<tr>
				<td align="center">${horario.hoursDesc}</td>
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1}" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
				<td align="center"><span id="${dia}_${horario.id}">---</span></td>
				<c:set var="dia" value="${dia + 1 }" />
			</tr>
		</c:forEach>
	</table>

		<script type="text/javascript">
	<c:forEach items="${horariosTurmas}" var="hor">
			var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
			elem.innerHTML = '${hor.turma.disciplina.codigo}';
	</c:forEach>
		</script>
</c:if>

<%-- FIM DA TABELA DE HORÁRIO--%>
<br><br>
<div style="font-style: italic; text-align: right;">
Emitido em: <fmt:formatDate value="${dataAtual}" pattern="dd/MM/yyyy ' às ' HH:mm'h'"/>
</div>
<table width="100%" cellpadding='1' cellspacing='0'>
        <tr>
            <td align=center>
            	<BR><BR>
                <B>ATENÇÃO</B><BR><BR>
                ESTE DOCUMENTO SÓ SERÁ CONSIDERADO OFICIAL COM A CHANCELA DA <BR>
                COORDENAÇÃO DO CURSO
                <ufrn:subSistema teste="tecnico">
                OU DA SECRETARIA DA ESCOLA
                </ufrn:subSistema>
                <BR><br><br>
            </td>
        </tr>
        <tr>
            <td align=center>
                ______________________________________ <br>
                     Coordenador do Curso ou <br>Secretário(a)
            </td>
        </tr>
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
