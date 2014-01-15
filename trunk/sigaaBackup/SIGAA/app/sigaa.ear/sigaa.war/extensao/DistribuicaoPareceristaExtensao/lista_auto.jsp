<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Distribui��o Autom�tica de Propostas para Avaliadores Ad hoc </h2>

	<div class="descricaoOperacao">
		<b>Aten��o:</b><br/> 	
		 Somente poder�o ser distribu�das as A��es de Extens�o (Isoladas) que j� foram 'SUBMETIDAS' � PROEx ou as que est�o 'AGUARDANDO AVALIA��O'.<br/>
	</div>

	<br/>
	
	<%@include file="/extensao/barra_filtro_atividade.jsp"%>
	
		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar A��o
	    </div>
		<br/>
	
		<h:form id="form">
		
		<table class="listagem">
		    <caption>A��es de Extens�o Encontradas (${fn:length(filtroAtividades.resultadosBusca)})</caption>
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
								<f:facet name="header"><h:outputText value="NA" title="N�mero de Avalia��es"/></f:facet>
								<h:outputText value="#{atividade.totalPareceristaAvaliando}" rendered="#{atividade.totalPareceristaAvaliando > 0}" />					
							</t:column>
							
                            <t:column style="width: 8%;">
                                <f:facet name="header"><f:verbatim>C�digo</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.codigo}" />                    
                            </t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>T�tulo</f:verbatim></f:facet>
								<h:outputText value="#{atividade.titulo}" />					
							</t:column>

                            <t:column>
                                <f:facet name="header"><f:verbatim>Situa��o</f:verbatim></f:facet>
                                <h:outputText value="#{atividade.situacaoProjeto.descricao}" />
                            </t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>�rea Tem�tica</f:verbatim></f:facet>
								<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
							</t:column>
							
							<t:column width="10%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim>Financiamento</f:verbatim></f:facet>
								<h:graphicImage url="/img/extensao/bullet_square_green.png" title="Financiamento Interno" rendered="#{atividade.financiamentoInterno}"/>		
								<h:graphicImage url="/img/extensao/bullet_square_red.png" title="Financiamento Externo" rendered="#{atividade.financiamentoExterno}"/>
								<h:graphicImage url="/img/extensao/bullet_square_blue.png" title="Auto Financiamento" rendered="#{atividade.autoFinanciado}"/>
								<h:graphicImage url="/img/extensao/bullet_square_yellow.png" title="Conv�nio Funpec" rendered="#{atividade.convenioFunpec}"/>
							</t:column>
																
							<t:column width="2%">
								<h:commandLink  title="Visualizar A��o" action="#{ atividadeExtensao.view }" id="visualizar_acao">
								      <f:param name="id" value="#{atividade.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>
							
							
						
						</t:dataTable>
						<c:if test="${empty filtroAtividades.resultadosBusca}">
							<center><font color="red">N�o h� a��es de extens�o pendentes para distribui��o</font> </center>
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
		 [NA = N�mero de Avalia��es]
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>