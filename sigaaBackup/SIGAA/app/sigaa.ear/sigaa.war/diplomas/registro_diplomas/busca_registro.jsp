<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Busca por Registro de ${buscaRegistroDiploma.obj.livroRegistroDiploma.tipoRegistroDescricao } </h2>
<h:form id="form">

	<table class="formulario" width="75%">
		<caption>Informe os Parâmetros da Busca </caption>
		<c:if test="${fn:length(buscaDiscenteGraduacao.niveisHabilitados) > 1}">
			<tr>
				<td width="5%">
				</td>
				<th style="text-align: right;" width="130px" class="obrigatorio">Nível de Ensino:</th>
				<td> 
					<h:selectOneMenu value="#{buscaRegistroDiploma.obj.livroRegistroDiploma.nivel}" id="nivelEnsinoEspecifico">
						<f:selectItems value="#{buscaDiscenteGraduacao.niveisHabilitadosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
		<tr>
			<td width="5%">
				<h:selectBooleanCheckbox value="#{buscaRegistroDiploma.filtroMatricula}" id="filtroMatricula"/>
			</td>
			<td width="25%">Matrícula:</td>
			<td>
				<h:inputText value="#{buscaRegistroDiploma.obj.discente.matricula}" size="15" maxlength="15"  id="matricula"
				 onfocus="$('form:filtroMatricula').checked = true;" onkeyup="return formatarInteiro(this);" />
			</td>
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{buscaRegistroDiploma.filtroNome}" id="filtroNome"/>
			</td>
			<td>Nome:</td>
			<td>
				<h:inputText value="#{buscaRegistroDiploma.obj.discente.pessoa.nome}" size="50" maxlength="120" onfocus="$('form:filtroNome').checked = true;" id="nome"/>
			</td>
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{buscaRegistroDiploma.filtroNumero}" id="filtroNumero"/>
			</td>
			<td>Número de Registro:</td>
			<td>
				<h:inputText value="#{buscaRegistroDiploma.obj.numeroRegistro}" size="6" maxlength="6" onkeyup="return formatarInteiro(this);" onfocus="$('form:filtroNumero').checked = true;" id="numero"/>
			</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton action="#{buscaRegistroDiploma.buscar}" value="Buscar" id="buscar"/>
				<h:commandButton action="#{buscaRegistroDiploma.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
<br>
<c:if test="${not empty buscaRegistroDiploma.registrosEncontrados}">
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Registro
	</div>
	<br />
	<table class="listagem">
	<caption>Total de Registros Encontrados: ${fn:length(buscaRegistroDiploma.registrosEncontrados)}</caption>
	<thead>
		<tr>
			<td style="text-align: right;" width="10%">Matrícula</td>
			<td width="35%">Nome</td>
			<td width="25%">Curso</td>
			<td width="15%">Livro</td>
			<td style="text-align: right;" width="5%">Folha</td>
			<td style="text-align: right;" width="10%">Nº de Registro</td>
			<td width="5%"></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{buscaRegistroDiploma.registrosEncontrados}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td style="text-align: right;">${item.discente.matricula}</td>
			<td>${item.discente.nome}</td>
			<td>${item.discente.curso.descricao}</td>
			<td>${item.livroRegistroDiploma.titulo}</td>
			<td style="text-align: right;">${item.folha.numeroFolha}</td>
			<td style="text-align: right;">${item.numeroRegistro}</td>
			<td>
				<h:commandLink title="Selecionar Registro" style="border: 0;" action="#{buscaRegistroDiploma.selecionaRegistro}" id="selecionarRegistro" >
					<f:param name="id" value="#{item.id}" />
					<h:graphicImage url="/img/seta.gif" alt="Selecionar Registro" />
				</h:commandLink>
			</td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
</c:if>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>