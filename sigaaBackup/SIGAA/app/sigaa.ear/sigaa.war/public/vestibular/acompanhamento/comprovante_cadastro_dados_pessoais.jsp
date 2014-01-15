<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<br/>
	<h:form id="form">
			<h2>Dados Pessoais Cadastrados Com Sucesso!</h2>
			<div class="descricaoOperacao">
			<p><b>Caro ${acompanhamentoVestibular.obj.nome},</b></p>
			<br/>
			<p>O cadastro dos seus <b>dados pessoais</b> foi concluído com sucesso!</p>
			<p>A partir de agora você poderá usar a senha que informou no cadastro para acessar a sua área pessoal e:
				<ul>
					<li>Alterar seus dados pessoais</li>
					<li>Relizar novas inscrições</li>
					<li>Reimprimir boletos para pagamento de taxa de inscrição</li>
					<li>Acessar outras opções.</li>
				</ul> 
			</p>
			<p><b>Você ainda não está inscrito no Vestibular.</b>
			Continue com o processo de inscrição clicando no link abaixo.<br/></p>
			<br/>
			<div align="center">
			<h:commandLink action="#{acompanhamentoVestibular.pulaConfirmacaoDadosPessoais }" id="realizarNovaInscricao">
				<h3>Clique Aqui para continuar o Processo de Inscrição do Vestibular >></h3>
			</h:commandLink>
			</div>
			</div>
			
			<br/>
	</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp"%>