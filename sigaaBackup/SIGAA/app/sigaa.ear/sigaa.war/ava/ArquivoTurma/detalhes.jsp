
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<%@include file="/portais/turma/menu_turma.jsp"%>
<h2>Associar arquivo a aula</h2>


<h:form>

	<table class="formulario" width="70%">
		<caption>Detalhes do Arquivo</caption>
		<tr><th>Nome do Arquivo: </th><td><h:inputText value="#{ arquivosTurma.obj.nome }" size="43"/> </td></tr>
		<tr><th>Detalhes: </th><td><h:inputTextarea value="#{ arquivosTurma.obj.detalhes }" rows="3" cols="40"/> </td></tr>
		<tfoot>
		<tr><td colspan="2"><h:commandButton value="Cadastrar" action="#{ arquivosTurma.cadastrar }"/> <h:commandButton value="Cancelar" action="#{ arquivosTurma.cancelar }"/> </td></tr>
		</tfoot>
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>