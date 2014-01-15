<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<a4j:keepAlive beanName = "gestorEspacoBean"/>

	<h2><ufrn:subSistema/> > Cadastrar Gestor de Espaço Físico </h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Gestores.</strong>
		</p>
		<br/>
		<p>
			Esta funcionalidade visa cadastrar um usuário como responsável por gerenciar todos os espaços físicos de uma Unidade ou um Espaço Físico em particular.
		</p>
		<br/>
		<p>
			Se você especificar que o gestor deve ser responsável por uma Unidade, isso englobará automaticamente todos os espaços físicos cadastrados para a Unidade.
			Caso especifique um Espaço Físico particular, o controle do gestor será restrito somente ao Espaço Físico indicado.
		</p>		
	</div>

			
	<h:form id="form" >
		<a4j:region id="regiaoForm" selfRendered="true">
		<table class="formulario" width="80%">
			<caption>Cadastrar Gestor</caption>
			<tbody>
				<tr>
					<th class="required" width="20%" nowrap="nowrap">Nome do Gestor:</th>
					<td>
						<h:inputHidden id="idUsuario" value="#{gestorEspacoBean.obj.usuario.id}"/>
						<h:inputText id="nomeUsuario" value="#{gestorEspacoBean.obj.usuario.pessoa.nome}" style="width: 90%" disabled="#{gestorEspacoBean.readOnly}" />
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
					<td colspan="2" align="center">
						<h:selectOneRadio value="#{gestorEspacoBean.tipo}">
							<f:selectItems value="#{gestorEspacoBean.tipos}"/>
							<a4j:support event="onclick" reRender="txts, selects" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th class="required">
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/ajax-loader.gif"/>
							</f:facet>
						</a4j:status>					
						<a4j:outputPanel id="txts">
							<h:outputText id="txtEspacoFisico" value="Espaço Físico:" rendered="#{gestorEspacoBean.espacoFisico}" />
							<h:outputText id="txtUnidade" value="Unidade:" rendered="#{gestorEspacoBean.unidade}" />
						</a4j:outputPanel>
					</th>
					<td>
						<a4j:outputPanel id="selects">
							<h:selectOneMenu id="espacosFisicos" value="#{gestorEspacoBean.obj.espacoFisico.id}" style="width: 90%"  rendered="#{gestorEspacoBean.espacoFisico}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE  --"/>
								<f:selectItems value="#{gestorUnidadesMBean.espacosCombo}"/>
							</h:selectOneMenu>
							<h:selectOneMenu id="unidade" value="#{gestorEspacoBean.obj.unidade.id}" style="width: 90%" rendered="#{gestorEspacoBean.unidade}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE  --"/>
								<f:selectItems value="#{gestorUnidadesMBean.unidadesCombo}"/>
							</h:selectOneMenu>
						</a4j:outputPanel>
					</td>				
				</tr>
				
				<tr>
					<th class="required">Permissão:</th>
					<td>
						<h:selectOneMenu id="permissao" value="#{gestorEspacoBean.obj.tipo.id}" style="width: 90%">
							<f:selectItem itemValue="0" itemLabel="--- SELECIONE  ---"/>
							<f:selectItems value="#{gestorEspacoBean.tipoGestorCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="#{gestorEspacoBean.confirmButton}" action="#{gestorEspacoBean.persistir}" />
						<h:commandButton value="Cancelar" action="#{gestorEspacoBean.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>								
		</table>
		</a4j:region>
	</h:form>
	

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br/>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>