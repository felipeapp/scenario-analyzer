<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<style>
		.rodapeTabela { text-align: center; }
		
		
	</style>

	<a4j:keepAlive beanName = "espacoFisicoBean"/>

	<h:panelGroup id="ajaxErros">
		<h:dataTable  value="#{espacoFisicoBean.avisosAjax}" var="msg" rendered="#{not empty espacoFisicoBean.avisosAjax}" width="100%">
			<t:column>
				<h:outputText value="<div id='painel-erros' style='position: relative; padding-bottom: 10px;'><ul class='erros'><li>#{msg.mensagem}</li></ul></div>" escape="false"/>
			</t:column>
		</h:dataTable>
	</h:panelGroup>

	<h2><ufrn:subSistema/> > Recursos disponíveis em um espaço físico </h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Espaço Físico.</strong>
		</p>
		<br/>
		<p>
			O cadastro foi dividido em 2 etapas:
			<ol>
				<li>A primeira etapa é responsável por descrever o espaço físico.</li>
				<li><strong>Durante a segunda etapa, detalhe os recursos que o espaço físico possui.</strong></li>
			</ol>
		</p>
	
	</div>

	<h:form id="form">
		<table class="formulario" width="50%">
			<caption>Recursos Disponíveis no Espaço Físico</caption>
			<tbody>
				<tr>
					<th class="required" width="40%" nowrap="nowrap">Tipo de Recurso:</th>
					<td>
						<h:selectOneMenu value="#{espacoFisicoBean.recurso.tipo.id}" id="tipoRecurso">
							<f:selectItems value="#{espacoFisicoBean.tipoRecursosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required" nowrap="nowrap">Quantidade:</th>
					<td>
						<h:inputText id="quantidade" value="#{espacoFisicoBean.recurso.quantidade}"  onkeypress="return ApenasNumeros(event);" size="4" maxlength="4"></h:inputText>
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<a4j:commandButton value="Adicionar" actionListener="#{espacoFisicoBean.adicionarRecurso}" id="adicionarRecurso" reRender="dataTableRecursos, ajaxErros" />
					</td>
				</tr>
			</tfoot>								
		</table>
	</h:form>
	

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br/>
			
	<div class="infoAltRem">
	   	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Excluir Recurso
	   	<br/>
	</div>
			
	<center>
		<a4j:status>
			<f:facet name="start">
				<h:graphicImage value="/img/ajax-loader.gif"/>
			</f:facet>
		</a4j:status>
	</center>
	
	
	<h:form id="listarRecursos">
		<rich:dataTable var="recurso" value="#{espacoFisicoBean.modelRecursos}" width="50%" 
				id="dataTableRecursos" rowKeyVar="row" styleClass="listagem" headerClass="linhaCinza" footerClass="rodapeTabela" >

			<f:facet name="caption">
				<h:outputText value="Recursos Cadastrados" />
			</f:facet>

			<rich:column>
				<f:facet name="header">
					<h:outputText value="Descrição" />
				</f:facet>
				<h:outputText id="denominacao" value="#{recurso.tipo.denominacao}" rendered="#{recurso.ativo}"/>
			</rich:column>                 

			<rich:column width="10%">
				<f:facet name="header">
					<h:outputText value="Quantidade" />
				</f:facet>
				<h:inputText id="quantidadeAjax" value="#{recurso.quantidade}" size="5" onkeypress="return ApenasNumeros(event);" rendered="#{recurso.ativo}">
					<a4j:support event="onblur" actionListener="#{espacoFisicoBean.atualizarRecurso}" reRender="dataTableRecursos, ajaxErros" />
				</h:inputText>
 				<rich:effect event="onclick"  type="Highlight" params="duration:1.0" rendered="#{recurso.ativo}" />
        		<rich:effect event="onclick"  for="efeito"  type="Appear" params="delay:3.0,duration:0.5" rendered="#{recurso.ativo}" />						
			</rich:column>    

			<rich:column width="5%">
				<a4j:commandButton id="remover" actionListener="#{espacoFisicoBean.removerRecurso}" image="/img/delete.gif" reRender="dataTableRecursos" rendered="#{recurso.ativo}" />
			</rich:column>			
			
			<f:facet name="footer">
				<h:panelGroup>
					<h:commandButton value="Finalizar" id="finalizar" action="#{espacoFisicoBean.persistirEspacoFisico}" />
					&nbsp;
					<h:commandButton value="<< Voltar" action="#{espacoFisicoBean.telaDadosDoEspacoFisico}" />
					<h:commandButton value="Cancelar" action="#{espacoFisicoBean.cancelar}" immediate="true" onclick="#{confirm}" />
				</h:panelGroup>
			</f:facet>			
			
		</rich:dataTable>
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>