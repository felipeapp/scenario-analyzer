	<tr>
		<th class="required"><h:outputLabel for="descricao">Título:</h:outputLabel></th>
		<td>
			<h:inputText value="#{enqueteComunidadeMBean.object.pergunta}" maxlength="500" size="40" id="titulo"/>
		</td>
	</tr>
	
	<tr>
		<th class="required"><h:outputLabel for="descricao">Publicada:</h:outputLabel></th>
		<td>
   			<h:selectOneMenu value="#{ enqueteComunidadeMBean.object.publicada }">
   				<f:selectItems value="#{ enqueteComunidadeMBean.simNao }"/>
   			</h:selectOneMenu> (Se a enquente já estará disponível para votação)
		</td>
	</tr>
	
	<tr>
		<th class="required"><h:outputLabel for="descricao">Respostas:</h:outputLabel></th>
		<td>
		     <t:dataTable var="item" value="#{ enqueteComunidadeMBean.object.respostas }" rowIndexVar="row" id="respostas">
				<t:column>
					<h:outputText value="#{ row + 1 }."/> <span class="required">&nbsp;</span>
				</t:column>
				<t:column>
					<h:inputHidden value="#{ item.id }"/>
					<h:inputText maxlength="500" value="#{ item.resposta }" size="40"/>
				</t:column>
				<t:column>
					<a4j:commandLink action="#{ enqueteComunidadeMBean.removerItem }" value="Remover" reRender="respostas">
						<f:param name="indice" value="#{ row }"/>
					</a4j:commandLink>
				</t:column>
			</t:dataTable>
			
			<div style="margin-left: 70px">
				<a4j:commandButton action="#{ enqueteComunidadeMBean.novoItem }" value="Acrescentar Resposta" reRender="respostas"/>
			</div>
		</td>
	</tr>
	
	<tr>
		<td style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ enqueteComunidadeMBean.object.notificarMembros }" styleClass="noborder" /> </td>
		<th style="text-align: left;">Notificar por e-mail?</th>
	</tr>
	
	
	
