<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Atualizar Credenciais do Web Service CNPq</h2>

	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Atualizar dados</caption>
			
			<tr>
				<th >Novo usuário:</th>
				<td><h:inputText id="usuario" size="20" maxlength="30" value="#{atualizarCredenciaisCnpqMBean.usuario}" /></td>
			</tr>
			<tr>
			<tr>
				<th >Nova senha:</th>
				<td><h:inputSecret id="novaSenha" size="20" maxlength="30" value="#{atualizarCredenciaisCnpqMBean.senha}" /></td>
			</tr>
			<tr>
				<th >Confirmar nova senha:</th>
				<td><h:inputSecret id="senhaConfirmar" size="20" maxlength="30" value="#{atualizarCredenciaisCnpqMBean.confirmaSenha}" /></td>
			</tr>
			<tr>
			 
			<tr>
			  <td colspan="2">
			   <c:set var="exibirApenasSenha" value="true" scope="request"/>
			   <%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
			  </td>
			 </tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnCadastrar" value="Confirmar" action="#{atualizarCredenciaisCnpqMBean.confirmar}" />
						<h:commandButton id="btnCancelar" immediate="true" value="Cancelar" onclick="#{confirm}" action="#{atualizarCredenciaisCnpqMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
