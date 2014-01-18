<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="forumMensagem" />
	<a4j:keepAlive beanName="forum" />
	<a4j:keepAlive beanName="listagemCursosForumDocenteMBean" />
	<h2><ufrn:subSistema /> > Novo Tópico para este Fórum</h2>

<div class="descricaoOperacao">
A partir do tópico é que as discussões serão realizadas.
</div>

	<h:form id="form" enctype="multipart/form-data">

		<table class="formulario">
			<caption class="formulario">Novo tópico</caption>
			<tr>
				<th class="obrigatorio">Título:</th>
				<td>
					<h:inputText id="titulo" size="83" maxlength="80" value="#{forumMensagem.object.titulo}"/>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio" style="padding-left: 10px;">Conteúdo:</th>
				<td style="padding-right: 10px;">
					<t:inputTextarea id="descricao" rows="10" cols="80" value="#{forumMensagem.object.conteudo}"/>
				</td>
			</tr>
			
			<c:if test="${forumMensagem.gestorDoProgramaEscolhido}">
			<tr>
				<th>Arquivo:</th>
				<td>
					<t:inputFileUpload id="myFileId" value="#{forumMensagem.arquivo}" storage="file" required="false" />
				</td>
			</tr>
			</c:if>

			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton action="#{forumMensagem.cadastrarMensagemForumProgramas}" value="Cadastrar Tópico" id="cadTopicos"/> 
						<h:commandButton value="<< Voltar" action="#{forumMensagem.listarForunsPorPrograma}" id="btnVolta"/>
						<h:commandButton value="Cancelar" action="#{forum.cancelar}" id="btnCancelar" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
				
		</table>
				<br>
				<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
					class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
				<br>
				</center>
	</h:form>
	
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
