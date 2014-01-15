<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Institutos de Ciência e Tecnologia</h2>
	<h:outputText value="#{institutoCienciaTecnologia.create}" />
	<h:messages showDetail="true"></h:messages>
	<h:form id="formConsulta">
		  <table class="formulario">
			<caption class="listagem">Critérios de Busca</caption>
			<tr>
				<th>
					<h:selectBooleanCheckbox value="#{institutoCienciaTecnologia.filtroUnidadeFederativa}" id="checkUnidade" styleClass="noborder"/>
				</th>
				<td>
					<label for="checkUnidade" onclick="$('formConsulta:checkUnidade').checked = !$('formConsulta:checkUnidade').checked;">Unidade Federativa:</label>
				</td>
				<td>
					<h:selectOneMenu value="#{ institutoCienciaTecnologia.obj.unidadeFederativa.id}" readonly="#{institutoCienciaTecnologia.readOnly}" disabled="#{institutoCienciaTecnologia.readOnly}">
					<f:selectItem itemLabel="--- Selecione ---" itemValue="0" />
					<f:selectItems value="#{institutoCienciaTecnologia.allUnidadesFederativas}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>
					<h:selectBooleanCheckbox value="#{institutoCienciaTecnologia.filtroCoordenador}" id="checkCoordenador" styleClass="noborder"/>
				</th>
				<td>
					<label for="checkCoordenador" onclick="$('formConsulta:checkCoordenador').checked = !$('formConsulta:checkCoordenador').checked;">Coordenador:</label>
				</td>
				<td>
					<h:inputHidden id="idCoordenador" value="#{institutoCienciaTecnologia.obj.coordenador.id}" /> 
					<h:inputText id="nomeCoordenador" value="#{institutoCienciaTecnologia.obj.coordenador.pessoa.nome}"	size="70" onkeyup="CAPS(this);" /> 
					<ajax:autocomplete
						source="formConsulta:nomeCoordenador" target="formConsulta:idCoordenador"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3"
						parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" /> 
					<span id="indicatorDocente" style="display: none;"> 
						<img src="/sigaa/img/indicator.gif" alt="Carregando..." title="Carregando..." /> 
					</span>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton id="buscar" action="#{institutoCienciaTecnologia.busca}" value="Buscar" /> 
						<h:commandButton id="cancelar" action="#{institutoCienciaTecnologia.cancelar}" value="Cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			<c:set var="lista" value="${institutoCienciaTecnologia.resultadosBusca}" />
		</table>
		<br />
		<center><h:messages />
			<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
						<h:commandLink action="#{institutoCienciaTecnologia.preCadastrar}" value="Cadastrar" />
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar 
			</div>
		</center>
		<br />
		<c:if test="${ not empty institutoCienciaTecnologia.allAtivos}">
			<table class="listagem">
				<caption class="listagem">Lista de Institutos de Ciência e Tecnologia</caption>
				<thead>
					<tr>
						<th>Nome</th>
						<th style=" text-align: right;" nowrap="nowrap">Volume de Recursos </th>
						<th style=" text-align: center;" nowrap="nowrap">Período Inicial</th>
						<th style=" text-align: center;" nowrap="nowrap">Período Final &nbsp</th>
						<th>Coordenador</th>
						<th>Unidade Federativa</th>
						<th style=" text-align: center;">Ativo</th>
						<th colspan="2" nowrap="nowrap"></th>
					</tr>
				</thead>
				<c:forEach items="#{institutoCienciaTecnologia.allPaginado}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.nome}</td>
						<td align="right"><ufrn:format type="valor" valor="${item.volumeRecursos}" /></td>
						<td style=" text-align: center;"><ufrn:format type="data" valor="${item.periodoInicio}" /></td>
						<td style=" text-align: center;"><ufrn:format type="data" valor="${item.periodoFim}" /></td>
						<td>${item.coordenador.pessoa.nome}</td>
						<td>${item.unidadeFederativa.descricao}</td>
						<td style=" text-align: center;">${item.ativo?'Sim':'Não'}</td>
						<td colspan="2" nowrap="nowrap">
							<h:commandLink title="Visualizar" action="#{institutoCienciaTecnologia.view}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						
							<h:commandLink action="#{ institutoCienciaTecnologia.atualizar }">
								<f:verbatim>
									<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
							<h:commandLink action="#{ institutoCienciaTecnologia.remover }" onclick="#{confirmDelete}">
								<f:verbatim>
									<img src="/shared/img/delete.gif" alt="Remover" title="Remover" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<c:if test="${ empty institutoCienciaTecnologia.allAtivos}">
		  <center> Nenhum instituto de ciência e tecnologia encontrado. </center>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
