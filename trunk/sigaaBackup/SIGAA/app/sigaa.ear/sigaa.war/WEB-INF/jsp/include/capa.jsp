<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/struts-logic" prefix="logic"  %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@page contentType="text/html; charset=ISO-8859-1" %>

<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
<br><br><br><br><br><br><br><br><br><br>

	<c:set var="bean" value="${ consolidarTurma }"/>
	<c:if test="${ consolidarTurma.turma.id == 0 }">
		<c:set var="bean" value="${ relatorioConsolidacao }"/>
	</c:if>
	
<html>

<body>

<div style="text-align: center;"><big style="font-weight: bold;"><big style="font-family: Verdana;">
 
<!-- <h1>Diário de Turma</h1> <br> -->
 
<span style="font-family: Verdana;"><h1>Diário de Turma</h1></span><br>
<span style="font-family: Verdana;"></span><br>
<span style="font-family: Verdana;"></span><br>
<span style="font-family: Verdana;"><h1><span style="font-weight: bold;">Secretaria de Educação a Distância</h1></span><br>
<br>

</span>

<table style="text-align: left; width: 100%;" border="0" cellpadding="2" cellspacing="2">
  <tbody>
    <tr>
      <td></td>
      <td style="font-weight: bold; white-space: nowrap;"><big>Centro: </big></td><br>
      <td style="font-weight: bold; white-space: nowrap;"><big> ${ bean.turma.descricaoCentro } </big></td><br>
      <td><br></td><br>
    </tr>
    <tr>
      <td></td>
      <td style="font-weight: bold;"><big>Departamento: </big></td>
      <td style="white-space: nowrap; font-weight: bold;"><big>${ bean.turma.descricaoDepartamento }</big></td>
      <td></td>
    </tr>
    <tr>
      <td></td>
      <td style="font-weight: bold;"><big>Disciplina: </big></td>
      <td style="font-weight: bold; white-space: nowrap;"><big> ${ bean.turma.descricaoDisciplina } </big></td>
      <td style="font-weight: bold; white-space: nowrap;"><big> Ano/Semestre: ${ bean.turma.anoPeriodo } </big></td>
      <td></td>
    </tr>
    <tr>
      <td></td>
      <td style="font-weight: bold;"><big>Carga Hor&aacute;ria: </big> </td>
      <td style="font-weight: bold; white-space: nowrap;"><big>${ bean.turma.chTotalTurma } hs</big></td>
      <td></td>
    </tr>
    <tr>
      <td></td>
      <td style="font-weight: bold;"><big>P&oacute;lo: </big></td>
      
      <td style="font-weight: bold;"><big>${ bean.turma.polo.descricao }</big></td>
      
      <td style="white-space: nowrap; font-weight: bold;"><big>Turma: ${ bean.turma.codigo } </big></td>
    </tr>

    
    <tr>
      <td></td>
      <td><b><big>Matrícula:</big></b></td>
      <td><b><big>Docente(s):</big></b></td>
    </tr>
    
    <c:forEach var="item" items="${ bean.turma.docentesMatriculaComNome }">
	    <tr>
	    	<td></td>
	      		<td><b><big>${ item.siape }</big></b></td>
	      		<td><b><big>${ item.nome }</big></b></td>
	    </tr>
	    <tr>
	 	     <td></td>
	    	 <td></td>
	      	<td></td>
	    </tr>
    </c:forEach>
    
  </tbody>
</table>
<!--  <span style="font-family: Verdana;"><big></big></span></div> -->
</body>


</html>


<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
<br><br><br><br><br><br><br><br><br><br><br><br>

