<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jsp:useBean id="sigaaSubSistemas" class="br.ufrn.arq.seguranca.SigaaSubsistemas" scope="page" />

<c:set var="_portalDocente" value="<%=sigaaSubSistemas.PORTAL_DOCENTE%>" />
<c:set var="_portalLato" value="<%=sigaaSubSistemas.LATO_SENSU%>" />

<f:view>
	<c:if test="${programaComponente.portalDocente}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>

	<c:if test="${programaComponente.portalCoordenadorLato}">
		<%@include file="/lato/menu_coordenador.jsp" %>
	</c:if>

	<c:if test="${programaComponente.portalCoordenadorGraduacao}">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</c:if>


	<h2><ufrn:subSistema /> &gt; Programa de Componentes Curriculares &gt; Consulta de Componentes</h2>
	
	<h:form id="buscaCC">
		<table class="formulario" width="90%">
			<caption>Busca por Componentes Curriculares</caption>
			<tbody>
				<tr>
					<td width="5%">
						<input type="radio" id="checkCodigo" name="paramBusca" value="codigo" class="noborder" ${programaComponente.paramBusca == 'codigo' ? 'checked' : '' }>
					</td>
					<th style="text-align: left">
						<label for="checkCodigo">Código:</label>
					</th>
					<td>
						<h:inputText id="codigo" size="10" value="#{programaComponente.obj.componenteCurricular.codigo }" onfocus="marcaCheckBox('checkCodigo')" onkeyup="CAPS(this)" />
					</td>
				</tr>
				<tr>
					<td>
						<input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder" ${programaComponente.paramBusca == 'nome' ? 'checked' : '' }>
					</td>
					<th style="text-align: left">
						<label for="checkNome">Nome:</label>
					</th>
					<td>
						<h:inputText id="nome" size="60" value="#{programaComponente.obj.componenteCurricular.nome }" onfocus="marcaCheckBox('checkNome')" />
					</td>
				</tr>
				<tr>
					<td>
						<input type="radio" id="checkTipo" name="paramBusca" value="tipo" class="noborder" ${programaComponente.paramBusca == 'tipo' ? 'checked' : '' }>
					</td>
					<th style="text-align: left">
						<label for="checkTipo">Tipo:</label>
					</th>
					<td>
						<h:selectOneMenu id="tipos" value="#{programaComponente.obj.componenteCurricular.tipoComponente.id}" onfocus="marcaCheckBox('checkTipo')">							
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{componenteCurricular.tiposComponentes}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<c:if test="${programaComponente.permiteBuscarUnidade}">
				<tr>
					<td>
						<input type="radio" id="checkUnidade" name="paramBusca" value="unidade" class="noborder" ${programaComponente.paramBusca == 'unidade' ? 'checked' : '' }>
					</td>
					<th>
						<label for="checkUnidade">Unidade Acadêmica:</label>
					</th>
					<td>
						<h:selectOneMenu id="unidades" style="width: 400px" value="#{programaComponente.obj.componenteCurricular.unidade.id}" onfocus="marcaCheckBox('checkUnidade')">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{unidade.allDeptosEscolasCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{programaComponente.buscarComponente}" id="busca" />
						<h:commandButton value="Cancelar" action="#{programaComponente.cancelar}" onclick="#{confirm}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<c:if test="${empty programaComponente.componentesEncontrados}">
		<br />
		<div style="font-style: italic; text-align:center">Nenhum registro encontrado com esses critérios de busca.</div>
	</c:if>
	
	<c:if test="${not empty programaComponente.componentesEncontrados}">
		<br />
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Cadastrar Programa
		    <h:graphicImage url="/img/alterar.gif" style="overflow: visible;"/>: Alterar Programa Existente
		    <h:graphicImage url="/img/buscar.gif" style="overflow: visible;"/>: Visualizar Programa Existente
		</div>
		<br />
		<table class="listagem">
			<caption class="listagem">COMPONENTES CURRICULARES ENCONTRADOS</caption>
			<thead>
				<tr>
					<td>Período</td>
					<td style="text-align: right;">Código</td>
					<td>Nome</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{programaComponente.componentesEncontrados}" var="cc" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${(cc.programa.ano != null and cc.programa.periodo != null) ? cc.programa.anoPeriodo : '' }</td>
					<td width="20" align="right">${cc.detalhes.codigo}</td>
					<td>${cc.detalhes.nome}</td>
					<c:choose>
					<c:when test="${cc.programa.ano == programaComponente.calendarioVigente.ano and cc.programa.periodo == programaComponente.calendarioVigente.periodo}">
						<td width="1%" align="center">
							<h:commandLink styleClass="noborder" title="Alterar Programa Existente"
									action="#{programaComponente.selecionaComponente}" id="alterar">
								<f:param name="id" value="#{cc.id}" />
								<f:param name="operacao" value="alteracao" />
								<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</td>
						<td width="1%" align="center">
							<h:commandLink styleClass="noborder" title=" Visualizar Programa Existente"
									action="#{programaComponente.gerarRelatorioPrograma}" id="visualizar">
								<f:param name="idComponente" value="#{cc.id}" />
								<f:param name="visualizar" value="visualizar" />
								<h:graphicImage url="/img/buscar.gif" />
							</h:commandLink>
						</td>
						
					</c:when>
					<c:otherwise>
						<td width="1%" align="center" 	>
							<h:commandLink styleClass="noborder" title="Cadastrar Programa"
									action="#{programaComponente.selecionaComponente}" id="cadastrar">
								<f:param name="id" value="#{cc.id}" />
								<h:graphicImage url="/img/adicionar.gif" />
								<f:param name="operacao" value="cadastro" />
							</h:commandLink>
						</td>
						<td></td>
					</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
			</h:form>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
