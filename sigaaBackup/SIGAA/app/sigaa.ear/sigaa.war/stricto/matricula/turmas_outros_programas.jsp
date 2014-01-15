<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<f:view>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<c:set value="turmas_outros_programas" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_botoes_superiores.jsp"%>

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
		<table class="formulario" width="90%">
			<caption>Buscar Turmas Abertas</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkCodigo" value="#{matriculaGraduacao.boolDadosBusca[0]}" styleClass="noborder" />
					</td>
					<th><label for="form:checkCodigo">Código do Componente</label></th>
					<td><h:inputText size="10" value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.codigo }" id="codigoDisciplina"
						onfocus="marcaCheckBox('form:checkCodigo')" onkeyup="CAPS(this)" /></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNome" value="#{matriculaGraduacao.boolDadosBusca[1]}" styleClass="noborder" />
					</td>
					<th><label for="form:checkNome">Nome do Componente</label></th>
					<td><h:inputText size="60" value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.nome }" id="nomeDaDisciplina"
						onfocus="marcaCheckBox('form:checkNome')" onkeyup="CAPS(this)" /></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkUnidade" value="#{matriculaGraduacao.boolDadosBusca[4]}" styleClass="noborder" />
					</td>
					<th><label for="form:checkUnidade">Unidade Responsável</label></th>
					<td><h:selectOneMenu style="width: 400px"	value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.unidade.id}"
						onfocus="marcaCheckBox('form:checkUnidade')" id="comboDepartamento">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{unidade.allProgramaPosCombo}" />
					</h:selectOneMenu></td>
				</tr>
					
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{matriculaGraduacao.buscarOutrasTurmas}" id="botaoBuscarOutrasTurmas"/></td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${empty resultadoTurmasBuscadas}">
		<br><center style="font-style: italic;">
			Informe critérios para refinar a busca de turmas abertas.
			</center>
		</c:if>
		<c:if test="${not empty resultadoTurmasBuscadas}">
			<br>

			<c:if test="${not empty resultadoTurmasBuscadas}">
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
			<c:if test="${not empty resultadoTurmasBuscadas}">
			<c:set var="disciplinaAtual" value="0" />
			<c:set var="atualPrograma" value="0" />
			<c:forEach items="#{resultadoTurmasBuscadas}" var="turma" varStatus="s">

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
							<h:commandButton title="Adicionar as turmas selecionadas à solicitação de matrícula"
								image="/img/graduacao/matriculas/adicionar.gif"
								action="#{matriculaGraduacao.selecionarTurmas}" id="cmdTurmas"/><br />
							<h:commandLink value="Adicionar Turmas" action="#{matriculaGraduacao.selecionarTurmas}" id="botaoAdiiconarTurmas"></h:commandLink>
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