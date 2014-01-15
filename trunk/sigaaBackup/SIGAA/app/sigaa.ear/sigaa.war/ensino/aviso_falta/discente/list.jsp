<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>

<script>
var PainelLerObservacao = (function() {
	var painel;
	return {
        show : function(docenteId, turmaId, dataAula, externo){
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
					 url: '/sigaa/ensino/falta_docente/observacao.jsf?ajaxRequest=true&docenteId=' + docenteId + '&turmaId=' + turmaId + '&dataAula=' + dataAula + '&externo=' + externo,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando a observação...'
					 });
        }
	};
})();

</script>

<f:view>


	<h2>
		<ufrn:subSistema /> &gt;
		Listar Aviso de Falta
	</h2>
		

	<c:if test="${not faltaDocente.relatorioDetalhado}">
	
		<div class="infoAltRem">
			<img src="${ctx}/img/view.gif"/>: Ver Observações
			<h:graphicImage value="/img/biblioteca/ok.png" style="overflow: visible;" />: Homologar Aviso e Solicitar Plano de Reposição
			<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: Negar Aviso de Falta
		</div>
	
		<c:if test="${empty faltaDocente.faltasDetalhado}">
			<center><i>Não existe notificação de falta.</i></center>
		</c:if>	
	
		<c:if test="${not empty faltaDocente.faltasDetalhado}">
			<h:form id="formDenuncia">
				<table class="listagem">
					<caption class="listagem">Lista de Notificação de Falta</caption>
					<thead>
						<tr>
							<th>Disciplina</th>
							<th align="left">Turma</th>
							<th>Quantidade Avisos</th>
							<th>Data da Aula</th>
							<th></th>
						</tr>
					</thead>
		
					<tbody>
					
						<c:set var="nomeDocente" />
						<c:forEach var="falta" items="#{faltaDocente.faltasDetalhado}" varStatus="status">
		
								<c:if test="${falta.docenteNome != nomeDocente}">
									<c:set var="nomeDocente" value="${falta.docenteNome}"/>
									<tr>
										<td class="subFormulario" colspan="5"> ${nomeDocente} </td>
									</tr>
								</c:if>
		
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
									<td>${falta.turmaNome }</td>
									<td align="left">${falta.codigo}</td>
									<td align="center">${falta.qtdFalta }</td>
									<td><fmt:formatDate value="${falta.dataAula}" pattern="dd/MM/yyyy"/></td>
									<td>
										<a href="#" onclick="PainelLerObservacao.show('${falta.docenteId}', '${falta.turmaId}', '${falta.dataAula}', '${ falta.docenteExterno ? true : false }')" title="Ver Observaçoes dos alunos"> <img src="${ctx}/img/view.gif"/> </a>
										<h:commandLink title="Homologar Aviso e Solicitar Plano de Reposição" action="#{ faltaHomologada.iniciarAceitarHomologacao }"  rendered="#{falta.possuiFaltaHomologada == 0}" onclick="return confirm('Deseja realmente homologar o aviso de falta?');">
											<f:param name="idDocente" value="#{falta.docenteId}"/>
											<f:param name="idTurma" value="#{falta.turmaId}"/>
											<f:param name="dataAula" value="#{falta.dataAula}"/>
											<h:graphicImage url="/img/biblioteca/ok.png"/>
										</h:commandLink>
										<h:commandLink title="Negar Homologação de Falta" action="#{ faltaHomologada.iniciarNegarHomologacao }"  rendered="#{falta.possuiFaltaHomologada == 0}" onclick="return confirm('Deseja realmente negar o aviso de falta?');">
											<f:param name="idDocente" value="#{falta.docenteId}"/>
											<f:param name="idTurma" value="#{falta.turmaId}"/>
											<f:param name="dataAula" value="#{falta.dataAula}"/>
											<h:graphicImage url="/img/biblioteca/estornar.gif"/>
										</h:commandLink>										
									</td>
								</tr>
		
						</c:forEach>
					</tbody>			
				</table>
			</h:form>
		</c:if>
	</c:if>
	<c:if test="${faltaDocente.relatorioDetalhado}">
	
		<c:if test="${empty faltaDocente.faltasDetalhado}">
			<center><i>Não existe notificação de falta.</i></center>
		</c:if>
		<c:if test="${not empty faltaDocente.faltasDetalhado}">
			<h:form id="formDenunciaDetalhado">
				<table class="listagem">
					<caption class="listagem">Lista de Notificação de Falta (Por Mês)</caption>
					<thead>
						<tr>
							<th>Disciplina</th>
							<th>Quantidade de Avisos</th>
							<th>Mês</th>
							<th></th>
						</tr>
					</thead>
		
					<tbody>
					
						<c:set var="nomeDocente" />
						<c:forEach var="falta" items="${faltaDocente.faltasDetalhado}" varStatus="status">
		
								<c:if test="${falta.docenteNome != nomeDocente}">
									<c:set var="nomeDocente" value="${falta.docenteNome}"/>
									<tr>
										<td class="subFormulario" colspan="4"> ${nomeDocente} </td>
									</tr>
								</c:if>
		
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
									<td>${falta.turmaNome }</td>
									<td align="center">${falta.qtdFalta }</td>
									<td>
										<c:choose>
											<c:when test="${falta.mes == 1}">Janeiro</c:when>
											<c:when test="${falta.mes == 2}">Fevereiro</c:when>
											<c:when test="${falta.mes == 3}">Março</c:when>
											<c:when test="${falta.mes == 4}">Abril</c:when>
											<c:when test="${falta.mes == 5}">Maio</c:when>
											<c:when test="${falta.mes == 6}">Junho</c:when>
											<c:when test="${falta.mes == 7}">Julho</c:when>
											<c:when test="${falta.mes == 8}">Agosto</c:when>
											<c:when test="${falta.mes == 9}">Setembro</c:when>
											<c:when test="${falta.mes == 10}">Outubro</c:when>
											<c:when test="${falta.mes == 11}">Novembro</c:when>
											<c:when test="${falta.mes == 12}">Dezembro</c:when>										
										</c:choose>
									</td>
									<td>
										<a href="#" onclick="PainelLerObservacao.show('${falta.docenteId}', '${falta.turmaId}', '${falta.dataAula}')" title="Ver Observaçoes dos alunos"> <img src="${ctx}/img/view.gif"/> </a>
									</td>									
								</tr>
		
						</c:forEach>
					</tbody>			
				</table>
			</h:form>
		</c:if>
	</c:if>	
</f:view>
<p style="text-align: center"><a href="javascript:history.go(-1)"> << Voltar</a></p>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
