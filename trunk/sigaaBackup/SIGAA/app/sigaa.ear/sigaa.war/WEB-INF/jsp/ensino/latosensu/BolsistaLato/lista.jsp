<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2> Lista de Bolsistas </h2>

<html:form action="/ensino/latosensu/cadastroBolsistaLato">
	<table class="formulario" width="75%">
		<caption>Busca Bolsistas</caption>

		<tbody>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaDiscente" />
				</td>
				<td>Nome:</td>
            	<td>
            		<c:set var="idAjax" value="obj.discenteLato.id"/>
	                <c:set var="nomeAjax" value="obj.discenteLato.pessoa.nome"/>
	                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
            	</td>
			</tr>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="2" styleClass="noborder"
					styleId="buscaEntidadeFinanciadora" />
				</td>
				<td>Entidade Financiadora:</td>
            	<td>
            		<html:select property="obj.entidadeFinanciadora.id" onfocus="javascript:forms[0].tipoBusca[1].checked=true;">
						<html:option value="">-- SELECIONE --</html:option>
		            	<html:options collection="entidadesFinanciadoras" property="id" labelProperty="nome"/>
					</html:select>
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
	properties="discenteLato.pessoa.nome,entidadeFinanciadora.nome"
	headers="Aluno, Entidade Financiadora"
	title="Bolsistas" crud="true" />

</c:if>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
