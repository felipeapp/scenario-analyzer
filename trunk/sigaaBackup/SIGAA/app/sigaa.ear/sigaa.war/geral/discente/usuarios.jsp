<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema/> &gt; Usuários do Discente </h2>

<f:view>

<h:form>

<table class="formulario" width="50%">
<caption>Usuários Encontrados</caption>
<c:forEach var="usr" items="#{ alterarDadosUsuarioAlunoMBean.usuariosDoDiscente }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${ usr.login }</td><td align="right">
<h:commandLink action="#{ alterarDadosUsuarioAlunoMBean.selecionaUsuario }">
	<h:graphicImage value="/img/seta.gif"/>
	<f:param name="idUsuario" value="#{ usr.id }"/>
</h:commandLink>
</td></tr>
</c:forEach>
<tfoot>
</tfoot>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>