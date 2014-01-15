<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<script type="text/javascript">
function habilitarDetalhes(idDiscente) {
	var linha = 'linha_'+ idDiscente;
	var icone = 'icone_'+ idDiscente;
	if ( $(linha).style.display != 'table-cell' ) {
		if ( !Element.hasClassName(linha, 'populado') ) {
			getContent("/sigaa/geral/atendimento_aluno/tela_aluno.jsf?id=" + idDiscente, linha);
			Element.addClassName(linha, 'populado');
		}
		$(linha).style.display = 'table-cell';
		$(icone).src= '/sigaa/img/biblioteca/cima.gif';
	} else {
		$(linha).style.display = 'none';
		$(icone).src= '/sigaa/img/biblioteca/baixo.gif';
	}
}
</script>

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
<ufrn:link action="verPortalDiscente">Portal do Discente</ufrn:link> > <c:out value="Atendimento ao Aluno"/>
</h2>
<div id="operacaoAjuda" class="descricaoOperacao" style="display:none"><a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('operacaoAjuda').hide();$('ajuda').show();" href="javascript://nop/">  (^) mostrar ajuda </a></div>
<div id="ajuda" class="descricaoOperacao">
	<p>
	<strong>Atendimento ao Aluno</strong> é um canal de comunicação entre o estudante e a coordenação.
	Este canal de relacionamento foi desenvolvido para que você possa entrar em contato com a Coordenacao e tirar todas as dúvidas. 	
	</p>
	<p>A mensagem de resposta será enviada para seu email cadastrado no SIGAA. Você também poderá acompanhar o andamento da sua pergunta por meio desta página. Para ler a resposta basta clicar no ícone de abrir pergunta.</p>
</div>


		<h:form id="novaPergunta">
			<br/>
			<div class="infoAltRem" style="font-variant: small-caps;">
				Sua Pergunta será enviada para <br/>${atendimentoAluno.destinatario}
			</div>
	    	<table class="formulario" style="width: 100%">
		   	    <tbody>
					<tr>
						<td>
							<p class="descricao">
								Informe um titulo <span class="required"> </span>
							</p>
							<p>
								<center> <h:inputText id="titulo" value="#{atendimentoAluno.obj.titulo}" style="width: 90%" /></center>
							</p>
							<p class="descricao">
								Elabore sua pergunta <span class="required"> </span>
							</p>
							<p>
								<center><h:inputTextarea id="pergunta" value="#{atendimentoAluno.obj.pergunta}" rows="6" style="width: 90%" /></center>
							</p>
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td>
							<h:commandButton id="gravar" value="Enviar" action="#{atendimentoAluno.cadastrar}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{atendimentoAluno.cancelar}" />
			    		</td>
			    	</tr>
				</tfoot>			
			</table>
		</h:form>
		
		<br/>
		
		<center>
			<img style="vertical-align: top;" src="/sigaa/img/required.gif"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>
		</center>
				
		<br/>
				
		<c:set var="perguntas" value="${atendimentoAluno.todasPerguntas}" />
	
		<br/>			
		<c:if test="${empty perguntas}">
			<i><center>Não possui perguntas.</center></i>
		</c:if>
		<c:if test="${not empty perguntas}">
			<div class="infoAltRem" style="font-variant: small-caps;">
				<h:graphicImage value="/img/biblioteca/baixo.gif" style="overflow: visible;" alt="Ver Resposta"/>: Ver Resposta
		 		<h:graphicImage value="/img/biblioteca/cima.gif" style="overflow: visible;" alt="Ocultar Resposta"/>: Ocultar Resposta
			</div>
					
			<table class="listagem" width="80%">
				<thead>
					<tr>
						<td width="60%">Pergunta</td>
						<td style="text-align:left;">Status</td>
						<td></td>
					</tr>
				</thead>
				<c:forEach items="${perguntas}" var="pergunta" varStatus="loop">
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${pergunta.titulo}</td>
						<td align="left">${ pergunta.statusAtendimento.descricao }</td>
						<td align="right">
							<a href="javascript:void(0)" onclick="habilitarDetalhes(${pergunta.id});"  title="Abrir Pergunta">
								<img src="${ctx}/img/biblioteca/baixo.gif" title="Abrir Pergunta" id="icone_${pergunta.id}"/>
							</a>
						</td>
					</tr>
					<tr>
						<td colspan="3" id="linha_${pergunta.id}" class="detalhesDiscente" ></td>	
					</tr>					
				</c:forEach>					
			</table>
		</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>