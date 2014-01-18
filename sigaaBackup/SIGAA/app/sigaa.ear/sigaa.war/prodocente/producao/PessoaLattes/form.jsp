<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/porta_arquivos.css" type="text/css" />

<f:view>
	<a4j:keepAlive beanName="pessoaLattesMBean"></a4j:keepAlive>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>

	<h2><ufrn:subSistema /> &gt; Autorizar Acesso do ${ configSistema['siglaSigaa'] } ao Currículo Lattes </h2>

	<h:form id="form_autorizacao">
	
		<center>
			<table class="ajudaPortaArquivos" width="100%">
				<tr>
					<td width="40" align="center" valign="middle"><html:img page="/img/help.png" width="32" height="32" /> </td>
					<td valign="top" style="text-align: justify">
						<h4>Prezado usuário,</h4>
						<ul>
							<li>Para que o ${ configSistema['siglaSigaa'] } possa obter automaticamente as informações do seu currículo na Plataforma
							Lattes do CNPq, você deve primeiro autorizar o sistema a acessar tais informações.
							Para tanto, marque a caixa ao lado do texto abaixo, informe o ano de referência para a importação e clique no botão para confirmar.</li>
							<li>O <strong>ano de referência</strong> é utilizado para filtrar as produções que os sitema importará. Apenas as produções cujo ano for igual ou mais recente
							que o ano de referência serão importadas. Por exemplo, informando o ano de referência 2013, apenas as produções registradas no Lattes de 2013 em diante serão importadas.</li> 
							<li>O ${ configSistema['siglaSigaa'] } acessará diariamente suas produções registradas no Lattes
							e importará automaticamente <strong>qualquer atualização*</strong> para o seu banco de dados institucional.
							</li>
						</ul>
					</td>
				</tr>
			</table>
		</center>
	
		<div class="descricaoOperacao" style="width: 75%">
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
				<h:selectBooleanCheckbox value="#{pessoaLattesMBean.obj.autorizaAcesso}" id="autorizaAcesso" />
				<h:outputLabel for="autorizaAcesso">
					Autorizo o acesso aos dados do meu currículo na Plataforma Lattes do CNPq  
					para implantação no banco de dados institucional da ${ configSistema['siglaInstituicao'] }.
				</h:outputLabel>
			</p>
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
				O sistema pode importar automaticamente minhas produções a partir do 
				<span class="required"><strong>Ano de Referência:</strong></span>
				<h:inputText value="#{pessoaLattesMBean.obj.anoReferencia}" id="anoReferencia" size="4" maxlength="4"/>.
			</p>
			
			<div align="center" style="margin: 10px;">
				<h:commandButton value="Confirmar" action="#{pessoaLattesMBean.confirmar}" style="margin: 2px;"/>	
				<h:commandButton value="Voltar" action="#{pessoaLattesMBean.cancelar}" style="margin: 2px;"/>	
			</div>
		</div>
	</h:form>

	<br />&nbsp;
	<p><strong>* Os seguintes tipos de produção estão sendo importados:</strong> Apresentações de Obras Artísticas; Apresentações de Trabalhos 
		em Eventos; Arranjos Musicais; Artigos Publicados em Revistas, Jornais ou Periódicos; Bancas; Capítulos de Livros; Cartas, Mapas e 
		Similares; Composição Musical; Desenvolvimento de Material Didático, Texto em Jornal ou Revista; Livros Publicados; Maquetes; Obras 
		de Arte Visual; Organização de Eventos; Partituras Musicais; Softwares; Sonoplastia; Publicações em Eventos; Traduções.</p>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>