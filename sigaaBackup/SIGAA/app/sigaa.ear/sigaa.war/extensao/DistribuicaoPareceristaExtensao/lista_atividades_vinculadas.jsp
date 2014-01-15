<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

	<h2><ufrn:subSistema /> > Distribuir Propostas para Avaliadores Ad Hoc</h2>

	<h:outputText value="#{distribuicaoExtensao.create}"/>
	<h:outputText value="#{atividadeExtensao.create}"/>	
		
	<div class="descricaoOperacao">
		<b>Aten��o:</b><br/> 	
		 Somente poder�o ser distribu�das as A��es de Extens�o que j� foram 'SUBMETIDAS' � PROEx ou as que est�o 'AGUARDANDO AVALIA��O'.<br/>
	</div>

	<br/>
	
		<div class="infoAltRem">
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" rendered="#{acesso.extensao}"/><h:outputText rendered="#{acesso.extensao}" value=": Distribuir A��o para Avaliadores Ad Hoc"/>
			<h:graphicImage value="/img/buscar.gif"style="overflow: visible;" rendered="#{acesso.extensao}"/><h:outputText rendered="#{acesso.extensao}" value=": Distribuir A��es Vinculadas"/>		    	    	
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar A��o
	    </div>
		<br/>
	
		<h:form>
		<table class="listagem">
		    <caption>A��es de Extens�o Encontradas (${fn:length(distribuicaoExtensao.atividadePai.atividades)})</caption>
	 		<tbody>
	 		
	 			<tr>
				 <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">Lista A��es vinculadas ao ${distribuicaoExtensao.atividadePai.tipoAtividadeExtensao.descricao}  '${distribuicaoExtensao.atividadePai.titulo}'</td>
				</tr> 
	 		
	 		
				<tr>
					<td>
				
						<t:dataTable id="dtAtividades" value="#{distribuicaoExtensao.atividadePai.atividades}" var="atividade" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
							
							<t:column>
								<f:facet name="header"><h:graphicImage url="/img/green_spot.gif" title="Total de Avaliadores com a A��o"/></f:facet>
								<h:outputText value="#{atividade.totalPareceristaAvaliando}" rendered="#{atividade.totalPareceristaAvaliando > 0}" />					
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Ano - T�tulo</f:verbatim></f:facet>
								<h:outputText value="#{atividade.anoTitulo}" />					
							</t:column>
							<t:column>
								<f:facet name="header"><f:verbatim>�rea Tem�tica</f:verbatim></f:facet>
								<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
							</t:column>
							
							<t:column width="10%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>						
								<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" />		
							</t:column>									
								
							<t:column>
                                <f:facet name="header"><f:verbatim>Situa��o</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.situacaoProjeto.descricao}" />                  
                            </t:column>

                            <t:column>
                                <f:facet name="header"><f:verbatim>Dimens�o Acad�mica</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENS�O'}" />                  
                            </t:column>
								
							<t:column width="5%" styleClass="centerAlign">
									<h:commandLink title="Distribuir A��o para Avaliadores Ad Hoc" action="#{ distribuicaoExtensao.selecionarAtividadeParecerista }" 
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
								<h:commandLink  title="Visualizar A��o de Extens�o" action="#{ atividadeExtensao.view }" id="btView">
								      <f:param name="id" value="#{atividade.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>
						
						</t:dataTable>
				
						<c:if test="${empty distribuicaoExtensao.atividadePai.atividades}">
							<center><font color="red">N�o h� a��es de extens�o vinculadas!</font> </center>
						</c:if>
					
				</td>
			</tr>
				
			</tbody>
		 	
		 </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>