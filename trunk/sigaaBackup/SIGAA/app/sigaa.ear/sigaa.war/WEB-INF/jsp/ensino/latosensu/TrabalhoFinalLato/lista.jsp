<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema></ufrn:subSistema> &gt;
Lista de Trabalhos Finais
</h2>


<html:form action="/ensino/latosensu/cadastroTrabalhoFinalLato">
	<table class="formulario" width="75%">
		<caption>Busca Trabalhos Finais</caption>

		<tbody>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaDiscente" />
				</td>
				<td>Aluno:</td>
            	<td>
            		<c:set var="idAjax" value="obj.discenteLato.id"/>
	                <c:set var="nomeAjax" value="obj.discenteLato.pessoa.nome"/>
	                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
            	</td>
			</tr>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="2" styleClass="noborder"
					styleId="buscaTitulo" />
				</td>
				<td>Título:</td>
            	<td>
            		<html:text property="obj.titulo" size="55" onfocus="javascript:forms[0].tipoBusca[1].checked=true;" />
            	</td>
			</tr>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="3" styleClass="noborder"
					styleId="buscaTodos" />
				</td>
				<td>Todos</td>
            </tr>
            </tbody>
            <tfoot>
            <tr>
				<td colspan="4" align="center">
				<html:hidden property="buscar" value="true"/>
				<html:button dispatch="list" value="Buscar"/>
				</td>
			</tr>
            </tfoot>
     </table>
</html:form>
<br>
<c:if test="${not empty lista}">

<ufrn:table collection="${lista}"
	properties="discenteLato.pessoa.nome, titulo, servidor.pessoa.nome"
	headers="Aluno, Título do Trabalho, Orientador"
	title="Trabalhos Finais" crud="true" />

</c:if>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
