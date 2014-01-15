<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	#abas-relatorio div.yui-ext-tabitembody{
		background: #EAF3FD;
		padding: 5px 15px;
	}

	#abas-relatorio textarea {
		width: 98%;
		margin: 0 auto;
	}

	p.descricao {
		padding: 5px 100px 10px;
		font-style: italic;
		text-align: center;
	}

</style>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	<c:out value="Relatório Parcial de Bolsa"/>
</h2>
<div id="operacaoAjuda" class="descricaoOperacao" style="display:none"><a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('operacaoAjuda').hide();$('ajuda').show();" href="javascript://nop/">  (^) mostrar ajuda </a></div>
<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Relatório Parcial de Iniciação à Pesquisa.</strong>
		</p>
		<br/>
		<p>
			Qual a diferença entre <em>Apenas Gravar</em> e <em>Gravar e Enviar</em>?
		</p>
		<br/>
		<p>
			<strong><em>Apenas Gravar:</em></strong> Salva o que você digitou até o momento, mas não submete ao seu orientador. É apenas um rascunho. Sendo assim, você pode terminar o seu relatório em um outro momento, podendo parar e continuar quantas vezes for necessário.
		</p>
		<p>
			Mas atenção com relação ao prazo de envio, se o relatório não for enviado dentro do prazo informado no calendário acadêmico você não poderá mais enviar este relatório.
		</p>
		<br/>
		<p>
			<strong><em>Gravar e Enviar:</em></strong> Envia o relatório para o seu orientador emitir o parecer. Depois desta etapa voce não poderá mais editar o texto. Use esta opção somente quando seu relatório estiver pronto.
		</p>
		<br/>
		<c:if test="${not empty fimPeriodo}">
			<p>
				<center><strong>Muito cuidado para não perder o prazo de envio que é até <ufrn:format name="fimPeriodo" type="data" />.</strong></center>
			</p>
		</c:if>
		<a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('ajuda').hide();$('operacaoAjuda').show();" href="javascript://nop/">  (x) fechar ajuda </a>
</div>

<c:set var="plano" value="${formRelatorioBolsaParcial.obj.planoTrabalho}"/>
<html:form action="/pesquisa/relatorioBolsaParcial" method="post" focus="obj.atividadesRealizadas">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.planoTrabalho.id" value="${ plano.id }" />
	<html:hidden property="obj.planoTrabalho.membroProjetoDiscente.id" value="${ plano.membroProjetoDiscente.id }" />

	<c:set var="rows" value="9"/>
    <table class="formulario" width="95%">
		<caption>Preencha os campos do relatório</caption>
   	    <tbody>
			<tr>
				<th> <b>Discente:</b> </th>
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
				<th nowrap="nowrap"> <b>Título do Plano de Trabalho:</b> </th>
				<td>
					${plano.titulo}
				</td>
			</tr>
			<tr>
				<th> <b>Projeto:</b> </th>
				<td>
					${plano.projetoPesquisa.codigo} - ${plano.projetoPesquisa.titulo}
				</td>
			</tr>
			<c:if test="${not empty formRelatorioBolsaParcial.obj.dataEnvio }">
			<tr>
				<th nowrap="nowrap"> <b>Última alteração em:</b> </th>
				<td>
					<ufrn:format type="dataHora" name="formRelatorioBolsaParcial" property="obj.dataEnvio"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<td colspan="2">
				<div id="abas-relatorio">
					<div id="atividades-relatorio">
						<p class="descricao">
							Neste item devem ser informadas as participações nas reuniões da base, em congressos, apresentações de seminários, etc.
							<html:img page="/img/required.gif" style="vertical-align: top;" />
						</p>
	 					 <html:textarea property="obj.atividadesRealizadas" rows="10" />
					</div>
					<div id="comparacao-relatorio">
						<p class="descricao">
							Neste item devem ser informadas as atividades desenvolvidas e se o plano de trabalho original foi executado ou passou por modificações.
							<html:img page="/img/required.gif" style="vertical-align: top;" />
						</p>
						 <html:textarea property="obj.comparacaoOriginalExecutado" rows="10" /> 	
					</div>
					<div id="outras-relatorio">
						<p class="descricao">
							Neste item devem ser informadas atividades complementares não relacionadas, especificamente, ao plano de trabalho.
							<html:img page="/img/required.gif" style="vertical-align: top;" />
						</p>
						<html:textarea property="obj.outrasAtividades" rows="10" />
					</div>
					<div id="resultado-preliminares">
						<p class="descricao">
							Nesse item deve ser informado o resultado preliminar do Relatório de Bolsa parcial
							<html:img page="/img/required.gif" style="vertical-align: top;" />
						</p>
					    <html:textarea property="obj.resultadosPreliminares" rows="10" />
					</div>
				</div>
				</td>
			</tr>

		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<html:button dispatch="gravar">Apenas Gravar (Rascunho)</html:button>
				<html:button dispatch="enviar">Gravar e Enviar</html:button>
				<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
	    	</td></tr>
		</tfoot>
	</table>
</html:form>

<script type="text/javascript">
var Tabs = {
    init : function(){
        var tabs = new YAHOO.ext.TabPanel('abas-relatorio');
	   	tabs.addTab('atividades-relatorio', "Atividades realizadas");
		tabs.addTab('comparacao-relatorio', "Comparação entre o plano original e o executado");
		tabs.addTab('outras-relatorio', "Outras atividades");
		tabs.addTab('resultado-preliminares', "Resultados preliminares");
		tabs.activate('atividades-relatorio');
	}
}
YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>