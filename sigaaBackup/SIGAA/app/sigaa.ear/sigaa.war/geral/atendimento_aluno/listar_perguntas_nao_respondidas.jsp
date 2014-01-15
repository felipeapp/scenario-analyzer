<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Atendimento ao Aluno</h2>

<f:view>

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

<h:form id="teste">	
		<c:if test="${empty atendimentoAluno.perguntasNaoRespondidas}">		
			<p class="vazio">
				Não há perguntas pendentes.
			</p>
		</c:if>		
		<c:if test="${not empty atendimentoAluno.perguntasNaoRespondidas}">
		    <div class="descricaoOperacao" style="width : 80%">
		       <p>Esta lista contém todas as perguntas não respondidas dos discentes.</p>
		       <p>Clique sobre a pergunta para visualizar.</p>
		    </div>
		    <div class="infoAltRem" style="width : 80%">
		       Responder: <h:graphicImage url="/img/seta.gif" /> <br/> 
		       Remover: <h:graphicImage url="/img/delete.gif" />
		    </div>
			<table class="listagem" style="width : 80%">
			    <caption>Atendimento ao Aluno</caption>
				<thead>
					<tr>
						<th width="45%" style="text-align: left;padding-left: 2px;padding-right: 2px;">Pergunta</th>
						<th width="45%" style="text-align: left;padding-left: 2px;padding-right: 2px;">Discente</th>
						<th width="10%" style="text-align: center;">Enviada</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="pergunta" items="#{atendimentoAluno.perguntasNaoRespondidas}" varStatus="status">
						<tr class='${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
							<td><a onclick="PainelResponder.show(${pergunta.id})" href="#" title="Selecioner Discente">${fn:substring(pergunta.titulo,0,25)} ${fn:length(pergunta.titulo) > 25 ? '...' : '' }</a></td>
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
								<h:commandLink action="#{atendimentoAluno.desativaPergunta}" onclick="if(!confirm(\"Tem certeza que deseja excluir?\")) return false;" title="Remover" id="remover">
									<f:param name="idPergunta" value="#{pergunta.id }" />
									<h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
							</td>															
						</tr>
					</c:forEach>
				</tbody>
			</table>		
		</c:if>
</h:form>				
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>