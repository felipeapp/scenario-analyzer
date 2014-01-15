<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	#abas-relatorio div.yui-ext-tabitembody{
		background: #EAF3FD;
		padding: 5px 15px;
	}

	#abas-relatorio textarea {
		width: 99%;
		margin: 0 auto;
	}

	p.descricao {
		padding: 5px 100px 10px;
		font-style: italic;
		text-align: center;
	}

	.ytab-strip .ytab-text {
		font-size: 0.8em;
	}

	span.info {
		font-size: 0.9em;
		color: #555;
	}

</style>

<h2>
	<ufrn:subSistema /> &gt;
	<c:out value="Relat�rio Final de Inicia��o Cient�fica"/>
</h2>
<div id="operacaoAjuda" class="descricaoOperacao" style="display:none"><a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('operacaoAjuda').hide();$('ajuda').show();" href="javascript://nop/">  (^) mostrar ajuda </a></div>
<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Relat�rio Final de Inicia��o � Pesquisa.</strong>
		</p>
		<br/>
		<p>
			Qual a diferen�a entre <em>Apenas Gravar</em> e <em>Gravar e Enviar</em>?
		</p>
		<br/>
		<p>
			<strong><em>Apenas Gravar:</em></strong> Salva o que voc� digitou at� o momento, mas n�o submete ao seu orientador. � apenas um rascunho. Sendo assim, voc� pode terminar o seu relat�rio em um outro momento, podendo parar e continuar quantas vezes for necess�rio.
		</p>
		<p>
			Mas aten��o com rela��o ao prazo de envio, se o relat�rio n�o for enviado dentro do prazo informado no calend�rio acad�mico voc� n�o poder� mais enviar este relat�rio.
		</p>
		<br/>
		<p>
			<strong><em>Gravar e Enviar:</em></strong> Envia o relat�rio para o seu orientador emitir o parecer. Depois desta etapa voce n�o poder� mais editar o texto. Use esta op��o somente quando seu relat�rio estiver pronto.
		</p>
		<br/>
		<p>
			<center><strong>Muito cuidado para n�o perder o prazo de envio que � at� <ufrn:format name="fimPeriodo" type="data" />.</strong></center>
		</p>
		<a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('ajuda').hide();$('operacaoAjuda').show();" href="javascript://nop/">  (x) fechar ajuda </a>
</div>


