<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>

	<h:form>
	<h2 class="title"><ufrn:subSistema /> > Notificação Acadêmica</h2>
	
	<div class="descricaoOperacao">
	<b>Caro usuário,</b> 
	<br/><br/>
	Esta operação permite que sejam enviadas notificações a grupos de discentes as quais serão visualizadas quando acessarem o sistema. Se necessário, é possível solicitar a marcação da ciência pelos discentes, impossibilitando seu acesso ao restante do sistema até que seja confirmada a leitura. 
	<br/><br/>
	Novas opções de notificação podem ser criadas solicitando-as aos administradores do sistema
	</div>
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
					<c:if test="${acesso.administradorSistema}">
						<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
						<h:commandLink action="#{notificacaoAcademica.preCadastrar}" value="Cadastrar"/>
					</c:if>
					<img src="/sigaa/img/group.png">: Listar Destinatários
					<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/>: Simular Visualização pelo Discente
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			</div>
	</center>
	
	<c:if test="${ empty notificacaoAcademica.all }">
	<p style="font-weight:bold;text-align:center;color:red;margin:20px;">Nenhum item foi encontrado</p>
	</c:if>
	
	<c:if test="${ not empty notificacaoAcademica.all }">
			<table class="formulario" width="100%">
				<caption class="listagem">Lista de Notificações</caption>
				<thead>
					<tr>
						<th width="3%"></th>
						<th width="70%">Descrição</th>
						<c:if test="${notificacaoAcademica.possuiAnoPeriodoReferencia}">
							<th width="16%"><p align="center">Ano-Período</p></th>
						</c:if>	
						<th width="16%"><p align="center">Exige Confirmação</p></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				
				<tbody>
				<a4j:repeat value="#{notificacaoAcademica.all}" var="n" rowKeyVar="linha" >
						
					<tr class="<h:outputText value="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" />" style="position:relative;width:80%;margin:auto;border-right:1px solid #CCC;border-left:1px solid #CCC;">
						<td>
							<label title='<h:outputText value="#{n.descricao} " />'>
							<span style="display:inline-block;width:3%;" id="exibe_<h:outputText value='#{n.id}' />">
								<h:selectBooleanCheckbox styleClass="selecionados" value="#{n.exibirNumeroDiscentes}" valueChangeListener="#{notificacaoAcademica.marcarNotificacao}">
									<f:attribute name="idNotificacao" value="#{n.id}" /> 
									<a4j:support event="onclick" onsubmit="true" reRender="exibirDiscentes, discentes" />
								</h:selectBooleanCheckbox>
							</span>
							</label>
						</td>
						
						<td>
							<h:outputText value="#{n.descricao}" />
						</td>	
						
						<c:if test="${notificacaoAcademica.possuiAnoPeriodoReferencia}">
							<td align="center">
								<a4j:outputPanel rendered="#{n.anoPeriodoReferencia}">
									<h:selectOneMenu value="#{ n.anoReferencia }">
										<f:selectItems value="#{ notificacaoAcademica.anosCombo }" />
									</h:selectOneMenu>
									-
									<h:selectOneMenu value="#{ n.periodoReferencia }">
										<f:selectItems value="#{ notificacaoAcademica.periodosCombo }" />
									</h:selectOneMenu>
								</a4j:outputPanel>
								<a4j:outputPanel rendered="#{!n.anoPeriodoReferencia}">
								-
								</a4j:outputPanel>
							</td>	
						</c:if>
							
						<td align="center">
							<h:outputText value="Sim" rendered="#{ n.exigeConfirmacao }" />
							<h:outputText value="Não" rendered="#{ !n.exigeConfirmacao }" />
						</td>

						<td align="center">
							<h:commandLink style="border: 0;" action="#{notificacaoAcademica.carregarDestinatarios}" title="Listar Destinatários">
								<f:param name="id" value="#{n.id}" />
								<h:graphicImage value="/img/group.png" style="overflow: visible;"/>
							</h:commandLink>
						</td>
						
						<td>
							<h:commandLink action="#{notificacaoAcademica.mostrar}" style="border: 0;" title="Simular Visualização pelo Discente">
								<f:param name="id" value="#{n.id}" />
								<h:graphicImage url="/img/buscar.gif" alt="Simular Visualização pelo Discente" />
							</h:commandLink>
						</td>	
						
						<td>
							<h:commandLink action="#{notificacaoAcademica.atualizar}" style="border: 0;" title="Alterar">
								<f:param name="id" value="#{n.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
							</h:commandLink>
						</td>
						
						<td>
							<h:commandLink title="Remover" style="border: 0;" action="#{notificacaoAcademica.desativar}" onclick="#{ confirmDelete }">
								<f:param name="id" value="#{n.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover" />
							</h:commandLink>
						</td>
					</tr>
				</a4j:repeat>
				</tbody>
			</table>
			
			<div style="position:relative;width:80%;margin:auto;border-top:1px solid #CCC;"></div>
				
			<div align="center" style=";margin-top:5px;">
				<h:commandButton value="Notificar" action="#{notificacaoAcademica.confirmar}"/>
			</div>
		</c:if>		
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
