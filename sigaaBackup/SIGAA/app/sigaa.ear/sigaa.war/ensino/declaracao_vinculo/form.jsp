<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
	h2{
		padding-top: 2cm;
		padding-bottom: 2cm;
		font-size: 1.5em;
		letter-spacing: 0.4em;
		word-spacing: 0.4em;
		text-align: center;
	}
-->
</style>
<f:view>

	<h2 style="border-bottom: 0;">Declaração de Vínculo</h2>

	<p align="justify" style="font-size: 1.3em; line-height: 1.5em">
		Declaramos para os devidos fins que o discente <h:outputText value="#{portalDiscente.discenteUsuario.nome}"/>, 
		matrícula <h:outputText value="#{portalDiscente.discenteUsuario.matricula}"/>, encontra-se vinculado à ${ configSistema['siglaInstituicao'] } na presente data.
	</p>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
