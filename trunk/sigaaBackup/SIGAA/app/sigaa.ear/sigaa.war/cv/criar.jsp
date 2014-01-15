<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	dl { margin-left: 3em; }
	dt { font-weight: bold; }
	dd { margin-left: 10px; margin-bottom: 7px;}
</style>

<f:view>
	<h:form>
		<h2><ufrn:subSistema /> &gt; Criar Comunidade Virtual</h2>

		<div class="descricaoOperacao">
			<p>
				Caro Usuário, 
			</p>
			<br/>
			<p>
				A <b>Comunidade Virtual</b> é um ambiente que proporciona a socialização e interação virtual aos usuários do nosso sistema acadêmico. Ela se assemelha
				ao <em>Ambiente Virtual de Aprendizado</em> no sentido de permitir compartilhar informações, disponibilizar fóruns, download de arquivos, enquentes, notícias 
				e chats para os seus participantes.
			</p>
			<p>
				É possível criar várias comunidades sobre os temas que lhe sejam convenientes e deixá-las públicas a qualquer usuário do sistema ou restrita
				a um grupo de convidados, tudo isso de acordo com sua necessidade.
			</p>
			<p> Também é possível disponibilizar o material da comunidade virtual para o público geral acessando as configurações da comunidade e selecionando <b>Publicar Comunidade</b>.</p>
			<p>
				Veja abaixo os tipos de comunidades virtuais disponíveis:
			</p>
			<dl>
			
				<dt> Privada  </dt>
					<dd> Apenas os moderadores podem convidar membros à comunidade. Comunidades privadas não serão listadas na busca de comunidades virtuais. </dd>
					
					<dt> Pública e Não Moderada </dt> 
						<dd> Qualquer usuário do sistema pode inscrever-se na comunidade, sem a necessidade de solicitar permissão para tal. </dd>
					<dt> Moderada
						<dd> A comunidade será listada nas buscas dentro do sistema, mas é necessário que os usuários solicitem participação na comunidade aos moderadores. </dd>
						
				<c:if test="${comunidadeVirtualMBean.gestorComunidades}"> 
					<dt> Restrito a Grupos
						<dd> A comunidade será criada para um grupo personalizado de usuários. Seu acesso na busca de comunidades é público
							mas somente os pertencentes aquele grupo podem ingressar nela. 	</dd>
				</c:if>
			</dl>
		</div>
		
		<table class="formulario" width="90%">
			<caption> Dados da Comunidade Virtual </caption>
			<tbody>
				<tr>
					<th class="required" width="20%">
						<h:outputLabel for="nomeComunidade" value="Nome:"/> 
					</th>
					<td>
						<h:inputText value="#{ comunidadeVirtualMBean.obj.nome }" id="nomeComunidade" style="width: 95%" />
					</td>
				</tr>
				<tr>
					<th class="required">
						<h:outputLabel for="descricaoComunidade" value="Descrição:"/> 
					</th>
					<td>
						<h:inputTextarea value="#{ comunidadeVirtualMBean.obj.descricao }" id="descricaoComunidade" rows="4" style="width: 95%" />
					</td>
				</tr>
				<tr>
					<th class="required">
						<h:outputLabel for="tipoComunidade" value="Tipo da Comunidade:"/> 
					</th>
					<td> 
						<h:selectOneRadio value="#{ comunidadeVirtualMBean.obj.tipoComunidadeVirtual.id }" id="tipoComunidadeID">
							<f:selectItems value="#{ comunidadeVirtualMBean.tiposComunidades }"/>
						</h:selectOneRadio> 
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cadastrar" action="#{ comunidadeVirtualMBean.cadastrar }" /> 
						<h:commandButton value="Cancelar" action="#{ comunidadeVirtualMBean.cancelar }" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br />		
		<div style="text-align: center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena">Campos de preenchimento obrigatório. </span> <br/>
		</div>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>