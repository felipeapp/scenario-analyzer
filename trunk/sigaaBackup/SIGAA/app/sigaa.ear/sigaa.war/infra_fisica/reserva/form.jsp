<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js"> </script>

<style>
 .rich-spinner-input {
 	width:18px;
 
 }
 .formulario .rich-spinner-c td {
 	padding: 0px;
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

	<h:form id="form2">
		<table width="100%" class="formulario">
			<caption>Informação sobre a Reserva</caption>
			<tbody>
				<tr>
					<th>Descrição:</th>
					<td><h:inputTextarea style="width: 90%" value="#{reservaEspacoFisico.obj.descricao}" /></td>
				</tr>
			</tbody>
		</table>
		
		<br />
		
		<table width="100%" class="formulario">
			<caption>Informação sobre o Horario</caption>
			
			<tbody>
				<tr>
					<th>Titulo:</th>
					<td colspan="4"><h:inputText value="#{reservaEspacoFisico.reservaHorario.titulo}" style="width: 90%" /></td>
				</tr>
				<tr>
					<th>Descrição:</th>
					<td colspan="4"><h:inputTextarea value="#{reservaEspacoFisico.reservaHorario.descricao}" style="width: 89%" /></td>
				</tr>
				<tr>
					<th>Inicio:</th>
					<td width="15%">
						<rich:calendar datePattern="dd/MM/yyyy" inputSize="10" id="calendarioInicio" value="#{reservaEspacoFisico.reservaHorario.inicio}"  />
					</td>
					<td align="right" width="10%">
						<rich:inputNumberSpinner value="#{reservaEspacoFisico.reservaHorario.inicio.hours}"  maxValue="23" minValue="00" />
					</td>
					<td align="center" width="2%">
						:
					</td>
					<td align="left">
						<rich:inputNumberSpinner value="#{reservaEspacoFisico.reservaHorario.inicio.minutes}"  maxValue="59" minValue="00" />
					</td>
				</tr>
				<tr>
					<th>Fim:</th>
					<td width="15%">
						<rich:calendar datePattern="dd/MM/yyyy" inputSize="10" id="calendarioFim" value="#{reservaEspacoFisico.reservaHorario.fim}" />
					</td>
					<td align="right" width="10%">
						<rich:inputNumberSpinner value="#{reservaEspacoFisico.reservaHorario.fim.hours}" maxValue="23" minValue="00" id="horaFim" />
					</td>
					<td align="center" width="2%">
						:
					</td>
					<td align="left">
						<rich:inputNumberSpinner id="minutoFim" value="#{reservaEspacoFisico.reservaHorario.fim.minutes}" maxValue="59" minValue="00" />
					</td>
				</tr>				
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5">
						<a4j:commandButton value="Adicionar Horario" actionListener="#{reservaEspacoFisico.adicionarHorario}" id="novoHorario" reRender="painel, ajaxErros" />
						<h:commandButton value="#{reservaEspacoFisico.confirmButton}" action="#{reservaEspacoFisico.finalizar}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{reservaEspacoFisico.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />					
					</td>
				</tr>
			</tfoot>			
		</table>
	
			
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