<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<a4j:keepAlive beanName="cadastroUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
<style>
	descricaoOperacao p{
		line-height: 1.25em;
		margin: 8px 10px;
	}
</style>

	<h2>  <ufrn:subSistema /> &gt; Termo de Responsabilidade de Adesão às Bibliotecas </h2>
	
	<h:form id="form">
	<div class="descricaoOperacao">
		<h4>Termo de Responsabilidade</h4> <br />
		<p style="line-height:200%; padding-left: 30px;">
			${cadastroUsuarioBibliotecaMBean.termoAdesaoBibliotecas.texto}
		</p>
		<br>
		<p style="text-align: center;">${ configSistema['cidadeInstituicao'] }, ${cadastroUsuarioBibliotecaMBean.termoAdesaoBibliotecas.dataFormatada}</p>
		<br>
		<p style="text-align: center;">${cadastroUsuarioBibliotecaMBean.termoAdesaoBibliotecas.nomePessoa}</p>
		
	 		<br>
			<h:panelGroup id="grupoConcordancia" layout="block" style="text-align: center;">
					<h:selectBooleanCheckbox value="#{cadastroUsuarioBibliotecaMBean.concordoCheck}" id="checkConcordancia" />
					<h:outputLabel for="checkConcordancia" id="labelConcordancia" style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em; text-align: center; font-weight: bold;">
					Declaro que li e concordo com o Termo de Responsabilidade de Adesão às Bibliotecas da ${ configSistema['siglaInstituicao'] }.
					</h:outputLabel>
			</h:panelGroup>
		
	</div>

	<center>
		<h:commandButton value="Iniciar Cadastro >>" action="#{cadastroUsuarioBibliotecaMBean.paginaCadastro}" id="btnIniciarSolicit"/>
	</center>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
