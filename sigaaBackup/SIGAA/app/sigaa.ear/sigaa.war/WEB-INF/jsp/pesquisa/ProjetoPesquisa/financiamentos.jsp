<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%--
<script>
	function checaTipoFinanciamento(){
		var select = $F('tipoNaturezaFinanciamento');
		if( select == 2 || select == 3 ){
			var elementos = document.getElementsByClassName('bolsaExterna');
			elementos.each(
				function(e) { Element.show(e); }
			);
		} else{
			var elementos = document.getElementsByClassName('bolsaExterna');
			elementos.each(
				function(e) { Element.hide(e); }
			);
		}

		var mod = document.getElementById('div_modalidades_bolsa');
	}
</script>
--%>

<h2> <ufrn:steps/> </h2>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post" focus="financiamentoProjetoPesq.entidadeFinanciadora.id">
	<table class="formulario" style="width: 90%">
	<caption class="listagem">Financiamentos</caption>
	<tr>
		<th width="35%" class="required">Entidade Financiadora:</th>
		<td>
	        <html:select property="financiamentoProjetoPesq.entidadeFinanciadora.id" styleId="make">
	        	<html:option value=""> -- SELECIONE UMA OPÇÃO -- </html:option>
	        	<html:options collection="entidadesFinanciadoras" property="id" labelProperty="nome" />
	        </html:select>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<p style="font-style: italic; text-align: center; background: #F5F5F5">
	        Caso não exista a entidade financiadora desejada, solicite o cadastro junto à Pró-Reitoria de Pesquisa.
	        </p>
	    </td>
	</tr>

	<tr>
		<th class="required">
		Natureza do Financiamento:
		</th>
		<td>
	        <html:select styleId="tipoNaturezaFinanciamento" property="financiamentoProjetoPesq.tipoNaturezaFinanciamento.id">
	        <html:option value=""> -- SELECIONE UMA OPÇÃO -- </html:option>
	        <html:options collection="tiposNaturezaFinanciamento" property="id" labelProperty="descricao" />
            </html:select>
		</td>
	</tr>

	<tr>
		<th class="required"> Data de Início: </th>
		<td>
		<ufrn:calendar property="dtInicioFinanciamento"/>
		</td>
	</tr>

	<tr>
		<th class="required">
		Data de Fim:
		</th>
		<td>
		<ufrn:calendar property="dtFimFinanciamento"/>
		</td>
	</tr>

	<tfoot>
		<tr>
		<td colspan=2>
		<html:button dispatch="addFinanciamento" value="Adicionar Financiamento"/>
		</td>
		</tr>

	</tfoot>
	</table>
<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>


<script>
	checaTipoFinanciamento();
</script>

<html:hidden property="posLista" styleId="posicao"/>
<c:set var="posicao" value="0"/>

<br/>
<div class="infoAltRem">
	<html:img page="/img/delete.gif" style="overflow: visible;"/>
    : Remover Financiamento
</div>
<table class="listagem" width="80%">
<caption class="listagem">Financiamentos</caption>
	<c:choose>
		<c:when test="${not empty projetoPesquisaForm.obj.financiamentosProjetoPesq}">
	       <thead>
	       	<tr>
		        <th>Entidade Financiadora</th>
		        <th>Natureza do Financiamento</th>
		        <th>Data de Início</th>
		        <th>Data de Fim</th>
		        <th> </th>
	       	</tr>
	       </thead>
	       <tbody>
	
	       <c:forEach items="${projetoPesquisaForm.obj.financiamentosProjetoPesq}" var="financiamento">
	           <tr>
	                   <html:hidden property="financiamento.id" value="${financiamento.id}" />
	                   <td>${financiamento.entidadeFinanciadora.nome}</td>
	                   <td>${financiamento.tipoNaturezaFinanciamento.descricao}</td>
	                   <td><fmt:formatDate value="${financiamento.dataInicio}"pattern="dd/MM/yyyy" /></td>
	                   <td><fmt:formatDate value="${financiamento.dataFim}"pattern="dd/MM/yyyy" /></td>
	
	                   <td>
	                       <html:image value="${posicao}" dispatch="removeFinanciamento" property="posicao" alt="Remover Financiamento" title="Remover Financiamento"></html:image>
	                   </td>
	
	           </tr>
	           <c:set var="posicao" value="${posicao + 1}"/>
	       </c:forEach>
	       </tbody>
       </c:when>
       <c:otherwise>
			<tr>
				<td align="center" style="font-style: italic; padding: 10px;" colspan="5">
					Este projeto não possui financiamentos
				</td>
			</tr>
		</c:otherwise>
	</c:choose>
	<tfoot class="formulario">
		<tr>
			<td colspan="5">
				<c:if test="${not projetoPesquisaForm.renovacao}">
					<html:button view="descricao" value="<< Voltar"/>
				</c:if>
				<c:if test="${projetoPesquisaForm.renovacao}">
					<html:button view="dadosRenovacao" value="<< Voltar"/>
				</c:if>
				<html:button dispatch="gravar" value="Gravar e Continuar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
				<html:button dispatch="docentes" value="Avançar >>"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
