<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.numero_avaliadores {
		padding: 2px;
		font-size: 1.3em;
		text-align: center;
		color: #292;
	}
</style>

<f:view>

	<h2><ufrn:subSistema /> > Confirmação da Distribuição Automática </h2>
	
	<div class="descricaoOperacao">
		<b>Atenção:</b><br/>
		As Ações de Extensão serão distribuídas automaticamente entre os avaliadores habilitados da Área Temática selecionada. <br/>
		Para distribuir informe a quantidade de avaliadores que cada ação deve receber e click do botão 'Confirmar Distribuição'. 
	</div>
	
		<h:form id="form">
		
		<table class="listagem">
		    <caption>Ações de Extensão Selecionadas (${fn:length(filtroAtividades.resultadosBusca)})</caption>
	 		<tbody>
				<tr>
					<td >
				
						<t:dataTable id="listaProjetos" value="#{ distribuicaoExtensao.obj.atividades }" var="atividade" 
						  align="center" width="100%" styleClass="listagem tablesorter" rowClasses="linhaPar, linhaImpar">
							
							<t:column style="text-align: right;">
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
							
						
						</t:dataTable>
						<c:if test="${empty filtroAtividades.resultadosBusca}">
							<center><font color="red">Não há ações de extensão distribuídas</font> </center>
						</c:if>					
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<b>Número de Avaliadores por Projeto:</b> &nbsp;
						<h:inputText id="numeroAvaliadoresProjeto" styleClass="numero_avaliadores"
							value="#{distribuicaoExtensao.obj.numAvaliacoesPorProjeto}" 
							size="2" maxlength="2" onkeyup="formatarInteiro(this);"/>
					</td>
				</tr>
			
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Confirmar Distribuição" action="#{ distribuicaoExtensao.confirmarDistribuicaoAuto }" id="btConfirmarDistribuicaoAuto" />
						<h:commandButton value="<< Distribuir Outras Ações" action="#{ distribuicaoExtensao.novaDistribuicaoAuto }" id="btNovaDistribuicaoAuto" />
						<h:commandButton value="Cancelar" action="#{ atividadeExtensao.cancelar }" onclick="#{confirm}" id="btCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		 	
		 </table>
		 [NA = Número de Avaliações]
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>