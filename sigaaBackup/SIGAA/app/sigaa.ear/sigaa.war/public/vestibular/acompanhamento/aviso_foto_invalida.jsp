<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<style>
th.rotulo{ text-align: right; font-weight: bold; }
</style>
<f:view>
	<h2>Confirma��o de Dados Pessoais</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Candidato,</b></p>
		<p>Seus dados pessoais foram migrados de outros
			concursos/vestibulares anteriores e est�o incompletos. Por favor,
			atualize seus dados pessoais, bem com a sua foto, para poder
			continuar com sua inscri��o.
	</div>
	<div align="center">
		<h:form id="form">
			<h:commandLink action="#{acompanhamentoVestibular.atualizarDadosPessoais }" id="atualizarDadosPessoais">
			<b>ATEN��O</b>
			<h3>Atualiza��o de Dados Pessoais</h3>
			Clique aqui para atualizar seus Dados Pessoais e poder continuar com sua inscri��o.
			</h:commandLink>
		</h:form>
	</div>
	<br/>
</f:view>
<%@include file="/public/include/rodape.jsp"%>