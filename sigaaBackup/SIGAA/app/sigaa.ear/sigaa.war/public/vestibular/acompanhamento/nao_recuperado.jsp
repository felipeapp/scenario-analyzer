<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<h2>Vestibular > Solicitação de Recuperação de Senha</h2>
	
		<div class="descricaoOperacao">
			<h3>Não Foi Possível Recuperar a Senha</h3>
			<br/>		
			<p>Provavelmente a
		solicitação de recuperação de senha já tinha sido atendida, ou o prazo
		para recuperar a senha está expirado.</p>
		<p>Lembramos que uma solicitação de recuperação de senha tem a validade
		de 24 horas. Após este período, será necessário realizar uma nova
		solicitação de recuperação de senha.</p>
			<br/>
			<div align="center"><h:commandLink value="Página Inicial" action="#{acompanhamentoVestibular.cancelar}" id="cancelar" /></div>
		</div>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>