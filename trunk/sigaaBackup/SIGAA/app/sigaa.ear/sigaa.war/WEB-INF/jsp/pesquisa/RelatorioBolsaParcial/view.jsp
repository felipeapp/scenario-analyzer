<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Relatório Parcial de Bolsa"/>
</h2>

<style>
.visualizacao p {
	padding: 2px 8px 10px;
	line-height: 1.2em;
}

</style>

<table class="visualizacao">
<caption>Relatório Parcial</caption>
<tbody>
	<tr>
		<th width="20%"> <b>Discente:</b> </th>
		<td>
			${relatorio.membroDiscente.discente.matricula} -
			${relatorio.membroDiscente.discente.pessoa.nome}
		</td>
	</tr>
	<tr>
		<th> <b>Projeto:</b> </th>
		<td>
			${relatorio.planoTrabalho.projetoPesquisa.codigo} - ${relatorio.planoTrabalho.projetoPesquisa.titulo}
		</td>
	</tr>
	<tr>
		<th> <b>Orientador:</b> </th>
		<td>
			${relatorio.planoTrabalho.orientador.pessoa.nome}
		</td>
	</tr>
	<tr>
		<th> <b>Data de Envio:</b> </th>
		<td>
			<ufrn:format type="dataHora" name="relatorio" property="dataEnvio"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario" style="text-align:center">
			Corpo do Relatório
		</td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Atividades Realizadas</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="atividadesRealizadas"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Comparação entre o plano original e o executado</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="comparacaoOriginalExecutado"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b> Outras atividades</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="outrasAtividades"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b> Resultados Preliminares</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="resultadosPreliminares"/></p></td>
	</tr>
	<c:if test="${relatorio.parecerOrientador != null}">
	<tr>
		<td colspan="2" class="subFormulario" style="text-align:center">
			Parecer (emitido em <ufrn:format type="dataHora" name="relatorio" property="dataParecer" />)
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<p> <ufrn:format type="texto" name="relatorio" property="parecerOrientador"/> </p>
		</td>
	</tr>
	</c:if>
</tbody>
</table>
<br />
<div class="voltar" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>