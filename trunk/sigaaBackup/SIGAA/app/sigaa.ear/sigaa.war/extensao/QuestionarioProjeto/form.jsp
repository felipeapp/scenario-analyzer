<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Associar Questionário a um Grupo de Usuários </h2>

	<a4j:keepAlive beanName="questionarioProjetoExtensaoMBean" />
	<h:form id="form">
		
		<div id="ajuda" class="descricaoOperacao">
			<p>Caro Usuário, </p>
				<p>
					É possível associar um questionário para um grupo específico de destinatários, a filtragem pode ser 
					realizada pelo edital ou pelo ano do projeto, se ambos forem informados serão destinados os questionários para os projetos
					que atenderem a uma das restrições informadas.
				</p>
				<p>
					Caso a associação seja marcada como <b>Sim</b> o usuário do sistema só terá acesso ao sistema depois de responder o questionário destinado para o mesmo.
				</p>
		</div>
			
		<table class="formulario" width="60%">
			<caption class="listagem">Associar Questionário</caption>	

			<tr>
				<th class="obrigatorio" width="30%">Questionário:</th>
				<td>
					<h:selectOneMenu value="#{questionarioProjetoExtensaoMBean.obj.questionario.id}" id="idQuetionario">
						<f:selectItem  itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{questionarioProjetoExtensaoMBean.allQuestionarioExtensao}" />																				
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Grupos de Usuários:</th>
				<td>
					<h:selectOneMenu value="#{questionarioProjetoExtensaoMBean.obj.tipoGrupo}" id="idGrupo">
						<f:selectItem  itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{questionarioProjetoExtensaoMBean.allGrupoQuestionarioExtensao}" />																				
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th>Tipo Ação:</th>
				<td>
					<h:selectOneMenu value="#{questionarioProjetoExtensaoMBean.obj.tipoAtividade.id}" id="idTipoAcao">
						<f:selectItem  itemValue="-1" itemLabel="-- TODOS --" />
						<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />																				
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th>Edital:</th>
				<td>
					<h:selectOneMenu value="#{questionarioProjetoExtensaoMBean.obj.edital}" id="idEdital">
						<f:selectItem  itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{questionarioProjetoExtensaoMBean.allEditaisExtensao}" />																				
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th>Ano do Projeto:</th>
				<td>
					<h:inputText value="#{ questionarioProjetoExtensaoMBean.obj.ano }" maxlength="4" size="3" onkeyup="return formatarInteiro(this);"
					id="anoProjeto" label="Ano do Projeto"/>
				</td>
			</tr>

			<tr>
				<th>Obrigatório:</th>
				<td>
					<h:selectOneRadio id="obrigatoriedade" value="#{ questionarioProjetoExtensaoMBean.obj.obrigatoriedade }">
						<f:selectItems value="#{questionarioProjetoExtensaoMBean.simNao}"/>
					</h:selectOneRadio>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Associar" action="#{questionarioProjetoExtensaoMBean.salvar}" id="btSalvar"/>
						<h:commandButton value="Cancelar" action="#{questionarioProjetoExtensaoMBean.cancelar}" id="btCancelar" /> 
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
		<br />
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>