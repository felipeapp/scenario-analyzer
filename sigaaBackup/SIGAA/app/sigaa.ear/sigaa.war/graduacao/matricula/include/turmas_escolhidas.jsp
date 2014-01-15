	<c:if test="${matriculaGraduacao.exibirOrientacoes}">
		<div class="descricaoOperacao">
		<h:form>
			Caro(a) Aluno(a),<br>
				existem orientações sobre as matrículas. Clique em
				<h:commandLink value="Ver Orientações da Coordenação" action="#{matriculaGraduacao.telaSolicitacoes}"></h:commandLink>
				 para visualizá-las.
		</h:form>
		</div>
	</c:if>
	<!-- turmas selecionadas e horários -->
	<table width="100%">
	<tr>
	<td valign="top">
	<div id="abas-turmas" class="yui-navset">
		<div id="turmas" class="aba" style='${(matriculaGraduacao.exibirQuadroHorarios?'height: 355px;':'')} position: relative; overflow: auto' >
			<div class="infoAltRem">
				<img src="/sigaa/img/delete.gif">: Remover Turma
			</div>
			<table class="listagem" style="width: 100%;">
					<c:if test="${ matriculaGraduacao.compulsoria or matriculaGraduacao.foraPrazo }">
						<thead>
							<td colspan="6" >
							Status das Matrículas:
							${(matriculaGraduacao.situacao.id == 0)?
								"Não escolhido":
								matriculaGraduacao.situacao.descricao}
							</td>
						</thead>
					</c:if>
					<tbody>
					<c:if test="${empty matriculaGraduacao.turmas}">
						<tr>
						<td colspan="6" >Nenhuma turma foi selecionada.</td>
						</tr>
					</c:if>
					<c:set var="possuiAlgumaTurmaHorarioFlexivel" value="false" />
					<c:if test="${!empty matriculaGraduacao.turmas}">
							<table width="100%">
							<thead>
								<tr>
									<td width="10%"> Turma </td>
									<td colspan="2"> Comp. Curricular </td>
									<td width="10%"> CR/CH </td>
									<td> </td>
								</tr>
							</thead>
							<h:form>
							<c:set value="0" var="cr" />
							<c:set value="0" var="ch" />
							<c:forEach items="#{matriculaGraduacao.turmas}" var="turma" varStatus="status">
								<c:set value="${cr + turma.disciplina.crTotal}" var="cr" />
								<c:set value="${ch + turma.disciplina.chTotal}" var="ch" />
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}"
										style="font-size: xx-small;">
									<td style="height: 5px;" align="center">
									<a href="javascript:void(0);" onclick="PainelTurma.show(${turma.id})" title="Ver Detalhes dessa turma">
										${turma.codigo}
									</a>
									<c:if test="${turma.disciplina.permiteHorarioFlexivel}">
										<c:set var="possuiAlgumaTurmaHorarioFlexivel" value="true" />
										<img src="/sigaa/img/required.gif" title="Turma com horário flexível"/>
									</c:if>
									</td>
									<td width="10%">
										<a href="#" onclick="PainelComponente.show(${turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
											${turma.disciplina.codigo}
										</a>
									</td>
									<td>
									${turma.disciplina.nome}
									</td>
									<td align="center"> ${turma.disciplina.crTotal} /  ${turma.disciplina.chTotal} </td>
									<td width="1%" rowspan="2">
										<h:commandLink  title="Remover Turma" id="linkexcluirTurmas"
										onclick="if (!confirm('Tem certeza que não deseja mais se matricular nessa turma?')) return false"
											action="#{matriculaGraduacao.removerTurma}">
											<h:graphicImage url="/img/delete.gif"/>
											<f:param name="idTurma" value="#{turma.id}"/>
										</h:commandLink>
									</td>
								</tr>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}"
									style="font-size: xx-small; border-bottom: thin dashed;">
									<td colspan="4" style="padding-bottom: 7px;">Docente(s): ${turma.docentesNomes}</td>
								</tr>
							</c:forEach>
							</h:form>
							<tr class="linhaPar" style="font-size: xx-small;">
								<td colspan="2" style="height: 5px"></td>
								<td align="right" colspan="2">
									Total:
									<span style="color: #292; font-weight: bold;">
										${cr} créditos / ${ch} horas
									</span>
								</td>
								<td>
								</td>
							</tr>
					</c:if>
					</tbody>
				</table>
			<c:if test="${not empty matriculaGraduacao.turmasIndeferidas}">
				<table class="listagem" style="width: 100%; position: relative; bottom: 0; left: 0; "  >
					<thead>
						<td colspan="1">
							Matrículas ${ matriculaGraduacao.discente.stricto ? 'Negadas' : 'Indeferidas'} 
						</td>
					</thead>
					<c:forEach items="#{matriculaGraduacao.turmasIndeferidas}" var="t" >
						<c:if test="${ t != null }">
						<tr style="font-size: xx-small;">
							<td>
							<a href="javascript:void(0);" onclick="PainelTurma.show(${t.id})" title="Ver Detalhes dessa turma">
								Turma ${t.codigo}
							</a>
							 -
							<a href="javascript:void(0);" onclick="PainelComponente.show(${t.disciplina.id})" title="Ver Detalhes do Componente Curricular">
							${t.disciplina.detalhes.codigo}
							</a>
							- ${t.disciplina.detalhes.nome} (${t.disciplina.crTotal} crs.)
							</td>
						</tr>
						</c:if>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${ matriculaGraduacao.exibirTurmasJaMatriculadas }">
				<table class="listagem" style="width: 100%; position: relative; bottom: 0; left: 0; "  >
					<thead>
						<td colspan="2">Turmas já Matriculadas</td>
					</thead>
					<c:forEach items="#{matriculaGraduacao.matriculadas}" var="turma" >
						<tr style="font-size: xx-small;">
							<td>
								<a href="javascript:void(0);" onclick="PainelTurma.show(${turma.id})" title="Ver Detalhes dessa turma">
									Turma ${turma.codigo}
								</a>
								<c:if test="${turma.disciplina.permiteHorarioFlexivel}">
									<c:set var="possuiAlgumaTurmaHorarioFlexivel" value="true" />
									<img src="/sigaa/img/required.gif" title="Turma com horário flexível"/>
								</c:if>
								 -
								<a href="javascript:void(0);" onclick="PainelComponente.show(${turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
								${turma.disciplina.detalhes.codigo}
								</a>
								- ${turma.disciplina.detalhes.nome} (${turma.disciplina.crTotal} crs.)
							</td>
							<td width="1%">
								<h:form>
									<h:commandLink  title="Remover Turma Matriculada" id="linkexcluirTurmasMatriculadas"
										onclick="if (!confirm('Tem certeza que não deseja mais se matricular nessa turma?')) return false"
										action="#{matriculaGraduacao.removerTurmaMatriculada}" rendered="#{matriculaGraduacao.alunoIngressante}">
										<h:graphicImage url="/img/delete.gif"/>
										<f:param name="idTurma" value="#{turma.id}"/>
									</h:commandLink>
								</h:form>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</div>
	</div>
	</td>
	<td ${(matriculaGraduacao.exibirQuadroHorarios && !possuiAlgumaTurmaHorarioFlexivel)?'width=35%':''} valign="top">
		<c:if test="${matriculaGraduacao.exibirQuadroHorarios && !possuiAlgumaTurmaHorarioFlexivel}">
			<div id="abas-horarios" class="yui-navset" >
				<div id="horarios" class="aba" style="min-height: 355px">
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
						<c:forEach items="#{matriculaGraduacao.horarios}" var="horario" varStatus="s">
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
					<c:if test="${not empty matriculaGraduacao.horariosTurma}">
						<script type="text/javascript">
							<c:forEach items="${matriculaGraduacao.horariosTurma}" var="hor">

									
									
									<c:if test="${not hor.turma.disciplina.permiteHorarioFlexivel}">
										var elem = document.getElementById('${hor.dia}_${hor.horario.id}');
										if (elem) {
											elem.innerHTML = '<acronym title="${hor.turma.disciplina.descricaoResumida}">${hor.turma.disciplina.codigo}</acronym>';
										}
									</c:if>
							</c:forEach>
						</script>
					</c:if>
				</div>
			</div>
		</c:if>
	</td>
	</tr>
	<tr>
	<td align="center"><c:if test="${possuiAlgumaTurmaHorarioFlexivel}">
				<span><img src="/sigaa/img/required.gif"/> Turma com horário flexível </span>
			</c:if>
	</td>
	<td></td>
	</tr>
	</table>
	
	<c:if test="${possuiAlgumaTurmaHorarioFlexivel}">
		<br/>
		<h:form id="formTurma">
			<p:schedule id="scheduleAgenda" 
						value="#{matriculaGraduacao.turmasAgendaModel}"
						editable="false"
						draggable="false" 
						widgetVar="minhaAgenda"
					 	minTime="7"
					 	maxTime="23"
					 	view="month" 
					 	locale="pt"
					 	aspectRatio="2" 
					 	initialDate="#{matriculaGraduacao.primeiroDiaAula}" />
	    </h:form>
	</c:if>
	
	



<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-turmas');
        abas.addTab('turmas', "Turmas Selecionadas");
        abas.activate('turmas');
    }
};
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
<c:if test="${matriculaGraduacao.exibirQuadroHorarios && !possuiAlgumaTurmaHorarioFlexivel}">
Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-horarios');
        abas.addTab('horarios', "Horários das Turmas Selecionadas");
        abas.activate('horarios');
    }
};
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</c:if>
</script>