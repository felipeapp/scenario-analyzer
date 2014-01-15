<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style>
	#container{ width:  99%; } /* Sobre escreve o estilo padr�o do sigaa para fica em tela cheia  */
	html.background {
		background: none;
	}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Classificar Projetos</h2>

	<a4j:keepAlive beanName="classificarProjetosBean" />
	<h:form id="form">
		<table class="formulario">
			<caption>Selecione um edital</caption>
				<tr>	
					<th class="rotulo">Edital:</th>
					<td>
						<h:selectOneMenu value="#{classificarProjetosBean.edital.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />							
							<f:selectItems value="#{editalMBean.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			
				<tfoot>
					<tr>	
						<td colspan="2">
							<h:commandButton action="#{classificarProjetosBean.preView}" value="Buscar"/>
						 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{classificarProjetosBean.cancelar}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
				
		</table>
		
		<br/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Projeto
	    </div>
		<br/>
		
		<table class="listagem">
		    <caption>Projetos localizados (${fn:length(classificarProjetosBean.classificacao) }) </caption>
	 		<tbody>
				<tr>
					<td>
				
						<h:dataTable value="#{classificarProjetosBean.classificacao}" var="projeto" binding="#{classificarProjetosBean.uiProjetos}" 
							width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

							<t:column>
								<f:facet name="header"><f:verbatim>Class.</f:verbatim></f:facet>
								<h:outputText value="#{projeto['classificacao']}�" />
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
								<h:outputText value="#{projeto['ano']}" />													
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>T�tulo</f:verbatim></f:facet>
								<h:outputText value="#{projeto['titulo']}" />													
							</t:column>							
							
							<t:column>
								<f:facet name="header"><f:verbatim>Coordenador(a).</f:verbatim></f:facet>					
								<h:outputText value="#{projeto['coordenador']}" />					
							</t:column>
							
							<t:column>
								<f:facet name="header"><f:verbatim>Depto.</f:verbatim></f:facet>					
								<h:outputText value="#{projeto['sigla_unidade']}" />					
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>�rea CNPq.</f:verbatim></f:facet>					
								<h:outputText value="#{projeto['area_cnpq']}" />					
							</t:column>
							
							<t:column>
								<f:facet name="header"><f:verbatim>Dimens�o</f:verbatim></f:facet>					
								<h:outputText value="#{projeto['ensino'] ? 'Ensino ' : ''}" />
								<h:outputText value="#{projeto['pesquisa'] ? 'Pesquisa ' : ''}" />
								<h:outputText value="#{projeto['extensao'] ? 'Extens�o' : ''}" />
							</t:column>			
										
							
							<t:column>
								<f:facet name="header"><h:outputText value="Discentes" title="N� de Discentes"/></f:facet>					
								<h:outputText value="#{projeto['total_discentes_envolvidos']}" />					
							</t:column>
							
							<t:column>
								<f:facet name="header"><h:outputText value="Bolsas" title="N� de Bolsas Solicitadas"/></f:facet>					
								<h:outputText value="#{projeto['bolsas_solicitadas']}" />					
							</t:column>

							
							<t:column>
                                <f:facet name="header"><h:outputText value="Pessoa Jur�dica" title="Or�amento solicitado para Pessoa Jur�dica"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['pessoa_juridica']}" title="Or�amento solicitado para Pessoa Jur�dica">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>      
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Pessoa F�sica" title="Or�amento solicitado para Pessoa F�sica"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['pessoa_fisica']}" title="Or�amento solicitado para Pessoa F�sica">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>          
                                <h:outputText value="</font>" escape="false"/>        
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Material de Consumo" title="Or�amento solicitado para Material de Consumo"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['material_consumo']}" title="Or�amento solicitado para Material de Consumo">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>       
                                <h:outputText value="</font>" escape="false"/>           
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Di�rias" title="Or�amento solicitado para Di�rias"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['diarias']}" title="Or�amento solicitado para Material de Di�rias">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>    
                                <h:outputText value="</font>" escape="false"/>              
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Passagens" title="Or�amento solicitado para Passagens"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['passagens']}" title="Or�amento solicitado para Material de Passagens">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>               
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Equipamentos" title="Or�amento solicitado para Equipamentos"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['equipamentos']}" title="Or�amento solicitado para Material de Equipamentos">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>  
                                <h:outputText value="</font>" escape="false"/>                
                            </t:column>
							


							<t:column>
								<f:facet name="header"><h:outputText value="Qtd." title="Avalia��es Ad Hoc (Quantidade)"/></f:facet>
								<h:outputText value="#{projeto['total_avaliacoes_ad_hoc']}" title="Total de avalia��es ad hoc."/>
							</t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="M�x." title="Avalia��es Ad Hoc (Maior Nota)"/></f:facet>
                                <h:outputText value="#{projeto['max_avaliacao_ad_hoc']}" title="Maior Nota das avalia��es ad hoc.">
                                	<f:convertNumber pattern="#0.0"/>
                                </h:outputText>                  
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="M�n." title="Avalia��es Ad Hoc (Menor Nota)"/></f:facet>
                                <h:outputText value="#{projeto['min_avaliacao_ad_hoc']}" title="Menor Nota das avalia��es ad hoc.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="M�d." title="M�dia das avalia��es ad hoc."/></f:facet>
                                <h:outputText value="<font color='blue'>" escape="false"/>
                                <h:outputText value="#{projeto['med_avaliacao_ad_hoc']}" title="M�dia das avalia��es ad hoc.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>



							<t:column>
								<f:facet name="header"><h:outputText value="Qtd." title="Total de avalia��es do comit�."/></f:facet>
								<h:outputText value="#{projeto['total_avaliacoes_comite']}" title="Total de avalia��es do comit�."/>
							</t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="M�x." title="Maior Nota das avalia��es do comit�."/></f:facet>
                                <h:outputText value="#{projeto['max_avaliacao_comite']}" title="Maior Nota das avalia��es do comit�.">
                                	<f:convertNumber pattern="#0.0"/>
                                </h:outputText>                  
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="M�n." title="Menor Nota das avalia��es do comit�."/></f:facet>
                                <h:outputText value="#{projeto['min_avaliacao_comite']}" title="Menor Nota das avalia��es do comit�.">                                
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="M�d." title="M�dia das avalia��es do comit�."/></f:facet>
                                <h:outputText value="<font color='blue'>" escape="false"/>
                                <h:outputText value="#{projeto['med_avaliacao_comite']}" title="M�dia das avalia��es do comit�.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>


							<t:column>
                                <f:facet name="header"><h:outputText value="M�dia Geral" title="M�dia das avalia��es."/></f:facet>
                                <h:outputText value="#{projeto['media']}" title="M�dia Geral das avalia��es.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                            </t:column>
							
							
							<t:column width="2%">
								<h:commandLink  title="Visualizar" action="#{ projetoBase.view }" id="btView">
								      <f:param name="id" value="#{projeto['id_projeto']}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>						
						</h:dataTable>
					
						<c:if test="${empty classificarProjetosBean.classificacao}">
							<center><i> Nenhum projeto foi localizado.</i></center>
						</c:if>
				</td>
			</tr>				
			</tbody>
			
			<tfoot>
				<tr>	
					<td align="center">
						<h:commandButton action="#{classificarProjetosBean.confirmarClassificacao}" value="Confirmar Classifica��o"/>
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{classificarProjetosBean.cancelar}" immediate="true"/>
					</td>
				</tr>
			</tfoot>			
			
		 </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>