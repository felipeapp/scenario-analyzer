<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>

<br />
<br />
<h:form>
<table class=formulario width="100%">
 <caption class="listagem">Cadastro de Natureza do Exame</caption>
<h:inputHidden value="#{naturezaExame.confirmButton}"/> <h:inputHidden value="#{naturezaExame.obj.id}"/>
<tr>
	<th> Descrição:</th>
	<td> <h:inputText value="#{naturezaExame.obj.descricao}" size="60" maxlength="255" readonly="#{naturezaExame.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{naturezaExame.confirmButton}" action="#{naturezaExame.cadastrar}" /> <h:commandButton value="Cancelar" action="#{naturezaExame.cancelar}" /></td>
</tr></tfoot>
</table>
</h:form>
<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
