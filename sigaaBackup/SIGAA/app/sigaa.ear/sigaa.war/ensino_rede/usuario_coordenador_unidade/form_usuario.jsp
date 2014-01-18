<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
	<h2><ufrn:subSistema /> > Cadastrar Usuário para Coordenador de Unidade</h2>
	<table class="formulario" width="700">
		<caption class="listagem">Dados do Usuário</caption>
			<tr>
				<th class="required">E-mail:</th>
				<td>
					<h:inputText value="#{ usuarioCoordenadorUnidadeMBean.usuario.email }" size="45" id="txtEmail" />
				</td>
			</tr>			
			<tr>
				<th class="required">Login:</th>
				<td>
					<h:inputText value="#{ usuarioCoordenadorUnidadeMBean.usuario.login }" id="txtlogin" size="15" />
				</td>
			</tr>
			<tr>
				<th class="required">Senha:</th>
				<td>
					<h:inputSecret value="#{ usuarioCoordenadorUnidadeMBean.usuario.senha }"  id="secretSenha" size="15" />
				</td>
			</tr>
			<tr>
				<th class="required">Confirmar Senha:</th>
				<td>
					<h:inputSecret value="#{ usuarioCoordenadorUnidadeMBean.usuario.confirmaSenha }" size="15" id="secretConfirmSenha" />
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="2">
				 <h:commandButton value="Cadastrar" action="#{usuarioCoordenadorUnidadeMBean.cadastrar}" id="btncadastrar" /> 
				 <h:commandButton value="<< Voltar" action="#{usuarioCoordenadorUnidadeMBean.voltarTelaListaCoord}" id="btnvoltar" /> 
				 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{usuarioCoordenadorUnidadeMBean.cancelar}" id="btncancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>