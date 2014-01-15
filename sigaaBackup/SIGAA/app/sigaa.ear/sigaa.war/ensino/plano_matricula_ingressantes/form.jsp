<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>
<script type="text/javascript">
<!--
	function markCheck(c) {
		var idMarcado = c.id.substring(0, c.id.indexOf('CHK'));
		var linha = $(idMarcado+'TR');
		var cor = (c.checked ? '#B5EEB5' : '');
		linha.style.backgroundColor=cor;
	}

	function marcarSemestre(chk) {
		var semestre = 's_' + chk.id;
		var checks = document.getElementsByName('selecaoTurmas');
		var compMarcado = ' ';
		for (i=0;i<checks.length;i++) {
			if (checks[i].id.indexOf(semestre) >= 0) {
				var id = checks[i].id.substring(checks[i].id.indexOf('cc_')+3,checks[i].id.indexOf('t_'));
				var proximoId;
				if ((i + 1) < checks.length){
					proximoId = checks[i+1].id.substring(checks[i+1].id.indexOf('cc_')+3,checks[i+1].id.indexOf('t_'));
				}
				if (id != compAtual) {
					if (compMarcado == '0') {
						checks[i].checked = chk.checked;
						markCheck(checks[i]);
					}
					compMarcado = '0';
				}
				if (proximoId != id && compMarcado == '0') {
					checks[i].checked = chk.checked;
					markCheck(checks[i]);
					compMarcado = id;
				}
				var compAtual = id;
				if (checks[i].id.indexOf('_temReserva') >= 0 && compAtual != compMarcado) {
					checks[i].checked = chk.checked;
					markCheck(checks[i]);
					compMarcado = compAtual;
				}
			}
		}
	}

