<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribuição de Ações de Extensão para Membros do Comitê (Automático)</h2>

	<h:form id="form">
	  <table class="formulario" width="100%">
		<caption class="listagem">Distribuir Ações de Extensão</caption>
			
			<tr>
				<td colspan="2">
					<t:dataTable id="listagem" value="#{filtroAtividades.resultadosBusca}" var="atividade" align="center" width="100%" styleClass="listagem tablesorter" rowClasses="linhaPar, linhaImpar">
					    <t:column style="width: 8%;" rendered="#{ atividade.selecionado }">
                               <f:facet name="header"><f:verbatim><p>Código</p></f:verbatim></f:facet>
                               <h:outputText value="#{atividade.codigo}"/>                    
                           </t:column>
						<t:column rendered="#{ atividade.selecionado }">
							<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
							<h:outputText value="#{atividade.titulo}" />					
						</t:column>
						<t:column rendered="#{ atividade.selecionado }">
                               <f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
                               <h:outputText value="#{atividade.situacaoProjeto.descricao}" />                  
                           </t:column>
						<t:column rendered="#{ atividade.selecionado }">
							<f:facet name="header"><f:verbatim>Área Temática</f:verbatim></f:facet>
							<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
						</t:column>
					</t:dataTable>
				</td>
			</tr>
			
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
					<td colspan="4" align="center">	
						<h:commandButton action="#{distribuicaoExtensao.distribuirAuto}" value="Confirmar Distribuição" id="btDistribuir"/>
						<h:commandButton action="#{distribuicaoExtensao.cancelar}" value="Cancelar" onclick="#{confirm}" id="btCancelar"/>						
					</td>		
				</tr>
			</tfoot>
			
		</table>
		
  </h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>