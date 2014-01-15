<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<br>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link> 
	<fmt:message key="titulo.${acao}">
		<fmt:param value="Pais"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<br>
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="do Pais"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<style>
.areaDeDados .dados .texto {
  margin-left: 15em;
}
</style>

<html:javascript staticJavascript="false" formName="paisForm"/>
<html:form action="/ensino/${acao}Pais" method="post" focus="codigoPaisIso" onsubmit="return validatePaisForm(this);">
	<html:hidden property="id" />
	
    <div class="areaDeDados">
    
	    <c:if test="${(acao == 'criar') or (acao == 'alterar')}">
	    
	        <h2>Dados do País</h2>
	        
	        <div class="dados">
	            <div class="head">Código ISO:</div>
	            <div class="texto">
	                <html:text property="codigoPaisIso" maxlength="7" size="7" value="${pais.codigoPaisIso}" /> 
	                <span class="required">&nbsp;</span><br/>
					<html:errors property="codigoPaisIso" />
	            </div>
	            <br/>
	            
	            <div class="head">Nome:</div>
	            <div class="texto">
					<html:text property="nome" maxlength="100" size="55" value="${pais.nome}" /> 
					<span class="required">&nbsp;</span><br/>
					<html:errors property="nome" />
	            </div>
	            <br/>
	            
	        </div>
	        
		</c:if>	<!-- fim do c:if {(acao == 'criar') or (acao == 'alterar')} -->        
	
	    <c:if test="${acao == 'remover'}">
	   
	        <h2>Dados do País</h2>
	        <div class="dados">
	            <div class="head">Codigo ISO:</div>
	            <div class="texto">${pais.codigoPaisIso}</div>
	            <br/>
	
	            <div class="head">Nome:</div> 
	            <div class="texto">${pais.nome}</div>
	            <br/>
	
	        </div>
	    </c:if> <!-- fim do c:if acao == 'remover' -->

        <div class="botoes">
            <html:submit><fmt:message key="botao.${acao}" /></html:submit>
            <c:choose>
                	<c:when test="${acao == 'criar'}">
		                <html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuEnsino.do?tipoEnsino=${tipoEnsino}'"><fmt:message key="botao.cancelar" /></html:button>
                	</c:when>
                	<c:otherwise>
	                	<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/ensino/listarPaises.do'"><fmt:message key="botao.cancelar" /></html:button>
                	</c:otherwise>
             </c:choose>
        </div> <!-- fim do div botoes -->
	
    </div>	<!-- fim do div areaDeDados -->

</html:form>	

<center>	
	<c:if test="${(acao == 'criar') or (acao == 'alterar')}">
		<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</c:if>
	
	<br><br>

	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>
