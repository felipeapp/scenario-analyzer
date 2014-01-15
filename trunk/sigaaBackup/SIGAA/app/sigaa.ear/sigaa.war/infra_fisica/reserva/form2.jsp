<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js"> </script>

<style>
 .rich-spinner-input {
 	width:18px;
 }
 
</style>

	<h:panelGroup id="ajaxErros">
		<h:dataTable  value="#{reservaEspacoFisico.avisosAjax}" var="msg" rendered="#{not empty reservaEspacoFisico.avisosAjax}" width="100%">
			<t:column>
				<h:outputText value="<div id='painel-erros' style='position: relative; padding-bottom: 10px;'><ul class='erros'><li>#{msg.mensagem}</li></ul></div>" escape="false"/>
			</t:column>
		</h:dataTable>
	</h:panelGroup>

<h2><ufrn:subSistema /> > Reserva Espaço Físico</h2>


	<h:form id="form">
		<h:panelGrid width="100%" styleClass="formulario">
			<h:panelGrid width="100%" styleClass="subFormulario">
				<f:facet name="caption">
					<h:outputText value="Reserva"/>
				</f:facet>
									
				<rich:panel>
					<f:facet name="header">
						<h:outputText value="Descrição da Reserva"/>
					</f:facet>			
					<h:inputTextarea style="width: 100%" value="#{reservaEspacoFisico.obj.descricao}" />
				</rich:panel>
			</h:panelGrid>
		</h:panelGrid>
		
		<br/>
		
		<h:panelGrid width="100%" styleClass="formulario">
			<h:panelGrid styleClass="subFormulario" width="100%" columns="2">
				<f:facet name="caption">
					<h:outputText value="Definição de datas"/>
				</f:facet>
				
				<rich:panel>
		
					<f:facet name="header">
						<h:outputText value="Inicio"/>
					</f:facet>
		
					<h:panelGroup>
						<h:panelGrid columns="4">
							<rich:calendar datePattern="dd/MM/yyyy" inputSize="10" id="calendarioInicio" value="#{reservaEspacoFisico.reservaHorario.inicio}"  />
							<rich:inputNumberSpinner value="#{reservaEspacoFisico.reservaHorario.inicio.hours}"  maxValue="23" minValue="00" />
							<h:outputText value="<strong>:</strong>" escape="false" />
							<rich:inputNumberSpinner value="#{reservaEspacoFisico.reservaHorario.inicio.minutes}"  maxValue="59" minValue="00"/>
						</h:panelGrid>
						<%-- 
						<rich:inplaceInput value="#{reservaEspacoFisico.reservaHorario.inicio.hours}" defaultLabel="Digite a Hora" id="horaInicio" /> <h:outputText value=" : " /> <rich:inplaceInput value="#{reservaEspacoFisico.reservaHorario.inicio.minutes}" defaultLabel="Digite os Minutos" id="minutoInicio" />
						--%>
					</h:panelGroup>
				</rich:panel>
				
				<rich:panel>
					<f:facet name="header">
						<h:outputText value="Fim"/>
					</f:facet>			
				
					<h:panelGroup>
						<h:panelGrid columns="4">
							<rich:calendar datePattern="dd/MM/yyyy" inputSize="10" id="calendarioFim" value="#{reservaEspacoFisico.reservaHorario.fim}" />
							<rich:inputNumberSpinner value="#{reservaEspacoFisico.reservaHorario.fim.hours}" maxValue="23" minValue="00" id="horaFim" />
							<h:outputText value="<strong>:</strong>" escape="false" />
							<rich:inputNumberSpinner id="minutoFim" value="#{reservaEspacoFisico.reservaHorario.fim.minutes}" maxValue="59" minValue="00" />
						</h:panelGrid>
					</h:panelGroup>
				</rich:panel>
	
			</h:panelGrid>
		</h:panelGrid>
		
		<br />
		
		<h:panelGrid width="100%" styleClass="formulario">
			<h:panelGrid width="100%" styleClass="subFormulario">
				<f:facet name="caption">
					<h:outputText value="Descrição sobre o evento"/>
				</f:facet>
									
				<rich:panel>
					<f:facet name="header">
						<h:outputText value="Titulo"/>
					</f:facet>			
					<h:inputText style="width: 100%" value="#{reservaEspacoFisico.reservaHorario.titulo}" />
				</rich:panel>
				
				<rich:panel>	
					<f:facet name="header">
						<h:outputText value="Comentario"/>
					</f:facet>			
	
					<h:inputTextarea style="width: 100%" value="#{reservaEspacoFisico.reservaHorario.descricao}" />
				</rich:panel>												
				<f:facet name="footer">
					<h:panelGroup>
						<a4j:commandButton value="Adicionar Horario" actionListener="#{reservaEspacoFisico.adicionarHorario}" id="novoHorario" reRender="painel, ajaxErros" />
						<h:commandButton value="#{reservaEspacoFisico.confirmButton}" action="#{reservaEspacoFisico.finalizar}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{reservaEspacoFisico.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
					</h:panelGroup>
				</f:facet>						
			</h:panelGrid>
		</h:panelGrid>
		
		<br />
		<div class="infoAltRem">
		   	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Excluir Horario
		   	<br/>
		</div>
		
		<center>
			<div style="height: 30px">
				<a4j:status>
					<f:facet name="start">
						<h:graphicImage value="/img/ajax-loader.gif"/>
					</f:facet>
				</a4j:status>
			</div>
		</center>				
		
		<rich:panel id="painel">
			<rich:dataTable id="tabelaHorarios" value="#{reservaEspacoFisico.modelHorarios}" var="h" 
							width="100%" rendered="#{reservaEspacoFisico.obj.horariosCadastrados}" rowKeyVar="row">
							
				<f:facet name="header">
				
					<rich:columnGroup>
					
						<rich:column id="headerTitulo">
							<h:outputText value="Titulo" />
						</rich:column>				
					
						<rich:column id="headerInicio">
							<h:outputText value="Inicio" />
						</rich:column>
			
						<rich:column id="headerTermino">
							<h:outputText value="Termino" />
						</rich:column>
						
						<rich:column id="headerOperacao">
						</rich:column>						
					</rich:columnGroup>
				</f:facet>
			
				<rich:column id="colunaTitulo" rendered="#{h.ativo}">
					<h:panelGroup>
						<rich:panel>
							<h:outputText value="#{h.titulo}" />
						</rich:panel>
					</h:panelGroup>
				</rich:column> 		
			
				<rich:column id="colunaInicio" rendered="#{h.ativo}">
					<h:panelGroup>
						<rich:panel>
							<h:outputText value="#{h.inicio}">
								<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
							</h:outputText>
						</rich:panel>
					</h:panelGroup>
				</rich:column> 		
	
				<rich:column id="colunaFim" rendered="#{h.ativo}">
					<h:panelGroup>
						<rich:panel>
							<h:outputText value="#{h.fim}">
								<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
							</h:outputText>
						</rich:panel>
					</h:panelGroup>
				</rich:column> 		
			
				<rich:column id="colunaOperacao"  rendered="#{h.ativo}">
					<h:panelGroup>
						<rich:panel>
							<a4j:commandButton id="remover" actionListener="#{reservaEspacoFisico.removerHorario}" image="/img/delete.gif" reRender="painel" />
						</rich:panel>
					</h:panelGroup>
				</rich:column> 		
							
			</rich:dataTable>
		</rich:panel>
			
	</h:form>		

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>