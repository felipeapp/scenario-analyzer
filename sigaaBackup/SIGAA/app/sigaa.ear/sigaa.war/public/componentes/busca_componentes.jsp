<%@include file="/public/include/cabecalho.jsp" %>

<h2> Consulta de Componentes Curriculares </h2>

<style>
	span.info {
		color: #777;
		font-size: 0.9em;
		padding: 2px 5px;
	}
	
	input {
		padding: 2px;
	}
</style>

<script>
	var marcar = function(idCheck) {
		$(idCheck).checked = true;
	}
</script>

<f:view>
	<h:outputText value="#{componenteCurricular.create}" />
	<h:outputText value="#{nivelEnsino.create}" />
	
	${componenteCurricular.popularNivel}
	
	<div class="descricaoOperacao">
		<p>
			Através desta página você pode consultar os componentes curriculares 
			(disciplinas, atividades acadêmicas específicas, blocos e módulos)
			oferecidos aos cursos da ${ configSistema['siglaInstituicao'] }. Para cada componente é possível visualizar os detalhes 
			que o caracterizam e ainda consultar seu programa atual.
		</p>
		<p>
			Utilize os critérios de busca abaixo para filtrar os componentes de acordo com os critérios desejados.
		</p>
	</div>
	
	<h:form id="form">
		<table class="formulario" align="center" width="75%">
		<caption class="listagem">Informe os critérios de consulta</caption>
			
			<tr>
				<td width="3%"></td>
				<td width="25%">Nível de Ensino:</td>
				<td>
					
					<h:selectOneMenu id="nivel" value="#{componenteCurricular.componenteBusca.nivel}" 
						style="width:40%">
						<f:selectItems value="#{nivelEnsino.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>	
					
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroTipo}" id="checkTipo" /> </td>			
				<td>Tipo do Componente:</td>
				<td>
					<h:selectOneMenu id="tipo" value="#{componenteCurricular.componenteBusca.tipoComponente.id}" onchange="marcar('form:checkTipo');" style="width:40%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{componenteCurricular.allTiposComponentes}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroCodigo}" id="checkCodigo" /> </td>			
				<td>Código do Componente:</td>
				<td>
					<h:inputText value="#{componenteCurricular.componenteBusca.codigo}" onchange="marcar('form:checkCodigo');" style="width:15%;"/>
					<span class="info"> (Ex. MAT0311)</span>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroNome}" id="checkNome" /> </td>			
				<td>Nome do Componente:</td>
				<td>
					<h:inputText value="#{componenteCurricular.componenteBusca.nome}" style="width: 95%;" onchange="marcar('form:checkNome');"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{componenteCurricular.filtroUnidade}" id="checkUnidade" /> </td>			
				<td>Unidade Responsável:</td>
				<td>
					<h:selectOneMenu id="unidades" style="width: 95%" value="#{componenteCurricular.componenteBusca.unidade.id}" onchange="marcar('form:checkUnidade');">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA UNIDADE ACADÊMICA --" />
						<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{componenteCurricular.buscarComponente}" id="btnBuscarComponentes" value="Buscar Componentes">
							<f:setPropertyActionListener value="#{true}" target="#{componenteCurricular.filtroNivel}"/>
						</h:commandButton> 
						&nbsp;
						<h:commandButton action="#{componenteCurricular.cancelarPublico}" value="Cancelar"
							onclick="#{confirmDelete}" id="btnCancelarBuscarComponentes"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<c:if test="${not empty componenteCurricular.componentes}">
	<br /> <br />
	
	<div class="legenda" style="width: 90%;">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Detalhes do Componente Curricular
		<h:graphicImage value="/img/report.png" style="overflow: visible;" />: Programa Atual do Componente
	</div>	
	
	<h:form id="formListagemComponentes">
	<table class="listagem" style="width: 90%;">
		<caption class="listagem">Componentes Curriculares Encontrados</caption>

		<thead>
			<tr>
				<th>Código</th>
				<th>Nome</th>
				<th>Tipo</th>				
				<th>CR Total</th>
				<th>CH Total</th>
				<th></th>
			</tr>
		</thead>

		<tbody>
			<c:forEach items="#{componenteCurricular.componentes}" var="componente" varStatus="loop">
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${componente.codigo}</td>
					<td>${componente.detalhes.nome}</td>
					<td>${componente.tipoComponente.descricao}</td>
					<td>
						<c:choose>
							<c:when test="${componente.detalhes.crTotal != 0}">
								${componente.detalhes.crTotal} crédito(s)
							</c:when>
							<c:otherwise>
								<center>--</center>							
							</c:otherwise>
						</c:choose>	
					</td>
					<td>${componente.detalhes.chTotal}h</td>
					<td>
						<h:commandLink title="Detalhes do Componente Curricular" action="#{componenteCurricular.detalharComponente}">
							<h:graphicImage url="/img/view.gif"/>
							<f:param name="id" value="#{componente.id}"/>
							<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>
						</h:commandLink>
						<h:commandLink title="Programa Atual do Componente" action="#{programaComponente.gerarRelatorioPrograma}">
							<h:graphicImage url="/img/report.png"/>
							<f:param name="idComponente" value="#{componente.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</h:form>
	</c:if>
	
	<%@include file="/public/include/voltar.jsp" %>
</f:view>

<br />
<%@include file="/public/include/rodape.jsp" %>