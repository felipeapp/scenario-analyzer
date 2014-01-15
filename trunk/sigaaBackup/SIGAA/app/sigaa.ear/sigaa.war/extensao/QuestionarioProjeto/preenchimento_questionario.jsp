<%@include file="/ensino/notificacoes_academicas/cabecalho.jsp"%>

<f:view>
	<h2> Ficha de Acompanhamento dos Projetos de Extensão </h2>

	<a4j:keepAlive beanName="questionarioProjetoExtensaoMBean" />
	<h:form id="form">
			<table class="formulario" width="100%">
				<caption> Ficha de Acompanhamento do Projeto </caption>
					<tr>
						<th width="10%;"><b>Projeto:</b></th>
						<td>${ questionarioProjetoExtensaoMBean.obj.projeto.titulo }</td>
					</tr>
					<tr>
						<th width="10%;"><b>Questionário:</b></th>
						<td>${ questionarioProjetoExtensaoMBean.obj.questionario.titulo }</td>
					</tr>
			</table>
			
			<%@include file="/geral/questionario/_formulario_respostas.jsp" %>

		<table class="formulario" width="100%" border="1">
			<tfoot>
				<tr>
					<td>
						<h:commandButton action="#{ questionarioProjetoExtensaoMBean.cadastrarRespostas }" value="Cadastrar" />  
						<h:commandButton action="#{ questionarioProjetoExtensaoMBean.listarAcoesPendentesQuestionario }" value="Cancelar" onclick="#{ confirm }"/> 
					</td>
				</tr>
			</tfoot>
		</table>					
	</h:form>
</f:view>

<%@include file="/ensino/notificacoes_academicas/rodape.jsp"%>