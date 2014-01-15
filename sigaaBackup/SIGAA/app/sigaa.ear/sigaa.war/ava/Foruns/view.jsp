<%@include file="/ava/cabecalho.jsp" %>


<style>
<!--
.formAva th { 
	font-weight: bold!important;
	text-align: left!important;
	width: 25%!important;
	}
-->
</style>
<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<h:form id="form">	
		<fieldset> 
			<legend>Fórum</legend>
			
			<table class="formAva" style="margin-left:0">
				<tr>
					<th>Título:</th>
					<td><h:outputText value="#{ forumBean.obj.titulo }" /></td>
				</tr>
				<tr>
					<th>Descrição:</th>
					<td class="conteudoMensagemForum"><h:outputText value="#{ forumBean.obj.descricao }" escape="false"/></td>
				</tr>
				<tr>
					<th>Autor(a):</th>
					<td><h:outputText value="#{ forumBean.obj.usuario.pessoa.nome }" escape="false"/></td>
				</tr>					
				<tr>
					<th>Arquivo:</th>
					<td>
						<h:commandLink action="#{ forumBean.viewArquivo }" title="Visualizar Arquivo"	id="verArquivo" target="blank" 
							rendered="#{ forumBean.obj.possuiArquivoAnexo }">
		                      <f:param name="idArquivo" value="#{ forumBean.obj.idArquivo }" />
	                             <h:graphicImage url="/img/view.gif" />
	                    </h:commandLink>
                    </td>
				</tr>
				<tr>
					<th>Monitorar Leitura:</th>
					<td><h:outputText value="#{ forumBean.obj.monitorarLeitura ? 'SIM' : 'NÃO' }" /></td>
				</tr>
				<tr>
					<th>Tipo:</th>
					<td><h:outputText value="#{ forumBean.obj.tipo.descricao }" /></td>
				</tr>
				<tr>
					<th>Ordenação Padrão:</th>
					<td><h:outputText value="#{ forumBean.obj.ordenacaoPadrao.descricao }" /></td>
				</tr>
				<tr>
					<th>Criado em:</th>
					<td>
						<h:outputText value="#{ forumBean.obj.dataCadastro }">
							<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
						</h:outputText>
					</td>
				</tr>
			</table>
		
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumMensagemBean.novoTopico }" value="Cadastrar #{ forumBean.obj.tipo.labelMensagem }" id="cmdNovoTopico"  
						rendered="#{ forumBean.obj.tipo.permiteCriarTopico && forumBean.permiteNovoTopico }" >
						<f:setPropertyActionListener value="#{ forumBean.obj.id }" target="#{ forumMensagemBean.forum.id }"/> 
					</h:commandButton>
				</div>
				<div class="other-actions">	
					<h:commandButton action="#{ forumBean.atualizar }" value="Editar" id="cmdEditar" rendered="#{ forumBean.usuarioLogado.id == forumBean.obj.usuario.id }"/>
					<input type="button" onclick="javascript: history.back();" value="<< Voltar" />
					<%-- h:commandButton action="#{ forumBean.listar }" value=" << Voltar" id="cmdListar" immediate="true"/ --%>
				</div>
			</div>	
			
		</fieldset>	
			
			<ul>
				<li>
					<%@include file="/ava/Foruns/_topicos.jsp" %>
				</li>
			</ul>
			
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
