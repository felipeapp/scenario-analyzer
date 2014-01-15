
<ul class="form">
	<li>
		<label class="required" for="titulo">Título: <span class="required">&nbsp;</span></label>
		<h:inputText id="titulo" size="59" value="#{forum.object.titulo}"/>
	</li>
	<li>
		<label class="required" for="descricao">Descrição: <span class="required">&nbsp;</span></label>
		<t:inputTextarea value="#{forum.object.descricao}" />
	</li>
	<%--
	<li>
		<label class="required" for="tipo">Tipo <span class="required">&nbsp;</span></label>
		<t:selectOneMenu id="tipo" value="#{forum.object.topicos}" styleClass="noborder">
			<f:selectItem itemLabel="Fórum de Mensagens" itemValue="false"/>
			<f:selectItem itemLabel="Fórum de Tópicos" itemValue="true"/>
		</t:selectOneMenu>
		<span class="descricao-campo">(Fórum de Tópicos é um tipo de fórum em que as mensagens podem ser agrupadas por assunto, através da criação de tópicos de discussão.)</span>
		<span class="descricao-campo">(Fórum de Mensagens é um tipo de fórum em que todas as mensagens são vistas em uma mesma página.)</span>
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
			<f:selectItem itemLabel="Não" itemValue="false"/>
			<f:selectItem itemLabel="Sim" itemValue="true"/>
		</t:selectOneMenu>
		<span class="descricao-campo">(Selecione sim se desejar enviar um e-mail para todos os participantes da turma avisando sobre a criação do Fórum.)</span>
	</li>
	<li>
		<label for="ativo">Fórum Ativo?</label>
		<t:selectOneMenu id="ativo" value="#{forum.object.ativo}">
			<f:selectItem itemLabel="Sim" itemValue="true"/>
			<f:selectItem itemLabel="Não" itemValue="false"/>
		</t:selectOneMenu>
		<span class="descricao-campo">(Se o Fórum estiver marcado como inativo, não será permitido postar mensagens.)</span>
	</li>
</ul>