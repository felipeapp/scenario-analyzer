<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view beforePhase="#{atividadeExtensao.checkChangeRole}">

<c:set var="GRAVADO" 	value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO) %>" 		scope="application"/>

<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="formAtividade">
	
			<h:messages/>
			<h2><ufrn:subSistema /> > A��es de Extens�o com cadastro em andamento</h2>
			<h:outputText value="#{atividadeExtensao.create}"/>
			<h:outputText value="#{discenteExtensao.create}"/>
			
			
			
			<div class="descricaoOperacao">
				<table width="100%" id="aviso">
					<tr>
						<td width="10%"> 
							<html:img page="/img/warning.gif"/>
						</td>
						<td align="justify">
						<b>Aten��o:</b> 
									Esta � a lista de todas as A��es de Extens�o com cadastros em andamento.
									Para continuar o cadastro da a��o clique no link correspondente. 
									Para cadastrar uma nova A��o de Extens�o clique nos bot�es da barra de navega��o logo abaixo.
						</td>
					</tr>
				</table>
			</div>
				<br/>
			
			
			
		
		<!-- A��es GRAVADAS PELO USUARIO LOGADO-->
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Continuar Cadastro
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Proposta
			</div>
			<br/>
		
			
			<h:dataTable id="datatableAtividadesGravadas" value="#{atividadeExtensao.atividadesGravadas}" 
			     var="atividade" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista das A��es de Extens�o Pendentes de Envio" />
				</f:facet>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>T�tulo</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.anoTitulo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Tipo A��o</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}  #{atividade.registro ? '<font color=red>(REGISTRO)</font>' : '(PROPOSTA)' }" escape="false"/>
				</t:column>


				<t:column>
					<f:facet name="header">
						<f:verbatim>Situa��o</f:verbatim>
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
			<center><font color='red'>N�o h� a��es de extens�o com cadastro em andamento pelo usu�rio atual.</font></center>
		</c:if>
		
		<br/>
		<br/>

		<!-- FIM DAS A��es GRAVADAS PELO USUARIO LOGADO-->

		
		<table class=formulario width="100%">
			<tfoot>
				<tr>
					<td><h:commandButton value="Registrar A��o Anterior" action="#{atividadeExtensao.iniciarRegistro}" id="btRegistro"/></td>
					<td><h:commandButton value="Submeter Nova Proposta" action="#{atividadeExtensao.iniciarPropostaCompleta}" id="btProposta"/></td>
				</tr>
				</tfoot>
		</table>
		
		
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>