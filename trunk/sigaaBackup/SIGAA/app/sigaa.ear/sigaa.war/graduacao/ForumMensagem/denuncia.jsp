<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Programa de Componentes Curriculares &gt; Dados</h2>

	<table class="visualizacao">
		<tr>
			<th width="30%">Autor da Postagem:</th>
			<td>${forumMensagem.object.usuario.pessoa.nome}</td>
		</tr>
		<tr>
			<th  width="30%" >Data:</th>
			<td><fmt:formatDate value="${ forumMensagem.object.data }" pattern="dd/MM/yyyy HH:mm:ss" /></td>
		</tr> 
		
		<tr>
			<th  width="30%" >Conteúdo da Postagem:</th>
			<td>${forumMensagem.object.conteudo}</td>
		</tr> 
	</table>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Estas informações serão disponibilizadas ao coordenador do curso, é de sua responsabilidade
		as denúncias aqui postadas.</p>
	</div>
	
	<br />
	<h:form id="form">
		<table class="formulario" width="680px">
			<caption>Denunciar Mensagem</caption>
			<tr>
				<td colspan="2">
					<c:set value="true" var="editar" />
					<p class="ajuda">
					Utilize o espaço abaixo para definir o motivo da denúncia. 
					<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
					</p>
					<h:inputTextarea value="#{forumMensagem.denuncia.motivoDenuncia}" id="objetivo" rows="10" cols="110" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Denunciar" action="#{forumMensagem.cadastrarDenuncia}" 
								id="denunciar" />
						
						<input type="hidden" value="${ forumMensagem.object.id }" name="idForumMensagem" id="idForumMensagem" />		
						
						<h:commandButton value="<< Voltar"	action="#{forumMensagem.mostrarForumMensagemCurso}" 
								id="selecionarOutro" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{forumMensagem.cancelarDenuncia}" id="cancelar" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>