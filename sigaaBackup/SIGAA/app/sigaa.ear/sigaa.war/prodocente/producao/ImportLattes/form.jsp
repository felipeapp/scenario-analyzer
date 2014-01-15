<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/porta_arquivos.css" type="text/css" />

<f:view>

	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>

	<h2><ufrn:subSistema /> &gt; Importar Currículo Lattes </h2>

	<h:form enctype="multipart/form-data">
	
		<center>
			<table class="ajudaPortaArquivos" width="100%">
				<tr>
					<td width="40" align="center" valign="middle"><html:img page="/img/help.png" width="32" height="32" /> </td>
					<td valign="top" style="text-align: justify">
						<center><h4>ATENÇÃO!</h4></center>
						<ol>
							<li>Agora é possível ter mais de uma importação por ano. Você poderá importar os seus dados, colocar novas produções
								no Currículo Lattes e importá-los novamente. Apenas as novas produções serão importadas.</li>
							<li>O <a href="${ pageContext.request.contextPath }/manuais/Prodocente/importar_curriculo_lattes/completo.jsp">
								manual</a> para a importação do currículo já está disponível.</li>
							<li>Antes de enviar o currículo você deve descompactar o arquivo baixado no site do CNPq com o 
								<a href="http://download.winzip.com/winzip111.exe">WinZip</a> ou o 
								<a href="http://www.rarlab.com/rar/wrar37b7br.exe">WinRAR</a>.
							</li>
							<li>Se o arquivo estiver corrompido, você deve entrar em contato com o CNPq. A equipe do SIGAA não é responsável 
								por este problema.</li>
						</ol>
					</td>
				</tr>
			</table>
		</center>

		<table class="formulario">
			<caption>Informe o arquivo</caption>
			<tr>
				<th class="obrigatorio">Ano de Referência:</th>
				<td>
					<h:inputText value="#{ uploadCurriculo.anoReferencia }" id="anoReferencia" maxlength="4" 
							size="4" onkeyup="formatarInteiro(this, event)" />
					<%--				
					<ufrn:help img="/img/ajuda.gif">Informe um intervalo. Ex.: 2000-2010</ufrn:help>
					<h:selectOneMenu value="#{ uploadCurriculo.anoReferencia }" id="anoReferencia">
						<f:selectItems value="#{ uploadCurriculo.anosPossiveis }" />
					</h:selectOneMenu>
					 --%>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Arquivo do Currículo:</th>
				<td><t:inputFileUpload size="30" value="#{ uploadCurriculo.arquivo }" id="arquivoCurriculo" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Enviar" action="#{ uploadCurriculo.importarCurriculo }" id="enviar" />
					<h:commandButton value="Cancelar" action="#{ uploadCurriculo.cancelar }" id="cancelar" />
				</td>
			</tr>
		</table>
	</h:form>

	<br />&nbsp;
	<p><strong>Os seguintes tipos de produção estão sendo importados:</strong> Apresentações de Obras Artísticas; Apresentações de Trabalhos 
		em Eventos; Arranjos Musicais; Artigos Publicados em Revistas, Jornais ou Periódicos; Bancas; Capítulos de Livros; Cartas, Mapas e 
		Similares; Composição Musical; Desenvolvimento de Material Didático, Texto em Jornal ou Revista; Livros Publicados; Maquetes; Obras 
		de Arte Visual; Organização de Eventos; Partituras Musicais; Softwares; Sonoplastia; Publicações em Eventos; Traduções.</p>

</f:view>

<script>

	function validarAno(campo, event){
		campo.value = campo.value.replace(/\D/g, "");
		if (campo.value.length >= 5)
			campo.value = campo.value.substring(0,4) + "-" + campo.value.substr(4);
	}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>