<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colIcone{text-align: right;width: 1%;}
	.colDocente{width: 25%;}
	.colAtividade{width: 25%;}
	.colPeriodo{text-align: center !important;width: 20%;}
	.colSituacao{text-align: left !important;width: 10%;}
</style>

<f:view>
	<a4j:keepAlive beanName="consultaAtividadeAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > ${consultaAtividadeAP.operacao.nome}</h2>

	<h:form id="formBuscaParticipante" prependId="true">

		<c:if test="${empty consultaAtividadeAP.operacao.mapaCamposEscondidos.filtros}">
		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>O formulário abaixo permite efetuar a busca das atividades de atualização pedagógica.</p>
			<p>Para o filtro Grupo de Atividade, por favor preencha com o texto inicial do grupo desejado que uma listagem será exibida para seleção.</p>
		</div>
		
		<%-- VISUALIZAÇÃO DO GRUPO DE ATIVIDADES --%>
			<table class="formulario">
				
				<caption>Busca de Atividade</caption>
				
				<tbody>
										
					<c:if test="${empty consultaAtividadeAP.operacao.mapaCamposEscondidos.filtroGrupo}">
					<tr>
						<td>
							
						</td>
						<th class="required">Grupo de Atividade:</th>
						<td>
							 	<h:inputText value="#{consultaAtividadeAP.nomeGrupo}"	id="grupo" size="65"/>
								<rich:suggestionbox id="suggestion"  width="430" height="100" minChars="3" 
							 	for="grupo" suggestionAction="#{consultaAtividadeAP.autocompleteGrupoAtividade}" 
							 	var="_g" fetchValue="#{_g.denominacao}">
								<h:column>
									<h:outputText value="#{_g.denominacao}"/>
								</h:column>
								<a4j:support event="onselect" reRender="formBuscaParticipante" focus="grupo" >
									<f:setPropertyActionListener target="#{consultaAtividadeAP.filtroGrupo}" value="#{_g.id}"/>
									<f:setPropertyActionListener target="#{consultaAtividadeAP.filtroAtividade}" value="#{0}"/>
									<f:setPropertyActionListener target="#{consultaAtividadeAP.chkGrupo}" value="#{true}"/>
								</a4j:support>
							  </rich:suggestionbox>
						</td>
					</tr>
					</c:if>
				
					
				</tbody>
				
				<tfoot>		
						
					<tr>
						<td colspan="3">
							<c:if test="${empty consultaAtividadeAP.operacao.mapaCamposEscondidos.buscar}">
								<h:commandButton value="Buscar" 
									action="#{consultaAtividadeAP.buscar}" id="btnBuscarParticipante">
								</h:commandButton>
							</c:if>		 
							<h:commandButton value="Cancelar" immediate="true" onclick="#{confirm}"
									action="#{consultaAtividadeAP.cancelar}" id="btnCancelarBuscarAtividade"/> 		
						</td>
					</tr>
					
				</tfoot>
					
			</table>	
		</c:if>
		</h:form>
		
		<h:form id="resultadoBuscaParticipante" prependId="true">
		
		<c:if test="${not empty consultaAtividadeAP.resultadosBusca}">
			<br clear="all"/>	
			<center>
					<h:messages/>
					<div class="infoAltRem">
					    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Atividade
					</div>
			</center>
			
			<table class="listagem"  width="80%" >
				<caption>Resultado da Consulta(${fn:length(consultaAtividadeAP.resultadosBusca)})</caption>
			</table>
			
		</c:if>
		
		<t:dataTable value="#{consultaAtividadeAP.resultadosBusca}" rendered="#{not empty consultaAtividadeAP.resultadosBusca}"
			 var="_reg" styleClass="listagem"  width="80%" columnClasses="colDocente,colAtividade,colPeriodo,colIcone"
				  rowClasses="linhaPar, linhaImpar">
					
					<t:column styleClass="colAtividade" headerstyleClass="colAtividade">
						<f:facet name="header">
							<f:verbatim>Atividade</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.nome}"/>
					</t:column>
					
					<t:column styleClass="colAtividade" headerstyleClass="colAtividade">
						<f:facet name="header">
							<f:verbatim>Grupo</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.grupoAtividade.denominacao}"/>
					</t:column>
					
					
					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.inicio}"/> a
						<h:outputText value="#{_reg.fim}"/>
					</t:column>
				
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
			
						<h:commandButton image="/img/seta.gif" actionListener="#{consultaAtividadeAP.escolheAtividade}"
								title="Selecionar Atividade" id="selecionarAtividade">
							<f:attribute name="idAtividade" value="#{_reg.id}" />
						</h:commandButton>
					</t:column>	
					
							
		</t:dataTable>
		
		
	</h:form>
	
	<br>
	<center>
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
