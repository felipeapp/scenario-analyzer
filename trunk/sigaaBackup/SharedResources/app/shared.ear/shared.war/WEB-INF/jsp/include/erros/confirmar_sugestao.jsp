<%@page import="br.ufrn.sipac.arq.struts.ConstantesAction"%>
<%@include file="/include/head.jsp"%>

<span class="title">
  Enviar sugestão
</span>
<span class="subtitle">
  Confirma dados?
</span>
<hr>
<br>

<html:form action="enviarSugestao" focus="sugestao.tipo">
 <table align="center" class="formulario" width="500">
   <caption class="listagem"> Enviar Sugestão </caption>
   <tr><td><table width="90%" align="center">
   <tr>
     <td style="font-weight: bold;" width="90"> Tipo: </td>
     <td colspan="2"> ${ tipo.denominacao } </td>
   </tr>
   <tr>
     <td style="font-weight: bold;"> Subsistema: </td>
     <td colspan="2"> ${ sugestaoForm.sugestao.subSistema.id == 0? "Todos/Não se aplica": sugestaoForm.sugestao.subSistema.nome } </td>
   </tr>
   
   <tr>
     <td style="font-weight: bold;"> Ramal: </td>
     <td colspan="2"> ${ sugestaoForm.sugestao.ramal} </td>
   </tr>
   
   <tr>
     <td style="font-weight: bold;"> Email: </td>
     <td colspan="2"> ${ sugestaoForm.sugestao.email} </td>
   </tr>
   
   <tr>
     <td style="font-weight: bold;"> Descrição: </td>
     <td colspan="2">
      <sipac:format name="sugestaoForm" property="sugestao.descricao" type="texto"/>
     </td>
   </tr>
   
   
   
   <tr height="30" valign="middle">
    <td colspan="2"><table align="center" valign="middle"><tr>
    <html:hidden property="acao" value="<%= String.valueOf(ConstantesAction.CONFIRMAR) %>"/>
    <html:hidden property="sugestao.url"/>
    <html:hidden property="sugestao.descricao"/>
    <html:hidden property="sugestao.tipo"/>
    <html:hidden property="sugestao.subSistema.id"/>
    <html:hidden property="sugestao.email"/>
    <html:hidden property="sugestao.ramal"/>
    
    <%-- Usado apenas em reportar erros --%>
    <html:hidden property="sugestao.excecao"/>
    <html:hidden property="sugestao.cause"/>
    <html:hidden property="sugestao.stack"/>
    <html:hidden property="sugestao.stackCause"/>
    <html:hidden property="sugestao.parametros"/>
    
    <td align="right">
     <html:submit value="Confirmar"/>
    </td>
    </html:form>
    
    <html:form action="populaSugestao">
      <html:hidden property="sugestao.url"/>
      <html:hidden property="sugestao.descricao"/>
      <html:hidden property="sugestao.tipo"/>
      <html:hidden property="sugestao.subSistema.id"/>
      <html:hidden property="sugestao.email"/>
      <html:hidden property="sugestao.ramal"/>
      
      <%-- Usado apenas em reportar erros --%>
      <html:hidden property="sugestao.excecao"/>
      <html:hidden property="sugestao.cause"/>
      <html:hidden property="sugestao.stack"/>
      <html:hidden property="sugestao.stackCause"/>
      <html:hidden property="sugestao.parametros"/>
      <td align="center">
       <html:submit value="<< Voltar"/>
      </td>
    </html:form>
    
    <td align="left">
     <sipac:button action="cancelar "value="Cancelar"/>
    </td>
    </tr></table></td>
   </tr>
   </table></td></tr>
  </table>

<div align="center" style="margin-top: 30"> <sipac:linkSubsistema/> </div>

<%@include file="/include/tail.jsp"%>