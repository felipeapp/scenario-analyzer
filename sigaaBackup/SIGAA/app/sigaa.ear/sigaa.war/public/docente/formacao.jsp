<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<div id="formacao-academica">
	<h4>Formação Acadêmica</h4>

	<c:if test="${not empty  portal.formacoes}">
	<dl>
		<c:forEach var="formacao" items="${portal.formacoes}">
		<dt>
			<span class="ano"><ufrn:format type="ano" name="formacao" property="dataFim"/></span> - ${formacao.formacao.descricao}
		</dt>
		<dd>
			${formacao.grau} <br />
			${formacao.entidade} <br />
		</dd>
		</c:forEach>
	</dl>
	</c:if>

	<c:if test="${empty portal.formacoes}">
		<p class="vazio">
			Formação acadêmica não cadastrada
		</p>
	</c:if>
</div>

</f:view>
<%@include file="/public/include/rodape.jsp" %>
