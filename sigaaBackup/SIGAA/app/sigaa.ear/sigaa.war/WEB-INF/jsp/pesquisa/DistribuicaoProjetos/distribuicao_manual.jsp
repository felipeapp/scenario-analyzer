<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Distribuição Projeto de Pesquisa
</h2>

<style>
	td.addProjeto {
		line-height: 1.8em;
		text-align: center;
		border-bottom: 1px solid #DDD;
		background: #F1F1F1;
	}

	td.addProjeto .codigo{
		padding: 2px;
	}

	td.addProjeto span.info {
		font-size: 0.8em;
		color: #444;
		margin-right: 15px;
	}

	.vazio {
		text-align: center;
		line-height: 2.5em;
		color: #922;
		background: #FAFAFA;
	}
</style>

<html:form action="/pesquisa/distribuirProjetoPesquisa" method="post" focus="codigo.numero">
<table class="formulario" width="90%">
	<caption> Definição da Distribuição </caption>
	<tr>
		<th> Consultor: </th>
		<td colspan="3">
			<c:set var="idAjax" value="consultor.id"/>
			<c:set var="nomeAjax" value="consultor.nome"/>
			<%@include file="/WEB-INF/jsp/include/ajax/consultor.jsp" %>
		</td>
	</tr>
	<tr>
		<td colspan="4" class="subFormulario"> Lista de Projetos a Distribuir </td>
	</tr>
	<tr>
		<td colspan="4" class="addProjeto">
			Código do Projeto:
			PXX
			<html:text property="codigo.numero" size="5" maxlength="7" styleClass="codigo" onkeyup="formatarInteiro(this)"/>
			-<html:text property="codigo.ano" size="4" maxlength="4" styleClass="codigo" onkeyup="formatarInteiro(this)"/>
			<html:button dispatch="adicionarProjeto" value="Adicionar projeto à lista" />
		</td>
	</tr>

	<c:forEach var="projeto" items="${ formDistribuicaoProjetos.projetos }" varStatus="loop">
	<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<td> ${projeto.codigo } </td>
		<td> ${projeto.titulo } </td>
		<td> ${projeto.coordenador.pessoa.nome } </td>
		<td>
			<ufrn:link action="/pesquisa/distribuirProjetoPesquisa" param="dispatch=removerProjeto&acao=${loop.index}">
				<img src="${ctx}/img/delete.gif" alt="Remover Projeto" title="Remover Projeto" />
			</ufrn:link>
		</td>
	</tr>
	</c:forEach>

	<c:if test="${empty formDistribuicaoProjetos.projetos }">
		<tr>
			<td colspan="4" class="vazio">
				Informe no mínimo um projeto para a distribuição
			</td>
		</tr>
	</c:if>

	<tfoot>
		<tr>
			<td colspan="4">
				<html:button dispatch="manual" value="Realizar Distribuição"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
	    	</td>
	    </tr>
	</tfoot>
</table>
</html:form>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>