--></script>
<f:view>
	<h2><ufrn:subSistema /> &gt; Planos de Matrícula de Discentes Ingressantes</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>O Plano de Matrícula em Turmas de Discentes Ingressantes permite
			que os discentes ingressantes de uma matriz curricular sejam
			matriculados automaticamente em um grupo de turmas selecionadas
			quando os mesmos são cadastrados. Para isto, informe um código,
			uma matriz curricular e escolha quais turmas os discentes serão
			matriculados.</p>
		<p><b>Serão listadas apenas as turmas com reservas de vagas para a matriz curricular selecionada</b></p>
	</div>
	<br/>
	<h:form id="form">
		<table class="formulario"  width="90%">
			<caption>Informe os Dados do Plano de Matrícula</caption>
			<tbody>
				<tr>
					<th class="rotulo">Ano-Período:</th>
					<td>
						<h:inputText value="#{planoMatriculaIngressantesMBean.obj.ano}" id="ano" size="4" title="ano" maxlength="4" onkeyup="formatarInteiro(this);" converter="#{ intConveter }"/>
						-
						<h:inputText value="#{planoMatriculaIngressantesMBean.obj.periodo}" id="periodo" size="1"  title="periodo" maxlength="1" onkeyup="formatarInteiro(this);" converter="#{ intConveter }" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Código:</th>
					<td>
						<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.descricao }" rendered="#{ not empty planoMatriculaIngressantesMBean.obj.descricao }"/>
						<h:outputText value="Será atribuído automaticamente" rendered="#{ empty planoMatriculaIngressantesMBean.obj.descricao }"/>
					 </td>
				</tr>
				<tr>
					<th class="${ planoMatriculaIngressantesMBean.portalCoordenadorGraduacao ? 'rotulo' : 'obrigatorio' }">
						Curso:
					</th>
					<td>
					 	<h:outputText value="#{ planoMatriculaIngressantesMBean.obj.curso.nome }" rendered="#{ planoMatriculaIngressantesMBean.portalCoordenadorGraduacao}" />
						<h:selectOneMenu value="#{ planoMatriculaIngressantesMBean.obj.curso.id }" id="curso" 
							rendered="#{ not planoMatriculaIngressantesMBean.portalCoordenadorGraduacao}" onchange="submit()"
							valueChangeListener="#{ planoMatriculaIngressantesMBean.carregaMatrizesCurriculares }">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ cursoGrad.allCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				<c:if test="${ planoMatriculaIngressantesMBean.obj.graduacao }">
					<tr>
						<th class="obrigatorio">Matriz Curricular:</th>
						<td>
							<h:selectOneMenu value="#{ planoMatriculaIngressantesMBean.obj.matrizCurricular.id }" 
								id="matrizCurricular" rendered="#{ planoMatriculaIngressantesMBean.obj.graduacao}"
								valueChangeListener="#{ planoMatriculaIngressantesMBean.carregaTurmasAbertas }"
								onchange="submit()">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{ planoMatriculaIngressantesMBean.matrizCurricularCombo }"/>
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio">Capacidade:</th>
					<td>
						<h:inputText value="#{ planoMatriculaIngressantesMBean.obj.capacidade }" id="capacidade"
							size="3" maxlength="3" immediate="true"
							onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
					</td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">
						<h:outputText value="Turmas com Reserva de Vagas para #{ planoMatriculaIngressantesMBean.obj.matrizCurricular }" rendered="#{ planoMatriculaIngressantesMBean.obj.matrizCurricular.id > 0 }" />
						<h:outputText value="Turmas Abertas" rendered="#{ planoMatriculaIngressantesMBean.obj.matrizCurricular.id == 0 }" />
					</td>
				</tr>
				<c:if test="${empty planoMatriculaIngressantesMBean.turmasAbertas}">
					<tr>
						<td colspan="2" style="text-align:center;">
							Não foram encontradas turmas abertas para os parâmetros informados.
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty planoMatriculaIngressantesMBean.turmasAbertas}">
					<tr>
						<td colspan="2">
							<div class="infoAltRem">
								<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<table class="listagem" id="lista-turmas-curriculo" >
							<thead>
							<tr>
								<th colspan="3"> </th>
								<th> Turma </th>
								<th width="30%"> Docente(s) </th>
								<th width="20%"> Horário </th>
								<th> Local </th>
							</tr>
							</thead>
							<tbody>
							<c:set var="semestreAtual" value="0" />
							<c:set var="disciplinaAtual" value="0" />
							<c:forEach items="${planoMatriculaIngressantesMBean.turmasAbertas}" var="sugestao" varStatus="status">
								<%-- Semestre de Oferta --%>
								<c:if test="${ semestreAtual != sugestao.nivel}">
									<c:set var="semestreAtual" value="${sugestao.nivel}" />
									<c:if test="${matriculaGraduacao.graduacao}">
										<tr class="periodo">
											<td width="2%">
											<a name="nivel_${sugestao.nivel}"></a>
											<input type="checkbox" id="${sugestao.nivel}" class="noborder" onclick="marcarSemestre(this)"/>
											</td>
											<td colspan="6">
											<b><label for="${sugestao.nivel}">${sugestao.nivel}</label></b>
											</td>
										</tr>
									</c:if>
								</c:if>
								<%-- Componente Curricular --%>
								<c:if test="${ disciplinaAtual != sugestao.turma.disciplina.id}">
									<c:set var="disciplinaAtual" value="${sugestao.turma.disciplina.id}" />
									<tr class="disciplina" >
										<td>
										</td>
										<td colspan="4" style="font-size: xx-small">
											<a href="javascript:void(0);"
												onclick="PainelComponente.show(${sugestao.turma.disciplina.id}, '#nivel_${sugestao.nivel}')" title="Ver Detalhes do Componente Curricular">
												* ${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
											</a>
											<c:if test="${not empty sugestao.obrigatoriaDescricao}">
											<span style="font-size: xx-small; font-style: italic;">(${sugestao.obrigatoriaDescricao})</span>
											</c:if>
										</td>
										<td style="font-size: 9px; font-family: Arial; font-weight: normal; text-align: right;" colspan="2">
											<c:if test="${sugestao.buscarEquivalentes}">
												[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.equivalencia}')" title="Mostrar disciplinas equivalentes">Equivalentes</a>]
												<br />
											</c:if>
											<c:if test="${sugestao.buscarPreRequisitos}">
												[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.preRequisito}')" title="Mostrar disciplinas de pré-requisitos">Pré-requisitos</a>]
												<br />
											</c:if>
											<c:if test="${sugestao.buscarCoRequisitos}">
												[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.coRequisito}')" title="Mostrar disciplinas de pré-requisitos">Co-requisitos</a>]
												<br />
											</c:if>
										</td>
									</tr>
								</c:if>
				
								<%-- Turma --%>
								<c:set value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" var="stLinha" />
								<tr class="${stLinha} turmas${sugestao.turma.disciplina.id}" style="display:table-row"
										id="cc_${sugestao.turma.disciplina.id}t_${sugestao.turma.codigo}s_${sugestao.nivel}TR">
									<td>
									</td>
									<td width="2%">
										<c:if test="${sugestao.podeMatricular}">
										<a href="javascript:void(0);" onclick="PainelTurma.show(${sugestao.turma.id})" title="Ver Detalhes dessa turma">
											<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt=""
												class="noborder" />
										</a>
										</c:if>
									</td>
									<td width="2%">
										<c:set var="idCheckbox" value="cc_${sugestao.turma.disciplina.id}t_${sugestao.turma.codigo}s_${sugestao.nivel}CHK" />
										<c:if test="${sugestao.temReservaPraMatriz}">
											<c:set var="idCheckbox" value="${idCheckbox}'_temReservas'" />
										</c:if>
										<input type="checkbox" name="selecaoTurmas" value="${sugestao.turma.id}" ${sugestao.selected ? 'checked="checked"' : ''}
											id="${idCheckbox}" class="noborder" onclick="marcarTurma(this)"/>
									</td>
									<td width="11%" style="font-size: xx-small">
										<label for="${idCheckbox}">Turma ${sugestao.turma.codigo} <h:outputText value="#{sugestao.selected}"/></label>
									</td>
									<td style="font-size: xx-small">
										<label for="${idCheckbox}">
										<c:if test="${not empty sugestao.turma.observacao}">
										   <strong><ufrn:format valor="${sugestao.turma.observacao}" type="texto" length="60"/></strong> - 
										</c:if>
										<c:if test="${not empty sugestao.turma.especializacao.descricao}">
										  	<strong>${sugestao.turma.especializacao.descricao}</strong> - 
										</c:if>
										${sugestao.docentesNomes}
										</label>
									</td>
									<td width="10%" style="font-size: xx-small">
										<label for="${idCheckbox}">${sugestao.turma.descricaoHorario}</label>
									</td>
									<td width="7%" style="font-size: xx-small">
										<label for="${idCheckbox}">${sugestao.turma.local}</label>
									</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{planoMatriculaIngressantesMBean.listar}" id="cancelar" onclick="#{ confirm }"/>
						<h:commandButton value="Próximo Passo >>" action="#{planoMatriculaIngressantesMBean.submeterDadosGerais}" id="submeter"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
