<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colNome{text-align: left;}
	.colIcone{ width: 16px;}
	.colData{width: 10%;}
</style>

<f:view>
	<a4j:keepAlive beanName="notificarParticipanteAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema />  > Notificação de Participantes > Mensagem</h2>
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>o formulário abaixo exibe todos os e-mails dos participantes  que receberão a notificação, como também os dados da mensagem que receberão.</p>
		<p>Também é disponibilizado uma lista das notificações efetuadas anteriormente, possibilitando que os textos possam ser reaproveitados.
	</div>
	
	<h:form id="formMensagemNotifcacaoAP">
	
		<center>
				<h:messages/>
				<div class="infoAltRem">
				    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Aproveitar Dados na Nova Notificação
				</div>
		</center>
		
		<h:dataTable value="#{notificarParticipanteAP.all}" rendered="#{not empty notificarParticipanteAP.all}"
			 var="_nq" styleClass="listagem" headerClass="colNome,colData,colIcone" id="ListagemNotificacaoPassadas"
			 columnClasses="colNome,colData,colIcone" rowClasses="linhaPar, linhaImpar">
					<f:facet name="caption">
						<f:verbatim>Histórico das Notificações Enviadas</f:verbatim>
					</f:facet>
					
					<t:column styleClass="colNome" headerstyleClass="colNome">
						<f:facet name="header">
							<f:verbatim>Título</f:verbatim>
						</f:facet>
						<h:outputText value="#{_nq.titulo}"/>
					</t:column>
					
					<t:column styleClass="colData" headerstyleClass="colData">
						<f:facet name="header">
							<f:verbatim>Data de Envio</f:verbatim>
						</f:facet>
						<h:outputText value="#{_nq.data}"/>
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim></f:verbatim>
						</f:facet>
						<h:commandLink actionListener="#{notificarParticipanteAP.popularNovaNotificacao}" 
							title="Aproveitar Dados na Nova Notificação">
							<h:graphicImage url="/img/alterar.gif" />
							<f:attribute name="notificacaoAnterior" value="#{_nq}"/>
						</h:commandLink>
					</t:column>

										
		</h:dataTable>
		<br/>
	
		<table class="formulario" width="100%">
			<caption>Formulário de Mensagem de Notificação</caption>
			<tbody>
				<tr>
					<td colspan="2" class="subFormulario">Participantes Selecionados</td>
				</tr>
				<tr>
					<td colspan="2" >
						<h:outputText style="overflow:auto;display:block;position:relative;height:50px;width:90%" 
							 value="#{notificarParticipanteAP.destinatariosDesc}" />
					</td>
				</tr>
				
				<tr>
					<td colspan="2" class="subFormulario">Dados da Mensagem </td>
				</tr>
				
				<tr>
					<th class="required" width="10%">Título:</th>
					<td><h:inputText size="120" maxlength="255" value="#{notificarParticipanteAP.obj.titulo}"/> </td>
				</tr>
				
				<tr>
					<th class="required" valign="top">Mensagem:</th>
					<td><h:inputTextarea rows="10" cols="118" value="#{notificarParticipanteAP.obj.mensagem}"/> </td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton action="#{notificarParticipanteAP.telaParticipantes}" 
						value="<< Voltar"
						title="Enviar Notificação" id="voltarListaParticipantes"/>
					<h:commandButton action="#{notificarParticipanteAP.enviar}" 
						value="Enviar Notificação"
						title="Enviar Notificação" id="enviarNotificacaoParticipante"/>
					<h:commandButton action="#{notificarParticipanteAP.cancelar}" 
							value="Cancelar" immediate="true"
							onclick="#{confirm}" title="Cancelar" id="cancelarNotificacaoParticipante"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
