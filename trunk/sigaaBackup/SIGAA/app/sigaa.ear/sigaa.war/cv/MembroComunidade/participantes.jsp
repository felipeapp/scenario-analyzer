<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="membroComunidadeMBean" />
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>
<div class="secaoComunidade">
	<rich:panel header="Participantes da Comunidade (#{ fn:length(participantes) }) " headerClass="headerBloco">
	<h:form>
	<div class="infoAltRem">
		<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar}">
			<c:if test="${comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.restritoGrupo == false}">
				<h:graphicImage value="/img/adicionar.gif"/> 
				<h:commandLink id="cadastrarNovoParticipante" action="#{ membroComunidadeMBean.novo }" value="Cadastrar novo participante" />
	        </c:if>
	        <c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar && comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.restritoGrupo == true}">
	        	<h:graphicImage value="/img/adicionar.gif"/>
				<h:commandLink id="selecionarGrupoParticipantes" action="#{ comunidadeVirtualMBean.configuracoesComunidade }"
					 value="Selecionar grupo de participantes"/>
	        </c:if>
	        	<h:graphicImage value="/img/alterar.gif"/>: Alterar permissão
	        	<h:graphicImage value="/img/garbage.png"/>: Excluir participação
	 	</c:if>
		</div>
	<br/>
	<c:set var="participantes" value="#{membroComunidadeMBean.participantesPaginado}" />
	<c:if test="${ empty participantes}">
		<p class="vazio">Nenhum item foi encontrado.</p>
	</c:if>
	
	<c:if test="${ not empty participantes}">
	
		<%-- Exibe a paginação --%>
		<c:set var="itensPaginacao" value="#{ participantes }" />
		<c:set var="beanPaginacao" value="#{ membroComunidadeMBean }" />
		<c:set var="labelCrescente" value="Z-A" />
		<c:set var="labelDecrescente" value="A-Z" />
		<%@include file="/cv/include/paginacao.jsp" %>
		<hr />
	
		<c:forEach items="#{ participantes }" var="item" varStatus="loop">
			<table class="listagem" style="width:340px;height:120px;float:left;margin:0px 10px 10px 0px;padding:5px;border:none;">
				<c:if test="${loop.index % 2 == 0 }">
				<tr class="${ loop.index % 4 == 0 ? 'linhaPar' : '' }">
				</c:if>
				<td width="50" align="center" valign="top" style="padding:5px 0px;">
					<c:if test="${item.usuario.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.usuario.idFoto}&key=${ sf:generateArquivoKey(item.usuario.idFoto) }" width="48" height="60"/></c:if>
					<c:if test="${item.usuario.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
				</td>
				<td valign="top">
					
					<c:if test="${item.usuario.online}">
						<img src="${ctx}/img/portal_turma/online.png" style="float:left;"  title="Usuário On-Line no SIGAA"/>
					</c:if>
					<c:if test="${!item.usuario.online}">
						<img src="${ctx}/img/portal_turma/offline.png" style="float:left;"  title="Usuário Off-Line no SIGAA"/>
					</c:if>
					<strong style="position:relative;padding:0px;">${item.usuario.pessoa.nome} </strong>
					<br clear="all"/>
					<ul>
						<li>Permissão: <em>${ item.descricaoPermissao }</em></li>
						<li>Usuário: <em>${ item.usuario != null ? item.usuario.login : "Sem cadastro no sistema" }</em></li>
						<li>E-Mail: <em>${ item.usuario != null ? item.usuario.email : "Desconhecido" }</em></li>
					</ul>
					<c:if test="${ comunidadeVirtualMBean.membro.administrador}">
						<h:commandLink id ="alterarPermissao" action="#{ membroComunidadeMBean.editar }">
							<f:param name="id" value="#{ item.id }"/>
							<h:graphicImage value="/img/alterar.gif" title="Alterar permissão"/>
						</h:commandLink> &nbsp;
					</c:if>
					<c:if test="${  comunidadeVirtualMBean.membro.permitidoModerar}">
						<h:commandLink action="#{ membroComunidadeMBean.remover }"
							id="remover" styleClass="confirm-remover" 
							onclick="return(confirm('Se remover esse participante, ele não terá mais acesso a essa comunidade. Deseja realmente remover?'));">
							<f:param name="id" value="#{ item.id }"/>
							<h:graphicImage value="/img/garbage.png"  title="Excluir participação"/>
						</h:commandLink>
					</c:if>
				</td>
			</table>	
		</c:forEach>
		
		<div style="clear:both;"></div>
	
	</c:if>
</h:form>
</rich:panel>
</div>


</f:view>
<%@include file="/cv/include/rodape.jsp" %>