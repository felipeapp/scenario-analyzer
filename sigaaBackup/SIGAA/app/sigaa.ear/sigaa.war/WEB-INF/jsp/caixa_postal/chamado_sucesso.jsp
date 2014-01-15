<%--
JSP Exibida assim que o chamado é aberto
--%>
<%@page import="br.ufrn.sipac.arq.struts.ConstantesAction"%> 
<%@include file="../include/head.jsp"%><P align="center">  &nbsp;



<SPAN class="title">CHAMADO ABERTO COM SUCESSO
<hr>
Recebemos sua solicitação e responderemos em breve na sua Caixa Postal.<br>
Chamado Número ${mensagem.numChamado} <br><br>

<center>
<sipac:link action="menuUnidade" value="Menu Principal"/></center>

<%@include file="../include/tail.jsp"%>