<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<a4j:keepAlive beanName="questionarioTurma" />
	<a4j:keepAlive beanName="categoriaPerguntaQuestionarioTurma" />


	<%@include file="/ava/menu.jsp" %>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="title">  <ufrn:subSistema/> &gt; Questionário &gt; Adicionar Pergunta </h2>
<c:set var="alterarPergunta" value="false"/>

<%@include file="/ava/QuestionarioTurma/pergunta.jsp" %>

<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	<br><br>
</center>

</f:view>


<%@include file="/ava/rodape.jsp" %>