<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Projetos de Apoio a Grupos de Pesquisa</h2>
	<h:form id="formConsulta">

		<table class="formulario" align="center" width="60%">
		<caption class="listagem">Busca dos Projetos</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{buscaProjetoApoioGruposPesquisaMBean.checkAno}" id="checkAno" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkAno" onclick="$('formConsulta:checkAno').checked = !$('formConsulta:checkAno').checked;">Ano:</label></td>
				<td>
					<h:inputText id="ano" value="#{buscaProjetoApoioGruposPesquisaMBean.buscaAno}" size="4" maxlength="4" onfocus="$('formConsulta:checkAno').checked = true;" onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{buscaProjetoApoioGruposPesquisaMBean.checkTitulo}" id="checkTitulo" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkTitulo" onclick="$('formConsulta:checkTitulo').checked = !$('formConsulta:checkTitulo').checked;">Título:</label></td>
				<td>
					<h:inputText id="titulo" value="#{buscaProjetoApoioGruposPesquisaMBean.buscaTitulo}" size="60" onfocus="$('formConsulta:checkTitulo').checked = true;"/>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{buscaProjetoApoioGruposPesquisaMBean.checkCoordenador}" id="checkCoordenador" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkCoordenador" onclick="$('formConsulta:checkCoordenador').checked = !$('formConsulta:checkCoordenador').checked;">Coordenador:</label></td>
				<td>
					<h:inputHidden id="idServidor" value="#{buscaProjetoApoioGruposPesquisaMBean.buscaCoordenador.id}"/>
					<h:inputText id="nomeServidor" value="#{buscaProjetoApoioGruposPesquisaMBean.buscaCoordenador.pessoa.nome}" size="60" onkeyup="CAPS(this)"
						disabled="#{buscaCursoLatoMBean.readOnly}" readonly="#{buscaCursoLatoMBean.readOnly}" 
						onblur="$('formConsulta:checkCoordenador').checked = true;"/>

					<ajax:autocomplete source="formConsulta:nomeServidor" target="formConsulta:idServidor"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
							parser="new ResponseXmlToHtmlListParser()" />

					<span id="indicatorDocente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{buscaProjetoApoioGruposPesquisaMBean.checkStatus}" id="checkStatus" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkStatus" onclick="$('formConsulta:checkStatus').checked = !$('formConsulta:checkStatus').checked;">Status:</label></td>
				<td>
					<h:selectOneMenu id="status"
						value="#{buscaProjetoApoioGruposPesquisaMBean.buscaStatus}" style="width: 70%;"
						onfocus="$('formConsulta:checkStatus').checked = true;">
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM STATUS --"/>
						<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcoesAssociadasValidas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="btnBuscar" action="#{buscaProjetoApoioGruposPesquisaMBean.buscar}" value="Buscar"/>
					<h:commandButton id="btnCancelar" action="#{buscaProjetoApoioGruposPesquisaMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<br/>
		
		<c:if test="${not empty buscaProjetoApoioGruposPesquisaMBean.resultadosBusca}">
			<center>
					<h:messages/>
					<div class="infoAltRem">
						<h:form>
							<h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
					        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
						</h:form>
					</div>
			</center>
			<table class="formulario" width="100%">
				<caption> Projetos de Apoio a Grupos de Pesquisa (${ fn:length(buscaProjetoApoioGruposPesquisaMBean.resultadosBusca) }) </caption>
				
				<thead>
					<tr>
						<th>Título Projeto</th>
						<th>Coordenador</th>
						<th>Situação Projeto</th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
	
				<c:forEach items="#{ buscaProjetoApoioGruposPesquisaMBean.resultadosBusca }" var="linha">
					<tr>
						<td> ${ linha.projeto.titulo } </td>
						<td> ${ linha.grupoPesquisa.coordenador.pessoa.nome } </td>
						<td> ${ linha.projeto.situacaoProjeto.descricao } </td>
						<td width="20">
							<h:commandLink id="lnkVisualizar" action="#{ projetoApoioGrupoPesquisaMBean.view }" >
								<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar"/>
								<f:param name="id" value="#{ linha.id }"/>
							</h:commandLink>
						</td>
						<td width="20">
							<h:commandLink id="lnkAtualizar" action="#{ projetoApoioGrupoPesquisaMBean.atualizar }" >
								<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
								<f:param name="id" value="#{ linha.id }"/>
							</h:commandLink>
						</td>
						<td width="20">
							<h:commandLink id="lnkRemover" action="#{ projetoApoioGrupoPesquisaMBean.remover }" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
								<f:param name="id" value="#{ linha.id }"/>
							</h:commandLink>
						</td>
					</tr>				
				</c:forEach>
			</table>
		</c:if>	
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>