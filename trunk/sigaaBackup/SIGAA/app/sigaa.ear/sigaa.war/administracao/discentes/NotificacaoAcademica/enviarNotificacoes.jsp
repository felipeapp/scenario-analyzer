<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h:form>
	<h2 class="title"><ufrn:subSistema /> > Notifica��o Acad�mica</h2>
		
	<div class="descricaoOperacao">
	<b>Caro usu�rio, </b>
	<br/><br/>
	Ao lado de cada notifica��o, aparece a op��o de "Listar Destinat�rios", basta clicar no bot�o para exibir todos os discentes que ser�o notificados. 
	<br/><br/>
	Caso a notifica��o seja parametrizada, ser� poss�vel escolher o ano e per�odo de refer�ncia.   
	<br/><br/>
	Para enviar a notifica��o basta clicar em "Notificar" e confirmar a mensagem de seguran�a. 
	Uma vez que a notifica��o seja enviada, a opera��o n�o poder� ser desfeita. 
	</div>
	
	<div class="infoAltRem">
		<img src="/sigaa/img/group.png">: Listar Destinat�rios
	</div>
 
		<a4j:outputPanel id="notificacoes" style="margin-top:10px;" >
				<rich:dataTable  rowClasses="linhaPar,linhaImpar" var="n" value="#{notificacaoAcademica.notificacoesSelecionadas}" style="width:100%;">								
					<rich:column>
						<f:facet name="header"><f:verbatim><p align="left">
						<h:outputText value="Descri��o" />
						</p></f:verbatim></f:facet>
						
						<h:outputText value="#{ n.descricao}" />
					</rich:column>
					<rich:column style="width:16%;text-align:center">
						<f:facet name="header"><f:verbatim><p align="center">
						<h:outputText value="Ano-Per�odo" />
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
						<h:outputText value="Exige Confirma��o" />
						</p></f:verbatim></f:facet>
						<h:outputText value="Sim" rendered="#{ n.exigeConfirmacao }" />
						<h:outputText value="N�o" rendered="#{ !n.exigeConfirmacao }" />
					</rich:column>	
					<rich:column  style="width:5%;text-align:center">
						<f:facet name="header"><f:verbatim><p align="center">
						<h:outputText value="Total de Discentes" />
						</p></f:verbatim></f:facet>
						<h:outputText value="#{n.numeroDiscentes}" />
					</rich:column>	
					<rich:column style="width:2%">
						<h:commandLink title="Listar Destinat�rios" action="#{notificacaoAcademica.listarDestiantarios}">
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
