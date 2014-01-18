<%@include file="/include/head.jsp"%>

<br>
<br>
<br>
<br>
<br>
<br>


<table width="80%" align="center" bgcolor="#000080">

<tr>
<td class="erro" align="center">
<br><br>
<html:img src="img_css/erro_autorizacao.gif"/><br>
<font color="white">
ACESSO BLOQUEADO<br>
OPERAÇÃO NÃO PERMITA FORA DA REDE DA ${ configSistema['siglaInstituicao'] }
</font>
</td>
</tr>
<tr>
<td>
<br>
<table width="80%" align="center">
<tr>
<td>
<b>
<font color="white">
APENAS OPERAÇÕES DE CONSULTAS PODEM SER REALIZADAS A PARTIR DE COMPUTADORES
EXTERNOS A REDE DA INSTITUIÇÃO.
</font>
</b>
</td>
</tr>
</table>


<br>
<br>
<td>
</tr>
</table>


<br>
<br>
<center>
<a href="javascript:history.go(-1)"> Voltar </a>
</center>
<br>
<br>



<%@include file="/include/tail.jsp"%>