<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
  
<h2><ufrn:subSistema /> > Agenda de Compromissos </h2>

<style>
	.teste td{
		border: 1px solid #DDD;
	}
	
	div.schedule-detailed-evolution .entry {
		border-width: 0px;
	}
</style>

<f:view>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js"> </script>
	<br />
	<div class="infoAltRem">
	   	<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Cadastrar Reserva
	   	<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Reserva
	   	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	   	<br/>
	</div>
	<br />

	<h:form id="form">
		<rich:panel id="calendario">
			<t:panelGrid columns="3" cellspacing="3" width="100%">
				<rich:calendar popup="false" datePattern="dd/MM/yyyy" id="calendarioInicio" 
					currentDate="#{agenda.model.selectedDate}" 
					showFooter="false"
					value="#{agenda.model.selectedDate}">
					 <a4j:support event="onchanged" reRender="cal"></a4j:support> 
				</rich:calendar>

			</t:panelGrid>
		</rich:panel>
		
		<center>
			<div style="height: 30px">
				<a4j:status>
					<f:facet name="start">
						<h:graphicImage value="/img/ajax-loader.gif"/>
					</f:facet>
				</a4j:status>
			</div>
		</center>		
		
		<table align="center">
				<tr>
					<td>
						<h:commandLink action="#{compromisso.iniciarCadastroCompromisso}">
							<h:graphicImage url="/img/adicionar.gif" />
						</h:commandLink>
					
						<h:commandLink action="#{compromisso.iniciarAlteracaoCompromisso}">
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
					
						<h:commandLink action="#{compromisso.removerCompromisso}">
							<h:graphicImage url="/img/delete.gif" />
						</h:commandLink>	
					</td>
				</tr>
		</table>
		
		<rich:panel id="cal">			
			<t:schedule value="#{agenda.model}" id="calendarioExpandido"
				rendered="true" visibleEndHour="23" visibleStartHour="7" backgroundClass="background teste"
				workingEndHour="23" workingStartHour="7" readonly="false" tooltip="true"
				theme="evolution" />
		</rich:panel>
	</h:form>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>