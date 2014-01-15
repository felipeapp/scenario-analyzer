<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem thead th { border-bottom: 1px solid #CCC; text-align: left; }
	h3, h4 { font-size: 1.3em; font-weight: bold; text-align: center; font-variant: small-caps; }
	h4 { font-size: 1 em; margin-bottom: 15px; color: #555; }
</style>

<f:view>
	<h3> Comprovante de Solicitação de Matrícula 
		<c:if test="${not empty matriculaGraduacao.numeroSolicitacao}">
			N° ${matriculaGraduacao.numeroSolicitacaoFormatado } 
		</c:if>
	</h3>
	<h4> Período ${matriculaGraduacao.calendarioParaMatricula.anoPeriodo } </h4>

	<c:set value="#{matriculaGraduacao.discente }" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>

	<c:if test="${not empty matriculaGraduacao.solicitacoesMatriculas}">
	<table class="listagem" style="width: 100%">
		<caption>Turmas selecionadas</caption>
		<thead>
			<tr>
				<th> Componente Curricular </th>
				<th> Turma </th>
				<th> Local </th>
				<th> Situação </th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="#{matriculaGraduacao.solicitacoesMatriculas}" var="sol" varStatus="status">
				<c:if test="${not empty sol.turma}">
				<c:set value="#{sol.turma }" var="turma"></c:set>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small; border-top: thin dashed;">
					<td>${turma.disciplina.descricao}</td>
					<td width="12%">Turma ${turma.codigo}</td>
					<td width="12%">${turma.local}</td>
					<td width="15%" style="color:${(sol.deferida ? '#229' : ( sol.inDeferida || sol.negada ? '#922' : '') )}; text-align: center;">
						<c:choose>
							<c:when test="${matriculaGraduacao.discente.metropoleDigital}">
									<b>MATRICULADO</b>
							</c:when>
							<c:when test="${matriculaGraduacao.discente.stricto}">
								<b>${sol.statusDescricao}</b>
							</c:when>
							<c:otherwise>
								<c:if test="${!sol.processada}">
									${sol.processamentoStatus}
								</c:if>
								<c:if test="${sol.processada}">
									<c:if test="${ sol.rematricula != null && sol.rematricula }">
										<a href="#" onclick="window.open('${ctx}/relatorioProcessamento?idTurma=${ turma.id }&rematricula=true','resultado','height=480,width=730,resizable=yes,scrollbars=yes');">
										${sol.processamentoStatus}
										</a>
									</c:if>
									<c:if test="${ sol.rematricula == null || !sol.rematricula }">
										<a href="#" onclick="window.open('${ctx}/relatorioProcessamento?idTurma=${ turma.id }','resultado','height=480,width=730,resizable=yes,scrollbars=yes');">
										${sol.processamentoStatus}
										</a>
									</c:if>
								</c:if>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	
	<c:if test="${not empty matriculaGraduacao.solicitacoesAtividade}">
	<br/>
	<table class="listagem" style="width: 100%">
		<caption>Atividades selecionadas</caption>
		<thead>
			<tr>
				<th> Atividade </th>
				<th> Situação </th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaGraduacao.solicitacoesAtividade}" var="sol" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small; border-top: thin dashed;">
				<td>${sol.componente.descricao}</td>
				<td width="15%" style="color:${(sol.deferida ? '#229' : ( sol.inDeferida || sol.negada ? '#922' : '') )}; text-align: center;">
					<b>${sol.statusDescricao}</b>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>

	<c:if test="${matriculaGraduacao.exibirQuadroHorarios && !matriculaGraduacao.discente.metropoleDigital}">
		<br>
		<table width="100%" class="formulario" align="center"  >
			<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold; font-size: 0.8em;">
				<td width="14%" align="center">Horários</td>
				<td width="10%" align="center">Seg</td>
				<td width="10%" align="center">Ter</td>
				<td width="10%" align="center">Qua</td>
				<td width="10%" align="center">Qui</td>
				<td width="10%" align="center">Sex</td>
				<td width="10%" align="center">Sáb</td>
			</tr>
			<c:forEach items="#{matriculaGraduacao.horarios}" var="horario" varStatus="s">
				<c:set var="dia" value="2" />
				<c:if test="${horario.ordem == 1 and s.count > 1}">
				<tr><td colspan="7" style="height: 3px"></td></tr>
				</c:if>
				<tr style="font-size: 9px;">
					<td align="center">${horario.hoursDesc}</td>
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
		<c:if test="${not empty matriculaGraduacao.horariosTurma}">
			<script type="text/javascript">
				<c:set var="possuiAlgumaTurmaHorarioFlexivel" value="false" />
				<c:forEach items="${matriculaGraduacao.horariosTurma}" var="hor">

						<c:if test="${hor.turma.disciplina.permiteHorarioFlexivel}">
							<c:set var="possuiAlgumaTurmaHorarioFlexivel" value="true" />
						</c:if>
				
						<c:if test="${not hor.turma.disciplina.permiteHorarioFlexivel}">
							var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
							if (elem) {
								elem.innerHTML = '${hor.turma.disciplina.codigo}';
							}
						</c:if>
				</c:forEach>
			</script>
			<c:if test="${possuiAlgumaTurmaHorarioFlexivel}">
				<br/>
				<table class="subFormulario" style="width: 100%">
					<caption>Turmas com horário flexível</caption>
					<thead>
						<tr>
							<th> Componente Curricular </th>
							<th> Turma </th>
							<th> Horário </th>
						</tr>
					</thead>
					
					<tbody>
					<c:set var="id_turma"></c:set>
						<c:forEach items="#{matriculaGraduacao.turmas}" var="turma" varStatus="status">
							<c:if test="${turma.id != id_turma and turma.disciplina.permiteHorarioFlexivel}">
								<c:set value="#{turma.id }" var="id_turma"></c:set>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small; border-top: thin dashed;">
									<td>${turma.disciplina.descricao}</td>
									<td width="12%">Turma ${turma.codigo}</td>
									<td width="50%">${turma.descricaoHorario}</td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</c:if>
	</c:if>
	
	</div>
	<br/>
	<br>
	<div style="color: gray; text-align: center" >
		<i>Gravado em</i>: <ufrn:format type="datahorasec" valor="${matriculaGraduacao.gravadoEm}"></ufrn:format><br/>
		<i>Autenticação</i>: ${matriculaGraduacao.md5 }
	</div>
	<div>
		<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>	
	</div>
</f:view>