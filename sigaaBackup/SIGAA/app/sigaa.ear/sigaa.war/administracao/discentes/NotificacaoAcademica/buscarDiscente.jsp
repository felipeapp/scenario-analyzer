<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Notificação Individual </h2>

	<div class="descricaoOperacao">
	<b>Caro usuário,</b> 
	<br/><br/>
	Esta operação permite que seja criada e enviada uma notificação acadêmica para um único discente. A notificação será visualizada quando o 
	discente acessar o sistema. Se necessário, é possível solicitar a marcação da ciência pelo discente, impossibilitando seu acesso ao restante 
	do sistema até que seja confirmada a leitura da notificação.  
	<br/><br/>
	A operação é dividida em três etapas:
	<br/><br/>
	<p>- Escolha do discente que será notificado. </p>
	<p>- Cadastro da notificação acadêmica. </p>
	<p>- Confirmação do envio da notificação. </p>
	</div>

<f:view>

<h:form id="notificacao">

<c:set var="paramNivel" value="ufrn"/>
<c:if test="${!acesso.administradorSistema}">
	<c:set var="paramNivel" value="${sessionScope.nivel}"/>
</c:if>

<table class="formulario" width="70%">
<caption>Informe os Dados para a Notificação </caption>
<tbody>
<tr>
	<th class="obrigatorio"> Discente: </th>
	<td> <h:inputHidden id="idDiscente" value="#{ notificacaoAcademica.discente.id }"/>
		 <h:inputText id="nomeDiscente" value="#{ notificacaoAcademica.discente.pessoa.nome }" size="60"/>

		<ajax:autocomplete source="notificacao:nomeDiscente" target="notificacao:idDiscente"
			baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
			indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=${paramNivel}" 
			parser="new ResponseXmlToHtmlListParser()" />

		<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
	</td>
</tr>

</tbody>

<tfoot>
<tr>
	<td colspan="2">
		<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{notificacaoAcademica.cancelarIndividual}" /> 
		<h:commandButton value="Avançar >>" action="#{notificacaoAcademica.preCadastrarIndividual}" />
	</td>
</tfoot>

</table>
</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
