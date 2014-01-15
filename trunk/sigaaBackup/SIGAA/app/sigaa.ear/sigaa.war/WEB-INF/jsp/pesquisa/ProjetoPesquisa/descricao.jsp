<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.descricao {
	margin: 5px 100px;
	text-align: center;
	font-style: italic;
}
</style>
<h2> <ufrn:steps/> </h2>
<ufrn:keepAlive tempo="5"/>

<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post" focus="obj.introducao">

    <table class="formulario" align="center" width="95%" cellpadding="4" cellspacing="2">
    <caption class="listagem">Detalhes do projeto</caption>

	<tr>
		<td>
		<div id="abas-descricao">

			<div id="descricao-projeto" class="aba">
				<html:textarea property="obj.descricao" style="width: 98%" rows="13" />
			</div>
			<div id="justificatica-projeto" class="aba">
				<p class="descricao"> 
					Inclua na justificativa os benefícios esperados no processo ensino-aprendizagem dos alunos de graduação
					e/ou pós-graduação vinculados ao projeto. 
					Explicite também o retorno para os cursos de graduação e/ou pós-graduação e para os professores da
					${ configSistema['siglaInstituicao'] } em geral.
				</p>
				<html:textarea property="obj.justificativa" style="width: 98%" rows="13" />
			</div>
			<div id="objetivos-projeto" class="aba">
				<html:textarea property="obj.objetivos" style="width: 98%" rows="13" />
			</div>
			<div id="metodologia-projeto" class="aba">
				<html:textarea property="obj.metodologia" style="width: 98%" rows="13" />
			</div>
			<div id="bibliografia-projeto" class="aba">
				<html:textarea property="obj.bibliografia" style="width: 98%" rows="13" />
			</div>
		</div>
		</td>
	</tr>
	
	<tfoot>
		<tr>
		<td colspan="2">
			<html:button dispatch="gravar" value="Gravar e Continuar"/>
			<html:button dispatch="telaDadosGerais" value="<< Voltar"/>
    		<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			<c:choose>
				<c:when test="${ projetoPesquisaForm.financiado }">
					<html:button dispatch="financiamentos" value="Avançar >>"/>
				</c:when>
				<c:otherwise>
					<html:button dispatch="docentes" value="Avançar >>"/>
				</c:otherwise>
			</c:choose>
		</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<center>
		<img src="${ctx}/img/required.gif"> Todas as abas são de preenchimento obrigatório.
	</center>
</html:form>


<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('descricao-projeto', "Descrição Resumida<img src='${ctx}/img/required.gif'>");
		abas.addTab('justificatica-projeto', "Introdução/Justificativa<img src='${ctx}/img/required.gif'>");
		abas.addTab('objetivos-projeto', "Objetivos<img src='${ctx}/img/required.gif'>");
		abas.addTab('metodologia-projeto', "Metodologia<img src='${ctx}/img/required.gif'>");
		abas.addTab('bibliografia-projeto', "Referências<img src='${ctx}/img/required.gif'>");
        abas.activate('descricao-projeto');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>