<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/cv/include/cabecalho.jsp"%>

<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<a4j:keepAlive beanName="configuracoesComunidadeVirtual"></a4j:keepAlive>
	<a4j:keepAlive beanName="topicoComunidadeMBean"></a4j:keepAlive>
	
	<h:form id="form">
	
		<div class="colComunidadeForum" id="topicos">
			<a4j:outputPanel id="configuracoes">						
			<rich:panel header="CONFIGURAÇÃOES DOS TÓPICOS" style="position:relative;width:auto;" headerClass="headerBloco">
				<div style="margin-top:10px;">
					<table class="formulario">
					<caption>Configurar Dados</caption>
						<tr>
							<th>Ordem dos Tópicos:</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesComunidadeVirtual.object.ordemTopico }" valueChangeListener="#{configuracoesComunidadeVirtual.salvar}" styleClass="noborder">
									<f:selectItems value="#{ configuracoesComunidadeVirtual.comboOrdemTopicos }"/>
									<a4j:support event="onclick" onsubmit="true" reRender="panelTopicos" />
								</h:selectOneRadio>
							</td>
						</tr>
						<tfoot>
						<tr> 
							<td colspan="2"> 
							</td>
						</tr>
						</tfoot>
					</table>
				</div>
			</rich:panel>	
			</a4j:outputPanel>	
			<a4j:outputPanel id="panelTopicos">					
				<rich:panel header="TÓPICOS DA COMUNIDADE" style="position:relative;width:auto;" headerClass="headerBloco">
				<a4j:status>
					<f:facet name="start">
						<f:verbatim>
						<p align="center">
							<h:graphicImage value="/img/ajax-loader.gif" title="Carregando..."/>
						</p>
						</f:verbatim>
					</f:facet>
				</a4j:status>
				<rich:dragIndicator id="dragIndicator" />
				<a4j:repeat var="_topico" value="#{ topicoComunidadeMBean.topicos }">	
					<a4j:outputPanel id="dnd">
					<rich:dropSupport id="destinoDragDrop" acceptedTypes="topico" dropValue="#{ _topico.id }" dropListener="#{ topicoComunidadeMBean.moverTopico }" reRender="panelTopicos">
						<a4j:outputPanel layout="block" styleClass="item" style="margin-left: #{ (_topico.nivel * 20) }px;margin-top:10px;#{ configuracoesComunidadeVirtual.object.ordemTopicoLivre  ? 'padding:5px 5px 5px 20px; border-style:solid;border-width:1px; border-color:#BED6F8; border-radius:5px 5px 5px 5px; position:relative; background:url(/sigaa/ava/img/handle_part.jpg) repeat-y; background-position:0px 5px;' : ''}">
							<%-- Mover tópico --%>
				            <a4j:outputPanel rendered="#{configuracoesComunidadeVirtual.object.ordemTopicoLivre}">
	                            <rich:dragSupport dragIndicator="dragIndicator" dragType="topico" dragValue="#{ _topico.id }">
	                            	<rich:dndParam name="label" value="#{ _topico.descricao }" />
	                            </rich:dragSupport>
								<a4j:outputPanel layout="block" style="position:absolute;left:0;top:0;width:15px;height:100%;cursor:move;" title="Mover"/>
	                        </a4j:outputPanel>
	                        <h:outputText value="&nbsp;" escape="false"/>
						
							<a4j:outputPanel>
								<h3>
								<h:outputText value="#{ _topico.descricao }"/> 						
								<h:outputText value=" #{ _topico.dataFormatada }"/> 
								</h3>
							</a4j:outputPanel>	
						</a4j:outputPanel>	
	   				</rich:dropSupport>	
	   				</a4j:outputPanel>
				</a4j:repeat>
				</rich:panel>
			</a4j:outputPanel>
		</div>
	</h:form>
</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>