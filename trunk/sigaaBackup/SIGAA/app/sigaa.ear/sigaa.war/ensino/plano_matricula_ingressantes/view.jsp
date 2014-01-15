<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Planos de Matrícula de Discentes Ingressantes</h2>
	<br/>
	<h:form id="form">
		<table class="listagem" width="90%">
			<caption>Dados do Plano de Matrícula</caption>
			<tbody>
				<tr>
					<th class="rotulo">Ano-Período:</th>
					<td>${ planoMatriculaIngressantesMBean.obj.ano }.${ planoMatriculaIngressantesMBean.obj.periodo }</td>
				</tr>
				<tr>
					<th class="rotulo">Curso:</th>
					<td>
					 	<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.curso.nome }" />
					</td>
				</tr>
				<c:if test="${ planoMatriculaIngressantesMBean.obj.graduacao }">
					<tr>
						<th class="rotulo">Matriz Curricular:</th>
						<td>
							<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.matrizCurricular.descricao }"/>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<th class="rotulo">nº do Plano:</th>
					<td>
						<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.descricao }"/>
					</td>
				</tr>
				
				<tr>
					<th class="rotulo">Capacidade:</th>
					<td>
						<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.capacidade }" />
					</td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">
						Turmas do Plano
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="listagem" width="100%">
							<thead>
							<tr>
								<th>Componente Curricular</th>
								<th>Cód. Turma</th>
								<th>Horário</th>
								<th>Docentes</th>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="#{ planoMatriculaIngressantesMBean.obj.turmas }" var="item" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td>${ item.disciplina.codigoNome }</td>
									<td>${ item.codigo }</td>
									<td>${ item.descricaoHorario }</td>
									<td>${ item.nomesDocentes }</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">
						Quadro de Horários
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<c:set var="ordemAnterior" value="0" />
						<c:set var="avisoHorarioInativo" value="false" />
						<table width="100%" class="formulario" align="center"  style="font-size: 9px">
							<tr class="titulo" style="background-color: #333366; color: white; font-weight: bold">
								<td width="1%"></td>
								<td width="5%" align="center">Seg</td>
								<td width="5%" align="center">Ter</td>
								<td width="5%" align="center">Qua</td>
								<td width="5%" align="center">Qui</td>
								<td width="5%" align="center">Sex</td>
								<td width="5%" align="center">Sab</td>
							</tr>
							<c:set var="ordemAnterior" value="0" />
							<c:set var="avisoHorarioInativo" value="false" />
							<c:forEach items="#{planoMatriculaIngressantesMBean.horarios}" var="horario" varStatus="s">
								<c:set var="dia" value="2" />
								<c:if test="${horario.ordem < ordemAnterior and s.count > 1}">
								<tr><td colspan="6" style="height: 1px">&nbsp;</td></tr>
								</c:if>
								<tr>
									<td align="center" style="${horario.ativo ? ' ' : 'color: red' }">${fn:substring(horario.turno,0,1)}${horario.ordem}</td>
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
							<c:forEach items="${planoMatriculaIngressantesMBean.horariosTurma}" var="hor">
									<c:if test="${not hor.turma.disciplina.permiteHorarioFlexivel}">
										var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
										if (elem) {
											elem.innerHTML = '<acronym title="${hor.turma.disciplina.descricaoResumida}">${hor.turma.disciplina.codigo}</acronym>';
										}
									</c:if>
							</c:forEach>
						</script>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center">
						<h:commandButton value="<< Voltar" action="#{planoMatriculaIngressantesMBean.listar}" id="voltar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
