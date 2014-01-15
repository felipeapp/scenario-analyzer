<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

	<h2><ufrn:subSistema /> > Distribuir Propostas para Avaliadores Ad Hoc</h2>

	<h:outputText value="#{distribuicaoExtensao.create}"/>
	<h:outputText value="#{atividadeExtensao.create}"/>	
		
	<div class="descricaoOperacao">
		<b>Atenção:</b><br/> 	
		 Somente poderão ser distribuídas as Ações de Extensão que já foram 'SUBMETIDAS' à PROEx ou as que estão 'AGUARDANDO AVALIAÇÃO'.<br/>
	</div>

	<br/>
	
		<div class="infoAltRem">
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" rendered="#{acesso.extensao}"/><h:outputText rendered="#{acesso.extensao}" value=": Distribuir Ação para Avaliadores Ad Hoc"/>
			<h:graphicImage value="/img/buscar.gif"style="overflow: visible;" rendered="#{acesso.extensao}"/><h:outputText rendered="#{acesso.extensao}" value=": Distribuir Ações Vinculadas"/>		    	    	
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Ação
	    </div>
		<br/>
	
		<h:form>
		<table class="listagem">
		    <caption>Ações de Extensão Encontradas (${fn:length(distribuicaoExtensao.atividadePai.atividades)})</caption>
	 		<tbody>
	 		
	 			<tr>
				 <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">Lista Ações vinculadas ao ${distribuicaoExtensao.atividadePai.tipoAtividadeExtensao.descricao}  '${distribuicaoExtensao.atividadePai.titulo}'</td>
				</tr> 
	 		
	 		
				<tr>
					<td>
				
						<t:dataTable id="dtAtividades" value="#{distribuicaoExtensao.atividadePai.atividades}" var="atividade" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
							
							<t:column>
								<f:facet name="header"><h:graphicImage url="/img/green_spot.gif" title="Total de Avaliadores com a Ação"/></f:facet>
								<h:outputText value="#{atividade.totalPareceristaAvaliando}" rendered="#{atividade.totalPareceristaAvaliando > 0}" />					
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Ano - Título</f:verbatim></f:facet>
								<h:outputText value="#{atividade.anoTitulo}" />					
							</t:column>
							<t:column>
								<f:facet name="header"><f:verbatim>Área Temática</f:verbatim></f:facet>
								<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
							</t:column>
							
							<t:column width="10%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>						
								<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" />		
							</t:column>									
								
							<t:column>
                                <f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.situacaoProjeto.descricao}" />                  
                            </t:column>

                            <t:column>
                                <f:facet name="header"><f:verbatim>Dimensão Acadêmica</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENSÃO'}" />                  
                            </t:column>
								
							<t:column width="5%" styleClass="centerAlign">
									<h:commandLink title="Distribuir Ação para Avaliadores Ad Hoc" action="#{ distribuicaoExtensao.selecionarAtividadeParecerista }" 
									       rendered="#{acesso.extensao && atividade.permitidoIniciarAvaliacao}">
									      <f:param name="id" value="#{atividade.id}"/>
									      <h:graphicImage url="/img/seta.gif" />
									</h:commandLink>
							</t:column>


							<t:column width="5%" styleClass="centerAlign">
									<h:commandLink title="Distribuir Atividades Vinculadas" action="#{ distribuicaoExtensao.distribuirAtividadesVinculadasParecerista }" 
									   rendered="#{acesso.extensao && atividade.permitidoIniciarAvaliacao}">
									      <f:param name="id" value="#{atividade.id}"/>
									      <h:graphicImage url="/img/buscar.gif" />
									</h:commandLink>
							</t:column>
						
							
							<t:column width="5%" styleClass="centerAlign">
								<h:commandLink  title="Visualizar Ação de Extensão" action="#{ atividadeExtensao.view }" id="btView">
								      <f:param name="id" value="#{atividade.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>
						
						</t:dataTable>
				
						<c:if test="${empty distribuicaoExtensao.atividadePai.atividades}">
							<center><font color="red">Não há ações de extensão vinculadas!</font> </center>
						</c:if>
					
				</td>
			</tr>
				
			</tbody>
		 	
		 </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>