<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style>
	#container{ width:  99%; } /* Sobre escreve o estilo padrão do sigaa para fica em tela cheia  */
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
								<h:outputText value="#{projeto['classificacao']}º" />
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
								<h:outputText value="#{projeto['ano']}" />													
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
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
								<f:facet name="header"><f:verbatim>Área CNPq.</f:verbatim></f:facet>					
								<h:outputText value="#{projeto['area_cnpq']}" />					
							</t:column>
							
							<t:column>
								<f:facet name="header"><f:verbatim>Dimensão</f:verbatim></f:facet>					
								<h:outputText value="#{projeto['ensino'] ? 'Ensino ' : ''}" />
								<h:outputText value="#{projeto['pesquisa'] ? 'Pesquisa ' : ''}" />
								<h:outputText value="#{projeto['extensao'] ? 'Extensão' : ''}" />
							</t:column>			
										
							
							<t:column>
								<f:facet name="header"><h:outputText value="Discentes" title="Nº de Discentes"/></f:facet>					
								<h:outputText value="#{projeto['total_discentes_envolvidos']}" />					
							</t:column>
							
							<t:column>
								<f:facet name="header"><h:outputText value="Bolsas" title="Nº de Bolsas Solicitadas"/></f:facet>					
								<h:outputText value="#{projeto['bolsas_solicitadas']}" />					
							</t:column>

							
							<t:column>
                                <f:facet name="header"><h:outputText value="Pessoa Jurídica" title="Orçamento solicitado para Pessoa Jurídica"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['pessoa_juridica']}" title="Orçamento solicitado para Pessoa Jurídica">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>      
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Pessoa Física" title="Orçamento solicitado para Pessoa Física"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['pessoa_fisica']}" title="Orçamento solicitado para Pessoa Física">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>          
                                <h:outputText value="</font>" escape="false"/>        
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Material de Consumo" title="Orçamento solicitado para Material de Consumo"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['material_consumo']}" title="Orçamento solicitado para Material de Consumo">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>       
                                <h:outputText value="</font>" escape="false"/>           
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Diárias" title="Orçamento solicitado para Diárias"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['diarias']}" title="Orçamento solicitado para Material de Diárias">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>    
                                <h:outputText value="</font>" escape="false"/>              
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Passagens" title="Orçamento solicitado para Passagens"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['passagens']}" title="Orçamento solicitado para Material de Passagens">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>               
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Equipamentos" title="Orçamento solicitado para Equipamentos"/></f:facet>
                                <h:outputText value="<font color='green'>" escape="false"/>
                                <h:outputText value="#{projeto['equipamentos']}" title="Orçamento solicitado para Material de Equipamentos">
                                	<f:convertNumber pattern="#,###,##0.00" currencyCode="BRL" currencySymbol="R$" />
                                </h:outputText>  
                                <h:outputText value="</font>" escape="false"/>                
                            </t:column>
							


							<t:column>
								<f:facet name="header"><h:outputText value="Qtd." title="Avaliações Ad Hoc (Quantidade)"/></f:facet>
								<h:outputText value="#{projeto['total_avaliacoes_ad_hoc']}" title="Total de avaliações ad hoc."/>
							</t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Máx." title="Avaliações Ad Hoc (Maior Nota)"/></f:facet>
                                <h:outputText value="#{projeto['max_avaliacao_ad_hoc']}" title="Maior Nota das avaliações ad hoc.">
                                	<f:convertNumber pattern="#0.0"/>
                                </h:outputText>                  
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Mín." title="Avaliações Ad Hoc (Menor Nota)"/></f:facet>
                                <h:outputText value="#{projeto['min_avaliacao_ad_hoc']}" title="Menor Nota das avaliações ad hoc.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Méd." title="Média das avaliações ad hoc."/></f:facet>
                                <h:outputText value="<font color='blue'>" escape="false"/>
                                <h:outputText value="#{projeto['med_avaliacao_ad_hoc']}" title="Média das avaliações ad hoc.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>



							<t:column>
								<f:facet name="header"><h:outputText value="Qtd." title="Total de avaliações do comitê."/></f:facet>
								<h:outputText value="#{projeto['total_avaliacoes_comite']}" title="Total de avaliações do comitê."/>
							</t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Máx." title="Maior Nota das avaliações do comitê."/></f:facet>
                                <h:outputText value="#{projeto['max_avaliacao_comite']}" title="Maior Nota das avaliações do comitê.">
                                	<f:convertNumber pattern="#0.0"/>
                                </h:outputText>                  
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Mín." title="Menor Nota das avaliações do comitê."/></f:facet>
                                <h:outputText value="#{projeto['min_avaliacao_comite']}" title="Menor Nota das avaliações do comitê.">                                
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>
                            </t:column>
							<t:column>
                                <f:facet name="header"><h:outputText value="Méd." title="Média das avaliações do comitê."/></f:facet>
                                <h:outputText value="<font color='blue'>" escape="false"/>
                                <h:outputText value="#{projeto['med_avaliacao_comite']}" title="Média das avaliações do comitê.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>


							<t:column>
                                <f:facet name="header"><h:outputText value="Média Geral" title="Média das avaliações."/></f:facet>
                                <h:outputText value="#{projeto['media']}" title="Média Geral das avaliações.">
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
						<h:commandButton action="#{classificarProjetosBean.confirmarClassificacao}" value="Confirmar Classificação"/>
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{classificarProjetosBean.cancelar}" immediate="true"/>
					</td>
				</tr>
			</tfoot>			
			
		 </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>