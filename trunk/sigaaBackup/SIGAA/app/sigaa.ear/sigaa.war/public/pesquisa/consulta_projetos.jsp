<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">

	var prefixTr = "descricao_";

	function showDescricao(resultado){

		var index = resultado.id.substring(10);
		elementName = prefixTr + index;
		var elemento = getEl(elementName);

		elemento.setDisplayed(!elemento.isDisplayed());
	}

</script>


<h2>
	 Quem pesquisa o que na ${ configSistema['siglaInstituicao'] }?
</h2>
<br>
<style>
	span.info {
		font-size: 0.8em;
		color: #666;
	}

	p.descricao{
		padding: 10px 20px;
		font-style: italic;
		background: #F9F9F9;
		border: 1px solid #EEE;
		margin: 5px 20px;
	}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h:outputText value="#{consultaProjetos.create}"/>
	<h:form id="formConsulta">

		<table class="formulario" align="center" style="width: 80%">
		<caption class="listagem">Critérios de Busca dos Projetos</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroTitulo}" id="checkTitulo" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkTitulo">Título:</label></td>
				<td>
					<h:inputText id="titulo" value="#{consultaProjetos.obj.titulo}" onchange="$('formConsulta:checkTitulo').checked = true;" style="width: 75%;"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroObjetivos}" id="checkObjetivos" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkObjetivos">Objetivos <i>(Palavras chaves)</i>:</label></td>
				<td>
					<h:inputText id="objetivos" value="#{consultaProjetos.obj.objetivos}" onchange="$('formConsulta:checkObjetivos').checked = true;" style="width:75%"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroPesquisador}" id="checkPesquisador" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkPesquisador">Pesquisador:</label></td>
				<td>
					<h:inputText id="nomePesquisador"value="#{consultaProjetos.pesquisador.pessoa.nome}" onchange="$('formConsulta:checkPesquisador').checked = true;" style="width:75%"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroAno}" id="checkAno" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkAno">Ano:</label></td>
				<td><h:inputText onkeyup="return formatarInteiro(this);" id="ano" value="#{consultaProjetos.obj.ano}"  size="5" maxlength="4" onchange="$('formConsulta:checkAno').checked = true;"/></td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroCentro}" id="checkCentro" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkCentro">Centro:</label></td>
				<td>
					<h:selectOneMenu id="centro" value="#{consultaProjetos.obj.centro.id}" onchange="$('formConsulta:checkCentro').checked = true;" style="width:75%">
						<f:selectItem itemLabel=" -- SELECIONE UM CENTRO ACADÊMICO -- " itemValue="0"/>
						<f:selectItems value="#{unidade.centrosEspecificasEscolas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroUnidade}" id="checkUnidade" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkUnidade">Departamento:</label></td>
				<td>
					<h:selectOneMenu id="unidade" value="#{consultaProjetos.obj.unidade.id}" onchange="$('formConsulta:checkUnidade').checked = true;" style="width:75%">
						<f:selectItem itemLabel=" -- SELECIONE UM DEPARTAMENTO -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allDepartamentoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroArea}" id="checkArea" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkArea">Área de Conhecimento:</label></td>
				<td>
					<h:selectOneMenu id="area" value="#{consultaProjetos.obj.areaConhecimentoCnpq.id}" onchange="$('formConsulta:checkArea').checked = true;" style="width:95%">
						<f:selectItem itemLabel=" -- SELECIONE UMA ÁREA DE CONHECIMENTO -- " itemValue="0"/>
						<f:selectItems value="#{area.allAreas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroGrupo}" id="checkGrupo" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkGrupo">Grupo de Pesquisa (<i>Base</i>):</label></td>
				<td>
					<h:selectOneMenu id="grupo" value="#{consultaProjetos.obj.linhaPesquisa.grupoPesquisa.id}" onchange="$('formConsulta:checkGrupo').checked = true;" style="width:95%">
						<f:selectItem itemLabel="  -- SELECIONE UM GRUPO DE PESQUISA --  " itemValue="0"/>
						<f:selectItems value="#{consultaProjetos.allGruposCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{consultaProjetos.filtroAgencia}" id="checkAgencia" styleClass="noborder"/> </td>
				<td width="22%"><label for="formConsulta:checkAgencia">Agência Financiadora:</label></td>
				<td>
					<h:selectOneMenu id="agencia" value="#{consultaProjetos.financiamentoProjetoPesq.entidadeFinanciadora.id}" onchange="$('formConsulta:checkAgencia').checked = true;" style="width:75%">
						<f:selectItem itemLabel=" -- SELECIONE UMA AGÊNCIA FINANCIADORA -- " itemValue="0"/>
						<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{consultaProjetos.buscar}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{consultaProjetos.cancelar}" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
		</table>

	</h:form>

