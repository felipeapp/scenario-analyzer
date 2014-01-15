<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="membroComunidadeMBean" />
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>

<div class="secaoComunidade">
		<rich:panel header="Cadastrar Participante">	
		<div class="descricaoOperacao">
			<p> <b>Administrador: </b> Pode realizar <b>qualquer</b> opera��o na comunidade, como excluir ou alterar t�picos de outros usu�rios, adicionar ou remover participantes a comunidade, etc.</p>
			<p> <b>Moderador: </b> usu�rios com esse tipo de permiss�o poderam realizar opera��es como <b>excluir e editar</b> t�picos de f�runs, arquivos, enquetes, entre outras coisas. Esse tipo de permiss�o deve ser concedida para usu�rios que ir�o ajudar o administrador a gerenciar seu conte�do. </p>
			<p> <b>Membro: </b> usu�rios com essa permiss�o podem participar normalmente das atividades da comunidade (cadastrando t�picos, enquetes, participar de f�runs, etc), por�m eles <b>n�o</b> podem alterar ou remover nada que n�o seja de sua pr�pria autoria.</p>
			<br>
			A inclus�o de membros em uma Comunidade Virtual � baseada em convites. Os moderadores ou administradores podem enviar convites para os usu�rios do sistema e uma vez o convite aceito esse usu�rio passa ser
			membro da comunidade.
		</div>
		
	<h:form>
	<table class="formulario" style="width:50%;">
		<caption> Informe o nome do usu�rio </caption>
		<tbody>
			<tr>
				<th class="required">Nome:</th>
				<td> <h:inputText value="#{membroComunidadeMBean.nomePessoaBuscar}" size="85" /> </td>
			</tr>
			
			<c:if test="${ comunidadeVirtualMBean.membro.moderador && not empty membroComunidadeMBean.listaUsers}">
				<tr>
					<th class="required">Permiss�o:</th>
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
				<th class="required">Permiss�o:</th>
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
					<h:commandButton action="#{ membroComunidadeMBean.cancelar }" value="Cancelar" onclick="return(confirm('Deseja sair dessa p�gina ?'));" immediate="true"/>
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
						<img src="${ctx}/img/portal_turma/online.png" style="float:left;"  titles="Usu�rio On-Line no SIGAA"/>
					</c:if>
					<c:if test="${!item.usuario.online}">
						<img src="${ctx}/img/portal_turma/offline.png" style="float:left;"  title="Usu�rio Off-Line no SIGAA"/>
					</c:if>
					
					<strong style="position:relative;padding:0px;">${item.pessoa.nome} </strong>
					<br clear="all"/>
					<ul>
						<li>Usu�rio: <em>${ item != null ? item.login : "Sem cadastro no sistema" } </em></li>
						<li>E-Mail: <em>${ item != null ? item.email : "Desconhecido" }</em></li>
						<li> 
							<h:commandLink
									action="#{membroComunidadeMBean.cadastrar}" value="Convidar Usu�rio">
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