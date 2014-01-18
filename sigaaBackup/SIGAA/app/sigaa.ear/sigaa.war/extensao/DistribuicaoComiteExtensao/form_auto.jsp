<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribui��o de A��es de Extens�o para Membros do Comit� (Autom�tico)</h2>

	<h:form id="form">
	  <table class="formulario" width="100%">
		<caption class="listagem">Distribuir A��es de Extens�o</caption>
			
			<tr>
				<td colspan="2">
					<t:dataTable id="listagem" value="#{filtroAtividades.resultadosBusca}" var="atividade" align="center" width="100%" styleClass="listagem tablesorter" rowClasses="linhaPar, linhaImpar">
					    <t:column style="width: 8%;" rendered="#{ atividade.selecionado }">
                               <f:facet name="header"><f:verbatim><p>C�digo</p></f:verbatim></f:facet>
                               <h:outputText value="#{atividade.codigo}"/>                    
                           </t:column>
						<t:column rendered="#{ atividade.selecionado }">
							<f:facet name="header"><f:verbatim>T�tulo</f:verbatim></f:facet>
							<h:outputText value="#{atividade.titulo}" />					
						</t:column>
						<t:column rendered="#{ atividade.selecionado }">
                               <f:facet name="header"><f:verbatim>Situa��o</f:verbatim></f:facet>
                               <h:outputText value="#{atividade.situacaoProjeto.descricao}" />                  
                           </t:column>
						<t:column rendered="#{ atividade.selecionado }">
							<f:facet name="header"><f:verbatim>�rea Tem�tica</f:verbatim></f:facet>
							<h:outputText value="#{atividade.areaTematicaPrincipal.descricao}" />
						</t:column>
					</t:dataTable>
				</td>
			</tr>
			
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
					<td colspan="4" align="center">	
						<h:commandButton action="#{distribuicaoExtensao.distribuirAuto}" value="Confirmar Distribui��o" id="btDistribuir"/>
						<h:commandButton action="#{distribuicaoExtensao.cancelar}" value="Cancelar" onclick="#{confirm}" id="btCancelar"/>						
					</td>		
				</tr>
			</tfoot>
			
		</table>
		
  </h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>