<c:set var="plano" value="${formRelatorioBolsaFinal.obj.planoTrabalho}"/>
<html:form action="/pesquisa/relatorioBolsaFinal" method="post" focus="obj.resumo">
	
	<c:set var="rows" value="14"/>
    <table class="formulario" width="95%">
		<caption> Relat�rio Final de Inicia��o Cient�fica </caption>
   	    <tbody>
			<tr>
				<th> <b>Discente:</b> </th>
				<td>
					${formRelatorioBolsaFinal.obj.membroDiscente.discente.matricula} -
					${formRelatorioBolsaFinal.obj.membroDiscente.discente.pessoa.nome}
				</td>
			</tr>
			<tr>
				<th> <b>Orientador:</b> </th>
				<td>
					${plano.orientador.pessoa.nome}
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap"> <b>T�tulo do Plano de Trabalho:</b> </th>
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
			<c:if test="${not empty formRelatorioBolsaFinal.obj.dataEnvio }">
			<tr>
				<th nowrap="nowrap"> <b>�ltima altera��o em:</b> </th>
				<td>
					<ufrn:format type="dataHora" name="formRelatorioBolsaFinal" property="obj.dataEnvio"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<td class="subFormulario" colspan="2">
					Campos do Relat�rio
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Resumo</b> <span class="info"> (Este campo ser� utilizado como base para o resumo do CIC) </span>
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 3px 10px;">
					<ufrn:textarea property="obj.resumo" rows="8" readonly="${ requestScope.emitirparecer }" style="width:99%" maxlength="1500"/>
				</td>
			</tr>
			<tr>
				<td colspan="2"> <b>Palavras-Chave</b> <span class="info"> (limitado a 70 caracteres) </span></td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 3px 10px;">
					<html:text property="obj.palavrasChave" readonly="${ requestScope.emitirparecer }" style="width:99%"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Corpo do Relat�rio</b>
					<span class="info"> (selecione as diferentes abas para preencher todos as se��es do relat�rio)</span>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<div id="abas-relatorio">
					<div id="introducao-relatorio">
						<p class="descricao">
							A introdu��o exp�e o tema do trabalho de pesquisa, relacionando-o com a bibliografia consultada.
							Trata-se do elemento explicativo do autor para o leitor.
						</p>
						<ufrn:textarea property="obj.introducao" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="10000" styleId="txt-introducao"/>
					</div>
					<div id="objetivos-relatorio">
						<p class="descricao">
							Norteiam o aluno de Inicia��o Cient�fica na busca de solu��es para o problema investigado e
							tamb�m orientam a avalia��o dos resultados apresentados.
						</p>
						<ufrn:textarea  property="obj.objetivos" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="2000" styleId="txt-objetivos"/>
					</div>
					<div id="metodologia-relatorio">
						<p class="descricao">
							Diz respeito � descri��o precisa dos m�todos, materiais, t�cnicas e equipamentos utilizados.
							Deve permitir a repeti��o do experimento ou estudo com a mesma exatid�o por outros pesquisadores.
						</p>
						<ufrn:textarea property="obj.metodologia" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="10000" styleId="txt-metodologia"/>
					</div>
					<div id="resultados-relatorio">
						<p class="descricao">
							Apresentam os dados coletados na parte experimental ou pr�tica.
						</p>
						<ufrn:textarea property="obj.resultados" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="10000" styleId="txt-resultados"/>
					</div>
					<div id="discussao-relatorio">
						<p class="descricao">
							Restringe-se aos resultados do trabalho e ao confronto destes com dados da literatura consultada.
						</p>
						<ufrn:textarea property="obj.discussao" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="10000" styleId="txt-discussao"/>
					</div>
					<div id="conclusoes-relatorio">
						<p class="descricao">
							Destaca os resultados obtidos na trabalho de pesquisa.
							Deve ser breve, podendo incluir recomenda��es ou sugest�es para outras pesquisas na �rea.
						</p>
						<ufrn:textarea property="obj.conclusoes" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="2000" styleId="txt-conclusoes"/>
					</div>
					<div id="perspectivas-relatorio">
						<p class="descricao">
							Exp�em as vias futuras de explora��o do tema estudado suas extens�es e import�ncia.
						</p>
						<ufrn:textarea property="obj.perspectivas" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="2000" styleId="txt-perspectivas"/>
					</div>
					<div id="bibliografia-relatorio">
						<p class="descricao">
							Relata as fontes bibliogr�ficas utilizadas como suporte no desenvolvimento do trabalho de Inicia��o Cient�fica.
						</p>
						<ufrn:textarea property="obj.bibliografia" rows="${rows}" readonly="${ requestScope.emitirparecer }" styleId="txt-bibliografia"/>
						<span style="font-size: 0.95em"> sem limite de caracteres </span>
					</div>
					<div id="atividades-relatorio">
						<p class="descricao">
							Atividades complementares n�o relacionadas, especificamente, ao plano de trabalho.
						</p>
						<ufrn:textarea property="obj.outrasAtividades" rows="${rows}" readonly="${ requestScope.emitirparecer }" maxlength="2000" styleId="txt-atividades"/>
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
	   	tabs.addTab('introducao-relatorio', "Introdu��o");
		tabs.addTab('objetivos-relatorio', "Objetivos");
		tabs.addTab('metodologia-relatorio', "Metodologia");
	   	tabs.addTab('resultados-relatorio', "Resultados");
		tabs.addTab('discussao-relatorio', "Discuss�es");
		tabs.addTab('conclusoes-relatorio', "Conclus�es");
	   	tabs.addTab('perspectivas-relatorio', "Perspectivas");
		tabs.addTab('bibliografia-relatorio', "Bibliografia");
		tabs.addTab('atividades-relatorio', "Atividades");
		tabs.activate('introducao-relatorio');
	}
}
YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>