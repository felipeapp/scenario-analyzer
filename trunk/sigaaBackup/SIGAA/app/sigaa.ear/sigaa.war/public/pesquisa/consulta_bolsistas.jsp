<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>
	 Consulta de Bolsistas de Iniciação Científica
</h2>
<br>
<style>
	span.info {
		font-size: 0.8em;
		color: #444;
	}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h:outputText value="#{consultaBolsistas.create}"/>
	<h:form id="formConsulta">

		<table class="formulario" align="center" style="width: 80%">
		<caption class="listagem">Critérios de Busca dos Bolsistas</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroAluno}" id="checkAluno" styleClass="noborder"/> </td>
				<td width="22%">Aluno:</td>
				<td>
					<h:inputText id="nomeDiscente"	value="#{consultaBolsistas.obj.discente.pessoa.nome}" onchange="$('formConsulta:checkAluno').checked = true;" style="width:75%"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroOrientador}" id="checkOrientador" styleClass="noborder"/> </td>
				<td width="22%">Orientador:</td>
				<td>
					<h:inputText id="nomeOrientador" value="#{consultaBolsistas.membroProjeto.servidor.pessoa.nome}" onchange="$('formConsulta:checkOrientador').checked = true;" style="width:75%"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroModalidade}" id="checkModalidade" styleClass="noborder"/> </td>
				<td width="22%">Modalidade da Bolsa:</td>
				<td>
					<h:selectOneMenu id="modalidade" value="#{consultaBolsistas.obj.planoTrabalho.tipoBolsa.id}" onchange="$('formConsulta:checkModalidade').checked = true;" style="width:75%">
						<f:selectItem itemLabel=" -- SELECIONE UMA MODALIDADE -- " itemValue="0"/>
						<f:selectItems value="#{tipoBolsaPesquisa.allAtivosCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroCentro}" id="checkCentro" styleClass="noborder"/> </td>
				<td width="22%">Centro:</td>
				<td>
					<h:selectOneMenu id="centro" value="#{consultaBolsistas.centro.id}" onchange="$('formConsulta:checkCentro').checked = true;" style="width:75%">
						<f:selectItem itemLabel=" -- SELECIONE UM CENTRO ACADÊMICO -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allCentroCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="22%">Curso:</td>
				<td>
					<h:selectOneMenu id="curso" value="#{consultaBolsistas.obj.discente.curso.id}" onchange="$('formConsulta:checkCurso').checked = true;" style="width:95%">
						<f:selectItem itemLabel=" -- SELECIONE UM CURSO -- " itemValue="0"/>
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroGrupo}" id="checkGrupo" styleClass="noborder"/> </td>
				<td width="22%">Grupo de Pesquisa:</td>
				<td>
					<h:selectOneMenu id="grupo" value="#{consultaBolsistas.obj.planoTrabalho.projetoPesquisa.linhaPesquisa.grupoPesquisa.id}" onchange="$('formConsulta:checkGrupo').checked = true;" style="width:95%">
						<f:selectItem itemLabel="  -- SELECIONE UM GRUPO DE PESQUISA --  " itemValue="0"/>
						<f:selectItems value="#{consultaProjetos.allGruposCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
<%--			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroUnidade}" id="checkUnidade" styleClass="noborder"/> </td>
				<td width="22%">Departamento:</td>
				<td>
					<h:inputText id="nomeUnidade" value="#{consultaBolsistas.unidade.nome}" onchange="$('formConsulta:checkUnidade').checked = true;" style="width:75%"/>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaBolsistas.filtroSexo}" id="checkSexo" styleClass="noborder"/> </td>
				<td width="22%">Sexo:</td>
				<td>
					<h:selectOneRadio id="sexo" value="#{consultaBolsistas.obj.discente.pessoa.sexo}" onclick="$('formConsulta:checkSexo').checked = true;">
						<f:selectItem itemLabel="Masculino" itemValue="M"/>
						<f:selectItem itemLabel="Feminino" itemValue="F"/>
					</h:selectOneRadio>
				</td>
			</tr>
--%>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{consultaBolsistas.buscar}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{consultaBolsistas.cancelar}" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
		</table>

	</h:form>

	<style>
		.listagem tr.topo td {
			padding: 6px 2px 1px;
			border-bottom: 1px solid #DDD;
			font-weight: bold;
		}

		span.matricula {
			font-size: 0.9em;
			font-weight: normal;
			font-style: italic;
		}

		table.listagem tr.cota td {
			background-color: #C4D2EB;
			padding: 8px 10px 2px;
			border-bottom: 1px solid #BBB;
			font-variant: small-caps;

			font-style: italic;
		}
	</style>

	<c:set var="lista" value="${consultaBolsistas.resultadosBusca}"/>

	<c:if test="${not empty lista}">
	<br>
	<table class="listagem" style="width: 95%;">
		<caption> Alunos encontrados </caption>
		<thead>
			<tr>
				<th width="60%"> Discente </th>
				<th width="25%" style="text-align: center"> Tipo de Bolsa</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="cota" />
			<c:forEach var="membro" items="${lista}" varStatus="status">
			<c:set var="stripes">${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }</c:set>

			<c:if test="${ cota != membro.planoTrabalho.cota.descricao }">
				<c:set var="cota" value="${ membro.planoTrabalho.cota.descricao }" />
				<tr class="cota">
					<td colspan="2"> Cota ${ cota } </td>
				</tr>
			</c:if>

			<tr class="${stripes} topo">
				<td> ${membro.discente.nome} <%-- <span class="matricula">(Mat. ${membro.discente.matricula})</span> --%> </td>
				<td align="center"> ${membro.tipoBolsaString} </td>
			</tr>
			<tr class="${stripes}">
				<td valign="top" colspan="2">
					&nbsp;&nbsp;&nbsp;<i>Título:</i>
					${not empty membro.planoTrabalho.titulo ? membro.planoTrabalho.titulo : "não cadastrado"}
				</td>
			</tr>
			<tr class="${stripes}">
				<td valign="top"> &nbsp;&nbsp;&nbsp;<i>Orientador:</i> ${membro.planoTrabalho.orientador.pessoa.nome }</td>
				<td valign="top" nowrap="nowrap"> <%-- <i>Projeto:</i> ${membro.planoTrabalho.projetoPesquisa.codigo } --%> </td> 
			</tr>
			<tr class="${stripes}">
				<td valign="top" colspan="2">
					&nbsp;&nbsp;&nbsp;<i>Período:</i>
					<ufrn:format type="data" name="membro" property="dataInicio" />
					<c:if test="${ not empty membro.dataFim }">
						a <ufrn:format type="data" name="membro" property="dataFim" />
					 </c:if>
				</td>
			</tr>

			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4" align="center"> <b>${fn:length(lista)} bolsistas encontrados </b></td>
			</tr>
		</tfoot>
	</table>
	</c:if>
</f:view>
<br>
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;"><< voltar ao menu principal</a>
	</div>
<br>
<%@include file="/public/include/rodape.jsp" %>
