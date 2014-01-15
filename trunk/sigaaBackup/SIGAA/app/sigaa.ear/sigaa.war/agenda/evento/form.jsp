<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="eventoBean" />
	
	<h2>Agenda > Cadastro de Eventos</h2>
	<h:form>
		<table class="formulario">
			<caption> Informe os dados do evento </caption>
			
			<tr>
				<td> </td>
				<td> Agenda: </td>
				<td> <h:outputText value="#{eventoBean.obj.agenda.nome}"/> </td>
			</tr>			
			
			<tr>
				<td> </td>
				<th class="required" > <h:outputLabel for="titulo" value="Título:"/> </th>
				<td> <h:inputText value="#{eventoBean.obj.titulo}" size="80" id="titulo"/> </td>
			</tr>

			<tr>
				<td> </td>
				<th > <h:outputLabel for="local" value="Local:"/> </th>
				<td> <h:inputText value="#{eventoBean.obj.local}" size="80" id="local"/> </td>
			</tr>

			<tr>
				<td> </td>
				<th class="required"> <h:outputLabel for="dataEvento" value="Data:" /> </th>
				<td> 
					<t:inputCalendar value="#{eventoBean.obj.dataInicio}" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"
						id="inicio" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"/>
				</td>
			</tr>			

			<tr>
				<td colspan="2"> </td>
				<td> 
					<h:selectBooleanCheckbox value="#{eventoBean.obj.diaTodo}" id="diaTodo">
						<a4j:support event="onchange" reRender="horarioInicio, horarioFim" />
					</h:selectBooleanCheckbox>
					<h:outputLabel for="diaTodo" value="Este evento não tem um horário específico de duração, ocorrendo o dia todo. "/> 
				</td>
			</tr>

			<tr> 
				<td> </td>
				<th> <h:outputLabel for="dataEvento" value="Horário:"/>	</th>
				<td>
					<h:inputText value="#{eventoBean.horarioInicio}" size="5" onkeypress="return(formataHora(this, event))" id="horarioInicio" readonly="#{eventoBean.obj.diaTodo}">
						<f:convertDateTime pattern="HH:mm" />
					</h:inputText>
					
					às 
					
					<h:inputText value="#{eventoBean.horarioFim}" size="5" onkeypress="return(formataHora(this, event))" id="horarioFim" readonly="#{eventoBean.obj.diaTodo}">
						<f:convertDateTime pattern="HH:mm" />
					</h:inputText>
				</td>
			</tr>

			<tr class="subFormulario">
				<td class="subFormulario">
					<h:selectBooleanCheckbox value="#{eventoBean.recorrente}" id="recorrente"/>
				</td>
				<td class="subFormulario" colspan="2"> <h:outputLabel for="recorrente" value="Desejo cadastrar uma regra de recorrência para este evento. Ex.: Todos os dias, duas vezes por semana, anualmente etc."/></td>
			</tr>
			
			<tr>
				<td> </td>
				<th> <h:outputLabel for="frequencia" value="Frequência:"/> </th>
				<td> 
					<h:selectOneMenu value="#{ eventoBean.obj.recorrencia.frequencia }" id="frequencia">
						<f:selectItems value="#{ eventoBean.tiposFrequencia }"/>
					</h:selectOneMenu> 
				</td>
			</tr>
			
			<tr>
				<td> </td>
				<th> <h:outputLabel for="repetirAte" value="Ocorrerá até:"/> </th>
				<td> 
					<t:inputCalendar value="#{eventoBean.repetirAte}" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"
						id="repetirAte" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"/>
				</td>
			</tr>
									
			<tr>
				<td> </td>
				<th> <h:outputLabel for="intervalo" value="Intervalo entre eventos:"/> </th>
				<td> <h:inputText value="#{eventoBean.obj.recorrencia.RRule.interval}" size="3" id="intervalo"/> </td>
			</tr>	
			
			<tfoot>
				<tr>
					<td colspan="3"> 
						<h:commandButton value="Cadastrar" action="#{eventoBean.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{eventoBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>