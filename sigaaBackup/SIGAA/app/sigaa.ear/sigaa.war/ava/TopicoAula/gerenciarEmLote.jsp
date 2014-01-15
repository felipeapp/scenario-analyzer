<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp"%>
	<h:form id="form" onsubmit="prepararEnvio();">
		
		<c:set var="topicos" value="#{ topicoAula.todosTopicosTurma }" />
		<fieldset>
			<legend>Gerenciar Todos os T�picos de Aula</legend> 
		
			<div class="descricaoOperacao">
				<p>Utilize esta tela para criar t�picos nos dias de aula desta turma ou renomear os t�picos de aula existentes.</p>
				<ul style="list-style:circle;margin-left:70px;margin-top:10px;">
					<li>Caso n�o queira cadastrar um t�pico em um dia, simplesmente deixe o campo de texto referente �quele dia em branco.</li>
					<li>Para alterar o t�tulo de um t�pico de aula, altere o valor do campo de texto referente ao mesmo. (Apagar o t�tulo de um t�pico de aula n�o o deletar�)</li>
					<li>Para indicar que um t�pico de aula durar� mais do que um dia de aula, simplesmente associe o t�pico imediatamente abaixo do mesmo. Desta forma, o t�pico iniciar� na data inicial do principal e finalizar� na data final do �tlimo t�pico associado a ele.</li>
					<li>Ao final de tudo, clique em "Cadastrar" para salvar as modifica��es.</li>
				</ul>
			</div>
			
			<div class="infoAltRem">
				<h:graphicImage value="/ava/img/cima_direita.png" />: Associar ao t�pico anterior
				<h:graphicImage value="/img/delete.png" />: Desassociar do t�pico anterior
			</div>
		
			<script>
				var CTX = "${ctx}";
			</script>
		
			<jwr:script src="/javascript/turma-virtual/topicos-lote.js"/>
		
			<%-- Tabela base para o formul�rio a ser gerado. --%>
			<table class="listing">
				<thead>
					<tr>
						<th style="width:80px;text-align:center;">In�cio</th>
						<th style="width:80px;text-align:center;">Fim</th>
						<th style="width:30px;" width="30"></th>
						<th style="text-align:left;">Descri��o</th>
					</tr>
				</thead>
				<tbody id="tbody"></tbody>
			</table>
			
			<c:set var="ids" value="" />
		
			<c:forEach var="t" items="#{ topicos }" varStatus="loop">
				<c:set var="ids" value='${ids}${ids == "" ? "" : "%!%"}${t.id}%@%${t.descricao}%@%${t.dataFormatada}%@%${t.dataFimFormatada}' />
			</c:forEach>
			
			<input id="basedados" style="display:none;" value="${ids}" />
			
			<script>configurarTopicos(document.getElementById("basedados").value);</script>
		
			<h:inputHidden id="dadosTeL" value="#{topicoAula.dadosTopicosEmLote}" />
		
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{topicoAula.gerenciarEmLote}" value="Cadastrar" /> 
				</div>
				<div class="other-actions">
					<h:commandButton action="#{ topicoAula.listar }" value="<< Voltar"/> 
					<h:commandButton onclick="#{confirm}" action="#{ topicoAula.cancelar }" value="Cancelar"/> 
				</div>
			</div>
		</fieldset>
	</h:form>
	

</f:view>
<%@include file="/ava/rodape.jsp" %>