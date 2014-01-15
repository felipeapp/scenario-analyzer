<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Relatório Final de Iniciação Científica"/>
</h2>

<style>
.visualizacao p {
	padding: 2px 8px 10px;
	line-height: 1.2em;
}

</style>

<c:if test="${ not empty comprovante }">
	<div class="descricaoOperacao">
		<h3 style="text-align: center; margin-bottom: 15px;"> ATENÇÃO! </h3>
		<p>
			Não se esqueça de enviar também o resumo do <b>Congresso de Iniciação Científica</b>.
		</p>
		<h3 style="text-align: center; margin-top: 15px;">
			<a href="${ctx}/pesquisa/resumoCongresso.do?dispatch=iniciarEnvio">Clique aqui para enviar agora o seu resumo.</a>
		</h3> 
	</div>
</c:if>

<table class="visualizacao">
<caption>Relatório Final de Iniciação Científica</caption>
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
			Resumo
		</td>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="resumo"/></p></td>
	</tr>
	<tr>
		<td colspan="2" style="text-align: left;"> <b>Palavras-chave: </b> ${ relatorio.palavrasChave } </td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario" style="text-align:center">
			Corpo do Relatório
		</td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Introdução</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="introducao"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Objetivos</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="objetivos"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Metodologia</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="metodologia"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Resultados</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="resultados"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Discussões</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="discussao"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Conclusões</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="conclusoes"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Perspectivas</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="perspectivas"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Bibliografia</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="bibliografia"/></p></td>
	</tr>
	<tr>
		<th colspan="2" style="text-align: left;"> <b>Outras Atividades</b> </th>
	</tr>
	<tr>
		<td colspan="2"> <p><ufrn:format type="texto" name="relatorio" property="outrasAtividades"/></p></td>
	</tr>

	<%-- Parecer --%>
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
	<br/>
	<div align="center">
		<a href="javascript:history.back();" ><< Voltar</a>
	</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>