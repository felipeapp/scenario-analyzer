<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/porta_arquivos.css" type="text/css" />

<f:view>

	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>

	<h2><ufrn:subSistema /> &gt; Importar Curr�culo Lattes </h2>

	<h:form enctype="multipart/form-data">
	
		<center>
			<table class="ajudaPortaArquivos" width="100%">
				<tr>
					<td width="40" align="center" valign="middle"><html:img page="/img/help.png" width="32" height="32" /> </td>
					<td valign="top" style="text-align: justify">
						<center><h4>ATEN��O!</h4></center>
						<ol>
							<li>Agora � poss�vel ter mais de uma importa��o por ano. Voc� poder� importar os seus dados, colocar novas produ��es
								no Curr�culo Lattes e import�-los novamente. Apenas as novas produ��es ser�o importadas.</li>
							<li>O <a href="${ pageContext.request.contextPath }/manuais/Prodocente/importar_curriculo_lattes/completo.jsp">
								manual</a> para a importa��o do curr�culo j� est� dispon�vel.</li>
							<li>Antes de enviar o curr�culo voc� deve descompactar o arquivo baixado no site do CNPq com o 
								<a href="http://download.winzip.com/winzip111.exe">WinZip</a> ou o 
								<a href="http://www.rarlab.com/rar/wrar37b7br.exe">WinRAR</a>.
							</li>
							<li>Se o arquivo estiver corrompido, voc� deve entrar em contato com o CNPq. A equipe do SIGAA n�o � respons�vel 
								por este problema.</li>
						</ol>
					</td>
				</tr>
			</table>
		</center>

		<table class="formulario">
			<caption>Informe o arquivo</caption>
			<tr>
				<th class="obrigatorio">Ano de Refer�ncia:</th>
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
				<th class="obrigatorio">Arquivo do Curr�culo:</th>
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
	<p><strong>Os seguintes tipos de produ��o est�o sendo importados:</strong> Apresenta��es de Obras Art�sticas; Apresenta��es de Trabalhos 
		em Eventos; Arranjos Musicais; Artigos Publicados em Revistas, Jornais ou Peri�dicos; Bancas; Cap�tulos de Livros; Cartas, Mapas e 
		Similares; Composi��o Musical; Desenvolvimento de Material Did�tico, Texto em Jornal ou Revista; Livros Publicados; Maquetes; Obras 
		de Arte Visual; Organiza��o de Eventos; Partituras Musicais; Softwares; Sonoplastia; Publica��es em Eventos; Tradu��es.</p>

</f:view>

<script>

	function validarAno(campo, event){
		campo.value = campo.value.replace(/\D/g, "");
		if (campo.value.length >= 5)
			campo.value = campo.value.substring(0,4) + "-" + campo.value.substr(4);
	}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>