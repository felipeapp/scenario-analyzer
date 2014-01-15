<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<br>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
	<fmt:message key="titulo.${acao}">
		<fmt:param value="Turno"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<br>
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="do Turno"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<html:javascript staticJavascript="false" formName="turnoForm"/>

<html:form action="/geral/${acao}Turno" method="post" focus="codigo" onsubmit="return validateTurnoForm(this);">
	<html:hidden property="id" />
	
	<div class="areaDeDados">
		<c:if test="${acao == 'alterar' || acao == 'criar'}">
			<h2>Dados do Turno</h2>
			<div class="dados">
				<div class="head">Código:</div>
				<div class="texto">
					<html:text name="turno" property="codigo" maxlength="7" size="7" />
					<span class="required">&nbsp;</span><br/>
					<html:errors property="codigo" />
				</div>
				<br/>

				<div class="head">Sigla:</div>
				<div class="texto">
					<html:text name="turno" property="sigla" maxlength="7" size="7" />
					<span class="required">&nbsp;</span><br/>
					<html:errors property="sigla" />
				</div>
				<br/>
				
				<div class="head">Descrição:</div>
				<div class="texto">
					<html:text name="turno" property="descricao" maxlength="30" size="45" />
					<span class="required">&nbsp;</span><br/>
					<html:errors property="descricao" />
				</div>
				<br/>
			</div>
		</c:if>
	
		<c:if test="${acao == 'remover'}">
			<h2>Dados do Turno</h2>
			<div class="dados">
				<div class="head">Código:</div>
				<div class="texto">${turno.codigo}</div>
				<br/>
				<div class="head">Sigla:</div> 
				<div class="texto">${turno.sigla}</div>
				<br/>
				<div class="head">Descrição:</div> 
				<div class="texto">${turno.descricao}</div>
	        </div>
	    </c:if>
		<div class="botoes">
			<html:submit><fmt:message key="botao.${acao}" /></html:submit>
			<c:choose>
				<c:when test="${acao == 'criar'}">
					<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuEnsino.do?tipoEnsino=${tipoEnsino}'"><fmt:message key="botao.cancelar" /></html:button>
				</c:when>
				<c:otherwise>
					<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/geral/listarTurnos.do'"><fmt:message key="botao.cancelar" /></html:button>
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