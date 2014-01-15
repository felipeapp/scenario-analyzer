<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>

table.formulario tr th{
 	text-align: right;
}

</style>


<h2><ufrn:subSistema /> &gt; Projetos Financiados </h2>

<c:if test="${empty lista}">
	<html:form action="/pesquisa/projetoPesquisa/buscarProjetos" method="post">
	<table class="formulario" style="width: 80%" >
		<caption> Informe os critérios de busca </caption>
		<tbody>
		<tr>
			<th class="obrigatorio" width="20%">Ano dos Projetos:</th>
			<td>
				<html:text property="obj.codigo.ano" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio"> Entidade Financiadora: </th>
			<td>
				<html:select property="financiamentoProjetoPesq.entidadeFinanciadora.id" style="width:95%">
					<html:option value="-1">  -- SELECIONE --  </html:option>
					<html:options collection="entidades" property="id" labelProperty="nome" />
				</html:select>
			</td>
		</tr>
		<tr>
			<th> Classificação do Financiamento: </th>
			<td>
				<html:select property="financiamentoProjetoPesq.entidadeFinanciadora.classificacaoFinanciadora.id" style="width:95%">
					<html:option value="-1">  -- TODOS --  </html:option>
					<html:options collection="classificacoes" property="id" labelProperty="descricao" />
				</html:select>
			</td>
		</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="financiamentos" value="Buscar"/>
					<html:button dispatch="cancelar" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</html:form>
	
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</c:if>

<c:if test="${empty popular} ">
<ufrn:table
	collection="${lista}"
	properties="codigo, nome, financiamentoProjetoPesq.entidadeFinanciadora.classificacaoFinanciadora.descricao"
	headers="Código, Nome, Classificação do Financiamento"
	title="Projetos de Pesquisa Financiados">
</ufrn:table>
</c:if>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>