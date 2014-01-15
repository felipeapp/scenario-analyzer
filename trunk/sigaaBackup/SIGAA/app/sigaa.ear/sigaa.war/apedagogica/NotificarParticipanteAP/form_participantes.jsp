<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colNome{text-align: left;}
	.colIcone{ width: 16px;}
	.colSituacao{width: 10%;}
</style>

<f:view>
	<a4j:keepAlive beanName="notificarParticipanteAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Notificação de Participantes > Lista dos Participantes da Atividade </h2>
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>a listagem abaixo exibe todos os participantes de uma atividade de atualização pedagógica.</p>
		<p>Selecione somente os participantes que deseja notificar, e pressione o botão <strong>Adicionar Participantes a Notificação</strong>.</p>
	</div>
	
	<h:form id="formFiltroNotificacao">
		<table class="formulario" width="30%">
			<caption>Selecione o filtro abaixo</caption>
			<tr>
				<th width="45%">Situação</th>
				<td>
					<h:selectOneMenu id="filtroSituacao" 
						value="#{notificarParticipanteAP.situacaoFiltro}" 
						valueChangeListener="#{notificarParticipanteAP.marcarParticipanteStatus}" >
						<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
						<f:selectItems value="#{consultaParticipanteAP.statusCombo}"/>
						<a4j:support event="onchange" reRender="formListagemParticipacaoNotificacaoAP"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</table>
		<br/>
	</h:form>
	
	
	<h:form id="formListagemParticipacaoNotificacaoAP">


		<c:if test="${not empty notificarParticipanteAP.participantes}">
		<table class="listagem"  width="80%" >
			<caption>Listagem de Participantes(${fn:length(notificarParticipanteAP.participantes)})</caption>
		</table>
		</c:if>
		<t:dataTable value="#{notificarParticipanteAP.participantes}" rendered="#{not empty notificarParticipanteAP.participantes}"
			 var="_p" styleClass="listagem" headerClass="colIcone,colNome,colSituacao" id="formListagemParticipacaoNotificacaoAP"
			 columnClasses="colIcone,colNome,colSituacao" rowClasses="linhaPar, linhaImpar">
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
						<%-- 	<h:selectBooleanCheckbox value="#{notificarParticipanteAP.todosParticipantes}">
								<a4j:support event="onclick" 
									actionListener="#{notificarParticipanteAP.marcarDesmarcarParticipante}" 
									reRender="formListagemParticipacaoNotificacaoAP" />
							</h:selectBooleanCheckbox> --%>
						</f:facet>
						<h:selectBooleanCheckbox value="#{_p.selecionado}"/>
					</t:column>
					<t:column styleClass="colNome" headerstyleClass="colNome">
						<f:facet name="header">
							<f:verbatim>Nome</f:verbatim>
						</f:facet>
						<h:outputText value="#{_p.docente.nome}"/>
					</t:column>
					
					<t:column styleClass="colNome" headerstyleClass="colSituacao">
						<f:facet name="header">
							<f:verbatim>Status</f:verbatim>
						</f:facet>
						<h:outputText value="#{_p.descricaoSituacao}"/>
					</t:column>

										
		</t:dataTable>
		<table class="formulario"  width="100%">
			<tfoot>
				<tr>
					<td>
						<h:commandButton action="#{notificarParticipanteAP.voltar}" 
							value="<< Voltar" immediate="true" 
							title="Voltar" id="voltar"/>
						<h:commandButton action="#{notificarParticipanteAP.cancelar}" 
							value="Cancelar" immediate="true" onclick="#{confirm}"	
							title="Cancelar" id="cancelarNotificarParticipacao"/>
						<h:commandButton action="#{notificarParticipanteAP.submeterParticipantes}" 
							value="Próximo >>"title="Adicionar Destinatários" 
							id="submeterParticipantesSelecionados"/>
					</td>
				</tr>
			<tfoot>		
		</table>
		<center>
			<h:outputText  rendered="#{empty notificarParticipanteAP.participantes}" 
				value="Não existem participantes cadastrados até o momento."></h:outputText>
		</center>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
