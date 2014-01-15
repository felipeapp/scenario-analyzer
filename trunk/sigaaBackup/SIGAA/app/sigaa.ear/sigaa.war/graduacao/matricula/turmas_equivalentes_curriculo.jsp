<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>
<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<f:view>
<c:set value="turmas_equivalentes_curriculo" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<div class="descricaoOperacao" >
		<h:form id="formDescricao">
			Selecione uma ou mais turmas da lista abaixo e confirme a seleção através do botão <b>Adicionar Turmas</b>, localizado no final desta página.
			<br> Dúvidas sobre as disciplinas do seu currículo?
			<c:if test="${matriculaGraduacao.discente.graduacao}">
			<h:commandLink action="#{curriculo.gerarRelatorioCurriculo}" value="Clique Aqui" target="_blank" >
				<f:param name="id" value="#{matriculaGraduacao.discente.curriculo.id}" />
			</h:commandLink>
			</c:if>
			<c:if test="${matriculaGraduacao.discente.tecnico}">
				<h:commandLink action="#{estruturaCurricularTecnicoMBean.view}" value="Clique Aqui" target="_blank">
					<f:param name="id" value="#{matriculaGraduacao.discente.estruturaCurricularTecnica.id}" />
				</h:commandLink>
			</c:if>
			para ver seu currículo.
		</h:form>
	</div>
	<%@ include file="/graduacao/matricula/cabecalho_botoes_superiores.jsp"%>
	<%@ include file="_info_discente.jsp"%>

	<h:form>
	<input type="hidden" value="${matriculaGraduacao.discente.curriculo.id}" name="id" />

	<%-- Instruções --%>
	</h:form>
	<h:form>

	<%-- Legenda --%>
	<div class="infoAltRem">
		<img src="/sigaa/img/graduacao/matriculas/matricula_negada.png">: Não é permitido a matrícula do discente nessa turma
		<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
		<br/><img src="/sigaa/img/graduacao/matriculas/matricula_permitida.png">: É permitida a matrícula nesse componente
		<c:if test="${matriculaGraduacao.discente.graduacao}">
			<img src="/sigaa/img/graduacao/matriculas/matricula_tem_reservas.png" alt="" class="noborder" />: Turma possui reservas para seu curso
		</c:if>
	</div>

		<table class="listagem" id="lista-turmas-curriculo" >
			<caption>Turmas Abertas para os Componentes Equivalentes àqueles presentes em seu Currículo</caption>

			<thead>
			<tr>
				<th colspan="3"> </th>
				<th> Turma </th>
				<th> Docente(s) </th>
				<th> Horário </th>
				<th> Local </th>
			</tr>
			</thead>
			
			<tbody>

			<tbody>
			<c:if test="${empty matriculaGraduacao.turmasEquivalentesCurriculo}">
				<tr>
					<td colspan="7">
					<center>
						Não foi possível encontrar sugestões de matrícula em equivalentes para esse currículo.<br>
					</center>
					</td>
				</tr>
			</c:if>

			<c:set var="semestreAtual" value="0" />
			<c:set var="componenteCurricularAtual" value="0" />
			<c:set var="disciplinaAtual" value="0" />
			<c:if test="${not empty matriculaGraduacao.turmasEquivalentesCurriculo}">
			<c:forEach items="#{matriculaGraduacao.turmasEquivalentesCurriculo}" var="equivalente" varStatus="statusEquivalente">

				<%-- Semestre de Oferta --%>
				<c:if test="${ semestreAtual != equivalente.curriculoComponente.semestreOferta}">
					<c:set var="semestreAtual" value="${equivalente.curriculoComponente.semestreOferta}" />
					<c:if test="${matriculaGraduacao.graduacao}">
						<tr class="periodo">
							<td width="2%">
								<a name="nivel_${semestreAtual}" />
							</td>
							<td colspan="6">
								<b>${semestreAtual} ºNível</b>
							</td>
						</tr>
					</c:if>
				</c:if>

				<%-- Componente Curricular --%>
				<c:if test="${ componenteCurricularAtual != equivalente.componente.id && not empty equivalente.sugestoes}">
					<c:set var="componenteCurricularAtual" value="${equivalente.componente.id}" />
						<tr class="componente_curricular">
							<td colspan="7">
								<span class="label_equivalencia"> Componente do Currículo:</span>
								<a href="javascript:void(0);" onclick="PainelComponente.show(${equivalente.componente.id}, '#nivel_${semestreAtual}')" title="Ver Detalhes do Componente Curricular">
									${equivalente.componente.codigo} - ${equivalente.componente.nome}
								</a> 
								<c:if test="${not empty equivalente.curriculoComponente.descricaoObrigatoria}">
									<span style="font-size: xx-small; font-style: italic;">(${equivalente.curriculoComponente.descricaoObrigatoria})</span>
								</c:if>
								<br />
								<span class="label_equivalencia"> Equivalente a:</span>
								<span class="equivalencia">
									<sigaa:expressao expr="${equivalente.componente.equivalencia}"/>
								</span>
								<c:if test="${equivalente.matriculadoEquivalente}"> (MATRICULADO) </c:if>
								<c:if test="${equivalente.cumpriuEquivalente}"> (PAGO) </c:if>
							</td>
						</tr>
				</c:if>

				<c:forEach items="#{equivalente.sugestoes}" var="sugestao" varStatus="status">
				<%-- Componente Curricular --%>
				<c:if test="${ disciplinaAtual != sugestao.turma.disciplina.id}">
					<c:set var="disciplinaAtual" value="${sugestao.turma.disciplina.id}" />
					<tr class="disciplina" >
						<td>
						<c:if test="${not sugestao.podeMatricular}">
						<ufrn:help img="/img/graduacao/matriculas/matricula_negada.png" width="400">
							<li>${sugestao.motivoInvalido }</li>
						</ufrn:help>
						</c:if>
						<c:if test="${sugestao.podeMatricular}">
							<img alt="É permitida a matrícula nesse componente" title="É permitida a matrícula nesse componente" src="/sigaa/img/graduacao/matriculas/matricula_permitida.png">
						</c:if>
						</td>
						<td colspan="4" style="font-size: xx-small">
							<a href="javascript:void(0);" style="${sugestao.podeMatricular?'':'color: #666'}"
								onclick="PainelComponente.show(${sugestao.turma.disciplina.id}, '#nivel_${semestreAtual}')" title="Ver Detalhes do Componente Curricular">
							${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
							</a>
							<c:if test="${not empty sugestao.obrigatoriaDescricao}">
							<span style="font-size: xx-small; font-style: italic;">(${sugestao.obrigatoriaDescricao})</span>
							</c:if>
							<c:if test="${not sugestao.podeMatricular}">
							<span onclick="esconderMostrar(${sugestao.turma.disciplina.id})" onmouseover="this.style.cursor='pointer'" title="exibir/esconder turmas">
							[ + ]
							</span>
							</c:if>
						</td>
						<td style="font-size: 9px; font-family: Arial; font-weight: normal; text-align: right;" colspan="2">
							<c:if test="${sugestao.buscarEquivalentes}">
								[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.equivalencia}')" title="Mostrar disciplinas equivalentes">
								Equivalentes</a>]
							</c:if>
							<c:if test="${sugestao.buscarPreRequisitos}">
								[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.preRequisito}')" title="Mostrar disciplinas de pré-requisitos">
								Pré-requisitos</a>]
							</c:if>
							<c:if test="${sugestao.buscarCoRequisitos}">
								[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.coRequisito}')" title="Mostrar disciplinas de pré-requisitos">
								Co-requisitos</a>]
							</c:if>
						</td>
					</tr>
				</c:if>

				<%-- Turma --%>
				<c:set value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" var="stLinha" />
				<tr class="${stLinha} turmas${sugestao.turma.disciplina.id}" style="display: ${sugestao.podeMatricular ? 'table-row':'none'}"
						id="cc_${sugestao.turma.disciplina.id}t_${sugestao.turma.codigo}s_${sugestao.nivel}TR">
					<td>
					<c:if test="${sugestao.temReservaPraMatriz}">
						<ufrn:help img="/img/graduacao/matriculas/matricula_tem_reservas.png">
							<li>Essa turma possui vagas reservadas para seu curso</li>
						</ufrn:help>
					</c:if>
					</td>
					<td width="2%">
						<c:if test="${sugestao.podeMatricular}">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${sugestao.turma.id})" title="Ver Detalhes dessa turma">
							<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="Ver detalhes da turma"
								class="noborder" />
						</a>
						</c:if>
					</td>
					<td width="2%">
						<c:set var="idCheckbox" value="cc_${sugestao.turma.disciplina.id}t_${sugestao.turma.codigo}s_${sugestao.nivel}CHK" />
						<c:if test="${sugestao.temReservaPraMatriz}">
							<c:set var="idCheckbox" value="${idCheckbox}'_temReservas'" />
						</c:if>
						<c:if test="${sugestao.podeMatricular}">
							<input type="checkbox" name="selecaoTurmas" value="${sugestao.turma.id}"
								id="${idCheckbox}" class="noborder" onclick="marcarTurma(this)"/>
						</c:if>
					</td>
					<td width="11%" style="font-size: xx-small">
						<label for="${idCheckbox}">Turma ${sugestao.turma.codigo}</label>
					</td>
					<td style="font-size: xx-small">
						<label for="${idCheckbox}">
						${sugestao.docentesNomes}
						<c:if test="${not empty sugestao.turma.especializacao.descricao}">
						 - ${sugestao.turma.especializacao.descricao}
						</c:if>
						<c:if test="${not empty sugestao.turma.observacao}">
						 - ${sugestao.turma.especializacao.observacao}
						</c:if>
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

			</c:forEach>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="7" align="center">
						<div class="botoes confirmacao">
							<h:commandButton title="Adicionar as turmas selecionadas à solicitação de matrícula" id="addTurmasSolicitacao"
								image="/img/graduacao/matriculas/adicionar.gif"
								action="#{matriculaGraduacao.selecionarTurmas}"/><br />
							<h:commandLink value="Adicionar Turmas" action="#{matriculaGraduacao.selecionarTurmas}" id="btnaddTurmas"></h:commandLink>
						</div>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
</f:view>
<script type="text/javascript">
<!--
remarcarTurmasSubmetidas();

//-->
</script>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script>
	document.location = '#';
</script>