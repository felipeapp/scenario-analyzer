

<%@ taglib uri="/tags/struts-html" prefix="html"  %>

<html:form action="renomearArquivo" styleId="form-renomear">
	<table cellpadding="3" class="form-porta-arquivos">
	<tr><td><b>Nome anterior:</b></td><td> <label id="nomeAnterior"></label> </td></tr>
	<tr><td><p align="right">Novo nome:<p/></td><td> <input type="text" name="arquivo.nome" id="nomeArquivo" size="45" maxlength="200"/> </td></tr>
	</table>
	<input type="hidden" name="arquivo.id" id="idRenomearArquivo"/>
</html:form>	
				