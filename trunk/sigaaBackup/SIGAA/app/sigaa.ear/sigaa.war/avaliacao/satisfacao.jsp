
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<f:view>

<h:form>

<h2>Questionário de Satisfação</h2>

<h3>AVALIE O QUESTIONÁRIO ON-LINE!</h3>

<table class="formulario">
<caption>Responda as perguntas abaixo</caption>
<tr>
<td>Teve alguma dificuldade para preencher o questionário?</td>
<td>
<h:selectOneRadio value="#{ questionarioSatisfacaoMBean.obj.teveDificuldade }">
<f:selectItems value="#{ questionarioSatisfacaoMBean.simNao }"/>
</h:selectOneRadio>
</td>
</tr>
<tr>
<td colspan="2">Em caso afirmativo, qual a principal dificuldade?</td></tr>
<tr>
<td colspan="2"><h:inputTextarea value="#{ questionarioSatisfacaoMBean.obj.principalDificuldade }" rows="2" cols="100"/></td>
</tr>
<tr>
<td>Gostaria de elaborar alguma questão que não tenha sido contemplada nos itens deste questionário?</td>
<td><h:selectOneRadio value="#{ questionarioSatisfacaoMBean.obj.criarQuestao }">
<f:selectItems value="#{ questionarioSatisfacaoMBean.simNao }"/>
</h:selectOneRadio></td>
</tr>
<tr>
<td colspan="2">
Em caso afirmativo, qual a questão?</td></tr>
<tr>
<td colspan="2"><h:inputTextarea value="#{ questionarioSatisfacaoMBean.obj.qualQuestao }" rows="2" cols="100"/></td>
</tr>
</table>
<br/>
<center>
<h:commandButton value="Gravar Opinião" action="#{ questionarioSatisfacaoMBean.cadastrar }"/>
</center>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

