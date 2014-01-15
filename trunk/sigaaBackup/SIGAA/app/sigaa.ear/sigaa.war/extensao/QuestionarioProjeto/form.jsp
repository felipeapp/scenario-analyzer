<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Associar Question�rio a um Grupo de Usu�rios </h2>

	<a4j:keepAlive beanName="questionarioProjetoExtensaoMBean" />
	<h:form id="form">
		
		<div id="ajuda" class="descricaoOperacao">
			<p>Caro Usu�rio, </p>
				<p>
					� poss�vel associar um question�rio para um grupo espec�fico de destinat�rios, a filtragem pode ser 
					realizada pelo edital ou pelo ano do projeto, se ambos forem informados ser�o destinados os question�rios para os projetos
					que atenderem a uma das restri��es informadas.
				</p>
				<p>
					Caso a associa��o seja marcada como <b>Sim</b> o usu�rio do sistema s� ter� acesso ao sistema depois de responder o question�rio destinado para o mesmo.
				</p>
		</div>
			
		<table class="formulario" width="60%">
			<caption class="listagem">Associar Question�rio</caption>	

			<tr>
				<th class="obrigatorio" width="30%">Question�rio:</th>
				<td>
					<h:selectOneMenu value="#{questionarioProjetoExtensaoMBean.obj.questionario.id}" id="idQuetionario">
						<f:selectItem  itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{questionarioProjetoExtensaoMBean.allQuestionarioExtensao}" />																				
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Grupos de Usu�rios:</th>
				<td>
					<h:selectOneMenu value="#{questionarioProjetoExtensaoMBean.obj.tipoGrupo}" id="idGrupo">
						<f:selectItem  itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{questionarioProjetoExtensaoMBean.allGrupoQuestionarioExtensao}" />																				
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th>Tipo A��o:</th>
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
				<th>Obrigat�rio:</th>
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
			<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</center>
		<br />
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>