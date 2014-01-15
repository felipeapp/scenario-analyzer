<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 

<br/>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link> >
	<fmt:message key="titulo.listar">
		<fmt:param value="Pais"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/ensino/listarPaises" method="post" focus="codigoPaisIso">

	<div class="areaDeDados">
	
		<h2>Busca por Países</h2>
		<div class="dados">
            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder">Código País Iso</input></div>
            <div class="texto"><html:text property="codigoPaisIso" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
            <br/>
                
            <div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder"> Nome</input></div>
            <div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
            <br/>
                
            <div class="head"><input type="radio" name="tipoBusca" value="3" class="noborder"> Todos</input></div>
            <br/>
            
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div> <!-- fim do div dados -->
		
    </div> <!-- fim do div areaDeDados -->

</html:form>

<br>
<div class="infoAltRem">
    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
    : Alterar dados do País
    <html:img page="/img/delete.gif" style="overflow: visible;"/>
    : Remover País </b>
</div>
<br>

<div class="areaDeDados lista">
    <h2>Países Cadastrados</h2>
    <table>
        <thead>
        <th>Código País Iso</th>
        <th>Nome</th>

        <th colspan="2">&nbsp;</th>

        <tbody>

        <c:forEach items="${paises}" var="pais">
            <tr>
                <html:form action="/ensino/detalharPais" method="post">
                    <input type="hidden" name="acao" value="alterar" />
                    <input type="hidden" name="id" value="${pais.id}" />
                    <html:hidden name="pais" property="id"/>
                    <html:hidden property="acao" value="alterar"/>
                    <td> ${pais.codigoPaisIso} </td>
                    <td> ${pais.nome} </td>
                    <td width="15">
                        <html:image style="border: none" page="/img/alterar.gif" alt="Alterar dados do País" title="Alterar dados do País" value="Alterar"></html:image>
                    </td>
                </html:form>
                <html:form action="/ensino/detalharPais" method="post">
                    <input type="hidden" name="acao" value="remover" />
                    <input type="hidden" name="id" value="${pais.id}" />
                    <td width="15">
                        <html:image style="border: none" page="/img/delete.gif" alt="Remover País" title="Remover País" value="Remover"></html:image>
                    </td>
                </html:form>
            </tr>
        </c:forEach>  
    </table>
</div> <!-- fim do div areaDeDados lista -->

<br><br>
<center>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>
