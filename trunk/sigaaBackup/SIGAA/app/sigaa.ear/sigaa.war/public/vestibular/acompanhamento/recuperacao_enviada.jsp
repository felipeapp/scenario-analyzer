<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
		<h2>Vestibular > Solicita��o de Recupera��o de Senha</h2>
	
		<div class="descricaoOperacao">
			<h3>E-Mail Enviado</h3>
		
			<p>Voc� receber� um e-mail com instru��es de como recuperar sua senha de acesso � �rea pessoal. Esta solicita��o ter� validade de 24 horas. Ap�s este per�odo, ser� necess�rio realizar uma nova solicita��o de recupera��o de senha.</p>
			<br/>
			<div align="center"><h:commandLink value="P�gina Inicial" action="#{acompanhamentoVestibular.cancelar}" id="acompanhamentoVestibular_cancelar"/></div>
		</div>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>