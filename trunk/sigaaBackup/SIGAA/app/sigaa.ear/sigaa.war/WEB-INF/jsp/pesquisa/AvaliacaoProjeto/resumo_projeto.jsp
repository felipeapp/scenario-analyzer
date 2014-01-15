<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:steps /></h2>

<style>
	table.formulario td.media {
		font-size: 1.2em;
		font-weight: bold;
	}
</style>

<html:form action="/pesquisa/avaliarProjetoPesquisa" method="post" styleId="formAvaliacaoProjeto">

<table class="formulario" width="80%">
	<caption> Avalia��o de Projeto de Pesquisa </caption>
	<tbody>
		<tr>
			<th> Consultor: </th>
			<td> ${formAvaliacaoProjeto.obj.consultor.nome} </td>
		</tr>
		<tr>
			<th width="20%"> C�digo do Projeto: </th>
			<td> ${formAvaliacaoProjeto.obj.projetoPesquisa.codigo} </td>
		</tr>
		<tr>
			<th> T�tulo do Projeto: </th>
			<td> ${formAvaliacaoProjeto.obj.projetoPesquisa.titulo} </td>
		</tr>
		<tr>
			<th> M�dia: </h>
			<td class="media" style="${ formAvaliacaoProjeto.obj.media < 3.0 ? "color: #922" : "color: #292" }">
				<strong><ufrn:format type="valor1" name="formAvaliacaoProjeto" property="obj.media"/></strong>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center"> Itens da Avalia��o </td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="subFormulario listagem" style="width:100%; border: 0">
				<c:forEach items="${formAvaliacaoProjeto.obj.notasItens}" var="item" varStatus="status">
				<tbody>
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>
							${item.itemAvaliacao.descricao}
						</td>
						<td align="right" style="${item.nota < 3 ? "color: #922" : "color: #292" }">
							<strong><ufrn:format type="valor1" name="item" property="nota" /></strong>
						</td>
					</tr>
				</c:forEach>
				</tfoot>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="height: 10px;"></td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"  style="text-align: center"> Observa��es </td>
		</tr>
		<tr>
			<td colspan="2">
			<c:if test="${empty comprovante}">
				<html:textarea property="obj.observacoes" rows="4" style="width: 99%"/>
			</c:if>
			<c:if test="${not empty comprovante}">
				<p style="padding: 5px; text-indent: 2em; line-height: 1.25em">
					${formAvaliacaoProjeto.obj.observacoes}
				</p>
			</c:if>
			</td>
		</tr>
	</tbody>
	<c:if test="${empty comprovante}">
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button view="avaliacao"><< Voltar</html:button>
				<html:button dispatch="finalizar">Confirmar Avalia��o</html:button>
				<html:button dispatch="cancelar">Cancelar</html:button>
			</td>
		</tr>
	</tfoot>
	</c:if>
</table>

</html:form>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>