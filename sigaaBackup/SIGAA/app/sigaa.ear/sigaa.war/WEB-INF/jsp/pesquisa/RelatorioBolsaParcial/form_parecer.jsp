<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Parecer de Relatórios Parciais de Discentes"/>
</h2>

<c:set var="plano" value="${formRelatorioBolsaParcial.obj.planoTrabalho}"/>
<c:set var="relatorio" value="${formRelatorioBolsaParcial.obj}"/>
<html:form action="/pesquisa/relatorioBolsaParcial" method="post" focus="obj.atividadesRealizadas">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.planoTrabalho.id" value="${ plano.id }" />
	<html:hidden property="obj.planoTrabalho.membroProjetoDiscente.id" value="${ plano.membroProjetoDiscente.id }" />

	<c:set var="rows" value="9"/>
    <table class="formulario" width="95%">
		<caption>Analise o relatório enviado e realize a emissão do parecer</caption>
   	    <tbody>
			<tr>
				<th width="28%"> <b>Discente:</b> </th>
				<td>
					${plano.membroProjetoDiscente.discente.matricula} -
					${plano.membroProjetoDiscente.discente.pessoa.nome}
				</td>
			</tr>
			<tr>
				<th> <b>Orientador:</b> </th>
				<td>
					${plano.orientador.pessoa.nome}
				</td>
			</tr>
			<tr>
				<th> <b>Projeto:</b> </th>
				<td>
					${plano.projetoPesquisa.codigo} - ${plano.projetoPesquisa.titulo}
				</td>
			</tr>
			<tr>
				<th> <b>Data de Envio:</b> </th>
				<td>
					<ufrn:format type="dataHora" name="formRelatorioBolsaParcial" property="obj.dataEnvio"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario" style="text-align:center">
					Corpo do Relatório
				</td>
			</tr>
			<tr>
				<th> <b>Atividades Realizadas:</b> </th>
				<td colspan="2"> <ufrn:format type="texto" name="relatorio" property="atividadesRealizadas"/></td>
			</tr>
			<tr>
				<th> <b>Comparação entre o projeto original e o executado</b> </th>
				<td> <ufrn:format type="texto" name="relatorio" property="comparacaoOriginalExecutado"/></td>
			</tr>
			<tr>
				<th> <b> Outras atividades</b> </th>
				<td> <ufrn:format type="texto" name="relatorio" property="outrasAtividades"/></td>
			</tr>
			<tr>
				<th> <b> Resultados preliminares</b> </th>
				<td> <ufrn:format type="texto" name="relatorio" property="resultadosPreliminares"/></td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario" style="text-align:center">
					Parecer
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<ufrn:textarea property="obj.parecerOrientador" rows="8" maxlength="5000" style="width: 98%"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<html:button dispatch="listarOrientandos">Selecionar Outro Relatório</html:button>
				<html:button dispatch="emitirParecer">Emitir Parecer</html:button>
				<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
	    	</td></tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>