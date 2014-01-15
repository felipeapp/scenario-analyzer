<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<f:view>
	<%@ include file="/stricto/matricula/outros_programas/botoes_operacao.jsp"%>
	<c:set value="turmas_outros_programas" var="pagina"></c:set>

	<table class="visualizacao">
	<tr>
		<th width="20%"> Discente: </th>
		<td> ${matriculaStrictoBean.discente} </td>
	</tr>
	<tr>
		<th width="20%"> Curso: </th>
		<td> ${matriculaStrictoBean.discente.curso.nomeCursoStricto} </td>
	</tr>
	<tr>
		<th> Currículo: </th>
		<td> ${matriculaStrictoBean.discente.curriculo.codigo} </td>
	</tr>
	</table>

	<br />
	<h:form id="form">
		<c:if test="${empty matriculaOutroProgramaStrictoBean.resultadoTurmasBuscadas}">
		<br><center style="font-style: italic;">
			Informe critérios para refinar a busca de turmas abertas.
			</center>
		</c:if>
		<c:if test="${not empty matriculaOutroProgramaStrictoBean.resultadoTurmasBuscadas}">
			<br>

			<c:if test="${not empty matriculaOutroProgramaStrictoBean.resultadoTurmasBuscadas}">
			<%-- Instruções --%>
			<div class="descricaoOperacao" style="width: 70%;text-align: center;">
				Selecione uma ou mais turmas da lista abaixo e confirme a seleção através do botão <b>Adicionar Turmas</b>, localizado no final desta página.
			</div>
			</c:if>

			<div class="infoAltRem">
				<h4> Legenda</h4>
				<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
			</div>

			<%-- TURMAS ENCONTRADAS --%>
			<table class="listagem" id="lista-turmas-extra">
			<caption>Turmas Abertas Encontradas
			<c:if test="${matriculaGraduacao.discente.stricto and matriculaGraduacao.primeiraTurmaBusca != null}">
				de ${matriculaGraduacao.primeiraTurmaBusca.anoPeriodo}
			</c:if>
			</caption>

			<thead>
			<tr>
				<th colspan="2"> </th>
				<th> Turma </th>
				<th> Docente(s) </th>
				<th> Tipo </th>
				<th> Horário </th>
				<th> Local </th>
				<th> Capacidade</th>
			</tr>
			</thead>
			
			<tbody>
			<c:if test="${not empty matriculaOutroProgramaStrictoBean.resultadoTurmasBuscadas}">
			<c:set var="disciplinaAtual" value="0" />
			<c:set var="atualPrograma" value="0" />
			<c:forEach items="#{matriculaOutroProgramaStrictoBean.resultadoTurmasBuscadas}" var="turma" varStatus="s">

				<%-- Programa --%>
				<c:if test="${ atualPrograma != turma.disciplina.unidade.id}">
					<c:set var="atualPrograma" value="${turma.disciplina.unidade.id}" />
					<tr class="periodo">
						<td colspan="8">
							<b>${turma.disciplina.unidade}</b>
						</td>
					</tr>
				</c:if>

				<%-- Componente Curricular --%>
				<c:if test="${ disciplinaAtual != turma.disciplina.id}">
					<c:set var="disciplinaAtual" value="${turma.disciplina.id}" />
					<tr class="disciplina" >
					<td colspan="8" style="font-variant: small-caps;">
						<a href="javascript:void(0);" onclick="PainelComponente.show(${turma.disciplina.id});"
							title="Ver Detalhes do Componente Curricular">
						${turma.disciplina.codigo} - ${turma.disciplina.detalhes.nome}
						</a>
					</td></tr>
				</c:if>

				<c:set value="turma_${turma.id}CHK" var="idCheckbox"></c:set>
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small" id="turma_${turma.id}TR">
					<td width="2%">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${turma.id});" title="Ver Detalhes dessa turma">
							<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="" class="noborder" />
						</a>
					</td>
					<td width="2%">
						<input type="checkbox" name="selecaoTurmas" onclick="markCheck(this)" value="${turma.id}" id="${idCheckbox}" class="noborder"/>
					</td>
					<td width="8%"><label for="${idCheckbox}">Turma ${turma.codigo}</label></td>
					<td><label for="${idCheckbox}">${turma.docentesNomes}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.tipoString}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.descricaoHorario}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.local}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.qtdMatriculados}/${turma.capacidadeAluno} alunos</label></td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr>
					<td colspan="8" align="center">
						<div class="botoes confirmacao">
							<h:commandButton title="Adicionar as turmas selecionadas à solicitação de matrícula" image="/img/graduacao/matriculas/adicionar.gif" action="#{matriculaOutroProgramaStrictoBean.selecionarTurmas}" id="cmdTurmas"/>
							<br />
							<h:commandLink value="Adicionar Turmas" action="#{matriculaOutroProgramaStrictoBean.selecionarTurmas}" id="botaoAdiiconarTurmas"></h:commandLink>
						</div>
					</td>
				</tr>
			</tfoot>
			</c:if>
		</table>
		</c:if>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>