<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/porta_arquivos.css" type="text/css" />

<f:view>
	<a4j:keepAlive beanName="pessoaLattesMBean"></a4j:keepAlive>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>

	<h2><ufrn:subSistema /> &gt; Autorizar Acesso do ${ configSistema['siglaSigaa'] } ao Curr�culo Lattes </h2>

	<h:form id="form_autorizacao">
	
		<center>
			<table class="ajudaPortaArquivos" width="100%">
				<tr>
					<td width="40" align="center" valign="middle"><html:img page="/img/help.png" width="32" height="32" /> </td>
					<td valign="top" style="text-align: justify">
						<h4>Prezado usu�rio,</h4>
						<ul>
							<li>Para que o ${ configSistema['siglaSigaa'] } possa obter automaticamente as informa��es do seu curr�culo na Plataforma
							Lattes do CNPq, voc� deve primeiro autorizar o sistema a acessar tais informa��es.
							Para tanto, marque a caixa ao lado do texto abaixo, informe o ano de refer�ncia para a importa��o e clique no bot�o para confirmar.</li>
							<li>O <strong>ano de refer�ncia</strong> � utilizado para filtrar as produ��es que os sitema importar�. Apenas as produ��es cujo ano for igual ou mais recente
							que o ano de refer�ncia ser�o importadas. Por exemplo, informando o ano de refer�ncia 2013, apenas as produ��es registradas no Lattes de 2013 em diante ser�o importadas.</li> 
							<li>O ${ configSistema['siglaSigaa'] } acessar� diariamente suas produ��es registradas no Lattes
							e importar� automaticamente <strong>qualquer atualiza��o*</strong> para o seu banco de dados institucional.
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
					Autorizo o acesso aos dados do meu curr�culo na Plataforma Lattes do CNPq  
					para implanta��o no banco de dados institucional da ${ configSistema['siglaInstituicao'] }.
				</h:outputLabel>
			</p>
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
				O sistema pode importar automaticamente minhas produ��es a partir do 
				<span class="required"><strong>Ano de Refer�ncia:</strong></span>
				<h:inputText value="#{pessoaLattesMBean.obj.anoReferencia}" id="anoReferencia" size="4" maxlength="4"/>.
			</p>
			
			<div align="center" style="margin: 10px;">
				<h:commandButton value="Confirmar" action="#{pessoaLattesMBean.confirmar}" style="margin: 2px;"/>	
				<h:commandButton value="Voltar" action="#{pessoaLattesMBean.cancelar}" style="margin: 2px;"/>	
			</div>
		</div>
	</h:form>

	<br />&nbsp;
	<p><strong>* Os seguintes tipos de produ��o est�o sendo importados:</strong> Apresenta��es de Obras Art�sticas; Apresenta��es de Trabalhos 
		em Eventos; Arranjos Musicais; Artigos Publicados em Revistas, Jornais ou Peri�dicos; Bancas; Cap�tulos de Livros; Cartas, Mapas e 
		Similares; Composi��o Musical; Desenvolvimento de Material Did�tico, Texto em Jornal ou Revista; Livros Publicados; Maquetes; Obras 
		de Arte Visual; Organiza��o de Eventos; Partituras Musicais; Softwares; Sonoplastia; Publica��es em Eventos; Tradu��es.</p>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>