<style>
	table.listagem tr.ano td {
		font-weight: bold;
		background-color: #C4D2EB;
		padding: 3px 15px;
		border-bottom: 1px solid #CCC;
		font-size: 1.1em;
	}
	table.listagem tr.centro td {
		font-weight: bold;
		background-color: #C4D2EB;
		padding: 3px 15px;
		border-bottom: 1px solid #CCC;
		font-size: 1.5em;
		text-align: center;
	}
</style>

	<c:set var="lista" value="#{consultaProjetos.resultadosBusca}"/>

<c:if test="${not empty lista}">
<br>
<div class="legenda">
	<h:graphicImage url="/img/view.gif" />:&nbsp;Visualizar Projeto de Pesquisa
</div>
<h:form id="formConsultaProjeto">
	<table class="listagem" style="width: 99%;">
	<caption class="listagem">Lista de Projetos de Pesquisa Encontrados</caption>
	<thead>
		<tr>
			<th>Código</th>
			<th width="40%">Título / Coordenador</th>
			<th> &nbsp; </th>
			<th> Tipo </th>
			<th style="text-align: center;"> Situação </th>
			<th colspan="2"> &nbsp; </th>
		</tr>
	</thead>
	<tbody>
		<c:set var="centroProjeto" />
		<c:set var="anoProjeto" value="0" />
		<c:forEach var="projeto" items="#{lista}" varStatus="status">
	
		<c:if test="${projeto.centro.nome != centroProjeto}">
			<c:set var="centroProjeto" value="${projeto.centro.nome}" />
			<tr class="centro">
				<td colspan="7"> ${projeto.centro.nome} </td>
			</tr>
		</c:if>
	
		<c:if test="${projeto.codigo.ano != anoProjeto}">
			<c:set var="anoProjeto" value="${projeto.codigo.ano}" />
			<tr class="ano">
				<td colspan="7"> ${projeto.codigo.ano} </td>
			</tr>
		</c:if>
	
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td rowspan="2"> ${projeto.codigo} </td>
			<td colspan="5"> <i>${projeto.titulo}</i> </td>	
			<td rowspan="2">  </td>
		</tr>
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td colspan="2"> ${ not empty projeto.coordenador ? projeto.coordenador.pessoa.nome : "<i> Não Informado </i>" } </td>
			<td> ${ projeto.interno ? "INTERNO" : "EXTERNO" } </td>
			<td align="center"> ${projeto.situacaoProjeto.descricao}  </td>
			<td>
				<h:commandLink title="Visualizar Projeto de Pesquisa" action="#{consultaProjetos.view}">	
					<f:param name="id" value="#{projeto.id}"/>
					<h:graphicImage url="/img/view.gif"/><br clear="all"/>
				</h:commandLink>
			</td>
		</tr>
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td colspan="7">
				<p class="descricao" id="descricao_${status.index}">
					<ufrn:format type="texto" name="projeto" property="descricao" />
				</p>
			</td>
		</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7" style="text-align: center; font-weight: bold;">
				${ fn:length(lista) } projetos encontrados
			</td>
		</tr>
	</tfoot>
	</table>
</h:form>
</c:if>
</f:view>
<br>
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;"><< voltar ao menu principal</a>
	</div>
<br>
<%@include file="/public/include/rodape.jsp" %>

<script type="text/javascript">
	var lista = getEl(document).getChildrenByClassName('descricao')
	for (i = 0; i < lista.size(); i++) {
		lista[i].setDisplayed(false);
	}
</script>