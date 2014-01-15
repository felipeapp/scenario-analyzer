
<table class="formAva">

	<tr>
		<th id="descricaoTh" class="required"> Descrição:</th>
		<td><h:inputText value="#{dataAvaliacao.object.descricao}" id="descricao" size="59" maxlength="200"/></td>
	</tr>
	
	<tr>
		<th id="dataTh" class="required">Data:</th>
		<td>
			<t:inputCalendar title=" Data" id="Data" value="#{dataAvaliacao.object.data}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>
		</td>
	</tr>
	
	<tr>
		<th id="horaTh" class="required">Hora:</th>
		<td><h:inputText value="#{dataAvaliacao.object.hora}" id="hora" size="15" maxlength="30"/></td>
	</tr>

</table>