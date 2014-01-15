<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<h2>Vestibular > Solicita��o de Recupera��o de Senha</h2>
	
		<div class="descricaoOperacao">
			<h3>N�o Foi Poss�vel Recuperar a Senha</h3>
			<br/>		
			<p>Provavelmente a
		solicita��o de recupera��o de senha j� tinha sido atendida, ou o prazo
		para recuperar a senha est� expirado.</p>
		<p>Lembramos que uma solicita��o de recupera��o de senha tem a validade
		de 24 horas. Ap�s este per�odo, ser� necess�rio realizar uma nova
		solicita��o de recupera��o de senha.</p>
			<br/>
			<div align="center"><h:commandLink value="P�gina Inicial" action="#{acompanhamentoVestibular.cancelar}" id="cancelar" /></div>
		</div>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>