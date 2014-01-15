<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>

table.visualizacao tr td.subFormulario {
	padding: 3px 0 3px 20px;
}
p.corpo {
	padding: 2px 8px 10px;
	line-height: 1.2em;
}

</style>

<c:choose>
	<c:when test="${ projetoPesquisaForm.renovacao }">
		<h2> <ufrn:subSistema /> &gt; Renova��o de Projetos de Pesquisa &gt; Dados da Renova��o &gt; Membros &gt; Cronograma &gt; Resumo </h2>
	</c:when>
	<c:otherwise>
		<h2> <ufrn:steps/> </h2>
	</c:otherwise>
</c:choose>
<ufrn:keepAlive tempo="5"/>

<c:if test="${acao == 'remover'}">
	<span class="subtitle"> <fmt:message key="mensagem.confirma.remocao">
		<fmt:param value="do Projeto de Pesquisa"></fmt:param>
	</fmt:message> </span>
</c:if>

<c:set var="projeto" value="${ projetoPesquisaForm.obj }" />
<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa"
	enctype="multipart/form-data"
	method="post">

	<!-- dados do projeto -->
    <table class="visualizacao" align="center" style="width: 100%">
    <caption>Dados do Projeto de Pesquisa</caption>
	<tbody>
    	<%@include file="/WEB-INF/jsp/pesquisa/ProjetoPesquisa/include/resumo_projeto.jsp"%>

		<c:if test="${empty requestScope.remove}">
			<tr>
				<td colspan="2" class="subFormulario"> Arquivo do Projeto </td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao" style="margin:0; padding: 12px 100px; text-align: center">
						Voc� poder� <i>(opcionalmente)</i> submeter um arquivo contendo os dados do projeto para ser armazenado no sistema.
					</div>
				</td>
			</tr>
			<tr>
				<th style="height: 35px;"> ${novo} Arquivo: </th>
				<td> <html:file property="arquivoProjeto" size="55"></html:file> </td>
			</tr>
		</c:if>
		<c:if test="${projetoPesquisaForm.coordenadorColaborador}">
			<tr>
				<td colspan="2" class="subFormulario"> Cl�usula Condicionante </td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao" style="margin:0; padding: 12px 100px; text-align: center">
						O professor colaborador volunt�rio pode coordenar projetos de pesquisa restrito apenas 
						� coordena��o t�cnica do projeto, n�o envolvendo tal coordena��o, atividades administrativas.
					</div>
				</td>
			</tr>
			<tr>
				<th style="height: 35px;"> Voc� concorda com a cl�usula descrita acima?</th>
				<td> 
					<html:checkbox property="checkClausula" styleClass="noborder" styleId="checkSim"/>
					<label for="checkSim">Sim, eu concordo.</label>
				</td>
			</tr>
		</c:if>
			<tr>
				<td colspan="2" class="subFormulario"> Termo de Concord�ncia </td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao" style="margin:0; padding: 12px 100px; text-align: center">
						<html:checkbox property="obj.concordanciaTermo" styleClass="noborder" styleId="checkSim">
							<label for="checkSim">
								Declaro ${ projeto.interno ? "minha concord�ncia plena em rela��o as normas de edital de refer�ncia," : ""} 
								e que as informa��es passadas s�o ver�dicas, e que estou ciente das exig�ncias de controle 
								�tico previsto pela resolu��o CNS 196/96 para o caso de projetos de pesquisa envolvendo 
								sujeitos humanos e animais n�o-humanos.							
							</label>
						</html:checkbox>
					</div>
				</td>
			</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center; background: #C8D5EC; padding: 3px;">
			<c:choose>
				<c:when test="${not empty requestScope.remove}">
		    		<html:button dispatch="chamaModelo" value="Confirmar Remo��o"/>
		    		<html:button dispatch="cancelar" cancelar="true" value="Cancelar Remo��o"/>
	  			</c:when>
				<c:otherwise>
		    		<c:if test="${ not projetoPesquisaForm.renovacao and not projetoPesquisaForm.anexoProjetoBase }">
						<html:button dispatch="gravar" value="Gravar e Continuar"/>
					</c:if>
		    		<html:button dispatch="chamaModelo" value="Gravar e Enviar"/>
		    		<c:if test="${ not projetoPesquisaForm.anexoProjetoBase }">
		    			<html:button dispatch="telaCronograma" value="<< Voltar"/>
					</c:if>
					<c:if test="${ projetoPesquisaForm.anexoProjetoBase }">
		    			<html:button dispatch="telaDadosGerais" value="<< Voltar"/>
					</c:if>
		    		<html:button dispatch="cancelar" cancelar="true" value="Cancelar"/>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
	</tfoot>
	</table>

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>