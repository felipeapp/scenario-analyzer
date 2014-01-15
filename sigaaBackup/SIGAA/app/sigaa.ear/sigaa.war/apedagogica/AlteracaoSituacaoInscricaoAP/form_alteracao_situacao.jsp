<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colSiape,.colDocente,.colEmail,.colSituacao{text-align: left !important;}
	.colSiape,.colSituacao{width: 10%;}
</style>

<f:view>
	
	<a4j:keepAlive beanName="alteraSituacaoInscricaoAP"></a4j:keepAlive>
	<a4j:keepAlive beanName="consultaAtividadeAP"></a4j:keepAlive>
	<h2 class="title">
		<ufrn:subSistema /> > Altera��o da Situa��o da Inscri��o 
	</h2>
	
	<div class="descricaoOperacao">
		<p>Caro usu�rio,</p>
		<p>o formul�rio abaixo permite o Gestor do PAP alterar a situa��o das inscri��es dos participantes para atividade selecionada.</p>
	</div>
	
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/ava/img/page_edit_2.png"/>: Marcar Inscri��o
			<h:graphicImage value="/ava/img/user_ok.png"/>: Marcar Presen�a	
			<h:graphicImage value="/img/monitoria/user1_delete.png"/>: Marcar Aus�ncia		
		</div>
	</center>
		
	<h:form id="formalteraSituacaoInscricaoAP">
	<table class="formulario" width="90%">
		<caption class="visualizacao">Dados da Inscri��o do Participante</caption>

		<tr>
			<th class="rotulo">Grupo de Atividades:</th>
			<td>
				<h:outputText value="#{alteraSituacaoInscricaoAP.obj.grupoAtividade.denominacao}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th class="rotulo">Atividade:</th>
			<td>
				<h:outputText value="#{alteraSituacaoInscricaoAP.obj.nome}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th class="rotulo">Per�odo da Atividade:</th>
			<td>
				<h:outputText value="#{alteraSituacaoInscricaoAP.obj.inicio}"/> a
				<h:outputText value="#{alteraSituacaoInscricaoAP.obj.fim}"/>
			</td>
		</tr>
		
		<tr>
			<th class="rotulo">Carga Hor�ria:</th>
			<td>
				<h:outputText value="#{alteraSituacaoInscricaoAP.obj.ch}h" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th class="rotulo">N� de Vagas:</th>
			<td>
				<h:outputText value="#{alteraSituacaoInscricaoAP.obj.numVagas}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subListagem">Participantes</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<t:dataTable id="participantesAtividade" value="#{alteraSituacaoInscricaoAP.participantes}"  rendered="#{not empty alteraSituacaoInscricaoAP.participantes}"
					 var="_reg" styleClass="subListagem"  width="100%" headerClass="colSiape,colDocente,colEmail,colSituacao,icon,icon,icon" 
					 columnClasses="colSiape,colDocente,colEmail,colSituacao,icon,icon,icon" rowClasses="linhaImpar,linhaPar">
							
							<t:column styleClass="colSiape" headerstyleClass="colSiape">
								<f:facet name="header">
									<f:verbatim>Siape</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.siape}"/>
							</t:column>
							<t:column styleClass="colDocente" headerstyleClass="colDocente">
								<f:facet name="header">
									<f:verbatim>Participante</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.nome}"/>
							</t:column>
		
							<t:column styleClass="colEmail" headerstyleClass="colEmail">
								<f:facet name="header">
									<f:verbatim>E-mail</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.pessoa.email}"/> 
							</t:column>
							
							<t:column styleClass="colEmail" headerstyleClass="colEmail">
								<f:facet name="header">
									<f:verbatim>Categoria</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.categoria.descricao}"/> 
							</t:column>
							
							<t:column styleClass="colSituacao" headerstyleClass="colSituacao">
								<f:facet name="header">
									<f:verbatim>Situa��o</f:verbatim>
								</f:facet>
								<a4j:region>
									<h:selectOneMenu rendered="true" id="selecaoStatusParticipante" 
											valueChangeListener="#{alteraSituacaoInscricaoAP.addInscricaoStatusAlterado}" value="#{_reg.situacao}" >
											<f:selectItems value="#{consultaParticipanteAP.statusCombo}"/>
											<a4j:support event="onchange" reRender="participantesAtividade">
												<f:param  name="idParticipante" value="#{_reg.id}" />
											</a4j:support>
									</h:selectOneMenu>
								</a4j:region>
							</t:column>
					
							<t:column>
								<f:facet name="header">
								<h:panelGroup>						
									<a4j:commandLink id="inputTodosIncritos" title="Todos Inscritos" actionListener="#{alteraSituacaoInscricaoAP.changeAllInscrito}" reRender="participantesAtividade">
										<h:graphicImage value="/ava/img/page_edit_2.png"/>
									</a4j:commandLink>
								</h:panelGroup>	
								</f:facet>
								<h:panelGroup>						
									<a4j:commandLink title="Inscrito" actionListener="#{alteraSituacaoInscricaoAP.addInscricaoStatusInscrito}" reRender="participantesAtividade">
										<f:param  name="idParticipante" value="#{_reg.id}" />
										<h:graphicImage value="/ava/img/page_edit_2.png"/>
									</a4j:commandLink>
								</h:panelGroup>						
							</t:column>
					
							<t:column>
								<f:facet name="header">
								<h:panelGroup>						
									<a4j:commandLink id="inputTodosPresentes" title="Todos Presentes" actionListener="#{alteraSituacaoInscricaoAP.changeAllPresente}" reRender="participantesAtividade">
										<h:graphicImage value="/ava/img/user_ok.png"/>
									</a4j:commandLink>
								</h:panelGroup>	
								</f:facet>
								<h:panelGroup>						
									<a4j:commandLink title="Inscrito" actionListener="#{alteraSituacaoInscricaoAP.addInscricaoStatusConcluido}" reRender="participantesAtividade">
										<f:param  name="idParticipante" value="#{_reg.id}" />
										<h:graphicImage value="/ava/img/user_ok.png"/>
									</a4j:commandLink>	
								</h:panelGroup>		
							</t:column>
							
							<t:column>
								<f:facet name="header">
								<h:panelGroup>						
									<a4j:commandLink id="inputTodosAusentes" title="Todos Ausentes" actionListener="#{alteraSituacaoInscricaoAP.changeAllAusente}" reRender="participantesAtividade">
										<h:graphicImage value="/img/monitoria/user1_delete.png"/>
									</a4j:commandLink>
								</h:panelGroup>	
								</f:facet>
								<h:panelGroup>						
									<a4j:commandLink title="Ausente" actionListener="#{alteraSituacaoInscricaoAP.addInscricaoStatusAusente}" reRender="participantesAtividade">
										<f:param  name="idParticipante" value="#{_reg.id}" />
										<h:graphicImage value="/img/monitoria/user1_delete.png"/>
									</a4j:commandLink>
								</h:panelGroup>	
							</t:column>
					
				</t:dataTable>
			
			</td>
		</tr>
		
	</table>

	<table class="formulario" width="99%">
		<tfoot>
			<tr>
				<td>
					<h:commandButton action="#{alteraSituacaoInscricaoAP.cadastrar}" value="Alterar Situa��o"
								title="Alterar Situa��o" id="alterarParticipacao"/>
					<h:commandButton action="#{alteraSituacaoInscricaoAP.voltar}" value="<< Voltar" immediate="true"
								title="Voltar" id="voltarAlteracaoSituacao"/>
					<h:commandButton action="#{alteraSituacaoInscricaoAP.cancelar}" value="Cancelar" immediate="true"
							onclick="#{confirm}"	title="Cancelar" id="cancelarParticipacao"/>
				</td>
			</tr>	
		</tfoot>
	</table>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
