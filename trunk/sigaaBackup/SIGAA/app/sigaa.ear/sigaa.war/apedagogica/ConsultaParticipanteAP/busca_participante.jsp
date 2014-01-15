<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colIcone{text-align: right;width: 1%;}
	.colDocente{width: 25%;}
	.colAtividade{width: 25%;}
	.colPeriodo{text-align: center !important;width: 20%;}
	.colSituacao{text-align: left !important;width: 10%;}
</style>

<f:view>
	<a4j:keepAlive beanName="consultaParticipanteAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > ${consultaParticipanteAP.operacao.nome}</h2>

	<h:form id="formBuscaParticipante" prependId="true">

		<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.filtros}">
		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>O formulário abaixo permite efetuar a busca dos participantes em atividades de atualização pedagógica.</p>
			<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.grupo}">
			<p>Para o filtro Grupo de Atividade, por favor preencha com o texto inicial do grupo desejado que uma listagem será exibida para seleção.</p>
			</c:if>
			<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.atividade}">
			<p>Para o filtro Atividade, serão exibidas as atividades que pertencem ao grupo selecionado.</p>
			</c:if>
		</div>
		
		<%-- VISUALIZAÇÃO DO GRUPO DE ATIVIDADES --%>
			<table class="formulario">
				
				<caption>Busca de Participante</caption>
				
				<tbody>
				
					<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.filtroDocente}">
					<tr>
						<td></td>
						<th width="20%" class="${not empty consultaParticipanteAP.operacao.mapaCamposObrigatorios.filtroDocente?'required':''}">Participante:</th>
						<td>
							<h:inputText value="#{consultaParticipanteAP.filtroDocente}" 
							size="65" id="filtroDocente">
								<a4j:support event="onclick" reRender="labelFiltroAtividade,filtroAtividade">
								</a4j:support>
							</h:inputText>
						</td>
					</tr>
					</c:if>
						
					<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.filtroGrupo}">
					<tr>
						<td></td>
						<th class="${not empty consultaParticipanteAP.operacao.mapaCamposObrigatorios.filtroGrupo?'required':''}">Grupo de Atividade:</th>
						<td>
							 	<h:inputText value="#{consultaParticipanteAP.nomeGrupo}" id="grupo" size="65"/>
							 	<ufrn:help>O campo executa a operação auto-completar, onde ao ser preenchido, disponibiliza uma lista para seleção.</ufrn:help>
								<rich:suggestionbox id="suggestion"  width="430" height="100" minChars="3" immediate="true"
								 	for="grupo" suggestionAction="#{consultaParticipanteAP.autocompleteGrupoAtividade}"   
								 	var="_g" fetchValue="#{_g.denominacao}">
									<h:column>
										<h:outputText value="#{_g.denominacao}"/>
									</h:column>
									<a4j:support event="onselect" actionListener="#{consultaParticipanteAP.carregarAtividadesGrupo}" 
										reRender="formBuscaParticipante" focus="grupo" >
											<f:attribute name="idGrupo" value="#{_g.id}"  />
									</a4j:support>
							  </rich:suggestionbox>
						</td>
					</tr>
					</c:if>
									
					<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.filtroAtividade}">
					<tr>
						<td></td>
						<th>
							<h:outputLabel id="labelFiltroAtividade" 
								styleClass="#{(not empty consultaParticipanteAP.nomeGrupo && not empty consultaParticipanteAP.filtroGrupo ) || 
								not empty consultaParticipanteAP.operacao.mapaCamposObrigatorios.filtroAtividade?'required':''}" 
							value="Atividade:"/>
							
						</th>
						<td>
							<h:selectOneMenu value="#{consultaParticipanteAP.filtroAtividade}" id="filtroAtividade" immediate="true">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{consultaParticipanteAP.atividadesGrupoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					</c:if>
					
					<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.filtroSituacao}">
					<tr>
						<td>
							
						</td>
						<th width="20%"  class="${consultaParticipanteAP.operacao.mapaCamposObrigatorios.filtroSituacao?'require':''}">Situação:</th>
						<td>
							<h:selectOneMenu value="#{consultaParticipanteAP.filtroSituacao}" id="filtroSituacao">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{consultaParticipanteAP.statusCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					</c:if>
					
				</tbody>
				
				<tfoot>		
						
					<tr>
						<td colspan="3">
							<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.buscar}">
								<h:commandButton value="Buscar" 
									action="#{consultaParticipanteAP.buscar}" id="btnBuscarParticipante">
									<a4j:support event="onclick" reRender="labelFiltroAtividade,filtroAtividade"/>
								</h:commandButton>
							</c:if>		 
							<c:if test="${empty consultaParticipanteAP.operacao.mapaCamposEscondidos.imprimir}">
								<h:commandButton value="Imprimir" 
									action="#{consultaParticipanteAP.imprimir}" id="btnImprimir">
								</h:commandButton>
							</c:if>		 
							<h:commandButton value="Cancelar" immediate="true" onclick="#{confirm}"
									action="#{consultaParticipanteAP.cancelar}" id="btnCancelarBuscarParticipante"/> 		
						</td>
					</tr>
					
				</tfoot>
					
			</table>	
		</c:if>
		</h:form>
		
		<h:form id="resultadoBuscaParticipante" prependId="true" rendered="#{fn:length(consultaParticipanteAP.resultadosBusca)>0}">
		
		
			<br clear="all"/>	
			<center>
					<h:messages/>
					<div class="infoAltRem">
					    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Participante
					</div>
			</center>
			
			<table class="listagem"  width="80%" >
				<caption>Resultado da Consulta(${fn:length(consultaParticipanteAP.resultadosBusca)})</caption>
			</table>
			
		
		
		<t:dataTable value="#{consultaParticipanteAP.resultadosBusca}" 
				 var="_reg" styleClass="listagem"  width="80%" columnClasses="colDocente,colAtividade,colPeriodo,colIcone"
				 rowClasses="linhaPar, linhaImpar">
					<t:column styleClass="colDocente" headerstyleClass="colDocente" 
						rendered="#{empty consultaParticipanteAP.operacao.mapaCamposEscondidos.colunaDocente}">
						<f:facet name="header">
							<f:verbatim>Docente</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.docente.pessoa.nome}"/>
					</t:column>
					
					<t:column styleClass="colAtividade" headerstyleClass="colAtividade">
						<f:facet name="header">
							<f:verbatim>Atividade</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.nome}"/>
					</t:column>
					
					<t:column styleClass="colAtividade" headerstyleClass="colAtividade">
						<f:facet name="header">
							<f:verbatim>Grupo</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.grupoAtividade.denominacao}"/>
					</t:column>
					
					
					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.inicio}"/> a
						<h:outputText value="#{_reg.atividade.fim}"/>
					</t:column>
					
					<t:column styleClass="colSituacao" headerstyleClass="colSituacao"
						rendered="#{empty consultaParticipanteAP.operacao.mapaCamposEscondidos.colunaSituacao}">
						<f:facet name="header">
							<f:verbatim>Situação</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.descricaoSituacao}"/> 
					</t:column>
					

					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
			
						<h:commandButton image="/img/seta.gif" actionListener="#{consultaParticipanteAP.escolheParticipante}"
								title="Selecionar Participante" id="selecionarParticipante">
							<f:attribute name="idParticipante" value="#{_reg.id}" />
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
