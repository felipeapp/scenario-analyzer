<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table tbody tr th {
		font-weight: bold;
	}
</style>

<h2><ufrn:subSistema /> > Homologar 
<c:choose>
	<c:when test="${homologacao.banca.dadosDefesa.discente.stricto}">Diploma</c:when>
	<c:otherwise>Trabalho Final</c:otherwise>
</c:choose></h2>

<f:view>
<a4j:keepAlive beanName="homologacaoTrabalhoFinal"></a4j:keepAlive>

<h:form enctype="multipart/form-data" prependId="false">

<h:messages/>
<c:set var="homologacao" value="#{ homologacaoTrabalhoFinal.obj }"/>

<table class="formulario" width="80%">
	<caption>Dados da Homologação</caption>
	<tbody>
	<tr>
		<th width="27%">Discente: </th>
		<td>${ homologacao.banca.dadosDefesa.discente.matriculaNome }</td>
	</tr>
	<tr>
		<th>Orientador: </th>
		<td>${ homologacao.banca.dadosDefesa.discente.orientacao.descricaoOrientador }</td>
	</tr>
	<tr>
		<th>Linha de Pesquisa: </th>
		<td>${ homologacao.banca.dadosDefesa.discente.linha.denominacao }</td>
	</tr>
	<tr>
		<th>Área de Concentração: </th>
		<td>${ homologacao.banca.dadosDefesa.discente.area.denominacao }</td>
	</tr>
	<tr>
		<th>Data da Defesa: </th>
		<td><fmt:formatDate value="${ homologacao.banca.data }"/></td>
	</tr>
	<tr>
		<th>Grande Área: </th>
		<td>${ homologacao.banca.grandeArea.nome }</td>
	</tr>
	<tr>
		<th>Área: </th>
		<td>${ homologacao.banca.area.nome }</td>
	</tr>
	<tr>
		<th>Sub-Área: </th>
		<td>${ homologacao.banca.subArea.nome }</td>
	</tr>
	<tr>
		<th>Especialidade: </th>
		<td>${ homologacao.banca.especialidade.nome }</td>
	</tr>
	<tr>
		<th>Banca: </th>
		<td>
			<c:forEach items="${ homologacao.banca.membrosBanca }" var="mb">
				${ mb.membroIdentificacao }
				<br/>
			</c:forEach>
		</td>
	</tr>
	<tr><th>Local: </th><td>${ homologacao.banca.local }</td></tr>
	<tr><td colspan="2">
		<table class="subFormulario" width="100%">
			<caption>Dados do Trabalho</caption>
			<tr><th width="26%">Título: </th><td colspan="2">${ homologacaoTrabalhoFinal.obj.banca.dadosDefesa.titulo }</td></tr>
			<tr><th>Resumo: </th><td colspan="2">${ homologacaoTrabalhoFinal.obj.banca.dadosDefesa.resumo }</td></tr>
			<tr><th>Páginas: </th><td colspan="2">${ homologacaoTrabalhoFinal.obj.banca.dadosDefesa.paginas }</td></tr>
			<tr>
				<th  class="required">Arquivo:</th>
				<td colspan="2"><t:inputFileUpload value="#{homologacaoTrabalhoFinal.arquivo}" size="40" id="arquivo" /></td>
			</tr>
			<tr>
				<th >Link do Arquivo(BDTD):</th>
				<td width="200px">
					<h:inputText value="#{homologacaoTrabalhoFinal.obj.banca.dadosDefesa.linkArquivo}" maxlength="400" size="60" id="link"  />
				</td>
				<td align="left">
					<ufrn:help img="/img/ajuda.gif">Link de acesso a defesa no BDTD da UFRN.</ufrn:help>
				</td>
			</tr>
		</table>
	</td></tr>
	<c:if test="${homologacaoTrabalhoFinal.exigirDadosProcessoHomologacao}">
		<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados do Processo de Homologação</caption>
					<tr><th width="25%" class="required">Número do Processo: </th>
					<td><h:inputText value="#{ homologacaoTrabalhoFinal.numProcesso }" id="numero_processo" size="6" maxlength="6" onkeyup="return(formatarInteiro(this))"/></td></tr>
					<tr><th class="required">Ano do Processo: </th>
					<td><h:inputText value="#{ homologacaoTrabalhoFinal.anoProcesso }" size="4" maxlength="4" id="ano_processo" onkeyup="return(formatarInteiro(this))"/></td></tr>
				</table>
			</td>
		</tr>
	</c:if>
	</tbody>
	<tfoot>
		<tr><td colspan="2">
			<h:inputHidden value="#{ homologacaoTrabalhoFinal.obj.banca.id }" id="hiddenIdBanca"/>
			<h:commandButton value="#{homologacaoTrabalhoFinal.confirmButton}" action="#{ homologacaoTrabalhoFinal.cadastrar }" id="btnCadastrar"/> 
			<h:commandButton value="Cancelar" action="#{ homologacaoTrabalhoFinal.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
		</td></tr>
	</tfoot>
</table>
</h:form>

<br/> <center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>