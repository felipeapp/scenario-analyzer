<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<br>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link> >
	<fmt:message key="titulo.${acao}">
		<fmt:param value="Cidade"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<br>
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="da Cidade"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<!-- html:javascript formName="cidadeForm" /-->

<html:form action="/ensino/${acao}Cidade" method="post" focus="codigo" onsubmit="return validateCidadeForm(this);">
	<html:hidden property="id" />
    <div class="areaDeDados">
	
    <c:if test="${acao == 'alterar' || acao == 'criar'}">
        <h2>Dados da Cidade</h2>
        
        <div class="dados">
            <div class="head">Código:</div>
            <div class="texto">
                <html:text name="cidade" property="codigo" maxlength="7" size="7" value="${cidade.codigo}" />
                <span class="required">&nbsp;</span><br/>
                <html:errors property="codigo" />
            </div>
            <br/>
            
            <div class="head">Nome:</div>
            <div class="texto">
                <html:text name="cidade" property="nome" maxlength="100" size="45" value="${cidade.nome}" />
                <span class="required">&nbsp;</span><br/>
                <html:errors property="nome" />
            </div>
            <br/>
            
            <div class="head">Unidade Federativa:</div>
            <div class="texto">
                <html:select property="unidadeFederativa" value="${cidade.unidadeFederativa.id}" >
                <html:option value="">> Opções</html:option>
                <html:options collection="unidadesFederativas" property="id" labelProperty="nome"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="unidadeFederativa" />
            </div>
            <br/>
            
        </div>
        
    </c:if>
	
    <c:if test="${acao == 'remover'}">
        <h2>Dados da Cidade</h2>
        <div class="dados">
            <div class="head">Codigo:</div>
            <div class="texto">${cidade.codigo}</div>
            <br/>

            <div class="head">Nome:</div> 
            <div class="texto">${cidade.nome}</div>
            <br/>

            <div class="head">Unidade Federativa:</div> 
            <div class="texto">${cidade.unidadeFederativa.nome}</div>
            <br/>
        </div>
    </c:if>
       <div class="botoes">
           <html:submit><fmt:message key="botao.${acao}" /></html:submit>
           <c:choose>
            <c:when test="${acao == 'criar'}">
                    <html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuEnsino.do?tipoEnsino=${tipoEnsino}'"><fmt:message key="botao.cancelar" /></html:button>
            </c:when>
            <c:otherwise>
                    <html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/ensino/listarCidades.do'"><fmt:message key="botao.cancelar" /></html:button>
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