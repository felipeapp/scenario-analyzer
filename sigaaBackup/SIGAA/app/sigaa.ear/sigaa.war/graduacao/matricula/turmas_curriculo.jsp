<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<f:view>
	<c:set value="turmas_curriculo" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<div class="descricaoOperacao">
		<h:form id="formDescricao">
			Selecione uma ou mais turmas da lista abaixo e confirme a seleção através do botão <b>Adicionar Turmas</b>, localizado no final desta página.<br/>
			Dúvidas sobre as disciplinas do seu currículo?
			<c:if test="${matriculaGraduacao.discente.graduacao}">
				<h:commandLink action="#{curriculo.gerarRelatorioCurriculo}" value="Clique Aqui" target="_blank">
					<f:param name="id" value="#{matriculaGraduacao.discente.curriculo.id}" />
				</h:commandLink>
			</c:if>
			<c:if test="${matriculaGraduacao.discente.tecnico}">
				<h:commandLink action="#{estruturaCurricularTecnicoMBean.view}" value="Clique Aqui" target="_blank">
					<f:param name="id" value="#{matriculaGraduacao.discente.estruturaCurricularTecnica.id}" />
				</h:commandLink>
			</c:if>
			para ver os detalhes de sua estrutura curricular.
		</h:form>
	</div>  
	<%@ include file="/graduacao/matricula/cabecalho_botoes_superiores.jsp"%>
	<%@ include file="_info_discente.jsp"%>

	<h:form>
	<input type="hidden" value="${matriculaGraduacao.discente.curriculo.id}" name="id" />

	</h:form>
	
	<h:form>
	<%-- Legenda --%>
	<div class="infoAltRem">
		<img src="/sigaa/img/graduacao/matriculas/matricula_permitida.png" alt="*">: É permitida a matrícula nesse componente
		<img src="/sigaa/img/graduacao/matriculas/matricula_negada.png">: Não é permitida a matrícula nesse componente <br />
		<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
		<c:if test="${matriculaGraduacao.discente.graduacao}">
			<img src="/sigaa/img/graduacao/matriculas/matricula_tem_reservas.png" alt="" class="noborder" />: Turma possui reservas para seu curso
		</c:if>
	</div>

		<table class="listagem" id="lista-turmas-curriculo" >
			<caption>Turmas Abertas para os Componentes do seu Currículo</caption>

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

			<c:if test="${empty matriculaGraduacao.turmasCurriculo}">
				<tr>
					<td colspan="7">
					<center>
						Não foi possível encontrar sugestões de matrícula para esse discente.<br>
					</center>
					</td>
				</tr>
			</c:if>

			<c:set var="semestreAtual" value="0" />
			<c:set var="disciplinaAtual" value="0" />
			<h:outputText value="#{util.create}"></h:outputText>
			<c:forEach items="${matriculaGraduacao.turmasCurriculo}" var="sugestao" varStatus="status">

				<%-- Semestre de Oferta --%>
				<c:if test="${ semestreAtual != sugestao.nivel}">
					<c:set var="semestreAtual" value="${sugestao.nivel}" />
					<c:if test="${matriculaGraduacao.graduacao}">
						<tr class="periodo">
							<td width="2%">
							<a name="nivel_${sugestao.nivel}"></a>
							<c:if test="${matriculaGraduacao.exibirQuadroHorarios}">
							<input type="checkbox" id="${sugestao.nivel}" class="noborder" onclick="marcarSemestre(this)"/>
							</c:if>
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
								onclick="PainelComponente.show(${sugestao.turma.disciplina.id}, '#nivel_${sugestao.nivel}')" title="Ver Detalhes do Componente Curricular">
							<c:choose>
								<c:when test="${not sugestao.podeMatricular}">
									${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
								</c:when>
								<c:otherwise>
									* ${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
								</c:otherwise>
							</c:choose>
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
			<tfoot>
				<tr>
					<td colspan="7" align="center">
						<div class="botoes confirmacao">
							<h:commandButton title="Adicionar as turmas selecionadas à solicitação de matrícula" id="adicionarTurmasSolicitacaoMatricula"
								image="/img/graduacao/matriculas/adicionar.gif"
								action="#{matriculaGraduacao.selecionarTurmas}"/><br />
							<h:commandLink value="Adicionar Turmas" action="#{matriculaGraduacao.selecionarTurmas}" id="btaoSelecionarTurmas"></h:commandLink>
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