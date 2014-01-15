<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>

<div class="secaoComunidade">
		<rich:panel header="Cadastrar Participante">
		
		<div class="descricaoOperacao">
			<p> <b>Administrador: </b> Pode realizar <b>qualquer</b> opera��o na comunidade, como excluir ou alterar t�picos de outros usu�rios, adicionar ou remover participantes a comunidade, etc.</p>
			<p> <b>Moderador: </b> usu�rios com esse tipo de permiss�o poderam realizar opera��es como <b>excluir e editar</b> t�picos de f�runs, arquivos, enquetes, entre outras coisas. Esse tipo de permiss�o deve ser concedida para usu�rios que ir�o ajudar o administrador a gerenciar seu conte�do. </p>
			<p> <b>Membro: </b> usu�rios com essa permiss�o podem participar normalmente das atividades da comunidade (cadastrando t�picos, enquetes, participar de f�runs, etc), por�m eles <b>n�o</b> podem alterar ou remover nada que n�o seja de sua pr�pria autoria.</p>

		</div>
		
		<h:form id="form">
		
		<table class="formulario" width="50%">
			<caption>Dados do Participante</caption>
			<tbody>
				<tr>
					<th> <h:outputLabel for="nomeParticipante">Participante:</h:outputLabel> </th>
					<td> 
				
	  				<h:outputText id="pessoa" value="#{membroComunidadeMBean.object.pessoa.nome}" style="width: 350px;"/>
					<h:inputHidden value="#{membroComunidadeMBean.object.id}" id="idMembro"/>
					<h:inputHidden value="#{membroComunidadeMBean.object.pessoa.id}" id="idPessoa"/>
					<f:param name="id" value="#{ membroComunidadeMBean.object.id }"/>
						
					 </td>
				</tr>
				
				<c:if test="${ comunidadeVirtualMBean.membro.moderador}">
				<tr>
					<th class="required"> <h:outputLabel for="permissao1">Permiss�o:</h:outputLabel> </th>
					<td> 
						<h:selectOneMenu value="#{ membroComunidadeMBean.object.permissao }" id="permissao1" style="width: 50%">
							<f:selectItem itemLabel="MEMBRO" itemValue="3" />
							<f:selectItem itemLabel="MODERADOR" itemValue="2" />
						</h:selectOneMenu> 
					</td>
				</tr>
				</c:if>
				
				<c:if test="${ comunidadeVirtualMBean.membro.administrador}">
				<tr>
					<th class="required"> <h:outputLabel for="permissao2">Permiss�o:</h:outputLabel> </th>
					<td> 
						<h:selectOneMenu value="#{ membroComunidadeMBean.object.permissao }" id="permissao2" style="width: 50%">
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
					<td colspan="2">
						<h:commandButton action="#{membroComunidadeMBean.atualizar}" value="Confirmar" />
						<h:commandButton action="#{ membroComunidadeMBean.participantes }" value="Cancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
		</rich:panel>
</div>
</f:view>
		
<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>		
<%@include file="/cv/include/rodape.jsp" %>