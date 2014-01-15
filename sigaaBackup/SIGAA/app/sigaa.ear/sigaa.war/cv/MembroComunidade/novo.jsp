<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="membroComunidadeMBean" />
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>

<div class="secaoComunidade">
		<rich:panel header="Cadastrar Participante">	
		<div class="descricaoOperacao">
			<p> <b>Administrador: </b> Pode realizar <b>qualquer</b> operação na comunidade, como excluir ou alterar tópicos de outros usuários, adicionar ou remover participantes a comunidade, etc.</p>
			<p> <b>Moderador: </b> usuários com esse tipo de permissão poderam realizar operações como <b>excluir e editar</b> tópicos de fóruns, arquivos, enquetes, entre outras coisas. Esse tipo de permissão deve ser concedida para usuários que irão ajudar o administrador a gerenciar seu conteúdo. </p>
			<p> <b>Membro: </b> usuários com essa permissão podem participar normalmente das atividades da comunidade (cadastrando tópicos, enquetes, participar de fóruns, etc), porém eles <b>não</b> podem alterar ou remover nada que não seja de sua própria autoria.</p>
			<br>
			A inclusão de membros em uma Comunidade Virtual é baseada em convites. Os moderadores ou administradores podem enviar convites para os usuários do sistema e uma vez o convite aceito esse usuário passa ser
			membro da comunidade.
		</div>
		
	<h:form>
	<table class="formulario" style="width:50%;">
		<caption> Informe o nome do usuário </caption>
		<tbody>
			<tr>
				<th class="required">Nome:</th>
				<td> <h:inputText value="#{membroComunidadeMBean.nomePessoaBuscar}" size="85" /> </td>
			</tr>
			
			<c:if test="${ comunidadeVirtualMBean.membro.moderador && not empty membroComunidadeMBean.listaUsers}">
				<tr>
					<th class="required">Permissão:</th>
					<td> 
						<h:selectOneMenu value="#{ membroComunidadeMBean.object.permissao }" id="permissao1" style="width: 25%">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItem itemLabel="MEMBRO" itemValue="3" />
							<f:selectItem itemLabel="MODERADOR" itemValue="2" />
						</h:selectOneMenu> 
					</td>
				</tr>
			</c:if>
			
			<c:if test="${ comunidadeVirtualMBean.membro.administrador && not empty membroComunidadeMBean.listaUsers}">
			<tr>
				<th class="required">Permissão:</th>
				<td> 
					<h:selectOneMenu value="#{ membroComunidadeMBean.object.permissao }" id="permissao2" style="width: 25%">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItem itemLabel="MEMBRO" itemValue="3" />
						<f:selectItem itemLabel="MODERADOR" itemValue="2" />
						<f:selectItem itemLabel="ADMINISTRADOR" itemValue="1" />
					</h:selectOneMenu> 
				</td>
			</tr>
			</c:if>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{membroComunidadeMBean.buscarPessoas}" value="Buscar"/>
					<h:commandButton action="#{ membroComunidadeMBean.cancelar }" value="Cancelar" onclick="return(confirm('Deseja sair dessa página ?'));" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<table class="formulario" width="50%">
		<c:forEach items="#{ membroComunidadeMBean.listaUsers }" var="item" varStatus="loop">
			
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			
				<td width="50" align="center" valign="top" style="padding:5px 0px;">
					<c:if test="${item.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.idFoto}&key=${ sf:generateArquivoKey(item.idFoto) }" width="48" height="60"/></c:if>
					<c:if test="${item.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
				</td>
				
				<td valign="top">
					
					<c:if test="${item.usuario.online}">
						<img src="${ctx}/img/portal_turma/online.png" style="float:left;"  titles="Usuário On-Line no SIGAA"/>
					</c:if>
					<c:if test="${!item.usuario.online}">
						<img src="${ctx}/img/portal_turma/offline.png" style="float:left;"  title="Usuário Off-Line no SIGAA"/>
					</c:if>
					
					<strong style="position:relative;padding:0px;">${item.pessoa.nome} </strong>
					<br clear="all"/>
					<ul>
						<li>Usuário: <em>${ item != null ? item.login : "Sem cadastro no sistema" } </em></li>
						<li>E-Mail: <em>${ item != null ? item.email : "Desconhecido" }</em></li>
						<li> 
							<h:commandLink
									action="#{membroComunidadeMBean.cadastrar}" value="Convidar Usuário">
									<f:param name="idUsuario" value="#{item.id}" />
							</h:commandLink>
						</li>
					</ul>
				</td>
		</c:forEach>
	</table>
	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
			
	</h:form>
	</rich:panel>
</div>
</f:view>
<%@include file="/cv/include/rodape.jsp" %>