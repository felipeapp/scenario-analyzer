<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<link rel="stylesheet" media="all" href="/sigaa/css/atestado_matricula.css" type="text/css" />

<f:view>
<h3>Atestado de Matrícula</h3>
<h:outputText value="#{ atestadoMatriculaMedio.create }"/>

<table id="identificacao">
	<tr>
		<td>Ano Letivo:</td>
		<td><strong>${ atestadoMatriculaMedio.anoLetivo }</strong></td>
		<td>Nível:</td>
		<td><strong>${ atestadoMatriculaMedio.discente.nivelDesc }</strong></td>
	</tr>
	<tr>
		<td width="12%">Matrícula:</td>
		<td width="40%"> <strong>${ atestadoMatriculaMedio.discente.matricula }</strong></td>
		<td width="10%"> Vínculo: </td>
		<td> <strong>${atestadoMatriculaMedio.discente.tipoString }</strong> </td>
	</tr>
	<tr>
		<td>Nome: </td>
		<td colspan="4"><strong>${ atestadoMatriculaMedio.discente.pessoa.nome }</strong></td>
	</tr>
	<tr>
		<td>Curso: </td>
		<td colspan="4" ><strong> ${ atestadoMatriculaMedio.discente.curso.descricaoCompleta } </strong></td>
	</tr>	
	
</table>


<br />
<c:set var="matriculas" value="${ atestadoMatriculaMedio.disciplinasMatriculadas }"/>

<h4>
	Turmas Matriculadas: ${atestadoMatriculaMedio.qtdTurmasMatriculadas}
	<c:if test="${atestadoMatriculaMedio.qtdAtividadesMatriculadas > 0}">
		<br>Atividades Matriculadas: ${atestadoMatriculaMedio.qtdAtividadesMatriculadas}
	</c:if>
</h4>

<table id="matriculas" cellspacing="0">
	<thead>
		<tr>
			<c:if test="${atestadoMatriculaMedio.discente.discenteEad}">
			<th align="center"></th>
			</c:if>
			
			<th>Componentes Curriculares/Docentes</th>
			<th align="center">Série</th>
			<th align="center">Turma</th>
			<th align="center">Status</th>
			<th align="center">Horário</th>
		
		</tr>
	</thead>
	<tbody>
	<c:forEach var="item" items="${ matriculas }">
		<c:if test="${!item.componente.atividade}">
		<tr>
			<c:if test="${atestadoMatriculaMedio.discente.discenteEad}">
				<td class="codigo"> ${item.anoPeriodo} </td>
			</c:if>
			
			<td valign="top">
				<span class="componente"> ${item.componenteNome} <c:if test="${item.dependencia}">(DEPENDÊNCIA)</c:if> </span>
				<span class="docente">${ item.turma.docentesNomes }</span>
				<c:if test="${!atestadoMatriculaMedio.discente.discenteEad}">
					<span class="tipoComponente">
						<b>Tipo:</b> 
						${ item.turma.disciplina.tipoComponente.descricao  }
						<c:if test="${item.turma.disciplina.modulo}">
							(<fmt:formatDate value="${ item.turma.dataInicio }" pattern="dd/MM"/> a <fmt:formatDate value="${ item.turma.dataFim }" pattern="dd/MM"/>)
						</c:if>
					</span>
					<span class="local"><b>Local:</b> ${ item.turma.local }</span>
				</c:if>
			</td>
			<td align="center"> ${item.serie.numeroSerieOrdinal} </td>
			<td align="center">${item.dependencia ? "Dependência":item.turma.codigo }</td>
			<td align="center" style="font-variant: small-caps;">${ item.situacaoMatricula.descricao }</td>
			<td align="center">${ item.turma.descricaoHorario }</td>
		
		</tr>
		</c:if>
		
		<c:if test="${item.componente.atividade}">
		<tr>
			<td class="codigo">${ item.componenteCodigo }</td>
			<td valign="top">
				<span class="componente">${item.componenteNome}</span>
				<span class="docente">${ item.registroAtividade.docentesNomes}</span>
			</td>
			<td class="turma"> -- </td>
			<td class="status">${ item.situacaoMatricula.descricao }</td>
			<td class="horario"> -- </td>
		</tr>
		</c:if>
		
	</c:forEach>
	</tbody>
</table>

<%-- Horários --%>
<c:if test="${not empty atestadoMatriculaMedio.horariosTurma and !atestadoMatriculaMedio.discente.discenteEad}">
	<br />
	<h4>Tabela de Horários:</h4>
	<table width="80%" id="horario" align="center" cellspacing="0">
		<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold">
			<td align="center">Horários</td>
			<td width="13%" align="center">Dom</td>
			<td width="13%" align="center">Seg</td>
			<td width="13%" align="center">Ter</td>
			<td width="13%" align="center">Qua</td>
			<td width="13%" align="center">Qui</td>
			<td width="13%" align="center">Sex</td>
			<td width="13%" align="center">Sab</td>
		</tr>
		<c:set var="ordemAnterior" value="0" />
		<c:forEach items="#{atestadoMatriculaMedio.horarios}" var="horario" varStatus="s">
			<c:set var="dia" value="1" />
			<c:if test="${horario.ordem < ordemAnterior and s.count > 1}">
				<tr><td colspan="7" style="font-size: 0.1em;">&nbsp;</td></tr>
			</c:if>
			<tr>
				<td align="center" style="${horario.ativo ? ' ' : 'color: red' }">${horario.hoursDesc}</td>
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
			<c:set var="ordemAnterior" value="${horario.ordem }" />
			<c:if test="${!horario.ativo}">
				<c:set var="avisoHorarioInativo" value="true" />
			</c:if>
		</c:forEach>
		<c:if test="${avisoHorarioInativo}">
			<tr><td colspan="7" class="caixaCinza">OBS: os horários em vermelho não são mais ativos</td></tr>
		</c:if>
	</table>

		<script type="text/javascript">
	<c:forEach items="${atestadoMatriculaMedio.horariosTurma}" var="hor">
			var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
			if (elem) elem.innerHTML = '${hor.turma.disciplina.codigo}';
	</c:forEach>
		</script>
</c:if>

<div id="autenticacao">
	<h4>ATENÇÃO</h4>
	<p>
		Para verificar a autenticidade deste documento acesse
		<span>${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando a matrícula, a data de emissão e
		o código de verificação <span>${atestadoMatriculaMedio.codigoSeguranca}</span>
	</p>
</div>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>