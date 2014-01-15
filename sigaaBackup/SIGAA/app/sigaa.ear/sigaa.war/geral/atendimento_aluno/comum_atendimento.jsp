<script type="text/javascript">
var PainelResponder = (function() {
	var painel;
	return {
        show : function(id){
	        	var p = getEl('painel-responder');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-responder", {
	   	 		   autoCreate: true,
				   title: 'Detalhes da Pergunta',
                   proxyDrag: true,
                   constraintoviewport: false,
	               width: 700,
	               height: window.innerHeight - 400,
	               resizable: true
            	});

	       	 	painel.show();

				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/geral/atendimento_aluno/tela_atendente.jsf?ajaxRequest=true&id=' + id,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando a pergunta...'
					 });
        }
	};
})();
</script>

<div id="atendimento-aluno-portal" class="simple-panel">
<h:form id="teste">
	<h4>Atendimento ao Aluno</h4>
		<c:set var="perguntas" value="#{atendimentoAluno.listarPerguntasAtendente}"/>
				
		<c:if test="${empty perguntas}">		
			<p class="vazio">
				Não há perguntas pendentes.
			</p>
		</c:if>		
		<c:if test="${not empty perguntas}">
			<table>
				<thead>
					<tr>
						<th width="43%" style="text-align: left;padding-left: 2px;padding-right: 2px;">Pergunta</th>
						<th width="47%" style="text-align: left;padding-left: 2px;padding-right: 2px;">Discente</th>
						<th width="10%" style="text-align: center;">Enviada</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="pergunta" items="#{perguntas}" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "odd" : "" }">
							<td><a onclick="PainelResponder.show(${pergunta.id})" href="#" title="Selecionar Discente">${fn:substring(pergunta.titulo,0,25)} ${fn:length(pergunta.titulo) > 25 ? '...' : '' }</a></td>
							<td>${ pergunta.discente.nome }</td>
							<td style="text-align: center;">
								<fmt:formatDate value="${pergunta.dataEnvio}" pattern="dd/MM/yyyy" />
							</td>
							<td>
							<h:commandLink action="#{atendimentoAluno.preResponder}" title="Responder" id="responder">
								<f:param name="idPergunta" value="#{pergunta.id }" />
								<h:graphicImage url="/img/seta.gif" />
							</h:commandLink>	
							</td>
							<td>
								<h:commandLink action="#{atendimentoAluno.desativaPergunta}" title="Remover"  onclick="if(!confirm(\"Tem certeza que deseja excluir?\")) return false;" id="remover">
									<f:param name="idPergunta" value="#{pergunta.id }" />
									<h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
							</td>							
						</tr>
					</c:forEach>
				</tbody>
			</table>		
		</c:if>
		<div align="right" style="font-size : 10px">
			<h:commandLink action="#{atendimentoAluno.telaTodasPerguntasRespondidasAtendente}" value="ver todas as perguntas respondidas"/>&nbsp; | &nbsp;
			<h:commandLink action="#{atendimentoAluno.telaTotalPerguntasNaoRespondidas}" value="ver todas as perguntas não respondidas (#{atendimentoAluno.totalPerguntas})"/>
		</div>
		</h:form>		
</div>			