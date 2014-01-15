<%@include file="/ava/cabecalho.jsp" %>

<style>
	em, em *{ font-style: italic !important; }
	strong,strong *{ font-weight: bold !important; }
</style>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<h:form>
	
	<fieldset>
		<legend>Tópico de Aula</legend>
		
		<table class="formAva">
			<tr>
				<th style="width:120px;">Data Inicial:</th>
				<td>
					<h:outputText value="#{topicoAula.object.data}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText>
				</td>
			</tr>
			<tr>
				<th>Data Final:</th>
				<td>
					<h:outputText value="#{topicoAula.object.fim}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText>
				</td>
			</tr>
			<tr>
				<th>Tópico Pai:</th>
				<td>
					<h:outputText value="#{topicoAula.object.topicoPai.descricao}" rendered="#{ not empty topicoAula.object.topicoPai }" />
					<h:outputText value="--" rendered="#{ empty topicoAula.object.topicoPai }" />
				</td>
			</tr>
			<tr>
				<th>Descrição:</th>
				<td><h:outputText value="#{topicoAula.object.descricao}" /></td>
			</tr>
			<tr>
				<th style="vertical-align:top;">Conteúdo:</th>
				<td><h:outputText value="#{ topicoAula.object.conteudo }" escape="false" /></td>
			</tr>
			<tr>
			<td colspan="2">
				<br />
				<table class="listing" style="width: 530px; margin-left: 10px;">
					<caption style="font-weight: bold; text-align: center; background: #CCCCCC; padding: 2px;">Conteúdo deste Tópico de Aula</caption>
					<thead>
						<tr>
							<th style="text-align: center; width: 80px;">Data</th>
							<th style="text-align: left;">Nome</th>
							<th style="text-align: left; width: 80px;">Tipo</th>
						</tr>
					</thead>
					<c:forEach var="m_" items="#{topicoAula.object.materiais}" varStatus="loop">
						<tr tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
							<td class="first" style="text-align: center;">
								<ufrn:format type="data" valor="${m_.dataCadastro}" />
							</td>
							<td style="text-align: left;">${m_.nome}</td>
							<td style="text-align: left;">
								<c:if test="${m_.tipoArquivo}">Arquivo</c:if>
								<c:if test="${m_.tipoIndicacao }">Indicação</c:if>
								<c:if test="${m_.tipoTarefa}">Tarefa</c:if>
								<c:if test="${m_.tipoConteudo }">Conteúdo</c:if>
								<c:if test="${m_.tipoVideo }">Vídeo</c:if>
								<c:if test="${m_.tipoQuestionario }">Questionário</c:if>
								<c:if test="${m_.tipoForum }">Fórum</c:if>
								<c:if test="${m_.tipoChat }">Chat</c:if>
							</td>
		
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		</table>
		
		<div class="botoes-show">
			<input type="hidden" name="id" value="${ topicoAula.object.id }"/>
			
			<h:commandButton action="#{ topicoAula.editar }" value="Editar" rendered="#{ turmaVirtual.docente }" /> | 
			<h:commandButton action="#{ topicoAula.listar }" value="<< Voltar"/>
		</div>
	
	</fieldset>
	
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp" %>

