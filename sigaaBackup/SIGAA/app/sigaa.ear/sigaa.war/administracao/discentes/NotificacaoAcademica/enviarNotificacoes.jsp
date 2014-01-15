<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h:form>
	<h2 class="title"><ufrn:subSistema /> > Notificação Acadêmica</h2>
		
	<div class="descricaoOperacao">
	<b>Caro usuário, </b>
	<br/><br/>
	Ao lado de cada notificação, aparece a opção de "Listar Destinatários", basta clicar no botão para exibir todos os discentes que serão notificados. 
	<br/><br/>
	Caso a notificação seja parametrizada, será possível escolher o ano e período de referência.   
	<br/><br/>
	Para enviar a notificação basta clicar em "Notificar" e confirmar a mensagem de segurança. 
	Uma vez que a notificação seja enviada, a operação não poderá ser desfeita. 
	</div>
	
	<div class="infoAltRem">
		<img src="/sigaa/img/group.png">: Listar Destinatários
	</div>
 
		<a4j:outputPanel id="notificacoes" style="margin-top:10px;" >
				<rich:dataTable  rowClasses="linhaPar,linhaImpar" var="n" value="#{notificacaoAcademica.notificacoesSelecionadas}" style="width:100%;">								
					<rich:column>
						<f:facet name="header"><f:verbatim><p align="left">
						<h:outputText value="Descrição" />
						</p></f:verbatim></f:facet>
						
						<h:outputText value="#{ n.descricao}" />
					</rich:column>
					<rich:column style="width:16%;text-align:center">
						<f:facet name="header"><f:verbatim><p align="center">
						<h:outputText value="Ano-Período" />
						</p></f:verbatim></f:facet>
						<a4j:outputPanel rendered="#{n.anoPeriodoReferencia}">
							<h:selectOneMenu value="#{ n.anoReferencia }" valueChangeListener="#{notificacaoAcademica.carregarDiscentesAno}">
								<f:selectItems value="#{ notificacaoAcademica.anosCombo }" />
								<a4j:support event="onchange" onsubmit="true" reRender="notificacoes">
									<f:param name="id" value="#{n.id}" />
								</a4j:support>
							</h:selectOneMenu>
							<h:outputText value=" - "/>
							<h:selectOneMenu value="#{ n.periodoReferencia }" valueChangeListener="#{notificacaoAcademica.carregarDiscentesPeriodo}">
								<f:selectItems value="#{ notificacaoAcademica.periodosCombo }" />
								<a4j:support event="onchange" onsubmit="true" reRender="notificacoes">
									<f:param name="id" value="#{n.id}" />
								</a4j:support>
							</h:selectOneMenu>
						</a4j:outputPanel>
						<a4j:outputPanel rendered="#{!n.anoPeriodoReferencia}">
						<h:outputText value="-"/>
						</a4j:outputPanel>
					</rich:column>
					<rich:column  style="width:5%;text-align:center">
						<f:facet name="header"><f:verbatim><p align="center">
						<h:outputText value="Exige Confirmação" />
						</p></f:verbatim></f:facet>
						<h:outputText value="Sim" rendered="#{ n.exigeConfirmacao }" />
						<h:outputText value="Não" rendered="#{ !n.exigeConfirmacao }" />
					</rich:column>	
					<rich:column  style="width:5%;text-align:center">
						<f:facet name="header"><f:verbatim><p align="center">
						<h:outputText value="Total de Discentes" />
						</p></f:verbatim></f:facet>
						<h:outputText value="#{n.numeroDiscentes}" />
					</rich:column>	
					<rich:column style="width:2%">
						<h:commandLink title="Listar Destinatários" action="#{notificacaoAcademica.listarDestiantarios}">
							<f:param name="id" value="#{n.id}" />
							<h:graphicImage value="/img/group.png" style="overflow: visible;"/>
						</h:commandLink>
					</rich:column>	
				</rich:dataTable>	
			</a4j:outputPanel>
				
			<div align="center" style=";margin-top:5px;">
				<h:commandButton value="Notificar" action="#{notificacaoAcademica.notificar}" onclick="return(confirm('Deseja realmente notificar estes discentes?'));"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}"  action="#{notificacaoAcademica.cancelar}" />
			</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
