<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<table class="formAva" style="width:90%;">
	<tr>
    	<th class="required">T�tulo:</th>
    	<td>
    		<h:inputText value="#{enquete.object.pergunta}"  maxlength="500" size="40"  id="titulo"/>
    	</td>
	</tr>
	<tr>
   		<th class="required">Publicada:</th>
   		<td>
	   		<h:selectOneMenu value="#{ enquete.object.publicada }">
	   			<f:selectItems value="#{ enquete.simNao }"/>
	   		</h:selectOneMenu>
	   		<span class="texto-ajuda">(Se a enquete j� estar� dispon�vel para vota��o)</span>
   		</td>
	</tr>
	<tr>
		<th class="required">Prazo de Vota��o:</th>
		<td>
			<t:inputCalendar id="Prazo" value="#{enquete.object.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return (formataData(this,event));"  maxlength="10" title="Prazo">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>
			�s		<h:inputText id="horaPrazo" value="#{enquete.horaPrazo}" onkeyup="return formatarInteiro(this);" maxlength="2" style="width:20px;" /> :
			<h:inputText id="minutoPrazo" value="#{enquete.minutoPrazo}" onkeyup="return formatarInteiro(this);" maxlength="2" style="width:20px;" />
		</td>	
	</tr>
	
	<tr>
		<th class="required">T�pico de Aula:</th>
		<td>
			<h:selectOneMenu id="aula" value="#{ enquete.idTopicoAula }" rendered="#{ not empty topicoAula.comboIdentado }">
				<f:selectItem itemValue="0" itemLabel=" -- Selecione um t�pico de aula -- "/>
				<f:selectItems value="#{topicoAula.comboIdentado}" />
			</h:selectOneMenu>
			<h:selectOneMenu value="#{ enquete.idTopicoAula }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
				<f:selectItem itemLabel="Nenhum T�pico de Aula foi cadastrado" itemValue="0"/>
			</h:selectOneMenu>
			<ufrn:help>Selecione um t�pico de aula para exibir este f�rum na p�gina inicial da turma virtual.</ufrn:help>
		</td>
	</tr>
	
	<tr>
		<th class="required">Respostas:</th>
		<td>
			<span class="texto-ajuda">(Devem existir ao menos duas respostas.)</span>
			<br/>
			<br/>
			<div>
				<t:dataTable var="item" value="#{ enquete.object.respostas }" rowIndexVar="row" id="respostas" >
					<t:column>
						<h:outputText value="#{ row + 1 }."/>
					</t:column>
					<t:column>
						<h:inputHidden value="#{ item.id }"/>
						<h:inputText id="itemResposta" maxlength="40" value="#{ item.resposta }" size="40"/>
					</t:column>
					<t:column>
						<a4j:commandLink action="#{ enquete.removerItem }" value="Remover" reRender="respostas" rendered="#{ row > 1 }">
							<f:param name="indice" value="#{ row }"/>
						</a4j:commandLink>
					</t:column>
				</t:dataTable>
				<br/>
				<a4j:commandButton action="#{ enquete.novoItem }" value="Acrescentar Resposta" reRender="respostas" style="margin-left:15px;" />
			</div>
		</td>	
	</tr>
    
	<tr>
		<th>Notifica��o: </th>
		<td>
			<h:selectBooleanCheckbox id="notificacao" value="#{ enquete.object.notificarAlunos }" />
			<span class="texto-ajuda">(Notificar os alunos por e-mail)</span> 
		</td>
	</tr>
</table>   
