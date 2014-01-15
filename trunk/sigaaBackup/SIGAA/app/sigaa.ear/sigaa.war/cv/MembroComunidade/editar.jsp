<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>

<div class="secaoComunidade">
		<rich:panel header="Cadastrar Participante">
		
		<div class="descricaoOperacao">
			<p> <b>Administrador: </b> Pode realizar <b>qualquer</b> operação na comunidade, como excluir ou alterar tópicos de outros usuários, adicionar ou remover participantes a comunidade, etc.</p>
			<p> <b>Moderador: </b> usuários com esse tipo de permissão poderam realizar operações como <b>excluir e editar</b> tópicos de fóruns, arquivos, enquetes, entre outras coisas. Esse tipo de permissão deve ser concedida para usuários que irão ajudar o administrador a gerenciar seu conteúdo. </p>
			<p> <b>Membro: </b> usuários com essa permissão podem participar normalmente das atividades da comunidade (cadastrando tópicos, enquetes, participar de fóruns, etc), porém eles <b>não</b> podem alterar ou remover nada que não seja de sua própria autoria.</p>

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
					<th class="required"> <h:outputLabel for="permissao1">Permissão:</h:outputLabel> </th>
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
					<th class="required"> <h:outputLabel for="permissao2">Permissão:</h:outputLabel> </th>
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