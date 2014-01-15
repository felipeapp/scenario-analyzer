<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 

<br/>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link> >
	<fmt:message key="titulo.listar">
		<fmt:param value="Cidade"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/ensino/listarCidades" method="post" focus="codigo">
	<div class="areaDeDados">
		<h2>Busca por Cidade</h2>
		<div class="dados">
            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder">Código</input></div>
            <div class="texto"><html:text property="codigo" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
            <br/>
                
            <div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder"> Nome</input></div>
            <div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
            <br/>
                
            <div class="head"><input type="radio" name="tipoBusca" value="3" class="noborder"> Todos</input></div>
            <br/>
            
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
    </div>
</html:form>

<br/>
<div class="infoAltRem">
    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
    : Alterar dados da Cidade
    <html:img page="/img/delete.gif" style="overflow: visible;"/>
    : Remover Cidade </b>
</div>
<br>

<div class="areaDeDados lista">
    <h2>Cidades Cadastradas</h2>
    <table>
        <thead>
        <th>Código</th>
        <th>Nome</th>

        <th colspan="2">&nbsp;</th>

        <tbody>

        <c:forEach items="${cidades}" var="cidade">
            <tr>
                <html:form action="/ensino/detalharCidade" method="post">
                    <input type="hidden" name="acao" value="alterar" />
                    <input type="hidden" name="id" value="${cidade.id}" />
                    <html:hidden name="cidade" property="id"/>
                    <html:hidden property="acao" value="alterar"/>
                    <td> ${cidade.codigo} </td>
                    <td> ${cidade.nome} </td>
                    <td width="15">
                        <html:image style="border: none" page="/img/alterar.gif" alt="Alterar dados da Cidade" title="Alterar dados da Cidade" value="Alterar"></html:image>
                    </td>
                </html:form>
                <html:form action="/ensino/detalharCidade" method="post">
                    <input type="hidden" name="acao" value="remover" />
                    <input type="hidden" name="id" value="${cidade.id}" />
                    <td width="15">
                        <html:image style="border: none" page="/img/delete.gif" alt="Remover Cidade" title="Remover Cidade" value="Remover"></html:image>
                    </td>
                </html:form>
            </tr>
        </c:forEach>  
    </table>
</div>
<br><br>
<center>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>
