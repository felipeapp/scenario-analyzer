<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/porta_arquivos.css" type="text/css" />

<f:view>

	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>

	<h2><ufrn:subSistema /> &gt; Importar Discente </h2>

	<h:form enctype="multipart/form-data">
	
		<center>
			<table class="ajudaPortaArquivos" width="100%">
				<tr>
					<td width="40" align="center" valign="middle"><html:img page="/img/help.png" width="32" height="32" /> </td>
					<td valign="top" style="text-align: justify">
						<center><h4>ATENÇÃO!</h4></center>
						<ol>
							<li>O Layout do aquivo deve ser o seguinte:</li>
							<li>Nome</li>
							<li>Email</li>
							<li>Telefone</li>
							<li>Celular</li>
							<li>Identidade</li>
							<li>CPF</li>
							<li>Curso</li>
							<li>Processo Seletivo</li>
							<li>Turma Entrada Lato</li>
						</ol>
					</td>
				</tr>
			</table>
		</center>

		<table class="formulario">
			<caption>Informe o arquivo</caption>
			<tr>
				<th class="obrigatorio">Arquivo do Currículo:</th>
				<td><t:inputFileUpload size="30" value="#{ importacaoDiscenteMBean.arquivo }" id="arquivoCurriculo" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Enviar" action="#{ importacaoDiscenteMBean.importar }" id="enviar" />
					<h:commandButton value="Cancelar" action="#{ importacaoDiscenteMBean.cancelar }" id="cancelar" />
				</td>
			</tr>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>