
<ul class="form">
	<li>
		<label class="required" for="titulo">T�tulo: <span class="required">&nbsp;</span></label>
		<h:inputText id="titulo" size="59" value="#{forum.object.titulo}"/>
	</li>
	<li>
		<label class="required" for="descricao">Descri��o: <span class="required">&nbsp;</span></label>
		<t:inputTextarea value="#{forum.object.descricao}" />
	</li>
	<%--
	<li>
		<label class="required" for="tipo">Tipo <span class="required">&nbsp;</span></label>
		<t:selectOneMenu id="tipo" value="#{forum.object.topicos}" styleClass="noborder">
			<f:selectItem itemLabel="F�rum de Mensagens" itemValue="false"/>
			<f:selectItem itemLabel="F�rum de T�picos" itemValue="true"/>
		</t:selectOneMenu>
		<span class="descricao-campo">(F�rum de T�picos � um tipo de f�rum em que as mensagens podem ser agrupadas por assunto, atrav�s da cria��o de t�picos de discuss�o.)</span>
		<span class="descricao-campo">(F�rum de Mensagens � um tipo de f�rum em que todas as mensagens s�o vistas em uma mesma p�gina.)</span>
	</li>
	<li>
		<label class="required" for="tipo">Turma, mural ou pesquisa? <span class="required"></span></label>
		<t:selectOneMenu id="modeloforum" value="#{forum.object.tipo}" styleClass="noborder">
			<f:selectItem itemLabel="Turma" itemValue="1" />
			<f:selectItem itemLabel="Mural" itemValue="2" />
			<f:selectItem itemLabel="Pesquisa" itemValue="3" />
		</t:selectOneMenu>
	</li> --%>
	</li>
		<label for="email">Notificar por e-mail?</label>
		<t:selectOneMenu id="email" value="#{forum.notificar}">
			<f:selectItem itemLabel="N�o" itemValue="false"/>
			<f:selectItem itemLabel="Sim" itemValue="true"/>
		</t:selectOneMenu>
		<span class="descricao-campo">(Selecione sim se desejar enviar um e-mail para todos os participantes da turma avisando sobre a cria��o do F�rum.)</span>
	</li>
	<li>
		<label for="ativo">F�rum Ativo?</label>
		<t:selectOneMenu id="ativo" value="#{forum.object.ativo}">
			<f:selectItem itemLabel="Sim" itemValue="true"/>
			<f:selectItem itemLabel="N�o" itemValue="false"/>
		</t:selectOneMenu>
		<span class="descricao-campo">(Se o F�rum estiver marcado como inativo, n�o ser� permitido postar mensagens.)</span>
	</li>
</ul>