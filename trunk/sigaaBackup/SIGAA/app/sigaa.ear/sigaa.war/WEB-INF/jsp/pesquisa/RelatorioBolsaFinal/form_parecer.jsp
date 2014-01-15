<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Parecer de Relatórios Finais de Iniciação Científica"/>
</h2>

<style>
.formulario p {
	padding: 2px 8px 10px;
	line-height: 1.2em;
}

</style>

<c:set var="plano" value="${formRelatorioBolsaFinal.obj.planoTrabalho}"/>
<c:set var="relatorio" value="${formRelatorioBolsaFinal.obj}"/>
<html:form action="/pesquisa/relatorioBolsaFinal" method="post" focus="obj.atividadesRealizadas">

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
					<ufrn:format type="dataHora" name="formRelatorioBolsaFinal" property="obj.dataEnvio"/>
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
				<th> <b>Palavras-chave: </b> </th>
				<td> <p>${ relatorio.palavrasChave }</p></td>
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
			<tr>
				<td colspan="2" class="subFormulario" style="text-align:center">
					Parecer
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 2px 10px;">
					<ufrn:textarea property="obj.parecerOrientador" rows="10" maxlength="10000" style="width: 99%"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<html:button dispatch="emitirParecer">Emitir Parecer</html:button>
				<html:button dispatch="cancelar">Cancelar</html:button>
				<ufrn:subSistema teste="pesquisa">
					<c:if test="${acesso.pesquisa}">
						<html:button onclick="history.go(-1);"><< Selecionar Outro Relatório</html:button>
					</c:if>
				</ufrn:subSistema>
				<ufrn:subSistema teste="not pesquisa">
					<html:button dispatch="listarOrientandos"><< Selecionar Outro Relatório</html:button>
				</ufrn:subSistema>
	    	</td></tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>