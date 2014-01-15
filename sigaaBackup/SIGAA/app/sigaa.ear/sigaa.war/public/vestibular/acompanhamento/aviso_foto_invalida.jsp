<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<style>
th.rotulo{ text-align: right; font-weight: bold; }
</style>
<f:view>
	<h2>Confirmação de Dados Pessoais</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Candidato,</b></p>
		<p>Seus dados pessoais foram migrados de outros
			concursos/vestibulares anteriores e estão incompletos. Por favor,
			atualize seus dados pessoais, bem com a sua foto, para poder
			continuar com sua inscrição.
	</div>
	<div align="center">
		<h:form id="form">
			<h:commandLink action="#{acompanhamentoVestibular.atualizarDadosPessoais }" id="atualizarDadosPessoais">
			<b>ATENÇÃO</b>
			<h3>Atualização de Dados Pessoais</h3>
			Clique aqui para atualizar seus Dados Pessoais e poder continuar com sua inscrição.
			</h:commandLink>
		</h:form>
	</div>
	<br/>
</f:view>
<%@include file="/public/include/rodape.jsp"%>