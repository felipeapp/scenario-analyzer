<%@page import="br.ufrn.sipac.arq.struts.ConstantesAction"%>
<%@include file="/include/head.jsp"%>

<span class="title">
  Enviar sugest�o
</span>
<hr>

<div class="texto">
<logic:notEmpty name="sugestaoForm" property="sugestao.cause">
  Descreva detalhadamente o que estava fazendo no momento em que a mensagem de
  erro foi exibida. Procure ser o mais claro poss�vel. Dessa forma, ser� poss�vel
  detectar e corrigir mais rapidamente os erros informados.
</logic:notEmpty>
<logic:empty name="sugestaoForm" property="sugestao.cause">
  Ao enviar d�vidas, sugest�es e notifica��es de erros, seja o mais claro poss�vel. 
  Procure detalhar o m�ximo poss�vel para que sua solicita��o seja respondida mais
  rapidamente.
</logic:empty>
</div>

<%@include file="/include/erros.jsp"%>

<html:form action="enviarSugestao" focus="sugestao.tipo">
 <table align="center" class="formulario" width="640">
   <caption class="listagem"> Enviar Sugest�o </caption>
   <tr><td><table width="90%" align="center">
   <tr>
     <td style="font-weight: bold;"> Tipo </td>
     <td>
      <html:select property="sugestao.tipo">
        <html:options collection="tipos" property="id" labelProperty="denominacao"/>
      </html:select>
     </td>
   </tr>
   <tr>
     <td style="font-weight: bold;"> Subsistema </td>
     <td>
      <html:select property="sugestao.subSistema.id" style="width: 250px">
        <html:option value="0">N�o sei / Todos os subsistemas</html:option>
        <html:options collection="subsistemas" property="id" labelProperty="nome"/>
      </html:select>
     </td>
   </tr>
   
   <tr>
     <td style="font-weight: bold;"> Ramal </td>
     <td>
      <html:text property="sugestao.ramal" maxlength="<%= getRange("ramal") %>" size="<%= getRange("ramal") %>"/>
     </td>
   </tr>
   
   <tr>
     <td style="font-weight: bold;"> Email </td>
     <td>
      <html:text property="sugestao.email" maxlength="50" style="width: 250"/>
     </td>
   </tr>
   
   <tr>
     <td style="font-weight: bold;"> Descri��o </td>
     <td>
      <sipac:textarea cols="100" rows="10" maxlength="1000" property="sugestao.descricao" style="width: 500"/>
     </td>
   </tr>
   
   
   
   <tr height="30" valign="middle">
    <html:hidden property="acao" value="<%= String.valueOf(ConstantesAction.INSERIR) %>"/>
    
    <%-- Usado apenas em reportar erros --%>
    <html:hidden property="sugestao.excecao"/>
    <html:hidden property="sugestao.cause"/>
    <html:hidden property="sugestao.stack"/>
    <html:hidden property="sugestao.stackCause"/>
    <html:hidden property="sugestao.parametros"/>
    <html:hidden property="sugestao.url" style="width: 330px"/>
    
    <td colspan="2" align="center">
     <html:submit value="Enviar"/>
     <input type="button" onclick="javascript: history.back();" value="Cancelar">
    </td>
   </tr>
   </table></td></tr>
  </table>
</html:form>

<div align="center" style="margin-top: 30"> <sipac:linkSubsistema/> </div>

<%@include file="/include/tail.jsp"%>