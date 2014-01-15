<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<a4j:keepAlive beanName = "gestorEspacoBean"/>

	<h2><ufrn:subSistema/> > Buscar Gestor de Espaço Físico </h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo a Busca de Gestores</strong>
		</p>
		<br/>
		<p>
			Esta funcionalidade ajuda a localizar um Gestor de um Espaço Físico e fornece meios para alterar ou remover os dados dos gestores.
		</p>
	</div>

			
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Buscar Gestor</caption>
			<tbody>
				<tr>
					<td width="2%"><h:selectBooleanCheckbox id="usuarioCheck" value="#{gestorEspacoBean.filtros.usuario}" /></td>
					<td width="10%" nowrap="nowrap">Nome do Gestor:</td>
					<td>
						<h:inputHidden id="idUsuario" value="#{gestorEspacoBean.valores.usuario.id}"/>
						<h:inputText id="nomeUsuario" value="#{gestorEspacoBean.valores.usuario.pessoa.nome}" style="width: 90%" onfocus="$('form:usuarioCheck').checked = true" />
						<ajax:autocomplete source="form:nomeUsuario" target="form:idUsuario"
							baseUrl="/sigaa/ajaxUsuario" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDocente" style="display:none; "> 
						<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
						</span>						
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="espacoFisicoCheck" value="#{gestorEspacoBean.filtros.espacoFisico}" /></td>
					<td>Espaço Físico:</td>
					<td>
						<h:selectOneMenu id="espacoFisico" value="#{gestorEspacoBean.valores.espacoFisico.id}" style="width: 90%" onfocus="$('form:espacoFisicoCheck').checked = true">
							<f:selectItem itemValue="0" itemLabel="--- SELECIONE  ---"/>
							<f:selectItems value="#{gestorUnidadesMBean.espacosCombo}"/>
						</h:selectOneMenu>
					</td>				
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="unidadeCheck" value="#{gestorEspacoBean.filtros.unidade}" /></td>
					<td>Unidade:</td>
					<td>
						<h:selectOneMenu id="unidade" value="#{gestorEspacoBean.valores.unidade.id}" style="width: 90%" onfocus="$('form:unidadeCheck').checked = true">
							<f:selectItem itemValue="0" itemLabel="--- SELECIONE  ---"/>
							<f:selectItems value="#{gestorUnidadesMBean.unidadesCombo}"/>
						</h:selectOneMenu>
					</td>				
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton value="Buscar" actionListener="#{gestorEspacoBean.buscar}" />
						<h:commandButton value="Cancelar" action="#{gestorEspacoBean.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>								
		</table>
	</h:form>

	<br />

	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
	
	<br />
	
	<h:form id="buscaResultado" rendered="#{gestorEspacoBean.buscaModel.rowCount > 0}">
		<rich:dataTable styleClass="listagem" width="90%" rowClasses="linhaPar, linhaImpar" value="#{gestorEspacoBean.buscaModel}" var="gef" headerClass="linhaCinza">
			
			<f:facet name="caption">
				<h:outputText value="Resultado da Busca" />
			</f:facet>
		
			<rich:column>
				<f:facet name="header">
					<h:outputText value="Nome do Gestor" />
				</f:facet>
				<h:outputText value="#{gef.usuario.pessoa.nome}" />
			</rich:column>
			
			<rich:column>
				<f:facet name="header">
					<h:outputText value="Espaco Físico" />
				</f:facet>
				<h:outputText value="#{gef.espacoFisico.codigo}" />
			</rich:column>

			<rich:column>
				<f:facet name="header">
					<h:outputText value="Unidade" />
				</f:facet>
				<h:outputText value="#{gef.unidade.sigla}" />
			</rich:column>

			
			<rich:column width="5%" style="white-space:nowrap;">
				<h:commandLink action="#{gestorEspacoBean.iniciarAlterar}" title="Alterar">
					<h:graphicImage value="/img/alterar.gif" />
				</h:commandLink>
				<h:commandLink action="#{gestorEspacoBean.iniciarRemover}" title="Remover">
					<h:graphicImage value="/img/delete.gif" />
				</h:commandLink>				
			</rich:column>
		</rich:dataTable>	
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>