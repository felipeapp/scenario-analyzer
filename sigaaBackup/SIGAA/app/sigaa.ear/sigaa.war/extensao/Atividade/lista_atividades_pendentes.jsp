<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view beforePhase="#{atividadeExtensao.checkChangeRole}">

<c:set var="GRAVADO" 	value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO) %>" 		scope="application"/>

<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="formAtividade">
	
			<h:messages/>
			<h2><ufrn:subSistema /> > Ações de Extensão com cadastro em andamento</h2>
			<h:outputText value="#{atividadeExtensao.create}"/>
			<h:outputText value="#{discenteExtensao.create}"/>
			
			
			
			<div class="descricaoOperacao">
				<table width="100%" id="aviso">
					<tr>
						<td width="10%"> 
							<html:img page="/img/warning.gif"/>
						</td>
						<td align="justify">
						<b>Atenção:</b> 
									Esta é a lista de todas as Ações de Extensão com cadastros em andamento.
									Para continuar o cadastro da ação clique no link correspondente. 
									Para cadastrar uma nova Ação de Extensão clique nos botões da barra de navegação logo abaixo.
						</td>
					</tr>
				</table>
			</div>
				<br/>
			
			
			
		
		<!-- Ações GRAVADAS PELO USUARIO LOGADO-->
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Continuar Cadastro
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Proposta
			</div>
			<br/>
		
			
			<h:dataTable id="datatableAtividadesGravadas" value="#{atividadeExtensao.atividadesGravadas}" 
			     var="atividade" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista das Ações de Extensão Pendentes de Envio" />
				</f:facet>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Título</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.anoTitulo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Tipo Ação</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}  #{atividade.registro ? '<font color=red>(REGISTRO)</font>' : '(PROPOSTA)' }" escape="false"/>
				</t:column>


				<t:column>
					<f:facet name="header">
						<f:verbatim>Situação</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.situacaoProjeto.descricao}" />
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Continuar Cadastro" action="#{ atividadeExtensao.preAtualizar }" immediate="true">
					        <f:param name="id" value="#{atividade.id}"/>
				    		<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Remover Proposta" action="#{ atividadeExtensao.preRemover }" immediate="true">
					        <f:param name="id" value="#{atividade.id}"/>
				    		<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
					
				</t:column>
		
			</h:dataTable>
			
		<c:if test="${(empty atividadeExtensao.atividadesGravadas)}">
			<center><font color='red'>Não há ações de extensão com cadastro em andamento pelo usuário atual.</font></center>
		</c:if>
		
		<br/>
		<br/>

		<!-- FIM DAS Ações GRAVADAS PELO USUARIO LOGADO-->

		
		<table class=formulario width="100%">
			<tfoot>
				<tr>
					<td><h:commandButton value="Registrar Ação Anterior" action="#{atividadeExtensao.iniciarRegistro}" id="btRegistro"/></td>
					<td><h:commandButton value="Submeter Nova Proposta" action="#{atividadeExtensao.iniciarPropostaCompleta}" id="btProposta"/></td>
				</tr>
				</tfoot>
		</table>
		
		
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>