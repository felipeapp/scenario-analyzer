<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<br/>
	<h:form id="form">
			<h2>Dados Pessoais Cadastrados Com Sucesso!</h2>
			<div class="descricaoOperacao">
			<p><b>Caro ${acompanhamentoVestibular.obj.nome},</b></p>
			<br/>
			<p>O cadastro dos seus <b>dados pessoais</b> foi conclu�do com sucesso!</p>
			<p>A partir de agora voc� poder� usar a senha que informou no cadastro para acessar a sua �rea pessoal e:
				<ul>
					<li>Alterar seus dados pessoais</li>
					<li>Relizar novas inscri��es</li>
					<li>Reimprimir boletos para pagamento de taxa de inscri��o</li>
					<li>Acessar outras op��es.</li>
				</ul> 
			</p>
			<p><b>Voc� ainda n�o est� inscrito no Vestibular.</b>
			Continue com o processo de inscri��o clicando no link abaixo.<br/></p>
			<br/>
			<div align="center">
			<h:commandLink action="#{acompanhamentoVestibular.pulaConfirmacaoDadosPessoais }" id="realizarNovaInscricao">
				<h3>Clique Aqui para continuar o Processo de Inscri��o do Vestibular >></h3>
			</h:commandLink>
			</div>
			</div>
			
			<br/>
	</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp"%>