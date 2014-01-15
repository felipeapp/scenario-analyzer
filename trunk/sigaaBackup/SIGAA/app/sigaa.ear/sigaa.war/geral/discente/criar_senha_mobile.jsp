<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema/> > Criar Senha de Acesso por Celular</h2>

	<div class="descricaoOperacao">
	<p>Caro usuário,</p>
	<p>Para você ter acesso aos recursos do SIGAA no seu celular,
	   crie uma senha de 6 dígitos (somente números), confirmando
	   a operação com a senha que você já possui do sistema SIGAA.
	   <br/><br/>
	   Caso deseje alterar sua senha, o procedimento é o mesmo, basta digitar
	   a nova senha nos campos abaixo e confirmá-la.
	</p> 
	</div>

	<h:form id="form">
			<table class="formulario" style="width:420px;">
			
			<h:messages showDetail="true"></h:messages>
			
					<caption> Definir senha de acesso por celular </caption>
					<tr>
							<th> Digite a senha: </th>
							<td> 
								<input type="password" id="senha1" name="senha1" size="6" maxlength="6" onkeyup="formatarInteiro(this)" /> (somente números) 
							</td>
						</tr>
						<tr>
							<th nowrap="nowrap"> Confirme a senha: </th>
							<td>
								 <input type="password" id="senha2" name="senha2" size="6" maxlength="6" onkeyup="formatarInteiro(this)" />
							 </td>
						</tr>
						<tr>
							<th nowrap="nowrap"> Entre com a senha do SIGAA: </th>
							<td>  
								<input type="password" id="senhaSigaa" name="senhaSigaa" size="10" maxlength="15" /> 
							</td>
						</tr>
					<tr>
					
					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Criar senha" action="#{senhaMobileMBean.cadastrar}"/>
								<h:commandButton value="Cancelar" action="#{senhaMobileMBean.cancelar}" />
							</td>
						</tr>
					</tfoot>
			</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>