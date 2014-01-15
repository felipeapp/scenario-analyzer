<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Distribuição Automática de Propostas para Avaliadores Ad hoc </h2>

	<div class="descricaoOperacao">
		<b>Atenção:</b><br/> 	
		 Somente poderão ser distribuídas as Ações de Extensão (Isoladas) que já foram 'SUBMETIDAS' à PROEx ou as que estão 'AGUARDANDO AVALIAÇÃO'.<br/>
	</div>

	<br/>
	
	<%@include file="/extensao/barra_filtro_atividade.jsp"%>
	
		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Ação
	    </div>
		<br/>
	
		<h:form id="form">
		
		<table class="listagem">
		    <caption>Ações de Extensão Encontradas (${fn:length(filtroAtividades.resultadosBusca)})</caption>
	 		<tbody>
				<tr>
					<td >
				
						<t:dataTable id="listaProjetos" value="#{filtroAtividades.resultadosBusca}" var="atividade" 
						  align="center" width="100%" styleClass="listagem tablesorter" rowClasses="linhaPar, linhaImpar">
							
							<t:column style="width: 4%;">
									<f:facet name="header">
										<f:verbatim>
											<a href="javascript:selectAllCheckBox();"
												style="fontColor: blue">Todos</a>
										</f:verbatim>
									</f:facet>
									<h:selectBooleanCheckbox value="#{atividade.selecionado}" id="chkSelecionado"/>
							</t:column>

							<t:column style="width: 2%;">
								<f:facet name="header"><h:outputText value="NA" title="Número de Avaliações"/></f:facet>
								<h:outputText value="#{atividade.totalPareceristaAvaliando}" rendered="#{atividade.totalPareceristaAvaliando > 0}" />					
							</t:column>
							
                            <t:column style="width: 8%;">
                                <f:facet name="header"><f:verbatim>Código</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.codigo}" />                    
                            </t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
								<h:outputText value="#{atividade.titulo}" />					
							</t:column>

                            <t:column>
                                <f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.situacaoProjeto.descricao}" />
                            </t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Área Temática</f:verbatim></f:facet>
								<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
							</t:column>
							
							<t:column width="10%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim>Financiamento</f:verbatim></f:facet>
								<h:graphicImage url="/img/extensao/bullet_square_green.png" title="Financiamento Interno" rendered="#{atividade.financiamentoInterno}"/>		
								<h:graphicImage url="/img/extensao/bullet_square_red.png" title="Financiamento Externo" rendered="#{atividade.financiamentoExterno}"/>
								<h:graphicImage url="/img/extensao/bullet_square_blue.png" title="Auto Financiamento" rendered="#{atividade.autoFinanciado}"/>
								<h:graphicImage url="/img/extensao/bullet_square_yellow.png" title="Convênio Funpec" rendered="#{atividade.convenioFunpec}"/>
							</t:column>
																
							<t:column width="2%">
								<h:commandLink  title="Visualizar Ação" action="#{ atividadeExtensao.view }" id="visualizar_acao">
								      <f:param name="id" value="#{atividade.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>
							
							
						
						</t:dataTable>
						<c:if test="${empty filtroAtividades.resultadosBusca}">
							<center><font color="red">Não há ações de extensão pendentes para distribuição</font> </center>
						</c:if>					
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Distribuir >>" action="#{ distribuicaoExtensao.preDistribuirAuto }" id="btPreDistribuirAuto" />
						<h:commandButton value="Cancelar" action="#{ atividadeExtensao.cancelar }" onclick="#{confirm}" id="btCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		 	
		 </table>
		 [NA = Número de Avaliações]
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>