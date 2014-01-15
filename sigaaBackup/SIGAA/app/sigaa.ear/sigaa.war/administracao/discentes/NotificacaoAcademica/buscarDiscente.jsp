<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Notifica��o Individual </h2>

	<div class="descricaoOperacao">
	<b>Caro usu�rio,</b> 
	<br/><br/>
	Esta opera��o permite que seja criada e enviada uma notifica��o acad�mica para um �nico discente. A notifica��o ser� visualizada quando o 
	discente acessar o sistema. Se necess�rio, � poss�vel solicitar a marca��o da ci�ncia pelo discente, impossibilitando seu acesso ao restante 
	do sistema at� que seja confirmada a leitura da notifica��o.  
	<br/><br/>
	A opera��o � dividida em tr�s etapas:
	<br/><br/>
	<p>- Escolha do discente que ser� notificado. </p>
	<p>- Cadastro da notifica��o acad�mica. </p>
	<p>- Confirma��o do envio da notifica��o. </p>
	</div>

<f:view>

<h:form id="notificacao">

<c:set var="paramNivel" value="ufrn"/>
<c:if test="${!acesso.administradorSistema}">
	<c:set var="paramNivel" value="${sessionScope.nivel}"/>
</c:if>

<table class="formulario" width="70%">
<caption>Informe os Dados para a Notifica��o </caption>
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
		<h:commandButton value="Avan�ar >>" action="#{notificacaoAcademica.preCadastrarIndividual}" />
	</td>
</tfoot>

</table>
</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
