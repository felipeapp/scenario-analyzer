<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
  tinyMCE.init({
        mode : "textareas",
        theme : "simple",
        width : "650",
        height : "200"
 });
</script>
<script>
var PainelLerObservacao = (function() {
	var painel;
	return {
        show : function(idAluno, idPlanoTrabalho){
	        	var p = getEl('painel-lerObservacao');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-lerObservacao", {
	   	 		   autoCreate: true,
				   title: 'Observação dos alunos',
                   proxyDrag: true,
                   constraintoviewport: false,
	               width: 700,
	               height: 330,
	               resizable: true
            	});

	       	 	painel.show();

				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/graduacao/agregador_bolsa/resumo_qualificacao.jsf?ajaxRequest=true&idAluno=' + idAluno + '&idPlanoTrabalho=' + idPlanoTrabalho,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando a observação...'
					 });
        }
	};
})();

</script>

<a4j:keepAlive beanName="notificacoes"/>
<a4j:keepAlive beanName="enviarMsgInteressados"/>
<f:view>

${ notificacoes.iniciarFormulario }

<c:if test="${!empty notificacoes.titulo }">
	<h2><ufrn:subSistema /> > ${notificacoes.titulo} </h2>
</c:if>

<c:if test="${empty notificacoes.titulo }">
	<h2><ufrn:subSistema /> > Enviar mensagem </h2>
</c:if>

<c:if test="${!empty notificacoes.descricao }">
	<div id="ajuda" class="descricaoOperacao">    
		${notificacoes.descricao}
	</div>
</c:if>

<h:form id="form">
	<table class="formulario" width="80%">
		<caption>Enviar Mensagem</caption>
		<tr>
			<td>
				<table class="listagem">
					<caption>Alunos Interessados</caption>
					<thead>
						<tr>
							<th>Matrícula</th>
							<th>Nome</th>
							<th></th>
						</tr>
					</thead>
					<c:forEach items="#{enviarMsgInteressados.interessados}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td width="10%">${d.matricula}</td>
							<td>${d.nome}</td>
							<td align="right">
								<a href="#" onclick="PainelLerObservacao.show(${d.id_discente}, ${idPlanoTrabalho})" title="Ver detalhes"> <img src="${ctx}/img/comprovante.png"/> </a>						
								<h:commandLink action="#{ enviarMsgInteressados.detalhesDiscente }">
									<h:graphicImage value="/img/view.gif" alt="Ver detalhes" title="Ver detalhes"/>
									<f:param name="id" value="#{ d.id_discente }"/>
								</h:commandLink>
							</td>							
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<%@include file="/geral/mensagem/formulario.jsp"%>	
			</td>
		</tr>	
	</table>
</h:form>
	
<c:if test="${not empty formPlanoTrabalho.interessadoBolsa }">
		<div id="div-form">
			<div class="ydlg-hd">Qualificações do Aluno</div>
			<div class="ydlg-bd">
				<table class="formulario" width="100%" style="border: 0;">
					<caption> ${formPlanoTrabalho.interessadoBolsa.discente.nome} </caption>
					<tr>
						<th>Telefone:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.telefone}</td>
					</tr>
					<tr>
						<th>Email:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.email}</td>
					</tr>					
					<tr>
						<th>Lattes:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.linkLattes}</td>
					</tr>
					<tr>
						<th>Qualificações:</th>
						<td>${formPlanoTrabalho.interessadoBolsa.dados.qualificacoes}</td>
					</tr>							
				</table>
			</div>
		</div>
	</c:if>


<script type="text/javascript">
<!--
var PainelQualificacao = (function() {
	var painel;
	return {
        show : function(){
   	 		painel = new YAHOO.ext.BasicDialog("div-form", {
                modal: true,
                width: 400,
                height: 200,
                shadow: false,
                fixedcenter: true,
                resizable: false,
                closable: true
            });
       	 	painel.show();
        }
	};
})();
-->
</script>
<c:if test="${not empty formPlanoTrabalho.interessadoBolsa }">
	<script type="text/javascript">
	<!--
		PainelQualificacao.show();
	-->
	</script>
</c:if>	
	
	
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>