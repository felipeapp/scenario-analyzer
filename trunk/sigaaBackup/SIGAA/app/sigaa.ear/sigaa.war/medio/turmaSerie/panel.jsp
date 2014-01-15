<%-- Cadastro de Turma --%>
<rich:modalPanel id="panel" autosized="true" minWidth="700">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="CADASTRAR TURMA"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
		<h:panelGroup>
			<h:graphicImage value="/img/close.png" styleClass="hidelink"
				id="hidelink" />
			<rich:componentControl for="panel" attachTo="hidelink" operation="hide" event="onclick" />
		</h:panelGroup>
	</f:facet>
	<h:form>
	
	<table class="formulario" style="width: 90%">
		<caption>Dados da Turma</caption>
		
		<tbody>
			<tr>
				<th class="obrigatorio" width="40%">Ano:</th>
				<td align="left"><h:inputText value="#{turmaSerie.obj.ano}" size="10" maxlength="4" id="anoTurma"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td align="left">
				<a4j:region>
					<h:selectOneMenu value="#{turmaSerie.obj.serie.cursoMedio.id}" id="selectCursoTurma"
						valueChangeListener="#{turmaSerie.carregarSeriesByCurso }" style="width: 95%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerieTurma" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Série:</th>
				<td align="left">
					<h:selectOneMenu value="#{ turmaSerie.obj.serie.id }" style="width: 95%;" id="selectSerieTurma">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ turmaSerie.seriesByCurso }" />
					</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Nome:</th>
				<td align="left"><h:inputText value="#{turmaSerie.obj.nome}" size="10" maxlength="4" id="nomeTurma"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Turno:</th>
				<td align="left">
					<h:selectOneMenu value="#{ turmaSerie.obj.turno.id }" style="width: 95%;" id="selectTurno">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ turno.allCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="50%">Capacidade:</th>
				<td align="left"><h:inputText value="#{turmaSerie.obj.capacidadeAluno}" size="10" maxlength="4" id="capacidadeTurma"/></td>
			</tr>
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText id="local" value="#{ turmaSerie.obj.local }" maxlength="50" size="50"/>
				</td>
			</tr>
			<tr>
				<th class="required">Início:</th>
				<td align="left">
					<t:inputCalendar value="#{turmaSerie.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
					popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Fim:<br/>
				</th>
				<td align="left">
					<t:inputCalendar value="#{turmaSerie.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
					popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Cadastrar Turmas" action="#{turmaSerie.cadastrar}" id="cadastrarTurma" />
					<h:commandButton value="Cancelar" action="#{turmaSerie.cancelar}" onclick="#{confirm}" id="cancelarTurma" />
				</td>
			</tr>
		</tfoot>
	</table>		
	</h:form>
	
</rich:modalPanel>

