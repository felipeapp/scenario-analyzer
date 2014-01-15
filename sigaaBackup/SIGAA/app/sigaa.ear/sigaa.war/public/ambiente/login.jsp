<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
#ambiente {
	font-size: x-large;
	font-weight: bold;
	text-align: center;
	padding: 10px 0;
}
#atencao {
	text-align: center;
	margin: 10px;
	font-size: small;
}
#atencao strong {
	color: red;
	text-transform: uppercase;
	display: block;
}
</style>

<f:view>

<div id="ambiente">
${ configSistema['siglaInstituicao'] } - Ambiente de ${ logonAmbienteMBean.ambienteAtual.nome }
</div>

<div id="atencao">
<strong>Atenção!</strong> 
Para ter acesso a este ambiente é necessário autenticar-se.<br/> Após a autenticação entre com o login e senha desejado para a simulação das operações.
</div>


<br>      
<div class="logon" style="width:50%; margin: 0 auto;">
<h3>Entrar no Sistema</h3>

<h:form prependId="false">

	<input type="hidden" name="width" id="width" />
	<input type="hidden" name="height" id="height" />
	<h:inputHidden id="url" value="#{logonAmbienteMBean.url}" />

	<script>
		document.getElementById('width').value = screen.width;
		document.getElementById('height').value = screen.height;
	</script>

<table align="center" width="100%" cellspacing="0" cellpadding="3">
<tbody>
	<tr>
		<th width="35%">  Usuário: </th>
		<td> <h:inputText id="login" value="#{logonAmbienteMBean.login}"/>  </td>
	</tr>
	<tr>
		<th> Senha: </th>
		<td>  <h:inputSecret id="senha" value="#{logonAmbienteMBean.senha}"/>  </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton action="#{logonAmbienteMBean.login}" value="Entrar"/>
		</td>
	</tr>
</tfoot>
</table>
</h:form>

</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<c:if test="${ not empty param.url }">
<script type="text/javascript">
$('url').value = '${ param.url }';
</script>
</c:if>