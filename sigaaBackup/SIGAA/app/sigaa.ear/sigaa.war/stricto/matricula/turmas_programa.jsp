<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<f:view>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<c:set value="turmas_programa" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_botoes_superiores.jsp"%>

	<table class="visualizacao">
		<tr>
			<th width="20%"> Discente: </th>
			<td> ${matriculaStrictoBean.discente} </td>
		</tr>
		<c:if test="${!matriculaStrictoBean.discente.discente.especial}">
			<tr>
				<th width="20%"> Curso: </th>
				<td> ${matriculaStrictoBean.discente.curso.nomeCursoStricto} </td>
			</tr>
			<tr>
				<th> Currículo: </th>
				<td> ${matriculaStrictoBean.discente.curriculo.codigo} </td>
			</tr>		
		</c:if>
		<c:if test="${matriculaStrictoBean.discente.discente.especial}">
			<tr>
				<th width="20%"> Programa: </th>
				<td> ${matriculaStrictoBean.discente.gestoraAcademica.nome} </td>
			</tr>	
		</c:if>		
	</table>

	<h:form>
		<input type="hidden" value="${matriculaStrictoBean.discente.curriculo.id}" name="id" />
		<%-- Instruções --%>
		<div class="descricaoOperacao" style="width: 70%;text-align: center;">
			Selecione uma ou mais turmas da lista abaixo e confirme a seleção através do botão <b>Adicionar Turmas</b>, localizado no final desta página.
			
			<c:if test="${matriculaStrictoBean.discente.regular}">
				<br> 
				Dúvidas sobre as disciplinas do seu currículo?
				<h:commandLink action="#{curriculo.gerarRelatorioCurriculo}" value="Clique Aqui" target="_blank" id="linkGerarRelatorioCurriculo"/>
				para ver seu currículo.
			</c:if>
		</div>
	</h:form>
	
	<h:form>

	<%-- Legenda --%>
	<div class="infoAltRem">
		<h4> Legenda</h4>
		<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma <br />
		<img src="/sigaa/img/graduacao/matriculas/matricula_negada.png">: Não é permitida a matrícula do discente na turma
		<img alt="" src="/sigaa/img/graduacao/matriculas/matricula_permitida.png">: É permitida a matrícula do discente na turma
	</div>

		<table class="listagem" id="lista-turmas-curriculo" >
			<caption>Turmas Abertas para as Disciplinas do seu Programa</caption>

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
			<c:if test="${empty matriculaStrictoBean.sugestoesMatricula}">
				<tr>
					<td colspan="7">
					<center>
						Não foi possível encontrar sugestões de matrícula para esse discente.<br>
					</center>
					</td>
				</tr>
			</c:if>

			<c:set var="areaAtual" value="0" />
			<c:set var="disciplinaAtual" value="0" />
			<c:forEach items="${matriculaStrictoBean.sugestoesMatricula}" var="sugestao" varStatus="status">

				<%-- Área de Concentração --%>
				<c:if test="${areaAtual != sugestao.nivel}">
					<c:set var="areaAtual" value="${sugestao.nivel}" />
					<c:if test="${matriculaGraduacao.graduacao}">
						<tr class="periodo">
							<td colspan="7">
								<b>${sugestao.nivel}</b>
							</td>
						</tr>
					</c:if>
				</c:if>

				<%-- Disciplina --%>
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
								<img alt="É permitida a matrícula do discente na turma" title="É permitida a matrícula do discente na turma" src="/sigaa/img/graduacao/matriculas/matricula_permitida.png">
							</c:if>
						</td>
						<td colspan="4" style="font-size: xx-small">
							<a href="javascript:void(0);" style="${sugestao.podeMatricular?'':'color: #666'}"
								onclick="PainelComponente.show(${sugestao.turma.disciplina.id}, '#nivel_${sugestao.nivel}')" title="Ver Detalhes do Componente Curricular">
							${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
							</a>
							<c:if test="${not sugestao.podeMatricular}">
							<span onclick="esconderMostrar(${sugestao.turma.disciplina.id}, this)" onmouseover="this.style.cursor='pointer'" title="exibir/esconder turmas">
							[ + ]
							</span>
							</c:if>
						</td>
						<td style="font-size: 9px; font-family: Arial; font-weight: normal; text-align: right;" colspan="2">
							<c:if test="${sugestao.buscarEquivalentes}">
								[ <a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.equivalencia}')" title="Mostrar disciplinas equivalentes">
								Equivalentes</a>]
							</c:if>
							<c:if test="${sugestao.buscarPreRequisitos}">
								[ <a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.preRequisito}')" title="Mostrar disciplinas de pré-requisitos">
								Pré-requisitos</a>]
							</c:if>
							<c:if test="${sugestao.buscarCoRequisitos}">
								[ <a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.coRequisito}')" title="Mostrar disciplinas de pré-requisitos">
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
					</td>
					<td width="2%">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${sugestao.turma.id})" title="Ver Detalhes dessa turma">
							<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt=""
								class="noborder" />
						</a>
					</td>
					<td width="2%">
						<c:set var="idCheckbox" value="cc_${sugestao.turma.disciplina.id}t_${sugestao.turma.codigo}s_${sugestao.nivel}CHK" />
						<c:if test="${sugestao.podeMatricular}">
							<input type="checkbox" name="selecaoTurmas" value="${sugestao.turma.id}"
								id="${idCheckbox}" class="noborder" onclick="marcarTurma(this)"/>
						</c:if>
					</td>
					<td width="11%" style="font-size: xx-small">
						<label for="${idCheckbox}">Turma ${sugestao.turma.codigo}</label>
					</td>
					<td style="font-size: xx-small">
						<c:if test="${not empty sugestao.turma.observacao}">
						 <span style="text-transform: uppercase;"> 
							<label for="${idCheckbox}"><strong><ufrn:format valor="${sugestao.turma.observacao}" type="texto" length="60"/></strong>
							- </label>
						</span>
						</c:if>
						<label for="${idCheckbox}">
						${sugestao.docentesNomes}
						<c:if test="${not empty sugestao.turma.especializacao.descricao}">
						 - ${sugestao.turma.especializacao.descricao}
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
			<tfoot>
				<tr>
					<td colspan="7" align="center">
						<div class="botoes confirmacao">
							<h:commandButton title="Adicionar as turmas selecionadas à solicitação de matrícula" image="/img/graduacao/matriculas/adicionar.gif"
								id="linkAdicionaTurmasSelecionada" action="#{matriculaGraduacao.selecionarTurmas}"/><br />
							<h:commandLink value="Adicionar Turmas" action="#{matriculaGraduacao.selecionarTurmas}" id="adicionaTurmas"></h:commandLink>
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