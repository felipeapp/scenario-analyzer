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
				Caro Usu�rio, 
			</p>
			<br/>
			<p>
				A <b>Comunidade Virtual</b> � um ambiente que proporciona a socializa��o e intera��o virtual aos usu�rios do nosso sistema acad�mico. Ela se assemelha
				ao <em>Ambiente Virtual de Aprendizado</em> no sentido de permitir compartilhar informa��es, disponibilizar f�runs, download de arquivos, enquentes, not�cias 
				e chats para os seus participantes.
			</p>
			<p>
				� poss�vel criar v�rias comunidades sobre os temas que lhe sejam convenientes e deix�-las p�blicas a qualquer usu�rio do sistema ou restrita
				a um grupo de convidados, tudo isso de acordo com sua necessidade.
			</p>
			<p> Tamb�m � poss�vel disponibilizar o material da comunidade virtual para o p�blico geral acessando as configura��es da comunidade e selecionando <b>Publicar Comunidade</b>.</p>
			<p>
				Veja abaixo os tipos de comunidades virtuais dispon�veis:
			</p>
			<dl>
			
				<dt> Privada  </dt>
					<dd> Apenas os moderadores podem convidar membros � comunidade. Comunidades privadas n�o ser�o listadas na busca de comunidades virtuais. </dd>
					
					<dt> P�blica e N�o Moderada </dt> 
						<dd> Qualquer usu�rio do sistema pode inscrever-se na comunidade, sem a necessidade de solicitar permiss�o para tal. </dd>
					<dt> Moderada
						<dd> A comunidade ser� listada nas buscas dentro do sistema, mas � necess�rio que os usu�rios solicitem participa��o na comunidade aos moderadores. </dd>
						
				<c:if test="${comunidadeVirtualMBean.gestorComunidades}"> 
					<dt> Restrito a Grupos
						<dd> A comunidade ser� criada para um grupo personalizado de usu�rios. Seu acesso na busca de comunidades � p�blico
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
						<h:outputLabel for="descricaoComunidade" value="Descri��o:"/> 
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
			<span class="fontePequena">Campos de preenchimento obrigat�rio. </span> <br/>
		</div>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>