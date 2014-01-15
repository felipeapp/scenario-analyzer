<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.listagem .rodape{text-align: center !important;}
	.listagem .colData{width: 12% !important;text-align:center !important;}
	.listagem .colStatus{width: 25% !important;text-align: left;}
	.listagem .colIcones{width: 5% !important;text-align:center !important;}
	.listagem .colIcones img {padding-left:5px;}
</style>

<f:view>
<a4j:keepAlive beanName="notificarInscritos"></a4j:keepAlive>

<h:form id="formNotificacaoProcessoSeletivo">

	<h2>
		<ufrn:subSistema /> > 
		<h:commandLink action="#{processoSeletivo.listar}" value="Processos Seletivos"/> > 
		Notificar Inscritos
	</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>Caro Usuário, </p>
		<p>
		Esta operação permite ao usuário notificar os inscritos de um determinado processo seletivo. 
		Através desta página o usuário pode enviar uma mensagem para todos os inscritos e, também, visualizar as antigas notificações.
		</p>
	</div>


	<table class="formulario" width="100%">
		<caption>Informe o conteúdo da notificação</caption>
		<tbody>
			<tr>
				<th class="obrigatorio" style="padding-top:2px;">Status da Inscrição:</th>
				<td>
					<a4j:region>
						<h:selectManyCheckbox value="#{notificarInscritos.statusInscricaoSel}" id="statusInscricao">
							<f:selectItems value="#{notificarInscritos.comboStatusInscricao}" />
							<a4j:support event="onclick" action="#{notificarInscritos.carregaDestinatarios}"
								reRender="emailsNotificacao"/>
						</h:selectManyCheckbox>
					</a4j:region>
				</td>
			</tr>			
			<tr>
				<th style="vertical-align: top;font-weight:bold;">Destinatários:</th>
				<td>
					<a4j:outputPanel id="emailsNotificacao">
						<h:outputText style="overflow:auto;display:block;position:relative;height:50px;width:90%" 
							rendered="#{notificarInscritos.haDestinatarios}" value="#{notificarInscritos.destinatariosDesc}" />
						<h:outputText rendered="#{!notificarInscritos.haDestinatarios}" value="Não informado" />
					</a4j:outputPanel>
				</td>
			</tr>
			<tr>
				<th width="15%" class="obrigatorio">Assunto:</th>
				<td>
					<h:inputText id="assunto" style="width: 600px;" maxlength="255" value="#{notificarInscritos.obj.titulo}" />
				</td>
			</tr>
			<tr>	
				<th class="required">Mensagem:</th>		
				<td colspan="2">
					<h:inputTextarea id="mensagem" style="width: 600px;white-space:pre-line"   rows="9" value="#{notificarInscritos.obj.mensagem}" />
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton title="Enviar Mensagem" onclick="return confirm('Deseja realmente realizar esta operação?');"  
						value="Enviar Notificação" action="#{notificarInscritos.enviar}"/>
					<h:commandButton action="#{processoSeletivo.buscarInscritos}" immediate="true" 
						id="CancelarNotificarInscricao" value="<< Voltar"/>
				</td>
			</tr>
		</tfoot>	
	</table>

	<br/>
	<br/>
	
	<c:if  test="${not empty notificarInscritos.all}">
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes
			</div>
		</center>
	</c:if>
	
	<h:dataTable value="#{notificarInscritos.all}" var="item" rendered="#{not empty notificarInscritos.all}"  styleClass="listagem" 
				width="100%" rowClasses="linhaPar, linhaImpar" footerClass="rodape" 
				columnClasses="colData,colTitulo,colStatus,colIcones" >
				
		<f:facet name="caption"><f:verbatim>Notificações enviadas aos inscritos</f:verbatim></f:facet>
		
		<h:column headerClass="colData">
			<f:facet name="header"><f:verbatim>Data/Horário</f:verbatim></f:facet>
			<h:outputText value="#{item.data}">
				<f:convertDateTime pattern="dd/MM/yyyy hh:mm"/>
			</h:outputText>
		</h:column>
		
		<h:column>
			<f:facet name="header"><f:verbatim>Assunto</f:verbatim></f:facet>
			<h:outputText value="#{item.titulo}"/>
		</h:column>
		
		<h:column>
			<f:facet name="header"><f:verbatim>Enviado para Inscrições com o Status</f:verbatim></f:facet>
			<h:outputText value="#{item.statusInscricao}" />
		</h:column>
	
		<h:column>
			
			<f:facet name="header"><f:verbatim>&nbsp;</f:verbatim></f:facet>
				
			<h:commandLink title="Visualizar Detalhes" id="visualizarDetalhesNotificacao" actionListener="#{notificarInscritos.view}">
				<h:graphicImage url="/img/view.gif" />
				<f:param name="id" value="#{item.id}" />
			</h:commandLink>
		
		</h:column>
			
		<f:facet name="footer">
			<f:verbatim>
			<h:outputText escape="false" value="Total de <strong>#{notificarInscritos.qtdNotificacoes}</strong> registro(s)"/>
			</f:verbatim>
		</f:facet>
				
	</h:dataTable>
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	 
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>