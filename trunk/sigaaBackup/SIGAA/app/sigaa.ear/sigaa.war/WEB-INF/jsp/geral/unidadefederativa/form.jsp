<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
	<fmt:message key="titulo.${acao}">
		<fmt:param value="Unidade Federativa"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<br>
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="da Unidade Federativa"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<html:javascript staticJavascript="false" formName="unidadeFederativaForm"/>
<html:form action="/geral/${acao}UnidadeFederativa" method="post" focus="codigo" onsubmit="return validateUnidadeFederativaForm(this);">
	<html:hidden property="id" />
	
	<div class="areaDeDados">
	<c:if test="${acao == 'alterar' || acao == 'criar'}">
		<h2>Dados da Unidade Federativa</h2>
		<div class="dados">
			<div class="head">Nome:</div>
			<div class="texto">
				<html:text property="nome" maxlength="128" size="55" value="${unidadeFederativa.nome}" />
				<span class="required">&nbsp;</span><br/>
				<html:errors property="nome" />
			</div>
			<br/>

			<div class="head">Sigla:</div>
			<div class="texto">
				<html:text property="sigla" maxlength="2" size="5" value="${unidadeFederativa.sigla}" />
				<span class="required">&nbsp;</span><br/>
				<html:errors property="sigla" />
			</div>
			<br/>
		</div>
	</c:if>
	
	<c:if test="${acao == 'remover'}">
		<h2>Dados da Unidade Federativa</h2>
		<div class="dados">
			<div class="head">Nome:</div>
			<div class="texto">${unidadeFederativa.nome}</div>
			<br/>
			
			<div class="head">Sigla:</div>
			<div class="texto">${unidadeFederativa.sigla}</div>
			<br/>
		</div>
	</c:if>
	
		<div class="botoes">
			<html:submit><fmt:message key="botao.${acao}" /></html:submit>
			<c:choose>
			 <c:when test="${acao == 'criar'}">
				<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuEnsino.do'"><fmt:message key="botao.cancelar" /></html:button>
			 </c:when>
			 <c:otherwise>
			 	<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/geral/listarUnidadesFederativas.do'"><fmt:message key="botao.cancelar" /></html:button>
			 </c:otherwise>
			</c:choose>
		</div>
	</div>
</html:form>

<center>
	<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>

<br><br>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>

<script language="javascript" src="${pageContext.request.contextPath}/javascript/forms.js">
</script>