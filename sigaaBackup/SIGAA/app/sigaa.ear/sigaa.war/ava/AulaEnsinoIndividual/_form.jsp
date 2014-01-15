
<table class="formAva">

	<c:if test="${ not empty turmaVirtual.turma.subturmas }">
	<tr>
		<th id=turmaTh" class="required">Turma</th>
		<td>
			<h:selectOneMenu value="#{ aulaEnsinoIndividual.turma.id }" id="turma">
				<f:selectItems value="#{turmaVirtual.subTurmasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	</c:if>
	
	<tr>
		<th id=dataAulaTh" class="required">Data:</th>
		<td>	
			<t:inputCalendar id="data" value="#{aulaEnsinoIndividual.object.dataAula}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>
		</td>
	</tr>
	
	<h:inputHidden value="#{ aulaEnsinoIndividual.object.tipo }" id="tipo"/>
	
	<tr>
		<th id=numeroAulasTh" class="required">Número de Aulas:</th>
		<td>
			<h:inputText value="#{aulaEnsinoIndividual.object.numeroAulas}" id="numeroAulas" size="5" maxlength="2" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			<span class="texto-ajuda">Número de aulas de 50 minutos (importante para o lançamento da freqüência)</span>
		</td>
	</tr>
	
	<tr>
		<th id=descricaoTh" class="required">Descrição:</th>
		<td>
			<h:inputText value="#{aulaEnsinoIndividual.object.descricao}" id="descricao" size="58" maxlength="200"/>
		</td>
	</tr>
	
	<tr>
		<th id=observacoesTh">Observações: </th>
		<td>
			<h:inputTextarea value="#{aulaEnsinoIndividual.object.observacoes}" id="observacoes" rows="5" cols="55"/>
		</td>
	</tr>
	
	<tr>
		<th id=notificarTh" style="width:140px;">Notificar: </th>
		<td>
			<h:selectBooleanCheckbox value="#{aulaEnsinoIndividual.notificar}" id="notificar"/>
			<ufrn:help><p>Notificar os alunos por e-mail.</p></ufrn:help>	
		</td>
	</tr>
</table>

