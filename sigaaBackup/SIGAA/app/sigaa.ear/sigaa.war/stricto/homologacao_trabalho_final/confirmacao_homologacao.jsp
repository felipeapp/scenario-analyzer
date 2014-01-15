<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema /> > Homologar Trabalho Final
</h2>

<f:view>

	<a4j:keepAlive beanName="homologacaoTrabalhoFinal"></a4j:keepAlive>

	<h:form>
		<center>
			<h:commandLink actionListener="#{homologacaoTrabalhoFinal.gerarDocumentoRequisicaoDiploma}" id="linkGerarDocumentoRequisicaoDiploma">
				<h:graphicImage url="/img/printer_ok.png" title="Gerar Documentos"/>
			</h:commandLink>
			<h:commandLink actionListener="#{homologacaoTrabalhoFinal.gerarDocumentoRequisicaoDiploma}" value="Clique aqui para gerar: Requisição para Confecção de Diploma e Formulário para Cadastro de #{homologacaoTrabalhoFinal.obj.banca.dadosDefesa.discente.doutorado ? 'Tese' : 'Dissertação' }" id="gerarDocumentoRequisicaoDiploma"/>
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>