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

	<h2><ufrn:subSistema /> > Confirma��o da Distribui��o Autom�tica </h2>
	
	<div class="descricaoOperacao">
		<b>Aten��o:</b><br/>
		As A��es de Extens�o ser�o distribu�das automaticamente entre os avaliadores habilitados da �rea Tem�tica selecionada. <br/>
		Para distribuir informe a quantidade de avaliadores que cada a��o deve receber e click do bot�o 'Confirmar Distribui��o'. 
	</div>
	
		<h:form id="form">
		
		<table class="listagem">
		    <caption>A��es de Extens�o Selecionadas (${fn:length(filtroAtividades.resultadosBusca)})</caption>
	 		<tbody>
				<tr>
					<td >
				
						<t:dataTable id="listaProjetos" value="#{ distribuicaoExtensao.obj.atividades }" var="atividade" 
						  align="center" width="100%" styleClass="listagem tablesorter" rowClasses="linhaPar, linhaImpar">
							
							<t:column style="text-align: right;">
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
							
						
						</t:dataTable>
						<c:if test="${empty filtroAtividades.resultadosBusca}">
							<center><font color="red">N�o h� a��es de extens�o distribu�das</font> </center>
						</c:if>					
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<b>N�mero de Avaliadores por Projeto:</b> &nbsp;
						<h:inputText id="numeroAvaliadoresProjeto" styleClass="numero_avaliadores"
							value="#{distribuicaoExtensao.obj.numAvaliacoesPorProjeto}" 
							size="2" maxlength="2" onkeyup="formatarInteiro(this);"/>
					</td>
				</tr>
			
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Confirmar Distribui��o" action="#{ distribuicaoExtensao.confirmarDistribuicaoAuto }" id="btConfirmarDistribuicaoAuto" />
						<h:commandButton value="<< Distribuir Outras A��es" action="#{ distribuicaoExtensao.novaDistribuicaoAuto }" id="btNovaDistribuicaoAuto" />
						<h:commandButton value="Cancelar" action="#{ atividadeExtensao.cancelar }" onclick="#{confirm}" id="btCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		 	
		 </table>
		 [NA = N�mero de Avalia��es]
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>